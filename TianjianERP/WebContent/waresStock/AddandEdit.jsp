<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>物品入库登记</title>


<script type="text/javascript">


Ext.onReady(function (){
	
	
	mt_form_initDateSelect();
	
	
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
			   		mySubmit();
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
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
            	mySubmit();
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
	 
});
</script>

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:60%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
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
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn" ></div>
<div style="height: 92%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm" > 

<input type="hidden" id="uuid" name="uuid" value="${waresStock.uuid}" />

	<span class="formTitle" ><br>物品信息维护</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<tr>
			<td colspan="2" style="height: 15px;" class="data_tb_alignright"> 
				物品信息
			</td>
		</tr>
		<tr >
			<td align="right"  class="data_tb_alignright">名称<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content" width="80%">
				<input type="text" id="name" name="name" class="required" value="${waresStock.name}" >
				
				</td>
		      
		</tr>
		<!--  
		<tr >
			<td align="right"  class="data_tb_alignright">入库时间<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content" width="80%">
				<input type="text" id="putin_time" name="putin_time" ext_id="putin_time" ext_name="putin_time"  value="${waresStock.putin_time}" 
				ext_type=date  type="text"    
				/>
				
				</td>
		      
		</tr>
		-->
		<tr> 
			<td class="data_tb_alignright" align="right">描述：</td>
			<td class="data_tb_content">
				<textarea name="remark" id="remark" style="width: 250px;height: 80px;overflow: visible;">${waresStock.remark }</textarea> </td>
		</tr>
		
		<!-- 
		<tr>
			<td class="data_tb_alignright" align="right">用品类别<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input id="type" type="text" class="required"
				 name="type"  value="${waresStock.type}" title="请输入，不能为空！"
				 onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				multilevel=true
				norestorehint=true
				autoid=4584
				hideresult=true /></td>
		</tr>
		-->
	    <tr>  
			<td class="data_tb_alignright" align="right" >物品属性类型<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text"  class="required" name="pro_type"
				title="请输入，不能为空！"   value="${waresStock.pro_type}"  
				ext_id="pro_type"  id="pro_type"  ext_name="pro_type"  name="pro_type"  ext_type=singleSelect  type="text" valuemustexist=true      ext_select=700|物品属性类型  autoid="700"  refer="物品属性类型"
				 onselect="if(this.value=='领用库'){document.getElementById('spanNeedCheck').style.display='inline';}else{document.getElementById('spanNeedCheck').style.display='none';}"
				 />
				  
				</td>
		</tr>
	    <tr id="spanNeedCheck" style="display: none;">  
			<td class="data_tb_alignright" align="right" >需要审核<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input value="${waresStock.need_check_ind}" property=""  ext_id=need_check_ind  id="need_check_ind"  ext_name=need_check_ind  name="need_check_ind"  ext_type=singleSelect  type="text" valuemustexist=true      ext_select=700|truefalse  autoid="700"  refer="truefalse"  /></td>
		</tr>
		<tr>  
			<td class="data_tb_alignright" align="right" >编码<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="coding" class="required" name="coding"
				title="请输入，不能为空！" size="40"  value="${waresStock.coding}" /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >计量单位<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input id="unitUnit" type="text" class="required"
				  name="unitUnit"  value="${waresStock.unitUnit}" title="请输入，不能为空！" /></td>
		</tr>
		
	    <tr style="display: none;">
			<td class="data_tb_alignright" align="right" >单价<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input id="unit_price" type="text" 
				  name="unit_price"  value="${waresStock.unit_price}" title="请输入，不能为空！" /></td>
		</tr>
		
		<!--  
		<tr>
			<td class="data_tb_alignright" align="right" >最低库存<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input id="lowestStock" type="text" class="required	validate-digits"
				  name="lowestStock"  value="${waresStock.lowestStock}" title="请输入，不能为空！" /></td>
		</tr>
		-->
		<tr> 	 
			<td class="data_tb_alignright" align="right" >最低警戒库存<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="lowestWarnStock" class="required	validate-digits" name="lowestWarnStock"
				title="请输入，不能为空！"  value="${waresStock.lowestWarnStock}" /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >最高警戒库存<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input id="highestWarnStock" type="text" class="required validate-digits"
				maxlength="25" name="highestWarnStock"  value="${waresStock.highestWarnStock}" title="请输入，不能为空！" /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" align="right" >存放地点<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input id="local_code" type="text" class="required"
				maxlength="25" name="local_code"  value="${waresStock.local_code}" title="请输入，不能为空！" 
				ext_id="local_code"  id="local_code"  ext_name="local_code"  name="local_code"  ext_type=singleSelect  type="text" valuemustexist=true      ext_select=5010  autoid="5010"  
				/></td>
		</tr>
		<tr>  
			<td class="data_tb_alignright" align="right" >所属部门<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input type="text" id="departmentId" class="required" name="departmentId"
				title="请输入，不能为空！" size="40"  value="${waresStock.departmentId}"
				
				norestorehint=true
				autoid=30026
				hideresult=true
				 /></td>
		</tr>
	</table>
	
		<center><div id="sbtBtn" ></div></center>
		
 </form>
</div>
<br>
<script type="text/javascript">
new Validation('thisForm');

function mySubmit() {
	
	if (!formSubmitCheck('thisForm')) return ;

	if(document.getElementById("uuid").value!="") {
	
		document.thisForm.action="${pageContext.request.contextPath}/waresStock.do?method=update";
	} else {
	
		document.thisForm.action="${pageContext.request.contextPath}/waresStock.do?method=add";
	}
	document.thisForm.submit();
}

</script>

</body>
</html>
