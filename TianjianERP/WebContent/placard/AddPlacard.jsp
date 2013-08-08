<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ page import="com.matech.framework.listener.OnlineListListener" %>
<%@ page import="com.matech.audit.service.fileupload.MyFileUpload" %>
<%@ page import="com.matech.framework.pub.autocode.DELUnid" %>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>


<%
	ASFuntion CHF=new ASFuntion();
	PlacardTable pt = new PlacardTable();
	
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	 
	try{
		conn = new DBConnect().getConnect();
		PlacardService pm = new PlacardService(conn);
		String opt =  CHF.showNull(request.getParameter("opt"));
		String strSQL="select id from k_placard order by id desc limit 1";
		ps = conn.prepareStatement(strSQL);
		rs = ps.executeQuery();
		String myID="NoFindID";
		while(rs.next()){
			myID=rs.getString("id");
		}
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

		String user = userSession.getUserId();
	
		if(request.getParameter("next")!=null){
			MyFileUpload myfileUpload = new MyFileUpload(request);
			String randomFileName = DELUnid.getNumUnid();
			String myPath=PlacardService.NOTE_FILE_PATH+myID+"/";
			myfileUpload.UploadFile(null, myPath);
			
			
			Map parameters = myfileUpload.getMap();
			
			
			//System.out.println("aaaaaaaaa");
			
			if (parameters==null){
				//System.out.println("bbbbbbbbsfadsfadsfasfad");
			}else{
				//System.out.println("aaaaaaaaabbb:"+parameters.get("Caption"));
			}
	
		String Caption = CHF.showNull((String) parameters.get("Caption"));
		//System.out.println("aaaaaaaaa4444");

		String Filename=CHF.showNull((String) parameters.get("filename"));
		String ImageName="/placard/"+myID+"/"+Filename;
		if(Filename.equals(""))
		{
			ImageName="";
		}
		

	
		
		String ctype = CHF.showNull((String) parameters.get("ctype"));
		
		if("".equals(ctype)) {
			ctype = "项目交互";
		}
	//	org.util.Debug.prtOut(Caption);
		if(!"".equals(Caption)){
	String st = CHF.showNull((String) parameters.get("Matter")) + "&nbsp;&nbsp;&nbsp;<br>";
	//ZYQ
	/*
	st = st.replaceAll("\n","<br>");
	st = st.replaceAll("\r","");
	st = st.replaceAll("\"","\\\"");
	st = st.replaceAll(" ","&nbsp;");
	*/
	String IsReversion = CHF.showNull((String) parameters.get("IsReversion"));
	if("".equals(IsReversion)){
		IsReversion="0";
	}
	String IsNotReversion = CHF.showNull((String) parameters.get("IsNotReversion"));

	if("".equals(IsNotReversion)){
		IsNotReversion="0";
	}
	
	pt.setIsNotReversion(Integer.parseInt(IsNotReversion));
	pt.setProperty("");
	
	pt.setCaption(Caption);
	pt.setMatter(st);
	pt.setIsReversion(Integer.parseInt(IsReversion));
	pt.setAddresser(user);
	pt.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
	pt.setIsRead(0);
	pt.setCtype(ctype);
	pt.setImage(ImageName);
	
	
	
	String Usrs = CHF.showNull((String) parameters.get("Usrs"));
	//		org.util.Debug.prtOut(Usrs);
	String [] s = Usrs.split("\\|");
	for(int i=0;i<s.length; i++){
		if(s[i]!=null && !"".equals(s[i])){
			pt.setAddressee(s[i]);
			pm.AddPlacard(pt);
		}
	}
		
	//out.println("<script>window.close();</script>");
	System.out.print("11111111="+opt+"\n");
	if("".equals(opt)){
		out.println("<script>alert('发送公告成功');</script>");
		out.println("<script>window.location = 'View.jsp';</script>");
	}else{
		out.println("<script>alert('发送短信');</script>");
		out.println("<script>closeTab(parent.tab);</script>");
	}
		}
		}
%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="com.matech.audit.service.placard.model.PlacardTable"%>
<%@page import="com.matech.audit.service.placard.PlacardService"%>
<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>公告栏</title>

<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>

<script type="text/javascript">
	
	Ext.onReady(function () {
		var tbar = new Ext.Toolbar({
		region: 'north',
		height:27,
           items:[{
		            text:'发送',
		            icon:'${pageContext.request.contextPath}/img/confirm.gif',
		            handler:function(){
		            	goAdEd();
					}
      			},'-',{
		            text:'关闭',
		            icon:'${pageContext.request.contextPath}/img/close.gif',
		            handler:function(){
		            	closeTab(parent.tab);
					}
      			}
        	]
        });
        
        var Tree = Ext.tree;
        
        var data = new Tree.TreeLoader({
		 //dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getDepartmentList',
		 dataUrl:'${pageContext.request.contextPath}/placard.do?method=getUserJsonTree&addUser=true&checked=false',	
		 baseParams:{joinUser:'${joinUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
		});
        
		var tree = new Tree.TreePanel({
			region:'west',
	        id:'userTree',
	        autoScroll:true,
	        title:'选择收信人',
	        animate:true,
	        width:200,
	        rootVisible:false,
	        containerScroll: true, 
	        loader: data
	    });
	    
	    data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
		},data);
	
		tree.on('checkchange', function(node, checked) {   
			node.expand();   
			node.attributes.checked = checked; 
			var valuelist=document.getElementById("accepname").value;
			if(node.isLeaf() && node.attributes.checked){
			    if(valuelist.indexOf(node.attributes.username)==-1){
					valuelist+=node.attributes.username+",";
				}
			}else if(node.isLeaf() && node.attributes.checked==false){
			    valuelist=valuelist.replace(valuelist.substr(valuelist.indexOf(node.attributes.username),node.attributes.username.length+1),"");
			}
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;  
			   if(child.isLeaf() && child.attributes.checked){
				   valuelist+=child.attributes.username+",";
			   }else if(child.isLeaf() && child.attributes.checked==false){
			       valuelist=valuelist.replace(valuelist.substr(valuelist.indexOf(child.attributes.username),child.attributes.username.length+1),"");
			   }
			});
			  document.getElementById("accepname").value=valuelist;	
			}, tree);  
		
	   	var root = new Tree.AsyncTreeNode({
	        text: '发送人员列表',
	        draggable:false,
	        id:'root'
	    });
	    tree.setRootNode(root);
	    root.expand();
	    
	    var layout = new Ext.Viewport({
			layout:'border',
			items:[tbar,tree,{
				region:'center',
				contentEl: 'centerDiv',
				margins:'0 0 0 0',
				split:true,
				cmargins:'0 0 0 0',
		        lines:false
			}]
	    });
	});
	
