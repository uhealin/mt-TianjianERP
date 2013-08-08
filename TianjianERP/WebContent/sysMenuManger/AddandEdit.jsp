<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ page import="java.util.*"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>

<%
	ASFuntion CHF=new ASFuntion();
	String	menuDetail = CHF.showNull((String)request.getAttribute("string"));
	
	Vector vector=(Vector)request.getAttribute("vector");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<title></title>

</head>

<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="">
  <font color="#FF0000" size="2" ><strong>您现在所在位置： </strong><font color="#0000CC">系统管理-&gt;系统菜单管理-&gt;菜单项设置</font></font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
    </tr>
</table>
<br>
  <br>
  <br>
  <table width="75%" height="106" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="2%" height="24">&nbsp;</td>
      <td width="10%">编 号：</td>
      <td width="25%"><input name="menu_id" type="text" id="menu_id" value="" class="required validate-digits"  title="请输入数字！"></td>
      <td width="7%">&nbsp;</td>
      <td width="17%">类 型：</td>
      <td width="39%"><select name="type" id="type">
          <option value="000" >==请选择==</option>
          <option value="01" selected="selected">菜单项</option>
          <option value="02">按钮</option>
          <option value="03">Tab容器</option>
        </select></td>
    </tr>
    <tr>
      <td height="24">&nbsp;</td>
      <td>名 字：</td>
      <td><input name="name" type="text" id="name" class="required"  title="请输入名字！"></td>
      <td>&nbsp;</td>
      <td>所属菜单：</td>
      <td>
        <select name="parentid" id="parentid" style="width: 250px">
          <option value="000" selected="selected">==请选择==</option>
		  <option value="00">00</option>
		 <%if(vector!=null){



			for(int i=0;i<vector.size();i++)
		{       String[] vals=(String[])vector.elementAt(i);
				out.print("<option value=\""+vals[0]+"\">"+vals[0]+":"+vals[1]+"</option>");
			}
			}
		 %>
        </select></td>
    </tr>
    <tr>
      <td height="24">&nbsp;</td>
      <td>目 标：</td>
      <td><select name="target" id="target">
          <option value="_blank" >==请选择==</option>
          <option value="" selected="selected">主框架</option>
          <option value="leftFrame">左框架</option>
          <option value="rightFrame">右框架</option>
        </select></td>
      <td>&nbsp;</td>
      <td>是否有子菜单：</td>
      <td><select name="depth" id="depth">
          <option value="000" selected="selected">==请选择==</option>
          <option value="1">有</option>
          <option value="0" >无</option>
          <option value="2" >有按钮</option>
          <option value="-1" >执行脚本</option>
        </select></td>
    </tr>
    <tr>
      <td height="24">&nbsp;</td>
      <td>是否支持控件:</td>
      <td><select name="Amethod" id="Amethod" onchange="return showAmethod();">
          <option value="" >==请选择==</option>
          <option value="1">是</option>
          <option value="0" selected>否</option>
        </select></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr id="showAmethod" style="display:none;">
      <td height="24">&nbsp;</td>
      <td>调用控件方法：</td>
      <td>
      	<input type="text" id="ActiveX_method" name="ActiveX_method" value="" size="30"/>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
   <tr>
      <td>&nbsp;</td>
      <td>权限ID属性：</td>
      <td align="left">
      	<input type="text" id="power" name="power"  size="30" readonly="readonly"/>
      </td>
      <td colspan="3" align="left" style="color: red">用来获取人员的部门授权。</td>
    </tr>    
    <tr>
      <td height="24">&nbsp;</td>
      <td>加密狗类型：</td>
      <td colspan="4"><input  name="dogversion"
						            id="dogversion"
						            type="text"
						            size="30"
						            value="${dogVersion}"
						            class="required"
						            multiselect="true"
 									title="请输入，不得为空"
 									onkeydown="onKeyDownEvent();"
									onkeyup="onKeyUpEvent();"
									onclick="onPopDivClick(this);"
 									valuemustexist=true
 									autoid=350>
 	   </td>
    </tr>
    <tr>
      <td height="24">&nbsp;</td>
      <td>行 为：</td>
      <td colspan="4"><input name="act" type="text" id="act" size="64" maxlength="500"></td>
    </tr>

    <tr>
      <td height="24">&nbsp;</td>
      <td>帮助行为：</td>
      <td colspan="4"><input name="helpact" type="text" id="helpact" size="64" maxlength="100"></td>
    </tr>

    <tr>
      <td height="24">&nbsp;</td>
      <td nowrap="nowrap">二次登录角色：</td>
      <td colspan="4">
      	<input name="isvalidate" id="isvalidate" type="text" size="30"
 multiselect="true"  title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist=true autoid=178>
     	</td>
    </tr>
  </table>
  <table width="75%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td height="22" colspan="3">&nbsp;</td>
    </tr>
    <tr>
      <td width="37%" align="right">
        <input type="submit" name="next" id=opSave value="确  定" class="flyBT" onclick="goAdEd();">
      </td>
      <td width="8%">&nbsp;</td>
      <td width="55%"><input type="button" name="back" value="返  回" class="flyBT"  onClick="window.self.location='${pageContext.request.contextPath}/sysMenuManger.do';"></td>
    </tr>
  </table>

  <p>&nbsp;</p>
  <input name="submitStr" type="hidden" id="submitStr">
  <input name="id" type="hidden" id="id">
  <input name="adored" type="hidden" id="adored" value="ad">
