Ext.namespace("Ext.ux.grid");

Ext.ux.grid.MultiCellSelectionModel = function(config) {
    Ext.apply(this, config);
    this.addEvents({
        "beforecellselect": true,
        "selectionchange": true
    });
    var ck = this.cellKey;
    this.selection = new Ext.util.MixedCollection(false, function(o){
        return ck(o);
    });
    Ext.ux.grid.MultiCellSelectionModel.superclass.constructor.call(this);
}
Ext.extend(Ext.ux.grid.MultiCellSelectionModel, Ext.grid.AbstractSelectionModel, {
    constrainToSingleRow: false,
	nonceRow : false,
	witCopy : true,
    unselectableColumns: [],
	dragFlag : false,
    returnRecord: true,
    returnDataIndex: true,
    keyNavEnabled: false,
	selectedArea:"",
    initEvents: function() {
		this.keyBy(this);
        this.grid.on("cellmousedown", this.handleMouseDown, this);
        this.view = this.grid.view;

        this.view.on("beforerowremoved", this.clearSelections, this);
        this.view.on("beforerowsinserted", this.clearSelections, this);

        if(this.grid.isEditor)
            this.grid.on("beforeedit", this.beforeEdit,  this);

        var moveSelectionFunc = function(adjust) {
            return function(e) {
                if(this._activeItem) {
                    var active = this.selection.get(this._activeItem);
                    var row = active.row, col = active.col;

                    if(this._expandedTo) {  // e.shiftKey && 
                        row = this._expandedTo[0];
                        col = this._expandedTo[1];
                    }
                    var adjusted = adjust(row,col);
                    row = adjusted[0]; col = adjusted[1];
                    if(this.isCellAvailable(row,col) && this.isCellSelectable(row,col,this.getColumnDataIndex(col))) {
                        e.shiftKey ? this.expandSelection(row, col, true) : this.select(row, col, false);
                    }
                }
            }
        };

        if(this.keyNavEnabled) {
            this.keyNav = new Ext.KeyNav(this.grid.getGridEl(), {
                scope: this,
                up: moveSelectionFunc(function(row, col) {    return [row - 1, col     ]; }),
                down: moveSelectionFunc(function(row, col) {  return [row + 1, col     ]; }),
                left: moveSelectionFunc(function(row, col) {  return [row    , col - 1 ]; }),
                right: moveSelectionFunc(function(row, col) { return [row    , col + 1 ]; }),
                "enter": function(e) {
                    if(this._activeItem) {
                        var active = this.selection.get(this._activeItem), g = this.grid;
                        if(g.isEditor && !g.editing) {
                            g.startEditing(active.row, active.col);
                            e.stopEvent();
                        }
                    }
                }
            })
        }
    },
    isCellAvailable: function(row, col) {
        return col > -1 && row > -1 && !!this.view.getRow(row) && !!this.view.getCell(row,col);
    },
    beforeEdit: function() {

    },
    hasSelection: function() {
        return this.selection.getCount() > 0;
    },
    clearSelections: function(preventNotify) {
        if(this.hasSelection()) {
            if(!preventNotify)
                this.selection.each(function(cell){
					if(this.nonceRow){
						var cm=this.grid.getColumnModel();
						var c=cm.getColumnCount();
						for(var i=0;i<c;i++){
							if(typeof cm.getColumnById(cm.getColumnId(i)).hidden=="undefined"||!cm.getColumnById(cm.getColumnId(i)).hidden){
								this.view.onCellDeselect(cell.row, i);	
							}
						}
					}else{
                    	this.view.onCellDeselect(cell.row, cell.col);
					}
                }, this);
            this.selection.clear();
            this.fireEvent("selectionchange", this, this.selection);
        }
		//this.selectedArea = "";
    },
    handleMouseDown: function(g, row, col, e) {
        if(e.button != 0 || this.isLocked()) return;
        var hs = this.hasSelection();
        if(hs) {
            if(e.shiftKey) this.expandSelection(row, col, true);
            else if(e.ctrlKey) this.selectAdditionalCell(row,col,e);
            else this.select(row, col);
        } else this.select(row, col, e);
    },
    captureMouseMove: function(on) {
        if(on) {
            this.view.el.on('mouseup', this.captureMouseMove.createDelegate(this, [false]), this, { single: true });
            this.view.el.on('mousemove', this.onMouseMove, this);
        } else {
            this.view.el.un('mousemove', this.onMouseMove);
            this.fireEvent("selectionchange", this, this.selection);      
        }
    },
    onMouseMove: function(e, el) {
		if(this.dragFlag) {
			var row = this.view.findRowIndex(el), col = this.view.findCellIndex(el);
			if(!(col === false) && (!this._expandedTo || !(row == this._expandedTo[0] && col == this._expandedTo[1]))) {
				this.expandSelection(row, col, true);
			}
		}
    },
    isCellSelectable: function(row, col, dataIndex) {
        return (!this.grid.getColumnModel().isHidden(col)) && !(dataIndex && this.unselectableColumns.indexOf(dataIndex) != -1);

    },
    getColumnDataIndex: function(col) {
        if(this.returnDataIndex) {
            var cm = this.grid.getColumnModel();
            return cm.getColumnById(cm.getColumnId(col)).dataIndex;
        }
    },
    selectCell: function(row, col, preventViewNotify, preventFocus, r) {
		var key = this.cellKey(row, col);
		var v = this.grid.getView();
		if(this.nonceRow){
			var cm=this.grid.getColumnModel();
			var c=cm.getColumnCount();
			for(var i=0;i<c;i++){
				if(typeof cm.getColumnById(cm.getColumnId(i)).hidden=="undefined"||!cm.getColumnById(cm.getColumnId(i)).hidden){
					var dataIndex = this.getColumnDataIndex(col);
					r = this.returnRecord && (r || this.grid.store.getAt(row));
					var sel = {
						record : r,
						dataIndex: dataIndex,
						cell : [row, i],
						row: row, col: i
					};
					this.selection.add(key, sel);
					if(!preventViewNotify){
						v.onCellSelect(row, i);
						if(preventFocus !== true){
							v.focusCell(row, col);
						}
					}	
				}
			}
		}else{
			var dataIndex = this.getColumnDataIndex(col);
			if(!this.isCellSelectable(row, col, dataIndex)) return;
	
			r = this.returnRecord && (r || this.grid.store.getAt(row));
			var sel = {
				record : r,
				dataIndex: dataIndex,
				cell : [row, col],
				row: row, col: col
			};
			this.selection.add(key, sel);
			if(!preventViewNotify){
				v.onCellSelect(row, col);
				if(preventFocus !== true){
					v.focusCell(row, col);
				}
			}
		}
			
        return key;
    },
    deselectCellByKey: function(cellkey, preventViewNotify) {
			var cell = this.selection.get(cellkey);
			if(!cell) return;
			if(!preventViewNotify) this.view.onCellDeselect(cell.row, cell.col);
			this.selection.remove(cell);
    },
    deselectCell: function(row, col) {
        this.deselectCellByKey(this.cellKey(row, col));
    },
    deselectCells: function(cells) {
        for(var i=0; i<cells.length;i++)
            this.deselectCellByKey(cells[i]);
    },
    cellKey: function(row,col) {
        return String.format("{0}::{1}", row, col);
    },
    expandSelection: function(row, col, clearExpandedSelection) {
        if(this._expandedSelection && clearExpandedSelection) {
				if(!this.nonceRow)
            	this.deselectCells(this._expandedSelection);
        }
        var active = this.selection.get(this._activeItem);
        this._expandedSelection = [];
		
		//if(this.nonceRow){
        //    var s = this.selectCell(active.row, 0, false, true);
        //   this._expandedSelection.push(s);
        //    this._expandedTo = [active.row, 0];
		//}else 
		if(this.constrainToSingleRow || active.row == row) {
            var start = active.col + 1, end = col;
            if(col < active.col) {
                start = col;
                end = active.col -1;
            }
            for(var i = start; i <= end; i++) {
                var s = this.selectCell(active.row, i, false, true);
                this._expandedSelection.push(s);
            }
            this._expandedTo = [active.row, col];
        } else {
            var x0 = Math.min(col, active.col), x1 = Math.max(col, active.col);
            var y0 = Math.min(row, active.row), y1 = Math.max(row, active.row);
            for(var x=x0; x<=x1; x++) {
                for(var y=y0; y<=y1;y++) {
                    if(!(x==active.col && y==active.row)) {
                        var s = this.selectCell(y, x, false, true);
                        this._expandedSelection.push(s);
                    }
                }
            }
            this._expandedTo = [row, col];
        }
        this.fireEvent("selectionchange", this, this.selection);
    },
    getSelections: function() {
        return this.selection;
    },
    selectAdditionalCell: function(row, col, captureMouseMove,e) {
		this.dragFlag = false;
        var key = this.cellKey(row, col);
        if(this.constrainToSingleRow && this._activeItem) {
            var active = this.selection.get(this._activeItem);
            if(active.row != row) return;
        }
        if(this.selection.containsKey(key)) {
            this.deselectCellByKey(key);
        } else {
            this._activeItem = this.selectCell(row, col);
            this._expandedSelection = false;
            this._expandedTo = false;

            if(captureMouseMove !== false) {
                this.captureMouseMove(true);
			}
        }
    },
    select : function(rowIndex, colIndex, e, captureMouseMove, preventViewNotify, preventFocus, /*internal*/ r){
        if(this.fireEvent("beforecellselect", this, rowIndex, colIndex) !== false){
            this.clearSelections();
            this._activeItem = this.selectCell(rowIndex, colIndex, preventViewNotify, preventFocus, r);
            this._expandedSelection = false;
            this._expandedTo = false;
            if(captureMouseMove !== false) {
                this.captureMouseMove(true);
		
					this.dragFlag = true;
				
			}
			//alert(rowIndex + "==" + colIndex);
            this.fireEvent("selectionchange", this, this.selection);
        }
    },
	 keyBy : function(o) {
		var m = new Ext.KeyMap(Ext.get(this.grid.getEl().id), {
			key: 'c',
			fn: function(){mcsm_copy_(o)},
			ctrl:true
		});
	},
	copys : function(type){
		mcsm_copy_(this,type);
	},
	calculate : function(o) {
		mcsm_calculate(this);
	}
});

