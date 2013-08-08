var ROWS_LIMIT=30;//最多允许的行数
var CURRENT_ROWS=1;//当前行数
var ACCOUNT_COUNT=0;//标准科目的数量
var CURRENT_SELECT_OBJ=null;//当前行对象
var COLOR_MOUSEON="#FFFFFF";
var COLOR_MOUSEOFF="#B0C4DE";
var SHOW_IF_ONLY_ME=true;
var AUTO_WIDTH = 280;
var AUTO_HEIGHT = 250;
var tempHeight = 0;
var NOINPUT_COLOR = "#FFFFB3";	//noinput文本框的颜色
var INPUT_COLOR = "#E8FFDF";	//下拉框底色

//USERDEF_REFRESHURL这个全局变量不是在本JS定义，
//而是作为预留在用户调用的页面里面定义；
//USERDEF_REFRESHURL
//如果没有定义，则刷新的时候会调用这个联结：

var TEMP_SYSTEM_WEB_ROOT = "/AuditSystem/";

if(MATECH_SYSTEM_WEB_ROOT && MATECH_SYSTEM_WEB_ROOT!="") {
	TEMP_SYSTEM_WEB_ROOT = MATECH_SYSTEM_WEB_ROOT;
}

var DEFAULT_REFRESHURL= TEMP_SYSTEM_WEB_ROOT + "AS_SYSTEM/hint.do";
var NOSHOW_HINT;
var _myHistory;

