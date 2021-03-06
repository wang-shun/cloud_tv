package com.letv.portal.controller.clouddb;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.exception.ValidateException;
import com.letv.common.result.ResultObject;
import com.letv.portal.model.DbModel;
import com.letv.portal.proxy.IMonitorProxy;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IMonitorIndexService;
import com.letv.portal.service.gce.IGceServerService;
import com.letv.portal.service.slb.ISlbServerService;
/**
 * Program Name: MonitorController <br>
 * Description:  监控<br>
 * @author name: wujun <br>
 * Written Date: 2014年11月6日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {
	
	@Resource
	private IMonitorProxy monitorProxy;
	@Resource
	private IDbService dbService;
	@Resource
	private IGceServerService gceServerService;
	@Resource
	private ISlbServerService slbServerService;
	@Resource
	private IMonitorIndexService monitorIndexService;
	
	/**
	 * Methods Name: mclusterMonitorCharts <br>
	 * Description: 监控视图<br>
	 * @author name: liuhao1
	 * @param mclusterId
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/{dbId}/{chartId}/{strategy}/{isTimeAveraging}",method=RequestMethod.GET)
	public @ResponseBody ResultObject getDbConnMonitor(@PathVariable Long dbId,@PathVariable Long chartId,@PathVariable Integer strategy,@PathVariable boolean isTimeAveraging,ResultObject result) {
		result.setData(this.monitorProxy.getDbData(dbId, chartId, strategy,isTimeAveraging,0));
		return result;
	}
	@RequestMapping(value="/{type}/{severId}/{chartId}/{strategy}/{isTimeAveraging}/{format}",method=RequestMethod.GET)
	public @ResponseBody ResultObject getDbConnMonitor(@PathVariable String type,@PathVariable Long severId,@PathVariable Long chartId,@PathVariable Integer strategy,@PathVariable boolean isTimeAveraging,@PathVariable int format,ResultObject result) {
		result.setData(this.monitorProxy.getData(type,severId, chartId, strategy,isTimeAveraging,format));
		return result;
	}
	@RequestMapping(value="/index/{indexId}",method=RequestMethod.GET)
	public @ResponseBody ResultObject mclusterMonitorChartsCount(@PathVariable Long indexId,ResultObject result) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", indexId);
		result.setData(this.monitorIndexService.selectByMap(map));
		return result;
	}

}
