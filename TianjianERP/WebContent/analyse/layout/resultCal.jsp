<%@page import="com.matech.audit.service.analyse.model.ConditionVO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

    <%
       int[][] int2d=(int[][])request.getAttribute("int2d");
       List<ConditionVO> cols=(List<ConditionVO> )request.getAttribute("cols");
       List<ConditionVO> rows=(List<ConditionVO> )request.getAttribute("rows");
    %>
     
     
      <table id="tableResult" class="formTable">
       <thead>
          <tr>
            <th></th>
            <%for(ConditionVO conditionVO :cols){ %>
            <th><%=conditionVO.getCaption() %></th>
            <%} %>
          </tr>
       </thead>
       
       <tbody>
       
       <% for(int row=0;row<rows.size();row++){ %>
        <tr>
            <th><%=rows.get(row).getCaption() %></th>
            
            <% for(int i=0;i<int2d[row].length;i++){ %>
            <td><%=int2d[row][i] %></td>
            <%} %>
        </tr>
       <%} %>
       </tbody>
       
     </table>