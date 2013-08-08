package com.matech.audit.service.usersubject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class UserSubjectService {
	private Connection conn;
	
	public UserSubjectService(Connection conn){
		this.conn = conn;
	}
	
	public String getAutoSubject(String acc,String Month,String USubject) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ASFuntion CHF=new ASFuntion();
			StringBuffer sb = new StringBuffer("");
			String sql1 = "\n select '' accid,a.subjectId  ,SubjectName,SubjectFullName,ifnull(direction*Balance,0) bal,'' SID,ifnull(direction,case substring(a.Property,2,1) when 2 then -1 else 1 end) direction,ifnull(Balance,0) Balance, 0 levelid  " +
					"\n from c_accpkgsubject a  " +
					"\n left join c_account b on  b.accpackageid='"+acc+"' and submonth = '"+Month+"' and a.subjectId=b.subjectId " +
					"\n where a.accpackageid='"+acc+"' and a.isleaf=1 and a.AssistCode=1 " +
					"\n and not exists (select 1 from (select distinct accid from c_assitementryacc where accpackageid='"+acc+"') b where a.subjectid = accid) " +
					"\n and not exists (select 1 from c_usersubject where accpackageid='"+acc+"' and oriaccid='' and a.subjectid=orisubjectid)" +
					"\n union " +
					"\n select distinct accid,AssItemID,AssItemName,AssTotalName1,direction*Balance bal,b.subjectfullname,direction,Balance,levelid  " +
					"\n from c_assitementryacc a  " +
					"\n left join c_accpkgsubject b on b.accpackageid='"+acc+"' and b.AssistCode=1  " +
					//" and a.accid like concat(b.subjectId,'%') and b.level0=1 " +
					"\n and a.accid = b.subjectId " +
					"\n left join (select distinct AssItemID levelid,AssTotalName1 fullname from c_assitementryacc where level1 = 1 and accpackageid='"+acc+"') c" +
					"\n on a.AssTotalName1 = c.fullname or a.AssTotalName1 like concat(c.fullname,'/%') " + 
					"\n where a.accpackageid='"+acc+"' and SubMonth="+Month+" and isleaf1=1 and accid in (" +
					"\n select subjectId from c_accpkgsubject where accpackageid='"+acc+"' and isleaf=1 and AssistCode=1  ) " +
					"\n and not exists (select 1 from c_usersubject where accpackageid='"+acc+"' and a.accid=oriaccid and a.AssItemID=orisubjectid)" +
					"\n order by SubjectName ,concat(accid,subjectid)" ;
			
			if(!"".equals(USubject)){
				sql1 = " select * from ("+sql1+") a where SubjectName like '%"+USubject+"%' ";
			}
			
			String sql = "select a.*,cou,allbal from (" + sql1 + " ) a ,(" +
					"\n select SubjectName ,count(SubjectName) cou,direction*sum(Balance) allbal from (" + sql1 + ") a group by SubjectName having count(SubjectName)>1 order by count(SubjectName) desc " +
					"\n ) b where a.SubjectName = b.SubjectName order by a.SubjectName ,concat(accid,subjectid)";
			
			org.util.Debug.prtOut("py getAutoSubject:=|"+ sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String temName = "";
			int jj = 0;
			while(rs.next()){
				String SubjectName =  rs.getString("SubjectName");
				
				String accid =  rs.getString("accid");
				String subjectId =  rs.getString("subjectId");
				String SubjectFullName =  rs.getString("SubjectFullName");
				String bal =  rs.getString("bal");
				String SID =  rs.getString("SID");
				String cou =  rs.getString("cou");
				
				String allbal = rs.getString("allbal");
				
				String levelid =  rs.getString("levelid");
				
				if(!temName.toLowerCase().equals(SubjectName.toLowerCase())){
					jj ++;
					cou = " rowspan='" + cou + "' ";
					sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
					sb.append("<td "+cou+" align=\"left\" valign=\"middle\" >"+SubjectName+"</td>");
					sb.append(CHF.showMoney(allbal,cou));
					sb.append("<td "+cou+" align=\"center\" valign=\"middle\"><input type=button id='but' name='but' count='"+jj+"' sName='"+SubjectName+"' class=\"flyBT\" value=确认对照 onclick='return save(this);'/></td>");
					sb.append("<td align=\"left\" valign=\"middle\" nowrap>");
					
					if("".equals(accid)){
						sb.append("<input temName='"+SubjectName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SubjectFullName+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">"+subjectId+"</font>】"+SubjectFullName+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("<input temName='"+SubjectName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SID+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">科目 ："+accid +"</font>  ："+SID + "】");
						sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;［<font color=\"red\">核算  ："+subjectId+"</font>  ："+SubjectFullName+"］ ");
//						sb.append("");
					}
					sb.append("</td>");					
					sb.append(CHF.showMoney(bal,""));					
					sb.append("</tr>");
					temName = SubjectName;
				}else{
					sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
					sb.append("<td align=\"left\" valign=\"middle\" nowrap>");
					
					if("".equals(accid)){
						sb.append("<input temName='"+temName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SubjectFullName+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">"+subjectId+"</font>】"+SubjectFullName+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("<input temName='"+temName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SID+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">科目 ："+accid+"</font>  ："+SID + "】");
						sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;［<font color=\"red\">核算  ："+subjectId+"</font>  ："+SubjectFullName+"］ ");
//						sb.append("");
					}
					sb.append("</td>");
					sb.append(CHF.showMoney(bal,""));
					sb.append("</tr>");
				}
				sb.append("<input type='hidden' id='colcount' name='colcount' >");
			}
			sb.append("<input type='hidden' id='iCount' name='iCount' value='"+(jj)+"'>");
			sb.append("<input type='hidden' id='hiddenUSubject' name='hiddenUSubject' value='"+USubject+"'>");
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	
	public String getAutoSubject(String acc,String Month,String USubject,int pageSize,int pageBegin,int pageEnd) throws Exception{
		//pageCount = 10 每页多少行、pageBegin = 0  页开始、pageEnd = 1　页结束
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			DbUtil db = new DbUtil(conn);
			
			ASFuntion CHF=new ASFuntion();
			StringBuffer sb = new StringBuffer("");
			String sql1 = "\n	select '' accid,a.subjectId  ,SubjectName,SubjectFullName,ifnull(direction*Balance,0) bal,'' SID,ifnull(direction,case substring(a.Property,2,1) when 2 then -1 else 1 end) direction,ifnull(Balance,0) Balance, 0 levelid  " +
					"\n	from c_accpkgsubject a  " +
					"\n	left join c_account b on  b.accpackageid='"+acc+"' and submonth = '"+Month+"' and a.subjectId=b.subjectId " +
					"\n	where a.accpackageid='"+acc+"' and a.isleaf=1 and a.AssistCode=1 " +
					"\n	and not exists (select 1 from (" +
					
					"\n		select distinct accid  " +
					"\n		from c_assitementryacc a left join c_subjectassitem b" +
					"\n		on a.accpackageid='"+acc+"'" +
					"\n		and b.accpackageid='"+acc+"' " +
					"\n		and b.ifequal = 1" +
					"\n		and a.accid = b.subjectid" +
					"\n		where a.accpackageid='"+acc+"' " +
					"\n		and a.submonth = 1 " +
					"\n		and b.accpackageid is null " +
					
					"\n	) b where a.subjectid = accid) " +
					"\n	and not exists (select 1 from c_usersubject where accpackageid='"+acc+"' and oriaccid='' and a.subjectid=orisubjectid)" +
					
					"\n	union " +
					
					"\n	select distinct a.accid,a.AssItemID,a.AssItemName,a.AssTotalName1,direction*Balance bal,b.subjectfullname,direction,Balance,levelid  " +
					"\n	from c_assitementryacc a  " +
					"\n	left join c_accpkgsubject b on b.accpackageid='"+acc+"' and b.AssistCode=1 and a.accid = b.subjectId " +
					"\n	left join (" +
					"\n		select distinct AssItemID levelid,AssTotalName1 fullname from c_assitementryacc where level1 = 1 and accpackageid='"+acc+"'" +
					"\n	) c" +
					"\n	on a.AssTotalName1 = c.fullname or a.AssTotalName1 like concat(c.fullname,'/%') " +
					"\n	left join c_subjectassitem d on d.accpackageid='"+acc+"' and ifequal = 0 and a.accid = d.subjectid and d.AssItemID = c.levelid " +
					"\n	where a.accpackageid='"+acc+"' and SubMonth="+Month+" and isleaf1=1 " +
					"\n	and b.accpackageid is not null " +
					"\n	and d.accpackageid is not null " +
					"\n	and not exists (select 1 from c_usersubject where accpackageid='"+acc+"' and a.accid=oriaccid and a.AssItemID=orisubjectid)" ;

					//"\n	order by SubjectName ,concat(accid,subjectid)" ;
			
			if(!"".equals(USubject)){
				sql1 = " select * from ("+sql1+") a where SubjectName like '%"+USubject+"%' ";
			}
			
			String sql2 ="\n select SubjectName ,count(SubjectName) cou,direction*sum(Balance) allbal,abs(direction*sum(Balance)) allbal1 from (" + sql1 + 
			"\n ) a group by SubjectName having count(SubjectName)>1 " + 
			"order by allbal1 desc,SubjectName ,concat(accid,subjectid)  " ; 
//			System.out.println(sql2);
			ps = conn.prepareStatement(sql2);
			rs = ps.executeQuery();
			
			int rowCount = db.getRowCount(rs);
			int limit1 = 0,limit2 = 0 , limit = 0 ,opt = 0 ;
			
			while(rs.next() && opt < pageEnd * pageSize ){
				if(opt == pageBegin * pageSize){
					limit1 = limit;
					limit = 0;
				}
				
				limit += rs.getInt("cou");
				
				if(opt == pageEnd * pageSize - 1){
					limit2 = limit;
				}
				opt ++;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if(limit2 == 0){
				limit2 = limit;
			}
			
//			System.out.println("limit:"+limit1+ "|" + limit2);
			
			String sql = "select a.*,cou,allbal from (" + sql1 + " ) a ,(" +
					sql2 +
					"\n ) b where a.SubjectName = b.SubjectName order by b.allbal1 desc,a.SubjectName ,concat(accid,subjectid) ";
			sql += " limit " + limit1 + "," + limit2;
			
			
			org.util.Debug.prtOut("py getAutoSubject:=|"+ sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String temName = "";
			int jj = 0;
			while(rs.next() ){
				String SubjectName =  rs.getString("SubjectName");
				
				String accid =  rs.getString("accid");
				String subjectId =  rs.getString("subjectId");
				String SubjectFullName =  rs.getString("SubjectFullName");
				String bal =  rs.getString("bal");
				String SID =  rs.getString("SID");
				String cou =  rs.getString("cou");
				
				String allbal = rs.getString("allbal");
				
				String levelid =  rs.getString("levelid");
				
//				System.out.println((iii++) + "|" +accid + "|"+subjectId + "|" +SubjectName);
				if(!temName.toLowerCase().equals(SubjectName.toLowerCase())){
					jj ++;
					cou = " rowspan='" + cou + "' ";
					sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
					sb.append("<td "+cou+" align=\"left\" valign=\"middle\" >"+SubjectName+"</td>");
					sb.append(CHF.showMoney(allbal,cou));
					sb.append("<td "+cou+" align=\"center\" valign=\"middle\"><input type=button id='but' name='but' count='"+jj+"' sName='"+SubjectName+"' class=\"flyBT\" value=确认对照 onclick='return save(this);'/></td>");
					sb.append("<td align=\"left\" valign=\"middle\" nowrap>");
					
					if("".equals(accid)){
						sb.append("<input temName='"+SubjectName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SubjectFullName+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">"+subjectId+"</font>】"+SubjectFullName+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("<input temName='"+SubjectName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SID+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">科目 ："+accid +"</font>  ："+SID + "】");
						sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;［<font color=\"red\">核算  ："+subjectId+"</font>  ："+SubjectFullName+"］ ");
					}
					sb.append("</td>");					 
					sb.append(CHF.showMoney(bal,""));					
					sb.append("</tr>");
					temName = SubjectName;
				}else{
					sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
					sb.append("<td align=\"left\" valign=\"middle\" nowrap>");
					
					if("".equals(accid)){
						sb.append("<input temName='"+temName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SubjectFullName+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">"+subjectId+"</font>】"+SubjectFullName+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("<input temName='"+temName+"' levelid='"+levelid+"' checked name=\"ori"+jj+"\" id=\"ori"+jj+"\" type=\"checkbox\" sname='"+SID+"' value=\""+subjectId+"\" accid = \""+accid+"\">【<font color=\"#0000FF\">科目 ："+accid+"</font>  ："+SID + "】");
						sb.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;［<font color=\"red\">核算  ："+subjectId+"</font>  ："+SubjectFullName+"］ ");
					}
					sb.append("</td>");
					sb.append(CHF.showMoney(bal,""));
					sb.append("</tr>");
				}
				sb.append("<input type='hidden' id='colcount' name='colcount' >");
			}

			sb.append("<input type='hidden' id='iCount' name='iCount' value='"+(jj)+"'>");
			sb.append("<input type='hidden' id='hiddenUSubject' name='hiddenUSubject' value='"+USubject+"'>");

			sb.append("<tr height=20  bgColor=\"#EEEEEE\"><td colspan=100 align=\"right\">");

			sb.append(selectPage(rowCount, pageSize,  pageEnd));
			
			sb.append("</td></tr>");
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public StringBuffer selectPage(int rowCount,int pageSize, int currentPage) throws Exception {
		StringBuffer sb = new StringBuffer("");
		int firstPage = 1;
		int lastPage = (rowCount + (pageSize - 1)) / pageSize;
		if (rowCount == 0) {
			lastPage = 1;
		}
		
		if (currentPage == firstPage) {
			sb.append("<font color=\"#e3e3e3\">【首页】&nbsp;〖上一页〗</font>");
		} else {
			sb.append("<a style=\"cursor:hand\" onClick=\"changeGrid('page',"+ firstPage+ ");\">【首页】</a>&nbsp;<a style=\"cursor:hand\" onClick=\"changeGrid('page',"+ (currentPage - 1) + ");\">〖上一页〗</a>");
		}
		if (currentPage == lastPage) {
			sb.append("<font color=\"#e3e3e3\">〖下一页〗&nbsp;【末页】</font>");
		} else {
			sb.append("<a style=\"cursor:hand\" onClick=\"changeGrid('page',"+ (currentPage + 1)+ ");\">〖下一页〗</a>&nbsp;<a style=\"cursor:hand\" onClick=\"changeGrid('page',"+ lastPage+ ");\">【末页】</a>");
		}

		sb.append("&nbsp;&nbsp;每页<input type=text name=countPage size=5 value="+ pageSize+ " onkeydown=\"onKeyDownPage();\" onkeyup=\"return getRecords('pageSize',this.value); \">条&nbsp;&nbsp;直接查看第<select size=\"1\" name=\"selectPage\" onchange=\"changeGrid('page',this.options[this.selectedIndex].value)\" style='height:18px;width:50px;'>");

		String strSelected = "";
		for (int i = 1; i <= lastPage; i++) {
			if (currentPage == i) {
				strSelected = " selected";
			} else {
				strSelected = "";
			}
			sb.append("    <option value=\"" + i + "\"" + strSelected + ">"
					+ i + "</option>");
		}
		sb.append("</select>/" + lastPage+ "页&nbsp;共有记录组：<span id=\"colcount\">" + rowCount + "</span>");
		sb.append("<input id=\"rowCount\" name=\"rowCount\" type=\"hidden\" value=\"" + rowCount +"\" >");

		return sb;
	}
	
	
	
	public String getSubjectList(String acc,String Month) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		String type = "";
		try {
			StringBuffer sb = new StringBuffer("");
			ASFuntion CHF=new ASFuntion();
			String sql1 = "select distinct AccPackageID, a.accid, a.SubjectID, SubjectName, SubjectFullName, direction, " +
					" oriaccid, orisubjectid, orisubjectname, orisubjectfullname, oridirection, 	" +
					" oriParentSubjectID, oriParentDirection, Property ,oriParentSubjectID1,oriParentName1," +
					" Balance,fullname,oridirection*Balance oriBalance " +
					" from c_usersubject a left join (" +
					" select '' accid,a.SubjectID ,ifnull(Balance,0) Balance ,SubjectFullName fullname" +
					" from c_accpkgsubject a left join c_account b " +
					" on b.AccPackageID = '"+acc+"' and b.submonth="+Month+" and a.SubjectID=b.SubjectID" +
					" where a.AccPackageID = '"+acc+"' and isleaf=1" +
					" union " +
					" select accid,assitemid ,Balance Balance ,b.SubjectFullName" +
					" from c_assitementryacc a left join c_accpkgsubject b " +
					" on b.AccPackageID = '"+acc+"' and a.accid=b.SubjectID " +
					" where a.AccPackageID = '"+acc+"' and submonth="+Month+" and isleaf1=1" +
					" ) b on concat(a.oriaccid,a.orisubjectid) = concat(b.accid,b.subjectid) " +
					" where AccPackageID = '"+acc+"' ";
						
			String sql = "select a.*,fname,cou,bal from ("+sql1+") a left join (" +
					" select distinct accid,subjectid,subjectname,fullname fname,count(subjectid) cou,sum(direction*Balance) bal  from (" +
					sql1 +" ) a group by concat(accid,subjectid) " +
					" ) b on concat(a.accid,a.subjectid) =concat(b.accid,b.subjectid) " +
					" order by concat(a.accid,a.subjectid) ,concat(a.oriaccid,a.orisubjectid)";
			org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String temName = "";
			int jj = 0;
			
			while(rs.next()){
				String accid = rs.getString("accid");
				String subjectid = rs.getString("subjectid");
				String subjectname = rs.getString("subjectname");
				
				String orisubjectid = rs.getString("orisubjectid");
				String orisubjectfullname = rs.getString("orisubjectfullname");
				String oriaccid = rs.getString("oriaccid");
				
				String oriParentSubjectID1 = rs.getString("oriParentSubjectID1");
				String fullname = rs.getString("fullname");
				
				String oriParentName1  = rs.getString("oriParentName1");
				if("".equals(accid)){
					type = "<font color=\"#0000FF\">(科目)</font>";
				} else {
					type = "<font color=\"red\">(核算)</font>  <font color=\"#0000FF\">科目："+accid + "</font>";
				}
				
				String oriBalance = rs.getString("oriBalance");
				
				if(!temName.equals(subjectname)){
					String cou = " rowspan='" + rs.getString("cou") + "' ";
					
					String bal = rs.getString("bal");
					
					sb.append("<tr  bgColor=\"#f3f5f8\" >");
					sb.append("<td  "+cou+"  align=\"left\" valign=\"middle\" >【"+subjectid +"】"+subjectname+type+"</td>");
					
					sb.append(CHF.showMoney(bal,cou));
					
					sb.append("<td  "+cou+"  align=\"center\" valign=\"middle\"><input type=button id='but' name='but' AccPackageID='"+acc+"' subjectid='"+subjectid+"' accid='"+accid+"' class=\"flyBT\" value=\"修改\" onclick=\"return update(this);\"/> "+
							" <input type=button id='del' name='del' AccPackageID='"+acc+"' subjectid='"+subjectid+"' accid='"+accid+"' class=\"flyBT\" value=\"删除\" onclick=\"return goDelete(this);\"/></td>");
					sb.append("<td align=\"left\" valign=\"middle\" >");
					
					if("".equals(oriaccid)){
						sb.append("【<font color=\"#0000FF\">"+orisubjectid+"</font>】"+orisubjectfullname+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("【<font color=\"#0000FF\">科目 ："+oriaccid+"</font>  ："+fullname+"】<br>");
						sb.append("［<font color=\"red\">核算 ："+orisubjectid+"</font>  ："+orisubjectfullname+"］");
					}
					sb.append("</td>");
					sb.append(CHF.showMoney(oriBalance,""));
					sb.append("</tr>");
					
					temName = subjectname;
				}else{
					sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
					sb.append("<td align=\"left\" valign=\"middle\" >");
					
					if("".equals(oriaccid)){
						sb.append("【<font color=\"#0000FF\">"+orisubjectid+"</font>】"+orisubjectfullname+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("【<font color=\"#0000FF\">科目 ："+oriaccid+"</font>  ："+fullname+"】<br>");
						sb.append("［<font color=\"red\">核算 ："+orisubjectid+"</font>  ："+orisubjectfullname+"］");
					}
					sb.append("</td>");
					sb.append(CHF.showMoney(oriBalance,""));
					sb.append("</tr>");
				}
				
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	public String getSubjectList(String acc,String Month,int pageSize,int pageBegin,int pageEnd) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		String type = "";
		try {
			DbUtil db = new DbUtil(conn);
			
			StringBuffer sb = new StringBuffer("");
			ASFuntion CHF=new ASFuntion();
			String sql1 = "select distinct AccPackageID, a.accid, a.SubjectID, SubjectName, SubjectFullName, direction, " +
					" oriaccid, orisubjectid, orisubjectname, orisubjectfullname, oridirection, 	" +
					" oriParentSubjectID, oriParentDirection, Property ,oriParentSubjectID1,oriParentName1," +
					" Balance,fullname,oridirection*Balance oriBalance " +
					" from c_usersubject a left join (" +
					" select '' accid,a.SubjectID ,ifnull(Balance,0) Balance ,SubjectFullName fullname" +
					" from c_accpkgsubject a left join c_account b " +
					" on b.AccPackageID = '"+acc+"' and b.submonth="+Month+" and a.SubjectID=b.SubjectID" +
					" where a.AccPackageID = '"+acc+"' and isleaf=1" +
					" union " +
					" select accid,assitemid ,Balance Balance ,b.SubjectFullName" +
					" from c_assitementryacc a left join c_accpkgsubject b " +
					" on b.AccPackageID = '"+acc+"' and a.accid=b.SubjectID " +
					" where a.AccPackageID = '"+acc+"' and submonth="+Month+" and isleaf1=1" +
					" ) b on concat(a.oriaccid,a.orisubjectid) = concat(b.accid,b.subjectid) " +
					" where AccPackageID = '"+acc+"' ";
					
			String sql2 = " select distinct accid,subjectid,subjectname,fullname fname,count(subjectid) cou,sum(direction*Balance) bal  from (" +
					sql1 
					+" ) a group by concat(accid,subjectid) order by concat(a.accid,a.subjectid) ,concat(a.oriaccid,a.orisubjectid) " ;
			ps = conn.prepareStatement(sql2);
			rs = ps.executeQuery();
			
			int rowCount = db.getRowCount(rs);
			int limit1 = 0,limit2 = 0 , limit = 0 ,opt = 0 ;
			
			while(rs.next() && opt < pageEnd * pageSize ){
				if(opt == pageBegin * pageSize){
					limit1 = limit;
					limit = 0;
				}
				
				limit += rs.getInt("cou");
				
				if(opt == pageEnd * pageSize - 1){
					limit2 = limit;
				}
				opt ++;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if(limit2 == 0){
				limit2 = limit;
			}
			
			String sql = "select a.*,fname,cou,bal from ("+sql1+") a left join (" +
					sql2 + 
					" ) b on concat(a.accid,a.subjectid) =concat(b.accid,b.subjectid) " +
					" order by concat(a.accid,a.subjectid) ,concat(a.oriaccid,a.orisubjectid)";
			
			sql += " limit " + limit1 + "," + limit2;
			
			org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String temName = "";
			int jj = 0;
			
			while(rs.next()){
				String accid = rs.getString("accid");
				String subjectid = rs.getString("subjectid");
				String subjectname = rs.getString("subjectname");
				
				String orisubjectid = rs.getString("orisubjectid");
				String orisubjectfullname = rs.getString("orisubjectfullname");
				String oriaccid = rs.getString("oriaccid");
				
				String oriParentSubjectID1 = rs.getString("oriParentSubjectID1");
				String fullname = rs.getString("fullname");
				
				String oriParentName1  = rs.getString("oriParentName1");
				if("".equals(accid)){
					type = "<font color=\"#0000FF\">(科目)</font>";
				} else {
					type = "<font color=\"red\">(核算)</font>  <font color=\"#0000FF\">科目："+accid + "</font>";
				}
				
				String oriBalance = rs.getString("oriBalance");
				
				if(!temName.equals(subjectname)){
					String cou = " rowspan='" + rs.getString("cou") + "' ";
					
					String bal = rs.getString("bal");
					
					sb.append("<tr  bgColor=\"#f3f5f8\" >");
					sb.append("<td  "+cou+"  align=\"left\" valign=\"middle\" >【"+subjectid +"】"+subjectname+type+"</td>");
					
					sb.append(CHF.showMoney(bal,cou));
					
					sb.append("<td  "+cou+"  align=\"center\" valign=\"middle\"><input type=button id='but' name='but' AccPackageID='"+acc+"' subjectid='"+subjectid+"' accid='"+accid+"' class=\"flyBT\" value=\"修改\" onclick=\"return update(this);\"/> "+
							" <input type=button id='del' name='del' AccPackageID='"+acc+"' subjectid='"+subjectid+"' accid='"+accid+"' class=\"flyBT\" value=\"删除\" onclick=\"return goDelete(this);\"/></td>");
					sb.append("<td align=\"left\" valign=\"middle\" >");
					
					if("".equals(oriaccid)){
						sb.append("【<font color=\"#0000FF\">"+orisubjectid+"</font>】"+orisubjectfullname+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("【<font color=\"#0000FF\">科目 ："+oriaccid+"</font>  ："+fullname+"】<br>");
						sb.append("［<font color=\"red\">核算 ："+orisubjectid+"</font>  ："+orisubjectfullname+"］");
					}
					sb.append("</td>");
					sb.append(CHF.showMoney(oriBalance,""));
					sb.append("</tr>");
					
					temName = subjectname;
				}else{
					sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
					sb.append("<td align=\"left\" valign=\"middle\" >");
					
					if("".equals(oriaccid)){
						sb.append("【<font color=\"#0000FF\">"+orisubjectid+"</font>】"+orisubjectfullname+" ");
						sb.append("<font color=\"#0000FF\">(科目)</font>");
					}else{
						sb.append("【<font color=\"#0000FF\">科目 ："+oriaccid+"</font>  ："+fullname+"】<br>");
						sb.append("［<font color=\"red\">核算 ："+orisubjectid+"</font>  ："+orisubjectfullname+"］");
					}
					sb.append("</td>");
					sb.append(CHF.showMoney(oriBalance,""));
					sb.append("</tr>");
				}
				
			}
			
			sb.append("<tr height=20  bgColor=\"#EEEEEE\"><td colspan=100 align=\"right\">");

			sb.append(selectPage(rowCount, pageSize,  pageEnd));
			
			sb.append("</td></tr>");
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	public String getSubjectTable(String acc,String sName,String Month) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer("");
			ASFuntion CHF=new ASFuntion();
			if(!"".equals(sName)){
				sName = " and SubjectFullName like '%"+sName+"%'";
			}
			String sql = "select * from (" +
					"\n		select '' accid,a.subjectid,a.SubjectName,a.SubjectFullName,'' SID ,ifnull(b.direction* b.Balance,0) Balance " +
					"\n		from c_accpkgsubject a " +
					"\n		left join c_account b on b.submonth="+Month+" and b.AccPackageID ='"+acc+"' and a.subjectid=b.subjectid " +
					"\n		where a.AccPackageID ='"+acc+"' and a.isleaf=1 and a.AssistCode=1" +
//					"\n	and a.subjectid not in (select distinct accid from c_assitementryacc where accpackageid='"+acc+"' and SubMonth=1 and isleaf1=1)" +
					"\n		and not exists (select 1 from (" +
					
					"\n			select distinct accid  " +
					"\n			from c_assitementryacc a left join c_subjectassitem b" +
					"\n			on a.accpackageid='"+acc+"'" +
					"\n			and b.accpackageid='"+acc+"' " +
					"\n			and b.ifequal = 1" +
					"\n			and a.accid = b.subjectid" +
					"\n			where a.accpackageid='"+acc+"' " +
					"\n			and a.submonth = 1 " +
					"\n			and b.accpackageid is null " +
					
					"\n		) b where a.subjectid = accid) " +
					"\n		union " +
					"\n		select a.AccID,a.AssItemID,a.AssItemName,a.AssTotalName1,b.SubjectFullName ,ifnull(a.direction* a.Balance,0) Balance  " +
					"\n		from c_assitementryacc a " +
					"\n		left join c_accpkgsubject b on b.AccPackageID ='"+acc+"'  and a.accid =b.subjectid " +
					"\n		left join c_subjectassitem c on c.accpackageid = '"+acc+"' and ifequal = 0 and a.accid = c.subjectid and (a.AssTotalName1 = c.AssTotalName1 or a.AssTotalName1 like concat(c.AssTotalName1,'/%'))" +
					
					"\n		where a.AccPackageID ='"+acc+"' and SubMonth="+Month+" and isleaf1=1" +
					"\n		and c.accpackageid is not null" +
					"\n		and accid in (select subjectId from c_accpkgsubject where accpackageid='"+acc+"' and isleaf=1 and AssistCode=1  )" +
					"\n	) a where 1=1 " + sName +
					" order by  concat(accid,subjectid)";
			
//			System.out.println("getSubjectTable :" +sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii = 1;
			while(rs.next()){				
				if(ii==1){
					sb.append("<table  cellSpacing=\"1\" cellPadding=\"2\" width=\"100%\"  bgColor=\"#6595d6\" border=\"0\">");
					sb.append("<tr class=\"DGtd\" height=\"20\">");
					sb.append("<td  height=\"20\" colspan='2' align=\"middle\"  bgColor=\"#b9c4d5\">往来帐明细</td>");
					sb.append("</tr>");
				}
				String accid = rs.getString("accid");
				String subjectId = rs.getString("subjectId");
				String SubjectFullName = rs.getString("SubjectFullName");
				String SID = rs.getString("SID");
				
				String Balance = rs.getString("Balance");
				
				sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
				sb.append("<td align=\"left\" valign=\"middle\" >");
				if("".equals(accid)){
					sb.append("<input onclick=\"addTarget(this);\" name=\"Subject\" sname='"+SubjectFullName+"' id=\"Subject\" type=\"checkbox\" value=\""+subjectId+"\" accid=\""+accid+"\">【<font color=\"#0000FF\">"+subjectId+"</font>】"+SubjectFullName+" ");
					sb.append("<font color=\"#0000FF\">(科目)</font>");
				}else{
					sb.append("<input onclick=\"addTarget(this);\" name=\"Subject\" sname='"+SID+"' id=\"Subject\" type=\"checkbox\" value=\""+subjectId+"\" accid=\""+accid+"\">【<font color=\"#0000FF\">科目："+accid+"</font>  ："+SID + "】<br>［<font color=\"red\">核算："+subjectId+"</font>："+SubjectFullName+"］ ");
				}
				sb.append("</td>");
				sb.append(CHF.showMoney(Balance,""));
				sb.append("</tr>");
								
				ii ++ ;
			}
			if(ii != 1) {
				sb.append("</table>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	public String getSearchTable(String acc,String subjectID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer("");
			String sql = "select '' accid,subjectid,SubjectName,SubjectFullName " +
					"\n	from c_accpkgsubject a " +
					"\n	where AccPackageID ='"+acc+"' and isleaf=1 and AssistCode=1" +
//					"\n	and subjectid not in (select distinct accid from c_assitementryacc where accpackageid='"+acc+"' and SubMonth=1 and isleaf1=1)" +
					"\n	and not exists (select 1 from (" +
					
					"\n		select distinct accid  " +
					"\n		from c_assitementryacc a left join c_subjectassitem b" +
					"\n		on a.accpackageid='"+acc+"'" +
					"\n		and b.accpackageid='"+acc+"' " +
					"\n		and b.ifequal = 1" +
					"\n		and a.accid = b.subjectid" +
					"\n		where a.accpackageid='"+acc+"' " +
					"\n		and a.submonth = 1 " +
					"\n		and b.accpackageid is null " +
					
					"\n	) b where a.subjectid = accid) " +
					"\n	and SubjectFullName like (select concat(SubjectFullName,'%') from c_accpkgsubject where AccPackageID ='"+acc+"' and subjectid ='"+subjectID+"') " +					
					"\n	union " +
					"\n	select a.AccID,a.AssItemID,a.AssItemName,a.AssTotalName1 " +
					"\n	from c_assitementryacc a " +
					"\n	left join c_accpkgsubject b on b.AccPackageID ='"+acc+"' and a.accid = b.subjectid " +
					
					"\n	left join c_subjectassitem c on c.accpackageid = '"+acc+"' and ifequal = 0 and a.accid = c.subjectid and (a.AssTotalName1 = c.AssTotalName1 or a.AssTotalName1 like concat(c.AssTotalName1,'/%'))" +
					
					"\n	where a.AccPackageID ='"+acc+"' and SubMonth=1 and isleaf1=1" +
					"\n	and c.accpackageid is not null" +
					"\n	and accid in (select subjectId from c_accpkgsubject where accpackageid='"+acc+"' and isleaf=1 and AssistCode=1  )" +
					"\n	and SubjectFullName like (select concat(SubjectFullName,'%') from c_accpkgsubject where AccPackageID ='"+acc+"' and subjectid ='"+subjectID+"') " +
					"\n	order by subjectid";
			System.out.println("getSearchTable: "+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii = 1;
			while(rs.next()){				
				if(ii==1){
					sb.append("<table  cellSpacing=\"1\" cellPadding=\"2\" width=\"100%\"  bgColor=\"#6595d6\" border=\"0\">");
					sb.append("<tr class=\"DGtd\" height=\"20\">");
					sb.append("<td  height=\"20\"  align=\"middle\"  bgColor=\"#b9c4d5\">多科目对照</td>");
					sb.append("</tr>");
				}
				String accid = rs.getString("accid");
				String subjectId = rs.getString("subjectId");
				String SubjectFullName = rs.getString("SubjectFullName");
				
				sb.append("<tr  bgColor=\"#f3f5f8\" height=\"18\">");
				sb.append("<td align=\"left\" valign=\"middle\" >");
				if("".equals(accid)){
					sb.append("<input onclick=\"getTarget('"+accid+"','"+subjectId+"');\" name=\"Search\" id=\"Search\" type=\"radio\" accid='"+accid+"' value=\""+subjectId+"\">【<font color=\"#0000FF\">"+subjectId+"</font>】"+SubjectFullName+" ");
					sb.append("<font color=\"#0000FF\">(科目)</font>");
				}else{
					sb.append("<input onclick=\"getTarget('"+accid+"','"+subjectId+"');\" name=\"Search\" id=\"Search\" type=\"radio\" accid='"+accid+"' value=\""+subjectId+"\">【<font color=\"red\">"+subjectId+"</font>】"+SubjectFullName+" ");
					sb.append("<font color=\"red\">(核算)</font>　<font color=\"#0000FF\">科目："+accid + "</font>");
				}
				sb.append("</td>");
				sb.append("</tr>");
								
				ii ++ ;
			}
			if(ii != 1) {
				sb.append("</table>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);			
			DbUtil.close(ps);
		}
	}
	
	public String getTargetTable(String acc,String subjectID,String accid,String Month) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
//		ResultSet rs2 = null;
		try {
			
			ASFuntion CHF=new ASFuntion();
			
			StringBuffer sb = new StringBuffer("");
			
			
			String sql = "";
			if("".equals(accid)){
				sql = "select SubjectName from c_accpkgsubject where AccPackageID = '"+acc+"' and SubjectID = '"+subjectID+"'";
			}else{
				sql = "select AssItemName from c_assitementryacc where AccPackageID = '"+acc+"' and AccID = '"+accid+"' and AssItemID = '"+subjectID+"' and SubMonth=1";
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String Name = "";
			if(rs.next()){
				Name = rs.getString(1);
			}
			rs.close();
			ps.close();
			
			int ii = 1;
			
			int i1 = 0;
			int i2 = 0;
			String s1 = "";
//			String s2 = "";
			
//			sql = "select distinct oriaccid,orisubjectid,orisubjectfullname,oriParentSubjectID1,oriParentName1 from c_usersubject where AccPackageID = '"+acc+"' " +
//				" and concat(accid,SubjectID) =(select distinct concat(accid,SubjectID) from c_usersubject where AccPackageID = '"+acc+"' and orisubjectid = '"+subjectID+"' and oriaccid='"+accid+"') " +
//				" order by concat(accid,SubjectID)";
			
			sql = "select distinct oriaccid,orisubjectid,orisubjectname,orisubjectfullname,oriParentSubjectID1,oriParentName1,ifnull(Balance,0) Balance,ifnull(SNAME ,'') SNAME  " +
				"\n	from c_usersubject a left join (" +
				"\n		select '' accid,subjectid ,direction* Balance Balance,'' SNAME " +
				"\n		from c_account a where submonth="+Month+" and AccPackageID ='"+acc+"' and isleaf1=1 " +
//				"\n		and subjectid not in (select distinct accid from c_assitementryacc where accpackageid='"+acc+"' and SubMonth=1 and isleaf1=1) " +
				"\n		and not exists (select 1 from (" +
				
				"\n			select distinct accid  " +
				"\n			from c_assitementryacc a left join c_subjectassitem b" +
				"\n			on a.accpackageid='"+acc+"'" +
				"\n			and b.accpackageid='"+acc+"' " +
				"\n			and b.ifequal = 1" +
				"\n			and a.accid = b.subjectid" +
				"\n			where a.accpackageid='"+acc+"' " +
				"\n			and a.submonth = 1 " +
				"\n			and b.accpackageid is null " +
				
				"\n		) b where a.subjectid = accid) " +
				"\n		union " +
				"\n		select a.accid,a.assitemid,direction* Balance Balance ,b.SubjectFullName SNAME" +
				"\n		from c_assitementryacc a " +
				"\n		left join c_accpkgsubject b on b.AccPackageID ='"+acc+"'  and a.accid =b.subjectid" +
				"\n		left join c_subjectassitem c on c.accpackageid = '"+acc+"' and ifequal = 0 and a.accid = c.subjectid and (a.AssTotalName1 = c.AssTotalName1 or a.AssTotalName1 like concat(c.AssTotalName1,'/%'))" +
				
				"\n		where submonth="+Month+" and a.AccPackageID ='"+acc+"' and isleaf1=1" +
				"\n		and c.accpackageid is not null" +
				"\n	) b on concat(oriaccid,orisubjectid) = concat(b.accid,b.subjectid)" +
				"\n	where AccPackageID = '"+acc+"'  " +
				"\n	and concat(a.accid,a.SubjectID) =(select distinct concat(accid,SubjectID) from c_usersubject where AccPackageID = '"+acc+"' and orisubjectid = '"+subjectID+"' and oriaccid='"+accid+"')  " +
				"\n	order by concat(a.accid,a.SubjectID)";
			
			org.util.Debug.prtOut("getTargetTable sql:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			StringBuffer sb1 = new StringBuffer("");
			while(rs.next()){
				String oriaccid = rs.getString("oriaccid");
				String orisubjectid = rs.getString("orisubjectid");
				String orisubjectfullname = rs.getString("orisubjectfullname");
				String orisubjectname = rs.getString("orisubjectname");

				String Balance = rs.getString("Balance");
				
				String SNAME = rs.getString("SNAME");
				
//				String name = SNAME == null? orisubjectfullname : SNAME;
				
				s1 += " and (subjectId <>'"+orisubjectid+"' or accid<>'"+oriaccid+"') ";

				sb1.append("<tr bgColor=\"#f3f5f8\" height=\"18\">");
				sb1.append("<td align=\"left\" valign=\"middle\" nowrap>");
				
				if("".equals(oriaccid)){
					sb1.append("<input sname='"+orisubjectfullname+"' checked name=\"Target\" id=\"Target\" type=\"checkbox\" value=\""+orisubjectid+"\" accid=\""+oriaccid+"\">【<font color=\"#0000FF\">"+orisubjectid+"</font>】"+orisubjectfullname+" ");
					sb1.append("<font color=\"#0000FF\">(科目)</font>");
				}else{
					sb1.append("<input sname='"+SNAME+"' checked name=\"Target\" id=\"Target\" type=\"checkbox\" value=\""+orisubjectid+"\" accid=\""+oriaccid+"\">【<font color=\"#0000FF\">科目："+oriaccid+"</font>  ："+SNAME+"】［<font color=\"red\">核算："+orisubjectid+"</font>："+orisubjectfullname+"］ ");
//					sb1.append("<font color=\"red\">(核算："+orisubjectid+")</font>");
				}
				sb1.append("</td>");
				sb1.append(CHF.showMoney(Balance,""));
				sb1.append("</tr>");

				
				ii ++ ;
				
				i1 = 1;
			}
			rs.close();
			ps.close();
			org.util.Debug.prtOut("1:"+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			
			sql = "select * from (" +
				"\n		select '' accid,a.subjectId  ,a.SubjectName,a.SubjectFullName ,'' SID ,ifnull(direction* Balance,0) Balance " +
				"\n		from c_accpkgsubject a " +
				"\n		left join c_account b " +
				"\n		on b.accpackageid='"+acc+"' and b.submonth="+Month+" and a.subjectId=b.subjectId " +
				"\n		where a.accpackageid='"+acc+"' and a.isleaf=1 and a.AssistCode=1 " +
//				"\n	and a.subjectid not in (select distinct accid from c_assitementryacc where accpackageid='"+acc+"' and SubMonth=1 and isleaf1=1)" +
				"\n		and not exists (select 1 from (" +
				
				"\n			select distinct accid  " +
				"\n			from c_assitementryacc a left join c_subjectassitem b" +
				"\n			on a.accpackageid='"+acc+"'" +
				"\n			and b.accpackageid='"+acc+"' " +
				"\n			and b.ifequal = 1" +
				"\n			and a.accid = b.subjectid" +
				"\n			where a.accpackageid='"+acc+"' " +
				"\n			and a.submonth = 1 " +
				"\n			and b.accpackageid is null " +
				
				"\n		) b where a.subjectid = accid) " +
				"\n		union " +
				"\n		select a.AccID,a.AssItemID,a.AssItemName,a.AssTotalName1,b.subjectfullName ,ifnull(direction* Balance,0) Balance " +
				"\n		from c_assitementryacc a " +
				"\n		left join c_accpkgsubject b on b.AccPackageID ='"+acc+"' and a.accid =b.subjectid " +
				"\n		left join c_subjectassitem c on c.accpackageid = '"+acc+"' and ifequal = 0 and a.accid = c.subjectid and (a.AssTotalName1 = c.AssTotalName1 or a.AssTotalName1 like concat(c.AssTotalName1,'/%'))" +
				"\n		where a.AccPackageID ='"+acc+"' and SubMonth="+Month+" and isleaf1=1" +
				"\n		and c.accpackageid is not null" +
				"\n		and accid in (select subjectId from c_accpkgsubject where accpackageid='"+acc+"' and isleaf=1 and AssistCode=1  )" +
				"\n	) a where SubjectName='"+Name+"' " + s1 +
				"\n	order by concat(accid,SubjectID)";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			StringBuffer sb2 = new StringBuffer("");
			while(rs.next()){
				String oriaccid = rs.getString("accid");
				String orisubjectid = rs.getString("subjectId");
				String orisubjectfullname = rs.getString("SubjectFullName");
				
				String SID = rs.getString("SID");
				String Balance = rs.getString("Balance");
				
//				String name = SID == null? orisubjectfullname : SID;
				
				sb2.append("<tr bgColor=\"#f3f5f8\" height=\"18\">");
				sb2.append("<td align=\"left\" valign=\"middle\" nowrap>");
				
				if("".equals(oriaccid)){
					sb2.append("<input sname='"+orisubjectfullname+"' checked name=\"Target\" id=\"Target\" type=\"checkbox\" value=\""+orisubjectid+"\" accid=\""+oriaccid+"\">【<font color=\"#0000FF\">"+orisubjectid+"</font>】"+orisubjectfullname+" ");
					sb2.append("<font color=\"#0000FF\">(科目)</font>");
				}else{
					sb2.append("<input sname='"+SID+"' checked name=\"Target\" id=\"Target\" type=\"checkbox\" value=\""+orisubjectid+"\" accid=\""+oriaccid+"\">【<font color=\"#0000FF\">科目："+oriaccid+"</font>  ："+SID+"】［<font color=\"red\">核算："+orisubjectid+"</font>："+orisubjectfullname+"］ ");
//					sb2.append("<font color=\"red\">(核算："+orisubjectid+")</font>");
				}
				sb2.append("</td>");
				sb2.append(CHF.showMoney(Balance,""));
				sb2.append("</tr>");
				
				ii ++ ;
				
				i2 = 1;
			}
			rs.close();
			ps.close();
			org.util.Debug.prtOut("2:"+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			
//			sb.append("<table id=\"target\" cellSpacing=\"1\" cellPadding=\"2\" width=\"100%\"  bgColor=\"#6595d6\" border=\"0\">");
			sb.append("<table  cellSpacing=\"1\" cellPadding=\"2\" width=\"100%\"  bgColor=\"#6595d6\" border=\"0\">");
			sb.append("<tr class=\"DGtd\" height=\"20\">");
			sb.append("<td  height=\"20\" align=\"middle\"  style=\"CURSOR: hand\"  noWrap bgColor=\"#b9c4d5\"><b>对照科目</b></td>");
			sb.append("<td  align=\"middle\"  style=\"CURSOR: hand\"  noWrap bgColor=\"#b9c4d5\"><b>操作</b></td>");
			sb.append("<td  align=\"middle\"  style=\"CURSOR: hand\"  noWrap bgColor=\"#b9c4d5\"><b>挂帐科目明细</b></td>");
			sb.append("</tr>");
			sb.append("<tr  bgColor=\"#e4e8ef\" height=\"18\">");
			
			sb.append("<td  align=\"left\" valign=\"middle\" >"+Name+"</td>");
			sb.append("<td  align=\"center\" valign=\"middle\"><input sName='"+Name+"' type=button id='but' name='but' subjectID='"+subjectID+"' accid='"+accid+"' class=\"flyBT\" value=确认对照 onclick='return save(this);'/>");
			sb.append("<td align=\"left\" valign=\"top\"  >"); //style=\"padding:0px;\"
			
			sb.append("<table id=\"TTable\" cellSpacing=\"1\" cellPadding=\"2\" width=\"100%\"  bgColor=\"#6595d6\" border=\"0\">");			
			if(i1 == 1){
				sb.append("<tr class=\"DGtd\" height=\"20\">");
				sb.append("<td align=\"center\" valign=\"middle\" bgColor=\"#b9c4d5\" colspan='2' >已存在对照</td>");
				sb.append("</tr>");
				sb.append(sb1);
			}
			if(i2 == 1){
				sb.append("<tr class=\"DGtd\" height=\"20\">");
				sb.append("<td align=\"center\" valign=\"middle\" bgColor=\"#b9c4d5\" colspan='2' >对照名相同</td>");
				sb.append("</tr>");
				sb.append(sb2);
			}
			sb.append("<tr class=\"DGtd\" height=\"20\">");
			sb.append("<td align=\"center\" valign=\"middle\" bgColor=\"#b9c4d5\" colspan='2' >追加对照名</td>");
			sb.append("</tr>");
			sb.append("</table>");
			
			sb.append("</td>");
			sb.append("</tr>");
		
			sb.append("</table>");
			org.util.Debug.prtOut("3:"+String.valueOf(new java.util.Date(System.currentTimeMillis())));
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
//			DbUtil.close(rs2);	
			DbUtil.close(ps);
		}
	}
	
	public String getSave(String acc,String txtSubject,String txtOriSubject) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
						
			String sql = "";
			String [] txtSub = txtSubject.split("\\|");
			
			sql = "select * from c_usersubject where AccPackageID='"+acc+"' and oriaccid = '"+txtSub[1]+"' and orisubjectid='"+txtSub[2]+"' limit 1";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = get(acc,rs.getString("SubjectFullName"),txtOriSubject);
				if("ok".equals(result)){
					del(acc,rs.getString("accid"),rs.getString("SubjectID"));
					result = add(acc,txtSubject,txtOriSubject);
				}
			}else{
				result = get(acc,txtOriSubject);
				if("ok".equals(result)){
					result = add(acc,txtSubject,txtOriSubject);
				}
			}
			rs.close();
			ps.close();
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
			DbUtil.close(ps);	
		}
	}

	public String get(String acc,String txtOriSubject) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "ok";
		try {
			String sql = "";
			String s1 = "";
			String [] txtOri = txtOriSubject.split("`");
			for (int i = 0; i < txtOri.length; i++) {
				if(txtOri[i] != null && !"".equals(txtOri[i])){
					String [] Sub = txtOri[i].split("\\|");
					sql = "select * from c_usersubject where AccPackageID='"+acc+"' and oriaccid = '"+Sub[1]+"' and orisubjectid='"+Sub[2]+"' ";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						s1 += "保存失败：【"+rs.getString("orisubjectfullname")+"】已挂在【"+rs.getString("SubjectID")+rs.getString("SubjectName")+"】对照上\\n";						
					}
					DbUtil.close(rs);	
					DbUtil.close(ps);	
				}
			}
				
			if(!"".equals(s1)) result = s1;
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
			DbUtil.close(ps);	
		}
	}
	public String get(String acc,String txt,String txtOriSubject) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "ok";
		try {
			String sql = "";
			String s1 = "";
			String [] txtOri = txtOriSubject.split("`");
			for (int i = 0; i < txtOri.length; i++) {
				if(txtOri[i] != null && !"".equals(txtOri[i])){
					String [] Sub = txtOri[i].split("\\|");
					sql = "select * from c_usersubject where AccPackageID='"+acc+"' and oriaccid = '"+Sub[1]+"' and orisubjectid='"+Sub[2]+"' ";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						if(!txt.equals(rs.getString("SubjectFullName"))){
							s1 += "保存失败：【"+rs.getString("orisubjectfullname")+"】已挂在【"+rs.getString("SubjectID")+rs.getString("SubjectName")+"】对照上\\n";						
						}	
					}
					DbUtil.close(rs);	
					DbUtil.close(ps);	
				}
			}
				
			if(!"".equals(s1)) result = s1;
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
			DbUtil.close(ps);	
		}
	}
	
	public void del (String acc,String txtSubject) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			String [] txtSub = txtSubject.split("\\|");
			sql = "delete from c_usersubject where AccPackageID='"+acc+"' and accid='"+txtSub[1]+"' and SubjectID = '"+txtSub[2]+"'";
			org.util.Debug.prtOut("del sql3:"+sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
			DbUtil.close(ps);			
		}
	}
	
	public void del (String acc,String accid,String SubjectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			
			sql = "delete from c_usersubject where AccPackageID='"+acc+"' and accid='"+accid+"' and SubjectID = '"+SubjectID+"'";
			org.util.Debug.prtOut("del sql3:"+sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
			DbUtil.close(ps);			
		}
	}

	public void del (String acc) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			
			sql = "delete from c_usersubject where AccPackageID='"+acc+"' ";
			org.util.Debug.prtOut("del sql3:"+sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
			DbUtil.close(ps);			
		}
	}
	
	public String add(String acc,String txtSubject,String txtOriSubject) throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
			
			String sql = "";
//oriParentSubjectID,oriParentDirection
			sql = "insert into c_usersubject (AccPackageID, accid, SubjectID, SubjectName, SubjectFullName,direction, oriaccid, orisubjectid, orisubjectname, orisubjectfullname,oridirection,oriParentSubjectID1,oriParentName1,oriDataName )values (?,?,?,?,?,?,?,?,?,?,?,?,?,'0')";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			
			String [] txtSub = txtSubject.split("\\|");
			
			if("".equals(txtSub[1])){
				sql = "select '' AccID ,SubjectID,subjectname,SubjectFullName,case substring(a.Property,2,1) when 2 then -1 else 1 end direction from c_accpkgsubject a where AccPackageID = '"+acc+"' and SubjectID = '"+txtSub[2]+"'";				
			}else{
				sql = "select AccID,AssItemID,AssItemName,AssTotalName1,direction2 from c_assitementryacc where AccPackageID = '"+acc+"' and AccID = '"+txtSub[1]+"' and AssItemID = '"+txtSub[2]+"' and SubMonth=1";
			}
			ps1 = conn.prepareStatement(sql);
			rs = ps1.executeQuery();
			if(rs.next()){
				ps.setString(2, rs.getString(1));
				ps.setString(3, rs.getString(2));
				ps.setString(4, rs.getString(3));
				ps.setString(5, rs.getString(4));
				ps.setString(6, rs.getString(5));
			}else{
				return "保存失败：对照科目不存在！";
			}
			
			DbUtil.close(rs);	
			DbUtil.close(ps1);	
			
			String [] txtOri = txtOriSubject.split("`");
			for (int i = 0; i < txtOri.length; i++) {
				if(txtOri[i] != null && !"".equals(txtOri[i])){
					String [] Sub = txtOri[i].split("\\|");
					
					if("".equals(Sub[1])){
						sql = "select '' AccID ,a.SubjectID,a.subjectname,a.SubjectFullName,case substring(a.Property,2,1) when 2 then -1 else 1 end direction,b.subjectID,b.subjectName from c_accpkgsubject a,c_accpkgsubject b  where  a.AccPackageID = '"+acc+"' and a.SubjectID = '"+Sub[2]+"' and b.AccPackageID = '"+acc+"' and b.level0=1 and a.SubjectID like concat(b.SubjectID,'%') ";				
					}else{
						sql = "select AccID,AssItemID,AssItemName,AssTotalName1,direction2,b.subjectID,b.subjectName from c_assitementryacc a ,c_accpkgsubject b where a.AccPackageID = '"+acc+"' and AccID = '"+Sub[1]+"' and AssItemID = '"+Sub[2]+"' and SubMonth=1 and b.AccPackageID = '"+acc+"' and b.level0=1 and a.AccID like concat(b.SubjectID,'%') ";
					}
					ps1 = conn.prepareStatement(sql);
					rs = ps1.executeQuery();
					if(rs.next()){
						ps.setString(7, rs.getString(1));
						ps.setString(8, rs.getString(2));
						ps.setString(9, rs.getString(3));
						ps.setString(10, rs.getString(4));
						ps.setString(11, rs.getString(5));
						ps.setString(12, rs.getString(6));
						ps.setString(13, rs.getString(7));
						ps.addBatch();
					}
					
					DbUtil.close(rs);	
					DbUtil.close(ps1);	
				}				
			}
			
			ps.executeBatch();
			DbUtil.close(ps);	
			
			sql = "delete from c_usersubject where oriDataName<>'0'";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);	
			
			sql = "select distinct a.AccPackageID,a.accid,a.subjectid,b.dataname from c_usersubject a ,(select '' accid,subjectid,dataName from c_accountall where AccPackageID = '"+acc+"' and submonth=1 and isleaf1=1 and accsign=1 union select accid,assitemid,dataName from c_assitementryaccall where AccPackageID = '"+acc+"'  and submonth=1 and isleaf1=1 and accsign=1) b where a.AccPackageID = '"+acc+"' and concat(a.oriaccid,a.orisubjectid) = concat(b.accid,b.subjectid)";
			ps1 = conn.prepareStatement(sql);
			rs = ps1.executeQuery();
			while(rs.next()){
				
				sql = "insert into c_usersubject(AccPackageID,accid ,SubjectID ,  SubjectName,  SubjectFullName  ,oriaccid  ,orisubjectid,orisubjectname,orisubjectfullname ,Property ,   direction , oridirection ,oriDataName ,oriParentSubjectID1,oriParentName1 )" +
					" select distinct AccPackageID,accid ,SubjectID ,  SubjectName,  SubjectFullName  ,oriaccid  ,orisubjectid,orisubjectname,orisubjectfullname ,Property ,   direction , oridirection ,'"+rs.getString("dataName")+"' ,oriParentSubjectID1,oriParentName1  " +
					" from c_usersubject a where a.AccPackageID = '"+acc+"' and concat(a.accid,a.subjectid) = concat('"+rs.getString("accid")+"','"+rs.getString("subjectid")+"')";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);	
			}
			DbUtil.close(rs);	
			DbUtil.close(ps1);	
			
			updateOriParent(acc); 	//修改多科目组的ID和方向
			return "保存成功！";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);	
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}
	}
	
	public void updateOriParent(String acc) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if(!"".equals(acc)){
				String sql = "update c_usersubject a left join z_keyresult b on oriParentName1 = userkey and a.AccPackageID='"+acc+"' set a.oriParentSubjectID = b.standid,a.oriParentDirection = case b.Property when 2 then -1 else 1 end";
				ps = conn.prepareStatement(sql);
				ps.execute();
			}else{
				org.util.Debug.prtOut("帐套编号为空");
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
