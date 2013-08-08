
/*
 * 抽疑调析统一使用的java script方法。
 * 有改动需要，可copy到当前页面。而不引用本页面。
 * 
 *                       06.10.06    k
*/

function takeDoubt(s)
{

	var obj=document.getElementById("doubtTD"+s);
	var sign=obj.innerHTML;
      
	if(sign.indexOf("疑")>=0){
		
		var strResult = window.showModalDialog('/AuditSystem/voucherquery/DoubtfuPoint.jsp?autoid='+s+"&random=" + Math.random(),null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
        
        if(strResult.indexOf("ok")>=0)	{
			//obj.innerHTML="";
			refreshState2(obj,"<font color=\"red\">撤</font>");
		}
	}else if(sign.indexOf("撤")>=0){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","/AuditSystem/voucherquery/DoubtfuPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
		oBao.send();
		var noDel = unescape(oBao.responseText);
		if(noDel.indexOf("ok")>=0){
             refreshState2(obj,"疑");
		}
	}
}


function takeOutEntry(s)
{	
        var obj=document.getElementById("outA"+s);
        var sign=obj.innerHTML;

        if(sign.indexOf("抽")>=0){
        
          var strResult = "";
          try{
          	strResult=window.showModalDialog('/AuditSystem/voucherquery/takeoutPoint.jsp?autoid='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no');
          }catch(e){
          	
          	try{ 
				var oBao = new ActiveXObject("MTOffice.WebOffice");
		
				var myhost="http:\/\/"+window.location.host;
		
				strResult = oBao.ShowjsDialog(myhost+'/AuditSystem/voucherquery/takeoutPoint.jsp?autoid='+s+"&random=" + Math.random(),'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
		
				oBao =null;
				
			 }catch(e){alert('出错了：'+e);}
          }
          if(strResult && strResult.indexOf("ok")>=0){
            //obj.innerHTML="<font color=\"red\">撤</font>";
            //refreshState2(obj,"<font color=\"red\">撤</font>");
            refreshState2(obj,"<font color=\"red\">抽</font>");
          }

        }else if(sign.indexOf("撤")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/takeoutPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
           if(noDel.indexOf("ok")>=0){
           	
             refreshState2(obj,"抽");
           }
        }
}

function takeOutEntryExt(obj,s,tableId)
{		
      	var sign = obj.innerHTML ;
      	
      	 if(sign.indexOf("抽")>=0){
		      var strResult = "";
		      try{
		      	strResult=window.showModalDialog('/AuditSystem/voucherquery/takeoutPoint.jsp?tableId='+tableId+'&flag=ext&autoid='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no');
		      }catch(e){
		      	try{ 
					var oBao = new ActiveXObject("MTOffice.WebOffice");
					var myhost="http:\/\/"+window.location.host;
					strResult = oBao.ShowjsDialog(myhost+'/AuditSystem/voucherquery/takeoutPoint.jsp?tableId='+tableId+'&flag=ext&autoid='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
					oBao =null;
				 }catch(e){alert('出错了：'+e);}
		      }
		      
		      if(strResult && strResult.indexOf("ok")>=0){
		      	refreshStateExt(tableId,"<font color=\"red\">抽</font>",s);
		      }
      	 }else if(sign.indexOf("撤")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/takeoutPointBack.jsp?flag=ext&autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
           if(noDel.indexOf("ok")>=0){
           	 refreshStateExt(tableId,"抽");
           }
        }
    }

var attachWin = null; 
function setAttachExt(obj,s,tableId) {		
	var sign = obj.innerHTML ;

	if(sign.indexOf("附")>=0){
	    var strResult = "";
	    
	    try{
	    	
			var url="/AuditSystem/taskAttach.do?flag=ext&attachCode=" + s + "&random=" + Math.random();
			if(!attachWin) { 
			    attachWin = new Ext.Window({
			     	renderTo : Ext.getBody(),
			     	width: 600,
			     	height:420,
			     	id:'attachWin',
			     	title:'附件',
			     	closable:'flase',
			       	closeAction:'hide', 
			      	    listeners : {
			         	'hide':{
			         		fn: function () {
			         			new BlockDiv().hidden();
								attachWin.hide();
							}
						}
			        },
			       	html:'<iframe name="attachFrame" id="attachFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
			       	layout:'fit'
			    });
			   } else {
			   	document.getElementById("attachFrame").src = url;
			   }
			new BlockDiv().show();
			attachWin.show(); 
	    	
	    	//strResult=window.showModalDialog('/AuditSystem/taskAttach.do?flag=ext&attachCode='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no');
	    } catch(e){
	    	try{ 
				var oBao = new ActiveXObject("MTOffice.WebOffice");
				var myhost="http:\/\/"+window.location.host;
				strResult = oBao.ShowjsDialog(myhost+'/AuditSystem/taskAttach.do?flag=ext&attachCode='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
				oBao =null;
	 		}catch(e){
	 			alert('出错了：'+e);
	 		}
	    }
	    
	    if(strResult && strResult.indexOf("ok")>=0){
	    	refreshStateExt(tableId,"<font color=\"red\">附</font>",s);
	    }
	} 
}

    function refreshStateExt(tableId,state,autoId) {
	  	var grid = Ext.getCmp("gridId_"+tableId);
	  	var store = grid.getStore();
	  	var select = grid.getSelectionModel();
	  	var active = select.selection.get(select._activeItem);
        var row = active.row, col = active.col;
        var record = store.getAt(row);
        var voucherid = document.createElement(record.data['trValue']).voucherid;
        
        var count = store.getCount();
        for(var i=0;i<count;i++) {
        	var rs = store.getAt(i);
        	var vId = document.createElement(rs.data['trValue']).voucherid;
        	if(vId == voucherid) {
        		var o=select.grid.view.getCell(i,col);
        		o.innerHTML = "<DIV class=\"x-grid3-cell-inner x-grid3-col-p1 \" unselectable=\"on\">"
        					+ "<a onclick='takeOutEntryExt(this,\"" + autoId + "\",\""+tableId+"\");' href='#'>"
        					+ state
        					+ "</a></div>" ;
        	}
        }
	  	
    }

function takeOutEntry1(s,d){
  
	if(d==""){
		takeOutEntry(s);
	}else{
	
		 var obj=document.getElementById("outA"+s);
         var sign=obj.innerHTML;

        if(sign.indexOf("抽")>=0){

 			var url = "/AuditSystem/voucherquery/takeoutPointSave.jsp?autoid="+s+"&subjectid="+d+"&random=" + Math.random();
			strResult = ajaxLoadPageSynch(url,"a=1");

          if(strResult.indexOf("ok")>=0){
            //obj.innerHTML="<font color=\"red\">撤</font>";
            refreshState2(obj,"<font color=\"red\">撤</font>");
          }

        }else if(sign.indexOf("撤")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/takeoutPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
           if(noDel.indexOf("ok")>=0){
             //obj.innerHTML="抽";
             refreshState2(obj,"抽");
           }
        }
	}
}

function taskTax(s)
{

        var obj=document.getElementById("TaskTaxTD"+s);
    
        var sign=obj.innerHTML;
		
        if(sign.indexOf("税")>=0){
          var strResult = window.showModalDialog('/AuditSystem/voucherquery/tasktaxPoint.jsp?autoid='+s+"&random=" + Math.random(),null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
          if(strResult.indexOf("ok")>=0){
            var str ="<a id=\"outT"+ s + "\" href='javascript:taskTax(" + s + ");'><font color=\"red\">撤</font></a>&nbsp;|&nbsp;<a id=\"outT"+ s + "\" href='javascript:taskEdit(" + s + ");'><font color=\"red\">改</font></a>";  
         
            refreshState1(obj,str);
          }

        }else if(sign.indexOf("撤")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/taketaxPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
          if(noDel.indexOf("ok")>=0){   
             var str ="<a id=\"outT"+ s + "\" href='javascript:taskTax(" + s + ");'>税</a>";
             refreshState1(obj,str);
          }
        
        }  
}

function taskEdit(s)
{

      window.showModalDialog('/AuditSystem/voucherquery/tasktaxPoint.jsp?autoid='+s+"&random=" + Math.random()+'&opt=1',null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";

}


function eliminate(s)
{

	var obj=document.getElementById("EliminateTD"+s);
	var sign=obj.innerHTML;
      
	if(sign.indexOf("剔")>=0){
		
		var strResult = window.showModalDialog('/AuditSystem/voucherquery/EliminatePoint.jsp?autoid='+s+"&random=" + Math.random(),null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
        
        if(strResult.indexOf("ok")>=0)	{
			//obj.innerHTML="";
			refreshState2(obj,"<font color=\"red\">撤</font>");
		}
	}else if(sign.indexOf("撤")>=0){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","/AuditSystem/voucherquery/EliminatePointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
		oBao.send();
		var noDel = unescape(oBao.responseText);
		if(noDel.indexOf("ok")>=0){
             refreshState2(obj,"剔");
		}
	}
}


function goRectify(s){
  window.open("/AuditSystem/Voucher/AddandEdit.jsp?Autoid="+s);
}

function goAnalyze(s1,s2,s3){
  window.open("/AuditSystem/CheckoutInfo/CorrespondVoucherCheck.jsp?strDate="+s1+"&strSub="+s2+"&strDir="+s3+"&random=" + Math.random());
}


//抽疑后，调用的刷新方法
//将指定凭证的状态改变
function refreshState1(oTD,refreshValue){

	while(oTD.tagName!="TD"){
		oTD=oTD.parentElement;
	}

    //oTD所在的table
	var oTable=oTD;
	while(oTable.tagName!="TABLE"){
		oTable=oTable.parentElement;
	}
	

	var oTableHead=oTable.rows(0);
	
	//oTD所在的行
	var oTDRow=oTD;
	while(oTDRow.tagName!="TR"){
		oTDRow=oTDRow.parentElement;
	}
	
	//oTD在 行的索引
	//谁有更好的办法，请告知一下。cellIndex好像有问题。 
	var oTDidx=-1;
	for(var i=0;i<oTDRow.cells.length;i++){

		if(oTDRow.cells(i).innerHTML==oTD.innerHTML){
			oTDidx=i;
			break;
		}
	}
	if(oTDidx==-1){
		//无法定位所在行的行号
		return;
	}
	
	if (oTDRow.voucherid){
	
		
		oTDRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
		

	}else{
		//没有定义TR属性，就通过CELL单元格确定凭证日期列；
	
		//====凭证日期的索引
		var idx=-1;
		//获得凭证日期的索引
		var cell;
		for(var i=0;i<oTableHead.cells.length;i++){
			cell=oTableHead.cells(i);
			if(cell.innerHTML.indexOf("凭证日期")>=0){
				idx=i;
				break;
			}
		}
	
		if(idx==-1){
			return;
		}
		//====开始刷新
	
	
		//alert(oTD.innerHTML);
		//alert(oTD.parentElement.cells(oTDidx).innerHTML+":::"+oTDidx);
	    //第一列是表头，最后一列是页尾
	    for(var i=1;i<oTable.rows.length-1;i++){
			var oRow=oTable.rows(i);
	
			//判断凭证日期，凭证字，凭证编号是否都相同。
			if(oTDRow.cells(idx).innerHTML==oRow.cells(idx).innerHTML
			  	&&oTDRow.cells(idx+1).innerHTML==oRow.cells(idx+1).innerHTML
			  	&&oTDRow.cells(idx+2).innerHTML==oRow.cells(idx+2).innerHTML){
			  	//如果都相等，则刷新
			  	oRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
			}
		}
	}
}


//抽疑后，调用的刷新方法
//将指定凭证的状态改变
function refreshState2(oTD,refreshValue){
	
	while(oTD.tagName!="TD"){
		oTD=oTD.parentElement;
	}

    //oTD所在的table
	var oTable=oTD;
	while(oTable.tagName!="TABLE"){
		oTable=oTable.parentElement;
	}

	var oTableHead=oTable.rows(0);
	
	//oTD所在的行
	var oTDRow=oTD;
	while(oTDRow.tagName!="TR"){
		oTDRow=oTDRow.parentElement;
	}
	
	//oTD在 行的索引
	//谁有更好的办法，请告知一下。cellIndex好像有问题。 
	var oTDidx=-1;
	for(var i=0;i<oTDRow.cells.length;i++){

		if(oTDRow.cells(i).innerHTML==oTD.innerHTML){
			oTDidx=i;
			break;
		}
	}
	if(oTDidx==-1){
		//无法定位所在行的行号
		return;
	}
	
	if (oTDRow.voucherid){
		//
		//第一列是表头，最后一列是页尾
	    for(var i=1;i<oTable.rows.length-1;i++){
			var oRow=oTable.rows(i);
			//判断凭证日期，凭证字，凭证编号是否都相同。
				if(oTDRow.voucherid==oRow.voucherid){
			  		//如果都相等，则刷新
			  		oRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
				}
		}
	}else{
		//没有定义TR属性，就通过CELL单元格确定凭证日期列；
	
		//====凭证日期的索引
		var idx=-1;
		//获得凭证日期的索引
		var cell;
		for(var i=0;i<oTableHead.cells.length;i++){
			cell=oTableHead.cells(i);
			if(cell.innerHTML.indexOf("凭证日期")>=0){
				idx=i;
				break;
			}
		}
	
		if(idx==-1){
			return;
		}
		//====开始刷新
	
	
		//alert(oTD.innerHTML);
		//alert(oTD.parentElement.cells(oTDidx).innerHTML+":::"+oTDidx);
	    //第一列是表头，最后一列是页尾
	    for(var i=1;i<oTable.rows.length-1;i++){
			var oRow=oTable.rows(i);
	
			//判断凭证日期，凭证字，凭证编号是否都相同。
			if(oTDRow.cells(idx).innerHTML==oRow.cells(idx).innerHTML
			  	&&oTDRow.cells(idx+1).innerHTML==oRow.cells(idx+1).innerHTML
			  	&&oTDRow.cells(idx+2).innerHTML==oRow.cells(idx+2).innerHTML){
			  	//如果都相等，则刷新
			  	oRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
			}
		}
	}
}

/* 以后可能需要用到的代码 */
//	var obj=document.getElementById("outA"+s);
//	//alert(obj.innerText);
//	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
//	oBao.open("POST","voucherquery/takeOutEntry.jsp?AutoID="+s,false);
//	oBao.send();
//	var strResult = unescape(oBao.responseText);



//	if(strResult.indexOf("ok")>=0)
//	{
//		obj.innerHTML="<font color=\"red\">撤</font>";
//                window.showModalDialog('voucherquery/takeoutPoint.jsp?autoid='+s,'dialogWidth:400px;dialogHeight:300px;dialogLeft:200px;dialogTop:150px;center:yes;help:no;resizable:no;status:no');
//	}
//        if(strResult.indexOf("back")>=0)
//	{
//		obj.innerHTML="抽";
//	}
