/*
 * Really easy field validation with Prototype
 * http://tetlaw.id.au/view/blog/really-easy-field-validation-with-prototype
 * Andrew Tetlaw
 * Version 1.4 (2006-05-18)
 * Thanks:
 *  Mike Rumble http://www.mikerumble.co.uk/ for onblur idea
 *  Analgesia for spotting a typo
 *  Paul Shannon http://www.paulshannon.com for the reset idea
 *  Ted Wise for the focus-on-first-error idea
 *  Sidney http://www.creativelycrazy.de/ for the custom advice idea
 *
 * http://creativecommons.org/licenses/by-sa/2.5/
 */
Validator = Class.create();

Validator.prototype = {
	initialize : function(className, error, test) {
		this.test = test ? test : function(){ return true };
		this.error = error ? error : '域值输入非法.';
		this.className = className;
	}
}

var Validation = Class.create();

Validation.prototype = {
	initialize : function(form, options){
		this.options = Object.extend({
			stopOnFirst : false,
			immediate : false,
			focusOnError : true
		}, options || {});
		this.form = $(form);
		Event.observe(this.form,'submit',this.onSubmit.bind(this),false);
		if(this.options.immediate) {
			Form.getElements(this.form).each(function(input) { // Thanks Mike!
				Event.observe(input, 'blur', function(ev) { Validation.validate(Event.element(ev).id) });
			});
		}
	},
	onSubmit :  function(ev){
		if(!this.validate()) {
			Event.stop(ev);
			if(this.options.focusOnError) {
				$$('.validation-failed').first().focus();
			}
		}
	},
	validate : function() {
		var t;
		if(this.options.stopOnFirst) {
			t=Form.getElements(this.form).all(Validation.validate);
		} else {
			t=Form.getElements(this.form).collect(Validation.validate).all();
		}
		if (t) {
			//document.write("<span id=bxDlg_bg align=center oncontextmenu=\"return false\" onselectstart=\"return false\" style=\"visibility:hidden;width:100%;height:100%;position:absolute;left:0;top:0\"></span>");
			var scrollHeight=document.body.scrollTop;//滚动条纵向滚动的高度
  			var winHeight = document.body.clientHeight;//窗口高度
			var winWidth  = document.body.clientWidth;//窗口宽度
			var boxHeight=60;//文本框高度
			var boxWidth=200;//文本框宽度
			var boxTop=winHeight/2-boxHeight/2+scrollHeight;//文本框左上角所在的纵坐标
			var boxLeft=winWidth/2-boxWidth/2;//文本框左上角所在的横坐标

			var strTalk="<span id=bxDlg_bg align=center oncontextmenu=\"return false\" onselectstart=\"return false\" style=\"width:"+document.body.scrollWidth+";height:"+document.body.scrollHeight+";position:absolute;left:0;top:0\"><div id=bxDlg_bg1 style=height:100%;background:white;filter:alpha(opacity=50)>&nbsp;</div></span>"
				+"<span  style='background:#E4E4E4;POSITION:absolute;padding:20px 40px 20px 40px;"
    				+"left:"+boxLeft+";top:"+boxTop+"; width:"+boxWidth+"px; height:"+boxHeight+"px; border:1px solid #666666;'><img src='/AuditSystem/images/indicator.gif' />处理中<br/>,请稍候……</span>";
    			//这里如果用write方法，就会阻塞submit的继续提交，好奇怪啊。
    			document.body.insertAdjacentHTML("beforeEnd",strTalk);
    			document.close();
		}
		return t;
	},
	reset : function() {
		Form.getElements(this.form).each(Validation.reset);
	},
	check : function(){
		
		var t;
		if(this.options.stopOnFirst) {
			t=Form.getElements(this.form).all(Validation.validate);
		} else {
			t=Form.getElements(this.form).collect(Validation.validate).all();
		}
		return t;
	}
}

