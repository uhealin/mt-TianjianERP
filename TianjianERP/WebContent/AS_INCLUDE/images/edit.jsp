<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>


<script language="javascript">quote='';</script>


<link rel="STYLESHEET" type="text/css" href="${pageContext.request.contextPath}/AS_INCLUDE/images/edit.css">
<Script Src="${pageContext.request.contextPath}/AS_INCLUDE/images/DhtmlEdit.js"></Script>
<table id="oblog_Container" class="oblog_Body" height=100% width=585 cellpadding=1 cellspacing=0 border=0 >
  <tr> 
    <td  height="10"> <table cellpadding=0 cellspacing=0 >
        <tr class="yToolbar" ID="ExtToolbar0" > 
          <td> <select language="javascript" class="oblog_TBGen" id="FontSize" onChange="FormatText('fontsize',this[this.selectedIndex].value);">
              <option class="heading" selected>字号
              <option value="1">1 
              <option value="2">2 
              <option value="3">3 
              <option value="4">4 
              <option value="5">5 
              <option value="6">6 
              <option value="7">7</option>
            </select> 
          <td class="oblog_Btn" TITLE="¼Ó´Ö" LANGUAGE="javascript" onClick="FormatText('bold', '');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/bold.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="Ð±Ìå" LANGUAGE="javascript" onClick="FormatText('italic', '');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/italic.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="ÏÂ»®Ïß" LANGUAGE="javascript" onClick="FormatText('underline', '');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/underline.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="È¡Ïû¸ñÊ½" LANGUAGE="javascript" onClick="FormatText('RemoveFormat', '');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/removeformat.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="×ó¶ÔÆë" NAME="Justify" LANGUAGE="javascript" onClick="FormatText('justifyleft', '');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/aleft.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="¾ÓÖÐ" NAME="Justify" LANGUAGE="javascript" onClick="FormatText('justifycenter', '');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/center.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="ÓÒ¶ÔÆë" NAME="Justify" LANGUAGE="javascript" onClick="FormatText('justifyright', '');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/aright.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="²åÈë±íÇé" LANGUAGE="javascript" onClick="oblog_foremot()" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/smiley.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td id="forecolor" name=forecolor class="oblog_Btn" TITLE="×ÖÌåÑÕÉ«" LANGUAGE="javascript" onClick="oblog_foreColor();" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/fgcolor.gif" WIDTH="16" HEIGHT="16" unselectable="on" > </td>
          <td id="backcolor" class="oblog_Btn" TITLE="×ÖÌå±³¾°ÑÕÉ«" LANGUAGE="javascript" onClick="oblog_backColor();ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn';> 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/fbcolor.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="²åÈë³¬¼¶Á´½Ó" LANGUAGE="javascript" onClick="oblog_forlink();ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/wlink.gif" WIDTH="18" HEIGHT="18" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="È¥µô³¬¼¶Á´½Ó" LANGUAGE="javascript" onClick="FormatText('Unlink');ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn'; > 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/unlink.gif" WIDTH="16" HEIGHT="16" unselectable="on"> </td>
          <td class="oblog_Btn" TITLE="ÇåÀí´úÂë" LANGUAGE="javascript" onClick="oblog_CleanCode();ondrag='return false;'" onmouseover=this.className='oblog_BtnMouseOverUp'; onmouseout=this.className='oblog_Btn';> 
            <img class="oblog_Ico" src="${pageContext.request.contextPath}/AS_INCLUDE/images/cleancode.gif" WIDTH="16" HEIGHT="16"></td>
        </tr>
      </table></tr>
  <tr> 
    <td height="100%" id=PostiFrame> <iframe class="oblog_Composition" ID="oblog_Composition" MARGINHEIGHT="5" MARGINWIDTH="5" width="100%" height="100%"></iframe> </td>
     </tr>
  <tr >
    <td height="20"><TABLE CELLPADDING=0 CELLSPACING=0 BORDER=0 width='100%'>
        <TR> 
          <TD><div align="right"><a href="javascript:oblog_Size(-100)"><img src="${pageContext.request.contextPath}/AS_INCLUDE/images/minus.gif" unselectable="on" border='0' height="20"></a> <a href="javascript:oblog_Size(100)"><img src="${pageContext.request.contextPath}/AS_INCLUDE/images/plus.gif" unselectable="on" border='0' height="20"></a></div></TD>
          <TD width='20'></TD>
        </TR>
      </TABLE></td>
  </tr>
</table>
<Script>
var oblog_bIsIE5=document.all;
var canusehtml='1';
var PostType=1;

if (oblog_bIsIE5){
	var IframeID=frames["oblog_Composition"];
}
else{
	var IframeID=document.getElementById("oblog_Composition").contentWindow;
	var oblog_bIsNC=true;
}

if (oblog_bLoad==false)
{
	oblog_InitDocument("Body","GB2312");
}
function submits(){
document.all("edit").value=IframeID.document.body.innerHTML;
}
function initx(){
IframeID.document.body.innerHTML=quote
}
initx();
</Script>
<Script Src="${pageContext.request.contextPath}/AS_INCLUDE/images/editor.js" charset="GBK"></Script>




