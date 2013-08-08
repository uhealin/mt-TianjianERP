<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
 
 <script type="text/javascript">
 
 var treeLoader, treePanel,viewport,treeRoot,winCondition;
 Ext.onReady(function(){
	    
     // NOTE: This is an example showing simple state management. During development,
     // it is generally best to disable state management as dynamically-generated ids
     // can change across page loads, leading to unpredictable results.  The developer
     // should ensure that stable state ids are set for stateful components in real apps.
     Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
     treeLoader=new Ext.tree.TreeLoader({
 		url:'${pageContext.request.contextPath}/query2d.do?method=tree'
 	});
     
     treePanel = new Ext.tree.TreePanel({
 	    animate:true, 
 	   autoScroll:true,
 	    containerScroll: true,
 	    loader:treeLoader,
 	    border: false,
 	    //layout:"fit",
 	    //autoHeight:true,
 	    
        height: document.body.clientHeight +55,
 	    rootVisible:false,
 	    dropConfig: {appendOnly:true}
 	    
 	}); 
 	
     treeLoader.on('beforeload',function(treeLoader,node){
 		this.baseParams.ntype = node.attributes.ntype
 		,this.baseParams.formid = node.attributes.formid
 		//this.baseParams.areaid = node.attributes.areaid,
 		//this.baseParams.departname = node.attributes.departname,
 		//this.baseParams.isSubject = node.attributes.isSubject
 	},treeLoader);

 	treePanel.on('click',function(node,event){
 		//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
 		//var divIframe = document.getElementById("divIframe"); 
 		var attr=node.attributes;
 		var formid=attr["formid"]||"";
 		if(attr["ntype"] == "table"){
 		   var url="query2d.do";
 		   $("#divTable").load(url,{method:"layoutTable",uuid:formid});
 		   
 		}else if(attr["ntype"]=="resultDisplay"){
 		   var url="query2d.do";
 	 	   $("#divTable").load(url,{method:"layoutTable",uuid:attr["tableid"]});
 	 	   $("#divResultDisplay").load(url,{method:"layoutResult",uuid:attr["uuid"]});
  	 	  
 		}
 		
 	});
 	
 	treeRoot=new Ext.tree.AsyncTreeNode({
 	   id:'0',
 	   text:'我的统计'
 	});
 	treePanel.setRootNode(treeRoot);

 	treePanel.render('divTree'); 
 	//tree.expandAll();
 	treeRoot.expand();
     
     viewport = new Ext.Viewport({
         layout: 'border',
         items: [
         // create instance immediately
          {
             region: 'west',
             id: 'west-panel', // see Ext.getCmp() below
             contentEl:"west",
             title: '统计分析',
             split: true,
             width: 200,
             minSize: 175,
             maxSize: 400,
             collapsible: true,
             margins: '0 0 0 5'
         },
         // in this instance the TabPanel is not wrapped by another panel
         // since no title is needed, this Panel is added directly
         // as a Container
         new Ext.Panel({
             region: 'center', // a center region is ALWAYS required for border layout
             deferredRender: false,
             contentEl: 'center',
             layout:"border",
             items:[
                 {
                	 region:"north",
                	 contentEl:"divTable",
                	 height:150 ,
                	 title:"统计设计",
                	 split:true
                 },
                 {
                	 //title:"统计结果",
                	 region:"center",
                	 contentEl:"divResult"
                 }
             ]
         })]
     });
     // get a reference to the HTML element with id "hideit" and add a click listener to it 
   
     
    new Ext.Toolbar({
		renderTo: "divTableToolbar",
 		items:[{
 			text:'新增设计',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				Ext.MessageBox.prompt("新增设计","请输入新设计名称",function(e,text){
					if(e=="ok"&&text!=""){
						var url="query2d.do";
						$.post(url,{caption:text,method:"doSaveTable"},function(str){alert(str);treeRoot.reload();});
					}
				});
			}
 		}]
    });
     
    new Ext.Toolbar({
		renderTo: "divResultToolbar",
 		items:[{
 			text:'重新计算',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				var url="query2d.do";
				var uuid=$('#tableid').val();
				if(uuid=="")return false;
				$("#divResultDisplay").load(url,{uuid:uuid,method:"doCal2d"});
	       }
 		},{
 			text:'保存结果',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				if(!$('#tableResult')){alert("请先计算结果");return;}
				Ext.MessageBox.prompt("保存结果","请输入保存结果名",function(e,text){
					if(e=="ok"&&text!=""){
						var url="query2d.do";
						var tableid=$('#tableid').val();
						var htmlstr=$('#tableResult').html();
						var caption=text;
						var param={tableid:tableid,htmlstr:htmlstr,caption:caption,method:"doSaveResult"};
					    $.post(url,param,function(str){alert(str);treeRoot.reload();});
					}
				});
	       }
 		},{
 			text:'导出',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
	       }
 		}
 		]
    });
     
 });
 
 function doDeleteRow(obj){
	 var uuid=obj.id;
	 var url="${pageContext.request.contextPath}/query2d.do";
	 var param={method:"doDeleteRow", uuid:uuid};
	 $.post(url,param,function(str){
		 alert(str);
		 reloadTable();
	 });
	 
 }
 
 function doDeleteCol(obj){
	 var uuid=obj.id;
	 var url="${pageContext.request.contextPath}/query2d.do";
	 var param={method:"doDeleteCol", uuid:uuid};
	 $.post(url,param,function(str){
		 alert(str);
		 reloadTable();
	 });
 }
 
 var center,treeConLoader,treeConPanel,treeConRoot,cr,selectCrUUid;
 function editCondition(obj,crr,conid){
	 cr=crr;
	 selectCrUUid=obj.id;
     if(conid){
		 
		 var url="query2d.do";
		 $("#divCondition").empty();
		 $("#divCondition").load(url,{method:"layoutCondition",uuid:conid}
		 ,function(){
	    	   if(cr=="row"){$("#btnSelectCon").html("选择设计到行");}
	    	   else if(cr=="col"){$("#btnSelectCon").html("选择设计到列");}
	    	   drawCondition();
	    	}
		 );
	 }else{
		 $("#divCondition").empty(); 
	 }
	 $("#divWinCondition").hide();
	 if(winCondition!=null) {winCondition.show(); return; }
	 
	  center=new Ext.Panel({
		 title:"查询条件",
		 layout:"fit",
		 contentEl:"divCondition",
		 region:"center"
	 });
	 
	 Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
      treeConLoader=new Ext.tree.TreeLoader({
 		url:'${pageContext.request.contextPath}/query2d.do?method=treeCondition'
 	});
     
      treeConPanel = new Ext.tree.TreePanel({
 	    animate:true, 
 	    region:"west",
 	    containerScroll: true,
 	    
 	    loader:treeConLoader,
 	    border: false,
 	    //autoHeight:true,
 	    width:250,
        height: 380,
        autoScroll:true,
 	    rootVisible:false,
 	    dropConfig: {appendOnly:true}
 	    
 	}); 
 	
     treeConLoader.on('beforeload',function(treeConLoader,node){
 		this.baseParams.ntype = node.attributes.ntype
 		,this.baseParams.formid = node.attributes.formid
 		//this.baseParams.areaid = node.attributes.areaid,
 		//this.baseParams.departname = node.attributes.departname,
 		//this.baseParams.isSubject = node.attributes.isSubject
 	},treeConLoader);

 	treeConPanel.on('click',function(node,event){
 		//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
 	    var attr=node.attributes;
 	    var ntype=attr["ntype"];
 	    var uuid=attr["uuid"];
 	    var formid=attr["formid"];
 	    if(ntype=="condition"){
 	    	//alert(ntype);
 	    	var url="query2d.do";
 	    	$("#divCondition").load(url,{uuid:uuid,method:"layoutCondition"}
 	    	,function(){
 	    	   if(cr=="row"){$("#btnSelectCon").html("选择设计到行");}
 	    	   else if(cr=="col"){$("#btnSelectCon").html("选择设计到列");}
 	    	  drawCondition();
 	    	}
 	    	);
 	    }else if(ntype=="addCondition"){
 	    	var ptext=node.parentNode.text;
 	    	Ext.MessageBox.prompt("新增 "+ptext+"查询","请输入设计名字",function(e,text){
 	    		if(e=="ok"&&text!=""){
 	    			var url="query2d.do";
 	    			var param={method:"doSaveCondition",caption:text,formid:formid};
 	    			$.post(url,param,function(str){alert(str);});
 	    			treeConRoot.reload();
 	    		}
 	    	});
 	    }
 		
 	});
 	
 	 treeConRoot=new Ext.tree.AsyncTreeNode({
 	   id:'0',
 	   text:'我的统计'
 	});
 	treeConPanel.setRootNode(treeConRoot);

 	//treeConPanel.render('divTree'); 
 	//tree.expandAll();
 	treeConRoot.expand();
	 
	 winCondition=new Ext.Window({
		 title:"候选条件",
		 width:1000,
	     height:400,
	     layout:"border",
	     closeAction:'hide',
	     autoScroll:true,
	     modal:true,
	     listeners:{
				'hide':{fn: function () {
					 $("#divWinCondition").hide();
				}}
	      },
	     items:[center,treeConPanel
	            ]
	 });
	 winCondition.show();
	
	 return;
 }
 
 function reloadTable(){
	 var tableid=$("#tableid").val();
	 var url="query2d.do";
	 $("#divTable").empty();
	 $("#divTable").load(url,{method:"layoutTable",uuid:tableid});
 }
 
 function doSaveTable(){
	 var tableid=$("#tableid").val();
	 var tablecaption=$("#tablecaption").val();
 }
 
 function doDeleteTable(){
	 var tableid=$("#tableid").val();
 }
 
 function drawCondition(){
	
	 $("#tbodyCondition").find("input[name=cond_oper],input[name=cond_logic],input[name=cond_column]").each(function(i){
		 var id=$(this).attr("id");
		 initCombox(id);
	 });
 }
 </script>
 
</head>
<body leftmargin="0" topmargin="0">

 <div id="west" >
        <div id="divTableToolbar"></div>
        <div id="divTree"></div>
    </div>
    <div id="center" >
    
       <div id="divTable">统计报</div>
       <div id="divResult" >
       <div id="divResultToolbar"></div>
        <div id="divResultDisplay" style="height:290px; overflow: scroll;"></div>
       </div>
     </div>
   
    <div id="props-panel" class="x-hide-display" style="width:200px;height:200px;overflow:hidden;">
    </div>
   
    <div id="divCondition" >
      
    </div>
     <div id="divConditionTree" >
      
    </div>
    <div id="divWinCondition"></div>
</body>
</html>