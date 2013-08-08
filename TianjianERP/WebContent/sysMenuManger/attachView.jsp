<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.MtComAttachVO"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.AttachExtVO"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.attachFileUploadService.model.AttachFile"%>
<%@page import="java.util.List"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.service.attachFileUploadService.AttachFileUploadService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

    
 <%
      String indexid=request.getParameter("indexid");
      Connection conn=new DBConnect().getConnect();
      DbUtil dbUtil=new DbUtil(conn);
      List<MtComAttachVO> mtcomattachVO= dbUtil.select(MtComAttachVO.class, 
    		"select * from {0} where indexid=?", indexid);
     
 %>

    <ol type="1">
      <% for(MtComAttachVO attachFile:mtcomattachVO){ 
        String src=MessageFormat.format(request.getContextPath()+"/common.do?method=attachDownload&attachId={0}", attachFile.getATTACHID());
      %>

      <li>
            <fieldset style="width:420px"><legend> <a href="<%=src%>"><%=attachFile.getATTACHNAME() %>&nbsp; 附件下载</a></legend><br/>
             <% if(attachFile.isImg()){ %>
              <img alt=""  src="<%=src%>" width="400" height="300"/>
              <%} else if(attachFile.isVideo()||attachFile.isAudio()){ %>
            
              
<object id=nstv classid=CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6 width=400 height=300 codebase=http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701standby=Loading Microsoft? Windows Media? Player components... type=application/x-oleobject>
<param name=URL value="<%=src%>">
<PARAM NAME=AutoStart value=false>
<PARAM NAME=Enabled value=true>
<PARAM NAME=enableContextMenu value=false>
</object>
              <%} else if(attachFile.isFalsh()){%>
           <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="400" height="300">
              <param name="movie" value="<%=src%>&ftype=flash">
              <param name="quality" value="high">
              <param name="wmode" value="transparent">
              <param name="menu" value="false">
              <embed src="<%=src %>&ftype=flash" width="400" height="300" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" wmode="transparent"></embed>
            </object>
              <%} %>					   
              
            </fieldset>
      </li>
      <% }%>
    </ol>