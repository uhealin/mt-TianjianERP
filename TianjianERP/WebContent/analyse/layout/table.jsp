<%@page import="com.matech.audit.service.analyse.model.*"%>

<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


   <%
     List<TableColVO> cols=(List<TableColVO>)request.getAttribute(TableColVO.class.getName());
   List<TableRowVO> rows=(List<TableRowVO>)request.getAttribute(TableRowVO.class.getName());
   %>
    
     <table class="formTable" style="margin: 0px">
        <tbody>
          <tr>
            <th>表设计</th>          
            <td><input id="tablecaption" value="${table.caption }" /> <input id="tableid" type="hidden" value="${table.uuid }" />
             <button onclick="var url='query2d.do';
                              var uuid=$('#tableid').val();
                              var caption=$('#tablecaption').val();
                              $.post(url,{uuid:uuid,caption:caption,method:'doSaveTable'},function(str){alert(str);treeRoot.reload();});
             ">保存设计</button>&nbsp;
             <button onclick="var url='query2d.do';
                 var uuid=$('#tableid').val();
             //var caption=$('#tablecaption').val();
             $.post(url,{uuid:uuid,method:'doDeleteTable'},function(str){alert(str);treeRoot.reload();});
             ">删除设计</button></td>
          </tr>
          <tr>
             <th>行</th>
             <td>
             <%
                for(TableRowVO row :rows){
             %>
              <span><a class="deleterow" id="<%=row.getUuid()%>"  onclick="doDeleteRow(this);" ><img src="img/delete.gif"></a> 
              <a class="row" id="<%=row.getUuid()%>" onclick="editCondition(this,'row','<%=row.getConid()%>');"><%=row.getCaption() %></a> </span>,
             <%} %>
             <a onclick="editCondition(this,'row',null);"><img src="img/add.gif"></a></td>
          </tr>
           <tr>
             <th>列</th>
              <td>
               <%
                for(TableColVO col :cols){
             %>
              <span><a class="deletecol" id="<%=col.getUuid()%>"  onclick="doDeleteCol(this);" ><img src="img/delete.gif"></a> 
              <a class="row" id="<%=col.getUuid()%>" onclick="editCondition(this,'col','<%=col.getConid()%>');"><%=col.getCaption() %></a> </span>,
             <%} %>
             <a onclick="editCondition(this,'col',null);"><img src="img/add.gif"></a></td>
          </tr>
        </tbody>
     </table>
     
    

