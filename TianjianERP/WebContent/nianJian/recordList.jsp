<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>    

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>人员列表</title>
<script>
function ext_init(){
	new Ext.Toolbar({
		renderTo:'GridDiv_recordNianJianList',
		items:[{ 
				text:'查询',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function(){
					queryWinFun();
				}
			},'-',{
				id:'email',
				text:'发邮件',
	           	cls:'x-btn-text-icon',
	           	icon:'${pageContext.request.contextPath}/img/mytask.gif',
				handler:function(){
					sendEmail();
				}
			},'-',{
				id:'message',
	      		text:'发短信',
	           	cls:'x-btn-text-icon',
	           	icon:'${pageContext.request.contextPath}/img/mytask.gif',
	          	handler:function(){
	          		sendMessage();
	          	}
				 
	      	},'-',{ 
				text:'证书申领及证明模板打印',
				icon:'${pageContext.request.contextPath}/img/edit.gif' ,
				handler:function(){
					to_print();
				}
			}
		]
	});
	var departmentId="${departmentId}"
	if(departmentId!="1247"){
		Ext.getCmp("message").disable();
		Ext.getCmp("email").disable();
	}else{
		Ext.getCmp("message").enable();
		Ext.getCmp("email").enable();
	}
}

var queryWin = null;
function queryWinFun(){
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '查询',
			width: 470,
			height:360,
			contentEl:'search',  
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			},
	        layout:'fit', 
	        contentEl:'search',
		    buttons:[{
	            text:'确定',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	        		goSearch_recordNianJianList(2);
	          		queryWin.hide();
	          	}
	        },{
	            text:'重置',
	            icon:'${pageContext.request.contextPath}/img/refresh.gif',
	            handler:function(){
	            	reset("thisForm");
	            }
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	queryWin.hide();
	            }
	        }]
	    });
	}

	queryWin.show();
}
var querySend = null;
function sendMessage(){
	if(getChooseValue("recordNianJianList")==""){
		alert("请选择要发送短信的人员！");
		return;
	}
	if(!queryWin) {
			var searchDiv = document.getElementById("send");
			searchDiv.style.display = "" ;
		    querySend = new Ext.Window({
		     title: '发送短信',
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
Ext.onReady(ext_init);
</script>
</head>
<body>
<div class="autoHeightDiv">
	<mt:DataGridPrintByBean name="recordNianJianList" />
</div>

<div id="search" style="display: none;">
	<form name="thisForm" id="thisForm" method="post">
		<br/>
		<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
		<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
		
		<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center">
			<tr>
				<td align="right">姓名：</td>
				<td align=left>
					<input type="text" name="name" id="name" size="30"/>
				</td>
			</tr>
			<tr>
				<td align="right">所属部门：</td>
				<td align=left>
					<input type="text" name="departmentId" id="departmentId"  autoid=30026 size="21"/>
				</td>
			</tr>
			<tr>
				<td align="right">登录名：</td>
				<td align=left>
					<input type="text" name="loginId" id="loginId"   size="21"/>
				</td>
			</tr>
			<tr>
				<td align="right">性别：</td>
				<td align=left>
					<input type="text" name="sex" id="sex"  autoid=10001 refer="用户性别" size="21"/>
				</td>
			</tr>
			
			<tr>
				<td align="right">主要项目清单生成模式：</td>
				<td align=left>
					<input type="text" name="reportType" id="reportType"  size="30"/>
				</td>
			</tr>
			<tr>
				<td align="right">注册会计师：</td>
				<td align=left>
					<input type="text" name="ryzhushi" id="ryzhushi"  autoid=10001 refer="是否" size="21"/>
				</td>
			</tr>
			<tr>
				<td align="right">注册税务师：</td>
				<td align=left>
					<input type="text" name="taxRegister" id="taxRegister"  autoid=10001 refer="是否" size="21"/>
				</td>
			</tr>
		</table>		
	</form>		
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
//发送短信
function goSendMassage(){
	var ids = getChooseValue("recordNianJianList");
	
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
			rand :Math.random()
			},
			url:"${pageContext.request.contextPath}/nianJian.do?method=sendMessage",
			success:function (response,options) {
  			var result = response.responseText;
  			alert(result);
			},
     		failure:function (response,options) {
				alert("后台出现异常,获取文件信息失败!");
			}
		});
	  }
	}
}
//计算字符长度
function showcount(){
	  var s = document.getElementById("conter").value;   //文本域
	  document.getElementById("count").innerHTML=s.length;
	  
	 }
//发送邮件
function sendEmail(){
	var ids=getChooseValue("recordNianJianList");
	if(ids==""){
		alert("请选择要发送的人员");
		return;
	}
		parent.openTab("nianRecordId","发送邮件","interiorEmail.do?method=goSendEmailByIds&ids="+ids);
}
/**
 * 打印
 */
function to_print(formId){
	/*
    var srcFormId = "";
    if(!formId) {
        formId = document.getElementById("formId").value;
    } else {
        srcFormId = document.getElementById("formId").value;
    }
    
 */
    var value= document.getElementById("chooseValue_recordNianJianList").value;
    
    if(value == ""){
        
        value = getChooseValue("recordNianJianList");
                
        if(value == "") {
            alert("请选择要打印的数据!");
            return;
        
        } else if(value.indexOf(",") > -1) {
            alert("请选择一条需要打印的数据!");
            return;
        }
    }
      templatePrint("nianJian.do?method=proveModel&random="+Math.random()+"&uuid="+value);
}

function templatePrint(url){

	var tab = parent.parent.mainTab ;

     //先用URL提交； 
     if(tab){
          var random =  Math.random();
          var n = tab.add({    
          title:"打印",    
          closable:true,  //通过html载入目标页
          id:random ,
          html:'<iframe id="frame' + random + '" name="frame' + random + '" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
          }); 

          tab.setActiveTab(n);
		
	}else {
		
		window.open(url);
	}


	/*
	不再是下载EXCEL模式，而是新开标签页模式
	try{
		
		//设置程序
		var loader;
		try {
			loader = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				loader = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e2) {
				loader = false;
			}
		}
	
		//访问后台得到提示
		loader.open("POST",url,true);
		loader.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		loader.onreadystatechange = function(){
			if (loader.readyState==4) {
				eval(loader.responseText);
			}
		}
		loader.send();

	}catch(e){
		//出错了，就是老模式
		window.open(url);
	}
	
	*/
}


</script>
</html>