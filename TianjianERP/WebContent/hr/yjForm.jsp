<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>应届招聘表单</title>

<style type="text/css">
   .inputstyle{ border:none;  width:99%;	       margin-left: 0cm;
           margin-right: 0cm;
           margin-top: 0cm;}
   p.MsoNormal
	{margin-bottom:.0001pt;
	text-align:justify;
	text-justify:inter-ideograph;
	font-size:10.5pt;
	font-family:"Times New Roman","serif";
	       margin-left: 0cm;
           margin-right: 0cm;
           margin-top: 0cm;
       }
.MsoNormalTable
	{font-size:12px;
	font-family:"border-collapse:collapse;
       }
.MsoNormalTable tr td{  border:#000000 1px solid; padding:1px;}
p.MsoBodyTextIndent
	{margin-bottom:.0001pt;
	text-align:justify;
	text-justify:inter-ideograph;
	text-indent:18.0pt;
	font-size:10.5pt;
	font-family:"Times New Roman","serif";
	       margin-left: 0cm;
           margin-right: 0cm;
           margin-top: 0cm;
       }


</style>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
	        	   if (!formSubmitCheck('thisForm')){
	        	   		return;
	        	   }else{
				   		mySubmit();
				   }
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	 
	new Ext.form.DateField({
		applyTo : 'starttime',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'birthday',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'afterschooltime',
		width: 133,
		format: 'Y-m-d'
	});
});
</script>

</head>
<body leftmargin="0" topmargin="0" >
<div style="overflow: scroll;height: 100%">
<div id="divBtn" ></div>
<form name="thisForm" action="" method="post" >
应届毕业生应聘报名表<span lang="EN-US"><o:p></o:p></span></span></p>
    <div align="center">
        <table  cellpadding="0" cellspacing="0" class="MsoNormalTable" style="border-collapse:collapse;mso-table-layout-alt:fixed;border:none;
 mso-border-alt:solid windowtext .5pt;mso-padding-alt:0cm 5.4pt 0cm 5.4pt;
 mso-border-insideh:.5pt solid windowtext;mso-border-insidev:.5pt solid windowtext" 
            width="768" border="1">
            <tr style="mso-yfti-irow:0;mso-yfti-firstrow:yes;page-break-inside:avoid;height:15.75pt">
                <td  width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">姓<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp;  </span>
                        </span>名</span></p>
                </td>
                <td  width="85">
                    <input name="name" type="text" id="name" class="required" value="vo.name"/>       
                </td>
                <td colspan="4" width="60">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">性<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp; </span></span>别</span></p>
                </td>
                <td colspan="2"  width="50">
                    <select name="gender" id="gender" style="width:50px; border:none;">
	<option value="男">男</option>
	<option value="女">女</option>
 
