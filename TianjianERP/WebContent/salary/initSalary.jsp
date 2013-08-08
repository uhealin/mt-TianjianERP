<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script type="text/javascript">
autoInit();
  //去除数组重复元素
function delRepeat(arr){
	for(var i=0,len=arr.length,result=[],item;i<len;i++){
	        item = arr[i].value;
	        if(result.indexOf(item) < 0) {
	            result[result.length] = item;
	        }
	    }
	    return result;
}
</script>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:90%;
	text-align:center;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top:20px;
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	margin-top:20px;
	width:20%;
	font-size: 13px;
	TEXT-ALIGN: center; 
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: center; 
	margin-top:20px;
	height:25PX;
	WORD-WRAP: break-word
}

</style>
</head>
<body>

</body>
<script type="text/javascript">
/加载页面自动初始化
function autoInit(){
	var executeStr=document.getElementsByName("executeStr");
   try{	
	for(var i=0;i<executeStr.length;i++){
		eval(executeStr[i].value);
	}
	}catch(e){
	    alert("构建视图失败");
	}
}

//页面自动计算
function m(userid,strFormulas_s,strCNames_s,strENames_s){
	var i,j,k,strT;
	var str = document.getElementById("evalScript").innerHTML;
	eval(str);
	eval("var strFormulas="+strFormulas_s);
	eval("var strCNames="+strCNames_s);
	eval("var strENames="+strENames_s);
	for (i=0;i<strFormulas.length;i++){
		//批量替换公式并完成计算
		strT=strFormulas[i];
		for (j=0;j<strCNames.length;j++){
 				//strT=strT.replace(strCNames[j], "document.getElementById('"+strENames[j]+userid+"').value");  
 				strT=strT.split(strCNames[j]).join("document.getElementById('"+strENames[j]+userid+"').value");  
 				strT=strT.replace("》",">");
 				
		}
		//最终公式
		//alert(strT);
		//计算
		try{
			eval(strT);
		}catch(e){alert('自动计算字段间关系出错：'+e)}
	}
	
}

//限制输入框只能输入数字和两位小数
function inputkeypress(inputobj){
   if(!inputobj.value.match(/^\d*?\.?\d*?$/))
    inputobj.value=inputobj.t_value;
   else
    inputobj.t_value=inputobj.value;
   if(inputobj.value.match(/^(?:\d+(?:\.\d+)?)?$/))
    inputobj.o_value=inputobj.value
   if(/\.\d\d$/.test(inputobj.value))event.returnValue=false
}
//限制输入框只能输入数字和两位小数
function inputkeyup(inputobj){
   if(!inputobj.value.match(/^\d*?\.?\d*?$/))
    inputobj.value=inputobj.t_value;
   else
    inputobj.t_value=inputobj.value;
   if(inputobj.value.match(/^(?:\d+(?:\.\d+)?)?$/))
    inputobj.o_value=inputobj.value
}
//限制输入框只能输入数字和两位小数
function inputblur(inputobj){
   if(!inputobj.value.match(/^(?:\d+(?:\.\d+)?|\.\d*?)?$/))
    inputobj.value=inputobj.o_value;
    else{
     if(inputobj.value.match(/^\.\d+$/))
      inputobj.value=0+inputobj.value;
     if(inputobj.value.match(/^\.$/))
      inputobj.value=0;
     inputobj.o_value=inputobj.value
    }
}
function showquery(){
	if(document.getElementById("chooseValue_businessYetList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		var url = "salary.do?method=listSalarybyDepart&p_ch="+document.getElementById("chooseValue_businessYetList").value;
	    // window.location.href=url;
	   	parent.openTab("salaryList","查看工资详情",url);
	}

}
</script>
</html>