Object.extend(Validation, {
	validate : function(elm, index, options){ // index is here only because we use this function in Enumerations
		var options = Object.extend({}, options || {}); // options still under development and here as a placeholder only
		elm = $(elm);
		var cn = elm.classNames();
		/*
		try{
			alert(elm);
			alert(elm.parentNode.innerHTML);
			alert(this.parentNode.innerHTML);
		}catch(e){}
		*/
		return result = cn.all(Validation.test.bind(elm)); 
	},

	//winner修改于20060529，增加了前后空格过滤功能
	trim : function (strSource){
		var t="";
		try{
		 t=strSource.replace(/^\s*/,'').replace(/\s*$/,'');
		}catch(e){}
		return t;
	},

	test : function(name) {
		
		var v = Validation.get(name);
		var prop = '__advice'+name.camelize();
		var passed =false;
		var backgroundvalid=false;
		//winner修改于20060529，增加了前后空格过滤功能，并增加了后台校验的功能
		//先无条件进行本地测试
		//这里$F居然取不出File，改为3元运算符
		
		if(this.clone) { //克隆出来的input框不验证
			return true;
		} 
		this.value = this.value.replace("请选择...","") ;
		this.value = this.value.replace("请选择或输入...","") ;
		
		//alert(Validation.isVisible(this) + this.name);
		if(!v.test(Validation.trim(((this.type=="file") ? this.value : $F(this)))) && Validation.isVisible(this)) {
			passed=false;
		}else{
			//测试通过，则看是否设置了valuemustexist，设置了则提交后台服务器进行检查
			passed=true;
			//if (this.valuemustexist && this.valuemustexist=="true" && !(this.value==null || this.value=="")){
			//增加CheckAnyWay on 20070824
			
			if ((this.valuemustexist && this.valuemustexist=="true" && !(this.value==null || this.value==""))
				||(this.CheckAnyWay && this.CheckAnyWay=="true")){
				//对框中有'+'的值进行转义
				var corectValue = this.value;
				
				var newValue = "";
				if(corectValue!=null && corectValue != ""  && corectValue.indexOf('+') != -1 ) {
				  for(var i=0; i<corectValue.length; i++) {
					var s = corectValue.charAt(i);
				    if(s == '+') {
					  newValue += "%2B";
					}else {
					  newValue += s;
					}
				  }
				}else{
					newValue = corectValue;
				}
				//alert(newValue);

				
				var url="/AuditSystem/AS_SYSTEM/hint.do?checkmode=1&autoid="+this.autoid+"&pk1="+ newValue;
				
				if (this.refer){
					var qqq=document.getElementById(this.refer);
					if (qqq && qqq.value!=null && qqq.value!=""){
						url+="&refer="+qqq.value;
					}else{
						url+="&refer="+this.refer;
					}
				}
				if (this.refer1){
					var qqq=document.getElementById(this.refer1);
					if (qqq && qqq.value!=null && qqq.value!=""){
						url+="&refer1="+qqq.value;
					}else{
						url+="&refer1="+this.refer1;
					}
				}
				if (this.refer2){
					var qqq=document.getElementById(this.refer2);
					if (qqq && qqq.value!=null && qqq.value!=""){
						url+="&refer2="+qqq.value;
					}else{
						url+="&refer2="+this.refer2;
					}
				}

				//进行后台校验
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				//oBao.asynchronous=false;
  				oBao.open("POST",url,false);
  				oBao.send();
  				var strResult = unescape(oBao.responseText);
  				if(strResult.indexOf('ERROR')>=0){
  					passed=false;
  					backgroundvalid=true;
  				}
			}
		}
		
		if (passed==false){
			
			if(!this[prop]) {
				
				//判断是否已经显示过提示，还没有显示过提示则进入，
				var advice = Validation.getAdvice(name, this.name);
				
				if(typeof advice == 'undefined') { 
					advice = document.createElement('span');
					//winner 修改于20060528,增加了优先显示TITLE的功能
					/* winner 调试使用
					try{
						alert(this.tagName);
						alert(this.title);
						alert(this.parentNode.innerHTML);
					}catch(e){}

					*/
					//advice.appendChild(document.createTextNode(v.error));
					advice.appendChild(document.createTextNode(this.value && backgroundvalid ? "输入的值不存在" : (this.title ? this.title :v.error)));
					advice.className = 'validation-advice';
					advice.id = 'advice2-' + this.name; 
					advice.style.display = 'none';   
					this.parentNode.insertBefore(advice, this.nextSibling);
				}else{
					advice.className = 'validation-advice';
					advice.style.display = 'none';
					advice.innerHTML=(this.value ? "输入的值不存在" : (this.title ? this.title :v.error));
				}

				if(typeof Effect == 'undefined') {
					advice.style.display = 'inline';
				} else {
					new Effect.Appear(advice.id, {duration : 1 });
				}
			}else{
				var advice = Validation.getAdvice(name, this.name);
				//advice.innerHTML=(this.value ? "输入的值不存在" : (this.title ? this.title :v.error));
			}
			this[prop] = true;
			this.removeClassName('validation-passed');
			this.addClassName('validation-failed');


			//设置已经显示的提示为空
			/*
			var hintadvice = Validation.getHintAdvice(this.name);
			if(typeof hintadvice != 'undefined') hintadvice.innerHTML="";
			*/
			
			return false;
		} else {
			var advice = Validation.getAdvice(name, this.name);
			if(typeof advice != 'undefined' && !advice.clone) advice.hide();
			this[prop] = '';
			this.removeClassName('validation-failed');
			this.addClassName('validation-passed');
			return true;
		}
	},
	isVisible : function(elm) {
		while(elm.tagName != 'BODY') {
			if(!$(elm).visible()) return false;
			elm = elm.parentNode;
		}
		return true; 
	},
	getAdvice : function(name, id) {
		var advice = Try.these(
			//function(){ return $('advice-' + name + '-' + id) },
			function(){
				var adviceObj = document.getElementById(id);
				if(adviceObj) {
					var tempId = "advice2-"+id ;
					var advice =  document.getElementById(tempId) ;;
					if(adviceObj.useAdvice) {
						adviceObj = document.getElementById('advice2-' + id) ;
					}
					
					if(!advice) {
						var newAdvice = document.createElement('span'); 
						newAdvice.id = tempId ;
						adviceObj.parentNode.insertBefore(newAdvice, adviceObj.nextSibling.nextSibling);
						return $(newAdvice.id);
					}
				}
				return $('advice2-' + id) 
			}
		);
		return advice;
	},
	getHintAdvice : function(name) {
		var advice = Try.these(
			function(){ return $('advice2-' + name) }
		);
		return advice;
	},
	reset : function(elm) {
		var cn = elm.classNames();
		cn.each(function(value) {
			var prop = '__advice'+value.camelize();
			if(elm[prop]) {
				var advice = Validation.getAdvice(value, elm.id);
				advice.hide();
				elm[prop] = '';
			}
			elm.removeClassName('validation-failed');
			elm.removeClassName('validation-passed');
		});
	},
	add : function(className, error, test, options) {
		var nv = {};
		nv[className] = new Validator(className, error, test, options);
		Object.extend(Validation.methods, nv);
	},
	addAllThese : function(validators) {
		var nv = {};
		$A(validators).each(function(value) {
				nv[value[0]] = new Validator(value[0], value[1], value[2], (value.length > 3 ? value[3] : {}));
			});
		Object.extend(Validation.methods, nv);
	},
	get : function(name) {
		return  Validation.methods[name] ? Validation.methods[name] : new Validator();
	},
	methods : {}
});

