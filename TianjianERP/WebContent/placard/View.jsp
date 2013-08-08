<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<%@ page import="java.util.*"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="com.matech.audit.service.placard.model.PlacardTable"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.audit.service.placard.PlacardService"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.datamanage.BackupUtil"%>

<%
	ASFuntion CHF=new ASFuntion();
	Connection conn=null;
	String dataBasePath ="";
	String myName="";
	String fileDirPath = "";
	String fileName="";
	String limitStr = "" ;
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
	String aUsr = userSession.getUserId();
	
	try{
		conn=new DBConnect().getConnect();
		PlacardService pm = new PlacardService(conn);
		
		String placardTitle = CHF.showNull(request.getParameter("placardTitle"));
	    String placardSender = CHF.showNull(request.getParameter("placardSender"));
		String placardTime = CHF.showNull(request.getParameter("placardTime"));
		String ids = CHF.showNull(request.getParameter("ids"));
		
		int totalCount = -1;
		int from = 0;
		int to = 0;
		
		totalCount = pm.getAllPlacard(aUsr,placardTitle,placardSender,placardTime,"").size() ;
		
		int pagesize = 20;//每页显示记录数
		int liststep = 9;//最多显示分页页数
		int pages = 1;//默认显示第一页
		if (request.getParameter("page") != null) {
			try {
				pages = Integer.parseInt(request.getParameter("page"));//分页页码变量
			} catch(Exception e) {
				pages = 1;
			}
		}
		int count = totalCount;//假设取出记录总数
		int pagescount = (int) Math.ceil((double) count / pagesize);//求总页数，ceil（num）取整不小于num
		if (pagescount < pages) {
			pages = pagescount;//如果分页变量大总页数，则将分页变量设计为总页数
		}
		if (pages < 1) {
			pages = 1;//如果分页变量小于１,则将分页变量设为１
		}
		int listbegin = (pages - (int) Math.ceil((double) liststep / 2));//从第几页开始显示分页信息
		if (listbegin < 1) {
			listbegin = 1;
		}
		int listend = pages + liststep / 2;//分页信息显示到第几页
		if (listend > pagescount) {
			listend = pagescount + 1;
		}

//		显示数据部分
		int recordbegin = (pages - 1) * pagesize;//起始记录
		int recordend = 0;
		recordend = recordbegin + pagesize;
//		最后一页记录显示处理
		if (pages == pagescount) {
			recordend = (int) (recordbegin + pagesize * (count % pagesize)
			* 0.1);
		}

		from = recordbegin + 1;
		to = from + pagesize -1;
		if(to>count) {
			to = count;
		}
		
		limitStr = " limit " + recordbegin + "," + pagesize + "" ;
		
		ArrayList al = new ArrayList();;
		
		if(!"".equals(aUsr)){
			
			if(!"".equals(ids)){
				al = pm.getAllPlacardById(aUsr,placardTitle,placardSender,placardTime,limitStr,ids); //差出指定ID的()
				pm.updateIsReadById(ids); //标记指定ID
			}else{
				al = pm.getAllPlacardById(aUsr,placardTitle,placardSender,placardTime,limitStr,""); //差出指定ID的
				pm.updateIsRead(aUsr); //标记所有已读
				//al = pm.getAllPlacard(aUsr,placardTitle,placardSender,placardTime,limitStr); //查出所有	
			}
			pagescount = al.size();
			pm.AddIsReversion(aUsr,CHF.getCurrentDate()+" "+CHF.getCurrentTime());
		}				
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查看公告</title>
<style>
td {
	font-size: 12px;
	text-decoration: none;
}
</style>

<script type="text/javascript">
	function readyFun() {
	var tbar_customer = new Ext.Toolbar({
			renderTo:'btnDiv',
            items:[{
	            text:'删除所有公告',
	            icon:'${pageContext.request.contextPath}/img/delete.gif',
	            handler:function(){
	            	deleteAll();
				}
       		}
          /*,'-',{
            text:'打印',
            icon:'${pageContext.request.contextPath}/img/print.gif',
           	handler:function(){
					goPrint();
			}
          
        }*/
          ,'-',{
            text:'查询',
            icon:'${pageContext.request.contextPath}/img/query.gif',
           	handler:function(){
					queryWinFun();
			}
        },'-',{
            text:'发送公告',
            icon:'${pageContext.request.contextPath}/img/mytask.gif',
           	handler:function(){
           			var url = MATECH_SYSTEM_WEB_ROOT+"placard/AddPlacard.jsp" ;
           			var tab = parent.tab ;
        	        if(tab && tab.id == "mainFrameTab"){
						var n = tab.add({    
							title:"发送公告",
							closable:true,
							html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'    
						});
						tab.setActiveTab(n);
        	        }else {
        				window.open(url);
        			}	
			}
        }
        <c:if test="${param.ids !=null}">
        ,'-',{
            text:'返回',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function () {
            	 history.back();
            }
        }
        </c:if>
        ,'-',{
            text:'关闭',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function () {
            	closeTab(parent.tab);
            }
        },'->']
        });  
	}
	var queryWin = null;
	        
	function queryWinFun() {
		if(!queryWin) {
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
		     title: '公告查询',
		     renderTo :'searchWin',
		     width: 455,
		     height:295,
		  	 //modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
					new BlockDiv().hidden();
					queryWin.hide();	         	
		        	}}
		        },
		       layout:'fit',
			  html:searchDiv.innerHTML,
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		               	queryWin.hide();
		               	checkPlacard();
		           }
		       },{
		           text:'清空',
		         	handler:function(){
		         		clearVal();
		           }
		       },{
		           text:'取消',
		           handler:function(){
		               queryWin.hide();
		           }
		       }]
		    });
	   }
	   new BlockDiv().show();
	   queryWin.show();
	}
