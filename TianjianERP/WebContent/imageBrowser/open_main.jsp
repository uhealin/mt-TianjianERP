<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="GBK"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- saved from url=(0058)http://www.bdo-lxdh.com:8080/general/picture/open_main.php -->
<HTML><HEAD><TITLE>图片管理</TITLE>
<LINK rel=stylesheet type=text/css href="image/style.css">
<SCRIPT src="image/ccorrect_btn.js"></SCRIPT>

<META content="text/html; charset=utf-8" http-equiv=Content-Type>
<SCRIPT>
function bbimg(o)
{
  var zoom=parseInt(o.style.zoom, 10)||100;
  zoom+=event.wheelDelta/12;
  if(zoom>0)
     o.style.zoom=zoom+'%';
  return false;
}
</SCRIPT>

<META name=GENERATOR content="MSHTML 8.00.6001.19154"></HEAD>
<BODY style="BACKGROUND-COLOR: gray" leftMargin=0 topMargin=3>
<TABLE id=pictable title=鼠标滚轮缩放，点击图片翻页 onmousewheel="return bbimg(this)" 
onclick=parent.open_control.open_pic(1); border=0 cellSpacing=0 cellPadding=0 
width="100%" height="100%" topmargin="3">
  <TBODY>
  <TR>
    <TD class=big height=20 vAlign=center align=middle><FONT color=white><B>
      <DIV id=file_name></DIV></B></FONT></TD></TR>
  <TR>
    <TD vAlign=center align=middle>
      <DIV id=div_image><FONT color=white>正在加载图片...</FONT></DIV>
	  </TD>
</TR></TBODY></TABLE>
	  

</BODY></HTML>
