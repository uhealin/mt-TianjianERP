<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>考试报名列表</title>
<script type="text/javascript">

var queryWinTake = null;
var tabs = "";
Ext.onReady(function(){
	var queryWinTake =null;
var tabs="";
Ext.onReady(function(){
	/*
	 tabs = new Ext.TabPanel({
		    renderTo: 'my-tab',
		    activeTab: 0,
		    layoutOnTabChange:true, 
		    forceLayout : true,
		    deferredRender:false,
		    height: document.body.clientHeight-Ext.get('my-tab').getTop(),
		    width : document.body.clientWidth, 
		    defaults: {autoWidth:true,autoHeight:true},
		    items:[
		        {contentEl:'tab1', title:'未报名', id:'cur1'},
		        {contentEl:'tab2', title:'已报名', id:'cur2'}
		    ]
		});
		
		tabs.on("tabchange",function(tabpanel,tab){
	    	if(tab.id == "cur2"){
				goSearch_alExamRegList();			
			}
	    });

		goSearch_examRegList();
	*/
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'报名',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
            handler:function(){
            	//goSearch(); //查询
            	goEdit();
			}
      	},'-',{
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.tab);
			}
      	}
        ]
        });  
        var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn1',
           items:[{text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.tab);
			}}]
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
})
});
</script>
</head>
<body>
<!-- 
<div id="my-tab">

	<div id="tab1">
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="examRegList" />
		</div>
	</div> 
	<div id="tab2">
		<div id="divBtn1"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="alExamRegList" />
		</div>
	</div> 
</div>
 -->
<div id="divBtn"></div>
<form name="thisForm" method="post" action="">
	<div id="weibaoming">
	 	<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
		<div style="height:expression(document.body.clientHeight-25);overflow:auto;">
			<mt:DataGridPrintByBean name="examRegList"/>
		</div>
	</div>

<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">考试类型：</td>
			<td align=left><input  type="text" name="examType" id="examType" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" autoid=3052/></td>
		</tr>
		<tr>
			<td align="right">考试科目：</td>
			<td align=left><input type="text" name="examSubject" id="examSubject"/></td>
		</tr>
		<tr>	
			<td align="right">资格要求：</td>
			<td align=left><input type="text" name="qualifications" id="qualifications"/></td>
		</tr>
	</table>
</div>
<div>

</form>
</div>
</body>
<script type="text/javascript">
function goEdit(){
	if(document.getElementById("chooseValue_examRegList").value=="")
	{
		alert("请选择要报名的培训班！");
	}
	else
	{
		var url="${pageContext.request.contextPath}/education.do?method=isRegExam";
		var requestString = "&examRegListId="+document.getElementById("chooseValue_examRegList").value;
		var request= ajaxLoadPageSynch(url,requestString);
		if(request=="success"){
			window.location="education.do?method=addExam&&act=reg&&id="+document.getElementById("chooseValue_examRegList").value;
		}else{
			alert("你已经报名了");
		}
	}
}
</script>
</html>