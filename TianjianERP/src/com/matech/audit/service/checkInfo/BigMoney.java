package com.matech.audit.service.checkInfo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.matech.framework.pub.db.DbUtil;
public class BigMoney {
	private Connection conn;

	public BigMoney(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}

  public void delMoneyCondition(String args,String prjID)throws Exception{
	  DbUtil.checkConn(conn);
    PreparedStatement ps=null;
    String sql="delete from z_bigMoney where projectid=? and MoneyCondition=?";
    try{
      
	  //根据项目ＩＤ切换数据库。
//      com.ASSys.work.Customer.CustomerMan cm=new com.ASSys.work.Customer.CustomerMan();
//      conn = new DBConnect().getConnect("");
//      cm.changeDataBase(conn, cm.getCustomerIDByProjectID(conn,prjID));

      
      ps=conn.prepareStatement(sql);
      ps.setString(1,prjID);
      ps.setString(2,args);
      ps.execute();
    }catch(Exception e){e.printStackTrace();}
    finally{
      try{
        if(ps!=null)ps.close();
       
      }catch(Exception e){e.printStackTrace();}
    }
  }


  public void insertMoneyCondition(String args,String prjID)throws Exception{
	  DbUtil.checkConn(conn);
    PreparedStatement ps=null;
    String sql="insert into  z_bigMoney(projectid,MoneyCondition)  values(?,?)";
    String selectSql="select autoid from z_bigMoney where projectid=? and MoneyCondition=?";
    try{
      
	  //根据项目ＩＤ切换数据库。
//      com.ASSys.work.Customer.CustomerMan cm=new com.ASSys.work.Customer.CustomerMan();
//      conn = new DBConnect().getConnect("");
//      cm.changeDataBase(conn, cm.getCustomerIDByProjectID(conn,prjID));

      
      ps=conn.prepareStatement(selectSql);
      ps.setString(1,prjID);
      ps.setString(2,args);
      if(!ps.executeQuery().next()){
        ps = conn.prepareStatement(sql);
        ps.setString(1, prjID);
        ps.setString(2, args);
        ps.execute();
      }
    }catch(Exception e){e.printStackTrace();}
    finally{
      try{
        if(ps!=null)ps.close();
       
      }catch(Exception e){e.printStackTrace();}
    }
  }

  public String getMoney(String projectID,String boxValue)throws Exception{
    NumberFormat f = new DecimalFormat("#,###,##0");
    DbUtil.checkConn(conn);
   
    PreparedStatement ps=null;
    ResultSet rs=null;
    String sql="select moneyCondition,projectid from z_bigmoney where projectid=? or projectid=0 order by moneyCondition";
    String result="";
    if(boxValue==null) {
    	boxValue="";
    }
    String boxValue1[] = boxValue.split("'");
    String check = "";


    try{
  	  //根据项目ＩＤ切换数据库。
//        com.ASSys.work.Customer.CustomerMan cm=new com.ASSys.work.Customer.CustomerMan();
//        conn = new DBConnect().getConnect("");
//        cm.changeDataBase(conn, cm.getCustomerIDByProjectID(conn,projectID));

      ps=conn.prepareStatement(sql);
      ps.setString(1,projectID);
      rs=ps.executeQuery();

//      rs.first();
      if(boxValue1.length>0) {
	      for(int i=0;i<boxValue1.length;i++) {
	    	  if("[-∞]".equals(boxValue1[i])) {
	    		  check = "checked";
	    	  }
	      }
      }
      result=result+"<tr><td><input name=\"mccbx\" type=\"checkbox\" value=\"[-∞]\" onclick=\"goMCC();\" "+check+"/></td><td align=\"right\">[-∞]</td><td width=\"17\"></td></tr>"+"\n";
//      rs.beforeFirst();
      while(rs.next()){
        String temp=rs.getString(1);
        check = "";
    	if(boxValue1.length>0) {
  	      for(int i=0;i<boxValue1.length;i++) {
  	    	  if(temp.equals(boxValue1[i])) {
  	    		  check = "checked";
  	    	  }
  	      }
        }
        if(rs.getString(2).equals("0")){
           result=result+"<tr><td><input name=\"mccbx\" type=\"checkbox\" value=\""+temp+"\" onclick=\"goMCC();\" "+check+" /></td><td align=\"right\" >"+ f.format(Double.parseDouble(temp)) +"</td><td width=\"17\"></td></tr>"+"\n";
        }else{
           result=result+"<tr><td><input name=\"mccbx\" type=\"checkbox\" value=\""+temp+"\" onclick=\"goMCC();\" "+check+" /></td><td align=\"right\">"+ f.format(Double.parseDouble(temp)) +"</td><td><img src=\"/AuditSystem/img/close.gif\" alt=\"按此删除\" onclick=\"operation('"+temp+"','del');\" /></td></tr>"+"\n";
        }
      }

//      rs.last();
      check = "";
      if(boxValue1.length>0) {
	      for(int i=0;i<boxValue1.length;i++) {
	    	  if("[ ∞]".equals(boxValue1[i])) {
	    		  check = "checked";
	    	  }
	      }
      }
      result=result+"<tr><td><input name=\"mccbx\" type=\"checkbox\" value=\"[+∞]\" onclick=\"goMCC();\" "+check+"/></td><td align=\"right\">[+∞]</td><td width=\"17\"></td></tr>"+"\n";

      result=result.substring(0,result.length()-1);
    }catch(Exception e){e.printStackTrace();}
    finally{
      try{
        if(rs!=null)rs.close();
        if(ps!=null)ps.close();
      
      }catch(Exception e){e.printStackTrace();}
    }
    result=" <table  border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"  >"+result+"</table>";
    return result;
  }


