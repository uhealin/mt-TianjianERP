package com.matech.audit.work.oa.insurancetype;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.oa.employeecertificate.model.employeecertificateTable;
import com.matech.audit.service.oa.family.FamilyService;
import com.matech.audit.service.oa.family.model.Family;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.insuranceType.InsuranceTypeService;
import com.matech.audit.service.oa.insuranceType.model.InsuranceTypeTable;
import com.matech.audit.service.oa.labor.model.LaborBargain;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class InsuranceTypeAction extends MultiActionController {
	private final String _strList = "oa/insurancetype/List.jsp";

	private final String _strListDo = "/AuditSystem/insurancetype.do";

	private final String _AddandEdit = "oa/insurancetype/AddandEdit.jsp";

	/**
	 * 跳转到员工培训情况管理列表
	 * 
	 * @param request
	 * @param Response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ModelAndView modelAndView = new ModelAndView(_strList);
		DataGridProperty pp = new DataGridProperty() {
		};

		String all = req.getParameter("all");

		// 必要设置
		pp.setTableID("insurancetype");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置

		String sql = " select autoid, ctype, carea, ctime, cmoney, insurance,property "
				+ " from oa_insurancetype ";

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("险种类型");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);
		pp.setInputAction(" autoid=${autoid}  onClick=\"select_insurancetype(this);\" ");

		pp.addColumn("保险类型", "ctype");
		
		pp.addColumn("保险期限", "ctime");
		pp.addColumn("保险金额", "cmoney");
		pp.addColumn("保险费", "insurance");
		pp.addColumn("保险责任范围", "carea");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		modelAndView.addObject("all", all);
		return modelAndView;
	}
/**
 * 添加
 * @param req
 * @param res
 * @param itt
 * @return
 * @throws Exception
 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String autoid = "";
		String all = "";
		try {
			
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("insuranceTypeFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			
			InsuranceTypeTable itt = new InsuranceTypeTable() ;
			
			itt.setCtype((String)parameters.get("ctype")) ;
			itt.setCtime((String)parameters.get("ctime")) ;
			itt.setCmoney((String)parameters.get("cmoney")) ;
			itt.setInsurance((String)parameters.get("insurance")) ;
			itt.setCarea((String)parameters.get("carea")) ;
			itt.setFileNames(myfileUpload.getFileNames()) ;
			itt.setFileRondomNames(myfileUpload.getFileRondomNames()) ;
			
			autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			conn = new DBConnect().getConnect("");
			InsuranceTypeService its = new InsuranceTypeService(conn);
			if ("".equals(autoid)) {//添加
				its.add(itt);
			} else {//修改
				itt.setAutoid(Integer.parseInt(autoid));
				its.update(itt);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}
/**
 * 删除
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
			InsuranceTypeService its = new InsuranceTypeService(conn);
			its.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("all", all);
		return modelAndView;
	}
/**
 * 显示要修改的记录信息
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
		InsuranceTypeTable itt = new InsuranceTypeTable();
		String autoid = "";
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			InsuranceTypeService its = new InsuranceTypeService(conn);
			itt = its.getInsuranceType(autoid);
			
			String fileNames = itt.getFileNames() ;
			String fileTempNames = itt.getFileRondomNames() ;

			
			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			modelAndView.addObject("fileArr",fileArr) ;
			modelAndView.addObject("fileTempArr",fileTempArr) ;
			modelAndView.addObject("itt", itt);
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
 * @param req
 * @param res
 * @param itt
 * @return
 * @throws Exception
 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			all = req.getParameter("all");
			InsuranceTypeService its = new InsuranceTypeService(conn);
			
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("insuranceTypeFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			InsuranceTypeTable itt = new InsuranceTypeTable() ;
			
			String autoId = asf.showNull(req.getParameter("autoid")) ;
			InsuranceTypeTable ittTemp = its.getInsuranceType(autoId) ;

			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(ittTemp.getFileNames()) && !"".equals(ittTemp.getFileRondomNames())) {
				 fileNames = ittTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = ittTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			
			itt.setAutoid(Integer.parseInt(autoId)) ;
			itt.setCtype((String)parameters.get("ctype")) ;
			itt.setCtime((String)parameters.get("ctime")) ;
			itt.setCmoney((String)parameters.get("cmoney")) ;
			itt.setInsurance((String)parameters.get("insurance")) ;
			itt.setCarea((String)parameters.get("carea")) ;
			itt.setFileNames(fileNames) ;
			itt.setFileRondomNames(setFileRondomNames) ;
			
			its.update(itt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo + "?all=" + all);
		return new ModelAndView(_strList);
	}
/**
 * 删除险种时，检查是否有该险种的保险记录，如果有的话就不给删除
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
	public ModelAndView checkExistInsurance(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = new DBConnect().getConnect("");
			String autoid = request.getParameter("autoid");
			String existValue = "";
			String sql = "select 1 from oa_insurancetype a,oa_insurancecircs b \n"
					+ " where  a.ctype=b.insurancetype \n"
					+ " and a.autoid="
					+ autoid;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				existValue = rs.getString(1);
			}

			response.setContentType("text/html;charset=utf-8"); // 设置编码
			PrintWriter out = response.getWriter();

			if (!"".equals(existValue)) {//存在指定险种的记录
				out.print("ok");
			} else {//不存在指定险种的记录
				out.print("");
			}
			out.close();
		} catch (Exception e) {
			Debug.print(Debug.iError, "读取保险类型信息失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
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
			
			InsuranceTypeService its = new InsuranceTypeService(conn) ;
			InsuranceTypeTable itt = its.getInsuranceType(autoId) ;
			
			String fileNames = itt.getFileNames() ;
			String fileTempNames = itt.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("insuranceTypeFoder",request);
			
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
						+"oa_insurancetype  where autoid = '" + autoId + "' ";
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
	
				sql = "update oa_insurancetype set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("insuranceTypeFoder",request);
				
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
