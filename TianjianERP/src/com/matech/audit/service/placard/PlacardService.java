package com.matech.audit.service.placard;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.framework.pub.db.DbUtil;

public class PlacardService {
	private Connection conn = null;
public static String NOTE_FILE_PATH = "../placard/";
	
	static {
		//获得程序发布路径
		String dataBasePath = BackupUtil.getDATABASE_PATH();

		NOTE_FILE_PATH = dataBasePath + NOTE_FILE_PATH;

		File file = new File(NOTE_FILE_PATH);
		if(!file.exists()) {
			file.mkdirs();
		}
	}

	public PlacardService(Connection conn) {
		this.conn = conn;
	}

	public void delAPlacard(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (id != null) {

				String mypath="";
				String prePath="";
				String myReadSQL="select myimage from k_placard where myimage=(select myimage from k_placard where id='"+id+"')";
				ps=conn.prepareStatement(myReadSQL);
				
				rs=ps.executeQuery();
				int i=0;
				while(rs.next())
				{
					i++;
					prePath=rs.getString("myimage");
					
				}
				
				
				if(i>1)
				{
				}
				else if(i==1)
				{
					int k=prePath.lastIndexOf("/")+1;
					int allcount=prePath.length();
					String FileName=prePath.substring(k,allcount );
					String myIDFile=prePath.replaceAll("/placard/", "").replaceAll(FileName, "").replaceAll("/", "");
					mypath=PlacardService.NOTE_FILE_PATH+myIDFile;
					//System.out.println("AAAAAAAAAAAAAAAAAAAAAAA "+mypath);
					File myfile = new File(mypath);
					ManuFileService.deleteFile(myfile);
					
				}
				ps = conn.prepareStatement("delete from k_placard where id='" + id + "'");
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
	}
		
	public void delAllAPlacardByUser(String userId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		ResultSet rs = null;
		String myID="";
		//ArrayList aaList=new ArrayList();
		try {
			if (userId != null) {
				String myReadSQL="select a.id from k_placard a,k_user b where Addressee='"+userId+"'and a.Addresser = b.id";
				ps=conn.prepareStatement(myReadSQL);
				rs1=ps.executeQuery();
				while(rs1.next())
				{
					myID+=rs1.getString(1)+",";
				//	aaList.add(rs.getString(1));
				}
			/*	for (Iterator iter = aaList.iterator(); iter.hasNext();) {
					String element = (String) iter.next();
					
				}
				for(int k=0;k<aaList.size();k++)
				{
					System.out.println(aaList.get(k));
				}
				*/
				String[] mystrID=myID.split(",");
				for(int i=0;i<mystrID.length;i++)
				{
					if(!mystrID[i].toString().equals(""))
					{
						int b=0;
						String mypath="";
						String prePath="";
					    String myReadSQL2="select myimage from k_placard where myimage=(select myimage from k_placard where id='"+mystrID.toString()+"')";
					    ps=conn.prepareStatement(myReadSQL2);
					    rs=ps.executeQuery();
					    while(rs.next())
					    {
					    	b++;
					    	prePath=rs.getString("myimage");
					    }
					    
					    if(b>1)
						{
						}
						else if(b==1)
						{
							int k=prePath.lastIndexOf("/")+1;
							int allcount=prePath.length();
							String FileName=prePath.substring(k,allcount );
							String myIDFile=prePath.replaceAll("/placard/", "").replaceAll(FileName, "").replaceAll("/", "");
							mypath=PlacardService.NOTE_FILE_PATH+myIDFile;
							//System.out.println("AAAAAAAAAAAAAAAAAAAAAAA "+mypath);
							File myfile = new File(mypath);
							ManuFileService.deleteFile(myfile);
							
						}
					}
					
				}
				ps = conn.prepareStatement("delete a from k_placard a,k_user b where Addressee='"+userId+"'and a.Addresser = b.id");
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs1 != null)
				rs1.close();
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}
		

	public ArrayList findAPlacard(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {
			if (id != null) {

				ps = conn.prepareStatement("select * from k_placard where id='"
						+ id + "'");
				rs = ps.executeQuery();
				if (rs.next()) {
					PlacardTable pt = new PlacardTable();
					pt.setID(rs.getInt("ID"));
					pt.setAddresser(rs.getString("Addresser"));
					pt.setAddresserTime(rs.getString("AddresserTime"));
					pt.setCaption(rs.getString("Caption"));
					pt.setMatter(rs.getString("Matter"));
					pt.setAddressee(rs.getString("Addressee"));
					pt.setIsRead(rs.getInt("IsRead"));
					pt.setIsReversion(rs.getInt("IsReversion"));
					pt.setCtype(rs.getString("ctype"));
					al.add(pt);
				}
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
	}

	public ArrayList getAllPlacard(String id,String placardTitle,String placardSender,String placardTime,String limitStr) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {
			String sql = "";
			
			sql = "select a.*,b.name from k_placard a,k_user b " 
				+" where Addressee='"+ id+"'"
				+" and a.Addresser = b.id and a.isRead=0 ";
					
			if(!"".equals(placardTitle)){
				sql += " and caption like '%" + placardTitle +"%'"; 
			}
			
			if(!"".equals(placardSender)){
				sql += " and name like '%" + placardSender +"%'"; 
			}
			
			if(!"".equals(placardTime)){
				sql += " and AddresserTime like'" + placardTime +"%'"; 
			}
				
			sql += " order by AddresserTime desc";
			
			sql += limitStr ;
			System.out.println("sk:查询公告的sql"+sql+"\n");
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlacardTable pt = new PlacardTable();
				pt.setID(rs.getInt("ID"));
				pt.setAddresser(rs.getString("Addresser"));
				pt.setAddresserTime(rs.getString("AddresserTime"));
				pt.setCaption(rs.getString("Caption"));
				pt.setMatter(rs.getString("Matter"));
				pt.setAddressee(rs.getString("Addressee"));
				pt.setIsRead(rs.getInt("IsRead"));
				pt.setIsReversion(rs.getInt("IsReversion"));
				pt.setName(rs.getString("name"));
				pt.setCtype(rs.getString("ctype"));
				pt.setImage(rs.getString("myimage"));

				pt.setIsNotReversion(rs.getInt("IsNotReversion"));
				pt.setProperty(rs.getString("Property"));

				al.add(pt);
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
	}

	
	public ArrayList getAllPlacardById(String userId,String placardTitle,String placardSender,String placardTime,String limitStr,String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {
			String sql = "";
			
			sql = "select a.*,b.name,a.uuid,a.url,a.uuidname,a.model from k_placard a " 
				+" left join k_user b on a.Addresser = b.id "
				+" where Addressee='"+ userId+"' ";
					
			if(!"".equals(placardTitle)){
				sql += " and caption like '%" + placardTitle +"%'"; 
			}
			
			if(!"".equals(placardSender)){
				sql += " and name like '%" + placardSender +"%'"; 
			}
			
			if(!"".equals(placardTime)){
				sql += " and AddresserTime like'" + placardTime +"%'"; 
			}
			

			if(!"".equals(id)){
				sql += " and a.id in (" + id +")"; 
			}else{
				sql +=" and a.isread=0 ";
			}
				
			sql += " order by AddresserTime desc";
			
			sql += limitStr ;
			System.out.println("sk:查询公告的sql"+sql+"\n");
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlacardTable pt = new PlacardTable();
				pt.setID(rs.getInt("ID"));
				pt.setAddresser(rs.getString("Addresser"));
				pt.setAddresserTime(rs.getString("AddresserTime"));
				pt.setCaption(rs.getString("Caption"));
				pt.setMatter(rs.getString("Matter"));
				pt.setAddressee(rs.getString("Addressee"));
				pt.setIsRead(rs.getInt("IsRead"));
				pt.setIsReversion(rs.getInt("IsReversion"));
				if("".equals(rs.getString("name")) || rs.getString("name")==null ){
					pt.setName("无姓名");
				}else{
					pt.setName(rs.getString("name"));
				}
				pt.setCtype(rs.getString("ctype"));
				pt.setImage(rs.getString("myimage"));
				
				pt.setUrl(rs.getString("url"));
				pt.setUuid(rs.getString("uuid"));
				pt.setModel(rs.getString("model"));
				pt.setUuidName(rs.getString("uuidName"));

				pt.setIsNotReversion(rs.getInt("IsNotReversion"));
				pt.setProperty(rs.getString("Property"));

				al.add(pt);
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
	}
	
	public ArrayList getNoRead(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {

			String sql = "select concat('<a href=../placard/View.jsp target=\"_blank\">',CONCAT(SUBSTRING(a.Caption,1,8),'...'),'</a> <span class=\"disable_font\">[',b.name,']</span>') news from k_placard a,k_user b where Addressee='"
					+ id
					+ "' and isread='0' and a.Addressee = b.id order by a.addresserTime desc";
			//  org.util.Debug.prtOut("sql="+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				al.add(rs.getString("news"));
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}
	}
	
	/**
	 * 取得用户未读消息列表
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ArrayList getNoReadListByUserId(String userId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {

			String sql = "select concat('<a href=#  onclick=\"goSetup()\">',CONCAT(SUBSTRING(a.Caption,1,8),'...'),'</a> <span class=\"disable_font\">[',b.name,']</span>') news "
						+ " from k_placard a,k_user b "
						+ " where Addressee= ? "
						+ " and isread='0' "
						+ " and a.Addresser = b.id "
						+ " order by a.addresserTime desc";
			//  org.util.Debug.prtOut("sql="+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				al.add(rs.getString("news"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return al;
	}

	public void AddPlacard(PlacardTable pt) throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			int i = 1;

			ps = conn
					.prepareStatement("INSERT INTO k_placard(Addresser,AddresserTime,Caption,Matter,Addressee,IsRead,IsReversion,isNotReversion,Property,ctype,myimage,uuid,model,url,uuidname) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(i++, pt.getAddresser());
			ps.setString(i++, pt.getAddresserTime());
			ps.setString(i++, pt.getCaption());
			ps.setString(i++, pt.getMatter());
			ps.setString(i++, pt.getAddressee());
			ps.setInt(i++, pt.getIsRead());
			ps.setInt(i++, pt.getIsReversion());
			ps.setInt(i++, pt.getIsNotReversion());
			ps.setString(i++, pt.getProperty());
			ps.setString(i++, pt.getCtype());
			ps.setString(i++, pt.getImage());
			ps.setString(i++, pt.getUuid());
			ps.setString(i++, pt.getModel());
			ps.setString(i++, pt.getUrl());
			ps.setString(i++, pt.getUuidName());
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}
	}
	
	public void AddPlacardtoOA(PlacardTable pt,String companyid) throws Exception { 

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			int i = 1;

			ps = conn
					.prepareStatement("INSERT INTO asdb.oa_k_placard(Addresser,AddresserTime,Caption,Matter,Addressee,IsRead,IsReversion,isNotReversion,Property,ctype,myimage,companyid,uuid,model,url,uuidname) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(i++, pt.getAddresser());
			ps.setString(i++, pt.getAddresserTime());
			ps.setString(i++, pt.getCaption()); 
			ps.setString(i++, pt.getMatter());
			ps.setString(i++, pt.getAddressee());
			ps.setInt(i++, pt.getIsRead());
			ps.setInt(i++, pt.getIsReversion());
			ps.setInt(i++, pt.getIsNotReversion());
			ps.setString(i++, pt.getProperty());
			ps.setString(i++, pt.getCtype());
			ps.setString(i++, pt.getImage());
			ps.setString(i++, companyid);
			ps.execute();
			ps.setString(i++, pt.getUuid());
			ps.setString(i++, pt.getModel());
			ps.setString(i++, pt.getUrl());
			ps.setString(i++, pt.getUuidName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}
	}

	public void updateIsRead(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {

			ps = conn
					.prepareStatement("update k_placard set IsRead=1 where Addressee='"
							+ id + "'");
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}
	}
	
	/**
	 * 根据ID标记为 已读
	 * @param id
	 * @throws Exception
	 */
	public void updateIsReadById(String id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {

			ps = conn
					.prepareStatement("update k_placard set IsRead=1 where id in("+ id + ")");
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}
	}

	public void AddIsReversion(String id, String time) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {

			String sql = "INSERT INTO k_placard (Addresser,AddresserTime,Caption,Matter,Addressee,IsRead,IsReversion)select '"
					+ id
					+ "' ,'"
					+ time
					+ "' ,concat('<�Ѷ�>',caption) , Matter, Addresser Addressee,'0','0' from  k_placard where Addressee='"
					+ id + "' and IsReversion='1'";
			ps = conn.prepareStatement(sql);
			ps.execute();
			sql = "update k_placard set IsReversion=0 where Addressee='" + id
					+ "'";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();

		}

	}
	
	
	
	/**
	 * 取得用户未读消息列表
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ArrayList getNoReadListByUserId2(String userId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {

			String sql = "select a.id,a.Addresser,a.addresserTime,a.caption  "
						+ " from k_placard a,k_user b "
						+ " where Addressee= ? "
						+ " and isread='0' "
						+ " and a.Addresser = b.id "
						+ " order by a.addresserTime desc limit 10";
			//  org.util.Debug.prtOut("sql="+sql);
			System.out.println(this.getClass()+"     sql="+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlacardTable pt = new PlacardTable();
				pt.setID(Integer.parseInt(rs.getString("id")));
				pt.setAddresser(rs.getString("Addresser"));
				pt.setAddresserTime(rs.getString("addresserTime"));
				pt.setCaption((rs.getString("caption")));
				al.add(pt);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return al;
	}
	
	/**
	 * 取得重大通知列表
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ArrayList getSpecialList(String userId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {

			 String sql = " SELECT * FROM (  "
						+ " 	SELECT a.id,a.Addresser,a.addresserTime,a.caption,a.ctype "
						+ " 	FROM k_placard a,k_user b "
						+ " 	WHERE Addressee= ? "
						+ " 	AND ctype='重要公告' "
						+ " 	AND a.Addresser = b.id "
						+ " 	ORDER BY a.addresserTime DESC "
						+ " ) a "
						+ " UNION "
						+ " SELECT * FROM ( " 
						+ " 	SELECT a.id,a.Addresser,a.addresserTime,a.caption,a.ctype "
						+ " 	FROM k_placard a,k_user b "
						+ " 	WHERE Addressee= ? "
						+ " 	AND isread='0' "
						+ " 	AND ctype<>'重要公告' "
						+ " 	AND a.Addresser = b.id "
						+ " 	order by addresserTime desc"
			 			+ ") b limit 10";
			
			System.out.println(this.getClass()+"     sql="+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				PlacardTable pt = new PlacardTable();
				pt.setID(Integer.parseInt(rs.getString("id")));
				pt.setAddresser(rs.getString("Addresser"));
				pt.setAddresserTime(rs.getString("addresserTime"));
				pt.setCaption((rs.getString("caption")));
				pt.setCtype(rs.getString("ctype")) ;
				al.add(pt);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return al;
	}
	
	
	
}