</form>
</body>
</html>
<script>
<%
	if(!menuDetail.equals("")) {
		//System.out.println(CHF.getXMLData(menuDetail,"ID"));
	%>
		document.thisForm.id.value="<%=CHF.getXMLData(menuDetail,"ID")%>";
		document.thisForm.menu_id.value="<%=CHF.getXMLData(menuDetail,"menu_id")%>";
		document.thisForm.type.value="<%=CHF.getXMLData(menuDetail,"type")%>";
		document.thisForm.name.value="<%=CHF.getXMLData(menuDetail,"name")%>";
		document.thisForm.target.value="<%=CHF.getXMLData(menuDetail,"target")%>";
		document.thisForm.parentid.value="<%=CHF.getXMLData(menuDetail,"parentid")%>";
		document.thisForm.depth.value="<%=CHF.getXMLData(menuDetail,"depth")%>";
		document.thisForm.act.value="<%=CHF.getXMLData(menuDetail,"act")%>";
		document.thisForm.helpact.value="<%=CHF.getXMLData(menuDetail,"helpact")%>";
		document.thisForm.isvalidate.value="<%=CHF.getXMLData(menuDetail,"isvalidate")%>";
		document.thisForm.power.value="<%=CHF.getXMLData(menuDetail,"power")%>";
		
		
		var ActiveX_method = "<%=CHF.getXMLData(menuDetail,"ActiveX_method")%>";
		if(ActiveX_method=="null"||ActiveX_method=="") {
			document.thisForm.ActiveX_method.value="";
			document.getElementById("Amethod").value="0";
		} else {
			document.thisForm.ActiveX_method.value=ActiveX_method;
			document.getElementById("Amethod").value="1";
			document.getElementById("Amethod").onchange();
		}
		
		
		
	<%
		}
if(vector!=null){

vector.clear();
}

	%>

function goAdEd()
{
if(<%=!menuDetail.equals("")%>){
	document.thisForm.adored.value="ed";
}
	document.thisForm.action="sysMenuManger.do?method=addAndEdit";

	thisForm.submitStr.value = "<ID>"+thisForm.id.value+"</ID>"
							 + "<menu_id>"+thisForm.menu_id.value+"</menu_id>"
							 + "<parentid>"+thisForm.parentid.value+"</parentid>"
							 + "<depth>"+thisForm.depth.value+"</depth>"
							 + "<type>"+thisForm.type.value+"</type>"
							 + "<name>"+thisForm.name.value+"</name>"
							 + "<act>"+thisForm.act.value+"</act>"
							 + "<helpact>"+thisForm.helpact.value+"</helpact>"
							 + "<target>"+thisForm.target.value+"</target>"
							 + "<isvalidate>" + document.getElementById("isvalidate").value + "</isvalidate>"
							 + "<power>" + document.getElementById("power").value + "</power>"
							 + "<ActiveX_method>" + document.getElementById("ActiveX_method").value + "</ActiveX_method>";

}


function showAmethod() {
	if("1"==document.getElementById("Amethod").value) {
		document.getElementById("showAmethod").style.display="";
	} else {
		document.getElementById("ActiveX_method").value="";
		document.getElementById("showAmethod").style.display="none";
		
	}
}
</script>
