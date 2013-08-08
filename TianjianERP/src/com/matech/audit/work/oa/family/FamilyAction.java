package com.matech.audit.work.oa.family;

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
import com.matech.audit.service.oa.family.FamilyService;
import com.matech.audit.service.oa.family.model.Family;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.specialitycompetence.specialityCompetenceService;
import com.matech.audit.service.oa.specialitycompetence.model.SpecialityCompetenceTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class FamilyAction extends MultiActionController {

	String _family = "/oa/family/Familylist.jsp";

	String _familyEdit = "/oa/family/Familyadd.jsp";

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

		ModelAndView modelandview = new ModelAndView(_family);

		HttpSession session = request.getSession();

		DataGridProperty pp = new DataGridProperty();

		pp.setCustomerId("");

		pp.setTableID("Familylist");

		pp.setInputType("radio");

		pp.setWhichFieldIsValue(1);

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);

		pp.setPrintColumnWidth("20,20,20,20,20");

		pp.setPrintTitle("家庭成员登记");

		pp.addColumn("姓名", "compellation");
		pp.addColumn("关系", "footing");
		pp.addColumn("工作单位", "workunit");
		pp.addColumn("联系电话", "phone");
		pp.addColumn("政治面貌", "government");
		String myUserid = (String) request.getSession()
				.getAttribute("myUserid");
		String sql = "select autoid,compellation,footing,workunit,phone,government,userid from oa_family where userid="
				+ myUserid;

		pp.setSQL(sql);

		pp.setOrderBy_CH("autoid");
		pp.setDirection("asc");

		session.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return modelandview;

	}

	/**
	 * 增加家庭成员信息
	 * 
	 * @param request
	 * @param response
	 * @param family
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addFamily(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();

		Connection conn = null;

		try {

			conn = new DBConnect().getConnect("");
			String userid = (String) session.getAttribute("myUserid");
			FamilyService fs = new FamilyService(conn);
			
			ASFuntion asf = new ASFuntion() ;
			
			MyFileUpload myfileUpload = new MyFileUpload(request);
			
			
			Foder foder  = new Foder("familyFoder",request);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			Family family = new Family() ;
			
			family.setCompellation((String)parameters.get("compellation")) ;
			family.setFooting((String)parameters.get("footing")) ;
			family.setGovernment((String)parameters.get("government")) ;
			family.setPhone((String)parameters.get("phone")) ;
			family.setWorkunit((String)parameters.get("workunit")) ;
			
			family.setFileNames(myfileUpload.getFileNames()) ;
			family.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			family.setUserid(userid);
			fs.addFamily(family);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/family.do");

		return null;

	}

	/**
	 * 修改
	 * 
	 * @param request
	 * @param response
	 * @param family
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateFamily(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;

		try {
			
			conn = new DBConnect().getConnect("");
			
			ASFuntion asf = new ASFuntion() ;
			
			MyFileUpload myfileUpload = new MyFileUpload(request);
			
			
			Foder foder  = new Foder("familyFoder",request);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			Family family = new Family() ;
			FamilyService fs = new FamilyService(conn);
			
			String autoId = asf.showNull(request.getParameter("autoid")) ;
			Family familyTemp = fs.getFamily(autoId) ;
			
			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(familyTemp.getFileNames()) && !"".equals(familyTemp.getFileRondomNames())) {
				 fileNames = familyTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = familyTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			
			
			family.setCompellation((String)parameters.get("compellation")) ;
			family.setFooting((String)parameters.get("footing")) ;
			family.setGovernment((String)parameters.get("government")) ;
			family.setPhone((String)parameters.get("phone")) ;
			family.setWorkunit((String)parameters.get("workunit")) ;
			family.setFileNames(fileNames) ;
			family.setFileRondomNames((String)parameters.get("fileRondomNames")) ;

			fs.updateFamily(family, autoId);

			;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/family.do");

		return null;
	}

	/**
	 * 显示要修改的信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exitFamily(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_familyEdit);

		Family family = new Family();

		// 要修改的劳动合同的autoid
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		ASFuntion CHF = new ASFuntion();
		try {
			String autoid = CHF.showNull(request.getParameter("autoid"));// 获取前台传过来的值 autoid
			if(!"".equals(autoid)){
				conn = new DBConnect().getConnect("");

				FamilyService fs = new FamilyService(conn);
				family = fs.getFamily(autoid);
				
				String fileNames = family.getFileNames() ;
				String fileTempNames = family.getFileRondomNames() ;

				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				
				modelAndView.addObject("fileArr",fileArr) ;
				modelAndView.addObject("fileTempArr",fileTempArr) ;
			}else{
				family.setFileRondomNames(UUID.randomUUID().toString());
			}
			
			modelAndView.addObject("autoid", autoid);// 传值
			modelAndView.addObject("family", family);// 传值

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		


		return modelAndView;

	}

	/**
	 * 删除信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeFamily(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			String autoid = request.getParameter("autoid");

			FamilyService fs = new FamilyService(conn);

			//删除附件
			Family family = fs.getFamily(autoid);
			AttachService attachService = new AttachService(conn);
			attachService.remove("oa/familyFoder", family.getFileRondomNames());	
			
			fs.removeLabor(autoid);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("/AuditSystem/family.do");

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
	
			FamilyService fs = new FamilyService(conn);
			Family family = fs.getFamily(autoId) ;
			
			String fileNames = family.getFileNames() ;
			String fileTempNames = family.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("familyFoder",request);
			
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
						+"oa_family  where autoid = '" + autoId + "' ";
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
	
				sql = "update oa_family set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("familyFoder",request);
				
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