  public String getSQL(String firstAccID,String lastAccID,String projectid,String user,double maxValue,double minValue,String conditionIndex){//sql模板
    StringBuffer sql=new StringBuffer();
    String condition=null;
    NumberFormat f = new DecimalFormat("#,###,##0");
//    if(maxValue==-1)
//      condition ="大于或等于"+ f.format(minValue);
//    else
//      condition = f.format(minValue)+"-"+f.format(maxValue);
//    if(maxValue==-1&&minValue==-1)
//      condition="全部";
    condition = f.format(minValue)+"-"+f.format(maxValue);
    sql.append(" select  a.voucherid as voucherNumber,d.vchid as p1,c.vchid as p2,'"+condition+"' as moneyCondition, \n");
    sql.append(" a.vchDate,a.typeid,a.oldvoucherid as voucherid,a.summary,a.subjectid as sonsubjectid,a.subjectname1 as subjectname,a.subjectfullname1 as subjectfullname, \n");
    sql.append(" a.dirction,a.occurvalue,"+conditionIndex+" as conditionIndex, a.autoid,a.currrate,a.CurrValue,a.Currency,a.Quantity,a.Unitprice,a.Unitname \n");
    sql.append(" from \n");
    sql.append(" c_subjectentry a  \n");


    sql.append(" left join (select vchid from z_question where projectid="+projectid+" and createor='"+user+"' ) c on a.voucherid=c.vchid \n");

    sql.append(" left join (select vchid from z_voucherspotcheck where projectid="+projectid+" and createor='"+user+"' ) d on a.voucherid=d.vchid \n");

    sql.append(" and d.projectid="+projectid+"\n");
    sql.append(" and d.createor='"+user+"'\n");

    sql.append(" where a.accpackageid >= '" + firstAccID + "' \n");
    sql.append(" and a.accpackageid <= '" + lastAccID + "' \n");
    sql.append(" and a.property like '1%' \n");

    sql.append(" and a.occurvalue<="+maxValue+" \n");
    sql.append(" and a.occurvalue>"+minValue+" \n");






    return sql.toString();
  }

