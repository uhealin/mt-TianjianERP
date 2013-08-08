<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
var queryWin = null;
function goView(){
	if(!queryWin) {
			var searchDiv = document.getElementById("search");
			searchDiv.style.display = "" ;
		    queryWin = new Ext.Window({
		     title: '选择企业资质',
   			 contentEl:'search',
		     width: 400,
		     height:250,
		  	 //modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
					new BlockDiv().hidden();
					queryWin.hide();	         	
		        	}}
		        },
		       layout:'fit',
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		         		onSubmit();
		               	queryWin.hide();
		            
		           }
		       },{
		           text:'取消',
		           handler:function(){
		               queryWin.hide();
		           }
		       }]
		    });
	   }
	   new BlockDiv().show();
	   queryWin.show();
}
function ext_init(){ 
	
	var tbar = new Ext.Toolbar({
	renderTo: 'divBtn',
	items:[{
				text:'查询',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:goView 
			},'-',{
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function () {
           		closeTab(parent.tab);
            }
        },'->'
    ]
    });  
	
	

}
window.attachEvent('onload',ext_init);


</script>
</head>
<body>
<div id="divBtn" ></div>

<div id="search" style="display:none">
<br/><br/><br/>
<form name="addForm" method="post"  id="addForm" >
<div style="margin:0 20 0 20">请在下面选择企业资质：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td width="120" style="text-align: right"><br>标题<font color="red">[*]</font>：</td>
			 <td align="left" colspan="3"><br>
					<input type="text"  name="eqId"  id="eqId" title="必填" size="30"
					onfocus="onPopDivClick(this);"
					onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);" autoWidth=200
			 		autoid=4600   noinput=true hideresult=true
					class="required" />
			</tr>
	</table>
	<input type="hidden" id="uuid" name="uuid">
	<input type="hidden" id="opt" name="opt">
</form>
</div>
<form name="thisForm" method="post" id="thisForm" >
<div id="divCenten" style="display: none;">
	<table  border="0" style="line-height: 30px;">
			<tr>
				<td width="150" style="text-align: right;">标题：</td>
				<td align="left" colspan="3">
					 ${eqm.title}
				</td>
			</tr>
			
			<tr>
				<td width="150" style="text-align: right;"><div style="vertical-align:top;">附件：</div></td>
				<td align="left" colspan="3">
					<div style="vertical-align:bottom;">
						<script type="text/javascript">
							attachInit('enterpriseQualification','${eqm.attachFileId}');					
						</script>
					</div>
				</td>
			</tr>
		</table>
  </div>
</form>
</body>
<script type="text/javascript">
new Validation('addForm');
function onSubmit(){
	if (!formSubmitCheck('addForm')) return ;
	var eqId = document.getElementById("eqId").value;
	if(eqId == ""){
		alert("请选中企业资质!");
		return ;
	}
	document.getElementById("opt").value = "true";
	document.getElementById("uuid").value = document.getElementById("advice-eqId").value ;
	document.getElementById("addForm").action = "${pageContext.request.contextPath}/enterpriseQualification.do?method=addAccessory";
	document.getElementById("addForm").submit();	
}

if("${opt}" ==""){
	goView();
}else{
	document.getElementById("divCenten").style.display = "block";	
}
</script>
</html>