package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.report.ReportService;
import com.matech.audit.service.task.TaskService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;

public class _5001_0_ct extends AbstractCtPathFunction {

	public String process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		
		
		String areaid = (String) args.get("areaid");
	System.out.println("_5001_0_ct:areaid="+areaid);
	
	//分析构造SQL
	String strSql = "";
	PreparedStatement ps = null;
	ResultSet rs = null;

	
	UserSession userSession = (UserSession) session.getAttribute("userSession");
	if (userSession == null)
		userSession = new UserSession();
	String customerid=userSession.getCurCustomerId();
	
	
	try {
		
		// 项目编号
		String projectid =(String) args.get("curProjectid");
		if (projectid == null || projectid.length() == 0) {
			throw new Exception("请提供项目编号");
		}

		// 底稿编号
		String taskid = (String) args.get("taskid");
		if (taskid == null || taskid.length() == 0) {
			String curTaskCode=(String) args.get("curTaskCode");
			if (curTaskCode == null || curTaskCode.length() == 0) {
				throw new Exception("请提供taskid或curTaskCode");
			}else{
				TaskService ts=new TaskService(conn,projectid);
				taskid=ts.getTaskIdByTaskCode(curTaskCode);
				org.util.Debug.prtOut("通过TASKCODE定位TASKID="+taskid);
			}
		}

		String strManuName = (String) args.get("ManuName");
		List result=null;
		
		ReportService reportservice=new ReportService(conn);
		
		//是否排序, 这里正数是按照ASC排序，负数是按照逆序排序
		
		
		String NodeName = (String) args.get("NodeName");
		if (NodeName == null || "".equals(NodeName)) {
			NodeName="明细表";
		}
		
		
		String strResult = "";
		String strTaskCode = (String) args.get("TaskCode");
		if (strTaskCode == null || "".equals(strTaskCode)) {
			//如果没有提供taskcode,则是合并报表，则通过taskname动态定位
			if (strManuName == null || strManuName.length() == 0) {
				result = reportservice.getTaskIdList(projectid,taskid);
			}else{
				result = reportservice.getTaskIdList(projectid,taskid,strManuName+".xls");
			}
			
			
			if (result.size()>0){
				for (int i = 0; i < result.size(); i++) {
					strResult += result.get(i) + ",";
				}
				org.util.Debug.prtOut("qwh:strResult下级=" + strResult);
			}else{
				//无法通过下级定位，说明是同级，也就是同一个单位里面的汇总表刷明细表录入结果
				strResult=reportservice.getTaskId(projectid, taskid, NodeName, strManuName+".xls") +",";
				org.util.Debug.prtOut("qwh:strResult同级=" + strResult);
			}
		}
		
		//显示的列和分组的列
		String AreaName = (String) args.get("AreaName");
		
		String allfields= (String) args.get("allfields"),allcountfields="";
		
		
		//分析前台对应列
		String allcount=(String)args.get("allcount");
		if (allcount=="" || allcount.equals("")){
			throw new Exception("请提供allcount参数");
		}
		int iAllCount=0;
		try {
			iAllCount = Integer.parseInt(allcount);
		} catch (Exception e) {
		}
		String[] strFieldName=null,strFieldValue=null,strFieldHead=null,strFieldCol=null,strFieldType=null,strFieldCount=null;
		if (iAllCount>0){
			strFieldName=new String[iAllCount+1];
			strFieldValue=new String[iAllCount+1];
			strFieldHead=new String[iAllCount+1];
			strFieldCol=new String[iAllCount+1];
			strFieldType=new String[iAllCount+1];
			strFieldCount=new String[iAllCount+1];
			
			for(int i=1;i<iAllCount;i++){
				strFieldName[i]=(String)args.get("qt_"+i);
				
				if (strFieldName[i]!=null){
					//切割掉s和sd
					strFieldCol[i]=strFieldName[i].replaceAll("s", "").replaceAll("d", "");
					            				
					strFieldHead[i]=(String)args.get("qt_head"+i);
					strFieldValue[i]=(String)args.get("ct_"+strFieldName[i]);
					
					//判断该列是否是数字还是字符串
					
					strSql="select d"+strFieldCol[i]+" from z_manudata \n"
						+"where projectid="+projectid+" \n";
				
					if (strTaskCode != null && !"".equals(strTaskCode)) {
						strSql+=" and taskcode='" + strTaskCode + "'\n";
					}else{
						strSql+=" and taskid in ("+ strResult + "-1) \n";
					}
				
					if (AreaName != null && !"".equals(AreaName)) {
						strSql += " and AreaName='" + AreaName + "' \n";
					}
				
					strSql +=" and d"+strFieldCol[i]+">'' and NOT (replace(replace(d"+strFieldCol[i]+",',',''),'￥','')  REGEXP '^(-?.?[0-9]+)(.[0-9]+)?$') limit 1";
					ps = conn.prepareStatement(strSql);
					rs = ps.executeQuery();
					if (rs.next()){
						strFieldType[i]="showCenter";
						strFieldCount[i]="' ' as d"+strFieldCol[i];
					}else{
						//是全数字
						strFieldType[i]="showMoney";
						strFieldCount[i]="sum(d"+strFieldCol[i]+") as d"+strFieldCol[i];
					}
					rs.close();
					
				}
				
				System.out.println("分析："+strFieldName[i]+"|"+strFieldValue[i]
				           +"|"+strFieldHead[i]+"|"+strFieldCol[i]);
			}
		}
		
		
		//分析前台对应的
		String strOrderBy= (String) args.get("OrderBy");
		String strGroupBy = (String) args.get("GroupBy");
		String strWhere=" ";
		
		if (strGroupBy!=null  && !"".equals(strGroupBy)){
			
			String[] strGroups = strGroupBy.split("_");
			
			int iLocate=-1;
			//拼装GROUP BY
			for (int i=0;i<strGroups.length;i++){
				iLocate=locateArray(strFieldCol,strGroups[i]);
				if (iLocate>=0){
					strWhere+=" and d"+ iLocate +" = '" + strFieldValue[iLocate] +"'";
				}
			}
		}else{
			int iLocate=-1;
			String leftcol=(String)args.get("leftcol");
			iLocate=locateArray(strFieldName,leftcol);
			if (iLocate>=0){
				strWhere+=" and d"+ strFieldCol[iLocate] +" = '" + strFieldValue[iLocate] +"'";
			}
		}
		
		//设置对应列
		DataGridProperty dgProperty = new DataGridProperty();
		
		dgProperty.addColumn("底稿编号", "taskcode","showCenter");
		dgProperty.addColumn("表页", "sheetname","showCenter");
		dgProperty.addColumn("区域", "areaname","showCenter");
		
		for (int i=0;i<strFieldName.length;i++){
			if (strFieldName[i]!=null && !"".equals(strFieldName[i])){
				dgProperty.addColumn(strFieldHead[i], "d"+strFieldCol[i],strFieldType[i]);
				
				allcountfields+=","+strFieldCount[i];
				
			}
		}
		
		strSql="select autoid,taskcode,sheetname,areaname"+allfields +" \n"
			+" from z_manudata \n"  
			+"where projectid="+projectid+" \n" ;
		
		if (strTaskCode != null && !"".equals(strTaskCode)) {
			strSql+=" and taskcode='" + strTaskCode + "'\n";
		}else{
			strSql+=" and taskid in ("+ strResult + "-1) \n";
		}
		
		strSql+=" and AreaName='"+AreaName+"'  \n" +strWhere;
			
		System.out.println("qwh:5001_ct="+strSql);			
		dgProperty.setSQL(strSql);
		
		//设置统计行
		strSql="select ' ' as taskcode,' 合计 ' as sheetname,' ' as areaname"+allcountfields +" \n"
			+" from z_manudata \n"  
			+"where projectid="+projectid+" \n" ;
	
		if (strTaskCode != null && !"".equals(strTaskCode)) {
			strSql+=" and taskcode='" + strTaskCode + "'\n";
		}else{
			strSql+=" and taskid in ("+ strResult + "-1) \n";
		}
		
		strSql+=" and AreaName='"+AreaName+"'  \n" +strWhere;
			
		System.out.println("qwh:5001_count_ct="+strSql);			

		dgProperty.setCountsql(strSql);
	
		//下面是构造查询结果；
		
		
		dgProperty.setTableID("_5001_ct");
		
		dgProperty.setPrintEnable(true);
		dgProperty.setPrintTitle("抵销分录");
		dgProperty.setPrintCharColumn("1`2`3");
		
		dgProperty.setCustomerId(customerid);
		
		
		dgProperty.setCurProjectDatabase(true);
		dgProperty.setPageSize_CH(50);
		
		
		
		
		
		
		
		dgProperty.setOrderBy_CH("taskcode,sheetname,areaname");
		dgProperty.setDirection("asc,asc,asc");
	
		request.getSession().setAttribute(DataGrid.sessionPre + dgProperty.getTableID(), dgProperty);
		
		
		
		//下面是抵销分录查询：
//		抵销分录条件
		String strEliminateFilterBy= (String) args.get("EliminateFilterBy");
		if (strEliminateFilterBy!=null && !"".equals(strEliminateFilterBy)){
			strEliminateFilterBy=" and " + strEliminateFilterBy.replaceAll("~", "'") +  " ";
		}
		
		//抵销分录发生额汇总方式
		String strEliminateOcc= (String) args.get("EliminateOcc"),strEliminateFilterBy1="";
		if ("借发生".equals(strEliminateOcc)){
			//至汇总本年
			strEliminateOcc="(if(direction=1,occurvalue,0)) as occ";
			strEliminateFilterBy1=" and accountyear='本年'";
		}else if ("贷发生".equals(strEliminateOcc)){
			strEliminateOcc="(if(direction=-1,occurvalue,0)) as occ";
			strEliminateFilterBy1=" and accountyear='本年'";
		}else if ("发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1="";
		}else if ("本期发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1=" and accountyear = '本年'";
		}else if ("期初发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1=" and accountyear <> '本年'";
		}else if ("期初借发生".equals(strEliminateOcc)){
			strEliminateOcc="(if(direction=1,occurvalue,0)) as occ";
			strEliminateFilterBy1=" and accountyear <> '本年'";
		}else if ("期初贷发生".equals(strEliminateOcc)){
			strEliminateOcc="(if(direction=-1,occurvalue,0)) as occ";
			strEliminateFilterBy1=" and accountyear <> '本年'";
		}else if ("1年前期初发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1=" and accountyear = '1年前'";
		}else if ("2年前期初发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1=" and accountyear = '2年前'";
		}else if ("3年前期初发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1=" and accountyear = '3年前'";
		}else if ("4年前期初发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1=" and accountyear = '4年前'";
		}else if ("5年前期初发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * occurvalue) as occ";
			strEliminateFilterBy1=" and accountyear = '5年前'";
		}
		//原币
		else if ("原币借发生".equals(strEliminateOcc)){
			strEliminateOcc="(if(direction=1,currvalue,0)) as occ";
			strEliminateFilterBy1=" and accountyear='本年'";
		}else if ("原币贷发生".equals(strEliminateOcc)){
			strEliminateOcc="(if(direction=-1,currvalue,0)) as occ";
			strEliminateFilterBy1=" and accountyear='本年'";
		}else if ("原币发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * currvalue) as occ";
			strEliminateFilterBy1=" ";
		}else if ("原币期初发生".equals(strEliminateOcc)){
			strEliminateOcc="(direction * currvalue) as occ";
			strEliminateFilterBy1=" and accountyear <> '本年'";
		}else{
			strEliminateOcc="";
		}
		
		//抵销分录加总方向
		String strEliminateDirection =(String) args.get("EliminateDirection");
		if (strEliminateDirection==null || "".equals(strEliminateDirection)){
			strSql="select subjectname from z_task where projectid="+projectid+" and taskid=" + taskid;
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();
			if (rs.next()){
				strEliminateDirection=rs.getString(1);
				org.util.Debug.prtOut("strEliminateDirection="+strEliminateDirection);
			}
			rs.close();
			ps.close();
			
			org.util.Debug.prtOut("strSql="+strSql);
			
			org.util.Debug.prtOut("strEliminateDirection="+strEliminateDirection);
		}
		if ("贷".equals(strEliminateDirection) || "－1".equals(strEliminateDirection)) {
			strEliminateDirection="-1";
		}else if (!"-1".equals(strEliminateDirection)) {
			//如果不等于-1，无条件置为1
			strEliminateDirection="1";
		}
		
		
		strSql="select a.accountid,a.accountid+1-( \n" 
				+"	select min(abs(accountid)) from z_accountrectifyentry1 where  projectid=20081383 \n"  
 				+"	) as accountid1,a.autoid,a.accpackageid,a.projectid,a.accountlevel as accountlevel1, \n" 
				+"	concat(a.relatecompany,'-',t2.taskname) as relatecompany,  a.accountdate,a.serail, \n"
				+"	concat(a.accounttype,'-',replace(t3.taskName,'明细表.xls','')) as accounttype, \n"
				+"	a.summary,a.reference,if(a.direction=1,'借','贷') as direction,a.direction as oridirection , \n"
				+"	a.occurvalue,  a.currrate,a.currvalue,a.currency,a.accountyear,a.filluser,a.property,  \n"
 				+"	concat(if(concat(n1,'[',v1,']')='[]','',concat(n1,'[',v1,']')),   \n"
				+"	if(concat(n2,'[',v2,']')='[]','',concat(n2,'[',v2,']')),  if(concat(n3,'[',v3,']')='[]','',concat(n3,'[',v3,']')), \n"  
				+"	if(concat(n4,'[',v4,']')='[]','',concat(n4,'[',v4,']')),  if(concat(n5,'[',v5,']')='[]','',concat(n5,'[',v5,']')),   \n"
				+"	if(concat(n6,'[',v6,']')='[]','',concat(n6,'[',v6,']')),  if(concat(n7,'[',v7,']')='[]','',concat(n7,'[',v7,']')),   \n"
				+"	if(concat(n8,'[',v8,']')='[]','',concat(n8,'[',v8,']')),  if(concat(n9,'[',v9,']')='[]','',concat(n9,'[',v9,']')),   \n"
				+"	if(concat(n10,'[',v10,']')='[]','',concat(n10,'[',v10,']'))) as dixiao   \n"
				+"	from ( select * from z_accountrectifyentry1 \n"
				
				+"			where projectid="+projectid+"   \n";
		if (strManuName!=null && !"".equals(strManuName)){
				strSql+="			and sheetname='"+strManuName+"' \n";
		}
		strSql+="			and areaname='"+AreaName+"' \n"
				+ strEliminateFilterBy1 +" \n";
		if (strTaskCode != null && !"".equals(strTaskCode)) {
					strSql+=" and accounttype='" + strTaskCode + "'\n";
		}else{
					strSql+="and accounttype in ( \n"
							+"	select taskcode from z_task \n"
							+"	where projectid="+projectid+" and taskid in ("+ strResult + "-1) \n" 
							+" ) \n";
		}

		//追加条件
		if (strEliminateFilterBy != null && !"".equals(strEliminateFilterBy)) {
			strSql += strEliminateFilterBy;
		}
		
		strSql+=")a  left join z_task t2 on t2.taskcode = a.relatecompany and t2.projectid=a.projectid \n"  
		+"	left join z_task t3 on t3.taskcode = a.accounttype and t3.projectid=a.projectid where a.projectid="+projectid;
		
		System.out.println("显示相关的抵销分录:"+strSql);
		
		
		//显示最终的sql
		DataGridProperty pp = new DataGridProperty();
		
		pp.setCurProjectDatabase(true);
		pp.setTableID("accountrectify");
		pp.setCustomerId(customerid);
		pp.setPageSize_CH(50);
		
		pp.setPrintEnable(true);	//关闭dg打印
		pp.setPrintTitle("抵销分录");
		pp.setPrintCharColumn("1`2`3`4`7`8");
		
		pp.setOrderBy_CH("accountid1,serail");
		pp.setDirection("asc,asc");
		
		String hiddenCol[] = {"accountid1"};
		pp.setHiddenCol(hiddenCol);
		
		pp.addColumn("合并编号", "accountid1");
		pp.addColumn("关联公司", "relatecompany");
		pp.addColumn("抵销到明细报表", "accounttype");
		pp.addColumn("摘要", "summary");
		pp.addColumn("方向", "direction","showCenter");
		pp.addColumn("金额", "occurvalue","showMoney");
		pp.addColumn("抵销年份", "accountyear");
		pp.addColumn("抵销人员", "filluser");
		pp.addColumn("附加条件", "dixiao");
		
		
		pp.addSqlWhere("relationC", "and a.relatecompany like '%${relationC}%'");
		pp.addSqlWhere("taskcode", "and a.accounttype like '${taskcode}%'");
		pp.addSqlWhere("FillUser", "and a.filluser like '%${FillUser}%'");
		pp.addSqlWhere("Summary", "and a.summary like '%${Summary}%'");
		pp.addSqlWhere("accountyear", "and a.accountyear = '${accountyear}'");
		
		pp.addSqlWhere("money", "and a.oridirection=${moneyItem} and occurvalue  ${moneyLogic} ${moneyValue} ");

		
		pp.setSQL(strSql);
		request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);

		
	
	} catch (Exception e) {
		e.printStackTrace();
		throw new Exception(e.getMessage());
	}finally{
		if (rs!=null)rs.close();
		if (ps!=null)ps.close();
	}
	
	
	return "/AuditSystem/ReportProject/ManuList.jsp";
		

	}
	
	private int locateArray(String[] myarray,String key){
		
		if (myarray==null){
			return -1;
		}
		if (key==null){
			return -1;
		}
		
		for (int i=0;i<myarray.length;i++){
			if (key.equals(myarray[i])){
				return i;
			}
		}
		
		return -1;
	}
	
}
