<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">

	fieldset {margin: 10px;}
	.tTable {margin-top:10px;border:#d7e2f3 1px solid;border-collapse:collapse;}
	.tTable td,th {
		padding: 5 5 5 1px;text-align: left;white-space:nowrap;border-top:#d7e2f3 1px solid;border-left: #d7e2f3 1px solid;height:30px;
	}
	.tTable th{background-color: #f8f9f9;}
	.tTable td{background-color: #f8f9f9;}
	.tTable input {border:1px solid #d7e2f3;}
	
	.a_href {
		blr:expression(this.onFocus=this.blur());
	}
	
	.data_tb_content {
		text-align: center;
	}
	
	.bottom_border {
		color: red;
	}
	
</style>
 
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	        items:[
	        	{ 
			       text:'返回',
			       icon:'${pageContext.request.contextPath}/img/back.gif', 
			       handler:function(){
						f_back();
				   }
			    }
	        	/*
	        	,'-',
	     	    { 
		           text:'保存',
		           icon:'${pageContext.request.contextPath}/img/save.gif' ,
		           handler:function(){
		        	   if (!formSubmitCheck('thisForm')) return;
						  f_save();
				   }
	     	    }*/
	     	    ,'->'
	  	 	]
	});
	
});
</script>
</head>
<body>
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-27);width:100%;overflow: auto;">
<form id="thisForm" name="thisForm" method="post" action="" style="background-color: #ecf2f2;border: 1px solid #AEC9D3;height: 100%">

	<fieldset>
		<legend>会议耗材信息</legend>
					
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" >
			<tr>
				<td style="text-align: center" width="25%">
					登记人：${userName}
				</td>
				
				<td width="25%">
				</td>
				
				<td  width="25%">
				</td>
				
				<td style="text-align: left;" width="25%">
					登记时间：${nowDate}
				</td>
			</tr>
		</table>
		
		<div style="height: 400px;overflow: auto;"> 
			<table cellpadding="1" align="center" cellspacing="1" width="100%" class="tTable" id="tab">
			
				<tr>
					<th style="text-align: center;background-color: #ecf2f2;white-space:nowrap" colspan="4">
						会议名称<span class="mustSpan">[*]</span>：
						
					
								<input type="text"
									   name="meetingOrderId"
									   id="meetingOrderId"
									   maxlength="80"
									   value="${meetingOrderId}"
									   title="必填"
									   class="required"
									   autoWidth="210"
				      				   onkeydown="onKeyDownEvent();" 
				      				   onkeyup="onKeyUpEvent();" 
				      				   onclick="onPopDivClick(this);" 
				      				   autoid=10024
									   size="50" readonly="readonly" /> &nbsp;&nbsp;&nbsp;&nbsp;
					
							
						
						<a href="#" class="a_href" onclick="addRow()">【 新增耗材 】</a>
					</th>
					
				</tr>
				<tr>
					<td style="text-align: center;background-color: #ecf2f2;width:50%;" >
						耗材名称
					</td>
					<td style="text-align: center;background-color: #ecf2f2;width:10%;" >
						耗材数量
					</td>
					<td style="text-align: center;background-color: #ecf2f2;width:20%;" >
						耗材金额
					</td>
					
				</tr>
				
				<c:forEach var="list" items="${lists}" varStatus="vl">
					<tr>
						<td style="text-align: center;width:50%;" >
							<input type="text" id='names"${vl.count }"' name="names" value="${list.names }" class="data_tb_content" style="width:80%;text-align: center;" readonly="readonly"/>
						</td>
						<td style="text-align: center;width:10%;" >
							<input type="text" id='counts"${vl.count }"' name="counts" value="${list.counts }" class="data_tb_content" style="width:80%;text-align: center;" readonly="readonly">
						</td>
						<td style="text-align: center;width:20%;" >
							<input type="text" id='moneys"${vl.count }"' name="moneys" value="${list.moneys }" class="data_tb_content" style="width:80%;text-align: center;" readonly="readonly">
						</td>
						
					</tr>
				</c:forEach>
			</table>
		</div>
	</fieldset>

<input type="hidden" id="opt" name="opt" value="${opt}">
<input type="hidden" id="pageOpt" name="pageOpt" value="${pageOpt}">
<input type="hidden" id="batchNumber" name="batchNumber" value="${batchNumber}">
<input type="hidden" id="tnum" name="tnum" value="0" >
</form>

</div>
</body>

