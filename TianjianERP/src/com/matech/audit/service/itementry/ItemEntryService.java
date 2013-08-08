package com.matech.audit.service.itementry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.*;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ItemEntryService {

	private Connection conn;

	public ItemEntryService(Connection conn) {
		this.conn = conn;
	}

	public String getDeparts(String Departid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			return "";
			
//			String sql = "select group_concat(b.departId) Departs from asdb.k_customer a , asdb.k_customer b where a.departId=? and b.fullpathid like concat(a.fullpathid,'%')"; 
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, Departid);
//			rs = ps.executeQuery();
//			if(rs.next()){
//				return rs.getString(1);
//			}else{
//				return "";
//			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public String getDepart(String ItemEntryID,String opt) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select * from  k_itementry where itemtypeid='"+ItemEntryID+"' and property='"+opt+"' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString("tablefield");
			}else{
				if("1".equals(opt)){
					return "_customerid";	//单位
				}else{
					return "projectid";		//项目
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
	
	
	public void delTable(String autoid,String ItemEntryID)throws Exception {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        try {
        	String [] str = getTable(ItemEntryID);
			String sql = "delete from i_"+str[1]+" where autoid in("+autoid+")";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(ps);
		}
	}
	
	public void saveFillUser(String ItemEntryID,String Name,String autoid) throws Exception {
		PreparedStatement ps = null;
		try {
			ASFuntion CHF=new ASFuntion();
			String [] str = getTable(ItemEntryID);
			String sql = "update i_" + str[1] + " a set a.i_filluser = '"+Name+"' ,a.i_filltime = '"+CHF.getCurrentDate()+"' where autoid in ("+autoid+")"  ;
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改上报人
	 * @param ItemEntryID
	 * @param Name
	 * @param autoid
	 * @throws Exception
	 */
	public void saveUpUser(String ItemEntryID,String Name,String autoid,String opt) throws Exception {
		PreparedStatement ps = null;
		try {
			ASFuntion CHF=new ASFuntion();
			String [] str = getTable(ItemEntryID);
			String sql = "";
			if("".equals(opt.trim()) || "1".equals(opt)){
				sql = "update i_" + str[1] + " a set a.i_upuser = '"+Name+"' ,a.i_uptime = '"+CHF.getCurrentDate()+"' where autoid in ("+autoid+")"  ;
			}else{
				sql = "update i_" + str[1] + " a set a.i_upuser = '' ,a.i_uptime = '' where autoid in ("+autoid+")"  ;
			}
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(ps);
		}
	}
	
	public void saveTable(String [][] iValue,String[] Items,String ItemEntryID,String Name) throws Exception {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	ASFuntion CHF=new ASFuntion();
        	String [] str = getTable(ItemEntryID);
        	String Item = "";
        	String ItemValue = "";
        	for (int i = 0; i < Items.length; i++) {
        		Item += Items[i] + ",";
        		ItemValue += "?,";
			}
        	
        	Item += "i_filluser,i_filltime,";	//填报人、填报时间
        	ItemValue += "?,?,";
        	
        	String sql = "insert into i_"+str[1]+ " ("+Item.substring(0,Item.length()-1)+") values ("+ItemValue.substring(0,ItemValue.length()-1)+")";
        	System.out.println("yuanquan:"+sql); 
        	ps = conn.prepareStatement(sql);
        	int ii = iValue[0].length;
        	for(int j=0;j<ii;j++){
	        	for(int i=0;i<iValue.length;i++){
	        		if(iValue[i][j] == null || "".equals(iValue[i][j])){
	        			ps.setString(i+1, "0.00");
	        		}else{
	        			ps.setString(i+1, iValue[i][j]);	
	        		}
	        		
	        	}
	        	
	        	ps.setString(iValue.length+1, Name);
	        	ps.setString(iValue.length+2, CHF.getCurrentDate());
	        	ps.addBatch();
        	}
        	ps.executeBatch();
        	DbUtil.close(ps);
        	
        	sql = "select * from k_itementry where property = '1' and itemtypeid = '"+ItemEntryID+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();        	
        	if(rs.next()){
        		String s1 = rs.getString("tablefield");
        		sql = "update i_"+str[1]+ " a left join k_customer b on a."+s1+ " = b.DepartID  set a.fullpathid=ifnull(b.fullpathid,'')";
            	ps = conn.prepareStatement(sql);
            	ps.execute();
        	}
        	DbUtil.close(rs);
        	DbUtil.close(ps);
        	
        	sql = "select * from k_itementry a left join (select distinct ctype from k_dic) b on a.property=b.ctype where b.ctype is not null and itemtypeid = '"+ItemEntryID+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();    
        	while(rs.next()){
        		String s1 = rs.getString("tablefield");
        		String s2 = rs.getString("ctype");
        		sql = "update i_"+str[1]+ " a left join k_dic b on b.ctype='"+s2+"' and a."+s1+ "=b.name set a."+s1+ "1 = b.value";
        		ps = conn.prepareStatement(sql);
            	ps.execute();
        	}
        	DbUtil.close(rs);
        	DbUtil.close(ps);
        	
        	sql = "select * from k_itementry where property = '3' and itemtypeid = '"+ItemEntryID+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();  
        	while(rs.next()){
        		String s1 = rs.getString("tablefield");
        		String s2 = rs.getString("inputhtml");
        		if(!"".equals(s2)){
        			sql = "update i_"+str[1]+ " a  set a."+s1+ " = " + s2 ;
            		ps = conn.prepareStatement(sql);
                	ps.execute();
        		}
        	}
        }catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}	
	}
	
	public String[] getTable(String ItemEntryID) throws Exception  {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        ResultSet rs = null;
        String [] result = null;
        try {
			String sql = "select name,tablename,ifnull(taskid,'') taskid from k_itemtype a left join k_tasktemplate b on b.typeid=0 and b.isleaf=1 and a.property=b.taskcode where a.autoid = '"+ItemEntryID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = new String[3];
				result[0] = new String();
				result[1] = new String();
				result[2] = new String();
				result[0] = rs.getString(1);
				result[1] = rs.getString(2);
				result[2] = rs.getString(3);
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
	
	public ArrayList getItemEntry(String ItemEntryID) throws Exception  {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList al = new ArrayList();
        try {
			String sql = "select * from k_itementry where itemtypeid='"+ItemEntryID+"' and property<>3 order by orderid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
//			rs.last();
//			int rowCount=rs.getRow();
//			rs.beforeFirst();
        	while(rs.next()){
        		ArrayList alrs = new ArrayList();
        		
        		String inputhtml = rs.getString("inputhtml");
        		String tablefield = rs.getString("tablefield");
        		String itemname = rs.getString("itemname");
        		
        		inputhtml = inputhtml.replaceAll("\\$\\{value\\}", tablefield).replaceAll("\\$\\{title\\}", itemname);
        		
        		alrs.add(rs.getString("itemname"));
        		alrs.add(rs.getString("tablefield"));
        		alrs.add(inputhtml);
        		alrs.add(rs.getString("property"));
        		alrs.add(rs.getString("defaultvalue"));
        		
        		al.add(alrs);
        	}
        	return al;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(rs);
			 DbUtil.close(ps);
		}		
	}
	
	public ArrayList getItemEntry1(String ItemEntryID) throws Exception  {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList al = new ArrayList();
        try {
			String sql = "select * from k_itementry where itemtypeid='"+ItemEntryID+"'  order by orderid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
//			rs.last();
//			int rowCount=rs.getRow();
//			rs.beforeFirst();
        	while(rs.next()){
        		ArrayList alrs = new ArrayList();
        		
        		String inputhtml = rs.getString("inputhtml");
        		String tablefield = rs.getString("tablefield");
        		String itemname = rs.getString("itemname");
        		
        		inputhtml = inputhtml.replaceAll("\\$\\{value\\}", tablefield).replaceAll("\\$\\{title\\}", itemname);
        		
        		alrs.add(rs.getString("itemname"));
        		alrs.add(rs.getString("tablefield"));
        		alrs.add(inputhtml);
        		alrs.add(rs.getString("property"));
        		
        		alrs.add(rs.getString("defaultvalue"));
        		alrs.add(rs.getString("orderbyname"));
        		al.add(alrs);
        	}
        	return al;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(rs);
			 DbUtil.close(ps);
		}		
	}
	
	public boolean isTableHead(String ItemEntryID) throws Exception  {
		PreparedStatement ps = null;
        ResultSet rs = null;
        boolean bool = false;
        try {
			String sql = "select * from k_itementry where  itemtypeid = '"+ItemEntryID+"' and orderbyname <> '' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = true;
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(rs);
			 DbUtil.close(ps);
		}
	}
	
	public Map isOrderbyNames(String ItemEntryID) throws Exception  {
		PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	Map map = new HashMap();
        	//convert(concat(orderbyname,'{',group_concat(itemname order by orderid),'}') using utf8)
        	String sql = "select orderbyname,cast(concat(orderbyname,'{',group_concat(itemname order by orderid),'}') as char) orderby " +
        			" from k_itementry where itemtypeid='"+ItemEntryID+"' and orderbyname <>'' group by orderbyname order by orderid ";
        	ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while (rs.next()){
				String str = rs.getString("orderby");
				map.put(rs.getString("orderbyname"),str );
			}
        	return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(rs);
			 DbUtil.close(ps);
		}
        
	}
	
	public String showType(String ItemEntryID,String itemname) throws Exception  {
		PreparedStatement ps = null;
        ResultSet rs = null;
        String result = "";
        try {
        	String sql = "select * from k_itementry where itemtypeid='"+ItemEntryID+"' and itemname='"+itemname+"'";
        	ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				String str = rs.getString("datatype");
				if("1".equals(str)){
					result = "showMoney";
				}
				if("2".equals(str)){
					result = "showMoney";
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
	
	
	public String getAddTable(String ItemEntryID) throws Exception  {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	StringBuffer sb = new StringBuffer();
        	String sql = "select * from k_itementry where itemtypeid='"+ItemEntryID+"' and property<>3 order by orderid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			sb.append("	var oTd = oTr.insertCell(-1);\n");
			sb.append("	oTd.align =\"center\";\n");
			sb.append("	oTd.innerHTML = \"<input type=\\\"checkbox\\\" id=\\\"checkLine\\\" name=\\\"checkLine\\\" value=\\\"1\\\"><input type=\\\"hidden\\\" id=\\\"auto\\\" name=\\\"auto\\\" value=\\\"\\\">\";\n");
			while(rs.next()){
				String inputhtml = rs.getString("inputhtml");
        		String tablefield = rs.getString("tablefield");
        		String itemname = rs.getString("itemname");
        		
        		inputhtml = inputhtml.replaceAll("\\$\\{value\\}", tablefield).replaceAll("\\$\\{title\\}", itemname);
        		
				sb.append("	oTd = oTr.insertCell(-1);\n");
				sb.append("	oTd.align =\"center\";\n");
				sb.append("	oTd.innerHTML = \""+inputhtml+"\";\n");
			}			
        	return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(rs);
			 DbUtil.close(ps);			
		}
	}
	
	public StringBuffer getTableValue(String autoid,String tableName,String ItemEntryID)throws Exception  {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	StringBuffer sb = new StringBuffer();
        	ASFuntion CHF=new ASFuntion();
        	ArrayList al = getItemEntry(ItemEntryID);
        	
        	String sql = "select * from i_"+ tableName + " where 1=1 and autoid in ("+autoid+")";
        	ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii =0 ;
			while(rs.next()){
				sb.append("aValue();\n");
				for(int i=0;i<al.size();i++){
					ArrayList alrs = (ArrayList)al.get(i);
					String result = CHF.showNull(rs.getString((String)alrs.get(1)));
					sb.append("var "+(String)alrs.get(1)+" = document.getElementsByName(\""+(String)alrs.get(1)+"\");\n");
					sb.append(""+(String)alrs.get(1)+"["+ii+"].value=\""+result+"\";\n");
				}
				sb.append("var auto = document.getElementsByName(\"auto\");\n");
				sb.append("auto["+ii+"].value=\""+CHF.showNull(rs.getString("autoid"))+"\";\n");
				
				ii ++ ;
			} 
			
        	return sb;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			 DbUtil.close(rs);
			 DbUtil.close(ps);		
		}
	}
	
	
	public void updateTable(String [][] iValue,String[] Items,String ItemEntryID,String[] auto) throws Exception {
		DbUtil.checkConn(conn);		
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	String [] str = getTable(ItemEntryID);
        	String Item = "";
//        	String ItemValue = "";
        	for (int i = 0; i < Items.length; i++) {
        		Item += Items[i]+"= ?" + ",";
//        		ItemValue += "?,";
			}
        	String sql = "update  i_"+str[1]+ " set "+Item.substring(0,Item.length()-1)+" where autoid =? ";
        	ps = conn.prepareStatement(sql);
        	int ii = iValue[0].length;
        	for(int j=0;j<ii;j++){
	        	for(int i=0;i<iValue.length;i++){
	        		ps.setString(i+1, iValue[i][j]);
	        	}
	        	ps.setString(iValue.length+1, auto[j]);
	        	ps.addBatch();
        	}
        	ps.executeBatch();
        	
        	sql = "select * from k_itementry where property = '1' and itemtypeid = '"+ItemEntryID+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();        	
        	if(rs.next()){
        		String s1 = rs.getString("tablefield");
        		sql = "update i_"+str[1]+ " a left join k_customer b on a."+s1+ " = b.DepartID  set a.fullpathid=ifnull(b.fullpathid,'')";
            	ps = conn.prepareStatement(sql);
            	ps.execute();
        	}
        	
        	sql = "select * from k_itementry a left join (select distinct ctype from k_dic) b on a.property=b.ctype where b.ctype is not null and itemtypeid = '"+ItemEntryID+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();    
        	while(rs.next()){
        		String s1 = rs.getString("tablefield");
        		String s2 = rs.getString("ctype");
        		sql = "update i_"+str[1]+ " a left join k_dic b on b.ctype='"+s2+"' and a."+s1+ "=b.name set a."+s1+ "1 = b.value";
        		ps = conn.prepareStatement(sql);
            	ps.execute();
        	}

        	sql = "select * from k_itementry where property = '3' and itemtypeid = '"+ItemEntryID+"'";
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();  
        	while(rs.next()){
        		String s1 = rs.getString("tablefield");
        		String s2 = rs.getString("inputhtml");
        		if(!"".equals(s2)){
        			sql = "update i_"+str[1]+ " a  set a."+s1+ " = " + s2 ;
            		ps = conn.prepareStatement(sql);
                	ps.execute();
        		}
        	}
        	
        }catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}	
	}

}
