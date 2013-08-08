Ext.namespace("Ext.matech.form");  

var TEMP_SYSTEM_WEB_ROOT = "/sdcszj";

if(MATECH_SYSTEM_WEB_ROOT && MATECH_SYSTEM_WEB_ROOT!="") {
	TEMP_SYSTEM_WEB_ROOT = MATECH_SYSTEM_WEB_ROOT;
}

var DEFAULT_REFRESHURL = TEMP_SYSTEM_WEB_ROOT + "/system.do?method=combox";

var selectUrl = DEFAULT_REFRESHURL;
//var selectAttributeName = "comboxId" ;
var selectAttributeName = "autoid" ;
Ext.matech.form.mtCombox = Ext.extend(Ext.form.ComboBox, {
		triggerAction : 'all',
		displayField : 'text',
		valueField : 'value',
		loadingText : '正在加载数据...',
		lazyInit: true,// 控件获得焦点时才会初始化下拉框包括树
		typeAhead : false,
		resizable : true,
		checkField:'checked',
		queryParam : 'pk1',
		anchor : '100%',
		minChars:1,  //输入几个字符开始搜索 
		selectOnFocus:true,
		minListWidth:200, 
		valueNotFoundText:"",
		listeners : {   
		    'beforequery':function(e){   
		        var combo = e.combo; 
		        
		        var refer = combo.refer ;
			  	var refer1 = combo.refer1 ;
			  	var refer2 = combo.refer2 ;
			  	
			  	
			  	var referObj = document.getElementById(refer) ;
			  	var refer1Obj = document.getElementById(refer1) ;
			  	var refer2Obj = document.getElementById(refer2) ;
			  	
			  	var referValue = "";
			  	var refer1Value = "";
			  	var refer2Value = "";
			  	
			  	if(referObj) {
			  		referValue = referObj.value ; 
			  	}else {
			  		referValue = refer ; 
			  	}
			  	
			  	if(refer1Obj) {
			  		refer1Value = refer1Obj.value ; 
			  	}else {
			  		refer1Value = refer1 ; 
			  	}
			  	
			  	if(refer2Obj) {
			  		refer2Value = refer2Obj.value ; 
			  	}else {
			  		refer2Value = refer2 ; 
			  	}
			  	
			  	if(refer && !referValue ) {
			  		alert("请先录入或选择【" + (referObj.title ? referObj.title : referObj.id) + "】");
			  		return false ;
			  	}
			  	
			  	if(refer1 && !refer1Value) {
			  		alert("请先录入或选择【" + (refer1Obj.title ? refer1Obj.title : refer1Obj.id) + "】");
			  		return false ;
			  	}
			  	
			  	if(refer2 && !refer2Value) {
			  		alert("请先录入或选择【" + (refer2Obj.title ? refer2Obj.title : refer2Obj.id) + "】");
			  		return false ;
			  	}
			 
			  	combo.store.baseParams["refer"] = referValue;
			  	combo.store.baseParams["refer1"] = refer1Value;
			  	combo.store.baseParams["refer2"] = refer2Value;
			  	
		        var input = e.query;
		        if(combo.grid) {
		        	//过滤表格
		        	var grid = Ext.getCmp("mt_combox_grid_"+ combo.id + "_" +combo.autoid) ;
		        	if(!grid) return ;
		        	var start = combo.multiselect ? '' : 0 ;
		        	var limit = combo.multiselect ? '' : 50 ;
		        	grid.store.load({
						params:{grid:this.grid,autoid:this.autoid,start:start,limit:limit,pk1:input}
					});
		        	
		        	combo.expand(); 
		        	return false ; 
		        }else if(combo.multilevel) {
		        	//树状下拉,要使用树的过滤
		        	combo.filter.filter(input);
					combo.expand(); 
					return false ; 
		        }else {
		             if(this.mode == 'local'){
	                    combo.selectedIndex = -1;
	                    if(e.forceAll){
	                        combo.store.clearFilter();
	                    }else{
	                        combo.store.filter(this.displayField, input);
	                    }
	                    combo.onLoad();
	                }else{
	                    combo.store.baseParams[this.queryParam] = input;
	                    combo.store.load({
	                     //   params: this.getParams(input),
	                    	params: this.getParams(input),
	                        callback :function (r,options,success){
	                        	if(combo.store.getTotalCount()<=50) {
	                        		//记录不足两页就不显示分页了
	                        	//	if(combo.footer.isVisible()){
	                        			//combo.assetHeight = combo.assetHeight - combo.footer.getHeight();
	                        	//	}
	                        		combo.footer.hide() ;
	                        		combo.restrictHeight();
	                        	}else {
	                        		combo.footer.show() ;
	                        		combo.restrictHeight();
	                        	}
	                        	
	                        	if(combo.multiselect) {
	                        		//var value = ","+combo.getValue()+",";
	                        		var value = combo.separator+Ext.get(combo.hiddenId).dom.value+combo.separator;
	                        		
	                        		combo.store.each(function(r) {
	                        			if(value.indexOf(combo.separator+r.data["value"]+combo.separator) > -1) {
	                        				r.set(combo.checkField, true);
	                        			}
	                        		}, combo);
	                        	}
	                        }
	                    });
	                    combo.expand();
	                } 
		            /*
		            combo.store.filterBy(function(record,id){     
		                // 得到每个record的项目名称值
		                var text = record.get(combo.displayField);    
		                return text.indexOf(input) != -1 ; 
		            });   */
		            return false;     
	        	} 
		    }
		},   
	  
	  // ext的下面这个方法可能有bug 所有重写了
	  onViewClick : function(doFocus) {  
	        var index = this.view.getSelectedIndexes()[0], s = this.store, r = s.getAt(index);   
	        if (r) {   
	          this.onSelect(r, index);   
	        } else if (s.getCount() === 0) {   
	          this.collapse();   
	        }   
	        if (doFocus !== false) {   
	          this.el.focus();   
	        } 
      },
      
      onLoad : function(){
          if(!this.hasFocus){
              return;
          }
          if(this.store.getCount() > 0 || this.listEmptyText){
              this.expand();
              this.restrictHeight();
              if(this.lastQuery == this.allQuery){
                  if(this.editable){
                      this.el.dom.select();
                  }

                  if(this.autoSelect !== false && !this.selectByValue(this.value, true)){
                      this.select(0, true);
                  }
              }else{
                  if(this.autoSelect !== false){
                      this.selectNext();
                  }
                  if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                      this.taTask.delay(this.typeAheadDelay);
                  }
              }
          }else{
              //this.collapse();
          }

      },
      
      initReferParam: function () {
    	  
    	  	var param = new Array(3);
    	  //alert(this.id);
    	  	var refer = this.refer;
		  	var refer1 = this.refer1;
		  	var refer2 = this.refer2;
		  	
		  	var referObj = document.getElementById(refer) ;
		  	var refer1Obj = document.getElementById(refer1) ;
		  	var refer2Obj = document.getElementById(refer2) ;
		  	
		  	var referValue = "";
		  	var refer1Value = "";
		  	var refer2Value = "";
		  	
		  	if(referObj) {
		  		param[0] = referObj.value ; 
		  	}else {
		  		param[0] = refer ; 
		  	}
		  	
		  	if(refer1Obj) {
		  		param[1] = refer1Obj.value ; 
		  	}else {
		  		param[1] = refer1 ; 
		  	}
		  	
		  	if(refer2Obj) {
		  		param[2] = refer2Obj.value ; 
		  	}else {
		  		param[2] = refer2 ; 
		  	} 
		  	
		  	return param ;
      },
      
      // 重写这个方法，支持上下左右定位选择
       initEvents : function(){
       		Ext.matech.form.mtCombox.superclass.initEvents.call(this);
       		
	        this.keyNav = new Ext.KeyNav(this.el, {    
	            "up" : this.onKeyDown,    
	            "down" : function(e){    
	                if (!this.isExpanded()) {    
	                    this.onTriggerClick();    
	                }    
	                else {    
	                    this.onKeyDown(e);    
	                }    
	            },    
	            "left":this.onKeyDown,    
	            "right":this.onKeyDown, 
	            /*
	            "enter":function(){    
	                var node = this.tree.selModel.getSelectedNode();    
	                this.onTreeClick(node);    
	            },   */
	            "esc" : function(e){    
	                this.collapse();    
	            },    
	            "tab" : function(e){    
	                this.collapse();    
	                return true;    
	            },    
	            scope : this,     
	            forceKeyDown : true 
	        });    
	        this.dqTask = new Ext.util.DelayedTask(this.initQuery, this);    
	        if(!this.enableKeyEvents){    
	            this.mon(this.el, 'keyup', this.onKeyUp, this);    
	        }    
	   },
	   
	   getCheckedDisplay:function() {   
	        var re = new RegExp(this.separator, "g");   
	        return this.getCheckedValue(this.displayField).replace(re, this.separator + ' ');   
	   },  
	   
	   getCheckedValue:function(field) {   
	        field = field || this.valueField;   
	        var c = [];   
	        var snapshot = this.store.snapshot || this.store.data;   
	  
	        snapshot.each(function(r) {   
	            if(r.get(this.checkField)) {   
	                c.push(r.get(field));   
	            }   
	        }, this);   
	  
	        return c.join(this.separator);   
	    },
	    
		
	  initComponent : function() {
	  	
	 // 	var queryParam = this.initReferParam() ; 
	  	var queryParam;
	  	
	  	if(this.multilevel || this.grid) {
	  		queryParam = this.initReferParam() ; 
	  	}
	  	
	  	var url = selectUrl;	
	  	
	  	if(this.grid) {
	  		
	  		this.store = new Ext.data.SimpleStore({
				fields : [],
				data : [[]]
		 	}) ;
		 	
		 	this.mode = 'local' ;
		 	this.resizable = false ;  
	  		this.tpl = "<tpl for='.'><div style='height:300px' id='grid"+this.id+"'></div></tpl>" ;
	  		var gridPanel ;
	  		var comboObj = this ;
	  		var clickRecord ;
	  		var value = "" ;
			var text = "" ;  
			var jsonStore ;
			
			var start = this.multiselect ? '' : 0 ;
			var limit = this.multiselect ? '' : 50 ;
	  		
	  		Ext.Ajax.request({
					method: 'POST',
					url: url,
					success: function(response) {
						
						if(response.responseText.indexOf("ERROR") > -1) {
							alert(response.responseText.replace("ERROR",""));
							return ;
						}
						var obj = Ext.util.JSON.decode(response.responseText);
						
						var columns = eval(obj[0].columns) ;
						var fields = eval(obj[0].fields) ;
						
						
						if(comboObj.multiselect) {
							
							var chooseArr = [{id:'choose',header:'<input style="text-align:left" type="checkbox" onclick="mt_select_selectAll(\'' + comboObj.autoid + '\',\'' + comboObj.id + '\',this)" />',
											width:30,menuDisabled:true,sortable:false,width:50,dataIndex:columns[0].dataIndex,renderer:function(v,params,record,rowIndex){
								var group = record.data.hidden_select_group ||  record.data.select_group; //是否同组，同组时可以一起选中
								var groupStr = "" ;
								if(group) groupStr = " group="+group ;
								return '<input type=checkbox '+groupStr+' onclick=mt_select_groupSelect(\'' + comboObj.autoid + '\',\'' + comboObj.id + '\',this) value="'+v+'" name="mt_gridMultiSelect_' + comboObj.id + "_" + comboObj.autoid + '" rowIndex="' + rowIndex + '">' ;
							}}] ;
							columns = chooseArr.concat(columns);
						}   
						if(fields.length > 0) {
							value = fields[0].name ;
							text = fields[1].name ;
						}
						
						jsonStore = new Ext.data.JsonStore({ 
							fields: fields,
					 		url: url,
							root: 'data',
							totalProperty:'totalProperty',
							remoteSort:false 
						});
						
						jsonStore.on('beforeload', function(store) {
							queryParam = comboObj.initReferParam();
							//alert(queryParam[0]);
							jsonStore.baseParams={grid:comboObj.grid,autoid:comboObj.autoid,refer:queryParam[0],refer1:queryParam[1],refer2:queryParam[2]};
						}); 
						
						var bbar = null ;
						if(!comboObj.multiselect) {
							bbar = new Ext.PagingToolbar({
								store:jsonStore,
								pageSize: 50,  
								emptyMsg: "没有记录"
						  	}) ;
						}
						
						gridPanel = new Ext.grid.GridPanel({
							frame:true,  
							id:"mt_combox_grid_"+ comboObj.id + "_" +comboObj.autoid,
							store:jsonStore,
							columns:columns,
							sm :new Ext.grid.RowSelectionModel({singleSelect:false}),
							bbar:bbar
						}) ;
						
						gridPanel.on('rowclick', function(grid, rowIndex, e) {
							
							if(!comboObj.multiselect) {
								var selections = grid.getSelectionModel().getSelections();
						       	for (var i = 0; i < selections.length; i++) {
						            var record = selections[i];
						            
						            comboObj.value = record.get(value);  
					                comboObj.setRawValue(record.get(text)); 
					                
					                var columns = "" ; 
					                if(fields.length > 0) {
					                	columns += "{" ;
										for(var j=0;j<fields.length;j++) {
											columns += "'" + fields[j].name + "':" ;
											/*
											if(comboObj.gridFieldIndex == "false") {
												columns += "'" + fields[j].name + "':" ;
											} else {
												columns += "'" + fields[j].name + "_" + j + "':" ;
											}*/
											
											columns += "'" + record.get(fields[j].name) + "'," ;
										}
										columns = columns.substring(0, columns.length -1) ;
										columns += "}" ; 
									}
					                if(columns !="") {
					                	 Ext.get(comboObj.hiddenId).dom.columns = columns ;
					                	 Ext.get(comboObj.el.dom.id).dom.columns = columns ;  
					                }
					                
					                if(comboObj.hiddenId) {
					                	Ext.get(comboObj.hiddenId).dom.value = comboObj.value; 
					                }
					                
					                clickRecord = record ;
					                 
					                if(comboObj.onselect)   
					                	comboObj.onselect.call(Ext.get(comboObj.el.dom.id).dom,columns) ;
					                
					               
						       	}
						       	comboObj.collapse();
							}
					       	
					    });
						if(columns.length < 5) {
							gridPanel.getView().forceFit = true ;
						}
						comboObj.gridPanel = gridPanel ;
		 			},
					failure: function() {
		 				alert("后台获取数据失败");
		 			},
		 			params: {grid:this.grid,autoid:this.autoid,head:true,start:start,limit:limit,refer:queryParam[0],refer1:queryParam[1],refer2:queryParam[2]}
	 		});
	 		
	 		this.on('expand', function() {
	 			this.innerList.dom.style.overflowY="hidden";
	 			gridPanel.render('grid'+this.id); 
	 			gridPanel.store.load({
					params:{grid:this.grid,autoid:this.autoid,start:start,limit:limit},
					callback:function (){
						//勾选多选框 
			 			if(comboObj.value) {
			 				var valueArr = comboObj.value.split(",") ;
			 				var checkbox = document.getElementsByName("mt_gridMultiSelect_" + comboObj.id + "_" +comboObj.autoid) ;
			 				for(var k=0;k<checkbox.length;k++) {
			 					for(var y=0;y<valueArr.length;y++) {
			 						if(checkbox[k].value == valueArr[y]) {
			 							checkbox[k].checked = true ;
			 						}
			 					}
			 				}
			 			}
					}
				});
	 			
	 			this.restrictHeight();
	 			gridPanel.setWidth(this.listWidth*1);               
	 			gridPanel.setHeight(this.innerList.getHeight()*1) ;
	 			
			}); 
			
			this.onRealBlur = function() {   
				
				if(!this.multiselect) {
					this.list.hide();  
					if(!this.value && this.getRawValue()) {
						 this.value = this.getRawValue();   
		                 if(this.hiddenId) {
		                 	Ext.get(this.hiddenId).dom.value =  this.getRawValue(); 
		                 }
					 }
				}
		    } ;
		    
		    this.findRecord = function(prop, value){
		        var record;
		        if(!jsonStore) return ;
		        if(jsonStore.getCount() > 0){
		        	jsonStore.each(function(r){
		                if(r.data[prop] == value){
		                    record = r;
		                    return false;
		                }
		            });
		        }
		        return record;
		    } ;
		    
		    this.beforeBlur = function (){
		    	if(this.readOnly) return ;
		    	
	            var val = this.getRawValue(),
	                rec;

	            if(this.valueField && Ext.isDefined(this.value)){
	                rec = this.findRecord(value, this.value);
	            }
	            if(!rec || rec.get(text) != val){
	                rec = this.findRecord(text, val);
	            }
	            
	            if(!rec && this.forceSelection){
	                if(val.length > 0 && val != this.emptyText){
	                    this.el.dom.value = Ext.value(this.lastSelectionText, '');
	                    this.applyEmptyText();
	                }else{
	                    this.clearValue();
	                }
	            }else{
	                if(rec && value){
	                    if (this.value == val){
	                        return;
	                    }
	                    val = rec.get(value || text);
	                }
	                this.value = val;
	                if(this.hiddenId) {
	                 	Ext.get(this.hiddenId).dom.value = val; 
	                 }
	            }
		    	
		    } ;
		    
		    this.on({   
	             scope:this  
	            ,blur:this.onRealBlur
	        });
		    
			
		    this.girdMultiSelect = function (combo) {
		    	
		    	var choose = document.getElementsByName("mt_gridMultiSelect_"+ this.id + "_" +this.autoid) ;
		    	
		    	var grid = Ext.getCmp("mt_combox_grid_"+ comboObj.id + "_" +comboObj.autoid) ;
		    	if(!grid) return ;
    			var store = grid.store ;
    			
    			var fields = store.fields ;
    			
    			var json = "" ;
    			var columns = "" ; 
    			var value = "" ;
    			var text = "" ;
		    	for(var i=0;i<choose.length;i++) {
		    		
		    		if(choose[i].checked) {
		    			var rowIndex = choose[i].rowIndex ;
		    			var record = store.getAt(rowIndex) ;
		                 if(fields.length > 0) {
		                	columns += "{" ;
		                	//alert(typeof(fields)) ;
		                	fields.each(function(field,index){
		                		/*
		                		if(comboObj.gridFieldIndex == "false") {
									columns += "'" + field.name + "':" ;
								} else {
									columns += "'" + field.name + "_" + index + "':" ;
								}*/
		                		columns += "'" + field.name + "':" ;
								columns += "'" + record.get(field.name) + "'," ;
								
								if(index == 0) {
									value += record.get(field.name) + "," ;
								}else if(index == 1) {
									text += record.get(field.name) + "," ;
								}
		                	})  ;
							columns = columns.substring(0, columns.length -1) ;
							columns += "}," ; 
						}
		    		}
		    	}
		    	
		    	if(columns !="") {
		    		columns = columns.substring(0, columns.length -1) ;
		    		value = value.substring(0, value.length -1) ;
		    		text = text.substring(0, text.length -1) ;
		    		comboObj.value = value;  
	                comboObj.setRawValue(text); 
	                if(comboObj.hiddenId) {
	                	Ext.get(comboObj.hiddenId).dom.value = value; 
	                }
		    		
		    		json += "[" + columns + "]" ;
               	 	Ext.get(comboObj.hiddenId).dom.columns = json ;
               	 	Ext.get(comboObj.el.dom.id).dom.columns = json ;  
                }
		    	if(comboObj.onselect)   
                	comboObj.onselect.call(Ext.get(comboObj.el.dom.id).dom,json) ;
		    	
		    	
		    	comboObj.collapse();
		    } ;
		    
		    if(this.multiselect) {
		    	this.setValue = this.girdMultiSelect ;
		    }
		    	
	  		
	  	}else if(this.multilevel) {
	    	
			this.tpl = "<tpl for='.'><div style='height:300px'><div id='tree"+this.id+"'></div></div></tpl>" ;
	    	
	    	var root = new Ext.tree.AsyncTreeNode({   
	      		text :this.loadingText,  
	      		id : 'root',
	      		expanded : false
	    	});
	    	
	    	var treeLoader = new Ext.tree.TreeLoader({   
            	dataUrl : url,
            	autoLoad:false,
            	baseParams:{
            		autoid:this.autoid,
            		multilevel:this.multilevel,
            		multiselect:this.multiselect,
            		refer:queryParam[0],
            		refer1:queryParam[1],
            		refer2:queryParam[2],
            		pk1:Ext.get(this.hiddenId).dom.value
            	}
          	}) ; 
	    	var obj = this ;
	    	treeLoader.on('beforeload', function(store) {
				queryParam = obj.initReferParam();
				//alert(queryParam[0]);
				treeLoader.baseParams={multilevel:obj.multilevel,multiselect:obj.multiselect,autoid:obj.autoid,refer:queryParam[0],refer1:queryParam[1],refer2:queryParam[2]};
			}); 
	    	
		 	var tree = new Ext.tree.TreePanel({ 
				root :root,
				loader : treeLoader, 
				autoScroll : true,
				animate : false,
				useArrows : true,  
				rootVisible:true,  
				border : false  
	    	});
	    	
	    	tree.on('checkchange', function(node, checked) {   
				node.expand(false,false,function(){
					node.eachChild(function(child) {  
						child.ui.toggleCheck(checked);   
						child.attributes.checked = checked;   
						child.fireEvent('checkchange', child, checked);   
					}); 
				});   
				node.attributes.checked = checked; 
				  
			}, tree); 
	    	
			this.filter = new Ext.ux.form.TreeFilter(tree,{
				ignoreFolder:this.ignoreFolder,
				clearAction:'collapse'
			});
	    	
	    	
	    	if(this.multiselect) {
	    		
	    		this.getTreeCheckedValue = function () {
					var selects = tree.getChecked();
					var values = "" ;
					
					for(var i=0;i<selects.length;i++) {
						if(selects[i].isLeaf()) {
							values += selects[i].id + this.separator;  
						}
					}
					if(values != "") {
						values = values.substr(0,values.length-1) ;
					}
					return values ;
				} ;
				
				this.getTreeCheckedText = function () {
					var selects = tree.getChecked();
					var values = "" ;
					
					for(var i=0;i<selects.length;i++) {
						if(selects[i].isLeaf()) {
							values += selects[i].text + this.separator;  
						}
					}
					if(values != "") {
						values = values.substr(0,values.length-1) ;
					}
					return values ;
				} ;
				
				this.clearCheck = function () {
					var nodes = tree.getChecked();  
			        if (nodes && nodes.length) {  
			            for (var i = 0; i < nodes.length; i++) {  
			                //设置UI状态为未选中状态  
			                nodes[i].getUI().toggleCheck(false);  
			                //设置节点属性为未选中状态  
			                nodes[i].attributes.checked = false;  
			            }  
			        }  
				} ;
				
				 this.onRealBlur = function() {   
			         this.list.hide();  
	                 //如果没有选择数据，则保持现状
			         if(this.getTreeCheckedValue()==""){
			        	 return;
			         }
			         this.value = this.getTreeCheckedValue();   
			         this.setRawValue(this.getTreeCheckedText());   
	                 if(this.hiddenId) {
	                 	Ext.get(this.hiddenId).dom.value =  this.value; 
	                 }
	                 
			    } ;
			    
			    this.on({   
		             scope:this  
		            ,blur:this.onRealBlur   
		        });
	    		
	    	}else {
	    		var comboxObj = Ext.getCmp(this.id) ;
	    		
	    		var clickNode ;  //记录哪个结点被点击了 
	    		tree.on('click',function(node,event){
	    			 
		    		event.stopEvent();
		    		/*
		    		if (!node.isLeaf()){
		    			node.expand();
		    		    return ;
					}else { 
					   	// 叶子   
						clickNode = node ;
						comboxObj.setRawValue(node.text);  
						comboxObj.value = node.id ; 
		                 
		                if(comboxObj.hiddenId) {
		                   Ext.get(comboxObj.hiddenId).dom.value =  comboxObj.value ;  
		                }
		                
		          		comboxObj.collapse();  
					} 
					*/
		    		
		    		clickNode = node ;
					comboxObj.setRawValue(node.text);  
					comboxObj.value = node.id ; 
	                 
	                if(comboxObj.hiddenId) {
	                   Ext.get(comboxObj.hiddenId).dom.value =  comboxObj.value ;  
	                }
	                
	          		comboxObj.collapse();  
	   		  	});
	   		  	
	   		  	 //
	   		  	 this.onRealBlur = function() {
	   		  	 	this.list.hide();  
	   		  	 	if(clickNode) {
	   		  	 		//把最后点击节点的值赋到combox里面去
	   		  	 		 this.value = clickNode.id;   
		                 this.setRawValue(clickNode.text);   
		                 if(this.hiddenId) {
		                 	Ext.get(this.hiddenId).dom.value =  this.value; 
		                 }
	   		  	 	}
			    } ;
			    
			   this.beforeBlur = function (){
				 if(!this.value && this.getRawValue()) {
					 this.value = this.getRawValue();   
	                 if(this.hiddenId) {
	                 	Ext.get(this.hiddenId).dom.value =  this.getRawValue(); 
	                 }
				 }
			   } ;
	   		  	
	   		  	 this.on({   
		             scope:this  
		            ,blur:this.onRealBlur
		        });
		        
	    	}
	    	
			this.on('expand', function() { 
				//展开时才去加载树
				tree.root.reload(); 
				//Ext.get('tree'+this.id).parent().setHeight(20) ;
				tree.render('tree'+this.id); 
				var combo = this ;
				root.expand(false,true,function(){
					combo.restrictHeight();
					root.setText("");
				});
			}); 
			
			this.store = new Ext.data.SimpleStore({
				fields : [],
				data : [[]]
		 	}) ;
		 	
		 	this.mode = 'local' ;
    	    	
	    }else {
	    	 var jsonStore = new Ext.data.JsonStore({
		    	url:url,    
	            root:'data',    
	            totalProperty: 'totalProperty',    
	            remoteSort: true,    
	            fields:['value','text'],
				baseParams : {
					autoid : this.autoid
				}
			});
			
	        this.mode = 'remote' ;
			this.store = jsonStore ;
			this.pageSize = 100 ;
			
			if(this.multiselect) { 
				
				 if(!this.tpl) {
		            this.tpl =      
		                 '<tpl for=".">'  
		                +'<div class="x-combo-list-item">'  
		                +'<img src="' + Ext.BLANK_IMAGE_URL + '" '  
		                +'class="ux-lovcombo-icon ux-lovcombo-icon-'  
		                +'{[values.' + this.checkField + '?"checked":"unchecked"' + ']}">'  
		                +'<span class="ux-lovcombo-item-text">{' + (this.displayField || 'text' )+ '}</span>'  
		                +'</div>'  
		                +'</tpl>'   
		            ;   
		        }
		        
		         this.onSelect = function(record, index) {  
		        	 
			        if(this.fireEvent('beforeselect', this, record, index) !== false){   
			            // toggle checked field
			            record.set(this.checkField, !record.get(this.checkField));   
			  
			            // display full list
			            if(this.store.isFiltered()) {   
			                this.doQuery(this.allQuery);   
			            }   
			            // set (update) value and fire event
			            this.setValue(this.getCheckedValue());   
			            this.fireEvent('select', this, record, index);   
			        }
			        
					if(this.onselect)   
						this.onselect.call(Ext.get(this.el.dom.id).dom) ;
			    } ;
			    
			    this.setValue = function(v) {   
			        if(v) {
			            v = '' + v;   
			            if(this.valueField) {   
			                this.store.clearFilter();   
			                this.store.each(function(r) {
			                    var checked = !(!v.match(   
			                         '(^|' + this.separator + ')' + RegExp.escape(r.get(this.valueField))   
			                        +'(' + this.separator + '|$)'))   
			                    ;   
			  
			                    r.set(this.checkField, checked);   
			                }, this);   
			                this.value = this.getCheckedValue();   
			                this.setRawValue(this.getCheckedDisplay());   
			                if(this.hiddenId) {
			                    Ext.get(this.hiddenId).dom.value =  v ;
			                }   
			            }   
			            else {   
			                this.value = v;   
			                this.setRawValue(v);   
			                if(this.hiddenId) {
			                	Ext.get(this.hiddenId).dom.value =  v ;
			                }   
			            }   
			            if(this.el) {   
			                this.el.removeClass(this.emptyClass);   
			            }   
			        }   
			        else {   
			            this.clearValue();   
			        }   
			    } ;
			    
			    this.beforeBlur = function(){
			    	if(this.readOnly) return ;
				    var val = this.getRawValue();  
				    if(this.forceSelection){
				        if(val.length > 0 && val != this.emptyText){  
				           this.el.dom.value = Ext.isDefined(this.lastSelectionText) ? this.lastSelectionText : '';  
				            this.applyEmptyText();  
				        }else{  
				            this.clearValue();  
				        }  
				    }else{  
			            var texts = val.split(this.separator);  
			            var values='';  
			            for(var i=0;i<texts.length;i++){  
		                    var rec = this.findRecord(this.displayField, texts[i].trim());  
		                 	if(rec){  
		                        values+=(values.length>0?this.separator:'')+rec.data[this.valueField];  
		                    }  
		                }  
				        this.setValue(values);  
				    }  
				} ;
				
				this.clearCheckValue = function(){
			        this.value = '';  
			        this.setRawValue(this.value);  
			        this.store.clearFilter();  
			        this.store.each(function(r) {  
			            r.set(this.checkField, false);  
			        }, this);  
			        if(this.hiddenField) {  
			            this.hiddenField.value = '';  
			        }  
			        this.applyEmptyText();  
				} ;
			    
			    this.onRealBlur = function() {
			    	if(this.readOnly) return ;
			        this.list.hide();   
			        var rv = this.getRawValue();
			        var rva = rv.split(new RegExp(RegExp.escape(this.separator) + ' *'));   
			        var va = [];   
			        var snapshot = this.store.snapshot || this.store.data;   
			        Ext.each(rva, function(v) {   
			            snapshot.each(function(r) {
			                if(v === r.get(this.displayField)) {
			                    va.push(r.get(this.valueField));   
			                }   
			            }, this);   
			        }, this);
			        this.setValue(va.join(this.separator));   
			        this.store.clearFilter();   
			    } ;
			    
			    this.on({   
		             scope:this  
		            ,blur:this.onRealBlur   
		        });
			}else {
				//普通下拉，可以通过重写onSelect实现选择一个元素后的东西
				
				this.onSelect = function(record, index) {
					
					if(this.fireEvent('beforeselect', this, record, index) !== false){
			            this.setValue(record.data[this.valueField || this.displayField]);
			            this.collapse();
			            this.fireEvent('select', this, record, index);
			        }   
					
					if(this.onselect)   
						this.onselect.call(Ext.get(this.el.dom.id).dom) ;
			    } ;
			    
			    this.beforeBlur = function (){
			    	if(this.readOnly) return ;
			    	if(this.store.getCount() <=0 && this.startRawValue == this.getRawValue()) {
			    		return ;
			    	}else {
			    		this.assertValue();
			    	}
			    } ;
			    
			}
	    }  
	    Ext.matech.form.mtCombox.superclass.initComponent.call(this);  
	  },
	  
	  initList : function () {
	  	
        if(!this.list){
            var cls = 'x-combo-list',
                listParent = Ext.getDom(this.getListParent() || Ext.getBody()),
                zindex = parseInt(Ext.fly(listParent).getStyle('z-index'), 10);

            if (!zindex) {
                zindex = this.getParentZIndex();
            }

            this.list = new Ext.Layer({
                parentEl: listParent,
                shadow: this.shadow,
                cls: [cls, this.listClass].join(' '),
                constrain:false,
                zindex: (zindex || 12000) + 5
            });

            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
            this.list.setSize(lw, 0);
            this.list.swallowEvent('mousewheel');
            this.assetHeight = 0;
            if(this.syncFont !== false){
                this.list.setStyle('font-size', this.el.getStyle('font-size'));
            }
            
            if(this.title){
                this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
                this.assetHeight += this.header.getHeight();
            }
            
            var combo = this ;
	  		this.tbar = new Ext.Toolbar({
		           items:[{
					text:'清空',
			   		icon:TEMP_SYSTEM_WEB_ROOT + '/img/delete.gif',
			   		 handler:function(){
			   			combo.clear();
			   		 }
				},'-'
		        ]
		    });  
	  		
	  		if(this.grid && this.multiselect) {
	  			this.tbar.add({
					text:'确定多选',
			   		icon:TEMP_SYSTEM_WEB_ROOT + '/img/selectall.gif',
			   		 handler:function(){
			   			combo.girdMultiSelect(combo) ;
			   		 }
				},'-') ;
	  			this.tbar.doLayout() ;
	  		}
	  	   
		  	 if (this.tbar) {
	            var tbar = this.list.createChild();
	            this.tbar.render(tbar);
	            
	            this.assetHeight += tbar.getHeight();
	            var lw = this.listWidth ||  Math.max(this.list.getWidth(),tbar.getWidth());
	            this.list.setWidth(lw);
	        }

            this.innerList = this.list.createChild({cls:cls+'-inner'});
            this.mon(this.innerList, 'mouseover', this.onViewOver, this);
            this.mon(this.innerList, 'mousemove', this.onViewMove, this);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));
			
           
            if(this.pageSize){
                this.footer = this.list.createChild({cls:cls+'-ft'});
                this.pageTb = new Ext.PagingToolbar({
                    store: this.store,
                    pageSize: this.pageSize,
                    renderTo:this.footer
                });
                this.assetHeight += this.footer.getHeight();
            }

            if(!this.tpl){
                this.tpl = '<tpl for="."><div class="'+cls+'-item">{' + this.displayField + '}</div></tpl>';
            }

            this.view = new Ext.DataView({
                applyTo: this.innerList,
                tpl: this.tpl,
                singleSelect: true,
                selectedClass: this.selectedClass,
                itemSelector: this.itemSelector || '.' + cls + '-item',
                emptyText: this.listEmptyText,
                deferEmptyText: false
            });

            this.mon(this.view, {
                containerclick : this.onViewClick,
                click : this.onViewClick,
                scope :this
            });
	
            this.bindStore(this.store, true);
            if(this.resizable){
                this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
                this.mon(this.resizer, 'resize', function(r, w, h){
                    this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                    this.listWidth = w;
                    this.innerList.setWidth(w - this.list.getFrameWidth('lr'));
                    this.restrictHeight();
                }, this);

                this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
            }
        }
	  
	 },
	 
	 restrictHeight : function(){
	        this.innerList.dom.style.height = '';
	        var inner = this.innerList.dom,
	            pad = this.list.getFrameWidth('tb') + (this.resizable ? this.handleHeight : 0) + this.assetHeight,
	            h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight),
	            ha = this.getPosition()[1]-Ext.getBody().getScroll().top,
	            hb = Ext.lib.Dom.getViewHeight()-ha-this.getSize().height,
	            space = Math.max(ha, hb, this.minHeight || 0)-this.list.shadowOffset-pad-5;
	        h = Math.min(h, space, this.maxHeight);
	        
	        this.innerList.setHeight(h);
	        this.list.beginUpdate();
	        this.list.setHeight(h+pad);
	        this.list.alignTo.apply(this.list, [this.el].concat(this.listAlign));
	        this.list.endUpdate();
	       
	    },
	    
	 expand : function(){
        if(this.isExpanded() || !this.hasFocus){
            return; 
        }

        if(this.title || this.pageSize || this.tbar){
            this.assetHeight = 0;
            if(this.title){
                this.assetHeight += this.header.getHeight();
            }
            if(this.pageSize){
                this.assetHeight += this.footer.getHeight();
            }
            if(this.tbar) {
            	this.assetHeight += this.tbar.getHeight(); 
            }
        }

        if(this.bufferSize){
            this.doResize(this.bufferSize);
            delete this.bufferSize;
        }
        this.list.alignTo.apply(this.list, [this.el].concat(this.listAlign));

        var listParent = Ext.getDom(this.getListParent() || Ext.getBody()),
            zindex = parseInt(Ext.fly(listParent).getStyle('z-index') ,10);
        if (!zindex){
            zindex = this.getParentZIndex();
        }
        if (zindex) {
            this.list.setZIndex(zindex + 5);
        }
        
        this.list.show(); 
        
        if(Ext.isGecko2){
            this.innerList.setOverflow('auto'); // necessary for FF 2.0/Mac
        }
        this.mon(Ext.getDoc(), {
            scope: this,
            mousewheel: this.collapseIf,
            mousedown: this.collapseIf
        });
        this.fireEvent('expand', this);
    },
    clear : function () {
    	if(this.multiselect && !this.grid) {
    		if(this.multilevel) {
    			//去掉树结点选择并清空combox
    			this.clearCheck() ;
    			this.clearValue();
    		}else {
    			this.clearCheckValue();
    		}
    	}else {
    		this.clearValue();
    	}
    }
	
});


