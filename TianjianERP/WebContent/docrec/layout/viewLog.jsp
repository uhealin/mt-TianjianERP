<%@page import="com.matech.audit.service.doc.model.DocLogVO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
   List<DocLogVO> logs=(List<DocLogVO>)request.getAttribute("logs");
%>
    
   
      <table  class="formTable" cellpadding="3"  >
         <thead>
           <tr>
              <th style="width: 5%" >公开</th>
              <th style="width: 15%">审阅时间</th>
              <th style="width: 10%">审阅人</th>
              <th style="width: 70%">审阅意见</th>
              
           </tr>
         </thead>
         
         <tbody>
           <% for(DocLogVO log:logs){ %>
           <tr>
           <td><%="true".equals(log.getPublic_ind())?"是":"否" %></td>
             <td><%=log.getHandle_time() %></td>
             <td><%=log.getHandler_name() %></td>
             <td><%=log.getRemark() %></td>
             
           </tr>
           <%} %>
         </tbody>
         
      </table>
    