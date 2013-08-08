function _dtGetFieldLeft(oElem)
{
	var oEle = oElem;
	var vLeft = oEle.offsetLeft;
	while (oEle != oEle.offsetParent && oEle.offsetParent != null){
		oEle = oEle.offsetParent;
		vLeft += oEle.offsetLeft;
		vLeft -= oEle.scrollLeft; 
	}
	vLeft += window.screenLeft;
	return vLeft;
}

function _dtGetFieldTop(oElem)
{
	var oEle = oElem;
	var vTop = oEle.offsetTop;
	while (oEle != oEle.offsetParent && oEle.offsetParent != null){
		oEle = oEle.offsetParent;
		vTop += oEle.offsetTop;
		vTop -= oEle.scrollTop; 
	}
	vTop += window.screenTop;
	return vTop;
}


function _dtShowCalendar(target)
{
	var srcEle = document.all(target);
	
	var vLeft = _dtGetFieldLeft(srcEle);
	var vTop = _dtGetFieldTop(srcEle) + srcEle.offsetHeight;
	var vWidth = 300;
	var vHeight = 272;
	var oTarget = document.all(target);
	window.showModalDialog("/dlg/cal.html",oTarget, "dialogLeft:"+vLeft+"px;dialogTop:"+ vTop +
		"px;dialogWidth:"+vWidth+"px;dialogHeight:"+vHeight+"px;scroll:off");	
	oTarget.focus();
//	window.open("cal.html","_blank","left=" + vLeft + ", top="+ vTop + ", width=" + vWidth + 
//			", height=" +vHeight + ", location=0, menubar=0, toolbar=0, resizable = 0, scrollbars=0, status = 0");
}

function _dtCalendarField_doKeyPress()
{
	key = window.event.keyCode;
	srcElem = window.event.srcElement;
	if ((key >= 65 && key <= 90) ||
		(key >= 97 && key <= 122))
	{
		window.event.keyCode = 0;
		_dtShowCalendar(srcElem.uniqueID);
	}
}

var _dtWindowLoader = null;
function doLoadCalendar()
{
	if (_dtWindowLoader)
		_dtWindowLoader();
	var oInputs = document.all.tags("INPUT");
	for ( i = 0; i < oInputs.length; i++ )
	{
		var obj=document.getElementById("date_"+oInputs(i).uniqueID);
		if (oInputs(i).showcalendar && !obj) 
		{
			oInputs(i).insertAdjacentHTML("afterEnd", "<input type=button id=\"date_"+oInputs(i).uniqueID+"\" value=\"ѡ��\" class=\"flyBT\" style=\"height=22\" onclick=\"return showCalendar('"+oInputs(i).uniqueID+"', 'y-mm-dd');\">");  
			//oInputs(i).onkeypress= _dtCalendarField_doKeyPress;
		}
	} 
}

_dtWindowLoader = window.onload;
window.onload = doLoadCalendar;
window.onresizeend = doLoadCalendar;