Ext.ux.form.TreeFilter = function(tree,config){
	this.tree = tree;
	Ext.apply(this,config||{});
	this.pyCache = {};
	this.matches = [];
	this.lastQuery = '';
	this.cleared = true;
} ;

Ext.ux.form.TreeFilter.prototype = {
	ignoreFolder:true,
	clearAction:undefined,
	
	filter:function(value, attr , startNode){
		if(value == this.lastQuery){
			return;
		}
		value = value.trim();
		this.lastQuery = value;
		if(value.length == 0){
			this.clear(startNode);
			return;
		}else 
		startNode = startNode||this.tree.root;
		var reg = new RegExp('('+value+')');
		var fn = function(n){
			return reg.test(n.attributes["text"]) || reg.test(n.attributes["id"]);
		};
		this.filterAll(fn,startNode);
		this.showPaths();
		this.cleared = false;
	},
	
	//private get the matchs and hide other nodes
	filterAll:function(fn,startNode){
		var arr = [];
		startNode = startNode||this.tree.root;				
		startNode.cascade(function(n){
			if(!n.leaf){
				n.expand(false,false);
				if (this.ignoreFolder) {
					n.ui.hide();
					return;
				}	
			}
			if(fn.call(this,n)){
				arr.push(n); 	
			}else{
				n.ui.hide();
			}
		},this);
		this.matches = arr;
	},
	//显示匹配的父节点路径
	showPaths:function(){
		Ext.each(this.matches,function(n){
			n.bubble(function(n){
				n.ui.show();
			});
		}) ;
	},
	hasMatch:function(){
		return !Ext.isEmpty(this.matches);
	},
	clear : function(startNode){
		if(this.cleared === true){
			return;
		}
		startNode = startNode||this.tree.root;	
		startNode.cascade(function(n){
			n.ui.show();
			if (this.clearAction) {
				n[this.clearAction](true, true);
			}
		});
		this.cleared = true;
	},
	isCleared:function(){
		return this.cleared;
	},
	destroy:function(){
		Ext.destroyMembers(this,'pyCache','matches');
	}
} ; 

