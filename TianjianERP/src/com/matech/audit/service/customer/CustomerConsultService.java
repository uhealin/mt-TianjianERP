package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.matech.audit.service.customer.model.ConsultTxt;
import com.matech.audit.service.customer.model.CustomerConsult;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CustomerConsultService {
	private Connection conn = null;
	ASFuntion CHF = new ASFuntion();

	public CustomerConsultService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 增加客户接洽追踪记录
	 * 
	 * @param customerTrack
	 * @return
	 */
	public boolean addCustomerConsult(CustomerConsult customerConsult,String customerId)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "insert into oa_customerconsult (customerid,linkMan,qq,phone,EMAIL,customerName,visitTime," 
					   + "problem,state,dealTime,filename,finishRecode,unfinishProblem,unfinishDepart," 
					   + "unfinishMan,untillTime,recoder,recodeTime,dealman) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);

			int i = 1;
			ps.setString(i++, customerId);
			ps.setString(i++, customerConsult.getLinkMan());
			ps.setString(i++, customerConsult.getQQ());
			ps.setString(i++, customerConsult.getPHONE());
			ps.setString(i++, customerConsult.getEMAIL());
			ps.setString(i++, customerConsult.getCustomerName());
			ps.setString(i++, customerConsult.getVisitTime());
			ps.setString(i++, customerConsult.getProblem());
			ps.setString(i++, customerConsult.getState());
			ps.setString(i++, customerConsult.getDealTime());
			ps.setString(i++, customerConsult.getFilename());
			ps.setString(i++, customerConsult.getFinishRecode());
			ps.setString(i++, customerConsult.getUnfinishProblem());
			ps.setString(i++, customerConsult.getUnfinishDepart());
			ps.setString(i++, customerConsult.getUnfinishMan());
			ps.setString(i++, customerConsult.getUntillTime());
			ps.setString(i++, customerConsult.getRecoder());
			ps.setString(i++, customerConsult.getRecodeTime());
			ps.setString(i++, customerConsult.getFinishMan());
			
			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;
	}
	
	/**
	 * 修改客户追踪记录
	 * @param customerConsult
	 * @param autoid
	 * @return
	 */
	public boolean updateCustomerTrack(CustomerConsult customerConsult ,String autoid)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "update oa_customerconsult set linkMan=?,QQ=?,PHONE=?,EMAIL=?,customerName=?," 
					   + "visitTime=?,problem=?,state=?,dealTime=?,finishRecode=?,unfinishProblem=?,unfinishDepart=?," 
					   + "unfinishMan=?,untillTime=?,recoder=?,recodeTime=?,dealman=? where autoid = ?";

			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, customerConsult.getLinkMan());
			ps.setString(i++, customerConsult.getQQ());
			ps.setString(i++, customerConsult.getPHONE());
			ps.setString(i++, customerConsult.getEMAIL());
			ps.setString(i++, customerConsult.getCustomerName());
			ps.setString(i++, customerConsult.getVisitTime());
			ps.setString(i++, customerConsult.getProblem());
			ps.setString(i++, customerConsult.getState());
			ps.setString(i++, customerConsult.getDealTime());
			ps.setString(i++, customerConsult.getFinishRecode());
			ps.setString(i++, customerConsult.getUnfinishProblem());
			ps.setString(i++, customerConsult.getUnfinishDepart());
			ps.setString(i++, customerConsult.getUnfinishMan());
			ps.setString(i++, customerConsult.getUntillTime());
			ps.setString(i++, customerConsult.getRecoder());
			ps.setString(i++, customerConsult.getRecodeTime());
			ps.setString(i++, customerConsult.getFinishMan());
			ps.setString(i++, autoid);
			
			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;
	}
	
	
	/**
	 * 删除客户追踪记录
	 * 
	 * @param autoid
	 * @return
	 */
	public boolean removeCustomerTrack(String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "delete from oa_customerconsult where autoid = ?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);

			ps.execute();
			ps.close();
			
			sql = "delete from oa_consulttxt where recordid = ?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);

			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;
	}

	/**
	 * 获得客户追踪记录
	 * 
	 * @param autoid
	 * @return
	 */
	public CustomerConsult getCustomerTrack(String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;

		CustomerConsult customerConsult = new CustomerConsult();

		try {
			String sql = " select * from oa_customerconsult where autoid = ?";

			ps = conn.prepareStatement(sql);
			 
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			
			while(rs.next()){
				customerConsult.setLinkMan(CHF.showNull(rs.getString("linkman")));		
				customerConsult.setQQ(CHF.showNull(rs.getString("QQ")));
				customerConsult.setPHONE(CHF.showNull(rs.getString("PHONE")));
				customerConsult.setEMAIL(CHF.showNull(rs.getString("EMAIL")));
				customerConsult.setCustomerid(CHF.showNull(rs.getString("customerid")));
				customerConsult.setCustomerName(CHF.showNull(rs.getString("customerName")));
				customerConsult.setVisitTime(CHF.showNull(rs.getString("visitTime")));
				customerConsult.setProblem(CHF.showNull(rs.getString("problem")));
				customerConsult.setState(CHF.showNull(rs.getString("state")));
				customerConsult.setFinishMan(CHF.showNull(rs.getString("dealMan")));
				customerConsult.setDealTime(CHF.showNull(rs.getString("dealTime")));
				customerConsult.setFinishRecode(CHF.showNull(rs.getString("finishRecode")));
				customerConsult.setUnfinishProblem(CHF.showNull(rs.getString("unfinishProblem")));
				customerConsult.setUnfinishDepart(CHF.showNull(rs.getString("unfinishDepart")));
				customerConsult.setUnfinishMan(CHF.showNull(rs.getString("unfinishMan")));
				customerConsult.setUntillTime(CHF.showNull(rs.getString("untillTime")));
				customerConsult.setRecoder(CHF.showNull(rs.getString("recoder")));
				customerConsult.setRecodeTime(CHF.showNull(rs.getString("recodeTime")));
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return customerConsult;
	}
	
	/**
	 * 获得最大的AutoId
	 * @return
	 * @throws Exception
	 */
	public String getMaxAutoId()throws Exception {
		DbUtil.checkConn(conn);
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String autoid = "";
		try {
			String sql = "select max(autoid) as autoid from oa_customerconsult";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()){
				autoid = rs.getString("autoid");
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return autoid;	
	}
	
	/**
	 * 增加解决记录
	 * @return
	 * @throws Exception
	 */
	public boolean addCousultTxt(ConsultTxt consultTxt )throws Exception {
		DbUtil.checkConn(conn);
	
		PreparedStatement ps = null;

		try {
			String sql = "insert into oa_consulttxt(customerid,recordid,recordconent,department,person,untilltime,manager,state)"
				       + "values(?,?,?,?,?,?,?,?)";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, consultTxt.getCustomerId());
			ps.setString(2, consultTxt.getRecordId());
			ps.setString(3, consultTxt.getRecordContent());
			ps.setString(4, consultTxt.getDepartment());
			ps.setString(5, consultTxt.getPerson());
			ps.setString(6, consultTxt.getUntillTime());
			ps.setString(7, consultTxt.getManager());
			ps.setString(8, consultTxt.getState());
			
			ps.execute();
					
			return true;
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
		return false;

	}
	
	/**
	 * 获得解决历史记录
	 * @param customerid
	 * @param recordid
	 * @return
	 * @throws Exception
	 */
	public List getConsultTxts(String customerid,String recordid)throws Exception {
		DbUtil.checkConn(conn);
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List consultTxts = new ArrayList();

		try {
			String sql = "select a.state,a.recordconent,c.departName as department,a.person,a.untilltime,b.name as manager " +
						"from oa_consulttxt a " +
						"left join k_user b on a.manager = b.id "+
						"left join k_department c on a.department = c.autoId "+
						"where a.customerid=? and a.recordid=? order by a.autoid";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);
			ps.setString(2, recordid);
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				
				ConsultTxt consultTxt = new ConsultTxt();
				
				consultTxt.setState(CHF.showNull(rs.getString("state")));
				consultTxt.setRecordContent(CHF.showNull(rs.getString("recordconent")));
				consultTxt.setDepartment(CHF.showNull(rs.getString("department")));
				consultTxt.setPerson(CHF.showNull(rs.getString("person")));
				consultTxt.setUntillTime(CHF.showNull(rs.getString("untilltime")));
				consultTxt.setManager(CHF.showNull(rs.getString("manager")));
				
				consultTxts.add(consultTxt);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}	
		
		return consultTxts;
	}
	
	/**
	 * 自动填充客户信息
	 * @param tel
	 * @param qq
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public String autoFill(String tel) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//接洽表 或客户信息表 或客户股东表或中介机构表、专家顾问表
			
			//接洽表
			String sql = "select customerid,customerName,linkMan,QQ,EMAIL from oa_customerconsult where PHONE = '" + tel + "'";
			System.out.println("sql:" + sql);
			ps = conn.prepareStatement(sql); 
			rs = ps.executeQuery();
			if(rs.next()){
				String customerInfo = "";
				customerInfo += rs.getString(1) + "@`@";
				customerInfo += rs.getString(2) + "@`@";
				customerInfo += rs.getString(3) + "@`@";
				customerInfo += rs.getString(4) + "@`@";
				customerInfo += rs.getString(5);
				return customerInfo;
			}else{
				DbUtil.close(rs);
				DbUtil.close(ps);
				//客户信息表
				sql = "SELECT departid AS customerid,departName AS customerName,linkMan,'' AS QQ,EMAIL FROM asdb.k_customer WHERE PHONE ='" + tel + "'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					String customerInfo = "";
					customerInfo += rs.getString(1) + "@`@";
					customerInfo += rs.getString(2) + "@`@";
					customerInfo += rs.getString(3) + "@`@";
					customerInfo += rs.getString(4) + "@`@";
					customerInfo += rs.getString(5);
					return customerInfo;
				}else{
					DbUtil.close(rs);
					DbUtil.close(ps);
					//客户股东表
					
					//中介机构表
					sql = "SELECT DISTINCT customerid,b.departName AS customerName,a.linkMan,'' AS QQ,a.mailman AS EMAIL FROM k_agency a,k_customer b WHERE a.customerid = b.departid and a.PHONE ='" + tel + "'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						String customerInfo = "";
						customerInfo += rs.getString(1) + "@`@";
						customerInfo += rs.getString(2) + "@`@";
						customerInfo += rs.getString(3) + "@`@";
						customerInfo += rs.getString(4) + "@`@";
						customerInfo += rs.getString(5);
						return customerInfo;
					}else{
						//专家顾问表
						sql = "SELECT DISTINCT customerid,b.departName AS customerName,a.advisername AS linkMan,'' AS QQ,a.mailbox AS EMAIL FROM k_adviser a,k_customer b WHERE a.customerid = b.departid and a.PHONE ='" + tel + "'";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if(rs.next()){
							String customerInfo = "";
							customerInfo += rs.getString(1) + "@`@";
							customerInfo += rs.getString(2) + "@`@";
							customerInfo += rs.getString(3) + "@`@";
							customerInfo += rs.getString(4) + "@`@";
							customerInfo += rs.getString(5);
							return customerInfo;
						}
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}	
		return null;
	}
	
	/**
	 * 根据条件查询 客户信息
	 * @param ctype
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public String customerFill(String ctype,String number) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String customerId = "";
		try {
			
			//客户联系人表
			String sql = "SELECT customerid FROM k_manager  WHERE "+ctype+"='"+number+"'";
			
			if(ctype.equals("fixedphone")){
				sql +="or mobilephone="+number;
			}
			System.out.println("customerFillSql="+sql);
			
			ps = conn.prepareStatement(sql); 
			rs = ps.executeQuery();
			if(rs.next()){
				customerId+=rs.getString(1)+",";
			}
			
			if(!"".equals(customerId)){
				
				String[] departId = customerId.split(","); //如果有多个，就取第一个
				
				sql = "SELECT * FROM ( \n"
					  +"SELECT a.customerId,a.departName AS customerName,ifnull(a.name,'') as name,"
					  	+	"ifnull(b.departName,'') as departName,a.user1 as userId,a.departmentid FROM ( \n"
						+"	SELECT a.departId as customerId,a.departName,b.user1,c.name,c.departmentid   \n"
						+"	FROM k_customer a  \n"
						+"	LEFT JOIN k_customermanager b ON a.departId = b.customerid \n"
						+"	LEFT JOIN k_user c ON b.user1 = c.id \n"
						+"	WHERE 1=1  AND a.departId = '"+departId[0]+"' \n"
						+") a \n"
						+"LEFT JOIN k_department b ON a.departmentId = b.autoId \n"
						+") a";
				System.out.println("22222222222Sql="+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					String customerInfo = "";
					customerInfo += rs.getString(1) + "@`@";
					customerInfo += rs.getString(2) + "@`@";
					customerInfo += rs.getString(3) + "@`@";
					customerInfo += rs.getString(4) + "@`@";
					customerInfo += rs.getString(5) + "@`@";
					customerInfo += rs.getString(6);
					
					return customerInfo;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}	
		return null;
	}
	
	
	
	/**
	 * 把Name转换Id
	 * @param customerid
	 * @param recordid
	 * @return
	 * @throws Exception
	 */
	public String getUserByName(String name)throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String id="";
		try {
			String sql = "select id from k_user where `name`='"+name+"'	";		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();			
			if(rs.next()){
				 id=rs.getString("id");
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}	
		
		return id;
	}
	
	
	
	/**
	 * 根据客户名称找到总负责人的Id
	 * @param customerid
	 * @param recordid
	 * @return
	 * @throws Exception
	 */
	public String getCustomerBydepartName(String departName)throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String mostly="";
		try {
			String sql = " select b.id from k_customer a"+
						 " left join k_user b on a.mostly=b.Name"+
						 " where a.DepartID='"+departName+"'";		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();			
			if(rs.next()){
				mostly=rs.getString("b.id");
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}	
		
		return mostly;
	}
	
	
	/**
	 * 客户ID转换客户名称
	 * @param customerid
	 * @param recordid
	 * @throws Exception
	 */
	public String getCustomerIdTransformDepartName(String DepartID)throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String DepartName="";
		try {
			String sql = " SELECT DepartName FROM k_customer WHERE DepartID='"+DepartID+"'";		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();			
			if(rs.next()){
				DepartName=rs.getString("DepartName");
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}	
		
		return DepartName;
	}
}
