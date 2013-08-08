
var BlockDiv = function() {
	this.show = function(text) {
		var blockDiv = document.getElementById("divBlock");

		if (blockDiv) {
			blockDiv.style.display = "";
		} else {
			var div = document.createElement("div");
			document.body.appendChild(div);
			div.id = "divBlock";
			div.style.cssText = "position:absolute;width:100%;height:100%; top:0px; left:0px; z-index:1; padding:0px; margin:0px; background:#000000;filter:alpha(opacity=30); text-align:center; ";
		}
		
		if(text != "") {
			div.innerHTML = "<span style='margin-top:200px;'><img src='" + MATECH_SYSTEM_WEB_ROOT + "/img/loading.gif'>&nbsp;<font color='#ffffff'><strong>" + text + "</strong><font></span>";
		} else {
			div.innerHTML = "";
		}
	};

	this.hidden = function() { 
		var blockDiv = document.getElementById("divBlock");
		if (blockDiv) {
			try {
				blockDiv.style.display = "none";
				document.body.removeChild(blockDiv);
			}catch(e){}
		}
	};
	
};

// strUrl:要访问的网页的绝对路径，但不带http://127.0.0.1:5199；但自己要带/开头！！！
function myOpenUrl(strUrl){

	try{
	
		// 获得当前窗口的地址和端口先
		// alert("http:\/\/"+window.location.host +strUrl);
		var t,t1;
		
		// 找到主操作界面
		t = window.opener;
		
		if(!t)// 如果是框架页则找它的父窗口
			t = window.parent;
		if (t){
			// alert('被新开窗口');
			t1 = t.window.opener;
			
			if(!t1)// 如果是框架页则找它的父窗口
				t1 = t.window.parent;	
			while (t1){
				t = t1;
				t1 = t.window.opener;
				// alert(t1)
				if(!t1){// 如果是框架页则找它的父窗口
					t1 = t.window.parent;
					if(t1.bottomFrame){
						break;
					}
				}	
			}
		}else{
			// alert('没有被新开窗口');
			t = window;	
		}
		
		// 在主操作界面中找到最上面的那个WINDOW
		t1 = t.parent;
		while (t1 && t1 != t){
			t = t1;
			t1 = t.window.parent;
		}
		
		// 找到最后的那个URL
		if (t){
			t.bottomFrame.myOpenUrl("http:\/\/"+window.location.host + strUrl);
			// t.open(strUrl);
		}
	}catch(e){
		window.open(strUrl);
	}	
	// oframe.OpenURLEx('http://127.0.0.1:5199/AuditSystem/taskCommon.do?method=fileOpen&UNID=239950844228867565&isBack=no&random=0.26142378553784257');
	// parent.bottomFrame.statu.value="exitSystem";
}

function myOpenUrlByWindowOpen(url, target, param) {

	var targetTemp = "_blank";
	var paramTemp = "channelmode=1, resizable=yes,toolbar=no,menubar=no,titlebar=no,scrollbars=yes";

	if (target != "") {
		targetTemp = target;
	}

	if (param != "") {
		paramTemp = param;
	}
	window.open(url, targetTemp, paramTemp);
}

function showWaiting(hight,wight,msg){
  var ShowDialog=1;
	if(msg==null||msg=="") {
		msg = "处理中，请稍等……";
		ShowDialog=0;
	}
  var obj=document.getElementById("waiting");
  if(!obj){
    var oBody = document.body;
  	oBody.insertAdjacentHTML("beforeEnd", "<div id='waiting' onselectstart='return false' ></div>");
    obj=document.getElementById("waiting");
  }

  if(hight==null||hight==""){
    hight="100%";
  }
  if(wight==null||wight==""){
    wight="100%";
  }
  
   var strTalk="";
  if (ShowDialog==0){
  	strTalk="<div id=bxDlg_bg1 oncontextmenu='return false' onselectstart='return false' style=\"position:absolute; width:100%;height:100%; top:expression(this.offsetParent.scrollTop); z-index:9999; padding:10px; background:#ffffff;filter:alpha(opacity=50); text-align:center;\"> </div>"
  			+ "<div style=\"position:absolute;width:230px;height:60px; z-index:2;left:expression((document.body.clientWidth-200)/2);top:expression(this.offsetParent.scrollTop + 130); border:1px solid #666666; padding:20px 40px 20px 40px; background:#E4E4E4; \"> "
    		// + " <img src='/AuditSystem/images/indicator.gif' />"
    		+ "<img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/images/loading.gif\">"
    		+ msg + "</div>";
  }else{
	  strTalk="<span id=bxDlg_bg align=center oncontextmenu='return false'"
	    +" onselectstart='return false' style='width:"+wight+";height:"+hight+";position:absolute;left:0;top:0'>"
	    +"<div id=bxDlg_bg1 style=height:100%;background:white;filter:alpha(opacity=50)> </div></span>"
	    +"<span  style='background:#E4E4E4;POSITION:absolute;padding:20px 40px 20px 40px;left:150.5;top:164.5;"
	    +" width:400px; height:200px;  border:1px solid #666666;'>"
	    + msg + "</span>";
  }
  obj.innerHTML=strTalk;
  obj.style.display = "" ;
}

