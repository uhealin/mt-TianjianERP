<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>招人中的培训班</title>
<script type="text/javascript">
Ext.onReady(function(){
	var mytab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab:0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop(), 
       
        //autoHeitht:true,
        defaults: {autoHeight: true,autoWidth:true},
        items:[
            {contentEl: "kebaoming",title:"可报名",id:"kebaomingTab"},
            {title:"已报名",id:"yibaomingTab",html:
            	"<iframe src=\"${pageContext.request.contextPath}/education.do?method=myList\" style=\"width: 100%;height:550\" id=\"yibaom\" scrolling=\"no\"></iframe>"}
        ]
    });
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

});
</script>	        	
</head>
<body scroll="no">


<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">培训班</td>
			<td align=left><input  type="text" name="name" id="name"/></td>
			</tr>
			<tr>
			<td align="right">讲师</td>
			<td align=left><input type="text" name="teacherId" id="teacherId" onkeydown="onKeyDownEvent();" 
					onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true 
					autoid=2057/></td>
			</tr>
			<tr>	
			<td align="right">培训开始日期</td>
			<td align=left>
				<input type="text" name="trainStartTime" id="trainStartTime"/>
			</td>
			</tr>
			<tr>	
			<td align="right">培训结束日期</td>
			<td align=left>
				<input type="text" name="trainEndTime" id="trainEndTime"/>
			</td>
			</tr>
			<tr>
			<td align="right">报名开始日期</td>
			<td align=left>
				<input type="text" name="registrationStartTime" id="registrationStartTime"/>
			</td>
			</tr>
			<tr>
			<td align="right">报名结束日期</td>
			<td align=left>
				<input type="text" name="registrationEndTime" id="registrationEndTime"/>
			</td>
			</tr>
     		<tr>
      		<td align="right">培训地点</td>
      		<td align=left><input name="address" type="text" id="address"/></td>
     		</tr>	
	</table>
</div>
<div>
<form name="thisForm" method="post" action="">
<div id="divTab">
	<div id="kebaoming">
		<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
		<div id="divBtn"></div>
		<div style="height:expression(document.body.clientHeight-50);overflow: auto">
			<mt:DataGridPrintByBean name="educationRegDetail"/>
		</div>
		
	</div>
</div>
<!--  <div id="yibaoming" style="height:expression(document.body.clientHeight-27);"></div>-->
</form>
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

function goEdit()
{
	if(document.getElementById("chooseValue_educationRegDetail").value=="")
	{
		alert("请选择要报名的培训班！");
	}
	else
	{
		var url="${pageContext.request.contextPath}/education.do?method=isReg";
		var requestString = "&educationId="+document.getElementById("chooseValue_educationRegDetail").value;
		var request= ajaxLoadPageSynch(url,requestString);
		if(request=="success"){
			window.location="education.do?method=add&&act=reg&&id="+document.getElementById("chooseValue_educationRegDetail").value;
		}else{
			alert("你已经报名了");
		}
	}
}


</script>
</html>