package com.letv.portal.service.pay.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.letv.common.exception.ValidateException;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpClient;
import com.letv.common.util.MD5;
import com.letv.portal.constant.Constant;
import com.letv.portal.model.UserVo;
import com.letv.portal.model.letvcloud.BillUserAmount;
import com.letv.portal.model.order.Order;
import com.letv.portal.model.order.OrderSub;
import com.letv.portal.model.product.ProductInfoRecord;
import com.letv.portal.model.subscription.Subscription;
import com.letv.portal.service.IUserService;
import com.letv.portal.service.letvcloud.BillUserAmountService;
import com.letv.portal.service.letvcloud.BillUserServiceBilling;
import com.letv.portal.service.message.SendMsgUtils;
import com.letv.portal.service.openstack.billing.ResourceCreateService;
import com.letv.portal.service.openstack.billing.VmCreateListener;
import com.letv.portal.service.order.IOrderService;
import com.letv.portal.service.order.IOrderSubDetailService;
import com.letv.portal.service.order.IOrderSubService;
import com.letv.portal.service.pay.IPayService;
import com.letv.portal.service.product.IProductInfoRecordService;
import com.letv.portal.service.subscription.ISubscriptionDetailService;
import com.letv.portal.service.subscription.ISubscriptionService;
import com.mysql.jdbc.StringUtils;

@Service("payService")
public class PayServiceImpl implements IPayService {

	private static final Logger logger = LoggerFactory
			.getLogger(PayServiceImpl.class);

	@Autowired
	private IOrderService orderService;
	@Autowired
	private IOrderSubService orderSubService;
	@Autowired
	private IOrderSubDetailService orderSubDetailService;
	@Autowired
	private ResourceCreateService resourceCreateService;
	@Autowired
	private BillUserAmountService billUserAmountService;
	@Autowired
	private BillUserServiceBilling billUserServiceBilling;

	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ISubscriptionService subscriptionService;
	@Autowired
	private ISubscriptionDetailService subscriptionDetailService;
	@Autowired
	private IProductInfoRecordService productInfoRecordService;
	@Autowired
	private SendMsgUtils sendMessage;

	@Value("${pay.callback}")
	private String PAY_CALLBACK;

	@Value("${pay.success}")
	private String PAY_SUCCESS;