//堆栈和队列
//==============================================================
function DefineObject(Variable,VariableType)
 {
  return "var " + Variable + " = new " + VariableType + "(\""+Variable+"\")\;";
 }
 function CallEvent(EventName)
 {
  return "try\{if\(this\." + EventName + "\)\{this\." + EventName + "\(this\)\;\}\}catch\(e\)\{er.AddError\(new ErrorItem\(Name,\"" + EventName + " 调用失败! \"+e\)\)\;\}";
 }
 //非常规错误警告
 function MTError(PObject,PObjectName,PDescription)
 {
  //window.alert("\""+PObject+"\"对象"+PObjectName+"出错！出错原因："+PDescription)
 }
 function OnSError(sMsg,sUrl,sLine)
 {
  try
  {
   //window.alert("MTError: " + sMsg + "\nLine: " + sLine + "\nURL: " + sUrl);
  }
  catch(e)
  {
   window.alert("Sorry!Msg:"+e);
  }
  return false;
 }
 window.onerror = OnSError;
 //错误类型结构体
 function ErrorItem(PObject,PDescription,PLine,PURL,PType)
 {
  try
  {
   this.EObject  = new String(PObject);  //错误对象
   this.EDescription = new String(PDescription); //错误描述
   this.ELine   = new Number(PLine);  //错误行
   this.EType   = new Boolean(PType);  //错误类型
   this.EURL   = new String(PURL);   //错误地址
  }
  catch(e)
  {
   MTError(PObject,this.constructor,e);
  }
 }
 //常规错误对象
 function ErrorObject(PName)
 {
  try
  {
   //私有属性
   var Name = String(PName);
   var EObject = new Array(); //错误列表
   var Length = new Number(0);//列表长度
   //公有方法
   this.AddError = AddError; //添加错误码
   this.GetError = GetError; //错误函数
   this.GetLength = GetLength;//取得错误个数
   this.ToXML  = ToXML; //生成XML文档
   this.Clear = Clear;   //清理错误列表
   //事件
   this.OnError = null;//出错事件
   //方法的实现
   function AddError(PErrorItem)
   {
    try
    {
     try
     {
      if(PErrorItem.constructor == ErrorItem)
      {
       EObject[Length++] = PErrorItem;//录入错误元素
       try{if(this.OnError){this.OnError();}}catch(e){MTError(Name,"OnError",e);}
      }
     }
     catch(e)
     {
      MTError(Name,"PErrorItem",e);
     }
    }
    catch(e)
    {
     MTError(Name,"AddError",e);
    }
   }
   function GetError()
   {
    try
    {
     if(Length>0)
     {
      return EObject[Length-1]
     }
     else
     {
      return new ErrorItem();
     }
    }
    catch(e)
    {
     MTError(Name,"GetError",e);
    }
   }
   function GetLength()
   {
    try
    {
     return Length;
    }
    catch(e)
    {
     MTError(Name,"GetLength",e);
     return 0;
    }
   }
   function ToXML()
   {

   }
   function Clear()
   {
    try
    {
     Length = 0;
    }
    catch(e)
    {
     MTError(Name,"Clear",e);
    }
   }
  }
  catch(e)
  {
   MTError(Name,this.constructor,e);
  }

 }
 eval(DefineObject("er","ErrorObject"))
 er.OnError = MOnError;
 function MOnError()
 {
  window.alert(er.GetError().EObject+er.GetError().EDescription)
 }
 //列表对象
 function List(PName)
 {
  try
  {
   //私有属性
   var Name = PName
   var LObject = new Array();  //初始化数组对象
   var IsInit = new Boolean(); //是否初始化过
   var IsLoop = new Boolean(); //是否循环
   var Length = new Number(0); //列表长度
   var Cursor = new Number(0); //当前指针
   //公有方法
   this.GetCursor = GetCursor;//取得当前游标
   this.SetCursor = SetCursor;//设置当前游标
   this.GetLength = GetLength;//取得当前列表长度
   this.GetData = GetData; //取得当前元素
   this.Next  = Next;  //移动到下一个元素
   this.Previous = Previous; //移动到前一个元素
   this.First  = First; //移动到第一个元素
   this.Last  = Last;  //移动到最后一个元素
   this.Move  = Move;  //移动到某一位置的元素
   this.Insert  = Insert; //插入元素
   this.Update  = Update; //更新当前元素
   this.Delete  = Delete; //删除当前元素
   this.Search  = Search; //搜索当前元素位置
   this.Clear  = Clear; //清空
   //事件
   this.OnGetCursor = null ;//当取得当前游标
   this.OnSetCursor = null ;//当设置当前游标
   this.OnGetLength = null ;//当取得当前列表长度
   this.OnGetData  = null ;//当取得当前元素
   this.OnNext   = null ;//当移动到下一个元素
   this.OnPrevious  = null ;//当移动到前一个元素
   this.OnFirst  = null ;//当移动到第一个元素
   this.OnMove   = null ;//当移动到某一位置的元素
   this.OnLast   = null ;//当移动到最后一个元素
   this.OnInsert  = null ;//当插入元素
   this.OnUpdate  = null ;//当更新当前元素
   this.OnDelete  = null ;//当删除当前元素
   this.OnSearch  = null ;//当搜索当前元素位置
   this.OnClear  = null ;//当清空

   //公有方法的实现
   function GetCursor()
   {
    try
    {
     if(this.GetLength()==0)
     {
      Cursor = 0;
     }
     eval(CallEvent("OnGetCursor"));
     return Cursor;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"GetCursor调用失败"+e))
    }
   }
   function  SetCursor(PCursor)
   {
    try
    {
     var RCursor = new Number(PCursor);
     if(RCursor<0||isNaN(RCursor))
     {
      RCursor = 0;
     }
     if(RCursor>=Length)
     {
      RCursor = Length-1;
     }
     if(this.GetLength()==0)
     {
      RCursor = 0;
     }
     Cursor = RCursor;
     eval(CallEvent("OnSetCursor"));
     return Cursor;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"SetCursor调用失败"+e))
    }
   }
   function  GetLength()
   {
    try
    {
     eval(CallEvent("OnGetLength"));
     return Length;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"GetLength调用失败"+e))
    }
   }
   function  GetData(PCursor)
   {
    try
    {
     var TData = null;
     var TCursor = new Number(PCursor);
     if(this.GetLength()!=0)
     {
      if(!isNaN(TCursor))
      {
       if(TCursor>=0&&TCursor<this.GetLength())
       {
        TData = LObject[TCursor];
       }
      }
      else
      {
       TData = LObject[Cursor];
      }
     }
     eval(CallEvent("OnGetData"));
     return TData;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"GetData调用失败"+e))
    }
   }
   function  Next()
   {
    try
    {
     this.SetCursor(this.GetCursor()+1);
     eval(CallEvent("OnNext"));
     return this.GetData(Cursor);
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Next调用失败"+e))
    }
   }
   function  Previous()
   {
    try
    {
     this.SetCursor(this.GetCursor()-1);
     eval(CallEvent("OnPrevious"));
     return this.GetData(Cursor);
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Previous调用失败"+e))
    }
   }
   function  First()
   {
    try
    {
     this.SetCursor(0);
     eval(CallEvent("OnFirst"));
     return this.GetData(Cursor);
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"First调用失败"+e))
    }
   }
   function  Last()
   {
    try
    {
     this.SetCursor(Length-1);
     eval(CallEvent("OnLast"));
     return this.GetData(Cursor);
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Last调用失败"+e))
    }
   }
   function  Move(PCursor)
   {
    try
    {
     var TCursor = new Number(PCursor);
     if(!isNaN(TCursor))
     {
      this.SetCursor(TCursor);
     }
     eval(CallEvent("OnMover"));
     return this.GetData(Cursor);
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Move调用失败"+e))
    }
   }
   function  Insert(PData,PCursor)
   {
    try
    {
     var TCursor = new Number(PCursor);
     var i = Number(0);
     if(!isNaN(TCursor))
     {
      if(TCursor<this.GetLength())
      {
       this.SetCursor(TCursor);
      }
      else
      {
       Cursor = this.GetLength();
      }
     }
     for(i=this.GetLength();i>this.GetCursor();i--)
     {
      LObject[i] = LObject[i-1];
     }
     LObject[this.GetCursor()] = PData;
     Length++;
     eval(CallEvent("OnInsert"));
     return this.GetCursor();
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Insert调用失败"+e))
    }
   }
   function  Update(PData,PCursor)
   {
    try
    {
     var TCursor = new Number(PCursor);
     if(!isNaN(TCursor))
     {
      this.SetCursor(TCursor);
     }
     LObject[this.GetCursor()] = PData;
     eval(CallEvent("OnUpdate"));
     return this.GetCursor();
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Update调用失败"+e))
    }
   }
   function  Delete(PCursor)
   {
    try
    {
     var TCursor = new Number(PCursor);
     var TData = null;
     var i = Number(0);
     if(!isNaN(TCursor))
     {
      this.SetCursor(TCursor);
     }
     if(this.GetLength()!=0)
     {
      TData = LObject[this.GetCursor()];
      for(i=this.GetCursor();i<this.GetLength();i++)
      {
       LObject[i] = LObject[i+1];
      }
      Length--;
     }
     eval(CallEvent("OnDelete"));
     return TData;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Delete调用失败"+e))
    }
   }
   function  Search(PData)
   {
    try
    {
     var i = Number(0);
     for(i=0;i<this.GetLength();i++)
     {
      if(LObject[i]==PData)
      {
       eval(CallEvent("OnSearch"));
       return i;
      }
      else
      {
       try
       {
        if(LObject[i].Even(PData))
        {
         eval(CallEvent("OnSearch"));
         return i;
        }
       }
       catch(e)
       {

       }
      }
     }
     eval(CallEvent("OnSearch"));
     return -1;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Search调用失败"+e))
    }
   }
   function Clear()
   {
    try
    {
     Length = 0;
     Cursor = 0;
     eval(CallEvent("OnClear"));
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Clear调用失败"+e))
    }
   }
  }
  catch(e)
  {
   er.AddError(new ErrorItem(Name,"对象初始化失败！"))
  }
 }
 //队列处理对象
 function Queue(PName)
 {
  try
  {
   //私有属性
   var Name = PName;
   eval(DefineObject("QLObject","List"));
   //公有方法
   this.Pop = Pop;//出队列
   this.Push = Push;//进队列
   this.Clear = Clear;//清空
   //事件
   this.OnPop = null;
   this.OnPush = null;
   this.OnClear = null;
   //方法的实现
   function Pop()
   {
    try
    {
     var TData = null;
     QLObject.Last()
     TData = QLObject.Delete();
     eval(CallEvent("OnPop"));
     return TData;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Pop调用失败"+e))
    }
   }
   function Push(PData)
   {
    try
    {
     var TData = null;
     QLObject.First()
     TData = QLObject.Insert(PData);
     eval(CallEvent("OnPush"));
     return TData;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Push调用失败"+e))
    }
   }
   function Clear()
   {
    try
    {
     QLObject.Clear();
     eval(CallEvent("OnClear"));
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Clear调用失败"+e))
    }
   }
  }
  catch(e)
  {
   er.AddError(new ErrorItem(Name,"对象初始化失败！"))
  }
 }
 //栈处理对象
 function Stack(PName)
 {
  try
  {
   //私有属性
   var Name = PName;
   eval(DefineObject("SLObject","List"));
   //公有方法
   this.Pop = Pop;//出队列
   this.Push = Push;//进队列
   this.Clear = Clear;//清空
   //事件
   this.OnPop = null;
   this.OnPush = null;
   this.OnClear = null;
   //方法的实现
   function Pop()
   {
    try
    {
     var TData = null;
     SLObject.Last()
     TData = SLObject.Delete();
     eval(CallEvent("OnPop"));
     return TData;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Pop调用失败"+e))
    }
   }
   function Push(PData)
   {
    try
    {
     var TData = null;
     TData = SLObject.Insert(PData,SLObject.GetLength());
     eval(CallEvent("OnPush"));
     return TData;
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Push调用失败"+e))
    }
   }
   function Clear()
   {
    try
    {
     SLObject.Clear();
     eval(CallEvent("OnClear"));
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Clear调用失败"+e))
    }
   }
  }
  catch(e)
  {
   er.AddError(new ErrorItem(Name,"对象初始化失败！"))
  }
 }

//==========================堆栈和队列结束============================================


