<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*,java.awt.*,java.util.*" errorPage="" %>
<%request.setCharacterEncoding("utf-8");%>
<%@page pageEncoding="utf-8"%>
<%@ page import="com.zhuozhengsoft.ZSOfficeX.*, java.awt.*"%>
<jsp:useBean id="ZSCtrl" scope="page" class="com.zhuozhengsoft.ZSOfficeX.ZSOfficeCtrl"></jsp:useBean>
<%
WordResponse ZSWord = new WordResponse();
ZSWord.FormMode = false;

WordResDataRegion dataRegion = ZSWord.openDataRegion("test1");
//dataRegion.setValue("测试的内容1");
//dataRegion.setNeedSubmit(true);

//dataRegion = ZSWord.openDataRegion("test2");
//dataRegion.setValue("测试的内容2");

WebUtil webUtil=new WebUtil(request,response);
// 设置ZSOFFICE组件服务页面
ZSCtrl.ServerURL = webUtil.getWebRootPath()+"/zsserver.do";

ZSCtrl.MenubarStyle = 5;
ZSCtrl.MenubarColor = Color.decode("#FF6633");
ZSCtrl.TitlebarColor = Color.decode("#FFFFFF");
ZSCtrl.TitlebarTextColor = Color.decode("#50C048");
ZSCtrl.Caption = "最简单的Word数据填充";
ZSCtrl.SaveDataURL = "SaveData.jsp";

ZSCtrl.assign(ZSWord); // 注意！不要忘记此句代码！
ZSCtrl.webOpen("doc/test.doc", 0, "somebody", "Word.Document");

%>


<html >
<head runat="server">
    <title>演示：最简单的Word数据填充</title>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>
    <div style="font-size:12px; line-height:20px; border-bottom:dotted 1px #ccc;border-top:dotted 1px #ccc; padding:5px;">
     下面文件中的两个数据区域都是使用WordResponse对象赋值的，参看后台Word.jsp文件代码。<br />
     由于设置了：<span style="background-color:Yellow;">ZSWord.FormMode = true;</span>所以全文都是只读不可编辑的。<br />
        由于“数据区域1（test1）”设置了属性<span style="background-color:Yellow;">setNeedSubmit(true);</span>所以“数据区域1”可以编辑，并且保存的时候数据区域的值会提交。<br />
    <span style="color:red;">操作说明：</span>修改“数据区域1”的值并保存文件，提示保存文件失败对话框后，点对话框中的“详细描述”按钮，即可看到SaveData页已经获取到的“数据区域1”的值。
   
    <span style="background-color:Yellow;"></span></div><br />
    <form id="form1" runat="server">
    <div>
    <!--**************   ZSOFFICE 客户端代码开始    ************************-->
	<SCRIPT language="JavaScript" event="OnInit()" for="ZSOfficeCtrl">
		// 控件打开文档前触发，用来初始化界面样式
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnDocumentOpened(str, obj)" for="ZSOfficeCtrl">
		// 控件打开文档后立即触发，添加自定义菜单，自定义工具栏，禁止打印，禁止另存，禁止保存等等
		ZSOfficeCtrl.AppendToolButton(1, "保存", 1);
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnDocumentClosed()" for="ZSOfficeCtrl">
		
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnUserMenuClick(index, caption)" for="ZSOfficeCtrl">
		// 添加您的自定义菜单项事件响应
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnCustomToolBarClick(index, caption)" for="ZSOfficeCtrl">
		// 添加您的自定义工具栏按钮事件响应
		if(index == 1) 	ZSOfficeCtrl.WebSave();
	</SCRIPT>	
	<%=ZSCtrl.getDocumentView("ZSOfficeCtrl", request)%>
    <!--**************   ZSOFFICE 客户端代码结束    ************************-->
    </div>
    </form>
</body>
</html>
