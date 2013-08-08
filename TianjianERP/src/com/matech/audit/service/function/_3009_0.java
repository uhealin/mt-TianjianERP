package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * <p>Title: 取税的抽任记录，供批量刷新调用</p>
 * <p>Copyright: Copyright (c) 2007 matech LTD.</p>
 *
 * <p>Company: matech </p>
 */

public class _3009_0 extends AbstractAreaFunction {

    public ResultSet process(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response, Connection conn,
                             Map args) throws Exception {

        String curProjectid = (String) args.get("curProjectid");
        args.put("curProjectid", curProjectid);

        String resultSql = "";

        Statement st = null;
        ResultSet rs = null;
        try {
        	
            String taskCode = (String)request.getParameter("curTaskCode");
                
            args.put("taskCode", taskCode);
            
            st = conn.createStatement();

            //最终查询结果
            resultSql = getSql();
            resultSql = this.setSqlArguments(resultSql, args);
            System.out.println("zyq:3009:resultSql="+resultSql);
            rs = st.executeQuery(resultSql);

            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

/**
 * 通过z_taxcheck表的Vchid和c_subjectentry表的Autoid关联表，一次抽取的记录只是单记记录，不是整笔凭证
 * @return
 */   
    public String getSql(){
                return "select * from ( \n"
			+"select a.vchdate as vchdate,a.typeid as typeid,a.oldvoucherid as voucherid,b.summary as summary,b.subjectid as subjectid,b.subjectfullname1 as subjectfullname1,if(b.dirction=1,'借','贷') as direction,a.property as occurvalue,a.occvalue as localvalue,a.judge as judge \n"
			+"	from z_taxcheck a left join c_subjectentry b on a.vchid = b.autoid \n"
			+"	where projectid = '${curProjectid}' and a.subjectid like  '%,${taskCode},%'\n"
			+"	order by a.vchdate,a.typeid,a.oldvoucherid \n"
            +"  )a";
			
        }

}

