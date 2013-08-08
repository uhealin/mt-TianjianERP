package com.matech.audit.work.oa.employeecertificate;

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
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.oa.employeecertificate.employeecertificateService;
import com.matech.audit.service.oa.employeecertificate.model.employeecertificateTable;
import com.matech.audit.service.oa.family.FamilyService;
import com.matech.audit.service.oa.family.model.Family;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.worknote.worknoteService;
import com.matech.audit.service.oa.worknote.model.worknoteTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class employeecertificateAction extends MultiActionController {
	private final String _strList = "oa/employeecertificate/List.jsp";

	private final String _strListDo = "/AuditSystem/employeecertificate.do";

	private final String _AddandEdit = "oa/employeecertificate/AddandEdit.jsp";

	/**
	 * 跳转到人事档案缴费管理列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		DataGridProperty pp = new DataGridProperty() {
		};

		// 必要设置
		pp.setTableID("employeecertificate");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置
		// 从Session中读人员ID
		String myUserid = (String) req.getSession().getAttribute("myUserid");
		String sql = " select autoid, certificatetype, certificateid, \n"
				+ " hairdepartment, hairtime,  \n"
				+ " availabilitytime,remark,userid,  \n"
				+ " property  from  \n"
				+ " oa_employeecertificate where userid=" + myUserid;

		// 查询设置


		pp.setPrintEnable(true);
		pp.setPrintTitle("员工证件登记列表");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		pp.addColumn("执业资质", "certificatetype");
		pp.addColumn("执业证书编号", "certificateid");
		pp.addColumn("发证机关", "hairdepartment");
		pp.addColumn("发证时间", "hairtime");
		pp.addColumn("有效期", "availabilitytime");
		pp.addColumn("备注", "remark");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return new ModelAndView(_strList);
	}

	/**
	 * 增加模块
	 * 
	 * @param req
	 * @param res
	 * @param et
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		String myUserid = "";
		try {
			conn = new DBConnect().getConnect("");
			
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("employeeFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			
			employeecertificateTable ect = new employeecertificateTable() ;
			
			ect.setCertificatetype((String)parameters.get("certificatetype")) ;
			ect.setCertificateid((String)parameters.get("certificateid")) ;
			ect.setHairdepartment((String)parameters.get("hairdepartment")) ;
			ect.setHairtime((String)parameters.get("hairtime")) ;
			ect.setAvailabilitytime((String)parameters.get("availabilitytime")) ;
			ect.setRemark((String)parameters.get("remark")) ;
			ect.setFileNames(myfileUpload.getFileNames()) ;
			ect.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			employeecertificateService es = new employeecertificateService(conn);
			myUserid = (String) req.getSession().getAttribute("myUserid");
			if (myUserid != null) {
				ect.setUserid(myUserid);
			}

			es.add(ect);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		res.sendRedirect(_strListDo);
		return new ModelAndView(_strList);
	}

	/**
	 * 删除模块
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView del(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			employeecertificateService es = new employeecertificateService(conn);
			employeecertificateTable et = es.getEmployeeCertificate(autoid);
			//删除附件
			AttachService attachService = new AttachService(conn);
			attachService.remove("oa/employeeFoder", et.getFileRondomNames());	
			
			es.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}

		return new ModelAndView(_strList);
	}

	/**
	 * 显示修改信息
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
		employeecertificateTable et = new employeecertificateTable();
		try {
			autoid = CHF.showNull(req.getParameter("autoid"));
			if(!"".equals(autoid)){
				conn = new DBConnect().getConnect("");
				employeecertificateService es = new employeecertificateService(conn);
				et = es.getEmployeeCertificate(autoid);
				
				String fileNames = et.getFileNames() ;
				String fileTempNames = et.getFileRondomNames() ;
	
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				modelAndView.addObject("fileArr",fileArr) ;
				modelAndView.addObject("fileTempArr",fileTempArr) ;
			}else{
				et.setFileRondomNames(UUID.randomUUID().toString());
			}
			
			modelAndView.addObject("et", et);
			modelAndView.addObject("autoid", autoid);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}

	/**
	 * 修改数据
	 * 
	 * @param req
	 * @param res
	 * @param et
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			employeecertificateService es = new employeecertificateService(conn);
			
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("employeeFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			employeecertificateTable ect = new employeecertificateTable() ;

			String autoId = asf.showNull(req.getParameter("autoid")) ;

			employeecertificateTable ectTemp = es.getEmployeeCertificate(autoId) ;

			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(ectTemp.getFileNames()) && !"".equals(ectTemp.getFileRondomNames())) {
				 fileNames = ectTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = ectTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			
			ect.setAutoid(Integer.parseInt(autoId)) ;
			ect.setCertificatetype((String)parameters.get("certificatetype")) ;
			ect.setCertificateid((String)parameters.get("certificateid")) ;
			ect.setHairdepartment((String)parameters.get("hairdepartment")) ;
			ect.setHairtime((String)parameters.get("hairtime")) ;
			ect.setAvailabilitytime((String)parameters.get("availabilitytime")) ;
			ect.setRemark((String)parameters.get("remark")) ;
			ect.setFileNames(fileNames) ;
			ect.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			es.update(ect);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo);
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
	
			employeecertificateService ecs = new employeecertificateService(conn);
			employeecertificateTable ect = ecs.getEmployeeCertificate(autoId) ;
		
			String fileNames = ect.getFileNames() ;
			String fileTempNames = ect.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("employeeFoder",request);
			
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
						+"oa_employeecertificate  where autoid = '" + autoId + "' ";
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
	
				sql = "update oa_employeecertificate set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("employeeFoder",request);
				
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
