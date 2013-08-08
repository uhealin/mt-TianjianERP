<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>人员详细信息管理</title>
</head>

<%
	String myUserid = request.getParameter("myUserid");
	session.setAttribute("myUserid", myUserid);
%>

<script type="text/javascript">



function ext_init(){

	var root = new Ext.tree.TreeNode({
		   id:'0',
		   text:'所有用户'
	});
	
	var xmlHttpTree = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttpTree.open("POST","${pageContext.request.contextPath}/user.do?method=ajaxUpdateDetailsTree&random=" + Math.random(),false);
	xmlHttpTree.setRequestHeader("charset", "utf-8");
	xmlHttpTree.send();
	var strResult = unescape(xmlHttpTree.responseText);
	var jsonObj = eval("("+strResult+")");
	try{
		
		for(var i = 0; jsonObj.length;i++){
			var jsonId = jsonObj[i].id;
			var jsonText = jsonObj[i].text;
			var jsonUrl = jsonObj[i].url;
			//var rootJson = "root"+jsonId;
			var rootJson=new Ext.tree.TreeNode({
				   id:jsonId, 
				   text:jsonText,
				   url:jsonUrl
				});
			root.appendChild(rootJson);
		}	
	}catch(e){
	}
	
	 /*
	var root1=new Ext.tree.TreeNode({
	   id:'1',
	   text:'详细信息'
	});
	root.appendChild(root1);
		
	root1=new Ext.tree.TreeNode({
	   id:'2',
	   text:'家庭成员登记'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'3',
	   text:'专业资格登记'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'4',
	   text:'工作记录登记'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'5',
	   text:'工作岗位变动记录'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'6',
	   text:'员工证件登记'
	});
	root.appendChild(root1);
	*/
	/*
	root1=new Ext.tree.TreeNode({
	   id:'7',
	   text:'人事档案缴费管理'
	});
	root.appendChild(root1);
	*/
	/*
	root1=new Ext.tree.TreeNode({
	   id:'8',
	   text:'项目经历'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'9',
	   text:'劳动合同信息'
	});
	root.appendChild(root1);

	
	root1=new Ext.tree.TreeNode({
	   id:'10',
	 //  text:'保险信息'
	   text:'社会职务'
	});
	root.appendChild(root1);

	
	root1=new Ext.tree.TreeNode({
	   id:'11',
	   text:'员工培训情况管理'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'12',
	   text:'员工奖惩记录管理'
	});
	root.appendChild(root1);*/
	 
/*	
	root1=new Ext.tree.TreeNode({
	   id:'13',
	   text:'人员考核管理'
	});
	root.appendChild(root1);		
*/	
	
//	root1=new Ext.tree.TreeNode({
//	   id:'14',
//	   text:'相关扫描件上传'
//	});
//	root.appendChild(root1);
		
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
		var url = node.attributes.url;
		url = url.replace("[userId]","<%=myUserid%>");
		if(url.indexOf("www")>-1){
			if(url.indexOf("http")<1){
				url = "http:\/\/"+url;
			}
			Ext.get("taskFrame").dom.src = url;
		}else{
			Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/"+url;
		}
		
		/*
		switch(Math.abs(node.id)){
			case 1: //详细信息
				Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=2&id=<%=myUserid%>&myControl=myControl&act=no";
	     		break;
	   		case 2: //家庭成员登记
	   			Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/family.do"
	     		break;
			case 3: //专业资格登记
				Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/specialitycompetence.do"
	     		break;
	     	case 4: //工作记录登记
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/worknote.do"
	     		break;	
	     	case 5: //工作岗位变动记录
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/postchange.do"
	     		break;	
	     	case 6: //员工证件登记
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/employeecertificate.do"
	     		break;		
	     	case 7: //人事档案缴费管理
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/personCapture.do"
	     		break;	
	     	case 8: //项目经历
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/user.do?method=Item&id=<%=myUserid %>"
	     		break;		
	     	case 9: //劳动合同信息
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/laborbargain.do"
	     		break;	
	     	case 10: 
	     	   //Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/insurancetype.do"
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/worknote.do?method=societyList&userid=<%=myUserid %>"; //社会职务
	     		break;
	     	case 11: //员工培训情况管理
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/learncircs.do"
	     		//Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/worknote.do?method=societyList&userid=<%=myUserid %>"; //社会职务
	     		break;	
			case 12: //员工奖惩记录管理
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/encouragement.do"
	     		break;
			case 13: //人员考核管理
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/userLevel.do?method=levelHistory"
	     		break;
//	     	case 14://相关扫描件上传
//	     		Ext.get("taskFrame").dom.src ="${pageContext.request.contextPath}/scan.do?method=upload"
//	     		break;	       								
	     	default :
	     		break;	
		}*/
	
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
				    window.location="${pageContext.request.contextPath}/user.do?method=List&judge=${param.judge}";
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

	var iFrameUrl = "${pageContext.request.contextPath}/user.do?method=Edit&UserOpt=2&id=<%=myUserid%>&myControl=myControl&act=no";
	var center = new Ext.Panel({
		layout:'fit',
		region:'center',
		border:true,
		margins:'0 0 0 5'
		//html:'<iframe name="taskFrame" id="taskFrame" scrolling="yes" frameborder="0" width="100%" height="100%" src="'+iFrameUrl+'"></iframe>'
	});
	
	var mytab1 = new Ext.TabPanel({
        id: "tab1",
        region:'center',
        activeTab:0, //选中第一个 tab
        layoutOnTabChange:true, 
        forceLayout : true,
        deferredRender:false,
    //    height: document.body.clientHeight-Ext.get('divTab').getTop(),
   //     width : document.body.clientWidth, 
   //     defaults: {autoWidth:true,autoHeight:true},
        items:[
            {contentEl: "allList",title:"基础信息",id:"all",
            	html:'<iframe name="taskFrame" id="taskFrame" scrolling="yes" frameborder="0" width="100%" height="100%" src="'+iFrameUrl+'"></iframe>'
            }
       
        ]
       
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

<body>
<div id="divBtn" ></div>  
<div id="west"></div>
</body>  
<script type="text/javascript">
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
	
	function openUrl(url,name) {
		var tab = parent.tab ;
		if(tab && tab.id == "mainFrameTab"){
			try{
				n = parent.parent.tab.add({    
					'title':name,  
					 closable:true,  //通过html载入目标页    
					 html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
				});    
				parent.parent.tab.setActiveTab(n);
			}catch(e){
				window.location = url ;
			}
		}else {
			window.location = url ;
		}
		
	}
	
	try{
		
	}catch(e){
		aelrt(e);
	}
	
</script>
</html>

