<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="${ctx}/static/scripts/jquery.bootstrap-duallistbox.min.js"></script>
<div class="page-content-area">
	<div id="page-header-id" class="page-header">
		<h1> 
			<a href="${ctx}/db/list">数据库列表</a>
			<small> 
				<i class="ace-icon fa fa-angle-double-right"></i> 
				${dbApplyStandard.applyCode}
			</small>
		</h1>
	</div>
	<!-- /.page-header -->
</div>

<input type="text" class="form-control hide" id="clusterId" name="clusterId"/>

<div class="row clearfix">
	<div class="col-md-5 column">
		<div class="col-sm-12">
		<table class="table table-bordered" id="db_detail_table">	
			<caption>用户申请单</caption>
			<tr>
			<td>项目名称</td>
			<td>${dbApplyStandard.applyName}</td>
		</tr>
		<tr>
			<td>业务描述</td>
			<td>${dbApplyStandard.descn}</td>
		</tr>
		<tr>
			<td>链接类型</td>
			<td>${dbApplyStandard.linkType}</td>
		</tr>
		<tr>
			<td>最大访问量</td>
			<td>${dbApplyStandard.maxConcurrency}</td>
		</tr>
		<tr>
			<td>读写比例</td>
			<td>${dbApplyStandard.readWriterRate}</td>
		</tr>
		<tr>
			<td>开发语言</td>
			<td>${dbApplyStandard.developLanguage}</td>
		</tr>
		<tr>
			<td>ip访问列表</td>
			<td>${dbApplyStandard.dataLimitIpList}</td>
		</tr>
		<tr>
			<td>管理员ip访问列表</td>
			<td>${dbApplyStandard.mgrLimitIpList}</td>
		</tr>
		<tr>
			<td>数据库引擎</td>
			<td>${dbApplyStandard.engineType}</td>
		</tr>
		<tr>
			<td>邮件通知</td>
			<td>
				<c:if test="${dbApplyStandard.isEmailNotice == '1'}">开启</c:if>
				<c:if test="${dbApplyStandard.isEmailNotice != '1'}">关闭</c:if>
			</td>
		</tr>
		<tr>
			<td>申请时间</td>
			<td>${dbApplyStandard.createTime}</td>
		</tr>
		</table>
		</div>
	</div>
	<div class="col-md-7 column">
		<ul id="myTab" class="nav nav-tabs">
		   <li class="active">
		      <a href="#create_on_cluster" data-toggle="tab">在已有集群创建</a>
		   </li>
		   <li>
		   	  <a href="#create_on_new_cluster" data-toggle="tab">在新集群创建</a>
		   </li>
		   <li>
		   	  <a href="#disagree" data-toggle="tab">不同意</a>
		   </li>
		</ul>					
		<div id="myTabContent" class="tab-content" >
			<div class="tab-pane fade in active" id="create_on_cluster" style="margin-top: 50px;">
			<form class="form-horizontal" role="form" action="${ctx}/db/audit/save" method="post">
				<input type="text" class="form-control hide" value="${dbApplyStandard.applyCode}" id="applyCode" name="applyCode"/>
				<input type="text" class="form-control hide" id="dbId" name="dbId" value="${dbApplyStandard.belongDb}"/>
				<input type="text" class="form-control hide" value="1" id="auditType" name="auditType"/>
				<input type="text" class="form-control hide" value="${dbApplyStandard.id}" id="dbApplyStandardId" name="dbApplyStandardId"/>
			  <div class="form-group">
			    <label for="text" class="col-sm-2 control-label">选择集群</label>
			    <div class="col-sm-8">
			      <select id="mclusterOption" name="mclusterId" class="form-control">
						<option value=""></option>
			        <c:forEach var="mcluster" items="${mclusters}">
						<option value="${mcluster.id}">${mcluster.mclusterName}</option>
					</c:forEach>
			      </select>
			    </div>
			    <div class="col-sm-2">
			      <button type="submit" class="btn btn-primary">创建</button>
			    </div>
			  </div>
			</form>
		   </div>
		   <div class="tab-pane fade" id="create_on_new_cluster">
				<form id="demoform" method="post" action="${ctx}/db/audit/save">
					<input type="text" class="form-control hide" value="${dbApplyStandard.applyCode} id="applyCode" name="applyCode"/>
					<input type="text" class="form-control hide" id="dbId" name="dbId" value="${dbApplyStandard.belongDb}"/>
					<input type="text" class="form-control hide" value="${dbApplyStandard.id}" id="dbApplyStandardId" name="dbApplyStandardId"/>
					<input type="text" class="form-control hide" value="2" id="auditType" name="auditType"/>
		            <select multiple="multiple" size="10" name="hostIds" id="duallistbox" style="height: 256px;">
					<c:forEach var="host" items="${hosts}">
						<option value="${host.id}">${host.hostName}(${host.hostIp})</option>
					</c:forEach>
		            </select>
	          <button type="submit" class="btn btn-primary pull-right" style="margin-top: 10px;">创建</button>				     
	          </form>
		   </div>
		   <div class="tab-pane fade" id="disagree">

		      <form role="form" method="post" action="${ctx}/db/audit/save">
				  <input type="text" class="form-control hide" value="-1" id="auditType" name="auditType"/>
					<input type="text" class="form-control hide" value="${dbApplyStandard.applyCode} id="applyCode" name="applyCode"/>
					<input type="text" class="form-control hide" id="dbId" name="dbId" value="${dbApplyStandard.belongDb}"/>
					<input type="text" class="form-control hide" value="${dbApplyStandard.id}" id="dbApplyStandardId" name="dbApplyStandardId"/>
				  <div class="form-group form-group-lg">
				    <label class="control-label" for="formGroupInputLarge">原因</label>
				    <div>
				      <textarea id="auditInfo" name="auditInfo" class="form-control" rows="12" placeholder="请输入未通过原因"></textarea>
				    </div>
				  </div>
				  <button type="submit" class="btn btn-primary pull-right">确定</button>
			  </form>
		   </div>
		   </div>
	</div>
</div>
<script type="text/javascript">
$(function(){
	hostDualListBox();
	$("#pageMessage").hide();
});

function translateStatus(status){
	if(status = 1)
	{
		return "是";
	}else
	{
		return "否";
	}
	
}

function request(paras)
{ 
    var url = location.href; 
    var paraString = url.substring(url.indexOf("?")+1,url.length).split("&"); 
    var paraObj = {} 
    for (i=0; j=paraString[i]; i++){ 
    paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length); 
    } 
    var returnValue = paraObj[paras.toLowerCase()]; 
    if(typeof(returnValue)=="undefined"){ 
    return ""; 
    }else{ 
    return returnValue; 
    } 
}

function hostDualListBox()
{	
	 var demo1 = $('select[name="hostIds"]').bootstrapDualListbox();
     $("#demoform").submit(function() {
    	 if(4 != $("#duallistbox").val().length){
			$('#pageMessage').html(pageMessage("warning","请选择4台物理机")).show().fadeOut(3000);
    		return false;
    	 }else{
    		 return true;
    	 }
     });		
}
</script>
