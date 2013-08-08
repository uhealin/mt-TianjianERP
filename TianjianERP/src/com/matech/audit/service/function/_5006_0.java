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
 * 5006公式，专门刷新出全表保存的块使用；
 * 
 * 
 * 
 * 公式修改说明
 * 
 * 1.on 20080322:支持参数：
 * 
 * TaskCode: 被引用底稿的底稿编号；
 * ManuName: 被引用底稿的名字；
 *      如果没有提供TaskCode，就按照ManuName来定位，如果连ManuName也没有提供，就按照和当前刷新底稿的名字相同的底稿的方式来寻找；
 * 
 * SheetName：缓存被引用的底稿表页的名字；如果被缓存的底稿只有一个表页，则这个参数可以不填；
 * ColNo：引用最终块的第ColNo列；
 * NodeName:底稿所在的节点，不提供则缺省是“明细表”
 * TokenColNo:标志性单元格所在的列号；
 * 
 * StartRow:从第几行起；
 * StartToken: 在没有提供StartRow的情况下，则根据最先出现的标记性文字定位；请注意，这里取的是StartToken文字的下一行；
 * StartTokenRepeat: 缺省为1，就是有的时候，一张底稿的一列里面Token出现多次，这里可以通过StartTokenRepeat来指定是从第StartTokenRepeat（比如说2）个StartToken起；
 * 
 * EndRow:从第几行起；
 * EndToken: 在没有提供EndRow的情况下，则根据最先出现的标记性文字定位；请注意，这里取的是EndToken文字的上一行；
 * EndTokenRepeat: 缺省为1，就是有的时候，一张底稿的一列里面Token出现多次，这里可以通过EndTokenRepeat来指定是从第EndTokenRepeat（比如说2）个EndToken起；
 * MergeRow：附加行
 * ConstValue:常量行
 * 
 * 
 * 调用说明：
 * =取列公式插入(5006, "", "d1", "&ColNo=1&StartRow=7&SheetName=生产性生物资产附注表&ManuName=生产性生物资产附注表&NodeName=数据附注")
 * 
 * 
 * @author winnerQ
 *
 */
public class _5006_0 extends AbstractAreaFunction {

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
			
			
			String NodeName = request.getParameter("NodeName");
			if (NodeName == null || "".equals(NodeName)) {
				NodeName="明细表";
			}
			
