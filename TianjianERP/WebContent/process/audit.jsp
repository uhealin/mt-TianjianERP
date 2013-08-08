<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
   WebUtil webUtil=new WebUtil(request,response);
   String preUrl=webUtil.getPreUrl();
%>
<html>
<head>
<title>流程审批</title>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/form.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">

var hidenextUserDiv = false ;
function ext_init() {
	
	var tbar = new Ext.Toolbar({
		region:'north',
   		id:'tbar',
   		height:30
	});
	
	var disabled = false ;
	if("${apply}" == "true") {
		disabled = true ;
		
		tbar.add({ 
	        text:'保存',
	        icon:'${pageContext.request.contextPath}/img/save.gif' ,
	        handler:function(){
					        	
	        	if (!formSubmitCheck('thisForm')) {
	        		return;
	        	}
	        	
	        	if(funExists("beforeSave")) {
	        		var formId = document.getElementById("mt_formid").value;
	        		var uuid = document.getElementById("uuid").value;
	        		if(!beforeSave(formId, uuid)) {
	        			return;
	        		}
	        	}
	        	
	        	check("save");
			}
	  	}) ;
		
	}
	
	if("${param.apply}" == "true") {
		
		tbar.add({ 
	        text:'删除',
	        icon:'${pageContext.request.contextPath}/img/delete.gif' ,
	        handler:function(){
			    Ext.MessageBox.confirm("操作提示","是否要删除申请数据?",function(e){
			    	if(e!="yes")return;
			    	var uuid="${formEntityId}";
			    	var pkey="${param.pKey}";
			    	var formid="${formId}";
			    	var url="process.do";
			    	var param={uuid:uuid,formid:formid,method:"doDeleteFormData"};
			    	$.post(url,param,function(str){
			    		alert(str);
			    		closeTab(parent.parent.tab);
			    	});
			    });
			}
	  	}) ;
	}
	
	var isShowNextBtn = false  ;
	
	if("${taskId}" != "" && "${apply}" == "true") {
		isShowNextBtn = true ;
	}
	
	if("${view}" == "true") {
		isShowNextBtn = false ;
		//mt_form_initView();
	}
	
	if(isShowNextBtn) {
		tbar.add({ 
	        text:'下一步',
	        icon:'${pageContext.request.contextPath}/img/start.png' ,
	        //disabled:disabled,
	        handler:function(){
	        	goNext() ;
			}
	  	}) ;
	}
	
	if("${param.apply}" != "true"&&isShowNextBtn) {
		tbar.add({ 
	        text:'取消流程',
	        icon:'${pageContext.request.contextPath}/img/start.png' ,
	        //disabled:disabled,
	        handler:function(){
	       		//验证子表是否有数据
			    Ext.MessageBox.confirm("操作提示","确认要取消流程?",function(e){
			    	if(e!="yes")return;
			    	var uuid="${formEntityId}";
			    	var pkey="${param.pKey}";
			    	var url="process.do";
			    	var param={uuid:uuid,pkey:pkey,method:"doCancel"};
			    	$.post(url,param,function(str){
			    		alert(str);
			    		closeTab(parent.parent.tab);
			    	});
			    });
			}
	  	}) ;		
	}
	
	tbar.add('-',{ 
        text:'查看流程图',
        icon:'${pageContext.request.contextPath}/img/query.gif' ,
        handler:function(){
        	viewImage();
		}
  	}) ;
	
	var docReferrer = document.referrer ;
	if("${view}" != "true") {  
	/*	tbar.add('-',{     
		    text:'返回',
		    icon:'${pageContext.request.contextPath}/img/back.gif',
		    handler:function(){
		    	if("${apply}" == "true") {
		    		if("#"=="<%=preUrl%>"){
		    		window.location = "${pageContext.request.contextPath}/formDefine.do?method=formListView&uuid=${formId}" ;
		    		}else{
			    		window.location = "<%=preUrl%>" ;
		    		}
		    	}else {
		    		window.location = "${pageContext.request.contextPath}//process.do?method=auditList&pkey=${pKey}" ;
		    	}
		    	//window.history.back();
			}
		}) ;  */
	}
	
	tbar.add('-',{ 
	    text:'关闭',
	    icon:'${pageContext.request.contextPath}/img/close.gif',
	    handler:function(){
	    	closeTab(parent.tab); 
		}
	}) ;
	
	if("${apply}" == "true") {
		tbar.add(
			'<div style="margin-left:50px;color:#ff6600;">注：请先保存表单,再按【下一步】提交申请</div>'
		) ;
	}
	
	
	tbar.doLayout();
	
	var mytab = new Ext.TabPanel({
        id: "tab",
        region:'south',
        activeTab:0, //选中第一个 tab
        layoutOnTabChange:true, 
        forceLayout : true,
        deferredRender:false,
        height: 200
    });
	
	if("${view}" != "true") {
		mytab.add({title:"审批意见",id:"adviceTab",el:"adviceDiv"}) ;
	}else {
		document.getElementById("adviceDiv").style.display = "none" ;
	}
	mytab.add({title:"流程信息",id:"gridTab",el:"gridDiv"}) ;
	
	mytab.doLayout();
	
	var isRefresh = false ;
    mytab.on("tabchange",function(tabpanel,tab) {
   		if(tab.id == "gridTab") {
			if(!isRefresh) {
				goSearch_processTransferList();
			}
			isRefresh = true ; 
		}
		  
   	}) ;
	
	 //定义页面布局
	 var layout = new Ext.Viewport({
			layout:'border',
			items:[tbar,new Ext.Panel({
					region:'center',
					contentEl: 'center',
					margins:'0 0 0 0',
					autoScroll:true, 
			        lines:false
				}),mytab
			 ]
		});
	 
	 if("${apply}" == "true") {
		 layout.items.get(2).hide() ;
		 layout.doLayout();
	}
	 
	 /*
	 var store = new Ext.data.Store({   
	     proxy: new Ext.data.HttpProxy({      
	         url: '${pageContext.request.contextPath}/process.do?method=getDealUser'     
	     }),      
	     reader: new Ext.data.JsonReader({      
		     id: Ext.id()  
	     }, [      
	         {name: 'userId', mapping: 'userId'},      
	         {name: 'userName', mapping: 'userName'}      
	     ])      
    });   */
	 
	 var store = new Ext.data.JsonStore({
         autoLoad:false ,
         url : "${pageContext.request.contextPath}/process.do?method=getDealUser",
         fields : [{name : 'userId'}, {name:'userName'}]
     });
	 
	 var combo = new Ext.form.ComboBox({
		id:"nextUser",
        store: store,
        displayField:'userName',
        valueField : 'userId',
        typeAhead: true,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        width:150,
        emptyText:'请选择办理人...',
        selectOnFocus:true,
        lazyInit: true,
        autoLoad : false,
        transform: 'dealuserid',
        
        valueNotFoundText:"",
        listeners: {  
            beforequery: function(q){  
                delete q.combo.lastQuery;  
            }  
        } 
    });
	 
	 store.on('beforeload', function(st) {
		 var nodeName = getNextNode() ;
		 var transName = getNextTrans() ;
		 st.baseParams["nodeName"] = nodeName ;
		 st.baseParams["taskId"] = "${taskId}" ;
		 st.baseParams["pdId"] = "${pdId}" ;
		 st.baseParams["transName"] = transName ;
	//	 st.baseParams={nodeName:nodeName,taskId:"${taskId}",pdId:"${pdId}"};
	
		 var fieldObj = eval('${fieldJson}') ;
		 for(var i=0;i< fieldObj.length;i++){
			 if(fieldObj[i].isProcessVariable == "是") {
				 var obj = document.getElementById(fieldObj[i].name) ;
				 if(obj){
					 var fieldValue = obj.value ;
					 st.baseParams[fieldObj[i].enname] = fieldValue ;
				 }
			 }
		 }
	 }); 
 	
	 combo.on("select",function(){
		 var nodeName = getNextNode() ;
		 
		 if(nodeName == "") {
			 alert("请先选择下级节点!") ;
			 return ;
		 }
		 /*
		 store.load({callback:function(st,record){
			// alert(store.getTotalCount()) ; 这里可以处理只有一个人时的情况，现在暂没处理
		 }});*/
	 }) ;
	 
	 if(hidenextUserDiv) {
		 document.getElementById("nextUserDiv").style.display = "none" ;
	 }
	 
	 new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		columns:2,
		items:[{
            text: '同意',
            id:'agree', 
            icon:'${pageContext.request.contextPath}/img/start_32.png' ,
            scale: 'large',
               handler:function(){
            	   goNext() ;
   			   }
           },{
               text: '拒绝',
               id:'reject', 
               icon:'${pageContext.request.contextPath}/img/cancel.gif' ,
               scale: 'large',
               handler:function(){
            	   if(confirm("拒绝后流程将直接结束,是否确定?")) {
            		   reject();
	          	   }
               	  
      		   }
           }
        ]  
	});
	 
}

