<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>规章制度查看</title>
<script Language=JavaScript>

	function ext_init(){
		
		new ExtButtonPanel({
			desc:'',
			renderTo:'sbtBtn',
			items:[
			{
	            text: '发表',
	            id:'appSubmit23', 
	            icon:'${pageContext.request.contextPath}/img/receive.png' ,
	            scale: 'large',
	            handler:function(){
	            	mySubmit();
	   			}
	           },{
	            text: '关闭',
	            id:'appSubmit25', 
	            icon:'${pageContext.request.contextPath}/img/back_32.png' ,
	            scale: 'large',
	               handler:function(){
	            	  //closeTab(parent.tab);
	            	   closeTab(parent.tab);
	   			   }
	           }
	        ]  
		});    

		
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
            	text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
            		closeTab(parent.tab);
	            }
       		},'-',{ 
				text:"返回",
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/back.gif',
				handler:function(){
					 window.location="${pageContext.request.contextPath}/regulations.do";
				}
	   		},'->'
			]
        });
        
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body>
<div id="divBtn"></div>
	<div style="overflow: auto;height: 100%">
	<form name="thisForm" action="${pageContext.request.contextPath}/regulations.do" method="post">
		<input name="autoId" type="hidden" id="autoId" value="${regulations.autoId}" />
		<input name="fileName" type="hidden" id="fileName" value="${fileName}" />
		<input name="filePath" type="hidden" id="filePath" value="${filePath}" />
		
		<table width="100%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
			<tr  class="DGtd">
		      <td height="30" bgColor="d3e1f1" id="title" align="center" style="size: 25px !important; ">${regulations.title}</td>
		     </tr>
		     <tr>
		     <td height="22" align="right">
			  分类： ${regulations.ctype } 发文单位： ${regulations.ctype}
			  <span id="Policy_company"></span>
			  日期： ${regulations.updateTime }&nbsp;&nbsp;</td>
			  </tr>
		</table>
		  	<div style="word-wrap:break-word;height: 99%;width:95%;margin-left: 100px;padding-right: 50px;margin-top: 15px;">
		  		<pre>${regulations.contents}</pre>
		  	</div>
			<div  style="width: 90%;height: 30px;margin-left: 80px;">
			   附&nbsp;件： 
			  	<script type="text/javascript">
						attachInit('regulations','${regulations.attachmentId}',"showButton:false,remove:false");
				</script>
		  </div>
		   
		<table   width="90%" align="center" style="border-collapse: collapse;border: solid #4682B4 1px;" id="IdeaId">
		    <tr align="center">
		      <td style="background-color:c8d3df;height: 40px; " colspan="2">全部评论</td>
		    </tr>
		     <tr >
	            <td align="center" height="30"  style="width: 100;border: solid #4682B4 1px;padding-left: 15px">
	            	署名
	            </td>
	             <td style="border: solid #4682B4 1px;" align="center">内容</td>
	          </tr>
			<c:forEach items="${listMapIdea}" var="Map">
	          <tr >
	            <td height="25" align="center" style="width: 100;border: solid #4682B4 1px;padding-left: 15px">${Map.userName}</td>
	             <td height="20" style="border: solid #4682B4 1px;padding-left: 10px;">
	             	<pre>${Map.content}</pre></td>
	          </tr>
	        </c:forEach>
  		</table>
  		
  		<table   width="90%" align="center" style="border-collapse: collapse;border: solid #4682B4 1px;margin-top: 20px;">
		    <tr align="left">
		      <td style="background-color:c8d3df;height: 40px; " colspan="2" > &nbsp;&nbsp;发表评论：</td>
		    </tr>
		     <tr >
	            <td align="center" height="30"  style="width: 100;border: solid #4682B4 1px;padding-left: 15px">
	            	内容：
	            </td>
	             <td style="border: solid #4682B4 1px;" align="left">
	             	<textarea style="width: 100%;height: 100px;overflow: visible;" id="centent" name="centent"></textarea>
	             </td>
	          </tr>
	          <tr >
	            <td height="25" align="center" style="width: 100;border: solid #4682B4 1px;padding-left: 15px">
	            	署名：
	            </td>
	             <td height="20" style="border: solid #4682B4 1px;padding-left: 10px;">
	             	<input type="text" value="${userSession.userName }" class="required" size="10" readonly="readonly" 
	             		style="background-color: #d3e1f1;">
	             </td>
	          </tr>
  		</table>
  			<center><div id="sbtBtn" ></div></center>
  			
  			<div style="margin-top: 30px;"></div>
		</form>
	</div>
</body>
<script>
	//下载 
	function down(fileName,filePath){
		document.getElementById("fileName").value = fileName;
		document.getElementById("filePath").value = filePath;
		document.thisForm.action = "${pageContext.request.contextPath}/regulations.do?method=download";
		document.thisForm.submit();
			
	}
	
	var result = true;
	function mySubmit(){
		var centent = document.getElementById("centent").value;
		var autoId = document.getElementById("autoId").value;
		
		if(autoId !="" && centent!=""){
			
			if(result){
				 Ext.Ajax.request({
						method:'POST',
						params : { 
							read: Math.round(),
							centent:centent,
							autoId:autoId
						},
						url:"${pageContext.request.contextPath}/regulations.do?method=addRGIdea",
						success:function (response,options) {
							var request = response.responseText;
							if(request =="true"){
								 var tableObj = document.getElementById("IdeaId");
								 
								 var newTr = tableObj.insertRow(-1);
								 var newTdName = newTr.insertCell();
								 var newTdValue = newTr.insertCell();
								 
								 //newTdName.style="width: 100;border: solid #4682B4 1px;padding-left: 15px;height:30px;text-align:center";
								 newTdName.style.width="100";
								 newTdName.style.border="solid #4682B4 1px";
								 newTdName.align="center";
								 //newTdValue.style = "border: solid #4682B4 1px;padding-left: 10px;";
								 
								 newTdName.innerHTML="&nbsp;&nbsp;&nbsp;${userSession.userName}";
								 newTdValue.innerHTML="<pre>"+centent+"</pre>";
								 result=false;
								 alert("发表成功!");
							 }else{
								 alert("发表失败!");
								 return ;
							 }
						},
						failure:function (response,options) {
							alert("后台出现异常,获取文件信息失败!");
						}
					});
			}else{
				alert("您已经发表过评论了，无能再次发表!");
				return ;
			}
		}
		
	}
</script>
</html>
