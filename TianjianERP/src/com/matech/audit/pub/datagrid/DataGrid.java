package com.matech.audit.pub.datagrid;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.listener.UserSession;

/**
 * 
 * <p>Title:查询模块控件，用来输出数据table到jsp</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Description:</p>
 * <p>Company: Matech 广州铭太信息科技有限公司</p>
 * 
 * @author k
 * @version 3.0
 */
public class DataGrid extends com.matech.framework.pub.datagrid.ExtGrid {

	private DataGridProperty pp;

	public DataGrid(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, DataGridProperty property) {
		super(request, response, property);
		this.pp = property;
	}

	/*
	 * changeGrid.jsp的路径
	 */

	public String getChangeGridPath() {
		return "/AuditSystem/changeGrid.jsp";
	}
	
	public String getPrintPath() {
		return "/AuditSystem/AS_SYSTEM/setDataGridPrint.jsp";
	}

	public String getPrintCustomerId() {
		return getCustomerId();
	}
	
	public String getContextPath() {
		return "/AuditSystem";
	}
	
	/*
	 * 获取数据的方法。不同数据有不同设置
	 * 
	 * @see com.matech.framework.pub.datagrid.DataGrid#newString(java.sql.ResultSet,
	 *      int)
	 */
//	protected String newString(ResultSet rs, int colIndex) throws Exception {
//		return rs.getString(colIndex);
//	}

//	 mysql 用的方法
	 protected String newString(ResultSet rs, int colIndex) throws Exception {
	 byte[] b=rs.getBytes(colIndex);
	 if(b==null)
	 return "";
	 else return new String(b,"GBK");
	 }

	// 父类DataGrid会调这个方法取连接，并在父类里关闭连接。
	protected Connection getConnect() {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect(getCustomerId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 获得单位编号，用来切换数据库。
	public String getCustomerId() {
		
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		
		if(userSession == null) {
			return "";
		}
		
		if (pp.getCustomerId() != null) {
			return pp.getCustomerId();
		} 
		
		// 获取客户ＩＤ.
		if (pp.isCurProjectDatabase()) {
			return CHF.showNull(userSession.getCurCustomerId());
		} else {
			String accpackageid = CHF.showNull(userSession.getCurChoiceAccPackageId());
			if ("".equals(accpackageid)) {
				accpackageid = CHF.showNull(userSession.getCurAccPackageId());
			}

			if (accpackageid.length() >= 6) {
				return accpackageid.substring(0, 6);
			} else {
				return "";
			}
		}

	}
}
