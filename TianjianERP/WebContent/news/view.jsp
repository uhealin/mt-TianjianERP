<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>新闻查看</title>

<style type="text/css">

span{
 font-family:宋体;
 font-size: 12pt;
 mos-fareast-font-family:宋体;
}

TD {
	FONT-SIZE: 12px}
A:link {
TEXT-DECORATION: none
; font-size: 12px; color: #000000}
A:visited {
TEXT-DECORATION: none
; font-size: 12px; color: #000000}
A:active {
	TEXT-DECORATION: none
; font-size: 12px}
A:hover {
	COLOR: #CC0000; TEXT-DECORATION: underline
; font-size: 12px}
.biaoti {  font-size: 14px; font-weight: bold.px; color: #000000; font-weight: bold}
.f14 {  font-size: 14px; text-decoration: none; line-height: 1.5}
.f12 {
	font-size: 12px;
	line-height: 1.5;
	text-decoration: none;
	font-weight: normal;
}
#input1 {  font-size: 12px; border-color: #000000 #000000 #000000 #000000;border-style: ridge; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px; background-color: #FFFFFF}
.p1 {  font-size: 10px}
.f14b {  font-size: 14px; font-weight: bold; line-height: 1.3}
.button {  background-repeat: no-repeat; height: 19px; width: 45px; left: 0px; top: 0px; clip:   rect(0px 0px 0px 0px); background-position: center center; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding-top: 2px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; background-color: #EFEFEF; clear: 0; float: 0; background-image: url("images/20030118/button.gif"/*tpa=http://www.chinaacc.com/images/20030118/button.gif*/); border-style: none; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px; font-size: 12px; line-height: 1.3}
#input2 {  font-size: 12px; border-color: #000000 #000000 #000000 #000000;border-style: ridge; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px; background-color: #F7F9FF}
#input3 {  font-size: 12px; border-color: #000000 #000000 #000000 #000000;border-style: ridge; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px; background-color: #FFFFF7}
.button1 {  font-size: 12px; background-repeat: no-repeat; height: 19px; width: 45px; left: 0px; top: 0px; clip:   rect(0px 0px 0px 0px); background-position: center center; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding-top: 2px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; background-color: #EFEFEF; clear: 0; float: 0; background-image: url("images/20030118/button1.gif"/*tpa=http://www.chinaacc.com/images/20030118/button1.gif*/); border-style: none; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px; font-size: 12px; line-height: 1.3}
.button2 {  font-size: 12px; background-repeat: no-repeat; height: 19px; width: 45px; left: 0px; top: 0px; clip:   rect(0px 0px 0px 0px); background-position: center center; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding-top: 2px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; background-color: #EFEFEF; clear: 0; float: 0; background-image: url("images/20030118/button2.gif"/*tpa=http://www.chinaacc.com/images/20030118/button2.gif*/); border-style: none; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px; font-size: 12px; line-height: 1.3}
.button3 {  font-size: 12px; background-repeat: no-repeat; height: 19px; width: 45px; left: 0px; top: 0px; clip:   rect(0px 0px 0px 0px); background-position: center center; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding-top: 2px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; background-color: #EFEFEF; clear: 0; float: 0; background-image: url("images/20030118/button3.gif"/*tpa=http://www.chinaacc.com/images/20030118/button3.gif*/); border-style: none; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px; font-size: 12px; line-height: 1.3}
.button4 {  font-size: 12px; background-repeat: no-repeat; height: 19px; width: 68px; left: 0px; top: 0px; clip:   rect(0px 0px 0px 0px); background-position: center center; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding-top: 2px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; background-color: #EFEFEF; clear: 0; float: 0; background-image: url("images/20030118/button4.gif"/*tpa=http://www.chinaacc.com/images/20030118/button4.gif*/); border-style: none; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px; font-size: 12px; line-height: 1.3}
A.wt:link {
	COLOR: #000099;
	TEXT-DECORATION: underline
;
	font-size: 12px;
	line-height: 1.5;}
A.wt:visited {
	COLOR: #000099; TEXT-DECORATION: underline
; font-size: 12px}
A.wt:active {
	TEXT-DECORATION: underline
; font-size: 12px}
A.wt:hover {
	COLOR: #CC0000; TEXT-DECORATION: none
; font-size: 12px}
A.gg:link {
	COLOR: #003366;
	TEXT-DECORATION: underline
;
	font-size: 12px;
	line-height: 1.5;}
A.gg:visited {
	COLOR: #003366; TEXT-DECORATION: underline
; font-size: 12px}
A.gg:active {
	TEXT-DECORATION: underline
; font-size: 12px}
A.gg:hover {
	COLOR: #0000FF; TEXT-DECORATION: none
; font-size: 12px}
A.dt:link {
	COLOR: #000099;
	TEXT-DECORATION: underline
;
	font-size: 12px;
	line-height: 1.5;}
A.dt:visited {
	COLOR: #000099; TEXT-DECORATION: underline
; font-size: 12px}
A.dt:active {
	TEXT-DECORATION: underline
; font-size: 12px}
A.dt:hover {
	COLOR: #990000; TEXT-DECORATION: none
; font-size: 12px}
A.zs:link {
	COLOR: #000099;
	TEXT-DECORATION: underline
;
	font-size: 12px;
	line-height: 1.5;}
A.zs:visited {
	COLOR: #000099; TEXT-DECORATION: underline
; font-size: 12px}
A.zs:active {
	TEXT-DECORATION: underline
; font-size: 12px}
A.zs:hover {
	COLOR: #FF0000; TEXT-DECORATION: none
; font-size: 12px}
A.zp:link {
	COLOR: #000099;
	TEXT-DECORATION: underline
;
	font-size: 12px;
	line-height: 1.5;}
A.zp:visited {
	COLOR: #000099; TEXT-DECORATION: underline
; font-size: 12px}
A.zp:active {
	TEXT-DECORATION: underline
; font-size: 12px}
A.zp:hover {
	COLOR: #009900; TEXT-DECORATION: none
; font-size: 12px}
.f14bd {  font-size: 14px; line-height: 1.5; font-weight: bold}
.seleclass {
	BACKGROUND-COLOR: #F7F9FF; BORDER-BOTTOM: 0px; BORDER-LEFT: 0px; BORDER-RIGHT: 0px; BORDER-TOP: 0px; BOTTOM: 0px; CLIP: rect(0px 0px 0px 0px); COLOR: #000000; LEFT: 0px; MARGIN: -1px -1px; RIGHT: 0px; TOP: 0px
; padding: 0px 0px
; font-size: 12px
}
.f12t {  font-size: 12px}
#TopInfo1 {border-bottom:1px dashed #CCC; border-top:1px dashed #CCC; background-color:#FFF; margin-bottom:1px;}
#TopInfo2 {border-bottom:1px dashed #CCC; background-color:#FFF; margin-bottom:1px;}
.BodyRight {border-left:1px dashed #CCC;}


</style>
<script Language=JavaScript>

	function ext_init(){
		/*
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
            	text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
            		closeTab(parent.tab);
	            }
       		}
            //,{
            //	text:'返回',
	         //   cls:'x-btn-text-icon',
	          //  icon:'${pageContext.request.contextPath}/img/back.gif',
	          //  handler:function () {
            //		window.history.back();
	         //   }
       		//}
			]
        });
        */
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body leftmargin="0" topmargin="0" >

<div id="divBtn"></div>
 	
 <div style="text-align: center;height: 98%;overflow: scroll;">
 
    <div style="width:90%;margin-top: 2%;padding-bottom: 20px">
     
       <div id="newsTitle" style="border-style: dashed;border-width:1px 0px 1px 0px;padding: 10px 10px 10px 10px;">
          <h2 style="font-size: 20px;font-family: 黑体; font-weight: bolder;" >${news.title }</h2><br/>
          <h2>${news.doc_no }</h2>
          <div style="text-align: right">
            <span style="font-size: 1.0em">发布日期  :&nbsp;${news.updateTime}</span>&nbsp;
            
         <!--   <span>发部部门:&nbsp;【${departName}】</span>&nbsp;  -->
            <font style="font-size: 1.0em;width: 30px;height:12px;overflow: hidden; ">${areaName }</font> <span style="font-size: 1.0em">【${news.type}】</span>&nbsp;
          </div>
            
       </div>
      
       <div id="content" style="text-align: left;margin: 10px 0px 10px 0px;FONT-SIZE: 14px;line-height: 1.5">
       <font id="zoom">
         ${news.contents }
        </font>
       </div>
    
    
       <div style="text-align: right; border-style: dashed;border-width:1px 0px 1px 0px;padding: 10px 10px 10px 10px;">
                                          页面功能 【字体：<span style="cursor: pointer;font-size: 1.0em" class="txt" onclick="$('#content p').css('font-size','1.5em')">大</span> 
            <span style="cursor: pointer;font-size: 1.0em" class="txt" onclick="$('#content p').css('font-size','1.0em')">中</span>
            <span style="cursor: pointer;font-size: 1.0em" class="txt" onclick="$('#content p').css('font-size','0.7em')">小</span>                              
                                          】【<span style="cursor: pointer;font-size: 1.0em" class="txt" onclick="closeTab(parent.tab);">关闭</span>】
       </div>
       
       <jsp:include page="/sysMenuManger/attachView.jsp">
				  <jsp:param value="${news.attachmentId}" name="indexid"/>
				</jsp:include>
       
       <div style="text-align: center;margin-top: 35px">
      
       </div>
      
     
            
    </div>
 
 
 </div>
 	  
         
</body>



</html>