//==========================增加的专门为缓存后退而做的对象============================
//栈处理对象
 function MyHistory(iCount,strIds)
 {
  try
  {
   //私有属性
   var pCount = iCount;
   var pIds= strIds.split("~");
   var pStack= new Array(iCount);

   for (var i=0;i<iCount ;i++ )
   {
		pStack[i]=new Stack();
   }

   //公有方法
   this.Pop = Pop;//出队列
   this.Push = Push;//进队列
   this.Clear = Clear;//清空

	//通过ID来定位是第几个堆栈
   function locate(strId){
		for(var i=0;i<pCount;i++){
			if (pIds[i]==strId){
				return i;
			}
		}
		return -1;
   }

   //指定该堆栈弹出数据
   function Pop(strId)
   {
    try
    {
	 var i=locate(strId);
	 if (i>=0){
		var tData;
		tData=pStack[i].Pop();
		return tData;
	 }
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Pop调用失败"+e))
    }
	return null;
   }

	//指定该堆栈压入数据
   function Push(strId,PData)
   {
    try
    {
	 var i=locate(strId);
	 if (i>=0){
		pStack[i].Push(PData);
	 }else{
		//新的控件，还没有来得及初始化的,所以直接初始化一下
		pIds[pIds.length]=strId;
		pCount++;
		pStack[pStack.length]=new Stack();
		//因为上句话增加了一个数组单元，所以这里要-1；
		pStack[pStack.length-1].Push(PData);
	 }
    }
    catch(e)
    {
	alert(e);
     er.AddError(new ErrorItem(Name,"Pop调用失败"+e))
    }
   }

   function Clear(strId)
   {
	try
    {
	 var i=locate(strId);
	 if (i>=0){
		pStack[i].Clear();
	 }
    }
    catch(e)
    {
     er.AddError(new ErrorItem(Name,"Pop调用失败"+e))
    }
   }
  }
  catch(e)
  {
   er.AddError(new ErrorItem(Name,"对象初始化失败！"))
  }
 }
//==========================增加的专门为缓存后退而做的对象============================


//找到当前对象所在的行对象
function getTR() {
    var obj=event.srcElement;
    while (obj.tagName != "TR") {
        obj = obj.parentElement;
    }
    return obj;
}

function objMouseOver(obj) {
	obj.style.background=COLOR_MOUSEON;
}

function objMouseOut(obj) {
	obj.style.background=COLOR_MOUSEOFF;
}

function fMouseOver(){
    var el=getTR();
    el.style.background=COLOR_MOUSEON;
}

function fMouseOut(el){
    var el=getTR();
    el.style.background=COLOR_MOUSEOFF;
}

//隐藏列表框
function HideSelect(){
  var AUTO_HINT_IFRAME = document.getElementById("AUTO_HINT_IFRAME") ;
  AUTO_HINT_LIST.style.visibility = "hidden";
  AUTO_HINT_IFRAME.style.display = "none"; 
    //删除原来下载的内容
  var rowsLength=accTABLE.rows.length;
  for (var i=1;i<=rowsLength;i++){
    accTABLE.deleteRow(0);
  }
 // TTags(); //PY修改
}


//得到某个控件在页面上的坐标数组
function GetXY(aTag){
  var oTmp = aTag;
  var pt = new aPoint(0,0);
  do {
    pt.x += oTmp.offsetLeft -oTmp.scrollLeft;
  	pt.y += oTmp.offsetTop-oTmp.scrollTop;
  	oTmp = oTmp.offsetParent;
  } while(oTmp.tagName!="BODY");
  return pt;
}

//定义坐标对象
function aPoint(iX, iY){
	this.x = iX;
	this.y = iY;
}


//为了拴牢当前下拉列表框，将页面原有的select框隐藏
function ToggleTags(obj){
	/*
	with (document.all.tags("SELECT")){
	 	for (i=0; i<length; i++)
	 		if (TagInBound(item(i))){
	 			item(i).style.visibility = "hidden";
	 //			SeleTag[SeleTag.length] = item(i); //PY修改
	 		}
  	}*/
  	var AUTO_HINT_IFRAME = document.getElementById("AUTO_HINT_IFRAME") ;
  	//用iframe去挡住那个select的div
  	AUTO_HINT_IFRAME.style.width = obj.offsetWidth;   
    AUTO_HINT_IFRAME.style.height = obj.offsetHeight -20;   
    AUTO_HINT_IFRAME.style.top = obj.style.top;   
    AUTO_HINT_IFRAME.style.left = obj.style.left;   
    AUTO_HINT_IFRAME.style.zIndex = obj.style.zIndex - 1;   
 //   AUTO_HINT_IFRAME.style.display = "block";   
}

//显示下拉列表框 PY修改
function TTags(){
  with (document.all.tags("SELECT")){
 	for (i=0; i<length; i++)
 		if (TagInBound(item(i))){
 			item(i).style.visibility = "visible";
 //			SeleTag[SeleTag.length] = item(i);
 		}
  }
}

function TagInBound(aTag){
	return true;
  /*with (AUTO_HINT_LIST.style){
  	var l = parseInt(left);
  	var t = parseInt(top);
  	var r = l+parseInt(width);
  	var b = t+parseInt(height);
	var ptLT = GetXY(aTag);
	return !((ptLT.x>r)||(ptLT.x+aTag.offsetWidth<l)||(ptLT.y>b)||(ptLT.y+aTag.offsetHeight<t));
  }
  */
}

//弹出列表框
function onPopDivClick(SelectObj){

	CURRENT_SELECT_OBJ=SelectObj;

	if (!CURRENT_SELECT_OBJ) return;

	if (CURRENT_SELECT_OBJ.readOnly) return;

  	if (!CURRENT_SELECT_OBJ.autoid){
  		HideSelect();
  		setSelectionName("","当前输入框未定义autoid属性");
  		HideSelect();
  		return;
  	}

	//如果设置下拉列表的高
	if(CURRENT_SELECT_OBJ.autoHeight) {
		tempHeight = CURRENT_SELECT_OBJ.autoHeight;
	} else {
		tempHeight = AUTO_HEIGHT;
	}

	//如果设置下拉列表的宽
	if(CURRENT_SELECT_OBJ.autoWidth) {
		AUTO_HINT_LIST.style.width = CURRENT_SELECT_OBJ.autoWidth;
		document.getElementById("AUTO_HINT_IFRAME").style.width = CURRENT_SELECT_OBJ.autoWidth;
	} else {
		AUTO_HINT_LIST.style.width = AUTO_WIDTH;
		document.getElementById("AUTO_HINT_IFRAME").style.width = AUTO_WIDTH;
	}

	//激活窗口
	if(onKeyUpEvent()) {

		//检查事件激发对象以及坐标
		event.cancelBubble=true;

		//var SelectObj=event.srcElement;
		var point = GetXY(SelectObj);
		//记录当前选中的对象

		//调整坐标到
		with (AUTO_HINT_LIST.style) {
	  		left = point.x-0;
	  		top  = point.y+SelectObj.offsetHeight+2;
	  		width = AUTO_HINT_LIST.offsetWidth;
	  		height = AUTO_HINT_LIST.offsetHeight;
	  		
	  		ToggleTags(AUTO_HINT_LIST);	//隐藏下拉列表框
	  		visibility = 'visible';
	  	}
		AUTO_HINT_LIST.scrollTop=0;
	}


}

