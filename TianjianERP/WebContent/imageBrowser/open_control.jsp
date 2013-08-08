<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="gbk"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- saved from url=(0125)http://www.bdo-lxdh.com:8080/general/picture/open_control.php -->
<%@page import="com.matech.audit.service.attachFileUploadService.model.Attach"%>
<%@page import="java.util.List"%>
<%

	List<Attach> listImage =(List<Attach>) request.getAttribute("listImages");
    String defaultImageId = (String)request.getAttribute("defaultImageId");
    if("".equals(defaultImageId)){
    	Attach defaultImage = listImage.get(1);
    	defaultImageId = defaultImage.getAttachId(); 
	    //defaultImageId = "260b3a1d-0162-4646-8b14-e39e92b06b69"; 测试
    }
%>

<HTML>
<HEAD>
<TITLE>图片管理</TITLE>
<LINK rel=stylesheet type=text/css href="image/style.css">

<SCRIPT src="image/ccorrect_btn.js"></SCRIPT>
<META content="text/html; charset=utf-8" http-equiv=Content-Type>
<META name=GENERATOR content="MSHTML 8.00.6001.19154"></HEAD>
<BODY style="BACKGROUND-IMAGE: url(/images/topbar.gif)" 
onload="inionload('${pageContext.request.contextPath}/common.do?method=attachDownload&attachId=<%=defaultImageId%>')" leftMargin=0 topMargin=0>
<SCRIPT language=javascript>
var FILE_ATTR_ARRAY=new Array(
    <%
    
	for (int i =0; i<listImage.size();i++ ){
		
		Attach images = listImage.get(i);
		//String imageId = "${pageContext.request.contextPath}/common.do?method=attachDownload&op=1&attachId="+images.getId();
			if(listImage.size()-1 == i){
				
    %>
     	
				new Array("<%=i%>","${pageContext.request.contextPath}/common.do?method=attachDownload&attachId=<%=images.getAttachId()%>","<%=images.getUpdateTime()%>","<%=images.getAttachName()%>","<%=images.getWidth()%>","<%=images.getHeight()%>")
	<%
			}else{
			
	%>
				new Array("<%=i%>","${pageContext.request.contextPath}/common.do?method=attachDownload&attachId=<%=images.getAttachId()%>","<%=images.getUpdateTime()%>","<%=images.getAttachName()%>","<%=images.getWidth()%>","<%=images.getHeight()%>"),
	<%
			}
		
		 }
	%>	
	
		);
</SCRIPT>

<SCRIPT language=javascript>

//
var cur_pic_no=0;

//放大
var up_width,up_height;
//var mywidth,myheight;
function blowup()
{
	 mywidth=parent.open_main.image.width;
	 myheight=parent.open_main.image.height;

	 up_width=mywidth * 1.1;
	 up_height=myheight * 1.1;

	 parent.open_main.image.width=up_width;
	 parent.open_main.image.height=up_height;
}
//缩小
function reduce()
{
	 mywidth=parent.open_main.image.width;
	 myheight=parent.open_main.image.height;

	 up_width=mywidth * 0.9;
	 up_height=myheight * 0.9;

	 parent.open_main.image.width=up_width;
	 parent.open_main.image.height=up_height;
}

//最适合  实际大小
function adapt(flag)
{
	 parent.open_main.pictable.style.zoom="100%";

	 true_width = FILE_ATTR_ARRAY[cur_pic_no][4];
	 true_height = FILE_ATTR_ARRAY[cur_pic_no][5]
	 clientWidth = parent.open_main.document.body.clientWidth;
	 clientHeight = parent.open_main.document.body.clientHeight;

	 if(flag==1) //实际大小
	 {
	   up_width=true_width;
	   up_height=true_height;
   }
	 else if(flag==2) //最适合
	 {
	 	 padbottom = 30;
	 	 if(true_width > clientWidth && true_height <= clientHeight)
	   {
	   	  up_width=clientWidth;
	   	  up_height=true_height*clientHeight/true_width - padbottom;
	   }
	   if(true_height > clientHeight && true_width <= clientWidth)
	   {
	   	  up_height=clientHeight - padbottom;
	   	  up_width=true_width*clientHeight/true_height;
	   }
	 	 if(true_width > clientWidth && true_height > clientHeight)
	   {
	   	  if(true_width >= true_height)
	   	  {
	   	  	 up_height=clientHeight - padbottom;
	   	     up_width=true_width*clientHeight/true_height;
	   	  }
	   	  else
	   	  {
	   	    up_width=clientWidth;
	   	    up_height=true_height*clientWidth/true_width - padbottom;
	   	  }
	   }
	 	 if(true_width < clientWidth && true_height < clientHeight)
	   {
	   	  up_height=true_height;
	   	  up_width=true_width;
	   }
   }
   parent.open_main.image.width =up_width;
   parent.open_main.image.height =up_height;
}

