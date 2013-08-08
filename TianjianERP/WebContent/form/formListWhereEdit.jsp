
<%@page import="com.matech.audit.service.form.model.ButtonVO"%>
<%@page import="com.matech.audit.service.form.model.FormButton"%>
<%@page import="com.matech.audit.work.form.FormDefineAction"%>
<%@page import="java.util.*"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.service.form.model.FormVO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% 
   Connection conn=new DBConnect().getConnect();
   DbUtil dbUtil=null;
   String formid=request.getParameter("formid");
   FormVO formVO=null;
   List<ButtonVO> formButtons=new ArrayList<ButtonVO>();
   try{
	   dbUtil=new DbUtil(conn);
	   formVO=dbUtil.load(FormVO.class, formid);
	   formButtons=dbUtil.select(ButtonVO.class, "select * from {0} where formid=?", formid);
   }catch(Exception ex){
	   throw ex;
   }finally{
	   DbUtil.close(conn);
   }
%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript">

  function doCheckSql(code){
	  var formid=$("input[name=uuid]").val();
	  var where_sql=$("#where_"+code+"_sql").val();
	  var url="${pageContext.request.contextPath}/formDefine.do";
	
	  var param={formid:formid,where_sql:where_sql,method:"checkWhere"};
	  $.post(url,param,function(str){
		  //alert(str);
		  var json=eval("("+str+")");
		  var suc=json["re"]==0;
		  var title=suc?"测试成功":"测试失败,请检查sql";
		  $("#re_"+code).html(title);
		  if(!suc){
		    Ext.MessageBox.alert(title,json["sql"]);
		  }
	  });
  }

</script>
</head>
<body>
    <fieldset style="width: 99%">
    <legend><%=formVO.getNAME() %></legend>
    <textarea rows="6" style="width:98% " readonly="readonly"><%=formVO.getLISTSQL() %></textarea>
    </fieldset>

    <form action="${pageContext.request.contextPath}/formDefine.do?method=updateFormWhere" method="post">
    <input name="formTypeId" type="hidden" value="${param.formTypeId }" />
    <input name="uuid" type="hidden" value="<%=formVO.getUUID() %>" />
 
    <% for(String where_id:FormDefineAction.WHERE_IDS){ 
      Object temp=null;
      temp=FormVO.class.getDeclaredMethod("getWhere_"+where_id+"_name").invoke(formVO);
      String where_name=temp==null?"":temp.toString();
      temp=FormVO.class.getDeclaredMethod("getWhere_"+where_id+"_sql").invoke(formVO);
      String where_sql=temp==null?"":temp.toString();
      temp=FormVO.class.getDeclaredMethod("getWhere_"+where_id+"_hiddenbtn").invoke(formVO);
      String where_hiddenbtn=temp==null?"":temp.toString();
      temp=FormVO.class.getDeclaredMethod("getWhere_"+where_id+"_hiddencol").invoke(formVO);
      String where_hiddencol=temp==null?"":temp.toString();
      %>
        <fieldset style="width: 99%">
          <legend><%=where_id%>式：<%=FormDefineAction.pathFormListExtView(formid,where_id) %>
          </legend>
           按钮 参考值:<% for(ButtonVO formButton :formButtons){ %>
               <%=formButton.getENNAME() %>(<%=formButton.getNAME() %>),  
                 <%} %>
          <table class="formTable">
            <tbody>
              <tr>
                 <th>说明</th>
                 <td><input style="width: 400px" name="where_<%=where_id %>_name" type="text"  value="<%=where_name %>" /></td>
              </tr> 
              <tr>
                 <th>隐藏按钮(格式:[{按钮英文名},{按钮英文名},])<p>
               
                 </th>
                 <td><input style="width: 400px" name="where_<%=where_id %>_hiddenbtn" type="text"  value="<%=where_hiddenbtn %>" /></td>
              </tr>
              <tr>
                 <th>隐藏列(格式:[{按钮英文名},{按钮英文名},])</th>
                 <td><input style="width: 400px" name="where_<%=where_id %>_hiddencol" type="text"  value="<%=where_hiddencol %>" /></td>
              </tr>
              <tr>
                 <th>查询条件(无需加 where)</th>
                 <td>
                     <textarea style="width: 98%" rows="6" cols="90" name="where_<%=where_id %>_sql" id="where_<%=where_id %>_sql"><%=where_sql %></textarea>
                 </td>
              </tr>
              <tr>
                 <td colspan="2">
                   <button type="button" onclick="doCheckSql('<%=where_id%>')">检查</button><span id="re_<%=where_id%>"></span>             
                 </td>
              </tr>
            </tbody>
          </table>

        </fieldset>
         
    <%} %>
    <button type="submit">保存</button>
    <button type="button"
    onclick="window.location.href='${pageContext.request.contextPath}/formDefine.do?method=formList&formTypeId=${param.formTypeId}'"
    >返回</button>
    </form>
</body>
</html>