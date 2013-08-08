//新增
function mt_formList_add(_param) {
	
	var formId = document.getElementById("formId").value;
	var url = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId;
	
	var param=getParamObject();
	for(var key in param){
		if(key=='method'||key=="uuid")continue;
		url+="&"+key+"="+param[key];
		
	}
	_param=_param||{};
	for(var key in _param){
		url+="&"+key+"="+_param[key];
	}
	window.location=url; 
}

//编辑
function mt_formList_edit(_param) {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	
	var value= document.getElementById("chooseValue_" + tableId).value;
	if(value == ''){
		
		value = getChooseValue(tableId);
				
		if(value == "") {
			alert('请选择要修改的数据!');
			return;
		
		} else if(value.indexOf(",") > -1) {
			alert('请选择一条需要修改的数据!');
			return;
		}
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId + "&uuid="+value; 
	var param=getParamObject();
	for(var key in param){
		if(key=='method'||key=="uuid")continue;
		url+="&"+key+"="+param[key];
		
	}
	_param=_param||{};
	for(var key in _param){
		url+="&"+key+"="+_param[key];
	}
	window.location.href=url;
}

//预约
function mt_formList_addOrEdit() {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	var value= document.getElementById("chooseValue_" + tableId).value;
	if(value == ''){
		value = getChooseValue(tableId);
		if(value == "") {
			window.location = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId;
			return;
		
		} else if(value.indexOf(",") > -1) {
			alert('请选择一条需要修改的数据!');
			return;
		}
	}
	
	window.location = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId + "&uuid="+value; 
}

//删除
function mt_formList_remove() {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	
	var value = document.getElementById("chooseValue_" + tableId).value;
	if(value == ""){
		
		value = getChooseValue(tableId);
		
		if(value == "") {
			alert('请选择要删除的数据!');
			return;
		}
	} 
	
	if(confirm("您确认要删除当前选中数据吗？")){
		var param=getParamObject();
		
		var url =MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=removeFormData&formId=" + formId + "&uuid="+value;
		
		for(var key in param){
			if(key=='method'||key=="uuid")continue;
			url+="&"+key+"="+param[key];
		}
		 window.location =  url;
	} else {
		return;
	}
}


//假删除
function mt_formList_remove_noreal() {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	
	var value = document.getElementById("chooseValue_" + tableId).value;
	if(value == ""){
		
		value = getChooseValue(tableId);
		
		if(value == "") {
			alert('请选择要删除的数据!');
			return;
		}
	} 
	
	if(confirm("您确认要删除当前选中数据吗？")){
		var param=getParamObject();
		
		var url =MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=removeFormDataNotReal&formId=" + formId + "&uuid="+value;
		
		for(var key in param){
			if(key=='method'||key=="uuid")continue;
			url+="&"+key+"="+param[key];
		}
		 window.location =  url;
	} else {
		return;
	}
}

//查看
function mt_formList_view(formId) {
	
	var srcFormId = "";
	if(!formId) {
		formId = document.getElementById("formId").value;
	} else {
		srcFormId = document.getElementById("formId").value;
	}
	
	var tableId = document.getElementById("tableId").value;
	
	var value= document.getElementById("chooseValue_" + tableId).value;
	
	if(value == ''){
		
		value = getChooseValue(tableId);
				
		if(value == "") {
			alert('请选择要查看的数据!');
			return;
		
		} else if(value.indexOf(",") > -1) {
			alert('请选择一条需要查看的数据!');
			return;
		}
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&view=true&formId=" + formId + "&uuid="+value + "&srcFormId=" + srcFormId; 
	var param=getParamObject();
	for(var key in param){
		if(key=='method'||key=="uuid")continue;
		url+="&"+key+"="+param[key];
	}
	window.location.href=url;
}

//返回
function mt_formList_back() {
	window.history.back(); 
}

//关闭按钮
function mt_formList_close() {
	//closeTab(parent.parent.mainTab);
	closeTab(parent.parent.tab);

}

function mt_form_getRowValues() {
	var tableId = document.getElementById("tableId").value;
	
	var uuid = document.getElementById("chooseValue_" + tableId).value;
	
	if(uuid == "") {
		uuid = getChooseValue(tableId);
		if(uuid == "") {
			alert('请选择要操作的数据!');
			return;
		}
	} 
	
	var json = "[";
	
	var uuids = uuid.split(",");
	
	for(var i=0; i < uuids.length; i++) {
		
		var divObj = document.getElementById("trValueId_" + uuids[i]);
		var oAttribs = divObj.attributes;
		
		json += " {";
		
		var data = "";
		
		for (var j = 0; j < oAttribs.length; j++){
			if(oAttribs[j].specified == true){
				data += "'" + oAttribs[j].nodeName + "':'" + oAttribs[j].nodeValue + "',";
			}
		}
		
		if(data != "") {
			data = data.substring(0, data.length -1);
		}
		
		json += data + "}";
		
		if(i != uuids.length-1) {
			json += ",";
		}
	}
	
	json += "]";

	return eval(json);
}

//列表按钮接口
function mt_form_listBtn_callJava(tableId, btnId) {
	//alert(tableId);
	
	var uuid = document.getElementById("chooseValue_" + tableId).value;
	
	if(uuid == "") {
		uuid = getChooseValue(tableId);
		if(uuid == "") {
			alert('请选择要操作的数据!');
			return;
		}
	}
	
	var requestString = "";
	
	var uuids = uuid.split(",");
	
	for(var i=0; i < uuids.length; i++) {
		
		var divObj = document.getElementById("trValueId_" + uuids[i]);
		var oAttribs = divObj.attributes;
		
		for (var j = 0; j < oAttribs.length; j++){
			if(oAttribs[j].specified == true){
				requestString += "&" + oAttribs[j].nodeName + "=" + oAttribs[j].nodeValue;
			}
		}
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formQueryConfig.do?method=buttonExtHandle&btnId=" + btnId;
	//alert(url);
	//alert(requestString);
	var result = ajaxLoadPageSynch(url, requestString);
	
	alert(result);
	
	
	//alert(method);
	try{
		var method = "goSearch_" + tableId + "(2)";
		eval(method);
	}catch(e){
		alert(e);
	}
}

//表单按钮接口
function mt_form_formBtn_CallJava(btnId) {
	//alert(tableId);
	
	var uuid = document.getElementById("uuid").value;
	
	var requestString = "&uuid=" + uuid;
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formQueryConfig.do?method=buttonExtHandle&btnId=" + btnId;
	
	var result = ajaxLoadPageSynch(url, requestString);
	
	alert(result);
}

//创建行
function mt_createRow(tableId,rows,cells){
	var rs,cs;
	
	if(rows == ""){
		rs = 0;
	} else {
		rs = rows;
	}
	
	if(cells == ""){
		cs = 0; 
	} else {
		cs = cells;
	}
	
	var table = document.getElementById(tableId);
       
	for(var i=0; i<rs; i++){
		//添加一行
        var newTr = table.insertRow();
		for(var j = 0; j < cs; j++){
	       //添加列
			var newTd = newTr.insertCell();
		}
	}
}

//删除
function mt_remove(t){
	
	if(confirm("您确定要删除吗?")){
		t.parentNode.parentNode.removeNode(true);
		
		if(t.group) { 
			//删除同组的记录
			var imgs = Ext.query("img[group="+t.group+"]") ;
			Ext.each(imgs,function(img){
				img.parentNode.parentNode.removeNode(true);
			}) ;
			
		}
	}
	
	mt_form_total();   
	
	//隐藏列
	if(funExists("mt_subform_after_del")) {
		mt_subform_after_del(t);
	}
}

//删除所有行
function mt_remove_all(table) {
	
    var tableObj = document.getElementById(table);
    
    for(var i=tableObj.rows.length-1; i >= 0 ; i--) {
    	tableObj.deleteRow(i);
    }
}

//新增一行
function mt_add(table,length){
	
	var randomId = Math.round(Math.random() * 10000);
	
	var mt_slist = eval('(' + document.getElementById("mt_slist_" + table).value + ')');
	
	mt_createRow(table,1,length);
	
	var slistLength = mt_slist.length ;
	
	var tbField = document.getElementById(table);
	
	tbField.rows[tbField.rows.length-1].cells[0].innerHTML = "<img id='"+table+"_delImg_"+randomId+"' flag="+table+ "_del style='cursor:hand;' alt='删除本行' onclick='mt_remove(this)' src='" + MATECH_SYSTEM_WEB_ROOT + "/img/close.gif' >";
	
	var colCount = 0;
	
	var cell = tbField.rows[tbField.rows.length-1].cells[0];
	
	for(var i = 1; i <= slistLength; i++){
	
		var inputObj = document.createElement(mt_slist[i-1]) ;
		
		
		if(inputObj.id.indexOf("hidden_") < 0) {
			colCount++;
		}
		
		inputObj.id = inputObj.id + '_' + randomId ;
		
		if(inputObj.refer) {
			//$2, 替换成本行的ID
			inputObj.refer = inputObj.refer.replace("_\$rowIndex", "_" + randomId);
		}
		
		if(inputObj.refer1) {
			//$3, 替换成本行的ID
			inputObj.refer1 = inputObj.refer1.replace("_\$rowIndex", "_" + randomId);
		}
		
		if(inputObj.refer2) {
			//$4, 替换成本行的ID
			inputObj.refer2 = inputObj.refer2.replace("_\$rowIndex", "_" + randomId);
		}
		
	
		if(inputObj.type != "hidden") {
			cell = cell.nextSibling;
		}
		if(cell) {
			cell.appendChild(inputObj) ;
		}else {
			alert("列数设置超过表头定义列数,请检查配置!");
			return ;
		}
		
		if(inputObj.autoid) {
			initCombox(inputObj);
		}
		
		if(inputObj.ext_type == "date") {
			mt_form_initDateSelect(inputObj);
		}
		
	}
	//隐藏列
	if(funExists("mt_subform_after_add")) {
		mt_subform_after_add(table);
	}
	return randomId;
}

//下拉GRID填充列表值
function mt_form_setRowValue(obj) {

	var inputId = obj.inputId;
	var inputProperty = obj.property;
	var name = inputId.replace(inputProperty,"");
	
	var rowIndex = name.split("_")[1];
	var json = Ext.util.JSON.decode(obj.columns);
	
	for(var field in json) {
		var fieldId = field.toLowerCase().replace("hidden_","");
		fieldId = inputProperty + fieldId + "_" + rowIndex;
		if(document.getElementById(fieldId)) {
			document.getElementById(fieldId).value = json[field];
			
			if(Ext.getCmp(fieldId)) {
				Ext.getCmp(fieldId).setRealValue(json[field]);
			}
		}
	}
}

//下拉GRID填充列表值
function mt_form_setRowValues(obj) {
	var jsonArray = Ext.util.JSON.decode(obj.columns);

	var property = obj.property;
	var tableName = property.split("`")[0];
	var colCount = property.split("`")[1];
	
	mt_remove_all(tableName);
	
	for(var i=0; i < jsonArray.length; i++) {
		var json = jsonArray[i];
		var rowIndex = mt_add(tableName, colCount);
		for(var field in jsonArray[i]) {
			var fieldId = field.toLowerCase().replace("hidden_","");
			
			if(fieldId == "select_group") {
				document.getElementById(tableName +"_delImg_"+rowIndex).group = json[field];
			}
			
			fieldId = tableName + "_" + fieldId + "_" + rowIndex;
			if(document.getElementById(fieldId)) {
				document.getElementById(fieldId).value = json[field];
				
				if(Ext.getCmp(fieldId)) {
					Ext.getCmp(fieldId).setRealValue(json[field]);
				}
			}
		}
	}
}

//下拉GRID填充表单值
function mt_form_setValue(obj) {

	var json = Ext.util.JSON.decode(obj.columns);
	
	for(var field in json) {
		var fieldId = field.toLowerCase().replace("hidden_","");
		if(document.getElementById(fieldId)) {
			document.getElementById(fieldId).value = json[field];
			
			if(Ext.getCmp(fieldId)) {
				Ext.getCmp(fieldId).setRealValue(json[field]);
			}
			//Ext.getCmp(fieldId).setRawValue
			//initCombox(document.getElementById(fieldId));
		}
	}
}

//初始化附件上传控件
function mt_form_initAttachFile(param) {
	var inputArray ;
	
	if(param) {
		if(typeof(param) == "string") {
			inputArray = Ext.query("#"+param) ;
		}else {
			var arr = new Array();
			arr.push(param) ;
			inputArray = arr ;
		}
	} else {
		inputArray = Ext.query("input[ext_type=attachFile]") ;
	}
	
	 
	Ext.each(inputArray,function(input){
		attachInit(input.id);
	});

}

//初始化extjs日期控件
function mt_form_initDateSelect(param) {
	var inputArray ;
	
	if(param) {
		if(typeof(param) == "string") {
			inputArray = Ext.query("#"+param) ;
		}else {
			var arr = new Array();
			arr.push(param) ;
			inputArray = arr ;
		}
	} else {
		inputArray = Ext.query("input[ext_type=date]") ;

	}
	
	var plugins = "";
	var format = "Y-m-d";
	
	Ext.each(inputArray,function(input){
		
		if(!input.readOnly) {
			if(input.ext_format) {
				
				if(input.ext_format == "yyyy-MM-dd") {
					plugins = "";
					format = "Y-m-d";
				} else if(input.ext_format == "yyyy-MM") {
					plugins = "monthPickerPlugin";
					format = "Y-m";
				} 
			}
			
			new Ext.form.DateField({
				applyTo:input.id,
				width:100,  
				plugins: plugins,  
				format: format,  
				editable: false,
				cls:"inline"
			});
		}
		
	});
}

//初始化只读
function mt_form_initReadonly() {
	var inputArray = Ext.query("input[ext_readonly]") ;
	
	Ext.each(inputArray,function(input){
			input.className = "readonly";
	});
}

function mt_form_total(obj) {
	
	var formulaArray = Ext.query("input[formula]") ;
	
	Ext.each(formulaArray,function(input){
		
		var formula = input.formula ;
		formula = formula.replace(new RegExp("sum\\(","gm"),"mt_form_sum("); 
		formula = formula.replace(new RegExp("value\\(","gm"),"mt_form_value("); 
		formula = formula.replace(new RegExp("sumif\\(","gm"),"mt_form_sumif("); 
		formula = formula.replace(new RegExp("rowValue\\(","gm"),"mt_form_rowValue("); 
		formula = formula.replace(new RegExp("`","gm"),"'"); 
		formula = formula.replace(new RegExp("，","gm"),","); 
		formula = formula.replace(new RegExp("《","gm"),"<"); 
		formula = formula.replace(new RegExp("》","gm"),">"); 
		
		if(obj) {
			//只执行相关的
			var objName = obj.name ;
			if(formula.indexOf(objName) > -1) {
				
				if(formula) {
	 				var formulaValue = eval(formula) ;
	 				input.value = formulaValue ;
	 				input.fireEvent("onchange") ;
	 			}
			}
		}else {
			//执行全部
			if(formula) {
 				var formulaValue = eval(formula) ;
 				input.value = formulaValue ;
 				input.fireEvent("onchange") ;
 			}
		}
		
			
	});
}

function mt_form_sum(name){
	var sum = 0.00;
	var sumArray = document.getElementsByName(name) ;
	for(var i = 0;i<sumArray.length;i++){
		var sumValue = sumArray[i].value ;
		
		if(sumValue) {
			sum += parseFloat(sumValue);
		}
	}
	return sum ;
}
	
function mt_form_value(name){
		
	var sum = 0.00;
	var sumArray = document.getElementsByName(name) ;
	if(sumArray) {
		if(sumArray[0].value)
			sum += parseFloat(sumArray[0].value);
	}
	return sum ;
}
	
function mt_form_sumif(condition,name1,name2){
	
	var name1Arr = document.getElementsByName(name1) ;
	var name2Arr = document.getElementsByName(name2) ; 
	var forName = name1Arr ;
	if(name1Arr.length < 1) {
		forName = name2Arr ;
	}
	
	var sumValue = 0.00 ;
	for(var i = 0;i<forName.length;i++){
		var curCondition = condition.replace(new RegExp("\\\$rowObj","gm"),"curObj"); 
		var curObj = forName[i] ;
		var conditionResult ; 
		
		try {
			conditionResult = eval(curCondition);
		}catch(e) {
			alert("条件【" + condition + "】出现语法错误,错误原因："+e+"请联系管理员检查!") ;
			return ;
		}
		if(conditionResult) {
			if(name1Arr[i]) {
				sumValue += parseFloat(name1Arr[i].value ? name1Arr[i].value : 0) ;
			}
		}else {
			if(name2Arr[i]) {
				sumValue += parseFloat(name2Arr[i].value ? name2Arr[i].value : 0) ;
			}
		}
	}
	return sumValue ;
}

function mt_form_rowValue(name,obj){
	if(!obj) return ; 
	var srcElement = obj ;  
	
	var trObj = srcElement.parentNode.parentNode ;
	var trElement = Ext.fly(trObj) ;
	var curRowObj = trElement.child('input[name='+name+']',true) ;
	var value = 0.00;
	if(curRowObj) {
		value = curRowObj.value ;
	}
	if(!value) value = 0.00;
	return parseFloat(value) ;
}


function mt_form_initSubmit() {
	var formArray = Ext.query("form") ;
	
	Ext.each(formArray,function(form){
		form.tempSubmit = form.submit ;
		form.submit = function (){
			showWaiting();
			form.tempSubmit();
		};	 
	});
}

function mt_form_checkState(stateField) {
	var data = mt_form_getRowValues();
	if(!data) {
		return false;
	}

	for(var i=0; i < data.length; i++) {
		var state = eval("data[" + i + "]." + stateField);
		if(state != '草稿' && state != '退件') {
			alert("该数据状态为[" + state  + "],不允许操作该数据!!");
			return false;
		} 
	}
   
	return true;
}

function mt_form_saveUrl(){
	
	Ext.Ajax.request({
        url : 'formDefine.do'
        ,method:'post',
        params:{
            method:'saveUrl'
            ,url:window.location.href
            }

        ,success : function(response, options) {}
	});
	return true;
}

Ext.onReady(function(){
	mt_form_initReadonly();
	mt_form_initSubmit();
});