<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="java.io.File"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.matech.framework.pub.autocode.DELUnid"%>
<%@ page import="com.matech.audit.pub.db.DBConnect"%>
<%@ page import="com.matech.audit.service.fileupload.MyFileUpload"%>
<%@ page import="com.matech.framework.pub.db.DbUtil" %>
<%@ page import="com.matech.audit.service.salary.SalaryService"%>
<%
	String pch = request.getParameter("pch");
	

	if (pch!=null && pch.length()>0 ){
		//处理装载
		MyFileUpload myfileUpload = new MyFileUpload(request);
		
		String userid = request.getParameter("userid");
		String areaid = request.getParameter("areaid");
		System.out.println("当前人与所在分所:userid="+userid+"|areaid="+areaid);
		
		String strTempFile =  DELUnid.getNumUnid() + ".txt";
		
		//正式上传文件
		myfileUpload.UploadFile(strTempFile,"c:\\temp\\");		
		//org.util.Debug.prtOut("文件上传成功dddddddddddddd：strTempFile111="+strTempFile);
		
		//把文件导入到数据库
		Connection conn = null;
		PreparedStatement ps = null;
		String strSql="";
		try {
			DBConnect db= new DBConnect();
			conn = db.getConnect("");
			
			
			//创建或清除临时表
			SalaryService salaryService = new SalaryService(conn);
			
			strSql="delete from tt_k_salary where pch=?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1,pch);
			ps.execute();
			ps.close();
			
			//快速装载文档
			//项目`项目`部门`部门`姓名`姓名`年份`年份`月份`月份`备注`备注`
			try{
				strSql="LOAD DATA INFILE \"c:/temp/" + strTempFile +"\" \n"
						+" INTO TABLE tt_k_salary FIELDS TERMINATED BY '`' \n"
						+" LINES TERMINATED BY '\\r\\n' \n"
						+"( pchname,pchname,departmentname,departmentname,username,username,nowYear, nowYear,nowMonth,nowMonth,memo,memo, \n"
								+"v1,c1,v2,c2,v3,c3,v4,c4,v5,c5,v6,c6,v7,c7,v8,c8,v9,c9,v10,c10, \n"
								+"v11,c11,v12,c12,v13,c13,v14,c14,v15,c15,v16,c16,v17,c17,v18,c18,v19,c19,v20,c20, \n"
								+"v21,c21,v22,c22,v23,c23,v24,c24,v25,c25,v26,c26,v27,c27,v28,c28,v29,c29,v30,c30, \n"
								+"v31,c31,v32,c32,v33,c33,v34,c34,v35,c35,v36,c36,v37,c37,v38,c38,v39,c39,v40,c40, \n"
								+"v41,c41,v42,c42,v43,c43,v44,c44,v45,c45,v46,c46,v47,c47,v48,c48,v49,c49,v50,c50 \n"
						+") \n"
						+"set pch=? ";
					ps = conn.prepareStatement(strSql);
					System.out.println("qwh:saveexelsql="+strSql);
					ps.setString(1, pch);
					ps.execute();
					ps.close();
			}catch (Exception e) {
				System.out.println("以上SQL出错！！用旧的SQL试一次！");
				strSql="LOAD DATA INFILE \"c:/temp/" + strTempFile +"\" \n"
						+" INTO TABLE tt_k_salary FIELDS TERMINATED BY '`' \n"
						+" LINES TERMINATED BY '\\r\\n' \n"
						+"( pchname,pchname,departmentname,departmentname,username,username,nowYear, nowYear,nowMonth,nowMonth,memo,memo, \n"
								+"v1,c1,v2,c2,v3,c3,v4,c4,v5,c5,v6,c6,v7,c7,v8,c8,v9,c9,v10,c10, \n"
								+"v11,c11,v12,c12,v13,c13,v14,c14,v15,c15,v16,c16,v17,c17,v18,c18,v19,c19,v20,c20, \n"
								+"v21,c21,v22,c22,v23,c23,v24,c24,v25,c25,v26,c26,v27,c27,v28,c28,v29,c29,v30,c30) \n"
						+"set pch=? ";
					ps = conn.prepareStatement(strSql);
					System.out.println("qwh:saveexelsql="+strSql);
					ps.setString(1, pch);
					ps.execute();
					ps.close();
			}
			
			
			/**
			 * 设置字段名称
			 * 对EXCEL操作时，有可能会删除一列
			 * 所以对于n1....等字段名称只能通过第一列来还原
			 */
			//如果前台是全表保存，就不删除，否则就删除多装载的空行
			//strSql="delete from tt_k_salary where pch=? and ( trim(username)='' or username='\r' or username='姓名' or username is null or username='\n')";
			strSql="delete from tt_k_salary where pch=? and ( trim(username)='' or username='\r' or username is null or username='\n')";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, pch);
			ps.execute();
			ps.close();
			
			//更新工资
			salaryService.updateDataTxt(pch,areaid);
		
			
			strSql="delete from tt_k_salary where pch=?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1,pch);
			ps.execute();
			ps.close();
			
			
			out.println("保存成功");
			
		}catch (Exception e) {
			out.println("执行导入失败！");
			System.out.println("执行EXCEL保存后台导入失败,sql="+strSql);
			e.printStackTrace();
		} finally {
			
			//关闭
			DbUtil.close(ps);
			DbUtil.close(conn);
			
			try{
				//删除文档
				//new File("c:\\temp\\"+strTempFile).delete();
			}catch(Exception e){}
		}
		
	}else{
		//显示上传界面
%>
<form name="thisForm" method="post" action="saveExcelFromUrl.jsp?curProjectid=2007632&taskid=333" id="thisForm" enctype="multipart/form-data">
	<input type="file" name="myfile" id="myfile" value=""  size="90" title="请输入，不得为空">
	<input type="submit" name="next" value="确  定" class="flyBT" onclick="return checkit();" >
</form>
<%
	}

%>