function stopWaiting(){
	var obj =  document.getElementById("waiting") ;
	if(obj) {
	    obj.innerHTML="";
	    obj.style.display = "none" ;
    }
}
// -----------------------------------
// 把表单内的input拼成url字符串返回
// -----------------------------------
function formToRequestString(form_obj) {
	var query_string='';
	var and='';
	// alert(form_obj.length);
	for (var i=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		if ((e.tagName=='INPUT' || e.tagName=='SELECT' || e.tagName=='TEXTAREA') && e.name!='') {
			if (e.type=='select-one') {
				element_value=e.options[e.selectedIndex].value;
			} else if (e.type=='checkbox' || e.type=='radio') {
				if (e.checked==false) {
					// break;
					continue;
				}
				element_value=e.value;
			} else {
				element_value=e.value;
			}
			query_string+=and+e.name+'='+element_value.replace(/\&/g,"%26");
			and="&";
		}

	}
	return query_string;
}

// 异步
function ajaxLoadPage(url,request,container) {
	var loading_msg='正在加载数据,请稍候...';
	var loader;

	try {
		loader = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			loader = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			loader = false;
		}
	}

	loader.open("POST",url,true);
	loader.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	loader.onreadystatechange = function(){
		if (loader.readyState==1) {
			container.innerHTML=loading_msg;
			try {
				showWaiting("100%","100%");
			} catch(e) {

			}
		}

		if (loader.readyState==4) {
			container.innerHTML=loader.responseText;
			try {
				stopWaiting();
			} catch(e) {

			}
		}
	};

	loader.send(request);
}

//同步
function ajaxLoadPageSynch(url,request) {

	var loader;

	try {
		loader = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			loader = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			loader = false;
		}
	}

	loader.open("POST",url,false);
	loader.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	loader.send(request);

	return unescape(loader.responseText);
}


// 页面显示进度
var timer;   
function initMessage(key,time) {
	// 创建一个显示消息的等待框
	var msgBar = document.createElement("DIV") ;
	msgBar.className = "" ;
	msgBar.id = "msgBarDiv" ;
	msgBar.innerHTML = "<div class=\"msg_background_div\" id=\"bgDiv\"></div><div class=\"msg_info_div\" id=\"msg_info_div\"><div class=\"msg_center_div\" id=\"msg_center_div\"><strong>提示：</strong><p>请等待...</p></div></div>" ;
	document.body.appendChild(msgBar) ;
	timer = window.setTimeout("startMessageListener('"+key+"','"+time+"')",time); 
}   

var oXmlhttp;   
function startMessageListener(key,time){
	
	if(!oXmlhttp) { 
	    try{   
	        oXmlhttp = new ActiveXObject('Msxm12.XMLHTTP');   
	    }catch(e){   
	        try{   
	            oXmlhttp = new ActiveXObject('Microsoft.XMLHTTP');   
	        }catch(e){   
	            try{   
	                oXmlhttp = new XMLHttpRequest();   
	            }catch(e){}   
	        }   
	    } 
	}
	
    oXmlhttp.open("post",MATECH_SYSTEM_WEB_ROOT + "frontProcess.do?method=getMessage&key="+key+"&random="+Math.random(),true);   
     oXmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded"); 
     oXmlhttp.onreadystatechange = function(){
        if(oXmlhttp.readyState == 4){   
            if(oXmlhttp.status == 200){
            var msgCenter = document.getElementById("msg_center_div") ;
            var temp = oXmlhttp.responseText.indexOf("end");
            if (  temp > -1 ){
       			var msgBarDiv = document.getElementById("msgBarDiv");
       			if(msgBarDiv) {
       				msgBarDiv.style.display = "none" ;
       			}    			   
            	window.clearTimeout(timer);   
            }else{
            	 msgCenter.innerHTML = ""; 
           		 msgCenter.innerHTML = "<strong>提示：</strong><p>"+oXmlhttp.responseText+"</p>";
            	timer = window.setTimeout("startMessageListener('"+key+"')",time);   
            }   
            }   
        }   
    }
    oXmlhttp.send(null);   
}

// 表单提交post到另外一个新的标签页
function tabSubmit(form,url,tabTitle) {
	var randStr = Math.random();
      		
	var newTab = mainTab.add({    
		title:tabTitle,    
		closable:true,  // 通过html载入目标页
		html:'<iframe name="newTab_' + randStr + '" scrolling="auto" frameborder="0" width="100%" height="100%" src=""></iframe>'   
	}); 
	
	mainTab.setActiveTab(newTab);
	
	form.action = url;
	form.target = "newTab_" + randStr;
	form.submit();
}


// 提交表单前的通用检查
function formSubmitCheck(formid){
	var vd = new Ext.matech.form.Validation({formId:formid,tipType:"advice"});
	return vd.validate() ; 
}

// 在TAB里面打开新页面
function openTab(id,title,url,parent) {
	var n = parent.mainTab.getComponent(id);    
	if (!n) { // 判断是否已经打开该面板
		n = parent.mainTab.add({    
			 id:id,    
			 title:title,  
			 closable:true,  // 通过html载入目标页
			 html:'<iframe name="projectFrame" id="projectFrame" scrolling="yes" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
		});    
	} 	
	
	parent.mainTab.setActiveTab(n);
}

// 检查目录名合法性
function checkFileName(strFile){
	var reg= new RegExp("/^[^/\\:\*\?,\",<>\|]+$/ig"); 

	if(!reg.test(strFile)){
		return " ";
	}else{
		return strFile;
	}
}

// 关闭让前台设置
function notifyManuClose(contextPath,taskId,curProjectId){
	try {
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST",contextPath+"/taskCommon.do?method=fileClose&taskId="+taskId+"&projectId="+curProjectId, true);
		oBao.send();

	} catch(e) {
		//
	}
}

// 关闭标签页的方法
var closeTab = function(tab) {
	if(tab && tab.id == "mainFrameTab") {
		tab.remove(tab.getActiveTab()); 
	}else {
		window.close();
	}
}

