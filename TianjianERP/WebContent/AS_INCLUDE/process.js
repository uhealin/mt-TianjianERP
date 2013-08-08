function mt_process_viewImage(tableId) {
		
	var id = document.getElementById("chooseValue_"+tableId).value;
	
	if(id == "") {
		alert("请选择需要查看流程图的记录!") ;
		return ;
	}
	
	var trValue = document.getElementById("trValueId_"+id);
	
	var pdId = trValue.pdid ;
	var pId = trValue.pid ;
	var pkey = trValue.pkey ;
	
	if(pdId == "") {
		alert("流程尚未发布,不能显示流程图!") ;
		return ;
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT+"/process.do?method=viewImageByPIdOrKey&key=" + pkey + "&id="+pId;
	
	var tab = parent.mainTab ;
    if(tab){
		n = tab.add({    
			'title':"流程图",    
			closable:true,  //通过html载入目标页    
			html:'<iframe name="imageFrm" scrolling="auto" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'   
		}); 
        tab.setActiveTab(n);
	}else {
		window.open(url);
	}	
	
}

function mt_process_view(pId,pName,viewUuid){
	var url = MATECH_SYSTEM_WEB_ROOT+'/process.do?method=processTransfer&view=true&pId='+ pId + "&uuid=" + viewUuid ;
	var n = parent.mainTab.add({     
		'title':pName,  
		 closable:true,  //通过html载入目标页    
		 html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
	});    
	parent.mainTab.setActiveTab(n);  
}

function startProByPkey(pKey) {
	if(mt_form_saveUrl()){
	window.location = MATECH_SYSTEM_WEB_ROOT + "/process.do?method=processTransfer&pKey=" + pKey;
    }
}

//编辑
function mt_process_edit(pKey) {
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
	
	window.location = MATECH_SYSTEM_WEB_ROOT + "/process.do?method=processTransfer&pKey=" + pKey + "&uuid="+value; 
}


//检查状态
function mt_process_checkState(pKey,uuid) { 
	
	window.location = MATECH_SYSTEM_WEB_ROOT + "/process.do?method=processTransfer&pKey=" + pKey + "&uuid="+value; 
}