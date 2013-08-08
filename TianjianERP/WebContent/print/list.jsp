<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>报表打印管理</title>
</head>

<script type="text/javascript">
var mytab;
function ext_init(){
	new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
        items:[{
            text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	window.location = "${pageContext.request.contextPath}/print.do?method=edit&menuid=${param.menuid}";
			}
      	},'-', {
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
	          	if(document.getElementById("chooseValue_${tablename}").value==""){
					alert("请选择要修改名称的对象！");
					return;
				}
            	
	          	window.location = "${pageContext.request.contextPath}/print.do?method=edit&menuid=${param.menuid}&uuid="+document.getElementById("chooseValue_${tablename}").value;
            	
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	if(document.getElementById("chooseValue_${tablename}").value==""){
					alert("请选择要删除的对象！");
					return;
				}
				if(confirm("确定删除此对象？")){
					Ext.Ajax.request({
	        			method:'POST',
	        			params : { 
	        				uuid : document.getElementById("chooseValue_${tablename}").value
	        			},
	        			url:"${pageContext.request.contextPath}/print.do?method=del",
	        			success:function (response,options) {
	        				var result = response.responseText;
	        				alert(result);
	        				goSearch_${tablename}();
	        			},
	        			failure:function (response,options) {
	        				alert("后台出现异常,获取文件信息失败!");
	        			}
	        		});
				}
            }
        },'-',{
	    	text:'运行',
	        cls:'x-btn-text-icon',
	        icon:'${pageContext.request.contextPath}/img/query.gif',
	        handler:function(){
	        	var templateid = document.getElementById("chooseValue_${tablename}").value;
	        	if(templateid == ""){
	        		alert("请先选择报表模板！");
	        		return;
	        	}
	        	document.getElementById("checkvalue").value = templateid;
	        	
	        	QryEmWinFun();
			}
		},'-',{ 
		        text:'关闭',
		        cls:'x-btn-text-icon',
		        icon:'${pageContext.request.contextPath}/img/close.gif',
		        handler:function(){
		        	closeTab(parent.tab);
				}
	    },'->']
	});
	
	//initCombox(document.getElementById("templateid"));
} 

Ext.onReady(function(){
	ext_init();
});

</script>

<body>

<form name="thisForm" method="post" action="" id="thisForm" >
<div id="divBtn"></div>
<div id="tabUser1" style="height:expression(document.body.clientHeight-32);width:100%">
	<mt:DataGridPrintByBean name="${tablename}"   />
</div>
<input type="hidden" id="tablename" name="tablename" value="${tablename}" />
<input type="hidden" id="departmentid" name="departmentid" value="${departmentid}" />
<input type="hidden" id="emtype" name="emtype" value="${emtype}" /> 
<input type="hidden" id="qryWhere_em" name="qryWhere_em" value="${qryWhere_em} " /> 
<input type="hidden" id="qryJoin_em" name="qryJoin_em" value="${qryJoin_em }" /> 
<input type="hidden" id="checkvalue" name="checkvalue" value="" />

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="divBatchUpdate" style="display: none;">
  <table class="formTable">
     <tbody>
       <tr>
          <th>子集</th>
          <td><input autoid="5020" noinput="true" name="item" id="item" refer='checkvalue' /></td>
        </tr>
        <tr>
          <th>属性</th>
          <td><input autoid="5012|item" refer="item" noinput="true" name="pro" id="pro" /></td>
        </tr>
              <tr>
          <th>值</th>
          <td><input  name="val" id="val" /></td>
        </tr>
     </tbody>
  </table>
</div>

<div id="customQry_em" style="display: none;">
 <br/>
 <table class="qryTb" align="center"> 
 	<tr>
 		<th style="width: 100px" > 
 			<a href="javascript:;" onclick="addEmQuery('em');"> 
 			<img src="/AuditSystem/img/add.gif" />
 			</a> 
 		</th> 
 		<th>子集</th>
 		<th>列名称</th> 
 		<th >条件</th>
 		<th >值</th>
 		<th >删除</th>
	</tr>
	<tbody id="queryTBody_em">
	</tbody> 
 </table>
 <input type="hidden" id="qryWhere_em" name="qryWhere_em">
</div>

</form>



</body>
</html>

<script>

var jarrEmTable=[];
var jarrQueryitems='${jarrQueryitems}';