Ext.onReady(readyFun);
</script>

</head>

<body bgcolor="#FFFFFF">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="btnDiv"></div>
<form name="thisForm" method="post" action="" >
<input type="hidden" name="page" id="page">
<div style="height:expression(document.body.clientHeight -27);overflow:auto;">
<table width="100%" height="121" border="0" cellpadding="0" cellspacing="1" >
    <tr>
      <td align="center" valign="top" bordercolor="#CCCCCC" bgcolor="#FFFFFF">

  <table width="98%"  border="0" cellpadding="0" cellspacing="0">
  <tr>
      <td height="5" align="left"></td>
  </tr>
    <tr>
      <td height="18" align="left">
	  </td>
  </tr>
 
</table>


<%
	for(int i=0;i<al.size();i++){
		PlacardTable pt = (PlacardTable)al.get(i);
		//dataBasePath = BackupUtil.getDATABASE_PATH();
		if(pt.getImage()!=null&&!pt.getImage().equals("")){
			myName=PlacardService.NOTE_FILE_PATH;
			fileDirPath = myName.replaceAll("/placard/","")+pt.getImage();
			fileName=pt.getImage().substring(pt.getImage().lastIndexOf("/")+1,pt.getImage().length());
		}
%>
  <table width="98%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
	<tr height="20" class="DGtd">
		<td height="23" align="left" bgColor="#d7ebfa" colspan="2" style="padding-left: 20px;">
		<strong>文件标题：</strong> <%=pt.getCaption()%>
		</td>
	</tr>
	<%if(pt.getImage()!=null && !"".equals(pt.getImage().toString()))
	{
		%>
		<tr height=18>
	  <td height="22" align="center" width=100 bgColor="#EEEEEE"><strong>附件</strong></td>
	  <td align="left" bgColor="#ffffff" style="word-break: break-all; word-wrap:break-word;">
      <%if(pt.getImage().toLowerCase().indexOf(".jpg")>-1 || pt.getImage().toLowerCase().indexOf(".gif")>-1 || pt.getImage().toLowerCase().indexOf(".bmp")>-1) 
     {
      %>
        <img src='<%=fileDirPath%>'>
         </tr>
      <% 
      }
      else
      {
    	  %>
          
    	  <input type="button" class="flyBT" value="<%=pt.getImage().substring(pt.getImage().lastIndexOf("/")+1,pt.getImage().length()) %>" onclick="openFile('<%=fileDirPath %>','<%=fileName %>');">
    	 
        <% 
      }
      %>

		<% 
	}
	
	%>
	<tr height=18>
	  <td align="left" bgColor="#eaf1f6" style="word-break: break-all; word-wrap:break-word;line-height: 20px;" colspan="2">
	  	<div style="padding-left: 20px;padding-top: 10px;">
	  	<%="【"+pt.getName()+"】 "+pt.getAddresserTime()+""%><br>
	    <%=pt.getMatter().replaceAll("\r","<br>").replaceAll("\n","<br>").split("<br><br>附件：<input type=")[0]%> 
	   </div>
	   <div style="padding-bottom: 10px;" align="right" >
 		 	<%if(pt.getUrl()!=null){ %>
			<!-- 
				<input type="button" name="Read" value="查看<%=pt.getModel() %>" onClick="goRead('<%=pt.getUrl()%>','<%=pt.getUuidName()%>','<%=pt.getUuid()%>','<%=pt.getModel()%>')"/>
			 
				<img alt="查看<%=pt.getModel() %>" src="${pageContext.request.contextPath}/img/task.gif" onClick="goRead('<%=pt.getUrl()%>','<%=pt.getUuidName()%>','<%=pt.getUuid()%>','<%=pt.getModel()%>')">-->
				<a href="javascript:void(0)" onClick="goRead('<%=pt.getUrl()%>','<%=pt.getUuidName()%>','<%=pt.getUuid()%>','<%=pt.getModel()%>')">查看<%=pt.getModel() %></a>
			<%} %>
	  		 <% if(pt.getIsNotReversion()!=1){%>
	       		 <a href="javascript:void(0)"  onClick="onAdd(<%=pt.getID()%>);">回复</a>
	       		<img alt="回复" src="${pageContext.request.contextPath}/img/goMsg.png"  style="display: none;" onClick="onAdd(<%=pt.getID()%>);"> 
	       		 
	         <%} %>
	          <a href="javascript:void(0)"  onClick="onDel(<%=pt.getID()%>);" >删除</a> 
	          <img alt="删除" src="${pageContext.request.contextPath}/img/delete.gif" style="display: none;" onClick="onDel(<%=pt.getID()%>);">
	        <!-- 
			<input type="button" name="Reversion" value="回复"  onClick="onAdd(<%=pt.getID()%>);" >
			<input type="button" name="Delete" value="删除"  onClick="onDel(<%=pt.getID()%>);" > -->
			<input name="moneysql" type="hidden" id="moneysql">&nbsp;
		</div>
		</td>
	  </tr>

</table>
<br>
<%}
	StringBuffer pageInfo = new StringBuffer();
	pageInfo.append("<div style=\"font-size:16px;text-align:center;\">");
	if(pagescount==0){
		pageInfo.append("暂无系统公告消息!");
	}
	if (pages > 1) {
		pageInfo.append(
		"<a href=javascript:goTo(" + (pages - 1) + ")>上一页</a>");
	}//>显示上一页
	//<显示分页码
	for (int i = listbegin; i < listend; i++) {
		if (i != pages) {//如果i不等于当前页
			pageInfo.append(
			"<a href=javascript:goTo(" + i + ")>[" + i + "]</a>");
		} else {
			pageInfo.append("[" + i + "]");
		}
	}//显示分页码>
	//<显示下一页
	if (pages != pagescount && pagescount>0) {
		pageInfo.append(
		"<a href=javascript:goTo(" + (pages + 1) + ")>下一页</a>");
	}//>显示下一页
	//>显示分页信息
	pageInfo.append("</div>");
	
