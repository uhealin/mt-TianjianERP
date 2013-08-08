package com.matech.audit.service.checkInfo;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

import com.matech.audit.pub.db.DBConnect;

public class ErrorSubject {
	public String getVoucherDIs(String firstAccID,String lastAccID,String vocationid){
		
		final String sql="" +
		"    select group_concat(a.voucherid) as voucherids from\n" + 
		"    (   /*格式化分录表*/\n" + 
		"        select\n" + 
		"          voucherid,\n" + 
		"          concat(',',group_concat(distinct if(dirction=1,a.subjectid,'') ),',,') as debitsubject,\n" + 
		"          concat(',',group_concat(distinct if(dirction=-1,a.subjectid,'')),',,') as creditsubject\n" + 
		"        from c_subjectentry a\n" + 
		"        where accpackageid>="+firstAccID+"\n" +
		"          and accpackageid<="+lastAccID+"\n" +
		"        group by a.voucherid \n" + 
		"    ) a\n" + 
		"    inner join\n" + 
		"    (\n" + 
		"    /*把subjectname换成subjectid*/\n" + 
		"        select b.subjectid as debitsubject,c.subjectid as creditsubject from\n" + 
		"        (   /*放大两个字段*/\n" + 
		"            select debitsubject,creditsubject from\n" + 
		"            (\n" + 
		"                  select debitsubject,creditsubject from k_errorsubject where vocationid="+vocationid+"\n" + 
		"                  union\n" + 
		"                  select TRIM(replace(replace(replace(replace(CONCAT(a.debitsubject,'         '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2),'`','')) as debitsubject,\n" + 
		"                         a.creditsubject\n" + 
		"                  from k_errorsubject a,\n" + 
		"                  k_key b,k_key c,k_key d\n" + 
		"                  where a.vocationid="+vocationid+"\n" + 
		"                  and a.debitsubject like concat('%',b.key1,'%')\n" + 
		"                  and a.debitsubject like concat('%',c.key1,'%')\n" + 
		"                  and a.debitsubject like concat('%',d.key1,'%')\n" + 
		"            ) a\n" + 
		"    \n" + 
		"            union\n" + 
		"            \n" + 
		"            select a.debitsubject,TRIM(replace(replace(replace(replace(CONCAT(a.creditsubject,'         '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2),'`','')) as creditsubject\n" + 
		"            from \n" + 
		"            (\n" + 
		"                  select debitsubject,creditsubject from k_errorsubject where vocationid="+vocationid+"\n" + 
		"                  union\n" + 
		"                  select TRIM(replace(replace(replace(replace(CONCAT(a.debitsubject,'         '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2),'`','')) as debitsubject,\n" + 
		"                         a.creditsubject\n" + 
		"                  from k_errorsubject a,\n" + 
		"                  k_key b,k_key c,k_key d\n" + 
		"                  where a.vocationid="+vocationid+"\n" + 
		"                  and a.debitsubject like concat('%',b.key1,'%')\n" + 
		"                  and a.debitsubject like concat('%',c.key1,'%')\n" + 
		"                  and a.debitsubject like concat('%',d.key1,'%')        \n" + 
		"            ) a,k_key b,k_key c,k_key d\n" + 
		"            where a.creditsubject like concat('%',b.key1,'%')\n" + 
		"            and a.creditsubject like concat('%',c.key1,'%')\n" + 
		"            and a.creditsubject like concat('%',d.key1,'%')\n" + 
		"        ) a INNER join (select subjectid,subjectname from c_accpkgsubject where accpackageid>="+firstAccID+" and accpackageid<="+lastAccID+" union select '' as subjectid,'' as subjectname) b\n" + 
		"        on a.debitsubject=b.subjectname\n" + 
		"        inner join (select subjectid,subjectname from c_accpkgsubject where accpackageid>="+firstAccID+" and accpackageid<="+lastAccID+" union select '' as subjectid,'' as subjectname) c\n" + 
		"        on a.creditsubject=c.subjectname\n" + 
		"    ) b\n" + 
		"    on  a.debitsubject like concat('%,',b.debitsubject,'%,%')\n" + 
		"    and a.creditsubject like concat('%,',b.creditsubject,'%,%')\n" + 
				"";
		
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try{
			conn=new DBConnect().getConnect(firstAccID.substring(0,6));
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}else{
				return "''";
			}
		}catch(Exception e){
			e.printStackTrace();
			return "''";
		}
		finally{
			try{
				if(rs!=null)rs.close();
				if(ps!=null)ps.close();
				if(conn!=null)conn.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
}