var customQryWin;
//�Զ����ѯ
function QryEmWinFun() {
	var tableId="em";
	//var customQryWin = this["customQryWin"] ;
	document.getElementById("customQry_"+tableId).style.display = "";
	
	if(customQryWin == null) { 
		customQryWin = new Ext.Window({
			title: '查询条件',
			width: 780,
			height:300,
			contentEl:'customQry_'+tableId, 
	        closeAction:'hide',
	        autoScroll:true,
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("customQry_"+tableId).style.display = "none";
				}}
			},
	        layout:'fit',
		    buttons:[{
	            text:'打印',
	          	handler:function() {
	          		var qryWhere = createEmQryWhere(tableId);
	          		var qryJoin = createEmQryJoin(tableId);
	          		if(qryWhere == false) return ;
	          		document.getElementById("qryWhere_"+tableId).value = qryWhere ;
	          		document.getElementById("qryJoin_"+tableId).value = qryJoin ;
	          		var templateid = document.getElementById("chooseValue_${tablename}").value;
	          		//alert(templateid+"|"+qryWhere + "|" + qryJoin);
	        		Ext.Ajax.request({
	        			method:'POST',
	        			params : { 
	        				menuid : '${param.menuid}',
	        				tablename : document.getElementById("tablename").value,
	        				templateid : templateid,
	        				emtype : document.getElementById("emtype").value,
	        				departmentid : document.getElementById("departmentid").value,
	        				qryWhere_em : document.getElementById("qryWhere_em").value,
	        				qryJoin_em : document.getElementById("qryJoin_em").value
	        				
	        			},
	        			url:"${pageContext.request.contextPath}/print.do?method=print",
	        			success:function (response,options) {
	        				var result = response.responseText;
	        				
	        				if(result.indexOf("false|")>-1){
	        					alert((result.split('|'))[1]);
	        				}else{
	        					eval(result);	
	        				}
	        			},
	        			failure:function (response,options) {
	        				alert("后台出现异常,获取文件信息失败!");
	        			}
	        		});
	        		
	          	}
	        },
	        /*
	        {
	            text:'保存查询',
	            handler:function(){
	            	Ext.Msg.prompt('保存查询', '请输入自定义查询名', function(btn, text){
	            	    if (btn != 'ok')return false;
	            	    if(text.length<0){
	            	        	alert("自定义查询名不能为空");
	            	        	return false;
	            	    }
	              		var qryWhere = createEmQryWhere("em");
	              		var qryJoin = createEmQryJoin("em");
	              		var departmentid=document.getElementById("departmentid").value;
	              		var emtype=document.getElementById("emtype").value;
	                    var context=$('#queryTBody_'+tableId).html();
	              		var url="employment.do?method=doSaveUserQuery";
	              		var query_table = document.getElementsByName("query_table_"+tableId) ;
	              		var query_logic = document.getElementsByName("query_logic_"+tableId) ;
	              		var query_column = document.getElementsByName("query_column_"+tableId) ;
	              		var query_operator = document.getElementsByName("query_operator_"+tableId) ;
	              		var query_condition = document.getElementsByName("query_condition_"+tableId) ;
	              		var querys=[];
	              		
	              		for(var i=0;i<query_logic.length;i++) {
	              			var logic = query_logic[i].value ;
	              			var column = query_column[i].value ;
	              			var operator = query_operator[i].value ;
	              			var condition = query_condition[i].value ;
	              			var table = query_table[i].value ;
	              			querys.push({
	              				logic:logic,column_name:column,operator:operator,condition_name:condition,table_name:table
	              			});
	              		}
	              		var qstr=Ext.util.JSON.encode(querys);
	              		
	              		var request={
	              				qry_where:qryWhere,qry_join:qryJoin,
	              				departmentid:departmentid,emtype:emtype,context:context,name:text,querys:qstr
	              		};
	              		
	              		//var result=ajaxLoadPageSynch(url,request);
	              		$.post(url,request,function(str){
	              			alert(str);
	              		});
	            	        
	            	    
	            	});

	            }
	        },
	        */
	        {
	            text:'取消',
	            handler:function(){
	            	customQryWin.hide();
	            }
	        }]
	    }); 
		this["customQryWin_"+tableId] = customQryWin ;
		if(jarrQueryitems.length==0){
		  addEmQuery(tableId,true);
		}else{
			for(var i=0;i<jarrQueryitems.length;i++){
				addEmQueryWithValue(tableId,i==0,jarrQueryitems[i]);
			}
		}
		
	}
	customQryWin.show();
}

