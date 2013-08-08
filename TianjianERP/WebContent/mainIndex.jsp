<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>


<%
   WebUtil webUtil=new WebUtil(request,response);
   UserSession userSession=webUtil.getUserSession();
  
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/index.js"></script>


<title>我的工作台</title>

<style> 
	body{
		height: 100% ;
		width: 100% ;
	}

	.linkDiv{border-bottom:1px #DDDDEE dotted;padding:3px;}
	.col_div{float:left;margin:0px 0px 0px 0px;width: 33%;}
	.drag_div{ width:100%; margin:0px 5px 5px 5px auto; border:1px solid #999; padding:0px;}
	.drag_header{ height:22px;width:100%;font-weight:bold;padding:6px 0px 0px 0px;}
	.linkgray{color:#999;}
	
	a {
		text-decoration: none;
	}
	
	.special {
		font-weight: bold;
		color: red;
	}
	
	
	/* 日程表格样式 */
	#calTable {
	    border-collapse:collapse;
	    border:5px solid #C3D9FF;
	}
	
	/* 每日单元格样式 */
	td.calBox {
	    border:1px solid #CCDDEE;
	    width:50px;
	    height:35px;
	    vertical-align:top;
	}
	
	/* 每日单元格内日期样式 */
	td.calBox div.date {
	    background:#E8EEF7;
	    font-size:11px;
	    padding:2px;
	    cursor:pointer;
	   
	}
	
	/* 每日单元格内周六周日样式 */
	td.sat div.date, td.sun div.date{
	    color:red;
	   
	}
	
	/* 今日样式 */
	td.calBox div.today {
	    background:#BBCCDD;
	}
	
	/* 周标识样式 */
	td.day {
	    text-align:center;
	    background:#C3D9FF;
	    border:1px solid #CCDDEE;
	    color:#112ABB;
	    
	}
	
	/* 当前显示的年月样式 */
	#dateInfo {
	    font-weight:bold;
	    margin:3px;
	    
	}
	
	/* 添加任务div样式 */
	#addBox {
	    display:none;
	    position:absolute;
	    width:300px;
	    border:1px solid #000;
	    height:100px;
	    background:#FFFF99;
	    padding:10px;
	}
	
	/* 添加任务内日期样式 */
	#taskDate {
	    height:30px;
	    font-weight:bold;
	    padding:3px;
	}
	
	/* 按钮样式 */
	.taskBtn {
	    margin:10px;
	}
	
	/* 编辑任务div样式 */
	#editBox {
	    display:none;
	    position:absolute;
	    width:300px;
	    border:1px solid #000;
	    height:60px;
	    background:#99FF99;
	    padding:10px;
	}
	
	/* 任务样式 */
	div.task {
	    width:60px;
	    overflow:hidden;
	    white-space:nowrap;
	    background:#668CB3;
	    border:1px solid #FFF;
	    color:#FFF;
	    padding:1 2 1 3;
	    cursor:pointer;
	}
</style>