function goNext(){
	if(funExists("beforeNextStep")) {
 	   if(!beforeNextStep()) {
 			return;
 		}
    }
	   var nodesLength = document.getElementsByName("nextNode").length ;
	   
	   if(nodesLength == 1) {
		   
		   if(isEnd()) {
				check();
				return ;
		   }
		   
		   var nextUserArr = getNextUser() ;
		   if(nextUserArr.length == 1) {
			   //下级节点只有一个，并且办理人也只有一个就直接下一步
			   
				var obj = document.getElementById("mt_temp_advice");   
       	    if (obj.value.length > 1999){   
       	        alert("意见超过系统最大字数限制,最大字数为2000字,请重新输入!");
       	        return ;
       	    }   

       		
       		if (!formSubmitCheck('thisForm')) {
	        		return;
	        	}
	        	
	        	if(funExists("beforeNext")) {
	        		var formId = document.getElementById("mt_formid").value;
	        		var uuid = document.getElementById("uuid").value;
	        		var taskId = document.getElementById("taskId").value;
	        		if(!beforeNext(formId, uuid, taskId)) {
	        			return;
	        		}
	        	}
	        	
	        	Ext.getCmp("nextUser").setValue(nextUserArr[0].userId) ;
	        	
       		var goApplyUrl = document.getElementById("apply").value ;
       		document.getElementById("goApplyUrl").value = goApplyUrl;
       		document.getElementById("apply").value = "" ;
       		
       		if(confirm("该任务将提交给【" + nextUserArr[0].userName + "】办理,是否确定?")) {
       			check();
       		}
       		
		   }else {
				winFun();
		   }
	   }else {
			winFun(); 
	   }
}