%>
<%=pageInfo.toString()%>
<br>

  </td>
    </tr>
  </table>
  <p>&nbsp;</p>
  </div>
  </form>
  <form name="theForm1" id="theForm1" method="post">
	<input type="hidden" name="filePath" id="filePath"  >
	<input type="hidden" name="fileName" id="fileName"  >
</form>

<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<table width="80%" cellspacing="0" cellpadding="0" align="center">
	<tr>
		<td align="right">公告标题：&nbsp;</td>
		 	<td><input type="text" name="placardTitle" id="placardTitle" value="<%=placardTitle%>" size="20" />&nbsp;&nbsp;</td>
		 	</tr>
		<tr>
		<td align="right">发信人：&nbsp;</td><td><input type="text" name="placardSender" id="placardSender" value="<%=placardSender%>" size="20" />&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td align="right">发信时间：&nbsp;</td><td><input type="text" name="placardTime" id="placardTime" value="<%=placardTime%>" size="20" showcalendar="true"/>&nbsp;&nbsp;
		</td>
	</tr>
	
</table>
</div>

</body>
</html>
<script>
function openFile(filepath,filename){
	document.getElementById("filePath").value =  filepath;
	document.getElementById("fileName").value = filename;
	//var theForm1 = document.getElementById("theForm1");
	theForm1.action = "${pageContext.request.contextPath}/common.do?method=download";
	theForm1.submit();

}


