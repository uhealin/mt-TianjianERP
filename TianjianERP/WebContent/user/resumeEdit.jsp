<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>简历信息</title>
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
		
    }
    window.attachEvent('onload',ext_init);

</script>
</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-30);overflow: auto;" >

<input name="table" type="hidden" id="table" value="${table}">
<input name="unid" type="hidden" id="unid" value="${edit.unid }">

<span class="formTitle" >简历信息<br/><br/> </span>
	<br>
	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
	<tr>
	  <td class="data_tb_alignright" align="center" colspan="4">个人简历信息</td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">系统自动编号：</td>
	  <td  class="data_tb_content" colspan="3" >
	  <input class="before" readonly  value='${edit.resumeid }' name="resumeid" type="text" id="resumeid"  >
	  <font color="red">生成规则按照“日期-4位顺序号”格式统一编号</font>
	  </td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">人力资源建议岗位：</td>
	  <td  class="data_tb_content" width="35%"><input class="required" value='${edit.jobname }' name="jobname" type="text" id="jobname" multiselect="true" autoid=741 onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true ></td>
	  <td class="data_tb_alignright"  width="15%" align="right">状态：</td>
	  <td  class="data_tb_content" width="35%">
			<select id='state' name="state">
			<option value = '退回' <c:if test="${edit.state == '退回'}">selected</c:if> >退回</option>
			<option value = '候选' <c:if test="${edit.state == '候选'}">selected</c:if> >候选</option>
			<option value = '入选' <c:if test="${edit.state == '入选'}">selected</c:if> >入选</option>
			<option value = '通知到达' <c:if test="${edit.state == '通知到达'}">selected</c:if> >通知到达</option>
			<option value = '通知未达' <c:if test="${edit.state == '通知未达'}">selected</c:if> >通知未达</option>
			<option value = '初试通过' <c:if test="${edit.state == '初试通过'}">selected</c:if> >初试通过</option>
			<option value = '复式通过' <c:if test="${edit.state == '复式通过'}">selected</c:if> >复式通过</option>
			<option value = '录取' <c:if test="${edit.state == '录取'}">selected</c:if> >录取</option>
			<option value = '被拒' <c:if test="${edit.state == '被拒'}">selected</c:if> >被拒</option>
			</select>
	  </td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">证件类型：</td>
	  <td  class="data_tb_content" ><input class="required" value='${edit.paperstype }' name="paperstype" type="text" id="paperstype"  autoid=700 refer='证件类型' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150 ></td>
	  <td class="data_tb_alignright"  width="15%" align="right">证件编号：</td>
	  <td  class="data_tb_content" ><input class="validate-positiveInt" validchar="0123456789" onkeypress="return blockChar(this)" onpaste="return false" value='${edit.papersnumber }' name="papersnumber" type="text" id="papersnumber"  ></td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">姓名：</td>
	  <td  class="data_tb_content"  ><input class="required" value='${edit.name }' name="name" type="text" id="name"  ></td>
	  <td class="data_tb_alignright"  width="15%" align="right">执业资质：</td>
	  <td  class="data_tb_content"  ><input value='${edit.cpano }' name="cpano" type="text" id="cpano" multiselect="true" autoid=700 refer='考试项目' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150 ></td>
	</tr>
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">性别：</td>
	  <td  class="data_tb_content" >
	  	<input name="sex" type="radio" value="男" <c:if test="${edit.sex == '男'}">checked</c:if> >男 
	  	<input name="sex" type="radio" value="女" <c:if test="${edit.sex == '女'}">checked</c:if> >女
	  </td>
	  <td class="data_tb_alignright"  width="15%" align="right">出生年月：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.borndate }' class="validate-date-cn" showcalendar="true" name="borndate" type="text" id="borndate" ></td>	  
	</tr>		
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">学历：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.educational }' name="educational" type="text" id="educational"></td>
	  <td class="data_tb_alignright"  width="15%" align="right">毕业院校及专业：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.diploma }' name="diploma" type="text" id="diploma" ></td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">手机：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.mobilephone }' validchar="0123456789" onkeypress="return blockChar(this)" onpaste="return false" class="validate-phonenumber" name="mobilephone" type="text" id="mobilephone" ></td>  
	  <td class="data_tb_alignright"  width="15%" align="right">邮箱：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.email }' name="email" type="text" id="email" ></td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">工作年限：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.workyears }' class="validate-positiveInt" validchar="0123456789" onkeypress="return blockChar(this)" onpaste="return false" name="workyears" type="text" id="workyears"></td>
	  <td class="data_tb_alignright"  width="15%" align="right">薪资要求：</td>
	  <td  class="data_tb_content" width="35%"><input value='${edit.payrequest }' validchar="0123456789" onkeypress="return blockChar(this)" onpaste="return false" class="validate-positiveInt" name="payrequest" type="text" id="payrequest" ></td>
	</tr>	
	<tr> 
	  <td class="data_tb_alignright"  width="15%" align="right">工作简历：</td>
	  <td  class="data_tb_content" colspan="3"><textarea name="specialty" id="specialty" cols="100" rows="10" maxlength="500"  onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);">${edit.specialty}</textarea></td>
	</tr>
	<tr> 
	  <td class="data_tb_alignright"  width="15%" align="right">附件：</td>
	  <td  class="data_tb_content" colspan="3">
	  	<input value='${edit.attachid }' name="attachid" type="hidden" id="attachid" >
	  	<script>
			attachInit('k_resume','${edit.attachid}');					
		</script>
	  </td>
	</tr>	
	<tr>
	  <td class="data_tb_alignright"  width="15%" align="right">最后修改人：</td>
	  <td  class="data_tb_content" width="35%"><input class="before" readonly value='${edit.lastuser }' name="lastuser" type="text" id="lastuser" ></td>
	  <td class="data_tb_alignright"  width="15%" align="right">最后修改时间：</td>
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