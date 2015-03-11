/*翻译时间戳为时间格式*/
function date(format, timestamp){
    //var a, jsdate=((timestamp) ? new Date(timestamp) : new Date());
	var a;
	if(timestamp == null){
		return "---";
	}else{
		var jsdate=new Date(timestamp);
	}
	
    var pad = function(n, c){
        if((n = n + "").length < c){
            return new Array(++c - n.length).join("0") + n;
        } else {
            return n;
        }
    };
    var txt_weekdays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    var txt_ordin = {1:"st", 2:"nd", 3:"rd", 21:"st", 22:"nd", 23:"rd", 31:"st"};
    var txt_months = ["", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    var f = {
        // Day
        d: function(){return pad(f.j(), 2)},
        D: function(){return f.l().substr(0,3)},
        j: function(){return jsdate.getDate()},
        l: function(){return txt_weekdays[f.w()]},
        N: function(){return f.w() + 1},
        S: function(){return txt_ordin[f.j()] ? txt_ordin[f.j()] : 'th'},
        w: function(){return jsdate.getDay()},
        z: function(){return (jsdate - new Date(jsdate.getFullYear() + "/1/1")) / 864e5 >> 0},
       
        // Week
        W: function(){
            var a = f.z(), b = 364 + f.L() - a;
            var nd2, nd = (new Date(jsdate.getFullYear() + "/1/1").getDay() || 7) - 1;
            if(b <= 2 && ((jsdate.getDay() || 7) - 1) <= 2 - b){
                return 1;
            } else{
                if(a <= 2 && nd >= 4 && a >= (6 - nd)){
                    nd2 = new Date(jsdate.getFullYear() - 1 + "/12/31");
                    return date("W", Math.round(nd2.getTime()/1000));
                } else{
                    return (1 + (nd <= 3 ? ((a + nd) / 7) : (a - (7 - nd)) / 7) >> 0);
                }
            }
        },
       
        // Month
        F: function(){return txt_months[f.n()]},
        m: function(){return pad(f.n(), 2)},
        M: function(){return f.F().substr(0,3)},
        n: function(){return jsdate.getMonth() + 1},
        t: function(){
            var n;
            if( (n = jsdate.getMonth() + 1) == 2 ){
                return 28 + f.L();
            } else{
                if( n & 1 && n < 8 || !(n & 1) && n > 7 ){
                    return 31;
                } else{
                    return 30;
                }
            }
        },
       
        // Year
        L: function(){var y = f.Y();return (!(y & 3) && (y % 1e2 || !(y % 4e2))) ? 1 : 0},
        //o not supported yet
        Y: function(){return jsdate.getFullYear()},
        y: function(){return (jsdate.getFullYear() + "").slice(2)},
       
        // Time
        a: function(){return jsdate.getHours() > 11 ? "pm" : "am"},
        A: function(){return f.a().toUpperCase()},
        B: function(){
            // peter paul koch:
            var off = (jsdate.getTimezoneOffset() + 60)*60;
            var theSeconds = (jsdate.getHours() * 3600) + (jsdate.getMinutes() * 60) + jsdate.getSeconds() + off;
            var beat = Math.floor(theSeconds/86.4);
            if (beat > 1000) beat -= 1000;
            if (beat < 0) beat += 1000;
            if ((String(beat)).length == 1) beat = "00"+beat;
            if ((String(beat)).length == 2) beat = "0"+beat;
            return beat;
        },
        g: function(){return jsdate.getHours() % 12 || 12},
        G: function(){return jsdate.getHours()},
        h: function(){return pad(f.g(), 2)},
        H: function(){return pad(jsdate.getHours(), 2)},
        i: function(){return pad(jsdate.getMinutes(), 2)},
        s: function(){return pad(jsdate.getSeconds(), 2)},
        //u not supported yet
       
        // Timezone
        //e not supported yet
        //I not supported yet
        O: function(){
            var t = pad(Math.abs(jsdate.getTimezoneOffset()/60*100), 4);
            if (jsdate.getTimezoneOffset() > 0) t = "-" + t; else t = "+" + t;
            return t;
        },
        P: function(){var O = f.O();return (O.substr(0, 3) + ":" + O.substr(3, 2))},
        //T not supported yet
        //Z not supported yet
       
        // Full Date/Time
        c: function(){return f.Y() + "-" + f.m() + "-" + f.d() + "T" + f.h() + ":" + f.i() + ":" + f.s() + f.P()},
        //r not supported yet
        U: function(){return Math.round(jsdate.getTime()/1000)}
    };
       
    return format.replace(/[\\]?([a-zA-Z])/g, function(t, s){
        if( t!=s ){
            // escaped
            ret = s;
        } else if( f[s] ){
            // a date function exists
            ret = f[s]();
        } else{
            // nothing special
            ret = s;
        }
        return ret;
    });
}
/*翻译所有状态信息*/
function translateStatus(status){
	if (status == 0){
		return "未审核";
	}else if(status == 1){
		return "运行中";
	}else if(status == 2){
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>创建中...";
	}else if(status == 3){
		return "创建失败";
	}else if(status == 4){
		return "<font color=\"red\">审核失败</font>";
	}else if(status == 5){
		return "<font color=\"orange\">异常</font>";
	}else if(status == 6){
		return "正常";
	}else if(status == 7){
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>启动中...";
	}else if(status == 8){
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>停止中...";
	}else if(status == 9){
		return "已停止";
	}else if(status == 10){
		return "<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>删除中...";
	}else if(status == 11){
		return "已删除";
	}else if(status == 12){
		return "不存在";
	}else if(status == 13){
		return "<font color=\"orange\">危险</font>";
	}else if(status == 14){
		return "<font color=\"red\">严重危险</font>";
	}else if(status == 15){
		return "禁用";
	}else if(status == 'FAILD'){
		return "备份失败";
	}else if(status == 'SUCCESS'){
		return "备份成功";
	}else if(status == 'BUILDING'){
		return "备份中...";
	}	
}
/*错误提示框,一般在异步请求返回信息中用到*/
function error(errorThrown,time) {
	if(errorThrown.result == 0) {
		if(!time){
			time = 1000;
		}
		$.gritter.add({
			title: '错误',
			text: errorThrown.msgs,
			sticky: false,
			time: time,
			class_name: 'gritter-error'
		});
		return true;
	}
	return false;
}
/*警告提示框,一般在操作提示中用到*/
function warn(warnMsg,time) {
	if(!time){
		time = 1000;
	}
	$.gritter.add({
		title: '警告',
		text: warnMsg,
		sticky: false,
		time: time,
		class_name: 'gritter-error'
	});
	return;
}
/*成功提示框,一般在操作提示中用到*/
function success(warnMsg,time) {
	if(!time){
		time = 1000;
	}
	$.gritter.add({
		title: '成功',
		text: warnMsg,
		sticky: false,
		time: time,
		class_name: 'gritter-success'
	});
	return;
}
/*确认框脚本
 * 
 * */
function confirmframe(title,content,question,ok,cancle){
	$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
		_title : function(title) {
			var $title = this.options.title || '&nbsp;'
			if (("title_html" in this.options)
					&& this.options.title_html == true)
				title.html($title);
			else
				title.text($title);
		}
	}));
	
	$('#dialog-confirm').removeClass('hide').dialog({
		resizable : false,
		modal : true,
		title : "<div class='widget-header'><h4 class='smaller'>"+title,
		title_html : true,
		buttons : [
				{
					html : "确定",
					"class" : "btn btn-primary btn-xs",
					click : function() {
						ok();
						$(this).dialog("close");
					}
				},
				{
					html : "取消",
					"class" : "btn btn-xs",
					click : function() {
						$(this).dialog("close");
					}
				} ]
	});
	$('#dialog-confirm-content').html(content);
	$('#dialog-confirm-question').html(question);
}
/*高级搜索toggle效果*/
function bt_toggle(containerId){
	$('#'+containerId).click(function(event){
		var temp=$(this).attr("id");
		$('#'+temp+"-div").slideToggle();
	});
}
function getLoading(){
	$('body').append("<div class=\"spin\"></div>");
	$('body').append("<div class=\"far-spin\"></div>");
}
function removeLoading(){
	$('body').find('.spin').remove();
	$('body').find('.far-spin').remove();
}
function FilterNull(data){
	if(data == null || data == undefined){
		return '';
	}else{
		return data;
	}
}
//chosen 组件配置
function chosenConf(){	
	$('.chosen-select').chosen("destroy"); 
	$('.chosen-select').chosen({
		allow_single_deselect:true,
		search_contains:true,
		no_results_text:"未找到匹配数据"
		}); 
	//resize the chosen on window resize

	$(window)
	.off('resize.chosen')
	.on('resize.chosen', function() {
		$('.chosen-select').each(function() {
			 var $this = $(this);
			 $this.next().css({'width': $this.parent().width()});
		})
	}).trigger('resize.chosen');
}