function addEmQuery(tableId,first) {
	var trObj ;
	var tdObj ;
	var r=parseInt(Math.random()*10000+1); 
	var columns = [{id:'111',header:"333",freequery:"4545"}];
	var tbody = document.getElementById("queryTBody_"+tableId);
	trObj = tbody.insertRow();
	trObj.id = "queryTr_" + tableId;
	
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var display = "" ;
	var type="text";
	var logic="and";
	if(first) {
		display = "display:none;" ;
		type="hidden";
	}
	tdObj.innerHTML ="<div  style=\"width:100%;"+display+"\">"
					+" <input value='"+logic+"'   name='query_logic_" + tableId + "' id='query_logic_" + tableId +r+ "' noinput='true' autoid='700|query_logic' refer='query_logic' style='width:80px;'  />" ;
					+ "</div>" ;
	
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var tableHtml = "<input autoid='5020' name='query_table_" + tableId + "' refer='checkvalue' noinput='true' id='query_table_" + tableId +r+ "'>";
    tdObj.innerHTML = tableHtml ;


	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var columnHtml = "<input autoid='5012|query_table_" + tableId +r+ "' noinput='true' noinput='true' refer='query_table_" + tableId +r+ "' name='query_column_" + tableId + "' id='query_column_" + tableId +r+ "'>";
	tdObj.innerHTML = columnHtml ;
	
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	tdObj.innerHTML = " <input value='like'  name='query_operator_" + tableId + "' noinput='true' id='query_operator_" + tableId+r + "' autoid='700|query_operator' refer='query_operator' style='width:100px' />" ;

	tdObj = trObj.insertCell();
	tdObj.align = "center";

	tdObj.innerHTML = "<input type=text id='query_condition_" + tableId +r+ "'  name='query_condition_" + tableId + "'  size='30'>";
	
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	if(!first) {
		tdObj.innerHTML = "<a href='javascript:;' onclick='removeEmQuery(this);' ><img src=" + MATECH_SYSTEM_WEB_ROOT + "img/delete.gif></a>" ;
	}
	initCombox("query_logic_" + tableId +r);
	initCombox("query_operator_" + tableId +r);
	initCombox("query_table_" + tableId +r);
	initCombox("query_column_" + tableId +r);

}


function addEmQueryWithValue(tableId,first,item) {
	var trObj ;
	var tdObj ;
	var r=parseInt(Math.random()*10000+1); 
	var columns = [{id:'111',header:"333",freequery:"4545"}];
	var tbody = document.getElementById("queryTBody_"+tableId);
	trObj = tbody.insertRow();
	trObj.id = "queryTr_" + tableId;
	
	//����
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var display = "" ;
	var type="text";
	if(first) {
		display = "display:none;" ;
		type="hidden";
	}
	tdObj.innerHTML = "<div  style=\"width:100%;"+display+"\">"
				    +" <input   name='query_logic_" + tableId + "' id='query_logic_" + tableId+r + "' noinput='true' autoid='700|query_logic' refer='query_logic' style='width:80px' value='"+item["logic"]+"' />" ;
					+ "</div>" ;
  
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var tableHtml = "<input autoid='5020' name='query_table_" + tableId + "' noinput='true' refer='checkvalue' id='query_table_" + tableId +r+ "' noinput='true'  value='"+item["table_name"]+"' />"
    tdObj.innerHTML = tableHtml ;

	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var columnHtml = "<input autoid='5012|query_table_" + tableId +r+ "' noinput='true'  refer='query_table_" + tableId +r+ "' noinput='true' name='query_column_" + tableId + "' id='query_column_" + tableId +r+ "' value='"+item["column_name"]+"' />";
	tdObj.innerHTML = columnHtml ;
	
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	tdObj.innerHTML = "<div class=selectDiv style=\"width:80px;\">"
	+ "	<select class=mySelect style=\"width:80px;\" name='query_operator_" + tableId + "' noinput='true' id='query_operator_" + tableId + "'  value='"+item["operator"]+"' >"
	+ "		<option value='='> 等于(=) </option> "
	+ "		<option value='!='> 不等于(!=) </option> "
	+ "		<option value='>'> 大于(&gt;) </option> "
	+ "		<option value='<'> 小于(&lt;) </option> "
	+ "		<option value='>='> 大于等于(&gt;=) </option> "
	+ "		<option value='<='> 小于等于(&lt;=) </option> "
	+ "		<option value='like'> 包含</option> "
	+ "		<option value='not like'> 不包含 </option> "
	+ " </select>";
	+ " </div>";
	tdObj.innerHTML=" <input  name='query_operator_" + tableId + "' noinput='true' id='query_operator_" + tableId +r+ "' autoid='700|query_operator' refer='query_operator' style='width:80px' value='"+item["operator"]+"' />" ;

	tdObj = trObj.insertCell();
	tdObj.align = "center";

	tdObj.innerHTML = "<input type=text id='query_condition_" + tableId + "'  name='query_condition_" + tableId + "'  size='30' value='"+item["condition_name"]+"' />";
	
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	if(!first) {
		tdObj.innerHTML = "<a href='javascript:;' onclick='removeEmQuery(this);' ><img src=" + MATECH_SYSTEM_WEB_ROOT + "img/delete.gif></a>" ;
	}
	initCombox("query_table_" + tableId +r);
	initCombox("query_column_" + tableId +r);
	initCombox("query_logic_" + tableId +r);
	initCombox("query_operator_" + tableId +r);
}

