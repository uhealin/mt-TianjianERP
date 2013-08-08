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
 * @author LuckyStar
 *
 */
public class _5002_0 extends AbstractAreaFunction {

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

			
			
			List result=null;
			
			ReportService reportservice=new ReportService(conn);
			
			//是否排序, 这里正数是按照ASC排序，负数是按照逆序排序
			String strOrderBy= request.getParameter("OrderBy");
			String strOrderFieldsBySum = "",strOrderFields="";
			if (strOrderBy!=null && !"".equals(strOrderBy)){
				String[] strOrders=strOrderBy.split("_");
				strOrderBy="";
				String strOrderTemp="";
				for (int i = 0; i < strOrders.length; i++) {
					
					if (strOrders[i].charAt(0)=='-'){
						
						//如果是负数，就按照降序排列
						strOrderTemp=strOrders[i].substring(1);
						strOrderBy +=",od"+ strOrderTemp  + " desc,ood"+ strOrderTemp  + " desc";
						
					}else{
						
						//如果不是负数，就按照升序排列
						strOrderTemp=strOrders[i];
						
						strOrderBy +=",od"+ strOrderTemp  + " asc,ood"+ strOrderTemp  + " asc";
					}
					
					strOrderFieldsBySum +=",sum(replace(replace(d"+strOrderTemp+",',',''),'￥','')) as od" +strOrderTemp+",sum(replace(replace(d"+strOrderTemp+",',',''),'￥','')) as ood" +strOrderTemp;
					
					//convert(d4,DECIMAL )  as od4
					strOrderFields +=",convert(d"+strOrderTemp+",DECIMAL ) as od" +strOrderTemp+",d"+strOrderTemp+" as ood" +strOrderTemp;	
					
				}
				//去掉开头的，
				strOrderBy=" order by "+ strOrderBy.substring(1);
			}

			
			/**
			 * 处理第一个合并区域SQL的代码段
			 * 支持的参数包括：
			 * ManuName：底稿名称，不提供就通过TASKID来定位；
			 *                    或通过TaskCode参数来定位；
			 */
			String strManuName = request.getParameter("ManuName");
			
			String strResult = "";
			String strTaskCode = request.getParameter("TaskCode"); 
			if (strTaskCode == null || "".equals(strTaskCode)) {
				//如果没有提供taskcode,则是合并报表，则通过taskname动态定位
				if (strManuName == null || strManuName.length() == 0) {
					result = reportservice.getTaskIdList(projectid,
							taskid);
				}else{
					result = reportservice.getTaskIdList(projectid,
							taskid,strManuName+".xls");
				}
				
				
				if (result.size()>0){
					for (int i = 0; i < result.size(); i++) {
						strResult += result.get(i) + ",";
					}
						//System.out.println("qwh:strResult下级=" + strResult);
				}else{
					//无法通过下级定位，说明是同级，也就是同一个单位里面的汇总表刷明细表录入结果
					strResult=reportservice.getTaskId(projectid, taskid, "明细表", strManuName+".xls") +",";
						//System.out.println("qwh:strResult同级=" + strResult);				
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
				//System.out.println("qwh:strFilterBy=" + strFilterBy);		
			}
			
			//显示的列和分组的列
			String AreaName = request.getParameter("AreaName");
			String strGroupBy = request.getParameter("GroupBy");
			String strFields="",strJoinSelect="";
			int iGroupBy=0;
			boolean bFound=false;
			String[] strGroups=null;
			if (strGroupBy==null||"".equals(strGroupBy)){
				
				iGroupBy=0;
				
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
				
			}else{
				
				iGroupBy=1;
				
				//有GROUPBY
				strGroups = strGroupBy.split("_");
				
				bFound=false;
				
				//拼装GROUP BY
				for (int i=0;i<strGroups.length;i++){
					if (strColNo.equals(strGroups[i])){
						bFound=true;
					}
					strFields+="d"+strGroups[i]+",";
					
					strJoinSelect+="a.d"+strGroups[i]+",";
					
				}
				strFields=strFields.substring(0,strFields.length()-1);
				strJoinSelect=strJoinSelect.substring(0,strJoinSelect.length()-1);
				
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
					
				}else{
					//没有找到，说明是SUM列
					strSql = "select " + strFields + ",sum(replace(replace(d" + strColNo + ",',',''),'￥','')) as d" + strColNo +strOrderFieldsBySum+ " from z_manudata \n"
						+"where projectid="+projectid+" \n";
					
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
				}
				
				strSql+= "group by " + strFields +"\n";
				
			}
			
			
			
			/**
			 * 处理第二个合并区域SQL的代码段
			 * 支持的参数包括：
			 * MinusManuName：做减法的底稿区域名称，不提供就通过等同于ManuName；
			 */
			
			String strMinusManuName = request.getParameter("MinusManuName");
			