/*预警管理页面查看详情通用函数 begin*/
function strToJson(str){
	var jsonArr = {};
	var strArr = str.split(",");
	var len = strArr.length;
	if(len <= 1){
		return str;
	}else{
		for(var i=0;i < len; i++){
			strArrC = strArr[i].split("=");
			if(strArrC.length == 2){
				jsonArr[strArrC[0]] = strArrC[1];
			}
		}
		return jsonArr;
	}
}

function msgFormat(data,obj,failedObj){
	if(typeof(data) == "string"){
		var span = $("<span>" + data +"</span>" + "<br/>");
		obj.append(span);
	}else{
		for (var key in data){
			var span = $("<span>" + key + "  :  " + data[key] + "</span>" + "<br/>");
			if( (key.indexOf("failed") >= 0 || key.indexOf("lost")>=0 ) && data[key] > 0){
				failedObj.val(data[key]);
				span.eq(0).addClass("monitor-text-danger");
			}
			obj.append(span);
		}
	}
}

function alarmFormat(str,obj,failedObj){
	var span = $("<span>" + str + "</span>" + "<br/>");
	var failNum = failedObj.val()
	if(failNum == "0"){
		span.eq(0).addClass("monitor-text-success");
	}else if(failNum == "1"){
		span.eq(0).addClass("monitor-text-danger");
	}else if(failNum == "2"){
		span.eq(0).addClass("monitor-text-serious");
	}
	
	obj.append(span);
}

