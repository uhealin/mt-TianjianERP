<%@ page language="java" contentType="text/html; charset=utf-8"pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>表单设计器</title>
<script type="text/javascript"> 

function ext_init(){
	    /* 在工具栏divBtn中显示的按钮 */
		new Ext.Toolbar({
	   		renderTo: "divBtn",
	           items:[
	          
      	       {
           		   text:'新增',
                   icon:'${pageContext.request.contextPath}/img/add.gif',
                   handler:function () {
				     add();
                   }
                },'-',
      	        {
          		   text:'修改',
                   icon:'${pageContext.request.contextPath}/img/edit.gif',
          	       handler:function(){
          	            var value=document.getElementById('chooseValue_sform_list').value;
          	       	    if(value==''){
          	       	    	alert('请选择要修改的列!');
          	       	    	return;
          	       	    }
          	       	    window.location="${pageContext.request.contextPath}/formDefine.do?method=formEdit&formTypeId=${param.formTypeId}&uuid="+value;  
          	       }  	 
                 },'-',
                { 
		             text:'删除',
		             icon:'${pageContext.request.contextPath}/img/delete.gif' ,
		             handler:function(){
		             	var value=document.getElementById('chooseValue_sform_list').value;
          	       	    if(value==''){
          	       	    	alert('请选择要删除的列!');
          	       	    	return;
          	       	    }
          	       	    if(confirm("您确定删除该行吗?")){
		                   deleteSform();	
		                }
		             }
		         },'-',
		         { 
		               text:'查询',
		               icon:'${pageContext.request.contextPath}/img/query.gif' ,
		               handler:function(){
		            	   selectQuery();
		   			   }
		           },'-',{ 
		               text:'预览',
		               icon:'${pageContext.request.contextPath}/img/query.gif' ,
		               handler:function(){
		            	   var value=document.getElementById('chooseValue_sform_list').value;
		            	   if(value==''){
	          	       	    	alert('请选择要预览的表单!');
	          	       	    	return;
	          	       	    }
		            	   
		            	   window.location="${pageContext.request.contextPath}/formDefine.do?method=formListView&formTypeId=${param.formTypeId}&uuid="+value;
		   			   }
		           },'-',{ 
		               text:'表单设计器',
		               icon:'${pageContext.request.contextPath}/img/design.png' ,
		               handler:function(){
		            	   var value=document.getElementById('chooseValue_sform_list').value;
	          	       	    if(value==''){
	          	       	    	alert('请先选择要修改的表单!');
	          	       	    	return;
	          	       	    }
	          	       	    
		          	       	var tab = parent.parent.tab ;
		          	       	var url = "${pageContext.request.contextPath}/formDefine.do?method=design&formId="+value ;
		        	        if(tab){
		        				n = tab.add({    
		        					title:"表单设计器",    
		        					closable:true,  //通过html载入目标页    
		        					html:'<iframe name="designFrm" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
		        				}); 
		        		        tab.setActiveTab(n);
		        			}else {
		        				window.open(url);
		        			}		
								          	       	    
		   			   }
		           },'-',{ 
		               text:'设置表单列表',
		               icon:'${pageContext.request.contextPath}/img/edit.gif' ,
		               handler:function(){
		            	   var value=document.getElementById('chooseValue_sform_list').value;
		            	   if(value==''){
	          	       	    	alert('请选择要设置的表单!');
	          	       	    	return;
	          	       	    }
		            	   window.location="${pageContext.request.contextPath}/formQueryConfig.do?method=queryConfigEdit&formTypeId=${param.formTypeId}&formId=" + value;
		   			   }
		           },'-', { 
		               text:'设置表单按钮',
		               icon:'${pageContext.request.contextPath}/img/edit.gif' ,
		               handler:function(){
		            	   var value= document.getElementById('chooseValue_sform_list').value;
		            	   if(value==''){
	          	       	    	alert('请选择要设置的表单!');
	          	       	    	return;
	          	       	    }
		            	   window.location="${pageContext.request.contextPath}/formQueryConfig.do?method=updateButtonsList&formTypeId=${param.formTypeId}&formId="+value;
		   			   }
		           },'-', { 
		               text:'10式表单',
		               icon:'${pageContext.request.contextPath}/img/edit.gif' ,
		               handler:function(){
		            	   var value= document.getElementById('chooseValue_sform_list').value;
		            	   if(value==''){
	          	       	    	alert('请选择要设置的表单!');
	          	       	    	return;
	          	       	    }
		            	   window.location="${pageContext.request.contextPath}/form/formListWhereEdit.jsp?formTypeId=${param.formTypeId}&formid="+value;
		   			   }
		           },'-', { 
		               text:'查看表单url',
		               icon:'${pageContext.request.contextPath}/img/edit.gif' ,
		               handler:function(){
		            	   var value= document.getElementById('chooseValue_sform_list').value;
		            	   if(value==''){
	          	       	    	alert('请选择要设置的表单!');
	          	       	    	return;
	          	       	    }
		            	   var jsonUrl="formDefine.do?method=jsonFormDefine&uuid="+value;
		            	   var url1="formDefine.do?method=formListView&uuid="+value;
		            	   
		            	   $.getJSON(jsonUrl,{},function(json){
		                       var context="普通表单:"+json["listViewUrl"]+"<br/>10式表单:<br/>";
		                       for(var i=0;i<json["listExtViews"].length;i++){
		                    	   var v=json["listExtViews"][i];
		                    	  context+=String.format("{0}式:{1}:{2} <br/>",v["where_id"],v["where_name"],v["url"]);
		                       }
		                       
			            	   Ext.MessageBox.alert("表单连接",context); 
		            	   });
		            	  
		   			   }
		           }]
		});
  }
  window.attachEvent('onload',ext_init);

