package com.matech.audit.service.function;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class _1020_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		Statement st=null;
		ResultSet rs=null;
		try{
			
			st = conn.createStatement();
			
			ASFuntion CHF = new ASFuntion();
			String subjectname=CHF.showNull(request.getParameter("subjectname")); //subjectname 为all，就刷所有调整
			String vchdate=CHF.showNull(request.getParameter("vchdate"));
			String projectid = CHF.showNull((String) args.get("curProjectid"));
			String curPackageid = CHF.showNull((String) args.get("curPackageid"));
			
			String itemtype = CHF.showNull(request.getParameter("itemtype")); //调整类型
			
			String name = subjectname;
			
			if("".equals(subjectname)) {
				 //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
				subjectname = getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));	
			}
			String sql = "";
			
//			把前台拼的参数放在后台拼，同时支持以前的格式，现在格式用分号";"分隔，如：&subjectname=  库存现金;银行存款;其他货币资金;
			if(subjectname.toLowerCase().indexOf("存货") >0 && subjectname.toLowerCase().indexOf("存货跌价")==-1 ){
				
//				翻译存货
				sql="select a.subjectfullname from k_standsubject a,k_customer b,z_project c \n"
					+"where a.property1='2' \n"
					+"and c.projectid="+projectid +" \n"
					+"and c.customerid=b.departid \n"
					+"and b.vocationid=a.vocationid";
				org.util.Debug.prtOut("qwh sql="+sql);
				rs=st.executeQuery(sql);
				
				subjectname="";
				while (rs.next()){
					subjectname += " union select '" + rs.getString(1) + "' ";
				}
				rs.close();
				
				if ("".equals(subjectname)){
					subjectname= " ";
				}
				System.out.println("存货翻译完的subjectname="+subjectname);
//				subjectname += " union select '开发产品'";
//				subjectname += " union select '在产品'";
//				subjectname += " union select '受托代销商品'";
//				subjectname += " union select '委托代销商品'";
//				subjectname += " union select '包装物'";
//				subjectname += " union select '周转材料'";
//				subjectname += " union select '低值易耗品'";
//				subjectname += " union select '半成品'";
//				subjectname += " union select '委托加工物资'";
//				subjectname += " union select '商品进销差价'";
//				subjectname += " union select '发出商品'";
//				subjectname += " union select '库存商品'";
//				subjectname += " union select '材料成本差异'";
//				subjectname += " union select '原材料'";
//				subjectname += " union select '在途物资'";
//				subjectname += " union select '材料采购'";
				 
			}else{
//				翻译;切割的
				if(subjectname.toLowerCase().indexOf("union") == -1){
					String subjectnames []=subjectname.trim().replaceAll("'","").split(";");
					subjectname = "";
					for(int i=0;i<subjectnames.length;i++) {
						subjectname += " union select '" + subjectnames[i] + "' ";
					}
				}
				
								
			}
			args.put("subjectname",subjectname);
		
			sql = " select * \n"
			+ " from ("
			+" 		select AccPackageID,subjectid,accname as subjectname,subjectfullname1,subjectfullname2 from c_account where accpackageid='${curPackageid}' and submonth=1 \n" 
			+" 		union    \n"
			+" 		select AccPackageID,subjectid,subjectname,subjectfullname,subjectfullname from z_usesubject where projectID='${curProjectid}' and accpackageid='${curPackageid}' \n"  
			+ ") a ,( \n"
			+ " 	select '' as subjectname \n"   
			+ " 	${subjectname}  \n"
			+ " 	union   \n"
			+ " 	select standkey from z_keyresult a, \n" 
			+ " 	(    \n"
			+ " 	select '' as subjectname   ${subjectname} \n"  
			+ " 	) b   \n"
			+ " 	where userkey=subjectname \n" 
			+ " 	union   \n"
			+ " 	select userkey from z_keyresult a, \n" 
			+ " 	(    \n"
			+ " 	select '' as subjectname   ${subjectname} \n"  
			+ " 	) b    \n"
			+ " 	where standkey=subjectname \n"  
			+ " ) b where (a.subjectfullname2 = b.subjectname or a.subjectfullname2 like concat(b.subjectname,'/%')) \n";
