package com.matech.audit.service.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class InventoryService {

	private Connection conn = null;
	
	public InventoryService(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 判断帐套有没有存货
	 * @param acc	帐套编号
	 * @return		存在返回ArrayList,不存在返回null
	 * @throws Exception
	 */
	public ArrayList isInventory(String acc) throws Exception  {
		String T1 = acc.substring(0,6);
		String BeginYear = acc.substring(6);
		String EndYear = acc.substring(6);
		return isInventory( T1, BeginYear, EndYear);
	}
	
	/**
	 * 判断帐套区间内有没有存货
	 * @param T1	客户编号
	 * @param BeginYear		开始年份
	 * @param EndYear		结束年份
	 * @return		存在返回ArrayList,不存在返回null
	 * @throws Exception
	 */
	public ArrayList isInventory(String T1,String BeginYear,String EndYear) throws Exception  {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ArrayList al = new ArrayList();
			String sql = "select distinct AccPackageID from c_inventorytype where AccPackageID >= ? and AccPackageID <= ? order by AccPackageID";
			ps = conn.prepareStatement(sql);
			ps.setString(1, T1 + BeginYear);
			ps.setString(2, T1 + EndYear);
			rs = ps.executeQuery();
			int ii = 0;
			while(rs.next()){
				al.add(rs.getString("AccPackageID"));
				ii ++;
			}
			if(ii == 0){
				return null;
			}else{
				return al;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;			
		}finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 判断帐套存货有没有用外币结算
	 * @param acc	帐套编号
	 * @return		存在返回ArrayList,不存在返回null
	 * @throws Exception
	 */
	public ArrayList isCurrency(String acc) throws Exception  {
		String T1 = acc.substring(0,6);
		String BeginYear = acc.substring(6);
		String EndYear = acc.substring(6);
		return isCurrency( T1, BeginYear, EndYear);
	}
	
	/**
	 * 判断帐套区间内存货有没有用外币结算
	 * @param T1
	 * @param BeginYear
	 * @param EndYear
	 * @return
	 * @throws Exception
	 */
	public ArrayList isCurrency(String T1,String BeginYear,String EndYear) throws Exception  {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ArrayList al = new ArrayList();
			String sql = "select distinct Currency from c_inventoryaccount where AccPackageID >= ? and AccPackageID <= ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, T1 + BeginYear);
			ps.setString(2, T1 + EndYear);
			rs = ps.executeQuery();
			int ii = 0;
			while(rs.next()){
				String s = rs.getString(1);
				if(!"".equals(s.trim())){
					al.add(s);
					ii ++;
				}
			}
			if(ii == 0){
				return null;
			}else{
				return al;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;			
		}finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 得到指定存货编号的存货全路径
	 * @param T1
	 * @param BeginYear
	 * @param EndYear
	 * @param InventoryId
	 * @return
	 * @throws Exception
	 */
	public String getFullName(String T1,String BeginYear,String EndYear,String InventoryId )throws Exception  {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_inventorytype where AccPackageID >= ? and AccPackageID <= ? and InventoryId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, T1 + BeginYear);
			ps.setString(2, T1 + EndYear);
			ps.setString(3, InventoryId);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("InventoryFullName");
			}
			
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;			
		}finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 得到指定存货编号的存货全路径
	 * @param T1
	 * @param BeginYear
	 * @param EndYear
	 * @param InventoryId
	 * @return
	 * @throws Exception
	 */
	public String getStockFullName(String T1,String BeginYear,String EndYear,String StockId )throws Exception  {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_stocktype where AccPackageID >= ? and AccPackageID <= ? and StockId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, T1 + BeginYear);
			ps.setString(2, T1 + EndYear);
			ps.setString(3, StockId);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("StockFullName");
			}
			
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;			
		}finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 *
	 * @param VoucherID String
	 * @param AccPackageID String
	 * @param projectid String
	 * @param user String
	 * @param type String
	 *    type=1 : 没有外币，没有数量。
	 *    type=2 : 有外币，没有数量。
	 *    type=1 : 没有外币，有数量。
	 * @return String
	 * @throws Exception
	 */
	public String getSortEntry(String InventoryEntryId,String InventoryDate,String InventoryInOutType,String AccPackageID,UserSession us,
			String projectid, String user, String type, String tbnfame)
			throws Exception {
		ASFuntion CHF = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "";
		int blank = 6;

		try {
			ASFuntion asf = new ASFuntion();
			new DBConnect().changeDataBase(AccPackageID.substring(0, 6), conn);
			if (!"".equals(InventoryEntryId)) {
				sql = "select "
					+" InventoryentryType,InvoiceId,InventoryEntryId,InventoryDate,concat('[',Stockid,']',StockName) as StockName,Stockid,if(InventoryInOutType=1,'入库','出库')as InventoryInOutType,InventoryId,InventoryName,InventoryfullName,InventoryType,Quantity,UomUnit,prices,OccurValue,Currency,CurrValue,subStr(InventoryDate,6,2) as submonth, \n"
					+" FillUser,AuditUser,KeepUser from c_inventoryentry where InventoryEntryId = '"+InventoryEntryId+"' and InventoryDate = '"+InventoryDate+"' and InventoryInOutType = '"+InventoryInOutType+"'  and AccPackageID = '"+AccPackageID+"'\n"
				 	+" order by Serail ";
			//	System.out.println("yzm:sql="+sql);
				        org.util.Debug.prtOut(sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				StringBuffer sbf = new StringBuffer("");
				StringBuffer sbb = new StringBuffer("");
				StringBuffer sbe = new StringBuffer("");
				int sbjID = 0;
				double sumOccurValue = 0.00;
				
				sbb.append("<tr height=18 >");
				sbb.append("<td align=\"middle\" width=\"\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\" >存货编码</td>");
				sbb.append("<td align=\"middle\" width=\"\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">存货名称</td>");
				sbb.append("<td align=\"middle\" width=\"\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">存货型号</td>");			
				sbb.append("<td align=\"middle\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">数量</td>");
				sbb.append("<td align=\"middle\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">计量单位</td>");
				sbb.append("<td align=\"middle\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">单价</td>");
				sbb.append("<td align=\"middle\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">金额</td>");
				sbb.append("<td align=\"middle\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">原币</td>");
				sbb.append("<td align=\"middle\" onmouseover=\"this.bgColor='#E4E8EF';\"  onmouseout=\"this.bgColor='#B9C4D5';\" nowrap=\"nowrap\"  bgcolor=\"#b9c4d5\">原币金额</td>");
				sbb.append("</tr>");
				
				while (rs.next()) {
					sbjID++;
				
					if(sbjID == 1){

						sbf.append("<tr height=18  bgColor=\"#FFFFFF\" >");
						sbf.append("<td align=\"left\" width=\"\">业务类型："
								+ asf.showNull(rs.getString("InventoryentryType")) + "</td>");
						sbf.append("<td align=\"left\" width=\"\">发票号:"
								+ asf.showNull(rs.getString("InvoiceId")) + "</td>");
						sbf.append("<td align=\"left\" >"+ asf.showNull(rs.getString("InventoryInOutType"))+"单号："
										+ asf.showNull(rs.getString("InventoryEntryId")+ "</td>"));			
						sbf.append("<td align=\"left\"  colspan=\"2\">"+ asf.showNull(rs.getString("InventoryInOutType"))+"日期："
								+ asf.showNull(rs.getString("InventoryDate"))+ "</td>");
						sbf.append("<td align=\"left\"  colspan=\"2\">仓库："
								+ asf.showNull(rs.getString("StockName"))+ "</td>");
						sbf.append("<td align=\"left\"  colspan=\"2\">出入库类别："
								+ asf.showNull(rs.getString("InventoryInOutType"))+ "</td>");
						sbf.append("</tr>");
						
						sbe.append("<tr height=\"20\" class=\"DGtd\">");
						sbe.append("<td height=\"22\" colspan=100 align=\"center\" bgcolor=\"#FFFFFF\"><table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">");
						sbe.append("  <tr bgcolor=\"#EEEEEE\">");
						sbe.append("    <td width=\"71\">记帐人：</td>");
						sbe.append("    <td width=\"158\" align=left>"
									+ asf.showNull(rs.getString("KeepUser")) + "</td>");
						sbe.append("    <td width=\"82\">审核人：</td>");
						sbe.append("    <td width=\"197\" align=left>"
									+ asf.showNull(rs.getString("AuditUser")) + "</td>");
						sbe.append("    <td width=\"87\">制单人：</td>");
						sbe.append("    <td width=\"150\" align=\"left\"   >"
									+ asf.showNull(rs.getString("FillUser")) + "</td>");
						sbe.append("  </tr>");
						sbe.append("</table></td>");
						sbe.append("</tr>");
					}
						sbb.append("<tr height=18 SubjectID1='"+asf.showNull(rs.getString("Stockid"))+"' AssItemID1='"+asf.showNull(rs.getString("InventoryId"))+"' BeginDate='"+asf.showNull(rs.getString("submonth"))+"' EndDate='"+asf.showNull(rs.getString("submonth"))+"'  bgColor=\"#FFFFFF\" style='cursor:hand;' onDBLclick='goSort(this);'>");
						sbb.append("<td align=\"left\" width=\"\">"
								+ asf.showNull(rs.getString("InventoryId")) + "</td>");
						sbb.append("<td align=\"left\" width=\"\">"
								+ asf.showNull(rs.getString("InventoryfullName")) + "</td>");
						sbb.append("<td align=\"left\" >"
										+ asf.showNull(rs.getString("InventoryType")+ "</td>"));			
						sbb.append(asf.showMoney(rs.getString("Quantity")));
						sbb.append("<td align=\"middle\" >"
								+ asf.showNull(rs.getString("UomUnit"))+ "</td>");
						sbb.append( asf.showMoney(rs.getString("prices")));
						sbb.append(asf.showMoney(rs.getString("OccurValue")));
						sbb.append("<td align=\"left\" width=\"\">"
								+ asf.showNull(rs.getString("Currency")) + "</td>");
						sbb.append(asf.showMoney(rs.getString("CurrValue")));
						sbb.append("</tr>");
						
						
						sumOccurValue += rs.getDouble("OccurValue");
	
				}
				
			
					sbf.append("<tr height=10  bgColor=\"#FFFFFF\"><td colspan=100><input  type=\"hidden\" name=\"sumOccurValue\" id=\"sumOccurValue\" value=\""+sumOccurValue+"\"></td></tr>");
					sbf.append(sbb.toString());
					sbf.append("<tr height=10  bgColor=\"#FFFFFF\"><td></td><td align=\"left\" >合计：</td><td></td><td></td><td></td><td></td>"+asf.showMoney(sumOccurValue+"")+"<td></td><td></td></tr>");
					
					sbf.append(sbe.toString());
					
				if (sbf.toString().equals("")) {
					sbf
							.append("<tr height=18  bgColor=\"#FFFFFF\"><td colspan=100>没有数据</td></tr>");
					sbf.append("<script>document.getElementById('" + tbnfame
							+ "').style.display='none';</script>");
				}
				return sbf.toString();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "<tr height=18  bgColor=\"#FFFFFF\"><td colspan=100>没有数据</td></tr>";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
	
	
//	输出上一页，下一页的脚本。
	public String nextPreviousPage(String opt,String apkID, String InventoryDate,String InventoryEntryId,String InventoryInOutType,String VoucherID) {
		Statement st = null; 
		ResultSet rs = null; 

		String sql1 = "";
		
		String concatString = InventoryDate+InventoryEntryId+InventoryInOutType+"";

		String result = "";
		try {

			//根据项目ＩＤ切换数据库。
			new DBConnect().changeDataBase(apkID.substring(0, 6), conn);

			 sql1 = ""
				+"select max(concat(InventoryDate,InventoryEntryId,InventoryInOutType)),min(concat(InventoryDate,InventoryEntryId,InventoryInOutType)) from c_inventoryentry \n" 
				+"where 1=1  \n" 
				+"and accpackageid='"+apkID+"'   \n"
				+"group by  accpackageid \n"
				+"order by  InventoryDate,InventoryEntryId,InventoryInOutType  \n" ;
			 
			st = conn.createStatement();

			rs = st.executeQuery(sql1);
			
			String previous = "1";
			String last = "1";
			if (rs.next()) {
				
				String max = rs.getString(1);
				String min = rs.getString(2);
				if(concatString.equals(max)){
					last = "";
				}	
				if(concatString.equals(min)){
					previous = "";
				}
			}

			rs.close();
			st.close();
		
				if ("".equals(previous)) {
					result += "<input disabled=\"true\" id=\"previousPage\" name=\"previousPage\" type=\"button\"  value=\"上一张\" class=\"flyBT\" >";
				} else {
					result += "<input  id=\"previousPage\" name=\"previousPage\" type=\"button\"  value=\"上一张\" class=\"flyBT\" onclick=\"location='SortList.jsp?AccPackageID="
							+ apkID + "&opt=1&InventoryDate=" + InventoryDate + "&InventoryInOutType=" + InventoryInOutType +"&InventoryEntryId=" + InventoryEntryId +"'\">";
				}
			

			if (!"".equals(last)) {
				result += "<input id=\"previousPage\" name=\"previousPage\" type=\"button\"  value=\"下一张\" class=\"flyBT\" onclick=\"location='SortList.jsp?AccPackageID="
						+ apkID + "&opt=-1&InventoryDate=" + InventoryDate + "&InventoryInOutType=" + InventoryInOutType +"&InventoryEntryId=" + InventoryEntryId +"'\">";
			} else {
				result += "<input disabled=\"true\" id=\"previousPage\" name=\"previousPage\" type=\"button\"  value=\"下一张\" class=\"flyBT\" >";
			}
			
			result += "<input id=\"gotoVoucher\" name=\"gotoVoucher\" type=\"button\"  value=\"对应凭证\" class=\"flyBT\" onclick=\"window.open('../voucherquery/SortList.jsp?AccPackageID="
				+ apkID + "&VoucherID=" + VoucherID+"','_blank','height=480,width=640, resizable=yes,scrollbars=yes')\">";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	public String getVoucherId(String accpackageid,String oldVoucherID,String TypeID,String VchDate) {
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String VoucherID = "";
		try{
			if(!"".equals(oldVoucherID)){
				String sql = "select autoid from c_voucher where accpackageid ='"+accpackageid+"' and VoucherID='"+oldVoucherID+"' and TypeID = '"+TypeID+"' and VchDate = '"+VchDate+"'";
				ps =  conn.prepareStatement(sql);
				rs = ps.executeQuery(sql);
			
				if(rs.next()){
					VoucherID = rs.getString(1);
				}
		
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return VoucherID;
	}
	
	
	public String getInventoryFullNameById(String accpackageid,String InventoryId) {
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String InventoryFullName = "";
		try{
			
				String sql = "select distinct InventoryFullName from c_inventorytype where accpackageid ='"+accpackageid+"' and Inventoryid = '"+InventoryId+"'";
				ps =  conn.prepareStatement(sql);
				rs = ps.executeQuery(sql);
			
				if(rs.next()){
					InventoryFullName = rs.getString(1);
				}
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return InventoryFullName;
	}
}