</script>

</head>

<body>

<div id="centerDiv">
<form name="thisForm" method="post" id="thisForm" class="autoHeightForm" action="" enctype="multipart/form-data">
	<input type="hidden" value="<%=opt %>" id="opt" name="opt">
  <table>
  	 <tr height="20">
		<td height="23" align="center" ><strong>收件人：</strong></td>
        <td height="23" align="left"><input name="accepname" id="accepname" type="text" maxlength="50" style="height:20px;width:300px">&nbsp;&nbsp;
       
      </td>
	</tr>
	<tr height="20">
		<td height="23" align="center" ><strong>文件标题：</strong></td>
      <td height="23" align="left"><input name="Caption" type="text" maxlength="50" style="height:20px;width:300px">&nbsp;&nbsp;
      <input type="checkbox" name="IsReversion" value="1">已读回复&nbsp;&nbsp;
      <input type="checkbox" name="IsNotReversion" value="1">查看人无权回复
      </td>
	</tr>
	<tr>
	<td height="23" align="center"><strong>公告分类：</strong></td>
	<td height="23" align="left"><input name="ctype" id="ctype" type="text" size="30" onKeyDown="onKeyDownEvent();" value="项目交互" onKeyUp="onKeyUpEvent();" maxlength="50"  onClick="onPopDivClick(this);" valuemustexist=true autoid=371>
   </td>
   
	</tr>
	<tr>
	<td height="23" align="center" ><strong>附件上传：</strong></td>
	<td height="23" align="left" ><input type="file" name="image" id="image" value="" size="90" title="请输入，不得为空">
   </td>
   
	</tr>
	<tr height=18>
	  <td height="320" align="center"><strong>文件内容：</strong></td>
	  <td align="left" ><label>
	    <jsp:include page="../AS_INCLUDE/images/edit.jsp"/> 
	  </label></td>
	  </tr>