//键盘按下
function onKeyDownEvent() {

	if (!CURRENT_SELECT_OBJ) return;

	/*
	//多选不允许输入，只能选择
	try {
		if (CURRENT_SELECT_OBJ&&CURRENT_SELECT_OBJ.multiselect){
	    	event.returnValue = false;
	        return false;
    	}
	} catch(e) {
		//
	}
	*/


	//不是多选则继续
	var obj=event.srcElement;

    switch (event.keyCode) {
      case 13: // Enter
        HideSelect();
        break;
      case 8:  // Backspace
		if (obj.multilevel){
			if (obj.value==document.selection.createRange().text){
				//alert('用户想要全部删除');
				var objName=getName(CURRENT_SELECT_OBJ);
				//记录已经选中的值，已被回退
				_myHistory.Clear(objName);
			}
		}else{
			if (obj.noinput=="true"  && obj.value !=document.selection.createRange().text){
				event.returnValue = false;
		        return false;
			}
		}
      case 9:  // Tab
      case 35: // End
      case 36: // Home
      case 37: // Left Arrow
      case 39: // Right Arrow
      case 46: // Del
		if (obj.multilevel){
			if (obj.value==document.selection.createRange().text){
				//alert('用户想要全部删除');
				var objName=getName(CURRENT_SELECT_OBJ);
				//记录已经选中的值，已被回退
				_myHistory.Clear(objName);
			}
		}else{
			//不是全选删除方式
			if (obj.noinput=="true"  && obj.value !=document.selection.createRange().text){
				event.returnValue = false;
		        return false;
			}
		
		}

      return true;
    }

    if (obj.noinput=="true" ){
    	event.returnValue = false;
        return false;
    }else if (obj.valuemustbenumber=="true"){
    	// 只处理 '0'～'9'，其它键忽略掉
		if (!(event.keyCode >= 48 && event.keyCode <= 57 ) && !( event.keyCode>=96 && event.keyCode<=105 )) {
			event.returnValue = false;
        	return false;
    	}
    } else {
		event.returnValue = true;
	}

}

//键盘弹起
function onKeyUpEvent(){
    if (event.keyCode==13 || event.keyCode==9) return;
    var pk1=event.srcElement.value;
    SHOW_IF_ONLY_ME=true;

	//event.srcElement.value = pk1.replace(/(\\|\$)*/g,'');
	//屏蔽非法字符
	pk1 = pk1.replace(/(\\|\$)*/g,'');

    return ajax_select_refresh(pk1);
}

//设置提示列表后台刷新
function ajax_select_refresh(pk1){

  try{

	if (!CURRENT_SELECT_OBJ) return;

  	if (!CURRENT_SELECT_OBJ.autoid){
		CURRENT_SELECT_OBJ=null;
  		HideSelect();
  		setSelectionName("","当前输入框未定义autoid属性");
  		HideSelect();
  		return;
  	}

	/*
	if(accTBODY.innerHTML!="") {
		alert(accTBODY.innerHTML);
	}
	*/
  	//开始下载
	accTBODY.addBehavior("#default#download");
	var suburl;
	try{
		suburl=USERDEF_REFRESHURL;
	}catch(e){
		//如果没有定义USERDEF_REFRESHURL，就使用默认的；
		//alert('如果没有定义USERDEF_REFRESHURL，就使用默认的；');
		suburl=DEFAULT_REFRESHURL+"?autoid="+CURRENT_SELECT_OBJ.autoid;
		if (pk1!=""){
			suburl+="&pk1="+pk1;
		};
		if (CURRENT_SELECT_OBJ.refer){
			var qqq = document.getElementById(CURRENT_SELECT_OBJ.refer);

			//如果qqq是网页元素
			if(qqq) {
				if (qqq.tagName.toUpperCase()=="INPUT" && qqq.type.toUpperCase()=="FILE" ){
					//file类型的
					if (qqq.value!=null && qqq.value!=""){
						//暂是只支持装载文件名
						var _filename =qqq.value.substring(qqq.value.lastIndexOf('\\')+1);
						_filename = _filename.substring(0,_filename.indexOf("_"));
						//alert(_filename);
						suburl += "&refer="+_filename;
					}
				}else{
					//非file类型的INPUT等
					if (qqq.value!=null && qqq.value!=""){
						suburl += "&refer="+qqq.value;
					}
				}
			} else {
				suburl += "&refer=" + CURRENT_SELECT_OBJ.refer;
			}

		/*	else{
				if (qqq.title!=null && qqq.title!=""){
					suburl="请先完成："+qqq.title;
				}else{
					suburl="请先完成："+CURRENT_SELECT_OBJ.refer;
				}
			}
		*/
		}
		if (CURRENT_SELECT_OBJ.refer1){
			var qqq1=document.getElementById(CURRENT_SELECT_OBJ.refer1);

			//如果qqq1是网页元素
			if(qqq1) {
				if (qqq1.value!=null && qqq1.value!=""){
					suburl += "&refer1="+qqq1.value;
				}
			} else {
				suburl += "&refer1=" + CURRENT_SELECT_OBJ.refer1;
			}


		/*	else{
				if (qqq.title!=null && qqq.title!=""){
					suburl="请先完成："+qqq.title;
				}else{
					suburl="请先完成："+CURRENT_SELECT_OBJ.refer1;
				}
			}
		*/
		}
		if (CURRENT_SELECT_OBJ.refer2){
			var qqq2=document.getElementById(CURRENT_SELECT_OBJ.refer2);

			//如果qqq2是网页元素
			if(qqq2) {
				if (qqq2.value!=null && qqq2.value!=""){
					suburl += "&refer2="+qqq2.value;
				}
			} else {
				suburl += "&refer2=" + CURRENT_SELECT_OBJ.refer2;
			}

		}
		if(CURRENT_SELECT_OBJ.refer){
		//	var qqq=document.getElementById(CURRENT_SELECT_OBJ.refer);
			if(qqq) {
				if (qqq.value==null || qqq.value==""){
					if (qqq.title!=null && qqq.title!=""){
						suburl="请先完成："+qqq.title;
					}else{
						suburl="请先完成："+CURRENT_SELECT_OBJ.refer;
					}
				}
			}
		}
		if(CURRENT_SELECT_OBJ.refer1){
		//	var qqq=document.getElementById(CURRENT_SELECT_OBJ.refer1);
			if(qqq1) {
				if (qqq1.value==null || qqq1.value==""){
					if (qqq1.title!=null && qqq1.title!=""){
						suburl="请先完成："+qqq1.title;
					}else{
						suburl="请先完成："+CURRENT_SELECT_OBJ.refer1;
					}
				}
			}
		}
		if(CURRENT_SELECT_OBJ.refer2){
		//	var qqq2=document.getElementById(CURRENT_SELECT_OBJ.refer2);
			if(qqq2) {
				if (qqq2.value==null || qqq2.value==""){
					if (qqq2.title!=null && qqq2.title!=""){
						suburl="请先完成："+qqq2.title;
					}else{
						suburl="请先完成："+CURRENT_SELECT_OBJ.refer2;
					}
				}
			}
		}

	}
	if (suburl.indexOf("请先完成")>=0){
		//报错，提示用户先选择关联项，再选择本项目，然后返回
		alert(suburl);

		try {
			var qqq=document.getElementById(CURRENT_SELECT_OBJ.refer);
			qqq.focus();
			onPopDivClick(qqq);
		} catch(e) {}

		return false;

	}else{
		createXmlHttp();
		xmlHttp.open("POST", suburl , true);
		xmlHttp.onreadystatechange = onDownload;
		xmlHttp.send(null);
		//accTBODY.startDownload(suburl,onDownloadSelectHintDone);

		return true;
	}

  }catch(ex){}
}

