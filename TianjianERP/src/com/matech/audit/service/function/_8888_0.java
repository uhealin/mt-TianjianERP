package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.inventory.InventoryService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 一个公式，可以按照3种方式来刷新：
 * 1、	对于刷存货余额表；（有存货刷存货余额账、没有刷数量科目/辅助核算余额表）；
 * 2、	对于刷存货明细（有存货刷存货明细账、没有刷数量明细账）；
 * 3、	2个刷到一起（刷存货余额表 left join 刷存货明细；）
 * 
 * 存货余额表需要提供：
 *	存货名称、品种、计量单位、
 *	期末（数量、期末单价、期末余额）
 *	本期增加(数量、单价、金  额)
 *	本期发出(数量、单价、金  额)
 *	期初(数量、单价、金  额)
 *	月份
 *
 *条件：
 *1.指定科目名称
 *  	刷出这个对应的所有的....
 * 	先翻译所有这个科目及其下级科目，先看有无存货数据，有就去存货分录表里面找存货分类ID；没有就是这个科目；
 *
 *2.指定 分月=支持  ，不提供的话， 缺省就是全年；
 *	分月的话，要刷出，要支持跨年项目；
 *	期初
 *3.指定存货名称；兼容名称重复（sum）；
 *  存货名称，要兼容有存货模块和无存货模块的情况；
 *  程序的处理逻辑是：优先找有无存货模块；有存货模块，就去存货分类表里面按照名称＝定位，但是名称是有可能重复的，所以要groupby +sum；
 *  对于没有存货模块的，先按名字去数量辅助核算余额表里面找，没有，就去数量科目余额表；
 *
 *  对于刷存货明细（有存货刷存货明细账、没有刷数量明细账）；
 *
 * 存货明细需要提供：
 * 出入库单日期、出入库单编号、凭证号码（凭证字+凭证号）、品名、实际数量、实际单价	实际金额、发票、往来单位（需要盛坤从采集那里补充提高）、对应凭证字、对应凭证号、对应凭证号码、对应凭证日期、出入库单编制人、审核人、记账人
 *
 *条件：
 *1.指定存货名称；兼容名称重复（sum）；
 *  存货名称，要兼容有存货模块和无存货模块的情况；
 *  程序的处理逻辑是：优先找有无存货模块；有存货模块，就去存货分类表里面按照名称＝定位，但是名称是有可能重复的，所以要groupby +sum；
 *  对于没有存货模块的，先按名字去数量辅助核算余额表里面找，没有，就去数量科目余额表；
 *2.倒推月份；
 *  倒推月份＝-1，表示从项目结束区间日期往前找1个月；
 *  倒推月份＝-2，表示从项目结束区间日期往前找2个月；
 *  倒推月份＝-24，表示从项目结束区间日期往前找24个月；
 *  没有提供倒推月份这个参数就缺省是项目起始区间；
 *
 *3.正推月份；
 *  倒推月份＝1，表示从项目结束区间日期往后找1个月；
 *  倒推月份＝2，表示从项目结束区间日期往后找2个月；
 *  倒推月份＝24，表示从项目结束区间日期往后找24个月；
 *
 *4.出入库单据类型
 *  缺省是全部；提供可选：出库单、入库单；
 *
 *5.必填的一个条件，刷新方式
 *  可选：存货余额、存货明细、存货余额加存货明细

 * 
 * @author yzm
 *
 */

public class _8888_0 extends AbstractAreaFunction {
	
	
	/**
	 * 参数：
	 * 	科目名称：存货对应的科目
	 *	刷新内容:存货余额(显示存货余额表/科目、核算余额表);存货明细(显示存货明细表或科目、核算明细表);
	 *  存货规格：求同规格的存货	注：只在有存货模块时有效	
	 *  
	 * 	年份：0为本年（默认）,-1为上年...	注：只在【刷新内容=存货余额】时有效
	 * 	月份:1-12月		注：只在【刷新内容=存货余额】时有效
	 * 	比较年份:-1表示包括上年存货	注：只在【刷新内容=存货余额】时有效
	 * 
	 *  核对方式：0为明细账与存货核对,1为存货与明细账核对,注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 	出入库类别：1为入库,-1为出库;		注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 	开始日期：用于标志明细的开始区间,为空【开始日期=项目的开始日期】; 注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 	结束日期：用于标志明细的结束区间,为空【结束日期=项目的结束日期】; 注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 	截止日期：用于标志明细的截止日期; 注：只在有【存货模块】并且【刷新内容=存货明细】和【截止类型】时有效
	 * 	截止类型：用于标志截止日期的显示类型,【截止类型=0】表示截止日期之前,【截止类型=1】表示截止日期之后 ; 注：只在有【存货模块】并且【刷新内容=存货明细】时有效	
	 * 	存货名称:求同名的存货	注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 
	 *	刷新内容:存货余额(显示存货余额表/科目、核算余额表);存货明细(显示存货明细表或科目、核算明细表);
	 * 	出入库类别：1为入库,-1为出库;		注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 	存货规格：求同规格的存货	注：只在有存货模块时有效	
	 * 	
	 * 	刷余额表合计:是(按类型来全年合计，否则按类型来分月合计); 	注：只在【刷新内容=存货余额】时有效 (废掉)
	 * 	截止性抽凭的凭证日期:从来没有过，暂时废掉	注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 	倒推的月份:从来没有过，暂时废掉	注：只在有【存货模块】并且【刷新内容=存货明细】时有效
	 * 
	 * 返回值：
	 * 	1、存货余额：(有存货刷存货，没有就刷科目或核算)
	 * 		Inventoryid(存货编号),InventoryName(存货名称),
	 * 		InventoryType(品种),uomunit(计量单位),
	 * 		DebitOccQ(本期增加数量),DebitOccPrice(本期增加单价),DebitOccF(本期增加金额),
	 * 		CreditOccQ(本期发出数量),CreditOccPrice(本期发出单价),CreditOccF(本期发出金额),
	 * 		BalanceQ(期末数量),BalancePrice(期末单价),BalanceF(期末余额),
	 * 		RemainQ(期初数量),RemainPrice(期初单价),RemainF(期初金额),
	 * 		debitsov(调整借),creditsov(调整贷),
	 * 		InventoryNameType(品名=存货名称+存货类型),Inventorytype1(存货类别),endoccurvalue(审定数)
	 * 		InventoryFullName,
	 *  
	 * 	2、存货明细：(有存货刷存货，没有就刷科目或核算)
	 * 
	 * 		InventoryDate(出入库单日期),InventoryEntryId(出入库单编号),
	 * 		oldVoucherID1(凭证号码=凭证字+凭证号),InventoryNameType(品名=存货名称+存货类型),uomunit(计量单位)	,
	 * 		VendorName(往来单位),VchDate(对应凭证日期),
	 * 		FillUser(编制人),AuditUser(记账人),KeepUser(审核人),
	 * 		InventoryQuantity(存货数量),InventoryPrices(存货单价),InventoryOccurValue(存货金额),
	 * 		Quantity(凭证数量),unitprice(凭证单价),OccurValue(凭证金额)
	 * 		summary(摘要)
	 * 
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
			
			this.tempTable = "tt_"+DELUnid.getCharUnid();
			
			String acc = CHF.showNull((String) args.get("curAccPackageID"));
	        String projectid = CHF.showNull((String) args.get("curProjectid"));   
			String SubjectName = CHF.showNull((String)args.get("科目名称"));		//科目名称
			
			String InventoryName = CHF.showNull((String)args.get("存货名称"));  //存货名称(废掉)
			String InventoryType = CHF.showNull((String)args.get("存货规格"));  //存货规格
			
			String type1 = CHF.showNull((String)args.get("刷新内容"));  //刷新类型
			String InventoryInOutType = CHF.showNull((String)args.get("出入库类别"));  //出入库类别
			String sum1 = CHF.showNull((String)args.get("刷余额表合计"));  //刷余额表合计(废掉)
			
			String allYear = CHF.showNull((String)args.get("比较年份"));	//比较年份=-1
			String year = CHF.showNull((String)args.get("年份"));  //刷新年份
			String month = CHF.showNull((String)args.get("月份"));  //刷新年份
			
			String backMonth = CHF.showNull((String)args.get("倒推的月份"));  //倒推的月份(废掉)
			String sampleFlow = CHF.showNull((String)args.get("截止性抽凭的凭证日期"));  //截止性抽凭的凭证日期(废掉)
			args.put("ProjectID",projectid);
			
			if (SubjectName==null || SubjectName.equals("")){
                String manuid=(String)args.get("manuid");
                if (manuid==null || manuid.equals("")){
                    SubjectName=getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));
                }else{
                    //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                    SubjectName = getTaskSubjectNameByManuID(conn, manuid);
                }
                
            }

            String sName1 = changeSubjectName(conn,projectid,SubjectName);
            if(!"".equals(sName1)){
            	SubjectName = sName1; 
            }            
            args.put("SubjectName",SubjectName);

            ProjectService projectService = new ProjectService(conn);
			Project project = projectService.getProjectById(projectid);
			args.put("project", project);
			
            String customerId = project.getCustomerId();
            
            int[] result1 = getProjectAuditAreaByProjectid(conn, projectid);
            String  strStartYearMonth = String.valueOf(result1[0] * 12 + result1[1]);
            String strEndYearMonth = String.valueOf(result1[2] * 12 + result1[3]);
          
            if("".equals(allYear)||allYear==null){
				allYear="0";
			}
			strStartYearMonth = String.valueOf((Integer.parseInt(strStartYearMonth)+Integer.parseInt(allYear)*12));
			
            args.put("StartYearMonth", strStartYearMonth);
            args.put("EndYearMonth", strEndYearMonth);
			
            InventoryService inventoryService = new InventoryService(conn);
            //得到项目有存货的所有的帐套编号（存货）
//            ArrayList arrayList = inventoryService.isInventory(customerId, String.valueOf((result1[0] + Integer.parseInt(allYear))), String.valueOf(result1[2]));
			
            String strSelectSql = getRuleSQL( conn, args);
            if(!"".equals(InventoryType)){//存货规格不为空
            	strSelectSql = "select * from ("+strSelectSql+") a where 1=1 ${InventoryType} ";
				args.put("InventoryType"," and InventoryType = '"+InventoryType+"' " );
			}
        	strSelectSql = this.setSqlArguments(strSelectSql, args);
        	System.out.println("GET SQL:" + strSelectSql);
        	
        	
        	
        	
            /**
			 * 求出8888公式的范围
			 */
            if(type1.equals("存货余额")){
            	//求余额,对应存货余额表
            	sql = "create table " + tempTable + " " + strSelectSql;
    			st.execute(sql);
            	
    			sql = "alter table " + tempTable + 
    			" add column DebitOccQ decimal (15,2) DEFAULT '0.00', " + //(本期增加数量)
    			" add column DebitOccPrice decimal (15,2) DEFAULT '0.00', " + //(本期增加单价)
    			" add column DebitOccF decimal (15,2) DEFAULT '0.00', " + //(本期增加金额)
    			
    			" add column CreditOccQ decimal (15,2) DEFAULT '0.00', " + //(本期发出数量)
    			" add column CreditOccPrice decimal (15,2) DEFAULT '0.00', " + //(本期发出单价)
    			" add column CreditOccF decimal (15,2) DEFAULT '0.00', " + //(本期发出金额)
    			
    			" add column BalanceQ decimal (15,2) DEFAULT '0.00', " + //(期末数量)
    			" add column BalancePrice decimal (15,2) DEFAULT '0.00', " + //(期末单价)
    			" add column BalanceF decimal (15,2) DEFAULT '0.00', " + //(期末余额)
    			
    			" add column RemainQ decimal (15,2) DEFAULT '0.00', " + //(期初数量)
    			" add column RemainPrice decimal (15,2) DEFAULT '0.00', " + //(期初单价)
    			" add column RemainF decimal (15,2) DEFAULT '0.00', " + //(期初金额)
    			
    			" add column debitsov decimal (15,2) DEFAULT '0.00', " + //(调整借)
    			" add column creditsov decimal (15,2) DEFAULT '0.00', " + //(调整贷)
    			" add column endoccurvalue decimal (15,2) DEFAULT '0.00' " ;//(审定数)
    			st.execute(sql);
    			
    			sql = "alter table " + tempTable + " add index accpackageid (accpackageid)";
    			st.addBatch(sql);
    			sql = "alter table " + tempTable + " add index subjectid (subjectid)";
    			st.addBatch(sql);
    			sql = "alter table " + tempTable + " add index Inventoryid (Inventoryid)";
    			st.addBatch(sql);
    			sql = "alter table " + tempTable + " add index InventoryType (InventoryType)";
    			st.addBatch(sql);
    			st.executeBatch();
    			
    			setProjectValue ( conn, args);
    			
    			//取数SQL
    			sql = "select subjectid,Inventoryid,InventoryName,InventoryFullName,InventoryType,uomunit, " +
    			"	concat(a.InventoryName,' ',a.InventoryType) as InventoryNameType," +
    			"	SUBSTR(a.InventoryFullName,1,IF(LOCATE('/',a.InventoryFullName) = 0,LENGTH(a.InventoryFullName),LOCATE('/',a.InventoryFullName)-1)) AS Inventorytype1," +
    			"	sum(DebitOccQ) as DebitOccQ, DebitOccPrice, sum(DebitOccF) as  DebitOccF, " + 
    			" 	sum(CreditOccQ) as CreditOccQ, CreditOccPrice, sum(CreditOccF) as CreditOccF," +
    			" 	sum(BalanceQ) as BalanceQ, BalancePrice,sum(BalanceF) as BalanceF, " + 
    			" 	sum(RemainQ) as RemainQ, RemainPrice, sum(RemainF) as RemainF, " + 
    			" 	sum(debitsov) as debitsov, sum(creditsov) as creditsov,sum(endoccurvalue) as endoccurvalue " +
    			"	from " + this.tempTable + " a " +
    			"	group by a.subjectid,a.inventoryid,a.InventoryType" +
    			"	order by a.subjectid,a.inventoryid,a.InventoryType ";
    			
    			rs = st.executeQuery(sql);
    			
            }else{
            	//求明细,对应存货明细表
            	String table = tempTable + "1";
            	sql = "create table " + table + " " + strSelectSql;	//用于确定明细的存货范围
    			st.execute(sql);
            	
    			//加上索引
    			sql = " ALTER TABLE " + table + " ADD INDEX accpackageid (accpackageid), ADD INDEX subjectid (subjectid), ADD INDEX Inventoryid (Inventoryid)";
    			st.execute(sql); 
    			
    			setProjectValue ( conn, args, table);//明细
    			
    			sql = "DROP TABLE IF EXISTS " + table ; //删除临时表
    			st.execute(sql);
            	
    			sql = "select a.*," +
    			"	CONCAT(TypeID, ' ', oldVoucherID) AS oldVoucherID1," +
    			"	CONCAT(InventoryName,' ',InventoryType) AS InventoryNameType " +
    			"	from " + this.tempTable + " a " +
    			"	ORDER BY a.InventoryEntryId,a.VchDate,a.TypeID,ABS(a.oldvoucherid),a.InventoryDate,a.SubjectID,a.InventoryId ";
    			System.out.println(sql);
    			rs = st.executeQuery(sql);
            }
			
//            System.out.println("tempTable = " + tempTable);
//            this.tempTable = "";
            
