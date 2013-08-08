<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>物品登记</title>
<script type="text/javascript">


Ext.onReady(function (){
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
<form name="thisForm" method="post" action="" id="thisForm" > 

<input type="hidden" id="waresStockId" name="waresStockId" value="${waresStock.uuid}" />
<input type="hidden" id="ctype" name="ctype" value="${ctype}" />
<input type="hidden" id="paramSkip" name="paramSkip" value="${paramSkip}" />

	<span class="formTitle" ><br>物品登记信息</span>
	<table border="0"  style="line-height: 28px"   class="data_tb" align="center">
		<tr>
			<td colspan="2" style="height: 15px;" class="data_tb_alignright"> 
				物品登记信息
			</td>
		</tr>
		<c:if test="${ctype == '入库'}">
		  <tr >
			<td align="right"  class="data_tb_alignright"> 采购单号：</td>
			<td class="data_tb_content" width="80%">
				<input type="text" id="pruc_id" name="pruc_id" noinput="true" 
				class="required" autoid="5016|${waresStock.uuid}|未采购" refer="${waresStock.uuid}" 
				refer1="未采购"  onchange="pruc_onchange(this)" ></td>
             
		  </tr>
		 <tr >
			<td align="right"  class="data_tb_alignright"> 采购数量：</td>
			<td class="data_tb_content" >
			   <input id="real_quantity" readonly="readonly"/>
	         </td>
		  </tr>
		  
		 <tr >
			<td align="right"  class="data_tb_alignright"> 可入库数量：</td>
			<td class="data_tb_content" >
			     <input id="allow_putin_quantity" readonly="readonly"/>
		     </td>
		  </tr>
		</c:if>
		<tr >
			<td align="right"  class="data_tb_alignright">名称：</td>
			<td class="data_tb_content" width="80%">
				<input type="text" id="name" name="name" class="required" value="${waresStock.name}" disabled="disabled" ></td>
		</tr>
		
		<tr> 
			<td class="data_tb_alignright" align="right">描述：</td>
			<td class="data_tb_content">
				<textarea name="remark" id="remark" style="width: 250px;height: 80px;overflow: visible;" disabled="disabled" >${waresStock.remark }</textarea> </td>
		</tr>
		
		<!--  
				<tr>
			<td class="data_tb_alignright" align="right">用品类别：</td>
			<td class="data_tb_content"><input id="type" type="text" class="required"
				 name="type"  value="${waresStock.type}" title="请输入，不能为空！" disabled="disabled"
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
			<td class="data_tb_alignright" align="right" >数量<font color="red" size=3>[*]</font>：</td>
			<td class="data_tb_content"><input id="quantity" type="text" class="validate-digits"
				  name="quantity" title="请输入，不能为空！" /></td>
		</tr>
		<c:if test="${ctype == '入库'}">
			<tr> 	 
				<td class="data_tb_alignright" align="right" >单价<font color="red" size=3>[*]</font>：</td>
				<td class="data_tb_content"><input type="text" id="price" class="validate-digits" name="price"
					title="请输入，不能为空！"   /></td>
			</tr>
	
			<tr>
				<td class="data_tb_alignright" align="right" >供应商：</td>
				<td class="data_tb_content"><input id="suppliers" type="text"
					name="suppliers"   title="请输入，不能为空！"  size="30"/></td>
			</tr>
		</c:if>
		
		<c:if test="${ctype == '归还'}">
			<tr> 	 
				<td class="data_tb_alignright" align="right" >归还人<font color="red" size=3>[*]</font>：</td>
				<td class="data_tb_content">
					<input type="text" id="userId" class="required" name="userId"
						onfocus="onPopDivClick(this);"
						onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);"
						
						norestorehint=true
						autoid=10016
						hideresult=true
					title="请输入，不能为空！"   /></td>
			</tr>
		</c:if>
		
		<c:if test="${ctype == '报废'}">
			<tr> 	 
				<td class="data_tb_alignright" align="right" >报废申请人<font color="red" size=3>[*]</font>：</td>
				<td class="data_tb_content">
					<input type="text" id="userId" class="required" name="userId"
						onfocus="onPopDivClick(this);"
						onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);"
						multilevel=true
						norestorehint=true
						autoid=11
						hideresult=true
					title="请输入，不能为空！"   value="${userSession.userName }"  readonly="readonly" /></td>
			</tr>
			
			<tr> 	 
				<td class="data_tb_alignright" align="right" >报废日期<font color="red" size=3>[*]</font>：</td>
				<td class="data_tb_content">
				 <input name="scrap_time" id="scrap_time" / >
				</td>
			</tr>
			
			<tr> 	 
				<td class="data_tb_alignright" align="right" >报废申请信息<font color="red" size=3>[*]</font>：</td>
				<td class="data_tb_content">
				 <textarea rows="3" cols="60" name="scrap_msg"></textarea>
				</td>
			</tr>
			
			<script type="text/javascript">
			   Ext.onReady(function(){
				   new Ext.form.DateField({
		        		applyTo : 'scrap_time',
		        		width: 133,
		        		format: 'Y-m-d'
		        	});	

			   });
			</script>
		</c:if>
	</table>
	
		<center><div id="sbtBtn" ></div></center>
		
 </form>


<script type="text/javascript">
new Validation('thisForm');

function mySubmit() {
	
	if (!formSubmitCheck('thisForm')) return ;
	
	var url="${pageContext.request.contextPath}/waresStock.do";
	if("入库"=="${ctype}")
	{
		var pruc_id=$("#pruc_id").val();
		var quantity=$("#quantity").val();
		$.post(url,{uuid:pruc_id,quantity:quantity,method:"doCheckPurcQuantity"},function(text){
			if(text=="0"){
				document.thisForm.action=url+"?method=stockRegister";
				document.thisForm.submit();
			}else{
				alert(text);
			}
		});
	}else{
	document.thisForm.action=url+"?method=stockRegister";
	document.thisForm.submit();
	}
}

function pruc_onchange(obj){
	var url="waresStock.do";
	
	var param={method:"getPruc",uuid:obj.value};
	
	$.getJSON(url,param,function(json){
		//alert(json["expect_quantity"]);
		$("#real_quantity").val(json["real_quantity"]);
		$("#allow_putin_quantity").val(json["expect_quantity"]-json["real_quantity"]);
	});
}

</script>

</body>
</html>
