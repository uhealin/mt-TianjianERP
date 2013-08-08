package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.matech.audit.service.report.ReportService;
import com.matech.audit.service.task.TaskService;



/**
 * 
 * 5001公式，专门供合并报表体系使用
 * 
 * =取列公式插入(5001, "", "d1", "&ColNo=1&AreaName=明细&GroupBy=1&ManuName=明细A")
 * 
 * 
 * 公式修改说明
 * 
 * 1.on 20080316:新增参数：
 * EliminateOcc: 抵销分录模式：借发生,贷发生,发生,原币借发生,原币贷发生，原币发生
 * EliminateFilterBy: 合并报表过滤条件，比如单项重大、不重大等；
 * 
 * 调用说明：
 * =取列公式覆盖(5001, "", "sdd4", "&ColNo=4&AreaName=明细&GroupBy=1&ManuName=现金明细表&EliminateOcc=发生")
 * 
 * 2.on 20080316:增加一个Sum参数
 * 指定后将无条件使用sum得到一个汇总数
 * 
 * =取自定义函数(5001,"sdd4","&ColNo=4&AreaName=明细&GroupBy=1&ManuName=现金明细表&EliminateOcc=发生&Sum=1")
 * 其后台调用的URL：
 * http://127.0.0.1:5199/AuditSystem/AS_SYSTEM/function.jsp&fname=getZdyHs&areaid=5001&field=sdd4&limit=1&ColNo=4&AreaName=明细&GroupBy=1&ManuName=现金明细表&EliminateOcc=发生&Sum=1&curProjectid=200788268&curPackageid=1000332002&taskid=103028&userId=19&curTaskCode=02-2-65&sessionId=4CAC4C967947E200084FDC8479651D2E&manuid=113306&manuname=1.xls&readonly=false&_UTF8=1&_qq=0.533424
 * 
 * 3.on 20080317:EliminateOcc新增加支持的模式，包括： 期初发生、1年前期初发生、2年前期初发生、3年前期初发生、4年前期初发生、5年前期初发生；
 * 并修改了原来支持的发生、借发生、贷发生的内部逻辑，总体平衡关系说明如下：
 * 
 * I、1年前期初发生 将只汇总调整到【1年前】的金额（借－贷）；2年前期初发生、3年前期初发生、4年前期初发生、5年前期初发生类似；
 * II、期初发生 将汇总所有调整到【1年前、2年前、3年前、4年前、5年前】的金额；
 * III、发生（实际相当于期末发生）将汇总所有的发生，包括本年以及1到5年前的；
 * IV、借发生将只汇总调整到【本年】的借方金额，贷发生类似（不含1年前至5年前的调整数）；
 * 
 * 原币逻辑处理类似，支持的类型说明如下：
 * I、原币期初发生：汇总所有调整到【1年前、2年前、3年前、4年前、5年前】的原币金额
 * II、原币发生（实际相当于原币期末发生）将汇总所有的原币发生数，包括本年以及1到5年前的；
 * III、原币借发生将只汇总调整到【本年】的原币借方金额，原币贷发生类似（不含1年前至5年前的调整数）；
 * 
 * 截至20080317，EliminateOcc总共支持的类型包括：
 * 借发生,贷发生,发生,期初发生、1年前期初发生、2年前期初发生、3年前期初发生、4年前期初发生、5年前期初发生
 * 原币借发生,原币贷发生，原币发生，原币期初发生；
 * 
 * 相互间平衡关系如下：
 * I、期初发生＝1年前期初发生＋2年前期初发生＋3年前期初发生＋4年前期初发生＋5年前期初发生
 * II、期初发生＋借发生＋贷发生＝发生
 * III、原币期初发生＋原币借发生＋原币贷发生＝原币发生
 * 
 * 4.on 20080318: 新增参数EliminateDirection
 * 如果提供EliminateDirection，我就按照EliminateDirection指定的方向；
	如果没有提供，我就去取底稿对应科目方向；
	如果底稿对应科目方向也没有提供，我就无条件按借方走。
 * 
 * 5 on 20080320:新增参数NodeName,以支持从附注节点找表
 * 不提供，则缺省按明细表节点查找
 * 
 * 引用举例：
 * =取列公式覆盖(5001, "", "d4", "&ColNo=4&AreaName=明细&ManuName=现金明细表&NodeName=附注表")
 * 
 * 
 * @author winnerQ
 *
 */
