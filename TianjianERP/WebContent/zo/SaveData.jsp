<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*,java.awt.*,java.util.*" errorPage="" %>
<%request.setCharacterEncoding("utf-8");%>
<%@page pageEncoding="utf-8"%>
<%@ page import="com.zhuozhengsoft.ZSOfficeX.*"%>
<%
        WordRequest ZSWord = new WordRequest(request, response);

        String sTest1 = ZSWord.openDataRegion("test1").getValue();
        //out.print("Test1的值：" + sTest1);

	ZSWord.showPage(300, 200); // 显示SaveData 执行结果,正式使用注释此句.
        // ReturnOK方法是给客户端返回处理成功的信息。取消下面的注释的话，点保存的时候就不会弹出保存失败了。
        //ZSWord.returnOK();
%>