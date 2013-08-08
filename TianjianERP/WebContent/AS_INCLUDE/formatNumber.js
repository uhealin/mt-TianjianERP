/*
参数1：以#表示数字或字符，可加入一个小数点，两个小数点就会出错,格式化后的样式
参数2：每组格式之间的连接符
*/
String.prototype.HBformatNumber=function(FormatParttern,ConnectOption)
{
if(!/#/.test(FormatParttern))//判断格式是不是可以进行格式化
{
return "格式错误";
}
var FormatPartternArray=FormatParttern.split("\.");//分开小数部分和整数部分的格式
return splitNUM(this)
function splitNUM(num)//此函数进行分开数字
{
try{
	var tt=(parseFloat(num).toFixed(2)).toString();
	//alert(tt);
	//alert(typeof(tt));
	NumArray=tt.split("\.")
}catch(e){
	NumArray=num.split("\.")
}
if(NumArray.length==1)
{
return formatNUM(NumArray[0],FormatPartternArray[0],true)
}
else
{
if(FormatPartternArray.length==1)
{
return formatNUM(NumArray[0],FormatPartternArray[0],true)+"."+NumArray[1]
}
else
{
return formatNUM(NumArray[0],FormatPartternArray[0],true)+"."+formatNUM(NumArray[1],FormatPartternArray[1],false)
}
}
}


/*
格式化字符串函数
参数1：待格式化的字符串
参数2：格式化样式
参数3：是否为整数格式化，true为整数格式化，false为小数，字符串也为true
*/
function formatNUM(num,Formatparttern,ifInterger)
{
var IntergerPushArray=new Array();//定义一个数组栈来存放整数
var IntergerFormatPartternNum=Formatparttern.match(/\#/g).length//得到一组格式化样式中包含多少个数字/
var IntegerParttern=(ifInterger)?(new RegExp(Formatparttern.replace(/[^#]/g,"").replace(/#/g,".")+"$")):new RegExp("^"+Formatparttern.replace(/[^#]/g,"").replace(/#/g,"."))//创建正则为了把每组数字取出来
//为避免出现-,127,123.00的情况，对负数进行处理，去掉负号，处理完再加上负号，显示为-127,123.00
var type="0";
if(num.indexOf("-")>-1) {
	num=num.substring(1,num.length);
	type="1";
}
while(IntegerParttern.test(num))
{
IntergerPushArray.push(num.match(IntegerParttern))//把每一组放到数组中
num=num.replace(IntegerParttern,"");//去掉这组，去拿下一组
}
if(num!="")
{
var RemainNum=num.length//剩下不够一组数字的时候判断还有几个数字
var deleteNum=IntergerFormatPartternNum-RemainNum//一会需要去掉的数字个数
for(var i=0;i<IntergerFormatPartternNum;i++)//把最后一组不够的位数加0填充
{
num=(ifInterger)?("0"+num):(num+"0")
}
num=(ifInterger)?num.substr((num.length-parseInt(IntergerFormatPartternNum))):num.substr(0,IntergerFormatPartternNum)//得到最后一个格式化后的函数
IntergerPushArray.push(num);//将最后一组进栈
}
for(var i=0;i<IntergerPushArray.length;i++)
{
var j=-1;
var str=IntergerPushArray[i].toString();
IntergerPushArray[i]=Formatparttern.replace(/(#)/g,function($1){j++;return str.substr(j,1)})//替换
}
if(ifInterger)//如果整数返回数组逆序，并且通过连接字符连接每组数据，去掉多余的0；否则返回格式化后的小数部分
{
if(type=="1") {//为负数时，把之前去掉了负号加上去
	return "-"+(IntergerPushArray.reverse().join(ConnectOption).substr(parseInt(deleteNum)));
} else {
	return (IntergerPushArray.reverse().join(ConnectOption).substr(parseInt(deleteNum)));
}
}
else
{
return(IntergerPushArray.join(ConnectOption))
}
}
}