function openFullWindow(url,target,oldUrl, localUrl) {
	var x = window.open(url,target,'top=0,left=0,width=' + (window.screen.availWidth-8) + ',height=' + (window.screen.availHeight-50) + ',resizable=no,menubar=no,toolbar=no,scrollbars=yes,status=no,location=no');
	try {
		if(!x) {
			alert('对不起,系统的弹出窗口给您的浏览器阻止了\n请【关闭弹窗口阻止程序】或【点击】浏览器上方黄色提示框,选择：总是允许来自此站点的弹出窗口'); 
			if(oldUrl || oldUrl != '') {
				window.location = oldUrl;
			}
			
		} else {
			window.location = localUrl;
		}
	} catch(e) {
		// alert(e);
	}
}

// 检查名称是否唯一
function checkQueryResultName(menuId, queryResultName) {
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=checkQueryResultName";
	var requestString = "menuId=" + menuId
					  + "&queryResultName=" + queryResultName;
					 
	var result = ajaxLoadPageSynch(url, requestString);
	// alert(requestString + "," + result);
	
	if(result == "false") {
		alert("该名称已经存在，请重新输入!!");
		return false;
	} else {
		return true;
	}
}

var queryResultSaveWin = null;
// 保存查询结果
function saveQueryResult(menuId, gridId, formId) {
	var html = "<div style=\"padding:5px;\">"
			 + "查询结果名称：<span class=\"mustSpan\">[*]</span>"
			 + " <input type=\"text\" name=\"queryResultName\" id=\"queryResultName\" class=\"required\" />"
			 + "</div> ";
	
	if(queryResultSaveWin == null) { 
	    queryResultSaveWin = new Ext.Window({
			title: '保存查询结果',
			width: 300,
			height:100, 
			html:html,
	        closeAction:'hide',
	        modal:true,
	        layout:'fit',
		    buttons:[{
	            text:'确定',
	            icon:MATECH_SYSTEM_WEB_ROOT + '/img/confirm.gif',
	          	handler:function() {
	          		var queryResultName = document.getElementById("queryResultName").value;
	          		
	          		// 检查名称是否唯一
	          		if(!checkQueryResultName(menuId, queryResultName)) {
	          			return;
	          		}
	          	
	          		if(queryResultName == "") {
	          			alert("请输入查询结果的名称");
	          			return;
	          		} else {
	          			
	          			
	          			var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=saveQueryResult";
						var requestString = "menuId=" + menuId
										  + "&gridId=" + gridId
										  + "&formId=" + formId
										  + "&queryResultName=" + queryResultName;
										 
						var result = ajaxLoadPageSynch(url, requestString);
						
						if(result == "ok") {
							// alert("保存查询结果成功!!");
						} 
						
						queryResultSaveWin.hide();
	          		}
	          	}
	        },{
	            text:'取消',
	            icon:MATECH_SYSTEM_WEB_ROOT + '/img/close.gif',
	            handler:function(){
	            	queryResultSaveWin.hide();
	            }
	        }]
	    });
	}
	queryResultSaveWin.show();
	
}

// 获取查询结果
function getQueryResult(paramString) {
	var params = paramString.split(",");
	for(var i=0; i < params.length; i++) {
		var keyValue = params[i].split("=");
		
		if(keyValue[1]) {
			document.getElementById(keyValue[0]).value = keyValue[1];
		}
	}
}

// -----------------------------------
// 重置标签里面的所有文本框、复选框、单选框等
// -----------------------------------
function reset(objId) {
	var obj = document.getElementById(objId);
	
	for (i = 0; i < obj.length; i++ ) {
		e = obj[i];
		if ((e.tagName=='INPUT' || e.tagName=='SELECT' || e.tagName=='TEXTAREA') && e.name!='') {
		
			if (e.type=='text') {
				e.value = "";
			}else if (e.type=='select-one') {
				e.value = "";
			} else if (e.type=='checkbox' || e.type=='radio') {
				e.checked = false;
			} else {
				try{
					Ext.getCmp(e.id).clear();
				} catch(e) {
					e.value = "";
				}
			}
		}
	}
}

// 初始化图形
function createChart(url, chartType , chartId, height, width) {
	var chartDiv = document.getElementById("chartDiv_" + chartId);
	var chartXML = document.getElementById("chartXML_" + chartId);
	var chartURL = document.getElementById("chartURL_" + chartId);
	var chartTypeObj = document.getElementById("chartType_" + chartId);

	if(!chartDiv) {
		document.write("<div id=\"chartDiv_" + chartId + "\" align=\"center\"></div>");
		chartDiv = document.getElementById("chartDiv_" + chartId);
	}
	
	if(!chartXML) {
		document.write("<input type=\"hidden\" id=\"chartXML_" + chartId + "\" value=\"\"> ");
		chartXML = document.getElementById("chartXML_" + chartId);
	}
	
	if(!chartURL) {
		document.write("<input type=\"hidden\" id=\"chartURL_" + chartId + "\" value=\"\"> ");
		chartURL = document.getElementById("chartURL_" + chartId);
	}
	
	if(!chartTypeObj) {
		document.write("<input type=\"hidden\" id=\"chartType_" + chartId + "\" value=\"\"> ");
		chartTypeObj = document.getElementById("chartType_" + chartId);
	}
	
	var request = "&chartType=" + chartType;
	strXML = ajaxLoadPageSynch(url, request);
	
	chartURL.value = url;
	chartXML.value = strXML;
   	chartTypeObj.value = chartType;
    
    changeChart(chartType, chartId, height, width);

} 