public class _5001_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		String strSql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			// 项目编号
			String projectid = request.getParameter("curProjectid");
			if (projectid == null || projectid.length() == 0) {
				throw new Exception("请提供项目编号");
			}

			// 底稿编号
			String taskid = request.getParameter("taskid");
			if (taskid == null || taskid.length() == 0) {
				String curTaskCode=request.getParameter("curTaskCode");
				if (curTaskCode == null || curTaskCode.length() == 0) {
					throw new Exception("请提供taskid或curTaskCode");
				}else{
					TaskService ts=new TaskService(conn,projectid);
					taskid=ts.getTaskIdByTaskCode(curTaskCode);
					org.util.Debug.prtOut("通过TASKCODE定位TASKID="+taskid);
				}
			}

			String strManuName = request.getParameter("ManuName");
			List result=null;
			
			ReportService reportservice=new ReportService(conn);
			
			//是否排序, 这里正数是按照ASC排序，负数是按照逆序排序
			String strOrderBy= request.getParameter("OrderBy");
			String strOrderFieldsBySum = "",strOrderFields="";
			String strGroupBy = request.getParameter("GroupBy");
			if (strOrderBy!=null && !"".equals(strOrderBy)){
				String[] strOrders=strOrderBy.split("_");
				strOrderBy="";
				String strOrderTemp="";
				
				for (int i = 0; i < strOrders.length; i++) {
					if (strOrders[i].charAt(0)=='-'){
						
						strOrderTemp=strOrders[i].substring(1);
						
						//如果是负数，就按照降序排列
						strOrderBy +=",od"+ strOrderTemp  + " desc,ood"+ strOrderTemp  + " desc";
						
					}else{
						strOrderTemp=strOrders[i];
						
						//如果不是负数，就按照升序排列
						strOrderBy +=",od"+ strOrderTemp  + " asc,ood"+ strOrderTemp  + " asc";
					}
					
					strOrderFieldsBySum +=",sum(d"+strOrderTemp+") as od" +strOrderTemp+",group_concat(d"+strOrderTemp+") as ood" +strOrderTemp;
					
					//convert(d4,DECIMAL )  as od4
					strOrderFields +=",convert(d"+strOrderTemp+",DECIMAL ) as od" +strOrderTemp+",d"+strOrderTemp+" as ood" +strOrderTemp;	
					
				}
				
				
				//去掉开头的，
				strOrderBy=" order by "+ strOrderBy.substring(1);
				
			}

			
			String NodeName = request.getParameter("NodeName");
			if (NodeName == null || "".equals(NodeName)) {
				NodeName="明细表";
			}
			
			
			String strResult = "";
			String strTaskCode = request.getParameter("TaskCode");
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
			
			// 获取哪一列
			String strColNo = request.getParameter("ColNo");
			if (strColNo == null) {
				throw new Exception("ColNo未指定");
			}
			int iColNo = 0;
			try {
				iColNo = Integer.parseInt(strColNo);
			} catch (Exception e) {
			}
			if (iColNo < 1 || iColNo > 30) {
				throw new Exception("ColNo[" + iColNo + "]错误,超过1到30范围");
			}

			//条件
			String strFilterBy= request.getParameter("FilterBy");
			if (strFilterBy!=null && !"".equals(strFilterBy)){
				strFilterBy=" and " + strFilterBy.replaceAll("~", "'") +  " ";
			}
			
			//抵销分录条件
			String strEliminateFilterBy= request.getParameter("EliminateFilterBy");
			if (strEliminateFilterBy!=null && !"".equals(strEliminateFilterBy)){
				strEliminateFilterBy=" and " + strEliminateFilterBy.replaceAll("~", "'") +  " ";
			}
			
			//抵销分录发生额汇总方式
			String strEliminateOcc= request.getParameter("EliminateOcc"),strEliminateFilterBy1="";
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
			String strEliminateDirection =request.getParameter("EliminateDirection");
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
			
			//显示的列和分组的列
			String AreaName = request.getParameter("AreaName");
			
