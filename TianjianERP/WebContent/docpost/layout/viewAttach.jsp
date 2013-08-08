<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.audit.service.user.model.UserVO"%>
<%@page import="com.matech.audit.service.doc.model.DocPostVO"%>
<%@page import="java.util.ArrayList"%>
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
      String uuid=request.getParameter("uuid");
      WebUtil webUtil=new WebUtil(request,response);
      UserSession userSession=webUtil.getUserSession();
      Connection conn=null;
      DbUtil dbUtil=null;
      DocPostVO docPostVO=null;
      List<MtComAttachVO> mtcomattachVOs= new ArrayList<MtComAttachVO>();
      String mode=request.getParameter("mode");
      boolean isView="view".equals(mode);
      try{
    	  conn=new DBConnect().getConnect();
    	  dbUtil=new DbUtil(conn);
    	  docPostVO=dbUtil.load(DocPostVO.class,uuid);
    	  mtcomattachVOs= dbUtil.select(MtComAttachVO.class, 
    	    		"select * from {0} where indexid=? order by updatetime desc", docPostVO.getAttach_id());
      }catch(Exception ex){
    	  
      }finally{
    	  
      }
 %>


<table class="formTable">
      
     <thead>
     
       <tr>
         <td>
         <%if(!isView){ %>
         <input type="hidden" id="attachid" value="<%=docPostVO.getAttach_id() %>" />
         <input type="button" value="上传附件" class="flyBT" onclick="attachUpload_('attachid');" /></td>
        <%} %>
         <th>文件名</th>
         <th>上传时间</th>
         <th>上传人</th>
         <th>操作</th>
       </tr>
     </thead>
     
     <tbody>
       
      <% for(int i=0;i<mtcomattachVOs.size();i++){ 
    	MtComAttachVO mtComAttachVO=mtcomattachVOs.get(i);
        String src=MessageFormat.format(request.getContextPath()+"/common.do?method=attachDownload&attachId={0}", mtComAttachVO.getATTACHID());
        UserVO userVO=dbUtil.load(UserVO.class, Integer.parseInt(mtComAttachVO.getUPDATEUSER()));
      %>
        <tr>
          <td><%=i+1 %></td>
          <td><a href="<%=src%>" title="下载 <%=mtComAttachVO.getATTACHNAME() %>"><img src="${pageContext.request.contextPath}/img/download.gif" /></a><%=mtComAttachVO.getATTACHNAME() %></td>
          <td><%=mtComAttachVO.getUPDATETIME() %></td>
          <td><%=userVO.getName() %></td>
          <td>
            <% if(userSession.getUserId().equals(mtComAttachVO.getUPDATEUSER())&&!isView){ %>
             <a href="#" onclick="attachRemove_('<%=mtComAttachVO.getATTACHID() %>','attachid')" ><img src="${pageContext.request.contextPath}/img/delete.gif" /></a>
             
            <%} %>
          </td>
        </tr>
      <% }%>
      </tbody>
 </table>
 