// 改变图形类型
function changeChart(chartType, chartId, height, width) {
	var chartXML = document.getElementById("chartXML_" + chartId).value;
	var height = height || (document.body.clientHeight-54)/2;
	var width = width || document.body.clientWidth;
	var chart = new FusionCharts(MATECH_SYSTEM_WEB_ROOT + "/charts/" + chartType + ".swf", chartId, width, height);
    chart.addParam("wmode","Opaque");
    chart.setDataXML(chartXML);
    chart.render("chartDiv_" + chartId);
}

// 更新图形数据
function updateChart(url, param, chartId){
	var chartType = document.getElementById("chartType_" + chartId);
	var request = "&chartType=" + chartType + param;
	strXML = ajaxLoadPageSynch(url, request);
	document.getElementById("chartXML_" + chartId).value = strXML;

	updateChartXML(chartId, strXML); 
}

function getUUID() {
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=getUUID";
	var result = ajaxLoadPageSynch(url, null);
	
	if(result == "") {
		result = Math.random();
	}
	
	return result;
}

var attachUploadWin = null;
var attachUploadForm = null;

// 上传附件
function attachUpload(inputId,mode,imgId) {
	var inputObj = document.getElementById(inputId);
	
	var indexTable = inputObj.indexTable;
	var indexId = inputObj.value;
	mode=mode||"";
	
	if(!checkMaxAttach(inputId)) {
		return;
	}
	
	if(attachUploadForm == null) {
		attachUploadForm = new Ext.FormPanel({
			url: "",
			border:false,
	        fileUpload: true,
	        autoHeight: true,
	        autoWidth: true,
	        frame: true,
			bodyStyle: 'padding: 5px;',
	        labelWidth: 1,
	        defaults: {
	            anchor: '95%',
	            allowBlank: false,
	            msgTarget: 'side'
	        },
	        items: [{
	            xtype: 'fileuploadfield',
	            id: 'form-file',
	            emptyText: '请选择需要上传的文件',
	            name: 'attachPath',
	            buttonText: '',
	            buttonCfg: {
	            	text:'选择文件'
	            }
	        }]
	    });
	} else {
		attachUploadForm.getForm().reset();
	}
	
	//每次重置表单url地址
	attachUploadForm.form.url = MATECH_SYSTEM_WEB_ROOT + '/common.do?method=attachUpload&indexTable=' + indexTable + "&indexId=" + indexId+"&mode="+mode;
	//改为每次创建新窗口
    attachUploadWin = new Ext.Window({
		title: '文件上传',
		width: 500,
		height:116,
        modal:true,
        resizable:false,
		layout:'fit',
		closeAction:'hide',
		items: attachUploadForm,
		buttons: [{
            text: '确定',
            icon:MATECH_SYSTEM_WEB_ROOT + '/img/confirm.gif',
            handler: function(){
                if(attachUploadForm.getForm().isValid()){
                	// 显示进度条
                	Ext.MessageBox.show({ 
					    title: '上传文件', 
					    width:240, 
					    progress:true, 
					    closable:false
					}); 
				
					// 提交表单
	                attachUploadForm.getForm().submit();
	                
	                var i = 0;
				    var timer = setInterval(function(){
						// 请求事例
						Ext.Ajax.request({
							url: MATECH_SYSTEM_WEB_ROOT + '/common.do?method=attachUploadProcess&rand=' + Math.random(),
							method: 'post',
							// 处理ajax的返回数据
							success: function(response, options){
								status = response.responseText + " " + i++;
								var obj = Ext.util.JSON.decode(response.responseText);
								if(obj.success!=false){
									if(obj.finished){
										clearInterval(timer);	
										// status = response.responseText;
										Ext.MessageBox.updateProgress(1, 'finished', 'finished');
										Ext.MessageBox.hide();
										attachUploadWin.hide();
										if(imgId){
										attachImageInit(inputId,imgId);	
										}else{
										attachInit(inputId);
										}
									} else {
										Ext.MessageBox.updateProgress(obj.percentage, obj.msg);	
									}
								}
							},
							failure: function(){
								clearInterval(timer);
								Ext.Msg.alert('错误', '上传文件出错。');
							} 
						});
				    }, 500);
                }
            }
        },{
            text: '重置',
            icon:MATECH_SYSTEM_WEB_ROOT + '/img/refresh.gif',
            handler: function(){
                attachUploadForm.getForm().reset();
            }
       	},{
       		text: '取消',
       		icon:MATECH_SYSTEM_WEB_ROOT + '/img/close.gif',
       		handler: function(){
       			attachUploadWin.hide();
       		}
       	}]
    });
	attachUploadWin.show();
}

// 格式化数字
function formatDecimal(x,maxLength) {
   var f_x = parseFloat(x);
   if (isNaN(f_x)) {
      return x;
   }
   var f_x = Math.round(x*100)/100;
   var s_x = f_x.toString();
   var pos_decimal = s_x.indexOf('.');
   if (pos_decimal < 0) {
      pos_decimal = s_x.length;
      s_x += '.';
   }
   while (s_x.length <= pos_decimal + maxLength) {
      s_x += '0';
   }
   return s_x;
}

// 截取字符最大长度
function maxString(str) {
	if(str.length > 25) {
		str = str.substring(0,22) + "...";
	} 
	
	return str;
}

// 检查最大附件数
function checkMaxAttach(inputId) {
	var inputObj = document.getElementById(inputId);
	var maxAttach = inputObj.maxAttach || 0;
	
	if(maxAttach != 0 && getAttachCount(inputId) >= maxAttach) {
		alert("对不起，只允许上传" + maxAttach + "个文件,请先删除后再上传!");
		return false;
	} else {
		return true;
	}
}

