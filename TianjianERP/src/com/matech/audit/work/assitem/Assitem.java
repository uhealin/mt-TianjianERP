package com.matech.audit.work.assitem;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.func.ASTextKey;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.work.subjectentry.SubjectInfo;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class Assitem {
	private Connection conn = null;
	
	private boolean splitBool = false; //用来标志：明细账多外币时，每个外币分开显示
	
	public Assitem(Connection conn) {
		this.conn = conn;
	}
	
	  public String getPAss(String SubjectID,String AssItemID, String accpackageid) throws Exception {		  
		  PreparedStatement ps = null;
		  ResultSet rs = null;  
		  String result = "";
		  try {
		      if (SubjectID == null || AssItemID == null || accpackageid == null) {
		        return "";
		      }else {		          
		          String sql = "select * from c_assitem a,(select * from c_assitem where accpackageid='"+accpackageid+"' and AccID ='"+SubjectID+"' and AssItemID = '"+AssItemID+"') b where a.accpackageid='"+accpackageid+"' and a.Level0=1 and b.AssTotalName like concat(a.AssTotalName,'%') and a.accid='"+SubjectID+"'";
		          ps = conn.prepareStatement(sql);
		          rs = ps.executeQuery();
		          if(rs.next()){
		        	  result = rs.getString("assitemid");
		          }
		          return result;
		      }
		      
		  }
		    catch (Exception e) {
		      return "";
		    }finally {
		    	DbUtil.close(rs);
				DbUtil.close(ps);
			}   
	  }
	  
	public String getAssTotalName(String accpackageid, String assitemid) throws Exception{		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String str = "";
			String sql = "select AssTotalName from c_assitem where accpackageid = '"
					+ accpackageid + "' and assitemid ='" + assitemid + "'";			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				str = rs.getString(1);
			}
			return str;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getAssItemName(String acc, String assitemid)  throws Exception {			
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String str = "";
			String sql = "select AssItemName from c_assitem where accpackageid = '"
					+ acc + "' and assitemid ='" + assitemid + "'";			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				str = rs.getString(1);
			}
			return str;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getAssProject(String acc, String assItemName, String SubjectID ,String AssItemID) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try {
			String sql = "select distinct assitemid,asstotalname from c_assitem where  \n"
					+ " accpackageid = '" + acc + "' and accid like CONCAT('"
					+ SubjectID + "','%') and AssTotalName like  CONCAT('" + assItemName
					+ "','%') order by assitemid";
			System.out.println(sql);			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			sb.append("<table width=\"100%\"  border=\"0\" cellSpacing=\"1\" cellPadding=\"2\"  bgColor=\"#6595d6\" id=\"DataAssItem\">");
			sb.append("<tr height=\"10\" class=\"DGtd\"><td  align=\"center\" width=\"30%\" bgColor=\"#B9C4D5\"  nowrap>编号</td><td align=\"center\"  width=\"70%\" bgColor=\"#B9C4D5\"  nowrap>名称</td></tr>");
			while (rs.next()) {
				
				String s = rs.getString("assitemid");
				if(AssItemID.equals(s)){
					sb.append("<tr style=\"color:#FF0000\" onclick=\"goSubmit('"+s+"');\" onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='#F3F5F8';\" bgColor=\"#F3F5F8\" height=\"18\">");
					sb.append("<td>『"+s+"』</td>");
					sb.append("<td>"+rs.getString("asstotalname")+"</td>");
					sb.append("</tr>");
				}else{
					sb.append("<tr onclick=\"goSubmit('"+s+"');\" onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='#F3F5F8';\" bgColor=\"#F3F5F8\" height=\"18\">");					
					sb.append("<td>『"+s+"』</td>");
					sb.append("<td>"+rs.getString("asstotalname")+"</td>");
					sb.append("</tr>");
				}				
			}
			sb.append("</table>");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	
	public String getAssXProject(String acc, String assItemName1, String assItemName2 ,String AssItemID) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try {
			
			String AssItemIDS1 =  AssitemXLevel(acc,assItemName1);
			String AssItemIDS2 =  AssitemXLevel(acc,assItemName2);
			String sql ="select a_autoid,a_subjectid,a_assitemid,b_assitemid,b.AssItemName  b_AssItemName, c.AssItemName  c_AssItemName from ("
						+" select group_concat(CONVERT(a.autoid,char)) as a_autoid,group_concat(distinct a.subjectid) as a_subjectid,a.assitemid a_assitemid,b.assitemid b_assitemid from c_assitementry a \n"
				 		+" inner join c_assitementry b\n"
				 		+" on a.voucherid = b.voucherid and a.serail = b.serail  \n"
				 		+" where 1=1 \n"
				 		+" and a.assitemid in ("+AssItemIDS1+") \n"
				 		+" and a.AccPackageID='"+acc+"' \n"
				 		+" and b.AccPackageID='"+acc+"' \n"
				 		+" and b.assitemid in ("+AssItemIDS2+")\n"	 	
				 		+" and a.assitemid != b.assitemid \n"
				 		+" group by a.assitemid,b.assitemid \n"
				 		+" order by a.assitemid,b.assitemid \n"
				 		+"  )a \n"
						+" left join (select distinct assitemid,AssItemName from c_assitem where AccPackageID='"+acc+"' and assitemid in ("+AssItemIDS1+")) b\n"
						+" on a_assitemid = b.assitemid   \n"
						+" left join (select distinct assitemid,AssItemName from c_assitem where AccPackageID='"+acc+"'and assitemid in ("+AssItemIDS2+")) c\n"
						+" on b_assitemid = c.assitemid   \n";
			org.util.Debug.prtOut(sql);			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			sb.append("<table width=\"100%\"  border=\"0\" cellSpacing=\"1\" cellPadding=\"2\"  bgColor=\"#6595d6\" id=\"DataAssItem\">");
			sb.append("<tr height=\"10\" class=\"DGtd\"><td  align=\"center\" width=\"30%\" bgColor=\"#B9C4D5\"  nowrap>"+assItemName1+"</td><td align=\"center\"  width=\"70%\" bgColor=\"#B9C4D5\"  nowrap>"+assItemName2+"</td></tr>");
			while (rs.next()) {
				
				String s = rs.getString("a_assitemid");
				String bAssItemName = rs.getString("b_AssItemName");
				String cAssItemName = rs.getString("c_AssItemName");
				if(AssItemID.equals(s)){
					sb.append("<tr style=\"color:#FF0000\" onclick=\"goSubmit(this);\" a_assitemid='"+s+"' bAssItemName='"+bAssItemName+"' cAssItemName='"+cAssItemName+"' a_accid='"+rs.getString("a_autoid")+"' a_subjectids='"+rs.getString("a_subjectid")+"' onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='#F3F5F8';\" bgColor=\"#F3F5F8\" height=\"18\">");
					sb.append("<td>"+rs.getString("b_AssItemName")+"</td>");
					sb.append("<td>"+rs.getString("c_AssItemName")+"</td>");
					sb.append("</tr>");
				}else{
					sb.append("<tr onclick=\"goSubmit(this);\" a_assitemid='"+s+"' bAssItemName='"+bAssItemName+"' cAssItemName='"+cAssItemName+"' a_accid='"+rs.getString("a_autoid")+"' a_subjectids='"+rs.getString("a_subjectid")+"' onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='#F3F5F8';\" bgColor=\"#F3F5F8\" height=\"18\">");					
					sb.append("<td>"+rs.getString("b_AssItemName")+"</td>");
					sb.append("<td>"+rs.getString("c_AssItemName")+"</td>");
					sb.append("</tr>");
				}				
			}
			sb.append("</table>");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	
	/**
	 * 是否存在辅助核算
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	public boolean isNotAssitem(String acc) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		boolean result = false;
		try {			
			sql = "select COUNT(*) from c_assitementryacc where accpackageid='"+acc+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt(1)>0){
					result = true;
				}
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	private String getLevel(String acc,String sid,String aid) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		try {
			
			sql = "select `Level0` from c_assitem where accpackageid = '"+acc+"'  and accid='"+sid+"'  and assitemid = '"+aid+"' ";
			ps =conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = String.valueOf(rs.getInt(1)+1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			
		}
	}
	public StringBuffer getAssStr(String acc,String sid,String aid,String bdate,String edate,String dataName) throws Exception{		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		StringBuffer result = new StringBuffer(); 		
		int colCount = 0;
		try {			
			ASFuntion CHF=new ASFuntion();
			String level = getLevel(acc,sid,aid);
			String ss =  new ASTextKey(conn).getACurrRate(acc);
			
			String AssTotalName = getAssTotalName(acc,aid);
			
			sql = "select * from (" +
			"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname,case dataname when '0' then '"+ss+"' else dataname end dataname," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
			"\n 	case a.direction when 0 then abs(sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
			"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='"+edate+"' then Balance else 0 end )>0 then '借' when sum(case when SubMonth='"+edate+"' then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
			"\n 	case a.direction when 0 then abs(sum(case when SubMonth='"+edate+"' then Balance else 0 end )) else sum(case when SubMonth='"+edate+"' then Balance else 0 end ) *a.direction end qmremain," +
			"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname " +
			"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid=b.subjectid " +
			"\n 	where a.accpackageid = '"+acc+"' and accid='"+sid+"' and AssTotalName1 like concat('"+AssTotalName+"','%')  and `level1`='"+level+"' and submonth>='"+bdate+"' and submonth <='"+edate+"' and a.dataName='"+dataName+"' " +
			"\n 	GROUP by accid,AssItemID" +
			
			"\n 	union " +
			
			"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
			"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
			"\n 	case a.direction when 0 then abs(sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubMonth='"+bdate+"' then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
			"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='"+edate+"' then Balance else 0 end )>0 then '借' when sum(case when SubMonth='"+edate+"' then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
			"\n 	case a.direction when 0 then abs(sum(case when SubMonth='"+edate+"' then Balance else 0 end )) else sum(case when SubMonth='"+edate+"' then Balance else 0 end ) *a.direction end qmremain," +
			"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname " +
			"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid=b.subjectid " +
			"\n 	where a.accpackageid = '"+acc+"' and accid='"+sid+"' and AssTotalName1 like concat('"+AssTotalName+"','%')  and `level1`='"+level+"' and submonth>='"+bdate+"' and submonth <='"+edate+"' and a.dataName='"+dataName+"' " +
			"\n 	GROUP by accid,AssItemID" +
			
			"\n ) a where 1=1   order by accid,AssItemID ";
			
			
			org.util.Debug.prtOut("sql :=|"+sql);
			ps =conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			colCount = RSMD.getColumnCount();
						
//			result.append("|"); 	//旧的查询
//			while(rs.next()){
//				for (int i = 1; i < colCount; i++) {
//					result.append(rs.getString(i) + "`");
//				}
//				result.append(rs.getString(colCount) + "|");
//			}
//			if("|".equals(result.toString())){
//				result.delete(0,result.length());
//			}
			
			int iCount = 0;
			String bgColor = "#dddccc";
			while(rs.next()){
				String accid = rs.getString("accid");
				String subjectName = rs.getString("subjectName");
				String subjectfullname = rs.getString("subjectfullname");
				
				String AssItemID = rs.getString("AssItemID");
				String asstotalname = rs.getString("asstotalname");
				String dataname = rs.getString("dataname");
				String QcWay = rs.getString("QcWay");
				String qcremain = rs.getString("qcremain");
				String debit = rs.getString("debit");
				String credit = rs.getString("credit");
				String QmWay = rs.getString("QmWay");
				String qmremain = rs.getString("qmremain");
				String direction = rs.getString("direction");
				String isleaf = rs.getString("isleaf");
				String dname = rs.getString("dname");
				if("1".equals(isleaf)){
					bgColor = "#f3f5f8";
				}
				result.append("<tr id='_0_"+sid+"_"+dataName+"_"+aid+"' ondblclick='goSort();' onmouseover=\"this.bgColor='#E4E8EF';\" style='CURSOR: hand' onmouseout=\"this.bgColor='"+bgColor+"';\" bgColor='"+bgColor+"' height='18' AccPackageID='"+acc+"' DataName='"+dname+"' ; isleaf="+isleaf+" AssItemID='"+AssItemID+"' SubjectID='"+accid+"' subMonth='0' >");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+accid+"</td>");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+subjectfullname+"</td>");
				if("0".equals(isleaf)){
					result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap><img src=\"/AuditSystem/images/plus.jpg\" onclick=\"changeImg(this);goSubSort(this)\"/>&nbsp;"+AssItemID+"</td>");
				} else {
					result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+AssItemID+"</td>");
				}
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+asstotalname+"</td>");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+dataname+"</td>");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" style='TEXT-ALIGN: center'>"+QcWay+"</td>");
				result.append(CHF.showMoney(qcremain));
				result.append(CHF.showMoney(debit));
				result.append(CHF.showMoney(credit));				
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" style='TEXT-ALIGN: center'>"+QmWay+"</td>");
				result.append(CHF.showMoney(qmremain));
				result.append("</tr>");
				
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		} 

	}
	
	public StringBuffer getAssStr(String acc,String sid,String aid,String bdate,String edate,String dataName,String subMonth) throws Exception{	
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		StringBuffer result = new StringBuffer(); 		
		int colCount = 0;
		try {			
			ASFuntion CHF=new ASFuntion();
			String level = getLevel(acc,sid,aid);
			String ss =  new ASTextKey(conn).getACurrRate(acc);
			
			String AssTotalName = getAssTotalName(acc,aid);
			
			sql = "select * from (" +
			"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname," +
			"\n		case dataname when '0' then '"+ss+"' else dataname end dataname," +
			
			"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
			"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
			"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
			"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
			"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +
			
			"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'0' a " +
			"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid=b.subjectid " +
			"\n 	where a.accpackageid = '"+acc+"' and accid='"+sid+"' and AssTotalName1 like concat('"+AssTotalName+"','%')  and `level1`='"+level+"' and concat(subyearmonth,LPAD(SubMonth,2,'0'))='"+subMonth+"'  and a.dataName='"+dataName+"'  " +
			
			"\n 	union " +
			
			"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
			"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
			
			"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
			"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
			"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
			"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
			"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
			"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +

			"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'1' a " +
			"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid=b.subjectid " +
			"\n 	where a.accpackageid = '"+acc+"' and accid='"+sid+"' and AssTotalName1 like concat('"+AssTotalName+"','%')  and `level1`='"+level+"' and concat(subyearmonth,LPAD(SubMonth,2,'0'))='"+subMonth+"' and a.dataName='"+dataName+"'  " +
			
			"\n ) a where 1=1 order by accid,AssItemID ,yearmonth ";

			org.util.Debug.prtOut("sql :=|"+sql);
			ps =conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			colCount = RSMD.getColumnCount();
			
			int iCount = 0;
			String bgColor = "#dddccc";
			while(rs.next()){
				String accid = rs.getString("accid");
				String subjectName = rs.getString("subjectName");
				String subjectfullname = rs.getString("subjectfullname");
				
				String AssItemID = rs.getString("AssItemID");
				String asstotalname = rs.getString("asstotalname");
				String dataname = rs.getString("dataname");
				
				String yearmonth = rs.getString("yearmonth");
				
				String QcWay = rs.getString("QcWay");
				String ycremain = rs.getString("ycremain");
				
				String qcremain = rs.getString("qcremain");
				String debit = rs.getString("debit");
				String credit = rs.getString("credit");
				String DebitTotalOcc = rs.getString("DebitTotalOcc");
				String CreditTotalOcc = rs.getString("CreditTotalOcc");
				
				String QmWay = rs.getString("QmWay");
				String qmremain = rs.getString("qmremain");
				String direction = rs.getString("direction");
				String isleaf = rs.getString("isleaf");
				String dname = rs.getString("dname");
				if("1".equals(isleaf)){
					bgColor = "#f3f5f8";
				}
				result.append("<tr id='_"+yearmonth+"_"+sid+"_"+dataName+"_"+aid+"' ondblclick='goSort();' onmouseover=\"this.bgColor='#E4E8EF';\" style='CURSOR: hand' onmouseout=\"this.bgColor='"+bgColor+"';\" bgColor='"+bgColor+"' height='18' AccPackageID='"+acc+"' DataName='"+dname+"' ; isleaf="+isleaf+" AssItemID='"+AssItemID+"' SubjectID='"+accid+"'  subMonth='"+yearmonth+"' >");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+accid+"</td>");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+subjectfullname+"</td>");
				if("0".equals(isleaf)){
					result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap><img src=\"/AuditSystem/images/plus.jpg\" onclick=\"goSubSort(this)\"/>&nbsp;"+AssItemID+"</td>");
				} else {
					result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+AssItemID+"</td>");
				}
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+asstotalname+"</td>");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+dataname+"</td>");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" noWrap>"+yearmonth+"</td>");
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" style='TEXT-ALIGN: center'>"+QcWay+"</td>");
				result.append(CHF.showMoney(ycremain));
				result.append(CHF.showMoney(qcremain));
				result.append(CHF.showMoney(debit));
				result.append(CHF.showMoney(credit));	
				result.append(CHF.showMoney(DebitTotalOcc));
				result.append(CHF.showMoney(CreditTotalOcc));
				result.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\" style='TEXT-ALIGN: center'>"+QmWay+"</td>");
				result.append(CHF.showMoney(qmremain));
				result.append("</tr>");
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		} 

	}
	
	public int AssitemProperty(String acc, String SubjectID,String AssItemID) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {			
			String sql = "select AccSign,count(AccSign) Acou from (select DISTINCT dataname,AccSign from c_assitementryaccall a, c_assitem b where a.AccPackageID = '"
					+ acc
					+ "'  and b.AccPackageID = '"
					+ acc
					+ "' and a.accid like '"+ SubjectID+ "%' and a.AssItemID='"+AssItemID
					+ "' and a.accid=b.accid and a.AssItemID=b.AssItemID and b.isleaf=1 order by AccSign) a GROUP by AccSign";
			org.util.Debug.prtOut("py AssitemProperty :=" + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (rs.getString("AccSign").equals("1")) {
					i += 1;
				}
				if (rs.getString("AccSign").equals("2")) {
					i += 2;
				}
			}
			return (i);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public int LowAssItemProperty(String acc, String SubjectID,String AssItemID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {	
			String sql = "select AccSign,count(AccSign) Acou from (select DISTINCT dataname,AccSign from c_assitementryaccall a, c_assitem b where a.AccPackageID = '"
					+ acc
					+ "'  and b.AccPackageID = '"
					+ acc
					+ "' and a.accid='"
					+ SubjectID
					+ "' and a.AssItemID like concat('"+AssItemID
					+ "','%') and a.accid=b.accid and a.AssItemID=b.AssItemID and b.isleaf=1 order by AccSign) a GROUP by AccSign";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (rs.getString("AccSign").equals("1")) {
					i += 1;
				}
				if (rs.getString("AccSign").equals("2")) {
					i += 2;
				}
			}
			return (i);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public boolean ExistsTable(String TabName) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean bool = false;
		try {			
			String sql = "show TABLES  like '" + TabName + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				bool = true;
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}

	}
	
	public Map AssitemCurrency(String acc, String SubjectID,String AssItemID) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map map = new HashMap();
		try {			
			String sql = "select DISTINCT dataname,AccSign from c_assitementryaccall a, c_assitem b where a.AccPackageID = '"
					+ acc
					+ "'  and b.AccPackageID = '"
					+ acc
					+ "' and a.accid='"+SubjectID+"' and a.assitemid like concat('"
					+ AssItemID
					+ "','%') and a.accid=b.accid and a.assitemid=b.assitemid and b.isleaf=1 and a.AccSign=1 order by AccSign";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String str = "Curr";
			int i = 1;
			while (rs.next()) {
				map.put(str + String.valueOf(i), rs.getString("dataname"));
				i++;
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public Map AssitemUnitName(String acc, String SubjectID,String AssItemID) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map map = new HashMap();
		try {
			String sql = "select DISTINCT dataname,AccSign from c_assitementryaccall a, c_assitem b where a.AccPackageID = '"
				+ acc
				+ "'  and b.AccPackageID = '"
				+ acc
				+ "' and a.accid='"+SubjectID+"' and a.assitemid like concat('"
				+ AssItemID
				+ "','%') and a.accid=b.accid and a.assitemid=b.assitemid and b.isleaf=1 and a.AccSign=2 order by AccSign";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String str = "Unit";
			int i = 1;
			while (rs.next()) {
				map.put(str + String.valueOf(i), rs.getString("dataname"));
				i++;
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}

	}
	
	/**
	 * 0:非叶子与无外币和无数量的叶子
	 * @param TabName
	 * @throws Exception
	 */
	public void CreateTable(String TabName) throws Exception {		
		PreparedStatement ps = null;
		try {			
			String sql = "CREATE TABLE `" + TabName + "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
//					+ " p1 varchar(10) default NULL," 
//			        + " p2 varchar(10) default NULL," 
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(10) default NULL,"
					+ " typeid varchar(50) default NULL,"
					+ " oldvoucherid varchar(10) default NULL,"
					+ " assitemid varchar(50) default NULL,"
					+ " summary varchar(100) default NULL,"
					+ " debit varchar(20) default NULL,"
					+ " credit varchar(20) default NULL,"
					+ " dateRemain varchar(20) default NULL,"
					+ " yearRemain varchar(20) default NULL,"
					+ " subjects varchar(500) default NULL,"
					+ " rsubject varchar(500) default NULL,"
					+ " PRIMARY KEY  (id)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";

			org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			DbUtil.close(ps);	
		}

	}
	
	/**
	 * 1:叶子有外币无数量
	 * 2:叶子无外币有数量
	 * @param TabName
	 * @param map
	 * @throws Exception
	 */
	public void CreateTable(String TabName, Map map)throws Exception {		
		PreparedStatement ps = null;
		try {			
			String sql = "CREATE TABLE `" + TabName + "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
//					+ " p1 varchar(10) default NULL,"
//					+ " p2 varchar(10) default NULL,"
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(10) default NULL,"
					+ " typeid varchar(50) default NULL,"
					+ " oldvoucherid varchar(10) default NULL,"
					+ " assitemid varchar(50) default NULL,"
					+ " summary varchar(100) default NULL,"
					+ " subjects varchar(500) default NULL,"
					+ " rsubject varchar(500) default NULL,"//发生科目
					+ " rCurrency varchar(500) default NULL,";//币种或单位
			Set coll = map.keySet();
			int i = 1;
			for (Iterator iter = coll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				// String str = "rate"+i;
				if(this.isSplitBool()) {
					sql += "debitrate" + i + " varchar(20) default NULL,debitCurr" + i + " varchar(20) default NULL,";
					sql += "creditrate" + i + " varchar(20) default NULL,creditCurr" + i + " varchar(20) default NULL,";
					sql += "dRemainCurr" + i + " varchar(20) default NULL,yRemainCurr"+ i + " varchar(20) default NULL,";
					break;
				}
				sql += "debitrate" + i + " varchar(20) default NULL,debit" + key + " varchar(20) default NULL,";
				sql += "creditrate" + i + " varchar(20) default NULL,credit" + key + " varchar(20) default NULL,";
				sql += "dRemain" + key + " varchar(20) default NULL,yRemain"+ key + " varchar(20) default NULL,";
				i++;
				
			}
			sql += " debit varchar(20) default NULL,";
			sql += " credit varchar(20) default NULL,";
			sql += " dateRemain varchar(20) default NULL, yearRemain varchar(20) default NULL,PRIMARY KEY  (id)) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void CreateTable(String TabName, Map Cmap, Map Umap) throws Exception {	
		PreparedStatement ps = null;
		try {
			String sql = "CREATE TABLE `" + TabName + "` ("
				+ " id int(10) NOT NULL auto_increment,"
				+ " autoid varchar(20) default NULL,"
				+ " voucherid varchar(20) default NULL,"
//				+ " p1 varchar(10) default NULL,"
//				+ " p2 varchar(10) default NULL,"
				+ " vchyear varchar(10) default NULL,"
				+ " vchmonth varchar(10) default NULL,"
				+ " vchdate varchar(10) default NULL,"
				+ " typeid varchar(50) default NULL,"
				+ " oldvoucherid varchar(10) default NULL,"
				+ " assitemid varchar(50) default NULL,"
				+ " summary varchar(100) default NULL,"
				+ " subjects varchar(500) default NULL,"
				+ " rsubject varchar(500) default NULL,"//发生科目
				+ " rCurrency varchar(500) default NULL,"//币种
				+ " rUnitName varchar(500) default NULL,";//数量
			
			Set Ucoll = Umap.keySet();
			Set Ccoll = Cmap.keySet();
			int i = 1;
			if(!this.isSplitBool()) {
				for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					sql += "debitPrice" + i + " varchar(20) default NULL,debit" + key + " varchar(20) default NULL,";
					sql += "creditPrice" + i + " varchar(20) default NULL,credit" + key + " varchar(20) default NULL,";
					sql += "dRemain" + key + " varchar(20) default NULL,yRemain" + key + " varchar(20) default NULL,";
					
					i++;
					if(this.isSplitBool()) break;
				}
			}
			i = 1;
			
			for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				if(this.isSplitBool()) {
					sql += "debitrate" + i + " varchar(20) default NULL,debitCurr" + i + " varchar(20) default NULL,";
					sql += "creditrate" + i + " varchar(20) default NULL,creditCurr" + i + " varchar(20) default NULL,";
					sql += "dRemainCurr" + i + " varchar(20) default NULL,yRemainCurr"+ i + " varchar(20) default NULL,";
					break;
				}
				
				sql += "debitrate" + i + " varchar(20) default NULL,debit"+ key + " varchar(20) default NULL,";
				sql += "creditrate" + i + " varchar(20) default NULL,credit" + key + " varchar(20) default NULL,";
				sql += "dRemain" + key + " varchar(20) default NULL,yRemain"+ key + " varchar(20) default NULL,";
				i++;
			}
			sql += " debit varchar(20) default NULL,";
			sql += " credit varchar(20) default NULL,";
			sql += " dateRemain varchar(20) default NULL, yearRemain varchar(20) default NULL,PRIMARY KEY  (id)) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public String AssitemLevel(String acc,String SubjectID,String AssItemID) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String sql = "select a1.assitemid from c_assitem a1 , c_assitem b1 " +
				" where b1.accpackageid = '"+acc+"'  " +
				" and b1.accid like concat('"+SubjectID+"','%') and b1.assitemid='"+AssItemID+"' " + 
				" and a1.accid=b1.accid  " +
				" and (a1.AssTotalName = b1.AssTotalName or a1.AssTotalName like concat(b1.AssTotalName,'/%')) " + 
				" and a1.accpackageid = '"+acc+"' ";
			String string  = "";
			rs = st.executeQuery(sql);
			while(rs.next()){
				string += "'"+rs.getString(1)+"',";
			}
			if(!"".equals(string)){
				string = string.substring(0, string.length()-1);
			}
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	public String AssitemXLevel(String acc,String AssItemName) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String sql = "select distinct a1.assitemid from c_assitem a1  " +
				" where 1=1 and (a1.AssTotalName ='"+AssItemName+"' or a1.AssTotalName like '"+AssItemName+"%') " + 
				" and a1.accpackageid = '"+acc+"' "+
				" and a1.isleaf = '1' ";
			String string  = "";
			rs = st.executeQuery(sql);
			while(rs.next()){
				string += "'"+rs.getString(1)+"',";
			}
			if(!"".equals(string)){
				string = string.substring(0, string.length()-1);
			}
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	
	/**
	 * 0:非叶子与无外币和无数量的叶子
	 * @param TabName
	 * @param user
	 * @param proid
	 * @param T1
	 * @param SubjectID
	 * @param AssItemID
	 * @param BeginYear
	 * @param BeginMonth
	 * @param EndYear
	 * @param EndMonth
	 * @throws Exception
	 */
	public void DataToTable(String TabName,String user, String proid, 
			String T1, String SubjectID, String AssItemID, 
			String BeginYear, String BeginMonth,String EndYear, String EndMonth) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		try{
			String sql = "";
			int bDate = Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginMonth);
			int eDate = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndMonth);
			
			String acc = T1 + EndYear;
			
			String string =  AssitemLevel(acc,SubjectID,AssItemID);
			if("".equals(string)) string = "''";
			
			st = conn.createStatement(); 
			for(int i=bDate ; i<=eDate ; i++){
				String strSql = "";
				if(i != bDate){
					strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,assitemid,SUBSTRING(vchdate,6,2) AS submonth FROM c_assitementry a where a.subjectid like concat('"+SubjectID+"','%') and a.property like '1%' and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2)="+ i +" and a.assitemid in ("+string+")) b WHERE a.accpackageid = b.accpackageid AND a.accid = b.subjectid and a.assitemid = b.assitemid AND a.submonth = b.submonth) ) "; 
				}
				sql = "insert into `"+ TabName+ "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,assitemid,summary,debit,credit,subjects,rsubject) " +
					"\n select * from (" +
					"\n 	select a.AutoId,a.voucherid,SUBSTRING(a.vchdate,1,4) as vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.vchdate,a.typeid,a.oldvoucherid,a.assitemid,a.summary,if(a.dirction=1,assitemsum,0.00) as debit, if(a.dirction=1,0.00,assitemsum) as Credit,  " +
					"\n     REPLACE(REPLACE(if(b.Dirction*b.occurvalue<0,REPLACE(b.debitsubjects,',,',','),REPLACE(b.creditsubjects,',,',',')),',,',','),',,',',') subjects," +
					"\n 	CONCAT(b.subjectname1,'(',a.subjectid,')|核算：',c.AssItemName) AS rsubject " +
					"\n 	from c_assitementry a " +
					"\n		left join c_subjectentry b on a.voucherid = b.voucherid and a.Serail = b.Serail " +
					"\n		INNER JOIN c_assitementryacc c ON a.AccPackageID = c.AccPackageID AND a.AssItemID = c.AssItemID AND a.SubjectID = c.AccID " +
					"\n 	where a.subjectid like concat('"+SubjectID+"','%') and a.property like '1%' and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2)="+ i +
					"\n 	and a.assitemid in ("+string+")  and b.subjectid like concat('"+SubjectID+"','%') " +
					"\n		and substring(b.vchdate,1,4)*12+substring(b.vchdate,6,2)="+ i +
					"\n		AND c.SubYearMonth*12+c.submonth = "+ i +
					"\n 	union " +
					"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本月合计' as summary,sum(a.DebitOcc) debit,sum(a.CreditOcc) Credit,'' as subjects," +
					"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject " +
					"\n 	from c_assitementryacc a INNER JOIN c_account c ON a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " +
					"\n		where 1=1 and a.AccID like concat('"+SubjectID+"','%') and a.assitemid ='"+AssItemID+"' and a.SubYearMonth*12+a.submonth="+ i +" AND c.SubYearMonth*12+c.submonth ="+ i + strSql + " group by a.SubYearMonth,a.submonth "+
					"\n 	union " +
					"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本年累计' as summary,sum(a.DebitTotalOcc) debit,sum(a.CreditTotalOcc) Credit,'' as subjects," +
					"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject " +
					"\n 	from c_assitementryacc a INNER JOIN c_account c ON a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " +
					"\n		where 1=1 and a.AccID like concat('"+SubjectID+"','%') and a.assitemid ='"+AssItemID+"' and a.SubYearMonth*12+a.submonth="+ i +" AND c.SubYearMonth*12+c.submonth ="+ i + strSql + " group by a.SubYearMonth "+
					"\n 	union " +
					"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>期初余额' as summary,0.00 debit,0.00 Credit,'' as subjects, " +
					"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject " +
					"\n 	from c_assitementryacc a INNER JOIN c_account c ON a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " +
					"\n		where 1=1 and a.AccID like concat('"+SubjectID+"','%') and a.assitemid ='"+AssItemID+"' and a.SubYearMonth*12+a.submonth="+ i + " AND c.SubYearMonth*12+c.submonth ="+ i +
					strSql + 
					"\n ) a where 1=1  order by VchDate,typeid,abs(oldvoucherid),autoid" ;
				
				org.util.Debug.prtOut(i+" DataToTable = "+sql);	
				//org.util.Debug.prtOut(i+" DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
