<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">
	fieldset {margin: 10px;}
	.tTable {margin-top:10px;border:#d7e2f3 1px solid;border-collapse:collapse;}
	.tTable td,th {
		padding: 5 5 5 10px;text-align: left;white-space:nowrap;border-top:#d7e2f3 1px solid;border-left: #d7e2f3 1px solid;height:30px;
	}
	.tTable th{background-color: #f8f9f9;}
	.tTable input {border:0px;border-bottom:1px solid #aaa;}
</style>
 
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
	        	   if (!formSubmitCheck('thisForm')){
	        	   		return;
	        	   }else{
				   		f_save();
				   }
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				f_back();
			}
	  	},'->']
	});
	 
	new Ext.form.DateField({
		applyTo : 'stockInDate',
		width: 100,
		format: 'Y-m-d'
	});
	
	new Ext.form.DateField({
		applyTo : 'stockOutDate',
		width: 100,
		format: 'Y-m-d'
	});
});
</script>
</head>
<body>

<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-27);width:100%;overflow: auto;">
<form id="thisForm" name="thisForm" method="post" action="" >

	<fieldset>
		<legend>投资情况信息</legend>
		<table cellpadding="1" align="center" cellspacing="1" width="80%" height="100%" class="tTable">
			<tr>
				<td style="text-align: right;background-color: #f8f9f9">操作人姓名：</td>
				<td align="left">
					<input type="text" name="loginName" id="loginName" size="30" value="${userSession.userName}" readonly="readonly" />  
				</td>
				<td style="text-align: right;background-color: #f8f9f9">操作时间：</td>
				<td align="left">
					<input type="text" name="setTime" id="setTime" size="30" value="${nowTime}" readonly="readonly" />  
				</td>
			</tr>
			
			<tr>	
				<td style="text-align: right;background-color: #f8f9f9" >投资人姓名<font color="red">[*]</font>：</td>
				<td align="left">
					<input type="text" name="userName" id="userName" size="30" maxlength="20" title="必填" class="required" />  
				</td>
				<td style="text-align: right;background-color: #f8f9f9">投资人与本人关系<font color="red">[*]</font>：</td>
				<td align="left">
					<input type="text" name="relations" id="relations"
						   size="30"  maxlength="30" class="required" title="必填"
						   onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" 
						   onfocus="onPopDivClick(this);" autoid=866 refer="investRelations" />
				</td>
			</tr>
				
			<tr>
				<td style="text-align: right;background-color: #f8f9f9">是否有进行股票买卖：</td>
				<td style="text-align: left" colspan="3">
					<input type="radio" 
						   name="answer" 
						   id="answerY" style="border: 0px;" onclick="f_display('Y')" checked="checked" value="是" />是
					<input type="radio" 
						   name="answer" 
						   id="answerN"
						   size="30" 
						   style="border: 0px;" onclick="f_display('N')" value="否" />否 
				</td>
			</tr>
			
			<tr id="trStockNum">
				<td style="text-align: right;background-color: #f8f9f9">深A股票帐号：</td>
				<td>
					<input type="text"
					   name="ssstockNum"
					   id="ssstockNum"
					   size="30"
					   value="${im.ssstockNum}" maxlength="20" />
				</td>
				<td style="text-align: right;background-color: #f8f9f9">深B股票帐号：</td>
				<td>
					<input type="text"
					   name="ssstockNum2"
					   id="ssstockNum2"
					   size="30"
					   value="${im.ssstockNum2}" maxlength="20" />
				</td>
			</tr>
			<tr id="hsstockNumTr">
				<td style="text-align: right;background-color: #f8f9f9" >沪A股票帐号：</td>
				<td>
					<input type="text" 
					   name="hsstockNum" 
					   id="hsstockNum"
					   size="30" 
					   value="${im.hsstockNum}" maxlength="20" />
				</td>
				<td style="text-align: right;background-color: #f8f9f9">沪B股票帐号：</td>
				<td>
					<input type="text" 
					   name="hsstockNum2" 
					   id="hsstockNum2"
					   size="30" 
					   value="${im.hsstockNum2}" maxlength="20" />
				</td>
			</tr>
			
			<tr id="trStockNum2">
				<td style="text-align: right;background-color: #f8f9f9">港市股票帐号：</td>
				<td colspan="3">
					<input type="text"
					   name="gsstockNum"
					   id="gsstockNum"
					   size="30"
					   value="${im.gsstockNum}" maxlength="20" />
				</td>
			</tr>
			
			<tr id="statement">
				<td colspan="4" style="text-align: center;background-color: #f8f9f9;">近期投资情况<a href="#" style="margin-left: 30px; " onclick="f_addRow()">【追加】</a></td>
			</tr>
			<tr id="trstatement">
				<td colspan="4" style="text-align: center;background-color: #f8f9f9;height: 100%">
					<table cellpadding="1" align="center" cellspacing="1" width="95%"  class="tTable" id="statementTab">
						<tr>
							<th style="text-align:  center;width: 115px;">股票代码</th>
							<th style="text-align:  center;width: 185px;">股票名称</th>
					<!-- 		<th style="text-align:  center;width: 85px;">股数</th>  -->
							<th style="text-align:  center;width: 105px;">首次买入日期</th>
							<th style="text-align:  center;width: 105px;">最后卖出日期</th>
							<th style="text-align:  center;width: 115px;">操作</th>
						</tr>
						<tr>
							<td style="text-align:  center">
								<input type="text" 
								   name="stockCode" 
								   id="stockCode"
								   value=""
								   style="width: 100%;text-align: center;" />
							</td>
							<td style="text-align:  center">
								<input type="text" 
								   name="stockName" 
								   id="stockName"
								   value=""
								   style="width: 100%;text-align: center;" />
							</td>
							<!-- 
							<td style="text-align:  center">
								<input type="text" 
								   name="stockCount" 
								   id="stockCount"
								   value=""
								   style="width: 100%;text-align: center;" onkeyup="f_count(this);" />
							</td>
							 -->
							<td style="text-align:  center">
								<input type="text" 
								   name="stockInDate" 
								   id="stockInDate"
								   value=""
								   style="width: 100%;text-align: center;" />
							</td>
							<td style="text-align:  center">
								<input type="text" 
								   name="stockOutDate" 
								   id="stockOutDate" 
								   style="width: 100%;text-align: center;" />
							</td>
							<td style="text-align:  center">
								<span style="text-align: center;"><a href="#" onclick="f_remove(this)">【删&nbsp;除】</a>&nbsp;&nbsp;<a href="#" onclick="f_view(this)">【查&nbsp;看】</a> </span>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</fieldset>