// 获取附件数量
function getAttachCount(inputId) {
	var inputObj = document.getElementById(inputId);
	var prefix = inputObj.id;
	var attachUlId = "attachUl_" + prefix; 
	
	return document.getElementById(attachUlId).children.length;
}

function attachImageInit(inputId,imgId) {
	var inputObj = document.getElementById(inputId);
	//alert(imgId);
	// 按钮文字,默认为添加附件
	var buttonText = inputObj.buttonText || "添加图片";
	
	var showButton = true;
	var remove = true;
	
	if(inputObj.readOnly) {
		showButton = false;
		remove = false;
	}
	if(inputObj["attachFile"]=="true"){
		showButton = true;
		remove = true;
	}
	
	
	//不再单独控制，通过只读来设置
	//
	// 是否显示上传按钮,默认为true
	//var showButton = inputObj.showButton == false ? false : true;
	// 是否允许删除,默认为true
	//var remove = inputObj.remove == false ? false : true;
	
	var indexTable = inputObj.indexTable||inputObj.indextable;
	
	if(inputObj.value == "") {
		inputObj.value = getUUID();
	}
	
	var indexId = inputObj.value;
	var prefix = inputObj.id;
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=getAttachList";
	var request = "indexTable=" + indexTable + "&indexId=" + indexId;
	
	var result = ajaxLoadPageSynch(url, request);
	//alert(result);
	var attachList = Ext.util.JSON.decode(result);
	
	var html = "";
	if(attachList.length>0){
		var src=MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attachList[0].attachId;
		//alert(src);
		document.getElementById(imgId).src=src;
	}
	/*
	for(var i=0; i < attachList.length; i++) {
		var attach = attachList[i];
		html += "<li>"
			  + "<span>"
			  + "<a href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"下载：" + attach.attachName + "\">" + maxString(attach.attachName) + "</a>"
			  + "&nbsp;<font style=\"color:#CCCCCC;\">" + formatDecimal((attach.fileSize/1024),2) + " KB</font>"
			  + "</span>"
			  + "&nbsp;<a href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"下载：" + attach.attachName + "\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/download.gif\"></a>";
		
		if(remove) {
			html += "&nbsp;<a href=\"#\" onclick=\"attachRemove('" + attach.attachId + "','" + inputId + "');\" title=\"删除\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/delete.gif\"></a>";
		}
		
		html += "</li>";
	}*/
	//alert(11);
	var attachUlId = "attachUl_" + prefix; 
	var attachButtonId = "attachButton_" + prefix;
	var attachDivId = "attachDiv_" + prefix;
	
	var ul = document.getElementById(attachUlId);
	if(ul == null || !ul) {
		
		var divObj = document.createElement("<div id=\"" + attachDivId + "\"></div>");
					
		divObj = inputObj.parentElement.insertBefore(divObj);
		
		var buttonDiv = document.createElement("<div id=\"" + attachButtonId +"\"></div>");
		ul = document.createElement("<ul id=\"" + attachUlId + "\"></ul>");
		
		divObj.appendChild(buttonDiv);
		divObj.appendChild(ul);
	}

	ul.innerHTML = html;
	
	// 是否显示按钮
	if(showButton) {
		var attachButton = document.getElementById(attachButtonId);
		attachButton.innerHTML = "<input type=\"button\" class=\"flyBT\" value=\"" + buttonText + "\" onclick=\"attachUpload('" + inputId + "','single','"+imgId+"')\" ><br/><br/>";
	}
}


if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment)
{
     Range.prototype.createContextualFragment = function(html)
     {
         var frag = document.createDocumentFragment(),  
         div = document.createElement("div");
         frag.appendChild(div);
         div.outerHTML = html;
         return frag;
     };
}

function mycreateElement(html,objtype,id){
	try{  
		return document.createElement(html);
	}catch(e){
		//ie9以上版本
		var new_name_item = document.createElement(objtype);  
        new_name_item.id = id;  
		return new_name_item;
	}
	
	
}


function attachInit(inputId) {
	var inputObj = document.getElementById(inputId);
	
	// 按钮文字,默认为添加附件
	var buttonText = inputObj.buttonText || "添加附件";
	
	var showButton = true;
	var remove = true;
	
	if(inputObj.readOnly) {
		showButton = false;
		remove = false;
	}
	if(inputObj["attachFile"]=="true"){
		showButton = true;
		remove = true;
	}
	
	
	//不再单独控制，通过只读来设置
	//
	// 是否显示上传按钮,默认为true
	//var showButton = inputObj.showButton == false ? false : true;
	// 是否允许删除,默认为true
	//var remove = inputObj.remove == false ? false : true;
	
	var indexTable = inputObj.indexTable||inputObj.indextable;
	
	if(inputObj.value == "") {
		inputObj.value = getUUID();
	}
	
	var indexId = inputObj.value;
	var prefix = inputObj.id;
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=getAttachList";
	var request = "indexTable=" + indexTable + "&indexId=" + indexId;
	
	var result = ajaxLoadPageSynch(url, request);
	var attachList = Ext.util.JSON.decode(result);
	
	var html = "";
	for(var i=0; i < attachList.length; i++) {
		var attach = attachList[i];
		html += "<li>"
			  + "<span>"
			  + "<a class='aAtt' href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"下载：" + attach.attachName + "\">" + maxString(attach.attachName) + "</a>"
			  + "&nbsp;<font style=\"color:#CCCCCC;\">" + formatDecimal((attach.fileSize/1024),2) + " KB</font>"
			  + "</span>"
			  + "&nbsp;<a href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"下载：" + attach.attachName + "\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/download.gif\"></a>";
		
		if(remove) {
			html += "&nbsp;<a href=\"#\" onclick=\"attachRemove('" + attach.attachId + "','" + inputId + "');\" title=\"删除\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/delete.gif\"></a>";
		}
		
		html += "</li>";
	}
	//alert(11);
	var attachUlId = "attachUl_" + prefix; 
	var attachButtonId = "attachButton_" + prefix;
	var attachDivId = "attachDiv_" + prefix;
	
	var ul = document.getElementById(attachUlId);
	if(ul == null || !ul) {
		
		var divObj = mycreateElement("<div id=\"" + attachDivId + "\"></div>","div",attachDivId);
					
		divObj = inputObj.parentElement.insertBefore(divObj);
		
		var buttonDiv = mycreateElement("<div id=\"" + attachButtonId +"\"></div>","div",attachButtonId);
		ul = mycreateElement("<ul id=\"" + attachUlId + "\"></ul>","ul",attachUlId);
		
		divObj.appendChild(buttonDiv);
		divObj.appendChild(ul);
	}

	ul.innerHTML = html;
	
	// 是否显示按钮
	if(showButton) {
		var attachButton = document.getElementById(attachButtonId);
		//alert(inputObj.readOnly);
		if(inputObj.readOnly==true){
			attachButton.innerHTML = "<input type=\"button\"  class=\"flyBT\" value=\"" + buttonText + "\" onclick=\"attachUpload('" + inputId + "')\" disabled=\"disabled\" /><br/><br/>";
		}else{
			attachButton.innerHTML = "<input type=\"button\" class=\"flyBT\" value=\"" + buttonText + "\" onclick=\"attachUpload('" + inputId + "')\"  /><br/><br/>";
		}
	}
}