//-------------------------------------------
// 在 Microsoft 浏览器上创建 XMLHttpRequest 对象
//-------------------------------------------
function createXmlHttp() {
	try {
		xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			xmlHttp = false;
		}
	}
}


function onDownload(){
	if(xmlHttp.readyState == 4) {
		if(xmlHttp.status == 200) {
			onDownloadSelectHintDone(unescape(xmlHttp.responseText));
		}
	}

}

//下载科目完成执行的函数
//s=OK|pk1|code`name|code`name
function onDownloadSelectHintDone(s){

  if (!CURRENT_SELECT_OBJ) return;

  var lineArray = s.split("|");
  if (lineArray[0] == "ERROR") {
    alert("操作失败:+"+lineArray[1]);
    return;
  }
  
  var theSelectCode="";
  var theDownSelectCode="";
  var objName=getName(CURRENT_SELECT_OBJ);

  //记录之前选择的值
  var preSelectCode=theSelectCode;
  //当前选择的值
  theSelectCode=lineArray[1];

  //删除原来下载的内容
  var rowsLength=accTABLE.rows.length;
  for (var i=1;i<=rowsLength;i++){
    accTABLE.deleteRow(0);
  }

  var allHTML="";

  //构造回到上级的菜单
  if (CURRENT_SELECT_OBJ.multilevel){
		if ( theSelectCode!="" && theSelectCode!="null"){
			allHTML+="<TR height=\"18\" style=\"CURSOR: hand\" noWrap align=center onmouseover='fMouseOver()' onclick='checkedBackSpaceSelection()' onmouseout='fMouseOut()' bgColor="+COLOR_MOUSEOFF+" pk1='"+preSelectCode+"' pk1_name='"+preSelectCode+"'> <TD noWrap align=middle colSpan=3><FONT color=red><CODE>回到上一选项</CODE></FONT></TD></TR> ";
		}
  }

  //如果是多级，那么选中最后一级的情况下，总是补全一个单元格，让其好显示

	//winner add on 20071103 for hint
	try{
		var autoselect_lastbutton_html = "";
		var _value = "关  闭";

		if (CURRENT_SELECT_OBJ.multiselect){
			_value = "确定多选";
		}

		autoselect_lastbutton_html = "<span onclick='_doClose();' style='border: 1px solid #EEEEEE; width:100%; border-top: 0px;cursor:hand;text-align: center;padding:2px;' onMouseOver='objMouseOver(this);' onMouseOut='objMouseOut(this);' >" + _value +"</span>";

		document.getElementById("_autoselect_lastbutton_").innerHTML=autoselect_lastbutton_html;
	}catch(e){}

  var num=0;

  try {

 		var strnoWrap="";
		if (CURRENT_SELECT_OBJ.canWrap!="true") {
			strnoWrap=" noWrap ";
		}

		//如果是多选，就构造多选的复选框
		if (CURRENT_SELECT_OBJ.multiselect) {
			var curTxtValue = CURRENT_SELECT_OBJ.value;
			curTxtValue = "," + curTxtValue + ",";

			allHTML += "<TR height=\"18\"  style=\"CURSOR: hand\" bgColor="+COLOR_MOUSEOFF+">"
					+ "<TD noWrap width=\"10%\">"
					+ "	<INPUT type=checkbox id=_sekectAllCheckBox onclick='_multiselectAll();' value=\"\" name=_sekectAllCheckBox></TD>"
					+ "<TD style=\"COLOR: red\" noWrap align=middle colSpan=2>"
					+ "<label for='_sekectAllCheckBox' onclick='_multiselectAll();' style='font-weight:normal;width:50%;height:22px; border-right:1px solid #eeeeee;cursor:hand;text-align: center;padding:2px;' onMouseOver='objMouseOver(this);' onMouseOut='objMouseOut(this);' >全  选</label>"
					+"<span onclick='_doClear();' style='width:50%;height:22px; cursor:hand;text-align: center;padding:2px;' onMouseOver='objMouseOver(this);' onMouseOut='objMouseOut(this);' >清  空</span>"
					+ "</TD>"
					+ "</TR> ";




			for (var i=2; i<lineArray.length-1; i++) {
				num++;
				txtArray = lineArray[i].split("`");
				theDownSelectCode=txtArray[0];


				var strChecked="";
				if(( ','+curTxtValue+ ',').indexOf( ','+txtArray[0] + ',') >0 || curTxtValue==txtArray[0]) {
					strChecked=" checked=true ";
				}

				//
				allHTML+="<TR height=\"18\" title='请选择完成后点击[确定]按钮' style=\"CURSOR: hand\" bgColor="+COLOR_MOUSEOFF+" pk1='"+txtArray[0]+"' pk1_name='"+txtArray[1]+"'><TD noWrap width=\"10%\"><INPUT type=checkbox  "+strChecked+" value='"+txtArray[0]+","+txtArray[1]+"' name=ajaxpk1></TD> <TD noWrap width=\"30%\">『"+txtArray[0]+"』</TD> <TD "+strnoWrap+">"+txtArray[1]+"</TD></TR> ";

		  	}


		}else{
			allHTML+="<TR height=\"18\" style=\"CURSOR: hand\" bgColor="+COLOR_MOUSEOFF+" onmouseover=objMouseOver(this); onclick=_doClear(); onmouseout=objMouseOut(this);><TD style=\"COLOR: red\" noWrap align=center colSpan=3><SPAN >清 空</SPAN></TD></TR> ";

			for (var i=2; i<lineArray.length-1; i++) {
				//统计总共有多少行
				num++; 


				txtArray = lineArray[i].split("`");
				theDownSelectCode=txtArray[0];

				allHTML+="<TR height=\"18\" style=\"CURSOR: hand\" onmouseover='fMouseOver()' onmouseout='fMouseOut()' onclick='checkedOneSelection()' bgColor="+COLOR_MOUSEOFF+" pk1='"+txtArray[0]+"' pk1_name='"+txtArray[1]+"'><TD noWrap width=\"30%\">『<CODE>"+txtArray[0]+"</CODE>』</TD><TD "+strnoWrap+"><CODE>"+txtArray[1]+"</CODE></TD></TR>";

			}
		}


		allHTML="<table border='0' cellspacing='1' cellpadding='0'  bgcolor='#EEEEEE' width='100%' id='accTABLE'><TBODY id=accTBODY>"+allHTML+"</TBODY></table>";
		spanaccTABLE.innerHTML=allHTML;


		var accTable = document.getElementById("accTable");

		//alert((accTable.rows.length+1)*(accTable.rows[1].height));
		var actualHeight = (accTable.rows.length+2)*(accTable.rows[1].height);

		if (CURRENT_SELECT_OBJ.multilevel){
			actualHeight = actualHeight + 6;
		} else if (CURRENT_SELECT_OBJ.multiselect) {
			actualHeight = actualHeight + 13;
		} else {
			actualHeight = actualHeight + 8;
		}


		//调整高度
		if(tempHeight<actualHeight) {
			document.getElementById("AUTO_HINT_LIST").style.height = tempHeight;
			document.getElementById("AUTO_HINT_IFRAME").style.height = tempHeight -20;
		} else {
			document.getElementById("AUTO_HINT_LIST").style.height = actualHeight;
			document.getElementById("AUTO_HINT_IFRAME").style.height = actualHeight - 20;
		}
		
		//调整位置
		var bodyHeight = document.body.clientHeight ;
		var top = document.getElementById("AUTO_HINT_LIST").style.top ;
		var height = document.getElementById("AUTO_HINT_LIST").style.height ;
  		//alert("top:"+top+" heigth:"+height + " bodyHeight:"+bodyHeight);
  		if(parseInt(top)+parseInt(height) > bodyHeight) {
  			//如果超出的页面的高度,则下拉框显示在文本框的上方
  			top = parseInt(top) - parseInt(height) - 8;
  			document.getElementById("AUTO_HINT_LIST").style.top = top ;
  			document.getElementById("AUTO_HINT_IFRAME").style.top = top ;
  		}
		
		document.getElementById("AUTO_HINT_IFRAME").style.display = "block";
		//如果满足隐藏提示的条件
		if (!SHOW_IF_ONLY_ME ){
	 		//如果定义了多级，则还要检查在选定了最后的一个叶子节点后才能隐藏
	 		if (CURRENT_SELECT_OBJ.multilevel){
				if ( theDownSelectCode==theSelectCode || num==0) HideSelect();
			}else{
				//如果没有定义多级，则满足隐藏条件就可以隐藏了
		   		HideSelect();
			}
		}else if (num==0){
			if (!CURRENT_SELECT_OBJ.multilevel){
					//不是多级的情况下，才隐藏；
			 		HideSelect();
		  	}
		}else{
			//一开始什么都没有选的时候
		   	AUTO_HINT_LIST.style.visibility = "visible";
		}
	} catch(e) {

	}

}