</select>
                </td>
                <td colspan="2"  width="61">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">籍<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp; </span></span>贯</span></p>
                </td>
                <td width="70">
 <input name="nativeplace" type="text" id="nativeplace" class="inputstyle" value="${vo.nativeplace}" />                    
                </td>
                <td colspan="3"  width="48">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        民<span lang="EN-US"><span style="mso-spacerun:yes">&nbsp; </span></span>族</span></p>
                </td>
                <td colspan="2"  width="60">
                    <input name="nation" type="text" id="nation" class="inputstyle" value="${vo.nation}"/> 
                </td>
                <td width="66" align="center">
                    <p align="center" class="MsoNormal" style="text-align:center">
                    <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">党/团员</span></p>
                </td>
                <td  width="70">
                    <input name="political" type="text" id="political" class="inputstyle" value="${vo.political}" /> 
                </td>
            </tr>
            <tr >
                <td >
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        出生年月日</span></p>
                </td>
                <td colspan="7" width="167">
                      <input name="birthday" type="text" id="birthday"  vaule="${vo.birthday }" /> 
                </td>
                <td colspan="2" width="61">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        家庭地址</span></p>
                </td>
                <td colspan="4" width="95">
                      <input name="address" type="text" id="address" value="${vo.address}" /> 
                </td>
                <td colspan="2"  width="71">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        身份证号</span></p>
                </td>
                <td colspan="2">
                    <input name="id" type="text" id="id" class="inputstyle" value="${vo.id}/> 
                </td>
            </tr>
            <tr style="">
                <td rowspan="2"  width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        何院校何专业何时毕业</span></p>
                </td>
                <td colspan="7"  width="167">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">院<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp;&nbsp; </span></span>校<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp;&nbsp; </span></span>名<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp;&nbsp; </span></span>称</span></p>
                </td>
                <td colspan="4" width="109">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">专<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp; </span></span>业</span></p>
                </td>
                <td colspan="2" width="47">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        学制</span></p>
                </td>
                <td colspan="2" width="71">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">学<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp; </span></span>历</span></p>
                </td>
                <td colspan="2"  width="110">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        毕业年月</span></p>
                </td>
            </tr>
            <tr style="mso-yfti-irow:3;page-break-inside:avoid;height:15.75pt">
                <td colspan="7"  width="167">
                    <input name="graduation" type="text" id="graduation" class="inputstyle" value="${vo.graduation }"/> 
                </td>
                <td colspan="4" width="109">
                    <input name="profession" type="text" id="profession" class="inputstyle" value="${vo.profession }"/> 
                </td>
                <td colspan="2" width="47">
                      <input name="educationyears" type="text" id="educationyears" class="inputstyle" style="width:20px;"value="${vo.educationyears}"/>年
                </td>
                <td colspan="2"  width="71">
                    <input name="degrees" type="text" id="degrees" class="inputstyle" value="${vo.degrees}"/> 
                </td>
                <td colspan="2"  width="110">
                      <input name="afterschooltime" type="text" id="afterschooltime" class="inputstyle" value="${vo.afterschooltime}"" /> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:4;page-break-inside:avoid;height:15.75pt">
                <td  width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        学位（√）</span></p>
                </td>
                <td colspan="10" align="center"  width="275">
                    <input id="Checkbox1" name="XW" value="博士" type="checkbox" />博士&nbsp;&nbsp;  
                    <input id="Checkbox2" name="XW" value="硕士" type="checkbox" />硕士&nbsp;&nbsp;  
                    <input id="Checkbox3" name="XW" value="双学士" type="checkbox" />双学士&nbsp;&nbsp;  
                    <input id="Checkbox4" name="XW" value="学士" type="checkbox" />学士&nbsp;&nbsp;                
                </td>
                <td colspan="5"  width="119">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        辅修专业</span></p>
                </td>
                <td colspan="2" width="110">
                    <input name="otherprofession" type="text" id="otherprofession" class="inputstyle" value="${vo.otherprofession}"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:5;page-break-inside:avoid;height:15.75pt">
                <td  width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        在班级或年</span></p>
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        级成绩名次</span></p>
                </td>
                <td colspan="3"  width="107">
                    <p class="MsoNormal">
                        <u>                        
                        <span style="font-size:7.5pt;
  mso-bidi-font-size:10.0pt;font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot; text-align:center;">&nbsp; （名次）&nbsp; &nbsp; (
