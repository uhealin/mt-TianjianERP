package com.matech.audit.service.form;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.matech.audit.service.form.model.FormButton;
import com.matech.audit.service.form.model.FormQuery;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

public class FormQueryConfigService {
	Connection conn = null;

	public FormQueryConfigService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 查询表单字段表
	 * 
	 * @param formId
	 * @return
	 */
	public List<FormQuery> getformQuery(String formId) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<FormQuery> list = new ArrayList<FormQuery>();
		String sql = "select * from MT_COM_FORM_QUERY where formid = ? order by orderid ";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, formId);
			rs = ps.executeQuery();
			while (rs.next()) {
				FormQuery formQuery = new FormQuery();
				formQuery.setUuid(rs.getString("UUID")); // uuid
				formQuery.setFormid(formId); // 表单id
				formQuery.setName(rs.getString("NAME")); // 字段名
				formQuery.setEnname(rs.getString("ENNAME"));//字段显示名（英文）
				formQuery.setBshow(rs.getInt("BSHOW")); // 是否在列表显示
				formQuery.setBhiddenrow(rs.getInt("BHIDDENROW")); // 是否放到trproperty（隐藏域）
				formQuery.setBorder(rs.getInt("BORDER")); // 按abs(数值)顺序排序
				formQuery.setBtype(rs.getString("BTYPE")); // 字段类型
				formQuery.setOrderid(rs.getInt("ORDERID"));
				formQuery.setRowFlag(rs.getString("ROWFLAG"));
				formQuery.setWidth(rs.getString("WIDTH"));
				list.add(formQuery);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return list;
	}
	/**
	 * 查询表单字段表,获取单条记录
	 * 
	 * @param formId
	 * @return
	 */
	public FormQuery getFormFieldById(String uuid) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from MT_COM_FORM_QUERY where uuid = '" + uuid+ "' ";
		FormQuery formQuery = new FormQuery();		
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				formQuery.setUuid(rs.getString("uuid")); // uuid
				formQuery.setFormid(rs.getString("formId")); // 表单id
				formQuery.setName(rs.getString("name")); // 字段名
				formQuery.setEnname(rs.getString("enname"));//字段显示名（英文）
				formQuery.setBshow(rs.getInt("bshow")); // 是否在列表显示
				formQuery.setBhiddenrow(rs.getInt("bhiddenrow")); // 是否放到trproperty（隐藏域）
				formQuery.setBorder(rs.getInt("border")); // 按abs(数值)顺序排序
				formQuery.setBtype(rs.getString("btype")); // 字段类型
				formQuery.setOrderid(rs.getInt("orderid"));
				formQuery.setWidth(rs.getString("width"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return formQuery;
	}

	/**
	 * 新增表单字段
	 * 
	 * @param FormQuery
	 */
	public void addFormField(FormQuery formQuery) {

		PreparedStatement ps = null;
		String sql = "insert into MT_COM_FORM_QUERY(" 
					+ " 	UUID, NAME, ENNAME, FORMID, BSHOW, "
					+ " 	BHIDDENROW, BORDER, BTYPE, ORDERID, ROWFLAG, "
					+ " 	WIDTH "
					+ " ) values( "
					+ " 	?,?,?,?,?, "
					+ " 	?,?,?,?,?, "
					+ " 	? "
					+ " ) " ;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, formQuery.getUuid());
			ps.setString(2, formQuery.getName());
			ps.setString(3, formQuery.getEnname());
			ps.setString(4, formQuery.getFormid());
			ps.setInt(5, formQuery.getBshow());
			ps.setInt(6, formQuery.getBhiddenrow());
			ps.setInt(7, formQuery.getBorder());
			ps.setString(8, formQuery.getBtype());
			ps.setInt(9, formQuery.getOrderid());
			ps.setString(10, formQuery.getRowFlag());
			ps.setString(11, formQuery.getWidth());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	/**
	 * 更新表单字段表
	 * 
	 * @param FormQuery
	 */
	public void updateFormField(FormQuery formQuery) {

		PreparedStatement ps = null;
		String sql = "update MT_COM_FORM_QUERY set " +
				"name=?,bshow=?,bhiddenrow=?,border=?,btype=? " +
				"where uuid=?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, formQuery.getName());
			ps.setInt(2, formQuery.getBshow());
			ps.setInt(3, formQuery.getBhiddenrow());
			ps.setInt(4, formQuery.getBorder());
			ps.setString(5, formQuery.getBtype());
			ps.setString(6, formQuery.getUuid());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 新增表单时，无条件往按钮表插5个按钮
	 * @param formId
	 */
	public void initFormListButton(String formId) throws Exception {
		
		String[] buttonNames = {"新增", "修改", "删除", "查看", "关闭"};
		String[] buttonIcons = {"add.gif", "edit.gif", "delete.gif", "query.gif", "close.gif"};
		String[] buttonOnlicks = {"mt_formList_add();", "mt_formList_edit();", "mt_formList_remove();", "mt_formList_view();", "mt_formList_close();"};
		
		for (int i = 0; i < buttonNames.length; i++) {
			FormButton formButton = new FormButton();
			
			formButton.setUuid(StringUtil.getUUID());
			formButton.setName(buttonNames[i]);
			formButton.setFormid(formId);
			formButton.setOrderid(i);
			formButton.setIcon(buttonIcons[i]);
			formButton.setAftergroup(1);
			formButton.setOnclick(buttonOnlicks[i]);
			formButton.setButtonType("0") ;
			formButton.setHandleType("0");
			formButton.setEnname(buttonIcons[i].substring(0,buttonIcons[i].indexOf(".")));
			addFormButton(formButton);
		}
	}

	/**
	 * 
	 * 查询表单按钮表
	 * 
	 * @param formId
	 * @return
	 */
	public List<FormButton> getFormButton(String formId, String buttonType) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<FormButton> list = new ArrayList<FormButton>();
		String sql = "select * from MT_COM_FORM_BUTTON "
					+ " where formid=? "
					+ " and buttonType=? "
					+ " ORDER BY orderid ";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, formId);
			ps.setString(2, buttonType);
			rs = ps.executeQuery();
			while (rs.next()) {
				FormButton formButton = new FormButton();
				formButton.setUuid(rs.getString("uuid")); // uuid
				formButton.setFormid(formId); // 表单id
				formButton.setName(rs.getString("name")); // 按钮名
				formButton.setEnname(rs.getString("enname")); // 按钮名
				formButton.setOrderid(rs.getInt("orderid")); // 排序编号
				formButton.setIcon(rs.getString("icon")); // 图标
				formButton.setAftergroup(rs.getInt("aftergroup")); // 0或1，1的话会在按钮后显示|分隔
				formButton.setOnclick(rs.getString("onclick")); // 点击调用的JS函数
				formButton.setExtjs(rs.getString("extjs")); // 扩展的JS函数
				formButton.setClassName(rs.getString("className"));
				formButton.setSql(rs.getString("sql"));
				formButton.setHandleType(rs.getString("handleType"));
				formButton.setButtonType(rs.getString("buttonType"));
				
				formButton.setBeforeClick(rs.getString("beforeClick"));
				formButton.setBeforeClickJs(rs.getString("beforeClickJs"));
				
				formButton.setAfterClick(rs.getString("afterClick"));
				formButton.setAfterClickJs(rs.getString("afterClickJs"));
				
				list.add(formButton);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return list;
	}

	/**
	 * 更新表单按钮表
	 * 
	 * @param formButton
	 */
	public void updateFormButton(FormButton formButton) throws Exception {

		PreparedStatement ps = null;
		String sql = "update MT_COM_FORM_BUTTON set "
					+ " name=?, orderid=?, icon=?, aftergroup=?, onclick=?, "
					+ " extjs=?, property=?, className=?,`sql`=?, handleType=?,  "
					+ " buttonType=?,beforeClick=?, beforeClickJs=?,afterClick=?,afterClickJs=?,enname=? "
					+ " where `uuid`=?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, formButton.getName());
			ps.setInt(2, formButton.getOrderid());
			ps.setString(3, formButton.getIcon());
			ps.setInt(4, formButton.getAftergroup());
			ps.setString(5, formButton.getOnclick());
			
			ps.setString(6, formButton.getExtjs());
			ps.setString(7, formButton.getProperty());
			ps.setString(8, formButton.getClassName());
			ps.setString(9, formButton.getSql());
			ps.setString(10, formButton.getHandleType());
			
			ps.setString(11, formButton.getButtonType());
			ps.setString(12, formButton.getBeforeClick());
			ps.setString(13, formButton.getBeforeClickJs());
			ps.setString(14, formButton.getAfterClick());
			ps.setString(15, formButton.getAfterClickJs());
			ps.setString(16, formButton.getEnname());
			ps.setString(17, formButton.getUuid());
			
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 
	 * 查询单条表单按钮记录
	 * 
	 * @param formId
	 * @return
	 */
	public FormButton getFormButtonByUuid(String uuid) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		FormButton formButton = null;
		String sql = "select * from MT_COM_FORM_BUTTON where uuid = '" + uuid+"'";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				formButton = new FormButton();
				formButton.setUuid(rs.getString("uuid")); // uuid
				formButton.setFormid(rs.getString("formId")); // 表单id
				formButton.setName(rs.getString("name")); // 按钮名
				formButton.setOrderid(rs.getInt("orderid")); // 排序编号
				formButton.setIcon(rs.getString("icon")); // 图标
				formButton.setAftergroup(rs.getInt("aftergroup")); // 0或1，1的话会在按钮后显示|分隔
				formButton.setOnclick(rs.getString("onclick")); // 点击调用的JS函数
				formButton.setExtjs(rs.getString("extjs")); // 扩展的JS函数
				formButton.setClassName(rs.getString("className"));
				formButton.setSql(rs.getString("sql"));
				formButton.setHandleType(rs.getString("handleType"));
				formButton.setButtonType(rs.getString("buttonType"));
				
				formButton.setBeforeClick(rs.getString("beforeClick"));
				formButton.setBeforeClickJs(rs.getString("beforeClickJs"));
				formButton.setEnname(rs.getString("enname"));
				formButton.setAfterClick(rs.getString("afterClick"));
				formButton.setAfterClickJs(rs.getString("afterClickJs"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return formButton;
	}
	
	/**
	 * 增加表单按钮
	 * 
	 * @param formButton
	 */
	public void addFormButton(FormButton formButton) throws Exception {

		PreparedStatement ps = null;
		String sql = "INSERT INTO MT_COM_FORM_BUTTON ( "
				+ " 	`uuid`, name, enname, formid, orderid, "
				+ " 	icon, aftergroup, onclick, extjs, className, "
				+ " 	`sql`,handleType, buttonType, beforeClick, beforeClickJs, "
				+ " 	afterClick,afterClickJs "
				+ " ) VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?)" ;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, formButton.getUuid());
			ps.setString(2, formButton.getName());
			ps.setString(3, formButton.getEnname());
			ps.setString(4, formButton.getFormid());
			ps.setInt(5, formButton.getOrderid());
			
			ps.setString(6, formButton.getIcon());
			ps.setInt(7, formButton.getAftergroup());
			ps.setString(8, formButton.getOnclick());
			ps.setString(9, formButton.getExtjs());
			ps.setString(10, formButton.getClassName());
			
			ps.setString(11, formButton.getSql());
			ps.setString(12, formButton.getHandleType());
			ps.setString(13, formButton.getButtonType());
			ps.setString(14, formButton.getBeforeClick());
			ps.setString(15, formButton.getBeforeClickJs());
			
			ps.setString(16, formButton.getAfterClick());
			ps.setString(17, formButton.getAfterClickJs());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 删除表单按钮
	 * 
	 * @param formButton
	 */
	public void delFormButton(String uuid) {

		PreparedStatement ps = null;
		String sql = "DELETE FROM MT_COM_FORM_BUTTON WHERE UUID='"+uuid+"'";
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 查询表单的listSQL
	 * 
	 * @param uuid
	 * @return listSql
	 */
	public List<String> findListSql(String uuid) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> list = new ArrayList<String>();
		String sql = "select LISTSQL,LISTHTML from MT_COM_FORM WHERE UUID='"+uuid+"'";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				list.add(rs.getString(1));
				list.add(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return list;
	}
	/**
	 * 更新表单的listSql
	 * 
	 * @param uuid
	 * @param listSql
	 */
	public void updateListSql(String uuid,String listSql) {

		PreparedStatement ps = null;
		String sql = "update MT_COM_FORM set LISTSQL=? where UUID=?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, listSql);
			ps.setString(2, uuid);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	/**
	 * 更新表单的listSql、listHtml
	 * 
	 * @param uuid
	 * @param listSql
	 * @param listHtml
	 */
	public void updateListSql(String uuid,String listSql,String listHtml) {

		PreparedStatement ps = null;
		String sql = "update MT_COM_FORM set LISTSQL=?,LISTHTML=?  where UUID=?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, listSql);
			ps.setString(2, listHtml);
			ps.setString(3, uuid);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	/**
	 * 执行sql更新字段名,返回list
	 * 
	 * @param uuid
	 * @param listSql
	 */
	public List<FormQuery> updateNameBySql(String formId,String listSql) throws Exception {

		List<FormQuery> list = new ArrayList<FormQuery>();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null; 
		try {
			String sql = " select * from (" + listSql + ") a where '1'='2' ";
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			
			FormQuery formQuery = null;
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				
				formQuery = getFieldByFieldName(formId, rsmd.getColumnLabel(i));
				
				if(formQuery == null) {
					formQuery = new FormQuery();
					formQuery.setUuid(UUID.randomUUID().toString());
					formQuery.setName("");
					formQuery.setEnname(rsmd.getColumnLabel(i));
					formQuery.setFormid(formId);
					formQuery.setOrderid(i);
					formQuery.setBshow(1);
					formQuery.setBhiddenrow(0);
					formQuery.setBorder(0);
					formQuery.setBtype("showLeft");
				} else {
					formQuery.setOrderid(i);
				}
				
				list.add(formQuery);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	/**
	 * 获取字段
	 * @param formId
	 * @param fieldName
	 * @return
	 */
	public FormQuery getFieldByFieldName(String formId, String enName) {
		FormQuery formQuery = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select uuid "
						+ " from MT_COM_FORM_QUERY "
						+ " where formId=? "
						+ " and upper(enName)=upper(?) ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, formId);
			ps.setString(2, enName);

			rs = ps.executeQuery();

			if (rs.next()) {
				formQuery = getFormFieldById(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return formQuery;
	}
	/**
	 * 获取字段
	 * @param formId
	 * @param fieldName
	 * @return
	 */
	public void removeAllField(String formId) {
		PreparedStatement ps = null;
		try {
			String sql = " delete from MT_COM_FORM_QUERY where formid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, formId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
}