<script type="text/javascript">
	
	var mytab1 ;
	Ext.onReady(function(){
	

		
	    mytab1 = new Ext.TabPanel({
	        id: "tab1",
	        region:'center',
	        activeTab:0, //选中第一个 tab
	        layoutOnTabChange:true, 
	        forceLayout : true,
	        deferredRender:false,
	    //    height: document.body.clientHeight-Ext.get('divTab').getTop(),
	   //     width : document.body.clientWidth, 
	   //     defaults: {autoWidth:true,autoHeight:true},
	        items:[
	            {contentEl: "allList",title:"全部",id:"all"},
	            {contentEl: "placardList",title:"天健资讯",id:"placard"},
	            {contentEl: "newsList",title: "技术支持",id:"news"},
	            {contentEl: "regulationsList",title: "期刊读物",id:"regulations"}
	        ]
           
	    });
	    

		 var isRefresh = false ;
		 var isRefresh2 = false ;
		 var isRefresh3 = false ;
		 mytab1.on("tabchange",function(tabpanel,tab) {
	    	if(tab.id == "placard") {
				if(!isRefresh) {
					goSearch_placardList();
				}
				isRefresh = true ; 
			}else if(tab.id == "news") {
				if(!isRefresh2) {
					goSearch_newsList();
				}
				isRefresh2 = true ; 
			}else if(tab.id == "regulations") {  
				if(!isRefresh3) {
					goSearch_regulationsList();
				}
				isRefresh3 = true ; 
			}
			  
	    }) ;
	     
	 	var layout = new Ext.Viewport({
			layout:'border',
			items:[{region:'east',
		    		id:'east-panel',
		    		layout: 'vbox',
		    		split:true,
		    		width: 320,
		    		minSize: 175,
		    		maxSize: 400,
		    		margins:'0 0 0 0',
		    		title:'我的信息门户',
		    		lines:false,
		    		collapsible :true,
		    		items:[{
		    		    title: '日程表',
		    		    flex:4,
		    		    collapsible :true,
		    		    autoScroll:true,
		    		    contentEl:'calendar'
		    		    ,height:260
		    		},{
		    		    title: '待办提醒',
		    		    flex:2,
		    		    collapsible :true,
		    		    autoScroll:true,
		    		    contentEl:'taskDiv'
		    		}
		    		/*,{
		    		    title: '未读邮件',
		    		    flex:2,
		    		    collapsible :true,
		    		    contentEl:'eMailDiv'
		    		}*/
		    		]

				},{id:"news",
					/*
					region:'center',
	    			id:'center-panel',
	    			split:true,
	    			margins:'0 0 0 0',
	    			layout:'fit',
	    			lines:false,
	    			layoutConfig:{
	    				animate:false
	    			}
					//html:'<iframe id="mainFrame" scrolling="no" frameborder="0"  width=100% height=100% src="Panchina_url_convert.jsp?url=Panchina_News/jumpToNews.action&areaID=${userSession.areaid }&autoID=${userSession.userId}" />'
					*/
					region:"center",
					contentEl:"allList"
				}
			 ]
		});
	 	
	 	layout.doLayout();
	 	
	 	
	 	/* 
	 					<div class="linkdiv">
					<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
					<c:if test="${remind.acturl !='' }">
					<a style="color:#344456; text-decoration: none" href="javascript:void(0);" onclick="openUrl('${pageContext.request.contextPath}/${remind.acturl}','${remind.actname}')" >
					</c:if>
					<c:if test="${remind.acturl =='' }">
					<a style="color:#344456; text-decoration: none" href="javascript:void(0);">
					</c:if>
						<span tagoc="link_16341" style="font-size: 12px">${remind.hinttxt}</span>
					</a>
				</div>
	 	*/
	 	//http://www.pccpa.com.cn/Panchina_TX/remind.action?userName=%25E8%2583%25A1%25E5%25B0%2591%25E5%2585%2588
	 	/*
	     $.post("http://www.pccpa.com.cn/Panchina_TX/remind.action",{userName:userName},function(str){
	 		//alert(str);
	 		
	 		var jarr=Ext.util.JSON.decode(str);
	 		//alert(jarr.length);
	 		for(var i=0;i<jarr.length;i++){
	 			//jumptoUrl ,remindStr
	 			var jumptoUrl=jarr[i]["jumptoUrl"]||"";
	 			var remindStr=jarr[i]["remindStr"]||"";
	 			var titleStr=jarr[i]["titleStr"]||"";
	 			//alert(jumptoUrl);
	 			var img=$("<img border='0' alt='' align='absMiddle' src='${pageContext.request.contextPath}/images/news_bullet.gif' />");
	 			var span=$("<span tagoc='link_16341' style='font-size: 12px'></span>");
	 			span.html("&nbsp;"+remindStr);
	 			var a=$("<a style='color:#344456; text-decoration: none' href='javascript:void(0);'></a>");
	 			a.append(span);
	 			a.click(function(){openUrl(jumptoUrl,titleStr)});
	 			
	 			var linkdiv=$("<div class='linkdiv'></div>");
	 			linkdiv.append(img).append(a);
	 			$("#taskDiv").prepend(linkdiv);
	 		}
	 	}); */
	}) ;
    var userName="<%=userSession.getUserName()%>";
</script>

</head>

<body>

<div id="center">

