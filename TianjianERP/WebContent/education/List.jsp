<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>培训班管理</title>
<script type="text/javascript">
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'增加',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	},'-',{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				goEdit();
			}
        },'-',{
           text:'删除',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/delete.gif',
          	handler:function(){
				goDelete();
			}
        },
        /**'-',{
           text:'打印',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/print.gif',
          	handler:function(){
          		print_education();
				//window.open('glossary.do?method=print','','height=1000,width=2000,location=no;');
			}
        },
        */'-',{
           text:'查询',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:goSearch  
			 
		},'-',{
           text:'查看报名情况',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
          	handler:goSearchState 
			 
		},'-',{
	           text:'评价标准维护',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/edit.gif',
	          	handler:function (){
	        	   if(!document.getElementById("chooseValue_education").value=="")
	        		{
	        			var url = "evaluate.do?method=list&educationId="+document.getElementById("chooseValue_education").value;
	       				var pageId = new Date().getTime();
	       				parent.parent.openTab(pageId,"评价标准维护",url);
	        			
	        		}else{
	        			alert("请选择一个培训班后再设置评价标准！");
		        	}
      			
	          		 
	          	}  
				                             
		},'-',{
	           text:'查看评价结果',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/query.gif',
	           handler:function (){
	        	   var  id = document.getElementById("chooseValue_education").value;
	        	   if(id=="")
	        		{
	        			alert("请选择要查看的数据！");
	        			return ;
	        		}
       				var url = "evaluate.do?method=assessResultList&id="+id;
       				var pageId = new Date().getTime();
       				parent.parent.openTab(pageId,"评价结果",url);
	          		 
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
		               	goSearch_education();
		            
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

});	        	
</script>
</head>
<body>
<div id="divBtn"></div>
<div>
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div style="height:expression(document.body.clientHeight-28);overflow:auto;">
<mt:DataGridPrintByBean name="education"/>
</div>
</form>
</div>
<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">培训班：</td>
			<td align=left><input  type="text" name="name" id="name"/></td>
			</tr>
			<tr>
			<td align="right">讲师：</td>
			<td align=left><input type="text" name="teacherId" id="teacherId" onkeydown="onKeyDownEvent();" 
					onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true 
					autoid=2057/></td>
			</tr>
			<tr>	
			<td align="right">培训开始日期：</td>
			<td align=left>
				<input type="text" name="trainStartTime" id="trainStartTime"/>
			</td>
			</tr>
			<tr>	
			<td align="right">培训结束日期：</td>
			<td align=left>
				<input type="text" name="trainEndTime" id="trainEndTime"/>
			</td>
			</tr>
			<tr>
			<td align="right">报名开始日期：</td>
			<td align=left>
				<input type="text" name="registrationStartTime" id="registrationStartTime"/>
			</td>
			</tr>
			<tr>
			<td align="right">报名结束日期：</td>
			<td align=left>
				<input type="text" name="registrationEndTime" id="registrationEndTime"/>
			</td>
			</tr>
     		<tr>
      		<td align="right">培训地点：</td>
      		<td align=left><input name="address" type="text" id="address"/></td>
     		</tr>	
	</table>
</div>
</body>
<script type="text/javascript">
new Ext.form.DateField({			
	applyTo : 'trainStartTime',
	width: 150,
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'trainEndTime',
	width: 150,
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'registrationStartTime',
	width: 150,
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'registrationEndTime',
	width: 150,
	format: 'Y-m-d'	
});
function goAdd()
{
	window.location="${pageContext.request.contextPath}/education.do?method=add&&act=add";
}

function goEdit()
{
	if(document.getElementById("chooseValue_education").value=="")
	{
		alert("请选择要修改的数据！");
	}
	else
	{
		window.location="${pageContext.request.contextPath}/education.do?method=add&&act=edit&&id="+document.getElementById("chooseValue_education").value;
	}
}


function goDelete()
{
	if(document.getElementById("chooseValue_education").value=="")
	{
		alert("请选择要删除的数据！");
	}
	else
	{
		
		 var url="${pageContext.request.contextPath}/education.do?method=getState";
		 var requestString = "&id="+document.getElementById("chooseValue_education").value;
		 var request= ajaxLoadPageSynch(url,requestString);
		 if(request == "结束"){
			 alert("已结束的培训班不能删除");
			 return ;
		 }
		if(confirm("确定删除此数据？")){
			window.location="${pageContext.request.contextPath}/education.do?method=del&&id="+document.getElementById("chooseValue_education").value;
		}
	}
}
//查看报名情况
function goSearchState(){
	
	if(document.getElementById("chooseValue_education").value=="")
	{
		alert("请选择要查看的数据！");
	}
	else
	{

		var url = "education.do?method=regDetail&&id="+document.getElementById("chooseValue_education").value;
		var pageId = new Date().getTime();
		
		
		
		parent.parent.openTab(pageId,"报名情况",url);
	}
}

 
//双击
function grid_dblclick(obj) {
	window.location="${pageContext.request.contextPath}/education.do?method=add&&act=edit&&id="+obj.educationId;
}
</script>
</html>