//				st.addBatch(sql);
				st.execute(sql);
			}
//			st.executeBatch();
			
			UpdateToTable(TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear),"");
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
				
	}
	
	
	/**
	 * 0:非叶子与无外币和无数量的叶子
	 * @param TabName
	 * @param user
	 * @param proid
	 * @param T1
	 * @param SubjectID
	 * @param AssItemID
	 * @param BeginYear
	 * @param BeginMonth
	 * @param EndYear
	 * @param EndMonth
	 * @throws Exception
	 */
	public void DataToTableX(String TabName,String user, String proid, 
			String T1, String SubjectID, String AssItemID, 
			String BeginYear, String BeginMonth,String EndYear, String EndMonth,String a_accid,String a_subjectids) throws Exception{
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String[] subjectIds = a_subjectids.split(","); 
			String sql = "";
			int bDate = Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginMonth);
			int eDate = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndMonth);
			
			String acc = T1 + EndYear;
			String string = AssitemXLevel(acc,AssItemID);
			if("".equals(string)) string = "''";
		//	String string =  AssitemLevel(acc,SubjectID,AssItemID);
		//	if("".equals(string)) string = "''";
			
			st = conn.createStatement(); 
			
			for(int i=bDate ; i<=eDate ; i++){
				for(int j=0 ; j<subjectIds.length ; j++){
					
					String tempSql = "select SUBSTRING(vchdate,6,2) as month ,subString(vchdate,1,7) as subDate  from c_assitementry where SubjectID = '"+subjectIds[j]+"' and  autoid in ("+a_accid+") and substring(vchdate,1,4)*12+substring(vchdate,6,2)="+ i ; 
					
					ps = conn.prepareStatement(tempSql);
					rs = ps.executeQuery();
					String month = "000000";
					String subDate = "000000";
					String countSql = " and 1=2 ";
					if(rs.next()){
						month = rs.getString("month");
						subDate = rs.getString("subDate");
						countSql = "";
					}
					rs.close();
					ps.close();
					sql = "insert into `"+ TabName+ "` (autoid,voucherid,vchmonth,vchdate,typeid,oldvoucherid,assitemid,summary,debit,credit) " +
						"\n select * from (" +
						"\n 	select a.AutoId,a.voucherid,SUBSTRING(a.vchdate,6,2) vchmonth,a.vchdate,a.typeid,a.oldvoucherid,subjectid,a.summary,if(a.dirction=1,assitemsum,0.00) as debit, if(a.dirction=1,0.00,assitemsum) as Credit  " +
						"\n 	from c_assitementry a "+
						"\n 	where 1=1 and a.autoid in ("+a_accid+") and a.SubjectID='"+subjectIds[j]+"' and a.property like '1%' and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2)="+ i +
						"\n 	union " +
						"\n 	select '' as AutoId,'' as voucherid,SUBSTRING(vchdate,6,2) vchmonth,concat(subString(vchdate,1,7),'-97') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本月合计' as summary,sum(if(dirction=1,assitemsum,0.00)) debit,sum(if(dirction=1,0.00,assitemsum)) Credit " +
						"\n 	from c_assitementry where 1=1 and SubjectID = '"+subjectIds[j]+"' and  autoid in ("+a_accid+") and substring(vchdate,1,4)*12+substring(vchdate,6,2)="+ i +" group by SubjectID,vchmonth "+
						"\n 	union " +
						"\n 	select '' as AutoId,'' as voucherid,'"+month+"' vchmonth,concat('"+subDate+"','-98') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本年累计' as summary,sum(if(dirction=1,assitemsum,0.00)) debit,sum(if(dirction=1,0.00,assitemsum)) Credit " +
						"\n 	from c_assitementry where 1=1 and SubjectID = '"+subjectIds[j]+"' and  autoid in ("+a_accid+") and substring(vchdate,1,4)*12+substring(vchdate,6,2)<="+ i +" and substring(vchdate,1,4)*12+substring(vchdate,6,2)>="+ bDate +" "+countSql+" group by SubjectID "+
						"\n 	union " +
						"\n 	select '' as AutoId,'' as voucherid,SUBSTRING(vchdate,6,2) vchmonth,concat(subString(vchdate,1,7),'-00') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>期初余额' as summary,0.00 debit,0.00 Credit " +
						"\n 	from c_assitementry where 1=1 and SubjectID = '"+subjectIds[j]+"' and  autoid in ("+a_accid+") and substring(vchdate,1,4)*12+substring(vchdate,6,2)="+ i +" group by SubjectID,vchmonth "+
						"\n ) a where 1=1  order by VchDate,typeid,abs(oldvoucherid),autoid" ;
					
					//org.util.Debug.prtOut(i+" DataToTable = "+sql);	
	//				//org.util.Debug.prtOut(i+" DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
	//				st.addBatch(sql);
					st.execute(sql);
	//				UpdateToTable(TabName, T1, subjectIds[j], AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear));
				}
			}