<div id="allList" style="height:expression(document.body.clientHeight);width:100%">		
</div>

	<!--<div id="divTab" style="overflow:auto"> 
	
		<div id="allList" style="height:expression(document.body.clientHeight-17);width:100%">
			<mt:DataGridPrintByBean name="allList"  />
		</div>
		<!--  
		<div id="placardList" class="x-hide-display" style="height:expression(document.body.clientHeight-27);width:100%">
			<mt:DataGridPrintByBean name="placardList" outputData="false" outputType="invoikSearch" />
		</div>
		
		<div id="newsList" class="x-hide-display" style="height:expression(document.body.clientHeight-27);width:100%">
			<mt:DataGridPrintByBean name="newsList" outputData="false" outputType="invoikSearch" />
		</div>
		
		<div id="regulationsList" class="x-hide-display" style="height:expression(document.body.clientHeight-27);width:100%">
			<mt:DataGridPrintByBean name="regulationsList" outputData="false" outputType="invoikSearch" />
		</div>
		
  	</div> -->
	
</div>
	

<div id="east">
	<div id="taskDiv">
		<c:forEach items="${remindList}" var="remind">
				
				<div class="linkdiv">
					<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
					<c:if test="${remind.acturl !='' }">
					<a style="color:#344456; text-decoration: none" href="javascript:void(0);" onclick="openUrl('${pageContext.request.contextPath}/${remind.acturl}','${remind.actname}')" >
					</c:if>
					<c:if test="${remind.acturl =='' }">
					<a style="color:#344456; text-decoration: none" href="javascript:void(0);">
					</c:if>
						<span tagoc="link_16341" style="font-size: 12px">${remind.hinttxt}</span>
					</a>
				</div> 
		
		</c:forEach>
	</div>
	
	<div id="eMailDiv">
		<c:forEach items="${emailList}" var="email">
			<div class="linkdiv">
			<img border="0" alt="" align="absMiddle" src="${pageContext.request.contextPath}/images/news_bullet.gif">
			<a style="color: #344456; text-decoration: none" href="javascript:void(0);" onclick="openUrl('${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid=${email.uuid}','内部邮件')" >
				<span tagoc="link_16341" style="font-size: 12px">${email.title}</span>
			</a>
			<span class="linkgray10">(${email.senddate})</span>
			</div> 
		</c:forEach>
	</div>
	
	<div id="calendar">
		<!-- 日历表格 -->
		<table id="calTable">
		<tr>
		    <td class="day">周日</td>
		    <td class="day">周一</td>
		    <td class="day">周二</td>
		    <td class="day">周三</td>
		    <td class="day">周四</td>
		    <td class="day">周五</td>
		    <td class="day">周六</td>
		</tr>
		<tr>
		    <td class="calBox sun" id="calBox0"></td>
		    <td class="calBox" id="calBox1"></td>
		    <td class="calBox" id="calBox2"></td>
		    <td class="calBox" id="calBox3"></td>
		    <td class="calBox" id="calBox4"></td>
		    <td class="calBox" id="calBox5"></td>
		    <td class="calBox sat" id="calBox6"></td>
		</tr>
		<tr>
		    <td class="calBox sun" id="calBox7"></td>
		    <td class="calBox" id="calBox8"></td>
		    <td class="calBox" id="calBox9"></td>
		    <td class="calBox" id="calBox10"></td>
		    <td class="calBox" id="calBox11"></td>
		    <td class="calBox" id="calBox12"></td>
		    <td class="calBox sat" id="calBox13"></td>
		</tr>
		<tr>
		    <td class="calBox sun" id="calBox14"></td>
		    <td class="calBox" id="calBox15"></td>
		    <td class="calBox" id="calBox16"></td>
		    <td class="calBox" id="calBox17"></td>
		    <td class="calBox" id="calBox18"></td>
		    <td class="calBox" id="calBox19"></td>
		    <td class="calBox sat" id="calBox20"></td>
		</tr>
		<tr>
		    <td class="calBox sun" id="calBox21"></td>
		    <td class="calBox" id="calBox22"></td>
		    <td class="calBox" id="calBox23"></td>
		    <td class="calBox" id="calBox24"></td>
		    <td class="calBox" id="calBox25"></td>
		    <td class="calBox" id="calBox26"></td>
		    <td class="calBox sat" id="calBox27"></td>
		</tr>
		<tr>
		    <td class="calBox sun" id="calBox28"></td>
		    <td class="calBox" id="calBox29"></td>
		    <td class="calBox" id="calBox30"></td>
		    <td class="calBox" id="calBox31"></td>
		    <td class="calBox" id="calBox32"></td>
		    <td class="calBox" id="calBox33"></td>
		    <td class="calBox sat" id="calBox34"></td>
		</tr>
		<tr>
		    <td class="calBox sun" id="calBox35"></td>
		    <td class="calBox" id="calBox36"></td>
		    <td class="calBox" id="calBox37"></td>
		    <td class="calBox" id="calBox38"></td>
		    <td class="calBox" id="calBox39"></td>
		    <td class="calBox" id="calBox40"></td>
		    <td class="calBox sat" id="calBox41"></td>
		</tr>
		</table>
	</div>