// 删除附件
function attachRemove(attachId, inputId) {
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachRemove";
	var request = "attachId=" + attachId;
	var result = ajaxLoadPageSynch(url, request);
	
	if(result == "success") {
		attachInit(inputId);
	}
}

// 检查开始年份和结束年份 startYear endYear

function chkYear(){
	var startYear = document.getElementById("startYear").value;
	var endYear = document.getElementById("endYear").value;

	if(startYear != "" && endYear ==""){
		alert("请同时选择结束年份！");
		document.getElementById("endYear").foucs();
		return false;
	}
	if(startYear == "" && endYear !=""){
		alert("请同时选择开始年份！");
		document.getElementById("startYear").foucs();
		return false;
	}
	if(startYear != "" && endYear !=""){
		if(endYear <= startYear){
			alert("结束年份必须大于开始年份！");
			return false;
		}
	}
	return true;
}


// 创建ajax 请求
function createRequest() {
	var request;
	  try {
	    request = new XMLHttpRequest();
	  } catch (trymicrosoft) {
	    try {
	      request = new ActiveXObject("Msxml2.XMLHTTP");
	    } catch (othermicrosoft) {
	      try {
	        request = new ActiveXObject("Microsoft.XMLHTTP");
	      } catch (failed) {
	        request = false;
	      }
	    }
	  }
	  if (!request)
	    alert("Error initializing XMLHttpRequest!");
	  
	  return request;
}



//调整gird框无法适应浏览器resize
//针对将grid框放到TabPanel中的情况
//_fromObj:grid框所属容器，该容器会随着浏览器变动而自动调整高度与长度
//_toObj:为需要根据_fromObj进行手工调整的gird的ID字符串，如：gridId_myDealList,gridId_myApplyList
//_adjSize[长度，高度]:需要减去的长度与高度，微调使用
//添加日期：2012-3-16
function resizeGridPanel(_fromObj,_toObj,_adjSize){
	var _resizeInterval;//计时器
	//监听浏览器变动
	Ext.EventManager.onWindowResize (function(){
		_resizeInterval=setInterval(GridPanelResize,500);
	});
	//调整页面gridpanel长度和宽度
	function GridPanelResize(){
		var realWidth=Ext.getCmp(_fromObj).getWidth();
		var realHeight=Ext.getCmp(_fromObj).getHeight();
		var gridPanels=_toObj.split(",");
		for(var i=0;i<gridPanels.length;i++){			
			Ext.getCmp(gridPanels[i]).setWidth(realWidth-_adjSize[i][0]);
			Ext.getCmp(gridPanels[i]).setHeight(realHeight-_adjSize[i][1]);
		}
		clearInterval(_resizeInterval);
	}	
}
//针对单个grid放到页面的情况
function resizeSingleGridPanel(_toObj,_adjWidth,_adjHeigh){
	var _resizeInterval;//计时器
	//监听浏览器变动
	Ext.EventManager.onWindowResize (function(){
		_resizeInterval=setInterval(GridPanelResize,500);
	});
	//调整页面gridpanel长度和宽度
	function GridPanelResize(){
		var realWidth=Ext.getBody().getWidth()-_adjWidth;
		var realHeight=Ext.getBody().getHeight()-_adjHeigh;
		
		Ext.getCmp(_toObj).setWidth(realWidth);
		Ext.getCmp(_toObj).setHeight(realHeight);
		
		clearInterval(_resizeInterval);
	}	
}
//隐藏grid框的刷新按钮
function hideMyExtGridComponent(itemContainer,itemIndex){
	var _hideExtComponentInterval;
	_hideExtComponentInterval=setInterval(hideComponent,500);
	function hideComponent(){
		if(Ext.getCmp(itemContainer)){
			Ext.getCmp(itemContainer).get(itemIndex-1).setVisible(false);
			Ext.getCmp(itemContainer).get(itemIndex).setVisible(false);
			clearInterval(_hideExtComponentInterval);	
		}
	}
}
//权限判断函数
function optPriviligeJudge(curPrivilige,sysPrivilige){
	if(sysPrivilige.indexOf(curPrivilige)>=0){
		return true;
	}else{
		Ext.MessageBox.alert("提示信息","没有操作权限！");
		return false;
	}
}
//阻止input按钮在disabled和readOnly时按backspace返回前一个页面
Ext.EventManager.on(Ext.getBody(),"keydown",function(e, t) {   
    if (e.getKey() == e.BACKSPACE &&(t.disabled || t.readOnly)) {   
        e.stopEvent();   
    }
});


