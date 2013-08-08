<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.matech.audit.service.department.model.DepartmentVO"%>
<%@ page import="com.matech.audit.service.userdef.Userdef"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%

	String act=(String)request.getAttribute("act");

	DepartmentVO departTable=(DepartmentVO)request.getAttribute("departmentVO");
	Userdef[] al=(Userdef[])request.getAttribute("userdef");

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>部门管理</title>
<script>

	function ext_init(){
        
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            
	            	if (!formSubmitCheck('thisForm')) return;
	            	
	            	if (goAdEd())
						document.thisForm.submit();
				}  
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'->'
			]
        });
        
    }
    window.attachEvent('onload',ext_init);




	function deleteLine(){
		var t=false;
		for (var i=UserDefTbody.children.length-1; i>=0 ; i-- )
		if (UserDefTbody.children[i].firstChild.firstChild.checked){
			UserDefTbody.deleteRow(i);
			t=true;
		}
		if(!t)
		{
			alert("请选定其中一列！！");
		}
	}
	
	function addLine(){
	
		var obj=document.getElementById("UserDefTbodyCount");
		//增加计数
		obj.value=obj.value-0+1;
	
	    //var attachstable=document.getElementById("attachstable");
	
		var objTr=attachstable.insertRow(-1);
	
		var objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=checkbox id=checkLine name=\"checkbox\">";
		//alert(objTr.innerHTML);
	
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input name=\"UserDefName\" id=\"UserDefName"+obj.value+"\" class='required'  maxLength=20  title='请输入，不得为空' size=10 style=\"width: 400; height: 18\">";
	
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input name=\"UserDefValue\" id=\"UserDefValue"+obj.value+"\" class='required'  maxLength=100 onKeyUp=\"if(this.value.length>100)this.value=this.value.substring(0,100);\" title='请输入，不得为空' size=10 style=\"width: 150; height: 18\">";
	}
</script>
</head>
<body leftmargin="0" topmargin="0">

<div id="divBtn"></div>

<form name="thisForm" method="post" class="autoHeightForm">
  <input name="autoID" type="hidden" id="autoID" value="<%=departTable.getAutoId()==0 ? 555555 : departTable.getAutoId() %>">
  		
  <span class="formTitle" >
	部门信息维护<br/><br/> 
	</span>
  
  <table width="75%" height="182" border="0" cellpadding="0" cellspacing="0">

    <tr>
      <td width="140"><div align="right">部门名称<span class="mustSpan">[*]</span>：</div></td>
      <td>
      	<input name="departName" id="departName" maxlength="20" type="text" style="height:20px;width:300px"　 
      			class="required" title="请输入部门名称, 不能为空！">
      	<span id="attentionSpan"></span>
      	<input name="act" id="act" maxlength="20" type="hidden">
      </td>
    </tr>
	
	<tr>
      <td width="140"><div align="right">上级部门<span class="mustSpan">[*]</span>：</div></td>
      <td>
      	<input name="parentID" type="text" id="parentID" value="555555"  noinput=true  class="required" refer=autoID
      	onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=124>
      </td>
    </tr>

	<tr>
      <td width="140"><div align="right">部门默认底稿模板：</div></td>
      <td>
      	<input name="typeid" type="text" id="typeid"  noinput=true  
      	onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=65>
      </td>
    </tr>	
	
	
	<tr>
      <td width="140"><div align="right">部门服务器地址<span class="mustSpan"></span>：</div></td>
      <td>
      	 <input name="url" type="text" id="url" value=""  class="ip-wheninputed"  >
	    <br>如果为空则是本地部门，如果有ip则为外网部（用于集团监控）。
	    <br>[例子]内容可以是ip:"202.96.128.68"，或者域名"www.google.com"。
      </td>
    </tr>
    
    <tr>
      <td width="140"><div align="right">邮&nbsp;&nbsp;&nbsp;&nbsp;编：</div></td>
      <td>
      	<input name="postalcode" type="text" id="postalcode" >
      </td>
    </tr>
    <tr>
      <td width="140"><div align="right">地&nbsp;&nbsp;&nbsp;&nbsp;址：</div></td>
      <td>
      	<input name="address" type="text" id="address" style="height:20px;width:300px">　
      </td>
    </tr>
    
    
</table>




<div style="display:none">

