package com.matech.audit.service.investManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.investManage.model.InvestManage;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class InvestManageService {
	private Connection conn = null;
	public InvestManageService(Connection conn) {
		this.conn = conn;
	}


	/**
	 * 根据autoId得到 单个 投资(invest)情况
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public InvestManage getInvestManageByAutoId(String autoId) throws Exception{
		InvestManage  im = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			im = new InvestManage();
			String sql = " select autoId,loginid,loginname,setTime,investId,userName,relations,answer, " 
				       + " ssStockNum,ssStockNum2,hsStockNum,hsStockNum2,gsstockNum,stockCode,stockName,stockCount, "
				       + " stockInDate,stockOutDate,property "
				       + " from K_investManage where autoid = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, autoId);
			rs = ps.executeQuery();
			if(rs.next()){
				im.setAutoId(rs.getString("autoId"));
				im.setLoginId(rs.getString("loginId"));
				im.setLoginName(rs.getString("loginName"));
				im.setSetTime(rs.getString("setTime"));
				im.setInvestId(rs.getString("investId"));
				im.setUserName(rs.getString("userName"));
				im.setRelations(rs.getString("relations"));
				im.setAnswer(rs.getString("answer"));
				
				im.setSsstockNum(rs.getString("ssstockNum"));
				im.setSsstockNum2(rs.getString("ssstockNum2"));
				im.setHsstockNum(rs.getString("hsstockNum"));
				im.setHsstockNum2(rs.getString("hsstockNum2"));
				im.setGsstockNum(rs.getString("gsstockNum"));
				im.setStockCode(rs.getString("stockCode"));
				im.setStockName(rs.getString("stockName"));
				im.setStockCount(rs.getString("stockCount"));
				
				im.setStockInDate(rs.getString("stockInDate"));
				im.setStockOutDate(rs.getString("stockOutDate"));
				im.setProperty(rs.getString("property"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return im;
	}
	
	
	/**
	 * 根据investId得到 list 投资(invest)情况
	 * @param investId
	 * @return
	 * @throws Exception
	 */
	public List getInvestManageByInvestId(String investId) throws Exception{
		List list = null;
		InvestManage  im = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			list = new ArrayList();
			String sql = " select autoId,loginid,loginname,setTime,investId,userName,relations,answer, " 
				       + " ssStockNum,ssStockNum2,hsStockNum,hsStockNum2,gsstockNum,stockCode,stockName,stockCount, "
				       + " stockInDate,stockOutDate,property "
				       + " from K_investManage where investId = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, investId);
			rs = ps.executeQuery();
			while(rs.next()){
				
				im = new InvestManage();
				
				im.setAutoId(rs.getString("autoId"));
				im.setLoginId(rs.getString("loginId"));
				im.setLoginName(rs.getString("loginName"));
				im.setSetTime(rs.getString("setTime"));
				im.setInvestId(rs.getString("investId"));
				im.setUserName(rs.getString("userName"));
				im.setRelations(rs.getString("relations"));
				im.setAnswer(rs.getString("answer"));
				
				im.setSsstockNum(rs.getString("ssstockNum"));
				im.setSsstockNum2(rs.getString("ssstockNum2"));
				im.setHsstockNum(rs.getString("hsstockNum"));
				im.setHsstockNum2(rs.getString("hsstockNum2"));
				im.setGsstockNum(rs.getString("gsstockNum"));
				im.setStockCode(rs.getString("stockCode"));
				im.setStockName(rs.getString("stockName"));
				im.setStockCount(rs.getString("stockCount"));
				
				im.setStockInDate(rs.getString("stockInDate"));
				im.setStockOutDate(rs.getString("stockOutDate"));
				im.setProperty(rs.getString("property"));
				
				list.add(im);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	
	 
	/**新增 投资(invest)情况
	 * @param im
	 * @throws Exception
	 */
	public void addInvestManage(InvestManage im) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into K_investManage (loginid,loginname,setTime,investId,userName," 
					   + " relations,answer,ssStockNum,ssStockNum2,hsStockNum, " 
				       + " hsStockNum2,gsstockNum,stockCode,stockName,stockCount, "
				       + " stockInDate,stockOutDate,property )"
				      +  " values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?)";
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, im.getLoginId());
			ps.setString(i++, im.getLoginName());
			ps.setString(i++, im.getSetTime());
			ps.setString(i++, im.getInvestId());
			ps.setString(i++, im.getUserName());
			
			ps.setString(i++, im.getRelations());
			ps.setString(i++, im.getAnswer());
			ps.setString(i++, im.getSsstockNum());
			ps.setString(i++, im.getSsstockNum2());
			ps.setString(i++, im.getHsstockNum());
			
			ps.setString(i++, im.getHsstockNum2());
			ps.setString(i++, im.getGsstockNum());
			ps.setString(i++, im.getStockCode());
			ps.setString(i++, im.getStockName());
			ps.setString(i++, im.getStockCount());
			
			ps.setString(i++, im.getStockInDate());
			ps.setString(i++, im.getStockOutDate());
			ps.setString(i++, im.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	
	 
	/** 修改投资(invest)情况
	 * @param im
	 * @throws Exception
	 */
	public void updateInvestManage(InvestManage im) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = " update K_investManage set userName=?,relations=?,answer=?,ssStockNum=?,ssStockNum2=?," 
				       + " hsStockNum=?,hsStockNum2=?,gsstockNum=?,stockCode=?,stockName=?,"
				       + " stockCount=?,stockInDate=?,stockOutDate=?"
				       + " where autoid=? ";
			ps = conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, im.getUserName());
			ps.setString(i++, im.getRelations());
			ps.setString(i++, im.getAnswer());
			ps.setString(i++, im.getSsstockNum());
			ps.setString(i++, im.getSsstockNum2());
			
			ps.setString(i++, im.getHsstockNum());
			ps.setString(i++, im.getHsstockNum2());
			ps.setString(i++, im.getGsstockNum());
			ps.setString(i++, im.getStockCode());
			ps.setString(i++, im.getStockName());
			
			ps.setString(i++, im.getStockCount());
			ps.setString(i++, im.getStockInDate());
			ps.setString(i++, im.getStockOutDate());
			
			ps.setString(i++, im.getAutoId());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	
	 
	/** 删除 投资(invest)情况
	 * @param autoId
	 * @throws Exception
	 */
	public void deleteInvestManage(String autoId) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = " delete from K_investManage where autoid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoId);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/** 删除 投资(invest)情况
	 * @param autoId
	 * @throws Exception
	 */
	public void deleteInvestManageByInvestId(String investId) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = " delete from K_investManage where investId = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, investId);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	//加方法
	/**
	 * 通过股票代码，查看有无还在持有(卖出日期为空) 的人员
	 * 有，就给该人员发出以下消息
	 * @param stockCode 股票代码
	 * @param map 消息内容
	 * @throws Exception
	 */
	public void getInvestByStockCode(String stockCode,Map map) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			ASFuntion CHF = new ASFuntion();
			String userid = (String)map.get("userid"); //当前登录人
			String customerid = (String)map.get("customerid"); //股票代码 所在的客户名称
			
			if("".equals(CHF.showNull(stockCode))) return; //股票代码为空，不用检查
			
			String customername = "",departmentname = "";
			sql = "select a.departname as customername ,b.departname " +
			"	from k_customer a " +
			"	left join k_department b on a.departmentid = b.autoid " +
			"	where a.departid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);
			rs = ps.executeQuery();
			if(rs.next()){
				customername = CHF.showNull(rs.getString("customername"));
				departmentname = CHF.showNull(rs.getString("departname"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			PlacardService placardService=new PlacardService(conn); 
			PlacardTable placardTable=new PlacardTable(); 
			placardTable.setAddresser(userid);//发起
			placardTable.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
			placardTable.setIsRead(0);
			placardTable.setIsReversion(0);
			placardTable.setIsNotReversion(0);
			placardTable.setCaption("客户独立性检查");
			String sbString= "";
						
			//有买股票并还没有卖出
			sql = "select * from k_investManage " +
			"	where stockCode = ? " +
			"	and ifnull(answer,'') = '是' " +
			"	and ifnull(stockOutDate,'')= ''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, stockCode);
			rs = ps.executeQuery();
			while(rs.next()){
				
				String loginid = rs.getString("loginid"); //所内用户
				String username = rs.getString("username"); //股票拥有者
				String relations = rs.getString("relations"); //选择自己是事务所谁的XXX关系
				String str = "（"+relations+"："+username+"）";
				if("本人".equals(relations)){
					str = "";
				}
				
				sbString = customername+"公司（股票代码："+stockCode+"）" +
				"已经于"+CHF.getCurrentDate()+"成为我公司"+departmentname+"的委托客户，" +
				"鉴于准则独立性要求，您"+str+"需要在收到本通知的一个月内卖出相关股票，请遵照执行。";
				
				placardTable.setMatter(sbString);
				placardTable.setAddressee(loginid); //接收的老大UserId
				placardService.AddPlacard(placardTable);
	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
}
