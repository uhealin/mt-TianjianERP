//TableId数组
var copydataArray = new Array();
//选取td对象
var selectedArray = new Array();
//记录选取的行号列号,避免重复
var selectedFlag = "";
//记录选取计算器
var selectedCalculator;
var startRow=1;
var startCell=1;
//表格初始化

String.prototype.Trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
String.prototype.replaceAll  = function(s1,s2){return this.replace(new RegExp(s1,"gm"),s2); }  
function initTableData(id) {
	if(document.getElementById(id)) {
		document.getElementById(id).style.cursor = "hand";
	}
	if(selectedCalculator!=null) {
		selectedCalculator.value = "";
	}
}
//td对象
function selectedIndex(x,y,obj) {
	this.rowIndex = x;
	this.cellIndex = y;
	this.focusElement = obj;
}

//多个选取
function selectMultiCell(e){
	//在哪个table上发生的时间
	var focusElement = e.srcElement.parentElement.parentElement.parentElement;
	if(e.srcElement.tagName=="INPUT") {
		focusElement = e.srcElement.parentElement.parentElement.parentElement.parentElement; 
	}
	var flag = 0;
	for(var i=0; i<copydataArray.length; i++) {
		var copyElement = document.getElementById(copydataArray[i]);
		if(focusElement.id == copyElement.id) {
			flag=1;
			break;
		}
	}
	//如果不是datagrid 返回
	if(!flag) {
		return;
	}
	if(e.button==1)
	{    
		//是否按下Ctrl键
		if(e.ctrlKey) {
		} else {
			//清楚选取
			clearSelectedArea(selectedArray);
		}
		var endRow=e.srcElement.parentNode.rowIndex;
		var endCell=e.srcElement.cellIndex;
		if(e.srcElement.tagName=="INPUT") {
			endRow=e.srcElement.parentElement.parentElement.rowIndex;
			endCell=e.srcElement.parentElement.cellIndex;
		}
		
		//拖动选取设置样式
		for(var m=Math.min(startRow,endRow);m<=Math.max(startRow,endRow);m++)
		{
			for(var n=Math.min(startCell,endCell);n<=Math.max(startCell,endCell);n++)
			{
				focusElement.rows[m].cells[n].style.backgroundColor="#FFFFDD";
				focusElement.rows[m].cells[n].setAttribute("name","sel");	
				
				if(selectedFlag.indexOf("("+m+","+n+")")>-1) {
				} else {
					selectedFlag += "("+m+","+n+")";
					var selectedTD = new selectedIndex(m,n,focusElement);
					selectedArray[selectedArray.length] = selectedTD;
				}
			}
		}
		
		var calculatorDiv;
		if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
			calculatorDiv = document.getElementById("calculatorDiv");
			selectedCalculator = document.getElementById("selected4Calculate");
		} else {
			calculatorDiv = window.parent.document.getElementById("calculatorDiv");
			selectedCalculator = window.parent.document.getElementById("selected4Calculate");
		}
		
		if(calculatorDiv!=null&&calculatorDiv.style.display!="none") {
			var calculatorExpression;
			
			if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
				calculatorExpression = document.getElementById("calculatorExpression");
			} else {
				calculatorExpression = window.parent.document.getElementById("calculatorExpression");
			}
			var value = calculatorExpression.value;
			
			//验证货币类型的正则表达式
			var re = RegExp(/^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/);					
			
			for(var i=0;i<selectedArray.length; i++)
			{
				var tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].innerText;
				if(tdValue=="") {
					try {
						tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].getElementsByTagName("input")[0].value;
					} catch(e) {
						return;
					}
				}
				if(selectedCalculator.value.indexOf("(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")")>-1) {
					continue;
				} 
				selectedCalculator.value += "(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")";
				
				//判断选中的值是否是货币,如果不是就直接返回
				if(( re.test(tdValue))||( tdValue.substring(0,1) == "-" && re.test(tdValue.substring(1)))||( tdValue.substring(0,1) == "(" && re.test(tdValue.substring(1,tdValue.length-1))) ) {
					if(tdValue.indexOf("-") == 0) {
						tdValue = "(" + tdValue +")";
					} else if(tdValue.indexOf("(")==0) {
						tdValue = "(-" + tdValue.substring(1,tdValue.length-1) + ")";
					}
					if(value=="") {
						calculatorExpression.value += tdValue;
					} else {
						calculatorExpression.value += " + " + tdValue;
					}
				} 
			}
			calculatorResult();
		}
	}
};
//单选
function selectCell(e)
{
	
	var tagName = e.srcElement.tagName;
	var focusElement = e.srcElement.parentElement.parentElement.parentElement;
	if(tagName == "INPUT") {
		focusElement = e.srcElement.parentElement.parentElement.parentElement.parentElement;
	}
	var flag = 0;
	for(var i=0; i<copydataArray.length; i++) {
		var copyElement = document.getElementById(copydataArray[i]);
		if(focusElement.id == copyElement.id) {
			flag=1;
			break;
		}
	}
	if(!flag) {
		return;
	}
	
	if(e.button==1)
	{	
		//是否按下Ctrl键
		if(e.ctrlKey) {
		} else {
			clearSelectedArea(selectedArray);
		}
		//是否按下shift键
		
		if(e.shiftKey) {
			var endRow=e.srcElement.parentNode.rowIndex;
			var endCell=e.srcElement.cellIndex;
			if(e.srcElement.tagName=="INPUT") {
				endRow=e.srcElement.parentElement.parentElement.rowIndex;
				endCell=e.srcElement.parentElement.cellIndex;
			}
			for(var m=Math.min(startRow,endRow);m<=Math.max(startRow,endRow);m++)
			{
				for(var n=Math.min(startCell,endCell);n<=Math.max(startCell,endCell);n++)
				{
					focusElement.rows[m].cells[n].style.backgroundColor="#FFFFDD";
					focusElement.rows[m].cells[n].setAttribute("name","sel");
					
					if(selectedFlag.indexOf("("+m+","+n+")")>-1) {
					
					} else {
						selectedFlag += "("+m+","+n+")"	
						var selectedTD = new selectedIndex(m,n,focusElement);
						selectedArray[selectedArray.length] = selectedTD;
					}
				}
			}
		} else {
			if(e.srcElement.tagName=="TD") {
				startRow=e.srcElement.parentNode.rowIndex;
				startCell=e.srcElement.cellIndex;
				e.srcElement.style.backgroundColor="#FFFFDD";
				e.srcElement.setAttribute("name","sel");
			} else if(e.srcElement.tagName=="INPUT") {
				startRow=e.srcElement.parentNode.parentElement.rowIndex;
				startCell=e.srcElement.parentElement.cellIndex;
				e.srcElement.parentElement.style.backgroundColor="#FFFFDD";
				e.srcElement.parentElement.setAttribute("name","sel");
			}
			
			if(selectedFlag.indexOf("("+startRow+","+startCell+")")>-1) {
					
			} else {
				selectedFlag += "("+startRow+","+startCell+")";	
				var selectedTD = new selectedIndex(startRow,startCell,focusElement);
				selectedArray[selectedArray.length] = selectedTD;
			}
		}
		
		var calculatorDiv;
		if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
			calculatorDiv = document.getElementById("calculatorDiv");
			selectedCalculator = document.getElementById("selected4Calculate");
		} else {
			calculatorDiv = window.parent.document.getElementById("calculatorDiv");
			selectedCalculator = window.parent.document.getElementById("selected4Calculate");
		}
		
		if(calculatorDiv!=null&&calculatorDiv.style.display!="none") {
			
			var calculatorExpression;
			
			if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
				calculatorExpression = document.getElementById("calculatorExpression");
			} else {
				calculatorExpression = window.parent.document.getElementById("calculatorExpression");
			}
			var value = calculatorExpression.value;
			
			//验证货币类型的正则表达式
			var re = RegExp(/^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/);
			
			for(var i=0;i<selectedArray.length; i++)
			{
				var tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].innerText;
				if(tdValue=="") {
					try {
						tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].getElementsByTagName("input")[0].value;
					} catch(e) {
						return;
					}
				}
				if(selectedCalculator.value.indexOf("(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")")>-1) {
					continue;
				} 
				selectedCalculator.value += "(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")";
				
				//判断选中的值是否是货币,如果不是就直接返回
				if(( re.test(tdValue))||( tdValue.substring(0,1) == "-" && re.test(tdValue.substring(1)))||( tdValue.substring(0,1) == "(" && re.test(tdValue.substring(1,tdValue.length-1))) ) {
					if(tdValue.indexOf("-") == 0) {
						tdValue = "(" + tdValue +")";
					} else if(tdValue.indexOf("(")==0) {
						tdValue = "(-" + tdValue.substring(1,tdValue.length-1) + ")";
					}
					if(value=="") {
						calculatorExpression.value += tdValue;
					} else {
						calculatorExpression.value += " + " + tdValue;
					}
				} 
			}
			calculatorResult();
		}
	}
};
//清除选区
function clearSelectedArea(array)
{
	 // window.event.cancelBubble = true; 
	//window.event.returnValue = false;

	try{
		//alert(array.length);
		if(array) {
			for(var i=0; i<array.length; i++) {
			
				var selectedTD = array[i];
				selectedTD.focusElement.rows[selectedTD.rowIndex].cells[selectedTD.cellIndex].style.backgroundColor="";
				selectedTD.focusElement.rows[selectedTD.rowIndex].cells[selectedTD.cellIndex].removeAttribute("name");
			}	
		}
		
	} catch(e) {
	}
	selectedArray = new Array();
	selectedFlag = "";
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

//获取excel格式数据
function getData(obj,includeHead){
	var text = ""; 
	var array = setFlag(obj);
	var dataArray = new Array();
	var cellIndexArr = new Array();
	
	var cellIndexStr = "" ; //记录要添加表头的列
	for(var i=0; i<selectedArray.length; i++) {
		if(selectedArray[i].focusElement.id==obj.id) {
			var count = 0;
			var focusElement = selectedArray[i].focusElement;
			var rowIndex = selectedArray[i].rowIndex;
			var cellIndex = selectedArray[i].cellIndex;

			for(var j=0; j<cellIndex; j++) {
				if(array[j]>0) {
					count += 1;
				}
			}
			//alert(rowIndex+"|"+(obj.rows[rowIndex].cells[cellIndex].innerText.Trim()));
			if(dataArray[rowIndex]) {
				//alert(obj.rows[rowIndex].cells[cellIndex].innerText.Trim());
				
				if(cellIndexStr.indexOf(cellIndex) == -1) {
					cellIndexStr += cellIndex + "," ;
				}
				
				dataArray[rowIndex][count] = obj.rows[rowIndex].cells[cellIndex].innerText.Trim();
			} else {
				dataArray[rowIndex] = new Array();
				if(cellIndexStr.indexOf(cellIndex) == -1) {
					cellIndexStr += cellIndex + "," ;
				}
				dataArray[rowIndex][count] = obj.rows[rowIndex].cells[cellIndex].innerText.Trim();
			}
		}
	}
	
	if(cellIndexStr != "") {
		cellIndexStr = cellIndexStr.substring(0,cellIndexStr.length-1) ;
	}
	
	if(includeHead) {
		//复制含表头
		var cellIndexArr ;
		if(cellIndexStr != "") {
			cellIndexArr = cellIndexStr.split(",") ;
		}
		
		dataArray[0] = new Array(); 
		for(var i=0;i<cellIndexArr.length;i++) {
		//	alert(obj.rows[0].cells[cellIndexArr[i]].innerText.Trim());
			
			//这里如果是多表头，就取第二行做表头，所以只支持二级表头
			if(obj.rows[0].cells.length == array.length) {
				dataArray[0][i] = obj.rows[0].cells[cellIndexArr[i]].innerText.Trim();
			}else {
				dataArray[0][i] = obj.rows[1].cells[cellIndexArr[i]].innerText.Trim();
			}
		}
	}
	for(var i=0; i<dataArray.length; i++) {
		var temp = "";
		if(dataArray[i]!=null) {
			for(var k=0; k<dataArray[i].length; k++) {
				if(dataArray[i][k]!=null) {
					temp += dataArray[i][k];
				}
			}
			
			if(temp.length>0) {
				for(var j=0; j<dataArray[i].length; j++) {
					if(dataArray[i][j]!=null) {
						//替换中文逗号，支持金额模糊查询
						text += dataArray[i][j].replaceAll("，",",")+"\t";
					} else {
						text += "\t";
					}
				}
				text = text.substring(0,text.length-1);
				text += "\n";
			} 
		}
		
	}
	text = text.substring(0,text.length-1);
	return text;
}

//设置标志位
function setFlag(obj) {
	var length = obj.rows.length ;
	if(obj.rows.length > 3) {
		var arrayLength = obj.rows[2].cells.length;
	} 
	var array = new Array(arrayLength);
	for(var i=0; i<arrayLength; i++) {
		array[i] = 0;
	}
	
	for(var j=0; j<selectedArray.length; j++) {
		if(selectedArray[j].focusElement.id == obj.id) {
			array[selectedArray[j].cellIndex] += 1;
		}
	}
	return array;
}
//去除数组中重复元素
function addCopyTableId(name) {
	var flag = 1;
	for(var i=0; i<copydataArray.length; i++) {
		if(copydataArray[i]==name) {
			flag = 0;
		}
	}
	if(flag) {
		copydataArray.push(name);
	}
}


function rightMenuCopy(flag) {
	var text = "";
	try{
		
		
		var element = window.event.srcElement.parentElement;
		
		for(var i=0; i<copydataArray.length; i++) {
			
			var tableObj = document.getElementById(copydataArray[i]) ;
			
			if(copydataArray[i] == element.obj.id) {
				if(flag==1) {
					//转置复制
					text += getData(element.obj);
					text = changeRowsAndCols(text);
				} else if(flag == 2) {
					//复制(带表头)
					text += getData(element.obj,true);
				}else if(flag == 3)  {
					//转置复制（带表头）
					text += getData(element.obj,true);
					text = changeRowsAndCols(text);
				}else {
					//复制
					text += getData(element.obj);
				}
			}
			
		}		
		try{
	
			//获得当前窗口的地址和端口先
			//alert("http:\/\/"+window.location.host +strUrl);
			var t,t1;
			
			//找到主操作界面
			t = window.opener;
			
			if(!t)//如果是框架页则找它的父窗口
				t = window.parent;
			if (t){
				//alert('被新开窗口');
				t1 = t.window.opener;
				
				if(!t1)//如果是框架页则找它的父窗口
					t1 = t.window.parent;	
				while (t1){
					t = t1;
					t1 = t.window.opener;
					//alert(t1)
					if(!t1){//如果是框架页则找它的父窗口
						t1 = t.window.parent;
						if(t1.bottomFrame){
							break;
						}
					}	
				}
			}else{
				//alert('没有被新开窗口');
				t = window;	
			}
			
			//在主操作界面中找到最上面的那个WINDOW
			t1 = t.parent;
			while (t1 && t1 != t){
				t = t1;
				t1 = t.window.parent;
			}
			//找到最后的那个URL
			if (t){
				t.bottomFrame.myTextToClipboard(text);
				//t.open(strUrl);
			}
		}catch(e){
			var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
			AuditReport.subClipboardSetText(text);
			AuditReport=null;
		}
		
		//clipboardData.setData("text",text);
		hideRightMenu();
	} catch(e) {
	}
	//alert("复制成功");
}

function rightMenuCancel() {
	clearSelectedArea(selectedArray);
	hideRightMenu();
}

//右键菜单
document.oncontextmenu = showRightMenu;
function showRightMenu() {
	

	try{
		var obj = window.event.srcElement;
		do{
			if(obj.tagName == "TABLE") break ;
			obj = obj.parentElement;
		}while(obj.parentElement != null);
		createRightMenu(obj);
	} catch(e) {
		return true;
	}
	var flag = 0;
	var rightMenu = document.getElementById("rightMenu");
	var element = event.srcElement;
	do{
		if(element.id=="data_copy" || element.id=="data_copy1") {
			rightMenuCopy();
			clearSelectedArea(selectedArray);
			break;
		} else if(element.id=="data_paste"){
			try{
				rightMenuCancel();
			} catch(e) {
				
			}
			break;
		} 
		for(var i=0;i<copydataArray.length; i++) {
			var copyElement = document.getElementById(copydataArray[i]);
			if(element.id ==copyElement.id) {
				flag=1;
				break;
			}
		} 
		if(element.parentElement) {
			element = element.parentElement;
		}
	}while(element.parentElement);
	if(flag) {
		rightMenu.style.position = "absolute";
		rightMenu.style.left = event.clientX + document.body.scrollLeft;
		rightMenu.style.top = event.clientY + document.body.scrollTop;
		rightMenu.style.display="";
		rightMenu.style.visibility = "visible" ;
		return false;
	}
	else {
		rightMenu.style.display="none";
		return true;
	}
}

function createRightMenu(obj) {
	var txtHtml = "";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy\" style=\"text-align:center;padding:5px 4px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy();\">复  制</div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy\" style=\"text-align:center;padding:0px 6px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy(2);\">复制(带表头)</div>";
	txtHtml +="<div class=\"coolMenuDivider\"></div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy1\" style=\"text-align:center;padding:5px 4px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy(1);\">转置复制</div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy\" style=\"text-align:center;padding:0px 0px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy(3);\">转置复制(带表头)</div>";
	txtHtml +="<div class=\"coolMenuDivider\"></div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_paste\" style=\"text-align:center;padding:5px 6px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCancel();\">取  消</div>";
	document.getElementById("rightMenu").className = "coolMenu";
	document.getElementById("rightMenu").innerHTML = txtHtml;
	document.getElementById("rightMenu").obj = obj ;
}

function hideRightMenu() {
	document.getElementById("rightMenu").style.display="none";
	var element = event.srcElement;
	var flag = 0;
	do{
		for(var i=0;i<copydataArray.length; i++) {
			var copyElement = document.getElementById(copydataArray[i]);
			if(element.id ==copyElement.id ) {
				flag=1;
				break;
			}
		}
		element = element.parentElement;
	}while(element.parentElement);
	if(!flag) {
		clearSelectedArea(selectedArray);
	}
}