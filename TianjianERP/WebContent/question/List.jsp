<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>问题库</title>

<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_question',
           items:[{
            text:'新增',
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
            handler:function () {
            	goDelete();
            }
        },'-',{
            text:'查看',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
            handler:function () {
            	goRead();
            }
        },'-',{
            text:'打印',
            id:'btn-print',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function(){
            	print_question();
            }
        },'-',{
            text:'分类管理',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/flow.gif',
            handler:function () {
            	goPType();
            }
        },'-',{
            text:'全文检索',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
            handler:function () {
            	searchForm();
            }
        }
        ]
        });  

});

</script>

</head>
<body>
<form name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-30);" >
<mt:DataGridPrintByBean name="question"  />
</div>	
</form>
<form action="${pageContext.request.contextPath}/questionsearch/Search.jsp" method="post" id="searchForm" name="searchForm" target="searchQuestion">
	<input type="hidden" name="searchArea" id="searchArea">
	<input type="hidden" name="ifOne" id="ifOne" value="one">
</form>
<Script>
function searchForm(){
	//var question = document.getElementById("question").value;
	//if(question == ""){
	//	alert("请输入查询条件!");
	//	return ;
	//}
	//document.getElementById("searchArea").value=question;
	
	//parent.parent.openTab('searchQuestion','全文检索','questionsearch/Search.jsp');
	//document.getElementById("searchForm").submit();
	openTabQu("全文检索","${pageContext.request.contextPath}/questionsearch/Search.jsp");
	
	
}
function openTabQu(name,url) {
	 //其他中心
   var n = parent.parent.tab.getComponent(id); 
 
   if (!n) { //判断是否已经打开该面板    
        n = parent.parent.tab.add({    
           'id':'searchQuestion',      
           'title':name,  
            closable:true,  //通过html载入目标页    
            html:'<iframe id="searchQuestion" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'   
        }).show();
        //document.getElementById("searchQuestion").src = url;
        //document.getElementById("searchForm").submit();
    }    
   parent.parent.tab.setActiveTab(n);    
    $('.children').stop(true,true).hide();
}
function goPType(){
	window.location="${pageContext.request.contextPath}/questiontype/List.jsp?pid=${param.pid}";
}

function goAdd(){
	if("${param.pid}"=="null")
	{
		alert("不能在此新增问题");
		return false;
	}
	window.location="${pageContext.request.contextPath}/question/AddAndEdit.jsp?tid=${param.pid}";
}
function goDelete(){
	if(document.getElementById("chooseValue_question").value==""){
		alert("请选择要删除的问题！");
	}else{
		//alert(document.thisForm.chooseValue.value);
		var str="您的操作会永远删除该记录，您将无法还原该记录，是否继续？";
		if(confirm(str,"提示")){
			Ext.Ajax.request({
			method:'POST',
			params:{unid : id},
			url:'${pageContext.request.contextPath}/question.do?method=del&chooseValue='+document.getElementById("chooseValue_question").value,
			success:function (response,options) {
				//alert(response.responseText);	
				var result = response.responseText;
				if(result.indexOf("OK")>-1){
					alert("删除问题或案例成功！");
					goSearch_question();
				}
			},
			failure:function (response,options) {
				alert("后台出现异常,获取文件信息失败!");
			}
		});		
		}
		
	}
}
function goEdit(){
	if(document.getElementById("chooseValue_question").value==""){
		alert("请选择要修改的问题！");
	}else{
		//alert(document.thisForm.chooseValue.value);
		window.location="${pageContext.request.contextPath}/question/AddAndEdit.jsp?chooseValue="+document.getElementById("chooseValue_question").value;
	}
}

function goRead()
{
	if(document.getElementById("chooseValue_question").value==""){
		alert("请选择要查看的问题！");
	}else{
		//alert(document.thisForm.chooseValue.value);
		window.location="${pageContext.request.contextPath}/question/View.jsp?chooseValue="+document.getElementById("chooseValue_question").value;
	}
}
function subClearSearch()
{
	window.location="List.jsp";
}
</script>
