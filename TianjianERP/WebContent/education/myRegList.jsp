<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>我参加过的培训班</title>
<script type="text/javascript">
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'培训评价',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
            handler:function(){
            	var id = document.getElementById("chooseValue_myRegList").value;
            	if(id =="")
            	{
            		alert("请选择要进行评价的数据！");
            		return ;
            	}
            	//openPage(id);
          }
         } ,'-', {
             text:'关闭',
             cls:'x-btn-text-icon',
             icon:'${pageContext.request.contextPath}/img/close.gif',
             handler:function(){
             	closeTab(parent.parent.tab);
 			}
       	},'->', {
            text:'刷新',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/switch2.gif',
            handler:function(){
            	goSearch_myRegList();
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
</form>
</div>

<div id="list_course" style="display:none;font: 14" align="left">
</div>
<div id="search" style="display:none">
<br/> 
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
<div style="height:expression(document.body.clientHeight-27);overflow: auto;">
<mt:DataGridPrintByBean name="myRegList"/>
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
	window.location="education.do?method=add&&act=add";
}

function showview(uuid,linkcount){
	if(linkcount=="0"){
		alert("该培训班没有在线课件");
		return;
	}
    var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var url="course.do?method=getVideoId&uuid="+uuid;
	
	oBao.open("POST", url, false);
	oBao.send();
	var strResult = unescape(oBao.responseText);
	if(strResult=='no'){
	   alert("没有在线课件");
	}else{
		var array = strResult.split(",");
	    if(array.length==1){
	       var url = "course.do?method=view&id="+array[0].split(":")[0];
	       var tabName = array[0].split(":")[1];
	       if(tabName ==""){
	    	   tabName = "在线课程";
	       }else{
	    	   tabName = tabName.length>2?tabName.substring(0,10)+"...":tabName;
	       }
	       try{
		       parent.parent.openTab("view",tabName,url);
	       }catch(e){
		       parent.openTab("view",tabName,url);
	       }
	   }else{
	        var showcourse = "";
	        for(var i=0;i<array.length;i++){
	           var tmp = array[i].split(":");
	           var tabName = tmp[1];
	           showcourse += "课件名：<a href='javascript:void(0)' onclick=\"myOpenTab(\'"+tmp[0]+"\',\'"+tabName+"\')\">"+tmp[1]+"</a>  <br>"; 
	        } 
	        document.getElementById("list_course").innerHTML=showcourse;
	   		mycourse();
	   		
	   }
   } 
}
var queryWin1 = null;
function myOpenTab(courseId,tabName){
	 if(tabName ==""){
  	   tabName = "在线课程";
     }
	 queryWin1.hide();	  
     var url = "course.do?method=view&id="+courseId;
     try{
	     parent.parent.openTab(courseId,tabName,url);
     }catch(e){
    	 parent.openTab(courseId,tabName,url);
     }
      
}

function goEdit()
{
	if(document.getElementById("chooseValue_education").value=="")
	{
		alert("请选择要修改的数据！");
	}
	else
	{
		window.location="education.do?method=add&&act=edit&&id="+document.getElementById("chooseValue_education").value;
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
		if(confirm("确定删除此数据？")){
			window.location="education.do?method=del&&id="+document.getElementById("chooseValue_education").value;
		}
	}
}
function goSearchState(){
	if(document.getElementById("chooseValue_education").value=="")
	{
		alert("请选择要查看的数据！");
	}
	else
	{
		window.location="education.do?method=regDetail&&id="+document.getElementById("chooseValue_education").value;
	}
}

function mycourse(){
		var searchDiv = document.getElementById("list_course");
		searchDiv.style.display = "" ;
	     queryWin1 = new Ext.Window({
		     title: '在线课件',
	 		 contentEl:'list_course',
		     width: 350,
		     height:220,
		     closeAction:'hide',
		     listeners: {
		        'hide':{fn: function () {
				 new BlockDiv().hidden();
				 queryWin1.hide();	         	
		        	}}
		        },
		       layout:'fit'
	    });
	   new BlockDiv().show();
	   queryWin1.show();
}
        
  function grid_dblclick(obj, tableId) {
	var id = obj.educationId;
	if(id ==""){
		return ;
	}
	openPage(id);
}
function openPage(id){
//	  var url = "evaluate.do?method=evaluate&id="+id+"&flag=reg&rand="+Math.random();
//	  parent.parent.openTab("evaluateId","我的评价",url);
      window.location="evaluate.do?method=evaluate&id="+id+"&flag=reg&rand="+Math.random();
}	
</script>
</html>