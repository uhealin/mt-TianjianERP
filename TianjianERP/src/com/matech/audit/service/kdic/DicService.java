package com.matech.audit.service.kdic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.kdic.model.Dic;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class DicService {
	
	private Connection conn = null;

	public DicService(Connection conn) {

		this.conn = conn;
	}
	
	
	
	/**
	 * 添加
	 * @param dic
	 * @return
	 * @throws Exception
	 */
	public boolean add(Dic dic) throws Exception {
		DbUtil.checkConn(conn) ;
		PreparedStatement ps = null ;
		
		try {
		String sql = "insert into k_dic(Name,Value,ctype,userdata,property)" 
			         +"values(?,?,?,?,?)";
		
		ps = conn.prepareStatement(sql) ;
		
		ps.setString(1,dic.getName()) ;
		ps.setString(2,dic.getValue()) ;
		ps.setString(3,dic.getCtype()) ;
		ps.setString(4,"1") ;
		ps.setString(5,dic.getProperty()) ;
		
		ps.execute();
		
		return true ;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(ps) ;
		}
		return false ;
	}
	
	
	/**
	 * 修改
	 * @param dic
	 * @return
	 * @throws Exception
	 */
	public boolean update(Dic dic) throws Exception {
		DbUtil.checkConn(conn) ;
		PreparedStatement ps = null ;
		
		try {
		String sql = "update k_dic set Name=?,Value=?,ctype=?,property=? where autoId=?"  ;
			         
		
		ps = conn.prepareStatement(sql) ;
		
		ps.setString(1,dic.getName()) ;
		ps.setString(2,dic.getValue()) ;
		ps.setString(3,dic.getCtype()) ;
		ps.setString(4,dic.getProperty()) ;
		ps.setString(5,dic.getAutoId());
		
		ps.execute();
		
		return true ;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(ps) ;
		}
		return false ;
	}
	
	/**
	 * 删除
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String autoId) throws Exception {
		DbUtil.checkConn(conn) ;
		PreparedStatement ps = null ;
		
		try {
		String sql = "delete from k_dic where autoId =?"  ;
			         
		
		ps = conn.prepareStatement(sql) ;
		
		ps.setString(1,autoId) ;
		
		ps.execute();
		
		return true ;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(ps) ;
		}
		return false ;
	}
	

	/**
	 * 导 EXCEL 数据到 临时表里面
	 * @throws MatechException
	 */
    public void newTable() throws MatechException {
		dropTable();
		String sql = "CREATE TABLE tt_k_performance like k_performance";
		PreparedStatement ps = null;
		try {

			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}
	
    /**
     * 删除临时表
     * @throws MatechException
     */
    public void dropTable() throws MatechException {
		String sql = "DROP TABLE IF EXISTS tt_k_performance";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}
    
    
    /**
     * 删除记录
     * @throws MatechException
     */
    public void del() throws MatechException {
		String sql = " delete from tt_k_performance "
				   + " where ifnull(excellence,'')='' "
				   + " and ifnull(favorable,'')='' "
				   + " and ifnull(average,'')='' "
				   + " and ifnull(lower,'')='' "
				   + " and ifnull(short,'')='' ";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}
    
    
    /**
     * 删除物理表里面的 该 年份 已经存在 的 记录
     * @throws MatechException
     */
    public void delData() throws MatechException {
		String sql = " delete a from k_performance a ,(select distinct year from tt_k_performance ) b where a.year = b.year ";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}
    
    /**
     * 把 临时表里面的数据 挪到 物理表里面
     * @throws MatechException
     */
    public void insertData()throws MatechException{
		PreparedStatement ps = null;
		try {
			String sql = " insert into k_performance( " 
					   + " 		orderId,year,vocation,scale,project," 
					   + " 		excellence,favorable,average,lower,short ) " 
					   + " select orderId,year,vocation,scale,project, " 
					   + " 		excellence,favorable,average,lower,short " 
					   + " 		from tt_k_performance order by orderid ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("Flush tables");
			DbUtil.close(ps);
			
			// 删除临时表
			dropTable(); 
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
	}
    
    /**
     * k_dic 的树
     * @param parentids
     * @return
     * @throws Exception
     */
    public String getTree() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		String id; //编号
		String typeName;  //名称
		try {
			
			String sql="SELECT DISTINCT ctype FROM k_dic WHERE userdata=1 ";
			
			ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			
			sb.append("[");
			while (rs.next()) {
				id= rs.getString("ctype");
				typeName= rs.getString("ctype");
				
				sb.append("{");
				sb.append("id:'"+id+"',");
				sb.append("text:'"+typeName+"',");
				sb.append("leaf:"+true);
				 
				sb.append("}");
				if(!rs.isLast()) {
					sb.append(",");
				}
				
			}
			sb.append("]");
			System.out.println(sb);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return sb.toString() ;
	}
}