function errorRecordFormat(json,obj){
	if(!$.isEmptyObject(json)){
		for (var key in json){
			if(typeof(json[key] == "object")){
				var span = $("<span>"+ key + "  :   " + JSON.stringify(json[key]) + "</span>" + "<br/>")
				obj.append(span);
			}else{
				var span = $("<span>"+ key + "  :   " + json[key] + "</span>" + "<br/>")
				obj.append(span);
			}
		}		
	}
}

function dataAppend(data,tableFlag,failedObj){
	for(var key in data){
		var tr = $("<tr></tr>");
		var tdStr = $("<td>"+ key + "</td>");
		var tdInfo = $("<td></td>");
		if( key == "message" ){
			var msgJson = strToJson(data[key]);
			if(!$.isEmptyObject(msgJson)){
				msgFormat(msgJson,tdInfo,failedObj)
			}
		}else if(key == "alarm"){
			alarmFormat(data[key],tdInfo,failedObj);
		}else if(key == "error_record"){
			errorRecordFormat(data[key],tdInfo)
		}else{
			var tdInfo = $("<td>"+ JSON.stringify(data[key]) + "</td>");
		}
		
		tr.append(tdStr).append(tdInfo);
		tableFlag.after(tr);
	}
}
/*预警管理页面查看详情通用函数 end*/