//var $V = Validation.validate;
//var $VG = Validation.get;
//var $VA = Validation.add;

Validation.add('IsEmpty', '', function(v) {
				return  ((v == null) || (v.length == 0) || /^\s+$/.test(v));
			});

Validation.addAllThese([
	/*-- 校验必填 --*/
	['required', '请输入有效值.', function(v) {
				//alert(v);
				return !Validation.get('IsEmpty').test(v);
			}],
	['checkexist-wheninputed', '请输入有效值.', function(v) {

    		if (v==null || v==""){
    			return true;
    		}else{
				return !Validation.get('IsEmpty').test(v);
			}
			}],
	['validate-number', 'Please use numbers only in this field?', function(v) {
				return Validation.get('IsEmpty').test(v) || !isNaN(v);
			}],
	/*-- 校验整数 --*/
	['validate-digits', '请输入整数.', function(v) {

				return Validation.get('IsEmpty').test(v) ||  !/[^\d*$]/.test(v);
			}],
				/*-- 校验整数 --*/
	['validate-positiveInt', '请输入大于0的整数.', function(v) {

				return Validation.get('IsEmpty').test(v) || /^[0-9]*[1-9][0-9]*$/.test(v);
			}],
	['validate-alpha', 'Please use letters only (a-z) in this field.', function (v) {
				return Validation.get('IsEmpty').test(v) ||  /^[a-zA-Z]+$/.test(v)
			}],
	['validate-alphanum', 'Please use only letters (a-z) or numbers (0-9) only in this field. No spaces or other characters are allowed.', function(v) {
				return Validation.get('IsEmpty').test(v) ||  !/\W/.test(v)
			}],
	['validate-date', 'Please enter a valid date.', function(v) {
				try{
				var test = new Date(v);
				return Validation.get('IsEmpty').test(v) || !isNaN(test);
				}catch(e){}
			}],
	/*-- 校验邮箱 --*/
	['validate-email', '请输入有效邮箱. 例如 username@domain.com .', function (v) {
				return Validation.get('IsEmpty').test(v) || /\w{1,}[@][\w\-]{1,}([.]([\w\-]{1,})){1,3}$/.test(v)
			}],
	['validate-date-au', 'Please use this date format: dd/mm/yyyy. For example 17/03/2006 for the 17th of March, 2006.', function(v) {
				if(!Validation.get('IsEmpty').test(v)) {
					var upper = 31;
					if(/^(\d{2})\/(\d{2})\/(\d{4})$/.test(v)) { // dd/mm/yyyy
						if(RegExp.$2 == '02') upper = 29;
						if((RegExp.$1 <= upper) && (RegExp.$2 <= 12)) {
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return true;
				}
			}],
	/*-- 校验货币 --*/
	['validate-currency', '请输入有效货币.例如100.00 .', function(v) {
				// [$]1[##][,###]+[.##]
				// [$]1###+[.##]
				// [$]0.##
				// [$].##
				return Validation.get('IsEmpty').test(v) ||  /^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/.test(v)
			}]

	/*  winner adds here	*/
	,
	/*-- 校验日期为空就不检查 --*/
	['validate-date-cn','请使用日期格式: yyyy-mm-dd. 例如 2006-03-17', function(v){
    		/*-- 日期格式：(四位)年份 + (至多两位)月份 + (至多两位)日期 ,已经考虑了闰年等日期--*/
    		if (v==null || v==""){
    			return true;
    		}
     		if(!/^(?:([0-9]{4}-(?:(?:0?[1,3-9]|1[0-2])-(?:29|30)|((?:0?[13578]|1[02])-31)))|([0-9]{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\d|2[0-8]))|(((?:(\d\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))-0?2-29)))$/.test(v))
     		{
      			return false;
     		}
     		return true;
		}]
		,
	/*-- 校验日期为空会认为检查不通过 --*/
	['validate-date-cn-required','请使用日期格式: yyyy-mm-dd. 例如 2006-03-17', function(v){
    		/*-- 日期格式：(四位)年份 + (至多两位)月份 + (至多两位)日期 ,已经考虑了闰年等日期--*/
     		if(!/^(?:([0-9]{4}-(?:(?:0?[1,3-9]|1[0-2])-(?:29|30)|((?:0?[13578]|1[02])-31)))|([0-9]{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\d|2[0-8]))|(((?:(\d\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))-0?2-29)))$/.test(v))
     		{
      			return false;
     		}
     		return true;
		}]

	,
	/*-- 校验两次输入密码,请注意需要第一次输入密码的ID叫做password --*/
	['validate-passwd-identical', '两次输入的密码必须一致', function(v){
			if(Validation.get('IsEmpty').test(v) && Validation.get('IsEmpty').test($F("password"))){
				return true;
			}
        	return !Validation.get('IsEmpty').test(v) && v == $F("password");
    	}]
    	,
	/*-- 校验电话号码 --*/
	['validate-phonenumber', '电话号码可以用+开头，只能输入数字,-符号 例如020-12345678', function(v){
        	if(!/^[+]{0,1}(\d){1,3}[ ]?([-－]?((\d)|[ ]){1,12})+$/.test(v))
     		{
      			return false;
     		}
     		return true;
    	}]
	/*winner's add ends here*/
        ,
        /*-- 校验电话号码 可以为空--*/
	['phonenumber-wheninputed', '电话号码可以用+开头，只能输入数字,-符号 例如020-12345678', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^[+]{0,1}(\d){1,3}[ ]?([-－]?((\d)|[ ]){1,12})+$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}]
        ,
        /*-- 年份验证 可以为空--*/
	['year-wheninputed', '年份是由四个数字组成。', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^\d{4}$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}]
        ,
        /*-- 月份验证 可以为空--*/
	['month-wheninputed', '月份是由两个数字组成。', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^\d{2}$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}]
    	
        ,
        /*-- 数字，字母，下划线，和 - ---*/
	['alphanum-wheninputed', '请输入数字，字母，下划线，或 - 。', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^[\w-]{1,}[\w-]*$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}] 

        ,
        /*-- 文件名验证---*/
	['filename-wheninputed', '文件名不能包含\\/:*?"<>|', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(/[\\\/\*\?"<>|]+/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}] 
		,
		/*-- 0至100的数字验证 --*/
	['0-100-wheninputed', '请输入0－100的数字', function(v) {
				return (Validation.get('IsEmpty').test(v) || !isNaN(v))&&parseFloat(v)<100&&parseFloat(v)>0;
			}]
			
		,	
	/*-- ip,网络地址的验证 --*/
	['ip-wheninputed', '请输入ip或者网址', function(v) {
				return (Validation.get('IsEmpty').test(v) || isIP(v));
			}]

]);


function setObjDisabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=true;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=true;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=true;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=true;
			if(sType=="CHECKBOX")oElem.disabled=true;
			if(sType=="RADIO")oElem.disabled=true;
			}
			break;
		default:
			oElem.disabled=true;
			break;
		}
	//set style
	oElem.style.backgroundColor="#eeeeee";
}

function setObjEnabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=false;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=false;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=false;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=false;
			if(sType=="CHECKBOX")oElem.disabled=false;
			if(sType=="RADIO")oElem.disabled=false;
			}
			break;
		default:
			oElem.disabled=false;
			break;
		}
	//set style
	oElem.style.backgroundColor="#FFFFFF";
}


function show(obj){
	$(obj).style.visibility=""
}

function hide(obj){
	$(obj).style.visibility="hidden"
}

function isIP(strIP) { 
	if (isNull(strIP)) return false; 
	var re=/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/   //匹配IP地址的正则表达式 
	var res= /^[a-zA-z0-9]+(\.[a-zA-z0-9]+)*$/  //匹配用服务器名的正则表达式 
	re=new RegExp(re);
	res=new RegExp(res);
	if(re.test(strIP)) 
	{ 
		if( RegExp.$1 <256 && RegExp.$2<256 && RegExp.$3<256 && RegExp.$4<256)
		{
	
			return true;
		}
	} 
	if(res.test(strIP)){
		return true;
	}
	return false; 
} 
 
/* 
用途：检查输入字符串是否为空或者全部都是空格 
输入：str 
返回： 
如果全是空返回true,否则返回false 
*/ 
function isNull( str ){ 
if ( str == "" ) return true; 
var regu = "^[ ]+$"; 
var re = new RegExp(regu); 
return re.test(str); 
} 
