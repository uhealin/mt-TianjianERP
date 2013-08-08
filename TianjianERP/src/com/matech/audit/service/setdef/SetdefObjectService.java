package com.matech.audit.service.setdef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.setdef.model.SetdefObject;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

/**
 *
 * <p>Title: 示例模版程序</p>
 *
 * <p>Description: 作为通用的模版，供开发组复用</p>
 *
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.
 * All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author Phoenix
 * @version 3.0
 */

/**
 * @author Administrator
 *
 */
public class SetdefObjectService {

    private Connection conn=null;

    public SetdefObjectService(Connection conn) {
        this.conn=conn;
    }
	
 /**
  * 增加自定义的方法
  * @param args
  */
    public boolean add(SetdefObject obj) throws Exception{
        
    	DbUtil.checkConn(conn);
    	//判断标题是否为空
    	if(obj.getDefName()!=null && !"".equals(obj.getDefName())){
    		
    		PreparedStatement ps = null;
    		try {
                int i = 1; 
				ps = conn.prepareStatement("insert into k_setdef (defName,defType,dictype)" +
						"values(?,?,?)");
				
				ps.setString(i++,obj.getDefName());
			//	ps.setString(i++, obj.getDefValue());
				ps.setString(i++, obj.getDefType());
				ps.setString(i++, obj.getDicType().trim());
				
				ps.execute();
				
				return true;
				
			} catch (Exception e) {
				Debug.print(Debug.iError, "添加自定义出错！", e);
				throw new MatechException("添加失败：" + e.getMessage(), e);
			} finally {
	            
				DbUtil.close(ps);
			}
    		
    	}
    	
    	return false;
    }
/**
 * 删除自定义的方法
 * @param args
 */
    public boolean remove(String autoid ) throws Exception{
    	
    	DbUtil.checkConn(conn);
    	//判断编号是否为空
		if (autoid!= null && !"".equals(autoid)) {

			PreparedStatement ps = null;
			ResultSet rs = null;
			try {

				
				String getnamesql = "select defName,defType from k_setdef where autoid = ?";
				ps = conn.prepareStatement(getnamesql);
				ps.setString(1, autoid);
				rs = ps.executeQuery();
				rs.next();
				String defname = rs.getString("defName");
				String deftype = rs.getString("defType");
				rs.close();
				
				
				ps = conn.prepareStatement("delete from k_setdef where autoid=?");
				ps.setString(1, autoid);
				ps.execute();
				
				ps = conn.prepareStatement("delete from k_userdef where name=? and property=? or contrastid ='common'");
				ps.setString(1, defname);
				ps.setString(2, "com_"+deftype);
				ps.execute();
				
				return true;
				
			} catch (Exception e) {
				Debug.print(Debug.iError, "删除自定义出错！", e);
				throw new MatechException("删除失败：" + e.getMessage(), e);
			} finally {
	            
				DbUtil.close(ps);
			}
		}
    	return false;
    }
/**
 * 修改自定义的方法
 * @param args
 */    
	public boolean update(SetdefObject obj) throws Exception{
		
		DbUtil.checkConn(conn);
		//判断编号是否为空
		if(obj.getDefName()!=null && !"".equals(obj.getDefName())){
			
			PreparedStatement ps = null;		
			PreparedStatement ps2 = null;
			ResultSet rs = null;
			   										
			try {
	            int i = 1; 
	            
	            String autoid = obj.getAutoid();
	            
	            String getnamesql = "select defName from k_setdef where autoid = ?";
				ps = conn.prepareStatement(getnamesql);
				ps.setString(1, autoid);
				rs = ps.executeQuery();
				rs.next();
				String defname = rs.getString("defName");
				rs.close();
	            
				String sql = "update k_setdef set defName = ?,defType = ?,dicType = ? where autoid = ?";
				ps = conn.prepareStatement(sql);				
				ps.setString(i++,obj.getDefName());
		//		ps.setString(i++, obj.getDefValue());
				ps.setString(i++, obj.getDefType());
				ps.setString(i++, obj.getDicType());
				ps.setString(i++, autoid);
				ps.execute();
							
				String updatedef = "update k_userdef set name = ?,value='' where property ='com_"+obj.getDefType()+"' and name =?";
				ps2 = conn.prepareStatement(updatedef);
				ps2.setString(1, obj.getDefName());
				ps2.setString(2, defname);
				ps2.execute();
				
				
				return true;
																		
			} catch (Exception e) {
				Debug.print(Debug.iError, "修改自定义出错！", e);
				throw new MatechException("修改失败：" + e.getMessage(), e);
			} finally {
				DbUtil.close(ps);
				DbUtil.close(ps2);
			}
			
		}
		   	
		return false;
	}

/**
 * 获得自定义的方法
 * @param args
 */ 
    public SetdefObject getDocumentByAutoid(String autoid ) throws Exception{
    	
    	DbUtil.checkConn(conn);
    	
		PreparedStatement ps = null;
		ResultSet rs = null; 
		
		SetdefObject obj = new SetdefObject();	
			
		try {
			//判断编号是否为空		
			if (autoid!=null&&!"".equals(autoid)) {
				
				ps = conn.prepareStatement("select * from k_setdef where autoid=?");
				ps.setString(1,autoid);
				
				rs = ps.executeQuery();
			
				if(rs.next()){
																	
					obj.setAutoid(rs.getString("autoid"));
					obj.setDefName(rs.getString("defName"));
			//		obj.setDefValue(rs.getString("defValue"));
					obj.setDefType(rs.getString("defType"));
					obj.setDicType(rs.getString("dicType"));
				}									
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问出错！", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
            
			DbUtil.close(ps);
		}						
			
		return obj;
		
    }
    
    
	/**
	 *验证自定义名称是否存在的方法 
	 */
	public boolean SelectName(String defName, String defType) throws MatechException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "";
		//判断自定义的类别
		if ("user".equals(defType) && defType != null) {
			sql = "select defName from k_setdef where defType='"+defType+ "' and defName='"+defName+"'";
		} else if ("cust".equals(defType) && defType != null) {
			sql = "select defName from k_setdef where defType='"+defType+ "' and defName='"+defName+"'";
		} else if ("depart".equals(defType) && defType != null) {
			sql = "select defName from k_setdef where defType='"+defType+ "' and defName='"+defName+"'";
		}else if ("proj".equals(defType) && defType != null) {
			sql = "select defName from k_setdef where defType='"+defType+ "' and defName='"+defName+"'";
		}

		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()){
				return false;
			}
										
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return true;
	}
	
	
	/**
	 * 获得自定义列表的方法
	 * @param defType
	 * @return
	 */
	public List getSetValueList(String defType){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		
		List list = new ArrayList();
		
		try {
			
			String defsql = "select defName from k_setdef where defType=?";
			ps = conn.prepareStatement(defsql);
			ps.setString(1, defType);
			
			rs = ps.executeQuery(); 
						
			SetdefObject object ;
			
			while(rs.next()){
				
				object = new SetdefObject();
				
				object.setDefName(rs.getString(1));
//				object.setDefValue(rs2.getString(1));
				
				list.add(object);
					
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return list;
	}
	
	
	/**
	 * 获得自定义列表的方法
	 * @param defType
	 * @return
	 */
	public List getSetValueList(String defType,String contrastid,String property){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		List list = new ArrayList();
		
		try {
			
			String defsql = "select defName,dictype from k_setdef where defType=?";
			ps = conn.prepareStatement(defsql);
			ps.setString(1, defType);
			
			rs = ps.executeQuery(); 
				
			
			
			SetdefObject object ;
			
			while(rs.next()){
				
				object = new SetdefObject();
				String name = rs.getString("defName");
				String dictype = rs.getString("dictype");
				object.setDefName(name);
				object.setDicType(dictype);
				
				String commondefsql = "select value from k_userdef where contrastid=? and property=? and name=?";
				ps2 = conn.prepareStatement(commondefsql);
				ps2.setString(1, contrastid);
				ps2.setString(2,property );
				ps2.setString(3, name);
			
				rs2 = ps2.executeQuery(); 
				if(rs2.next()){
					object.setDefValue(rs2.getString(1));
				}else{
					object.setDefValue("");
				}
								
				list.add(object);
					
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(rs2);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}

		return list;
	}
	
    /**
     * main方法,用于测试
     *
     * @param args
     */
    public static void main(String args[]) throws Exception {

    	Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			
			} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
    	
    }
}
