package com.matech.audit.work.oa.labor;

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
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.oa.employeecertificate.employeecertificateService;
import com.matech.audit.service.oa.employeecertificate.model.employeecertificateTable;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.labor.LaborBargainService;
import com.matech.audit.service.oa.labor.model.LaborBargain;
import com.matech.audit.service.oa.specialitycompetence.specialityCompetenceService;
import com.matech.audit.service.oa.specialitycompetence.model.SpecialityCompetenceTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class LaborBargainAction extends MultiActionController {

	private String _laborbargain = "/oa/labor/LaborBargain.jsp";

	private String _laborbargainEdit = "/oa/labor/LaborBargainadd.jsp";

	/**
	 * 显示信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelandview = new ModelAndView(_laborbargain);

		HttpSession session = request.getSession();
		
		ASFuntion asf = new ASFuntion();
		String all = request.getParameter("all");

		DataGridProperty pp = new DataGridProperty();
		
		String sqlWhere = "";
		String laborCode = asf.showNull(request.getParameter("laborCode"));
		String laborPerson = asf.showNull(request.getParameter("laborPerson"));
		
		if(!"".equals(laborCode)) {
			sqlWhere += " and bargainID = '"+laborCode+"' ";
		}
		
		if(!"".equals(laborPerson)) {
			sqlWhere += " and userid like '%"+laborPerson+"%' ";
		}

		pp.setCustomerId("");

		pp.setTableID("LaborBargainList");

		pp.setInputType("radio");

		pp.setWhichFieldIsValue(1);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);

//		pp.setPrintColumnWidth("20,20,20,20,20,20,20,20");
		pp.setPrintTitle("劳动合同列表");

		pp.addColumn("合同编号", "bargainID");
		if ("all".equals(all)) {// 如果显示全部人的信息时，显示合同人姓名
			pp.addColumn("合同人", "username");
		}
		pp.addColumn("合同类型", "bargaintype");
		pp.addColumn("签订日期", "endorsedate");
		pp.addColumn("基本工资", "emolument","showMoney");
		pp.addColumn("试用到期日", "trialtime");
		pp.addColumn("合同到期日", "ineffecttime");
		pp.addColumn("其他", "other");
		pp.addColumn("登记人", "checkinperson");
		pp.addColumn("登记日期", "checkindate");

		String myUserid = (String) request.getSession()
				.getAttribute("myUserid");
		String sql = "";
		if ("all".equals(all)) {// 没有指定某个人时显示全部人的合同信息
			sql = "select autoid,b.name username,bargainID,bargainperson,endorsedate,emolument,ineffecttime,other,checkinperson,checkindate,userid,bargaintype,trialtime "
					+ " from oa_workbargain a left join k_user b on a.userid=b.id where 1=1 "+sqlWhere;
		} else {// 显示具体某个人的合同信息
			sql = "select autoid, bargainID,bargainperson,endorsedate,emolument,ineffecttime,other,checkinperson,checkindate,userid,bargaintype,trialtime from oa_workbargain where userid="
					+ myUserid;
		}

		pp.setSQL(sql);

		pp.setOrderBy_CH("autoid");
		pp.setDirection("asc");

		session.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		modelandview.addObject("all", all);

		return modelandview;
	}

	/**
	 * 增加方法
	 * 
	 * @param request
	 * @param response
	 * @param laborbargain
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addLabor(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession();

		Connection conn = null;

		String all = "";
		String userid = "";
		try {
			UserSession us = (UserSession) session.getAttribute("userSession");
			conn = new DBConnect().getConnect("");
			
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(request);
			
			Foder foder  = new Foder("laoborBargainFoder",request);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			
			LaborBargain laborbargain = new LaborBargain() ;
			
			laborbargain.setBargainID((String)parameters.get("bargainID")) ;
			laborbargain.setEmolument((String)parameters.get("emolument")) ;
			laborbargain.setEndorsedate((String)parameters.get("endorsedate")) ;
			laborbargain.setIneffecttime((String)parameters.get("ineffecttime")) ;
			laborbargain.setOther((String)parameters.get("other")) ;
			laborbargain.setFileNames(myfileUpload.getFileNames()) ;
			laborbargain.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			laborbargain.setBargaintype((String)parameters.get("bargaintype")) ;
			laborbargain.setTrialtime((String)parameters.get("trialtime")) ;
			
			all = request.getParameter("all");
			if ("all".equals(all)) {// 没有指定具体某个人时，userid从页面上取
				userid = request.getParameter("userid");
			} else {// 当指定某个人时，从session中取
				userid = (String) session.getAttribute("myUserid");
			}

			String checkinperson = us.getUserName();
			laborbargain.setUserid(userid);
			laborbargain.setCheckinperson(checkinperson);

			LaborBargainService ls = new LaborBargainService(conn);

			ls.addlabor(laborbargain);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/laborbargain.do?all=" + all);

		return null;

	}

	/**
	 * 修改
	 * 
	 * @param request
	 * @param response
	 * @param laborbargain
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updatelabor(HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession();

		Connection conn = null;
		String all = "";

		try {

			conn = new DBConnect().getConnect("");
			LaborBargain laborbargain = new LaborBargain() ;
			all = request.getParameter("all");
			if ("all".equals(all)) {// 没指定具体某个人时
				laborbargain.setUserid(request.getParameter("userid"));
			} else {// 指定具体某个人时
				laborbargain.setUserid((String) session
						.getAttribute("myUserid"));
			}
			LaborBargainService ls = new LaborBargainService(conn);
			
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(request);
			
			Foder foder  = new Foder("laoborBargainFoder",request);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			
			
			String autoId = asf.showNull(request.getParameter("autoid")) ;
			LaborBargain laborbargainTemp = new LaborBargain() ;

			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(laborbargainTemp.getFileNames()) && !"".equals(laborbargainTemp.getFileRondomNames())) {
				 fileNames = laborbargainTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = laborbargainTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			
			System.out.println(parameters);
			laborbargain.setAutoid(autoId) ;
			laborbargain.setBargainID((String)parameters.get("bargainID")) ;
			laborbargain.setEmolument((String)parameters.get("emolument")) ;
			laborbargain.setEndorsedate((String)parameters.get("endorsedate")) ;
			laborbargain.setIneffecttime((String)parameters.get("ineffecttime")) ;
			laborbargain.setOther((String)parameters.get("other")) ;
			laborbargain.setFileNames(fileNames) ;
			laborbargain.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			laborbargain.setBargaintype((String)parameters.get("bargaintype")) ;
			laborbargain.setTrialtime((String)parameters.get("trialtime")) ;

			ls.updatelabor(laborbargain, autoId);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/laborbargain.do?all=" + all);

		return null;
	}

	/**
	 * 获取劳动合同
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitLabor(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_laborbargainEdit);

		LaborBargain lb = new LaborBargain();
		ASFuntion CHF = new ASFuntion();

		// 要修改的劳动合同的autoid
		String autoid = request.getParameter("autoid");// 获取前台传过来的值 autoid
		String all = request.getParameter("all");

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(request.getParameter("autoid"));
			if(!"".equals(autoid)){
				LaborBargainService lbs = new LaborBargainService(conn) ;
				
				lb = lbs.getLaborBargain(autoid) ;
				
				
				String fileNames = lb.getFileNames() ;
				String fileTempNames = lb.getFileRondomNames() ;

				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				
				modelAndView.addObject("fileArr",fileArr) ;
				modelAndView.addObject("fileTempArr",fileTempArr) ;
			}else{
				lb.setFileRondomNames(UUID.randomUUID().toString());
			}
			
			modelAndView.addObject("autoid", autoid);// 传值
			modelAndView.addObject("lb", lb);// 传值
			modelAndView.addObject("all", all);// 传值
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}

		return modelAndView;

	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeLabor(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");

			String autoid = request.getParameter("autoid");
			all = request.getParameter("all");

			LaborBargainService ls = new LaborBargainService(conn);
			
			LaborBargain lb = ls.getLaborBargain(autoid) ;
			AttachService attachService = new AttachService(conn);
			attachService.remove("oa/laoborBargainFoder", lb.getFileRondomNames());
			
			ls.removeLabor(autoid);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/laborbargain.do?all=" + all);

		return null;
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
	
			LaborBargainService lbs = new LaborBargainService(conn) ;
			LaborBargain lb = lbs.getLaborBargain(autoId) ;
			
			String fileNames = lb.getFileNames() ;
			String fileTempNames = lb.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("laoborBargainFoder",request);
			
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
						+"oa_workbargain  where autoid = '" + autoId + "' ";
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
				sql = "update oa_workbargain set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("laoborBargainFoder",request);
				
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