function getNextUser(){
	var url = "${pageContext.request.contextPath}/process.do?method=getDealUser" ;
	
	 var nodeName = getNextNode() ;
	 var transName = getNextTrans() ;
	
	 var params = "&nodeName="+nodeName+"&taskId=${taskId}&pdId=${pdId}&transName="+transName ;
	 
	 var fieldObj = eval('${fieldJson}') ;
	 for(var i=0;i< fieldObj.length;i++){
		 if(fieldObj[i].isProcessVariable == "是") {
			 var obj = document.getElementById(fieldObj[i].name) ;
			 if(obj){
				 var fieldValue = obj.value ;
				 params += "&" + fieldObj[i].enname + "=" + fieldValue ;
			 }
		 }
	 }
	 
	 var responseText = ajaxLoadPageSynch(url,params) ;
	 
	 if(responseText) {
		 var nextUser = eval(responseText) ;
		 return nextUser ;
	 }
	 
}

	
var nextWin = null;
function winFun() {
	document.getElementById("nextDiv").style.display = "";
	if(nextWin == null) { 
		nextWin = new Ext.Window({
			title: '后续流程选择',
			width: 650,
			height:250,
			contentEl:'nextDiv', 
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("nextDiv").style.display = "none";
				}}
			},
	        layout:'fit',
		    buttons:[{
	            text:'确定',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	          		
	          		var obj = document.getElementById("mt_temp_advice");   
	          	    if (obj.value.length > 1999){   
	          	        alert("意见超过系统最大字数限制,最大字数为2000字,请重新输入!");
	          	        return ;
	          	    }   

	          		
	          		if (!formSubmitCheck('thisForm')) {
	          			nextWin.hide();
		        		return;
		        	}
		        	
		        	if(funExists("beforeNext")) {
		        		var formId = document.getElementById("mt_formid").value;
		        		var uuid = document.getElementById("uuid").value;
		        		var taskId = document.getElementById("taskId").value;
		        		if(!beforeNext(formId, uuid, taskId)) {
		        			nextWin.hide();
		        			return;
		        		}
		        	}
	          		var goApplyUrl = document.getElementById("apply").value ;
	          		document.getElementById("goApplyUrl").value = goApplyUrl;
	          		document.getElementById("apply").value = "" ;
	          		check();
	          	}
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	nextWin.hide();
	            }
	        }]
	    });
	}

	nextWin.show();
}


