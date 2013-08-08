Ext.namespace("Ext.matech.form");  
Ext.matech.form.listViewDialog = Ext.extend(Ext.Window , {
	
	id:'matech_listView_window',
	title: '列表控件属性',
	width: 800,
	height:450,  
    closeAction:'hide',
    contentEl:'listViewAttr',
    element:null,
    autoScroll:true,
    maximizable:true,
    listeners:{
		'hide':{fn: function () {
			 document.getElementById("listViewAttr").style.display = "none";
		}}
	},
    layout:'fit',
    modal:true,
    buttons:[{
        text:'确定',
      	handler:function() {
      		var win = Ext.getCmp("matech_listView_window") ;
      		var serialHtml = win.serialize();
      		var element = win.getElement() ;
      		if(element) {
      			//修改
      			var newElement = CKEDITOR.dom.element.createFromHtml(serialHtml);
      			element.setAttribute( 'matech_ext',newElement.getAttribute("matech_ext") ) ;
      			element.setAttribute( 'value',newElement.getAttribute("value") ) ;
      		}else {
      			win.editor.insertHtml(serialHtml) ;
      		}  
      		win.hide();
      	}
    },{
        text:'取消',
        handler:function(e){
        	var win = Ext.getCmp("matech_listView_window") ;
        	win.hide();
        }
    }],
    
    show:function(){
    	document.getElementById("listViewAttr").style.display = "";
    	Ext.matech.form.inputDialog.superclass.show.call(this) ;
    },
    
    setElement:function(element){
    	this.element = element ;
    },
    
    getElement:function(){
    	return this.element ;
    },
    
    serialize:function(){
    	
    	
    	var listHtml = "" ; //最终生成的html 
    	var columnWidth = "" ;
    	var columnList = "" ;
    	for(var i=0;i<listView_name.length;i++) {
    		var srcElement = listView_name[i] ;
    		var column = "ext_column={" ;
    		var trObj = srcElement.parentNode.parentNode ;
    		var trElement = Ext.fly(trObj) ;
    		
    		for(var j=0;j<mt_formDesign_listColumnObj.length;j++) {
    			var name = mt_formDesign_listColumnObj[j].name ;
    			var key = mt_formDesign_listColumnObj[j].key ;
    			
    			var curRowObj = trElement.child('input[name='+name+']',true) ;
    			var value = curRowObj.value ;
    			
    			//一些特殊处理
    			if(key == "ext_name") {
    				column += "ext_id=" + value + "," ;
    				column += key + "=" + value + "," ;
    			}else if(key == "ext_width") {
    				
    				if(value){
    	    			if(value.indexOf("%") > -1) {
    	    				columnWidth += value + "`" ;
    	    			}else {
    	    				columnWidth += value + "%`" ;
    	    			}
    	    			column += key + "=" + value + "," ;
    	    		} 
    			}else if(key == "ext_other"){
    				if(value) column += value + "," ;
    			}else {
    				if(value) column += key + "=" + value + "," ;
    			}
    		}
    		
    		if(column) column = column.substr(0,column.length - 1) ;
    		column += "};" ;
    		columnList += column ;
    	}
    	
    	var otherAttr = "" ;
    	var ext_selectList = "ext_selectList={" ;
    	var ext_selectAttr = "" ;
    	
    	
    	for(var k=0;k<mt_formDesign_listArr.length;k++) {
    		var record = mt_formDesign_listArr[k] ;
    		
    		var name = record.get("name") ;
			var key = record.get("key") ;
			var group = record.get("group") ;
			var value = Ext.getDom(name).value ; 
			
			if(group == "ext_selectList") {
				if(key == "ext_listOther") {
					ext_selectAttr += value + "," ;
				}else {
					if(value)
						ext_selectAttr += key + "=" + value + "," ; 
				}
			}else {
				if(value)
					otherAttr += key + "=" + value + ";" ;
			}
			
    	}
    	
    	if(ext_selectAttr) ext_selectAttr = ext_selectAttr.substr(0,ext_selectAttr.length - 1) ;
    	ext_selectList = ext_selectList + ext_selectAttr + "};" ;;
    	
    	if(columnWidth) columnWidth = columnWidth.substr(0,columnWidth.length - 1) ;
    	var listView_head_select = Ext.getDom("listView_head_select").value ;
    	
    	
    	listHtml += "<input value='{列表控件}' matech_ext='ext_type=sublist;" + otherAttr ;
    	
    	if(listView_head_select) {
    		listHtml += ext_selectList ;
    	}
    	if(columnWidth) {
    		listHtml += "ext_columnWidth=" + columnWidth + ";" ;
    	}
    	
    	listHtml += columnList ;
    	listHtml += " ext_end'/>"; 
    	return listHtml ;
    },
    parse:function (attr) {
    	if(attr) {
    		var attrArr = attr.split(";") ;
    		var ext_columnWidth ;
    		var widthCount = 0 ;
    		for(var k=0;k<attrArr.length;k++) {
    			
    			var attrStr = attrArr[k].replace("=","`~") ;
    			var keyValue = attrStr.split("`~") ;
    			
    			var key = keyValue[0] ;
    			var value = keyValue[1] ;
    			
    			if(value) {
    				key = key.replace("\r\n","") ; //替换换行符
    				key = key.replace(/\s+/g,"") ; //替换空格
    				
    				
	    			if(key =="ext_selectList"){
	    				
	    				value = value.replace(/[\s]+/gi,"") ; 
	    				value = value.substr(1,value.length - 2) ; //去掉两边大括号,
	    				
	    				var valueArr = value.split(",") ;
	    				
	    				var otherArr = "" ;
	    				for(var j=0;j<valueArr.length;j++) {
	    					var selectkeyValue = valueArr[j].split("=") ;
	    					var selectKey = selectkeyValue[0] ;
	    					var selectValue = selectkeyValue[1] ;
	    					
	    					var name = "" ; 
	    					for(var i=0;i<mt_formDesign_listArr.length;i++) {
		    					var record = mt_formDesign_listArr[i] ;
		    					if(record && selectKey == record.get("key")) {
		    						name = record.get("name") ;
		    					}
		    				}
		    				if(Ext.get(name)) {
		    					Ext.get(name).dom.value  = selectValue ;
		    				}else {
		    					otherArr += valueArr[j] + "," ;
		    				}
    		  			
	    				}  
	    				if(otherArr) {
	    					otherArr = otherArr.substr(0,otherArr.length - 1) ;
	    					Ext.get("ext_listOther").dom.value = otherArr ;
	    				}
	    				
	    			}else if(key =="ext_columnWidth"){
	    				if(value){
	    					ext_columnWidth = value.split("`") ;
	    				}
	    				
	    			}else if(key =="ext_column"){
	    				var colWidth = ext_columnWidth[widthCount] || "" ;
	    				mt_table_addLine(value,colWidth) ;
	    				widthCount ++ ;
	    			}else if(key == "ext_end"){
	    				
	    			}else {
	    				var name = "" ; 
	    				for(var i=0;i<mt_formDesign_listArr.length;i++) {
	    					var record = mt_formDesign_listArr[i] ;
	    					if(record && key == record.get("key")) {
	    						name = record.get("name") ;
	    					}
	    				}
	    				
	    				if(Ext.getCmp(name)) {
	    					Ext.getCmp(name).setRealValue(value) ;
	    				}else if(Ext.get(name)){
	    					Ext.get(name).dom.value  = value ;
	    				}
	    					
	    			}
    			}
    			
    		}
    	}
    	
    	initCombox();
    },
    clear:function(){
    	for(var k=0;k<mt_formDesign_listArr.length;k++) {
    		var record = mt_formDesign_listArr[k] ;
    		
    		var name = record.get("name") ;
			if(Ext.get(name)) Ext.get(name).dom.value = "" ;
			
    	}
    	
    	deleteAllRow() ;
    }
    
}
);
Ext.reg('listViewDialog',Ext.matech.form.listViewDialog);



