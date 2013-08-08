package com.matech.audit.work.oa.examinelibrary;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.oa.examinelibrary.ExamineLibraryService;
import com.matech.audit.service.oa.examinelibrary.model.ExamineLibraryTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class ExamineLibraryAction extends MultiActionController {
	private final String _strList = "oa/examinelibrary/List.jsp";

	private final String _strListDo = "/AuditSystem/examinelibrary.do";

	private final String _AddandEdit = "oa/examinelibrary/AddandEdit.jsp";

	/**
	 * 跳转到考核指标设定列表
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
		pp.setTableID("examinelibrary");
		// 基本设置

		pp.setCustomerId("");

		pp.setPageSize_CH(50);

		// sql设置
		String sql = "";

		sql = " select 	autoid, ctype, cname, if(property='定量',ccal,concat(ccal,cformula)) myccal, isenable, orderid, property, Memo from oa_examinelibrary";

		// 查询设置

		pp.setPrintEnable(true);
		pp.setPrintTitle("考核指标设定");

		pp.setSQL(sql);
		pp.setOrderBy_CH("ctype,orderid");
		pp.setDirection_CH("asc,asc");
		

		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);

		pp.addColumn("考核类型", "ctype");
		pp.addColumn("考核名称", "cname");
		pp.addColumn("考核规则", "myccal");
//		pp.addColumn("考核值", "cformula");
		pp.addColumn("是否有效", "isenable");
		pp.addColumn("定量定性", "property");
		pp.addColumn("说明", "Memo");

		req.getSession()
				.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

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
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		ExamineLibraryTable elt = new ExamineLibraryTable();
		String autoid = "";
		String ctype = "";
		String cname = "";
		String ccal = "";
		String cformula = "";
		String memo = "";
		String orderid = "";
		String isenable = "";
		String tip = "";

		try {

			autoid = CHF.showNull(req.getParameter("autoid"));
			ctype = CHF.showNull(req.getParameter("ctype"));
			cname = CHF.showNull(req.getParameter("cname"));
			ccal = CHF.showNull(req.getParameter("ccal"));
			cformula = CHF.showNull(req.getParameter("cformula"));
			memo = CHF.showNull(req.getParameter("memo"));
			orderid = CHF.showNull(req.getParameter("orderid"));
			isenable = CHF.showNull(req.getParameter("isenable"));
			tip = CHF.showNull(req.getParameter("tip"));
			
			elt.setCtype(ctype);
			elt.setCname(cname);
			elt.setCcal(ccal);
			elt.setCformula(cformula);
			elt.setMemo(memo);
			elt.setIsenable(isenable);
			if("".equals(autoid)) {
				elt.setProperty("定性");//只允许添加定性的
			}
			String sql = "";
			double orderid1 = 0;
			String anotherValue = "";
			conn = new DBConnect().getConnect("");
			
			//处理排序问题
			if ("".equals(orderid) || orderid == null) {
				//如果没有选择位置则放在最后一位
				sql = "select orderid from oa_examinelibrary where ctype='"+ctype+"' order by orderid desc limit 1";
				orderid = new DbUtil(conn).queryForString(sql);
				if("".equals(orderid) || orderid == null) {
					//如果里面没有数据则将序号定为100
					orderid1 = 100;
				} else {
					//有数据则取最后一位的加10作为序号
					orderid1 = Double.parseDouble(orderid) + 10;	
				}
				
			} else {
//				sql = "select orderid from oa_examinelibrary where ctype = '"+ctype+"' and cname='" + orderid + "'limit 1";
//				orderid = new DbUtil(conn).queryForString(sql);
				if (tip.equals("before")) {
					//选择在什么之前
					sql = "select orderid from oa_examinelibrary where ctype='"+ctype+"' and orderid<" + orderid + " order by orderid desc limit 1";
					anotherValue = new DbUtil(conn).queryForString(sql);
					if("".equals(anotherValue) || anotherValue == null) {
						//所选择的序号值之前没值则出所选序号值的一半
						orderid1 = Double.parseDouble(orderid) / 2;
					} else {
						//所选择的序号值之前有值则取所选值和前一值加起来的一半作为序号值
						orderid1 = (Double.parseDouble(orderid) + Double.parseDouble(anotherValue)) / 2;		
					}
				
				} else {
					//选择在什么之后
					sql = "select orderid from oa_examinelibrary where ctype='"+ctype+"' and  orderid>" + orderid + " order by orderid  limit 1";
					anotherValue = new DbUtil(conn).queryForString(sql);
					if("".equals(anotherValue) || anotherValue == null) {
						//选择值之后没值则取选择值加１０作为当前序号值
						orderid1 = Double.parseDouble(orderid) +10; 
					} else {
						//选择值之后有值，取所选值和下一个值和的一半作为当前值
						orderid1 = (Double.parseDouble(orderid) + Double.parseDouble(anotherValue)) / 2;	
					}
					
				}

			}
			elt.setOrderid(orderid1);
			
			ExamineLibraryService els = new ExamineLibraryService(conn);
			if ("".equals(autoid)) {
				els.add(elt);
			} else {
				elt.setAutoid(Integer.parseInt(autoid));
				els.update(elt);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo);
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

		ASFuntion CHF = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String autoid = CHF.showNull(req.getParameter("autoid"));
			ExamineLibraryService els = new ExamineLibraryService(conn);
			els.del(autoid);
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
		ExamineLibraryTable elt = new ExamineLibraryTable();
		String autoid = "";
		String sql = "";
		String property = "";
		String myType = "";
		try {
			conn = new DBConnect().getConnect("");
			autoid = CHF.showNull(req.getParameter("autoid"));
			sql = "select property from oa_examinelibrary where autoid="+autoid;
			property = new DbUtil(conn).queryForString(sql);
			if("定性".equals(property)) {//只有定量和定性两种
				myType = "1";
			} else {
				myType = "0";
			}
			ExamineLibraryService els = new ExamineLibraryService(conn);
			elt = els.getExamineLibrary(autoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		modelAndView.addObject("elt", elt);
		modelAndView.addObject("autoid", autoid);//判断是添加还是修改
		modelAndView.addObject("myType", myType);//判断是定性还是定量
		return modelAndView;
	}

	/**
	 * 更新信息
	 * 
	 * @param req
	 * @param res
	 * @param elt
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest req, HttpServletResponse res,
			ExamineLibraryTable elt) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ExamineLibraryService els = new ExamineLibraryService(conn);
			els.update(elt);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);

		}
		res.sendRedirect(_strListDo);
		return new ModelAndView(_strList);
	}
	
	public ModelAndView checkCname(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		try {
			conn= new DBConnect().getConnect("");
			String subjectfullname="";
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
					
			if(!"".equals(subjectfullname)) {
				out.print(subjectfullname);
			} else {
				out.print("");
			}
			out.close(); 
		} catch (Exception e) {
			Debug.print(Debug.iError, "读取科目信息失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}	
		return null;
	}

}