System.out.println("5001:AreaName="+AreaName);
			
			String strFields="";
			if (strGroupBy==null||"".equals(strGroupBy)){
				//没有GROUPBY
				strSql = "select d"
					+ iColNo
					+ ",autoid "+strOrderFields+" \n from z_manudata where projectid="+projectid+" \n";
								
				if (strTaskCode != null && !"".equals(strTaskCode)) {
					strSql+=" and taskcode='" + strTaskCode + "'\n";
				}else{
					strSql+=" and taskid in ("+ strResult + "-1) \n";
				}
				
				if (AreaName != null && !"".equals(AreaName)) {
					strSql += " and AreaName='" + AreaName + "' ";
				}
				
				if (strFilterBy != null && !"".equals(strFilterBy)) {
					strSql += strFilterBy;
				}
				
				if (strOrderBy!=null && !"".equals(strOrderBy)){
					strSql += strOrderBy;
				}else{
					strSql += "order by autoid";
				}
			}else{
				//有GROUPBY
				String[] strGroups = strGroupBy.split("_");
				
				boolean bFound=false;
				String strJoinSelect=""; //拼装成类似concat_ws('-',ifnull(d1,''),ifnull(d2,''))的格式
				 
				//拼装GROUP BY
				for (int i=0;i<strGroups.length;i++){
					if (strColNo.equals(strGroups[i])){
						bFound=true;
					}
					strFields+="d"+strGroups[i]+",";
					
					strJoinSelect+=",ifnull($关联表名$.d"+strGroups[i]+",'')";
				}
				strFields=strFields.substring(0,strFields.length()-1);
				strJoinSelect="concat_ws('-'" +strJoinSelect + ")";
				
				
				//再次拼装ORDERBY
				if ((strOrderBy==null || "".equals(strOrderBy)) && !(strGroupBy==null||"".equals(strGroupBy))){
					//有提供GROUPBY
					strOrderFieldsBySum=",min(autoid) as oautoid";
					strOrderBy="order by oautoid,"+strFields;
				}
				
				
				if (bFound){
					//找到了，
					strSql = "select " + strFields + strOrderFieldsBySum+ " \n from z_manudata \n"
						+"where projectid="+projectid+" ";
					
					if (strTaskCode != null && !"".equals(strTaskCode)) {
						strSql+=" and taskcode='" + strTaskCode + "'\n";
					}else{
						strSql+=" and taskid in ("+ strResult + "-1) \n";
					}
					
					if (AreaName != null && !"".equals(AreaName)) {
						strSql += " and AreaName='" + AreaName + "' \n";
					}
					
					if (strFilterBy != null && !"".equals(strFilterBy)) {
						strSql += strFilterBy;
					}
					
					//追加GROUPBY
					strSql+= "group by " + strFields +"\n";
					
				}else{
					//没有找到，说明是SUM列
					
					
					//判断当前这一列是字符串,还是数字或NULL；是字符，就用group_concat, 是数字，就用sum；
					strSql="select d"+strColNo+" from z_manudata \n"
						+"where projectid="+projectid+" \n";
					
					if (strTaskCode != null && !"".equals(strTaskCode)) {
						strSql+=" and taskcode='" + strTaskCode + "'\n";
					}else{
						strSql+=" and taskid in ("+ strResult + "-1) \n";
					}
					
					if (AreaName != null && !"".equals(AreaName)) {
						strSql += " and AreaName='" + AreaName + "' \n";
					}
					
					//strSql +=" and length(abs(d"+strColNo+"))!= length(d"+strColNo+") limit 1";
					
					strSql +=" and d"+strColNo+">'' and NOT (replace(replace(d"+strColNo+",',',''),'￥','')  REGEXP '^(-?.?[0-9]+)(.[0-9]+)?$') limit 1";
					
					org.util.Debug.prtOut("qwh:5001:检查是否字符串：strsql=" + strSql);
					
					
					ps = conn.prepareStatement(strSql);
					rs = ps.executeQuery();
					if (rs.next()){
						org.util.Debug.prtOut("qwh:5001:有记录，说明是字符串而不是数字或全NULL；" );
						
						/*
						 
							select d0,
							group_concat( d1) as result, 
							replace(
								concat(
									replace(substring(group_concat(d1),1,1),',','')
									,substring(group_concat(d1),2,length(group_concat( d1))-2)
									,replace(substring(group_concat( d1),length(group_concat( d1)),length(group_concat( d1))),',','')
								)
							,',,',',') as fixresult
							from
							(
							select 1 as d0,'' as d1
							union all
							select 1 as d0,null as d1
							union all
							select 1 as d0,'aa' as d1
							union all
							select 1 as d0,''
							union all
							select 1 as d0,''
							union all
							select 1 as d0,''
							union all
							select 1 as d0,''
							)a
							group by d0

						 	还有一种写法
						 	
						 	select a.*,b.d2 from 
							(
							select d1,min(autoid) as oautoid from z_manudata
							where projectid=2008138326 
							 and taskid in (103320,103543,103766,103989,104212,104435,-1)
							 and AreaName='明细'
							group by d1
							)a left join (
							select d1,group_concat(distinct d2) as d2 from z_manudata
							where projectid=2008138326 
							 and taskid in (103320,103543,103766,103989,104212,104435,-1)
							 and AreaName='明细'
							 and d2>''
							group by d1
							)b
							on a.d1=b.d1
							order by oautoid,d1
						 
						 */
						
						
						//有记录，说明是字符串而不是数字或全NULL；
						//strSql = "select " + strFields + ",group_concat(distinct d" + strColNo + ") as d" + strColNo +strOrderFieldsBySum+ " from z_manudata \n"
						//	+"where projectid="+projectid+" \n";
						strSql = "select " + strFields + ",if(group_concat(distinct d" + strColNo + ")=',','',group_concat(distinct d" + strColNo + ")) as d" + strColNo + " " +strOrderFieldsBySum+ " from z_manudata \n"
							+"where projectid="+projectid+" \n";
					}else{
						org.util.Debug.prtOut("qwh:5001://数字或者全NULL");
						//数字或者全NULL；
						strSql = "select " + strFields + ",sum(d" + strColNo + ") as d" + strColNo +strOrderFieldsBySum+ " from z_manudata \n"
							+"where projectid="+projectid+" \n";
					}
					rs.close();
					
					if (strTaskCode != null && !"".equals(strTaskCode)) {
						strSql+=" and taskcode='" + strTaskCode + "'\n";
					}else{
						strSql+=" and taskid in ("+ strResult + "-1) \n";
					}
					
					
					if (AreaName != null && !"".equals(AreaName)) {
						strSql += " and AreaName='" + AreaName + "' \n";
					}
					
					if (strFilterBy != null && !"".equals(strFilterBy)) {
						strSql+= strFilterBy;
					}
					
					//追加GROUPBY
					strSql+= "group by " + strFields +"\n";
					
					//有抵销分录模式
					if (strEliminateOcc.length()>0){
						/**
						 * 
						 拼装成最终类似以下的格式： 
						 
						 select a.*,a.d2 + ifnull(b.occ,0) as sdd2 from
						(
							select d1,sum(d2) as d2,min(autoid) as oautoid from z_manudata 
							where projectid=200788268 
							 and taskid in (102814,102808,-1) 
							 and AreaName='明细' 
							group by d1
						)a left join (
						select areavalue,
						sum(direction * occurvalue) as occ
						from z_accountrectifyentry1
						where projectid=200788268 
						and sheetname='现金明细表'
						and areaname='明细'
						and accounttype in (
							select taskcode from z_task
							where projectid=200788268  and taskid in (102814,102808,-1) 
						  )
						group by areavalue
						
						) b
						on a.d1 = b.areavalue
						order by oautoid,d1
						 

						这个SQL有错误，不支持
						strSql="select a.*, (a.d"+iColNo+" + "+strEliminateDirection+" * ifnull(b.occ,0) ) as sdd"+iColNo+" from \n"
								+"( \n"
									+ strSql
								+")a left join ( \n"
								+"select areavalue," + strEliminateOcc+ "\n"
								+"from z_accountrectifyentry1 \n"
								+"where projectid="+projectid+"  \n"
								+"and sheetname='"+strManuName+"' \n"
								+"and areaname='"+AreaName+"' \n"
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
						
						strSql+="group by areavalue \n"
								+") b \n on "+strJoinSelect+" = b.areavalue \n";
						
						*/
						
						
						/*
						 
						 新版的先获得抵销到的对照，再把抵销分录和原来的z_manu表 INNER JOIN到一起，然后再按照类似z_manudata group by 的方法来group by
						   最后再把2个结果left join到一起。
						 
						 

							select a.*, (a.d10 + 1 * ifnull(b.occ,0) ) as sdd10 from 
							( 
								select d1,d4,sum(d10) as d10,min(autoid) as oautoid from z_manudata 
								where projectid=200788268 
								 and taskid in (102857,103119,-1) 
								 and AreaName='明细' 
								 and d15='是' group by d1,d4
							)a left join ( 
							
							   select d1,d4,sum(occ) as occ from
							     (
									select distinct a.d1,a.d4 ,(b.direction * b.occurvalue) as occ
									from z_manudata a, 
									(
										select * from z_accountrectifyentry1
										 where projectid=200788268  
										and sheetname='短期借款明细表' 
										and areaname='明细' 
										and accounttype in ( 
											select taskcode from z_task 
											where projectid=200788268 and taskid in (102857,103119,-1) 
										 ) 
								
								
									)b
									where 
									a.projectid=200788268 
									and a.taskid in (102857,103119,-1) 
									and a.AreaName='明细' 
									and b.accounttype=a.taskcode
									and b.areavalue=concat(a.d1)
							     ) c
							     group by d1,d4
							) b 
							 on a.d1=b.d1 and a.d4=b.d4
							order by oautoid,d4
						 
						 */
						
						//获取对应底稿的抵销到设置
						String strSql1="select taskcontent from z_task \n" 
								+"where projectid="+projectid+" \n" ;
						
						if (strTaskCode != null && !"".equals(strTaskCode)) {
							strSql1+=" and taskcode='" + strTaskCode + "'\n";
						}else{
							strSql1+="and  taskid in ("+ strResult + "-1) \n";
						}
						strSql1+="and taskcontent like '%抵销到=%' order by orderid \n";
						
						ps = conn.prepareStatement(strSql1);
						rs = ps.executeQuery();
						
						String strDxd=strGroupBy;//缺省值就是分组；
						String[] strDxds=null;
						if (rs.next()){
							//有设置，就从数据库设置里面取；
							strDxd=rs.getString(1);
							strDxds=strDxd.split("\\|");
							for(int i=0;i<strDxds.length;i++) {
								if (strDxds[i]!=null && strDxds[i].indexOf("抵销到=")>-1){
									strDxds=strDxds[i].split("=");
									strDxd=strDxds[1].replaceAll("`", "_");
									break;
								}
							}
						}
						rs.close();
						ps.close();
						
						strDxds=strDxd.split("_");
						strDxd="";
						for (int i=0;i<strDxds.length;i++){
							strDxd+=",ifnull(a.d"+strDxds[i]+",'')";
						}
						strDxd="concat_ws('-'" +strDxd + ")";
						
						
						
						//开始最后的SQL拼装
						strSql="select a.*, (a.d"+iColNo+" + "+strEliminateDirection+" * ifnull(b.occ,0) ) as sdd"+iColNo+" from \n"
								+"( \n"
									+ strSql
								+")a left join ( \n"
								+"	select "+strFields+",sum(occ) as occ from \n"
								+"	( \n"
								
								
								+"		select distinct "+strFields+" ," + strEliminateOcc+ " \n"
								+"		from z_manudata a,  \n"
								+"		( \n"
								
								+"			select * from z_accountrectifyentry1 \n"
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
									
						strSql+="		)b \n"
								+"		where  \n"
								+"		a.projectid="+projectid+" \n" ;
						
						if (strTaskCode != null && !"".equals(strTaskCode)) {
							strSql+=" and a.taskcode='" + strTaskCode + "'\n";
						}else{
							strSql+=" and a.taskid in ("+ strResult + "-1) \n";
						}
						
						strSql+="		and a.AreaName='"+AreaName+"'  \n"
								+"		and b.accounttype=a.taskcode \n"
								+"		and b.areavalue="+strDxd+" \n"
								+"	) c \n"
								+"	group by "+strFields+" \n"
								+") b  \n"
								+"on " + strJoinSelect.replaceAll("\\$关联表名\\$", "a")+ " = " + strJoinSelect.replaceAll("\\$关联表名\\$", "b") +" \n";
					}
				}
				
				if (strOrderBy!=null && !"".equals(strOrderBy)){
					strSql += strOrderBy;
				}else{
					//如果没有提供order by 参数，就按照
					strSql+=" order by " + strFields;
				}
				
			}

			
			String strSum = request.getParameter("Sum");
