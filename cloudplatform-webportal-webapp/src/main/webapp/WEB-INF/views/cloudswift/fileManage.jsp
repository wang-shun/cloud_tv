<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<%@include file='main.jsp' %>
<body>
<div class="se-heading" id="headingOne">
	<div class="pull-left">
		<h5>文件管理</h5>
	</div>
	<div class="clearfix"></div>
</div>
<div class="container-fluid">
	<div class="row"><!-- main-content-center-begin -->
		<div class="col-sm-12 col-md-12">
			<table class="table table-hover table-se">
				<thead>
					<tr>
						<th width="10">
							<input type="checkbox">
						</th>
						<th class="padding-left-32">文件名称</th>
						<th>类型</th>
						<th>上传日期</th>
						<th>大小</th>
						<th>缓存过期</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody id="tby">
					<tr>
						<td width="10">
							<input type="checkbox">
						</td>
						<td><span>containertest</span></td>
						<td><span>文件夹</span></td>
						<td><span>2015-05-04 10:55:55</span></td>
						
						<td><span>40 MB</span></td>
						<td><span>未设置</span></td>
						<td><span class="text-explode font-disabled">删除</span></td>
					</tr>
				</tbody>
				<tfoot id="paginatorBlock">
					<tr class="tfoot">
						<td width="10">
							<input type="checkbox">
						</td>
						<td colspan="10">
							<div class="pull-left">
									<div class="clearfix" pagination-info="paginationInfo">
										<div class="pull-left">
											<button class="btn btn-default" disabled="disabled" style="height:30px;font-size:12px;">批量删除</button>
										</div>
									</div>
							</div>
							<div class="pull-right">
								<div class="pagination-info">
									<span class="ng-binding">共有<span id="totalRecords">1</span>条</span>， 
									<span class="ng-binding">每页显示：<span id="recordsPerPage">10</span>条</span>&nbsp;
								    <ul id="paginator" class="pagination pagination-sm"><li class="disabled"><a href="javascript:void(0);" title="Go to first page">«</a></li><li class="disabled"><a href="javascript:void(0);" title="Go to previous page">‹</a></li><li class="active"><a href="javascript:void(0);" title="Current page is 1">1</a></li><li class="disabled"><a href="javascript:void(0);" title="Go to next page">›</a></li><li class="disabled"><a href="javascript:void(0);" title="Go to last page">»</a></li></ul>
								</div>
							</div>
							<div class="clearfix"></div>
						</td>
					</tr>
				</tfoot>
			</table>
		    
		</div>
	</div><!-- main-content-center-end -->
</div>
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script>
//Set configuration
seajs.config({
	base: "${ctx}/static/modules/",
	alias: {
		"jquery": "jquery/2.0.3/jquery.min.js",
		"bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js"
	}
});
// seajs.use("${ctx}/static/page-js/cloudswift/fileManage/main");
/*self define*/

</script>
</body>
</html>