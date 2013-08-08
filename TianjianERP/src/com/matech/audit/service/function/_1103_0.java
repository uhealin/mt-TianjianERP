package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.matech.framework.pub.util.ASFuntion;

public class _1103_0 extends AbstractAreaFunction {


    public ResultSet process(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response, Connection conn,
                             Map args) throws Exception {

//		=取列公式覆盖(1103,"借","debitocc","&reporttype=资产负债表")
//		=取列公式覆盖(1103,"贷","creditocc","&reporttype=资产负债表")


        ASFuntion asf = new ASFuntion();

        String apkID = (String) args.get("curAccPackageID");
        String prjID = (String) args.get("curProjectid");
        String curTaskCode = (String) args.get("curTaskCode");
        String reporttype = (String) args.get("reporttype");

        String sql = "";

        PreparedStatement ps = null;
        Statement st = null;
        ResultSet rs = null;
        try {

            st = conn.createStatement();

            String taskID = "";
            String description = "";
            sql =
                    "select taskID,description from z_task where projectid=? and taskcode=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, prjID);
            ps.setString(2, curTaskCode);

            rs = ps.executeQuery();

            if (rs.next()) {
                taskID = rs.getString("taskid");
                description = rs.getString("description");
            } else {
                throw new Exception("找不到taskCode［" + curTaskCode +
                                    "］projectid［" + prjID + "］的taskid");
            }

            if (description==null || description.length()==0){
                sql="select description from z_task where projectid=? and taskid=("
                    +"select parenttaskid from z_task where projectid=? and taskcode=?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, prjID);
                ps.setString(2, prjID);
                ps.setString(3, curTaskCode);
                rs = ps.executeQuery();
                if (rs.next()) {
                    description = rs.getString("description");
                } else {
                    throw new Exception("找不到taskCode［" + curTaskCode +
                                        "］projectid［" + prjID + "］的上级节点taskid");
                }
            }

            //			当前底稿的对应参加报表结点的参加报表。
            sql = " select a.debitocc * b.direction as debitocc,a.crditocc * b.direction as creditocc from z_accountrectifyaccount a \n";
            sql += " inner join asdb.k_reportconfig b on a.reportentryid=b.rowindex and a.reporttype=b.reporttype  \n";
            sql += " where a.projectid = " + prjID + " \n";
            sql += " and a.reporttype ='" + reporttype + "' \n";
            sql += " and a.reportlevel=" + description + " \n";
            sql += " order  by a.reportentryid \n";
            rs = st.executeQuery(sql);
            return rs;
        } catch (Exception e) {
            System.out.println("sql=" + sql);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }finally{
            if (rs!=null)rs.close();
            if (ps!=null)ps.close();
        }
    }


}
