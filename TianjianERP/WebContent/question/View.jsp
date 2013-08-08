<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ page import="java.util.*"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%

	QuestionTable plt = new QuestionTable();
	
	ASFuntion CHF=new ASFuntion();
	String id = CHF.showNull(request.getParameter("chooseValue"));
	
	Connection conn =null;
	try{
	  	 conn = new DBConnect().getConnect();
	  	QuestionMan plm = new QuestionMan(conn);
	  	plm.updateViewCount(id);
		if(id.equals(""))
		{

			response.sendRedirect("List.jsp");
		}
		else
		{
			plt = plm.getAQuestionDetail(id);
		}
%>

<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.question.model.QuestionTable"%>
<%@page import="com.matech.audit.service.question.QuestionMan"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查看问题库</title>
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
  <td  class="data_tb_content" ><input value="<%=id.equals("")?"":plt.getTitle()%>" name="Title" type="text" size="100" maxlength="100" class="required" title="请输入文件标题，不能为空,不能多于100个汉字！" ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">问题分类：</td>
  <td  class="data_tb_content" ><input value="<%=id.equals("")?"":plt.getTitle()%>" name="QuestionType" type="text"  ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">记录时间：</td>
  <td  class="data_tb_content" ><%=id.equals("") ? "0" : plt.getGreateDate()%></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">作者：</td>
  <td  class="data_tb_content" ><input value="<%=id.equals("")?"":plt.getAuthor()%>" name="Author" type="text" maxlength="50"  class="required" title="请输入文号！" ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">关键字：</td>
  <td  class="data_tb_content" ><textarea name="KeyValue" cols="100" rows="5"><%=id.equals("")?"":plt.getKeyValue()%></textarea></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">问题说明：</td>
  <td  class="data_tb_content" ><textarea name="Context" cols="100" rows="30" class="required"><%=id.equals("")?"":plt.getContext()%></textarea></td>
</tr>
</table>

</div>
</form>
</body>
</html>
<script>
<%

	}catch(Exception e)
	{
		out.print(e);
	}finally{
		 DbUtil.close(conn);
	}
	%>

View();
function View(){
	var form_obj = document.all; 
	//form的值
	for (i=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = true ;
			e.className = "before";
			if(e.type == 'checkbox'){
				e.disabled = true ;
			}
		}
		if(e.tagName=='SELECT'){
			e.disabled= true;
			e.className = "before";
		}
		//alert(e.tagName);
		if(e.tagName == 'A'){
			e.style.display = "none";
		}
		if(e.tagName == "IMG"){
			e.style.display = "none";
		}
		
	}
		
	document.getElementById("sbtBtn").style.display = "none";
//	document.getElementById("memoTr").style.display = "none";
}
</script>
