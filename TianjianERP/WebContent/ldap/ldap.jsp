<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.matech.framework.pub.sys.UTILSysProperty"%>
<%@page import="com.matech.framework.listener.UserSession" %>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="java.sql.*"%>
<%@page import="java.io.*"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="javax.naming.NamingEnumeration"%>
<%@page import="javax.naming.directory.*"%>
<%@page import="javax.naming.ldap.InitialLdapContext"%>
<%@page import="java.util.Properties"%>
<%@page import="javax.naming.Context"%>
<%@page import="com.matech.audit.service.user.DesUtils"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
</head>

<body bgcolor="#DEEFF6";>
<%

	/*
	Properties p = new Properties();
	p.setProperty("java.naming.factory.initial","com.sun.jndi.ldap.LdapCtxFactory");
	p.setProperty("java.naming.provider.url","ldap://172.19.7.85:389");
	p.setProperty("java.naming.security.authentication","simple");
	//p.setProperty(Context.SECURITY_PRINCIPAL, "CN=administrator,cn=users,dc=lisa,dc=beijing,dc=indigo");
	p.setProperty(Context.SECURITY_PRINCIPAL, "qwh");
	p.put(Context.SECURITY_CREDENTIALS, "1234567");
	InitialLdapContext ictx = new InitialLdapContext(p,null);
	*/


	Connection conn = null;
	String givenname="",cn="",x="",m=""; 
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	FileWriter resultFile=null;
	try {
		conn = new DBConnect().getDirectConnect("");
		
		
		String filePath = "c:/1.bat";
        filePath = filePath.toString();
        File myFilePath = new File(filePath);
        if (!myFilePath.exists()) {
           	myFilePath.createNewFile();
        }
         resultFile = new FileWriter(myFilePath);
        PrintWriter myFile = new PrintWriter(resultFile);
		
        out.println("正在准备同步脚本<br>");
        out.flush();
        
		//String sql="select content from k_moralityEdit where type='职业道德说明' ";
		String sql="SELECT NAME,loginid,clientDogSysUi,identitycard FROM k_user where state=0 and identitycard>'' and loginid>'' ";
		ps=conn.prepareStatement(sql);
		rs=ps.executeQuery();
		
		myFile.println("dsrm  -subtree -exclude -noprompt -c \"ou=oa,dc=oa,dc=pccpa,dc=cn\"");
		String passwd="";
		DesUtils des=new DesUtils();
		while(rs.next() ){
			
			givenname = rs.getString("NAME"); 
			cn= rs.getString("loginid");
			if (!"".equals(givenname) && givenname!=null){
				x=givenname.substring(0,1);
				m=givenname.substring(1);
			}
			
			//还原密码
			passwd=rs.getString("clientDogSysUi");
			if (passwd==null || "".equals(passwd)){
				passwd=rs.getString("identitycard").toUpperCase();
				System.out.println(passwd+"|"+passwd.substring(passwd.length()-6));
				passwd=passwd.substring(passwd.length()-6);
			}else{
				passwd=des.decrypt(passwd);
			}
	        
			//dsadd user "cn=屈文浩,ou=oa,dc=oa,dc=pccpa,dc=cn" -samid qwh -upn qwh@pccpa.cn -fn  文号 -ln 屈 -pwd 1234567 -disabled no
	        myFile.println("dsadd user \"cn="+givenname+",ou=oa,dc=oa,dc=pccpa,dc=cn\" -samid "+cn+" -upn "+cn+"@pccpa.cn -fn  "+m+" -ln "+x+" -pwd "+passwd+" -disabled no");
	              

			/*
			String uid = rs.getString("id");
			String cn= rs.getString("loginid");
			Attributes attrs = new BasicAttributes("cn",cn,true); 
			Attribute objclass = new BasicAttribute("objectclass"); 
			objclass.add("top"); 
			objclass.add("person"); 
			objclass.add("organizationalPerson"); 
			objclass.add("inetorgperson"); 
			attrs.put(objclass); 
			
			givenname = rs.getString("NAME");
			String email=rs.getString("email");
			String phone=rs.getString("mobilephone");
			
			
			attrs.put("cn",cn);
			attrs.put("givenname", givenname); 
			attrs.put("sn", givenname); 
			attrs.put("uid", uid); 
			attrs.put("userpassword", "123456789^a"); 
			attrs.put("displayName",givenname);
			if (email!=null && !"".equals(email)){
				attrs.put("mail", email); 
			}
			if (phone!=null && !"".equals(phone)){
				attrs.put("telephonenumber",phone  ); 
			}
			
			
			int UF_ACCOUNTDISABLE = 0x0002;
            int UF_PASSWD_NOTREQD = 0x0020;
            int UF_PASSWD_CANT_CHANGE = 0x0040;
            int UF_NORMAL_ACCOUNT = 0x0200;
            int UF_DONT_EXPIRE_PASSWD = 0x10000;
            int UF_PASSWORD_EXPIRED = 0x800000;
	        
	            //Note that you need to create the user object before you can
	            //set the password. Therefore as the user is created with no 
	            //password, user AccountControl must be set to the following
	            //otherwise the Win2K3 password filter will return error 53
	            //unwilling to perform.
	 
	        attrs.put("userAccountControl",Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_CANT_CHANGE + UF_DONT_EXPIRE_PASSWD +UF_ACCOUNTDISABLE ));    
			
			//putAttribute(attrs, "inetuserstatus", "Active");
	
			//添加用户节点 
			try{
				ictx.createSubcontext("CN="+cn+",ou=OA,dc=oa,dc=pccpa,dc=cn" , attrs); 
				out.println("添加:"+givenname+"<br>");
			}catch(Exception e){out.println("添加"+givenname+"失败:"+e.getMessage()+"<br>");}
			
			*/
		}
		
		myFile.println("dir c:");
		
		if(resultFile!=null)
			resultFile.close();
		
		 out.println("正在执行同步脚本<br>");
	     out.flush();
		
		//执行导入
		try{
			 Runtime exe = Runtime.getRuntime();  
			 Process p=exe.exec("c:/1.bat");  
			 InputStream is = p.getInputStream();  
			 int data; 
			 while((data=is.read())!=-1){   
				 out.print((char)data);  
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 out.println("<script>alert('同步结束')</script>");
	     out.flush();
		 
	} catch (Exception e) {
		e.printStackTrace();
	} finally{
		if(resultFile!=null)
			resultFile.close();
		 
		DbUtil.close(rs);
		DbUtil.close(ps);
	}
%>

</body>


</html>