</div>

<input type="hidden" id="ctype" name="ctype" value="">
<input type="hidden" id="type" name="type" value="">

</body>

<script type="text/javascript">
	function openUrl(url,name) {
		var tab = parent.tab ;
		if(tab && tab.id == "mainFrameTab"){
			try{
				n = parent.parent.tab.add({    
					'title':name,  
					 closable:true,  //通过html载入目标页    
					 html:'<iframe scrolling="yes" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
				});    
				parent.parent.tab.setActiveTab(n);
			}catch(e){
				window.location = url ;
			}
		}else {
			window.location = url ;
		}
		
	}
	
	function goDeal(key,taskId,pName) {
	    if(key=="sealApplyFlow"){
	    		openUrl("${pageContext.request.contextPath}/seal.do?method=auditSkip&taskId"+taskId,pName) ;
	    }else if(key=="leaveFlow"){
	             openUrl("${pageContext.request.contextPath}/leave.do?method=auditSkip&taskId="+taskId,pName) ;
	    }else if(key=="massControl"){
	    	     openUrl("${pageContext.request.contextPath}/businessProject.do?method=auditSkip&taskId=="+taskId,pName) ;
	    }else if(key=="leaveOfficeFlow"){
	             openUrl("${pageContext.request.contextPath}/leaveOffice.do?method=auditSkip&taskId="+taskId,pName) ;	    
	    }else if(key=="userPrefermentFlow"){
	    		   openUrl("${pageContext.request.contextPath}/userPreferment.do?method=auditSkip&taskId="+taskId,pName) ;
	    }else if(key=="destroyLeaveFlow"){
	    		   openUrl("${pageContext.request.contextPath}/leave.do?method=auditSkip&taskId="+taskId,pName) ;	    
	    }else if(key=="meetingOrderFlow"){
	               openUrl("${pageContext.request.contextPath}/meetingOrderSy.do?method=toAuidt&taskId="+taskId,pName) ;	    
	    }else if(key=="docPostKey"){
                   openUrl("${pageContext.request.contextPath}/document.do?method=audit&taskId="+taskId,pName) ;	    
	    }else if(key=="waresApplyFlow"){	
	    	alert("我是ctype");
                   openUrl("${pageContext.request.contextPath}/waresStockSy.do?method=auditSkip&ctype=bumen&uuids="+taskId,pName) ;	    
	    }else if(key=="waresCancelFlow"){
                   openUrl("${pageContext.request.contextPath}/waresStockSy.do?method=cancelAuditSkip&taskId="+taskId,pName) ;	    	       
	    }else if(key=="proclamationFlow"){
                   openUrl("${pageContext.request.contextPath}/proclamationSy.do?method=auditSkip&taskId="+taskId,pName) ;	    	       	    
	    }else if(key=="reportDutyFlow"){
                   openUrl("${pageContext.request.contextPath}/job.do?method=auditSkip&taskId="+taskId,pName) ;	    	       	    	    
	    }
	}
	
	function goView(key,pId,pName) {
		//if(key == 'BusinessTakeKey') {
		//	openUrl('${pageContext.request.contextPath}/businessTake.do?method=processInfo&showTool=true&processInstanceId='+pId,pName) ;
		//}else if(key == 'TaskAuditKey'){
			openUrl('${pageContext.request.contextPath}/commonProcess.do?method=viewImage&id='+pId,pName) ;
		//}
	}
	
	function show(autoid,ctype,type) {
       
		if(ctype == 1) {
			//openUrl('${pageContext.request.contextPath}/proclamation.do?method=look&uuid='+autoid,"天健资讯") ;
		}else if(ctype ==2) {
			openUrl('${pageContext.request.contextPath}/news.do?method=viewNews&autoId='+autoid,type) ;
			
		
		}else if(ctype == 3) {
			//openUrl('${pageContext.request.contextPath}/regulations.do?method=viewRegulations&autoId='+autoid,"期刊读物") ;
		}
		
	}
	
	function grid_dblclick(trObj,tableId) {

		var tab = parent.tab ;
		//alert(trObj.uuid);
		//var url = "${pageContext.request.contextPath}/regulations.do?method=viewRegulations&autoId="+trObj.autoId+"&rand="+Math.random() ;
		//openUrl(url,'部门规章库');
		openUrl('${pageContext.request.contextPath}/news.do?method=viewNews&autoId='+autoid,"行业资讯") ;
		
	}
	
	function winReload(){
	//	goSearch_myDealList();
	//	goSearch_myApplyList();
	//	goSearch_myPlacardList();
		
		//刷新EMail
		Ext.Ajax.request({
			method:'POST',
			url:'${pageContext.request.contextPath}/info.do?method=reloadFlag&rand='+Math.random(),
			params: {flag : "email"},		
			success:function (response,options) {
				var result = "";
				var json = eval("("+response.responseText+")");
				if(json.length > 0) {
					for(var i=0;i<json.length;i++) {
						result += "<div class='linkdiv'>";
						result += "<img border='0' align='absMiddle' src='${pageContext.request.contextPath}/images/news_bullet.gif'>";
						result += "<a style='color: #344456; text-decoration: none' href='javascript:void(0);' onclick=\"openUrl('${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+json[i].uuid+"','内部邮件')\" >";
						result += "<span tagoc='link_16341' style='font-size: 12px'>"+json[i].title+"</span>";
						result += "</a>";
						result += "<span class='linkgray10'>("+json[i].senddate+")</span> ";
						result += "</div>";
					}
				}
				if(result != ""){
					document.getElementById("eMailDiv").innerHTML = result;	
				}
			},
			failure:function (response,options) {
				//alert("网络超时,匹配失败,请联系管理员");
				//window.location=MATECH_SYSTEM_WEB_ROOT+"common.do?method=exitSystem";
				return false ;
			}
		});
		
		//刷新公告通知
		Ext.Ajax.request({
			method:'POST',
			url:'${pageContext.request.contextPath}/info.do?method=reloadFlag&rand='+Math.random(),
			params:{flag : "proclamation"},		
			success:function (response,options) {
				var result = "";
				var json = eval("("+response.responseText+")");
				if(json.length > 0) {
					for(var i=0;i<json.length;i++) {
						result += "<div class='linkdiv'>";
						result += "<img border='0' align='absMiddle' src='${pageContext.request.contextPath}/images/news_bullet.gif'>";
						result += "<a style='color: #344456; text-decoration: none' href='javascript:void(0);' onclick=\"openUrl('${pageContext.request.contextPath}/proclamationSy.do?method=look&uuid="+json[i].uuid+"','公告通知')\" >";
						result += "<span tagoc='link_16341' style='font-size: 12px'>"+json[i].title+"</span>";
						result += "</a>";
						result += "<span class='linkgray10'>("+json[i].publishdate+")</span> ";
						result += "</div>";
					}
				}
				if(result != ""){
					document.getElementById("knowledgeDiv").innerHTML = result;	
				}
			},
			failure:function (response,options) {
				//alert("网络超时,匹配失败,请联系管理员");
				//window.location=MATECH_SYSTEM_WEB_ROOT+"common.do?method=exitSystem";
				return false ;
			}
		});
	}
	
	/*
var homePageRefreshTime = "30000";
if("${homePageRefreshTime}" !=""){
	homePageRefreshTime = "${homePageRefreshTime}"*1*1000;
}	window.setInterval(winReload,homePageRefreshTime);
*/
</script>
 
<script> 
function setIframeSrc() { 
var iframe1 = document.getElementById('allList'); 
iframe1.innerHTML='<iframe id="mainFrame" scrolling="no" frameborder="0"  width=100% height=100% src="Panchina_url_convert.jsp?url=Panchina_News/jumpToNews.action&areaID=${userSession.areaid }&autoID=${userSession.userId}" />';
} 
setTimeout(setIframeSrc,5); 
</script>

</html>