function setFieldReadOnly(id,name,type,tableName) {
	if(type == "mainFiled") {
		var field = Ext.getDom(id) ;
		
		if(field) {
			field.readOnly = true ;
			field.className = "readonly";
			field.backgroundImage = "none";
			field.value = field.value.replaceAll("请选择或输入...","") ;
			field.value = field.value.replaceAll("请选择...","") ;
		}
		
	}else if(type == "subList") {
		
		var imgs = Ext.query("img[flag="+tableName+"_del]") ;
		Ext.each(imgs,function(img){
			img.style.display = "none" ;
		}) ;
		
		var selectList = Ext.getDom("gridSeletList_"+tableName) ;
		if(selectList) {
			selectList.autoid = "" ;
			selectList.style.display = "none" ;
		}
		
	}else if(type == "subField") {
		var fields = Ext.query("input[name="+tableName+ "_" + id +"]") ;
		Ext.each(fields,function(field){
			field.readOnly = true ;
			field.className = "readonly";
			field.backgroundImage = "none";
			field.value = field.value.replaceAll("请选择或输入...","") ;
			field.value = field.value.replaceAll("请选择...","") ;
		}) ;
	}
}

window.attachEvent('onload',ext_init);
</script>

</head>
<body >

<!-- 表单区域DIV -->
<div id="center">
	<form name="thisForm" id="thisForm" class="formular"  method="post" >
		${formatHtml}
		
		<!-- 下一步人员和步骤选择DIV -->
		<div id="nextDiv" style="display:none;">
			<br/>
			<div style="margin:0 20 0 20">请选择下一环节：
				<c:forEach items="${nextTrans}" var="trans">
					
					<span>
					<input type="radio" name="nextNode" onclick="checkchage(this)" 
					<c:if test="${transSize <= 1}">
						checked=checked disabled="disabled"
					 </c:if> 
					value="${trans.name}" destName="${trans.destination.name}" >${trans.name} &nbsp;
					
					<!-- 只有一个节点并且下个节点是结束，就隐藏选人 -->
					<c:if test="${transSize <= 1 && (fn:indexOf(trans.destination.name,'结束') > -1 
							|| fn:indexOf(trans.destination.name,'end')>-1 
							|| fn:indexOf(trans.destination.name,'审结')>-1
							|| fn:indexOf(notSelectUserNodes,trans.destination.name)>-1)}">
						<script>
							hidenextUserDiv = true ;
						</script>   
					</c:if> 
				
					</span>
				</c:forEach>
			</div>
			<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
			
			<div style="margin:0 10 0 10" id=nextUserDiv>
				<table border="0" cellpadding="0" cellspacing="0" width="45%" align="left" >
				
					<td align="right">请选择下一环节办理人：</td>
					<td align=left>
						<input name=dealuserid id=dealuserid>
					</td>
				</tr>
				
				</table>
			
			</div>     
		</div>
		
		<input type="hidden" id="pId" name="pId" value="${pId}">
		<input type="hidden" id="pdId" name="pdId" value="${pdId}">
		<input type="hidden" id="pKey" name="pKey" value="${pKey}">
		<input type="hidden" id="taskId" name="taskId" value="${taskId}">
		<input type="hidden" id="formEntityId" name="formEntityId" value="${formEntityId}">
		<input type="hidden" id="formId" name="formId" value="${formId}">
		<input type="hidden" id="curNodeName" name="curNodeName" value="${curNodeName}">
		<input type="hidden" id="nextNodeName" name="nextNodeName">
		<input type="hidden" id="nextTrans" name="nextTrans">
		<input type="hidden" id="mt_process_nextUser" name="mt_process_nextUser">
		<input type="hidden" id="mt_process_advice" name="mt_process_advice">
		<input type="hidden" id="apply" name="apply" value="${apply}">
		<input type="hidden" id="goApplyUrl" name="goApplyUrl">
	</form>
</div>

<!-- 填写意见区域DIV -->
<div id=adviceDiv style="margin-left: 20px;">
	<table>
		
		<tr>
			<td>意见：</td>
			<td>
				<textarea id="mt_temp_advice" name="mt_temp_advice" cols="100" rows="5"></textarea>
			</td>
			<td><div id="sbtBtn"></div></td>
		</tr>
	</table>