  public String getSQL(String firstAccID,String lastAccID,String projectid,String user,String maxValue,String minValue,String conditionIndex){

    if(maxValue.equals("[+∞]")&&minValue.equals("[-∞]")){
      StringBuffer sql = new StringBuffer();
      String condition = null;

      condition = minValue + "-" +maxValue;
      sql.append(
          " select a.voucherid as voucherNumber,d.vchid as p1,c.vchid as p2,'" +
          condition + "' as moneyCondition, \n");
      sql.append(" a.vchDate,a.typeid,a.oldvoucherid as voucherid,a.summary,a.subjectid as sonsubjectid,a.subjectname1 as subjectname,a.subjectfullname1 as subjectfullname, \n");
      sql.append(" a.dirction,a.occurvalue," + conditionIndex +
                 " as conditionIndex, a.autoid,a.currrate,a.CurrValue,a.Currency,a.Quantity,a.Unitprice,a.Unitname \n");
      sql.append(" from \n");
      sql.append(" c_subjectentry a  \n");
 

      sql.append(" left join  (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'  ) c on a.voucherid=c.vchid \n");
      


      sql.append(" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) d on a.voucherid=d.vchid \n");


      sql.append(" where a.accpackageid >= '" + firstAccID + "' \n");
      sql.append(" and a.accpackageid <= '" + lastAccID + "' \n");
      sql.append(" and a.property like '1%' \n");


      return sql.toString();

    }else if(maxValue.equals("[+∞]")){

      StringBuffer sql = new StringBuffer();
      String condition = null;
      NumberFormat f = new DecimalFormat("#,###,##0");
      condition = f.format(Double.parseDouble(minValue)) + "-" + maxValue;
      sql.append(
          " select a.voucherid as voucherNumber,d.vchid as p1,c.vchid as p2,'" +
          condition + "' as moneyCondition, \n");
      sql.append(" a.vchDate,a.typeid,a.oldvoucherid as voucherid,a.summary,a.subjectid as sonsubjectid,a.subjectname1 as subjectname,a.subjectfullname1 as subjectfullname, \n");
      sql.append(" a.dirction,a.occurvalue," + conditionIndex +
                 " as conditionIndex, a.autoid,a.currrate,a.CurrValue,a.Currency,a.Quantity,a.Unitprice,a.Unitname \n");
      sql.append(" from \n");
      sql.append(" c_subjectentry a  \n");


      sql.append(" left join  (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  c on a.voucherid=c.vchid \n");



      sql.append(" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) d on a.voucherid=d.vchid \n");


      sql.append(" where a.accpackageid >= '" + firstAccID + "' \n");
      sql.append(" and a.accpackageid <= '" + lastAccID + "' \n");
      sql.append(" and a.property like '1%' \n");

      sql.append(" and a.occurvalue>" + minValue + " \n");
      return sql.toString();

    }else if(minValue.equals("[-∞]")){

      StringBuffer sql = new StringBuffer();
      String condition = null;
      NumberFormat f = new DecimalFormat("#,###,##0");

      condition = minValue + "-" + f.format(Double.parseDouble(maxValue));
      sql.append(
          " select a.voucherid as voucherNumber,d.vchid as p1,c.vchid as p2,'" +
          condition + "' as moneyCondition, \n");
      sql.append(" a.vchDate,a.typeid,a.oldvoucherid as voucherid,a.summary,a.subjectid as sonsubjectid,a.subjectname1 as subjectname,a.subjectfullname1 as subjectfullname, \n");
      sql.append(" a.dirction,a.occurvalue," + conditionIndex +
                 " as conditionIndex, a.autoid,a.currrate,a.CurrValue,a.Currency,a.Quantity,a.Unitprice,a.Unitname \n");
      sql.append(" from \n");
      sql.append(" c_subjectentry a  \n");


      sql.append(" left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   ) c on a.voucherid=c.vchid \n");



      sql.append(" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  d on a.voucherid=d.vchid \n");



      sql.append(" where a.accpackageid >= '" + firstAccID + "' \n");
      sql.append(" and a.accpackageid <= '" + lastAccID + "' \n");
      sql.append(" and a.property like '1%' \n");

      sql.append(" and a.occurvalue<=" + maxValue + " \n");

      return sql.toString();

    }else{
          return getSQL(firstAccID,lastAccID,projectid,user,Double.parseDouble(maxValue),Double.parseDouble(minValue),conditionIndex);
    }

  }



