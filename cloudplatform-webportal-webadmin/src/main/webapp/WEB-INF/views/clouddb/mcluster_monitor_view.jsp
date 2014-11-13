<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="page-content-area">
	<div class="page-header">
		<h1> 
			container集群监控视图
		</h1>
	</div>
	<!-- /.page-header -->
	<div class="row">
		<div id="monitor-view" class="col-xs-12"> 
		</div>
	</div>
</div>
<script src="${ctx}/static/scripts/highcharts/highcharts.js"></script>
<script src="${ctx}/static/scripts/highcharts/themes/grid.js"></script>
<script type="text/javascript">
function drawChart(obj,title,ytitle,unit,xdata,ydata){
    $(obj).highcharts({
        title: {
            text: title
      
        },
        xAxis: {
            categories: xdata 
        },
        yAxis: {
            title: {
                text: ytitle 
            }
        },
        tooltip: {
            valueSuffix: unit
        },
        series: ydata
    });

} 

function addChart(data){
	var view = $('#monitor-view');
	var div = $("<div name=\"data-chart\" style=\"min-width: 310px; height: 400px; margin: 0 auto\"></div>");
	div.appendTo(view);
	drawChart(div,data.title,data.ytitle,data.unit,data.xdata,data.ydata);
}

function queryAllChart(clusterId){
	$.ajax({
		type : "get",
		url : "${ctx}/monitor/mclusterChartsCount",
		dataType : "json", 
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			error(data);
			var array = data.data;
			for (var i=0,len = array.length;i < len; i ++){
				$.ajax({
					type : "get",
					url : "${ctx}/monitor/"+clusterId+"/"+array[i].id,
					dataType : "json", 
					contentType : "application/json; charset=utf-8",
					success:function(data){
				 		error(data);
				 		addChart(data.data);
					}
				});
			}
		}	
	});
}

$(function(){
	// $.ajax({
	// 	type : "get",
	// 	url : "${ctx}/monitor/2144",
	// 	dataType : "json", 
	// 	contentType : "application/json; charset=utf-8",
	// 	success : function(data) {
	// 		error(data);
	// 		addChart("1",data.data)
	// 	}	
	// });
	queryAllChart("2144");
});
</script>