//			+ " and a.accpackageid=${curPackageid} \n"
//			+ " and a.submonth=1 ";
		
			sql = this.setSqlArguments(sql, args);
			rs=st.executeQuery(sql);
			String subjectname1 = "";
			String subjectname2 = "";
			while(rs.next()){
				String subjectfullname2 = rs.getString("subjectfullname2");
				//subjectname1 += " (subjectfullname2 = '"+subjectfullname2+"' or subjectfullname2 like '"+subjectfullname2+"/%') or";
				//subjectname2 += " (subjectfullname = '"+subjectfullname2+"' or subjectfullname like '"+subjectfullname2+"/%') or";
				subjectname1 += " '"+subjectfullname2+"', ";
				subjectname2 += " '"+subjectfullname2+"', ";
			}
			DbUtil.close(rs);
			
			if(!"".equals(subjectname1)){
				subjectname1 = " and subjectfullname2 in (" + subjectname1.substring(0, subjectname1.length()-2) + ") ";
				subjectname2 = " and subjectfullname in (" + subjectname2.substring(0, subjectname2.length()-2) + ") ";
				args.put("subjectname",subjectname1);
				args.put("subjectname1",subjectname2);
			}else{
				if("all".equals(name)){
					args.put("subjectname"," ");
					args.put("subjectname1"," ");
				}else{
					args.put("subjectname"," and 1=2 ");
					args.put("subjectname1"," and 1=2 ");
				}
			}
			
			
			 
			String filluser = CHF.showNull(request.getParameter("filluser"));		//0为当前登录人；1为其他人
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String userName ="";
			if (filluser!=null && !"".equals(filluser)){
				if (userSession!=null){
					userName=userSession.getUserName();
				}else{
					String userId=request.getParameter("userId");
					com.matech.audit.service.user.model.User user=new com.matech.audit.service.user.UserService(conn).getUser(userId,"id");
					if (user!=null)
						userName=user.getName();
				}
			}
		 
		
			String resultSql="";
		
			/**
			 * 上年调整影响年末 否：上海立信
			 */
			String svalue = "";
			sql = "select svalue from s_config where sname='上年调整影响年末'";
			rs=st.executeQuery(sql);
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
			
			
			if("否".equals(svalue)){
				if (vchdate != null && !"".equals(vchdate)) {
					if(vchdate.equals("1")) {
						vchdate=" and substring('${curPackageid}',7)=substring(a.vchdate,1,4) ";
					} else {
						vchdate=" and substring('${curPackageid}',7)-1=substring(a.vchdate,1,4)  ";
					}
				} else {
					vchdate=" ";
				}
			}else{
				if (vchdate != null && !"".equals(vchdate)) {
					if(vchdate.equals("1")) {
						vchdate=" and substring('${curPackageid}',7)=substring(a.vchdate,1,4) ";
					} else {
						vchdate=" and substring('${curPackageid}',7)!=substring(a.vchdate,1,4) ";
					}
				} else {
					vchdate=" ";
				}
			}
			
			String property=CHF.showNull(request.getParameter("property"));
			int sYear = Integer.parseInt(curPackageid.substring(6)) -1 ;
			if(!"".equals(property)){
				property = " and a.property in ("+property+") ";	
				if(property.indexOf("511")>-1){
					property=" and (a.property in(611,711,811) or (a.property=511 and a.vchdate ='"+ String.valueOf(sYear)+"-12-31'))";
				}
				args.put("property",property);
			}else{
				args.put("property"," and a.property in (311,411) ");
			}
			
			if("0".equals(filluser)){
				args.put("filluser"," and filluser = '"+userName+"' ");
			}else if("1".equals(filluser)){
				args.put("filluser"," and filluser <> '"+userName+"' ");
			}else{
				args.put("filluser"," ");
			}
			
			if(!"".equals(itemtype)){
				itemtype = " and a.itemtype = '"+itemtype+"' ";
			}
			resultSql=getSql1(vchdate,itemtype);	
			//最终查询结果
			resultSql=this.setSqlArguments(resultSql, args);    
			
			System.out.println("resultSql1="+resultSql);
			rs=st.executeQuery(resultSql);
		
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	//=取列公式覆盖(1020, "", "property", "&subjectname=  union select '库存现金' &vchdate=0")
	//=取列公式覆盖(1020, "", "itemtype", "&subjectname=  union select '库存现金' &vchdate=0")
	public String getSql1(String vchdate,String itemtype){
		return ""
		+" select *, "
		+" subjectid1 as sn1, \n"
		+" subjectid as sn2, \n"
		+" subjectname1 as s1, \n"
		+" subjectname as s2, \n"
		+" if(SUBSTRING(subjectid,1,1) <=3,debitocc,0) occ1,  \n"
		+" if(SUBSTRING(subjectid,1,1) <=3,creditocc,0) occ2,  \n"
		+" if(SUBSTRING(subjectid,1,1) =5,debitocc,0) occ3,  \n"
		+" if(SUBSTRING(subjectid,1,1) =5,creditocc,0) occ4  , \n"

		+" if(dirction=1,subjectname,'')	s3, \n"
		+" if(dirction=-1,subjectname,'') s4, \n"
		+" debitocc occ5, \n"
		+" creditocc occ6, \n"

		+" if(dirction=1,'借','贷') as direction,aProperty as property,itemtype \n" 
		
		+" from ( \n"
		+" 	select * from ( \n"
		+" 		select @k_af_1020i:=0 as id,@k_af_1020v:=0 as prevoucherid, 0 as voucherid,'' as summary ,'' as subjectid,'' as subjectname, '' as subjectfullname,'' as nid,0 as debitocc,0 as creditocc,'' as filluser ,'' as subjectid1,'' as subjectname1,'' as nid1,'0' as dirction,'' AS serail,'' as aProperty,'' as itemtype \n"
		+" 	) a where 1=2   \n"
	
		+" 	UNION \n"
	
		+" 	select if(@k_af_1020v != a.voucherid,@k_af_1020i:=@k_af_1020i+1,@k_af_1020i) as id,@k_af_1020v:=a.voucherid as prevoucherid, a.* \n"
		+"	from ("
		+"	select "
		+" 	a.voucherid,if(a.summary > '',a.summary,'无摘要') as summary, \n"
		+" 	a.subjectid,concat(b.subjectname,if(ifnull(a.assitemname,'') = '' ,'',concat('|',ifnull(a.assitemname,'')))) as  subjectname,concat(b.subjectfullname1,if(ifnull(a.assitemname,'') = '' ,'',concat('|',ifnull(a.assitemname,'')))) as subjectfullname,concat(a.subjectid,b.subjectname) as nid, \n"  
		+" 	CASE a.dirction when 1 then a.occurvalue else 0 end as debitocc,   \n"
		+" 	CASE a.dirction when -1 then a.occurvalue else 0 end as creditocc, \n"
		+" 	d.filluser,   \n"
		+" 	b.subjectid1,b.subjectname1,b.nid1,dirction,a.serail, \n"
		+"	case LEFT(a.Property,1) when '3' then '调整'  when '6' then '调整' when '4' then '重分类' when '7' then'重分类' when '5' then '不符未调'when '8' then '不符未调' end as aProperty,itemtype \n"
		+" 	from ( \n"
		+" 		select b.* ,c.assitemid,d.assitemname \n" 
		+" 		from ( \n"
		+" 			select distinct a.ProjectID, a.voucherid \n"
		+" 			from z_subjectentryrectify a \n"
		+" 			inner join ( \n"
		+" 				select AccPackageID,subjectid,accname as subjectname,subjectfullname1,subjectfullname2 from c_account where accpackageid='${curPackageid}' and submonth=1 \n" 
		+" 				${subjectname} \n"
//		+" 				union    \n"
//		+" 				select a.AccPackageID,a.subjectid,a.subjectname,a.subjectfullname,a.subjectfullname \n"
//		+" 				from z_usesubject a,c_account b \n"
//		+" 				where a.projectID='${curProjectid}' and a.accpackageid='${curPackageid}' \n"
//		+" 				and b.accpackageid='${curPackageid}' and submonth=1 ${subjectname} \n"
//		+" 				and a.subjectid like concat(b.subjectid,'%') \n"
		
		+" 				union    \n"
		+" 				select a.AccPackageID,a.subjectid,a.subjectname,a.subjectfullname,a.subjectfullname \n"
		+" 				from z_usesubject a \n"
		+" 				where a.projectID='${curProjectid}' and a.accpackageid='${curPackageid}' \n"
		+" 				${subjectname1} \n"
		
		+" 			) b on a.subjectid = b.subjectid \n"
		+" 		) a  \n"
		+" 		inner join z_subjectentryrectify b \n" 
		+" 		on a.ProjectID = ${curProjectid}  \n"
		+" 		and b.ProjectID = ${curProjectid}  \n"
		+" 		and a.voucherid = b.voucherid \n"
		
		+" 		left join z_assitementryrectify c  \n" 
		+" 		on c.ProjectID = ${curProjectid}  \n"
		+" 		and b.ProjectID = ${curProjectid}  \n"
		+" 		and b.autoid = c.entryid  \n"
		
		+" 		left join z_assitemaccrectify d \n" 
		+" 		on c.ProjectID = ${curProjectid}  \n"
		+" 		and d.ProjectID = ${curProjectid}  \n"
		+" 		and c.assitemid = d.assitemid \n"
		+" 		and c.subjectid = d.subjectid \n"
		
		+" 	) a \n"
		+" 	inner join ( \n"
		
		+" 		select b.AccPackageID,b.subjectid,b.accname as subjectname,b.subjectfullname1,b.subjectfullname2 , \n"
		+" 		c.subjectid as subjectid1,c.accname as subjectname1,concat(c.subjectid,c.accname) as nid1 \n"
		+" 		from c_account b,(select * from c_account c where c.accpackageid='${curPackageid}' and c.submonth=1 and c.level1=1 ) c \n"
		+" 		where b.accpackageid='${curPackageid}' and b.submonth=1  \n"
		+" 		and (b.subjectfullname1 = c.subjectfullname1 or b.subjectfullname1 like concat(c.subjectfullname1,'/%')) \n"
		+" 		union     \n"
		+" 		select b.AccPackageID,b.subjectid,b.subjectname,b.subjectfullname,b.subjectfullname, \n"
		+" 		c.subjectid as subjectid1,c.accname as subjectname1,concat(c.subjectid,c.accname) as nid1 \n"
		+" 		from z_usesubject b,(select * from c_account c where c.accpackageid='${curPackageid}' and c.submonth=1 and c.level1=1 ) c \n" 
		+" 		where projectID='${curProjectid}' and b.accpackageid='${curPackageid}' \n"
		+" 		and b.tipsubjectid = c.subjectid \n"
		+" 		union  \n"
		+" 		select b.AccPackageID,b.subjectid,b.subjectname,b.subjectfullname,b.subjectfullname, \n"
		+" 		b.subjectid as subjectid1,b.subjectname as subjectname1,concat(b.subjectid,b.subjectname) as nid1 \n"
		+" 		from z_usesubject b \n"
		+" 		where projectID='${curProjectid}' and b.accpackageid='${curPackageid}' \n"
		+" 		and level0 = 1 \n"
		
		+" 	) b on a.subjectid = b.subjectid \n"
		
		+" 	inner join z_voucherrectify d \n"
		+" 	on a.ProjectID = ${curProjectid}  \n"
		+" 	and a.ProjectID=d.ProjectID  \n"
		+" 	and a.voucherid = d.autoid \n"
		+" 	where 1=1  \n"
		
		+"	${filluser} ${property}" 
		+ vchdate 
		+ itemtype
		+" order by a.voucherid,a.subjectid  "
		+" ) a \n"
		+" ) a \n"
		+" order by a.voucherid,a.serail,a.subjectid  ";
		
	}
	
	
	
}