function mcsm_calculate(obj) {
	//alert(1);
	//验证货币类型的正则表达式
	var re = RegExp(/^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/);
	
	var r=obj.grid.getStore().getCount();
	var cm=obj.grid.getColumnModel();
	var c=cm.getColumnCount();
	var selectedArea = obj.selectedArea;
	var h=0;
	var f=0;
	var n=0;
	var m=0;
	var calculateDiv = document.getElementById("sText");
	for(var j=0;j<r;j++){
		n=0;
		m=0;

		for(var i=0;i<c;i++){
			try{
				var o=obj.grid.view.getCell(j,i);
				if(typeof cm.getColumnById(cm.getColumnId(i)).hidden!="undefined"&&cm.getColumnById(cm.getColumnId(i)).hidden){
				}else{
					if(Ext.fly(o).hasClass("x-grid3-cell-selected")){
						var textValue = o.firstChild.innerText;
						
						
						if(obj.selectedArea.indexOf("[" + j + "," + i + "]")<0) {
							//判断选中的值是否是货币,如果不是就直接返回
							if(( re.test(textValue))||( textValue.substring(0,1) == "-" && re.test(textValue.substring(1)))||( textValue.substring(0,1) == "(" && re.test(textValue.substring(1,textValue.length-1))) ) {
								
								if(textValue.indexOf("-") == 0) {
									textValue = "(" + textValue +")";
								} 
								if(calculateDiv.value=="") {
									calculateDiv.value += textValue;
								} else {
									calculateDiv.value += " + " + textValue;
								}
							} 
							obj.selectedArea += "[" + j + "," + i + "],";
						}

						

						/*
						if(textValue.indexOf("-")>-1) 
							textValue = " (" + textValue + ") ";
						}
						calculateDiv.value += textValue+"+"; 
						*/
						if(f==0&&h==0)
							f=i;
						h++;
						n++;
					}else{
						m++;	
					}
				}
			}catch(e){
			}
		}
	}
}
//private
function mcsm_copy_(obj,type){
	setTimeout(function(){
		var s="";
		var r=obj.grid.getStore().getCount();
		var cm=obj.grid.getColumnModel();
		var c=cm.getColumnCount();
		var copyArray = new Array(r+1);
		var noBlanks = ",";
		var cellIndexStr = "" ; //记录要添加表头的列
		
		
		for(var j=0;j<r;j++){
			for(var i=0;i<c;i++){
				try{
					var o=obj.grid.view.getCell(j,i);
					if(typeof cm.getColumnById(cm.getColumnId(i)).hidden!="undefined"&&cm.getColumnById(cm.getColumnId(i)).hidden){
					}else{
							if(Ext.fly(o).hasClass("x-grid3-cell-selected")){
								if(!copyArray[j]) {
									copyArray[j] = new Array(c);
								}
								copyArray[j][i] = o.firstChild.innerText.Trim();
								noBlanks += j + ","
								if(cellIndexStr.indexOf(i) == -1) {
									cellIndexStr += i + "," ;
								}
							}
						
					}
				}catch(e){}
				
			}
			
		}
		
		if(cellIndexStr != "") {
			cellIndexStr = cellIndexStr.substring(0,cellIndexStr.length-1) ;
		}
		if(type=="2" || type=="4") {  //带表头
			copyArray[0] = new Array(c);
			for(var k=0;k<c;k++) {
				if(cellIndexStr.indexOf(k)>-1) {
					var oHead = obj.grid.view.getHeaderCell(k);
						copyArray[0][k] = oHead.firstChild.innerText.Trim();
				}
				
			}
			noBlanks +=0+"," ;
		}
		var startCol = 99999;
		for(var i=0; i<r+1; i++) {
			var flag = 0;
			if(noBlanks.indexOf(","+i+",")>-1) {
				for(var j=0; j<c; j++) {
					if(typeof(copyArray[i][j])=="undefined") {
						flag +=1
					} else {
						break;	
					}
				}
				if(flag < startCol) {
					startCol = flag;
				}
			}
		}
		
		var sText = "";
		for(var i=0; i<r+1; i++) {
			if(noBlanks.indexOf(","+i+",")>-1) {
				for(var j=startCol;j<c;j++) {
					if(typeof(copyArray[i][j])=="undefined") {
				//		sText += "\t";
					} else {
						sText += copyArray[i][j] + "\t";
					}
				}
				sText = sText.substring(0,sText.length-1);
				sText += "\n"
			}
		}
		sText = sText.substring(0,sText.length-1);
		
		if(type=="3" || type=="4") { //转置复制
			sText = changeRowsAndCols(sText) ;
		}
		
		var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
		AuditReport.subClipboardSetText(sText);
		AuditReport=null;
		
	//	window.clipboardData.setData('text',sText);
		try {
			obj.grid.getSelectionModel().clearSelections();
		}
		catch (e) {
			
		}
	},0);
}

