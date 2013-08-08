<%@page import="com.matech.framework.listener.UserSession"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ page import="java.util.*"%>


<%

	QuestionTable plt = new QuestionTable();
	
	ASFuntion CHF=new ASFuntion();
	UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
	String id = CHF.showNull(request.getParameter("chooseValue"));
	org.util.Debug.prtOut("id="+id);
	String tid = CHF.showNull(request.getParameter("tid"));
	String sContext = "";
	Connection conn =null;
	try{
	  	 conn = new DBConnect().getConnect("");
	  	QuestionMan plm = new QuestionMan(conn);
		if(id.equals(""))//增加页面
		{
			if(request.getParameter("Title")!=null)
			{
				String st = request.getParameter("Context").replace("“", "'").replace("”", "'").replace("\"", "'") ;
					/*	+ "&nbsp;&nbsp;&nbsp;<br>";
				st = st.replaceAll("\n","<br>");
				st = st.replaceAll("\r","");
				st = st.replaceAll("\"","\\\"");
				st = st.replaceAll(" ","&nbsp;");
				*/
				plt.setTitle(request.getParameter("Title"));
				plt.setId(Integer.parseInt(request.getParameter("pid")));
				int quantityType = Integer.parseInt(request.getParameter("QuestionType").replace("Q","").replace("C","")); 
				plt.setQuestionType(quantityType);
				plt.setAuthor(request.getParameter("Author"));
				plt.setKeyValue(request.getParameter("KeyValue"));
				plt.setGreateDate(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
				plt.setContext(st);
				plm.AddOrModifyAQuestion(plt,request.getParameter("adored"),userSession.getUserId());
				response.sendRedirect("List.jsp?init=no&pid="+request.getParameter("QuestionType"));
			}
		}
		else//修改页面
		{
			plt = plm.getAQuestionDetail(id);
		}

		if(!id.equals(""))
		{
		sContext = plt.getContext();
//		org.util.Debug.prtOut("sContext1 = "+sContext);
/*
		sContext = sContext.replaceAll("<br>","\\\\n");
		sContext = sContext.replaceAll("<BR>","\\\\n");
		sContext = sContext.replaceAll("<p>","\\\\n");
		sContext = sContext.replaceAll("<P>","\\\\n");
		sContext = sContext.replaceAll("</p>"," ");
		sContext = sContext.replaceAll("</P>"," ");
		sContext = sContext.replaceAll("&nbsp;"," ");
		sContext = sContext.replaceAll("\r","\\\\n");
		sContext = sContext.replaceAll("\n","\\\\n");
*/		
//		org.util.Debug.prtOut("sContext2 = "+sContext);
		}
%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.question.model.QuestionTable"%>
<%@page import="com.matech.audit.service.question.QuestionMan"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="<%=request.getContextPath()%>/AS_CSS/css_main.css" rel="stylesheet" type="text/css">
<title>新增</title>
<style>

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}

</style>
<script type="text/javascript">

	function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
					save();
				}
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					//closeTab(parent.tab);
					window.history.back();
				}
       		},'->'
			]
        });
        
		new ExtButtonPanel({
			desc:'',
			renderTo:'sbtBtn',
			items:[
			{
                text: '保存',
                id:'appSubmit23', 
                icon:'${pageContext.request.contextPath}/img/receive.png' ,
                scale: 'large',
	            handler:function(){
	            	  save();
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

<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-30);overflow: auto;" >
<span class="formTitle" >您的问题<br/><br/></span>

<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">问题标题：</td>
  <td  class="data_tb_content" ><input name="Title" type="text" size="100" maxlength="100" class="required" title="请输入文件标题，不能为空,不能多于100个汉字！" ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">问题分类：</td>
  <td  class="data_tb_content" >
  		<input name="QuestionType" type="text"  id="QuestionType"
  			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" 
  			class="required" maxlength="10" title="请正确选择分类!"  valuemustexist=true autoid=96 multilevel=true  >
  </td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">记录时间：</td>
  <td  class="data_tb_content" ><input class="before" name="GreateDate" id="GreateDate" type="text" readonly maxlength="10" value="<%=CHF.getCurrentDate()+" "+CHF.getCurrentTime() %>" ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">作者：</td>
  <td  class="data_tb_content" ><input name="Author" type="text" maxlength="50"  class="required" title="请输入作者！" value="<%=userSession.getUserName() %>" ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">关键字：</td>
  <td  class="data_tb_content" ><textarea name="KeyValue" cols="100" rows="5"></textarea><br><font color="red">若需输入多个关键字，可用“,”分隔。注：如果要自定义关键字，请在选择关键字后在自行追加</font></td>
</tr>
<tr>
  <td class="data_tb_alignright"  colSpan="2">
	  <div style="width:100%;height:150;overflow-x:hidden;overflow-y:scroll">
	  <table width="100%" height="100%" border="0"  cellpadding="0" cellspacing="0">
<%
	ArrayList alrs = plm.getTaskConn();
	int op =0;
	for(int i=0;i<alrs.size();i++){
		if(i%4==0){
			if(op==0){
				out.println("<tr class=\"DGtd\">");
				op=1;
			}else{
				out.println("</tr><tr>");
			}
		}
		out.println("<td bgColor=\"#ffffff\"><input type='checkbox' name='cbx' value='"+(String)alrs.get(i)+"' onclick='chkConn();'></td><td bgColor=\"#ffffff\">"+(String)alrs.get(i)+"</td>");
	}
	out.println("</tr>");
%>
	</table>
	</div>  
  </td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">问题说明：</td>
  <td  class="data_tb_content" style="height: 500px;">
  <input type="hidden" id="content" name="content" value="${proclamation.content }">
			<jsp:include page="../AS_INCLUDE/images/edit.html" />
  		<textarea   name="Context" id="Context" style="display: none;"><%=sContext%></textarea>
  			
  </td>
</tr>
</table>
<center><div id="sbtBtn" ></div></center>
</div>
<input name="adored" type="hidden" id="adored" value="ad">
<input name="pid" type="hidden" id="pid" value="1">
</form>
</body>
</html>
<script type="text/javascript">
	function editQuantityType(){
		var quantity = document.getElementById("QuestionType").value;
		if(quantity == ""){
			alert("请先选择")
		}
	}

    function save(){
    	if (!formSubmitCheck('thisForm')) return;
    	
    	var IframeID=document.getElementById("oblog_Composition").contentWindow;
		document.getElementById("Context").value = IframeID.document.body.innerHTML;
		var questionDescribe = IframeID.document.body.innerHTML;
	
		if(questionDescribe.indexOf("<P>&nbsp;</P>")>-1){
			questionDescribe = questionDescribe.replace("<P>&nbsp;</P>","");
		}
		
		if(questionDescribe=="" || questionDescribe==null){
			alert("内容不能为空!");
			return;
		}
    	
    	thisForm.action = "${pageContext.request.contextPath}/question/AddAndEdit.jsp";
    	thisForm.target = "";
    	thisForm.submit();
    }
</script>
<script>
<%
	if(!tid.equals("1")&&!tid.equals("") ){
		if(!tid.equals("00003")){
	%>
		document.thisForm.QuestionType.value="<%=tid%>";
		document.thisForm.QuestionType.readOnly =true;
		setObjDisabled("QuestionType");
	<%
		}
	}
	if(!id.equals(""))
	{
	%>

		document.thisForm.QuestionType.value="<%=plt.getQuestionType()%>";
		document.thisForm.Title.value="<%=plt.getTitle()%>";
		document.thisForm.Author.value="<%=plt.getAuthor()%>";
		document.thisForm.KeyValue.value="<%=plt.getKeyValue()%>";
		document.thisForm.pid.value="<%=plt.getId()%>";
		document.thisForm.GreateDate.value="<%=plt.getGreateDate()%>";
		document.thisForm.adored.value="ed";
		
		 var IframeID=document.getElementById("oblog_Composition").contentWindow;
		 var context = document.getElementById("Context").value;
		 IframeID.document.body.innerHTML =context;
		 
		if(document.thisForm.KeyValue.value!=""){
			var len = document.thisForm.cbx.length;
			var key = document.thisForm.KeyValue.value;
			var ss = key.split(",");
			for(var i=0;i<len;i++){
				for(var j=0;j<ss.length;j++){
					if(document.thisForm.cbx[i].value == ss[j]){
						document.thisForm.cbx[i].checked=true;
					}
				}
			}
		}
	<%
		}
	}catch(Exception e)
	{
		out.print(e);
	}finally{
		 DbUtil.close(conn);
	}
	%>
new Validation('thisForm');
function goAdEd()
{
	document.thisForm.action="AddAndEdit.jsp";
	document.thisForm.submit();
}

function chkConn(){
	var len = document.thisForm.cbx.length;
//	alert(len);
	document.thisForm.KeyValue.value = "";
	for(var i=0;i<len;i++){
		if(document.thisForm.cbx[i].checked==true)
		document.thisForm.KeyValue.value += document.thisForm.cbx[i].value + ",";
	}
}

</script>
