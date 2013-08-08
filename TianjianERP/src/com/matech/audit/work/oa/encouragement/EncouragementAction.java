package com.matech.audit.work.oa.encouragement;

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
import com.matech.audit.service.oa.employeecertificate.model.employeecertificateTable;
import com.matech.audit.service.oa.encouragement.EncouragementService;
import com.matech.audit.service.oa.encouragement.model.EncouragementTable;
import com.matech.audit.service.oa.family.FamilyService;
import com.matech.audit.service.oa.family.model.Family;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.learncircs.model.LearncircsTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class EncouragementAction extends MultiActionController {
	private final String _strList = "oa/encouragement/List.jsp";

	private final String _strListDo = "/AuditSystem/encouragement.do";

	private final String _AddandEdit = "oa/encouragement/AddandEdit.jsp";

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

		DataGridProperty pp = new DataGridProperty() {
		};

		ModelAndView modelAndView = new ModelAndView(_strList);
		ASFuntion asf = new ASFuntion();
		// 必要设置
		pp.setTableID("encouragement");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置
		String all = asf.showNull(req.getParameter("all"));
		String sql = "";
		String searchsql = "";
		
		String searchPerson = asf.showNull(req.getParameter("searchPerson"));
		String searchType = asf.showNull(req.getParameter("searchType"));

		if(!"".equals(searchPerson)) {
			searchsql += " and userid like '%"+searchPerson+"%' ";
		}
		
		if(!"".equals(searchType)) {
			searchsql += " and pricetype like '%"+searchType+"%' ";
		}
		
		String myUserid = (String) req.getSession().getAttribute("myUserid");
		if ("all".equals(all)) {// 显示全部人员信息
			sql = " select a.autoid,b.name username,userid,pricedate,pricetype,whys,result,"
					+ " remark,checkinperson,checkindate,property  from oa_encouragement a"
					+ " left join k_user b on a.userid=b.id  \n"
					+ " where 1=1 "+searchsql;

		} else {// 从人员入口进去，显示具体某个人的信息
			sql = " select autoid,userid,pricedate,pricetype,whys,result,"
					+ " remark,checkinperson,checkindate,property  from oa_encouragement"
					+ " where userid=" + myUserid;
		}

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("员工奖惩记录");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		if ("all".equals(all)) {// 显示全部人的时候需要显示对应人
			pp.addColumn("奖惩人", "username");
		}
		pp.addColumn("奖惩时间", "pricedate");
		pp.addColumn("奖惩类型", "pricetype");
		pp.addColumn("奖惩事由", "whys");
		pp.addColumn("奖惩结果", "result");
		pp.addColumn("备注", "remark");
		pp.addColumn("登记人", "checkinperson");
		pp.addColumn("登记日期", "checkindate");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		modelAndView.addObject("all", all);
		return modelAndView;
	}

	/**
	 * 添加信息
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		String autoid = "";
		String userid = "";
		String all = "";

		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			if ("all".equals(all)) {
				userid = CHF.showNull(req.getParameter("userid"));
			} else {
				userid = (String) req.getSession().getAttribute("myUserid");
			}

			EncouragementTable et = new EncouragementTable();
			UserSession usersession = (UserSession) req.getSession()
					.getAttribute("userSession");
			
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("encouragementFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();

			et.setUserid(userid);
			et.setPricedate((String)parameters.get("pricedate"));
			et.setPricetype((String)parameters.get("pricetype"));
			et.setWhys((String)parameters.get("whys"));
			et.setResult((String)parameters.get("result"));
			et.setRemark((String)parameters.get("remark"));
			et.setCheckinperson(usersession.getUserName());
			et.setFileNames(myfileUpload.getFileNames()) ;
			et.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			EncouragementService es = new EncouragementService(conn);
			if ("".equals(autoid)) {
				es.add(et);
			} else {
				et.setAutoid(Integer.parseInt(autoid));
				es.update(et);
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
			EncouragementService es = new EncouragementService(conn);
			EncouragementTable et = es.getEncouragement(autoid);
			AttachService attachService = new AttachService(conn);
			attachService.remove("oa/encouragementFoder", et.getFileRondomNames());
			
			es.del(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		
		modelAndView.addObject("all", all);
		return modelAndView;

//		return new ModelAndView(_strList);
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
		EncouragementTable et = new EncouragementTable();
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			if(!"".equals(autoid)){
				EncouragementService es = new EncouragementService(conn);
				et = es.getEncouragement(autoid);
				
				String fileNames = et.getFileNames() ;
				String fileTempNames = et.getFileRondomNames() ;

				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				
				modelAndView.addObject("fileArr",fileArr) ;
				modelAndView.addObject("fileTempArr",fileTempArr) ;
	
			}else{
				et.setFileRondomNames(UUID.randomUUID().toString());
			}
			
			modelAndView.addObject("eet", et);
			modelAndView.addObject("all", all);
			modelAndView.addObject("autoid", autoid);
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
	 * @param et
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		String userid = "";
		String all = "";
		ASFuntion CHF = new ASFuntion();
		try {
			conn = new DBConnect().getConnect("");
			EncouragementService es = new EncouragementService(conn);
			
			all = CHF.showNull(req.getParameter("all"));
			if ("all".equals(all)) {
				userid = CHF.showNull(req.getParameter("userid"));
			} else {
				userid = (String) req.getSession().getAttribute("myUserid");
			}
			
			UserSession usersession = (UserSession) req.getSession()
			.getAttribute("userSession");
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("encouragementFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			EncouragementTable et = new EncouragementTable() ;
			
			String autoId = asf.showNull(req.getParameter("autoid")) ;
			EncouragementTable etTemp = es.getEncouragement(autoId) ;
			
			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(etTemp.getFileNames()) && !"".equals(etTemp.getFileRondomNames())) {
				 fileNames = etTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = etTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			
			et.setAutoid(Integer.parseInt(autoId)) ;
			et.setUserid(userid);
			et.setPricedate((String)parameters.get("pricedate"));
			et.setPricetype((String)parameters.get("pricetype"));
			et.setWhys((String)parameters.get("whys"));
			et.setResult((String)parameters.get("result"));
			et.setRemark((String)parameters.get("remark"));
			et.setCheckinperson(usersession.getUserName());
			et.setFileNames(fileNames) ;
			et.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			
			es.update(et);

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
	
			EncouragementService es = new EncouragementService(conn) ;
			EncouragementTable et = es.getEncouragement(autoId) ;
			
			String fileNames = et.getFileNames() ;
			String fileTempNames = et.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("encouragementFoder",request);
			
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
						+"oa_encouragement  where autoid = '" + autoId + "' ";
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
	
				sql = "update oa_encouragement set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("encouragementFoder",request);
				
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