//判断JS函数是否存在
function funExists(funName){ 
	try{  
		if(typeof eval(funName)=="undefined"){
			return false;
		} 
		if(typeof eval(funName)=="function"){
			return true;
		}
	} catch(e){
		return false;
	}
}

//js 小写人民币转化为大写人民币
function RMBToCapital(num) { //转成人民币大写金额形式
    var str1 = '零壹贰叁肆伍陆柒捌玖'; //0-9所对应的汉字
    var str2 = '万仟佰拾亿仟佰拾万仟佰拾元角分'; //数字位所对应的汉字
    var str3; //从原num值中取出的值
    var str4; //数字的字符串形式
    var str5 = ''; //人民币大写金额形式
    var i; //循环变量
    var j; //num的值乘以100的字符串长度
    var ch1; //数字的汉语读法
    var ch2; //数字位的汉字读法
    var nzero = 0; //用来计算连续的零值是几个
    num = Math.abs(num).toFixed(2); //将num取绝对值并四舍五入取2位小数
    str4 = (num * 100).toFixed(0).toString(); //将num乘100并转换成字符串形式
    j = str4.length; //找出最高位
    if (j > 15) {
        return '溢出';
    }
    str2 = str2.substr(15 - j); //取出对应位数的str2的值。如：200.55,j为5所以str2=佰拾元角分
    //循环取出每一位需要转换的值
    for (i = 0; i < j; i++) {
        str3 = str4.substr(i, 1); //取出需转换的某一位的值
        if (i != (j - 3) && i != (j - 7) && i != (j - 11) && i != (j - 15)) { //当所取位数不为元、万、亿、万亿上的数字时
            if (str3 == '0') {
                ch1 = '';
                ch2 = '';
                nzero = nzero + 1;
            }
            else {
                if (str3 != '0' && nzero != 0) {
                    ch1 = '零' + str1.substr(str3 * 1, 1);
                    ch2 = str2.substr(i, 1);
                    nzero = 0;
                }
                else {
                    ch1 = str1.substr(str3 * 1, 1);
                    ch2 = str2.substr(i, 1);
                    nzero = 0;
                }
            }
        }
        else { //该位是万亿，亿，万，元位等关键位
            if (str3 != '0' && nzero != 0) {
                ch1 = "零" + str1.substr(str3 * 1, 1);
                ch2 = str2.substr(i, 1);
                nzero = 0;
            }
            else {
                if (str3 != '0' && nzero == 0) {
                    ch1 = str1.substr(str3 * 1, 1);
                    ch2 = str2.substr(i, 1);
                    nzero = 0;
                }
                else {
                    if (str3 == '0' && nzero >= 3) {
                        ch1 = '';
                        ch2 = '';
                        nzero = nzero + 1;
                    }
                    else {
                        if (j >= 11) {
                            ch1 = '';
                            nzero = nzero + 1;
                        }
                        else {
                            ch1 = '';
                            ch2 = str2.substr(i, 1);
                            nzero = nzero + 1;
                        }
                    }
                }
            }
        }
        if (i == (j - 11) || i == (j - 3)) { //如果该位是亿位或元位，则必须写上
            ch2 = str2.substr(i, 1);
        }
        str5 = str5 + ch1 + ch2;

        if (i == j - 1 && str3 == '0') { //最后一位（分）为0时，加上"整"
            str5 = str5 + '整';
        }
    }
    if (num == 0) {
        str5 = '零元整';
    }
    return str5;
}

//把2012改成贰零壹贰，转为票据打印使用
function DateToCapital(rq){
   if (rq){
   		rq=replaceAll(rq,'0','零');
   		rq=replaceAll(rq,'1','壹');
		rq=replaceAll(rq,'2','贰');
   		rq=replaceAll(rq,'3','叁');
   		rq=replaceAll(rq,'4','肆');
   		rq=replaceAll(rq,'5','伍');
   		rq=replaceAll(rq,'6','陆');
   		rq=replaceAll(rq,'7','柒');
   		rq=replaceAll(rq,'8','捌');
   		rq=replaceAll(rq,'9','玖');
   		return rq;
   }else{
   		return '';
   } 
}


function setObjDisabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=true;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=true;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=true;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=true;
			if(sType=="CHECKBOX")oElem.disabled=true;
			if(sType=="RADIO")oElem.disabled=true;
			}
			break;
		default:
			oElem.disabled=true;
			break;
		}
	//set style
	oElem.style.backgroundColor="#eeeeee";
}

function setObjEnabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=false;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=false;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=false;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=false;
			if(sType=="CHECKBOX")oElem.disabled=false;
			if(sType=="RADIO")oElem.disabled=false;
			}
			break;
		default:
			oElem.disabled=false;
			break;
		}
	//set style
	oElem.style.backgroundColor="#FFFFFF";
}

//替换所有字符
function replaceAll(str,oldStr,newStr) {
	return str.replace(new RegExp(oldStr,"gm"),newStr); 
}