function changeRowsAndCols(text) {
	if(text!="") {
		var textArray = text.split("\n");
		var myarray = new Array(textArray.length);
		for(var i=0; i<myarray.length; i++) {
			var textArray1 = textArray[i].split("\t");
			myarray[i] = new Array(textArray1.length);
			for(var j=0; j<myarray[i].length; j++) {
				myarray[i][j] = textArray1[j];
			}
		}
	}
	
	var text_rows = myarray.length;
	var text_cols = myarray[text_rows-1].length;
	var newArray = new Array(text_cols);
	for(var i=0; i<newArray.length; i++) {
		newArray[i] = new Array();
	}
	
	for(var i=0; i<myarray.length; i++) {
		for(var j=0; j<myarray[i].length; j++) {
			try {
				newArray[j][i] = myarray[i][j];
			} catch(e) {
				alert(e);
			}
		}
	}
	
	var returnText = "";
	for(var i=0; i<newArray.length; i++) {
		for(var j=0; j<newArray[i].length; j++) {
			if(newArray[i][j]=="" || typeof(newArray[i][j])=="undefined") {
				returnText += "\t"
			} else {
				returnText += newArray[i][j] + "\t"
			}
		}
		returnText = returnText.substring(0,returnText.length-1);
		returnText += "\n";
	}
	returnText = returnText.substring(0,returnText.length-1);
	return returnText;
}