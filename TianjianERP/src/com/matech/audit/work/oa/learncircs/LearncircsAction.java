package com.matech.audit.work.oa.learncircs;

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
import com.matech.audit.service.oa.family.FamilyService;
import com.matech.audit.service.oa.family.model.Family;
import com.matech.audit.service.oa.foder.Foder;
import com.matech.audit.service.oa.learncircs.LearncircsService;
import com.matech.audit.service.oa.learncircs.model.LearncircsTable;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class LearncircsAction extends MultiActionController {
	private final String _strList = "oa/learncircs/List.jsp";

	private final String _strListDo = "/AuditSystem/learncircs.do";

	private final String _AddandEdit = "oa/learncircs/AddandEdit.jsp";

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

		ASFuntion asf = new ASFuntion();
		ModelAndView modelAndView = new ModelAndView(_strList);
		// 必要设置
		pp.setTableID("learncircs");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置
		String all = req.getParameter("all");
		String sql = "";
		String sqlwhere = "";
		
		String searchPerson = asf.showNull(req.getParameter("searchPerson"));
		String department = asf.showNull(req.getParameter("department"));
		
		if(!"".equals(searchPerson)) {
			sqlwhere += " and userid like '%"+searchPerson+"%' ";
		}
		
		if(!"".equals(department)) {
			sqlwhere += " and learnframework like '%"+department+"%' ";
		}
		
		String myUserid = (String) req.getSession().getAttribute("myUserid");
		if ("all".equals(all)) {// 没指定具体某个人时显示的信息
			sql = " select autoid,b.name username,startlearndate,endlearndate,learncontent,learncertificate,learnachievement,learnframework,learnlocus,checkinperson,checkindate,remark,property,learntype,learnperiod \n"
					+ " from oa_learncircs a left join k_user b on a.userid=b.id where 1=1 "+sqlwhere;

		} else {// 指定具体某个人时显示的信息
			sql = " select autoid,userid,startlearndate,endlearndate,learncontent,learncertificate,learnachievement,learnframework,learnlocus,checkinperson,checkindate,remark,property,learntype,learnperiod \n"
					+ " from oa_learncircs where userid=" + myUserid;

		}

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("员工培训情况记录");

		pp.setSQL(sql);
		pp.setDirection_CH("autoid");

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		if ("all".equals(all)) {// 显示全部人的信息时，需显示：接受培训人
			pp.addColumn("接受培训人", "username");
		}
		
		pp.addColumn("培训负责机构", "learnframework");
		pp.addColumn("培训地点", "learnlocus");
		pp.addColumn("培训类型", "learntype");
		pp.addColumn("起始培训日期", "startlearndate");
		pp.addColumn("结束培训日期", "endlearndate");
		pp.addColumn("培训学时", "learnperiod");
		pp.addColumn("培训成绩", "learnachievement");
		pp.addColumn("培训证书", "learncertificate");
		//pp.addColumn("备注", "remark");
		pp.addColumn("登记人", "checkinperson");
		pp.addColumn("登记日期", "checkindate");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		modelAndView.addObject("all", all);

		return modelAndView;
	}

	/**
	 * 添加记录
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
		String userid = "";
		String all = "";

		try {
			all = CHF.showNull(req.getParameter("all"));
			if ("all".equals(all)) {// 全部人时
				userid = CHF.showNull(req.getParameter("userid"));
			} else {// 指定某个人
				userid = (String) req.getSession().getAttribute("myUserid");
			}

			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("learncircsFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			LearncircsTable lt = new LearncircsTable();
			UserSession usersession = (UserSession) req.getSession()
					.getAttribute("userSession");
			conn = new DBConnect().getConnect("");
			lt.setUserid(userid);
			lt.setStartlearndate((String)parameters.get("startlearndate"));
			lt.setEndlearndate((String)parameters.get("endlearndate"));
			lt.setLearncontent((String)parameters.get("learncontent"));
			lt.setLearncertificate((String)parameters.get("learncertificate"));
			lt.setLearnachievement((String)parameters.get("learnachievement"));
			lt.setLearnframework((String)parameters.get("learnframework"));
			lt.setLearnlocus((String)parameters.get("learnlocus"));
			lt.setRemark((String)parameters.get("remark"));
			lt.setFileNames(myfileUpload.getFileNames()) ;
			lt.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			lt.setLearntype((String)parameters.get("learntype")) ;
			lt.setLearnperiod((String)parameters.get("learnperiod")) ;
			
			LearncircsService ls = new LearncircsService(conn);
			ls.add(lt);

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
			LearncircsService ls = new LearncircsService(conn);
			
			LearncircsTable lt = ls.getLearncircs(autoid);
			AttachService attachService = new AttachService(conn);
			attachService.remove("oa/learncircsFoder", lt.getFileRondomNames());
			
			ls.del(autoid);
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
	 * 显示修改记录
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
		LearncircsTable lt = new LearncircsTable();
		String all = "";
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			all = CHF.showNull(req.getParameter("all"));
			if(!"".equals(autoid)){
				LearncircsService ls = new LearncircsService(conn);
				lt = ls.getLearncircs(autoid);
				
				String fileNames = lt.getFileNames() ;
				String fileTempNames = lt.getFileRondomNames() ;

				
				String[] fileArr = fileNames.split(",") ;
				String[] fileTempArr = fileTempNames.split(",") ;
				
				modelAndView.addObject("fileArr",fileArr) ;
				modelAndView.addObject("fileTempArr",fileTempArr) ;
	
			}else{
				lt.setFileRondomNames(UUID.randomUUID().toString());
			}
			modelAndView.addObject("llt", lt);
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
	 * @param lt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Connection conn = null;
		String all = "";
		String userid = "";
		try {
			all = req.getParameter("all");
			if("all".equals(all)) {//显示全部的时候从页面上取
				userid = req.getParameter("userid");
			} else {//显示具体某个人的时候，从Session取
				userid = (String)req.getSession().getAttribute("myUserid");
			}
			LearncircsTable lt = new LearncircsTable() ;
			conn = new DBConnect().getConnect("");
			LearncircsService ls = new LearncircsService(conn);
			
			ASFuntion asf = new ASFuntion() ;
			MyFileUpload myfileUpload = new MyFileUpload(req);
			
			Foder foder  = new Foder("learncircsFoder",req);
			
			String uploadtemppath = myfileUpload.UploadFiles(false, foder.getDirPath());

			Map parameters = myfileUpload.getMap();
			
			
			String autoId = asf.showNull(req.getParameter("autoid")) ;
			LearncircsTable ltTemp = ls.getLearncircs(autoId) ;

			String fileNames = "" ;
			String setFileRondomNames = "" ;
			if(!"".equals(ltTemp.getFileNames()) && !"".equals(ltTemp.getFileRondomNames())) {
				 fileNames = ltTemp.getFileNames()+","+myfileUpload.getFileNames() ;
				 setFileRondomNames = ltTemp.getFileRondomNames()+","+myfileUpload.getFileRondomNames() ;
			}else {
				 fileNames = myfileUpload.getFileNames() ;
				 setFileRondomNames = myfileUpload.getFileRondomNames() ;
			}
			lt.setAutoid(Integer.parseInt(autoId)) ;
			lt.setUserid(userid);
			lt.setStartlearndate((String)parameters.get("startlearndate"));
			lt.setEndlearndate((String)parameters.get("endlearndate"));
			lt.setLearncontent((String)parameters.get("learncontent"));
			lt.setLearncertificate((String)parameters.get("learncertificate"));
			lt.setLearnachievement((String)parameters.get("learnachievement"));
			lt.setLearnframework((String)parameters.get("learnframework"));
			lt.setLearnlocus((String)parameters.get("learnlocus"));
			lt.setRemark((String)parameters.get("remark"));
			lt.setFileNames(fileNames) ;
			lt.setFileRondomNames((String)parameters.get("fileRondomNames")) ;
			
			lt.setLearntype((String)parameters.get("learntype")) ;
			lt.setLearnperiod((String)parameters.get("learnperiod")) ;
			
			ls.update(lt);

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
	
		
			LearncircsService ls = new LearncircsService(conn) ;
			LearncircsTable lt = ls.getLearncircs(autoId) ;
			String fileNames = lt.getFileNames() ;
			String fileTempNames = lt.getFileRondomNames() ;

			String[] fileArr = fileNames.split(",") ;
			String[] fileTempArr = fileTempNames.split(",") ;
			
			String fileTempName = "" ;
			for(int i=0;i<fileArr.length;i++) {
				if(fileName.equals(fileArr[i])) {
					fileTempName = fileTempArr[i] ;
				}
			}
	
			fileName = URLEncoder.encode(fileName, "UTF-8");
			Foder foder  = new Foder("learncircsFoder",request);
			
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
						+"oa_learncircs  where autoid = '" + autoId + "' ";
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
	
				sql = "update oa_learncircs set uploadFileName=?,uploadTempName=? where autoId=?" ;
				 ps2 = conn.prepareStatement(sql) ;
				 ps2.setString(1, fileNames) ;
				 ps2.setString(2, fileTempNames) ;
				 ps2.setString(3, autoId) ;
				 ps2.executeUpdate() ;
				
				fileName = URLEncoder.encode(fileName, "UTF-8");
				Foder foder  = new Foder("learncircsFoder",request);
				
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