function show_selectUser(objName,hideUserId,mode){
	var objParameter = new Object();
	objParameter.userName = objName;
	objParameter.userId = hideUserId;
	objParameter.partentWindowObj = window;
    mode=mode||"";
	window.showModalDialog(MATECH_SYSTEM_WEB_ROOT+"user/selectUser.jsp?mode="+mode,objParameter,"dialogHeight:420px;dialogWidth:500px;resizable:false;dialogHide:no;status:no;location=no;");
}

function show_selectJob(idFieldName,idFieldId,type){
	var objParameter = new Object();
	objParameter.idFieldName = idFieldName;
	objParameter.idFieldId = idFieldId;
	objParameter.partentWindowObj = window;
    type=type||"";
    
	window.showModalDialog(MATECH_SYSTEM_WEB_ROOT+"hr/selectJob.jsp?type="+type,objParameter,"dialogHeight:600px;dialogWidth:750px;resizable:false;dialogHide:no;status:no;location=no;");
}

function createNewWord(tempUrl,newUrl){
	var openDocObj = new ActiveXObject("SharePoint.OpenDocuments.2"); 
	openDocObj.CreateNewDocument(tempUrl, newUrl);
}

function editWord(url){
	var openDocObj = new ActiveXObject("SharePoint.OpenDocuments.2"); 
    openDocObj.EditDocument(url);
}

function viewWord(url){
	var openDocObj = new ActiveXObject("SharePoint.OpenDocuments.2"); 
	openDocObj.ViewDocument(url);
}

Array.prototype.contains = function (element) { 
	for (var i = 0; i < this.length; i++) { 
		if (this[i] == element) { 
			return true; 
		} 
	} 
	return false; 
}

function getParamObject() 
{ 
	var args = new Object( ); //声明一个空对象 
	var query = window.location.search.substring(1); // 取查询字符串，如从 http://www.snowpeak.org/testjs.htm?a1=v1&a2=&a3=v3#anchor 中截出 a1=v1&a2=&a3=v3。 
	var pairs = query.split("&"); // 以 & 符分开成数组 
	for(var i = 0; i < pairs.length; i++) { 
		var pos = pairs[i].indexOf('='); // 查找 "name=value" 对 
		if (pos == -1) continue; // 若不成对，则跳出循环继续下一对 
		var argname = pairs[i].substring(0,pos); // 取参数名 
		var value = pairs[i].substring(pos+1); // 取参数值 
		value = decodeURIComponent(value); // 若需要，则解码 
		args[argname] = value; // 存成对象的一个属性 
    } 
return args; // 返回此对象 
} 

function doUpdateSubsetID(){
	   var row=mt_form_getRowValues()[0];
	   var uuid=row.uuid;
	   var id=row.id;
	   var formid=getParamObject()["uuid"];
	   var url="employment.do";
	   
	   Ext.MessageBox.prompt("修改子集编号","请输入新的子集编号，必须为数字",function(e,text){
	       if(e!="ok")return;
	       var param={method:"doUpdateSubsetID",formid:formid,uuid:uuid,newID:text};
	       $.post(url,param,function(str){
	         alert(str);
	         window.location.reload();
	      });
	   });
	}


//身份证验证
function validateIdCard(obj)
{
 var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"};
  var iSum = 0;
 //var info = "";
 var strIDno = obj;
 var idCardLength = strIDno.length;
 if(!/^\d{17}(\d|x)$/i.test(strIDno)&&!/^\d{15}$/i.test(strIDno))
        return 1; //非法身份证号

 if(aCity[parseInt(strIDno.substr(0,2))]==null)
 return 2;// 非法地区

  // 15位身份证转换为18位
 if (idCardLength==15)
 {
    sBirthday = "19" + strIDno.substr(6,2) + "-" + Number(strIDno.substr(8,2)) + "-" + Number(strIDno.substr(10,2));
  var d = new Date(sBirthday.replace(/-/g,"/"))
  var dd = d.getFullYear().toString() + "-" + (d.getMonth()+1) + "-" + d.getDate();
  if(sBirthday != dd)
                return 3; //非法生日
              strIDno=strIDno.substring(0,6)+"19"+strIDno.substring(6,15);
              strIDno=strIDno+GetVerifyBit(strIDno);
 }

       // 判断是否大于2078年，小于1900年
       var year =strIDno.substring(6,10);
       if (year<1900 || year>2078 )
           return 3;//非法生日

    //18位身份证处理

   //在后面的运算中x相当于数字10,所以转换成a
    strIDno = strIDno.replace(/x$/i,"a");

  sBirthday=strIDno.substr(6,4)+"-"+Number(strIDno.substr(10,2))+"-"+Number(strIDno.substr(12,2));
  var d = new Date(sBirthday.replace(/-/g,"/"))
  if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate()))
                return 3; //非法生日
    // 身份证编码规范验证
  for(var i = 17;i>=0;i --)
   iSum += (Math.pow(2,i) % 11) * parseInt(strIDno.charAt(17 - i),11);
  if(iSum%11!=1)
                return 1;// 非法身份证号

   // 判断是否屏蔽身份证
    var words = new Array();
    words = new Array("11111119111111111","12121219121212121");

    for(var k=0;k<words.length;k++){
        if (strIDno.indexOf(words[k])!=-1){
            return 1;
        }
    }

 return 0;
}


function viewCadet(){
	   var row=mt_form_getRowValues()[0];
	   var uuid=row.uuid;
	   var url="cadet.do?method=view&mode=view&uuid="+uuid;
	   window.showModalDialog(url,{},"dialogWidth:800px;dialogHeight:500px;status=no;location=no;resizable=yes");

	
}


function mt_open(url,title,width,height){
	window.open(url);
	//window.open(url,title,'height='+height+', width='+width+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
	
}