</div>

<!-- 流程步骤gird区域DIV -->
<div id="gridDiv" style="height:120;width:100%">
	<mt:DataGridPrintByBean name="processTransferList"/>
</div>


<!-- 设置表单只读等属性 -->
<c:forEach items="${processFieldList}" var="field">
	<c:if test="${field.isReadOnly == '是'}">
		<script>
			setFieldReadOnly("${field.enname}","${field.name}","${field.type}","${field.tableName}") ;
		</script>
	</c:if>
</c:forEach>

</body>
</html>

<script language="javascript">
	
	//new Validation("thisForm");
	
	
	function pass() {
		document.thisForm.action = "${pageContext.request.contextPath}/process.do?method=audit" ;
		document.thisForm.submit();
	}
	
	function isEnd(){
		var nodeName = getNextNode();
		return !((nodeName.indexOf("结束") == -1) 
		&& (nodeName.indexOf("end") == -1) 
		&& (nodeName.indexOf("审结") == -1)  
		&& ("${notSelectUserNodes}".indexOf(nodeName) == -1)) ;
	}
	
	function check(type) {
		
		var transName = getNextTrans();
		var nodeName = getNextNode();
		
		var nextUser = Ext.getCmp("nextUser").getValue() ;
		if((nodeName.indexOf("结束") == -1) 
				&& (nodeName.indexOf("end") == -1) 
				&& (nodeName.indexOf("审结") == -1)  
				&& ("${notSelectUserNodes}".indexOf(nodeName) == -1)  
				&& type != "save") {
			if(nextUser == "") {
	  			alert("请选 择下级处理人") ;
	  			return false;
	  		}
		} 
		
		var mt_temp_advice = document.getElementById("mt_temp_advice").value ;
		document.getElementById("mt_process_advice").value = mt_temp_advice ;
		document.getElementById("mt_process_nextUser").value = nextUser ;
		document.thisForm.action = "${pageContext.request.contextPath}/process.do?method=dealTask" ;
		document.thisForm.submit();
	}
	
	function reject(){
		document.thisForm.action = "${pageContext.request.contextPath}/process.do?method=end" ;
		document.thisForm.submit();
	}
	
	
	function getNextNode(){
		var nextNode = document.getElementsByName("nextNode") ;
		
		var nodeName = "" ;
		for(var i=0;i<nextNode.length;i++) {
			if(nextNode[i].checked) {
				nodeName = nextNode[i].destName;
			}
		}
		document.getElementById("nextNodeName").value =  nodeName;
		
		return nodeName ;
	}
	
	function getNextTrans(){
		var nextNode = document.getElementsByName("nextNode") ;
		
		var nodeTrans = "" ;
		for(var i=0;i<nextNode.length;i++) {
			if(nextNode[i].checked) {
				nodeTrans = nextNode[i].value;
			}
		}
		document.getElementById("nextTrans").value =  nodeTrans;
		return nodeTrans ;
	}
	
	
	function viewImage() {
		
		var tab = parent.tab ;
		
        if(tab){
			n = tab.add({    
				'title':"流程图",    
				closable:true,  //通过html载入目标页    
				html:'<iframe name="imageFrm" scrolling="auto" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/process.do?method=viewImageByPIdOrKey&key=${pKey}&id=${pId}"></iframe>'   
			}); 
	        tab.setActiveTab(n);
		}else {
			window.open('${pageContext.request.contextPath}/process.do?method=viewImageByPIdOrKey&key=${pKey}&id=${pId}');
		}		
	}
	
	function checkchage(obj) {

		if(obj.checked) {
			Ext.getCmp("nextUser").setValue("") ;
			if(obj.destName.indexOf("结束") > -1 
					|| obj.destName.indexOf("end") > -1 
					|| obj.destName.indexOf("审结") > -1
					|| "${notSelectUserNodes}".indexOf(obj.destName) > -1) {
				document.getElementById("nextUserDiv").style.display = "none" ;
			}else {  
				document.getElementById("nextUserDiv").style.display = "" ;
			}
		}
	}
	
	function setAdvice(obj) {
		document.getElementById("mt_temp_advice").value = obj.value ;
	}

	Ext.onReady(function(){
		mt_form_initDateSelect();
		mt_form_initAttachFile();
		
		$("input[name=nextNode]:first").click();
	});
	
	
</script>
