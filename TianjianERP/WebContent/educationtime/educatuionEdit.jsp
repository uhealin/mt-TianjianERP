<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>

<script type="text/javascript">
  Ext.onReady(function(){
    new Ext.form.DateField({			
		applyTo: 'educationtime',
		width: 133,
		format: 'Y-m-d'	
  })
     var tbar_user = new Ext.Toolbar({
		renderTo: 'btn_div',
		items:[{
			text:'保存',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/save.gif',
			handler:goSave
		},'-',{
			text:'返回',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/back.gif',
			handler:function(){
			    window.location.href="${pageContext.request.contextPath}/educationTime.do";
				//goEdit();
			}
		}]
  })
  })
</script>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>修改学时</title>
</head>
<body style="font-size: 14">
<div id="btn_div"></div>
<div style="height:100%;width: 80%">
  <form  name="myform" id="thisForm" method="post">
     <table  class="formTable" width="60%">
     <thead>
     <th colspan="4">学时管理</th>
     </thead>
     <tbody>
        <tr>
           <th> 培训日期<span class="mustSpan">[*]</span>：<input type="hidden" name="id" id="id" value="${educationtime.id }"></th>
           <td><input type="text" id ="educationtime" name="educationtime" value="${educationtime.educationtime }"></td>
           <th >学员姓名<span class="mustSpan">[*]</span>：</th>
           <td><input title="请输入学员姓名" type="text" id ="username" name="username" class="required" value="${educationtime.username}"> </td>
        </tr>
         <tr>
           <th>学时数<span class="mustSpan">[*]</span>：</th>
           <td ><input type="text"  title="请输入学时数" id ="hoursNum" name="hoursNum" class="required"  value="${educationtime.hoursNum }"></td>
           <th>学时形式<span class="mustSpan">[*]</span>：</th>
           <td><input type="text" id ="hoursType" title="请输入学时形式" name="hoursType"
           			onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true 
					autoid=2058
            class="required" value="${educationtime.hoursType }"></td>
        </tr>
        <tr>
           <th>培训班号<span class="mustSpan">[*]</span>：</th>
           <td><input type="text" id ="classNum" name="classNum" title="请输入培训班号" class="required" value="${educationtime.classNum }"></td>
           <th>证书编号<span class="mustSpan">[*]</span>：</th>
           <td><input type="text" id ="classNum" name="graduationNum" title="请输入证书编号" class="required" value="${educationtime.graduationNum }"></td>
        </tr>
  </tbody>
     </table>
     
  </form>
  </div>
</body>
</html>
<script type="text/javascript">
new Validation('thisForm'); 

    function goSave(){
       if (!formSubmitCheck('thisForm')) return ;
       document.thisForm.action="${pageContext.request.contextPath}/educationTime.do?method=updateTime"
       document.thisForm.submit();
    }
</script>