<input type="hidden" id="rowNum" name="rowNum" value="0">
</form>

</div>

</body>

<script type="text/javascript">
	
// 显示隐藏
function f_display(p){
	if(p=="Y"){
		// 保存按钮
		document.getElementById("saveBtn").style.display = "";
		
		document.getElementById("trStockNum").style.display = "";
		document.getElementById("trStockNum2").style.display = "";
		document.getElementById("statement").style.display = "";
		document.getElementById("trstatement").style.display = "";
		document.getElementById("hsstockNumTr").style.display = "";
		
	}else{
		// 保存按钮
		//document.getElementById("saveBtn").style.display = "none";
		
		document.getElementById("trStockNum").style.display = "none";
		document.getElementById("trStockNum2").style.display = "none";
		document.getElementById("statement").style.display = "none";
		document.getElementById("trstatement").style.display = "none";
		document.getElementById("hsstockNumTr").style.display = "none";
	}

}



// 添加行
function f_addRow(){
	   var table = document.getElementById("statementTab");
       //添加一行
       var newTr = table.insertRow();

       //添加两列
       var newTd1 = newTr.insertCell();
       var newTd2 = newTr.insertCell();
    //   var newTd3 = newTr.insertCell();
       var newTd4 = newTr.insertCell();
       var newTd5 = newTr.insertCell();
       var newTd6 = newTr.insertCell();
       
       var rowNum = document.getElementById("rowNum").value;
       rowNum = rowNum*1+1;
       
       //设置列内容和属性
       newTd1.innerHTML = "<input type='text' id='stockCode"+rowNum+"' name='stockCode' style='width:100%;text-align: center;'>"; 
       newTd2.innerHTML = "<input type='text' id='stockName"+rowNum+"' name='stockName' style='width:100%;text-align: center;'>";
     //  newTd3.innerHTML = "<input type='text' id='stockCount"+rowNum+"' name='stockCount' style='width:100%;text-align: center;' onkeyup='f_count(this)' > ";
       newTd4.innerHTML = "<input type='text' id='stockInDate"+rowNum+"' name='stockInDate' style='width:100%;text-align: center;' >";
       newTd5.innerHTML = "<input type='text' id='stockOutDate"+rowNum+"' name='stockOutDate' style='width:100%;text-align: center;' >";
       newTd6.innerHTML = "<span style='width:100%;text-align: center;'><a href='#' onclick='f_remove(this)'>【删&nbsp;除】</a>&nbsp;&nbsp;<a href='#' onclick='f_view(this)'>【查&nbsp;看】</a> </span> ";
       
       f_createTime('stockInDate'+rowNum);
       f_createTime('stockOutDate'+rowNum);
       
       document.getElementById("rowNum").value=rowNum;
}


// 删除
function f_remove(t){
	if(confirm("您确定要删除吗?")){
		t.parentNode.parentNode.parentNode.removeNode(true);
	}
}

// 保存
function f_save(){
	var userName = document.getElementById("userName").value;
	var relations = document.getElementById("relations").value;
	
	if(userName=="" || null==userName){
		alert("投资人姓名不能为空！");
		return;
	}else if(relations=="" || null==relations){
		alert("投资人与本人关系不能为空！");
		return;
	}else{
		// 删除股票代码为空的行
		var stockCodes = document.getElementsByName("stockCode");
		for(var i=0;i<stockCodes.length;i++){
			if(stockCodes[i].value=="" || stockCodes[i].value==null){
				stockCodes[i].parentNode.parentNode.parentNode.removeNode(true);
			}
		}
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/investManage.do?method=save&paramSave=add";
		document.getElementById("thisForm").submit();
	}
	
}

// 返回
function f_back(){
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/investManage.do?method=list";
	document.getElementById("thisForm").submit();
}

// 查看
function f_view(t){
	// 获取 股票编号
	var stockCode = t.parentNode.parentNode.parentNode.cells[0].childNodes[0].value;
	if(stockCode=="" || stockCode==null){
		alert("请录入股票编号再进行对比查看！");
	}else{
		
		var url = "${pageContext.request.contextPath}/investManage.do";
		var query_String = "method=viewStockCode&stockCode="+stockCode;
		var resText2 = ajaxLoadPageSynch(url,query_String);
	
		if(resText2=="N"){
			t.parentNode.parentNode.parentNode.cells[0].childNodes[0].select();
			alert("该股票编号在禁止投资名单中,请尽快处理好！");
		}else{
			alert("该股票编号正常,请放心使用！");
		}
	}
}

// 数量
function f_count(t){
	t.value=t.value.replace(/[^\d\.\\-]/g,'');
}


// 创建时间
function f_createTime(t){
	new Ext.form.DateField({
		applyTo : t,
		width: 100,
		format: 'Y-m-d'
	});
}


</script>
</html>