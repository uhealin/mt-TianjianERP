package com.matech.audit.work.kdic;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.kdic.DicService;
import com.matech.audit.service.kdic.model.Dic;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.service.excelupload.ExcelUploadService;

public class DicAction extends MultiActionController{
	
	private final String _dicList = "/kdic/List.jsp";
	private final String _dicEdit = "/kdic/AddandEdit.jsp" ;
	private final String _treeList = "/kdic/treeList.jsp" ;

	

	/**
	 * 列表方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView modelandview = new ModelAndView(_dicList);
		ASFuntion asf = new ASFuntion();
		HttpSession session = request.getSession();
		
		DataGridProperty pp = new DataGridProperty();
		
		//点击树的时候使用
		String ctype = asf.showNull(request.getParameter("ctype")); 
		
		pp.setCustomerId("");

		pp.setTableID("dicList");

		pp.setInputType("radio");

		pp.setWhichFieldIsValue(1);	
		

		pp.setPrintEnable(true);
		pp.setPrintVerTical(false);

		pp.setPrintColumnWidth("20,20,20");

		pp.setPrintTitle("dic表信息");

		pp.addColumn("名字", "Name");
		pp.addColumn("值", "Value");
		pp.addColumn("类型", "ctype");
		
		String sql ="";
		
		if("".equals(ctype)){
			
		  sql = "select autoId,Name,Value,ctype " +
					"from k_dic " +
					"where 1=1 and userdata='1' ${ctype} ${dicName} ${dicValue}";
		}else{
			
			sql = "select autoId,Name,Value,ctype " +
				"from k_dic " +
				"where 1=1 and userdata='1' ";
			
			if("treeView".equals(ctype)){

			}else if(!"0".equals(ctype)){
				sql+="and ctype='"+ctype+"'";
			}
			
			modelandview.addObject("ctype",ctype);
			 
		}
		
		pp.setSQL(sql.toString());

		pp.setOrderBy_CH("autoId");
		pp.setDirection("asc");

		pp.addSqlWhere("ctype"," and ctype like '%${dicType}%'");
		pp.addSqlWhere("dicName"," and `name` like '%${dicName}%'");
		pp.addSqlWhere("dicValue"," and value like '%${dicValue}%'");
		
		session.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		return modelandview ;
	}
	
	
	/**
	 * 添加跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_dicEdit);
		
		String ctype = request.getParameter("ctype");
		modelAndView.addObject("ctype", ctype);
		return modelAndView;

	}
	
	
	/**
	 * 添加保存
	 * @param request
	 * @param response
	 * @param dic
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addDic(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		String ctype = request.getParameter("ctype");
		String property = request.getParameter("property");
		Connection conn = null;
		
		try {
			Dic dic = new Dic();
			dic.setName(name);
			dic.setValue(value);
			dic.setCtype(ctype);
			dic.setProperty(property);
			
			conn = new DBConnect().getConnect("");
			DicService ds = new DicService(conn) ;
			
			ds.add(dic) ;
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	 
		//response.sendRedirect(request.getContextPath()+"/kdic.do?method=list");
		return list(request, response);

	}
	
	
	/**
	 * 修改跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(_dicEdit);

		Dic dic = new Dic() ;

		String autoId = request.getParameter("autoId");// 获取前台传过来的值 autoId

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			String sql = "select autoId,Name,Value,ctype,property from k_dic where autoId='"
					+ autoId + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.next()) {
				dic.setAutoId(rs.getString(1)) ;
				dic.setName(rs.getString(2)) ;
				dic.setValue(rs.getString(3)) ;
				dic.setCtype(rs.getString(4)) ;
				dic.setProperty(rs.getString(5)) ;
 			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		modelAndView.addObject("autoId", autoId);// 传值
		modelAndView.addObject("dic",dic);// 传值
		String ctype = request.getParameter("ctype");
		modelAndView.addObject("ctype", ctype);
		return modelAndView;

	}
	
		
	/**
	 * 修改保存
	 * @param request
	 * @param response
	 * @param dic
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String autoId = request.getParameter("autoId");
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		String ctype = request.getParameter("ctype");
		String property = request.getParameter("property");
		Connection conn = null;
		
		try {
			Dic dic = new Dic();
			dic.setAutoId(autoId);
			dic.setName(name);
			dic.setValue(value);
			dic.setCtype(ctype);
			dic.setProperty(property);
			
			conn = new DBConnect().getConnect("");
			DicService ds = new DicService(conn) ;
			
			ds.update(dic) ;
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return list(request, response);
		//response.sendRedirect("kdic.do?method=list&ctype="+ctype);
	}
	
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;

		try {

			conn = new DBConnect().getConnect("");

			DicService ds = new DicService(conn);

			ASFuntion as = new ASFuntion() ;
			String autoId = as.showNull(request.getParameter("autoId")) ;
			ds.delete(autoId) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(conn);
		}
		return list(request, response);
	}
	
	
	
	/**
	 * 保存批量导入
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView SaveUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		PrintWriter out = null;
		Connection conn=null;
		Single sl = new Single();
		UserSession us=(UserSession)request.getSession().getAttribute("userSession");
		String lockmsg = "企业绩效";
		try {
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
//			response.setHeader("title", "EXCEL导入");
			out = response.getWriter();
			
			Map parameters = null;

			String uploadtemppath = "";
			String strFullFileName = "";
			
			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();

			//如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			strFullFileName = uploadtemppath
					+ (String) parameters.get("filename");
			org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
			uploadtemppath = (String) parameters.get("tempdir");

			if (uploadtemppath.equals(""))
				out.print("Error\n企业绩效数据上传及预处理失败");
			else
				out.println("企业绩效数据上传并分析成功!<br>正在加载，请等待<br><br><br>");

			int error = 0; //用于标记程序是否出错,出错了后面就不会再继续执行了


			//分析帐套文件,取出帐套年份;

			out.println("预处理分析企业绩效数据<br/>");
			out.flush();
			

			conn = new DBConnect().getDirectConnect("");

//			初始化业务对象
			ExcelUploadService upload = null;
			try {
				upload = new ExcelUploadService(conn,strFullFileName);
			} catch (Exception e) {
				e.printStackTrace();
				out.println("临时路径或者企业绩效编号设置有误,请与系统管理员联系<br>");
				error = 1;
			}
			
//			检查用户指定年份的帐套是否存在;

			//定义单一，避免其他用户干扰；
			
			
			try {
				sl.locked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println(e.getMessage() + "<br/>");
				error = 1;
			}

			if (error > 0) {
				out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
			} else {
				org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
				out.println("继续处理装载<br>");
				out.flush();
				
				
				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				//开始装载科目余额表信息
				//首先清空指定表的指定帐套的数据;
				out.println("正在装载企业绩效内容!......");
				out.flush();

				
				DicService ds = new DicService(conn);
				ds.newTable();

				upload.setExcelNum("");
				upload.setExcelString("顺序号,年份,行业,规模,项目,优秀值,良好值,平均值,较低值,较差值");
				String[] exexlKmye = { "顺序号", "年份", "行业", "规模", "项目", "优秀值", "良好值", "平均值", "较低值", "较差值" };
				String[] tableKmye = { "orderId", "year", "vocation", "scale", "project", "excellence", "favorable", "average", "lower", "short"};

				String result = "";

				result = upload.LoadFromExcel("企业绩效", "tt_k_performance",exexlKmye, tableKmye,null,null);

				out.println("装载企业绩效内容完毕!<BR>");

				out.flush();
				out.println("开始更新企业绩效列表!......");
				out.flush();
				// 删除 （优秀值,良好值,平均值,较低值,较差值） 这几列的值同时都是空的记录
				ds.del();
				
				// 删除  物理表里面 已有的 该年份的 数据
				ds.delData();
				
				// 把 临时表里面的数据插到 物理表里面
				ds.insertData();
				
				out.println("更新企业绩效列表完毕!<BR>");
				
				if (result != null && result.length() > 0) {
					out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("<hr>数据装载成功 <a href=\"##\" onclick=\" window.close();\">关闭</a></font>");
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ e.getMessage());
			out.println("<a href=\"user.do?method=Upload\">返回装载页面</a>\"</font>");
			
			e.printStackTrace();
		} finally {
			try {
				sl.unlocked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * dic tree
	 * 
	 * @param request
	 * @param response
	 */
	public void getTree(HttpServletRequest request,
			HttpServletResponse response) {

		Connection conn = null;
		response.setContentType("text/html;charset=utf-8");
		ASFuntion asf = new ASFuntion();
		 
		PrintWriter out = null;
		try {
			out = response.getWriter();
			conn = new DBConnect().getConnect("");

			DicService dicService = new DicService(conn);
			String sb = dicService.getTree();
			System.out.println(sb);
			out.write(JSONArray.fromObject(sb).toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
			out.close();
		}

	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getCtype(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView modelandview = new ModelAndView(_dicEdit);
	 
		String ctype = request.getParameter("ctype");
		
		modelandview.addObject("ctype",ctype);
		
		return modelandview ;
	}

}
