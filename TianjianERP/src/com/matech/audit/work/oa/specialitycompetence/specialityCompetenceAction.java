package com.matech.audit.work.oa.specialitycompetence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.specialitycompetence.specialityCompetenceService;
import com.matech.audit.service.oa.specialitycompetence.model.SpecialityCompetenceTable;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class specialityCompetenceAction extends MultiActionController {
	private final String _strList = "oa/specialitycompetence/List.jsp";

	private final String _strListDo = "/AuditSystem/specialitycompetence.do";

	private final String _AddandEdit = "oa/specialitycompetence/AddandEdit.jsp";

	/**
	 * 跳转到专业资格登记列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		ASFuntion asf = new ASFuntion();
		
		DataGridProperty pp = new DataGridProperty() {
		};

		ModelAndView modelAndView = new ModelAndView(_strList);

		String specialPerson = asf.showNull(req.getParameter("specialPerson"));
		String specialCode = asf.showNull(req.getParameter("specialCode"));
		String specialDepartment = asf.showNull(req.getParameter("specialDepartment"));
		String sqlWhere = "";
		
		if(!"".equals(specialPerson)) {
			sqlWhere += " and userid like '%"+specialPerson+"%' ";
		}
		
		if(!"".equals(specialCode)) {
			sqlWhere += " and certificateid like '%"+specialCode+"%' ";
		}
		
		if(!"".equals(specialDepartment)) {
			sqlWhere += " and certificatedepartment like '%"+specialDepartment+"%' ";
		}
		
		// 必要设置
		pp.setTableID("specialitycompetence");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置

		String all = req.getParameter("all");
		String sql = "";

		String myUserid = (String) req.getSession().getAttribute("myUserid");
		if ("all".equals(all)) {// 显示所有人时的信息

			sql = " select 	autoid,b.name username, certificate, certificateid, certificatedepartment, \n"
					+ " certificatetime,  \n"
					+ " availabilitytime,  \n"
					+ " ifera,  \n"
					+ " remark,  \n"
					+ " userid,  \n"
					+ " property \n"
					+ " from  \n"
					+ " oa_specialitycompetence a left join k_user b on a.userid=b.id where 1=1 "+sqlWhere;

		} else {// 显示具体某个人的信息
			sql = " select 	autoid, certificate, certificateid, certificatedepartment, \n"
					+ " certificatetime,  \n"
					+ " availabilitytime,  \n"
					+ " ifera,  \n"
					+ " remark,  \n"
					+ " userid,  \n"
					+ " property \n"
					+ " from  \n"
					+ " oa_specialitycompetence where userid=" + myUserid;

		}

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("专业资格登记");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		if ("all".equals(all)) {// 显示所有人时，需显示：持证人姓名
			pp.addColumn("持证人", "username");
		}

		pp.addColumn("证书名称", "certificate");
		pp.addColumn("证书编号", "certificateid");
		pp.addColumn("发证机关", "certificatedepartment");
		pp.addColumn("取得证书时间", "certificatetime");
		pp.addColumn("有效期", "availabilitytime");
		pp.addColumn("是否代管", "ifera");
		pp.addColumn("备注", "remark");

		modelAndView.addObject("all", all);
		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return modelAndView;
	}

	/**
	 * 添加记录
	 * 
	 * @param req
	 * @param res
	 * @param sct
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		String myUserid = "";
		String all = "";

		
		try {
			
			conn = new DBConnect().getConnect("");
			ASFuntion asf = new ASFuntion() ;
						
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			
			Foder foder  = new Foder("specialityFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			SpecialityCompetenceTable sct = new SpecialityCompetenceTable() ;
			
			
			 sct.setUserid((String)parameters.get("userid")) ;
			 sct.setCertificate((String)parameters.get("certificate")) ;
			 sct.setCertificateid((String)parameters.get("certificateid")) ;
			 sct.setCertificatedepartment((String)parameters.get("certificatedepartment")) ;
			 sct.setIfera((String)parameters.get("ifera")) ;
			 sct.setCertificatetime((String)parameters.get("certificatetime")) ;
			 sct.setAvailabilitytime((String)parameters.get("availabilitytime")) ;
			 sct.setRemark((String)parameters.get("remark")) ;
			 sct.setFileNames(myfileUpload.getFileNames()) ;
			 sct.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			specialityCompetenceService scs = new specialityCompetenceService(conn);
			all = req.getParameter("all");
			if ("all".equals(all)) {// 显示所有人时，userid从页面上取
				myUserid = req.getParameter("userid");
			} else {// 显示具体某一个人时，userid从Session取
				myUserid = (String) req.getSession().getAttribute("myUserid");
			}

			if (myUserid != null) {
				sct.setUserid(myUserid);
			}

			scs.add(sct);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}

	/**
	 * 删除信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView del(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strList);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			specialityCompetenceService scs = new specialityCompetenceService(conn);
			SpecialityCompetenceTable sct = scs.getSpeciality(autoid);
			AttachService attachService = new AttachService(conn);
			attachService.remove("oa/specialityFoder", sct.getFileRondomNames());
			
			scs.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 修改信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ModelAndView modelAndView = new ModelAndView(_AddandEdit);
		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String autoid = "";
		String all = "";
		SpecialityCompetenceTable sct = new SpecialityCompetenceTable();
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			if(!"".equals(autoid)){
				specialityCompetenceService scs = new specialityCompetenceService(
						conn);
				sct = scs.getSpeciality(autoid);
				
				String fileNames = sct.getFileNames() ;
				String fileTempNames = sct.getFileRondomNames() ;

				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				modelAndView.addObject("fileArr",fileArr) ;
				modelAndView.addObject("fileTempArr",fileTempArr) ;
			}else{
				sct.setFileRondomNames(UUID.randomUUID().toString());
			}
			
			modelAndView.addObject("sct", sct);
			modelAndView.addObject("autoid", autoid);
			modelAndView.addObject("all", all);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		
		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param req
	 * @param res
	 * @param sct
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			
			ASFuntion asf = new ASFuntion() ;
			
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			
			Foder foder  = new Foder("specialityFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			SpecialityCompetenceTable sct = new SpecialityCompetenceTable() ;
			specialityCompetenceService scs = new specialityCompetenceService(
					conn);
			
			String autoId = asf.showNull(req.getParameter("autoid")) ;
			SpecialityCompetenceTable sctTemp = scs.getSpeciality(autoId) ;

			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(sctTemp.getFileNames()) && !"".equals(sctTemp.getFileRondomNames())) {
				 fileNames = sctTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = sctTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			
			
			 sct.setAutoid(Integer.parseInt(autoId)) ;
			 sct.setUserid((String)parameters.get("userid")) ;
			 sct.setCertificate((String)parameters.get("certificate")) ;
			 sct.setCertificateid((String)parameters.get("certificateid")) ;
			 sct.setCertificatedepartment((String)parameters.get("certificatedepartment")) ;
			 sct.setIfera((String)parameters.get("ifera")) ;
			 sct.setCertificatetime((String)parameters.get("certificatetime")) ;
			 sct.setAvailabilitytime((String)parameters.get("availabilitytime")) ;
			 sct.setRemark((String)parameters.get("remark")) ;
			 sct.setFileNames(fileNames) ;
			 sct.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			all = req.getParameter("all");

			if ("all".equals(all)) {// 显示所有人时，Userid从页面上取
				sct.setUserid(req.getParameter("userid"));
			} else {// 显示具体某个人时，Userid从Session取
				sct.setUserid((String) req.getSession()
						.getAttribute("myUserid"));
			}
			
			scs.update(sct);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}
	
	
	/**
	 * 下载附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView download(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		try {
			response.setContentType("text/html;charset=utf-8");
			conn = new DBConnect().getConnect("");
			ASFuntion CHF = new ASFuntion();
			
			String fileName =CHF.showNull(request.getParameter("fileName_CH"));
			String autoId = CHF.showNull(request.getParameter("autoid")) ;
	
			specialityCompetenceService scs = new specialityCompetenceService(
					conn);
			SpecialityCompetenceTable sct = scs.getSpeciality(autoId) ;
			
			String fileNames = sct.getFileNames() ;
			String fileTempNames = sct.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("specialityFoder",request);
			
			String filePath = foder.getDirPath()+fileTempName;
			

			if (!new File(filePath).exists()) {
				PrintWriter out = response.getWriter();
				out.println("找不到文件，请联系管理员。<input type=\"button\" value=\"返回\" onclick=\"history.back();\" >");
				return null;
			} else {
				response.setContentType("application/x-msdownload");
				response.setHeader("Content-disposition",
						"attachment; filename=" + fileName);
				//更新下载次数
				OutputStream os = response.getOutputStream();
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(new File(filePath)));

				byte b[] = new byte[512];
				int len;

				while ((len = bis.read(b)) != -1) {
					os.write(b, 0, len);
				}

				os.flush();
				bis.close();
				os.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	
	public void deleteUpload(HttpServletRequest request,
			HttpServletResponse response) {
		
		Connection conn = null ;
		PreparedStatement ps = null ;
		PreparedStatement ps2 = null ;
		ResultSet rs = null ;
		PrintWriter out = null ;
		ASFuntion asf = new ASFuntion();
		String fileNames = "" ;
		String fileTempNames = "" ;
		
		try {
			 out = response.getWriter() ;
			 
			 String fileName = request.getParameter("fileName") ;
			 String autoId = asf.showNull(request.getParameter("autoId")) ;
			 
			 
			conn = new DBConnect().getConnect("") ;
			String sql = "select uploadFileName,uploadTempName \n" 
						+"from \n"
						+"oa_specialitycompetence  where autoid = '" + autoId + "' ";
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			if(rs.next()) {
				
				 fileNames = rs.getString(1) ;
				 fileTempNames = rs.getString(2) ;
				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				
				
				String fileTempName = "" ;
				 fileNames = "" ;
				 fileTempNames = "" ;
				for(int i=0;i<fileArr.length;i++) {
					
					if(fileName.equals(fileArr[i])) {
						fileTempName = fileTempArr[i] ;
					}else {
						fileNames += fileArr[i] +",";
						fileTempNames += fileTempArr[i] +",";
					}
				}
				
				
				if(!"".equals(fileNames) && !"".equals(fileTempNames)) {
					fileNames = fileNames.substring(0,fileNames.length()-1) ;
					fileTempNames = fileTempNames.substring(0,fileTempNames.length()-1) ;
				}
				sql = "update oa_specialitycompetence set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("specialityFoder",request);
				
				String filePath = foder.getDirPath()+fileTempName;
				

				if (!new File(filePath).exists()) {
						out.write("notFound") ;
				} else {
					if(new File(filePath).delete()) {
						out.write("suc") ;
					}else {
						out.write("fail") ;
					}
				}
				
				
			}
		
			
		}catch (Exception e) {
			out.write("fail") ;
			Debug.print(Debug.iError, "删除附件失败！", e);
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
			out.close();
		}
		
	}
	

}
