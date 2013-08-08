package com.matech.audit.service.rectify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.rectify.RectifyService;
import com.matech.audit.service.rectify.RectifyTable;
import com.matech.audit.service.rectify.SubjectEntryTable;
import com.matech.audit.service.rectify.VoucherTable;
import com.matech.audit.service.rectify.model.AnalsyeRectify;
import com.matech.audit.work.repair.Repair;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SaveFromUrl {
	private String proid;
	private String pkgid;
	private String tempTable;
	private String userid;
	private String username;
	private HttpServletRequest request;
	private Connection conn;
	
	ASFuntion CHF = new ASFuntion();
	
	public SaveFromUrl(Connection conn, String proid, String pkgid,
			String tempTable, String userid, String username,
			HttpServletRequest request) {
		this.conn=conn;
		this.proid = proid; // 项目ID
		this.pkgid = pkgid; // 帐套编号
		this.tempTable = tempTable; // 临时表名称
		
		this.userid = userid; // 帐套编号
		this.username = username; // 临时表名称
		
		this.request = request; // 网页信息
	}
	
	public String getResult() throws Exception {
		String result = "";
		
		try{
			if(this.tempTable.toLowerCase().equals("t_subjectentryrectify".toLowerCase())){
				//调整EXCEL导入
				result = setRectify();
				
			}
			
			
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 调整EXCEL导入
	 * @return
	 * @throws Exception
	 */
	private String setRectify() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try{
			//rectype 为空，默认为“调整”
			sql = "update t_subjectentryrectify set rectype='调整' where ProjectID = ? and rectype = '' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.proid);
			ps.execute();
			DbUtil.close(ps);
			
			//修改标志
			sql = "update t_subjectentryrectify set rectype='负值重分类' where ProjectID = ? and (standdirection =1 or standdirection = -1) and rectype = '重分类' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.proid);
			ps.execute();
			DbUtil.close(ps);
			
			String result = "";

//			Project project = new ProjectService(conn).getProjectById(proid);	//当前项目
			
			int year = "".equals(this.pkgid) ? 0 : Integer.parseInt(this.pkgid.substring(6));
			
			RectifyService vm = new RectifyService(conn);
			
			VoucherTable vt = new VoucherTable();
			SubjectEntryTable set = new SubjectEntryTable();
			RectifyTable rt = new RectifyTable();
			
			sql = "select distinct VoucherID from z_subjectentryrectify " +
			"	where 1=1 " +
//			"	and bankid like '%EXCEL导入%'" +
			"	and ProjectID = ? " ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.proid);
			rs = ps.executeQuery();
			while(rs.next()){
				vm.delVoucher(rs.getString(1), this.pkgid);
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
			
            setSubject();	//新增科目和核算　修改t_subjectentryrectify表的subjectcode和assitemcode
            
			vt.setAccpackageid(pkgid);
//			vt.setFilluser(username);
			vt.setFilluser("");
			vt.setProjectID(proid);
			vt.setTypeid("调");
			vt.setAudituser("");
		    vt.setKeepuser("");
		    vt.setDirector("");
		    vt.setAffixcount(1);
		    vt.setDoubtuserid("");

			set.setAccpackageid(pkgid);
			set.setProjectID(proid);
			set.setTypeid("调");
			set.setCurrrate(0);
		    set.setCurrvalue(0);
		    set.setCurrency("");
		    set.setQuantity(0);
		    set.setUnitprice(0);
		    set.setBankid("");

			rt.setAccpackageId(pkgid);
			rt.setProjectID(proid);
			
			
			sql = "select * from t_subjectentryrectify " +
			" where ProjectID = ? " +
			" and ifnull(VoucherID,'') <> ''" +
			" and abs(debitocc0) + abs(creditocc0) > 0" +
			" order by voucherid,recyear,rectype ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.proid);
			rs = ps.executeQuery();
			
			String VoucherID1 = "",recyear1 = "",type1 = "";
			int serail = 1;
			
			String VKeyId = "";
				
			while(rs.next()){
				
                String VoucherID = CHF.showNull(rs.getString("VoucherID"));                                    
                String Summary = CHF.showNull(rs.getString("Summary"));		//摘要 	
                String subjectid = CHF.showNull(rs.getString("subjectid"));                                       
                String subjectcode = CHF.showNull(rs.getString("subjectcode")); //科目　“编号|名称”
                String assitemid = CHF.showNull(rs.getString("assitemid"));                                       
                String assitemcode = CHF.showNull(rs.getString("assitemcode")); //核算　“编号|名称”                                   
                String debitocc0 = CHF.showNull(rs.getString("debitocc0"));                                   
                String creditocc0 = CHF.showNull(rs.getString("creditocc0"));                                  
                String recyear = CHF.showNull(rs.getString("recyear"));   // 0 为本年，-1 为上年                                          
                String rectype = CHF.showNull(rs.getString("rectype"));   //调整、重分类、不符未调
                
                String type = CHF.showNull(rs.getString("rectype"));   //调整、重分类、不符未调、负值重分类
                
                String standsubject = CHF.showNull(rs.getString("standsubject"));   //standsubject 标准科目
                
                String standdirection = CHF.showNull(rs.getString("standdirection"));    //科目方向
                
                if("调整".equals(rectype)){
                	rectype = "311";
                }else if("重分类".equals(rectype) || "负值重分类".equals(rectype)){
                	rectype = "411";
                }else if("不符未调".equals(rectype)){
                	rectype = "611";
                }else{
                	rectype = "311";
                }
                
                //recxm:调整细目；
                //curname:外币名称
                //debitocc1:调整外币借
                //creditocc1：调整外币贷
                String recxm = "",curname ="", debitocc1 = "",creditocc1 = "";
                try{
                	recxm = CHF.showNull(rs.getString("recxm")); 
                	curname = CHF.showNull(rs.getString("curname")); 
                	debitocc1 = CHF.showNull(rs.getString("debitocc1")); 
                	creditocc1 = CHF.showNull(rs.getString("creditocc1"));
                	System.out.println(recxm+"|"+curname+"|"+debitocc1+"|"+creditocc1);
                	if(!"".equals(curname)){
                		double rate = 0.00;
                    	
                    	set.setCurrency(curname); //外币名称
                    	if(Math.abs(Double.parseDouble(creditocc1)) > 0.00){
                    		set.setCurrvalue(Double.parseDouble(creditocc1)); //外币
                    		rate = Double.parseDouble(creditocc0) / Double.parseDouble(creditocc1);
                    	}else{
                    		set.setCurrvalue(Double.parseDouble(debitocc1)); //外币
                    		rate = Double.parseDouble(debitocc0) / Double.parseDouble(debitocc1);
                    	}
                    	set.setCurrrate(rate); //汇率
                	}else{
                		set.setCurrency("");
                		set.setCurrvalue(0.00);
                		set.setCurrrate(0.00);
                	}
                	
                	set.setItemtype(recxm); //调整细目
                	
                	if("客户不同意".equals(recxm) || "明显微小错报".equals(recxm)){
                		//调整.客户不同意 + 重分类.客户不同意 = 不符未调
                		if("311".equals(rectype)){
                			//调整
                			rectype = "611";
                		}else if("411".equals(rectype)){
                			//重分类
                			rectype = "711";
                		}
                	}else if("帐表差异".equals(recxm)){
                		if("311".equals(rectype)){
                			//调整
                			rectype = "831";
                		}else if("411".equals(rectype)){
                			//重分类
                			rectype = "841";
                		}
                	}
                	
                }catch(Exception e){}
                
                vt.setProperty(rectype);
                set.setProperty(rectype);
                
                int year1 = "".equals(recyear) ? 0 : Integer.parseInt(recyear);
                
                vt.setVchdate(String.valueOf(year + year1) + "-12-31");
                set.setVchdate(String.valueOf(year + year1) + "-12-31");
                
                if(!(VoucherID1.equals(VoucherID) && recyear1.equals(recyear) && type1.equals(type))){
                	VoucherID1 = VoucherID;
                	recyear1 = recyear;
                	type1 = type;
                	serail = 1;
                	
                	VKeyId = new DELAutocode().getAutoCode("AUVO","");
                	vt.setAutoid(Integer.parseInt(VKeyId));
                	
    				String vid = new DELAutocode().getAutoCode("VOID","");
    				vt.setVoucherid(Integer.parseInt(vid));
    				vt.setDescription(""); //备注
    				
    				vm.AddOrModifyVoucher(vt,"ad"); //增加调整表    	
                }
                
                String SUId = new DELAutocode().getAutoCode("SUAU","");
				set.setAutoid(Integer.parseInt(SUId));
				set.setVoucherid(Integer.parseInt(VKeyId));
				set.setSerail(serail++);
				set.setBankid("EXCEL导入");
				
				set.setSummary(Summary);//摘要	
				
				if(Math.abs(Double.parseDouble(creditocc0)) > 0.00){
					set.setOccurvalue(Double.parseDouble(creditocc0));
					set.setDirction(-1);	
				}else{
					set.setOccurvalue(Double.parseDouble(debitocc0));
					set.setDirction(1);	
				}
					
				/**
				 * 解析科目编号、核算编号
				 */
				if("".equals(subjectid)){
					String [] subject = subjectcode.split("\\|");
					subjectid = "".equals(subject[0].trim()) ? "" : subject[0].trim(); 
				}
			 
				if("".equals(assitemid)){
					String [] assitem = assitemcode.split("\\|");
					assitemid = "".equals(assitem[0].trim()) ? "" : assitem[0].trim(); 
				}
				
				String str = vm.getSCurrency(this.pkgid,subjectid);
				if(result.indexOf(str) == -1){
					result += str;
				}
				
				set.setSubjectid(subjectid);
				
				if(!"".equals(assitemid.trim())){
					rt.setEntryId(SUId);	
					rt.setAssitemId(assitemid);
					rt.setSubjectId(subjectid);
					vm.AddRectify(rt,"ad");	
				}else{
					rt.setEntryId("");	
					rt.setAssitemId("");
					rt.setSubjectId("");
				}
				
				vm.AddOrModifySubjectEntry(set,"ad"); //增加调整分录表
				
				/**
				 * 账龄
				 */
				AnalsyeRectify ar = new AnalsyeRectify() ;
				ar.setAutoId(String.valueOf(set.getAutoid())) ;
				ar.setProjectID(proid) ;
				ar.setSubjectID(set.getSubjectid()) ;
				ar.setAssItemID(CHF.showNull(rt.getAssitemId())) ;
				
				ar.setSubMonth("0");
				ar.setSubYearMonth(String.valueOf(year1)) ;
				
				ar.setAnalsyeBalance(String.valueOf(set.getOccurvalue())) ;
				ar.setDirection(set.getDirction());	//调整方向 
				
				ar.setDataName("0");	//本位币
				ar.setProperty(set.getProperty());
				vm.addAnalsyeRectify(pkgid,ar) ;
				
				
			}
			vm.updateAnalsyeRectify(pkgid, proid);
			new Repair(conn).deleteData(pkgid, proid);
			vm.createTzhz(pkgid,proid);
			vm.createWbTzhz(pkgid, proid);
			vm.createAssitem(pkgid,proid);
			
			return result;
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	/**
	 * 用于新增科目和核算
	 * 修改t_subjectentryrectify表的subjectcode和assitemcode
	 */
	public void setSubject() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String sql = "";
		try{
			
			String customerid=pkgid.substring(0,6);
	        
	        String vocationid = new CustomerService(conn).getCustomer(customerid).getVocationId();		//会计制度
	        RectifyService vm = new RectifyService(conn);
	        
	        /**
	         * 这是比较名称的
	         */
	        System.out.println("1:"+CHF.getCurrentTime());
	        sql = "update t_subjectentryrectify a,( \n" +
	        "	select a.*, \n" +
	        "	if(ifnull(b.accid,'') = '','0','1') as isassitem \n" + 
			"	from (  \n" +
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property,  \n" +
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname ,ifnull(subjectfullname2,subjectfullname) as subjectfullname2 \n" +
			"		from c_accpkgsubject a   \n" +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.subjectid = b.subjectid \n" +  
			"		where a.accpackageid='"+pkgid+"' \n" +   
											
			"		union   \n" +
											
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, \n" + 
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname  ,ifnull(b.subjectfullname2,subjectfullname) as subjectfullname2  \n" +
			"		from z_usesubject a " +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.tipsubjectid = b.subjectid \n" +
			"		where projectid = "+proid+" \n" +  
					
			"	) a left join (  \n" +
			"		select distinct accid  \n" +  
			"		from c_assitementryacc a " +
			"		where 1=1 \n" +
			"		and a.accpackageid='"+pkgid+"' and a.submonth = 1 \n" +   
			"	) b on a.subjectid = b.accid \n" + 
			
			"	union   \n" +
										
			"	select distinct a.accid ,a.assitemid,a.AssItemName,a.AssTotalName1,a.isleaf1,a.Level1, \n" +
			"	if(a.direction = -1,'02','01') as Property,  \n" +
			"	concat(subjectid,'|',AccName) as sname,concat(assitemid,'|',AssItemName) as aname,subjectfullname2,'0' \n" + 
			"	from c_assitementryacc a ,c_account  b  \n" +
			"	where 1=1  \n" +
			
			"	and b.accpackageid='"+pkgid+"' \n" +
			"	and b.submonth = 1 \n" +
			"	and a.accpackageid='"+pkgid+"' \n" +
			"	and a.isleaf1 = 1 \n" +
			"	and a.submonth = 1 \n" +
			"	and a.accid = b.subjectid \n" + 
			 
			
			") b  \n" +
			"set a.subjectcode = b.sname, \n" +
			"a.assitemcode=aname \n" +
			"where  a.ProjectID = '"+this.proid+"' " +
			"and (b.subjectfullname2 = a.standsubject or b.subjectfullname2 like concat(a.standsubject,'/%')) \n" +
			"and b.isleaf = 1 \n" + //因为TB调整有可能会调到非叶子科目 
			"and a.subjectname = b.subjectname";
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps); 
	        System.out.println("2:"+CHF.getCurrentTime());
	        /**
	         * 这是比较全路径的
	         */
	        sql = "update t_subjectentryrectify a,( \n" +
	        "	select a.*, \n" +
	        "	if(ifnull(b.accid,'') = '','0','1') as isassitem, \n" +
	        "	subjectfullname as fullname " + 
			"	from (  \n" +
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property,  \n" +
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname ,ifnull(subjectfullname2,subjectfullname) as subjectfullname2 \n" +
			"		from c_accpkgsubject a   \n" +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.subjectid = b.subjectid \n" +  
			"		where a.accpackageid='"+pkgid+"' \n" +   
											
			"		union   \n" +
											
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, \n" + 
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname  ,ifnull(b.subjectfullname2,subjectfullname) as subjectfullname2  \n" +
			"		from z_usesubject a " +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.tipsubjectid = b.subjectid \n" +
			"		where projectid = "+proid+" \n" +    
					
			"	) a left join (  \n" +
			"		select distinct accid  \n" +  
			"		from c_assitementryacc a " +
			"		where 1=1 \n" +
			"		and a.accpackageid='"+pkgid+"' and a.submonth = 1 \n" +   
			"	) b on a.subjectid = b.accid \n" + 
			
			"	union   \n" +
										
			"	select distinct a.accid ,a.assitemid,a.AssItemName,a.AssTotalName1,a.isleaf1,a.Level1, \n" +
			"	if(a.direction = -1,'02','01') as Property,  \n" +
			"	concat(subjectid,'|',AccName) as sname,concat(assitemid,'|',AssItemName) as aname,subjectfullname2,'0', \n" +
			"	concat(subjectfullname1,'/',AssItemName) as fullname " + 
			"	from c_assitementryacc a ,c_account  b  \n" +
			"	where 1=1  \n" +
			
			"	and b.accpackageid='"+pkgid+"' \n" +
			"	and b.submonth = 1 \n" +
			"	and a.accpackageid='"+pkgid+"' \n" +
			"	and a.isleaf1 = 1 \n" +
			"	and a.submonth = 1 \n" +
			"	and a.accid = b.subjectid \n" + 
			
			
			") b  \n" +
			"set a.subjectcode = b.sname, \n" +
			"a.assitemcode=aname \n" +
			"where  a.ProjectID = '"+this.proid+"' " +
			/**
			 * "and (b.subjectfullname2 = a.standsubject or b.subjectfullname2 like concat(a.standsubject,'/%')) \n" +
			 * 不知为什么在1.8版本上注释掉了;
			 * 
			 * 开放原因：罗宁硅酮2007年审项目
			 * TB表:
			 * 	借： 标准科目：应付职工薪酬，科目/核算名称：应付工资，金额：1945
			 *  贷： 标准科目：主营业务成本，科目/核算名称：生产成本，金额：1945
			 *  注：【主营业务成本】和【生产成本】都可以在标准科目中找到
			 *  因为没有比较标准科目,结果贷方的一级科目变为了【生产成本】
			 *  改动：把比较标准科目的SQL开放
			 */
			"and (b.subjectfullname2 = a.standsubject or b.subjectfullname2 like concat(a.standsubject,'/%')) \n" + 
			"and a.subjectname = b.fullname " +
			"and ifnull(a.subjectcode,'') = '' ";
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps);
	        System.out.println("3:"+CHF.getCurrentTime());
	        
	        //多两条SQL，standsubject 是用户科目
	        sql = "update t_subjectentryrectify a,( \n" +
	        "	select a.*, \n" +
	        "	if(ifnull(b.accid,'') = '','0','1') as isassitem, \n" + 
	        "	subjectfullname as fullname " + 
			"	from (  \n" +
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property,  \n" +
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname ,ifnull(subjectfullname2,subjectfullname) as subjectfullname2 \n" +
			"		from c_accpkgsubject a   \n" +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.subjectid = b.subjectid \n" +  
			"		where a.accpackageid='"+pkgid+"' \n" +   
											
			"		union   \n" +
											
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, \n" + 
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname  ,ifnull(b.subjectfullname2,subjectfullname) as subjectfullname2  \n" +
			"		from z_usesubject a " +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.tipsubjectid = b.subjectid \n" +
			"		where projectid = "+proid+" \n" +  
					
			"	) a left join (  \n" +
			"		select distinct accid  \n" +  
			"		from c_assitementryacc a " +
			"		where 1=1 \n" +
			"		and a.accpackageid='"+pkgid+"' and a.submonth = 1 \n" +   
			"	) b on a.subjectid = b.accid \n" + 
			
			"	union   \n" +
										
			"	select distinct a.accid ,a.assitemid,a.AssItemName,a.AssTotalName1,a.isleaf1,a.Level1, \n" +
			"	if(a.direction = -1,'02','01') as Property,  \n" +
			"	concat(subjectid,'|',AccName) as sname,concat(assitemid,'|',AssItemName) as aname,subjectfullname2,'0', \n" +
			"	concat(subjectfullname1,'/',AssItemName) as fullname " + 
			"	from c_assitementryacc a ,c_account  b  \n" +
			"	where 1=1  \n" +
			
			"	and b.accpackageid='"+pkgid+"' \n" +
			"	and b.submonth = 1 \n" +
			"	and a.accpackageid='"+pkgid+"' \n" +
			"	and a.isleaf1 = 1 \n" +
			"	and a.submonth = 1 \n" +
			"	and a.accid = b.subjectid \n" + 
			 
			
			") b  \n" +
			"set a.subjectcode = b.sname, \n" +
			"a.assitemcode=aname \n" +
			"where  a.ProjectID = '"+this.proid+"' " +
			"and (b.fullname = a.standsubject or b.fullname like concat(a.standsubject,'/%')) \n" +
			"and b.isleaf = 1 \n" + //因为TB调整有可能会调到非叶子科目 
			"and a.subjectname = b.subjectname " +
			"and ifnull(a.subjectcode,'') = '' ";
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps); 
	        System.out.println("2:"+CHF.getCurrentTime());
	        /**
	         * 这是比较全路径的
	         */
	        sql = "update t_subjectentryrectify a,( \n" +
	        "	select a.*, \n" +
	        "	if(ifnull(b.accid,'') = '','0','1') as isassitem, \n" +
	        "	subjectfullname as fullname " + 
			"	from (  \n" +
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property,  \n" +
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname ,ifnull(subjectfullname2,subjectfullname) as subjectfullname2 \n" +
			"		from c_accpkgsubject a   \n" +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.subjectid = b.subjectid \n" +  
			"		where a.accpackageid='"+pkgid+"' \n" +   
											
			"		union   \n" +
											
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, \n" + 
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname  ,ifnull(b.subjectfullname2,subjectfullname) as subjectfullname2  \n" +
			"		from z_usesubject a " +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.tipsubjectid = b.subjectid \n" +
			"		where projectid = "+proid+" \n" +    
					
			"	) a left join (  \n" +
			"		select distinct accid  \n" +  
			"		from c_assitementryacc a " +
			"		where 1=1 \n" +
			"		and a.accpackageid='"+pkgid+"' and a.submonth = 1 \n" +   
			"	) b on a.subjectid = b.accid \n" + 
			
			"	union   \n" +
										
			"	select distinct a.accid ,a.assitemid,a.AssItemName,a.AssTotalName1,a.isleaf1,a.Level1, \n" +
			"	if(a.direction = -1,'02','01') as Property,  \n" +
			"	concat(subjectid,'|',AccName) as sname,concat(assitemid,'|',AssItemName) as aname,subjectfullname2,'0', \n" +
			"	concat(subjectfullname1,'/',AssItemName) as fullname " + 
			"	from c_assitementryacc a ,c_account  b  \n" +
			"	where 1=1  \n" +
			
			"	and b.accpackageid='"+pkgid+"' \n" +
			"	and b.submonth = 1 \n" +
			"	and a.accpackageid='"+pkgid+"' \n" +
			"	and a.isleaf1 = 1 \n" +
			"	and a.submonth = 1 \n" +
			"	and a.accid = b.subjectid \n" + 
			
			
			") b  \n" +
			"set a.subjectcode = b.sname, \n" +
			"a.assitemcode=aname \n" +
			"where  a.ProjectID = '"+this.proid+"' " +
			"and (b.fullname = a.standsubject or b.fullname like concat(a.standsubject,'/%')) \n" + 
			"and a.subjectname = b.fullname " +
			"and ifnull(a.subjectcode,'') = '' ";
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps);
	        System.out.println("3:"+CHF.getCurrentTime());
	        
	        sql = "DROP TABLE IF EXISTS t1_subjectentryrectify ";
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps);
	        
	        sql = "create table t1_subjectentryrectify " +
	        "select * from  t_subjectentryrectify a " +
	        "where projectid = " + proid + " and ifnull(a.subjectcode,'') <> ''  "; 
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps);
	        
	        sql = "delete from t_subjectentryrectify  " +
	        "where projectid = " + proid + " and ifnull(subjectcode,'') <> ''  "; 
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps);
	        
			/**
			 * 判断标准科目是否存在
			 * 是：判断用户科目是否存在
			 * 否：新增一级用户科目
			 */
			sql = "select distinct a.subjectname as subjectcode, a.standsubject,b.subjectid as standid,b.subjectname,b.subjectfullname,b.level0, \n" +
			"	case b.property when 2 then -1 else b.property end as direction2 \n" +
			"	from t_subjectentryrectify a \n" +
			"	left join asdb.k_standsubject b \n" +
			"	on b.vocationid = "+vocationid+" \n" +
			"	and a.standsubject = b.subjectfullname \n" +
			"	where a.projectid = " + proid + 
			"	and ifnull(a.subjectcode,'') = '' ";
			System.out.println("1:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){	
				//用于修改t_subjectentryrectify表的subjectcode和assitemcode，并且新增科目和核算
				String subjectid0 = "";
				String subjectname0 = "";
				String assitemid0 = "";
				String assitemname0 = "";
				
				String sname0 = "";
				String aname0 = "";
				
				String subjectcode = CHF.showNull(rs.getString("subjectcode"));		//subjectname
				String standsubject = CHF.showNull(rs.getString("standsubject"));
				
				String standid = CHF.showNull(rs.getString("standid"));
				String subjectname = CHF.showNull(rs.getString("subjectname"));
				String subjectfullname = CHF.showNull(rs.getString("subjectfullname"));
				String direction2 = CHF.showNull(rs.getString("direction2"));
				String level0 = CHF.showNull(rs.getString("level0"));
				
				//System.out.println(subjectfullname+"|"+standsubject+"|"+subjectcode);
				String [] sqlit = subjectcode.split("/");
				if(sqlit.length == 1){	//单科目名称
					//System.out.println("＝＝＝＝＝＝＝＝1＝＝＝＝＝＝＝＝＝");
					setSubject( subjectfullname, standsubject, subjectcode);
				}else{	//科目全路径
					//System.out.println("＝＝＝＝＝＝＝＝2＝＝＝＝＝＝＝＝＝");
					setSubject( subjectfullname, standsubject, subjectcode, sqlit);
				}
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
			
			/**
			 * 修改非叶子的科目和叶子科目有核算
			 */
            sql = "select distinct a.subjectname,a.standsubject,b.subjectfullname2,IsLeaf,Level0,isassitem " +
            "	from t_subjectentryrectify a,( \n" +
	        "	select a.*, \n" +
	        "	if(ifnull(b.accid,'') = '','0','1') as isassitem \n" + 
			"	from (  \n" +
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property,  \n" +
			"		concat(a.subjectid,'|',subjectname) as sname,'' as aname ,ifnull(subjectfullname2,subjectfullname) as subjectfullname2 \n" +
			"		from c_accpkgsubject a   \n" +
			"		left join c_account b  \n" +
			"		on a.accpackageid='"+pkgid+"'   and b.accpackageid='"+pkgid+"' \n" +   
			"		and b.submonth = 1 and a.subjectid = b.subjectid \n" +  
			"		where a.accpackageid='"+pkgid+"' \n" +   
											
			"		union   \n" +
											
			"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, \n" + 
			"		concat(subjectid,'|',subjectname) as sname,'' as aname  ,subjectfullname \n" +
			"		from z_usesubject a where projectid = "+proid+" \n" +   
					
			"	) a left join (  \n" +
			"		select distinct accid ,tokenid as tids \n" +  
			"		from c_assitementryacc a ,(  \n" +
			"			select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" + 
			"			where 1=1   \n" +
			"			and a.accpackageid='"+pkgid+"' \n" +   
					
			"		) b  \n" +
			"		where 1=1  \n" +
			"		and a.accid = b.subjectid  \n" +
			"	) b on a.subjectid = b.accid \n" + 
			
			"	union   \n" +
										
			"	select distinct a.accid ,a.assitemid,a.AssItemName,a.AssTotalName1,isleaf1,Level1, \n" +
			"	if(direction = -1,'02','01') as Property,  \n" +
			"	concat(subjectid,'|',AccName) as sname,concat(assitemid,'|',AssItemName) as aname,subjectfullname2,'0' \n" + 
			"	from c_assitementryacc a ,(  \n" +
			"		select distinct accpackageid,subjectid,AccName,tokenid,subjectfullname2  from c_account a \n" + 
			"		where 1=1   \n" +
			"		and a.accpackageid='"+pkgid+"' \n" +   
					
			"	) b  \n" +
			"	where 1=1  \n" +
			"	and a.accpackageid='"+pkgid+"' \n" +   
			"	and a.accid = b.subjectid \n" + 
			"	and isleaf1 = 1 \n" + 
			
			") b  \n" +
			"where a.ProjectID = '"+this.proid+"' and a.subjectcode = b.sname and a.assitemcode = b.aname \n" +
			"and (isleaf = 0 or (isleaf = 1 and isassitem = 1))";
	        ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
            while(rs.next()){
            	String subjectname = CHF.showNull(rs.getString("subjectname"));		//subjectname
				String standsubject = CHF.showNull(rs.getString("standsubject"));
				
				String subjectfullname2 = CHF.showNull(rs.getString("subjectfullname2"));
				setSubject( subjectfullname2, standsubject, subjectname);
				
            }
            
            sql = "insert into t_subjectentryrectify " +
            "select * " +
            "from t1_subjectentryrectify ";
            ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps); 
	        
	        sql = "DROP TABLE IF EXISTS t1_subjectentryrectify ";
	        ps = conn.prepareStatement(sql);
	        ps.execute();
	        DbUtil.close(ps);
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	public void setSubject(String subjectfullname,String standsubject,String subjectcode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String sql = "";
		try {
			RectifyService vm = new RectifyService(conn);
			
			String subjectid0 = "";
			String subjectname0 = "";
			String assitemid0 = "";
			String assitemname0 = "";
			
			String sname0 = "";
			String aname0 = "";
			
			if("".equals(subjectfullname)){
				subjectfullname = standsubject;
			}
			
			
			if(!"".equals(subjectfullname)){
				//会计制度 中有标准科目
				/**
				 * 判断用户科目是否存在这个标准科目
				 * 是：判断用户科目下级科目或核算是否有和subjectcode同名的存在
				 * 否：新增一级用户科目
				 */
				sql = " select * from ( \n" +
				
				" 	select a.SubjectID as standid,AccName as subjectname,SubjectFullName1,subjectfullname2,direction2,level1,isleaf1, \n" +
				"	if(ifnull(b.accid,'') = '','0','1') as isassitem " +
				" 	from c_account a left join ( \n" +
				"		select distinct accid " +
				"		from c_assitementryacc a " +
				" 		where accpackageid = '"+pkgid+"' \n" +
				" 		and submonth = 1 \n" +
				"	) b on a.SubjectID = b.accid " +
				" 	where accpackageid = '"+pkgid+"' \n" +
				" 	and submonth = 1 \n" +

				" 	union  \n" +
		
				" 	select distinct a.SubjectID ,a.SubjectName,SubjectFullName, \n" +
				" 	case when c.level0=1 then concat(c.standkey,substring(a.subjectfullname,locate('/',a.subjectfullname))) when c.level0=2 then concat(c.standkey,'/',a.subjectfullname) else a.subjectfullname end as subjectfullname2, \n" +
				" 	case ifnull(c.property,substring(a.property,2,1)) when 2 then -1 else ifnull(c.property,substring(a.property,2,1)) end as direction2, \n" +
				" 	a.level0,a.isleaf,'0' \n" +
				" 	from ( \n" +
				" 		select a.* \n" +
				" 		from c_accpkgsubject a left join c_account b \n" +
				" 		on a.accpackageid = '"+pkgid+"'  \n" +
				" 		and b.accpackageid = '"+pkgid+"' and b.submonth = 1 \n" +
				" 		and a.subjectid = b.subjectid  \n" +
				" 		where b.accpackageid is null \n" +
				" 		and a.accpackageid = '"+pkgid+"'  \n" +
				" 		order by a.subjectid \n" +
				" 	) a \n" +
				" 	left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%')) \n" + 
		
				" 	union  \n" +
		
				" 	select a.subjectid,a.SubjectName,a.subjectfullname,a.subjectfullname, \n" +
				" 	case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,level0,isleaf,'0' \n" +
				" 	from z_usesubject a where projectid = "+proid+"  \n" +
		
				" ) a \n" +
				" where a.subjectfullname2 = '"+subjectfullname+"' and a.level1 = 1 ";
//				//System.out.println("2:"+sql);
				ps = conn.prepareStatement(sql);
				rs1 = ps.executeQuery();
				if(rs1.next()){
					
					String standid1 = CHF.showNull(rs1.getString("standid"));
					String subjectname1 = CHF.showNull(rs1.getString("subjectname"));
					String subjectfullname11 = CHF.showNull(rs1.getString("subjectfullname1"));	//用户科目全路径
					String subjectfullname21 = CHF.showNull(rs1.getString("subjectfullname2"));
					String direction21 = CHF.showNull(rs1.getString("direction2"));
					String level11 = CHF.showNull(rs1.getString("level1"));
					String isleaf11 = CHF.showNull(rs1.getString("isleaf1"));
					
					String isassitem1 = CHF.showNull(rs1.getString("isassitem"));	//本科目是否有核算
					
					if(!"".equals(standid1)){	
						//标准科目有对照的用户科目
						/**
						 * 判断用户[末级]科目/核算有没有这个名字(这里一定要注意末级，是末级)
						 * 是：就用科目或核算生成调整分录 　叶子，非叶子
						 * 否：判断是否是新增科目还是新增核算
						 */
						
						sql = "select * from ( \n" + 
						"	select a.*," +
						"	if(ifnull(b.accid,'') = '','0','1') as isassitem " +
						"	from ( \n" +
						"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, \n" +
						"		concat(subjectid,'|',subjectname) as sname,'' as aname \n" +
						"		from c_accpkgsubject a  \n" +
						"		where a.accpackageid='"+pkgid+"' \n" +  
						"		and (a.subjectfullname = '"+subjectfullname11+"' or a.subjectfullname like '"+subjectfullname11+"/%') \n" +
//						"		and isleaf = 1" +
								
						"		union \n" + 
								
						"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, " +
						"		concat(subjectid,'|',subjectname) as sname,'' as aname \n" + 
						"		from z_usesubject a where projectid = "+proid+"   \n" +
						"		and (a.subjectfullname = '"+subjectfullname11+"' or a.subjectfullname like '"+subjectfullname11+"/%') \n" +
//						"		and isleaf = 1 " +
							
						"	) a left join ( \n" +
						"		select distinct accid ,tokenid as tids \n" + 
						"		from c_assitementryacc a ,( \n" +
						"			select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +
						"			where 1=1  \n" +
						"			and a.accpackageid='"+pkgid+"' \n" +  
						"			and (a.subjectfullname1 like '"+subjectfullname11+"/%'  or a.subjectfullname1 = '"+subjectfullname11+"' ) \n" + 
						"		) b \n" +
						"		where 1=1 \n" +
						"		and a.accid = b.subjectid \n" +
						"	) b on a.subjectid = b.accid \n" +
//						"	where b.accid is null \n" +
							
						"	union  \n" +
							
						"	select distinct a.accid ,a.assitemid,a.AssItemName,a.AssTotalName1,isleaf1,Level1," +
						"	if(direction = -1,'02','01') as Property, \n" +
						"	concat(subjectid,'|',AccName) as sname,concat(assitemid,'|',AssItemName) as aname,'0' \n" +
						"	from c_assitementryacc a ,( \n" +
						"		select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +
						"		where 1=1  \n" +
						"		and a.accpackageid='"+pkgid+"' \n" +  
						"		and (a.subjectfullname1 like '"+subjectfullname11+"/%'  or a.subjectfullname1 = '"+subjectfullname11+"' ) \n" + 
						"	) b \n" +
						"	where 1=1 \n" +
						"	and a.accpackageid='"+pkgid+"' \n" +  
						"	and a.accid = b.subjectid \n" +
						"	and isleaf1 = 1 \n" +
						") a  \n" +
						"where a.subjectname = '"+subjectcode+"'";
//						//System.out.println("3:"+sql);
						
						ps = conn.prepareStatement(sql);
						rs2 = ps.executeQuery();
						if(rs2.next()){		//有用户科目（标准），但没有子科目
							//是：就用科目或核算生成调整分录 　叶子，非叶子
							/**
							 * 叶子　　就生成分录
							 * 非叶子　就在下级生成
							 */
							String subjectid2 = rs2.getString("subjectid");
							String assitemid2 = rs2.getString("assitemid"); 
							String subjectname2 = rs2.getString("subjectname"); 
							String subjectfullname2 = rs2.getString("subjectfullname"); 
							String IsLeaf2 = rs2.getString("IsLeaf"); 
							String Level02 = rs2.getString("Level0"); 
							String Property2 = rs2.getString("Property");
							String isassitem2 = rs2.getString("isassitem");
							
							
							String sname = rs2.getString("sname"); 
							String aname = rs2.getString("aname");
							
							if("1".equals(isassitem2)){	//subjectcode = subjectname2，但subjectname2有核算
								
								/**
								 * 1、科目一级，有核算，披露核算
								 * 3、科目一级，有核算，不披露核算
								 * 2、非一级科目，有核算，披露核算
								 * 4、非一级科目，有核算，不披露核算
								 * 
								 * 有核算披露就新增核算，没有就随机　
								 */
								sql = "select * from c_subjectassitem " +
								"	where accpackageid = '"+pkgid+"' " +
								"	and subjectid = '"+subjectid2+"' " +
								"	and ifequal = 0" +
								"	order by assitemid limit 1 ";
//								//System.out.println("41:"+sql);
								ps = conn.prepareStatement(sql);
								rs2 = ps.executeQuery();
								if(rs2.next()){		//有核算，披露核算
									String assitemid3 = rs2.getString("assitemid");
									
									assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, subjectcode, subjectid2);  //新增核算
									sname0  = subjectid2 + "|" + subjectname2;	//结果
									aname0  = assitemid3 + "|" + subjectcode;
									
								}else{	//有核算，不披露核算
									DbUtil.close(rs2);
						            DbUtil.close(ps);
									
						            sql = "select * from c_assitementryacc" +
						            "	where accpackageid = '"+pkgid+"' " +
									"	and accid = '"+subjectid2+"' " +
									"	and level1 = 1" +
									"	order by assitemid limit 1 ";
//						            //System.out.println("51:"+sql);
						            ps = conn.prepareStatement(sql);
									rs2 = ps.executeQuery();
									if(rs2.next()){		//有核算，披露核算
										String assitemid3 = rs2.getString("assitemid");
										
										assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, subjectcode, subjectid2);  //新增核算
										sname0  = subjectid2 + "|" + subjectname2;	//结果
										aname0  = assitemid3 + "|" + subjectcode;
										
									}
								}
								
								
								
							}else{//subjectcode = subjectname2，但subjectname2无核算
								if("1".equals(IsLeaf2)){
									sname0  = sname;	//结果
									aname0  = aname;	
								}else{
									subjectid0 = vm.insertData( pkgid,  proid,  subjectname2, subjectid2);
									sname0  = subjectid0 + "|" + subjectname2;	//结果
									aname0  = "";
								}
							}
						}else{
							//否：判断是否是新增科目还是新增核算
							
							DbUtil.close(rs2);
				            DbUtil.close(ps);
				            
							if("0".equals(isassitem1)){	//生成新增科目
								
								subjectid0 = vm.insertData( pkgid,  proid,  subjectcode, standid1);
								sname0  = subjectid0 + "|" + subjectcode;	//结果
								aname0  = "";
								
							}else{	//生成新增核算
								/**
								 * 1、科目一级，有核算，披露核算
								 * 3、科目一级，有核算，不披露核算
								 * 2、非一级科目，有核算，披露核算
								 * 4、非一级科目，有核算，不披露核算
								 * 
								 * 有核算披露就新增核算，没有就随机　
								 */
								sql = "select * from c_subjectassitem " +
								"	where accpackageid = '"+pkgid+"' " +
								"	and subjectid = '"+standid1+"' " +
								"	and ifequal = 0" +
								"	order by assitemid limit 1 ";
//								//System.out.println("4:"+sql);
								ps = conn.prepareStatement(sql);
								rs2 = ps.executeQuery();
								if(rs2.next()){		//有核算，披露核算
									String assitemid3 = rs2.getString("assitemid");
									
									assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, subjectcode, standid1);  //新增核算
									sname0  = standid1 + "|" + subjectname1;	//结果
									aname0  = assitemid3 + "|" + subjectcode;
									
								}else{	//有核算，不披露核算
									DbUtil.close(rs2);
						            DbUtil.close(ps);
									
						            sql = "select * from c_assitementryacc" +
						            "	where accpackageid = '"+pkgid+"' " +
									"	and accid = '"+standid1+"' " +
									"	and level1 = 1" +
									"	order by assitemid limit 1 ";
//						            //System.out.println("5:"+sql);
						            ps = conn.prepareStatement(sql);
									rs2 = ps.executeQuery();
									if(rs2.next()){		//有核算，披露核算
										String assitemid3 = rs2.getString("assitemid");
										
										assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, subjectcode, standid1);  //新增核算
										sname0  = standid1 + "|" + subjectname1;	//结果
										aname0  = assitemid3 + "|" + subjectcode;
										
									}
								}
								
								
							}
							
							
							
						}
						DbUtil.close(rs2);
			            DbUtil.close(ps);
						
						
					}else{
						//标准科目没有对照的用户科目
						/**
						 * 1、subjectcode 与　standsubject　相同，就新增此标准科目
						 * 2、subjectcode 与　standsubject　不同，就先增加stands	ubject1的标准科目，然后增加subjectcode1的科目
						 */
						if(subjectcode.equals(standsubject)){
							subjectid0 = vm.insertData( pkgid,  proid,  subjectcode, "");
							sname0  = subjectid0 + "|" + subjectcode;	//结果
							aname0  = "";
						}else{
							subjectid0 = vm.insertData( pkgid,  proid,  standsubject, "");
							subjectid0 = vm.insertData( pkgid,  proid,  subjectcode, subjectid0);
							sname0  = subjectid0 + "|" + subjectcode;	//结果
							aname0  = "";
						}
						
						
					}
					
				}else{
					//标准科目没有对照的用户科目
					/**
					 * 1、subjectcode 与　standsubject　相同，就新增此标准科目
					 * 2、subjectcode 与　standsubject　不同，就先增加standsubject1的标准科目，然后增加subjectcode1的科目
					 */
					if(subjectcode.equals(standsubject)){
						subjectid0 = vm.insertData( pkgid,  proid,  subjectcode, "");
						sname0  = subjectid0 + "|" + subjectcode;	//结果
						aname0  = "";
					}else{
						subjectid0 = vm.insertData( pkgid,  proid,  standsubject, "");
						subjectid0 = vm.insertData( pkgid,  proid,  subjectcode, subjectid0);
						sname0  = subjectid0 + "|" + subjectcode;	//结果
						aname0  = "";
					}
				}
				DbUtil.close(rs1);
	            DbUtil.close(ps);
				
				
				
				
			}else{//会计制度没有此标准科目
				/**
				 * 判断用户科目有没有此标准科目
				 */
				
				//会计制度　中没有这个标准科目,就新增一级
				subjectid0 = vm.insertData( pkgid,  proid,  subjectcode, "");		
				sname0  = subjectid0 + "|" + subjectcode;	//结果
				aname0  = "";
			}
			
			//System.out.println(sname0 + "|||" + aname0);
			
			/**
			 * 更新subjectcode和assitemcode的值　
			 */
			sql = "update t_subjectentryrectify " +
			"	set subjectcode = ?," +
			"	assitemcode = ? " +
			"	where projectid = ? " +
			"	and subjectname = ?" +
			"	and standsubject = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, sname0);
			ps.setString(2, aname0);
			
			ps.setString(3, proid);
			ps.setString(4, subjectcode);
			ps.setString(5, standsubject);
			
			ps.execute();
			DbUtil.close(ps);
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	
	public void setSubject(String subjectfullname,String standsubject,String subjectcode,String [] split) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String sql = "";
		try {
			
			RectifyService vm = new RectifyService(conn);
			
			String subjectid0 = "";
			String subjectname0 = "";
			String assitemid0 = "";
			String assitemname0 = "";
			
			String sname0 = "";
			String aname0 = "";
			
			String subjectcode1 = subjectcode;
			
			if(subjectcode.indexOf("/")>-1){
//				String string = subjectcode.substring(0,subjectcode.indexOf("/"));
//				if(standsubject.indexOf(string)>-1){
//					subjectcode  = standsubject + subjectcode.substring(subjectcode.indexOf("/"));
//				}
				subjectcode  = standsubject + subjectcode.substring(subjectcode.indexOf("/"));
			}
			
			String subject = subjectcode;
			
			for(int iSplit = split.length-1 ; iSplit >= 0 ; iSplit --){
				
				//System.out.println("iSplit=|"+iSplit);
				
				sql = " select * from ( \n" +
				
				" 	select a.SubjectID as standid,AccName as subjectname,SubjectFullName1,subjectfullname2,direction2,level1,isleaf1, \n" +
				"	if(ifnull(b.accid,'') = '','0','1') as isassitem " +
				" 	from c_account a left join ( \n" +
				"		select distinct accid " +
				"		from c_assitementryacc a " +
				" 		where accpackageid = '"+pkgid+"' \n" +
				" 		and submonth = 1 \n" +
				"	) b on a.SubjectID = b.accid " +
				" 	where accpackageid = '"+pkgid+"' \n" +
				" 	and submonth = 1 \n" +

				" 	union  \n" +
		
				" 	select distinct a.SubjectID ,a.SubjectName,SubjectFullName, \n" +
				" 	case when c.level0=1 then concat(c.standkey,substring(a.subjectfullname,locate('/',a.subjectfullname))) when c.level0=2 then concat(c.standkey,'/',a.subjectfullname) else a.subjectfullname end as subjectfullname2, \n" +
				" 	case ifnull(c.property,substring(a.property,2,1)) when 2 then -1 else ifnull(c.property,substring(a.property,2,1)) end as direction2, \n" +
				" 	a.level0,a.isleaf,'0' \n" +
				" 	from ( \n" +
				" 		select a.* \n" +
				" 		from c_accpkgsubject a left join c_account b \n" +
				" 		on a.accpackageid = '"+pkgid+"'  \n" +
				" 		and b.accpackageid = '"+pkgid+"' and b.submonth = 1 \n" +
				" 		and a.subjectid = b.subjectid  \n" +
				" 		where b.accpackageid is null \n" +
				" 		and a.accpackageid = '"+pkgid+"'  \n" +
				" 		order by a.subjectid \n" +
				" 	) a \n" +
				" 	left join z_keyresult c on c.standkey not like '%/%' and (a.subjectfullname=c.userkey  or a.subjectfullname like concat(c.userkey,'/','%')) \n" + 
		
				" 	union  \n" +
		
				" 	select a.subjectid,a.SubjectName,a.subjectfullname,a.subjectfullname, \n" +
				" 	case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction2,level0,isleaf,'0' \n" +
				" 	from z_usesubject a where projectid = "+proid+"  \n" +
		
				" ) a \n" +
				" where a.subjectfullname2 = '"+subject+"'  ";
				//System.out.println(sql);
				ps = conn.prepareStatement(sql);
				rs1 = ps.executeQuery();
				if(rs1.next()){
					
					String standid1 = CHF.showNull(rs1.getString("standid"));
					String subjectname1 = CHF.showNull(rs1.getString("subjectname"));
					String subjectfullname11 = CHF.showNull(rs1.getString("subjectfullname1"));	//用户科目全路径
					String subjectfullname21 = CHF.showNull(rs1.getString("subjectfullname2"));
					String direction21 = CHF.showNull(rs1.getString("direction2"));
					String level11 = CHF.showNull(rs1.getString("level1"));
					String isleaf11 = CHF.showNull(rs1.getString("isleaf1"));
					
					String isassitem1 = CHF.showNull(rs1.getString("isassitem"));	//本科目是否有核算
					
					if(subjectcode.equals(subjectfullname21)){
						
						if("1".equals(isleaf11)){	//全路径一样，并且是叶子科目
							if("1".equals(isassitem1)){		//叶子科目有核算
								
								sql = "select * from c_subjectassitem " +
								"	where accpackageid = '"+pkgid+"' " +
								"	and subjectid = '"+standid1+"' " +
								"	and ifequal = 0" +
								"	order by assitemid limit 1 ";
								ps = conn.prepareStatement(sql);
								rs2 = ps.executeQuery();
								if(rs2.next()){		//有核算，披露核算
									String assitemid3 = rs2.getString("assitemid");
									
									assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, split[split.length-1], standid1);  //新增核算
									sname0  = standid1 + "|" + subjectname1;	//结果
									aname0  = assitemid3 + "|" + split[split.length-1];
									
								}else{	//有核算，不披露核算
									DbUtil.close(rs2);
						            DbUtil.close(ps);
									
						            sql = "select * from c_assitementryacc" +
						            "	where accpackageid = '"+pkgid+"' " +
									"	and accid = '"+standid1+"' " +
									"	and level1 = 1" +
									"	order by assitemid limit 1 ";
						            //System.out.println("5:"+sql);
						            ps = conn.prepareStatement(sql);
									rs2 = ps.executeQuery();
									if(rs2.next()){		//有核算，披露核算
										String assitemid3 = rs2.getString("assitemid");
										
										assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, split[split.length-1], standid1);  //新增核算
										sname0  = standid1 + "|" + subjectname1;	//结果
										aname0  = assitemid3 + "|" + split[split.length-1];
										
									}
								}
								
							}else{	//叶子科目没有核算
								sname0 = standid1 + "|" + subjectname1;
								aname0 = "";
							}
							
						}else{	//全路径一样，但不是叶子科目
							
							sql = "select * from ( \n" + 
							"	select a.*," +
							"	if(ifnull(b.accid,'') = '','0','1') as isassitem " +
							"	from ( \n" +
							"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, \n" +
							"		concat(subjectid,'|',subjectname) as sname,'' as aname \n" +
							"		from c_accpkgsubject a  \n" +
							"		where a.accpackageid='"+pkgid+"' \n" +  
							"		and ( a.subjectfullname like '"+subjectfullname11+"/%') \n" +
									
							"		union \n" + 
									
							"		select a.subjectid,'' as assitemid,a.subjectname,a.subjectfullname,IsLeaf,Level0,Property, " +
							"		concat(subjectid,'|',subjectname) as sname,'' as aname \n" + 
							"		from z_usesubject a where projectid = "+proid+"   \n" +
							"		and ( a.subjectfullname like '"+subjectfullname11+"/%') \n" +
								
							"	) a left join ( \n" +
							"		select distinct accid ,tokenid as tids \n" + 
							"		from c_assitementryacc a ,( \n" +
							"			select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +
							"			where 1=1  \n" +
							"			and a.accpackageid='"+pkgid+"' \n" +  
							"			and (a.subjectfullname1 like '"+subjectfullname11+"/%'   ) \n" + 
							"		) b \n" +
							"		where 1=1 \n" +
							"		and a.accid = b.subjectid \n" +
							"	) b on a.subjectid = b.accid \n" +
								
							"	union  \n" +
								
							"	select distinct a.accid ,a.assitemid,a.AssItemName,a.AssTotalName1,isleaf1,Level1," +
							"	if(direction = -1,'02','01') as Property, \n" +
							"	concat(subjectid,'|',AccName) as sname,concat(assitemid,'|',AssItemName) as aname,'0' \n" +
							"	from c_assitementryacc a ,( \n" +
							"		select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +
							"		where 1=1  \n" +
							"		and a.accpackageid='"+pkgid+"' \n" +  
							"		and (a.subjectfullname1 like '"+subjectfullname11+"/%'  ) \n" + 
							"	) b \n" +
							"	where 1=1 \n" +
							"	and a.accpackageid='"+pkgid+"' \n" +  
							"	and a.accid = b.subjectid \n" +
							"	and isleaf1 = 1 \n" +
							") a  \n" +
							"where a.subjectname = '"+split[split.length-1]+"'";
							ps = conn.prepareStatement(sql);
							rs2 = ps.executeQuery();
							if(rs2.next()){		//表示下级有同名科目
								String subjectid2 = rs2.getString("subjectid");
								String assitemid2 = rs2.getString("assitemid"); 
								String subjectname2 = rs2.getString("subjectname"); 
								String subjectfullname2 = rs2.getString("subjectfullname"); 
								String IsLeaf2 = rs2.getString("IsLeaf"); 
								String Level02 = rs2.getString("Level0"); 
								String Property2 = rs2.getString("Property");
								String isassitem2 = rs2.getString("isassitem");
								
								String sname = rs2.getString("sname"); 
								String aname = rs2.getString("aname");
								
								if("1".equals(isassitem2)){		//表示同名科目是有核算的，就要新增到核算	
									
									sql = "select * from c_subjectassitem " +
									"	where accpackageid = '"+pkgid+"' " +
									"	and subjectid = '"+subjectid2+"' " +
									"	and ifequal = 0" +
									"	order by assitemid limit 1 ";
									ps = conn.prepareStatement(sql);
									rs2 = ps.executeQuery();
									if(rs2.next()){		//有核算，披露核算
										String assitemid3 = rs2.getString("assitemid");
										
										assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, split[split.length-1], subjectid2);  //新增核算
										sname0  = subjectid2 + "|" + subjectname2;	//结果
										aname0  = assitemid3 + "|" + split[split.length-1];
										
									}else{	//有核算，不披露核算
										DbUtil.close(rs2);
							            DbUtil.close(ps);
										
							            sql = "select * from c_assitementryacc" +
							            "	where accpackageid = '"+pkgid+"' " +
										"	and accid = '"+subjectid2+"' " +
										"	and level1 = 1" +
										"	order by assitemid limit 1 ";
							            //System.out.println("5:"+sql);
							            ps = conn.prepareStatement(sql);
										rs2 = ps.executeQuery();
										if(rs2.next()){		//有核算，披露核算
											String assitemid3 = rs2.getString("assitemid");
											
											assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, split[split.length-1], subjectid2);  //新增核算
											sname0  = subjectid2 + "|" + subjectname2;	//结果
											aname0  = assitemid3 + "|" + split[split.length-1];
											
										}
									}
									
									
								}else{	//表示同名科目没有核算，调到此科目上
									sname0 = sname;
									aname0 = aname;
								}
								
							}else{ //表示下级没有同名科目，就新增下级科目
								
								subjectid0 = vm.insertData( pkgid,  proid,  split[split.length-1], standid1);
								sname0  = subjectid0 + "|" + split[split.length-1];	//结果
								aname0  = "";
								
							}
							
						}
						
					}else{	//全路径不一样
						
						/**
						 * 全路径不一样：（iSqlit > 0）
						 * 1、科目是叶子且有核算，加核算
						 * 2、科目是叶子无核算，加下级科目
						 * 3、科目是非叶子，加下级科目
						 */
						if("1".equals(isassitem1)){	//1、科目是叶子且有核算，加核算
							
							String strSplit = "";
							for(int i = split.length-1; i > iSplit; i--){
								strSplit = split[i] + "-" + strSplit; 
							}
							if(!"".equals(strSplit)){
								strSplit = strSplit.substring(0, strSplit.length()-1);
							}
							//System.out.println("strSplit = |"+strSplit);
							
							sql = "select * from c_subjectassitem " +
							"	where accpackageid = '"+pkgid+"' " +
							"	and subjectid = '"+standid1+"' " +
							"	and ifequal = 0" +
							"	order by assitemid limit 1 ";
							ps = conn.prepareStatement(sql);
							rs2 = ps.executeQuery();
							if(rs2.next()){		//有核算，披露核算
								String assitemid3 = rs2.getString("assitemid");
								
								assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, strSplit, standid1);  //新增核算
								sname0  = standid1 + "|" + subjectname1;	//结果
								aname0  = assitemid3 + "|" + strSplit;
								
							}else{	//有核算，不披露核算
								DbUtil.close(rs2);
					            DbUtil.close(ps);
								
					            sql = "select * from c_assitementryacc" +
					            "	where accpackageid = '"+pkgid+"' " +
								"	and accid = '"+standid1+"' " +
								"	and level1 = 1" +
								"	order by assitemid limit 1 ";
					            ps = conn.prepareStatement(sql);
								rs2 = ps.executeQuery();
								if(rs2.next()){		//有核算，披露核算
									String assitemid3 = rs2.getString("assitemid");
									
									assitemid3 = vm.insertAssitem1( pkgid,  proid, assitemid3, strSplit, standid1);  //新增核算
									sname0  = standid1 + "|" + subjectname1;	//结果
									aname0  = assitemid3 + "|" + strSplit;
									
								}
							}
							
						}else{	
							//2、科目是叶子无核算，加科目
							//3、科目是非叶子，加下级科目
							subjectid0 = standid1;
							for(int i = iSplit + 1; i < split.length;i++){		//加多个下级
								String strSplit = split[i];
								subjectid0 = vm.insertData( pkgid,  proid,  strSplit, subjectid0);
								
							}
							sname0  = subjectid0 + "|" + split[split.length-1];	//结果
							aname0  = "";
							
						}
					}
					
					
					/**
					 * 更新subjectcode和assitemcode的值　
					 */
					sql = "update t_subjectentryrectify " +
					"	set subjectcode = ?," +
					"	assitemcode = ? " +
					"	where projectid = ? " +
					"	and subjectname = ?" +
					"	and standsubject = ? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1, sname0);
					ps.setString(2, aname0);
					
					ps.setString(3, proid);
					ps.setString(4, subjectcode1);
					ps.setString(5, standsubject);
					
					ps.execute();
					DbUtil.close(ps);
					
					break;
				}else{
					if(subject.lastIndexOf("/") > -1){
						subject = subject.substring(0,subject.lastIndexOf("/"));
					}
					continue;
				}
				
			}
			
			
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs2);
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	public static void main(String[] args) throws Exception{
		String subjectfullname = "管理费用";
		String standsubject = "管理费用";
		String subjectcode = "管理费用/管理费用（九）/技术开发费（DTMP项目）/新产品设计费";
		String []split = new String[]{"管理费用","管理费用（九）","技术开发费（DTMP项目）","新产品设计费"};
		
		DBConnect db= new DBConnect();
		Connection conn = db.getConnect("");
		db.changeDataBaseByProjectid(conn,"2011242122");
		
		//setSubject(String subjectfullname,String standsubject,String subjectcode,String [] split)
		SaveFromUrl sss = new SaveFromUrl(conn, "2011242122", "1000812010", "t_subjectentryrectify", null, null, null);
		sss.setSubject(subjectfullname, standsubject, subjectcode, split);
		
		
	}
}
