<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>

<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>


<style type="text/css">

  .formTable input{
    width: 90%;
  }
  

</style>

<script Language=JavaScript>

	function ext_init(){
	
		mt_form_initDateSelect();
		
		
		
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'提交申请',
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
					window.history.back();
				}
       		}
			]
        });
        
    }
    window.attachEvent('onload',ext_init);
</script>
<style type="text/css">

 #t {
		{border-collapse:collapse;border:none;};
	}
	
	#t td{
		{border:solid #6595d6 1px;};
	}

</style>
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>
<form action="${pageContext.request.contextPath}/official.do?method=apply" method="post" name="thisForm">
<table class="formTable" style="width:93%;" >
	<tbody>
		<tr>
			<th>
				姓名</th>
			<td>
				<input  type="text" id="name" name="name" value="${employee.name}" readonly="readonly"/></td>
			<th>
				性别</th>
			<td>
				<input type="text" name="sex" id="sex" value="${employee.sex}"> 
				<!--  
				<select id="sex" name="sex" readonly="readonly">
							<option value="男"
							 <c:if test="${employee.sex eq 男 }">selected</c:if>>男</option>
							<option value="女"
							<c:if test="${employee.sex eq 女 }">selected</c:if>>女</option>
			</select>
			-->
			</td>
			<th>
				出生日期</th>
			<td>
				<input id="birthday" name="birthday"  ext_id=birthday ext_name =birthday ext_type=date  type="text" style="width:100px" value="${employee.brithday}" readonly="readonly"/></td>
			<th>
				部门</th>
			<td width="120px" colspan="3">
				<input id="departmentId" name="departmentId" type="text" autoid="30026" value="${employee.departmentId}"  readonly="readonly"/></td>
		</tr>
		<tr>
			<th>
				参加工作时间</th>
			<td>
				<input id="joinTime" name="joinTime"  ext_type=date type="text" value="${employee.work_strart_time1}" readonly="readonly"/></td>
			<th colspan="2">
				进所工作日期</th>
			<td colspan="2">
				<input id="entryTime" name="entryTime"  ext_type=date ext_type=date type="text" value="${employee.assume_office_time}" readonly="readonly"/></td>
			<th>
				合同期限</th>
			<td width="95px">
				<input id="pactlimit" name="pactlimit"  ext_type=date ext_type=date type="text" class="required"/>
				</td><td>至</td><td><input id="pactlimitEnd" name="pactlimitEnd"  ext_type=date ext_type=date type="text" class="required"/></td>
		</tr>
		<tr>
			<th>
				文化程度</th>
			<td>
				<input id="education" name="education" type="text" value="${employee.grad_formal}" readonly="readonly"/></td>
			<th>
				毕业学校</th>
			<td colspan="2">
				<input id="school" name="school" type="text" value="${employee.grad_school1}" readonly="readonly"/></td>
			<th>
				专业</th>
			<td colspan="4">
				<input id="specialty" name="specialty" type="text" value="${employee.profession_from_prac}" readonly="readonly"/></td>
		</tr>
		<tr>
	    <th>
		  审核人</th>
			<td colspan="2">
				<input name="checker_id" id="checker_id" autoid="10099"    class="required"/>
				<input name="cardNum" id="cardNum" type="hidden" value="${employee.idcard}"/></td>
		</tr>
		
	</tbody>
</table>
</form>
</body>
<script type="text/javascript">

	function save() {
  	
  	if (!formSubmitCheck('thisForm')){
  		return ;
  	}
  	document.thisForm.submit();
  	}
 
	function view(){
		var form_obj = document.all; 
		
		//form的值
		for (var i=0; i < form_obj.length; i++ ) {
			e=form_obj[i];
			if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
				e.readOnly = true ;
				e.className = "readonly";
				e.disabled = true;
				e.backgroundImage = "none";
			}
			if(e.tagName=='SELECT'){
				e.disabled= true;
				e.className = "readonly";
			}
			if(e.tagName == 'A'){
				e.style.display = "none";
				e.disabled = true;
			}
			if(e.tagName == "IMG"){
				e.style.display = "none";
				e.disabled = true;
			}
		}
	}


</script>
</html>