function goTo(page) {
	document.getElementById("page").value = page;  
	document.thisForm.submit();
}

function onAdd(s){
	window.location="AddPlacard.jsp?chooseValue="+s;
}
function onDel(s){

	if(confirm("您是否删除要此公告！")==true)
		window.location="Del.jsp?chooseValue="+s;
}

function deleteAll(){
	if(confirm("您是否删除所有公告！")==true)
		window.location="Del.jsp?chooseValue=all&userId="+<%=aUsr%>;
}

function closeWindow()
{
	this.opener=null;
	window.close();
}

function aaa(temp){

	var obj=document.getElementById(temp).value;
	
  // if(obj.indexOf("&&moneysql=")>-1){
   		var objs=obj.split("&&moneysql=");
   		
   		//alert(objs[0])
   		//alert(objs[1])
   		document.getElementById("moneysql").value=objs[1];
   		
   		document.thisForm.action=objs[0];
   		
   		document.thisForm.target="_blank"
   		
   		document.thisForm.submit();
   	//}
}

function checkPlacard(){
    var checkForm = document.getElementById("thisForm");
    checkForm.action = "View.jsp?menuid=722005";
    checkForm.submit();    
}


function clearVal(){
	document.getElementById("placardTitle").value = "";
	document.getElementById("placardSender").value = "";
	document.getElementById("placardTime").value = "";
}


function goPrint(){
	var placardTitle = document.getElementById("placardTitle").value ;
	var placardSender = document.getElementById("placardSender").value ;
	var placardTime = document.getElementById("placardTime").value ;
	window.open("${pageContext.request.contextPath}/placard.do?method=print&placardTitle="+placardTitle+"&placardSender="+placardSender+"&placardTime="+placardTime,"_blank","resizable=yes, toolbar=yes,menubar=yes,titlebar=yes,scrollbars=yes");		
	
}

function goRead(url,uuidName,uuid,model){
    var newUrl = "${pageContext.request.contextPath}/"+url+"&"+uuidName+"="+uuid;
	var newTab = parent.tab.add({
			 id:Math.random(),
	         title:model,    
	         closable:true,  //通过html载入目标页    
	         html:'<iframe frameborder="0" width="100%" height="100%" src="'+newUrl+'" scroll="auto"></iframe>'   
	     }); 
     parent.tab.setActiveTab(newTab);   
}


</script>
<%

	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		if(conn!=null)conn.close();
	}

%>