<input id="classranking" style="width:25px" name="classranking" class="inputstyle" type="text" value="${vo.classranking }"/>)</span></u>
                    </p>
                    <p class="MsoNormal">
                        <span style="font-size:7.5pt;mso-bidi-font-size:10.0pt;
  font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">（班级总人数）(<input id="classnum" style="width:25px" name="classnum" class="inputstyle" type="text" value=""vo.classnum /> )</span>
                    </p>
                </td>
                <td colspan="5"  width="120">
                    <p class="MsoNormal">
                        <u>
                        
                        <span style="font-size:7.5pt;
  mso-bidi-font-size:10.0pt;font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">&nbsp;&nbsp;（名次）&nbsp;&nbsp; (<input id="greadranking" style="width:25px" name="greadranking" class="inputstyle" type="text" value="${vo.greadranking}"/>)</span></u>
                    </p>
                    <p class="MsoNormal">
                        <span style="font-size:7.5pt;mso-bidi-font-size:10.0pt;
  font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">（年级总人数）(<input id="greadnum" style="width:25px" name="greadnum" class="inputstyle" type="text" value="${vo.greadnum}" />)</span></p>
                </td>
                <td colspan="5"  width="96">
                    <p class="MsoNormal">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        高考分/一本线分：</span></p>
                    <p class="MsoNormal">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        (<input name="LqpcGK" type="text" id="LqpcGK" style="width:30px" class="inputstyle" />)/(<input name="LqpcYB" type="text" id="LqpcYB" style="width:30px" class="inputstyle" />) </span>
                    </p>
                </td>
                <td colspan="4"  width="181">
                    <p class="MsoNormal">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        高校录取批次<span lang="EN-US"> (</span>√<span lang="EN-US">) </span>：</span></p>
                        <input type="checkbox" name="receivetype" value="一本" onclick="selectOne(this,'receivetype')">一本&nbsp;
                        <input type="checkbox" name="receivetype" value="二本" onclick="selectOne(this,'receivetype')">二本&nbsp;
                        <input type="checkbox" name="receivetype" value="三本" onclick="selectOne(this,'receivetype')">三本&nbsp;
                        <input type="checkbox" name="receivetype" checked="checked" value="其他" onclick="selectOne(this,'receivetype')">其他&nbsp;
                </td>
            </tr>
            <tr style="mso-yfti-irow:6;page-break-inside:avoid;height:15.75pt"">
                <td rowspan="2" width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        &nbsp;外语语种<span style="mso-spacerun:yes">&nbsp; </span>等<span 
                            lang="EN-US"><span style="mso-spacerun:yes">&nbsp;&nbsp;&nbsp; </span>
                        </span>级</span></p>
                </td>
                <td colspan="3" rowspan="2" width="107">
                    <input name="englishlevel" type="text" id="englishlevel" class="inputstyle" value="${vo.englishlevel}" /> 
                </td>
                <td colspan="5" align="center" rowspan="2"  width="120">
                 <p align="center" class="MsoNormal" style="text-align:center">
                    <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        计算机等级</span></td></p>
                <td colspan="5" rowspan="2"  width="96">
                    <input name="computerdegree" type="text" id="computerdegree" class="inputstyle" value="${vo.computerdegree}"/>
                </td>
                <td colspan="4"  width="181">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        下列已报考的标注<span lang="EN-US">(</span>√<span lang="EN-US">)</span>：</span></p>
                </td>
            </tr>
            <tr style="mso-yfti-irow:7;page-break-inside:avoid;height:11.25pt">
                <td colspan="4" width="181">
                <input id="Checkbox5" name="examremark" value="研究生" type="checkbox" />研究生&nbsp;&nbsp;
                <input id="Checkbox6" name="examremark" value="公务员" type="checkbox" />公务员&nbsp;&nbsp;
                 <input id="Checkbox7" name="examremark" value="已考未取" type="checkbox" />已考未取&nbsp;&nbsp;
                </td>
            </tr>
            <tr style="mso-yfti-irow:8;page-break-inside:avoid;height:10.3pt">
                <td rowspan="2" width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        健康状况</span></p>
                </td>
                <td colspan="2" width="84">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">身高</span></p>
                </td>
                <td colspan="5" width="83">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">体重</span></p>
                </td>
                <td width="60">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">视力</span></p>
                </td>
                <td colspan="5" width="96">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        婚否</span></p>
                </td>
                <td colspan="4"  width="181">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        期望薪资（年薪）</span></p>
                </td>
            </tr>
            <tr style="mso-yfti-irow:9;page-break-inside:avoid;height:15.45pt">
            
            
                <td colspan="2" width="84">
                    <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        （<input id="heigth" style="width:30px" name="heigth" class="inputstyle" type="text" value="${vo.heigth}" />）公分</span>
                </td>
                <td colspan="5" width="83">