	public String getSQLnew(String firstAccID,String lastAccID,String projectid,String user,String[] sctions){
		StringBuffer sql=new StringBuffer();
		String condition = "";
		String conditionSqlWhere="";
		String conditionIndex="";
		
		if(sctions.length<2){
			return " ## where 1=2  ";
		}
		
		//构建区间显示字段，构建区间sql的where条件,金额排序序号
	    if(sctions[0].indexOf("[")>=0&&sctions[1].indexOf("[")>=0){
	    	condition+= "     case when 1=1 then '[-∞] - [+∞]' \n";
	    	conditionIndex+=" case when 1=1 then 1 \n";
	    	//conditionSqlWhere+=" and ( 1=1 ";
	    }
	    else if(sctions[0].indexOf("[")>=0){
	    	condition+="      case when occurvalue <="+sctions[1]+" then '[-∞] - " + formatMoney(sctions[1])+"' \n";
	    //	conditionSqlWhere+=" and (  occurvalue <="+sctions[1]+"  \n";
	    	conditionIndex+="      case when occurvalue <="+sctions[1]+" then 1 \n";
	    }
	    else if(sctions[1].indexOf("[")>=0){
	    	condition+="      case when occurvalue >"+sctions[0]+" then '" +formatMoney(sctions[0])+" - [+∞]' \n";
	    	//conditionSqlWhere+=" and (    occurvalue >"+sctions[0]+"  \n";
	    	conditionIndex+="      case when occurvalue >"+sctions[0]+" then 1 \n";
	    }
	    else{
	    	condition+="      case when (occurvalue <="+sctions[1]+" and occurvalue > "+sctions[0]+") then '"+formatMoney(sctions[0])+" - " +formatMoney(sctions[1])+"' \n";
	    	//conditionSqlWhere+=" and (   ( occurvalue <="+sctions[1]+" and occurvalue > "+sctions[0]+") \n";
	    	conditionIndex+="      case when (occurvalue <="+sctions[1]+" and occurvalue > "+sctions[0]+") then 1 \n";
	    }
	    
//	  拼接 子查询 的金额条件
		if(sctions[0].indexOf("[")>-1&&sctions[sctions.length-1].indexOf("[")>-1){
    		conditionSqlWhere+="   ";
    	}else if(sctions[0].indexOf("[")>-1&&sctions[sctions.length-1].indexOf("[")<=-1){
    		conditionSqlWhere+="  and  occurvalue <="+sctions[sctions.length-1]+"  \n";
    	}else if(sctions[sctions.length-1].indexOf("[")>-1&&sctions[0].indexOf("[")<=-1){
    		conditionSqlWhere+="  and  occurvalue >="+sctions[0]+"  \n";
    	}else if(sctions[sctions.length-1].indexOf("[")<=-1&&sctions[0].indexOf("[")<=-1){
    		conditionSqlWhere+="  and  occurvalue >="+sctions[0]+" and occurvalue <="+sctions[sctions.length-1]+"   \n";
    	}
	    
	    for (int i = 2; i < sctions.length-1; i++) {
	    	condition+="      when occurvalue <= "+sctions[i]+"   then '"+formatMoney(sctions[i-1])+" - "+formatMoney(sctions[i])+"' \n";
	    	
	    
	    	
	    	conditionIndex+="      when occurvalue <= "+sctions[i]+"   then "+i+" \n";
		}
	    
	    //最后一个conditionIndex的判断
	    conditionIndex+="    else "+(sctions.length-1)+" end as conditionIndex \n";
	    
	    //last record 无需判断两个都［，因为最后一个只需要一个条件。而且不可能两个都是［。
	    if(sctions.length>2){
		    if(sctions[sctions.length-1].indexOf("[")>=0){
		    	condition+="      else '" + formatMoney(sctions[sctions.length-2])+ " - [+∞]' end as moneyCondition, \n";
		    //	conditionSqlWhere+=" or  occurvalue > "+sctions[sctions.length-2]+" )";
		    }else{
		    	condition+="      when occurvalue <=" + sctions[sctions.length-1]+" then '"+formatMoney(sctions[sctions.length-2])+" - "+formatMoney(sctions[sctions.length-1])+"' end as moneyCondition, \n";
		    //	conditionSqlWhere+="      or (occurvalue <="+sctions[sctions.length-1]+" and occurvalue > "+sctions[sctions.length-2]+") )\n";
		    }		    	
	    }else{
	    	condition+=" end as moneyCondition, \n";
	    	//conditionSqlWhere+=" ) ";
	    }
	    
	    
	    
	   //构建sql
	    
	    
	      sql.append(
	              " select a.voucherid as voucherNumber,d.vchid as p1,c.vchid as p2,x.vchid as p5," +
	              condition + " \n");
	          sql.append(" a.vchDate,a.typeid,a.oldvoucherid as voucherid,a.summary,a.subjectid as sonsubjectid,a.subjectname1 as subjectname,a.subjectfullname1 as subjectfullname, \n");
	          sql.append(" a.dirction,a.occurvalue, "+conditionIndex+", a.autoid,a.currrate,a.CurrValue,a.voucherdebitocc as sumocc,a.Currency,a.Quantity,a.Unitprice,a.Unitname,a.tokenid \n");
	          sql.append(" from ( select * from \n");
	          sql.append(" c_subjectentry a  \n");
	          sql.append("  ${sqlwhere}\n");
	          sql.append(conditionSqlWhere);        
	          sql.append("	${ORDERBY} \n");
	          sql.append("	${LIMIT} \n"); 
	          sql.append("	)a \n"); 
	          sql.append(" left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )   c on a.voucherid=c.vchid \n");
	          sql.append(" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) d on a.voucherid=d.vchid \n");
	          sql.append(" left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )   x on a.autoid=x.vchid \n");
  
	    String subString = "  ${sqlwhere} "+conditionSqlWhere;
	    
	    
	    
		return sql.toString()+"##"+subString;
		
	}


