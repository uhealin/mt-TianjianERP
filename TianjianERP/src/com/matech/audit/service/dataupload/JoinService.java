package com.matech.audit.service.dataupload;

import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletResponse;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class JoinService {
	private Connection conn = null;
	
	public JoinService(Connection conn) {
		this.conn = conn;
	}
	
	public String check(String customerid,String year) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String table = "asdb_" + customerid;
			String acc = customerid + year;
			
			sql = "select * from k_customer where DepartID = '"+customerid+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String strName = "";
			if(rs.next()){
				strName = rs.getString("DepartName");
			}
			
			sql = " select t2.* " +
			" from(  " +
			" 	select distinct standid as stansubjectid,standkey as stansubjectname,userkey as expsubjectname,level0    " +
			"	from "+table+".z_keyresult " + 
			" ) t1  " +
			" right outer join ( " + 
			" 	select   " +
			" 	ifnull(b.subjectid,a.subjectid) subjectid, " +  
			" 	ifnull(b.accname,a.subjectname) accname " +
			" 	from "+table+".c_accpkgsubject a  " +
			" 	left join "+table+".c_account b on a.subjectid=b.subjectid " + 
			" 	where a.accpackageid= "+acc+" and a.level0=1 and b.submonth=1 " +
			" ) t2 on t1.expsubjectname = t2.accname  " +
			" where t1.stansubjectid is null";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String string  = "";
			while(rs.next()){
				string += "<font color='blue'>［"+rs.getString("subjectid")+"］" + rs.getString("accname") + "</font> 科目没有作完整性；<br>" ;
			}
			if(!"".equals(string)){
				string = "客户：" + strName + "<br>" + string + "<br>"; 
			}
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void insertSubject( HttpServletResponse response,String customerid,String [] customer,int year,String vocationid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF=new ASFuntion();
			
			org.util.Debug.prtOut("join a0=" + new ASFuntion().getCurrentTime());
			
			String acc = customerid + year;
			
			int i = 1;
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			
			out.write("开始合并帐套信息...");
			
			sql = "insert into t_c_AccPackage (AccPackageID,AccPackageType,CustomerID,AccPackageYear,ExportDate,SoftVersion,CurrName) values" +
				" (?,?,?,?, now(),?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, "1000");
			ps.setString(i++, customerid);
			ps.setInt(i++, year);
			ps.setString(i++, "并帐");
			ps.setString(i++, "人民币");
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并帐套信息成功<br>");
			out.flush();
			out.write("开始生成一级科目信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubject \n"
				+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property) \n"
				+" select distinct ?, SubjectID,SubjectName,SubjectFullName,'' as ParentSubjectId,IsLeaf,  Level0,LPAD(Property,2,'0') as Property "
				+" from k_standsubject where VocationID = ? and Level0 = 1 "; 
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a1=" + new ASFuntion().getCurrentTime());
			
			out.write("生成一级科目信息成功<br>");
			out.flush();
			
			out.write("开始生成一级科目的期初数...");
			
			i = 1;
			sql="insert into t_c_accpkgsubjectbegin \n"
				+"(AccPackageID,subjectid,datatype,debitremain,creditremain,accsign,DebitRemainF,CreditRemainF)  \n"
				+" select distinct ?, SubjectID,0 as datatype,0.00 as debitremain,0.00 as creditremain,0 as accsign,0.00 as DebitRemainF,0.00 as CreditRemainF "
				+" from k_standsubject where VocationID = ? and Level0 = 1 "; 
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a1=" + new ASFuntion().getCurrentTime());
			
			out.write("生成一级科目的期初数成功<br>");
			out.flush();
			
			boolean dZfbp = false;
			
			for(int ii = 0 ; ii<customer.length; ii ++){
				
				String acc1 = customer[ii] + year;
				String table = "asdb_" + customer[ii];
				
				i = 1;
				String DepartID = "",Departname = "";
				
				
				sql = "select * from k_customer where DepartID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, customer[ii]);
				rs = ps.executeQuery();
				if(rs.next()){
					DepartID = rs.getString("DepartID");
					Departname = rs.getString("Departname");
					String customerShortName = CHF.showNull(rs.getString("customerShortName"));
					if(!"".equals(customerShortName)) Departname = customerShortName;
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				out.write("<br>合并［"+year+"］年的［"+customer[ii]+"］客户信息");
				out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的科目信息...");
				
				i = 1;
				sql="insert into t_c_accpkgsubject \n"
					+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property,SubjectCode) \n"
					+"\n select "
					+"\n ?, "
					+"\n ifnull(concat(b.subjectid,'-',a.subjectid),a.subjectid) as subjectid , "
					+"\n a.subjectname, "
					+"\n ifnull(concat(b.subjectfullname,'/',a.subjectfullname),a.subjectfullname) as subjectfullname, "
					+"\n ifnull(if(a.ParentSubjectId = '',b.SubjectId,concat(b.subjectid,'-',a.ParentSubjectId)),a.ParentSubjectId) as ParentSubjectId , "
					+"\n if(a.Level0=1 and a.isleaf=1,0, a.IsLeaf) as  Level0,a.Level0 + 1 as Level0,a.Property,sid "
					+"\n from ( "
					+"\n 	select a.accpackageid,ifnull(subjectfullname2,subjectfullname ) as subjectfullname2, "
					+"\n 	concat(?,'-',a.subjectid) as subjectid , "
					+"\n 	concat(?,'-',a.subjectname) as subjectname, "
					+"\n 	concat(?,'-',replace(subjectfullname,'/',concat('/',?,'-'))) as subjectfullname, "
					//+"\n 	if(level0 = 1 ,concat(?,'-',a.subjectname) ,a.subjectname) as subjectname, "
					//+"\n 	concat(?,'-',a.subjectfullname) as subjectfullname, "
					+"\n 	if(ParentSubjectId = '',ParentSubjectId,concat(?,'-',ParentSubjectId)) as ParentSubjectId , "
					+"\n 	IsLeaf,Level0,a.Property,a.subjectid as sid "
					
					+"\n 	from "+table+".c_accpkgsubject a " 
					+"\n 	join "+table+".c_account c "
					+"\n 	on a.accpackageid = ? and c.accpackageid = ? and a.subjectid = c.subjectid and submonth=1 "
					+"\n ) a  "
					+"\n left join k_standsubject b " 
					+"\n on VocationID = ? and b.Level0 = 1 " 
					+"\n and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) "; 
				System.out.println("t_c_accpkgsubject:sql=" +sql);
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, DepartID);
				ps.setString(i++, Departname);
				ps.setString(i++, Departname);
				ps.setString(i++, Departname);
				
				ps.setString(i++, DepartID);
				ps.setString(i++, acc1);
				ps.setString(i++, acc1);
				ps.setString(i++, vocationid);
				ps.execute();
				DbUtil.close(ps);
				
				org.util.Debug.prtOut("join a2 "+ii+"=" + new ASFuntion().getCurrentTime());
				out.write("合并科目信息成功<br>");
				out.flush();
				out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的凭证信息...");
				
				i = 1;
				sql="insert into t_c_voucher \n"
					+"(AccPackageID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId, \n"
					+"Property,creditocc,debitocc) \n"
					+"\n select ?,VoucherID,concat(?,'-',a.TypeID) as TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId,\n"
					+"\n a.Property,creditocc,debitocc "
					+"\n from "+ table +".c_voucher a \n"
					+"\n where AccPackageID = ? ";
				
				System.out.println("t_c_voucher:sql=" +sql);
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, DepartID);
				ps.setString(i++, acc1);
				ps.execute();
				DbUtil.close(ps);
				
				org.util.Debug.prtOut("join a2 "+ii+"=" + new ASFuntion().getCurrentTime());
				out.write("合并凭证信息成功<br>");
				out.flush();
				out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的凭证分录信息...");
				
				i = 1;
				sql="insert into t_c_subjectentry \n"
					+"(AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction, \n"
					+"OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,BankID,Property,subjectname1,SubjectFullName1) \n"
					+"\n select ?,VoucherID,OldVoucherID,concat(?,'-',a.TypeID) as TypeID,VchDate,Serail,Summary,b.SubjectID,Dirction, \n"
					+"\n OccurValue,CurrRate,CurrValue,a.Currency,a.Quantity,a.UnitPrice, \n"
					+"\n a.UnitName,BankID,a.Property,b.subjectname,b.SubjectFullName "
					+"\n from "+ table +".c_subjectentrybegin a,t_c_accpkgsubject b  "
					+"\n where a.accpackageid =? "
					+"\n and b.accpackageid =? "
					+"\n and a.subjectid = b.SubjectCode ";
				
				System.out.println("t_c_subjectentry:sql=" +sql);
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, DepartID);
				ps.setString(i++, acc1);
				ps.setString(i++, acc);
				ps.execute();
				DbUtil.close(ps);				
				
				i = 1;
				sql="insert into t_c_subjectentry \n"
					+"(AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction, \n"
					+"OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,BankID,Property,subjectname1,SubjectFullName1) \n"
					+"\n select ?,OldVoucherID,VoucherID,concat(?,'-',a.TypeID) as TypeID,VchDate,Serail,Summary,b.SubjectID,Dirction, \n"
					+"\n OccurValue,CurrRate,CurrValue,a.Currency,a.Quantity,a.UnitPrice, \n"
					+"\n a.UnitName,BankID,a.Property,b.subjectname,b.SubjectFullName "
					+"\n from "+ table +".c_subjectentry a,t_c_accpkgsubject b  "
					+"\n where a.accpackageid =? "
					+"\n and b.accpackageid =? "
					+"\n and a.subjectid = b.SubjectCode ";
				
				System.out.println("t_c_subjectentry:sql=" +sql);
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, DepartID);
				ps.setString(i++, acc1);
				ps.setString(i++, acc);
				ps.execute();
				DbUtil.close(ps);
				
				org.util.Debug.prtOut("join a2 "+ii+"=" + new ASFuntion().getCurrentTime());
				out.write("合并凭证分录信息成功<br>");
				out.flush();
				out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的期初数信息...");
				
				i = 1;
				sql="insert into t_c_accpkgsubjectbegin \n"
					+"(AccPackageID,subjectid,datatype,debitremain,creditremain,accsign,DebitRemainF,CreditRemainF) \n"
					+"select ?,b.subjectid, a.DataName,a.DebitRemain,a.CreditRemain,a.accsign,a.DebitRemainF,a.CreditRemainF \n"
					+"from ( \n"
					+"	select subjectid,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table +".c_account where accpackageid =? and submonth=1 and isleaf1 = 1 \n"
					+"	union \n"
					+"	select subjectid,DataName,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF  from "+ table +".c_accountall where accpackageid =? and submonth=1  and isleaf1 = 1 \n" 
					+") a ,t_c_accpkgsubject b   \n"
					+"where  b.accpackageid =? \n"
					+"and a.subjectid = b.SubjectCode";
				
				System.out.println("t_c_accpkgsubjectbegin:sql=" +sql);
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, acc1);
				ps.setString(i++, acc1);
				ps.setString(i++, acc);
				ps.execute();
				DbUtil.close(ps);
				
				org.util.Debug.prtOut("join a2 "+ii+"=" + new ASFuntion().getCurrentTime());
				out.write("合并期初数信息成功<br>");
				out.flush();
				
				/**
				 * 检查期初数底层与一级的平衡性
				 */
				i = 1;
				double dZfbp1 = 0, dZfbp2 = 0;
				sql = "select  sum(a.debitremain)+sum(a.creditremain) from "+ table +".c_account a where a.accpackageid=? and a.isleaf1=1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc1);
				rs = ps.executeQuery();
				if(rs.next()){
					dZfbp1 = rs.getDouble(1);
					if (dZfbp1 != 0) {
//						 发现本帐套数据底层科目年初数借贷不平衡";
						DbUtil.close(rs);
						DbUtil.close(ps);
						
						i = 1;
						sql = "select  sum(a.debitremain)+sum(a.creditremain) from "+ table +".c_account a where a.accpackageid=? and a.level1=1 ";
						ps = conn.prepareStatement(sql);
						ps.setString(i++, acc1);
						rs = ps.executeQuery();
						if(rs.next()){
							dZfbp2 = rs.getDouble(1);
							
							if (dZfbp2 != 0) { 	
								//发现本帐套数据一级科目年初数借贷不平衡
								
								out.write("<br>合并期初数发现底层科目年初数借贷不平衡：" + dZfbp1 + "<br>");
								out.write("合并期初数发现一级科目年初数借贷不平衡：" + dZfbp2 + "<br>"); 
								out.write("因为科目年初数借贷不平衡，所以并帐时会导致并帐的期初数也是借贷不平衡<br>");
								out.flush();
								dZfbp = true; //总不平、分也不平，也要加出一级的期初
							}else{
								dZfbp = true;
							}
						}
						
					}
				}
				
				if(dZfbp){
//					本帐套数据一级科目年初数借贷是平的，但底层科目年初数借贷不平衡
					i = 1;
					sql = " update  t_c_accpkgsubjectbegin a ,(" +
						"	select b.subjectid, a.DataName,sum(a.DebitRemain) as DebitRemain,sum(a.CreditRemain) as CreditRemain,a.accsign,sum(a.DebitRemainF) as DebitRemainF,sum(a.CreditRemainF) as CreditRemainF \n"  +
						"	from ( \n" +
						"		select subjectid,subjectfullname2,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table +".c_account where accpackageid =? and submonth=1 and level1=1 \n" +
						"	) a \n" +
						"	left join k_standsubject b " +  
						"	on VocationID = ? and b.Level0 = 1 " + 
						"	and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) " +
						"	group by b.subjectid " +
						
						" ) b " +
						" set a.DebitRemain = a.DebitRemain + b.DebitRemain,a.CreditRemain = a.CreditRemain + b.CreditRemain," +
						" a.DebitRemainF = a.DebitRemainF + b.DebitRemainF,a.CreditRemainF = a.CreditRemainF + b.CreditRemainF " +
						" where a.accpackageid =? and a.Datatype = b.DataName and b.subjectid = a.subjectid ";
					ps = conn.prepareStatement(sql);
					ps.setString(i++, acc1);
					ps.setString(i++, vocationid);
					ps.setString(i++, acc);
					ps.execute();
				}
				
				
				i = 1;
				sql = "update t_c_accpkgsubject set SubjectCode = '' where accpackageid =?";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.execute();
				DbUtil.close(ps);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void insertSubject2(HttpServletResponse response,String customerid,String [] customer,int year,String vocationid,String fBMonth1,String fEMonth1,String fBMonth2,String fEMonth2) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF=new ASFuntion();
			
			org.util.Debug.prtOut("join a0=" + new ASFuntion().getCurrentTime());
			
			String acc = customerid + year;
			int i = 1;
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			
			out.write("开始合并帐套信息...");
			
			sql = "insert into t_c_AccPackage (AccPackageID,AccPackageType,CustomerID,AccPackageYear,ExportDate,SoftVersion,CurrName) values" +
				" (?,?,?,?, now(),?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, "1000");
			ps.setString(i++, customerid);
			ps.setInt(i++, year);
			ps.setString(i++, "并帐");
			ps.setString(i++, "人民币");
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并帐套信息成功<br>");
			out.flush();
			out.write("开始生成一级科目信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubject \n"
				+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property) \n"
				+" select distinct ?, SubjectID,SubjectName,SubjectFullName,'' as ParentSubjectId,IsLeaf,  Level0,LPAD(Property,2,'0') as Property "
				+" from k_standsubject where VocationID = ? and Level0 = 1 "; 
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a1=" + new ASFuntion().getCurrentTime());
			
			out.write("生成一级科目信息成功<br>");
			out.flush();
			
			out.write("开始生成一级科目的期初数...");
			
			i = 1;
			sql="insert into t_c_accpkgsubjectbegin \n"
				+"(AccPackageID,subjectid,datatype,debitremain,creditremain,accsign,DebitRemainF,CreditRemainF)  \n"
				+" select distinct ?, SubjectID,0 as datatype,0.00 as debitremain,0.00 as creditremain,0 as accsign,0.00 as DebitRemainF,0.00 as CreditRemainF "
				+" from k_standsubject where VocationID = ? and Level0 = 1 "; 
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a1=" + new ASFuntion().getCurrentTime());
			
			out.write("生成一级科目的期初数成功<br>");
			out.flush();
			
			String [] acc1 = new String[customer.length];
			String [] table = new String[customer.length];
			String [] DepartID = new String[customer.length];
			String [] Departname = new String[customer.length];
			
			for(int ii = 0 ; ii<customer.length; ii ++){
				
				acc1[ii] = new String();
				table[ii] = new String();
				DepartID[ii] = new String();
				Departname[ii] = new String();
				
				acc1[ii] = customer[ii] + year;
				table[ii] = "asdb_" + customer[ii];
				
				i = 1;
				sql = "select * from k_customer where DepartID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, customer[ii]);
				rs = ps.executeQuery();
				if(rs.next()){
					DepartID[ii] = rs.getString("DepartID");
					Departname[ii] = rs.getString("Departname");
					String customerShortName = CHF.showNull(rs.getString("customerShortName"));
					if(!"".equals(customerShortName)) Departname[ii] = customerShortName;
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			
			out.write("<br>合并［"+year+"］年的［"+customer[0]+"］客户信息<br>");
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的科目信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubject \n"
				+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property,SubjectCode) \n"
				+"\n select "
				+"\n ?, "
				+"\n ifnull(concat(b.subjectid,'-',a.subjectid),a.subjectid) as subjectid , "
				+"\n a.subjectname, "
				+"\n ifnull(concat(b.subjectfullname,'/',a.subjectfullname),a.subjectfullname) as subjectfullname, "
				+"\n ifnull(if(a.ParentSubjectId = '',b.SubjectId,concat(b.subjectid,'-',a.ParentSubjectId)),a.ParentSubjectId) as ParentSubjectId , "
				+"\n if(a.Level0=1 and a.isleaf=1,0, a.IsLeaf) as  Level0,a.Level0 + 1 as Level0,a.Property,sid "
				+"\n from ( "
				+"\n 	select a.accpackageid,ifnull(subjectfullname2,subjectfullname ) as subjectfullname2, "
				+"\n 	concat(?,'-',a.subjectid) as subjectid , "
				+"\n 	concat(?,'-',a.subjectname) as subjectname, "
				+"\n 	concat(?,'-',replace(subjectfullname,'/',concat('/',?,'-'))) as subjectfullname, "
				//+"\n 	if(level0 = 1 ,concat(?,'-',a.subjectname) ,a.subjectname) as subjectname, "
				//+"\n 	concat(?,'-',a.subjectfullname) as subjectfullname, "
				+"\n 	if(ParentSubjectId = '',ParentSubjectId,concat(?,'-',ParentSubjectId)) as ParentSubjectId , "
				+"\n 	IsLeaf,Level0,a.Property,a.subjectid as sid "
				
				+"\n 	from "+table[0]+".c_accpkgsubject a " 
				+"\n 	join "+table[0]+".c_account c "
				+"\n 	on a.accpackageid = ? and c.accpackageid = ? and a.subjectid = c.subjectid and submonth=1 "
				+"\n ) a  "
				+"\n left join k_standsubject b " 
				+"\n on VocationID = ? and b.Level0 = 1 " 
				+"\n and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) "; 
			System.out.println("t_c_accpkgsubject:sql=" +sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, DepartID[0]);
			ps.setString(i++, Departname[0]);
			ps.setString(i++, Departname[0]);
			ps.setString(i++, Departname[0]);
			
			ps.setString(i++, DepartID[0]);
			ps.setString(i++, acc1[0]);
			ps.setString(i++, acc1[0]);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a2 =" + new ASFuntion().getCurrentTime());
			out.write("合并科目信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的凭证信息...");
			
			i = 1;
			sql="insert into t_c_voucher \n"
				+"(AccPackageID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId, \n"
				+"Property,creditocc,debitocc) \n"
				+"\n select ?,VoucherID,concat(?,'-',a.TypeID) as TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId,\n"
				+"\n a.Property,creditocc,debitocc "
				+"\n from "+ table[0] +".c_voucher a \n"
				+"\n where AccPackageID = ? "
				+"\n and month(VchDate)>= ? "
				+"\n and month(VchDate)<= ? ";
			
			System.out.println("t_c_voucher:sql=" +sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, DepartID[0]);
			ps.setString(i++, acc1[0]);
			
			ps.setString(i++, fBMonth1);
			ps.setString(i++, fEMonth1);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a2 =" + new ASFuntion().getCurrentTime());
			out.write("合并凭证信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的凭证分录信息...");
			
			i = 1;
			sql="insert into t_c_subjectentry \n"
				+"(AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction, \n"
				+"OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,BankID,Property,subjectname1,SubjectFullName1) \n"
				+"\n select ?,OldVoucherID,VoucherID,concat(?,'-',a.TypeID) as TypeID,VchDate,Serail,Summary,b.SubjectID,Dirction, \n"
				+"\n OccurValue,CurrRate,CurrValue,a.Currency,a.Quantity,a.UnitPrice, \n"
				+"\n a.UnitName,BankID,a.Property,b.subjectname,b.SubjectFullName "
				+"\n from "+ table[0] +".c_subjectentry a,t_c_accpkgsubject b  "
				+"\n where a.accpackageid =? "
				+"\n and b.accpackageid =? "
				+"\n and month(a.VchDate)>= ? "
				+"\n and month(a.VchDate)<= ? "
				+"\n and a.subjectid = b.SubjectCode ";
			
			System.out.println("t_c_subjectentry:sql=" +sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, DepartID[0]);
			ps.setString(i++, acc1[0]);
			ps.setString(i++, acc);
			
			ps.setString(i++, fBMonth1);
			ps.setString(i++, fEMonth1);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a2 =" + new ASFuntion().getCurrentTime());
			out.write("合并凭证分录信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的期初数信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubjectbegin \n"
				+"(AccPackageID,subjectid,datatype,debitremain,creditremain,accsign,DebitRemainF,CreditRemainF) \n"
				+"select ?,b.subjectid, a.DataName,a.DebitRemain,a.CreditRemain,a.accsign,a.DebitRemainF,a.CreditRemainF \n"
				+"from ( \n"
				+"	select subjectid,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table[0] +".c_account where accpackageid =? and submonth=?  and isleaf1 = 1 \n"
				+"	union \n"
				+"	select subjectid,DataName,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF  from "+ table[0] +".c_accountall where accpackageid =? and submonth=?  and isleaf1 = 1 \n"
				+") a ,t_c_accpkgsubject b   \n"
				+"where  b.accpackageid =? \n"
				+"and a.subjectid = b.SubjectCode";
			
			System.out.println("t_c_accpkgsubjectbegin:sql=" +sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, acc1[0]);
			ps.setString(i++, fBMonth1);
			ps.setString(i++, acc1[0]);
			ps.setString(i++, fBMonth1);
			ps.setString(i++, acc);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 检查期初数底层与一级的平衡性
			 */
			i = 1;
			boolean dZfbp = false;
			double dZfbp1 = 0, dZfbp2 = 0;
			sql = "select sum(a.debitremain)+sum(a.creditremain) from "+ table[0] +".c_account a where a.accpackageid=? and a.isleaf1=1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc1[0]);
			rs = ps.executeQuery();
			if(rs.next()){
				dZfbp1 = rs.getDouble(1);
				if (dZfbp1 != 0) {
//					 发现本帐套数据底层科目年初数借贷不平衡";
					DbUtil.close(rs);
					DbUtil.close(ps);
					
					i = 1;
					sql = "select  sum(a.debitremain)+sum(a.creditremain) from "+ table[0] +".c_account a where a.accpackageid=? and a.level1=1 ";
					ps = conn.prepareStatement(sql);
					ps.setString(i++, acc1[0]);
					rs = ps.executeQuery();
					if(rs.next()){
						dZfbp2 = rs.getDouble(1);
						
						if (dZfbp2 != 0) { 	
							//发现本帐套数据一级科目年初数借贷不平衡
							
							out.write("<br>合并期初数发现底层科目年初数借贷不平衡：" + dZfbp1 + "<br>");
							out.write("合并期初数发现一级科目年初数借贷不平衡：" + dZfbp2 + "<br>"); 
							out.write("因为科目年初数借贷不平衡，所以并帐时会导致并帐的期初数也是借贷不平衡<br>");
							out.flush();
							dZfbp = true; //总不平、分也不平，也要加出一级的期初
						}else{
							dZfbp = true;
						}
					}
					
				}
			}
			
			if(dZfbp){
//				本帐套数据一级科目年初数借贷是平的，但底层科目年初数借贷不平衡
				i = 1;
				sql = " update  t_c_accpkgsubjectbegin a ,(" +
					"	select b.subjectid, a.DataName,sum(a.DebitRemain) as DebitRemain,sum(a.CreditRemain) as CreditRemain,a.accsign,sum(a.DebitRemainF) as DebitRemainF,sum(a.CreditRemainF) as CreditRemainF \n"  +
					"	from ( \n" +
					"		select subjectid,subjectfullname2,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table[0] +".c_account where accpackageid =? and submonth=1 and level1=1 \n" +
					"	) a \n" +
					"	left join k_standsubject b " +  
					"	on VocationID = ? and b.Level0 = 1 " + 
					"	and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) " +
					"	group by b.subjectid " +
					
					" ) b " +
					" set a.DebitRemain = a.DebitRemain + b.DebitRemain,a.CreditRemain = a.CreditRemain + b.CreditRemain," +
					" a.DebitRemainF = a.DebitRemainF + b.DebitRemainF,a.CreditRemainF = a.CreditRemainF + b.CreditRemainF " +
					" where a.accpackageid =? and a.Datatype = b.DataName and b.subjectid = a.subjectid ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc1[0]);
				ps.setString(i++, vocationid);
				ps.setString(i++, acc);
				ps.execute();
			}
			
			org.util.Debug.prtOut("join a2 =" + new ASFuntion().getCurrentTime());
			out.write("合并期初数信息成功<br>");
			out.flush();
			
			/**
			 * 下半年
			 */
			
			out.write("<br>合并［"+year+"］年的［"+customer[1]+"］客户信息<br>");
			out.write("开始合并［"+customer[1]+"］客户的［"+year+"］年的科目信息...");
			i = 1;
			sql="insert into t_c_accpkgsubject \n"
				+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property,SubjectCode) \n"
				+"\n select "
				+"\n ?, "
				+"\n ifnull(concat(b.subjectid,'-',a.subjectid),a.subjectid) as subjectid , "
				+"\n a.subjectname, "
				+"\n ifnull(concat(b.subjectfullname,'/',a.subjectfullname),a.subjectfullname) as subjectfullname, "
				+"\n ifnull(if(a.ParentSubjectId = '',b.SubjectId,concat(b.subjectid,'-',a.ParentSubjectId)),a.ParentSubjectId) as ParentSubjectId , "
				+"\n if(a.Level0=1 and a.isleaf=1,0, a.IsLeaf) as  Level0,a.Level0 + 1 as Level0,a.Property,sid "
				+"\n from ( "
				+"\n 	select a.accpackageid,ifnull(subjectfullname2,subjectfullname ) as subjectfullname2, "
				+"\n 	concat(?,'-',a.subjectid) as subjectid , "
				+"\n 	concat(?,'-',a.subjectname) as subjectname, "
				+"\n 	concat(?,'-',replace(subjectfullname,'/',concat('/',?,'-'))) as subjectfullname, "
				//+"\n 	if(level0 = 1 ,concat(?,'-',a.subjectname) ,a.subjectname) as subjectname, "
				//+"\n 	concat(?,'-',a.subjectfullname) as subjectfullname, "
				+"\n 	if(ParentSubjectId = '',ParentSubjectId,concat(?,'-',ParentSubjectId)) as ParentSubjectId , "
				+"\n 	IsLeaf,Level0,a.Property,a.subjectid as sid "
				
				+"\n 	from "+table[1]+".c_accpkgsubject a " 
				+"\n 	join "+table[1]+".c_account c "
				+"\n 	on a.accpackageid = ? and c.accpackageid = ? and a.subjectid = c.subjectid and submonth=1 "
				+"\n ) a  "
				+"\n left join k_standsubject b " 
				+"\n on VocationID = ? and b.Level0 = 1 " 
				+"\n and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) "; 
			System.out.println("t_c_accpkgsubject:sql=" +sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc1[1]);
			ps.setString(i++, DepartID[1]);
			ps.setString(i++, Departname[1]);
			ps.setString(i++, Departname[1]);
			ps.setString(i++, Departname[1]);
			
			ps.setString(i++, DepartID[1]);
			ps.setString(i++, acc1[1]); 
			ps.setString(i++, acc1[1]);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a2 =" + new ASFuntion().getCurrentTime());
			out.write("合并科目信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[1]+"］客户的［"+year+"］年的凭证信息...");
			
			i = 1;
			sql="insert into t_c_voucher \n"
				+"(AccPackageID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId, \n"
				+"Property,creditocc,debitocc) \n"
				+"\n select ?,VoucherID,concat(?,'-',a.TypeID) as TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId,\n"
				+"\n a.Property,creditocc,debitocc "
				+"\n from "+ table[1] +".c_voucher a \n"
				+"\n where AccPackageID = ? "
				+"\n and month(VchDate)>= ? "
				+"\n and month(VchDate)<= ? ";
			
			System.out.println("t_c_voucher:sql=" +sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, DepartID[1]);
			ps.setString(i++, acc1[1]);
			
			ps.setString(i++, fBMonth2);
			ps.setString(i++, fEMonth2);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a2 =" + new ASFuntion().getCurrentTime());
			out.write("合并凭证信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[1]+"］客户的［"+year+"］年的凭证分录信息...");
			
			i = 1;
			sql="insert into t_c_subjectentry \n"
				+"(AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction, \n"
				+"OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,BankID,Property,subjectname1,SubjectFullName1) \n"
				+"\n select ?,OldVoucherID,VoucherID,concat(?,'-',a.TypeID) as TypeID,VchDate,Serail,Summary,b.SubjectID,Dirction, \n"
				+"\n OccurValue,CurrRate,CurrValue,a.Currency,a.Quantity,a.UnitPrice, \n"
				+"\n a.UnitName,BankID,a.Property,b.subjectname,b.SubjectFullName "
				+"\n from "+ table[1] +".c_subjectentry a,t_c_accpkgsubject b  "
				+"\n where a.accpackageid =? "
				+"\n and b.accpackageid =? "
				+"\n and month(a.VchDate)>= ? "
				+"\n and month(a.VchDate)<= ? "
				+"\n and a.subjectid = b.SubjectCode ";
			
			System.out.println("t_c_subjectentry:sql=" +sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, DepartID[1]);
			ps.setString(i++, acc1[1]);
			ps.setString(i++, acc1[1]);
			
			ps.setString(i++, fBMonth2);
			ps.setString(i++, fEMonth2);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a2 =" + new ASFuntion().getCurrentTime());
			out.write("合并凭证分录信息成功<br>");
			out.flush();
			
			i = 1;
			sql = "update t_c_accpkgsubject set accpackageid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.execute();
			DbUtil.close(ps);
			
			i = 1;
			sql = "update t_c_accpkgsubject set SubjectCode = '' where accpackageid =?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.execute();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
		
		
	public void insertSubject2(HttpServletResponse response,String customerid,String [] customer,int year,String vocationid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			String acc = customerid + year;
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			
			out.write("开始合并帐套信息...");
			
			int i = 1;
			sql = "insert into t_c_AccPackage (AccPackageID,AccPackageType,CustomerID,AccPackageYear,ExportDate,SoftVersion,CurrName) values" +
				" (?,?,?,?, now(),?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, "1000");
			ps.setString(i++, customerid);
			ps.setInt(i++, year);
			ps.setString(i++, "并帐");
			ps.setString(i++, "人民币");
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并帐套信息成功<br>");
			out.flush();
			out.write("开始生成一级科目信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubject \n"
				+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property) \n"
				+" select distinct ?, SubjectID,SubjectName,SubjectFullName,'' as ParentSubjectId,IsLeaf,  Level0,LPAD(Property,2,'0') as Property "
				+" from k_standsubject where VocationID = ? and Level0 = 1 "; 
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
						
			out.write("生成一级科目信息成功<br>");
			out.flush();
			
			out.write("开始生成一级科目的期初数...");
			
			i = 1;
			sql="insert into t_c_accpkgsubjectbegin \n"
				+"(AccPackageID,subjectid,datatype,debitremain,creditremain,accsign,DebitRemainF,CreditRemainF)  \n"
				+" select distinct ?, SubjectID,0 as datatype,0.00 as debitremain,0.00 as creditremain,0 as accsign,0.00 as DebitRemainF,0.00 as CreditRemainF "
				+" from k_standsubject where VocationID = ? and Level0 = 1 "; 
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			org.util.Debug.prtOut("join a1=" + new ASFuntion().getCurrentTime());
			
			out.write("生成一级科目的期初数成功<br>");
			out.flush();
			
			String acc1 = "";
			String table1 = "";
			String customerId1 = "";
			String acc2 = "";
			String table2 = "";
			String customerId2 = "";

			int maxMonth =0;
			
			
			
			for(int ii = 0 ; ii<customer.length; ii ++){				
				String table = "asdb_" + customer[ii];
				
				sql = "select min(month(VchDate)) from "+table+".c_subjectentry where Property<>'199'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				int iMonth = 0;
				while(rs.next()){
					iMonth = Integer.parseInt(rs.getString(1));
				}
				ps.close();
				if(iMonth>1){	
					acc2 = customer[ii] + year;
					table2 = table;
					maxMonth = iMonth;
					customerId2 = customer[ii]; 
				}else{
					acc1 = customer[ii] + year;
					table1 = table;
					customerId1 = customer[ii]; 
				}
			}
			
			//合并上半年数据
			out.write("<br>合并［"+year+"］年的［"+customer[0]+"］客户信息<br>");
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的科目信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubject \n"
				+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property,SubjectCode) \n"
				+" select "
				+" AccPackageID, "
				+" ifnull(concat(b.subjectid,'-',a.subjectid),a.subjectid) as subjectid , "
				+" a.subjectname, "
				+" ifnull(concat(b.subjectfullname,'/',a.subjectfullname),a.subjectfullname) as subjectfullname, "
				+" ifnull(if(a.ParentSubjectId = '',b.SubjectId,concat(b.subjectid,'-',a.ParentSubjectId)),a.ParentSubjectId) as ParentSubjectId , "
				+" if(a.Level0=1 and a.isleaf=1,0, a.IsLeaf) as  Level0,a.Level0 + 1 as Level0,a.Property,sid "
				+" from ( "
				+" 	select a.accpackageid,ifnull(subjectfullname2,subjectfullname ) as subjectfullname2, "
				+" 	concat(DepartID,'-',a.subjectid) as subjectid , "
				
				+" 	concat(Departname,'-',a.subjectname) as subjectname, "
				+" 	concat(Departname,'-',replace(a.subjectfullname,'/',concat('/',Departname,'-'))) as subjectfullname, "
				//+" 	if(level0 = 1 ,concat(Departname,'-',a.subjectname) ,a.subjectname) as subjectname, "
				//+" 	concat(Departname,'-',a.subjectfullname) as subjectfullname, "
				
				+" 	if(ParentSubjectId = '',ParentSubjectId,concat(DepartID,'-',ParentSubjectId)) as ParentSubjectId , "
				+" 	IsLeaf,Level0,a.Property,a.subjectid as sid "
				
				+" 	from "+table1+".c_accpkgsubject a " 
				+" 	join k_customer b  "
				+" 	on a.accpackageid =? and DepartID = ? and a.accpackageid like concat(DepartID,'%') "
				+" 	join "+table1+".c_account c "
				+" 	on a.accpackageid = ? and c.accpackageid = ? and a.subjectid = c.subjectid and submonth=1 "
				+" ) a  "
				+" left join k_standsubject b " 
				+" on VocationID = ? and b.Level0 = 1 " 
				+" and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) "; 
			ps = conn.prepareStatement(sql);
//			ps.setString(i++, acc);
			ps.setString(i++, acc1);
			ps.setString(i++, customerId1);
			ps.setString(i++, acc1);
			ps.setString(i++, acc1);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并科目信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的凭证信息...");
			
			i = 1;
			sql="insert into t_c_voucher \n"
				+"(AccPackageID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId, \n"
				+"Property,creditocc,debitocc) \n"
				+"select AccPackageID,VoucherID,concat(DepartID,'-',a.TypeID) as TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId,\n"
				+"a.Property,creditocc,debitocc "
				+"from "+ table1 +".c_voucher a \n"
				+"join k_customer b  "
				+"on a.accpackageid =? and DepartID = ? and a.accpackageid like concat(DepartID,'%') "
				+"where AccPackageID = ?  "
				+"and month(VchDate)<"+maxMonth;
			ps = conn.prepareStatement(sql);
//			ps.setString(i++, acc);
			ps.setString(i++, acc1);
			ps.setString(i++, customerId1);
			ps.setString(i++, acc1);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并凭证信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的凭证分录信息...");
			
			i = 1;
			sql="insert into t_c_subjectentry \n"
				+"(AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction, \n"
				+"OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,BankID,Property,subjectname1,SubjectFullName1) \n"
				+"select a.AccPackageID,OldVoucherID,VoucherID,concat(DepartID,'-',a.TypeID) as TypeID,VchDate,Serail,Summary,b.SubjectID,Dirction, \n"
				+"OccurValue,CurrRate,CurrValue,a.Currency,a.Quantity,a.UnitPrice, \n"
				+"a.UnitName,BankID,a.Property,b.subjectname,b.SubjectFullName "
				+"from "+ table1 +".c_subjectentry a,t_c_accpkgsubject b , k_customer c "
				+"where a.accpackageid =? "
				+"and b.accpackageid =? "
				+"and c.DepartID =? "
				+"and a.subjectid = b.SubjectCode "
				+"and a.accpackageid like concat(DepartID,'%') "
				+"and month(VchDate)<"+maxMonth;
			ps = conn.prepareStatement(sql);
//			ps.setString(i++, acc);
			ps.setString(i++, acc1);
			ps.setString(i++, acc1);
			ps.setString(i++, customerId1);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并凭证分录信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[0]+"］客户的［"+year+"］年的期初数信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubjectbegin \n"
				+"(AccPackageID,subjectid,datatype,debitremain,creditremain,accsign,DebitRemainF,CreditRemainF) \n"
				+"select ?,b.subjectid, a.DataName,a.DebitRemain,a.CreditRemain,a.accsign,a.DebitRemainF,a.CreditRemainF \n"
				+"from ( \n"
				+"	select subjectid,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table1 +".c_account where accpackageid =? and submonth=1 \n"
				+"	union \n"
				+"	select subjectid,DataName,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF  from "+ table1 +".c_accountall where accpackageid =? and submonth=1 \n"
				+") a ,t_c_accpkgsubject b   \n"
				+"where  b.accpackageid =? \n"
				+"and a.subjectid = b.SubjectCode";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.setString(i++, acc1);
			ps.setString(i++, acc1);
			ps.setString(i++, acc1);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并期初数信息成功<br>");
			out.flush();
			
//			i = 1;
//			sql = "update t_c_accpkgsubject set SubjectCode = '' where accpackageid =?";
//			ps = conn.prepareStatement(sql);
//			ps.setString(i++, acc);
//			ps.execute();
//			DbUtil.close(ps);
			
			//合并下半年的数据
			
			out.write("<br>合并［"+year+"］年的［"+customer[1]+"］客户信息<br>");
			out.write("开始合并［"+customer[1]+"］客户的［"+year+"］年的科目信息...");
			
			i = 1;
			sql="insert into t_c_accpkgsubject \n"
				+"(AccPackageID,SubjectID,SubjectName, SubjectFullName, ParentSubjectId, IsLeaf,  Level0,  Property,SubjectCode) \n"
				+" select "
				+" AccPackageID, "
				+" ifnull(concat(b.subjectid,'-',a.subjectid),a.subjectid) as subjectid , "
				+" a.subjectname, "
				+" ifnull(concat(b.subjectfullname,'/',a.subjectfullname),a.subjectfullname) as subjectfullname, "
				+" ifnull(if(a.ParentSubjectId = '',b.SubjectId,concat(b.subjectid,'-',a.ParentSubjectId)),a.ParentSubjectId) as ParentSubjectId , "
				+" if(a.Level0=1 and a.isleaf=1,0, a.IsLeaf) as  Level0,a.Level0 + 1 as Level0,a.Property,sid "
				+" from ( "
				+" 	select a.accpackageid,ifnull(subjectfullname2,subjectfullname ) as subjectfullname2, "
				+" 	concat(DepartID,'-',a.subjectid) as subjectid , "
				
				+" 	concat(Departname,'-',a.subjectname) as subjectname, "
				+" 	concat(Departname,'-',replace(a.subjectfullname,'/',concat('/',Departname,'-'))) as subjectfullname, "
				//+" 	if(level0 = 1 ,concat(Departname,'-',a.subjectname) ,a.subjectname) as subjectname, "
				//+" 	concat(Departname,'-',a.subjectfullname) as subjectfullname, "
				
				+" 	if(ParentSubjectId = '',ParentSubjectId,concat(DepartID,'-',ParentSubjectId)) as ParentSubjectId , "
				+" 	IsLeaf,Level0,a.Property,a.subjectid as sid "
				
				+" 	from "+table2+".c_accpkgsubject a " 
				+" 	join k_customer b  "
				+" 	on a.accpackageid =? and DepartID = ? and a.accpackageid like concat(DepartID,'%') "
				+" 	join "+table2+".c_account c "
				+" 	on a.accpackageid = ? and c.accpackageid = ? and a.subjectid = c.subjectid and submonth=1 "
				+" ) a  "
				+" left join k_standsubject b " 
				+" on VocationID = ? and b.Level0 = 1 " 
				+" and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) "; 
			ps = conn.prepareStatement(sql);
//			ps.setString(i++, acc);
			ps.setString(i++, acc2);
			ps.setString(i++, customerId2);
			ps.setString(i++, acc2);
			ps.setString(i++, acc2);
			ps.setString(i++, vocationid);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并科目信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[1]+"］客户的［"+year+"］年的凭证信息...");
			
			i = 1;
			sql="insert into t_c_voucher \n"
				+"(AccPackageID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId, \n"
				+"Property,creditocc,debitocc) \n"
				+"select AccPackageID,VoucherID,concat(DepartID,'-',a.TypeID) as TypeID,VchDate,FillUser,AuditUser,KeepUser,Director,AffixCount,Description,DoubtUserId,\n"
				+"a.Property,creditocc,debitocc "
				+"from "+ table2 +".c_voucher a \n"
				+"join k_customer b  "
				+"on a.accpackageid =? and DepartID = ? and a.accpackageid like concat(DepartID,'%') "
				+"where AccPackageID = ?  "
				+"and month(VchDate)>="+maxMonth;
			ps = conn.prepareStatement(sql);
//			ps.setString(i++, acc);
			ps.setString(i++, acc2);
			ps.setString(i++, customerId2);
			ps.setString(i++, acc2);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并凭证信息成功<br>");
			out.flush();
			out.write("开始合并［"+customer[1]+"］客户的［"+year+"］年的凭证分录信息...");
			
			i = 1;
			sql="insert into t_c_subjectentry \n"
				+"(AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction, \n"
				+"OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,BankID,Property,subjectname1,SubjectFullName1) \n"
				+"select a.AccPackageID,OldVoucherID,VoucherID,concat(DepartID,'-',a.TypeID) as TypeID,VchDate,Serail,Summary,b.SubjectID,Dirction, \n"
				+"OccurValue,CurrRate,CurrValue,a.Currency,a.Quantity,a.UnitPrice, \n"
				+"a.UnitName,BankID,a.Property,b.subjectname,b.SubjectFullName \n"
				+"from "+ table2 +".c_subjectentry a,t_c_accpkgsubject b , k_customer c \n"
				+"where a.accpackageid =? "
				+"and b.accpackageid =? "
				+"and c.DepartID =? "
				+"and a.subjectid = b.SubjectCode "
				+"and a.accpackageid like concat(DepartID,'%') "
				+"and month(VchDate)>="+maxMonth;
			ps = conn.prepareStatement(sql);
//			ps.setString(i++, acc);
			ps.setString(i++, acc2);
			ps.setString(i++, acc2);
			ps.setString(i++, customerId2);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("合并凭证分录信息成功<br>");
			out.flush();
			
			i = 1;
			sql = "update t_c_accpkgsubject set SubjectCode = '' ,accpackageid =?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.execute();
			DbUtil.close(ps);
					
			i = 1;
			sql = "update t_c_voucher set  accpackageid =?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.execute();
			DbUtil.close(ps);
			
			i = 1;
			sql = "update t_c_subjectentry set  accpackageid =?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.execute();
			DbUtil.close(ps);
			
			i = 1;
			sql = "update t_c_accpkgsubjectbegin set accpackageid =?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, acc);
			ps.execute();
			DbUtil.close(ps);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void insertAssitem(HttpServletResponse response,String customerid,String [] customer,String assitemList1,String assitemList2,int year,String joinType,String vocationid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		
		String[] assitemList1s = assitemList1.split(",");
		String[] assitemList2s = assitemList2.split(",");
		
		try {
			sql = "alter table t_c_assitembegin add index AccID (AccID)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			sql = "alter table t_c_assitembegin add index AssItemID (AssItemID)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
		} catch (Exception e) {}
		
		
		try{
			String acc = customerid + year;

			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			
			out.write("<br>开始合并核算体系...");
			out.flush();
//-----------------------------合并核算体系-----------------------------
			//插入一级核算体系
			for(int ii = 0 ; ii<assitemList1s.length; ii ++){
				String[] assitemTypeList = assitemList1s[ii].split("`");
				int i = 1;
				sql = "insert into t_c_assitem (AccPackageID,accid,AssItemID,AssItemName,AssTotalName,ParentAssItemId,IsLeaf,Level0,UomUnit,Curr,Property) values" +
					" (?,'0000',?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, assitemTypeList[0]);
				ps.setString(i++, assitemTypeList[1]);
				ps.setString(i++, assitemTypeList[1]);
				ps.setString(i++, "");
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setString(i++, "");
				ps.setString(i++, "");
				ps.setString(i++, "1");
				ps.execute();
				DbUtil.close(ps);
			}
			
			//直接将原单位的核算体系照搬过来
			for(int ii=0 ; ii<customer.length ; ii++){
				
				String table = "asdb_" + customer[ii];
				String acc1 = customer[ii] + year;
				
				int i = 1;
				sql = "insert into t_c_assitem (AccPackageID,accid,AssItemID,AssItemName,AssTotalName,ParentAssItemId,\n"
					+ "DebitRemain,CreditRemain,IsLeaf,Level0,UomUnit,Curr,Property) \n"
					+ "select distinct AccPackageID,'0000',AssItemID,AssItemName,AssTotalName,ParentAssItemId,\n"
					+ "DebitRemain,CreditRemain,IsLeaf,Level0,UomUnit,Curr,Property \n"
					+ "from "+ table +".c_assitem where AccPackageID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc1);
				ps.execute();
				DbUtil.close(ps);
				
				//获取单位名称
				i = 1;
				sql = "select * from k_customer where DepartID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, customer[ii]);
				rs = ps.executeQuery();
				String yCustomerName = "",DepartID ="";
				while(rs.next()){
					DepartID = rs.getString("DepartID");
					yCustomerName = rs.getString("Departname");
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				
				//更新核算名称
				sql = "update t_c_assitem set AssItemName=concat('"+yCustomerName+"-"+"',AssItemName) \n" 
					+ "where AccPackageID="+acc1 +" and level0=1";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
//				更新核算对应科目编号			
//				sql = "update t_c_assitem a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customer[ii]+"-',-1)=a.AccID \n" 
//					+ "set a.AccID=b.SubjectID where b.level0 <> 1 ";			
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
			}
			
//			开始更新核算体系
//				1.更新核算全路径
//				2.更新父级核算编号
//				3.更新核算编号
//				assitemTypeList2[1]:原单位单位编号
//				assitemTypeList2[2]:原单位一级核算编号
//				assitemTypeList2[0]:合并后的一级核算			
			for(int ii = 0 ; ii<assitemList2s.length; ii ++){
				String[] assitemTypeList2 = assitemList2s[ii].split("`");
				
//				1.更核算全路径名称
				
				//获取一级核算名称
				String cAssItemName = "";
				for(int j=0 ; j<assitemList1s.length ; j++){
					String[] assitemTypeList = assitemList1s[j].split("`");
					if(assitemTypeList[0]==assitemTypeList2[0]||assitemTypeList[0].equals(assitemTypeList2[0])){
						cAssItemName=assitemTypeList[1];
					}
				}
				
//				获取单位名称
				sql = "select DepartName from asdb.k_customer where DepartID="+assitemTypeList2[1];
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				String yCustomerName = "";
				while(rs.next()){
					yCustomerName = rs.getString(1);
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				/**
				 * 先更新一级的父核算
				 */
				sql = "update t_c_assitem set ParentAssItemId='"+assitemTypeList2[0]+"' \n" 
				+ "where AccPackageID="+assitemTypeList2[1]+year +" and AssItemID = '"+assitemTypeList2[2]+"' and level0=1 ";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				DbUtil.close(ps);
				
				String afullname = "";
				sql = "select * from t_c_assitem where AccPackageID="+assitemTypeList2[1]+year +" and AssItemID = '"+assitemTypeList2[2]+"' and level0=1 ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					afullname = rs.getString("AssTotalName");
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
//				更新核算全路径
				sql = "update t_c_assitem set AssTotalName=concat('"+cAssItemName+"/"+yCustomerName+"-"+"',AssTotalName) \n" 
					+ "where AccPackageID="+assitemTypeList2[1]+year +" and (AssTotalName = '"+afullname+"' or AssTotalName like '"+afullname+"/%') and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
//				2.更新父级核算编号
				sql = "update t_c_assitem set ParentAssItemId=concat('"+assitemTypeList2[0]+"-"+assitemTypeList2[1]+"-"+"',ParentAssItemId) \n" 
					+ "where AccPackageID="+assitemTypeList2[1]+year +" and (AssTotalName = '"+afullname+"' or AssTotalName like '"+afullname+"/%') and level0>1 and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "update t_c_assitem set ParentAssItemId='"+assitemTypeList2[0]+"' \n" 
				+ "where AccPackageID="+assitemTypeList2[1]+year +" and AssItemID = '"+assitemTypeList2[2]+"' and level0=1 and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
//				3.更新核算编号				
				sql = "update t_c_assitem set AssItemID=concat('"+assitemTypeList2[0]+"-"+assitemTypeList2[1]+"-"+"',AssItemID) \n" 
					+ "where AccPackageID="+assitemTypeList2[1]+year +" and (AssTotalName = '"+afullname+"' or AssTotalName like '"+afullname+"/%') and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();	
				DbUtil.close(ps);				
			}
						
			//汇总期初到一级
//			for(int ii = 0 ; ii<assitemList1s.length; ii ++){
//				String[] assitemTypeList = assitemList1s[ii].split("`");
//				int i = 1;
//				sql = "update t_c_assitem a inner join \n"
//					+ "(select '"+assitemTypeList[0]+"' as AssItemID, sum(DebitRemain) as DebitRemain from t_c_assitem where AssItemID like '"+assitemTypeList[0]+"%' and Level0=1) b \n" 
//					+ "on a.AssItemID='"+assitemTypeList[0]+"'"
//					+ "set a.DebitRemain = b.DebitRemain";
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
//				
//				i = 1;
//				sql = "update t_c_assitem a inner join \n"
//					+ "(select '"+assitemTypeList[0]+"' as AssItemID, sum(CreditRemain) as CreditRemain from t_c_assitem where AssItemID like '"+assitemTypeList[0]+"%' and Level0=1) b \n" 
//					+ "on a.AssItemID='"+assitemTypeList[0]+"' \n"
//					+ "set a.CreditRemain = b.CreditRemain";
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
//			}
			
			//更新科目级别
			sql = "update t_c_assitem set Level0=Level0+1\n";				
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("生成核算体系成功<br>");
			out.flush();
			
//			-----------------------------合并核算凭证-----------------------------
//			直接将原单位的核算核算凭证分录照搬过来
			
			String [] temp = new String[customer.length];
			for(int ii=0 ; ii<customer.length ; ii++){
				String table = "asdb_" + customer[ii];
				String acc1 = customer[ii] + year;
				temp[ii] = "tt_" + acc1;
				int i = 1;
				
//				获取单位名称
				i = 1;
				sql = "select * from k_customer where DepartID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, customer[ii]);
				rs = ps.executeQuery();
				String yCustomerName = "",DepartID ="";
				while(rs.next()){
					DepartID = rs.getString("DepartID");
					yCustomerName = rs.getString("Departname");
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				i = 1;
				sql="create table " + temp[ii] + " as "
					+"\n select "
					+"\n ?, "
					+"\n ifnull(concat(b.subjectid,'-',a.subjectid),a.subjectid) as subjectid , "
					+"\n a.subjectname, "
					+"\n ifnull(concat(b.subjectfullname,'/',a.subjectfullname),a.subjectfullname) as subjectfullname, "
					+"\n ifnull(if(a.ParentSubjectId = '',b.SubjectId,concat(b.subjectid,'-',a.ParentSubjectId)),a.ParentSubjectId) as ParentSubjectId , "
					+"\n if(a.Level0=1 and a.isleaf=1,0, a.IsLeaf) as  Level0,a.Level0 + 1 as Level1,a.Property,sid "
					+"\n from ( "
					+"\n 	select a.accpackageid,ifnull(subjectfullname2,subjectfullname ) as subjectfullname2, "
					+"\n 	concat(?,'-',a.subjectid) as subjectid , "
					+"\n 	if(level0 = 1 ,concat(?,'-',a.subjectname) ,a.subjectname) as subjectname, "
					+"\n 	concat(?,'-',a.subjectfullname) as subjectfullname, "
					+"\n 	if(ParentSubjectId = '',ParentSubjectId,concat(?,'-',ParentSubjectId)) as ParentSubjectId , "
					+"\n 	IsLeaf,Level0,a.Property,a.subjectid as sid "
					
					+"\n 	from "+table+".c_accpkgsubject a " 
					+"\n 	join "+table+".c_account c "
					+"\n 	on a.accpackageid = ? and c.accpackageid = ? and a.subjectid = c.subjectid and submonth=1 "
					+"\n ) a  "
					+"\n left join k_standsubject b " 
					+"\n on VocationID = ? and b.Level0 = 1 " 
					+"\n and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) "; 
				System.out.println("t_c_accpkgsubject:sql=" +sql);
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, DepartID);
				ps.setString(i++, yCustomerName);
				ps.setString(i++, yCustomerName);
				ps.setString(i++, DepartID);
				
				ps.setString(i++, acc1);
				ps.setString(i++, acc1);
				ps.setString(i++, vocationid);
				ps.execute();
				DbUtil.close(ps);
				
				out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的核算分录信息...");
				out.flush();
				sql = "insert into t_c_assitementry (AccPackageID,SubjectID,VoucherID,OldVoucherID,VchDate,TypeID,Serail,\n"
					+"AssItemID,Summary,Dirction,AssItemSum,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,Property)\n"
					+"select "+ acc + ",a.SubjectID,a.VoucherID,a.OldVoucherID,a.VchDate,concat('"+customer[ii]+"','-',a.TypeID) as TypeID,a.Serail,\n"
					+"a.AssItemID,a.Summary,a.Dirction,a.AssItemSum,a.CurrRate,a.CurrValue,a.Currency,a.Quantity,a.UnitPrice,a.UnitName,a.Property \n"
					+"from "+table+".c_assitementrybegin a where AccPackageID = ? \n";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc1);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "insert into t_c_assitementry (AccPackageID,SubjectID,VoucherID,OldVoucherID,VchDate,TypeID,Serail,\n"
					+"AssItemID,Summary,Dirction,AssItemSum,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,Property)\n"
					+"select "+ acc + ",a.SubjectID,a.OldVoucherID,a.VoucherID,a.VchDate,concat('"+customer[ii]+"','-',a.TypeID) as TypeID,a.Serail,\n"
					+"a.AssItemID,a.Summary,a.Dirction,a.AssItemSum,a.CurrRate,a.CurrValue,a.Currency,a.Quantity,a.UnitPrice,a.UnitName,a.Property \n"
					+"from "+table+".c_assitementry a where AccPackageID = ? \n";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc1);
				ps.execute();
				DbUtil.close(ps);
				
				//更新对应科目编号
//				sql = "update t_c_assitementry a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customer[ii]+"-',-1)=a.SubjectID \n" 
//					+ "set a.SubjectID=b.SubjectID";			
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
				
				sql = "update t_c_assitementry a ,"+temp[ii]+" b  set a.SubjectID=b.SubjectID where a.SubjectID =b.sid";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			
//				更新核算编号				
				sql = "update t_c_assitementry a inner join t_c_assitem b on substring_index(b.AssItemID,'"+customer[ii]+"-',-1)=a.AssItemID \n" 
					+ "set a.AssItemID=b.AssItemID";		
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				out.write("合并核算分录信息成功<br>");
				out.flush();
			}
//			-----------------------------插入核算期初-----------------------------
			if(joinType=="2"||"2".equals(joinType)){
				String customerId1 = ""; 
				for(int ii = 0 ; ii<customer.length; ii ++){				
					String table = "asdb_" + customer[ii];
					
					sql = "select min(month(VchDate)) from "+table+".c_subjectentry where Property<>'199'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					int iMonth = 0;
					while(rs.next()){
						iMonth = Integer.parseInt(rs.getString(1));
					}
					ps.close();
					if(iMonth>1){	
						
					}else{
						customerId1 = customer[ii]; 
					}
				}
				String table = "asdb_" + customerId1;
				String acc1 = customerId1 + year;
				
				
				out.write("开始合并［"+customerId1+"］客户的［"+year+"］年的期初数信息...");
				out.flush();
				int i = 1;
				sql="insert into t_c_assitembegin \n"
					+"(AccPackageID,AccID,AssItemID,DataType,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF) \n"
					+"select ?,a.AccID,a.AssItemID,a.DataName,a.DebitRemain,a.CreditRemain,a.accsign,a.DebitRemainF,a.CreditRemainF \n"
					+"from ( \n"
					+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table +".c_assitementryacc where accpackageid =? and submonth=1 \n"
					+"	union \n"
					+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF  from "+ table +".c_assitementryaccall where accpackageid =? and submonth=1 \n"
					+") a \n";

				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, acc1);
				ps.setString(i++, acc1);
				ps.execute();
				DbUtil.close(ps);
				
//				更新对应科目编号
//				sql = "update t_c_assitembegin a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customerId1+"-',-1)=a.AccID \n" 
//					+ "set a.AccID=b.SubjectID";			
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);

				sql = "update t_c_assitembegin a ,"+temp[0]+" b  set a.AccID=b.SubjectID where a.accid =b.sid";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);

//				更新核算编号				
				sql = "update t_c_assitembegin a inner join t_c_assitem b on substring_index(b.AssItemID,'"+customerId1+"-',-1)=a.AssItemID \n" 
					+ "set a.AssItemID=b.AssItemID";		
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				out.write("合并期初数信息成功<br>");
				out.flush();
			}else{
				for(int ii=0 ; ii<customer.length ; ii++){
					String table = "asdb_" + customer[ii];
					String acc1 = customer[ii] + year;
					
					out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的期初数信息...");
					out.flush();
					int i = 1;
					sql="insert into t_c_assitembegin \n"
						+"(AccPackageID,AccID,AssItemID,DataType,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF) \n"
						+"select ?,a.AccID,a.AssItemID,a.DataName,a.DebitRemain,a.CreditRemain,a.accsign,a.DebitRemainF,a.CreditRemainF \n"
						+"from ( \n"
						+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table +".c_assitementryacc where accpackageid =? and submonth=1 \n"
						+"	union \n"
						+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF  from "+ table +".c_assitementryaccall where accpackageid =? and submonth=1 \n"
						+") a  \n";
						
					ps = conn.prepareStatement(sql);
					ps.setString(i++, acc);
					ps.setString(i++, acc1);
					ps.setString(i++, acc1);
					ps.execute();
					DbUtil.close(ps);
					
//					更新对应科目编号
//					sql = "update t_c_assitembegin a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customer[ii]+"-',-1)=a.AccID \n" 
//						+ "set a.AccID=b.SubjectID";			
//					ps = conn.prepareStatement(sql);
//					ps.execute();
//					DbUtil.close(ps);
					
					sql = "update t_c_assitembegin a ,"+temp[ii]+" b  set a.AccID=b.SubjectID where a.accid =b.sid";			
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
//					更新核算编号				
					sql = "update t_c_assitembegin a inner join t_c_assitem b on substring_index(b.AssItemID,'"+customer[ii]+"-',-1)=a.AssItemID \n" 
						+ "set a.AssItemID=b.AssItemID";		
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					out.write("合并期初数信息成功<br>");
					out.flush();
				}
			}
			
			sql = "update t_c_assitem set AccPackageID="+acc+" \n";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			for(int ii = 0 ;ii<temp.length;ii++){
				sql = "drop table if exists " + temp[ii];
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 上下半年并账
	 */
	public void insertAssitem(HttpServletResponse response,String customerid,String [] customer,String assitemList1,String assitemList2,int year,String joinType,String vocationid,String [][]fMonth) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		
		String[] assitemList1s = assitemList1.split(",");
		String[] assitemList2s = assitemList2.split(",");
		
		try {
			sql = "alter table t_c_assitembegin add index AccID (AccID)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			sql = "alter table t_c_assitembegin add index AssItemID (AssItemID)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
		} catch (Exception e) {}
		
		
		try{
			String acc = customerid + year;

			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			
			out.write("<br>开始合并核算体系...");
			out.flush();
//-----------------------------合并核算体系-----------------------------
			//插入一级核算体系
			for(int ii = 0 ; ii<assitemList1s.length; ii ++){
				String[] assitemTypeList = assitemList1s[ii].split("`");
				int i = 1;
				sql = "insert into t_c_assitem (AccPackageID,accid,AssItemID,AssItemName,AssTotalName,ParentAssItemId,IsLeaf,Level0,UomUnit,Curr,Property) values" +
					" (?,'0000',?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, assitemTypeList[0]);
				ps.setString(i++, assitemTypeList[1]);
				ps.setString(i++, assitemTypeList[1]);
				ps.setString(i++, "");
				ps.setInt(i++, 0);
				ps.setInt(i++, 0);
				ps.setString(i++, "");
				ps.setString(i++, "");
				ps.setString(i++, "1");
				ps.execute();
				DbUtil.close(ps);
			}
			
			//直接将原单位的核算体系照搬过来
			for(int ii=0 ; ii<customer.length ; ii++){
				
				String table = "asdb_" + customer[ii];
				String acc1 = customer[ii] + year;
				
				int i = 1;
				sql = "insert into t_c_assitem (AccPackageID,accid,AssItemID,AssItemName,AssTotalName,ParentAssItemId,\n"
					+ "DebitRemain,CreditRemain,IsLeaf,Level0,UomUnit,Curr,Property) \n"
					+ "select distinct AccPackageID,'0000',AssItemID,AssItemName,AssTotalName,ParentAssItemId,\n"
					+ "DebitRemain,CreditRemain,IsLeaf,Level0,UomUnit,Curr,Property \n"
					+ "from "+ table +".c_assitem where AccPackageID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc1);
				ps.execute();
				DbUtil.close(ps);
				
				//获取单位名称
				i = 1;
				sql = "select * from k_customer where DepartID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, customer[ii]);
				rs = ps.executeQuery();
				String yCustomerName = "",DepartID ="";
				while(rs.next()){
					DepartID = rs.getString("DepartID");
					yCustomerName = rs.getString("Departname");
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				
				//更新核算名称
				sql = "update t_c_assitem set AssItemName=concat('"+yCustomerName+"-"+"',AssItemName) \n" 
					+ "where AccPackageID="+acc1 +" and level0=1";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
//				更新核算对应科目编号			
//				sql = "update t_c_assitem a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customer[ii]+"-',-1)=a.AccID \n" 
//					+ "set a.AccID=b.SubjectID where b.level0 <> 1 ";			
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
			}
			
//			开始更新核算体系
//				1.更新核算全路径
//				2.更新父级核算编号
//				3.更新核算编号
//				assitemTypeList2[1]:原单位单位编号
//				assitemTypeList2[2]:原单位一级核算编号
//				assitemTypeList2[0]:合并后的一级核算			
			for(int ii = 0 ; ii<assitemList2s.length; ii ++){
				String[] assitemTypeList2 = assitemList2s[ii].split("`");
				
//				1.更核算全路径名称
				
				//获取一级核算名称
				String cAssItemName = "";
				for(int j=0 ; j<assitemList1s.length ; j++){
					String[] assitemTypeList = assitemList1s[j].split("`");
					if(assitemTypeList[0]==assitemTypeList2[0]||assitemTypeList[0].equals(assitemTypeList2[0])){
						cAssItemName=assitemTypeList[1];
					}
				}
				
//				获取单位名称
				sql = "select DepartName from asdb.k_customer where DepartID="+assitemTypeList2[1];
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				String yCustomerName = "";
				while(rs.next()){
					yCustomerName = rs.getString(1);
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				/**
				 * 先更新一级的父核算
				 */
				sql = "update t_c_assitem set ParentAssItemId='"+assitemTypeList2[0]+"' \n" 
				+ "where AccPackageID="+assitemTypeList2[1]+year +" and AssItemID = '"+assitemTypeList2[2]+"' and level0=1 ";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				DbUtil.close(ps);
				
				String afullname = "";
				sql = "select * from t_c_assitem where AccPackageID="+assitemTypeList2[1]+year +" and AssItemID = '"+assitemTypeList2[2]+"' and level0=1 ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					afullname = rs.getString("AssTotalName");
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
//				更新核算全路径
				sql = "update t_c_assitem set AssTotalName=concat('"+cAssItemName+"/"+yCustomerName+"-"+"',AssTotalName) \n" 
					+ "where AccPackageID="+assitemTypeList2[1]+year +" and (AssTotalName = '"+afullname+"' or AssTotalName like '"+afullname+"/%') and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
//				2.更新父级核算编号
				sql = "update t_c_assitem set ParentAssItemId=concat('"+assitemTypeList2[0]+"-"+assitemTypeList2[1]+"-"+"',ParentAssItemId) \n" 
					+ "where AccPackageID="+assitemTypeList2[1]+year +" and (AssTotalName = '"+afullname+"' or AssTotalName like '"+afullname+"/%') and level0>1 and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "update t_c_assitem set ParentAssItemId='"+assitemTypeList2[0]+"' \n" 
				+ "where AccPackageID="+assitemTypeList2[1]+year +" and AssItemID = '"+assitemTypeList2[2]+"' and level0=1 and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
//				3.更新核算编号				
				sql = "update t_c_assitem set AssItemID=concat('"+assitemTypeList2[0]+"-"+assitemTypeList2[1]+"-"+"',AssItemID) \n" 
					+ "where AccPackageID="+assitemTypeList2[1]+year +" and (AssTotalName = '"+afullname+"' or AssTotalName like '"+afullname+"/%') and AssItemID not like '%-"+assitemTypeList2[1]+"-%'";			
				ps = conn.prepareStatement(sql);
				ps.execute();	
				DbUtil.close(ps);				
			}
						
			//汇总期初到一级
//			for(int ii = 0 ; ii<assitemList1s.length; ii ++){
//				String[] assitemTypeList = assitemList1s[ii].split("`");
//				int i = 1;
//				sql = "update t_c_assitem a inner join \n"
//					+ "(select '"+assitemTypeList[0]+"' as AssItemID, sum(DebitRemain) as DebitRemain from t_c_assitem where AssItemID like '"+assitemTypeList[0]+"%' and Level0=1) b \n" 
//					+ "on a.AssItemID='"+assitemTypeList[0]+"'"
//					+ "set a.DebitRemain = b.DebitRemain";
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
//				
//				i = 1;
//				sql = "update t_c_assitem a inner join \n"
//					+ "(select '"+assitemTypeList[0]+"' as AssItemID, sum(CreditRemain) as CreditRemain from t_c_assitem where AssItemID like '"+assitemTypeList[0]+"%' and Level0=1) b \n" 
//					+ "on a.AssItemID='"+assitemTypeList[0]+"' \n"
//					+ "set a.CreditRemain = b.CreditRemain";
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
//			}
			
			//更新科目级别
			sql = "update t_c_assitem set Level0=Level0+1\n";				
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			out.write("生成核算体系成功<br>");
			out.flush();
			
//			-----------------------------合并核算凭证-----------------------------
//			直接将原单位的核算核算凭证分录照搬过来
			
			String [] temp = new String[customer.length];
			for(int ii=0 ; ii<customer.length ; ii++){
				String table = "asdb_" + customer[ii];
				String acc1 = customer[ii] + year;
				temp[ii] = "tt_" + acc1;
				int i = 1;
				
//				获取单位名称
				i = 1;
				sql = "select * from k_customer where DepartID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, customer[ii]);
				rs = ps.executeQuery();
				String yCustomerName = "",DepartID ="";
				while(rs.next()){
					DepartID = rs.getString("DepartID");
					yCustomerName = rs.getString("Departname");
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				i = 1;
				sql="create table " + temp[ii] + " as "
					+"\n select "
					+"\n ?, "
					+"\n ifnull(concat(b.subjectid,'-',a.subjectid),a.subjectid) as subjectid , "
					+"\n a.subjectname, "
					+"\n ifnull(concat(b.subjectfullname,'/',a.subjectfullname),a.subjectfullname) as subjectfullname, "
					+"\n ifnull(if(a.ParentSubjectId = '',b.SubjectId,concat(b.subjectid,'-',a.ParentSubjectId)),a.ParentSubjectId) as ParentSubjectId , "
					+"\n if(a.Level0=1 and a.isleaf=1,0, a.IsLeaf) as  Level0,a.Level0 + 1 as Level1,a.Property,sid "
					+"\n from ( "
					+"\n 	select a.accpackageid,ifnull(subjectfullname2,subjectfullname ) as subjectfullname2, "
					+"\n 	concat(?,'-',a.subjectid) as subjectid , "
					+"\n 	if(level0 = 1 ,concat(?,'-',a.subjectname) ,a.subjectname) as subjectname, "
					+"\n 	concat(?,'-',a.subjectfullname) as subjectfullname, "
					+"\n 	if(ParentSubjectId = '',ParentSubjectId,concat(?,'-',ParentSubjectId)) as ParentSubjectId , "
					+"\n 	IsLeaf,Level0,a.Property,a.subjectid as sid "
					
					+"\n 	from "+table+".c_accpkgsubject a " 
					+"\n 	join "+table+".c_account c "
					+"\n 	on a.accpackageid = ? and c.accpackageid = ? and a.subjectid = c.subjectid and submonth=1 "
					+"\n ) a  "
					+"\n left join k_standsubject b " 
					+"\n on VocationID = ? and b.Level0 = 1 " 
					+"\n and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%')) "; 
				System.out.println("t_c_accpkgsubject:sql=" +sql);
				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, DepartID);
				ps.setString(i++, yCustomerName);
				ps.setString(i++, yCustomerName);
				ps.setString(i++, DepartID);
				
				ps.setString(i++, acc1);
				ps.setString(i++, acc1);
				ps.setString(i++, vocationid);
				ps.execute();
				DbUtil.close(ps);
				
				out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的核算分录信息...");
				out.flush();
//				sql = "insert into t_c_assitementry (AccPackageID,SubjectID,VoucherID,OldVoucherID,VchDate,TypeID,Serail,\n"
//					+"AssItemID,Summary,Dirction,AssItemSum,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,Property)\n"
//					+"select "+ acc + ",a.SubjectID,a.VoucherID,a.OldVoucherID,a.VchDate,concat('"+customer[ii]+"','-',a.TypeID) as TypeID,a.Serail,\n"
//					+"a.AssItemID,a.Summary,a.Dirction,a.AssItemSum,a.CurrRate,a.CurrValue,a.Currency,a.Quantity,a.UnitPrice,a.UnitName,a.Property \n"
//					+"from "+table+".c_assitementrybegin a where AccPackageID = ? \n";
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, acc1);
//				ps.execute();
//				DbUtil.close(ps);
				
				sql = "insert into t_c_assitementry (AccPackageID,SubjectID,VoucherID,OldVoucherID,VchDate,TypeID,Serail,\n"
					+"AssItemID,Summary,Dirction,AssItemSum,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,Property)\n"
					+"select "+ acc + ",a.SubjectID,a.OldVoucherID,a.VoucherID,a.VchDate,concat('"+customer[ii]+"','-',a.TypeID) as TypeID,a.Serail,\n"
					+"a.AssItemID,a.Summary,a.Dirction,a.AssItemSum,a.CurrRate,a.CurrValue,a.Currency,a.Quantity,a.UnitPrice,a.UnitName,a.Property \n"
					+"from "+table+".c_assitementry a where AccPackageID = ? \n"
					+"\n and month(a.VchDate)>= ? "
					+"\n and month(a.VchDate)<= ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, acc1);
				ps.setString(2, fMonth[ii][0]);
				ps.setString(3, fMonth[ii][1]);
				ps.execute();
				DbUtil.close(ps);
				
				//更新对应科目编号
//				sql = "update t_c_assitementry a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customer[ii]+"-',-1)=a.SubjectID \n" 
//					+ "set a.SubjectID=b.SubjectID";			
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);
				
				sql = "update t_c_assitementry a ,"+temp[ii]+" b  set a.SubjectID=b.SubjectID where a.SubjectID =b.sid";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			
//				更新核算编号				
				sql = "update t_c_assitementry a inner join t_c_assitem b on substring_index(b.AssItemID,'"+customer[ii]+"-',-1)=a.AssItemID \n" 
					+ "set a.AssItemID=b.AssItemID";		
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				out.write("合并核算分录信息成功<br>");
				out.flush();
			}
//			-----------------------------插入核算期初-----------------------------
			if(joinType=="2"||"2".equals(joinType)){
				String customerId1 = ""; 
				for(int ii = 0 ; ii<customer.length; ii ++){				
					String table = "asdb_" + customer[ii];
					
					sql = "select min(month(VchDate)) from "+table+".c_subjectentry where Property<>'199'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					int iMonth = 0;
					while(rs.next()){
						iMonth = Integer.parseInt(rs.getString(1));
					}
					ps.close();
					if(iMonth>1){	
						
					}else{
						customerId1 = customer[ii]; 
					}
				}
				String table = "asdb_" + customerId1;
				String acc1 = customerId1 + year;
				
				
				out.write("开始合并［"+customerId1+"］客户的［"+year+"］年的期初数信息...");
				out.flush();
				int i = 1;
				sql="insert into t_c_assitembegin \n"
					+"(AccPackageID,AccID,AssItemID,DataType,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF) \n"
					+"select ?,a.AccID,a.AssItemID,a.DataName,a.DebitRemain,a.CreditRemain,a.accsign,a.DebitRemainF,a.CreditRemainF \n"
					+"from ( \n"
					+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table +".c_assitementryacc where accpackageid =? and submonth=1 \n"
					+"	union \n"
					+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF  from "+ table +".c_assitementryaccall where accpackageid =? and submonth=1 \n"
					+") a \n";

				ps = conn.prepareStatement(sql);
				ps.setString(i++, acc);
				ps.setString(i++, acc1);
				ps.setString(i++, acc1);
				ps.execute();
				DbUtil.close(ps);
				
//				更新对应科目编号
//				sql = "update t_c_assitembegin a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customerId1+"-',-1)=a.AccID \n" 
//					+ "set a.AccID=b.SubjectID";			
//				ps = conn.prepareStatement(sql);
//				ps.execute();
//				DbUtil.close(ps);

				sql = "update t_c_assitembegin a ,"+temp[0]+" b  set a.AccID=b.SubjectID where a.accid =b.sid";			
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);

//				更新核算编号				
				sql = "update t_c_assitembegin a inner join t_c_assitem b on substring_index(b.AssItemID,'"+customerId1+"-',-1)=a.AssItemID \n" 
					+ "set a.AssItemID=b.AssItemID";		
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				out.write("合并期初数信息成功<br>");
				out.flush();
			}else{
				for(int ii=0 ; ii<customer.length ; ii++){
					String table = "asdb_" + customer[ii];
					String acc1 = customer[ii] + year;
					
					out.write("开始合并［"+customer[ii]+"］客户的［"+year+"］年的期初数信息...");
					out.flush();
					int i = 1;
					sql="insert into t_c_assitembegin \n"
						+"(AccPackageID,AccID,AssItemID,DataType,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF) \n"
						+"select ?,a.AccID,a.AssItemID,a.DataName,a.DebitRemain,a.CreditRemain,a.accsign,a.DebitRemainF,a.CreditRemainF \n"
						+"from ( \n"
						+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemain as DebitRemainF,CreditRemain as CreditRemainF from "+ table +".c_assitementryacc where accpackageid =? and submonth=1 \n"
						+"	union \n"
						+"	select AccID,AssItemID,DataName,DebitRemain,CreditRemain,accsign,DebitRemainF,CreditRemainF  from "+ table +".c_assitementryaccall where accpackageid =? and submonth=1 \n"
						+") a  \n";
						
					ps = conn.prepareStatement(sql);
					ps.setString(i++, acc);
					ps.setString(i++, acc1);
					ps.setString(i++, acc1);
					ps.execute();
					DbUtil.close(ps);
					
//					更新对应科目编号
//					sql = "update t_c_assitembegin a inner join t_c_accpkgsubject b on substring_index(b.SubjectID,'"+customer[ii]+"-',-1)=a.AccID \n" 
//						+ "set a.AccID=b.SubjectID";			
//					ps = conn.prepareStatement(sql);
//					ps.execute();
//					DbUtil.close(ps);
					
					sql = "update t_c_assitembegin a ,"+temp[ii]+" b  set a.AccID=b.SubjectID where a.accid =b.sid";			
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
//					更新核算编号				
					sql = "update t_c_assitembegin a inner join t_c_assitem b on substring_index(b.AssItemID,'"+customer[ii]+"-',-1)=a.AssItemID \n" 
						+ "set a.AssItemID=b.AssItemID";		
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					out.write("合并期初数信息成功<br>");
					out.flush();
				}
			}
			
			sql = "update t_c_assitem set AccPackageID="+acc+" \n";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			for(int ii = 0 ;ii<temp.length;ii++){
				sql = "drop table if exists " + temp[ii];
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
}