//			st.executeBatch();
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
				
	}
	
	/**
	 * 1:叶子有外币无数量
	 * 2:叶子无外币有数量
	 * @param map
	 * @param TabName
	 * @param user
	 * @param proid
	 * @param T1
	 * @param SubjectID
	 * @param AssItemID
	 * @param BeginYear
	 * @param BeginMonth
	 * @param EndYear
	 * @param EndMonth
	 * @param opt 1 为外币，2 为数量
	 * @throws Exception
	 */
	public void DataToTable(Map map,String TabName,String user, String proid, 
			String T1, String SubjectID, String AssItemID, 
			String BeginYear, String BeginMonth,String EndYear, String EndMonth,int opt) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "";
			int bDate = Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginMonth);
			int eDate = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndMonth);
			
			String acc = T1 + EndYear;
			
			String string =  AssitemLevel(acc,SubjectID,AssItemID);
			if("".equals(string)) string = "''";
			
			st = conn.createStatement();
			String s1 = "";
			String s2 = "";
			String s3 = "";
			String s4 = "";
			String s5 = "";
			
			int i = 1;
			
			String opt1 = "Currency";
			String opt2 = "CurrRate";
			String opt3 = "CurrValue";
			if(opt == 2){
				opt1 = "UnitName";
				opt2 = "UnitPrice";
				opt3 = "Quantity";
			}
			
			
			if(this.isSplitBool()){
				//用来标志：明细账多外币时，每个外币分开显示
				
				//生成本位币的明细账
//				DataToTable(TabName,"", "", T1,SubjectID, AssItemID, BeginYear, BeginMonth,EndYear, EndMonth); //本位币
				
				//生成外币和数量的明细账
				String s6 = "";
				Set coll = map.keySet();
				for (Iterator iter = coll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					
					s1 = "debitrate" + i + ",debitCurr" + i + ",creditrate" + i + ",creditCurr" + i + ",";

					s2 = "if(a."+opt1+"='" + value + "',a."+opt2+",'') debitrate" + i + ",";
					s2 += "if(a."+opt1+"='" + value + "',IF(a.dirction=1,a."+opt3+",0.00),'') debitCurr" + i + ",";
					s2 += "if(a."+opt1+"='" + value + "',a."+opt2+",'') creditrate" + i + ",";
					s2 += "if(a."+opt1+"='" + value + "',IF(a.dirction=1,0.00,a."+opt3+"),'') creditCurr" + i + ",";
					
					s3 = "'' DebitRate" + i + ",a.DebitOcc,'' CreditRate" + i + ", a.CreditOcc,";
					
					s5 = " and a.DataName='" + value + "'";
					
					s6 = " and a."+opt1+"='" + value + "'";
					
					for(int ii=bDate ; ii<=eDate ; ii++){
						
						int myyear=ii / 12;
						int mymonth=ii % 12;
						String myyearmonth="";
						switch (mymonth){
							case 0:
								myyearmonth=String.valueOf(myyear-1)+"-12-%";
								break;
							case 1:
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							case 8:
							case 9:
								myyearmonth=String.valueOf(myyear)+"-0"+String.valueOf(mymonth)+"-%";
								break;
							default:
								myyearmonth=String.valueOf(myyear)+"-"+String.valueOf(mymonth)+"-%";
								break;
						}
							
						String strSql = "";
						if(ii != bDate){
							strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,assitemid,SUBSTRING(vchdate,6,2) AS submonth FROM c_assitementry a where a.subjectid ='"+SubjectID+"' and a.property like '1%' and a.vchdate like '" +myyearmonth + "' and a.assitemid in ("+string+") "+ s6 + " ) b WHERE a.accpackageid = b.accpackageid AND a.accid = b.subjectid and a.assitemid = b.assitemid AND a.submonth = b.submonth) ) "; 
						}
						
						String s31 = s3 ; 
						sql = "insert into `"+ TabName + "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,assitemid,summary," + s1 + "debit,credit,subjects,rsubject,rCurrency) " +
							"\n select * from (" +
							"\n 	select a.AutoId,a.voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.vchdate,a.typeid,a.oldvoucherid,a.assitemid,a.summary," + s2 + "if(a.dirction=1,assitemsum,0.00) as debit, if(a.dirction=1,0.00,assitemsum) as Credit, " + 
							"\n     REPLACE(REPLACE(if(b.Dirction*b.occurvalue<0,REPLACE(b.debitsubjects,',,',','),REPLACE(b.creditsubjects,',,',',')),',,',','),',,',',') subjects, " +
							"\n		CONCAT(b.subjectname1,'(',a.subjectid,')|核算：',c.AssItemName) AS rsubject,a."+opt1+" as rCurrency " +
							"\n 	from c_assitementry a left join c_subjectentry b on a.voucherid = b.voucherid and a.Serail = b.Serail " +
							"\n		INNER JOIN c_assitementryacc c ON c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.AssItemID = c.AssItemID AND a.SubjectID = c.AccID " +
							"\n 	where a.subjectid ='"+SubjectID+"' and a.property like '1%' and a.vchdate like '" +myyearmonth + "' " +
							"\n 	and a.assitemid in ("+string+")  and b.subjectid = '"+SubjectID+"' and b.vchdate like '" +myyearmonth + "' " +
							"\n		" + s6 + 
							
							"\n 	union " +
							
							"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本月合计' as summary,"+ s31 + "a.DebitOccF debit,a.CreditOccF Credit,'' as subjects," +
							"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,a.dataname AS Currency " +
							"\n 	from c_assitementryaccall a ,c_account c " + 
							"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
							"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql;
						
						s31 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll("CreditOcc", "CreditTotalOcc");
						sql += "\n 	union " +
							"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本年累计' as summary,"+ s31 + "a.DebitTotalOccF debit,a.CreditTotalOccF Credit,'' as subjects," +
							"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,a.dataname AS Currency " +
							"\n 	from c_assitementryaccall a ,c_account c " + 
							"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
							"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql;
						
						s31 = "";
						i = 1;
						s31 += "'' debitRate" + i + ",0.00 debitCurr" + i + ",'' CreditRate" + i + ",0.00 CreditCurr" + i + ",";
						
						sql += "\n 	union " +
							"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>期初余额' as summary,"+ s31 + "0.00 debit,0.00 Credit,'' as subjects ," +
							"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,a.dataname AS Currency " +
							"\n 	from c_assitementryaccall a ,c_account c " + 
							"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
							"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql;
					
						sql += "\n ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
						
						org.util.Debug.prtOut("1 DataToTable = "+sql);	
						st.execute(sql);
						
					}
					
					//org.util.Debug.prtOut(" DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
					
					UpdateToTable(TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear),s5);			
					UpdateToTable( map, TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear));

					
					
				}
			}else{
				//用来标志：明细账多外币时，每个外币分开显示
				
				Set coll = map.keySet();
				for (Iterator iter = coll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					
					s1 += "debitrate" + i + ",debit" + key + ",creditrate" + i + ",credit" + key + ",";

					s2 += "if(a."+opt1+"='" + value + "',a."+opt2+",'') debitrate" + i + ",";
					s2 += "if(a."+opt1+"='" + value + "',IF(a.dirction=1,a."+opt3+",0.00),'') debit" + key + ",";
					s2 += "if(a."+opt1+"='" + value + "',a."+opt2+",'') creditrate" + i + ",";
					s2 += "if(a."+opt1+"='" + value + "',IF(a.dirction=1,0.00,a."+opt3+"),'') credit" + key + ",";
					
					s3 += "'' DebitRate" + i + ",a" + i + ".DebitOcc,'' CreditRate" + i + ", a" + i + ".CreditOcc,";
					
					s4 += "c_assitementryaccall a" + i + ",";
					
					s5 += " and a" + i + ".accpackageid = a.accpackageid  ";
					s5 += " and a" + i + ".SubYearMonth*12+a" + i + ".submonth=a.SubYearMonth*12+a.submonth";
					s5 += " and a" + i + ".DataName='" + value + "'";
					s5 += " and a" + i + ".accid=a.AccID and a" + i + ".Assitemid=a.Assitemid";
					i++;
				}		
				
				
				for(int ii=bDate ; ii<=eDate ; ii++){
					
					int myyear=ii / 12;
					int mymonth=ii % 12;
					String myyearmonth="";
					switch (mymonth){
						case 0:
							myyearmonth=String.valueOf(myyear-1)+"-12-%";
							break;
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
						case 8:
						case 9:
							myyearmonth=String.valueOf(myyear)+"-0"+String.valueOf(mymonth)+"-%";
							break;
						default:
							myyearmonth=String.valueOf(myyear)+"-"+String.valueOf(mymonth)+"-%";
							break;
					}
						
					String strSql = "";
					if(ii != bDate){
						strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,assitemid,SUBSTRING(vchdate,6,2) AS submonth FROM c_assitementry a where a.subjectid ='"+SubjectID+"' and a.property like '1%' and a.vchdate like '" +myyearmonth + "' and a.assitemid in ("+string+") ) b WHERE a.accpackageid = b.accpackageid AND a.accid = b.subjectid and a.assitemid = b.assitemid AND a.submonth = b.submonth) ) "; 
					}
					
					String s31 = s3 ; 
					sql = "insert into `"+ TabName + "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,assitemid,summary," + s1 + "debit,credit,subjects,rsubject,rCurrency) " +
						"\n select * from (" +
						"\n 	select a.AutoId,a.voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.vchdate,a.typeid,a.oldvoucherid,a.assitemid,a.summary," + s2 + "if(a.dirction=1,assitemsum,0.00) as debit, if(a.dirction=1,0.00,assitemsum) as Credit, " + 
						"\n     REPLACE(REPLACE(if(b.Dirction*b.occurvalue<0,REPLACE(b.debitsubjects,',,',','),REPLACE(b.creditsubjects,',,',',')),',,',','),',,',',') subjects, " +
						"\n		CONCAT(b.subjectname1,'(',a.subjectid,')|核算：',c.AssItemName) AS rsubject,a."+opt1+" as rCurrency " +
						"\n 	from c_assitementry a left join c_subjectentry b on a.voucherid = b.voucherid and a.Serail = b.Serail " +
						"\n		INNER JOIN c_assitementryacc c ON c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.AssItemID = c.AssItemID AND a.SubjectID = c.AccID " +
						"\n 	where a.subjectid ='"+SubjectID+"' and a.property like '1%' and a.vchdate like '" +myyearmonth + "' " +
						"\n 	and a.assitemid in ("+string+")  and b.subjectid = '"+SubjectID+"' and b.vchdate like '" +myyearmonth + "' " +
						"\n 	union " +
						"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本月合计' as summary,"+ s31 + "a.DebitOcc debit,a.CreditOcc Credit,'' as subjects," +
						"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,'' AS Currency " +
						"\n 	from " + s4 + " c_assitementryacc a ,c_account c " + 
						"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
						"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql;
					
					s31 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll("CreditOcc", "CreditTotalOcc");
					sql += "\n 	union " +
						"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本年累计' as summary,"+ s31 + "a.DebitTotalOcc debit,a.CreditTotalOcc Credit,'' as subjects," +
						"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,'' AS Currency " +
						"\n 	from " + s4 + " c_assitementryacc a ,c_account c " + 
						"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
						"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql;
					
					s31 = "";
					i = 1;
					for (Iterator iter = coll.iterator(); iter.hasNext();) {
						String key = (String) iter.next();
						s31 += "'' debitRate" + i + ",0.00 debit" + key + ",'' CreditRate" + i + ",0.00 Credit" + key + ",";
						i++;
					}
					sql += "\n 	union " +
						"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>期初余额' as summary,"+ s31 + "0.00 debit,0.00 Credit,'' as subjects ," +
						"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,'' AS Currency " +
						"\n 	from " + s4 + " c_assitementryacc a ,c_account c " + 
						"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
						"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql;
				
					sql += "\n ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
					
					org.util.Debug.prtOut("2 DataToTable = "+sql);	
					st.execute(sql);
					
				}
				
				//org.util.Debug.prtOut(" DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
				
				UpdateToTable(TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear),"");			
				UpdateToTable( map, TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear));

			}
			
						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	} 

	/**
	 * 3:叶子有外币有数量
	 * @param Cmap
	 * @param Umap
	 * @param TabName
	 * @param user
	 * @param proid
	 * @param T1
	 * @param SubjectID
	 * @param AssItemID
	 * @param BeginYear
	 * @param BeginMonth
	 * @param EndYear
	 * @param EndMonth
	 * @throws Exception
	 */
	public void DataToTable(Map Cmap,Map Umap,String TabName,String user, String proid, 
			String T1, String SubjectID, String AssItemID, 
			String BeginYear, String BeginMonth,String EndYear, String EndMonth) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = "";
			int bDate = Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginMonth);
			int eDate = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndMonth);
			
			String acc = T1 + EndYear;
			
			String string =  AssitemLevel(acc,SubjectID,AssItemID);
			if("".equals(string)) string = "''";
			
			st = conn.createStatement();
			
			String s1 = "",s2 = "",s3 = "",s4 = "",s5 = "",s6 = "";
			
			int i = 1;
			
			Set Ccoll = Cmap.keySet();
			Set Ucoll = Umap.keySet();
			
		
			/**
			 * 外币
			 */
			for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) Cmap.get(key);
				s1 += "debitrate" + i + ",debit" + key + ",creditrate" + i + ",credit" + key + ",";

				s2 += "if(a.Currency='" + value + "',a.CurrRate,'') debitrate" + i + ",";
				s2 += "if(a.Currency='" + value + "',IF(a.dirction=1,a.CurrValue,0.00),'') debit" + key + ",";
				s2 += "if(a.Currency='" + value + "',a.CurrRate,'') creditrate" + i + ",";
				s2 += "if(a.Currency='" + value + "',IF(a.dirction=1,0.00,a.CurrValue),'') credit" + key + ",";
				
				s3 += "'' DebitRate" + i + ",a" + i + ".DebitOcc,'' CreditRate" + i + ", a" + i + ".CreditOcc,";
				
				s4 += "c_assitementryaccall a" + i + ",";
				
