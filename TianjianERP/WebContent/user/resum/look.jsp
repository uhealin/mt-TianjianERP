<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>岗位详情查看</title>

<script Language=JavaScript>

	function ext_init(){
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
       		},{
            	text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function () {
            		window.history.back();
	            }
       		}
			]
        });
        new ExtButtonPanel({
			desc:'',
			renderTo:'sbtBtn',
			items:[
			{
                text: '岗位申请',
                id:'appSubmit23', 
                icon:'${pageContext.request.contextPath}/img/receive.png' ,
                scale: 'large',
	            handler:function(){
	            	var type;
	            	if(${resum.type == '社会招聘'}){
	            		     type=0;
	            			//window.open("${pageContext.request.contextPath}/formDefine.do?method=formView&formId=8252d0e7-6f0d-4ae1-b34d-97327b6270d5");
	            	}else{
	            			//window.open("${pageContext.request.contextPath}/formDefine.do?method=formView&formId=13916b9b-2f1b-47d6-9b00-66fddc927903");
	            	      type=1;
	            	}
	            	window.open("${pageContext.request.contextPath}/resume.do?method=toEdit&uuid=&type="+type);
	   			}
	           },{
                text: '返回',
                id:'appSubmit25', 
                icon:'${pageContext.request.contextPath}/img/back_32.png' ,
                scale: 'large',
	               handler:function(){
	            	  //closeTab(parent.tab);
						window.history.back();
	   			   }
	           }
            ]  
		});        
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body leftmargin="0" topmargin="0">

<div id="divBtn"></div>
 	<div style="overflow: auto;height: 95%">
	<form name="thisForm" action="${pageContext.request.contextPath}/news.do" method="post" scorll="auto">
		<input name="autoId" type="hidden" id="autoId" value="${autoId}" />
		<table width="100%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
			<tr  class="DGtd">
		      <td height="30" bgColor="d3e1f1" id="title" align="center" style="size: 25px !important; ">${resum.title}</td>
		     </tr>
		     
		</table>
		  	<div style="word-wrap:break-word;height: 99%;width:95%;margin-left: 100px;padding-right: 50px;margin-top: 15px;">
		  		<pre>${resum.content}</pre>
		  	</div>
			<center><div id="sbtBtn" ></div></center>		 		 		
	</form>
	</div>
</body>


<script>
	
</script>
</html>