//处理回退事件
function checkedBackSpaceSelection(){
  try{

  	if (!CURRENT_SELECT_OBJ) return;

	var oTR=getTR();
    var pk1=oTR.pk1;
    var pk1_name=oTR.pk1_name;

	var objName=getName(CURRENT_SELECT_OBJ);

	//记录已经选中的值，已被回退
	pk1=_myHistory.Pop(objName);
	pk1=_myHistory.Pop(objName);
	if (pk1==null)
	{pk1="";
	}

    setSelectionValueAndName(pk1,pk1_name);

    SHOW_IF_ONLY_ME=false;
    ajax_select_refresh(pk1);
  }catch(ex){}
}

//选中列表框的值，填入文本框，并且隐藏列表框
function checkedOneSelection(){
  try{

  if (!CURRENT_SELECT_OBJ) return;

	var oTR=getTR();
    var pk1=oTR.pk1;
    var pk1_name=oTR.pk1_name;
	setSelectionValueAndName(pk1,pk1_name);

		if (CURRENT_SELECT_OBJ.multilevel){
			var objName=getName(CURRENT_SELECT_OBJ);
			//记录已经选中的值，已被回退
			_myHistory.Push(objName,pk1);
		}

      	SHOW_IF_ONLY_ME=false;
      	ajax_select_refresh(pk1);
  }catch(ex){}
}

function getAdviceName(obj){
	return 'advice-'+obj.name;
}

function getAdviceId(obj){
	return 'advice-'+obj.id;
}

function getAdviceObj(obj){
	var adviceobj;
	try{

		//查找对应的值
		var adviceId = "" ;
		var objId = "" ;
		if(!obj.id) {
			adviceId = getAdviceName(obj) ;
			objId = obj.name
		}else {
			adviceId = getAdviceId(obj) ;
			objId = obj.id ;
		}
		adviceobj=document.getElementById(adviceId);
		
		if(obj.clone) {
			return obj ;
		}
		if (adviceobj==null){ 
			var useAdvice = obj.useAdvice ;  //用第二个字段放到输入框里去
			//没有找到就创建1个
			if(useAdvice) {
				//没有找到就创建1个
				if (adviceobj==null){
					adviceobj = document.createElement('span');
					adviceobj.id = getAdviceName(obj);
					obj.parentNode.insertBefore(adviceobj, obj.nextSibling);
				}
			}else {
					var adviceobj = obj.cloneNode();
					var objName =  getAdviceName(obj);
					adviceobj.id = adviceId ;
					adviceobj.removeAttribute("name");
					adviceobj.clone = true ;
					adviceobj.cloneObj = obj;
					adviceobj.cloneId = objId;
					
					obj.parentNode.insertBefore(adviceobj, obj.nextSibling);
					
					if(obj.hideresult || obj.multiselect) {
						adviceobj.style.display = "none" ;
					}else {
						obj.style.display = "none" ; 
					}
			}
		}
		
	}catch(e){
		alert("出错了"+e.description);
	}
	return adviceobj;
}

//将选择的代码和名称写到页面上
function setSelectionValueAndName(pk1,pk1_name){
	

    if (!CURRENT_SELECT_OBJ) return;

  	//如果设置了hideresult就不显示NAME提示
  	if (CURRENT_SELECT_OBJ.hideresult=="true" && CURRENT_SELECT_OBJ.useAdvice){
  		
  	}else
  		setSelectionName(pk1,pk1_name);

	//更新选中值,如果一样，就放弃退出
	if (CURRENT_SELECT_OBJ.value!=pk1){
		
		if(CURRENT_SELECT_OBJ.clone) {
			var cloneObj = CURRENT_SELECT_OBJ.cloneObj;
			if(!cloneObj) {
				cloneObj = document.getElementById(CURRENT_SELECT_OBJ.cloneId) ;
			}
			cloneObj.value = pk1 ;
			CURRENT_SELECT_OBJ.value=pk1_name;
		}else {
			CURRENT_SELECT_OBJ.value=pk1;
		}
		//如果设置了关联项目，则设置关联项目的VALUE为空；
		if (CURRENT_SELECT_OBJ.refreshtarget){
			try{
				var qqq=document.getElementById(CURRENT_SELECT_OBJ.refreshtarget);
				if (qqq){
					qqq.value="";
				}

				qqq=document.getElementById("advice-"+CURRENT_SELECT_OBJ.refreshtarget);
				
				if (qqq){
					if(qqq.clone) {
						qqq.value="";
					}else {
						qqq.innerHTML="";
					}
					
				}


			}catch(e){}
		}

		//如果设置了关联项目，则设置关联项目的VALUE为空；
		if (CURRENT_SELECT_OBJ.refreshtarget1){
			document.getElementById(CURRENT_SELECT_OBJ.refreshtarget1).value="";
		}
		//如果设置了关联项目，则设置关联项目的VALUE为空；
		if (CURRENT_SELECT_OBJ.refreshtarget2){
			document.getElementById(CURRENT_SELECT_OBJ.refreshtarget2).value="";
		}
		//激发选定输入框的onchange事件，以备他用
		try {
			if(CURRENT_SELECT_OBJ.clone) {
				CURRENT_SELECT_OBJ.cloneObj.onchange();
			}else {
				CURRENT_SELECT_OBJ.onchange();
			}
		}catch(e){}

	}
	
	if(CURRENT_SELECT_OBJ.clone) {
		CURRENT_SELECT_OBJ.value=pk1_name;
		var cloneObj = CURRENT_SELECT_OBJ.cloneObj;
		if(!cloneObj) {
			cloneObj = document.getElementById(CURRENT_SELECT_OBJ.cloneId) ;
		}
		cloneObj.value = pk1;
	}
	
  
}