<span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        （<input id="weigth" style="width:30px" name="weigth" class="inputstyle" type="text" value="${vo.weigth }"/>）公斤</span>
                </td>
                <td  width="60">
                    <input id="eye" style="width:30px" name="eye" class="inputstyle" type="text" value="${vo.eye}"/> 
                </td>
                
                
                <td colspan="5"  width="96">
                 <select name="married" id="married" style="width:50px; border:none;">
					<option value="已婚">已婚</option>
					<option value="未婚">未婚</option>
				</select>
                </td>
                <td colspan="4"  width="181">
                    <input name="salary" type="text" id="salary" class="inputstyle" value="${vo.salary}" /> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:10;page-break-inside:avoid;height:15.75pt">
                <td rowspan="5" width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        学习简历</span></p>
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-size:7.5pt;mso-bidi-font-size:10.0pt;font-family:黑体;mso-hansi-font-family:
  &quot;Times New Roman&quot;">（初中入学起）</span></p>
                </td>
                <td colspan="6"  width="144">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        何年何月<span lang="EN-US">-</span>何年何月</span></p>
                </td>
                <td colspan="11"  width="360">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        何地<span lang="EN-US">/</span>何校<span lang="EN-US">/</span>何专业<span lang="EN-US">/</span>何学历</span></p>
                </td>
            </tr>
            
            
            
            
            <tr style="mso-yfti-irow:11;page-break-inside:avoid;height:15.75pt">
                <td colspan="6" width="144">
                
                    <input id="studytime1" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="studytime1" type="text" value="${vo.studytime1 }"/>
                </td>
                <td colspan="11"  width="360">
                    <input id="departdet1"  class="inputstyle" name="departdet1" type="text" value="${vo.departdet1 }/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:12;page-break-inside:avoid;height:15.75pt">
                <td colspan="6"  width="144">
                    <input id="studytime2" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="studytime2" type="text" value="${vo.studytime1}"/> 
                </td>
                <td colspan="11"  width="360">
                      <input id="departdet2"  class="inputstyle" name="departdet2" type="text" value="${vo.departdet2}"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:13;page-break-inside:avoid;height:15.75pt">
                <td colspan="6" width="144">
                    <input id="studytime3" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="studytime3" type="text" value="${vo.studytime3}"/> 
                </td>
                <td colspan="11" width="360">
                      <input id="departdet3"  class="inputstyle" name="departdet3" type="text" value="${vo.departdet3}"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:14;page-break-inside:avoid;height:15.75pt">
                <td colspan="6"  width="144">
                    <input id="studytime4" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="studytime4" type="text" value="${vo.studytime4}"/> 至
                </td>
                <td colspan="11" width="360">
                      <input id="departdet4"  class="inputstyle" name="departdet4" type="text" value="${vo.departdet4}"/> 
                </td>
                
                
                
                
                
            </tr>
            <tr style="mso-yfti-irow:15;page-break-inside:avoid;height:15.75pt">
                <td rowspan="5"  width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        社会实践<span lang="EN-US"><o:p></o:p></span></span></p>
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        经历（天健的实习经历必填）</span></p>
                </td>
                <td colspan="6"  width="144">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        何年何月<span lang="EN-US">-</span>何年何月</span></p>
                </td>
                <td colspan="11" width="360">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        单位<span lang="EN-US">/</span>部门<span lang="EN-US">/</span>岗位</span></p>
                </td>
            </tr>
            <tr style="mso-yfti-irow:16;page-break-inside:avoid;height:15.75pt">
            
            
                <td colspan="6"  width="144">
                    <input id="socialtime1" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="socialtime1" type="text" value="${vo.socialtime1}"/> 
                </td>
                <td colspan="11" width="360">
                      <input id="socialdet1" class="inputstyle" name="socialdet1" type="text" value="${vo.socialdet1 }"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:17;page-break-inside:avoid;height:15.75pt">
                <td colspan="6" width="144">
                    <input id="socialtime2" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="socialtime2" type="text" value="${vo.socialtime2}"/> 
                </td>
                <td colspan="11"  width="360">
                      <input id="socialdet2" class="inputstyle" name="socialdet2" type="text" value="${vo.socialdet2}"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:18;page-break-inside:avoid;height:15.75pt">
                <td colspan="6" width="144">
                    <input id="socialtime3" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="socialtime3" type="text" value="${vo.socialtime3 }"/> 
                </td>
                <td colspan="11" width="360">
                      <input id="socialdet3" class="inputstyle" name="socialdet3" type="text" value="${vo.socialdet3}"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:19;page-break-inside:avoid;height:15.75pt">
                <td colspan="6" width="144">
                    <input id="socialtime4" onclick="SelectDate(this,'yyyy年MM月')" style="width:75px" class="inputstyle" name="socialtime4" type="text" value="${vo.socialtime4}"/> 
                </td>
                <td colspan="11" width="360">
                      <input id="socialdet4" class="inputstyle" name="socialdet4" type="text" value="${vo.socialdet4}"/> 
                </td>
                
                
                
            </tr>
            <tr style="mso-yfti-irow:20;page-break-inside:avoid;height:15.75pt">
                <td style="width:70.55pt;border:solid windowtext 1.0pt;border-top:
  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt;height:23.75pt" width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        执业资格</span>
                    </p>
                </td>
                <td colspan="17" align="center" width="504"> 
              <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        注册会计师统考：
                  <input id="Checkbox8" name="YKG" value="会计" onclick="checkAll(this)" type="checkbox" />会计&nbsp;
                  <input id="Checkbox9" name="YKG" value="审计" onclick="checkAll(this)" type="checkbox" />审计&nbsp;
                  <input id="Checkbox11" name="YKG" value="财务成本管理" onclick="checkAll(this)" type="checkbox" />财务成本管理&nbsp;
                  <input id="Checkbox12" name="YKG" value="经济法" onclick="checkAll(this)" type="checkbox" />经济法&nbsp;
                  <input id="Checkbox13" name="YKG" value="税法" onclick="checkAll(this)" type="checkbox" />税法&nbsp;
                  <input id="Checkbox14" name="YKG" value="战略与风险管理" onclick="checkAll(this)" type="checkbox" />战略与风险管理&nbsp;
                  
                        等 <input name="TextNum" type="text" id="TextNum" style="width:10px" readonly="readonly" class="inputstyle" />门合格；<br /> 
                        
                  <input id="Checkbox10" name="WKG" value="会计" onclick="checkAll2(this)" type="checkbox" />会计&nbsp;
                  <input id="Checkbox15" name="WKG" value="审计" onclick="checkAll2(this)" type="checkbox" />审计&nbsp;
                  <input id="Checkbox16" name="WKG" value="财务成本管理" onclick="checkAll2(this)" type="checkbox" />财务成本管理&nbsp;
                  <input id="Checkbox17" name="WKG" value="经济法" onclick="checkAll2(this)" type="checkbox" />经济法&nbsp;
                  <input id="Checkbox18" name="WKG" value="税法" onclick="checkAll2(this)" type="checkbox" />税法&nbsp;
                  <input id="Checkbox19" name="WKG" value="战略与风险管理" onclick="checkAll2(this)" type="checkbox" />战略与风险管理&nbsp;
                  
                        等<input name="TextNum2" type="text" id="TextNum2" style="width:10px" readonly="readonly" class="inputstyle" />门已考，成绩待公布。
                        <br />ACCA、CGA等国外注册会计考试通过情况：<input name="TextACCA" type="text" id="TextACCA" style="width:400px" class="inputstyle" /> </span>
                </td>
                
                
                
                
                
            </tr>
            <tr style="mso-yfti-irow:21;page-break-inside:avoid;height:15.75pt">
                <td rowspan="2" width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        通讯地址</span></p>
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        及电话</span></p>
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-size:7.5pt;mso-bidi-font-size:10.0pt;font-family:黑体;mso-hansi-font-family:
  &quot;Times New Roman&quot;">（请详细填写）</span></p>
                </td>
                <td colspan="4" width="95">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        学校通讯地址</span></p>
                </td>
                <td colspan="8"  width="192">
                      <input name="TextXXDZ" type="text" id="TextXXDZ" class="inputstyle" /> 
                </td>
                <td colspan="2"  width="60" align="center">
                      <span lang="EN-US" style="font-size:10.5pt;mso-bidi-font-size:
