<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>招聘计划</title>
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
		
		new Ext.form.DateField({
			applyTo : 'toworktime',
			width: 133,
			format: 'Y-m-d'
		});
    }
    window.attachEvent('onload',ext_init);

</script>
</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-30);overflow: auto;" >

<input name="flag" type="hidden" id="flag" value="${flag}">
<input name="table" type="hidden" id="table" value="${table}">
<input name="unid" type="hidden" id="unid" value="${edit.unid }">
<input type="hidden" name="who" id="who" value="${who}">

<span class="formTitle" >招聘信息<br/><br/> </span>
	<br>
	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
	<tr>
	  <td class="data_tb_alignright" align="center" colspan="4">招聘情况</td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">总部/分所<span class="mustSpan">[*]</span>：</td>
	  <td  class="data_tb_content" width="35%">
	  		<input type="hidden" value='${edit.areaid }' name='areaid'>
	  		<input type="hidden" value='${edit.areaname }' name=areaname>${edit.areaname }
	  <td class="data_tb_alignright"  width="35%" align="right">部门<span class="mustSpan">[*]</span>：</td>
	  <td class="data_tb_content" width="15%">
	  		<input type="hidden" value='${edit.departmentid }' name='departmentid'>
	  		<input type="hidden" value='${edit.departmentname }' name='departmentname'>${edit.departmentname }
	  		
	  		<input type="hidden" value='已审批' name='status'>
	  </td>
	</tr>	
	<tr>
	 
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">岗位名称<span class="mustSpan">[*]</span>：</td>
	  <td  class="data_tb_content"><input class="required" value='${edit.jobname }' name="jobname" type="text" id="jobname" size=40 class="required"></td>
	   <td class="data_tb_alignright"  width="15%" align="right">状态<span class="mustSpan">[*]</span>：</td>
	  <td  class="data_tb_content" width="25%">
			<select id='state' name="state">
			<option value = '有效' <c:if test="${edit.state == '有效'}">selected</c:if> >有效</option>
			<option value = '失效' <c:if test="${edit.state == '失效'}">selected</c:if> >失效</option>
			</select>
	  </td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">招聘人数<span class="mustSpan">[*]</span>：</td>
	  <td  class="data_tb_content" >
	  	<input class="validate-positiveInt"  validchar="0123456789"
	  		 onkeypress="return blockChar(this)" onpaste="return false" value='${edit.peoplecount }' name="peoplecount" type="text" id="peoplecount"  
	  		 class="required">
	  </td>
	  <td class="data_tb_alignright"  width="15%" align="right">招聘类型<span class="mustSpan">[*]</span>：</td>
	  <td class="data_tb_content" width="25%">
	  	<select id="type" name="type">
	  		<option value="应届生招聘" <c:if test="${edit.type == '应届生招聘'}">selected</c:if> >应届生招聘</option>
	  		<option value="社会招聘" <c:if test="${edit.type == '社会招聘'}">selected</c:if> >社会招聘</option>
	  	</select>
	  </td>
	</tr>		
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">资历要求：</td>
	  <td  class="data_tb_content" width="35%">
	  	<input value='${edit.qualifications }' name="qualifications" type="text" id="qualifications" 
	  	multiselect="true"  autoid=700 refer='资历' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" 
	  	noinput=true autoHeight=150 ></td>
	 <td class="data_tb_alignright"  width="15%" align="right">学历要求：</td>
	  <td  class="data_tb_content" width="35%">
	  	<input value='${edit.education }' name="education" type="text" id="education" autoid=700 refer='学历' 
	  	onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150 
	  	></td>	  
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">到岗时间要求：</td>
	  <td  class="data_tb_content"  ><input value='${edit.toworktime }' name="toworktime" type="text" id="toworktime" ></td>
	   <td class="data_tb_alignright"  width="15%" align="right">证书要求：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.certificate }' name="certificate" type="text" id="certificate" multiselect="true" 
	  autoid=700 refer='证书' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" 
	  noinput=true autoHeight=150 ></td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">工作城市：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.city }' name="city" type="text" id="city" autoid=740 multilevel=true 
	  	onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true 
	  	autoHeight=150 </td>  
	  <td class="data_tb_alignright"  width="15%" align="right">工时要求：</td>
	  <td  class="data_tb_content">
			<select id='working' name="working">
			<option value = '全职' <c:if test="${edit.working == '全职'}">selected</c:if> >全职</option>
			<option value = '实习' <c:if test="${edit.working == '实习'}">selected</c:if> >实习</option>
			</select>
	  </td>
	</tr>	
	<tr> 
	  <td class="data_tb_alignright"  width="15%" align="right">招聘要求<span class="mustSpan">[*]</span>：</td>
	  <td  class="data_tb_content" colspan="3"><textarea name="remark" id="remark" cols="100" rows="10" maxlength="500"  onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);">${edit.remark}</textarea></td>
	</tr>
	<tr> 
	  <td class="data_tb_alignright"  width="15%" align="right">招聘原因：</td>
	  <td  class="data_tb_content" colspan="3"><textarea name="reason" id="reason" cols="100" rows="10" maxlength="500"  >${edit.reason}</textarea></td>
	</tr>
	
	<tr style="display: none;">
	  <td class="data_tb_alignright"  width="15%" align="right">最后修改人：</td>
	  <td  class="data_tb_content" width="35%"><input class="before" readonly value='${edit.lastuser }' name="lastuser" type="text" id="lastuser" ></td>
	  <td class="data_tb_alignright"  width="15%" align="right">最后修改时间</td>
	  <td  class="data_tb_content" width="35%"><input class="before" readonly value='${edit.lasttime }' name="lasttime" type="text" id="lasttime" ></td>
	</tr>		
	
	</table>

<center><div id="sbtBtn" ></div></center>
</div>
</form>
</body>
</html>
<script type="text/javascript">

function save(){
	if (!formSubmitCheck('thisForm')) return;
	
	thisForm.action = "${pageContext.request.contextPath}/job.do?method=save";
	thisForm.target = "";
	thisForm.submit();
	
}

function blockChar(oText){
     sChar = oText.getAttribute("validchar");
     ddd = String.fromCharCode(window.event.keyCode);
     var res = sChar.indexOf(ddd) > -1;
     return res || window.event.ctrlKey;
    }
</script>