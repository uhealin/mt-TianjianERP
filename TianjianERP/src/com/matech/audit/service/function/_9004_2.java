package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;


/**
 *	银行函证
 *	=取列公式插入(9005,"subjectid","subjectid","&SubjectName=银行存款")
 *	=取列公式插入(9005,"subjectid","subjectid","&SubjectName=银行存款|应付账款")
 *	
 *	SubjectName 为标准科目,多科目时以“|”分隔
 *
 *返回：
 * "科目编号", "SubjectID
 * "核算编号", "AssItemID
 * "科目/核算名称", "newaccname"
 * "币种", "DataName
 * "重分类值", "occ"
 * "方向", "direction"
 * "科目余额", "Balance"
 * "发函金额", "remain"
 * "科目归属", "subOpt"
 */
public class _9004_2 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
		
			String acc = CHF.showNull((String) args.get("curAccPackageID"));		//账套编号
	        String projectid = CHF.showNull((String) args.get("curProjectid"));		//项目编号
			String SubjectName = CHF.showNull((String)request.getParameter("SubjectName"));	//标准科目
			String SubjectID = "";
			if(!"".equals(SubjectName)){
				
				String [] Names = SubjectName.split("\\|");
				String string = "";
				for(int i = 0 ;i<Names.length ;i++){
					string += "or (a.Subjectfullname2 = '"+Names[i]+"' or a.Subjectfullname2 like '"+Names[i]+"/%') ";
				}
				SubjectName = " and (" + string.substring(2) + ") ";
				System.out.println(SubjectName);
//				SubjectName = " and (a.Subjectfullname2 = '"+SubjectName+"' or a.Subjectfullname2 like '"+SubjectName+"/%')  ";
			}else{
				throw new Exception ("标准科目不能为空");
			}
			
			this.tempTable = "tt_"+DELUnid.getCharUnid();	//临时表
			
			Project project = new ProjectService(conn).getProjectById(projectid);
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);

			int bYear = Integer.parseInt(begin) * 12 + Integer.parseInt(bMonth);
			int eYear = Integer.parseInt(end) * 12 + Integer.parseInt(eMonth);

			sql = "select ifnull(group_concat(\"'\",subjectid,\"'\") ,'') as subjectID " +
			" from c_account a " +
			" where  accpackageid='"+acc+"' and SubMonth = "+eMonth+"  and isleaf1=1 " + SubjectName ; 
			rs = st.executeQuery(sql);
			if(rs.next()){
				SubjectID = rs.getString("SubjectID");
				if(!"".equals(SubjectID)){
					SubjectID = " and a.SubjectID in ("+SubjectID+") ";
				}
			}
			DbUtil.close(rs);
			
			String s1 = " and not exists (select 1 from c_accountall where 1=1 and accpackageid='"+acc+"' and SubMonth = "+eMonth+" and isleaf1=1 and accsign = 1 and a.subjectid=subjectid )";

			sql = "select a.*,if(direction='贷','银行借款','银行存款') subOpt,ifnull(b.newaccname,a.accname) as newaccname from (" +
			"\n select '0' opt,a.subjectid,'' assitemid,a.accname,subjectfullname1," +
			"\n case a.DataName when '0' then '本位币' else concat('外币：',a.DataName) end dataname," +
			"\n case a.direction when 1 then '借' else '贷' end direction," +
			"\n a.direction*Balance Balance,a.direction*a.occ occ,a.direction*(Balance+occ) remain,DataName dName,c.subjectname  " +
			"\n from (	" +
//			科目
			"\n	select if(ifnull(c.subjectid,'0')='0',a.subjectid,concat(a.subjectid,'00')) subjectid,if(ifnull(c.subjectid,'0')='0',a.accname,concat(a.accname,'(原帐金额)')) accname,SubjectFullName1, DataName,direction,Balance,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ  from ( " +
			"\n 	select b.subjectid,b.subjectname accname,b.SubjectFullName SubjectFullName1,ifnull(DataName,'0') DataName," +
			"\n 	ifnull(direction2,case substring(b.Property,2,1) when 2 then -1 else 1 end) direction,ifnull(Balance,0) Balance " +
			"\n 	from c_account a right join c_accpkgsubject b on b.accpackageid='"+acc+"' and a.accpackageid='"+acc+"' and SubMonth = "+eMonth+" " +
			"\n 	and a.subjectid=b.subjectid " +
			"\n 	where 1=1 and  b.isleaf=1 and b.accpackageid='"+acc+"' and a.accpackageid='"+acc+"' " + s1 +
			"\n		union " +
			"\n		select subjectID,SubjectName,SubjectFullName,0 DataName, case Property  when 01 then 1 when 02 then -1 else 0 end direction,0 Balance  from z_usesubject where projectID='"+projectid+"' and isleaf=1 " +
			"\n	) a  " +
			"\n	left join z_accountrectify b on projectID='"+projectid+"' and b.isleaf=1 and a.SubjectID=b.SubjectID " +
			"\n	left join z_usesubject c on a.SubjectId=c.ParentSubjectId and c.projectID='"+projectid+"'  " +
			"\n	where 1=1 " + SubjectID + //加条件

			"\n	union " +
//			科目外币
			"\n	select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
			"\n		select subjectid,accname,SubjectFullName1, DataName,direction2 direction,Balance  from c_accountall where accpackageid='"+acc+"' and SubMonth = "+eMonth+" and isleaf1=1 and accsign = 1 " +
			"\n	) a " +
			"\n	left join z_accountallrectify b on projectID='"+projectid+"' and b.isleaf1=1  and a.SubjectID=b.SubjectID and a.DataName=b.DataName  " +
			"\n	where 1=1 " + SubjectID + 	//加条件

			"\n ) a " +
			"\n left join c_accpkgsubject c on c.accpackageid='"+acc+"' and a.subjectid like concat(c.subjectid,'%') and c.level0=1 " +
			"\n where 1=1  and c.accpackageid='"+acc+"' " +
			"\n ) a " +
			
			"\n left join c_funcBank b on ((a.subjectid = b.subjectid and a.assitemid = b.assitemid ) or a.accname = b.accname) " +
			
			" where 1=1 " +
			" order by a.subjectid";
			
			
			sql = "create table " + this.tempTable + " " + sql ;
			st.execute(sql);
			
			/**
			 * 输出SQL
			 */
			sql = "select * from " + this.tempTable + " order by abs(remain) desc ";
			rs = st.executeQuery(sql);
			return rs;
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
			DbUtil.close(rs);
			DbUtil.close(st);
			throw e;
		} 
		
	}
}
