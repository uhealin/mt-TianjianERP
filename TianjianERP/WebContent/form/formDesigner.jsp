<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>表单设计器</title>
	<meta content="text/html; charset=utf-8" http-equiv="content-type" />
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/_source/plugins/input/dialogs/input.js" charset="gbk"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/_source/plugins/listView/dialogs/listView.js" charset="gbk"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor_source.js"></script>
	
	
	 
	<style type="text/css">
		
		.btn {
			width:80px;
			height:24px;
			margin:1px auto;
		}
		
		body {
			width:100%; 
			height: 100%;
		}
		#maindiv { 
			width:100%; 
			margin:0 auto;
			position:absolute; 
			height:100%;
		}
		
		.tTable {margin-top:10px;border:#99BBE8 1px solid;border-collapse:collapse;}
		.tTable td,th {
			padding: 5 5 5 2px;text-align:center;white-space:nowrap;border-top:#99BBE8 1px solid;border-left: #99BBE8 1px solid;height:30px;
		}
		.tTable th{background-color: #D9E8FB;}
		.tTable input {border:0px; border-bottom:1px solid #aaa;}
	</style>
	
	<script type="text/javascript">
		
		var mt_formDesign_inputArr = new Array();
		var mt_formDesign_listArr = new Array();
		var mt_formDesign_listColumnObj = new Array();
		
		function getTDHtml(record) {
			 if(!record) return ;
			 var key = record.get('key');
           	 var name = record.get('name');
           	 var label = record.get('label');
           	 var size = record.get('size');
           	 var exp = record.get('exp');
           	 var tips = record.get('tips');
           	 var autoid = record.get('autoid');
           	 var multiselect = record.get('multiselect');
           	 var refer = record.get('refer');
           	 var colspan = record.get('colspan');
           	 var tips = record.get('tips');
           	 
             if(!name) name = key ;   //如果没有设置name属性，就使用key
           	 if(exp) {
           		exp = "(" + exp + ")";
           	 }else {
           		exp = "";
           	 }
          	 //拼接表格html 	 
      	     var tdHtml =" <td align='right' nowrap='nowrap'>" + label + exp + "：</td> " ;
     	   	 tdHtml += "<td align=left " ;
     	   	 
      	   	 if(colspan) tdHtml += "colspan='" + colspan + "'" ; 
      	   	 
 	   		 tdHtml += ">" ;
 		 	 tdHtml += " <input type='text' name='" + name + "' id='" + name + "' size='" + size + "'  maxlength='"+size+"'  " ;
 		 	 
 		 	 if(autoid) {
 		 		 tdHtml += " autoid='" + autoid + "'" ; 
 		 		 
 		 	 } 
 		 	
 		 	 if(multiselect) {
 		 		 tdHtml += " multiselect=" + multiselect ; 
 		 	 }
 		 	 if(refer) {
 		 		 if(refer.indexOf("\\${") > -1 && refer.indexOf("tableName") > -1) {
 		 			refer = "${tableName}" ;
 		 		 }
 		 		 tdHtml += " refer='" + refer + "'" ; 
 		 	 }
 		 	 
 		 	 tdHtml += "> " ; 
      		 	 
 		 	 if(tips) {
 		 		tdHtml += "<span style='color:#ff6600;' align=left;>" ; 
 		 		tdHtml += tips ; 
 		 		tdHtml += "</span>" ; 
 		 	 }
      		 	 
      		 tdHtml += "</td> " ; 
           	 return tdHtml ;
		}
 		 	 
		function getListTableHtml(list) {
			 if(!list || list.length < 1) return ;
			 var table = "<table id=mt_listView_table cellspacing='0' cellpadding='0' border='0' class='tTable' width='1600' style='vertical-align: top;'>" ;
			 	 table += "<tr><th colspan=99 >列表控件列信息<a href='javascript:;' onclick='mt_table_addLine()'>【点击增加】</a></th></tr>" ;
			 	 table += "<tr>" ;
			 
			 var title = "<th width='5%'>操作</th>" ;
			 for(var i=0;i<list.length;i++) {
				 var record = list[i] ;
				 if(record) 
				 	title += "<th width='" + record.headWidth + "'>" + record.title + "</th>" ;
				 	
				 mt_formDesign_listColumnObj.push(record);
			 }
			 table += title + "</tr>" ;
			 table += "<tbody id=mt_listView_table_tbody>";
			 table += "</tbody>" ;
			 table += "</table>" ; 
			 
			 return table ;
		}
		
		Ext.onReady(function(){
			
			
             var inputStore = new Ext.data.JsonStore({ 
                autoLoad: true,  //自动加载数据 
                url: '${pageContext.request.contextPath}/form/json/formDesign.json', 
              	root:'input',    
              	fields:['key','name','size','exp','tips','autoid','refer','colspan','tips','label','multiselect'] 
             });
             
             var tableHtml = "<table id='inputTable' border='0' cellpadding='5' cellspacing='10' width='90%' align='center'>" ;
             
 			 
             inputStore.load({
           	  callback : function(){
           		  var selectArray = new Array() ;
           		  
           		  for(var i=0;i<inputStore.getTotalCount();i++) {
           			  var record = inputStore.getAt(i) ;
           			  
           			  var colspan = record.get('colspan');
           			  var autoid = record.get('autoid');
           			  var key = record.get('key');
           			  
           			  var trHtml = "<tr>" ;
           			  
           			  trHtml += getTDHtml(record) ;
           			  mt_formDesign_inputArr.push(record) ;
       	           	  if(!colspan || colspan == 1) {
       	           		 i++ ;
       	           		 
       	           		 var sRecord = inputStore.getAt(i) ;
       	           		 if(sRecord) {
        	           		 var colspan = sRecord.get('colspan');
        	           		 var nextkey = sRecord.get('key');
        	           		 if(colspan || colspan > 1) {
        	           			i-- ;
        	           		 }else {
        	           			trHtml += getTDHtml(sRecord) || "";
        	           		 	mt_formDesign_inputArr.push(sRecord) ;
        	           		 }
       	           		 }
       	           	   } 
       	           	   trHtml += "</tr>" ;
         	            	  
         	           tableHtml += trHtml || "";
           		 }
           		 
           		  var inputTBody = document.getElementById("inputAttr") ; 
           		  
           		  tableHtml += "</table>" ; 
           		  
                  inputTBody.innerHTML = tableHtml ;   
                     
           	  }
             });
             
             //初始化子列表属性...
             var listViewStore = new Ext.data.JsonStore({ 
                 autoLoad: true,  //自动加载数据 
                 url: '${pageContext.request.contextPath}/form/json/formDesign.json', 
               	 root:'listView',    
               	 fields:['key','name','size','exp','tips','autoid','refer','colspan','tips','columns','label','group','multiselect'] 
              });
              
              var listHtml = "<table id='inputTable' border='0' cellpadding='5' cellspacing='10' width='90%' align='center'>" ;
              
  			 
              listViewStore.load({
            	  callback : function(){
            		  var selectArray = new Array() ;  
            		  for(var i=0;i<listViewStore.getTotalCount();i++) {
            			  var record = listViewStore.getAt(i) ;
            			  
            			  var key = record.get('key');
            			  
            			  var trHtml = "<tr>" ;
            			  
            			  if(key != "ext_listView") {
            				  
            				  var autoid = record.get('autoid');
            				  var colspan = record.get('colspan');
            				  var key = record.get('key');
            				  trHtml += getTDHtml(record) ;
            				  mt_formDesign_listArr.push(record);
            				  if(!colspan || colspan == 1) {
               	           		 i++ ;
               	           		 
               	           		 var nextRecord = listViewStore.getAt(i) ;
               	           		 if(nextRecord) {
     	           	           		 var nextColspan = nextRecord.get('colspan');
     	           	           		 var nextKey = nextRecord.get('key');
     	           	           		 if(nextKey != "ext_listView") {
	     	           	           		 if(nextColspan || nextColspan > 1) {
	      	           	           			i-- ;
	      	           	           		 }else {
	      	           	           			trHtml += getTDHtml(nextRecord) || "";
	      	           	           			mt_formDesign_listArr.push(nextRecord);
	      	           	           		 }
     	           	           		 }
               	           		 }
               	           	  }
            			  }else {
            				  var columns = record.get('columns');
            				  
            				  trHtml += getListTableHtml(columns) || "" ;
            			  }
          	           	  
          	           	  trHtml += "</tr>" ;
          	           	  
          	           	listHtml += trHtml || "";
            		 }
            		 
            		  var listViewAttr = document.getElementById("listViewAttr") ; 
            		  
            		  listHtml += "</table>" ; 
            		  listViewAttr.innerHTML = listHtml ;   
                      
                      initCombox();
            	  }
              });
              
              //初始化ckeditor
              CKEDITOR.replace('formHtml', {
           	     customConfig : '${pageContext.request.contextPath}/ckeditor/config.js'
           	  });
             
		});
	</script>
</head>
<body>
<div id="maindiv" style="border: 1px solid blue; height: 100%;width: 100%;">
	<form id="thisForm" name="thisForm" method="post">
		<table id="formTable" border="0" style="width:100%;height: 95%;" cellpadding="0" cellspacing="0" >
			<tr>
			
				<td width="100px" valign="top">
					<button style="width:120;text-Align:left" onclick="executeCommand('input');">
						<img src="${pageContext.request.contextPath}/img/textfield.gif" height=20 width=20 align=absmiddle>输入框控件
					</button><br>
					    
					<button style="width:120;text-Align:left" onclick="executeCommand('listView');">
						<img src="${pageContext.request.contextPath}/img/listview.gif" height=20 width=20 align=absmiddle>列表控件
					</button><br>
					
					<button style="width:120;text-Align:left" onclick="executeCommand('table');">
						<img src="${pageContext.request.contextPath}/img/listview.gif" height=20 width=20 align=absmiddle>表格控件
					</button><br>
					
					<button style="width:120;text-Align:left" onclick="executeCommand('button');">
						<img src="${pageContext.request.contextPath}/img/button.png" height=20 width=20 align=absmiddle>按钮控件
					</button><br>
				
				</td>
				
				<td valign="top" rowspan="2">
					<textarea cols="80" id="formHtml" name="formHtml" rows="10" style="height: 100%;width:80%;">${html}</textarea>
					<input type="hidden" id="formId" name="formId" value="${formId}">
				</td>
			</tr>  
			
			<tr>
				<td valign="bottom">	
					<button style="width:120;text-Align:left" onclick="preview();">
						<img src="${pageContext.request.contextPath}/img/preview.png" height=22 width=22 align=absmiddle>预览表单
					</button><br>
					<button style="width:120;text-Align:left" onclick="save();">
						<img src="${pageContext.request.contextPath}/img/save20.png" height=22 width=22 align=absmiddle>&nbsp;保存表单
					</button><br>
				</td> 
			</tr>
		</table>
	</form>
</div>


	<div id="inputAttr" style="display: none;">
		
	</div> 
	
	
	<div id="listViewAttr" style="display: none;">
		
	</div>

</body>

<script type="text/javascript">
function save() {
	document.thisForm.action = "${pageContext.request.contextPath}/formDefine.do?method=updateFormHtml" ;
	document.thisForm.submit();
}

function preview() {
	if(confirm("预览表单前,系统会先自动保存表单,是否确定预览?")) {
		var tab = parent.parent.tab ;
	    var url = "${pageContext.request.contextPath}/formDefine.do?method=formPreView" ;
	    if(tab){
	    	var random =  Math.random();
			n = tab.add({    
				title:"表单预览",    
				closable:true,  //通过html载入目标页    
				html:'<iframe id="preFormFrm' + random + '" name="preFormFrm' + random + '" scrolling="no" frameborder="0" width="100%" height="100%" src=""></iframe>'   
			}); 
			tab.setActiveTab(n);
			
			document.thisForm.action = url ;
			document.thisForm.target = "preFormFrm"+random ;
			document.thisForm.submit();
	       
	        document.thisForm.target = "" ;
	        stopWaiting();
	        
		}else {
			window.open(url);
		}	
	}
}

function executeCommand(commandName) {
	var oEditor = CKEDITOR.instances.formHtml;
	oEditor.execCommand(commandName);
}

//添加行
function mt_table_addLine(attr,colWidth){
	if(attr) {
  	  attr = attr.replace(/[\s]+/gi,"") ; 
  	  attr = attr.substr(1,attr.length - 2) ; //去掉两边大括号,
	}
	
	var table = document.getElementById("mt_listView_table_tbody");
	
      var newTr = table.insertRow();

      //添加列
      var newTd1 = newTr.insertCell();
      newTd1.innerHTML = "<img alt='删除本行' style='cursor:hand;' onclick='mt_table_delLine(this)' src='${pageContext.request.contextPath}/img/close.gif' >" ;
      
      var random = Math.round(Math.random() * 10000);
      var initArr = new Array();
      for(var i=0;i<mt_formDesign_listColumnObj.length;i++) {
    	  var td = newTr.insertCell();
    	  var record = mt_formDesign_listColumnObj[i] ;
    	  var title = record.title ;
    	  var key = record.key ;
    	  var name = record.name ;
    	  var size = record.size ;  
    	  var headWidth = record.headWidth ;
    	  var autoid = record.autoid ;
    	  var multiselect = record.multiselect ;
    	  var refer = record.refer ;
    	  
    	  var tdHtml = "<input type='text' id='"+name+random+"' name='" + name + "' size='" + size + "' " ;
    	  
    	  if(autoid) {
    		  tdHtml += "autoid='" + autoid + "' " ;
    		  if(multiselect) {
    			  tdHtml += "multiselect='" + multiselect + "' " ;
    		  }
    		  initArr.push(name);
    	  }
    	  
    	  if(refer) {
    		  tdHtml += "refer='" + refer + "' " ;
    	  }
    	  
    	  //检查有无默认值 
    	  var width ;
    	  if(attr) {
        	  var attrArr = attr.split(",") ;
        	  
        	  for(var k=0;k<attrArr.length;k++) {
      			var keyValue = attrArr[k].split("=") ;
      			var attrKey = keyValue[0] ;
      			var value = keyValue[1] ;
      			if(key) {
      				if(attrKey == key) {
      					if(key == "ext_width") {
      						width = value ;
      					}else if(value) {
      						tdHtml += "value='" + value + "' " ;
      					}
      				}
      			}
      		}
          }
    	  
    	  if(key == "ext_width") {
    		  
    	  	  if(width){
    	  		tdHtml += "value='" + width + "' " ;
    	  	  }else if(colWidth) {
    	  		tdHtml += "value='" + colWidth + "' " ;
    	  	  }
    		  
    	  }
    	  
    	  
    	  tdHtml += ">" ;
    	  
    	  td.innerHTML = tdHtml; 
      }
      
      if(attr) {
    	  var attrArr = attr.split(",") ;
    	  //找出不能识别的属性
    	  var notExistsAttr = "" ;
    	  for(var k=0;k<attrArr.length;k++) {
  			var keyValue = attrArr[k].split("=") ;
  			var attrKey = keyValue[0] ;
  			var value = keyValue[1] ;
  			
  			var isArrExists = false ;
  			for(var i=0;i<mt_formDesign_listColumnObj.length;i++) {
  				var record = mt_formDesign_listColumnObj[i] ;
  				var key = record.key ;
  				
  				if(attrKey == key) {
  					isArrExists = true ;
  				}
  			}
  			
  			if(!isArrExists && attrKey != "ext_id") {
  				notExistsAttr += attrArr[k] + "," ;
  			}
  		}
    	  
    	if(notExistsAttr != "") notExistsAttr = notExistsAttr.substr(0,notExistsAttr.length -1) ;
    	  
    	Ext.get("listView_other"+random).dom.value = notExistsAttr ;
      }else {
    	  
      	  for(var i=0;i<initArr.length;i++) {
      		initCombox(initArr[i]+random);
      	  }
      }
      
}

//删除行
function mt_table_delLine(t){
	t.parentNode.parentNode.removeNode(true);
}

function genColumn() {
	//获取表头列信息
	var listView_head = document.getElementById("ext_columnName").value ;
	if(listView_head == "") {
		alert("请先按格式录入列表表头信息!") ;
		return ;
	}
	deleteAllRow() ;
	var arr = listView_head.split("`") ;
	var head = arr[arr.length - 1] ;
	
	var headArr = head.split("|") ;
	
	var width = Math.floor(100/headArr.length) ;
	for(var i=0;i<headArr.length;i++) {
		var attr = "{ext_id=" + headArr[i] + ",ext_name=" + headArr[i] + ",ext_type=text,ext_width=" + width + "}" ;
		mt_table_addLine(attr) ;
	}
	
	 initCombox();
}

function deleteAllRow() {
	var table = document.getElementById("mt_listView_table_tbody");
	var len = table.rows.length ;
	 //清空表中的行和列  
	 for(var i=0; i<len; i++){
		 table.deleteRow(i);
		 len=len-1;
         i=i-1;
	 }
}



</script>
</html>