</table>
 <input name="Usrs" type="hidden" id="Usrs" value="">
 <input name="Matter" type="hidden" id="Matter" value="" />
</form>

</div>
</body>
</html>
<script>

new Validation('thisForm');

function getUrsValue() {
		var tree = Ext.getCmp("userTree");
		var selects = tree.getChecked();
		var usrs = "" ;
		
		for(var i=0;i<selects.length;i++) {
			if(selects[i].isLeaf()) {
				usrs += selects[i].id + "|";
			}
		}
		if(usrs != "") {
			usrs = usrs.substr(0,usrs.length-1)
		}
		document.getElementById("Usrs").value = usrs ;
}

function goAdEd()
{
	var IframeID=document.getElementById("oblog_Composition").contentWindow;
	document.getElementById("Matter").value = IframeID.document.body.innerHTML;
	document.thisForm.action="AddPlacard.jsp?next=true&opt=<%=opt%>";
	getUrsValue();
	if(document.thisForm.Caption.value==""){
		alert("标题不能为空！");
		document.thisForm.Caption.focus();
		return false;
	}
	if(document.thisForm.Matter.value==""){
		alert("正文不能为空！");
		return false;
	}
	if(document.thisForm.Usrs.value==""){
		alert("收信人不能为空！");
		return false;
	}
	var f1=thisForm.image.value;
	if("<%=opt%>" !=""){
		if(confirm("您是否确定要发送短信吗？")==false) return false;
	}else{
		if(confirm("您是否确定要发送公告！")==false) return false;
	}
	document.thisForm.submit();
	return true;
}

 function getChange(){
 	var objCheckBox = document.getElementsByTagName("INPUT");
 	var objTbody = document.getElementsByTagName("TBODY");
 	var temp = document.all.IsReversion.checked;
 	for(var i=0; i < objTbody.length; i++) {
 		objTbody[i].style.display = "block";
 	}
 	
 	for(var i=0;i<objCheckBox.length;i++){
 		if(objCheckBox[i].type=='checkbox') {
 			objCheckBox[i].checked = true;
 		}
 		
 	}
 	
 	document.all.IsReversion.checked = temp;
 }
 function getNoChange(){
 	var objCheckBox = document.getElementsByTagName("INPUT");
 	var temp = document.all.IsReversion.checked;
 	
 	for(var i=0;i<objCheckBox.length;i++){
 		if(objCheckBox[i].type=='checkbox') {
 			objCheckBox[i].checked = false;
 		}
 		
 	}
 	
 	document.all.IsReversion.checked = temp;
 }
 function preview2(){
	var n=window.open('AddPlacard.jsp','newwindow','height=400,width=600,top=0,left=0, toolbar=no, menubar=no, scrollbars=yes, resizable=no,location=no, status=no');
	n.document.write("<head><title>预览</title>");
	n.document.write("<table><tr><td>");
	//n.document.write(document.getElementById('Caption').value);
	//n.document.write("</td></tr>");
	//n.document.write("<tr><td>");
	n.document.write(document.getElementById("oblog_Composition").contentWindow.document.body.innerHTML);
	n.document.write("</td></tr></table>");
	n.document.write("<table align='center'><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr><td><input type='button'  value='返  回' class='flyBT'  onClick='window.close();'></td></tr></table>");
}
</script>
<%

	}catch(Exception e){
		e.printStackTrace();
	}finally{
		if(rs!=null)rs.close();
		if(ps!=null)ps.close();
		if(conn!=null)conn.close();
	}

%>