10.0pt;font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;;mso-bidi-font-family:
&quot;Times New Roman&quot;;mso-font-kerning:1.0pt;mso-ansi-language:EN-US;mso-fareast-language:
ZH-CN;mso-bidi-language:AR-SA">E-mail</span></td>
                <td colspan="3"  width="144">
                      <input name="email" type="text" id="email" class="inputstyle" value="${vo.email}" /> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:22;page-break-inside:avoid;height:15.75pt">
                <td colspan="4" width="95">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">邮&nbsp;&nbsp;编</span></p>
                </td>
                <td colspan="3" width="59">
                      <input name="postnum" type="text" id="postnum" class="inputstyle" style="width:57px;" value="${vo.postnum}/> 
                </td>
                <td colspan="2"  width="61">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">电&nbsp;&nbsp;话</span></p>
                </td>
                <td colspan="3"  width="72">
                      <input name="phone" type="text" id="phone" class="required" value="${vo.phone}"/> 
                </td>
                <td align="center" colspan="2"  width="60">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot; text-align:center">手&nbsp;&nbsp;机</span>
                </td>
                <td colspan="3"  width="144">
                      <input name="tel" type="text" id="tel" class="required" value="${vo.tel}"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:23;page-break-inside:avoid;height:80.55pt">
                <td  width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        性格特长</span></p>
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        简述</span></p>
                </td>
                <td colspan="17"  width="504">
                      <textarea name="specialty" rows="2" cols="20" id="specialty" class="inputstyle" style="height:100px; margin:0px; width:99%;" value="${vo.specialty }"></textarea> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:24;page-break-inside:avoid;height:20.85pt">
                <td  width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        填写个人志愿排序<span lang="EN-US">1</span>、<span lang="EN-US">2</span>…</span></p>
                </td>
                <td colspan="17" width="504">
                    <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        考研（<input id="Text5" class="inputstyle" name="volunteer" style="width:30px" type="text" />）