function removeEmQuery(obj) {
	var tbody = obj.parentElement.parentElement.parentElement ;
	var trObj = obj.parentElement.parentElement ;
	if(trObj) {
		tbody.removeChild(trObj);
	}
}


function createEmQryJoin(tableId) {
	
	var query_table = document.getElementsByName("query_table_"+tableId) ;
	
	
	var qryJoin = "" ;
	var mapTable={};
	for(var i=0;i<query_table.length;i++) {
		var table = query_table[i].value ;
		if(!mapTable[table]){
			qryJoin+=" left join "+table+" st_"+table+" on st_"+table+".userid =a.id ";
			mapTable[table]=table;
		}
	}
	
	return qryJoin ; 
}


function createEmQryWhere(tableId) {
	var query_table = document.getElementsByName("query_table_"+tableId) ;
	var query_logic = document.getElementsByName("query_logic_"+tableId) ;
	var query_column = document.getElementsByName("query_column_"+tableId) ;
	var query_operator = document.getElementsByName("query_operator_"+tableId) ;
	var query_condition = document.getElementsByName("query_condition_"+tableId) ;
	
	var qryWhere = "" ;
	for(var i=0;i<query_logic.length;i++) {
		var logic = query_logic[i].value ;
		var column = query_column[i].value ;
		var operator = query_operator[i].value ;
		var condition = query_condition[i].value ;
		var table = query_table[i].value ;
		if(column == "") {
			alert("所有条件不能为空!") ;
			return false ;
		}
		
		if(operator.indexOf("like") > -1) {
			if(condition != "") {
				condition = "'%" + condition + "%'" ;
			}
		}else{ //if(isNaN(condition) || condition == "") {
			condition = "'" + condition + "'" ;
		}
		
		if(table.toUpperCase() == "k_user".toUpperCase()){
			qryWhere += " " + logic + " a." + column + " " + operator + " " + condition ;
		}else{
			qryWhere += " " + logic + " st_"+table+"." + column + " " + operator + " " + condition ;	
		}
		
	}
	
	return qryWhere ; 
}

var batchUpdateWin;
function openBatchUpdate(){
	document.getElementById("divBatchUpdate").style.display = "block";
	if(batchUpdateWin==null){
	batchUpdateWin = new Ext.Window({
		title: '批量处理',
		width: 500,
		
		contentEl:"divBatchUpdate", 
        closeAction:'hide',
        autoScroll:true,
        modal:true,
        listeners:{
			'hide':{fn: function () {
				 document.getElementById("divBatchUpdate").style.display = "none";
			}}
		},
        layout:'fit',
        buttons:[{
            text:'修改',
          	handler:function() {
          		var qryWhere = createEmQryWhere("em");
          		var qryJoin = createEmQryJoin("em");
          		var departmentid=document.getElementById("departmentid").value;
          		var emtype=document.getElementById("emtype").value;
          		var item=document.getElementById("item").value;
          		var pro=document.getElementById("pro").value;
          		var val=document.getElementById("val").value;
          		var url="employment.do?method=doBatchUpdate";
          		var request={
          				qryWhere:qryWhere,qryJoin:qryJoin,
          				departmentid:departmentid,emtype:emtype
          				,item:item,pro:pro,val:val
          		};
          		
          		$.post(url,request,function(str){
          			alert(str);
          		});
          		
          	}
        },{
            text:'取消',
            handler:function(){
            	batchUpdateWin.hide();
            }
        }]
	});
    }
	batchUpdateWin.show();
}
</script>



