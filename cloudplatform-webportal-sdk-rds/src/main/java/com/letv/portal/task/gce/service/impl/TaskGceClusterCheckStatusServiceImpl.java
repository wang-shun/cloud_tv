package com.letv.portal.task.gce.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.portal.constant.Constant;
import com.letv.portal.enumeration.MclusterStatus;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.IGcePythonService;
import com.letv.portal.service.IHostService;
import com.letv.portal.service.gce.IGceContainerService;

@Service("taskGceClusterCheckStatusService")
public class TaskGceClusterCheckStatusServiceImpl extends BaseTask4GceServiceImpl implements IBaseTaskService{

	@Autowired
	private IGcePythonService gcePythonService;
	@Autowired
	private IHostService hostService;
	@Autowired
	private IGceContainerService gceContainerService;
	
	@Value("${python_create_check_time}")
	private long PYTHON_CREATE_CHECK_TIME;
	@Value("${python_check_interval_time}")
	private long PYTHON_CHECK_INTERVAL_TIME;
	
	private final static Logger logger = LoggerFactory.getLogger(TaskGceClusterCheckStatusServiceImpl.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception{
		TaskResult tr = super.execute(params);
		if(!tr.isSuccess())
			return tr;
		
		GceCluster gceCluster = super.getGceCluster(params);
		HostModel host = super.getHost(gceCluster.getHclusterId());
		
		String result = gcePythonService.checkContainerCreateStatus(gceCluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
		tr = analyzeRestServiceResult(result);
		
		Long start = new Date().getTime();
		while(!tr.isSuccess()) {
			Thread.sleep(PYTHON_CHECK_INTERVAL_TIME);
			if(new Date().getTime()-start >PYTHON_CREATE_CHECK_TIME) {
				tr.setResult("check time over");
				break;
			}
			result = gcePythonService.checkContainerCreateStatus(gceCluster.getClusterName(),host.getHostIp(),host.getName(),host.getPassword());
			tr = analyzeRestServiceResult(result);
		}
		if(tr.isSuccess()) {
			List<Map> containers = (List<Map>)((Map)transToMap(result).get("response")).get("containers");
			for (Map map : containers) {
				GceContainer container = new GceContainer();
				BeanUtils.populate(container, map);
				container.setGceClusterId(gceCluster.getId());
				container.setIpMask((String) map.get("netMask"));
				container.setContainerName((String) map.get("containerName"));
				container.setStatus(MclusterStatus.RUNNING.getValue());
				//物理机集群维护完成后，修改此处，需要关联物理机id
				container.setHostIp((String) map.get("hostIp"));
				HostModel hostModel = this.hostService.selectByIp((String) map.get("hostIp"));
				if(null != hostModel) {
					container.setHostId(hostModel.getId());
				}
				List<Map> portBindings = (List<Map>) map.get("port_bindings");
				StringBuffer hostPost = new StringBuffer();
				StringBuffer containerPort = new StringBuffer();
				StringBuffer protocol = new StringBuffer();
				for (Map portBinding : portBindings) {
					hostPost.append((String)portBinding.get("hostPost")).append(",");
					containerPort.append((String)portBinding.get("containerPort")).append(",");
					protocol.append((String)portBinding.get("protocol")).append(",");
				}
				container.setBingHostPort(hostPost.length()>0?hostPost.substring(0, hostPost.length()-1):hostPost.toString());
				container.setBindContainerPort(containerPort.length()>0?containerPort.substring(0, containerPort.length()-1):containerPort.toString());
				container.setBingProtocol(protocol.length()>0?protocol.substring(0, protocol.length()-1):protocol.toString());
				
				this.gceContainerService.insert(container);
			}
		}
		tr.setParams(params);
		return tr;
	}
	
	@Override
	public TaskResult analyzeRestServiceResult(String result) throws Exception {
		TaskResult tr = new TaskResult();
		Map<String, Object> map = transToMap(result);
		if(map == null) {
			tr.setSuccess(false);
			tr.setResult("api connect failed");
			return tr;
		}
		Map<String,Object> meta = (Map<String, Object>) map.get("meta");
		Map<String,Object> response = null;
		
		boolean isSucess = Constant.PYTHON_API_RESPONSE_SUCCESS.equals(String.valueOf(meta.get("code")));
		if(isSucess) {
			response = (Map<String, Object>) map.get("response");
			isSucess = Constant.PYTHON_API_RESULT_SUCCESS.equals(String.valueOf(response.get("code")));
		}
		if(isSucess) {
			tr.setResult((String) response.get("message"));
		} else {
			tr.setResult((String) meta.get("errorType") +":"+ (String) meta.get("errorDetail"));
		}
		tr.setSuccess(isSucess);
		return tr;
	}
	
}