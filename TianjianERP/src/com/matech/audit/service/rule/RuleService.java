package com.matech.audit.service.rule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.rule.model.RuleTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class RuleService {

	private Connection conn = null;

	public RuleService(Connection conn) {
		this.conn = conn;
	}
/**
 * 添加记录
 * @param rt
 * @throws Exception
 */
	public void add(RuleTable rt) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
	
			int i = 1;
			String sql = "insert into k_rule (autoid,title, ctype, memo ,content, orderid, property,refer1,refer2)values (?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setInt(i++, rt.getAutoid());
			ps.setString(i++, rt.getTitle());
			ps.setString(i++, rt.getType());
			ps.setString(i++, rt.getMemo());
			ps.setString(i++, rt.getContent());
			ps.setDouble(i++, rt.getOrderid());
			ps.setString(i++, rt.getProperty());
			
			ps.setString(i++, rt.getRefer1());
			ps.setString(i++, rt.getRefer2());

			ps.execute();
			ps.execute("Flush tables");
		} catch (Exception e) {
			Debug.print(Debug.iError, "添加记录失败！", e);
			throw new MatechException("添加记录失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

/**
 * 修改记录
 * @param rt
 * @throws Exception
 */
	public void edit(RuleTable rt) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			int i = 1;

			String sql = "update k_rule set title = ?, ctype =?, memo =?, content =?, orderid =?, property =? where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, rt.getTitle());
			ps.setString(i++, rt.getType());
			ps.setString(i++, rt.getMemo());
			ps.setString(i++, rt.getContent());
			ps.setDouble(i++, rt.getOrderid());
			ps.setString(i++, rt.getProperty());
			ps.setInt(i++, rt.getAutoid());
			ps.execute();
			ps.execute("Flush tables");
		} catch (Exception e) {
			Debug.print(Debug.iError, "修改记录失败！", e);
			throw new MatechException("修改记录失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}
/**
 * 返回RuleTable对象集合
 * @param autoid
 * @return
 * @throws Exception
 */
	public RuleTable getRules(int autoid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		RuleTable rt = new RuleTable();
		try {

			String sql = "select autoid,title,ctype,memo,content,orderid,property from k_rule where autoid="+ autoid + "";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				rt.setAutoid(Integer.parseInt(rs.getString("autoid")));
				rt.setTitle(rs.getString("title"));
				rt.setType(rs.getString("ctype"));
				rt.setMemo(rs.getString("memo"));
				rt.setContent(rs.getString("content"));
				rt.setOrderid(rs.getDouble("orderid"));
				rt.setProperty(rs.getString("property"));
			}

			return rt;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	
	/**
	 * 返回RuleTable对象集合
	 * @param ruleName 指标名称
	 * @return
	 * @throws Exception
	 */
		public RuleTable getRules(String ruleName) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			RuleTable rt = new RuleTable();
			try {

				String sql = "select autoid,title,ctype,memo,content,orderid,property,refer1 from k_rule where title="+ ruleName + "";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					rt.setAutoid(Integer.parseInt(rs.getString("autoid")));
					rt.setTitle(rs.getString("title"));
					rt.setType(rs.getString("ctype"));
					rt.setMemo(rs.getString("memo"));
					rt.setContent(rs.getString("content"));
					rt.setOrderid(rs.getDouble("orderid"));
					rt.setProperty(rs.getString("property"));
					rt.setRefer1(rs.getString("refer1"));
				}

				return rt;
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败！", e);
				throw new MatechException("访问失败！" + e.getMessage(), e);
			} finally {
				DbUtil.close(rs);
				DbUtil.close(ps);
			}

		}
/**
 * 删除记录
 * @param autoid
 * @return
 * @throws Exception
 */
	public boolean del(String autoid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {

			String sql;
			sql = "delete from k_rule where autoid='" + autoid + "' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("Flush tables");
			return true;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败！", e);
			throw new MatechException("访问失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 全元素
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
		public String  getAllElement(String title) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			if(title==null){
				title="";
			}
			String[] titles=title.split(",");
			String aaa="";
			try{
				for(int i=0;i<titles.length;i++){
					//System.out.println("yzm:titles{"+i+"]="+titles[i]);
					titles[i]=titles[i].trim();
					//System.out.println("yzm:titles{"+i+"]a="+titles[i]);
					if("".equals(titles[i]))
						continue;
					String sql="select refer1 from k_rule where title=?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, titles[i]);
					rs=ps.executeQuery();
					String refer1="";
					if(rs.next()){		
						refer1=rs.getString(1);	
					}
					//System.out.println("yzm:refer1="+refer1);
					if(refer1==null||"".equals(refer1)){
						continue;
					}
					rs.close();
					ps.close();
					//System.out.println("yzm:refer1="+refer1);
					String sql1="select ifnull(group_concat(title),'') from k_rule where autoid in ("+refer1+")";
					//System.out.println("yzm:sql1="+sql1);
					ps = conn.prepareStatement(sql1);
					rs=ps.executeQuery();
					if(rs.next()){
						aaa=aaa+getAllElement(rs.getString(1))+",";
					}else{
						break;
					}
					rs.close();
					ps.close();	
				}
				
				//System.out.println("yzm:aaa="+aaa+title);
				//System.out.println("yzm:title="+title);
				return aaa+title;
				
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败！", e);
				throw new MatechException("访问失败！" + e.getMessage(), e);
			} finally {
				DbUtil.close(ps);
			}		
		}
		/**
		 * 删除重复
		 * @param title
		 * @return
		 * @throws Exception
		 */
			public String delElements(String title) throws Exception {
				DbUtil.checkConn(conn);
				ArrayList arrayList=new ArrayList();
				if(title==null){
					title="";
				}
				String[] titles=title.split(",");
				String reTitles="";
				int bools=1;
				for(int i=0;i<titles.length;i++){	
					
					for (Iterator iter = arrayList.iterator(); iter.hasNext();) {
						String element = (String) iter.next();
						if(titles[i].equals(element)){
							bools=0;
							break;
						}
					}
					if(bools==1){
						arrayList.add(titles[i]);
					}
					bools=1;
				}
				
				for (Iterator iter = arrayList.iterator(); iter.hasNext();) {
					String element = (String) iter.next();
					reTitles=reTitles+element+",";
				} 
				if(reTitles.endsWith(",")){
				if(reTitles.equals(",")){
					
					reTitles="";
				}else{
					
					reTitles=reTitles.substring(0,reTitles.length()-1);
				}
				}
			//System.out.println("yzm:reTitles="+reTitles);
				return reTitles;
			}
			
			
			/**
			 * 根据Title求AutoId
			 * @param title
			 * @return
			 * @throws Exception
			 */
			public String getAutoIdByTitle(String title) throws Exception {
				DbUtil.checkConn(conn);
				PreparedStatement ps = null;
				ResultSet rs = null;
				if(title==null){
					title="";
				}
				String[] titles=title.split(",");
				String autoids="";
				try{
					for(int i=0;i<titles.length;i++){
						
						titles[i]=titles[i].trim();
						if("".equals(titles[i]))
							continue;
						String sql="select autoid from k_rule where title = '"+titles[i]+"'";
						ps = conn.prepareStatement(sql);
						rs=ps.executeQuery();
						String autoid="";
						if(rs.next()){		
							autoid=rs.getString(1);
							autoids=autoids+autoid+",";
						}
				
						rs.close();
						ps.close();	
					}
					
					if(autoids.endsWith(",")){
						if(autoids.equals(",")){
							
							autoids="";
						}else{
							
							autoids=autoids.substring(0,autoids.length()-1);
						}
						}
					//System.out.println("yzm:autoids="+autoids);
					//System.out.println("yzm:autoids="+autoids);
					//System.out.println("yzm:title="+title);
					return autoids;
					
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败！", e);
					throw new MatechException("访问失败！" + e.getMessage(), e);
				} finally {
					DbUtil.close(ps);
				}		
			}
			
	
	public static void main(String[] args) throws Exception{
		Connection conn=new DBConnect().getConnect("");
		RuleService ruleService=new RuleService(conn);
		//String title=ruleService.getAllElement("222,333");
		//String autoids=ruleService.getAutoIdByTitle(title);
		ruleService.delElements("222,333,333,222,");
		
	}
}