<script type="text/javascript">
	
	new Validation("thisForm");
	
	
	// 添加行
	function addRow(){
		   var table = document.getElementById("tab");
	       //添加一行
	       var newTr = table.insertRow();
	
	       //添加两列
	       var newTd1 = newTr.insertCell();
	       newTd1.className="data_tb_content";
	       var newTd2 = newTr.insertCell();
	       newTd2.className="data_tb_content";
	       var newTd3 = newTr.insertCell();
	       newTd3.className="data_tb_content";
	       var newTd4 = newTr.insertCell();
	       newTd4.className="data_tb_content";
	       
	       var t = document.getElementById("tnum").value;
	       t = t*1+1;
	       
	       //设置列内容和属性
	       newTd1.innerHTML = "<div style='text-align: center;' ><input type='text' id='names"+t+"' name='names' style='width:80%;text-align: center;'></div>"; 
	       newTd2.innerHTML = "<div style='text-align: center;' ><input type='text' id='counts"+t+"' name='counts' style='width:80%;text-align: center;' onkeyup='f_money(this)' ></div>";
	       newTd3.innerHTML = "<div style='text-align: center;' ><input type='text' id='moneys"+t+"' name='moneys' style='width:80%;text-align: center;' onkeyup='f_money(this)' ></div> ";
	       newTd4.innerHTML = "<div style='text-align: center;' ><a href='#' class='a_href' onclick='f_remove(this)'>【 删 除 】</a></div>";
	       
	       document.getElementById("tnum").value=t;
	}
	
	
	// 删除
	function f_remove(t){
		if(confirm("您确定要删除吗?")){
			t.parentNode.parentNode.parentNode.removeNode(true);
		}
	}
	
	// 返回
	function f_back(){
		var pageOpt = document.getElementById("pageOpt").value;
		if(pageOpt!="listPass"){
			document.thisForm.action = "${pageContext.request.contextPath}/meetingConsumable.do?method=list";
			document.thisForm.submit();
		}else{
			document.thisForm.action = "${pageContext.request.contextPath}/meetingOrder.do?method=goPass";
			document.thisForm.submit();
		}
	}

	// 保存
	function f_save(){
		var opt = document.getElementById("opt").value;
		// 去除 空
		if(f_removeNull()){
			
			var names = document.getElementsByName("names");
			if(names.length<1){
				alert("请添加耗材信息！");
				return;
			};
			
			// 判断 耗材名称是否 重复
			if(f_repeatName()){
				if(opt=="add"){
					// 判断 是否要 替换 已经 登记过的 会议的 耗材
					var id = document.getElementById("meetingOrderId").value;
					var rs = f_alreadyRegist(id);
					if(rs.trim()!="" && rs.length!=0){
						if(confirm("对"+id+"会议已于"+rs+"进行过耗材登记，您是否要替换之前的登记的呢？")){
							// 如果 是 替换 就 删除 之前登记过的
							f_delAlreadyRegist(id);
						}
					}
				}
				
				// 防止网络慢 用户点击 多次保存
				showWaiting();
				document.thisForm.action = "${pageContext.request.contextPath}/meetingConsumable.do?method=save";
				document.thisForm.submit();
			}
		}
	}

	
	// 验证金额 
	function f_money(t){
	
		var value = t.value;
		
		if(/^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/.test(value)==false)
		{
			alert('请录入数字!');
			t.select();
		}
	}

	//删除 空行
	function f_removeNull(){
		var names = document.getElementsByName("names");
		var counts = document.getElementsByName("counts");
		var moneys = document.getElementsByName("moneys");
	
		for(var i=names.length-1;i>=0;i--){
			if(names[i].value.trim()==""){
				names[i].parentNode.parentNode.parentNode.removeNode(true);
			}else{
				if(counts[i].value.trim()==""){
					alert("请填写数量！");
					counts[i].focus();
					return false;
				}
				if(moneys[i].value.trim()==""){
					alert("请填写金额！");
					moneys[i].focus();
					return false;
				}
			}
		}
		return true;	
	}
	
	
	

	// 已经 登记过的 会议的 耗材
	function f_alreadyRegist(id) {
		var url = "${pageContext.request.contextPath}/meetingConsumable.do?method=alreadyRegist";
		var request = "&id=" + id ;
		var result = ajaxLoadPageSynch(url,request);
		
		return result;
	}
	
	// 删除之前登记的
	function f_delAlreadyRegist(id) {
		var url = "${pageContext.request.contextPath}/meetingConsumable.do?method=delAlreadyRegist";
		var request = "&id=" + id ;
		var result = ajaxLoadPageSynch(url,request);
		
		return result;
	}
	
	
	// 判断耗材名称是否重复
	function f_repeatName(){
		var names = document.getElementsByName("names");
		for(var i=0;i<names.length;i++){
			for(var j=0;j<names.length;j++){
				// 不合自己作比较
				if(i!=j){
					if(names[i].value==names[j].value){
						names[i].className="bottom_border";
						names[j].className="bottom_border";
						alert(names[i].value+"物品名称重复了！");
						return false;
					}
				}
			}
		}
		return true;
	}
</script>
</html>