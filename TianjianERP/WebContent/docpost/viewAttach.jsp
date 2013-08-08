<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.audit.service.user.model.UserVO"%>
<%@page import="com.matech.audit.service.doc.model.DocPostVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.MtComAttachVO"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.AttachExtVO"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.AttachFile"%>
<%@page import="java.util.List"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.service.attachFileUploadService.AttachFileUploadService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
    
 <%
      String uuid=request.getParameter("uuid");
      WebUtil webUtil=new WebUtil(request,response);
      UserSession userSession=webUtil.getUserSession();
      Connection conn=null;
      DbUtil dbUtil=null;
      DocPostVO docPostVO=null;
      
      try{
    	  conn=new DBConnect().getConnect();
    	  dbUtil=new DbUtil(conn);
    	  docPostVO=dbUtil.load(DocPostVO.class,uuid);
      }catch(Exception ex){
    	  
      }finally{
    	  
      }
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>发文附件:<%=docPostVO.getTitle() %></title>

<script type="text/javascript">

function attachRemove_(attachId, inputId) {
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachRemove";
	var request = "attachId=" + attachId;
	var result = ajaxLoadPageSynch(url, request);
	
	if(result == "success") {
		//window.location.search=window.location.search+"&random=1";
		//alert(window.location.search);
		//window.location.reload();
		//window.location.href=window.location.href+"&random=1";
		 reloadAttach();
	}
}

function attachUpload_(inputId,mode,imgId) {
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
										//window.location.search=window.location.search+"&random=2";
										//window.location.reload();
										//if(imgId){
										//attachImageInit(inputId,imgId);	
										//}else{
										//attachInit(inputId);
										//}
										 reloadAttach();
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
   
   function reloadAttach(){
	   var param=getParamObject();
	   var url = MATECH_SYSTEM_WEB_ROOT + "/docpost/layout/viewAttach.jsp";
	   $("#divAttach").load(url,param);
   }
   
   Ext.onReady(function(){
	   reloadAttach();
	   
   });

</script>

</head>
<body>
   <div id="divAttach"></div> 
 </body>
 
</html>