	public Map<String, Object> pay(String orderNumber, Map<String, Object> map, HttpServletResponse response) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String pattern = (String) map.get("pattern");
		if(pattern==null || (!Constant.ALI_PAY_PATTERN.equals(pattern) && !Constant.WX_PAY_PATTERN.equals(pattern))) {
			throw new ValidateException("传入的支付方式异常，支付方式："+pattern);
		}
		List<OrderSub> orderSubs = this.orderSubService.selectOrderSubByOrderNumber(orderNumber);
		if (orderSubs == null || orderSubs.size() == 0) {
			throw new ValidateException("参数未查出订单数据,orderNumber=" + orderNumber);
		}
		Order order = orderSubs.get(0).getOrder();
		if (order.getStatus().intValue() == 1) {
			ret.put("alert", "订单状态已失效");
			ret.put("status", 1);
		} else if (order.getStatus().intValue() == 2) {
			ret.put("alert", "订单状态已支付成功，请勿重复提交");
			ret.put("status", 2);
		} else {
			String userMoney = (String)map.get("accountMoney");
			BigDecimal price = getValidOrderPrice(orderSubs);
			if(userMoney!=null && Double.parseDouble(userMoney)>0) {
				BigDecimal accountMoney = new BigDecimal(userMoney);
				//验证账户金额>=accountaccountMoney
				BillUserAmount userAmount = this.billUserAmountService.getUserAmount(this.sessionService.getSession().getUserId());
				if(userAmount.getAvailableAmount().compareTo(accountMoney)==-1) {
					throw new ValidateException("账户余额小于传入金额!");
				}
				
				price = price.subtract(accountMoney);//需要支付的金额=总价-账户所选余额
				if(price.doubleValue()<0) {
					throw new ValidateException("传入金额不合法!");
				}
				//更新使用账户余额到订单表
				Order account = new Order();
				account.setId(orderSubs.get(0).getOrderId());
				account.setAccountPrice(accountMoney);
				this.orderService.updateBySelective(account);
			}
			
			if (price.doubleValue() == 0) {
				try {
					//设置用户支付部分余额为冻结余额
					if(!this.billUserAmountService.updateUserAmountFromAvailableToFreeze(orderSubs.get(0).getCreateUser(), getValidOrderPrice(orderSubs))) {
						ret.put("alert", "用户可使用余额不足");
						return ret;
					}
					if (updateOrderPayInfo(orderSubs.get(0).getOrderId(), "9999", 2)) {//9999代表订单支付金额为0或，订单金额全部使用账户余额支付
						//创建应用实例
						createInstance(orderSubs);
						response.sendRedirect(this.PAY_SUCCESS + "/" + orderNumber);
						return ret;
					} else {
						throw new ValidateException("更新订单状态异常");
					}
				} catch (IOException e) {
					logger.error("pay inteface sendRedirect had error, ", e);
				}
			}
			
			
			//增加用户充值信息
			this.billUserAmountService.recharge(orderSubs.get(0).getCreateUser(), price,orderNumber,Integer.valueOf(pattern));
			
			//充值
			Map<String, String> params = new HashMap<String, String>();
			String url = getParams(order.getOrderNumber(), price, pattern, this.PAY_CALLBACK, this.PAY_SUCCESS + "/" + orderNumber,
					orderSubs.size() == 1 ? orderSubs.get(0).getSubscription().getProductName() : orderSubs.get(0).getSubscription().getProductName()+ "...", 
					orderSubs.size() == 1 ? orderSubs.get(0).getSubscription().getProductDescn() : orderSubs.get(0).getSubscription().getProductDescn()+ "...", null, params);

			if (Constant.ALI_PAY_PATTERN.equals(pattern)) {// 支付宝方法
				logger.info("去支付宝支付：userId=" + sessionService.getSession().getUserId() +"交易信息=订单编号：" + order.getOrderNumber()+",价格："+price);
				try {
					response.sendRedirect(getPayUrl(url, params));
				} catch (IOException e) {
					logger.error("pay inteface sendRedirect had error, ", e);
				}
			} else if (Constant.WX_PAY_PATTERN.equals(pattern)) {// 微信支付
				logger.info("去微信支付：userId=" + sessionService.getSession().getUserId() +"交易信息=订单编号：" + order.getOrderNumber()+",价格："+price);
				String str = HttpClient.get(getPayUrl(url, params), 6000, 6000);
				ret = transResult(str);
				if(getValidOrderPrice(orderSubs).subtract(order.getAccountPrice()).compareTo(new BigDecimal((String) ret.get("price")))==0) {
					if (!updateOrderPayInfo(orderSubs.get(0).getOrderId(), (String) ret.get("ordernumber"), null)) {
						ret.put("alert", "微信方式支付异常");
					}
				} else {
					ret.put("alert", "微信方式支付异常");
				}
			}
		}
		return ret;
	}

	private String getParams(String number, BigDecimal price, String pattern,
			String backUrl, String frontUrl, String productName,
			String productDesc, String defaultBank, Map<String, String> params) {
		try {
			params.put("corderid", number);
			params.put("userid", this.sessionService.getSession().getUserId()
					+ "");
			params.put("username", URLEncoder.encode(this.sessionService
					.getSession().getUserName(), "UTF-8"));
			params.put("companyid", "4");
			params.put("deptid", "112");
			params.put("productid", "0");
			params.put("backurl", backUrl);
			params.put("fronturl", frontUrl);
			params.put("price", price.doubleValue()+"");
			params.put("buyType", "0");
			params.put("pid", "0");
			params.put("chargetype", "1");
			params.put("productname", productName==null?"":URLEncoder.encode(productName, "UTF-8"));
			params.put("productdesc", productDesc==null?"":URLEncoder.encode(productDesc, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("payService getParams had error :", e);
		}

		if (Constant.ALI_PAY_PATTERN.equals(pattern)) {// 支付宝支付
			params.put("defaultbank", defaultBank);
			return Constant.PAY_URL;
		}
		if (Constant.WX_PAY_PATTERN.equals(pattern)) {// 微信支付
			return Constant.WX_URL;
		}
		return null;
	}

	private String getPayUrl(String url, Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append("?");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append((String) entry.getKey());
			sb.append("=");
			sb.append((String) entry.getValue());
			sb.append("&");
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> transResult(String result) {
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String, Object> jsonResult = new HashMap<String, Object>();
		if (StringUtils.isNullOrEmpty(result))
			return jsonResult;
		try {
			jsonResult = (Map<String, Object>) resultMapper.readValue(result,
					Map.class);
		} catch (Exception e) {
			logger.error("transResult had error:", e);
		}
		return jsonResult;
	}

	public boolean callback(Map<String, Object> map) {
		// ①验证必须字段
		if ((map.get("corderid") == null) || (map.get("stat") == null)
				 || (map.get("money") == null)
				 || (map.get("ordernumber") == null)) {
			 return false;
		}

		List<OrderSub> orderSubs = this.orderSubService
				.selectOrderSubByOrderNumberWithOutSession((String) map
						.get("corderid"));
		if (orderSubs == null || orderSubs.size() == 0) {
			throw new ValidateException("无法查询到订单，订单编号："
					+ (String) map.get("corderid"));
		}

		// ②支付成功已通知服务器并且order的状态为已支付
		if (Integer.parseInt((String) map.get("stat")) == 1
				&& orderSubs.get(0).getOrder().getStatus() == 2) {
			return true;
		}

		// ③验证请求是否合法，规则：corderid=xxx&key&money=xxx&companyid=4
		String sign = getSign("4", Constant.SIGN_KEY, orderSubs.get(0)
				.getOrder().getOrderNumber(), getValidOrderPrice(orderSubs).subtract(orderSubs.get(0).getOrder().getAccountPrice()).doubleValue()+"");
		if (sign != null && sign.equals(map.get("sign"))) {
			
			if (updateOrderPayInfo(orderSubs.get(0).getOrderId(), (String) map.get("ordernumber"), 2)) {
				
				//更改用户充值信息
				//this.billUserAmountService.rechargeSuccess(orderSubs.get(0).getCreateUser(), order.getOrderNumber(), (String) map.get("ordernumber"), new BigDecimal((String) map.get("money")),false);
				this.billUserAmountService.rechargeSuccess(orderSubs.get(0).getCreateUser(), orderSubs.get(0).getOrder().getOrderNumber(), (String) map.get("ordernumber"), new BigDecimal((String) map.get("money")));
				//设置用户支付部分余额为冻结余额
				if(!this.billUserAmountService.updateUserAmountFromAvailableToFreeze(orderSubs.get(0).getCreateUser(), getValidOrderPrice(orderSubs))) {
					return false;
				}
				
				
				//发送用户通知
				//写入最近操作
				Long createUser = orderSubs.get(0).getCreateUser();
				UserVo ucUser = this.userService.getUcUserById(createUser);
				if(ucUser !=null && !StringUtils.isNullOrEmpty(ucUser.getMobile()))
				this.sendMessage.sendMessage(ucUser.getMobile(), "尊敬的用户，您购买的云主机已成功支付"+map.get("money")+"元，请登录网站matrix.letvcloud.com进行体验！如有问题，可拨打客服电话。");
				
				// ④创建应用实例
				createInstance(orderSubs);
				return true;
			}
		}
		return false;
	}

	/**
	 * @Title: getValidOrderPrice
	 * @Description: 获取有效的价格(优先使用折扣价)
	 * @param order
	 * @return double
	 * @throws
	 * @author lisuxiao
	 * @date 2015年9月17日 下午4:23:46
	 */
	private BigDecimal getValidOrderPrice(List<OrderSub> orderSubs) {
		BigDecimal price = new BigDecimal(0);
		for (OrderSub orderSub : orderSubs) {
			if ((orderSub.getDiscountPrice() == null)
					|| (orderSub.getDiscountPrice().compareTo(price)<0)) {// 使用原价
				price = orderSub.getPrice().add(price);
			} else {
				price = orderSub.getDiscountPrice().add(price);// 使用折扣价
			}
		}
		return price;
	}

	private boolean updateOrderPayInfo(long orderId, String orderNumber, Integer status) {
		Order o = new Order();
		o.setId(orderId);
		o.setStatus(status);
		o.setUpdateTime(new Timestamp(new Date().getTime()));
		o.setPayNumber(orderNumber);
		this.orderService.updateBySelective(o);
		return true;
	}

	public Map<String, Object> queryState(String orderNumber) {
		List<OrderSub> orderSubs = this.orderSubService
				.selectOrderSubByOrderNumber(orderNumber);
		if (orderSubs == null || orderSubs.size() == 0) {
			return null;
		}
		Order order = orderSubs.get(0).getOrder();
		if (order == null || order.getPayNumber() == null) {
			return null;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("companyid", "4");
		params.put("corderid", order.getPayNumber());
		params.put("sign",
				getSign("4", Constant.SIGN_KEY, order.getPayNumber(), null));
		String url = getPayUrl(Constant.QUERY_URL, params);
		String ret = HttpClient.get(url, 2000, 2000);
		Map<String, Object> map = transResult(ret);

		if ((map.get("status") != null) && (Integer) map.get("status") == 1) {// 支付成功
			if(getValidOrderPrice(orderSubs).subtract(orderSubs.get(0).getOrder().getAccountPrice()).compareTo(new BigDecimal((String) map.get("money")))==0) {
				return map;
			}
		}
		return null;
	}

	private String getSign(String companyId, String key, String payNumber,
			String money) {
		if ((companyId == null) || (key == null) || (payNumber == null)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("corderid=");
		sb.append(payNumber);
		sb.append("&");
		sb.append(key);
		if (money != null) {
			sb.append("&money=");
			sb.append(money);
		}
		sb.append("&companyid=");
		sb.append(companyId);
		MD5 m = new MD5();
		return m.getMD5ofStr(sb.toString()).toLowerCase();
	}

	@Async
	private void createInstance(final List<OrderSub> orderSubs) {
		for (OrderSub orderSub : orderSubs) {
			// 进行服务创建
			if (orderSub.getSubscription().getProductId() == 2) {// openstack
				if ("1".equals(orderSub.getProductInfoRecord().getInvokeType())) {
					List<ProductInfoRecord> records = new ArrayList<ProductInfoRecord>();
					for (OrderSub orders : orderSubs) {
						records.add(orders.getProductInfoRecord());
					}
					this.resourceCreateService.createVm(
							orderSub.getCreateUser(),
							orderSub.getProductInfoRecord().getParams(),new VmCreateListener(){
								@SuppressWarnings("unchecked")
								@Override
								public void vmCreated(String region,
										String vmId, int vmIndex,
										Object userData) throws Exception {
									List<ProductInfoRecord> records = (List<ProductInfoRecord>) userData;
									if(vmIndex>records.size()) {
										throw new ValidateException("云主机回调vmIndex超出List范围！");
									}
									ProductInfoRecord record = records.get(vmIndex);
									record.setParams(null);
									record.setProductType(null);
									record.setInvokeType(null);
									record.setInstanceId(region+"_"+vmId);
									productInfoRecordService.updateBySelective(record);
									
									//云主机创建成功后减冻结余额
									billUserAmountService.reduceFreezeAmount(orderSubs.get(0).getCreateUser(), getValidOrderPrice(orderSubs).divide(new BigDecimal(orderSubs.size())));
									if((vmIndex+1)==orderSubs.size()) {
										SimpleDateFormat df = new SimpleDateFormat("yyyyMM");//设置日期格式
										//生成用户账单。
										billUserServiceBilling.add(orderSubs.get(0).getCreateUser(), "1", orderSubs.get(0).getOrderId(), 
												df.format(new Date()), getValidOrderPrice(orderSubs).toString());
										//更新订阅订单起始时间
										updateSubscriptionAndOrderTime(orderSubs);
									}
									
									logger.info("callback success! num="+vmIndex);
								}
								
							}, records);
					logger.info("createInstance success!");
				}
			}
		}
	}
	
	private void updateSubscriptionAndOrderTime(List<OrderSub> orderSubs) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("startTime", date);
		for (OrderSub orderSub : orderSubs) {
			Subscription subscription = new Subscription();
			subscription.setId(orderSub.getSubscriptionId());
			subscription.setStartTime(date);
			cal.setTimeInMillis(date.getTime());
			cal.add(Calendar.MONTH, orderSub.getSubscription().getOrderTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        cal.add(Calendar.DAY_OF_MONTH, 1);
			subscription.setEndTime(cal.getTime());
			this.subscriptionService.updateBySelective(subscription);
			
			updateParams.put("endTime", cal.getTime());
			updateParams.put("subscriptionId", orderSub.getSubscriptionId());
			this.subscriptionDetailService.updateBuyTime(updateParams);
			
			OrderSub o = new OrderSub();
			o.setId(orderSub.getId());
			o.setStartTime(date);
			o.setEndTime(cal.getTime());
			this.orderSubService.updateBySelective(o);
			updateParams.put("orderSubId", orderSub.getId());
			this.orderSubDetailService.updateBuyTime(updateParams);
		}
	}

}