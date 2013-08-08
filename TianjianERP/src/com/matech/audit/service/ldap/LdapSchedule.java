/*     */ package com.matech.audit.service.ldap;
/*     */ 
/*     */ import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.user.DesUtils;
import com.matech.audit.service.user.EnCodeUtils;
/*     */ import com.matech.framework.pub.db.DbUtil;
/*     */ import com.matech.framework.pub.util.ASFuntion;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
import java.util.Scanner;

/*     */ import org.quartz.Job;
/*     */ import org.quartz.JobExecutionContext;
/*     */ import org.quartz.JobExecutionException;
/*     */ 
/*     */ public class LdapSchedule
/*     */   implements Job
/*     */ {
/*     */   public void execute(JobExecutionContext arg0)
/*     */     throws JobExecutionException
/*     */   {
	          
/*  20 */     ASFuntion asf = new ASFuntion();
/*     */ 
/*  22 */     Connection conn = null;
/*  23 */     String givenname = ""; String cn = ""; String x = ""; String m = "";
/*  24 */     PreparedStatement ps = null;
/*  25 */     ResultSet rs = null;
/*  26 */     String result = "";
/*  27 */     FileWriter resultFile = null;
              
/*     */     try {
/*  29 */       System.out.println("准备连接数据库<br>");
/*     */ 
/*  31 */       Runtime exe = Runtime.getRuntime();
/*  32 */       String cmd = "";
/*     */ 
/*  34 */       Class.forName("com.mysql.jdbc.Driver");
/*  35 */       conn = DriverManager.getConnection("jdbc:mysql://172.19.7.87:5188/asdb?characterEncoding=GBK", "xoops_root", "654321");
/*     */       //conn=new DBConnect().getConnect();
/*  37 */       System.out.println("连接数据库成功<br>");
/*     */ 
/*  39 */       String filePath = "c:/1.bat";
/*  40 */       filePath = filePath.toString();
/*  41 */       File myFilePath = new File(filePath);
/*  42 */       if (!myFilePath.exists()) {
/*  43 */         myFilePath.createNewFile();
/*     */       }
/*  45 */       resultFile = new FileWriter(myFilePath);
/*  46 */       PrintWriter myFile = new PrintWriter(resultFile);
/*     */ 
/*  48 */       System.out.println("正在准备同步脚本" + asf.getCurrentDate() + " " + asf.getCurrentTime() + "<br>");
/*     */ 
/*  51 */       String sql = "SELECT NAME,loginid,clientDogSysUi,identitycard FROM k_user where state=0 and identitycard>'' and loginid>''  and emtype='001' ";
/*  52 */       ps = conn.prepareStatement(sql);
/*  53 */       rs = ps.executeQuery();
/*     */ 
/*  61 */       String passwd = "";
/*  62 */       EnCodeUtils des = new EnCodeUtils();
/*  63 */       String strhint = "";
/*  64 */       int i = 0;
/*  65 */       while (rs.next())
/*     */       {
/*  67 */         i++;
/*     */ 
/*  69 */         givenname = rs.getString("NAME");
/*  70 */         cn = rs.getString("loginid");
/*  71 */         if ((!"".equals(givenname)) && (givenname != null)) {
/*  72 */           x = givenname.substring(0, 1);
/*  73 */           m = givenname.substring(1);
/*     */         }
/*     */ 
/*  77 */         passwd = rs.getString("clientDogSysUi");
/*  78 */         if ((passwd == null) || ("".equals(passwd))) {
/*  79 */           passwd = rs.getString("identitycard").toUpperCase();
/*  80 */           if (passwd.length() > 6) {
/*  81 */             passwd = passwd.substring(passwd.length() - 6);
/*  82 */             strhint = "身份证后6位";
/*     */           } else {
/*  84 */             strhint = "没修改密码或无法解密，且身份证位数小于6，用缺省1234567";
/*  85 */             passwd = "1234567";
/*     */           }
/*     */         } else {
/*  88 */           passwd = des.decodePWS(passwd);
/*  89 */           if ((passwd == null) || ("".equals(passwd)) || ("-1".equals(passwd))) {
/*  90 */             passwd = rs.getString("identitycard").toUpperCase();
/*  91 */             if (passwd.length() > 6) {
/*  92 */               passwd = passwd.substring(passwd.length() - 6);
/*  93 */               strhint = "无法解密，身份证后6位";
/*     */             } else {
/*  95 */               passwd = "1234567";
/*  96 */               strhint = "无法解密，没修改密码或无法解密，且身份证位数小于6，用缺省1234567";
/*     */             }
/*     */           } else {
/*  99 */             strhint = "用设置的新密码";
/*     */           }
/*     */         }
/* 102 */         System.out.println("第" + i + "人：" + givenname + "|" + cn + "|" + rs.getString("identitycard") + "|" + strhint + "<br>");
/*     */ 
/* 104 */         cmd = "dsadd user \"cn=" + givenname + cn + ",ou=oa,dc=oa,dc=pccpa,dc=cn\" -samid " + cn + " -upn " + cn + "@pccpa.cn -fn  " + m + " -ln " + x + " -pwd " + passwd + " -disabled no";
/* 105 */         myFile.println(cmd);
/* 106 */         myExe(exe, cmd);
/*     */ 
/* 109 */         cmd = "dsmod user \"cn=" + givenname + cn + ",ou=oa,dc=oa,dc=pccpa,dc=cn\" -pwd " + passwd;
/* 110 */         myFile.println(cmd);
/* 111 */         myExe(exe, cmd);
/*     */       }
/*     */ 
/* 115 */       if (resultFile != null) {
/* 116 */         resultFile.close();
/*     */       }
/*     */ 
/* 120 */       System.out.println("同步结束" + asf.getCurrentDate() + " " + asf.getCurrentTime() + ",一共同步" + i + "人");
/*     */     }
/*     */     catch (Exception e) {
/* 123 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/* 126 */         if (resultFile != null)
/* 127 */           resultFile.close();
/*     */       }
/*     */       catch (Exception localException2) {
/*     */       }
/* 131 */       DbUtil.close(rs);
/* 132 */       DbUtil.close(ps);
/*     */     }

/*     */   }
/*     */ 
/*     */   public String myExe(Runtime exe, String strCmd)
/*     */     throws Exception
/*     */   {
/* 142 */     Process p = null;
/* 143 */     InputStream is = null;
/* 144 */     InputStreamReader isr = null;
/* 145 */     BufferedReader br = null;
/* 146 */     String line = null;
/*     */     try
/*     */     {
/* 149 */       p = exe.exec(strCmd);
/* 150 */       is = p.getInputStream();
/*     */ 
/* 152 */       isr = new InputStreamReader(is);
/* 153 */       br = new BufferedReader(isr);
/* 154 */       while ((line = br.readLine()) != null)
/* 155 */         System.out.println(line);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 159 */       e.printStackTrace();
/*     */     } finally {
/* 161 */       p.destroy();
/* 162 */       br.close();
/* 163 */       isr.close();
/* 164 */       is.close();
/*     */     }
/*     */ 
/* 167 */     return line;
/*     */   }

              public static void main(String[] args){
            	  System.out.println("请输入加密字段");
            	  Scanner scanner=new Scanner(System.in);
            	  EnCodeUtils des = new EnCodeUtils();
            	  String pass = des.decodePWS(scanner.next());
            	  System.out.println(pass);
              }

/*     */ }

/* Location:           C:\Users\hhlin\Desktop\LDAP同步2012-12-31-2000\复件 LDAP同步\webRoot\AuditSystem\WEB-INF\classes\
 * Qualified Name:     com.matech.audit.service.ldap.LdapSchedule
 * JD-Core Version:    0.6.0
 */