			return rs;
		} catch (Exception e) {
			System.out.println("出错SQL："+sql);
			e.printStackTrace();
			DbUtil.close(st);
			throw e;
		}
	}
	
	
	//用于确定明细的存货范围
	public String getRuleSQL(Connection conn,Map args) throws Exception {
		String sql = "";
		ASFuntion CHF=new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sqlAssItem = "";
			String sqlStr = "select distinct a.ifequal,a.subjectid,a.asstotalname1  " +
			" from c_subjectassitem a,c_account b " +
			" where 1=1 " +
			" and b.subyearmonth*12+b.submonth >='${StartYearMonth}' " +
			" and b.subyearmonth*12+b.submonth <='${EndYearMonth}'  " +
			" and (b.subjectfullname2 = '${SubjectName}' or b.subjectfullname2 like '${SubjectName}/%') " +
			" and a.accpackageid =b.accpackageid " +
			" and a.subjectid = b.subjectid " ;
			sqlStr = this.setSqlArguments(sqlStr, args);
			System.out.println("辅助核算:"+sqlStr); 
			ps = conn.prepareStatement(sqlStr);
			rs = ps.executeQuery();
			while(rs.next()){
				String ifequal = rs.getString("ifequal");
				String accid = rs.getString("subjectid");
				String asstotalname1 = rs.getString("asstotalname1");
				
				if("0".equals(ifequal)){ //有核算披露
					sqlAssItem += "or (a.accid = '"+accid+"' and a.asstotalname1 like '"+asstotalname1+"/%' ) ";
				}
			
			}
			
			if(!"".equals(sqlAssItem)){
		    	sqlAssItem = " and ( " + sqlAssItem.substring(2)+ ") ";
		    }else{
		    	sqlAssItem = " and 1=2 ";
		    }
				
			DbUtil.close(rs);
			DbUtil.close(ps); 
				
			args.put("sqlAssItem", sqlAssItem); //核算过滤
			
			//存货GERSQL
			
			sql = "SELECT  \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,1,2),3),4),5) AS opt, \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,a.accpackageid,b.accpackageid),c.accpackageid),d.accpackageid),e.accpackageid) AS accpackageid, \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,a.subjectid,b.subjectid),c.accid),d.accid),e.subjectid) AS subjectid, \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,'',''),c.assitemid),d.assitemid),e.Inventoryid) AS Inventoryid, \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,a.accname,b.accname),c.assitemname),d.assitemname),e.InventoryName) AS InventoryName, \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,a.subjectfullname1,b.subjectfullname1),c.AssTotalName1),d.AssTotalName1),e.InventoryFullName) AS InventoryFullName, \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,'',b.DataName),''),d.DataName),e.InventoryType) AS InventoryType, \n" +
			"	IF(e.accpackageid IS NULL,IF(d.accpackageid IS NULL,IF(c.accpackageid IS NULL,IF(b.accpackageid IS NULL,'',b.DataName),''),d.DataName),e.uomunit) AS uomunit \n" +
			
			"	FROM ( \n" +
			"		SELECT DISTINCT \n" +
			"		a.accpackageid,a.subjectid,a.accname,a.subjectfullname1,a.DataName,a.subjectfullname2 \n" +
			"		FROM c_account a \n" +
			"		WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"		AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"		AND a.isleaf1 = 1 \n" +
			"		AND (a.subjectfullname2 LIKE '${SubjectName}/%'  OR a.subjectfullname2 = '${SubjectName}' ) \n" + 
			"	) a  \n" +
			
			"	LEFT JOIN ( \n" +
			"		SELECT DISTINCT \n" +
			"		a.accpackageid,a.subjectid,a.accname,a.subjectfullname1,a.DataName,a.subjectfullname2 \n" +
			"		FROM c_accountall a \n" +
			"		WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"		AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"		AND a.isleaf1 = 1 \n" +
			"		AND a.accsign = 2 \n" +
			"		AND (a.subjectfullname2 LIKE '${SubjectName}/%'  OR a.subjectfullname2 = '${SubjectName}' ) \n" + 
			"	) b ON a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid \n" +
			
			"	LEFT JOIN ( \n" +
			"		SELECT DISTINCT \n" +  
			"		a.accpackageid,a.AccID,a.AssItemID,a.AssItemName,a.AssTotalName1,a.DataName,b.subjectfullname1 \n" +
			"		FROM c_assitementryacc a,( \n" +
			"			SELECT DISTINCT accpackageid,subjectid,AccName,subjectfullname1  FROM c_account a \n" + 
			"			WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"			AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"			AND (a.subjectfullname2 LIKE '${SubjectName}/%'  OR a.subjectfullname2 = '${SubjectName}' ) \n" + 
			"		) b \n" +
			"		WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"		AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"		AND a.isleaf1 = 1 \n" +
			sqlAssItem + 
			"		AND a.accpackageid = b.accpackageid \n" +
			"		AND a.AccID = b.subjectid \n" +
			"	) c ON a.accpackageid = c.accpackageid AND a.subjectid = c.accid \n" +
			
			"	LEFT JOIN ( \n" +
			"		SELECT DISTINCT \n" +  
			"		a.accpackageid,a.AccID,a.AssItemID,a.AssItemName,a.AssTotalName1,a.DataName,b.subjectfullname1 \n" +
			"		FROM c_assitementryaccall a,( \n" +
			"			SELECT DISTINCT accpackageid,subjectid,AccName,subjectfullname1  FROM c_account a \n" + 
			"			WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"			AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"			AND (a.subjectfullname2 LIKE '${SubjectName}/%'  OR a.subjectfullname2 = '${SubjectName}' ) \n" + 
			"		) b \n" +
			"		WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"		AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"		AND a.isleaf1 = 1 \n" +
			"		AND a.accsign = 2 \n" +
			sqlAssItem + 
			"		AND a.accpackageid = b.accpackageid \n" +
			"		AND a.AccID = b.subjectid \n" +
			"	) d ON c.accpackageid = d.accpackageid AND c.accid = d.accid AND c.assitemid = d.assitemid \n" +
			
			"	LEFT JOIN ( \n" +
			"		SELECT DISTINCT \n" +
			"		a.AccPackageID,a.subjectid,	a.InventoryId,a.InventoryName,a.InventoryFullName,a.InventoryType,a.UomUnit \n" +
			"		FROM c_inventoryaccount a ,(                       \n" +
			"			SELECT DISTINCT accpackageid,subjectid,AccName,subjectfullname1  FROM c_account a \n" + 
			"			WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"			AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"			AND (a.subjectfullname2 LIKE '${SubjectName}/%'  OR a.subjectfullname2 = '${SubjectName}' ) \n" + 
			"		) b  \n" +
			"		WHERE SubYearMonth * 12 + SubMonth >= '${StartYearMonth}' \n" +
			"		AND SubYearMonth * 12 + SubMonth <= '${EndYearMonth}'  \n" +
			"		AND a.isleaf1 = 1 \n" +
			"		AND a.accpackageid = b.accpackageid \n" +
			"		AND a.subjectid = b.subjectid \n" +
			"	) e ON a.accpackageid = e.accpackageid AND a.subjectid = e.subjectid";
				
			
			return sql;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//求出取值的区间
	public void getMap(Map args) throws Exception {
		ASFuntion CHF=new ASFuntion();
		
		String Year = CHF.showNull((String)args.get("年份"));  //刷新年份 -1
		String Month = CHF.showNull((String)args.get("月份"));  //刷新年份
		Project project = (Project)args.get("project");
		
		String projectBeginYear = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
		String projectEndYear = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
		String projectBeginMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
		String projectEndMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);

		int StartYearMonth = 0 , EndYearMonth = 0 ; //取值的区间
		
		if(("".equals(Year) || "0".equals(Year)) ){	//本年
			if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){ //全年
				StartYearMonth = Integer.parseInt(projectBeginYear) * 12 + Integer.parseInt(projectBeginMonth);
				EndYearMonth = Integer.parseInt(projectEndYear) * 12 + Integer.parseInt(projectEndMonth); 
			}else{//分月
				int iYearMonthArea=Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth)-(Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth))+1;
				if (iYearMonthArea>12){
					throw new Exception("跨多年项目，不支持按指定月份取数");
				}
				
				int intStart = Integer.parseInt(projectBeginYear)*12+Integer.parseInt(Month); 
				int intEnd = Integer.parseInt(projectEndYear)*12+Integer.parseInt(Month); 
				
				int projectbegin = Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth);
				int projectend = Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth);
			
				//项目取数
				/*
				 * 
				 * 1.	指定月损益彭勇会采用比较特殊的算法，
				 * 因此虽然设定的还是1到12月，但是刷出来的数据，会是：      1到5月，是07年的；6到12月份，会是06年的，请特别注意！
				 * 
				 */
				if(projectbegin <= intStart && intStart <= projectend){
					StartYearMonth = intStart;
					EndYearMonth = intStart;
				}else if(projectbegin <= intEnd && intEnd <= projectend){
					StartYearMonth = intEnd;
					EndYearMonth = intEnd;
				}
			}
		}else{ //上年
			//没有提供，就是非IPO。也就是前面4种审计（ 年审、年审预审、外资年审、外资年审预审）的项目取上年同期数；
			StartYearMonth = Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth);
			EndYearMonth = Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth);

			int iYearMonthArea=Integer.parseInt(projectEndYear)*12+Integer.parseInt(projectEndMonth) - (Integer.parseInt(projectBeginYear)*12+Integer.parseInt(projectBeginMonth)) + 1;
			
			if("".equals(Month) || "0".equals(Month) || "00".equals(Month)){
				
				if (iYearMonthArea>=12){
					//正式审计 年审
					StartYearMonth += iYearMonthArea * Integer.parseInt(Year);
					EndYearMonth += iYearMonthArea * Integer.parseInt(Year);
				}else {
					//预审，不足12个月
					StartYearMonth = (Integer.parseInt(projectBeginYear)+Integer.parseInt(Year))*12+ Integer.parseInt(projectBeginMonth) ;
					EndYearMonth = StartYearMonth + 11;
				}
			}else{
				if (iYearMonthArea>12){
					throw new Exception("跨多年项目，不支持按指定月份取数");
				}
				
				int intStart = (Integer.parseInt(projectBeginYear) + Integer.parseInt(Year) )*12+Integer.parseInt(Month); 
				int intEnd = (Integer.parseInt(projectEndYear) + Integer.parseInt(Year) )*12+Integer.parseInt(Month); 
				
				StartYearMonth += iYearMonthArea * Integer.parseInt(Year);
				EndYearMonth += iYearMonthArea * Integer.parseInt(Year);
				
				//项目取数 
				/**
				 * 
				 * 1.	指定月损益彭勇会采用比较特殊的算法，
				 * 因此虽然设定的还是1到12月，但是刷出来的数据，会是：      1到5月，是07年的；6到12月份，会是06年的，请特别注意！
				 * 
				 */
				if(StartYearMonth <= intStart && intStart <= EndYearMonth){
					StartYearMonth = intStart;
					EndYearMonth = intStart;
				}else if(StartYearMonth <= intEnd && intEnd <= EndYearMonth){
					StartYearMonth = intEnd;
					EndYearMonth = intEnd;
				}
			}
		
		}
		
		args.put("StartYearMonth", String.valueOf(StartYearMonth));
		args.put("EndYearMonth", String.valueOf(EndYearMonth));
	}
	
	//只在【刷新内容=存货余额】时有效
	public void setProjectValue (Connection conn,Map args) throws Exception {
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			getMap( args); //时间区间
			
			ASFuntion CHF=new ASFuntion();
			
			String Year = CHF.showNull((String)args.get("年份"));  //刷新年份 -1
			Project project = (Project)args.get("project");
			String projectId = project.getProjectId();
			
			String projectEndYear = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String SubjectName = CHF.showNull((String)args.get("SubjectName"));	//科目名称
			
			int StartYearMonth = Integer.parseInt((String) args.get("StartYearMonth"));
			int EndYearMonth = Integer.parseInt((String) args.get("EndYearMonth"));
			
			String sqlAssItem = CHF.showNull((String)args.get("sqlAssItem")); //核算过滤
			
			sql = "select distinct opt from " + tempTable;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				int opt = rs.getInt("opt");
				switch (opt) {
				case 1://'科目',
					
					//调整
					String table = "z_accountrectify",strSql = "";
					if(!("".equals(Year) || "0".equals(Year)) ){	
						table = "z_accountyearrectify";
						strSql = " and yearrectify = '"+String.valueOf(Integer.parseInt(projectEndYear) + Integer.parseInt(Year)) + "'";
					}
					sql = "UPDATE " + this.tempTable + " a," + table + " b " +
					"	set " +
					"	a.debitsov = ifnull(debittotalocc1,0)+ifnull(debittotalocc2,0)," +
					"	a.creditsov = ifnull(credittotalocc1,0)+ifnull(debittotalocc2,0) " +
					"	WHERE a.opt = " + opt + 
					"	AND b.ProjectID = '" + projectId + "' " +
					strSql + 
					"	AND a.AccPackageID = b.AccPackageID " +
					"	AND a.SubjectID = b.SubjectID ";
					ps = conn.prepareStatement(sql);					
					ps.execute();
					DbUtil.close(ps);
					
					//余额
					sql = 
					"		SELECT a.accpackageid,a.subjectid,'' as inventoryid,'' as InventoryType, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (Debitremain + creditremain),0)) AS remainQ, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (Debitremain + creditremain),0)) AS remainF, \n" +
					
					"		SUM(debitocc) AS debitoccQ,SUM(creditocc) AS creditoccQ, \n" +
					"		SUM(debitocc) AS debitoccF,SUM(creditocc) AS creditoccF, \n" +
					
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * Balance,0)) AS BalanceQ, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * Balance,0)) AS BalanceF \n" +
						
					"		FROM c_account a \n" +
					"		WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
					"		AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
					"		AND a.isleaf1 = 1 \n" +
					"		AND (a.subjectfullname2 LIKE '" + SubjectName + "/%'  OR a.subjectfullname2 = '" + SubjectName + "' ) \n" + 
					"		GROUP BY a.accpackageid,a.subjectid \n" ;
					
					break;
				case 2://'科目数量'
					
					//调整
					table = "z_accountallrectify";
					strSql = "";
					if(!("".equals(Year) || "0".equals(Year)) ){	
						table = "z_accountallyearrectify";
						strSql = " and yearrectify = '"+String.valueOf(Integer.parseInt(projectEndYear) + Integer.parseInt(Year)) + "'";
					}
					sql = "UPDATE " + this.tempTable + " a," + table + " b " +
					"	set " +
					"	a.debitsov = ifnull(debittotalocc1f,0)+ifnull(debittotalocc2f,0)," +
					"	a.creditsov = ifnull(credittotalocc1f,0)+ifnull(debittotalocc2f,0) " +
					"	WHERE a.opt = " + opt + 
					"	AND b.ProjectID = '" + projectId + "' " +
					strSql + 
					"	AND a.AccPackageID = b.AccPackageID " +
					"	AND a.SubjectID = b.SubjectID " +
					"	and a.InventoryType = b.DataName";
					ps = conn.prepareStatement(sql);					
					ps.execute();
					DbUtil.close(ps);
					
					//余额
					sql = 
					"		SELECT a.accpackageid,a.subjectid,'' as inventoryid,a.DataName as InventoryType, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (Debitremain + creditremain),0)) AS remainQ, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (DebitremainF + creditremainF),0)) AS remainF, \n" +
					
					"		SUM(debitocc) AS debitoccQ,SUM(creditocc) AS creditoccQ, \n" +
					"		SUM(debitoccF) AS debitoccF,SUM(creditoccF) AS creditoccF, \n" +
					
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * Balance,0)) AS BalanceQ, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * BalanceF,0)) AS BalanceF \n" +
						
					"		FROM c_accountall a \n" +
					"		WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
					"		AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
					"		AND a.isleaf1 = 1 \n" +
					"		AND a.accsign = 2 \n" +
					"		AND (a.subjectfullname2 LIKE '" + SubjectName + "/%'  OR a.subjectfullname2 = '" + SubjectName + "' ) \n" + 
					"		GROUP BY a.accpackageid,a.subjectid,a.DataName \n" ;
					
					break;
				case 3://'核算'
					
					//调整
					table = "z_assitemaccrectify";
					strSql = "";
					if(!("".equals(Year) || "0".equals(Year)) ){	
						table = "z_assitemaccyearrectify";
						strSql = " and yearrectify = '"+String.valueOf(Integer.parseInt(projectEndYear) + Integer.parseInt(Year)) + "'";
					}
					sql = "UPDATE " + this.tempTable + " a," + table + " b " +
					"	set " +
					"	a.debitsov = ifnull(debittotalocc1,0)+ifnull(debittotalocc2,0)," +
					"	a.creditsov = ifnull(credittotalocc1,0)+ifnull(debittotalocc2,0) " +
					"	WHERE a.opt = " + opt + 
					"	AND b.ProjectID = '" + projectId + "' " +
					strSql + 
					"	AND a.AccPackageID = b.AccPackageID " +
					"	AND a.SubjectID = b.SubjectID " +
					"	and a.inventoryid = b.AssItemID";
					ps = conn.prepareStatement(sql);					
					ps.execute();
					DbUtil.close(ps);
					
					//余额
					sql = 
						"		SELECT a.accpackageid,a.accid as subjectid,a.assitemid as inventoryid,'' as InventoryType, \n" +
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (Debitremain + creditremain),0)) AS remainQ, \n" +
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (Debitremain + creditremain),0)) AS remainF, \n" +
						
						"		SUM(debitocc) AS debitoccQ,SUM(creditocc) AS creditoccQ, \n" +
						"		SUM(debitocc) AS debitoccF,SUM(creditocc) AS creditoccF, \n" +
						
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * Balance,0)) AS BalanceQ, \n" +
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * Balance,0)) AS BalanceF \n" +
							
						"		FROM c_assitementryacc a,( \n" +
						"			SELECT DISTINCT accpackageid,subjectid,AccName,subjectfullname1  FROM c_account a \n" + 
						"			WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
						"			AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
						"			AND (a.subjectfullname2 LIKE '" + SubjectName + "/%'  OR a.subjectfullname2 = '" + SubjectName + "' ) \n" + 
						"		) b \n" +
						"		WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
						"		AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
						"		AND a.isleaf1 = 1 \n" +
						sqlAssItem + 
						"		AND a.accpackageid = b.accpackageid \n" +
						"		AND a.AccID = b.subjectid \n" +
						"		GROUP BY a.accpackageid,a.accid,a.assitemid \n" ;
					
					break;
				case 4://'核算数量'
				
					//调整
					table = "z_assitemaccallrectify";
					strSql = "";
					if(!("".equals(Year) || "0".equals(Year)) ){	
						table = "z_assitemaccallyearrectify";
						strSql = " and yearrectify = '"+String.valueOf(Integer.parseInt(projectEndYear) + Integer.parseInt(Year)) + "'";
					}
					sql = "UPDATE " + this.tempTable + " a," + table + " b " +
					"	set " +
					"	a.debitsov = ifnull(debittotalocc1f,0)+ifnull(debittotalocc2f,0)," +
					"	a.creditsov = ifnull(credittotalocc1f,0)+ifnull(debittotalocc2f,0) " +
					"	WHERE a.opt = " + opt + 
					"	AND b.ProjectID = '" + projectId + "' " +
					strSql + 
					"	AND a.AccPackageID = b.AccPackageID " +
					"	AND a.SubjectID = b.SubjectID " +
					"	and a.inventoryid = b.AssItemID" +
					"	and a.InventoryType = b.DataName";
					ps = conn.prepareStatement(sql);					
					ps.execute();
					DbUtil.close(ps);
					
					//余额
					sql = 
						"		SELECT a.accpackageid,a.accid as subjectid,a.assitemid as inventoryid,a.DataName as InventoryType, \n" +
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (Debitremain + creditremain),0)) AS remainQ, \n" +
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', direction2 * (DebitremainF + creditremainF),0)) AS remainF, \n" +
						
						"		SUM(debitocc) AS debitoccQ,SUM(creditocc) AS creditoccQ, \n" +
						"		SUM(debitoccF) AS debitoccF,SUM(creditoccF) AS creditoccF, \n" +
						
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * Balance,0)) AS BalanceQ, \n" +
						"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', direction2 * BalanceF,0)) AS BalanceF \n" +
							
						"		FROM c_assitementryaccall a,( \n" +
						"			SELECT DISTINCT accpackageid,subjectid,AccName,subjectfullname1  FROM c_account a \n" + 
						"			WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
						"			AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
						"			AND (a.subjectfullname2 LIKE '" + SubjectName + "/%'  OR a.subjectfullname2 = '" + SubjectName + "' ) \n" + 
						"		) b \n" +
						"		WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
						"		AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
						"		AND a.isleaf1 = 1 \n" +
						"		AND a.accsign = 2 \n" +
						sqlAssItem + 
						"		AND a.accpackageid = b.accpackageid \n" +
						"		AND a.AccID = b.subjectid \n" +
						"		GROUP BY a.accpackageid,a.accid,a.assitemid,a.DataName \n" ;
					
					break;
				case 5://'存货'
					
					//调整
					//暂不支持 
					
					//余额
					sql = 
					"		SELECT a.accpackageid,a.subjectid,a.inventoryid,a.InventoryType, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', remainQ,0)) AS remainQ, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + StartYearMonth + "', remainF,0)) AS remainF, \n" +
					
					"		SUM(debitoccQ) AS debitoccQ,SUM(creditoccQ) AS creditoccQ, \n" +
					"		SUM(debitoccF) AS debitoccF,SUM(creditoccF) AS creditoccF, \n" +
					
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', BalanceQ,0)) AS BalanceQ, \n" +
					"		SUM(IF(SubYearMonth * 12 + SubMonth = '" + EndYearMonth + "', BalanceF,0)) AS BalanceF \n" +
						
					"		FROM c_inventoryaccount a ,(                       \n" +
					"			SELECT DISTINCT accpackageid,subjectid,AccName,subjectfullname1  FROM c_account a \n" + 
					"			WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
					"			AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
					"			AND (a.subjectfullname2 LIKE '" + SubjectName + "/%'  OR a.subjectfullname2 = '" + SubjectName + "' ) \n" + 
					"		) b  \n" +
					"		WHERE SubYearMonth * 12 + SubMonth >= '" + StartYearMonth + "' \n" +
					"		AND SubYearMonth * 12 + SubMonth <= '" + EndYearMonth + "'  \n" +
					"		AND a.isleaf1 = 1 \n" +
					"		AND a.accpackageid = b.accpackageid \n" +
					"		AND a.subjectid = b.subjectid \n" +
					"		GROUP BY a.accpackageid,a.subjectid,a.inventoryid,a.InventoryType \n" ;
					
					break;
				}
				//System.out.println(opt+"|"+sql);
				
				sql = "UPDATE " + tempTable + " a,(  \n" +
				sql +
				"	) b \n" +
				
				"	SET  \n" +
				"	a.remainQ = b.remainQ, a.remainF = b.remainF, \n" +
				"	a.debitoccQ = b.debitoccQ, a.creditoccQ = b.creditoccQ, \n" +
				"	a.debitoccF = b.debitoccF, a.creditoccF = b.creditoccF, \n" +
				"	a.BalanceQ = b.BalanceQ, a.BalanceF = b.BalanceF, \n" +
				
				"	a.RemainPrice = IFNULL(b.remainF / b.remainQ,0.00) , \n" +
				"	a.DebitOccPrice = IFNULL(b.debitoccF / b.debitoccQ,0.00), \n" +
				"	a.CreditOccPrice = IFNULL(b.creditoccF / b.creditoccQ,0.00) , \n" +
				"	a.BalancePrice = IFNULL(b.BalanceF / b.BalanceQ,0.00) \n" +
				
				"	WHERE  a.opt = '" + opt + "' " +
				"	and a.accpackageid = b.accpackageid  \n" +
				"	AND a.subjectid = b.subjectid \n" +
				"	AND a.inventoryid = b.inventoryid  \n" +
				"	AND a.InventoryType = b.InventoryType";
				
				ps = conn.prepareStatement(sql);					
				ps.execute();
				DbUtil.close(ps);
				
			}
			
			//审定数
			sql = "update " + tempTable + " set endoccurvalue = BalanceF + debitsov - creditsov ";
			ps = conn.prepareStatement(sql);					
			ps.execute();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			System.out.println("出错SQL:" + sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//只在【刷新内容=存货明细】时有效
	public void setProjectValue (Connection conn,Map args,String table) throws Exception {
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//明细临时表
			sql = "CREATE TABLE " + this.tempTable + " ("
			+ " id int(10) NOT NULL auto_increment,"
			//基础数据
			+ " Subjectid	 varchar(100) default NULL,"
			+ " InventoryId	 varchar(100) default NULL,"
			+ " InventoryName	 varchar(100) default NULL,"
			+ " InventoryFullName	 varchar(500) default NULL,"
			+ " InventoryType	 varchar(100) default NULL,"
			+ " uomunit	 varchar(100) default NULL,"//(计量单位)
			//凭证数据
			+ " oldVoucherID	 varchar(100) default NULL,"//凭证号
			+ " TypeID	 varchar(100) default NULL,"//凭证字
			+ " vchdate	 varchar(100) default NULL,"//(对应凭证日期)
			+ " serail	 varchar(100) default NULL,"
			+ " summary	 varchar(500) default NULL,"//摘要
			+ " OccurValue	 varchar(100) default NULL," //凭证金额
			+ " Quantity varchar(100) default NULL," //凭证数量
			+ " unitprice varchar(100) default NULL,"//凭证单价
			+ " unitname varchar(100) default NULL,"//数量名称
			//存货数据
			+ " stockid	 varchar(100) default NULL,"
			+ " InventoryEntryId	 varchar(100) default NULL,"//(出入库单编号)
			+ " InventoryEntryType	 varchar(100) default NULL,"
			+ " InventoryDate	 varchar(100) default NULL,"//(出入库单日期)
			+ " billId	 varchar(100) default NULL,"
			+ " InventoryQuantity	 varchar(100) default NULL," //Quantity 存货数量
			+ " InventoryOccurValue	 varchar(100) default NULL," //OccurValue 存货金额
			+ " InventoryPrices	 varchar(100) default NULL," //Prices	存货单价
			+ " FillUser	 varchar(100) default NULL,"//编制人
			+ " AuditUser	 varchar(100) default NULL,"//记账人
			+ " KeepUser	 varchar(100) default NULL,"//审核人
			+ " VendorName	 varchar(100) default NULL,"//(往来单位)
			+ " InventDisgn	 varchar(500) default NULL,"
			
			+ " PRIMARY KEY  (id)"
			+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			ASFuntion CHF=new ASFuntion();
			String SubjectName = CHF.showNull((String)args.get("SubjectName"));	//科目名称
			String sqlAssItem = CHF.showNull((String)args.get("sqlAssItem")); //核算过滤
			
			String check = CHF.showNull((String)args.get("核对方式"));//0为明细账与存货核对,1为存货与明细账核对
			if("".equals(check))check = "1"; 
			String inventoryname = CHF.showNull((String)args.get("存货名称"));
			String InventoryInOutType = CHF.showNull((String)args.get("出入库类别")); //1为入库,-1为出库;
			
			String begin = CHF.showNull((String)args.get("开始日期")); 
			String end = CHF.showNull((String)args.get("结束日期")); 
			String cutoff = CHF.showNull((String)args.get("截止日期")); 
			String offType = CHF.showNull((String)args.get("截止类型"));//【截止类型=0】表示截止日期之前,【截止类型=1】表示截止日期之后 
			
			Project project = (Project)args.get("project");
			String projectBegin = project.getAuditTimeBegin();
			String projectEnd = project.getAuditTimeEnd();
			
			String joinType = "",joinVchdate = ""; //关联方式
			if("".equals(check) || "0".equals(check)){
				//0为明细账与存货核对
				joinType = " INNER JOIN ";
				joinVchdate = "VchDate";
			}else{
				//1为存货与明细账核对
				joinType = " LEFT JOIN ";
				joinVchdate = "InventoryDate";
			}
			
			if("".equals(begin)) begin = projectBegin;
			if("".equals(end)) end = projectEnd;
			if("".equals(offType) || "0".equals(offType)){ 
				//【截止类型=0】表示截止日期之前
				if(!"".equals(cutoff)){
					//end = 截止日期
					end = cutoff;
				}
				begin = "'" + begin + "'";
				end = "'" + end + "'";
			}else{
				//【截止类型=1】表示截止日期之后 
				if(!"".equals(cutoff)){
					//begin = 截止日期+1 SQL:ADDDATE(" + begin + ",1)
					begin = "ADDDATE('"+cutoff+"',1)";
				}else{
					begin = "'" + begin + "'";
				}
				end = "'" + end + "'";
			}
			
			String sqlSelect1 = "",sqlSelect2 = "";
			if("1".equals(InventoryInOutType)){//出入库类别
				sqlSelect1 += "		AND a.inventoryinouttype = 1 \n" ;  
				sqlSelect2 += "		AND a.Dirction = 1 \n" ;
			}else if("-1".equals(InventoryInOutType)){
				sqlSelect1 += "		AND a.inventoryinouttype = -1 \n" ;  
				sqlSelect2 += "		AND a.Dirction = -1 \n" ;
			}
			
			if(!"".equals(inventoryname)){
				sqlSelect1 += "		AND (a.InventoryFullName = '" + inventoryname + "' or a.InventoryFullName like '"+inventoryname+"/%') \n" ;   
			}
			
			sql = "select distinct opt from " + table;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				int opt = rs.getInt("opt");
				switch (opt) {
				case 1://'科目',
				case 2://'科目数量'	
					if("".equals(check) || "0".equals(check)){
						//0为明细账与存货核对
						sql = "		SELECT  " +
						//基础数据
						"	b.Subjectid , \n" +
						"	b.InventoryId	 ,b.InventoryName	 ,b.InventoryFullName	 ,b.InventoryType	 ,b.uomunit	 , \n" +
						"	a.voucherFillUser as FillUser	 , \n" +
						"	a.voucherAuditUser as AuditUser	 , \n" +
						"	a.voucherKeepUser as KeepUser	 , \n" +
						//凭证数据
						"	a.oldVoucherID	 ,a.TypeID	 ,a.vchdate	 ,a.serail	 ,a.summary	 , \n" +
						"	a.OccurValue	 ,a.Quantity ,a.unitprice ,a.unitname  \n" +
						
						"	FROM c_subjectentry a , " + table + " b  \n" +
						"	WHERE 1=1 \n" +
						"	and b.opt = " + opt + " \n" +
						sqlSelect2 +
						"	AND DATE_FORMAT(a.VchDate,'%Y-%m-%d') >= DATE_FORMAT(" + begin + ",'%Y-%m-%d') \n" +
						"	AND DATE_FORMAT(a.VchDate,'%Y-%m-%d') <= DATE_FORMAT(" + end + ",'%Y-%m-%d') \n" +
						"	AND a.accpackageid = b.accpackageid \n" +
						"	AND a.subjectid = b.subjectid \n" ;
						System.out.println(opt + "|SQL:"+sql);
						
						sql = "insert into " + this.tempTable + " (" +
						"Subjectid,InventoryId,InventoryName,InventoryFullName,InventoryType,uomunit,FillUser,AuditUser,KeepUser," +//基础数据
						"oldVoucherID,TypeID,vchdate,serail,summary,OccurValue,Quantity,unitprice,unitname" +//凭证数据
						") \n" + sql ; 
						ps = conn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
						break;
						
					}
					break;
				case 3://'核算'
				case 4://'核算数量'
					if("".equals(check) || "0".equals(check)){
						//0为明细账与存货核对
						sql = "		SELECT  " +
						//基础数据
						"	b.Subjectid , \n" +
						"	b.InventoryId	 ,b.InventoryName	 ,b.InventoryFullName	 ,b.InventoryType	 ,b.uomunit	 , \n" +
//						"	a.voucherFillUser as FillUser	 , \n" +
//						"	a.voucherAuditUser as AuditUser	 , \n" +
//						"	a.voucherKeepUser as KeepUser	 , \n" +
						//凭证数据
						"	a.oldVoucherID	 ,a.TypeID	 ,a.vchdate	 ,a.serail	 ,a.summary	 , \n" +
						"	a.AssItemSum as OccurValue	 ,a.Quantity ,a.unitprice ,a.unitname  \n" +
						
						"	FROM c_assitementry a , " + table + " b  \n" +
						"	WHERE 1=1 \n" +
						"	and b.opt = " + opt + " \n" +
						sqlSelect2 +
						"	AND DATE_FORMAT(a.VchDate,'%Y-%m-%d') >= DATE_FORMAT(" + begin + ",'%Y-%m-%d') \n" +
						"	AND DATE_FORMAT(a.VchDate,'%Y-%m-%d') <= DATE_FORMAT(" + end + ",'%Y-%m-%d') \n" +
						"	AND a.accpackageid = b.accpackageid \n" +
						"	AND a.subjectid = b.subjectid \n" +
						"	and a.assitemid = b.inventoryid \n" ;
						System.out.println(opt + "|SQL:"+sql);
						
						sql = "insert into " + this.tempTable + " (" +
						"Subjectid,InventoryId,InventoryName,InventoryFullName,InventoryType,uomunit," +//基础数据
						"oldVoucherID,TypeID,vchdate,serail,summary,OccurValue,Quantity,unitprice,unitname" +//凭证数据
						") \n" + sql ; 
						ps = conn.prepareStatement(sql);
						ps.execute();
						DbUtil.close(ps);
					}
					break;
				case 5://'存货'
					//凭证临时表
					sql = "CREATE TABLE " + table + "_1 LIKE c_subjectentry";
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					sql = "insert into " + table + "_1 " +
					"		SELECT DISTINCT a.* FROM c_subjectentry a , (SELECT DISTINCT accpackageid,subjectid,opt FROM " + table + ")  b  \n" +
					"		WHERE 1=1 \n" +
					"		AND b.opt = " + opt + " \n" +
					sqlSelect2 +
					"		AND DATE_FORMAT(a.VchDate,'%Y-%m-%d') >= DATE_FORMAT(" + begin + ",'%Y-%m-%d') \n" +
					"		AND DATE_FORMAT(a.VchDate,'%Y-%m-%d') <= DATE_FORMAT(" + end + ",'%Y-%m-%d') \n" +
					"		AND a.accpackageid = b.accpackageid \n" +
					"		AND a.subjectid = b.subjectid \n" ;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					//存货明细临时表
					sql = "CREATE TABLE " + table + "_2 LIKE c_inventoryentry";
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					sql = "insert into " + table + "_2 " +
					"		SELECT a.* \n" +
					"		FROM c_inventoryentry a , " + table + " b \n" + 
					"		WHERE 1=1 \n" + 
					sqlSelect1 +
					"		AND DATE_FORMAT(a."+joinVchdate+",'%Y-%m-%d') >= DATE_FORMAT(" + begin + ",'%Y-%m-%d') \n" +
					"		AND DATE_FORMAT(a."+joinVchdate+",'%Y-%m-%d') <= DATE_FORMAT(" + end + ",'%Y-%m-%d') \n" +
					"		AND a.accpackageid = b.accpackageid \n" +
					"		AND a.subjectid = b.subjectid \n" +
					"		AND a.inventoryid = b.inventoryid \n" ;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					sql = "SELECT  " +
					//基础数据
					"	IFNULL(a.Subjectid,c.Subjectid) AS Subjectid	 , \n" +
					"	a.InventoryId	 ,a.InventoryName	 ,a.InventoryFullName	 ,a.InventoryType	 ,a.uomunit	 , \n" +
					"	IFNULL(a.FillUser,c.voucherFillUser) as FillUser	 , \n" +
					"	IFNULL(a.AuditUser,c.voucherAuditUser) as AuditUser	 , \n" +
					"	IFNULL(a.KeepUser,c.voucherKeepUser) as KeepUser	 , \n" +
					//凭证数据
					"	c.oldVoucherID	 ,c.TypeID	 ,c.vchdate	 ,c.serail	 ,c.summary	 , \n" +
					"	c.OccurValue	 ,c.Quantity ,c.unitprice ,c.unitname , \n" +
					//存货数据
					"	a.stockid	 ,a.InventoryEntryId	 ,a.InventoryEntryType	 ,a.InventoryDate	 ,a.billId	 , \n" +
					"	a.Quantity AS InventoryQuantity	 ,a.OccurValue AS InventoryOccurValue	 ,a.Prices AS InventoryPrices	 , \n" +
					"	a.VendorName	 ,a.InventDisgn	 \n" +
					
					"	FROM " + table + "_2 a " + joinType + " " + table + "_1 c ON 1=1 \n" +
					"	AND a.AccPackageID = c.AccPackageID \n" +
					"	AND a.typeid = c.typeid \n" +
					"	AND a.VchDate = c.VchDate \n" +
					"	AND a.oldvoucherid = c.oldvoucherid \n" +
					"	AND a.SubjectID = c.SubjectID";
					System.out.println(opt + "|SQL:"+sql);
					
					sql = "insert into " + this.tempTable + " (" +
					"Subjectid,InventoryId,InventoryName,InventoryFullName,InventoryType,uomunit,FillUser,AuditUser,KeepUser," +//基础数据
					"oldVoucherID,TypeID,vchdate,serail,summary,OccurValue,Quantity,unitprice,unitname," +//凭证数据
					"stockid,InventoryEntryId,InventoryEntryType,InventoryDate,billId,InventoryQuantity,InventoryOccurValue,InventoryPrices,VendorName,InventDisgn" +//存货数据
					") \n" + sql ; 
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					sql = "DROP TABLE IF EXISTS " + table + "_1"; //删除临时表
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					sql = "DROP TABLE IF EXISTS " + table + "_2"; //删除临时表
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					break;
				}
				
				
			}
			
		} catch (Exception e) {
			System.out.println("出错SQL:" + sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	

	
	
	
	
	
	
	//=================================================================================================================
	//重写8888公式
	//=取列公式插入(8888,"","存货名称","&刷新内容=存货余额&科目名称=${L3}&刷余额表合计=是")
	public ResultSet process1(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		try {
		    
			st = conn.createStatement();
			
			String acc = CHF.showNull((String) args.get("curAccPackageID"));
	        String projectid = CHF.showNull((String) args.get("curProjectid"));   
			String SubjectName = CHF.showNull((String)args.get("科目名称"));		//科目名称
			String InventoryName = CHF.showNull((String)args.get("存货名称"));  //存货名称
			String type1 = CHF.showNull((String)args.get("刷新内容"));  //刷新类型
			String InventoryInOutType = CHF.showNull((String)args.get("出入库类别"));  //刷新类型
			String year = CHF.showNull((String)args.get("年份"));  //刷新年份
			String month = CHF.showNull((String)args.get("月份"));  //刷新年份
			String backMonth = CHF.showNull((String)args.get("倒推的月份"));  //倒推的月份
			String sampleFlow = CHF.showNull((String)args.get("截止性抽凭的凭证日期"));  //截止性抽凭的凭证日期
			String sum1 = CHF.showNull((String)args.get("刷余额表合计"));  //刷余额表合计
			String InventoryType = CHF.showNull((String)args.get("存货规格"));  //存货规格
			
			String allYear = CHF.showNull((String)args.get("比较年份"));	//比较年份=-1
			
			args.put("projectid",projectid);
			
			if (SubjectName==null || SubjectName.equals("")){
                String manuid=(String)args.get("manuid");
                if (manuid==null || manuid.equals("")){
                    SubjectName = getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));
                }else{
                    //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                    SubjectName = getTaskSubjectNameByManuID(conn, manuid);
                }          
            }

			if(!"".equals(InventoryName)){
				SubjectName = InventoryName;
			}
			
            String sName1 = changeSubjectName(conn,projectid,SubjectName);
            if(!"".equals(sName1)){
            	SubjectName = sName1; 
            }            
            args.put("subjectname",SubjectName);
            
            //通过科目名称找到这个项目所有年份对应的科目编号
            String[] subjectids = getSubjectIdBySubjectName(conn,projectid,SubjectName);   
           
            

			if(!"".equals(InventoryName)){//存货名称不为空
				String getInventoryId = "";
				
	           	Statement st1 = null;
            	ResultSet rs1 = null;
            	try{
            		getInventoryId = "select ifnull(group_concat(Inventoryid SEPARATOR \"','\"),-1) from c_inventorytype where 1=1 and (InventoryFullName like '%/"+InventoryName+"%' or InventoryFullName like '"+InventoryName+"/%') and isleaf = 1";
    	           	st1 = conn.createStatement();
            		rs1 = st1.executeQuery(getInventoryId);
            		String inventoryId = "";
            		if(rs1.next()){
            			inventoryId = rs1.getString(1);
            		}
            		rs1.close();
            		
            		
            		args.put("Inventoryid"," and Inventorytype in ('"+inventoryId+"') " );
            		
            	} catch (Exception e) {
        			e.printStackTrace();
        		} finally {
        			DbUtil.close(rs1);
        			DbUtil.close(st1);
        		}
			
			}else{
				args.put("Inventoryid"," " );
			}
            
			if(!"".equals(InventoryType)){//存货规格不为空
				args.put("InventoryType"," and InventoryType = '"+InventoryType+"' " );
			}else{
				args.put("InventoryType"," " );
			}
			
            
            if(!"".equals(sampleFlow)){//截止日期不为空
            	Statement st1 = null;
            	ResultSet rs1 = null;
            	try{
            		String getFlowidSql = "select flowid from z_vouchersampleflow where sampleFlow = '"+sampleFlow+"' and projectId = '"+projectid+"' and subjectid = '"+subjectids[subjectids.length-1]+"' order by sampleDate desc limit 1";
            		st1 = conn.createStatement();
            		rs1 = st1.executeQuery(getFlowidSql);
            		String flowid = "";
            		if(rs1.next()){
            			flowid = rs1.getString(1);
            		}
            		rs1.close();
            		
            		String getVoucherSql = " select distinct entryVchDate,entryTypeID,entryOldVoucherID from z_voucherspotcheck where flowid='"+flowid+"' ";
            	
            		rs1 = st1.executeQuery(getVoucherSql);
            		String entryVchDate = "";
            		String entryTypeID = "";
            		String entryOldVoucherID = "";
            		if(rs1.next()){
            			entryVchDate = rs1.getString(1)+"";
            			entryTypeID = rs1.getString(2)+"";
            			entryOldVoucherID = rs1.getString(3)+"";
            		}
            		rs1.close();
            		
            		String endSql = "select ifnull(group_concat(distinct InventoryEntryId SEPARATOR \"','\"),-1) as InventoryEntryId  from c_inventoryentry where oldVoucherID = '"+entryOldVoucherID+"' and  TypeID = '"+entryTypeID+"' and VchDate ='"+entryVchDate+"'";
            		rs1 = st1.executeQuery(endSql);
            		String InventoryEntryId = "";
            		if(rs1.next()){
            			InventoryEntryId = rs1.getString(1)+"";	
            		}
            		rs1.close();
            		
            		args.put("InventoryEntryId"," and InventoryEntryId in ('"+InventoryEntryId+"') " );
            		
            	} catch (Exception e) {
        			e.printStackTrace();
        		} finally {
        			DbUtil.close(rs1);
        			DbUtil.close(st1);
        		}

            }else{
            	args.put("InventoryEntryId"," ");
            }
            
            
            if("是".equals(sum1)){
            	
            	args.put("groupby"," group by InventoryfullName,InventoryType");
            }else{
            	
            	args.put("groupby"," group by InventoryfullName,InventoryType,SubYearMonth,SubMonth ");
            }
            
            
        	ProjectService projectService = new ProjectService(conn);
			Project project = projectService.getProjectById(projectid);
			
			String strStart = project.getAuditTimeBegin();
            String strEnd = project.getAuditTimeEnd();
            String customerId = project.getCustomerId();
            
            String strStartYear = "";
            String strEndYear = "";
            String strEndMonth = "";
            if (strStart != null && strStart.length() == 10) {
           	 	strStartYear = strStart.substring(0, 4);
            }

            if (strEnd != null && strEnd.length() == 10) {
           	 	strEndYear = strEnd.substring(0, 4);
           	 	strEndMonth = strEnd.substring(5, 7);
            }
            
            int newMonth = 12;
            if(backMonth != null && !"".equals(backMonth)){
            	newMonth = Integer.parseInt(strEndMonth)-Integer.parseInt(backMonth);
            }
            
            
            args.put("strStart",strStart);
            args.put("strEnd", strEnd);
            args.put("InventoryName", InventoryName);
            args.put("newMonth",newMonth+"");
            
            int[] result1 = getProjectAuditAreaByProjectid(conn, projectid);
            String  strStartYearMonth = String.valueOf(result1[0] * 12 + result1[1]);
            String strEndYearMonth = String.valueOf(result1[2] * 12 + result1[3]);
          
        
            args.put("StartYearMonth", strStartYearMonth);
            args.put("EndYearMonth", strEndYearMonth);
            
            
            if(!"".equals(year)){
            	args.put("year",  " and SubYearMonth = "+year);
            }else{
            	args.put("year",  " ");
            }
            
            if(!"".equals(month)){
            	  args.put("month", " and SubMonth = "+month);
            }else{
          	      args.put("month",  " ");
            }
            
            InventoryService inventoryService = new InventoryService(conn);
            
            //得到项目所有的帐套编号
            ArrayList arrayList = inventoryService.isInventory(customerId, strStartYear, strEndYear);
           
            
            String resultSql = ""; 
           if(arrayList != null){//有存货模块
   	
        		   	if(!"".equals(InventoryName)){//有存货名称条件
        		   		
        		   		resultSql = getSql(type1,InventoryInOutType);
        		   		
        		   	}else{//没给存货名称条件
        		   		
        		   		resultSql = getSql1(type1,InventoryInOutType,arrayList,subjectids,conn);
        		   		
        		   	}

        	   
           }else{//没有存货模块
   	
               //查找该科目在客户中的科目id,请注意即使有一对多的科目，这里也只是取其中的一条；
               String[] result = this.getClientIDAndDirectionByStandName(conn,
            		   acc, projectid,
            		   SubjectName);
               String subjectid = result[0];

               //如果没有提供方向这个参数，则取科目余额方向
               String ptype = (String) args.get("ptype");
               if (ptype == null || ptype.equals("")) {
                   args.put("ptype", result[1]);
               }

               //判断该科目是否叶子并且有自增科目。
               st = conn.createStatement();
               resultSql = ""
                           + " select 1 from  \n"
                           + " c_account a \n"
                           + " inner join \n"
                           + " z_usesubject b \n"
                           + " on a.subjectid=b.tipsubjectid \n"
                           + " where a.accpackageid='" + acc + "' \n"
                           + "   and a.subjectfullname2='" + SubjectName + "' \n"
                           + "   and a.submonth=1 \n"
                           + "   and a.isleaf1=1 \n"
                           + "   and b.accpackageid='" + acc + "' \n"
                           + "   and b.projectid='" + projectid + "' \n";
               rs = st.executeQuery(resultSql);

               //把有无下级科目刷新放进去
               if (rs.next()) {
                   resultSql = getSql2("0", subjectid,args,conn);
               } else {
                   resultSql = getSql2("1", subjectid,args,conn);
               }

           }
          
            
           
           //最终查询结果
           resultSql = this.setSqlArguments(resultSql, args);
           System.out.println("resultSql="+resultSql);
           rs = st.executeQuery(resultSql);

           return rs;
            
		} catch (Exception e) {
			e.printStackTrace();
			//DbUtil.close(rs);
			DbUtil.close(st);
			throw e;
		}
	}
	
	

    /**
     *
     * 有存货模块有存货名称
     *    
     * 
     * 
     */
    public String getSql(String type1,String InventoryInOutType){
    	String sql = "";
    	if(type1.equals("存货余额")){
    		
    		 sql = "select *,concat(a.InventoryName,' ',a.InventoryType) as InventoryNameType,substr(a.InventoryFullName,1,if(locate('/',a.InventoryFullName) = 0,length(a.InventoryFullName),locate('/',a.InventoryFullName)-1)) as Inventorytype1,ifnull(a.BalanceF,0)+ifnull(b.debitsov,0)-ifnull(b.creditsov,0) as endoccurvalue from ( \n"
    			+"select Inventoryid,InventoryName,InventoryFullName,InventoryType,SubYearMonth,SubMonth,uomunit,sum(DebitOccQ) as DebitOccQ, \n"
    			+" ifnull(sum(DebitOccF)/sum(DebitOccQ),'0.00') as DebitOccPrice, \n"
    			+" sum(DebitOccF) as DebitOccF, \n"
    			+" sum(CreditOccQ) as CreditOccQ, \n"
    			+" ifnull(sum(CreditOccF)/sum(CreditOccQ),'0.00') as CreditOccPrice, \n"
    			+" sum(CreditOccF) as CreditOccF, \n"
    			+" sum(if(submonth=1,RemainQ,0))-sum(CreditOccQ)+sum(DebitOccQ) as BalanceQ, \n"
    			+" ifnull((sum(if(submonth=1,RemainF,0))-sum(CreditOccF)+sum(DebitOccF))/(sum(if(submonth=1,RemainQ,0))-sum(CreditOccQ)+sum(DebitOccQ)),'0.00') as BalancePrice, \n"
    			+" sum(if(submonth=1,RemainF,0))-sum(CreditOccF)+sum(DebitOccF) as BalanceF, \n"
    			+" sum(if(submonth=1,RemainQ,0)) as RemainQ, \n"
    			+" ifnull(sum(if(submonth=1,RemainF,0))/sum(if(submonth=1,RemainQ,0)),'0.00') as RemainPrice, \n"
    			+" sum(if(submonth=1,RemainF,0)) as RemainF \n"
    			+" from c_inventoryaccount where InventoryName = '${InventoryName}' \n" 
    			+ " ${InventoryType} \n"
    			+" and concat(SubYearMonth,'-',if(SubMonth<10,concat('0',SubMonth),SubMonth),'-','01')>='${strStart}' \n"
    			+" and concat(SubYearMonth,'-',if(SubMonth<10,concat('0',SubMonth),SubMonth),'-','31')<='${strEnd}' \n"
    			+"${year}"
    			+"${month}"
    			+" ${groupby} \n"
    			+")a \n"
    			+"left join \n"
    			+"( select inventorytype,ifnull(sum(if(Dirction = 1,OccurValue,0)),0.00) as debitsov,ifnull(sum(if(Dirction = -1,OccurValue,0)),0.00) as creditsov from z_subjectentryrectify where projectid = '${projectid}' ${Inventoryid} and (Property like '3%' or Property like '4%' or Property like '5%') group by projectid having inventorytype!='' and inventorytype is not null)b \n"
    			+"on 1=1";
    		 //+"on a.Inventoryid = b.inventorytype \n";
    		 	//+" order by SubYearMonth,InventoryId,SubMonth \n";
    			
    		 	
    		
    	}else if(type1.equals("存货明细")){
    		String subSqlwhere = "";
    		if(!"".equals(InventoryInOutType)){
    			subSqlwhere = " and InventoryInOutType = '"+InventoryInOutType+"' \n";
    		}
    		 sql =""
    					+" select InventoryDate,InventoryEntryId, \n"
    					+" concat(convert(oldVoucherID using utf8),' ',TypeID) as oldVoucherID, \n"
    					+" concat(InventoryName,' ',InventoryType) as InventoryNameType,  \n"
    					+" if(InventoryInOutType=1,Quantity,'0.00') as a_Quantity,if(InventoryInOutType=1,prices,'0.00') as a_prices,if(InventoryInOutType=1,OccurValue,'0.00') as a_OccurValue,  \n"
    					+" if(InventoryInOutType=-1,Quantity,'0.00') as b_Quantity,if(InventoryInOutType=-1,prices,'0.00') as b_prices,if(InventoryInOutType=-1,OccurValue,'0.00') as b_OccurValue,   \n"
    					+" InvoiceId,VendorName,VchDate,FillUser,AuditUser,KeepUser,uomunit "
    					+" from c_inventoryentry  \n"
    					+" where 1=1 \n"
    					+" and (InventoryFullName like '%${InventoryName}' or InventoryFullName like '%${InventoryName}/%') \n"
    					+" and InventoryDate>='${strStart}'  \n"
    					+" and InventoryDate<='${strEnd}'  \n"
    					+" ${InventoryEntryId}  \n"
    					+ " ${InventoryType} \n"
    					+subSqlwhere
    					+" order by InventoryDate, oldVoucherID";
    	}
    	
    	return sql;
    }
    
    
    /**
    *
    * 有存货模板，没有存货名称，有科目名称
    * 这里要注意兼容跨年
    *     
    */
   public String getSql1(String type1,String InventoryInOutType,ArrayList arrayList,String[] subjectids,Connection conn){
   	
	 String sql = "";
   	if(type1.equals("存货余额")){
   		
   		int i = 0;
   		String tempSqlWhere = "";
   		String and = "";
   		for (Iterator iter = arrayList.iterator(); iter.hasNext();) {//支持一年和多年
   		   	 String accpackageid = (String) iter.next();
   		   	 tempSqlWhere += "(accpackageid="+accpackageid+" and subjectid='"+subjectids[i]+"') or";
   		   	 and = " and ";
   		   	 i++;
   		}
   		if(tempSqlWhere.endsWith("r")){//去掉结尾的or
   			tempSqlWhere = tempSqlWhere.substring(0,tempSqlWhere.length()-2);
   		}
   		ArrayList list = new ArrayList();
   		PreparedStatement ps = null;
		ResultSet rs = null;
   		try{
   	   		String  tempsql = "select accpackageid,ifnull(group_concat(distinct InventoryId SEPARATOR \"','\"),-1) from c_inventoryentry where 1=1"+and+tempSqlWhere+" group by accpackageid  order by accpackageid";
   			ps = conn.prepareStatement(tempsql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				list.add(rs.getString(1));
				list.add(rs.getString(2));
			}
			
			rs.close();
			ps.close();
			
			
			
   		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
   		
		String tempSqlWhere1 = "";
		String and1 = "";
		for(int j=0;j<list.size();j=j+2){
			 tempSqlWhere1 += "(accpackageid="+list.get(j)+" and InventoryId in ('"+list.get(j+1)+"')) \n or";
   		   	 and1 = " and ";
		}
		
		if(tempSqlWhere1.endsWith("r")){//去掉结尾的or
   			tempSqlWhere1 = tempSqlWhere1.substring(0,tempSqlWhere1.length()-2);
   		}
		
	
		 sql = "select *,concat(a.InventoryName,' ',a.InventoryType) as InventoryNameType,substr(a.InventoryFullName,1,if(locate('/',a.InventoryFullName) = 0,length(a.InventoryFullName),locate('/',a.InventoryFullName)-1)) as Inventorytype1,ifnull(a.BalanceF,0)+ifnull(b.debitsov,0)-ifnull(b.creditsov,0) as endoccurvalue from ( \n"
			 	+"select Inventoryid,InventoryName,InventoryFullName,InventoryType,SubYearMonth,SubMonth,uomunit,sum(DebitOccQ) as DebitOccQ, \n"
 				+" ifnull(sum(DebitOccF)/sum(DebitOccQ),'0.00') as DebitOccPrice, \n"
 				+" sum(DebitOccF) as DebitOccF, \n"
 				+" sum(CreditOccQ) as CreditOccQ, \n"
 				+" ifnull(sum(CreditOccF)/sum(CreditOccQ),'0.00') as CreditOccPrice, \n"
 				+" sum(CreditOccF) as CreditOccF, \n"
 				+" sum(if(submonth=1,RemainQ,0))-sum(CreditOccQ)+sum(DebitOccQ) as BalanceQ, \n"
    			+" ifnull((sum(if(submonth=1,RemainF,0))-sum(CreditOccF)+sum(DebitOccF))/(sum(if(submonth=1,RemainQ,0))-sum(CreditOccQ)+sum(DebitOccQ)),'0.00') as BalancePrice, \n"
    			+" sum(if(submonth=1,RemainF,0))-sum(CreditOccF)+sum(DebitOccF) as BalanceF, \n"
    			+" sum(if(submonth=1,RemainQ,0)) as RemainQ, \n"
    			+" ifnull(sum(if(submonth=1,RemainF,0))/sum(if(submonth=1,RemainQ,0)),'0.00') as RemainPrice, \n"
    			+" sum(if(submonth=1,RemainF,0)) as RemainF \n"
				+" from c_inventoryaccount \n"
				+" where 1=1  \n"
				+" and isleaf1 = 1 \n"
				+" ${year}"
	    		+" ${month}"
				+and1+"  \n"
				+tempSqlWhere1+"  \n"
    			+" and concat(SubYearMonth,'-',if(SubMonth<10,concat('0',SubMonth),SubMonth),'-','01')>='${strStart}' \n"
    			+" and concat(SubYearMonth,'-',if(SubMonth<10,concat('0',SubMonth),SubMonth),'-','31')<='${strEnd}' \n"
				+" ${groupby} \n"
				+")a \n"
    			+"left join \n"
    			+"( select inventorytype as inventorytype2,ifnull(sum(if(Dirction = 1,OccurValue,0)),0.00) as debitsov,ifnull(sum(if(Dirction = -1,OccurValue,0)),0.00) as creditsov from z_subjectentryrectify where projectid = '${projectid}' and (Property like '3%' or Property like '4%' or Property like '5%') group by inventorytype having inventorytype!='' and inventorytype is not null)b \n"
    			+"on a.Inventoryid = b.inventorytype2 \n"
				+" order by SubYearMonth,InventoryId,SubMonth \n";
		 
		if(tempSqlWhere1.equals("")){
	   			sql = "select * from ("+sql+") a where 1=2";
	   	}
   		
   	}else if(type1.equals("存货明细")){
   		
   		int i = 0;
   		String tempSqlWhere = "";
   		String and = "";
   		for (Iterator iter = arrayList.iterator(); iter.hasNext();) {//支持一年和多年
   		   	 String accpackageid = (String) iter.next();
   		   	 
   		   	 tempSqlWhere += "(accpackageid="+accpackageid+" and subjectid='"+subjectids[i]+"') or";
   		   	 and = " and ";
   		   	 i++;
   		}
   		if(tempSqlWhere.endsWith("r")){//去掉结尾的or
   			tempSqlWhere = tempSqlWhere.substring(0,tempSqlWhere.length()-2);
   		}

		
   		
   		String subSqlwhere = "";
   		if(!"".equals(InventoryInOutType)){//定义是出库还是入库
   			subSqlwhere = " and InventoryInOutType = '"+InventoryInOutType+"' \n";
   		}

   		 sql =""
   					+" select InventoryDate,InventoryEntryId, \n"
   					+" concat(convert(oldVoucherID using utf8),' ',TypeID) as oldVoucherID, \n"
   					+" concat(InventoryName,' ',InventoryType) as InventoryNameType,  \n"
   					+" if(InventoryInOutType=1,Quantity,'0.00') as a_Quantity,if(InventoryInOutType=1,prices,'0.00') as a_prices,if(InventoryInOutType=1,OccurValue,'0.00') as a_OccurValue,  \n"
   					+" if(InventoryInOutType=-1,Quantity,'0.00') as b_Quantity,if(InventoryInOutType=-1,prices,'0.00') as b_prices,if(InventoryInOutType=-1,OccurValue,'0.00') as b_OccurValue,   \n"
   					+" InvoiceId,VendorName,VchDate,FillUser,AuditUser,KeepUser,uomunit "
   					+" from c_inventoryentry  \n"
   					+" where 1=1 \n"
   					+and
   					+tempSqlWhere
   					+subSqlwhere
					+" and InventoryDate>='${strStart}'  \n"
					+" and InventoryDate<='${strEnd}'  \n"
   					+" ${InventoryEntryId}  \n"
   					+" order by InventoryDate, oldVoucherID";
   		 
   		if(tempSqlWhere.equals("")){
   			sql = "select * from ("+sql+") a where 1=2";
   		}
   		 
   	}else{
   		
   		int i = 0;
   		String InventoryIds = "";
   		PreparedStatement ps = null;
		ResultSet rs = null;
   		try{
   	   		String  tempsql = "select ifnull(group_concat(distinct InventoryId SEPARATOR \"','\"),-1) from c_inventoryentry where 1=1 and subjectid = '"+subjectids[subjectids.length-1]+"' group by accpackageid  order by accpackageid";
   			ps = conn.prepareStatement(tempsql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				InventoryIds = rs.getString(1);
			}
			
			rs.close();
			ps.close();

   		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
   		
   		 sql = "select a.submonth,concat(b.InventoryName,' ',b.InventoryType) as InventoryNameType,a.inventoryid,b.InventoryDate,subString(b.inventorydate,1,4)*12+subString(b.inventorydate,6,2) as aaa,a.subyearmonth*12+submonth as bbb,a.submonth,a.inventoryname,a.uomunit,a.remainF,  \n"
   					+" concat(a.subyearmonth,'-',a.submonth,'-','30') as lastdate,a.balanceQ,a.balanceF/a.balanceQ as price,a.balanceF from c_inventoryaccount a  \n"
   					+" left join c_inventoryentry b  \n"
   					+" on a.inventoryid = b.inventoryid  \n"
   					+" where 1=1  \n"
   					+" and a.inventoryid in ('"+InventoryIds+"') \n"
   					+" and a.submonth>${newMonth} \n"
   					+" and  subString(b.inventorydate,1,4)*12+subString(b.inventorydate,6,2)=a.subyearmonth*12+submonth  \n"
   					+" order by subyearmonth,submonth,InventoryDate  \n";
   		
   	}
   	
   	return sql;
   }
   
   /**
   *
   * 无存货模块
   *    
   * 
   * 
   */
  public String getSql2(String rectifySign, String subjectid,Map args,Connection conn){
	  /*
	   * 1、有核算时没有过滤科目
	   * 2、核算的汇总没有用科目+核算来汇总
	   */  
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
		String sqlAssItem = "";
		String sqlNotSubject = "";
		String sqlStr = "select distinct a.ifequal,a.subjectid,a.asstotalname1  " +
		" from c_subjectassitem a,c_account b " +
		" where 1=1 " +
		" and b.subyearmonth*12+b.submonth >='${StartYearMonth}' " +
		" and b.subyearmonth*12+b.submonth <='${EndYearMonth}'  " +
		" and (b.subjectfullname2 = '${subjectname}' or b.subjectfullname2 like '${subjectname}/%') " +
		" and a.accpackageid =b.accpackageid " +
		" and a.subjectid = b.subjectid " ;
		sqlStr = this.setSqlArguments(sqlStr, args);
		System.out.println("辅助核算:"+sqlStr); 
		ps = conn.prepareStatement(sqlStr);
		rs = ps.executeQuery();
		while(rs.next()){
			String ifequal = rs.getString("ifequal");
			String accid = rs.getString("subjectid");
			String asstotalname1 = rs.getString("asstotalname1");
			
			if("0".equals(ifequal)){ //有核算披露
				sqlNotSubject += ",'"+accid+"'";
				sqlAssItem += "or (a.accid = '"+accid+"' and a.asstotalname1 like '"+asstotalname1+"/%' ) ";
			}
		
		}
		
		if(!"".equals(sqlAssItem)){
			sqlNotSubject = " and subjectid not in ("+sqlNotSubject.substring(1)+")";
	    	sqlAssItem = " and ( " + sqlAssItem.substring(2)+ ") ";
	    }else{
	    	sqlNotSubject = "";
	    	sqlAssItem = " and 1=2 ";
	    }
			
		DbUtil.close(rs);
		DbUtil.close(ps);  
		  
		return 
		  "select dataname as uomunit,balance as BalanceQ,cnybalance as BalanceF,debitocc as DebitOccQ,cnydebitocc as DebitOccF,creditocc as CreditOccQ,cnycreditocc as CreditOccF,debitsov3 as debitsov,creditsov3 as creditsov,ifnull(cnybalance,0)+ifnull(debitsov3,0)-ifnull(creditsov3,0) as endoccurvalue,ifnull(accname,cnysubjectname) as InventoryNameType, \n " +
			" '' as DebitOccPrice,'' as KeepUser,cnyinitbalance as RemainF,'' as BalancePrice,'' as InventoryType,ifnull(accname,cnysubjectname) as InventoryName, \n "+
			" '' as  InventoryDate, '' as CreditOccPrice, substr(ifnull(SubjectFullName,cnysubjectfullname),1,if(locate('/',ifnull(SubjectFullName,cnysubjectfullname)) = 0,length(ifnull(SubjectFullName,cnysubjectfullname)),locate('/',ifnull(SubjectFullName,cnysubjectfullname))-1)) as Inventorytype1, '' as a_prices, '' as a_OccurValue, '' as b_Quantity, '' as b_prices, '' as b_Quantity, \n "+
			"  '' as  inventoryid, '' as  a_Quantity, '' as  AuditUser, '' as  year, initbalance as  RemainQ, '' as  RemainPrice, '' as  month, '' as  InventoryEntryId, '' as  oldVoucherID,'' as  InvoiceId,'' as  VendorName,'' as  VchDate,'' as  FillUser,subject \n "+
		  	" from ( \n "+ 
			" 	select  \n "+ 
			" 	subjectid as subject,accname,subjectfullname2 as subjectfullname, DataName,\n "+ 
			" 	sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as balance,  \n "+ 
			" 	sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0))  as initbalance, \n  "+ 
			" 	sum(debitocc) as debitocc,sum(creditocc) as creditocc \n "+ 
			" 	from c_accountall  \n "+ 
			" 	where 1=1 \n " +
			"	and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') \n "+
			sqlNotSubject + 
			" 	and subyearmonth*12+submonth>=${StartYearMonth}  \n "+ 
			" 	and subyearmonth*12+submonth<=${EndYearMonth}  \n "+ 
			" 	and accsign=2 and isleaf1=1  \n "+ 
			" 	group by subjectid,DataName \n  "+ 
			" ) a right join ( \n " +
			"	select a.* , \n " +
			
			" 	(ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype}) * a.rectifySign as initsov, \n " +		
			" 	(ifnull(debittotalocc1,0) - ifnull(credittotalocc1,0))  * (${ptype}) * a.rectifySign as sov1, \n " +
			" 	(ifnull(debittotalocc2,0) - ifnull(credittotalocc2,0))  * (${ptype}) * a.rectifySign as sov2, \n " +
			" 	((ifnull(debittotalocc1,0) - ifnull(credittotalocc1,0)) + (ifnull(debittotalocc2,0) - ifnull(credittotalocc2,0)) ) * (${ptype}) * a.rectifySign as sov3, \n " +
			" 	ifnull(debittotalocc1,0) * a.rectifySign as debitsov1, \n " +
			" 	ifnull(credittotalocc1,0) * a.rectifySign as creditsov1, \n " +
			" 	ifnull(debittotalocc2,0) * a.rectifySign as debitsov2, \n " +
			" 	ifnull(credittotalocc2,0) * a.rectifySign as creditsov2, \n " +
			
			" 	(ifnull(debittotalocc1,0)+ifnull(debittotalocc2,0)) * a.rectifySign as debitsov3, \n " +
			" 	(ifnull(credittotalocc1,0)+ifnull(debittotalocc2,0)) * a.rectifySign as creditsov3, \n " +
			" 	(ifnull(debittotalocc1,0)+ifnull(debittotalocc4,0)+ifnull(debittotalocc6,0)) * a.rectifySign as debitsov4, \n " +
			" 	(ifnull(credittotalocc1,0)+ifnull(credittotalocc4,0)+ifnull(credittotalocc6,0)) * a.rectifySign as creditsov4, \n " +
			" 	(ifnull(debittotalocc2,0)+ifnull(debittotalocc5,0)) * a.rectifySign as debitsov5 , \n " +
			" 	(ifnull(credittotalocc2,0)+ifnull(credittotalocc5,0)) * a.rectifySign as creditsov5, \n " +
			
			" 	cnyinitbalance +(ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype}) * a.rectifySign as cnyinitbalance1, \n " +
			" 	cnybalance +( (ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype})  )  * a.rectifySign as cnybalance1, \n " +
			" 	cnybalance + ( (ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype}) + ((ifnull(debittotalocc1,0) - ifnull(credittotalocc1,0)) + (ifnull(debittotalocc2,0) - ifnull(credittotalocc2,0)) ) * (${ptype}) )  * a.rectifySign as cnysdbalance, \n " +
			"	cnydebitocc + (ifnull(debittotalocc1,0)+ifnull(debittotalocc2,0)) * a.rectifySign as sddebittotalocc,\n " +
			"	cnycreditocc + (ifnull(credittotalocc1,0)+ifnull(credittotalocc2,0)) * a.rectifySign as sdcredittotalocc, \n " + 
			"	cnyDebitBalance + (ifnull(debittotalocc1,0)+ifnull(debittotalocc4,0)+ifnull(debittotalocc6,0)+ifnull(debittotalocc2,0)+ifnull(debittotalocc5,0)) * a.rectifySign as  cnysdDebitBalance, \n " +
			" 	cnyCreditBalance + (ifnull(credittotalocc1,0)+ifnull(credittotalocc4,0)+ifnull(credittotalocc6,0)+ifnull(credittotalocc2,0)+ifnull(credittotalocc5,0)) * a.rectifySign  as cnysdCreditBalance \n " + 
			
			"	from ( \n "+ 
			" 		select  \n "+ 
			" 		subjectid as cnysubjectid,accname as cnysubjectname,subjectfullname2 as cnysubjectfullname, \n "+ 
			" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as cnybalance,  \n "+ 
			" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as cnyDebitBalance,  \n "+ 
			" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},CreditBalance * (-1),0)) as cnyCreditBalance,  \n "+ 
			
			" 		sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0))  as cnyinitbalance,  \n "+ 
			" 		sum(debitocc) as cnydebitocc,sum(creditocc) as cnycreditocc,"+rectifySign+" as rectifySign \n "+ 
			" 		from c_account \n "+ 
			" 		where 1=1 \n " +
			sqlNotSubject + 
			"		and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')  \n "+ 
			" 		and subyearmonth*12+submonth>=${StartYearMonth}  \n "+ 
			" 		and subyearmonth*12+submonth<=${EndYearMonth}  \n "+ 
			" 		and isleaf1=1  \n "+ 
			" 		group by subjectid  \n " +
			"	) a left join z_accountrectify  b on a.cnysubjectid = b.subjectid and b.projectid = ${projectid} and b.isleaf=1  \n "+ 
			" ) b on a.subject = b.cnysubjectid \n "+ 
			
			" union "+
			
			 "select dataname as uomunit,balance as BalanceQ,cnybalance as BalanceF,debitocc as DebitOccQ,cnydebitocc as DebitOccF,creditocc as CreditOccQ,cnycreditocc as CreditOccF,debitsov3 as debitsov,creditsov3 as creditsov,ifnull(cnybalance,0)+ifnull(debitsov3,0)-ifnull(creditsov3,0) as endoccurvalue,ifnull(AssItemname,cnysubjectname) as InventoryNameType, \n " +
				" '' as DebitOccPrice,'' as KeepUser,cnyinitbalance as RemainF,'' as BalancePrice,'' as InventoryType,ifnull(AssItemname,cnysubjectname) as InventoryName, \n "+
				" '' as  InventoryDate, '' as CreditOccPrice, substr(ifnull(SubjectFullName,cnysubjectfullname),1,if(locate('/',ifnull(SubjectFullName,cnysubjectfullname)) = 0,length(ifnull(SubjectFullName,cnysubjectfullname)),locate('/',ifnull(SubjectFullName,cnysubjectfullname))-1)) as Inventorytype1, '' as a_prices, '' as a_OccurValue, '' as b_Quantity, '' as b_prices, '' as b_Quantity, \n "+
				"  '' as  inventoryid, '' as  a_Quantity, '' as  AuditUser, '' as  year, initbalance as  RemainQ, cnyinitbalance1 as  RemainPrice, '' as  month, '' as  InventoryEntryId, '' as  oldVoucherID,'' as  InvoiceId,'' as  VendorName,'' as  VchDate,'' as  FillUser,subject \n "+
			  	" from ( \n "+ 
				" 	select  \n "+ 
				" 	accid as subject,AssItemID,AssItemname,AssTotalName1 as subjectfullname, DataName,\n "+ 
				" 	sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as balance,  \n "+ 
				" 	sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0))  as initbalance, \n  "+ 
				" 	sum(debitocc) as debitocc,sum(creditocc) as creditocc \n "+ 
				" 	from c_assitementryaccall  a \n "+ 
				" 	where 1=1 \n " +
				"	and (accid like '"+subjectid+"%') \n "+ 
				sqlAssItem + 
				" 	and subyearmonth*12+submonth>=${StartYearMonth}  \n "+ 
				" 	and subyearmonth*12+submonth<=${EndYearMonth}  \n "+ 
				" 	and accsign=2 and isleaf1=1  \n "+ 
				" 	group by accid,AssItemID,DataName \n  "+ 
				" ) a right join ( \n " +
				"	select a.* , \n " +
				
				" 	(ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype}) * a.rectifySign as initsov, \n " +		
				" 	(ifnull(debittotalocc1,0) - ifnull(credittotalocc1,0))  * (${ptype}) * a.rectifySign as sov1, \n " +
				" 	(ifnull(debittotalocc2,0) - ifnull(credittotalocc2,0))  * (${ptype}) * a.rectifySign as sov2, \n " +
				" 	((ifnull(debittotalocc1,0) - ifnull(credittotalocc1,0)) + (ifnull(debittotalocc2,0) - ifnull(credittotalocc2,0)) ) * (${ptype}) * a.rectifySign as sov3, \n " +
				" 	ifnull(debittotalocc1,0) * a.rectifySign as debitsov1, \n " +
				" 	ifnull(credittotalocc1,0) * a.rectifySign as creditsov1, \n " +
				" 	ifnull(debittotalocc2,0) * a.rectifySign as debitsov2, \n " +
				" 	ifnull(credittotalocc2,0) * a.rectifySign as creditsov2, \n " +
				
				" 	(ifnull(debittotalocc1,0)+ifnull(debittotalocc2,0)) * a.rectifySign as debitsov3, \n " +
				" 	(ifnull(credittotalocc1,0)+ifnull(debittotalocc2,0)) * a.rectifySign as creditsov3, \n " +
				" 	(ifnull(debittotalocc1,0)+ifnull(debittotalocc4,0)+ifnull(debittotalocc6,0)) * a.rectifySign as debitsov4, \n " +
				" 	(ifnull(credittotalocc1,0)+ifnull(credittotalocc4,0)+ifnull(credittotalocc6,0)) * a.rectifySign as creditsov4, \n " +
				" 	(ifnull(debittotalocc2,0)+ifnull(debittotalocc5,0)) * a.rectifySign as debitsov5 , \n " +
				" 	(ifnull(credittotalocc2,0)+ifnull(credittotalocc5,0)) * a.rectifySign as creditsov5, \n " +
				
				" 	cnyinitbalance +(ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype}) * a.rectifySign as cnyinitbalance1, \n " +
				" 	cnybalance +( (ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype})  )  * a.rectifySign as cnybalance1, \n " +
				" 	cnybalance + ( (ifnull(debittotalocc4,0) - ifnull(credittotalocc4,0) + ifnull(debittotalocc5,0) - ifnull(credittotalocc5,0) + ifnull(debittotalocc6,0) - ifnull(credittotalocc6,0)) * (${ptype}) + ((ifnull(debittotalocc1,0) - ifnull(credittotalocc1,0)) + (ifnull(debittotalocc2,0) - ifnull(credittotalocc2,0)) ) * (${ptype}) )  * a.rectifySign as cnysdbalance, \n " +
				"	cnydebitocc + (ifnull(debittotalocc1,0)+ifnull(debittotalocc2,0)) * a.rectifySign as sddebittotalocc,\n " +
				"	cnycreditocc + (ifnull(credittotalocc1,0)+ifnull(credittotalocc2,0)) * a.rectifySign as sdcredittotalocc, \n " + 
				"	cnyDebitBalance + (ifnull(debittotalocc1,0)+ifnull(debittotalocc4,0)+ifnull(debittotalocc6,0)+ifnull(debittotalocc2,0)+ifnull(debittotalocc5,0)) * a.rectifySign as  cnysdDebitBalance, \n " +
				" 	cnyCreditBalance + (ifnull(credittotalocc1,0)+ifnull(credittotalocc4,0)+ifnull(credittotalocc6,0)+ifnull(credittotalocc2,0)+ifnull(credittotalocc5,0)) * a.rectifySign  as cnysdCreditBalance \n " + 
				
				"	from ( \n "+ 
				" 		select  \n "+ 
				" 		AccID as cnysubjectid,AssItemID,AssItemName as cnysubjectname,AssTotalName1 as cnysubjectfullname, \n "+ 
				" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as cnybalance,  \n "+ 
				" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as cnyDebitBalance,  \n "+ 
				" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},CreditBalance * (-1),0)) as cnyCreditBalance,  \n "+ 
				
				" 		sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0))  as cnyinitbalance,  \n "+ 
				" 		sum(debitocc) as cnydebitocc,sum(creditocc) as cnycreditocc,"+rectifySign+" as rectifySign \n "+ 
				" 		from c_assitementryacc a \n "+ 
				" 		where 1=1 \n " +
				sqlAssItem + 
				"		and (accid like '"+subjectid+"%')  \n "+ 
				" 		and subyearmonth*12+submonth>=${StartYearMonth}  \n "+ 
				" 		and subyearmonth*12+submonth<=${EndYearMonth}  \n "+ 
				" 		and isleaf1=1  \n "+ 
				" 		group by AccID,AssItemID  \n " +
				"	) a left join z_assitemaccrectify  b on a.cnysubjectid = b.subjectid and a.AssItemID = b.AssItemID and b.projectid = ${projectid}  \n "+ 
				" ) b on a.subject = b.cnysubjectid and a.AssItemID = b.AssItemID \n "+ 
				" where 1=1 \n "+ 
				" order by subject";
	} catch (Exception e) {
		e.printStackTrace();
		return "";
	} finally {
		DbUtil.close(rs);
		DbUtil.close(ps);
	}
	  
	  
	  
	  
	  
	  
	  

  }
  
}