//设置提示值
function setSelectionName(pk1,pk1_name){
  try{
	if (!CURRENT_SELECT_OBJ) return;

	//获得显示提示信息的对象
	var adviceobj=getAdviceObj(CURRENT_SELECT_OBJ);
	
	//显示提示信息，如果显示名、值一样，就不显示了
	
	if(CURRENT_SELECT_OBJ.useAdvice) {
		adviceobj.innerHTML=(pk1==pk1_name ?"" :pk1_name);
	}else {
		
		if(CURRENT_SELECT_OBJ.clone) {
			var cloneObj = document.getElementById(CURRENT_SELECT_OBJ.cloneId) ;
		//	alert(CURRENT_SELECT_OBJ.cloneId);
			cloneObj.value = pk1;
			CURRENT_SELECT_OBJ.value=pk1_name;
			
		}else {
			adviceobj.value = pk1_name ;
			//CURRENT_SELECT_OBJ.onchange();
		}
	}
	return;
  }catch(ex){}
}


with (document) {

	write("<Div id=AUTO_HINT_LIST onclick='event.cancelBubble=true'  style='POSITION:absolute; z-index:999999;visibility:hidden;OVERFLOW-Y:auto;OVERFLOW-X:auto;'>");
	write("		<table border='0' cellspacing='1' cellpadding='0'  bgcolor='#6595d6' width='100%'>");
	write("			<tr>");
	write("				<td  nowrap='nowrap'>");

	write("					<span  width='100%' id='spanaccTABLE'><table border='0' cellspacing='1' cellpadding='0'  bgcolor='#EEEEEE' width='100%' id='accTABLE'>");
	write("  					<tbody id='accTBODY'>");
	write("  					</tbody>");
	write("  					</tbody>");
	write("					</table></span>");

	write("<table border='0' cellspacing='0' cellpadding='0'  bgcolor='#B0C4DE' width='100%'>");
	write("  <tr height=18>");
	write("  	<td align='center' nowrap='nowrap' id='_autoselect_lastbutton_' style='color:red;'>");
	write("  		<div border: 1px solid #EEEEEE; border-top: 0px;cursor:hand;text-align: center;padding:2px; onMouseOver='objMouseOver(this);' onMouseOut='objMouseOut(this);'  onclick='_doClose();'>");
	write("关  闭");
	write("			</div></td></tr>");
	write("</table>");
	write("</td>");
	write("</tr>");
	write("</table>");
	write("</Div>");
	
	write("<iframe id=AUTO_HINT_IFRAME src=\"javascript:false;\"  scrolling=\"no\" frameborder=\"0\" style=\"position:absolute;top:0px;left:0px; display:none;\"></iframe>") ;

	//修改让多选的时候点击其他地方也就完成了选择
	write("<SCRIPT event=onclick() for=document>_doClose()</SCRIPT>");
}

function _doClose(){
	//如果是多选，就设置返回值
	if (CURRENT_SELECT_OBJ && CURRENT_SELECT_OBJ.multiselect && !CURRENT_SELECT_OBJ.readOnly){
		checkedOneSelection2();
	}
	//隐藏
	HideSelect();

	//把当前选中对话框置为空
	CURRENT_SELECT_OBJ=null;

}

function getAdvice(id){
	try{
		return document.getElementById('advice-' + id);
	}catch(e){
		return null;
	}
}

//取得对象的名字，如果有定义ID，就返回ID，否则返回NAME
function getName(obj){
	if (obj!=null)
	{
		if (obj.id)
		{
			return obj.id
		}else{
			if (obj.name)
			{
				return obj.name
			}else{
				return "未命名"
			}
		}
	}
}

//将选择的代码和名称写到页面上
function setSelectionValueAndName2(){
  try{
    var multi_Pk1=setSelectionName2();
    //更新选中值,如果一样，就放弃退出
    if (CURRENT_SELECT_OBJ!=null){
		CURRENT_SELECT_OBJ.value=multi_Pk1;
		//如果设置了关联项目，则设置关联项目的VALUE为空；
		if (CURRENT_SELECT_OBJ.refreshtarget){
		  var qqq=document.getElementById(CURRENT_SELECT_OBJ.refreshtarget);
		  qqq.value="";
		}

		//如果设置了关联项目，则设置关联项目的VALUE为空；
		if (CURRENT_SELECT_OBJ.refreshtarget1){
			document.getElementById(CURRENT_SELECT_OBJ.refreshtarget1).value="";
		}
		//如果设置了关联项目，则设置关联项目的VALUE为空；
		if (CURRENT_SELECT_OBJ.refreshtarget2){
			document.getElementById(CURRENT_SELECT_OBJ.refreshtarget2).value="";
		}

		//激发选定输入框的onchange事件，以备他用
		CURRENT_SELECT_OBJ.onchange();
    }
  }catch(ex){}
}

//多选框全选
function _multiselectAll() {

	var chkObj = document.getElementById("_sekectAllCheckBox");

	var ajaxpk=document.getElementsByName("ajaxpk1");
	for(var i=0; i < ajaxpk.length; i++) {
		ajaxpk[i].checked = chkObj.checked;
	}
}

//清空文本框值
function _doClear() {
	try {
		//清空文本框值
		CURRENT_SELECT_OBJ.value = "";
		
		var advicObj = getAdvice(CURRENT_SELECT_OBJ.name);

		//清空提示值
		if(advicObj) {
			if(advicObj.useAdvice) {
				advicObj.innerHTML = "";
			}else {
				advicObj.value = "";
			}
		}
		
		if(CURRENT_SELECT_OBJ.clone) {
			//如果我是克隆出来的，就清空前面的值
			var cloneObj = CURRENT_SELECT_OBJ.cloneObj ;
			if(!cloneObj) {
				cloneObj = document.getElementById(CURRENT_SELECT_OBJ.cloneId) ;
			}
			
			cloneObj.value = "" ;
		}

		//刷新列表
		ajax_select_refresh('');

		//清空多选
		var ajaxpk = document.getElementsByName("ajaxpk1");

		for(var i=0; i < ajaxpk.length; i++) {
			ajaxpk[i].checked = false;
		}

		var _sekectAllCheckBox = document.getElementById("_sekectAllCheckBox");

		if(_sekectAllCheckBox) {
			_sekectAllCheckBox.checked = false;
		}
	} catch(e) {
	}
}

