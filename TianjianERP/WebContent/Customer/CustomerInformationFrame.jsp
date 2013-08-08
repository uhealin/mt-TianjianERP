<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>客户详细信息管理</title>
</head>

<%
	String customer = request.getParameter("customer");
%>

<script type="text/javascript">

function ext_init(){

	var root=new Ext.tree.TreeNode({
	   id:'0',
	   text:'所有客户'
	});
	
	var root1=new Ext.tree.TreeNode({
	   id:'1',
	   text:'详细信息'
	});
	root.appendChild(root1);
		
	root1=new Ext.tree.TreeNode({
	   id:'2',
	   text:'股东管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'3',
	   text:'联系人管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'4',
	   text:'关联公司管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'5',
	   text:'相关项目管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'6',
	   text:'客户商业文档管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'7',
	   text:'客户接洽记录管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'8',
	   text:'市场机会管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'9',
	   text:'客户评级管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'10',
	   text:'中介机构信息维护'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'11',
	   text:'专家顾问库维护'
	});
	
	root1=new Ext.tree.TreeNode({
	   id:'12',
	   text:'客户服务记录'
	});
	root.appendChild(root1);
	
		
	var tree=new Ext.tree.TreePanel({
		animate:true, 
		root:root,
		rootVisible:false,
	    autoScroll:true,
	    containerScroll: true,
	    border: true,
	    height:document.body.clientHeight - 38,
	    region:'west'
	});
	
	
	tree.on('click',function(node,event){
		switch(Math.abs(node.id)){
			case 1:
				Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=del&&act=update&&chooseValue=<%=customer%>&frameTree=1";//详细信息
	     		break;
	   		case 2:
	   			Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/stockholder.do?method=edit&departid=<%=customer%>&frameTree=1";//股东管理
	     		break;
			case 3:
				Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/manager1.do?departid=<%=customer%>&frameTree=1";//高管简历管理
	     		break;
	     	case 4:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/connectcompanys.do?acts=update&&chooseCustomer=<%=customer%>&frameTree=1";//关联公司管理
	     		break;	
	     	case 5:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/AuditProject.do?method=getCustomerProjects&customerid=<%=customer%>&frameTree=1";//相关项目管理
	     		break;	
	     	case 6:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/attach.do?opt=2&pid=0&cid=<%=customer%>&frameTree=1";//审计对象管理
	     		break;		
	     	case 7:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customerConsult.do?customerid=<%=customer%>&frameTree=1";//客户接洽记录管理
	     		break;	
	     	case 8:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=business&customerid=<%=customer%>&frameTree=1";//市场机会管理
	     		break;		
	     	case 9:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=levelHistory&customerid=<%=customer%>&frameTree=1";//客户评级管理
	     		break;	
	     	case 10:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/agency.do?departid=<%=customer%>&frameTree=1";//中介机构信息维护
	     		break;
	     	case 11:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/adviser.do?departid=<%=customer%>&frameTree=1";//专家顾问库维护
	     		break;	
	     	case 12:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customerNote.do?departid=<%=customer%>&frameTree=1";//客户服务记录
	     		break;	
	     	default :
	     		break;	
		}
	
	});
	
	var hd = new Ext.Toolbar({
   		height:30,
   		region:'north',
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
                 <c:choose>
                 <c:when test="${param.agent=='agent'}">
				    window.location="${pageContext.request.contextPath}/agent.do?method=birthToDepart";
				 </c:when>
				 <c:otherwise>
				   window.location="${pageContext.request.contextPath}/customer.do?flag=${param.flag}";
				 </c:otherwise>
               </c:choose>

			}
      	}]
	});

    var left = new Ext.Panel({
    	id:'leftPanel',
    	region:'west',
        containerScroll: true, 
        split:true,
        collapsible: true,
        margins:'0 0 5 5',
        cmargins:'0 0 0 0',
        lines:false,
        collapseMode:'mini',
        hideCollapseTool : true,
        width: 200,
        autoScroll:true,//自动出现滚动条
		items:[
			tree
		]
	});

	var center = new Ext.Panel({
		layout:'fit',
		region:'center',
		border:true,
		margins:'0 0 0 5',
		html:'<iframe name="taskFrame" id="taskFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/customer.do?method=del&&act=update&&chooseValue=<%=customer%>&frameTree=1"></iframe>'
	});
	
	var layout = new Ext.Viewport({
		layout:'border',
		items:[
			hd,
			left,
			center
		]
	});
	
	layout.doLayout();
}
/*
String.prototype.Trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
String.prototype.Ltrim = function(){return this.replace(/(^\s*)/g, "");}
String.prototype.Rtrim = function(){return this.replace(/(\s*$)/g, "");}
*/
window.attachEvent('onload',ext_init);
</script>  
<!-- 
  <frameset rows="50,*" framespacing="1" frameborder="NO" border="0">
   <frame  src="CustomerInformationHead.jsp" noresize="noresize" >
    <frameset name="mainFrame"  cols="180,*" framespacing="1" frameborder="NO" border="0">
     <frame src="CustomerChangeTree.jsp?customer=<%=customer%>" name="treeFrame" noresize="noresize" scrolling="no" >
     <frame src="CustomerInformationBody.jsp?customer=<%=customer%>" name="bodyFrame" scrolling="yes">
    </frameset>
  </frameset>
-->
<body>
<div id="divBtn" ></div>  
<div id="west"></div>
</body>  
</html>

<script>
//--------------------------
// 获得加密狗信息
//--------------------------
	function getDogInfo() {
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		xmlHttp.open("POST","${pageContext.request.contextPath}/info.do?method=dog&random=" + Math.random(),false);
		xmlHttp.send();
		var strResult = unescape(xmlHttp.responseText);
	
		if(strResult.indexOf('铭太科技内部专用')>-1) {
			document.getElementById("customerConsult").style.display = "";//客户接洽记录管理
			document.getElementById("customerLatency").style.display = "";//客户潜在项目管理
			document.getElementById("customerLevel").style.display = "";//客户评级管理
		}
	}
	
	try{
		//getDogInfo();
	}catch(e){
	
	}
	
</script>