<fieldset  style="width:100%">
<legend>自定义信息</legend>
<br>
       <table width="100%" border="1" cellpadding="0" cellspacing="0" name=attachstable id=attachstable>
          <thead id=thead1>
          <tr>
            <td width="5%">
              <div align="center">选</div>
            </td>
            <td width="60%">
              <div align="center">自定义名字</div>
            </td>
            <td width="30%">
              <div align="center">自定义值</div>
            </td>
          </tr>
          
	    <input type="hidden" name="setdef" id="setdef" value="0"> 
	    <c:choose>
			<c:when test="${act=='add'}">
			    <script>
			         document.getElementById("setdef").value="1";
			    </script>  
			       
		        <c:forEach items="${setValueList}" var="setvalue">
		          <tr>
	          		<td>&nbsp;</td>
	          		<td><input type="text" name="defName"  value="${setvalue.defName}" id="defName" size=10 readonly="readonly" maxLength=20  style="width: 400; height: 18"></td>
	              	<td><input type="text" name="defValue" value="" id="defValue" size=10  maxLength=50   style="width: 150; height: 18"></td>	         
		          </tr>
		        </c:forEach>  
		    </c:when>  
		    
		    <c:when test="${act=='update'}"> 
		   	    <script>
			         document.getElementById("setdef").value="1";
			    </script>  
			          
		        <c:forEach items="${setValueList}" var="setvalue">
		          <tr>
	          		<td>&nbsp;</td>
	          		<td><input type="text" name="defName"  value="${setvalue.defName}" id="defName" size=10 readonly="readonly" maxLength=20  style="width: 400; height: 18"></td>
	              	<td><input type="text" name="defValue" value="${setvalue.defValue}" id="defValue" size=10  maxLength=50   style="width: 150; height: 18"></td>	         
		          </tr>
		        </c:forEach>  
		    </c:when>   
	    </c:choose>   
          
          
  		  <tbody id=UserDefTbody>

          </tbody>
        </table>

<input type="hidden" id=UserDefTbodyCount name=UserDefTbodyCount value="0" />

<input type="hidden" id="commondefNames" name="commondefNames" value="names" />
<input type="hidden" id="commondefValues" name="commondefValues" value="values" />

<input type=button onClick="addLine()" value=添加 name="button">
<input type=button onClick="deleteLine()" value=删除 name="button">
</fieldset>
</div>















  <input name="adored" type="hidden" id="adored" value="ad">
</form>



<%
if(departTable.getAutoId()!=0)
	for(int i=0; i<al.length;i++){
		Userdef udtab = al[i];
%>
<script Language=JavaScript>
	addLine();
	document.getElementById("UserDefName<%=i+1%>").value = "<%=udtab.getName()%>"
	document.getElementById("UserDefValue<%=i+1%>").value = "<%=udtab.getValue()%>"
</script>
<%
	}
%>


<script>
	<%
		if(departTable.getAutoId()!=0) {
			String departName2 = departTable.getDepartmentName();
	%>
			document.thisForm.parentID.value="<%=departTable.getParentId()%>";
			document.thisForm.departName.value="<%=departName2%>";
			document.thisForm.url.value="<%=departTable.getUrl()%>";
			document.thisForm.postalcode.value="<%=departTable.getPostalcode()%>";
			document.thisForm.address.value="<%=departTable.getAddress()%>";
			if(document.thisForm.url.value!=""){
				document.thisForm.url.className="required ip-wheninputed";
				document.thisForm.url.title="该部门为外部网络部门，必须输入网络地址。";
			}else{
				document.getElementById("url").disabled="disabled";
			}
			document.thisForm.autoID.value="<%=departTable.getAutoId()%>";
			
			document.thisForm.typeid.value="<%=departTable.getTypeid()%>";
			
			document.thisForm.adored.value="ed";
	<%
		}

%>

new Validation('thisForm');

var flag = 0;

function goAdEd(){
	
	
	if(<%=departTable.getAutoId()%>!=0){
		document.all.act.value="update";
	}else{
		document.all.act.value="add";
	}
	
	checkExists(thisForm.departName.value);
	
	if(flag==1){
	
		alert("请不要输入重复的部门名称");		
		
			
		try {
			stopWaiting();
		} catch(e) {
		
		}
		
    	return false;
    }
	
	if(document.getElementById("setdef").value=="1"){

	    var defname = new Array();
	    var defvalue = new Array();
	    defname = document.getElementsByName("defName");
	    defvalue = document.getElementsByName("defValue");
	    var names="";
	    for(i=0;i<defname.length;i++){
	        if(defname[i].value!=""){
	        	names+=defname[i].value+"-";
	        }else{
	        	names+=+"?"+"-";
	        }
	    	
	    }
	    var values="";
	    for(i=0;i<defvalue.length;i++){
	    	if(defvalue[i].value!=""){
	    		values+=defvalue[i].value+"-";
	    	}else{
	    		values+=+"?"+"-";
	    	}
	    	
	    }
	    
	    document.getElementById("commondefNames").value=names;
	    document.getElementById("commondefValues").value=values;  
          
    } 
	document.thisForm.action="department.do?method=addAndEdit&&chooseValue="+<%=departTable.getAutoId()%>;
//	init();
//	
	//yesorno()
	return true;

}
//修改后代码，修改时间2007年5月11号，修改人杨舟皿
//**************************************************
function yesorno(){
if(document.getElementById("departName").value!=""&&document.getElementById("parentID").value!=""){
	var yon=window.confirm("是否设置权限？");
	if(yon){
	document.getElementById("opt").value="goToListPage";
}
}
}
//**************************************************
function checkExists(a) {
	if(a!="${departmentVO.departmentName}") {
		var url = "${pageContext.request.contextPath}/department.do?method=checkExists";
		var strResult = ajaxLoadPageSynch(url,"&departName=" + a);
		if(strResult=="yes") {
			document.getElementById("attentionSpan").innerHTML = "<font color='red' style='font-size:12px;'>部门名称已经存在！</font>"
			flag = 1;
		} else if(strResult=="no") {
			document.getElementById("attentionSpan").innerHTML = ""
			flag = 0;
		}
	}
}
</script>

</body>
</html>
