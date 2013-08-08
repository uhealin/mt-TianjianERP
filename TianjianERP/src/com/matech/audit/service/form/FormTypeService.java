package com.matech.audit.service.form;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.form.model.FormType;
import com.matech.framework.pub.db.DbUtil;


public class FormTypeService {
	private Connection conn = null;
	
	public FormTypeService(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 返回列表
	 * @param parentId
	 * @return
	 */
	public List getFormTypeJSONList(String parentId) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		List list = new ArrayList();

		try {

			String sql = "select distinct A.FORM_TYPE_ID, A.FORM_TYPE_NAME, A.PARENTID,case\n" +
						"                  when B.PARENTID is null then\n" + 
						"                   '1'\n" + 
						"                  else\n" + 
						"                   '0'\n" + 
						"                end as ISLEAF\n" + 
						"  from MT_COM_FORM_TYPE A\n" + 
						"  left join MT_COM_FORM_TYPE B\n" + 
						"    on A.FORM_TYPE_ID = B.PARENTID\n" + 
						" where A.PARENTID = ? "
						+ " order by A.FORM_TYPE_NAME desc ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentId);

			rs = ps.executeQuery();

			while (rs.next()) {

				Map map = new HashMap();
				String typeid = rs.getString("FORM_TYPE_ID");
				String typename = rs.getString("FORM_TYPE_NAME");
				String isleaf = rs.getString("PARENTID");

				map.put("id", typeid);
				map.put("text", typename);
				map.put("parentId", parentId);

				if (!"1".equals(isleaf)) {
					// 非叶�?
					map.put("leaf", false);
				} else {
					map.put("leaf", true);
				}

				list.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return list;
	}
 
	/**
	 * 删除分类
	 * @param formTypeId
	 * @return
	 */
	public int remove(String formTypeId) {
		int result = -1;

		PreparedStatement ps = null;

		String sql = " delete from MT_COM_FORM_TYPE "
					+ " where FORM_TYPE_ID=? ";

		try {
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, formTypeId);
			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return result;
	}
	
	/**
	 * 获取分类
	 * @param formTypeId
	 * @return
	 */
	public FormType getFormTypeByTypeId(String formTypeId) {
		FormType formType = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = " select FORM_TYPE_ID, FORM_TYPE_NAME, PARENTID, PROPERTY "
						+ " from MT_COM_FORM_TYPE "
						+ " where FORM_TYPE_ID=? ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, formTypeId);
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				formType = new FormType();
				formType.setFormTypeId(rs.getString(1));
				formType.setFormTypeName(rs.getString(2));
				formType.setParentId(rs.getString(3));
				formType.setProperty(rs.getString(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return formType;
	}
	
	/**
	 * 更新分类
	 * @param formType
	 * @return
	 */
	public int update(FormType formType) {
		int result = -1;

		PreparedStatement ps = null;

		String sql = " update MT_COM_FORM_TYPE set "
					+ " 	FORM_TYPE_NAME=?, PARENTID=?, PROPERTY=? "
					+ " where FORM_TYPE_ID=? ";

		try {
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, formType.getFormTypeName());
			ps.setString(2, formType.getParentId());
			ps.setString(3, formType.getProperty());
			ps.setString(4, formType.getFormTypeId());

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return result;
	}
	
	/**
	 * 保存分类
	 * @param formType
	 * @return
	 */
	public int save(FormType formType) {
		int result = -1;

		PreparedStatement ps = null;

		String sql = " insert into MT_COM_FORM_TYPE( "
					+ " 	FORM_TYPE_ID, FORM_TYPE_NAME, PARENTID, PROPERTY "
					+ " )values(?,?,?,?)";

		try {
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, formType.getFormTypeId());
			ps.setString(2, formType.getFormTypeName());
			ps.setString(3, formType.getParentId());
			ps.setString(4, formType.getProperty());

			result = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return result;
	}
}