	public String getSQLnew2(String firstAccID,String lastAccID,String projectid,String user,String[] sctions){
		StringBuffer sql=new StringBuffer();
		String condition = "";
		String conditionSqlWhere="";
		String conditionIndex="";
		
		if(sctions.length<2){
			return " ## where 1=2 ";
		}
		
		//构建区间显示字段，构建区间sql的where条件,金额排序序号
	    if(sctions[0].indexOf("[")>=0&&sctions[1].indexOf("[")>=0){
	    	condition+= "     case when 1=1 then '[-∞] - [+∞]' \n";
	    	conditionIndex+=" case when 1=1 then 1 \n";
	    	//conditionSqlWhere+=" and ( 1=1 ";
	    }
	    else if(sctions[0].indexOf("[")>=0){
	    	condition+="      case when voucherDebitOcc <="+sctions[1]+" then '[-∞] - " + formatMoney(sctions[1])+"' \n";
	    //	conditionSqlWhere+=" and (  debitocc <="+sctions[1]+"  \n";
	    	conditionIndex+="      case when voucherDebitOcc <="+sctions[1]+" then 1 \n";
	    }
	    else if(sctions[1].indexOf("[")>=0){
	    	condition+="      case when voucherDebitOcc >"+sctions[0]+" then '" +formatMoney(sctions[0])+" - [+∞]' \n";
	    //	conditionSqlWhere+=" and (    debitocc >"+sctions[0]+"  \n";
	    	conditionIndex+="      case when voucherDebitOcc >"+sctions[0]+" then 1 \n";
	    }
	    else{
	    	condition+="      case when (voucherDebitOcc <="+sctions[1]+" and voucherDebitOcc > "+sctions[0]+") then '"+formatMoney(sctions[0])+" - " +formatMoney(sctions[1])+"' \n";
	    //	conditionSqlWhere+=" and (   ( debitocc <="+sctions[1]+" and debitocc > "+sctions[0]+") \n";
	    	conditionIndex+="      case when (voucherDebitOcc <="+sctions[1]+" and voucherDebitOcc > "+sctions[0]+") then 1 \n";
	    }
	    
	    for (int i = 2; i < sctions.length-1; i++) {
	    	condition+="      when voucherDebitOcc <= "+sctions[i]+"   then '"+formatMoney(sctions[i-1])+" - "+formatMoney(sctions[i])+"' \n";
	    //	conditionSqlWhere+="      or (debitocc <="+sctions[i]+" and debitocc > "+sctions[i-1]+") \n";
	    	conditionIndex+="      when voucherDebitOcc <= "+sctions[i]+"   then "+i+" \n";
		}
	    
	    
//		  拼接 子查询 的金额条件
		if(sctions[0].indexOf("[")>-1&&sctions[sctions.length-1].indexOf("[")>-1){
    		conditionSqlWhere+="   ";
    	}else if(sctions[0].indexOf("[")>-1&&sctions[sctions.length-1].indexOf("[")<=-1){
    		conditionSqlWhere+="  and  voucherDebitOcc <="+sctions[sctions.length-1]+"  \n";
    	}else if(sctions[sctions.length-1].indexOf("[")>-1&&sctions[0].indexOf("[")<=-1){
    		conditionSqlWhere+="  and  voucherDebitOcc >="+sctions[0]+"  \n";
    	}else if(sctions[sctions.length-1].indexOf("[")<=-1&&sctions[0].indexOf("[")<=-1){
    		conditionSqlWhere+="  and  voucherDebitOcc >="+sctions[0]+" and voucherDebitOcc <="+sctions[sctions.length-1]+"   \n";
    	}
	    
	    
	    //最后一个conditionIndex的判断
	    conditionIndex+="    else "+(sctions.length-1)+" end as conditionIndex \n";
	    
	    //last record 无需判断两个都［，因为最后一个只需要一个条件。而且不可能两个都是［。
	    if(sctions.length>2){
		    if(sctions[sctions.length-1].indexOf("[")>=0){
		    	condition+="      else '" + formatMoney(sctions[sctions.length-2])+ " - [+∞]' end as moneyCondition, \n";
		    //	conditionSqlWhere+=" or  debitocc > "+sctions[sctions.length-2]+" )";
		    }else{
		    	condition+="      when voucherDebitOcc <=" + sctions[sctions.length-1]+" then '"+formatMoney(sctions[sctions.length-2])+" - "+formatMoney(sctions[sctions.length-1])+"' end as moneyCondition, \n";
		    //	conditionSqlWhere+="      or (debitocc <="+sctions[sctions.length-1]+" and debitocc > "+sctions[sctions.length-2]+") )\n";
		    }		    	
	    }else{
	    	condition+=" end as moneyCondition, \n";
	    	//conditionSqlWhere+=" ) ";
	    }
	    
	    
	    
	   //构建sql
	    
	    

		
		 sql.append(
	              " select a.voucherid as voucherNumber,d.vchid as p1,c.vchid as p2,x.vchid as p5," +
	              condition + " \n");
	          sql.append(" a.vchDate,a.typeid,a.oldvoucherid as voucherid,a.summary,a.subjectid as sonsubjectid,a.subjectname1 as subjectname,a.subjectfullname1 as subjectfullname, \n");
	          sql.append(" a.dirction,a.occurvalue, "+conditionIndex+", a.autoid,a.currrate,a.CurrValue,a.voucherDebitOcc as sumocc,a.Currency,a.Quantity,a.Unitprice,a.Unitname,a.tokenid \n");
	          sql.append(" from ( select * from \n");
	          sql.append(" c_subjectentry a  \n");
	          sql.append("  ${sqlwhere}\n");
	           
	          sql.append("	${ORDERBY} \n");
	          sql.append("	${LIMIT} \n"); 
	          sql.append("	)a \n"); 
	          sql.append(" left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )   c on a.voucherid=c.vchid \n");
	          sql.append(" left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) d on a.voucherid=d.vchid \n");
	          sql.append(" left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )   x on a.autoid=x.vchid \n");
	          sql.append(conditionSqlWhere); 
	          
	    String subString = "  ${sqlwhere} ";
	   
		return sql.toString()+"##"+subString;
		
		
	}
	
	public String formatMoney(String money){
		try{
			double tempMoney=Double.parseDouble(money);
			java.text.DecimalFormat df=new java.text.DecimalFormat("##,##0");
			return df.format(tempMoney);
		}catch(Exception e){
			e.printStackTrace();
			return money;
		}
	}
}