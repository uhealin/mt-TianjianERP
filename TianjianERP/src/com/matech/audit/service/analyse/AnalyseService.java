package com.matech.audit.service.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.matech.audit.work.analyse.Analyse;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class AnalyseService {

	private Connection conn;

	public AnalyseService(Connection conn) {
		this.conn = conn;
	}
	
	public String getRandom() {
		java.text.DecimalFormat df = new DecimalFormat("####");
		String i = df.format(Math.random() * 1000000000);
		
//		return i;
		return com.matech.framework.pub.autocode.DELUnid.getNumUnid();
	}
	
	/**
	 * 求出区间的科目ID
	 * @param acc 余额所在的AccPackageID
	 * @param accp 区间AccPackageID
	 * @param sid　余额所在的subjectid
	 * @return 得到区间subjectid
	 * @throws Exception
	 */
	public String getYearSubject(String acc,String accp,String sid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;		
		try {
			String result = "";
			String sql = "select distinct AccPackageID,subjectid,AccName,SubjectFullName1,SubjectFullName2,tokenid from c_account where AccPackageID='"+acc+"' and subjectid='"+sid+"' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				accp = "'"+accp.substring(0,accp.length()-1).replaceAll(",", "','")+"'";			
//				sql = "select distinct AccPackageID,subjectid,AccName,SubjectFullName1,SubjectFullName2 from c_account where substring(AccPackageID,1,6)='"+acc.substring(0,6)+"' and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ("+accp+") and SubjectFullName2='"+rs.getString("SubjectFullName2")+"' ";
				//科目连续性
				sql = "select distinct AccPackageID,subjectid,AccName,SubjectFullName1,SubjectFullName2 from c_account where substring(AccPackageID,1,6)='"+acc.substring(0,6)+"' and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ("+accp+") and tokenid='"+rs.getString("tokenid")+"' ";
				rs.close();
				ps.close();
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					result += rs.getString("subjectid") + ",";
				}
			}			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getYearAssItem(String acc,String accp,String sid,String aid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try {
			String result = "";
			
			accp = "'"+accp.substring(0,accp.length()-1).replaceAll(",", "','")+"'";	
			
			String sql = "select distinct a.AssItemID " +
			" from c_assitementryacc a, c_assitem b,( " +
			"	select distinct a.AccPackageID,a.subjectid " +
			"	from c_account a, c_account b " +
			"	where  b.AccPackageID='"+acc+"' " +
			"	and b.subjectid ='"+sid+"' " +
			"	and concat(a.SubYearMonth,LPAD(a.SubMonth,2,'0')) in ("+accp+") " +
			"	and a.tokenid = b.tokenid" +
			" ) c" +
			" where 1=1" +
			" and b.AccPackageID='"+acc+"' and b.AccID='"+sid+"' and b.AssItemID='"+aid+"' " +
			" and concat(a.SubYearMonth,LPAD(a.SubMonth,2,'0')) in ("+accp+") " +
			" and a.AccPackageID =c.AccPackageID" +
			" and a.AssItemName = b.AssItemName " +
			" and a.accid = c.subjectid";
			
//			System.out.println("getYearAssItem=|"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result += rs.getString("AssItemID") + ",";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public double getSubjectByRemain(String acc,String SubjectID,String accp)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try {
			double result = 0.00;
			accp = accp.substring(0,accp.length()-1);
//			int year = Integer.parseInt(accp.substring(0,4));
			int year = Integer.parseInt(getSubject(acc,SubjectID)) -1 ;
			
			int month = Integer.parseInt(accp.substring(4));
			month ++ ;
			if(month>12){
				year ++ ;
				accp = String.valueOf(year) + "01";
			}else{
				accp = String.valueOf(year) + accp.substring(4);
			}
			accp = "'"+accp+"'";
			String sql = "select distinct AccPackageID,subjectid,AccName,SubjectFullName1,SubjectFullName2 from c_account where AccPackageID='"+acc+"' and subjectid='"+SubjectID+"' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select direction*(debitremain+creditremain) remain from c_account where substring(AccPackageID,1,6)='"+acc.substring(0,6)+"' and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ("+accp+") and SubjectFullName2='"+rs.getString("SubjectFullName2")+"' ";
				rs.close();
				ps.close();
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					result = rs.getDouble(1);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getSubject(String acc,String SubjectID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try {
			String result = "";
//			String sql = "select min(substring(AccPackageID,7)) from c_accpkgsubject where substring(AccPackageID,1,6)='"+acc.substring(0,6)+"'  and subjectid='"+SubjectID+"'";
			String sql = "select min(substring(AccPackageID,7)) from c_account a " +
				" where substring(AccPackageID,1,6)='"+acc.substring(0,6)+"'  and submonth=1 " +
				" and tokenid=(select tokenid from c_account where AccPackageID='"+acc+"' and subjectid='"+SubjectID+"' and submonth=1 ) ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString(1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getAssitem(String acc,String SubjectID,String AssitemID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try {
			String result = "";
			
			String sql = "select min(substring(a.AccPackageID,7)) " +
					" from c_assitem a,(" +
					"	select distinct AssItemName" +
					"	from c_assitem " +
					"	where AccPackageID='"+acc+"' and AccID='"+SubjectID+"' and AssItemID='"+AssitemID+"'" +
					" ) b,( " +
					"	select distinct AccPackageID,subjectid from c_account a,(" +
					"		select distinct tokenid " +
					"		from c_account " +
					"		where AccPackageID='"+acc+"' and subjectid ='"+SubjectID+"' " +
					"	) b where a.tokenid = b.tokenid" +
					" ) c" +
					" where 1=1" +
					" and a.AccPackageID =c.AccPackageID" +
					" and a.AssItemName = b.AssItemName " +
					" and a.accid = c.subjectid";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString(1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public double getAssitemByRemain(String acc,String SubjectID,String AssitemID,String accp)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try {
			double result = 0.00;
			accp = accp.substring(0,accp.length()-1);
//			int year = Integer.parseInt(accp.substring(0,4));
			int year = Integer.parseInt(getAssitem(acc,SubjectID,AssitemID)) -1 ;
			int month = Integer.parseInt(accp.substring(4));
			month ++ ;
			if(month>12){
				year ++ ;
				accp = String.valueOf(year) + "01";
			}else{
				accp = String.valueOf(year) + accp.substring(4);
			}
			accp = "'"+accp+"'";
			String sql = "select distinct AccPackageID,AccID,AssItemID,AssItemName,AssTotalName from c_assitem where AccPackageID='"+acc+"' and AccID='"+SubjectID+"' and AssItemID='"+AssitemID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select direction*(debitremain+creditremain) remain from c_assitementryacc where substring(AccPackageID,1,6)='"+acc.substring(0,6)+"' and AccID='"+SubjectID+"' and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ("+accp+") and AssTotalName1 = '"+rs.getString("AssTotalName")+"'";
				rs.close();
				ps.close();
				
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					result = rs.getDouble(1);
				}
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void dataAssItemTable(String[][] an,Analyse analyse, String TableName) throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			String sql = "";
			
			String result ="";
			for(int i=0;i<analyse.getChoose_alist().length;i++){
				if(analyse.getChoose_alist()[i] !=null && !"".equals(analyse.getChoose_alist()[i])){
					result +="'"+analyse.getChoose_alist()[i]+"',";
				}
			}
			result = result.substring(0,result.length()-1);
			
			String acc = analyse.getCustomerID()+analyse.getEndYear();
			
			sql = "select * from (select AccPackageID,AccID,AssItemID,AssItemName, direction," +
				" if(direction=0,(Balance),direction*Balance) Balance,Balance bal,AssTotalName1 " +
				" from c_assitementryacc where AccPackageID='"+acc+"' and accid in (" +
				" 	select b.subjectid from c_accpkgsubject a, c_accpkgsubject b where a.subjectid = '"+analyse.getSubjectID()+"' " +
				" 	and a.AccPackageID = '"+acc+"' and b.AccPackageID = '"+acc+"' " +
				" 	and (b.subjectfullname like concat(a.subjectfullname ,'/%') or b.subjectfullname = a.subjectfullname)" +
				" 	and b.isleaf=1 " +
				" )" +
				" and SubMonth='"+analyse.getEndMonth()+"' and isleaf1=1 " +
				" and AssItemID in ("+result+") order by AssItemID ) a where 1=1 and abs(bal) > 0";
			
			org.util.Debug.prtOut("Balance sql = |"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			int count = 0;
			
			while(rs.next()){
//				org.util.Debug.prtOut("*:" + count + " 0");
				
				String AccID = rs.getString("AccID");
				String AssItemID = rs.getString("AssItemID");
				String AssItemName = rs.getString("AssItemName");
				String Direction = rs.getString("direction");
				double Balance = rs.getDouble("Balance");				
				String AssTotalName1 = rs.getString("AssTotalName1");
				
				String AID = AssItemID;
				String SubjectID = AccID;
				double Bal = Balance;
				double Remain = 0.00;
				
				String [] day = new String[an.length];
				
				String strYear = getAssitem(acc, AccID, AID);//得到核算连续性至的年份
				int iCompare = compare(strYear,an);//得到连续的最少编号
				
				/**
				 * 求期初
				 */
				Remain = getAssitemByRemain(acc,AccID,AID,an[an.length-1][2]);
				
				String contrast = "";
				if(analyse.getSubjects() != null){
					for(int i = 0; i<analyse.getSubjects().length; i++){
						if((AccID +"`|`"+ AssItemID).equals(analyse.getSubjects()[i])){
							if(analyse.getCreditPeriod() != null){
								if(analyse.getCreditPeriod()[i] == null || "".equals(analyse.getCreditPeriod()[i]) ){
									contrast = "0"; 
								}else{
									contrast = analyse.getCreditPeriod()[i]; 
								}
							}else{
								contrast = "0"; 
							}
							
							break;
						}
					}
					
					if(!"0".equals(contrast)){
						getContrast(contrast,an);
					}
				}
				
//				org.util.Debug.prtOut("*:" + count + " 1");
					String sql1 = "";
					
					for (int i = 0; i < an.length; i++) {
						SubjectID =  getYearSubject(acc,an[i][2],AccID);	//为科目连续性准备的
						
						AssItemID =  getYearAssItem(acc,an[i][2],AccID,AID);
						
						if(!"".equals(AssItemID)){
							String s = " sum(if(direction = 1 ,if(DebitOcc >0,DebitOcc,0) + if(CreditOcc < 0,(-1) * CreditOcc,0) ,if(CreditOcc >0,CreditOcc,0) + if(DebitOcc < 0,(-1) * DebitOcc,0)  ))  Occ, ";
							if(Balance < 0){
								s =	" sum(if(direction = 1 ,if(CreditOcc >0,CreditOcc,0) + if(DebitOcc < 0,(-1) * DebitOcc,0) ,if(DebitOcc >0,DebitOcc,0) + if(CreditOcc < 0,(-1) * CreditOcc,0)  ))  Occ, ";
							}
							
//							String s = " sum(case when DebitOcc<0 then 0 else DebitOcc end) Occ, ";
//							if("-1".equals(analyse.getDirection())){
//								s = "  sum(case when CreditOcc<0 then 0 else CreditOcc end) Occ, ";
//							}
							sql1 += " select "+ s + " '"+an[i][0]+"' vchdate from c_assitementryacc where " +
									" substring(AccPackageID,1,6) = '"+analyse.getCustomerID()+"' " +
									" and AccID in ('"+SubjectID.substring(0,SubjectID.length()-1).replaceAll(",", "','")+"') " +
									" and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ('"+an[i][2].substring(0,an[i][2].length()-1).replaceAll(",", "','")+"') " +
									" and AssItemID in ('"+AssItemID.substring(0,AssItemID.length()-1).replaceAll(",", "','")+"') union";							
		
						}
						
					}
					
					sql = "select * from (" + sql1.substring(0,sql1.length()-5) + ") a order by vchdate";
					org.util.Debug.prtOut("1:" + count + " sql = |"+sql);
					
					ps2 = conn.prepareStatement(sql);
					rs1 = ps2.executeQuery();
					System.out.println("time0000:="+String.valueOf(new java.util.Date(System.currentTimeMillis())));
					int ii = 0;
					while(rs1.next()){
						day[ii] = new String(); 
						double Occ = rs1.getDouble("Occ");

						if(Balance > 0){
							if(Balance-Occ>0){
								day[ii] = String.valueOf(Occ);
								Balance = Balance-Occ;
							}else{
								day[ii] = String.valueOf(Balance);
								Balance = 0;
							}
						}else{
							if(Balance + Occ < 0){
								day[ii] = String.valueOf((-1) * Occ);
								Balance = Balance+Occ;
							}else{
								day[ii] = String.valueOf(Balance);
								Balance = 0;
							}
						}
						
						ii++;
					}
					rs1.close();
					ps2.close();
					
					for(int i=0;i<day.length;i++){
						if(day[i]==null || "".equals(day[i])){
							day[i] = new String();
							day[i] = "0";
						}
					}
					
					
					/**
					 * 当余额还大于0，就把它放在科目连续最少区间内
					 * 例：科目A，只连续能2005年，大于0
					 * 余额就放在200412的区间内
					 */
					if(Balance != 0.00){
						day[iCompare] = String.valueOf(Double.parseDouble(day[iCompare])+Balance);
						Balance = 0;
					}
					
					if(Balance>0.00 && ii!=0){
						if(ii == day.length){							
							day[ii-1] = String.valueOf(Double.parseDouble(day[ii-1])+Balance);
						}else{
							day[ii] = String.valueOf(Double.parseDouble(day[ii])+Balance);	
						}						
						Balance = 0;
					}
										
					
					
					/*无凭证有余额*/
					if(Balance>0.00 && ii==0){
						day[day.length-1] = String.valueOf(Balance);
					}
					
//				}else{
////					org.util.Debug.prtOut("2:" + count + " 0");
//					String sql1 = "";
//					
//					for (int i = 0; i < an.length; i++) {
////						org.util.Debug.prtOut("2:" + count + " 00");
//						AssItemID =  getYearAssItem(acc,an[i][2],AccID,AID);
////						org.util.Debug.prtOut("2:" + count + " 01"); 
//						if(!"".equals(AssItemID)){
//							String s = " sum(case when DebitOcc<0 then 0 else DebitOcc end) Occ, ";
//							if("-1".equals(Direction)){
//								s = "  sum(case when CreditOcc<0 then 0 else CreditOcc end) Occ, ";
//							}
//							sql1 += " select "+ s + " '"+an[i][0]+"' vchdate from c_assitementryacc where " +
//									" substring(AccPackageID,1,6) = '"+analyse.getCustomerID()+"' " +
//									" and AccID = '"+AccID+"' " +
//									" and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ('"+an[i][2].substring(0,an[i][2].length()-1).replaceAll(",", "','")+"') " +
//									" and AssItemID in ('"+AssItemID.substring(0,AssItemID.length()-1).replaceAll(",", "','")+"') union";							
//		
//						}
//						
//					}
////					org.util.Debug.prtOut("2:" + count + " 02");
//					sql = "select * from (" + sql1.substring(0,sql1.length()-5) + ") a order by abs(vchdate)";
//					org.util.Debug.prtOut("2:" + count + " sql = |"+sql);
//					
////					org.util.Debug.prtOut("2:" + count + " 1");
//					ps2 = conn.prepareStatement(sql);
//					rs1 = ps2.executeQuery();
//					int ii = 0;
//					while(rs1.next()){
//						day[ii] = new String(); 
//						double Occ = rs1.getDouble("Occ");
//						if(Occ>0){
//							day[ii] = String.valueOf(Balance);
//							Balance = 0;
//						}else{
//							day[ii] = "0";
//						}
//						ii++;
//					}
//					rs1.close();
//					ps2.close();
//					
////					org.util.Debug.prtOut("2:" + count +  " 2");
//					for(int i=0;i<day.length;i++){
//						if(day[i]==null || "".equals(day[i])){
//							day[i] = new String();
//							day[i] = "0";
//						}
//					}
//					
//					/**
//					 * 当余额还大于0，就把它放在科目连续最少区间内
//					 * 例：科目A，只连续能2005年，大于0
//					 * 余额就放在200412的区间内
//					 */
//					if(Balance>0.00){
//						day[iCompare] = String.valueOf(Double.parseDouble(day[iCompare])+Balance);
//						Balance = 0;
//					}
//					
////					org.util.Debug.prtOut("2:" + count + " 3");
//					if(Balance < 0.00 && ii!=0){
//						if(ii == day.length){							
//							day[ii-1] = String.valueOf(Double.parseDouble(day[ii-1])+Balance);
//						}else{
//							day[ii] = String.valueOf(Double.parseDouble(day[ii])+Balance);	
//						}						
//						Balance = 0;
//					}
//										
////					org.util.Debug.prtOut("2:" + count +  " 4");
//					
//					/*无凭证有余额*/
//					if(Balance < 0.00 && ii==0){
//						day[day.length-1] = String.valueOf(Balance);
//					}
//					
//				}
//				org.util.Debug.prtOut("2:" + count +  " 5");
				if(count==0){
					sql = "insert into "+TableName+" (CID, CName,Year,Contrast, Dirction,Remain, Balance ";
					for(int i=0;i < an.length; i++){
						sql += ","+ an[i][0];
					}
					sql += ") values (?,?,?,?,?,?,?" ;
					for(int i=0;i < day.length; i++){
						sql += ",? ";
					}
					sql += ") ";
					
					ps1 = conn.prepareStatement(sql);
				}
//				org.util.Debug.prtOut("2:" + count + " 6");
				int n = 1;
				ps1.setString(n++,AID);
				ps1.setString(n++,AssItemName);
				
				ps1.setString(n++,getAssitem(acc, AccID, AID) + "年" );
				ps1.setString(n++,contrast );//信用期
				
				ps1.setString(n++,("1".equals(Direction)?"借":"贷"));					
				ps1.setDouble(n++,Remain);
				ps1.setDouble(n++,Bal);
				for(int i=0;i < day.length; i++){
					ps1.setDouble(n++,Double.parseDouble(day[i]));
				}
				ps1.addBatch();
//				org.util.Debug.prtOut("2:" + count + " 7");
				count ++;
				
				if (count % 100 == 0) {
					ps1.executeBatch();			            
		        }
//				org.util.Debug.prtOut("2:" + count + " 8");
				
			}
			rs.close();
//			org.util.Debug.prtOut("2:" + count + " 9");
			if (count % 100 != 0) {
				ps1.executeBatch();
			}
//			org.util.Debug.prtOut("2:" + count + " 10");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps1);
			DbUtil.close(ps2);
		}
	}
	
	public void dataSubjectTable(String[][] an,Analyse analyse, String TableName) throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			String sql = "";
			
//			String [][] contrast = new String [analyse.getChoose_slist().length][2];//信用期与科目一一对照
			
			String result ="";
			for(int i=0;i<analyse.getChoose_slist().length;i++){
				if(analyse.getChoose_slist()[i] !=null && !"".equals(analyse.getChoose_slist()[i])){
					result +="'"+analyse.getChoose_slist()[i]+"',";
				}
			}
			result = result.substring(0,result.length()-1);
			
			String acc = analyse.getCustomerID()+analyse.getEndYear();
			
			sql = "select * from (select AccPackageID,subjectid,AccName, direction," +
				" if(direction=0,abs(Balance),direction*Balance) Balance,Balance bal,SubjectFullName1,SubjectFullName2" +
				" from c_account where AccPackageID='"+acc+"' and SubMonth='"+analyse.getEndMonth()+"' and isleaf1=1 " +
				" and subjectid in ("+result+") order by subjectid ) a where 1=1 " +
				" and abs(bal) > 0 " ;
//				" and ("+analyse.getDirection()+") * bal>0 ";
			
			org.util.Debug.prtOut("Balance sql = |"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			int count = 0;
			
			while(rs.next()){
				String SubjectID = rs.getString("subjectid");
				String Direction = rs.getString("direction");
				double Balance = rs.getDouble("Balance");
				String AccName = rs.getString("AccName");
				String SubjectFullName2 = rs.getString("SubjectFullName2");
				
				String SID = SubjectID;
				double Bal = Balance;
				double Remain = 0.00;
					
				String [] day = new String[an.length];
				
				String strYear = getSubject(acc,SID);//得到科目连续性至的年份
				int iCompare = compare(strYear,an);//得到连续的最少编号
//				System.out.println("iCompare = " +iCompare);
				
				/**
				 * 求期初
				 */
				Remain = getSubjectByRemain(acc,SID,an[an.length-1][2]);
				String contrast = "";
				if(analyse.getSubjects() != null){
					for(int i = 0; i<analyse.getSubjects().length; i++){
						if(SubjectID.equals(analyse.getSubjects()[i])){
							if(analyse.getCreditPeriod() != null){
								if(analyse.getCreditPeriod()[i] == null || "".equals(analyse.getCreditPeriod()[i]) ){
									contrast = "0"; 
								}else{
									contrast = analyse.getCreditPeriod()[i]; 
								}
							}else{
								contrast = "0"; 
							}
							
							break;
						}
					}
					
					if(!"0".equals(contrast)){
						getContrast(contrast,an);
					}
				}
				
//				if(Direction.equals(analyse.getDirection())){		//同方向的
					
					String sql1 = "";
					
					for (int i = 0; i < an.length; i++) {
						SubjectID =  getYearSubject(acc,an[i][2],SID);	//为科目连续性准备的
												
						if(!"".equals(SubjectID)){
							String s = " sum(if(direction = 1 ,if(DebitOcc >0,DebitOcc,0) + if(CreditOcc < 0,(-1) * CreditOcc,0) ,if(CreditOcc >0,CreditOcc,0) + if(DebitOcc < 0,(-1) * DebitOcc,0)  ))  Occ, ";
							if(Balance < 0){
								s =	" sum(if(direction = 1 ,if(CreditOcc >0,CreditOcc,0) + if(DebitOcc < 0,(-1) * DebitOcc,0) ,if(DebitOcc >0,DebitOcc,0) + if(CreditOcc < 0,(-1) * CreditOcc,0)  ))  Occ, ";
							}
//							s =	" sum(case when DebitOcc<0 then 0 else DebitOcc end)  Occ, ";
//							if("-1".equals(analyse.getDirection())){
//								s = " sum(case when CreditOcc<0 then 0 else CreditOcc end) Occ, ";
//							}
							sql1 += " select "+ s + " '"+an[i][0]+"' vchdate from c_account where " +
									" substring(AccPackageID,1,6) = '"+analyse.getCustomerID()+"' " +
									" and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ('"+an[i][2].substring(0,an[i][2].length()-1).replaceAll(",", "','")+"') " +
									" and SubjectID in ('"+SubjectID.substring(0,SubjectID.length()-1).replaceAll(",", "','")+"') union";							
														
						}
						
					}
					
					sql = "select * from (" + sql1.substring(0,sql1.length()-5) + ") a order by vchdate";
					org.util.Debug.prtOut("sql = |"+sql);
					
					ps2 = conn.prepareStatement(sql);
					rs1 = ps2.executeQuery();
					int ii = 0;
					while(rs1.next()){
						day[ii] = new String(); 
						double Occ = rs1.getDouble("Occ");
						
						if(Balance > 0){
							if(Balance-Occ>0){
								day[ii] = String.valueOf(Occ);
								Balance = Balance-Occ;
							}else{
								day[ii] = String.valueOf(Balance);
								Balance = 0;
							}
						}else{
							if(Balance + Occ < 0){
								day[ii] = String.valueOf((-1) * Occ);
								Balance = Balance+Occ;
							}else{
								day[ii] = String.valueOf(Balance);
								Balance = 0;
							}
						}
						
						ii++;
					}
					rs1.close();
					ps2.close();
					
					for(int i=0;i<day.length;i++){
						if(day[i]==null || "".equals(day[i])){
							day[i] = new String();
							day[i] = "0";
						}
					}
					
					/**
					 * 当余额还大于0，就把它放在科目连续最少区间内
					 * 例：科目A，只连续能2005年，大于0
					 * 余额就放在200412的区间内
					 */
					if(Balance != 0.00){
						day[iCompare] = String.valueOf(Double.parseDouble(day[iCompare])+Balance);
						Balance = 0;
					}
					
					if(Balance>0.00 && ii!=0){
						if(ii == day.length){							
							day[ii-1] = String.valueOf(Double.parseDouble(day[ii-1])+Balance);
						}else{
							day[ii] = String.valueOf(Double.parseDouble(day[ii])+Balance);	
						}						
						Balance = 0;
					}															
					
					/*无凭证有余额*/
					if(Balance>0.00 && ii==0){
						day[day.length-1] = String.valueOf(Balance);
					}
					
//				}else{		//不同方向的
//					 
//					String sql1 = "";
//					
//					for (int i = 0; i < an.length; i++) {
//						SubjectID =  getYearSubject(acc,an[i][2],SID);	//为科目连续性准备的
//												
//						if(!"".equals(SubjectID)){
//							String s = " sum(case when DebitOcc<0 then 0 else DebitOcc end)  Occ, ";
//							if("-1".equals(Direction)){
//								s = " sum(case when CreditOcc<0 then 0 else CreditOcc end) Occ, ";
//							}
//							sql1 += " select "+ s + " '"+an[i][0]+"' vchdate from c_account where " +
//									" substring(AccPackageID,1,6) = '"+analyse.getCustomerID()+"' " +
//									" and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ('"+an[i][2].substring(0,an[i][2].length()-1).replaceAll(",", "','")+"') " +
//									" and SubjectID in ('"+SubjectID.substring(0,SubjectID.length()-1).replaceAll(",", "','")+"') union";							
//														
//						}
//						
//					}
//					
//					sql = "select * from (" + sql1.substring(0,sql1.length()-5) + ") a order by abs(vchdate)";
//					org.util.Debug.prtOut("sql = |"+sql);
//					
//					ps2 = conn.prepareStatement(sql);
//					rs1 = ps2.executeQuery();
//					int ii = 0;
//					while(rs1.next()){
//						day[ii] = new String(); 
//						double Occ = rs1.getDouble("Occ");
//						if(Occ>0){
//							day[ii] = String.valueOf(Balance);
//							Balance = 0;
//						}else{
//							day[ii] = "0";
//						}
//						ii++;
//					}
//					rs1.close();
//					ps2.close();
//					
//					for(int i=0;i<day.length;i++){
//						if(day[i]==null || "".equals(day[i])){
//							day[i] = new String();
//							day[i] = "0";
//						}
//					}
//					
//					/**
//					 * 当余额还大于0，就把它放在科目连续最少区间内
//					 * 例：科目A，只连续能2005年，大于0
//					 * 余额就放在200412的区间内
//					 */
//					if(Balance<0.00){
//						day[iCompare] = String.valueOf(Double.parseDouble(day[iCompare])+Balance);
//						Balance = 0;
//					}
//					
//					if(Balance < 0.00 && ii!=0){
//						if(ii == day.length){							
//							day[ii-1] = String.valueOf(Double.parseDouble(day[ii-1])+Balance);
//						}else{
//							day[ii] = String.valueOf(Double.parseDouble(day[ii])+Balance);	
//						}						
//						Balance = 0;
//					}															
//					
//					/*无凭证有余额*/
//					if(Balance < 0.00 && ii==0){
//						day[day.length-1] = String.valueOf(Balance);
//					}
//					
//				}
				
				if(count==0){
					sql = "insert into "+TableName+" (CID, CName,Year, Contrast,Dirction,Remain, Balance ";
					for(int i=0;i < an.length; i++){
						sql += ","+ an[i][0];
					}
					sql += ") values (?,?,?,?,?,?,?" ;
					for(int i=0;i < day.length; i++){
						sql += ",? ";
					}
					sql += ") ";
					
					ps1 = conn.prepareStatement(sql);
				}
				
				int n = 1;
				ps1.setString(n++,SID);
				ps1.setString(n++,AccName);
				
				ps1.setString(n++,getSubject(acc,SID) + "年");		//增加科目连续性至
				
				ps1.setString(n++, contrast);//信用期
				
				ps1.setString(n++,("1".equals(Direction)?"借":"贷"));					
				ps1.setDouble(n++,Remain);
				ps1.setDouble(n++,Bal);
				for(int i=0;i < day.length; i++){
					ps1.setDouble(n++,Double.parseDouble(day[i]));
				}
				ps1.addBatch();
				
				count ++;
				
				if (count % 100 == 0) {
					ps1.executeBatch();			            
		        }
				
				
			}
			rs.close();
			
			if (count % 100 != 0) {
				ps1.executeBatch();
		     }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps1);
			DbUtil.close(ps2);
		}
	}
	
	public String createTable(String[][] an,String TableName) throws Exception{
		PreparedStatement ps = null;
		String sql = "";
		
		try {
			if(!"".equals(TableName)){
				sql = "DROP TABLE IF EXISTS "+TableName+" ";	
				ps = conn.prepareStatement(sql);
	            ps.execute();
	            ps.close();
			}
            TableName = "tt_analyse"+getRandom();
            
			sql = "CREATE TABLE "+TableName+" ( "                
             + " CID varchar(100) default '', "          
             + " CName varchar(500) default NULL,"    
             + " Year varchar(10) default NULL,"   
             + " Contrast varchar(10) default NULL, "
             + " Dirction varchar(10) default NULL, "
             + " Remain decimal(20,2) default NULL, "  
             + " Balance decimal(20,2) default NULL";              
             for (int i = 0; i < an.length; i++) {
				String res = an[i][0];
				res = ", "+res+" decimal(20,2) default NULL ";
				sql += res;
             }
             sql +=  " ) ENGINE=MyISAM DEFAULT CHARSET=gbk   ";
             
             ps = conn.prepareStatement(sql);
             ps.execute();
             
             return TableName;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {		
			DbUtil.close(ps);
		}
	}
	
	
	
	public String getDay(String Projectid) throws Exception{
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "";		
		try {
			if(Projectid!=null && !"".equals(Projectid) && !"null".equals(Projectid)){
				sql = "select * from z_accyearconfig where Projectid ='"+Projectid+"' and  ConfigName ='区间设置'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					return rs.getString("ConfigValue");
				}
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void saveDay(String sDate,String Projectid) throws Exception{
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sql = "";
		String ConfigName = "区间设置";
		try {
			if(Projectid!=null && !"".equals(Projectid) && !"null".equals(Projectid)){
				if(!"".equals(sDate)){
					sql = "select 1 from z_accyearconfig where Projectid ='"+Projectid+"' and  ConfigName ='"+ConfigName+"' limit 1 ";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						sql = "delete from z_accyearconfig where Projectid ='"+Projectid+"' and  ConfigName ='"+ConfigName+"'";
						ps.close();
						
						ps = conn.prepareStatement(sql);
						ps.execute();
						ps.close();
					}
					sql = "INSERT INTO z_accyearconfig (Projectid,ConfigName,ConfigValue) VALUES('"+Projectid+"','"+ConfigName+"','"+sDate+"')";
					ps = conn.prepareStatement(sql);
					ps.execute();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void save(String projectID,String ConfigName,String ConfigValue) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "";
			
			sql = "delete from  z_accyearconfig where Projectid = ? and ConfigName = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, ConfigName);
			ps.execute();
			
			if("".equals(ConfigValue.trim())) return;
			
			sql = "INSERT INTO z_accyearconfig (Projectid,ConfigName,ConfigValue) VALUES(?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, ConfigName);
			ps.setString(3, ConfigValue);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public String get(String projectID,String ConfigName) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			
			sql = "select * from  z_accyearconfig where Projectid = ? and ConfigName = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			ps.setString(2, ConfigName);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("ConfigValue");
			}
			
			return "";
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public int compare(String year,String [][] an) {
		
		int y1 = Integer.parseInt(year) - 1;
		year = String.valueOf(y1) + "12"; 
		
		int returnInt = 0;
		int iCount = 0;
		for (int i = 0; i < an.length; i++) {
			String[] strings = an[i];
			
			if(strings[2].indexOf(year)>-1){
				returnInt = i;
				break;
			}
			
			iCount ++;
			
		}
		
		if(iCount == an.length){
			returnInt = an.length-1;
		}
		return returnInt;
	}
	
	public void getContrast(String contrast,String [][] an){
		
//		[Day0, 1个月以内, 200812,], 
//		[Day1, 1个月~2个月, 200811,], 
//		[Day2, 2个月~3个月, 200810,], 
//		[Day3, 3个月~半年, 200809,200808,200807,], 
//		[Day4, 半年~1年, 200806,200805,200804,200803,200802,200801,], 
//		[Day5, 1年~2年, 200712,200711,200710,200709,200708,200707,200706,200705,200704,200703,200702,200701,], 
//		[Day6, 2年~3年, 200612,200611,200610,200609,200608,200607,200606,200605,200604,200603,200602,200601,], 
//		[Day7, 3年以上, 200512,]
//		
//		余额	1月以内		1-2月		2-3月		3-半年	半年-1年	1-2年		2-3年		3年以上
//		账龄：
//		＝＝＝＝＝＝＝＝＝＝＝＝｜＝＝＝＝＝＝＝＝＝＝＝＝｜＝＝＝＝＝＝＝＝＝＝＝＝
//		＝	200812
//		　＝　200811
//			＝	200810
//			　＝＝＝	200809,200808,200807
//				　　＝＝＝＝＝＝｜ 200806,200805,200804,200803,200802,200801
//								＝＝＝＝＝＝＝＝＝＝＝＝｜
//														＝＝＝＝＝＝＝＝＝＝＝＝
//		加上信用期　3个月
//		＝＝＝＝＝＝＝＝＝＝＝＝｜＝＝＝＝＝＝＝＝＝＝＝＝｜＝＝＝＝＝＝＝＝＝＝＝＝
//		＝＝＝＝	200812,200811,200810,200809					
//		　－－－＝ 200808
//			－－－＝ 200807
//			　－－－＝＝＝ 200806,200805,200804
//				　　－－－＝＝＝｜＝＝＝ 200803,200802,200801,200712,200711,200710
//								－－－＝＝＝＝＝＝＝＝＝｜＝＝＝
//														－－－＝＝＝＝＝＝＝＝＝｜＝＝＝
		
		for(int i = 0; i<an.length; i++){
			
			String [] splits = an[i][2].split(",");
			
			String [] strAn = new String[splits.length + Integer.parseInt(contrast)];
			
			for(int j = 0; j<splits.length; j++){
				strAn[j] = new String();
				strAn[j] = splits[j];
			}
			
			int year = Integer.parseInt(splits[splits.length-1].substring(0,4));
			int month = Integer.parseInt(splits[splits.length-1].substring(4));
			
			for(int j=splits.length; j<strAn.length; j++){
				strAn[j] = new String();
				
				month = month - 1;	
				if( month == 0){
					year = year -1;
					month = 12;
				}
				
				strAn[j] = String.valueOf(year) + (month < 10 ?"0"+String.valueOf(month):String.valueOf(month));
				
			}
			
			an[i][2] = "";
			if(i==0){
				for(int j = 0; j<strAn.length; j++){
					an[i][2] += strAn[j] + ",";
				}
			}else{
				for(int j = Integer.parseInt(contrast); j<strAn.length; j++){
					an[i][2] += strAn[j] + ",";
				}
			}
		}
		
		
		
	}
	
	/**
	 * 得到分析方向
	 */
	public String getDirection(String acc,String subjectID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select *,if(substring(Property,2,1) = 2,-1,1) as Direction from c_accpkgsubject where AccPackageID = ? and subjectID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, subjectID);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("Direction");
			}
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String [][] getAnaly(String sDate,String year,String endMonth){
		String [] StrList = sDate.split("\\|");
		String [][] result = new String [StrList.length + 1][3];
		String str = "";
		String str1 = "";
		String str2 = "";
		str2 = endMonth;
		int jj = 0;
		for (int i = 0; i < StrList.length; i++) {
			if(StrList[i] !=null && !"".equals(StrList[i])){
				String [] sl = StrList[i].split("`");
				
				result [i][0] = new String();
				result [i][1] = new String();
				result [i][2] = new String();
//				result [i][3] = new String();
				
				result [i][0] = "Day"+i; //表字
				result [i][1] = sl[0];				
				if(i==0) {
					result [i][1] = sl[0] + "以内"; 
				}else{
					result [i][1] = str + "~" + result [i][1];
				}
				
				for (int j = jj; j < Integer.parseInt(sl[1]); j++) {
					if("0".equals(endMonth)){
						year = String.valueOf(Integer.parseInt(year) -1);
						endMonth = "12";
					}
					result [i][2] += year + (endMonth.length()<2 ?"0"+endMonth:endMonth) + ",";
					endMonth = String.valueOf(Integer.parseInt(endMonth) -1);					
				}
				jj = Integer.parseInt(sl[1]);
			
				str = sl[0];
			}
		}
		
		result [StrList.length][0] = new String();
		result [StrList.length][1] = new String();
		result [StrList.length][2] = new String();		
	
		result [StrList.length][0] = "Day"+StrList.length; //表字		
		result [StrList.length][1] = str + "以上";
		if("0".equals(endMonth)){
			year = String.valueOf(Integer.parseInt(year) -1);
			endMonth = "12";
		}
		result [StrList.length][2] += year + (endMonth.length()<2 ?"0"+endMonth:endMonth) + ",";

		
		return result;
	}
	
	/**
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 */
	
	public String createTable(String TableName,String[][] dayArray) throws Exception{
		PreparedStatement ps = null;
		String sql = "";
		
		try {
			
			if(!"".equals(TableName)){
				sql = "DROP TABLE IF EXISTS "+TableName+" ";	
				ps = conn.prepareStatement(sql);
	            ps.execute();
	            ps.close();
			}
			
            TableName = "tt_" + DELUnid.getNumUnid();
            
			sql = "CREATE TABLE "+TableName+" ( "                
             + " SubjectID varchar(100) default '', "          
             + " AssitemID varchar(100) default NULL,"
             + " subjectname varchar(500) default NULL,"
             + " subjectfullname1 varchar(500) default NULL,"
             + " tokenid varchar(500) default NULL,"
             + " AssTotalName1 varchar(500) default NULL,"
             + " isSubject varchar(500) default NULL,"
             + " direction2 varchar(10) default NULL, "
             
             + " Contrast varchar(10) default NULL, "
             
             + " Year varchar(10) default NULL,"	//连续年份   
             + " Remain decimal(20,2) default NULL, "  //期初
             + " Balance decimal(20,2) default NULL";  //期末            
             for (int i = 0; i < dayArray.length; i++) {
				String res = dayArray[i][0];
				res = ", "+res+" decimal(20,2) default NULL ";
				sql += res;
             }
             sql +=  " ) ENGINE=MyISAM DEFAULT CHARSET=gbk   ";
             
             ps = conn.prepareStatement(sql);
             ps.execute();
             
             return TableName;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {		
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 生成账龄分析表
	 */
	public void saveAnaly(String tempName,String CustomerID,String EndYear,String EndDate,String SubjectID,String [][] dayArray) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			int endTime = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate);
			String SubjectName = "",sqlAssItem = "";
			String acc = CustomerID + EndYear;
			
			/**
			 * 得到科目全路径
			 */
			sql = "select * from c_account where SubYearMonth * 12 + SubMonth = ?  and subjectid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, endTime);
			ps.setString(2, SubjectID);
			rs = ps.executeQuery();
			if(rs.next()){
				SubjectName = rs.getString("subjectfullname1");	//科目全路径
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			/**
			 * 得到与披露有关的核算类型
			 */
			sql = "select distinct a.ifequal,a.subjectid,a.asstotalname1  " +
			" from c_subjectassitem a,c_account b " +
			" where 1=1 " +
			" and a.accpackageid ='"+acc+"' " +
			" and b.accpackageid ='"+acc+"' " +
			" and (b.subjectfullname1 = '"+SubjectName+"' or b.subjectfullname1 like '"+SubjectName+"/%') " +
			" and a.subjectid = b.subjectid " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String ifequal = rs.getString("ifequal");
				String accid = rs.getString("subjectid");
				String asstotalname1 = rs.getString("asstotalname1");
				
				if("0".equals(ifequal)){ //有核算披露
					sqlAssItem += "or (a.accid = '"+accid+"' and a.asstotalname1 like '"+asstotalname1+"/%' ) ";
				}
			
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			if(!"".equals(sqlAssItem)){
		    	sqlAssItem = " and ( " + sqlAssItem.substring(2)+ ") ";
		    }else{
		    	sqlAssItem = " and 1=2 ";
		    }
			
			sql = "insert into "+tempName+" (subjectid,assitemid,subjectname,subjectfullname1,tokenid,AssTotalName1,isSubject,direction2) \n" + 
			"	select distinct \n" + 
			"	a.subjectid, \n" + 
			"	if(assitemname is not null,assitemid,'') as assitemid, \n" + 
			"	if(assitemname is not null,assitemname,subjectname) as subjectname, \n" + 
			"	if(assitemname is not null,concat(subjectfullname1,'/',assitemname),subjectfullname1) as subjectfullname1, \n" + 
			"	a.tokenid as tid, AssTotalName1 ,IF(b.AssTotalName1 is null,'科目','核算') as isSubject, \n" + 
			"	ifnull(b.direction2,a.direction2) as direction2  \n" + 
			"	from ( \n" + 
			
			"		select a.subjectid ,a.accname as subjectname,a.subjectfullname1,a.tokenid,a.dataname,a.direction2,b.tids \n" + 
			"		from ( \n" + 
			"			select * from c_account a \n" + 
			"			where a.subyearmonth*12+a.submonth="+endTime+" \n" + 
			"			and (a.subjectfullname1 = '"+SubjectName+"' or a.subjectfullname1 like concat('"+SubjectName+"','/%')) \n" + 
			"			and isleaf1 = 1 \n" + 
			"		) a left join ( \n" + 
			"			select distinct accid ,tokenid as tids \n" +  
			"			from c_assitementryacc a ,(  \n" + 
			"				select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +    
			"				where 1=1   \n" + 
			"				and a.subyearmonth*12+a.submonth="+endTime+" \n" + 
			"				and (a.subjectfullname1 = '"+SubjectName+"' or a.subjectfullname1 like concat('"+SubjectName+"','/%')) \n" + 
			"			) b  \n" + 
			"			where a.subyearmonth*12+a.submonth="+endTime+" \n" + 
			"			and a.isleaf1=1    \n" + 
			sqlAssItem + 
			"			and a.accpackageid = b.accpackageid \n" +  
			"			and a.accid = b.subjectid  \n" + 
			"		) b on a.tokenid = b.tids \n" + 
			
			"	) a left join ( \n" + 

			"		select distinct accid,assitemid,assitemname, AssTotalName1,tokenid,if(a.DataName='0','本位币',a.DataName) as DataName,direction2,concat(subyearmonth,'`',accid,'`',assitemid) as sid \n" +  
			"		from c_assitementryacc a,( \n" + 
			"			select distinct accpackageid,subjectid,AccName,tokenid  from c_account a \n" +    
			"			where 1=1    \n" + 
			"			and a.subyearmonth*12+a.submonth="+endTime+" \n" + 
			"			and (a.subjectfullname1 = '"+SubjectName+"' or a.subjectfullname1 like concat('"+SubjectName+"','/%')) \n" + 
			"		) b  \n" + 
			"		where 1=1 \n" + 
			"		and a.subyearmonth*12+a.submonth="+endTime+" \n" + 
			"		and a.isleaf1=1  \n" + 
			sqlAssItem + 
			"		and a.accpackageid =b.accpackageid and a.accid = b.subjectid \n" +  
			
			"	) b on a.tokenid = b.tokenid \n" + 
			"	order by subjectid,assitemid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			String tableName = "";
			
			sql = "select distinct issubject from " + tempName;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String isSubject = rs.getString(1);
				String where = "",where1 = "";
				
				if("科目".equals(isSubject)){
					tableName = "c_account";
					where = "	and a.subjectid = b.subjectid "; 
				}else{
					tableName = "c_assitementryacc";
					where = "	and a.accid = b.subjectid and a.assitemid = b.assitemid ";
				}

				/**
				 * 更新期末余额
				 */
				sql = "select b.subjectid,b.assitemid,a.direction2 * a.Balance as Balance " +
				"	from "+tableName+" a,"+tempName+" b " +
				"	where a.subyearmonth*12+a.submonth= " + endTime + 
				"	and b.issubject = '"+isSubject+"' " + where ;
				
				sql = "update "+tempName+" a ,( " + sql + " ) b " +
				"	set a.Balance = b.Balance " +
			 	"	where a.issubject = '"+isSubject+"'  " +
			 	"	and a.subjectid = b.subjectid  " +
			 	"	and a.assitemid = b.assitemid ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				/**
				 * 更新期初和最小年份
				 */
				if("科目".equals(isSubject)){
					sql = "select b.subjectid,b.assitemid,a.direction2 * (a.DebitRemain + a.CreditRemain) as Remain,SubYear " +
					"	from "+tableName+" a,"+tempName+" b,(" +
					"		select b.subjectid,min(SubYearMonth) as SubYear " +
					"		from "+tableName+" a,"+tempName+" b " +
					"		where 1=1 " +
					"		and b.issubject = '"+isSubject+"'" +
					"		and submonth=1 " +
					"		and a.tokenid = b.tokenid " +
					"		group by b.subjectid" +
					"	) c " +
					"	where 1=1" +
					"	and a.submonth = 1 " +
					"	and b.issubject = '"+isSubject+"' " +
					where + 
					"	and a.SubYearMonth = c.SubYear " +
					"	and b.subjectid = c.subjectid";
				}else{
					sql = "select b.subjectid,b.assitemid,a.direction2 * (a.DebitRemain + a.CreditRemain) as Remain,SubYear " +
					"	from "+tableName+" a,"+tempName+" b,(" +
					"		select subjectid,b.AssTotalName1,min(SubYearMonth) as SubYear " +
					"		from "+tableName+" a,"+tempName+" b " +
					"		where 1=1 " +
					"		and b.issubject = '"+isSubject+"'" +
					"		and submonth=1 " +
					"		and a.accid = b.subjectid" +
					"		and a.assitemid = b.assitemid		 " +
					"		group by b.subjectid,b.AssTotalName1	" +
					"	) c " +
					"	where 1=1" +
					"	and a.submonth = 1 " +
					"	and b.issubject = '"+isSubject+"' " +
					where + 
					"	and a.SubYearMonth = c.SubYear " +
					"	and b.subjectid = c.subjectid" +
					"	and b.AssTotalName1 = c.AssTotalName1 ";
				}
				
				System.out.println(sql);
				sql = "update "+tempName+" a ,( " + sql + " ) b " +
				"	set a.Remain = b.Remain,a.year = b.SubYear " +
			 	"	where a.issubject = '"+isSubject+"'  " +
			 	"	and a.subjectid = b.subjectid  " +
			 	"	and a.assitemid = b.assitemid ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				String strDayUpdate="";
				
				/**
				 * 更新账龄分析
				 */
				for(int i = 0;i<dayArray.length; i++){
					String [] day = dayArray[i];
					String zlStartMonth = day[2],zlEndMonth = day[3];
					
					if (i==0){
						strDayUpdate="0";
					}else{
						strDayUpdate+="+" + dayArray[i-1][0] ;
					}
					
					boolean bEndMonth = false;
					if(day[3] == "1000"){
						bEndMonth = true;
					}
					
					
					if("科目".equals(isSubject)){
						sql="update "+tempName+" a left join \n" +
						"( \n" +
						"	select tokenid,dataname,occ1,if(inarea=1,occ2,null) occ2 " +
						"	,occ3,if(inarea=1,occ4,null) occ4 " +
						"	from ( \n" +
						"		select tokenid,if(dataname='0','本位币',dataname) as dataname, \n" +
						
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"') ,  if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0) , 0 )) Occ1, \n" +
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0), 0 )) Occ2, \n" +
						
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"') , if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ3, \n" +
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ4, \n" +

						"        \n";
						
						if (bEndMonth){
							sql+=" 0 as inarea \n";
						}else{
							sql+="       if (('"+String.valueOf(endTime)+"'>=('"+String.valueOf(endTime)+"'-"+zlStartMonth+")  && ('"+String.valueOf(endTime)+"'-"+zlStartMonth+") >=min(subyearmonth)*12+1) \n" +
							"       	||('"+String.valueOf(endTime)+"'>= ('"+String.valueOf(endTime)+"'-"+zlEndMonth+") && ('"+String.valueOf(endTime)+"'-"+zlEndMonth+") >=min(subyearmonth)*12+1), \n" +
							"       	1,0) as inarea \n" ;
						}
						
						
						sql+="		from "+tableName+" a \n" + 
						"		where 1=1 \n" + 
						"		and subyearmonth*12+submonth<= '"+String.valueOf(endTime)+"' \n" +  
						" 		and (a.subjectfullname1 = '"+SubjectName+"' or a.subjectfullname1 like '"+SubjectName+"/%')" +
						"		group by tokenid,dataname	 \n" +
						"  )a \n" +
						")b \n" +
						" on a.tokenid=b.tokenid \n"+
						" set "+day[0]+"=if(abs("+strDayUpdate+")>=abs(a.balance),0,if(balance>0,if((balance -occ1) <0,0,if(occ2 is null,balance-occ1,if( (balance-occ1-occ2)<=0,balance-occ1,occ2))),if((balance +occ3) >=0,0,if(occ4 is null,balance+occ3,if( (balance+occ3+occ4)>=0,balance+occ3,(-1)*occ4))))) \n" +
						" where a.issubject = '"+isSubject+"' and b.tokenid is not null ";
					}else{
						sql="update "+tempName+" a left join \n" +
						"( \n" +
						"	select tokenid,AssTotalName1,dataname,occ1,if(inarea=1,occ2,null) occ2" +
						"	,occ3,if(inarea=1,occ4,null) occ4 " +
						"	from ( \n" +
						"		select tokenid,AssTotalName1,if(dataname='0','本位币',dataname) as dataname, \n" +	
						
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"') ,  if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0) , 0 )) Occ1, \n" +
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"' -"+zlStartMonth+") ,if(direction2 * DebitOcc >0,direction2 * DebitOcc,0) + if(direction2 * CreditOcc <0,(-1) * direction2 * CreditOcc,0), 0 )) Occ2, \n" +
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlStartMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"') , if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ3, \n" +
						"		sum(if( a.subyearmonth*12+a.submonth>('"+String.valueOf(endTime)+"'-"+zlEndMonth+") and a.subyearmonth*12+a.submonth<=('"+String.valueOf(endTime)+"' -"+zlStartMonth+") ,if(direction2 * CreditOcc >0,direction2 * CreditOcc,0) + if(direction2 * DebitOcc <0,(-1) * direction2 * DebitOcc,0) , 0 )) Occ4, \n" +
						
						"        \n" ;
						if (bEndMonth){
							sql+=" 0 as inarea \n";
						}else{
							sql+="       if (('"+String.valueOf(endTime)+"'>=('"+String.valueOf(endTime)+"'-"+zlStartMonth+")  && ('"+String.valueOf(endTime)+"'-"+zlStartMonth+") >=min(subyearmonth)*12+1) \n" +
							"       	||('"+String.valueOf(endTime)+"'>= ('"+String.valueOf(endTime)+"'-"+zlEndMonth+") && ('"+String.valueOf(endTime)+"'-"+zlEndMonth+") >=min(subyearmonth)*12+1), \n" +
							"       	1,0) as inarea \n";
						}
						
						sql+="		from "+tableName+" a,(" +
						"				select distinct accpackageid,subjectid,AccName,tokenid,subjectfullname1 \n" +
						"				from c_account \n" + 
						"				where 1=1 \n" +
						"				and subyearmonth*12+submonth<= '"+String.valueOf(endTime)+"' \n" +  
						"				and subyearmonth*12+submonth>('"+String.valueOf(endTime)+"'-"+zlEndMonth+") \n" +  
						"				and (subjectfullname1 = '"+SubjectName+"' or subjectfullname1 like '"+SubjectName+"/%') \n" +
						"		) b \n" + 
						"		where 1=1 \n" +
						"		and subyearmonth*12+submonth<= '"+String.valueOf(endTime)+"' \n" +
						"		and a.accpackageid = b.accpackageid \n" +
						"		and a.accid=b.subjectid   \n" +
						"		group by tokenid,AssTotalName1,dataname	 \n" +
						" ) a \n" +
						")b \n" +
						"on a.tokenid=b.tokenid and a.AssTotalName1=b.AssTotalName1 \n" +
						"set "+day[0]+"=if(abs("+strDayUpdate+")>=abs(a.balance),0,if(balance>0,if((balance -occ1) <0,0,if(occ2 is null,balance-occ1,if( (balance-occ1-occ2)<=0,balance-occ1,occ2))),if((balance +occ3) >=0,0,if(occ4 is null,balance+occ3,if( (balance+occ3+occ4)>=0,balance+occ3,(-1)*occ4))))) \n" +
						"  where a.issubject = '"+isSubject+"' and b.tokenid is not null ";
					}
					
//					System.out.println(sql);
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
				}
				
				
				
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
		} catch (Exception e) {
			System.out.println("出错的SQL:"+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	
	//sDate = "1个月`1|2个月`2|3个月`3|半年`6|1年`12|2年`24|3年`36|";
	public String [][] getAnaly(String sDate){
		String [] StrList = sDate.split("\\|");
		String [][] result = new String [StrList.length + 1][4];
		
		String strName = "";	//列字
		String strTime = "";	//时间
		
		for (int i = 0; i < StrList.length; i++) {
			if(StrList[i] !=null && !"".equals(StrList[i])){
				String [] sl = StrList[i].split("`");  //1个月`1
				
				result [i][0] = "Day"+i; //表字
				result [i][1] = sl[0];	//列字			
				if(i==0) {
					result [i][1] = sl[0] + "以内"; 
					result [i][2] = "0"; //开始时间
					result [i][3] = sl[1]; //结束时间
				}else{
					result [i][1] = strName + "~" + result [i][1];
					result [i][2] = strTime; //开始时间
					result [i][3] = sl[1]; //结束时间
				}
				strName = sl[0];
				strTime = sl[1];
			}
		}
		
		result [StrList.length][0] = new String();
		result [StrList.length][1] = new String();
		result [StrList.length][2] = new String();		
		result [StrList.length][3] = new String();	
		
		result [StrList.length][0] = "Day"+StrList.length; //表字		
		result [StrList.length][1] = strName + "以上";
		result [StrList.length][2] = strTime;
		result [StrList.length][3] = "1000";
		
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println("1111:"+ ((12-6)%12+1));
	}
	
}