//设置所选择的提示值
function setSelectionName2(){
  var multi_Pk1 = "";
  try{
    if (CURRENT_SELECT_OBJ) {

		var ajaxpk=document.getElementsByName("ajaxpk1");
		var pklen= ajaxpk.length;
		var ischoice;
		var pk1Values;
		var pk1Value="";
		var i;
		ischoice=false;
		for (i = 0;i< pklen;i++){
		  if (ajaxpk[i].checked==true) {
			pk1Values = ajaxpk[i].value.split(",");
			multi_Pk1 = multi_Pk1+pk1Values[0] + ',';
			pk1Value = pk1Value + pk1Values[1]+";";
		  }
		}
	}

	var t=multi_Pk1.length;
	//alert(multi_Pk1.substring(0,t-1));
	if (t>0 && multi_Pk1.substring(t-1,t)==",")
	{
		multi_Pk1=multi_Pk1.substring(0,t-1);
	}

	return multi_Pk1;
  }catch(ex){}
}

//选中列表框的值，填入文本框，并且隐藏列表框
function checkedOneSelection2(){
  try{
    setSelectionValueAndName2();
    SHOW_IF_ONLY_ME=false;
    //refresh2(pk1);
  }catch(ex){}
}

//点击复选框列表的时候切换
function switchOneSelection(){
  try{
    var oTR=getTR();

	if (oTR && event.srcElement.tagName!="INPUT")
	{
		var inputCheckbox=oTR.cells[0].childNodes[0];
		if (inputCheckbox)
		{
			inputCheckbox.checked =!inputCheckbox.checked;
		}
	}
  }catch(ex){}
}

function dtRestoreHintLoader()
{	
	var oInputs = document.all.tags("INPUT");
	var autoMultiCount=0;
	var strInputNames="";
	for ( i = 0; i < oInputs.length; i++ ) {
		//if(oInputs(i).readOnly == true) {
		//	continue;
		//}

		if (oInputs(i).autoid && !oInputs(i).clone){
			try{
				//关闭自动完成
				oInputs(i).autocomplete="off";
			}catch(e){}

			if (oInputs(i).multilevel)
			{
				//统计页面里面多层选择的输入框的个数
				autoMultiCount++;

				if (autoMultiCount==1)
				{
					strInputNames=getName(oInputs(i))
				}else{
					strInputNames+="~"+getName(oInputs(i))
				}
			}

			/*
			不知道有参数的函数映射，暂时先不提供自动挂接功能；
			oInputs(i).onkeydown=onKeyDownEvent;
			oInputs(i).onkeyup=onKeyUpEvent;
			oInputs(i).onclick=onPopDivClick(this);
			*/

			//如果文本框不允许输入,则改变文本框颜色
			if (oInputs(i).readOnly==false){
				if(oInputs(i).noinput=="true") {
					oInputs(i).style.backgroundColor = NOINPUT_COLOR;
					//oInputs(i).style.borderColor = NOINPUT_COLOR;
				} else {
					oInputs(i).style.backgroundColor = INPUT_COLOR;
				}
			}
			
			if (oInputs(i).value != null && oInputs(i).value !=""
				&& (!oInputs(i).norestorehint || !oInputs(i).norestorehint=="true")) {
				//如果没有禁止刷新，且初始值有内容，就后台取数；

				//var url="/AuditSystem/AS_SYSTEM/auto_hint_select.jsp?checkmode=1&autoid="+oInputs(i).autoid+"&pk1=" + oInputs(i).value;
				var url=DEFAULT_REFRESHURL + "?checkmode=1&autoid="+oInputs(i).autoid+"&pk1=" + oInputs(i).value;
					//进行后台校验

				//取引用值
				var referValue="";
				if(oInputs(i).refer){
					var oRefer=document.getElementById(oInputs(i).refer);
					if(oRefer && oRefer.value!=null&&oRefer.value!=""){
						referValue=referValue+"&refer="+oRefer.value;
					} else {
						referValue = referValue + "&refer=" + oInputs(i).refer;
					}
				}
				
				if(oInputs(i).refer1){
					var oRefer=document.getElementById(oInputs(i).refer1);
					if(oRefer && oRefer.value!=null&&oRefer.value!=""){
						referValue=referValue+"&refer1="+oRefer.value;
					} else {
						referValue = referValue + "&refer1=" + oInputs(i).refer1;
					}
				}
				if(oInputs(i).refer2){
					var oRefer=document.getElementById(oInputs(i).refer2);
					if(oRefer && oRefer.value!=null&&oRefer.value!=""){
						referValue=referValue+"&refer2="+oRefer.value;
					}else {
						referValue = referValue + "&refer2=" + oInputs(i).refer2;
					}
				}
				url=url+referValue;

				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
					//oBao.asynchronous=false;
	  			oBao.open("POST",url,false);
	  			oBao.send();
	  			var strResult = unescape(oBao.responseText);
	  			if(strResult.indexOf('OK')>=0){
	  				//取到名值对了
					//alert(strResult);
					var lineArray = strResult.split("|");
					var txtArray = lineArray[2].split("`");
	    				//alert(txtArray[1]);
					/*
					 * 	var adviceobj= oInputs(i) ; 
	    				adviceobj.cloneObj = oInputs(i) ;
	    				adviceobj.id = getAdviceName(oInputs(i))
	    				oInputs(i).parentNode.insertBefore(adviceobj,oInputs(i).nextSibling);	
					 * */
					//	var adviceobj = getAdviceObj(oInputs(i));
					
	    			var adviceobj=getAdviceObj(oInputs(i));
					//显示提示信息，如果显示名、值一样，就不显示了
	    			if(oInputs(i).useAdvice) {
	    				adviceobj.innerHTML=(txtArray[0]==txtArray[1] ? "" :txtArray[1]);
	    			}else {
	    				adviceobj.value= (txtArray[0]==txtArray[1] ? oInputs(i).value : txtArray[1]);
	    			}
					
				}
			}
		}
	}

	if (autoMultiCount>0)
	{
		//初始化后退的历史变量
		 _myHistory=new MyHistory(autoMultiCount,strInputNames);
	}
}

function _doReset(id) {
		var textObj = document.getElementById(id);
		textObj.value = "";
		
		var advicObj = getAdvice(textObj.name);

		//清空提示值
		if(advicObj) {
			if(advicObj.useAdvice) {
				advicObj.innerHTML = "";
			}else {
				advicObj.value = "";
			}
		}
		
		if(textObj.clone) {
			//如果我是克隆出来的，就清空前面的值
			var cloneObj = textObj.cloneObj ;
			if(!cloneObj) {
				cloneObj = document.getElementById(textObj.cloneId) ;
			}
			
			cloneObj.value = "" ;
		}
}

window.attachEvent('onload',dtRestoreHintLoader);

/*
//这一段测试自动增加后退历史单元的功能
//只初始化一个历史单元
var tt=new MyHistory(1,"input1");
tt.Push("input1",1000);
tt.Push("input1",2000);

alert(tt.Pop("input1"));
alert(tt.Pop("input1"));

//直接使用第二个，应该可以立刻初始化一个
tt.Push("input2",1);
tt.Push("input2",2);
tt.Push("input2",3);

alert(tt.Pop("input2"));
alert(tt.Pop("input2"));
*/