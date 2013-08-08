<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>报名明细</title>
<script type="text/javascript">
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.tab);
			}
      	},'-',{
      		text:'批量评价',
           	cls:'x-btn-text-icon',
           	icon:'${pageContext.request.contextPath}/img/check.png',
          	handler:goEvaluate
			 
      	},
      	'-',{
      		text:'查询',
           	cls:'x-btn-text-icon',
           	icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:goSearch  
			 
      	},'-',{
           text:'打印',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/print.gif',
           handler:function(){
          		print_educationRegDetail();
				//window.open('glossary.do?method=print','','height=1000,width=2000,location=no;');
			}
        },'-',{
      		text:'发短信',
           	cls:'x-btn-text-icon',
           	icon:'${pageContext.request.contextPath}/img/mytask.gif',
          	handler:sendMassage 
			 
      	},'-',{
      		text:'发邮件',
           	cls:'x-btn-text-icon',
           	icon:'${pageContext.request.contextPath}/img/mytask.gif',
          	handler:function(){
          		
          		var ids = parseInt(getChooseValue("educationRegDetail"));
          		if(ids==""){
          			alter("请选择要要发送的对象！");
          			return;
          		}
          		parent.openTab("customerFollowId","发送邮件","interiorEmail.do?method=goSendEmailByIds&ids="+ids);
          	}
      	},'->'
        ]
        });

var queryWin = null;
function goSearch(){
	if(!queryWin) {
			var searchDiv = document.getElementById("search");
			searchDiv.style.display = "" ;
		    queryWin = new Ext.Window({
		     title: '培训班查询',
   			 contentEl:'search',
		     width: 455,
		     height:295,
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
		               	queryWin.hide();
		               	goSearch_educationRegDetail();
		            
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
var querySend = null;
function sendMassage(){
	if(getChooseValue("educationRegDetail")==""){
		alert("请选择要发送短信的人员！");
		return;
	}
	if(!queryWin) {
			var searchDiv = document.getElementById("send");
			searchDiv.style.display = "" ;
		    querySend = new Ext.Window({
		     title: '发送手机短信',
   			 contentEl:'send',
		     width: 455,
		     height:295,
		  	 //modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
					new BlockDiv().hidden();
					querySend.hide();	         	
		        	}}
		        },
		       layout:'fit',
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		               	querySend.hide();
		               	goSendMassage();
		            
		           }
		       },{
		           text:'取消',
		           handler:function(){
		               querySend.hide();
		           }
		       }]
		    });
	   }
	   new BlockDiv().show();
	   querySend.show();
}

});
</script>
</head>
<body>
<div id="divBtn"></div>
<div>
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div style="height:expression(document.body.clientHeight-30);overflow:auto;">
<mt:DataGridPrintByBean name="educationRegDetail"/>
</div>
</form>
</div>
<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">报名时间：</td>
			<td align="left"><input type="text" name="time" id="time"></td>
		</tr>
		<tr align="rigth">
			<td align="right">参与人：</td>
			<td align="left"><input type="text" name="name" id="name"></td>
		</tr>
		<tr>
      		<td align="right">性别：</td>
      		<td align=left><input name="sex" type="text" id="sex" onkeydown="onKeyDownEvent();" 
					onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true 
					autoid=228/></td>
     	</tr>
     	<tr align="rigth">
			<td align="right">所属部门：：</td>
			<td align="left"><input type="text" name="departname" id="departname"></td>
		</tr>
	</table>
</div>
<div id="send" style="display:none">
<br/><br/><br/>
<div style="margin-left: 20px;">书写信息内容：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;" ></div>
	<table align="center" width="90%" height="80%"  border=0>
	<tr>
		<td>
			<textarea id="conter" onpropertychange="showcount()" name="conter" style="width: 90%;height:120px;"></textarea>
    	<div style="text-align: right;">
	    	<br>已经输入：
	    	<span id="count" style="color: red;font-weight: bold;font-size: 20px;">0</span>个字符!
    	</div>
    	</td>
    </tr>
	</table>
</div>
</body>
<script type="text/javascript">
new Ext.form.DateField({			
	applyTo : 'time',
	width: 150,
	format: 'Y-m-d'	
});

function showcount(){
  var s = document.getElementById("conter").value;   //文本域
  document.getElementById("count").innerHTML=s.length;
  
 }
function goSendMassage(){
	var ids = getChooseValue("educationRegDetail");
	
	var conter = document.getElementById("conter").value;
	if(ids==""){
		alert("请选择要发送短信的人员！");
		return;
	}else{
	  if(confirm("您确定要发送短信吗？","yes")){
		  Ext.Ajax.request({
			method:'POST',
			params : { 
			userIds :ids,
			conter:conter,
			//mobilePhone:mobilePhone,
			rand :Math.random()
			},
			url:"${pageContext.request.contextPath}/user.do?method=userMessager",
			success:function (response,options) {
  			var request = response.responseText;
  			alert(request);
			},
     		failure:function (response,options) {
				alert("后台出现异常,获取文件信息失败!");
			}
		});
	  }
	}
}
//批量设置评价
/*
function goEvaluate(){
	var uuids = getChooseValue("educationRegDetail");
	if(uuids==""){
		alert("请选择要评价的人员!");
		return;
	}else{
		
	}
	
}
*/

function goEvaluate(){
	var uuids = getChooseValue("educationRegDetail");
    var educationId="${educationId}"
	if(uuids==""){
		alert("请选择要评价的人员!");
		return;
	}else{
		var data={a:"优秀",b:"良好",c:"合格",d:"及格"};
	    
	    var msg="";
	    for(var k in data){
	       msg+="<br/>"+k+":"+data[k];
	    }
	    
	    var btns={ok:"确认",cancel:"取消"};
	 
	    Ext.MessageBox.prompt("评价培训人员","评价为(请输入对应英文代码 )"+msg
	     ,function(e,text){ 
	        if(e=="cancel")return false;
	        if(!data[text]){
	          alert("请输入有效的代码!");return false;
	        }
	        var url="education.do";
	        var param={method:"doEvaluate",uuids:uuids,
	        state:data[text],educationId:educationId};
	        $.post(url,param,function(str){alert(str);window.location.reload();});
	     }
	    
	    );
	}
    
}
</script>
</html>