</script>

<script type="text/javascript">
	//重定向到添加的页面
	function add(){
		var formTypeId = "${param.formTypeId}";
		window.location = "${pageContext.request.contextPath}/formDefine.do?method=formAdd&formTypeId=" + formTypeId;
	}
	
	//删除的方法
	function deleteSform(){
		var value=document.getElementById('chooseValue_sform_list').value;
		window.location="${pageContext.request.contextPath}/formDefine.do?method=removeForm&uuid="+value;
	}	
    //清空
</script>
</head>
<body>
	<div id="divBtn" ></div>
	<div style="height:expression(document.body.clientHeight-27);width:100%"> 
		<mt:DataGridPrintByBean name="sform_list"/>
	</div>	
    <!-- 查询的弹出窗口 -->
	<div id="selectQueryId" style="display: none;"><br/>
		     <div style="margin:0 20 0 20">请在下面输入查询条件</div>
			 <div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
		<form id="SelectFromId" method="post">
			<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center">
					<tr>
						 <td align="right">中文名:</td>
						  <td>
							   <input  type="text" name="cname" id="cname_id" title="支持模糊查询"/>
						   </td>
					</tr>
					
					<tr>
						   <td align="right">英文名:</td>
						   <td>
							     <input  type="text" name="enname" id="enname_id" title="支持模糊查询"/>
						    </td>
					</tr>
					
					<tr>
						   <td align="right">表单uuid:</td>
						   <td>
							     <input  type="text" name="uuidfind" id="uuidfind" title="支持模糊查询"/>
						    </td>
					</tr>
					
					<tr>
						   <td align="right">最后修改日期:</td>
						   <td>
							    <input  type="text" name="lastUTime" id="lastUTime_id" >
						    </td>
					</tr>
					<tr>
						   <td align="right">最后修改人:</td>
						   <td>
							     <input  type="text" name="lastUName" id="lastUName_id" title="支持模糊查询"/>
						    </td>
					</tr>
			</table>
		</form>
	</div>	
</body>
<script type="text/javascript">
//日期控件
new Ext.form.DateField({			
	applyTo : 'lastUTime_id',
	width: 133,
	format: 'Y-m-d'	
});
//查询的窗体
	var SelectQuery = null;
	function selectQuery(){
		document.getElementById("selectQueryId").style.display = "";
		var element=document.getElementById("selectQueryId");
		if(SelectQuery == null) { 
		    SelectQuery = new Ext.Window({
				title: '查询',
				width: 400, //调整对话框的宽度
				height:300, //调整对话框的高度
				contentEl:'selectQueryId', // ----
		        closeAction:'hide',
		        modal:true,
		        listeners:{
					'hide':{fn: function () {
						 document.getElementById("selectQueryId").style.display = "none";
					}}
				},
		        layout:'fit',
			    buttons:[
			    {
		            text:'显示所有',
		          	handler:function() {
		          		$('cname_id').value='';
		          		$('enname_id').vlaue='';
		          		$('lastUTime_id').value='';
		          		$('lastUName_id').value='';
		         		goSearch_sform_list()
		          		SelectQuery.hide();
                    }	
		        },
			    {
		            text:'确定',
		          	handler:function() {
		         		goSearch_sform_list()
		          		SelectQuery.hide();
                    }	
		        },{
		            text:'取消',
		            handler:function(){
		            	SelectQuery.hide();
		            }
		        }]
		    });
		}
	    SelectQuery.show();
	}
</script>
</html>