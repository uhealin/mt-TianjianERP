<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>声明</title>
<script type="text/javascript">

Ext.onReady(function(){
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_${tableid}',
           items:[
           {
            text:'查询',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
            handler:function(){
            	queryWinFun();
			}
      	},'-', {
            text:'发送短信',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/mytask.gif',
            handler:function(){
            	sendMsg();
			}
      	}, '-',{
		    text:'打印',
		    id:'btn-print',
		    cls:'x-btn-text-icon',
		    icon:'${pageContext.request.contextPath}/img/print.gif',
		    handler:function(){
		    	print_${tableid}();
		    }
		},'-',
       {
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
	            }
	     }
        ]
        });  

});
var queryWin = null;
function sendMsg(){
	var searchDiv = document.getElementById("message") ;
	searchDiv.style.display = "" ; 
	    queryWin = new Ext.Window({
			title: '发送短信',
			contentEl:'message',
	     	renderTo : searchWin,
	     	width: 300,
	     	height:160,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          		 	var content = document.getElementById("content").value;
          			goMsg(content);
          			queryWin.hide();
            	}
        	},{ 
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
    new BlockDiv().show();
    queryWin.show();
}
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 
	    queryWin = new Ext.Window({
			title: '独立性查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 300,
	     	height:260,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			var departId = document.getElementById("depart").value;
          			if( departId !=""){
          				//部门名称
          				//var departname = document.getElementById("advice-depart").value;
	          			//document.getElementById("departName").value =departname.substring(departname.indexOf("-")+1,departname.length) ;
	          			//部门ID
	          			//alert(departId.substring(departId.indexOf("-")+1,departId.length));
          				document.getElementById("departName").value =departId.substring(departId.indexOf("-")+1,departId.length);
          			}else{
          				document.getElementById("departName").value = "";
          			}
          			goSearch_${tableid}();
          			queryWin.hide();
            	}
        	},{ 
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
    new BlockDiv().show();
    queryWin.show();
}
</script>


</head>
<body>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="${tableid}"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
	<tr>
      <td align="right">违规类型：</td>
      <td align=left>
        <input onclick="go();" type="checkbox" name="ctype" id="type0"  value='0' />年度独立性申明违规<br>
        <input onclick="go();" type="checkbox" name="ctype" id="type1"  value='1,2' />投资合规性违规<br>
        <input onclick="go();" type="checkbox" name="ctype" id="type2"  value='3' />5年轮换制提醒
        <input type="hidden" name="atype" id="atype" >
      </td>
   	</tr>
   	<tr >
      <td align="right" height="5px"></td>
      <td align=left height="5px">
      	
      </td>
	</tr>
	<tr >
      <td align="right">姓名：</td>
      <td align=left>
      	<input  type="text" name="name" id="name"   />
      </td>
	</tr>
	<tr>
       <td align="right">部门：</td>
       <td align=left> 
          <input type="text" name="department" id="depart"   onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" 
				autoid=958 noinput=true multilevel=true norestorehint=true  valuemustexist=true/> 
		<input type="hidden"" id="departName" name="departName">
		</td>
    </tr>
</table>
</div>
<div id="message" style="display:none;">
<br>
 &nbsp;&nbsp; 短信：<br>            
 &nbsp;&nbsp; <textarea  style="overflow: hidden;width: 250;height: 50" id="content"></textarea>
</div>
</form>
</body>
</html>
<script type="text/javascript">

function go(){
	var go = document.getElementsByName("ctype");
	var str = "";
	for(var i=0;i<go.length;i++){
		if(go[i].checked){
			str += "," + go[i].value;
		}
	}
	if(str != "") str = str.substring(1);
	document.getElementById("atype").value = str;	
}

function goMsg(content){
  var userIds = getChooseValue("${tableid}");
  if(userIds !="" && content!=""){	
  
	if(confirm("您确定要发送短信吗？","yes")){
		Ext.Ajax.request({
				method:'POST',
				params : { 
					userIds :userIds,
					conter:content,
					rand :Math.random()
				},
				url:"${pageContext.request.contextPath}/user.do?method=userMessager",
				success:function (response,options) {
					  var request = response.responseText;
				 	  alert(request);
				 	 goSearch_${tableid}();
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
	}	
	}else{
			alert("请先选择人员和写入短信内容");
			return;
	}
}


</script>
