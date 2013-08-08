package com.matech.audit.service.rule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.rule.model.RuleTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;
import com.matech.rule.Project;
import com.matech.rule.ProjectUtil;
import com.matech.rule.RulePO;

public class EdRuleService {

	private Connection conn = null;

	public EdRuleService(Connection conn) {
		this.conn = conn;
	}
/**
 * 得到规则
 * @param ruleid 指标ID
 * @throws Exception
 */
	public RulePO getRulePo(String ruleid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String ruleContent="";
		String title="";
		String refer1="";
		String autoid="";
		String strRule="";
		
		Project.regiesterMethod(
				"取值",
				"getValue",
				com.matech.audit.service.function.RuleService.class ,
				new Class[] {
					String.class,
					String.class,
					String.class,
					String.class,
					String.class,
					String.class,
					String.class
				}
	);
		try {
			 
			String sql="select refer1,autoid from k_rule where autoid="+ruleid;
			//System.out.println("yzm:sql="+sql);
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				refer1=rs.getString(1);
				autoid=rs.getString(2);
			}
			rs.close();
			ps.close();
			if("".equals(refer1)||refer1==null){
				refer1=autoid;
			}else{
				refer1 = refer1+","+autoid;
			}
			//System.out.println("yzm:refer1="+refer1);
			String[] autoids=refer1.split(",");
		for (int i = 0; i < autoids.length; i++) {
			if(autoids[0]==null||"".equals(autoids))
				continue;
			String subAutoid = autoids[i];
			String subSql="select title,content from k_rule where autoid="+subAutoid;
			
			ps=conn.prepareStatement(subSql);
			rs=ps.executeQuery();
			
			if(rs.next()){
				
				title=rs.getString(1);
				ruleContent=rs.getString(2);	
			}
			rs.close();
			ps.close();
			//System.out.println("yzm:ruleContent="+ruleContent);
			ruleContent=ruleContent.replaceAll("\\{", "(").replaceAll("\\}", ")").replaceAll(";", ",");
			//System.out.println("yzm:ruleContent="+ruleContent);
			if(i==autoids.length-1){
				strRule = strRule+"结果="+ruleContent+";\n";
			}else{
				strRule = strRule+""+title+"="+ruleContent+";\n";
			}
		}
		
		String subStrRule= "输入(\"取数方式\"); \n"
			+"输入(\"项目或帐套编号\"); \n"
			+"输入(\"年份\"); \n"
			+"输入(\"月份\"); \n"
			+"输入(\"指标属性\"); \n"
			+"输入(\"币种\"); \n"
			+"输出(\"结果\"); \n";

		strRule = subStrRule+strRule;
		//System.out.println("yzmn:strRule="+strRule);
		RulePO rpo=new RulePO();
		rpo.setId(ruleid);		
		rpo.setRule(strRule);
		rpo.setName(title);
		
		return rpo;

		} catch (Exception e) {
			Debug.print(Debug.iError, "返回规则失败！", e);
			throw new MatechException("返回规则失败！" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 得到规则结果
	 * @param ruleid 指标ID
	 * @param method 取数方式
	 * @param prjectIdOrCustomerId 项目或客户编号
	 * @param year 年份
	 * @param month 月份
	 * @param bi 币种
	 * @throws Exception
	 */
		public double getRuleResult(String ruleid,String method,String prjectIdOrCustomerId,String year,String month,String ruleProperty,String bi) throws Exception {
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			ResultSet rs = null;
			//System.out.println("yzm:prjectIdOrCustomerId="+prjectIdOrCustomerId);
			try {
				RulePO rulePO = getRulePo(ruleid);
				Map  inputs=new HashMap();
				inputs.put("取数方式",method);
				inputs.put("项目或帐套编号",prjectIdOrCustomerId);
				inputs.put("年份",year);
				inputs.put("月份",month);
				inputs.put("指标属性",ruleProperty);
				inputs.put("币种",bi);
				Project project = ProjectUtil.runRuleByPO(rulePO, inputs);
				Map outputs = project.getOutputs();

		
				
				return Double.parseDouble(outputs.get("结果")+"");
				
			} catch (Exception e) {
				Debug.print(Debug.iError, "返回规则结果失败！", e);
				throw new MatechException("返回规则结果失败！" + e.getMessage(), e);
			} finally {
				DbUtil.close(rs);
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
				//	System.out.println("yzm:titles{"+i+"]="+titles[i]);
					titles[i]=titles[i].trim();
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
				DbUtil.close(rs);
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
				//	System.out.println("yzm:title="+title);
					return autoids;
					
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败！", e);
					throw new MatechException("访问失败！" + e.getMessage(), e);
				} finally {
					DbUtil.close(rs);
					DbUtil.close(ps);
				}		
			}
			

			
			
	public static void main(String[] args) throws Exception{
		Connection conn=new DBConnect().getConnect("");
		EdRuleService ruleService=new EdRuleService(conn);
		
		//ruleService.getRuleResult("4", "取帐套", "100002", "2006", "12", "0");
		
	}
}
