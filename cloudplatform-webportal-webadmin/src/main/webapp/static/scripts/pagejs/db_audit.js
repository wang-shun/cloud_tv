$(function(){
	//隐藏搜索框
	queryDbById();
	queryMcluserById();
	$('#nav-search').addClass("hidden");
	formValidate();
	$("#pageMessage").hide();
});
function createDbOnOldMcluster(){
	$.ajax({
		type : "post",
		url : "/db/audit",
		data :$('#create_on_old_cluster_form').serialize(),
		success : function (data){
			if(error(data)) return;
			window.location = "/list/db";
		}
	});
}
function createDbOnNewMcluster(){
	$.ajax({
		type : "post",
		url : "/db/audit",
		data :$('#create_on_new_cluster_form').serialize(),
		success:function (data){
			if(error(data)) return;
			window.location = "/list/db";
		}
	});
}
function refuseCreateMcluster(){
	$.ajax({
		type : "post",
		url : "/db/audit",
		data :$('#refuse_create_mcluster').serialize(),
		success:function (data){
			if(error(data)) return;
			window.location = "/list/db";
		}
	});
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
//创建db表单验证
function formValidate() {
	$("#create_on_new_cluster_form").bootstrapValidator({
	  message: '无效的输入',
         feedbackIcons: {
             valid: 'glyphicon glyphicon-ok',
             invalid: 'glyphicon glyphicon-remove',
             validating: 'glyphicon glyphicon-refresh'
         },
         fields: {
       	  mclusterName: {
                 validMessage: '请按提示输入',
                 validators: {
                     notEmpty: {
                         message: 'Container集群名称不能为空!'
                     },
			          stringLength: {
			              max: 40,
			              message: 'Container集群名过长'
			          },regexp: {
		                  regexp: /^([a-zA-Z_]+[a-zA-Z_0-9]*)$/,
  		                  message: "请输入字母数字或'_',Container集群名不能以数字开头."
                 	  },remote: {
	                        message: 'Container集群名已存在!',
	                        url: "/mcluster/validate"
	                    }
	             }
         	}	
         }
     }).on('error.field.bv', function(e, data) {
    	 $('#create-mcluster-botton').addClass("disabled");
     }).on('success.field.bv', function(e, data) {
    	 $('#create-mcluster-botton').removeClass("disabled");
     });
	
	$("#create_on_old_cluster_form").bootstrapValidator({
	  message: '无效的输入',
         fields: {
        	 mclusterId: {
                 validMessage: '请按提示选择',
                 validators: {
                     notEmpty: {
                         message: '选择Container集群不能为空!'
                     }
	             }
         	}	
         }
     }).on('error.field.bv', function(e, data) {
    	 $('#create_on_old_mcluster_botton').addClass("disabled");
     }).on('success.field.bv', function(e, data) {
    	 $('#create_on_old_mcluster_botton').removeClass("disabled");
     });
}
function queryDbById(){
	$.ajax({
		type:"get",
		url:"/db/"+$("#dbId").val(),
		dataType:"json",
		success:function(data){
			if(error(data)) return;
			var dbInfo = data.data;
			$("#headerDbName").append(dbInfo.dbName);
			$("#dbTableDbName").html(dbInfo.dbName);
			$("#dbTableBelongUser").html(dbInfo.user.userName);
			var linkType;
			if(dbInfo.linkType == 0){
				linkType = "长链接";
			}else{
				linkType = "短连接";
			}
			$("#dbTableType").html(linkType);
			var engineType;
			if(dbInfo.engineType == 0){
				engineType = "InnoDB";
			}
			$("#dbTableEngine").html(engineType);
			$("#dbTableApplyTime").html(date('Y-m-d H:i:s',dbInfo.createTime));
			$("#mclusterName").val(dbInfo.createUser+"_"+dbInfo.dbName);
		}
	});
}
function queryMcluserById(){
	$.ajax({
		type:"get",
		url:"/mcluster",
		dataType:"json",
		success:function(data){
			if(error(data)) return;
			var mclustersInfo = data.data;
			for(var i=0,len=mclustersInfo.length; i < len ;i++)
			{
				var option = $("<option value=\""+mclustersInfo[i].id+"\">"
								+mclustersInfo[i].mclusterName
								+"</option>");
				$("#mclusterOption").append(option);
			}
		}
	});
}