			//显示的列和分组的列
			String SheetName = request.getParameter("SheetName");
			
			
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
			
			
			String TokenColNo = request.getParameter("TokenColNo");
			if (TokenColNo == null) {
				throw new Exception("TokenColNo未指定");
			}
			try {
				iColNo = Integer.parseInt(TokenColNo);
			} catch (Exception e) {
			}
			if (iColNo < 1 || iColNo > 30) {
				throw new Exception("TokenColNo[" + TokenColNo + "]错误,超过1到30范围");
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
			
			String StartRow = request.getParameter("StartRow");
			String StartToken = request.getParameter("StartToken");
			String StartTokenRepeat = request.getParameter("StartTokenRepeat");
			int iStartTokenRepeat=0;
			
			if (StartRow==null || "".equals(StartRow)){
				//没有提供起始行参数，就检查有没有提供
				
				if (StartToken != null && !"".equals(StartToken)) {		
				
					strSql="select areaname from z_manudata \n"
							+"where projectid="+projectid+" \n";
					
					if (strTaskCode != null && !"".equals(strTaskCode)) {
						strSql+=" and taskcode='" + strTaskCode + "'\n";
					}else{
						strSql+=" and taskid in ("+ strResult + "-1) \n";
					}
				
					if (SheetName != null && !"".equals(SheetName)) {
						strSql+="and sheetname='"+SheetName+"' \n";
					}
					//兼容，和空格：and trim(replace(replace(d1,',',''),'，',''))= trim(' 母公司不是本企业最终控制方的说明最终控制方的名称。' )
					//strSql+="and d"+TokenColNo+"='"+StartToken+"' \n"
					strSql+="and trim(replace(replace(d"+TokenColNo+",',',''),'，',''))= trim('"+StartToken+"' ) \n"
							+"order by autoid \n";
					
					org.util.Debug.prtOut("qwh:5006:确定开始="+strSql);
					
					ps = conn.prepareStatement(strSql);
					rs = ps.executeQuery();
					
					//如果有提供iStartTokenRepeat参数，就变为整数
					if (StartTokenRepeat != null && !"".equals(StartTokenRepeat)) {
						try {
							iStartTokenRepeat = Integer.parseInt(StartTokenRepeat);
						} catch (Exception e) {
						}
					} else {
						//没有提供就设置为1
						iStartTokenRepeat=1;
					}
					int i=1,iHasData=0;
					for ( ; rs.next();i++){
						iHasData++;
						if (i==iStartTokenRepeat){
							StartRow=rs.getString(1);
							break;
						}
					}
					rs.close();
					ps.close();
					
					if (i!=iStartTokenRepeat && iHasData>0){
						throw new Exception("您指定了开始TOKEN["+StartToken+"]要重复["+iStartTokenRepeat+"]次，但实际数据只重复了["+i+"]次，无法定位块，刷新失败！");
					}
					
					
					if (StartRow==null){
						StartRow="0";
					}
					
					
				}else{
					StartRow="0";
				}
						
			}
			
			//再取得结束的
			String EndRow = request.getParameter("EndRow");
			String EndToken = request.getParameter("EndToken");
			String EndTokenRepeat = request.getParameter("EndTokenRepeat");
			int iEndTokenRepeat=0;
			
			if (EndRow==null || "".equals(EndRow)){
				//没有提供起始行参数，就检查有没有提供
				
				if (EndToken != null && !"".equals(EndToken)) {		
				
					strSql="select areaname from z_manudata \n"
							+"where projectid="+projectid+" \n";
					
					if (strTaskCode != null && !"".equals(strTaskCode)) {
						strSql+=" and taskcode='" + strTaskCode + "'\n";
					}else{
						strSql+=" and taskid in ("+ strResult + "-1) \n";
					}
				
					if (SheetName != null && !"".equals(SheetName)) {
						strSql+="and sheetname='"+SheetName+"' \n";
					}
					
					//strSql+="and d"+TokenColNo+"='"+EndToken+"' \n"
					strSql+="and trim(replace(replace(d"+TokenColNo+",',',''),'，',''))= trim('"+EndToken+"' ) \n"
							+"order by autoid \n";
					
					org.util.Debug.prtOut("qwh:5006:确定结束="+strSql);
					
					ps = conn.prepareStatement(strSql);
					rs = ps.executeQuery();
					
					//如果有提供iEndTokenRepeat参数，就变为整数
					if (EndTokenRepeat != null && !"".equals(EndTokenRepeat)) {
						try {
							iEndTokenRepeat = Integer.parseInt(EndTokenRepeat);
						} catch (Exception e) {
						}
					} else {
						//没有提供就设置为1
						iEndTokenRepeat=1;
					}
					int i=1,iHasData=0;
					for ( ; rs.next();i++){
						iHasData++;
						if (i==iEndTokenRepeat){
							EndRow=rs.getString(1);
							break;
						}
					}
					rs.close();
					ps.close();
					
					if (i!=iEndTokenRepeat && iHasData>0){
						throw new Exception("您指定了结束TOKEN["+EndToken+"]要重复["+iEndTokenRepeat+"]次，但实际数据只重复了["+i+"]次，无法定位块，刷新失败！");
					}
					
					if (EndRow==null){
						EndRow="65535";
					}
					
				}else{
					EndRow="65535";
				}
						
			}
			
			String MergeRow = request.getParameter("MergeRow");
			if (MergeRow == null || "".equals(MergeRow)) {
				MergeRow="0";
			}
			
			String ConstValue = request.getParameter("ConstValue");
			if (ConstValue == null || "".equals(ConstValue)) {
				ConstValue="0";
			}
			
			
			//条件
			String strFilterBy= request.getParameter("FilterBy");
			if (strFilterBy!=null && !"".equals(strFilterBy)){
				strFilterBy=" and " + strFilterBy.replaceAll("~", "'") +  " ";
			}
			
			
			//判断中间端是字符串,还是数字或NULL；是字符，就用group_concat, 是数字，就用sum；
			strSql="select d"+strColNo+" from z_manudata \n"
				+"where projectid="+projectid+" \n";
			
			if (strTaskCode != null && !"".equals(strTaskCode)) {
				strSql+=" and taskcode='" + strTaskCode + "'\n";
			}else{
				strSql+=" and taskid in ("+ strResult + "-1) \n";
			}
			if (SheetName != null && !"".equals(SheetName)) {
				strSql+="and sheetname='"+SheetName+"' \n";
			}
			strSql+="and convert(areaname,DECIMAL )> ("+StartRow+"+"+MergeRow+")  and convert(areaname,DECIMAL )<"+EndRow+" \n";
			
			//strSql +=" and length(abs(d"+strColNo+"))!= length(d"+strColNo+") limit 1";
			strSql +=" and d"+strColNo+">'' and NOT (replace(replace(d"+strColNo+",',',''),'￥','')  REGEXP '^(-?.?[0-9]+)(.[0-9]+)?$') limit 1";
			org.util.Debug.prtOut("qwh:5006:判断是否是：resultSql="+strSql);
			
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();
			String  strField="";
			if (rs.next()){
				org.util.Debug.prtOut("qwh:5006:有记录，说明是字符串而不是数字或全NULL；" );
				//有记录，说明是字符串而不是数字或全NULL；
				strField="trim(d"+strColNo+")";
			}else{
				org.util.Debug.prtOut("qwh:5006://数字或者全NULL");
				//数字或者全NULL；
				strField="if(trim(d"+strColNo+")='',0,d"+strColNo+")";
			}
			rs.close();
			
			
			//最终的SQL
			strSql="select areaname,"+strField+" as d"+strColNo+",autoid,if(d"+strColNo+">'','"+ConstValue+"','') as ConstValue from z_manudata \n"
				+"where projectid="+projectid+" \n";
			if (strTaskCode != null && !"".equals(strTaskCode)) {
				strSql+=" and taskcode='" + strTaskCode + "'\n";
			}else{
				strSql+=" and taskid in ("+ strResult + "-1) \n";
			}
			if (SheetName != null && !"".equals(SheetName)) {
				strSql+="and sheetname='"+SheetName+"' \n";
			}
			if (strFilterBy != null && !"".equals(strFilterBy)) {
				strSql += strFilterBy;
			}
			
			strSql+="and convert(areaname,DECIMAL )> ("+StartRow+"+"+MergeRow+")  and convert(areaname,DECIMAL )<"+EndRow+" \n"
					+"order by autoid";
			
			
			//增加LIMIT条件
			String strLimit = request.getParameter("Limit");
			if (strLimit!=null && !"".equals(strLimit)){
					strSql += " limit " +strLimit;
			}
			
			
			org.util.Debug.prtOut("qwh:5006:resultSql="+strSql);
			

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