String.prototype.trim = function() {                
  return this.replace(/^\s+|\s+$/g, '');          
} ;

if('function' !== typeof RegExp.escape) {     
    RegExp.escape = function(s) {     
        if('string' !== typeof s) {     
            return s;     
        }     
        // Note: if pasting from forum, precede ]/\ with backslash manually     
        return s.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');     
    };  
}

var initCombox = function (param){
	
	var inputArray ;
	
	if(param) {
		if(typeof(param) == "string") {
			inputArray = Ext.query("#"+param) ;
		}else {
			var arr = new Array();
			arr.push(param) ;
			inputArray = arr ;
		}
	}else {
		inputArray = Ext.query("input["+selectAttributeName+"]") ;
	}
	
	Ext.each(inputArray,function(input){
		var autoId = input.getAttribute(selectAttributeName) ;
		var id = input.getAttribute("id") ;
		var name = input.getAttribute("name") ;
		var noinput = input.getAttribute("noinput") ; // 是否允许输入
		var width = input.clientWidth ;
		var multiselect = input.getAttribute("multiselect") ; // 是否多选
		var multilevel = input.getAttribute("multilevel") ;  // 是否多级
		var separator = input.getAttribute("separator") ;  // 多选分隔符
		var grid = input.getAttribute("grid") ;  // 是否显示下拉表格
		var value = input.value;  
		var className = input.className ;//验证
		var size = input.size ;
		var onselect = input.onselect ;
		var readOnly = input.readOnly ;
		var property = input.getAttribute("property") ;
		var listWidth = input.getAttribute("listWidth") ;
		var disabled = input.disabled ;
		var onchange = input.onchange ;
		var validateId = input.validateId ;
		
		var gridFieldIndex = input.getAttribute("gridFieldIndex") ;
		var valuemustexist = input.getAttribute("valuemustexist") ;
		
		var refer = input.getAttribute("refer") ;
		var refer1 = input.getAttribute("refer1") ;
		var refer2 = input.getAttribute("refer2") ; 
		var validRefer = input.getAttribute("validRefer") ; 
		
		var refreshtarget = input.getAttribute("refreshtarget") ;
		var refreshtarget1 = input.getAttribute("refreshtarget1") ;
		var refreshtarget2 = input.getAttribute("refreshtarget2") ;
		var emptyText = "请选择或输入...";
		var editable = true ;
		if(noinput && noinput == "true" || multiselect == "true") {
			//设置了noinput或多选或下拉表格都设置不允许输入
			editable = false ;
			emptyText = "请选择...";
		}
		
		if(readOnly) {
			emptyText = "";
		}
		
		if(width == 0){
			width = size*8 + 20 ; //如果取不到宽度,就用size来取代  20为右边下拉图片宽度
			
		}
		
		if(!listWidth)  listWidth = width > 200 ? width : 200;
		
		
		var mtCombox = new Ext.matech.form.mtCombox({
			multilevel:multilevel,
			autoid:autoId,
			transform:id,
			emptyText:emptyText,
			listWidth :listWidth,
			hiddenName:name,
			hiddenId:id,
			width:width,
			multiselect:multiselect,
			id:id, 
			editable:editable,
			separator:separator || ',',
			grid:grid,
			validateId:validateId,
			refer:refer,
			refer1:refer1,
			refer2:refer2,
			disabled:disabled,
			validRefer:validRefer,
			readOnly:readOnly,
			refreshtarget:refreshtarget,
			refreshtarget1:refreshtarget1,
			refreshtarget2:refreshtarget2,
			onselect:onselect,
			gridFieldIndex:gridFieldIndex,
			valuemustexist:valuemustexist,
			setRealValue:function (value,callback){
				var queryParam = new Array(3) ;
				var referObj = document.getElementById(this.refer) ;
			  	var refer1Obj = document.getElementById(this.refer1) ;
			  	var refer2Obj = document.getElementById(this.refer2) ;
			  	
			  	if(referObj) {
			  		queryParam[0] = referObj.value ;
			  	}else {
			  		queryParam[0] = this.refer ;
			  	}
			  	
			  	if(refer1Obj) {
			  		queryParam[1] = refer1Obj.value ;
			  	}else {
			  		queryParam[1] = this.refer1 ;
			  	}
			  	
			  	if(refer2Obj) {
			  		queryParam[2] = refer2Obj.value ;
			  	}else {
			  		queryParam[2] = this.refer2 ;
			  	}
			  	
			  	var url = selectUrl ;
			  	
			  	this.value = value ;
			  	this.setRawValue(value);  
				if(this.hiddenId) {
	            	Ext.get(this.hiddenId).dom.value =  value; 
	            }
				
				var tempValue = "" ; //传到后台文本框的值
				if(this.multiselect && !this.grid) {
					 //多选时,处理字符串，让后台可以用in拼出sql
					// var reg2=new RegExp(",","g");    //替换英文,为','
					 var reg3=new RegExp(this.separator,"g");    //替换英文,为','
					 var tempValue = (value+"").replace(reg3,"','");
					
				 }else {
					 tempValue = value ;
				 }
				
				var combox = this ;
				Ext.Ajax.request({
					method: 'POST',
					url: url,
					success: function(response) {
						var reslutJson = response.responseText ;
						if(reslutJson.indexOf("ERROR|") > -1) {
							alert("下拉初始化错误,原因："+reslutJson.replace("ERROR|")) ;
							return ;
						}
						
						if(!combox.multiselect || combox.grid) {
							//单选
							if(reslutJson != "") {
								//alert(reslutJson);
								var jsonArr = Ext.util.JSON.decode(reslutJson) ;
								if(combox.grid) {
									jsonArr = jsonArr.data ;
								}
								if(jsonArr){
									var i = 0 ;
									var rsValue = "" ;
									var rsText = "" ;  
									jsonArr = jsonArr[0] ; 
									for(var j in jsonArr) {
										
										if(i == 0) {
											rsValue = jsonArr[j] ;
										} 
										  
										if(i == 1) {
											rsText = jsonArr[j] ; 
											break ;
										}
										i++ ;
									}
									//var rsValue = eval(reslutJson)[0].value ;
									//var rsText = eval(reslutJson)[0].text ;
									if(rsValue == value && rsText!= value) {
										combox.setRawValue(rsText);    
									}
								}
							}
						}else {
							//多选
							if(reslutJson != "") {
								//遍历，拼,号分隔字符串
								var jsonArr = eval(reslutJson) ;
								var text = "" ;
								for(var i=0;i<jsonArr.length;i++) {
									if(jsonArr[i]){
										var rsText = jsonArr[i].text ;
										text += rsText + combox.separator ;
									}
								}
								
								if(text != "") {
									text = text.substring(0,text.length - 1) ;
									combox.setRawValue(text); 
								}
							} 
						}
						
						if(callback){
							callback.call(this,combox) ;
						}
		 			},
					failure: function() {
		 				alert("后台发生异常,下拉初始化失败!");
		 			},
		 			params: {
		 				autoid:combox.autoid,
	            		multilevel:combox.multilevel,
	            		multiselect:combox.multiselect,
		 				grid:combox.grid,
		 				checkmode:true,
		 				pk1:tempValue,
		 				refer:queryParam[0],
		 				refer1:queryParam[1],
		 				refer2:queryParam[2]
		 			}
				});
				
				
			}
		}) ;
		
		if(mtCombox.el.dom.id) {
			Ext.get(mtCombox.el.dom.id).dom.property = property;
			Ext.get(mtCombox.el.dom.id).dom.inputId = id;
		}
		mtCombox.addClass(className) ;
		
		if(onchange)   
			mtCombox.on("change",onchange) ;
		
		if(value && value != "") {
			mtCombox.setRealValue(value,function(obj){
				//alert(obj.getRawValue());
				mtCombox.startRawValue = obj.getRawValue() ;
			}) ;
			
		}
		
	}) ;
	
	if(selectAttributeName == "autoid") {
		//如果属性设计成了autoid 就屏蔽原有下拉的事件，防止报错
		var onKeyDownEvent = Ext.emptyFn() ;
		var onKeyUpEvent = Ext.emptyFn() ;
		var onPopDivClick = Ext.emptyFn() ;
	}
} ;

var mt_select_selectAll = function(autoid,inputId,obj) {
	var check = obj.checked ;
	var checkboxs = Ext.query("input[name=mt_gridMultiSelect_"+inputId+"_"+ autoid +"]") ;
	for(var i=0;i<checkboxs.length;i++) {
		checkboxs[i].checked = check ;
	}
} ;

var mt_select_formatNumber = function(v) {
	var result = "";
	if(parseFloat(v) < 0 ) {  
		result = "<div style='color:red' >" + formatDecimal(v,2) +  "</div>";
	} else {
		result = "<div style='color:blue' >" + formatDecimal(v,2) +  "</div>";
	}
	
	return result;
};

var mt_select_groupSelect = function(autoid,inputId,obj) {
	var group = obj.group ;
	if(!group) return ; 
	var check = obj.checked ;
	var checkboxs = Ext.query("input[name=mt_gridMultiSelect_"+inputId+"_"+ autoid +"]") ;
	for(var i=0;i<checkboxs.length;i++) {
		if(checkboxs[i].group == group)
			checkboxs[i].checked = check ;
	}
} ;



Ext.onReady(initCombox) ;
  