System.out.println("qwh:strSum="+strSum);


			if (strSum!=null && !"".equals(strSum)){
				//提供了sum参数，无条件将其加总到一起，只供单元格取数调用
				strSql="select sum(sdd"+iColNo+") as sdd"+iColNo+",sum(d"+iColNo+") as d"+iColNo+" \n"
					+"from ("
					+strSql
					+") a";
			}else{
				//没有提供，则提供明细项
				String strLimit = request.getParameter("Limit");
				
				String strLeftOrderBy = request.getParameter("LeftOrderBy");
				if (strLeftOrderBy!=null && !"".equals(strLeftOrderBy)){
					String[] strLefts=strLeftOrderBy.split("_");
					strLeftOrderBy="select '"+strLefts[0]+"' as leftorderby \n";
					
					for (int i=1;i<strLefts.length;i++){
						strLeftOrderBy+="union \n select '"+strLefts[i]+"' \n";
					}
					
				}
				
				if (strLeftOrderBy!=null && !"".equals(strLeftOrderBy)){
					strSql="select a.leftorderby,b.* \n"
						+"from (\n"
						+ strLeftOrderBy 
						+")a left join (\n"
						+strSql
						+")b \n"
						+"on a.leftorderby=b."+strFields;
				}else{
					
					if (strLimit!=null && !"".equals(strLimit)){
						strSql += " limit " +strLimit;
					}
				}
				
			}

System.out.println("qwh:5001="+strSql);			
			
			org.util.Debug.prtOut("qwh:5001:resultSql="+strSql);
			

			// 返回指定列的缓存结果

			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

}