//					s5 += " and substring(a" + i + ".accpackageid,1,6)='" + T1 + "' ";
				s5 += " and a" + i + ".accpackageid = a.accpackageid  ";
				s5 += " and a" + i + ".SubYearMonth*12+a" + i + ".submonth=a.SubYearMonth*12+a.submonth";
				s5 += " and a" + i + ".DataName='" + value + "'";
				s5 += " and a" + i + ".accid=a.AccID and a" + i + ".Assitemid=a.Assitemid";
				i++;
			}
			i = 1;
			
			/**
			 * 数量
			 */
			for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) Umap.get(key);
				s1 += "debitPrice" + i + ",debit" + key + ",creditPrice" + i + ",credit" + key + ",";

				s2 += "if(a.UnitName='" + value + "',a.UnitPrice,'') debitPrice" + i + ",";
				s2 += "if(a.UnitName='" + value + "',IF(a.dirction=1,a.Quantity,0.00),'') debit" + key + ",";
				s2 += "if(a.UnitName='" + value + "',a.UnitPrice,'') creditPrice" + i + ",";
				s2 += "if(a.UnitName='" + value + "',IF(a.dirction=1,0.00,a.Quantity),'') credit" + key + ",";
				
				s3 += "'' debitPrice" + i + ",b" + i + ".DebitOcc,'' creditPrice" + i + ", b" + i + ".CreditOcc,";
				
				s4 += "c_assitementryaccall b" + i + ",";
				
