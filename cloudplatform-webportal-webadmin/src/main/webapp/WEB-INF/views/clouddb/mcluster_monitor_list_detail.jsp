<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div id="page-header-id" class="page-header">
		<h1>
			<a href="${ctx}/list/mcluster/monitor/1">返回cluster监控列表<i class="ace-icon fa fa-reply icon-only"></i></a>	
		</h1>
	</div>
	<input class="hidden" value="${ip}" name="vipaddr" id="vipaddr" type="text" />
	<!-- /.page-header -->
	<div class="row">
		<div class="col-sm-12 col-md-12">
			<div  class="col-sm-6 col-md-6" style="margin-top: 10px;">
			<table class="table table-bordered table-striped table-hover">
				<thead>
			       	<tr>
						<th colspan="6" style="text-align: center">cluster监控信息</th>
					</tr>
				</thead>
			   <tbody id="cluster_detail_table">
					<tr class="node">
						<td rowspan="4" style="width: 20%;">node</td>
						<input type="text" id="nodeFailNum" class="hidden" />
					</tr>
					<tr class="cluster">
						<td rowspan="3" style="width: 20%;">cluster</td>
						<input type="text" id="clusterFailNum" class="hidden" />
					</tr>
				</tbody>
			</table>
			</div>
		</div>
	</div>
</div>

<script src="${ctx}/static/scripts/pagejs/mcluster_monitor_list_detail.js"></script>