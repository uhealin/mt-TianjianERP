<%@page language="java"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@page buffer="none"%>
<%@page import="java.util.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=Utf-8">
<title>检索</title>
<style>
.flyBT{
	background: url(../images/bbk.gif);
	cursor: hand;
	background-color:#ffffff;
	height=24;
	font-size:12px;
	border:#333333 1px solid;
}
input { background-color:#ffffff;height:21px;border-left: 1px solid #CCCCCC;border-top:1px solid #CCCCCC;border-right: 1px solid #CCCCCC;border-bottom:1px solid #CCCCCC; font-family: "宋体"; font-size: 9pt; padding-left: 4; padding-right: 0; padding-top:3; padding-bottom: 0}
</style>
</head>

<body leftmargin="1" topmargin="1">
<%
	String allColName=",菜单ID, ,父菜单ID, ,深度, ,类型, ,名称, ,行为, ,目标";
  
       org.util.Debug.prtOut("===s5g==="+allColName);
%>

<form name="thisForm">
  <table width="460" border="0" cellpadding="0" cellspacing="1" bgcolor="#0000CC">
    <tr>
      <td><table width="460" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
          <tr>
            <td height="28" align="center" valign="middle" background="../images/sw_back.jpg">&nbsp;<strong><font color="#FFFFFF" size="3">┎</font></strong><strong><font color="#FFFFFF" size="3">检索窗口┒</font></strong></td>
          </tr>
          <tr>
            <td height="41"><table width="460" height="111" border="0" cellpadding="0" cellspacing="1" bgcolor="#FFFFFF">
                <tr align="center">
                  <td height="22" background="../images/sw_back2.jpg"><strong><font size="2">查询项</font></strong></td>
                  <td height="19" background="../images/sw_back2.jpg"><strong><font size="2">操作符</font></strong></td>
                  <td height="19" background="../images/sw_back2.jpg"><strong><font size="2">查找值</font></strong></td>
                  <td height="19" background="../images/sw_back2.jpg"><strong><font size="2">逻辑</font></strong></td>
                </tr>
                <tr align="center" bgcolor="#E4EAF2">
                  <td height="20"> <select name="searchField" onChange="openThose(0);">
                      <option value="">--请选择--</option>
                    </select></td>
                  <td height="20"> <select name="operator">
                    </select> </td>
                  <td height="20"> <input size="24" name="inputValue" onKeydown="javaScript:if(window.event.keyCode==13)submitSearch();">
                    &nbsp;</td>
                  <td height="20"> <select name="logical">
                    </select> &nbsp;</td>
                </tr>
                <tr align="center" bgcolor="#D3DDEB">
                  <td height="20"> <select name="searchField" onChange="openThose(1);">
                      <option value="">--请选择--</option>
                    </select></td>
                  <td height="20"> <select name="operator">
                    </select> </td>
                  <td height="20"> <input size="24" name="inputValue" onKeydown="javaScript:if(window.event.keyCode==13)submitSearch();">
                    &nbsp;</td>
                  <td height="20"> <select name="logical">
                    </select> &nbsp;</td>
                </tr>
                <tr align="center" bgcolor="#E4EAF2">
                  <td height="20"> <select name="searchField" onChange="openThose(2);">
                      <option value="">--请选择--</option>
                    </select></td>
                  <td height="20"> <select name="operator">
                    </select> </td>
                  <td height="20"> <input size="24" name="inputValue" onKeydown="javaScript:if(window.event.keyCode==13)submitSearch();" >
                    &nbsp;</td>
                  <td height="20">&nbsp;</td>
                </tr>
              </table></td>
          </tr>
          <tr bgcolor="#D3DDEB" >
            <td height="28" align="center" valign="middle" bgcolor="#D3DDEB">
              <input type="button" name="submit" value="确  定" class="flyBT" onClick="submitSearch();">
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
  </form>


<script language="javaScript">

	optSearchField="<%=allColName%>".split(",");

	optOperator=new Array(
		"like","模糊",
                "likeRight","右模糊",
                "likeLeft","左模糊",
		">'","大于",
		"<'","小于",
		"='","等于",
		">='","大于等于",
		"<='","小于等于"

		);
	optLogical=new Array(
		"","--",
		" or ","或",
		" and ","和"
		);


	//初始化下拉列表。
	for(var i=0;i<window.document.thisForm.searchField.length;i++)
	{
		addOptions_select(optSearchField,"",window.document.thisForm.searchField[i]);
	}

	for(var i=0;i<window.document.thisForm.operator.length;i++)
	{
		addOptions_select(optOperator,"",window.document.thisForm.operator[i]);
	}

	for(var i=0;i<window.document.thisForm.logical.length;i++)
	{
		addOptions_select(optLogical,"",window.document.thisForm.logical[i]);
	}


	for(var i=0;i<window.document.thisForm.searchField.length;i++)
	{
		window.document.thisForm.operator[i].disabled=true;
		window.document.thisForm.inputValue[i].disabled=true;
		if(i!=0)
		{
			window.document.thisForm.searchField[i].disabled=true;
		}
		if(i!=window.document.thisForm.searchField.length-1)
		{
			window.document.thisForm.logical[i].disabled=true;
		}
	}


	function openThose(indexId)
	{
		if(window.document.thisForm.searchField[indexId].value!="")
		{
			window.document.thisForm.operator[indexId].disabled=false;
			window.document.thisForm.inputValue[indexId].disabled=false;

			if(indexId!=window.document.thisForm.searchField.length-1)
			{
				window.document.thisForm.searchField[indexId+1].disabled=false;
			}
			if(indexId>0 && window.document.thisForm.searchField[indexId-1].value!="")
			{
				window.document.thisForm.logical[indexId-1].disabled=false;
			}
		}
		else
		{
			window.document.thisForm.operator[indexId].disabled=true;
			window.document.thisForm.inputValue[indexId].disabled=true;
			if(indexId==0)
			{
				window.document.thisForm.logical[0].disabled=true;
			}
			if(indexId>0)
			{
				window.document.thisForm.logical[indexId-1].value="";
				window.document.thisForm.logical[indexId-1].disabled=true;
			}
		}
	}


	function submitSearch()
	{
		var strWhere="";

                var s="$+=|'#&<>%*`^/\\\";";
                for(k=0;k<window.document.thisForm.inputValue.length;k++){
                  var str1=document.thisForm.inputValue[k].value;
                  for (i=0; i<str1.length; i++){
                    for(j=0;j<s.length;j++){
                      if (str1.charAt(i) == s.charAt(j)){
                        alert("关键字中不能包含特殊字符: $+=|'#&<>%*`^/\\\";");
                        return false;
                      }
                    }
                  }
                }

		for(var i=0;i<window.document.thisForm.searchField.length;i++)
		{

			if(window.document.thisForm.searchField[i].value!="")
			{
				strWhere=strWhere + "*" + window.document.thisForm.searchField[i].value;

				if(window.document.thisForm.operator[i].value=="like")
				{
					strWhere=strWhere + "*" + window.document.thisForm.operator[i].value + "*'$" + trim(window.document.thisForm.inputValue[i].value) + "$'";
				}
                                else if(window.document.thisForm.operator[i].value=="likeRight")
                                {
                                        strWhere=strWhere + "*like*'" + trim(window.document.thisForm.inputValue[i].value) + "$'";
                                }
                                else if(window.document.thisForm.operator[i].value=="likeLeft")
                                {
                                        strWhere=strWhere + "*like*'$" + trim(window.document.thisForm.inputValue[i].value) + "'";
                                }
				else
				{
					if(window.document.thisForm.operator[i].value.indexOf("'")>=0)
					{
						strWhere=strWhere + "*" + window.document.thisForm.operator[i].value + "" + trim(window.document.thisForm.inputValue[i].value) + "'";
					}
					else
					{
						strWhere=strWhere + "*" + window.document.thisForm.operator[i].value + "" + trim(window.document.thisForm.inputValue[i].value) + "";
					}
				}
			}
			if(i!=window.document.thisForm.searchField.length-1)
			{
				strWhere=strWhere + "*" + window.document.thisForm.logical[i].value;
			}
		}

		for(var i=0;i<window.document.thisForm.logical.length;i++)
		{
			if(window.document.thisForm.logical[i].disabled==false && window.document.thisForm.logical[i].value=="")
			{
				alert("必须选择‘逻辑操作符’!");
				return;
			}
		}

		if(window.document.thisForm.searchField[0].value=="")
		{
			alert("最少选择一个查询条件！");
			return;
		}
		if(strWhere=="" || window.document.thisForm.inputValue[0].value=="")
		{
			alert("请正确填写查询条件！");
			return;
		}
		strWhere="and*(" + strWhere + ")";
		window.opener.changeGrid_CH("strWhere_CH",strWhere);
		window.close();
	}
	//列出option 选项的函数
	function addOptions_select()
	{
		var i,start,step,len,Args0,Args1,Args2,strTemp;
		var Args=addOptions_select.arguments;
		Args0 = Args[0];
		Args1 = Args[1];
		Args2 = Args[2];
		len = Args0.length;
		start = 0;
		step = 2;
		for(i=start;i<len;i+=step)
		{
			var opt=document.createElement("OPTION");
			opt.value=Args0[i];
			opt.text=Args0[i+1];
                        if(opt.value!='p1'&&opt.value!='p2'&&opt.text!='编号'&&opt.text!='Autoid'){
                          Args2.add(opt);
                        }
			if(Args0[i]==Args1)
			{
				Args2[(i)/2].selected=true;
			}
		}
	}


//******************************************************//
//@caption: trim(str)
//@title: 去除字符串左右两边的空格
//@discription: 去除字符串左右两边的空格。
//@param: 字符串 如：" abc  "
//@output: 字符串 如："abc"
//@author: loist
//@date: 2003-2-11
//@modified:
//@date:
//******************************************************//
function trim(str)
{

        var i = 0,j=0;
        var trimStr
        var len = str.length;
        if ( str == "" )
        	return( str );
        flagBegin = true;
        flagEnd = true;
        while ( flagBegin == true && i< len)
        {
		if ( str.charAt(i) == " " )
		{
			i=i+1;
			flagBegin=true;
		}
                else
                {
                        flagBegin=false;
                }
        }
		j = len -1;
        while  (flagEnd == true && j>=0)
        {
            if (str.charAt(j)==" ")
		{
		        j=j-1;
		        flagEnd=true;
		}
                else
                {
                        flagEnd=false;
                }
        }
        if ( i > j )
        	return ("")
        trimStr = str.substring(i,j+1);
        return trimStr;
}


</script>
</body>

</html>