			String strMinusResult = "";
			
			//如果没有提供taskcode,则是合并报表，则通过taskname动态定位
			if (strMinusManuName == null || strMinusManuName.length() == 0) {
				strMinusResult = strResult;
			}else{
				result = reportservice.getTaskIdList(projectid,taskid,strMinusManuName+".xls");
				
				if (result.size()>0){
					for (int i = 0; i < result.size(); i++) {
						strMinusResult += result.get(i) + ",";
					}
					//System.out.println("qwh:strResult下级=" + strResult);
				}else{
					//无法通过下级定位，说明是同级，也就是同一个单位里面的汇总表刷明细表录入结果
					strMinusResult=reportservice.getTaskId(projectid, taskid, "明细表", strMinusManuName+".xls") +",";
					//System.out.println("qwh:strResult同级=" + strResult);				
				}
					
			}
				
			// 获取哪一列
			String strMinusColNo = request.getParameter("MinusColNo");
			if (strMinusColNo == null) {
				strMinusColNo=strColNo;
				
			}
			int iMinusColNo = 0;
			try {
				iMinusColNo = Integer.parseInt(strMinusColNo);
			} catch (Exception e) {
			}
			if (iMinusColNo < 1 || iMinusColNo > 30) {
				throw new Exception("MinusColNo[" + iMinusColNo + "]错误,超过1到30范围");
			}

			//条件
			String strMinusFilterBy= request.getParameter("MinusFilterBy");
			if (strMinusFilterBy!=null && !"".equals(strMinusFilterBy)){
				strMinusFilterBy=" and " + strMinusFilterBy.replaceAll("~", "'") +  " ";
				//System.out.println("qwh:strMinusFilterBy=" + strMinusFilterBy);		
			}
			
			//显示的列和分组的列
			String MinusAreaName = request.getParameter("MinusAreaName");
			if (MinusAreaName==null||"".equals(MinusAreaName)){
				MinusAreaName=AreaName;
			}
			
			String strMinusGroupBy = request.getParameter("MinusGroupBy");
			if (strMinusGroupBy==null||"".equals(strMinusGroupBy)){
				strMinusGroupBy=strGroupBy;
			}
			String strMinusFields="",strMinusSql,strMinusJoinSelect="",strJoinOn="";
			int iMinusGroupBy=0;
			if (strMinusGroupBy==null||"".equals(strMinusGroupBy)){
				
				iMinusGroupBy=0;
				
				//没有GROUPBY
				strMinusSql = "select d"
					+ iMinusColNo
					+ ",autoid "+strOrderFields+" \n from z_manudata where projectid="+projectid+" \n";
								

				strMinusSql+=" and taskid in ("+ strMinusResult + "-1) \n";

				if (MinusAreaName != null && !"".equals(MinusAreaName)) {
					strMinusSql += " and AreaName='" + MinusAreaName + "' ";
				}
				
				if (strMinusFilterBy != null && !"".equals(strMinusFilterBy)) {
					strMinusSql += strMinusFilterBy;
				}
				
			}else{
				
				iMinusGroupBy=1;
				
				//有GROUPBY
				String[] strMinusGroups = strMinusGroupBy.split("_");
				
				bFound=false;
				
				//拼装GROUP BY
				for (int i=0;i<strMinusGroups.length;i++){
					if (strMinusColNo.equals(strMinusGroups[i])){
						bFound=true;
					}
					strMinusFields+="d"+strMinusGroups[i]+",";
					
					strMinusJoinSelect+="b.d"+strMinusGroups[i]+",";
					
					strJoinOn+="a.d"+strGroups[i]+"=b.d"+strMinusGroups[i]+" and ";
					
				}
				strMinusFields=strMinusFields.substring(0,strMinusFields.length()-1);
				strMinusJoinSelect=strMinusJoinSelect.substring(0,strMinusJoinSelect.length()-1);
				
				if (bFound){
					//找到了，
					strMinusSql = "select " + strMinusFields + " \n from z_manudata \n"
						+"where projectid="+projectid+" ";
					
					strMinusSql+=" and taskid in ("+ strMinusResult + "-1) \n";
				}else{
					//没有找到，说明是SUM列
					strMinusSql = "select " + strMinusFields + ",sum(replace(replace(d" + strMinusColNo + ",',',''),'￥','')) as d" + strMinusColNo +strOrderFieldsBySum+ " from z_manudata \n"
						+"where projectid="+projectid+" \n";

					strMinusSql+=" and taskid in ("+ strMinusResult + "-1) \n";
					
				}
				
				if (MinusAreaName != null && !"".equals(MinusAreaName)) {
					strMinusSql += " and AreaName='" + MinusAreaName + "' \n";
				}
				
				if (strMinusFilterBy != null && !"".equals(strMinusFilterBy)) {
					strMinusSql += strMinusFilterBy;
				}
				
				strMinusSql+= "group by " + strMinusFields +"\n";
				
			}
			
			
			//装配合并列
			if ( iGroupBy+iMinusGroupBy==0){
				//都没有GROUPBY；
				throw new Exception("暂不支持，请联系wnnerQ!");
			}else if (iGroupBy+iMinusGroupBy == 2){
				//都有GROUPBY
				
				if (bFound){
					
					//是合并项；
					
					strSql=strSql + " union \n " + strMinusSql;
					
				}else{
					
					/**
					 * 最终的装配，类似下面的SQL：
					 * 
					 
					select a.d1,a.yz - ifnull(b.yz,0)
					from
						(
						select 'a公司'  as d1, 1000 as yz
						union
						select 'b公司'  as d1, 2000 as yz
						)
					a left join 
						(
						select 'a公司'  as d1, 600 as yz
						union
						select 'c公司'  as d1, 2000 as yz
						)b
					on a.d1=b.d1
						
						union
						
						
					select b.d1,ifnull(a.yz,0) - b.yz
						from
						(
						select 'a公司'  as d1, 1000 as yz
						union
						select 'b公司'  as d1, 2000 as yz
						)
					a right join 
						(
						select 'a公司'  as d1, 600 as yz
						union
						select 'c公司'  as d1, 2000 as yz
						)b
					on a.d1=b.d1
						
					order by 1
					  
					 * 
					 */
					
					
					//"select " + strMinusFields + ",sum(replace(replace(d" + strMinusColNo + ",',',''),'￥','')) as d" + strMinusColNo +strOrderFieldsBySum+ 
					
					strSql="select "+strJoinSelect+",a.d" + strColNo +" - ifnull(b.d" + strMinusColNo+",0) as d" + strColNo + " \n"
						+"from  \n"
						+"( \n"
							+strSql
						+") \n"
						+"a left join \n" 
						+"( \n"
							+strMinusSql
						+")b \n"
						+"on "+strJoinOn+" 1=1 \n"
						
						+"union \n"
						
						+"select "+strMinusJoinSelect+",ifnull(a.d" + strColNo +",0) - b.d" + strMinusColNo +"  \n"
						+"from \n"
						+"( \n"
							+strSql
						+") \n"
						+"a right join \n" 
						+"( \n"
							+strMinusSql
						+")b \n"
						+"on "+strJoinOn+" 1=1 \n";
				}
			}else{
				throw new Exception("GroupBy和MinusGroupBy必须同时提供或同时不提供！");
			}
			
			
//			抵销分录条件
			String strEliminateFilterBy= request.getParameter("EliminateFilterBy");
			if (strEliminateFilterBy!=null && !"".equals(strEliminateFilterBy)){
				strEliminateFilterBy=" and " + strEliminateFilterBy.replaceAll("~", "'") +  " ";
			}
			