、考公务员（<input id="Text37" class="inputstyle" name="volunteer" style="width:30px" type="text" />）
、出国深造（<input id="Text38" class="inputstyle" name="volunteer" style="width:30px" type="text" />）
、天健（<input id="Text39" class="inputstyle" name="volunteer" style="width:30px" type="text" />）
、其他选择（<input id="Text40" class="inputstyle" name="volunteer" style="width:30px" type="text" />）</span>
                </td>
            </tr>
            <tr style="mso-yfti-irow:25;page-break-inside:avoid;height:21.05pt">
                <td  width="94">
                    <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        求职岗位（主选备选各一）</span>
                </td>
                <td colspan="17" width="504">
                      <input name="wantjob" type="text" id="wantjob" class="inputstyle" style="height:20.05pt;" value="${vo.wantjob}"/> 
                </td>
            </tr>
            <tr style="mso-yfti-irow:26;mso-yfti-lastrow:yes;page-break-inside:avoid;  height:15.75pt">
                <td width="94">
                    <p align="center" class="MsoNormal" style="text-align:center">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        实习安排</span></p>
                </td>
                <td colspan="17" width="504">
                    <p class="MsoNormal">
                        <span style="font-family:黑体;mso-hansi-font-family:&quot;Times New Roman&quot;">
                        时间：<input id="plantimebegin" onclick="SelectDate(this,'yyyy 年MM 月 dd日')" style="width:150px" class="inputstyle" name="plantimebegin" type="text" value="${vo.plantimebegin}"/> 
                        至<input id="plantimeendtime" onclick="SelectDate(this,'yyyy 年MM 月 dd日')" style="width:150px" class="inputstyle" name="plantimeendtime" type="text"  value="${vo.plantimeendtime}"/> 
                        （尽早安排；自行解决住宿。）</span></p>
                </td>
            </tr>
        </table>
    </div>
</form>
</div>
        <p></p>



<!--<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="22" colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td width="37%" align="right"><input type="submit" name="next"
			value="确  定" class="flyBT" onclick="mySubmit();";></td>
		<td width="8%">&nbsp;</td>
		<td width="55%"><input type="button" name="back" value="返  回"
			class="flyBT" onClick="window.history.back();"></td>
	</tr>
</table>

--><input name="AS_dog" type="hidden" id="AS_dog" value=""></form>

<script type="text/javascript">
new Validation('thisForm');

function mySubmit() {
	
	
		thisForm.action="${pageContext.request.contextPath}/resume.do?method=add";
	
	document.thisForm.submit();
}

</script>

</body>
</html>