function inionload(file_name)
{
   for(var i=0;i<FILE_ATTR_ARRAY.length;i++)
   {
   	 if(FILE_ATTR_ARRAY[i][1]==file_name)
   	    cur_pic_no = FILE_ATTR_ARRAY[i][0];
   }

   if(typeof(parent.open_main.div_image)=="object")
   {
		  parent.open_main.div_image.innerHTML="<img onload='parent.open_control.adapt(2);' src='"+file_name+"' alt='鼠标滚轮缩放，点击图片翻页' border=0 id='image' width=1 height=1>";
		  
		  parent.open_main.file_name.innerText=FILE_ATTR_ARRAY[cur_pic_no][3];
		  parent.open_main.pictable.style.zoom="100%";
  }
}

function open_pic(op)
{
	 cur_pic_no=parseInt(cur_pic_no)+op;
   if(parseInt(cur_pic_no) <= -1)
   	  cur_pic_no = FILE_ATTR_ARRAY.length - 1;
   else if(parseInt(cur_pic_no) >= FILE_ATTR_ARRAY.length)
   	  cur_pic_no = 0;
   file_name=FILE_ATTR_ARRAY[cur_pic_no][1];
   parent.open_main.image.src=""+file_name;
   parent.open_main.file_name.innerText=FILE_ATTR_ARRAY[cur_pic_no][3];
   parent.open_main.pictable.style.zoom="100%";
}

function down_pic()
{
	//window.location="${pageContext.request.contextPath}/imagesBrowser/down.jsp?PIC_ID=4&SUB_DIR=&FILE_NAME="+FILE_ATTR_ARRAY[cur_pic_no][1];
	window.location=FILE_ATTR_ARRAY[cur_pic_no][1];
}


</SCRIPT>

<TABLE class=small border=0 cellSpacing=0 cellPadding=2 width="100%" 
align=center>
  <TBODY>
  <TR>
    <TD vAlign=center align=middle><SPAN style="PADDING-TOP: 2px"><A 
      href="javascript:open_pic(-1);"><IMG title=上一张 border=0 
      src="${pageContext.request.contextPath}/imageBrowser/image/pre_pic.png" width=48 height=48></A>&nbsp; <A 
      href="javascript:open_pic(1);"><IMG id=a_id title=下一张 border=0 
      src="${pageContext.request.contextPath}/imageBrowser/image/next_pic.png" width=48 height=48></A>&nbsp; <A 
      href="javascript:adapt(2);"><IMG title=最适合 border=0 
      src="${pageContext.request.contextPath}/imageBrowser/image/adapt.png" width=48 height=48></A>&nbsp; <A 
      href="javascript:adapt(1);"><IMG title=实际大小 border=0 
      src="${pageContext.request.contextPath}/imageBrowser/image/original.png" width=48 height=48></A>&nbsp; <A 
      href="javascript:blowup()"><IMG title=放大 border=0 
      src="${pageContext.request.contextPath}/imageBrowser/image/plus.gif" width=48 height=48></A>&nbsp; <A 
      href="javascript:reduce();"><IMG title=缩小 border=0 
      src="${pageContext.request.contextPath}/imageBrowser/image/minus.gif" width=48 height=48></A>&nbsp; <A 
      href="javascript:down_pic();"><IMG title=保存图片 border=0 
      src="${pageContext.request.contextPath}/imageBrowser/image/save.gif" width=48 height=48></A> 
  </SPAN></TD></TR></TBODY></TABLE></BODY></HTML>