			//抵销分录发生额汇总方式
			String strEliminateOcc= request.getParameter("EliminateOcc"),strEliminateFilterBy1="";
			if ("借发生".equals(strEliminateOcc)){
				//至汇总本年
				strEliminateOcc=",sum(if(direction=1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("贷发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(if(direction=-1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1="";
			}else if ("本期发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '本年'";
			}else if ("期初发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear <> '本年'";
			}else if ("期初借发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(if(direction=1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear <> '本年'";
			}else if ("期初贷发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(if(direction=-1,occurvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear <> '本年'";
			}else if ("1年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '1年前'";
			}else if ("2年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '2年前'";
			}else if ("3年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '3年前'";
			}else if ("4年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '4年前'";
			}else if ("5年前期初发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * occurvalue) as occ";
				strEliminateFilterBy1=" and accountyear = '5年前'";
			}
			//原币
			else if ("原币借发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(if(direction=1,currvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("原币贷发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(if(direction=-1,currvalue,0)) as occ";
				strEliminateFilterBy1=" and accountyear='本年'";
			}else if ("原币发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * currvalue) as occ";
				strEliminateFilterBy1=" ";
			}else if ("原币期初发生".equals(strEliminateOcc)){
				strEliminateOcc=",sum(direction * currvalue) as occ";
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
				 
				 */
				
				strSql="select a.*,a.d"+iColNo+" + "+strEliminateDirection+" * ifnull(b.occ,0) as sdd"+iColNo+" from \n"
						+"( \n"
							+ strSql
						+")a left join ( \n"
						+"select areavalue" + strEliminateOcc+ "\n"
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
			}
			
			
			//====最后指定排序
			
			if (strOrderBy!=null && !"".equals(strOrderBy)){
				strSql += strOrderBy;
			}else{
				//如果没有提供order by 参数，就按照
				strSql+=" \n order by " + strFields;
			}
			
			System.out.println("\nqwh:5002:strsql=" + strSql);
			
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