//					s5 += " and substring(b" + i + ".accpackageid,1,6)='" + T1 + "' ";
				s5 += " and b" + i + ".accpackageid = a.accpackageid  ";
				s5 += " and b" + i + ".SubYearMonth*12+b" + i + ".submonth=a.SubYearMonth*12+a.submonth";
				s5 += " and b" + i + ".DataName='" + value + "'";
				s5 += " and b" + i + ".accid=a.AccID and b" + i + ".Assitemid=a.Assitemid";
			}
			
			for(int ii=bDate ; ii<=eDate ; ii++){
				String strSql = "";
				if(ii != bDate){
					strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,assitemid,SUBSTRING(vchdate,6,2) AS submonth FROM c_assitementry a where a.subjectid ='"+SubjectID+"' and a.property like '1%' and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2)="+ ii +" and a.assitemid in ("+string+")) b WHERE a.accpackageid = b.accpackageid AND a.accid = b.subjectid and a.assitemid = b.assitemid AND a.submonth = b.submonth) ) "; 
				}
				String s31 = s3 ; 
				sql = "insert into `"+ TabName + "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,assitemid,summary," + s1 + "debit,credit,subjects,rsubject,rCurrency,rUnitName) " +
					"\n select * from (" +
					"\n 	select a.AutoId,a.voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.vchdate,a.typeid,a.oldvoucherid,a.assitemid,a.summary," + s2 + "if(a.dirction=1,assitemsum,0.00) as debit, if(a.dirction=1,0.00,assitemsum) as Credit, " + 
					"\n     REPLACE(REPLACE(if(b.Dirction*b.occurvalue<0,REPLACE(b.debitsubjects,',,',','),REPLACE(b.creditsubjects,',,',',')),',,',','),',,',',') subjects, " +
					"\n		CONCAT(b.subjectname1,'(',a.subjectid,')|核算：',c.AssItemName) AS rsubject,a.Currency as rCurrency,a.UnitName as rUnitName " +
					"\n 	from c_assitementry a left join c_subjectentry b on a.voucherid = b.voucherid and a.Serail = b.Serail " +
					"\n		INNER JOIN c_assitementryacc c ON c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.AssItemID = c.AssItemID AND a.SubjectID = c.AccID " +
					"\n 	where a.subjectid ='"+SubjectID+"' and a.property like '1%' and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2)="+ ii +
					"\n 	and a.assitemid in ("+string+")  and b.subjectid = '"+SubjectID+"' and substring(b.vchdate,1,4)*12+substring(b.vchdate,6,2)="+ ii +
					"\n 	union " +
					"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本月合计' as summary,"+ s31 + "a.DebitOcc debit,a.CreditOcc Credit,'' as subjects, " +
					"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,'' AS Currency,'' as UnitName " +
					"\n 	from " + s4 + " c_assitementryacc a ,c_account c " + 
					"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
					"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql;
				
				s31 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll("CreditOcc", "CreditTotalOcc");
				sql += "\n 	union " +
					"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本年累计' as summary,"+ s31 + "a.DebitTotalOcc debit,a.CreditTotalOcc Credit,'' as subjects, " +
					"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,'' AS Currency,'' as UnitName " +
					"\n 	from " + s4 + " c_assitementryacc a ,c_account c " + 
					"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
					"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID  " + s5 + strSql ;
				
				s31 = "";
				i = 1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					s31 += "'' debitRate" + i + ",0.00 debit" + key + ",'' CreditRate" + i + ",0.00 Credit" + key + ",";
					i++;
				}
				i = 1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					s31 += "'' debitPrice" + i + ",0.00 debit" + key + ",'' creditPrice" + i + ",0.00 Credit" + key + ",";
					i++;
				}
				
				sql += "\n 	union " +
					"\n 	select '' as AutoId,'' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>期初余额' as summary,"+ s31 + "0.00 debit,0.00 Credit,'' as subjects, " +
					"\n		CONCAT(c.AccName,'(',c.subjectid,')|核算：',a.AssItemName) AS rsubject ,'' AS Currency,'' as UnitName " +
					"\n 	from " + s4 + " c_assitementryacc a,c_account c  " + 
					"\n 	where 1=1 and a.SubYearMonth*12+a.submonth = " + ii + " and a.AccID = '"+SubjectID+"' and a.assitemid ='"+AssItemID+"' " +
					"\n		AND c.SubMonth = 1 AND a.AccPackageID = c.AccPackageID AND a.accid = c.SubjectID " + s5 + strSql ;
			
				sql += "\n ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
				
				//org.util.Debug.prtOut(" DataToTable = "+sql);	
				//org.util.Debug.prtOut(i+" DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
//					st.addBatch(sql);
				st.execute(sql);
			}
			
//				st.executeBatch();
			//org.util.Debug.prtOut(" DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			
			UpdateToTable(TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear),"");			
			UpdateToTable( Cmap, TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear));
			UpdateToTable( Umap, TabName, T1, SubjectID, AssItemID, bDate, eDate,Integer.parseInt(BeginYear),Integer.parseInt(EndYear));

						
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	/**
	 * 本位币余额
	 * @param TabName
	 * @param T1
	 * @param SubjectID
	 * @param AssItemID
	 * @param bDate
	 * @param eDate
	 * @throws Exception
	 */
	public void UpdateToTable(String TabName,String T1,String SubjectID,String AssItemID,int bDate,int eDate,int bYear,int eYear,String strSql) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
			
			String token = "",table = "c_assitementryacc",strSql1 = "",strSql2 = "";
			if(strSql != null && !"".equals(strSql)){
				token = "F";
				table = "c_assitementryaccall";
				strSql1 = strSql.replaceAll("DataName", "rCurrency") ; 
				strSql2 = " and a.rCurrency = b.rCurrency ";
			}
			
			sql = "update `"+TabName+"` a join (select accpackageid,SubYearMonth,SubMonth,sum(debitremain"+token+") as debitremain,sum(creditremain"+token+") as creditremain from "+table+" a where a.accid like concat('"+SubjectID+"','%') and a.assitemid='"+AssItemID+"' "+strSql+" group by SubYearMonth,SubMonth) b on substring(accpackageid,1,6) = '"+T1+"' and SubYearMonth*12+SubMonth>="+bDate+" and SubYearMonth*12+SubMonth<="+eDate+"  "+strSql1+" and submonth=vchmonth  and substring(vchdate,9)='00' set  dateRemain=(debitremain+creditremain)";			
			st.addBatch(sql);
	     	
	     	sql = "update `"+TabName+"` a join (select accpackageid,SubYearMonth,SubMonth,sum(debitremain"+token+") as debitremain,sum(creditremain"+token+") as creditremain from "+table+" a where a.accid like concat('"+SubjectID+"','%') and a.assitemid='"+AssItemID+"' "+strSql+" group by SubYearMonth,SubMonth) b on substring(accpackageid,1,6) = '"+T1+"' and SubYearMonth>="+bYear+" and SubYearMonth<="+eYear+" and substring(vchdate,9)='00' "+strSql1+" and SubMonth=1 and SubYearMonth=substring(vchdate,1,4) set  yearRemain=(debitremain+creditremain)";
	     	st.addBatch(sql);

	     	sql = "update `"+TabName+"` a join `"+TabName+"` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='97' "+strSql2+" and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.dateRemain+b.debit-b.credit),2) ";
	     	st.addBatch(sql);
	     	
	     	sql = "update `"+TabName+"` a join `"+TabName+"` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='98' "+strSql2+" and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.yearRemain+b.debit-b.credit),2) ";
	     	st.addBatch(sql);
	     	
	     	String vi="@i_ass"+this.getRandom();
	     	st.addBatch("set "+vi+":=0");
	     	st.executeBatch();
	     	
	     	sql = "UPDATE `" + TabName + "` a set dateremain= if(concat(dateremain)>''," + vi + ":=dateremain,if(concat(" + vi + ")>''," + vi + ":=ROUND(" + vi+ "+debit-credit,2)," + vi + ":=dateremain)) ";
	     	st.addBatch(sql);
	     	st.addBatch("set "+vi+":=null");
	     	st.executeBatch();
	     	 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(st);
		}
	}
	
	/**
	 * 外币余额
	 * @param map
	 * @param TabName
	 * @param T1
	 * @param SubjectID
	 * @param AssItemID
	 * @param bDate
	 * @param eDate
	 * @throws Exception
	 */
	public void UpdateToTable(Map map,String TabName,String T1,String SubjectID,String AssItemID,int bDate,int eDate,int bYear,int eYear) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
			Set coll = map.keySet();
			for (Iterator iter = coll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) map.get(key);
				
				String strSql1 = "",strSql2 = "";
				if(this.isSplitBool()){
					key = "Curr1";
					strSql1 = " and a.rCurrency = '"+value+"' ";
					strSql2 = " and a.rCurrency = b.rCurrency ";
				}
				
				sql = "update `"+TabName+"` a join (select * from c_assitementryaccall b where b.accid='"+SubjectID+"' and b.assitemid='"+AssItemID+"' and DataName='" + value + "')  b on substring(accpackageid,1,6) = '"+T1+"' and SubYearMonth*12+SubMonth>="+bDate+" and SubYearMonth*12+SubMonth<="+eDate+" and b.accid='"+SubjectID+"' and b.assitemid='"+AssItemID+"' "+strSql1+" and submonth=vchmonth  and substring(vchdate,9)='00' and DataName='" + value + "' set  dRemain"+ key + "=(debitremain+creditremain) ";			
				st.addBatch(sql);
				
				sql = "update `"+TabName+"` a join (select * from c_assitementryaccall b where b.accid='"+SubjectID+"' and b.assitemid='"+AssItemID+"' and DataName='" + value + "') b on substring(accpackageid,1,6) = '"+T1+"' and SubYearMonth>="+bYear+" and SubYearMonth<="+eYear+" and b.accid='"+SubjectID+"' and b.assitemid='"+AssItemID+"' "+strSql1+" and substring(vchdate,9)='00' and SubMonth=1 and SubYearMonth=substring(vchdate,1,4) and DataName='" + value + "' set  yRemain"+ key + "=(debitremain+creditremain)";
		     	st.addBatch(sql);
		     	
		     	sql = "update `"+TabName+"` a join `"+TabName+"` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='97' "+strSql2+" and a.vchmonth=b.vchmonth set b.dRemain"+ key + "=ROUND((a.dRemain"+ key + "+b.debit"+ key + "-b.credit"+ key + "),2) ";
		     	st.addBatch(sql);
		     	
		     	sql = "update `"+TabName+"` a join `"+TabName+"` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='98' "+strSql2+" and a.vchmonth=b.vchmonth set b.dRemain"+ key + "=ROUND((a.yRemain"+ key + "+b.debit"+ key + "-b.credit"+ key + "),2) ";
		     	st.addBatch(sql);
		     	
		     	String vi="@i_ass"+this.getRandom();
		     	st.addBatch("set "+vi+":=0");
		     	
		     	sql = "UPDATE `" + TabName + "` a set dRemain"+ key + "= if(concat(dRemain"+ key + ")>''," + vi + ":=dRemain"+ key + ",if(concat(" + vi + ")>''," + vi + ":=ROUND(" + vi+ "+debit"+ key + "-credit"+ key + ",2)," + vi + ":=dRemain"+ key + ")) ";
		     	st.addBatch(sql);
		     	st.addBatch("set "+vi+":=null");
		     	
			}
			st.executeBatch();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(st);
		}
	}
	
	
	
	//旧方法
	public void DataToTable(String TabName,String user, String proid, String acc, String AssType,
			String Subjectid, String Assitemid, String bMonth, String eMonth)
			throws Exception {
	
		Statement Stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String sqlDate = "", sqlMonth = "", strW = "",AssItemName="" ;

			if (!"".equals(bMonth) && !"".equals(eMonth)) {
				sqlDate = " and SUBSTRING(a.vchdate,6,2)  BETWEEN '" + bMonth
						+ "' and '" + eMonth + "' ";
				sqlMonth = " and SubMonth  BETWEEN '" + bMonth + "' and '"
						+ eMonth + "' ";
			} else if (!"".equals(bMonth) && "".equals(eMonth)) {
				sqlDate = " and SUBSTRING(a.vchdate,6,2)  >= '" + bMonth + "' ";
				sqlMonth = " and SubMonth  >= '" + bMonth + "' ";
			} else if ("".equals(bMonth) && !"".equals(eMonth)) {
				sqlDate = " and SUBSTRING(a.vchdate,6,2)  <= '" + eMonth + "' ";
				sqlMonth = " and SubMonth  <= '" + eMonth + "' ";
			}

			if (!"".equals(Assitemid)) {
				strW += "and assitemid in (select assitemid from c_assitem WHERE  accpackageid = '"
						+ acc
						+ "' and IsLeaf =1 and asstotalname like (select DISTINCT(concat(asstotalname,'%')) from c_assitem WHERE  accpackageid = '"
						+ acc + "'  and assitemid='" + Assitemid + "'))";
			} else {
				Assitemid =AssType;
			}

			if(!"".equals(AssType)){
				AssItemName = getAssItemName(acc,AssType);
			}
			
			String sqlYear = acc.substring(6);
						
			Stmt = conn.createStatement();
			
			if("".equals(bMonth))bMonth="01";
			if("".equals(eMonth))eMonth="12";
			
			for(int i=Integer.parseInt(bMonth);i<=Integer.parseInt(eMonth);i++){
		
			sql = "insert into `"
					+ TabName
					+ "` (autoid,voucherid,p1,p2,vchmonth,vchdate,typeid,oldvoucherid,assitemid,summary,debit,credit) select a.* from (select  b.AutoId,a.voucherid,y.vchid as p1,z.vchid as p2,SUBSTRING(a.vchdate,6,2) vchmonth,a.vchdate,a.typeid,a.oldvoucherid,assitemid,a.summary,if(a.dirction=1,assitemsum,0.00) as debit, if(a.dirction=1,0.00,assitemsum) as Credit from c_assitementry a left join (select distinct vchid from z_voucherspotcheck  where projectid='" +proid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  left join (select distinct vchid from z_question  where projectid='" +proid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  left join c_subjectentry b on b.accpackageid='"+acc+"' and a.voucherid=b.voucherid and a.serail=b.serail where a.accpackageid = '"
					+ acc
					+ "' and a.subjectid = '"
					+ Subjectid
					+ "'and a.property=1 and assitemid IN (select assitemid from c_assitem WHERE accpackageid = '"
					+ acc
					+ "' and IsLeaf =1 and AssTotalName like CONCAT('"
					+ AssItemName
					+ "','%') and accid='"
					+ Subjectid
					+ "') "
					+ strW
					+ sqlDate + " and SUBSTRING(a.vchdate,6,2)="+i+" "
					+ " UNION select '' as AutoId,'' as voucherid,'0' as p1,'0' as p2,LPAD(SubMonth,2,'0') vchmonth,concat('"
					+ sqlYear
					+ "','-',LPAD(SubMonth,2,'0'),'-97') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本月合计' as summary,DebitOcc debit,CreditOcc Credit from c_assitementryacc where accpackageid = '"
					+ acc
					+ "' and AccID = '"
					+ Subjectid
					+ "' and assitemid ='"
					+ Assitemid
					+ "' "
					+ sqlMonth + " and submonth="+i+" "
					+ " UNION select '' as AutoId,'' as voucherid,'0' as p1,'0' as p2,LPAD(SubMonth,2,'0') vchmonth,concat('"
					+ sqlYear
					+ "','-',LPAD(SubMonth,2,'0'),'-98') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>本年累计' as summary,DebitTotalOcc debit,CreditTotalOcc Credit from c_assitementryacc where accpackageid = '"
					+ acc
					+ "' and AccID = '"
					+ Subjectid
					+ "' and assitemid ='"
					+ Assitemid
					+ "' "
					+ sqlMonth + " and submonth="+i+" "
					+ " union select '' as AutoId,'' as voucherid,'0' as p1,'0' as p2,LPAD(SubMonth,2,'0') vchmonth,concat('"
					+ sqlYear
					+ "','-',LPAD(SubMonth,2,'0'),'-00') as VchDate,'' as typeid,'' as oldvoucherid,'' as assitemid,'>期初余额' as summary, 0.00 as Debit,0.00 as Credit from c_assitementryacc where accpackageid = '"
					+ acc + "' and AccID = '" + Subjectid
					+ "' and assitemid ='" + Assitemid + "' " + sqlMonth + " and submonth="+i+" "
					+ " )  a where 1=1 order by VchDate,typeid,abs(oldvoucherid),autoid";
			Stmt.addBatch(sql);
		//	ps = conn.prepareStatement(sql);
		//	ps.execute();
			//org.util.Debug.prtOut(i+" DataToTable = "+sql);
			//org.util.Debug.prtOut(i+" DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			}
			Stmt.executeBatch();
			
			//org.util.Debug.prtOut("DataToTable = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			UpdateToTable(TabName,acc,Subjectid,Assitemid);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}

	}
	
	//旧方法
	public void UpdateToTable(String TabName, String acc, 
			String Subjectid, String Assitemid
			)throws Exception{
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {		
			//org.util.Debug.prtOut("计算1 = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			sql = "update `"
					+ TabName
					+ "` a join (select * from c_assitementryacc b where b.accid='"+Subjectid+"' and b.assitemid='"+Assitemid+"' ) b on accpackageid='"
					+ acc
					+ "' and accid='"
					+ Subjectid
					+ "' and b.assitemid='"
					+ Assitemid
					+ "' and submonth=vchmonth  and substring(vchdate,9)='00' set  dateRemain=(debitremain+creditremain)";
			 ps = conn.prepareStatement(sql);
	     	 ps.execute();
	     	 //org.util.Debug.prtOut("计算2 = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
	     	 sql = "update `"
					+ TabName
					+ "` a join (select * from c_assitementryacc b where b.accid='"+Subjectid+"' and b.assitemid='"+Assitemid+"' ) b on accpackageid='"
					+ acc
					+ "' and accid='"
					+ Subjectid
					+ "' and b.assitemid='"
					+ Assitemid
					+ "' and SubYearMonth=substring(vchdate,1,4)  and substring(vchdate,9)='00' and SubMonth=1 set  yearRemain=(debitremain+creditremain)";
	     	 ps = conn.prepareStatement(sql);
	     	 ps.execute();
	     	 //org.util.Debug.prtOut("计算3 = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
	     	 sql = "update `"
					+ TabName
					+ "` a join `"
					+ TabName
					+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='97' and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.dateRemain+b.debit-b.credit),2) ";
	     	ps = conn.prepareStatement(sql);
	     	ps.execute();
	     	//org.util.Debug.prtOut("计算4 = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
	     	sql = "update `"
					+ TabName
					+ "` a join `"
					+ TabName
					+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='98' and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.yearRemain+b.debit-b.credit),2) ";
	     	ps = conn.prepareStatement(sql);
	     	ps.execute();
	     	//org.util.Debug.prtOut("计算5 = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
	     	String vi="@i_ass"+this.getRandom();
	     	ps.addBatch("set "+vi+":=0");
	     	ps.executeBatch();
	     	sql = "UPDATE `" + TabName
					+ "` a set dateremain= if(concat(dateremain)>''," + vi
					+ ":=dateremain,if(concat(" + vi + ")>''," + vi + ":=" + vi
					+ "+debit-credit," + vi + ":=dateremain)) ";
	     	ps=conn.prepareStatement(sql);
	        ps.executeUpdate();
	        ps.addBatch("set "+vi+":=null");
	        ps.executeBatch();
	        ps.clearBatch();
	        
	        //org.util.Debug.prtOut("计算0 = "+String.valueOf(new java.util.Date(System.currentTimeMillis())));
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
	}
	
	
	/**
	 * 通过核算的autoid得到凭证分录的autoid
	 *
	 * @param request
	 * @param Response
	 * @return
	 * @throws Excep
	 */

	public String getSubjectentryId(String Assitemid)throws Exception{
				
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String voucherid = "";
		String Serail = "";
		String accpackageid = "";
		String autoid = "";
		try {		
			
			sql = "select voucherid,Serail,accpackageid from c_assitementry where autoid = '"+Assitemid+"'";
			
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				voucherid = rs.getString(1);
				Serail = rs.getString(2);
				accpackageid = rs.getString(3);
				
			}
			rs.close();
			ps.close();
			
			sql = "select autoid from c_subjectentry where accpackageid = '"+accpackageid+"' and voucherid = '"+voucherid+"' and Serail = '"+Serail+"' ";
	        
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				autoid = rs.getString(1);
			}
			rs.close();
			ps.close();
	       
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
		
		return  autoid;
	}
	

	
	public String getRandom() {
		java.text.DecimalFormat df = new DecimalFormat("####");
		String i = df.format(Math.random() * 1000000000);
//		return i;
		return com.matech.framework.pub.autocode.DELUnid.getNumUnid();
	}
	
	public void DelTempTable(String TabName) throws Exception {		
		PreparedStatement ps = null;
		try {			
			// DROP TABLE IF EXISTS `tt`;
			String sql = "DROP TABLE IF EXISTS `" + TabName + "`";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);	
		}
	}
	/**
	 * 批量打印
	 */
	
	public ArrayList getAssItem(String T1,String EndYear, String EndDate, String bAssItemID,
			String eAssItemID, String SubjectType) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList listResult = new ArrayList();
		try {
			ArrayList b1 = new ArrayList();
			
			int end = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate);
			String acc = T1 + EndYear;
			String blAssItemID = "";
			String elAssItemID = "";
			
			String sql = "select distinct AssItemID from c_assitem where AccPackageID = ? and ? like concat(assitemid,'%') and level0=1 ";
			//System.out.println("sk：查询核算id的sql一："+"select distinct AssItemID from c_assitem where AccPackageID = "+acc+" and "+bAssItemID+" like concat(assitemid,'%') and level0=1 ");
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, bAssItemID);
			rs = ps.executeQuery();
			if(rs.next()){
				blAssItemID = rs.getString("AssItemID");
			}
			DbUtil.close(rs);
			
			ps.setString(2, eAssItemID);
			rs = ps.executeQuery();
			if(rs.next()){
				elAssItemID = rs.getString("AssItemID");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select distinct assitemid ,AssItemName,AssTotalName1 from c_assitementryacc where   subyearmonth * 12 + submonth = ? and assitemid>=? and assitemid<=? and level1=1 order by assitemid ";
			//System.out.println("sk：查询核算id的sql二："+"select distinct assitemid ,AssItemName,AssTotalName1 from c_assitementryacc where   subyearmonth * 12 + submonth = "+end+" and assitemid>="+blAssItemID+" and assitemid<="+elAssItemID+" and level1=1 order by assitemid  ");
			ps = conn.prepareStatement(sql);
			ps.setInt(1, end);
			ps.setString(2, blAssItemID);
			ps.setString(3, elAssItemID);
			rs = ps.executeQuery();
			while(rs.next()){
				
				listResult.add(new AssitemInfo(rs.getString("assitemid"), rs.getString("AssItemName")));
				
				b1.add(rs.getString("AssTotalName1"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if("0".equals(SubjectType)) {
				return listResult;	//返回1级
			}else{
				listResult = new ArrayList();
			}
			
			sql = "select distinct assitemid ,AssItemName,AssTotalName1 from c_assitementryacc where  subyearmonth * 12 + submonth = ? and (AssTotalName1 = ? or AssTotalName1 like concat(?,'/%')) and IsLeaf1=1 order by assitemid ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, end);
			for(int i=0 ;i< b1.size() ; i++){
				ps.setString(2, (String)b1.get(i));
				ps.setString(3, (String)b1.get(i));
				//System.out.println("sk：查询核算id的sql三："+"select distinct assitemid ,AssItemName,AssTotalName1 from c_assitementryacc where  subyearmonth * 12 + submonth = "+end+" and (AssTotalName1 = "+(String)b1.get(i)+" or AssTotalName1 like concat("+(String)b1.get(i)+",'/%')) and IsLeaf1=1 order by assitemid");
				rs = ps.executeQuery();
				while(rs.next()){
					listResult.add(new AssitemInfo(rs.getString("assitemid"), rs.getString("AssItemName")));
				}
				DbUtil.close(rs);
			}
			return listResult;	//返回所有的末级
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}			
	}

	public boolean isSplitBool() {
		return splitBool;
	}

	public void setSplitBool(boolean splitBool) {
		this.splitBool = splitBool;
	}
}
