package com.matech.audit.service.dataupload;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.del.JRockey2Opp;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.autotoken.AutoTokenService;
import com.matech.audit.service.customer.CustomerNameConfigService;
import com.matech.audit.service.keys.KeyValue;
import com.matech.audit.service.subjectType.SubjectTypeService;
import com.matech.audit.service.usersubject.ComeService;
import com.matech.audit.service.usersubject.SubjectAssitemService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.FileUtil;
import com.matech.framework.pub.util.MD5;

public class UploadDataService extends DefaultHandler {
	private Connection conn = null;

	private SAXBuilder sb = null;

	private Document doc = null;

	private String TableName = "";

	private String[] xmlFiles;

	private String formatFile = "";

	private String strDir = "";

	private ResultSetMetaData rsmd = null;

	private int pkgidPosition = -1;

	private DisposeTableService dts = null;
 
	private String value="";
	
	private SAXParserFactory saxsf = SAXParserFactory.newInstance();
	private SAXParser saxsp = saxsf.newSAXParser();
	
	private String newpackageid="";
	private int iField=1,iRecord=0;
	PreparedStatement saxps = null;
	
	public void setTableName(String strTableName) {
		this.TableName = strTableName;
		// 找到指定数据表的所有数据文件（表名_N.xml）
		this.xmlFiles = FileUtil.getFilesAndDir(strDir, strTableName + "_");
		this.formatFile = strTableName + "_1.xml";
	}

	public UploadDataService(String filePath, Connection conn1)
			throws Exception {
		if (filePath == null || filePath.equals("")) {
			throw new Exception("请先设置filePath属性");
		}

		DbUtil.checkConn(conn1);

		this.strDir = filePath;

		this.conn = conn1;
		this.dts = new DisposeTableService(conn1);
		this.sb = new SAXBuilder();
		if (this.sb == null)
			throw new Exception("XML分析器加载错误！");
		
	}

	/**
	 * 检查帐套中的key.dat是否存在 存在则和后台的机构信息比较 一致返回 2 不一致返回 1 文件不存在返回 0 出错返回-1
	 * 
	 */
	public int checkKeyDat(String strKey) {
		int iResult = 0;
		FileReader keydat = null;
		BufferedReader br = null;
		try {
			File keyFile = new File(this.strDir + "key.dat");
			if (keyFile.exists()) {
				keydat = new FileReader(keyFile);

				br = new BufferedReader(keydat);

				
				String strDogDat = MD5.getMD5String(strKey);
				
				String strKeydat = br.readLine();
				
				if(strKeydat == null || "null".equals(strKeydat.toLowerCase())) {
					org.util.Debug.prtOut("keyDat文件原始加密信息,将被改成空字符串：" + strKeydat);
					strKeydat = "";
				}
				
				String strSyskey1 = MD5.getMD5String("广州铭太科技");
				String strSyskey2 = MD5.getMD5String("铭太科技内部专用");
				String strSyskey3 = MD5.getMD5String("试用版本");
				String strSyskey4 = MD5.getMD5String("广州铭太信息科技有限公司");
				
				org.util.Debug.prtOut("系统机构信息加密信息：" + strDogDat);
				org.util.Debug.prtOut("keyDat文件加密信息：" + strKeydat);
				org.util.Debug.prtOut("通用校验1：" + strSyskey1);
				org.util.Debug.prtOut("通用校验2：" + strSyskey2);
				org.util.Debug.prtOut("通用校验3：" + strSyskey3);
				org.util.Debug.prtOut("通用校验4：" + strSyskey4);
				
				//机构备用名
				Map dogMap = JRockey2Opp.getInfoFromDog();
				
				String sysC2 = (String)dogMap.get("sysC2");
				boolean sycC2Pass = false,configPass = false;
				
				//没有MD5过的名称
				configPass = new CustomerNameConfigService(conn).checkKeyDat(strKey,strKeydat) ;
				
				if(sysC2 != null) {
					String[] names = sysC2.split("~`");
					
					for(int i=0; i < names.length; i++) {
						if(strKeydat.equals(MD5.getMD5String(names[i]))) {
							sycC2Pass = true;
							break;
						}			
					}
				} 

				// 将当前登陆用户的机构信息加密后与文件里面的值比较
				/**
				 * 等于 KEYDAT等于当前狗加密，或者KEYDAT等于试用狗，都放过去
				 */
				if (strKeydat.equals(strDogDat) || strKeydat.equals(strSyskey1)
						|| strKeydat.equals(strSyskey2)
						|| strKeydat.equals(strSyskey3)
						|| strKeydat.equals(strSyskey4)
						|| "".equals(strKeydat)
						|| strKeydat.equals("6d59e5e1e28d843975a64d303ea4875c")
						|| checkOASysCo(strKeydat)
						|| sycC2Pass
						|| configPass
				) {
					org.util.Debug.prtOut("相等");
					iResult = 2;
				} else {
					strSyskey1=br.readLine();
					if  (strKeydat.equals(MD5.getMD5String(strSyskey1)) && strSyskey1.indexOf("铭太")>=0 ){
						org.util.Debug.prtOut("新名字相等");
						iResult = 2;
					}else{
						org.util.Debug.prtOut("不等");
						iResult = 1;
					}
				}

			} else {
				iResult = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			iResult = -1;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (keydat != null) {
					keydat.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return iResult;
	}

	public String[] getAccPackageID(String customerid,String accpackageid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String result[] = new String[2];
			
			String sql = "select * from t_c_AccPackage where accpackageid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageid);
			rs = ps.executeQuery();
			
			if(rs.next()){
				result[0] = customerid + rs.getString("AccPackageYear"); 
				result[1] = rs.getString("AccPackageYear"); 
			}
			
			sql = "delete from  t_c_AccPackage  where accpackageid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, result[0]);
			ps.execute();
			
			sql = "update t_c_AccPackage set accpackageid = ? where accpackageid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, result[0]);
			ps.setString(2, accpackageid);
			ps.execute();
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
		}
	}
	
	
	public String[] getPackageId() throws Exception {
		
		String result[] = null;
		String [] sFile = FileUtil.getFilesAndDir(this.strDir,"c_AccPackage_");
		boolean bool = true;
		for (int i = 0; i < sFile.length; i++) {
			String filename = sFile[i];
			if(filename.toLowerCase().indexOf(".txt")>-1){
				bool = false;
				break;
			}
		}
		
		if(bool){
			result = getPackageIdFromXmlFile();
		}else{
			result = getPackageIdFromTxtFile();
		}
		return result;
	}
	
	/*
	 * 直接分析解压的TXT帐套文件，获取帐套编号 
	 * @return String 
	 * @throws Exception
	 */
	public String[] getPackageIdFromTxtFile() throws Exception {
		
		BufferedInputStream bis=null;
		
		try {
			String result[] = new String[2];
			
			String formatFile1 = "c_AccPackage_2.txt";
			
			byte[] buff = new byte[2048];
			bis = new BufferedInputStream(new FileInputStream(this.strDir + formatFile1));
			bis.read(buff);
			char a=0x10;
			String [] str = new String(buff).split(String.valueOf(a));
			result[0] = str[0]; 
			result[1] = str[1];
			
			return result; 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			bis.close();
		}
		
	}
	
	/*
	 * 直接分析解压的XML帐套文件，获取帐套编号 
	 * @return String 
	 * @throws Exception
	 */
	public String[] getPackageIdFromXmlFile() throws Exception {

		String result[] = new String[2];

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		Document doc1 = null;
		try {
			// 先分析xml1的文件，获得帐套编号和年份的位置
			doc1 = builder.build(new File(this.strDir + "c_AccPackage_1.xml"));
			Element root1 = doc1.getRootElement();
			List metadata1 = root1.getChildren("metadata");
			Element Fields1 = (Element) metadata1.get(0);
			List cols = Fields1.getChildren("field");
			int i = 0, iPackageid = -1, iYear = -1;
			for (Iterator iter = cols.iterator(); iter.hasNext();) {
				i++;
				String name = ((Element) iter.next()).getChildText("name");
				if (name.toLowerCase().equals("accpackageid")) {
					iPackageid = i;
				}
				if (name.toLowerCase().equals("accpackageyear")) {
					iYear = i;
				}
			}

			if (iPackageid < 0) {
				throw new Exception("帐套表XML文件内没有AccPackageID字段");
			}
			if (iYear < 0) {
				throw new Exception("帐套表XML文件内没有AccPackageYear字段");
			}

			// 再取得对应的值
			
			doc = builder.build(new File(this.strDir + "c_AccPackage_2.xml"));
			Element root = doc.getRootElement();
			List datas = root.getChildren("data");
			List records = ((Element) datas.get(0)).getChildren("record");
			for (Iterator iter = records.iterator(); iter.hasNext();) {
				Element Fields = (Element) iter.next();
				List colsValue = Fields.getChildren("field");
				i = 0;
				for (Iterator it = colsValue.iterator(); it.hasNext();) {
					Element e = (Element) it.next();
					i++;
					if (i == iPackageid) {
						// 客户原有帐套的编号,提取出来用于后面的批量更改;
						result[0] = e.getText().trim();
					}
					if (i == iYear) {
						// 客户帐套的年份
						result[1] = e.getText().trim();
					}
					// org.util.Debug.prtOut(String.valueOf(i)+e.getText());
				}
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("预处理帐套文件失败：" + e.getMessage(), e);
		}
		
		return result;
	}

	
	public String getExportDate() throws Exception {
		String result = ""; 
		String [] sFile = FileUtil.getFilesAndDir(this.strDir,"c_AccPackage_");
		boolean bool = true;
		for (int i = 0; i < sFile.length; i++) {
			String filename = sFile[i];
			if(filename.toLowerCase().indexOf(".txt")>-1){
				bool = false;
				break;
			}
		}
		if(bool){
			result = getExportDateFromXmlFile();
		}else{
			result = getExportDateFromTxtFile();
		}
		return result;
	}
	
	public String getExportDateFromTxtFile() throws Exception {
		try {
			String result= "";
			String formatFile1 = "c_AccPackage_2.txt";
			
			byte[] buff = new byte[2048];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this.strDir + formatFile1));
			bis.read(buff);
			char a=0x10;
			String [] str = new String(buff).split(String.valueOf(a));
			result = str[3];
			bis.close();
			return result; 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			
		}
	}
	
	/*
	 * 直接分析解压的XML帐套文件，获取采集时间 @return String @throws Exception
	 */
	public String getExportDateFromXmlFile() throws Exception {

		String result = "";
		// this.strDir = "c:\\temp\\1208413670828\\";
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		Document doc1 = null;
		try {
			// 先分析xml1的文件，获得帐套编号和年份的位置
			doc1 = builder.build(new File(this.strDir + "c_AccPackage_1.xml"));
			Element root1 = doc1.getRootElement();
			List metadata1 = root1.getChildren("metadata");
			Element Fields1 = (Element) metadata1.get(0);
			List cols = Fields1.getChildren("field");
			int i = 0, iExportdate = -1;
			for (Iterator iter = cols.iterator(); iter.hasNext();) {
				i++;
				String name = ((Element) iter.next()).getChildText("name");
				if (name.toLowerCase().equals("exportdate")) {
					iExportdate = i;
				}
			}

			if (iExportdate < 0) {
				throw new Exception("帐套表XML文件内没有exportdate字段");
			}

			// 再取得对应的值
			doc = builder.build(new File(this.strDir + "c_AccPackage_2.xml"));
			Element root = doc.getRootElement();
			List datas = root.getChildren("data");
			List records = ((Element) datas.get(0)).getChildren("record");
			for (Iterator iter = records.iterator(); iter.hasNext();) {
				Element Fields = (Element) iter.next();
				List colsValue = Fields.getChildren("field");
				i = 0;
				for (Iterator it = colsValue.iterator(); it.hasNext();) {
					Element e = (Element) it.next();
					i++;
					if (i == iExportdate) {
						// 客户原有帐套的编号,提取出来用于后面的批量更改;
						result = e.getText().trim();
					}
					// org.util.Debug.prtOut(String.valueOf(i)+e.getText());
				}
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("预处理帐套文件失败：" + e.getMessage(), e);
		}

		return result;
	}

	/**
	 * 返回now,lastdate,standdate时间最新的一个
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getDate() throws Exception {
		int icurrent_date = 0;
		int ilastdate = 0;
		int istanddate = 0;
		String scurrent_date = "";
		String slastdate = "";
		String sstanddate = "";
		String date = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select current_date(),lastdate,standdate from asdb.k_system  ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {

				icurrent_date = Integer.parseInt(rs.getString(1).replaceAll(
						"-", ""));
				scurrent_date = rs.getString(1);
				ilastdate = Integer.parseInt(rs.getString(2)
						.replaceAll("-", ""));
				slastdate = rs.getString(2);
				istanddate = Integer.parseInt(rs.getString(3).replaceAll("-",
						""));
				sstanddate = rs.getString(3);
				if (icurrent_date >= ilastdate) {
					date = scurrent_date;
				} else {
					date = slastdate;
					icurrent_date = ilastdate;
				}
				if (icurrent_date >= istanddate) {
				} else {
					date = sstanddate;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return date;
	}

	/**
	 * 备份置疑数据
	 * 
	 * @param newpackageid
	 *            String
	 * @throws Exception
	 * 
	 * 有一个隐性的BUG，如果同时有2个人在装载这个客户的不同年的帐套； 由于里面有删表操作，所以会出现并发冲突
	 * 
	 * 
	 */
	public void backupZy(String newpackageid) throws Exception {
		PreparedStatement ps = null;
		try {
			// 先备份
			this.dts.dropTable("t_question");
			String sql = "CREATE TABLE t_question like z_question";
			if (!this.dts.checkTableExist("t_question")) {
				ps = conn.prepareStatement(sql);
				ps.execute();
				ps.close();
			}

			this.dts.dropTable("t_voucherspotcheck");
			sql = "CREATE TABLE t_voucherspotcheck like z_voucherspotcheck";
			if (!this.dts.checkTableExist("t_voucherspotcheck")) {
				ps = conn.prepareStatement(sql);
				ps.execute();
				ps.close();
			}

			this.dts.dropTable("t_taxcheck");
			sql = "CREATE TABLE t_taxcheck like z_taxcheck";
			if (!this.dts.checkTableExist("t_taxcheck")) {
				ps = conn.prepareStatement(sql);
				ps.execute();
				ps.close();
			}

			/*
			 * sql = "delete from t_question " + "where accpackageid=?"; ps =
			 * conn.prepareStatement(sql); ps.setString(1,newpackageid);
			 * ps.execute(); ps.close();
			 */

			sql = "insert into t_question select * from z_question "
					+ "where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, newpackageid);
			ps.execute();
			ps.close();

			/*
			 * sql = "delete from t_voucherspotcheck " + "where
			 * entryaccpackageid=?"; ps = conn.prepareStatement(sql);
			 * ps.setString(1,newpackageid); ps.execute(); ps.close();
			 */

			sql = "insert into t_voucherspotcheck select * from z_voucherspotcheck "
					+ "where entryaccpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, newpackageid);
			ps.execute();
			ps.close();

			/*
			 * sql = "delete from z_taxcheck " + "where accpackageid=?"; ps =
			 * conn.prepareStatement(sql); ps.setString(1,newpackageid);
			 * ps.execute(); ps.close();
			 */

			sql = "insert into z_taxcheck select * from z_taxcheck "
					+ "where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, newpackageid);
			ps.execute();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("备份项目抽凭置疑出错" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 恢复置疑数据
	 * 
	 * @param newpackageid
	 *            String
	 * @throws Exception
	 */
	public void restoreZy(String accpackageId) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "update z_voucherspotcheck set vchid=-1 where entryAccPackageID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			ps.close();

			sql = "update z_voucherspotcheck a join c_voucher b "
					+ "on a.entryAccPackageID=? and b.accpackageid=? "
					+ "and a.entryOldVoucherID=b.VoucherID "
					+ "and a.entryTypeID=b.typeid "
					+ "and a.entryVchDate=b.vchdate " + "set a.vchid=b.autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			ps.close();

			sql = "update z_question set vchid=-1 where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			ps.close();

			sql = "update z_question a join c_voucher b "
					+ "on a.accpackageid=? and b.accpackageid=? "
					+ "and a.OldVoucherID=b.VoucherID "
					+ "and a.typeid=b.typeid and a.vchdate=b.vchdate "
					+ "set a.vchid=b.autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			ps.close();

			sql = "update z_taxcheck set vchid=-1 where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			ps.close();

			sql = "update z_taxcheck a join c_subjectentry b "
					+ "on a.accpackageid=? and b.accpackageid=? "
					+ "and a.OldVoucherID=b.oldVoucherID "
					+ "and a.typeid=b.typeid and a.vchdate=b.vchdate "
					+ "set a.vchid=b.autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			ps.close();

			/**
			 * 更新临时表
			 */
			sql = "update t_question a join z_question b "
					+ "on a.accpackageid=? and b.accpackageid=? "
					+ "and b.vchid=-1 " + "and a.OldVoucherID=b.OldVoucherID "
					+ "and a.typeid=b.typeid and a.vchdate=b.vchdate "
					+ "set a.vchid=-1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			ps.close();

			sql = "delete from  z_question "
					+ "where accpackageid=? and vchid=-1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			ps.close();

			sql = "update t_voucherspotcheck a join z_voucherspotcheck b "
					+ "on a.entryAccPackageID=? and b.entryAccPackageID=? "
					+ "and b.vchid=-1 "
					+ "and a.entryOldVoucherID=b.entryOldVoucherID "
					+ "and a.entryTypeID=b.entryTypeID and a.entryVchDate=b.entryVchDate "
					+ "set a.vchid=-1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			ps.close();

			sql = "delete from  z_voucherspotcheck "
					+ "where entryAccPackageID=? and vchid=-1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			ps.close();

			sql = "update z_taxcheck a join z_taxcheck b "
					+ "on a.accpackageid=? and b.accpackageid=? "
					+ "and b.vchid=-1 " + "and a.OldVoucherID=b.OldVoucherID "
					+ "and a.typeid=b.typeid and a.vchdate=b.vchdate "
					+ "set a.vchid=-1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			ps.close();

			sql = "delete from  z_taxcheck "
					+ "where accpackageid=? and vchid=-1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("恢复项目抽凭置疑出错" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 分析某一个数据库表结构，构造插入SQL；
	 * 
	 * @return
	 * @throws Exception
	 */
	public String readSQL() throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}

		StringBuffer sbf = new StringBuffer("insert into ");
		StringBuffer sbfv = new StringBuffer(" values( ");
		Element root = doc.getRootElement();
		// 先导入到临时表,所以要用T_开头;
		TableName = "t_" + root.getAttributeValue("name");

		this.doc = sb.build(new FileInputStream(this.strDir + formatFile));
		sbf.append(TableName);
		sbf.append(" (");
		List metadata = root.getChildren("metadata");
		Element Fields = (Element) metadata.get(0);
		List cols = Fields.getChildren("field");
		int i = 0;
		String strFields = "";
		pkgidPosition = -1; // 每次分析都必须强制置帐套ID位置为-1;
		for (Iterator iter = cols.iterator(); iter.hasNext();) {
			i++;
			String name = ((Element) iter.next()).getChildText("name");

			name = name.toUpperCase().equals("LEVEL") ? "level0" : name;
			name = name.toUpperCase().equals("TYPE") ? "ctype" : name;

			// System.out.println("XML 的表："+name);

			if (name.toUpperCase().equals("ACCPACKAGEID")) {
				pkgidPosition = i;
			}
			sbf.append(name);
			sbfv.append("?");
			strFields += name + ",";
			if (iter.hasNext()) {
				sbf.append(",");
				sbfv.append(",");
			}
		}
		sbf.append(")");
		sbfv.append(")");

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			strFields = "select " + strFields + "1 from " + TableName
					+ " where 1=2";
			ps = conn.prepareStatement(strFields);
			rs = ps.executeQuery();
			if (rs == null) {
				throw new Exception("无法获取" + TableName + "表信息");
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				throw new Exception("无法获取" + TableName + "表的字段信息");
			}
		} catch (Exception e) {
			throw new Exception("无法获取" + TableName + "表信息", e);
		}

		org.util.Debug.prtOut("sql=" + sbf.toString() + sbfv.toString());
		return sbf.toString() + sbfv.toString();
	}

	/**
	 * 读取XML的列值
	 * 
	 * @throws Exception
	 */
	public void readColsValue() throws Exception {
		Element root = doc.getRootElement();
		List datas = root.getChildren("data");
		List records = ((Element) datas.get(0)).getChildren("record");
		for (Iterator iter = records.iterator(); iter.hasNext();) {
			Element Fields = (Element) iter.next();
			List colsValue = Fields.getChildren("field");
			for (Iterator it = colsValue.iterator(); it.hasNext();) {
				System.out.print(((Element) it.next()).getText() + "|");
			}
			org.util.Debug.prtOut("");
		}
		org.util.Debug.prtOut(records.size());
	}

	public String insertToDB(String newpackageid) throws Exception {
		
		String [] sFile = FileUtil.getFilesAndDir(this.strDir,this.TableName + "_");
		boolean bool = true;
		for (int i = 0; i < sFile.length; i++) {
			String filename = sFile[i];
			if(filename.toLowerCase().indexOf(".txt")>-1){
				bool = false;
				break;
			}
		}
		
		String result = "";
		if(bool){
			result = insertXmlToDB(newpackageid); 
		}else{
			result = insertTxtToDB(newpackageid) ;
		}
		
		return result;
	}
	
	/**
	 * 
	 * 将TXT数据文件导入到指定表中
	 * @param newpackageid
	 * @return
	 * @throws Exception
	 */
	public String insertTxtToDB(String newpackageid) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;
		try {
			this.doc = sb.build(new FileInputStream(this.strDir + formatFile));
			if (doc == null)
				throw new Exception("表格式定义文件:" + xmlFiles[0] + "访问失败！");
			
			StringBuffer sbf = new StringBuffer("");
			
			Element root = doc.getRootElement();
			TableName = "t_" + root.getAttributeValue("name");
			this.doc = sb.build(new FileInputStream(this.strDir + formatFile));
			
			List metadata = root.getChildren("metadata");
			Element Fields = (Element) metadata.get(0);
			List cols = Fields.getChildren("field");
			int i = 0;
			String strFields = "";
			pkgidPosition = -1; // 每次分析都必须强制置帐套ID位置为-1;
			for (Iterator iter = cols.iterator(); iter.hasNext();) {
				i++;
				String name = ((Element) iter.next()).getChildText("name");

				name = name.toUpperCase().equals("LEVEL") ? "level0" : name;
				name = name.toUpperCase().equals("TYPE") ? "ctype" : name;

				// System.out.println("XML 的表："+name);

				if (name.toUpperCase().equals("ACCPACKAGEID")) {
					pkgidPosition = i;
				}
				sbf.append(name);
				strFields += name + ",";
				if (iter.hasNext()) {
					sbf.append(",");
				}
			}
			
			//System.out.println(sbf.toString());

			for (int filei = 1; filei < xmlFiles.length; filei++) {
				String xmlFile =  this.strDir + xmlFiles[filei];
				xmlFile = new ASFuntion().replaceStr(xmlFile, "\\", "/");
				String sql = "load data  infile '"+xmlFile+"' into table "+TableName+" " +
						" fields terminated by x'10' " +
						" optionally enclosed by x'11' escaped by x'13' " +
						" lines terminated by x'12' " + 
						" ("+sbf.toString()+") " + 
						" set AccPackageID = ? ";
				
				System.out.println(xmlFile + "|" + sql);
				ps = conn.prepareStatement(sql);
				ps.setString(1, newpackageid);
				ps.execute();
				DbUtil.close(ps);
			}
			
			return TableName;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * ====从这里开始，到后面标记结束的地方，都是SAX需要用到的
	 */
	
	/**
	 * 将XML导入到指定表中
	 * 
	 * @param newpackageid
	 * @return
	 * @throws Exception
	 */
	public String insertXmlToDB(String newpackageid) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		this.newpackageid=newpackageid;
		
		try{
			conn.setAutoCommit(false);
			this.doc = sb.build(new FileInputStream(this.strDir + formatFile));
			if (doc == null)
				throw new Exception("表格式定义文件:" + xmlFiles[0] + "访问失败！");
			saxps = conn.prepareStatement(readSQL());
	
			org.util.Debug.prtOut("qwh:formatFile=" + formatFile);
	
			// 遍历所有文件，完成装载
			for (int filei = 1; filei < xmlFiles.length; filei++) {
				iField=1;
				iRecord=0;
				System.out.println("处理："+this.strDir+ xmlFiles[filei]);
				saxsp.parse(new InputSource(this.strDir+ xmlFiles[filei]), this);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("将XML导入到指定表中失败,当前记录数"+iField+",错误原因:"+e.getMessage());
		}finally{
			DbUtil.close(saxps);
		}
		
		return TableName;
	}

	/**
	 * SAX:要求实例化的代码，记录中间处理的characters，切记，在一个<aaa>12313123</aaa>,这一段有可能被反复调用
	 */
	public   void   characters(char[] ch,int start,int length) throws SAXException   { 
		
		try{
			
			String temp = new String(ch,start,length); 
			
			if("".equals(temp.trim())) {
				return;
			}
			
			this.value+=temp;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	} 

	/**
	 * SAX:要求实例化的代码，最后文档结束时把还没有来得及插入的记录插入到表中
	 */
	public   void   endDocument() throws   SAXException{ 	
		try{
			super.endDocument();
			//最后一次插入
			
			if (iRecord % 100 != 0) {
					saxps.executeBatch();
					conn.commit();
			}
		}catch(Exception e){
			throw new SAXException("导入"+this.TableName+"在endDocument失败，错误:"+e.getMessage());
		}
		
	} 

	/**
	 * SAX:要求实例化的代码，每个</aaa>时激发
	 */
	public void endElement(String   uri,   String   localName,   String   qName) throws SAXException{
		
		try{
			super.endElement(uri,   localName,   qName); 
			
			if ("field".equals(qName)){
				//响应每个字段结束，填充PS的参数值
				//System.out.println("value="+value);
				
				if (iField == pkgidPosition) {
					// 为了提高效率,在插入时直接替换掉帐套编号
					saxps.setString(iField, newpackageid);
				} else {

					switch (rsmd.getColumnType(iField)) {
					case java.sql.Types.CHAR:
					case java.sql.Types.VARCHAR:
						
						//System.out.println("value="+value);
						
						saxps.setString(iField, this.value.trim());
						break;
					case java.sql.Types.INTEGER:
						if (this.value == null || this.value.equals(""))
							saxps.setInt(iField, 0);
						else
							saxps.setString(iField, this.value);
						break;
					case java.sql.Types.DECIMAL:
						if (this.value == null || this.value.equals(""))
							saxps.setInt(iField, 0);
						else {
							saxps.setString(iField,  new java.text.DecimalFormat("#0.00").format(Double.parseDouble(this.value)));
						}
						break;
					default:
						saxps.setString(iField, this.value);
					}// end of switch
				}
				
				iField++;
				
			}
			if ("record".equals(qName)){
				//执行一次插入
				iRecord++;
				saxps.addBatch();
				iField=1;
				
				if (iRecord % 100 == 0) {
					saxps.executeBatch();
					conn.commit();
				}
			}
			
		}catch(Exception e){
			throw new SAXException("导入"+this.TableName+"在endElement失败，错误:"+e.getMessage());
		}
		
		//清零中间缓存结果
		this.value="";
	} 
	
	
	/**
	 * ======================这里是SAX解析结束的地方===================================
	 */
	
	
	/**
	 * 当核算余额有记录，但科目没有。要补录科目
	 */
	public void patch(String AccPackageID, int year) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = null;
		try {
			conn.setAutoCommit(false);
			
			sql = "insert into t_c_account(accpackageid,subjectid,accname,subjectfullname1,isleaf1,level1,SubYearMonth,SubMonth,DataName,direction, DebitRemain,CreditRemain,DebitOcc,CreditOcc, Balance,DebitTotalOcc,CreditTotalOcc,DebitBalance,CreditBalance) \n" +
			" select distinct accpackageid,subjectid,SubjectName,SubjectFullName,IsLeaf,level0,substring(accpackageid,7) as year,c.submonth,0, case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end as direction, 0,0,0,0,0,0,0,0,0 \n" +
			" from t_c_accpkgsubject a ,( \n" +
			"	select accid  \n" +
			"	from t_c_account a right join ( \n" +
			"		select distinct accid  \n" +
			"		from t_c_assitementryacc  \n" +
			"		where accpackageid=?  \n" +
			"	) b on a.accpackageid=?  and a.SubjectID = b.accid \n" +
			"	where a.SubjectID is null \n" +
			") b ,k_month c  \n" +
			"where a.accpackageid=? " +
			"and c.monthtype=12  \n" +
			"and a.SubjectID = b.accid" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.execute();
			conn.commit();
			DbUtil.close(ps);
			
			sql = "insert into t_c_accountall(accpackageid,subjectid,accname,subjectfullname1,isleaf1,level1,SubYearMonth,SubMonth,DataName,AccSign,direction, DebitRemain,CreditRemain,DebitOcc,CreditOcc, Balance,DebitTotalOcc,CreditTotalOcc,DebitBalance,CreditBalance, DebitRemainF,CreditRemainF,DebitOccF,CreditOccF, BalanceF,DebitTotalOccF,CreditTotalOccF,DebitBalanceF,CreditBalanceF) \n" +
			" select distinct accpackageid,subjectid,SubjectName,SubjectFullName,IsLeaf,level0,substring(accpackageid,7) as year,c.submonth,b.DataName,b.AccSign, case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end as direction, 0,0,0,0,0,0,0,0,0 ,0,0,0,0,0,0,0,0,0 \n" +
			" from t_c_accpkgsubject a ,( \n" +
			"	select b.*  \n" +
			"	from t_c_accountall a right join ( \n" +
			"		select distinct accid,DataName,AccSign  \n" +
			"		from t_c_assitementryaccall  \n" +
			"		where accpackageid=?  \n" +
			"	) b on a.accpackageid=?  and a.SubjectID = b.accid and a.DataName = b.DataName \n" +
			"	where a.SubjectID is null \n" +
			") b ,k_month c  \n" +
			"where a.accpackageid=? " +
			"and c.monthtype=12  \n" +
			"and a.SubjectID = b.accid" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.execute();
			conn.commit();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			org.util.Debug.prtOut("错误时的SQL=" + sql);
			throw new Exception("执行失败" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}	
	}
	
	/**
	 * 删除或修改有外币名称没有外币期初、发生的记录
	 * @param AccPackageID
	 * @param year
	 * @throws Exception
	 */
	public void update(String AccPackageID, int year) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = null;
		try {
			conn.setAutoCommit(false);
			String sql1 = "select accid,assitemid,dataname,accsign from ("
				+" select accid,assitemid,dataname,submonth,accsign "
				+" from t_c_assitementryaccall " 
				+" where accpackageid="+AccPackageID+" "
				+" and debitremain=0 and creditremain=0 and creditocc=0 and debitocc=0 "
				+" order by accid,assitemid,submonth"
				+" ) t group by accid,assitemid,dataname,accsign having count(*)=12";
			
//			sql = "update t_c_subjectentry a,( "
//				+" 	select a.* from t_c_assitementry a,( "
//				+ 	sql1 
//				+" 	) t1 "
//				+" 	where a.accpackageid="+AccPackageID+" and accsign =1 "
//				+" 	and a.subjectid=t1.accid and a.assitemid=t1.assitemid and a.Currency = b.dataname "
//				+" 	and currvalue = 0 and assitemsum<>0  "
//				+" ) b set a.currency='' "
//				+" where a.accpackageid="+AccPackageID+" and a.VoucherID = b.VoucherID and a.serail =b.serail ";
//			ps = conn.prepareStatement(sql);
//			ps.execute();
//			conn.commit();
//			ps.close();
			
			sql = " update  t_c_assitementry a,( "
				+ 	sql1 
				+" 	) t1 set a.currency='' "
				+" 	where a.accpackageid="+AccPackageID+" and accsign =1 "
				+" 	and a.subjectid=t1.accid and a.assitemid=t1.assitemid and a.Currency = t1.dataname "
				+" 	and currvalue = 0 and assitemsum<>0  ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "delete a from t_c_assitementryaccall a,("
				+	sql1 
				+" ) b "
				+" where a.accpackageid="+AccPackageID+" and a.accid=b.accid and a.assitemid=b.assitemid and a.dataname = b.dataname and a.accsign =1 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			conn.commit();
			ps.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			org.util.Debug.prtOut("错误时的SQL=" + sql);
			throw new Exception("执行失败" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public void update(String AccPackageID,String table,String name,String rep1,String rep2)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "update "+table+" set "+name+" = REPLACE("+name+", '"+rep1+"', '"+rep2+"') where AccPackageID = " + AccPackageID;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 处理完成后汇总科目余额
	 * 
	 * @throws Exception
	 */
	public String createKmhz(String AccPackageID, int year) throws Exception {
		String strResult = "";
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;

		String sql = null;
		int i = 0;
		try {
			
			new KeyValue().auto(AccPackageID.substring(0, 6)); //重建k_key表的视图
			
			conn.setAutoCommit(false);

			sql = "update t_c_accpkgsubject set subjectname = replace(subjectname,'`','') where AccPackageID = ? and subjectname like '%`%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update t_c_accpkgsubject set subjectname = replace(subjectname,'''','') where AccPackageID = ? and subjectname like '%''%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 修改科目名中有“\”为“-”
			 */
			sql = "update t_c_accpkgsubject set subjectname = replace(subjectname,'\\\\','-') where AccPackageID = ? and subjectname like '%\\\\\\\\%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 修改科目名中有“&”为“＆”
			 */
			sql = "update t_c_accpkgsubject set subjectname = replace(subjectname,'&','＆') where AccPackageID = ? and subjectname like '%&%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 判断科目体系是否全是一级：(全部科目)parentsubjectid = null and isleaf = 1 and level0 =1 
			 */
			sql = "select count(*) from (" +
			"	select distinct ifnull(parentsubjectid,'') as parentsubjectid,ifnull(isleaf,'') as isleaf,ifnull(level0,'') as level0 " +
			"	from t_c_accpkgsubject where AccPackageID = ? " +
			") a where NOT (parentsubjectid='' AND isleaf=1 AND level0=1) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			conn.commit();
			if(rs.next()){
				int count = rs.getInt(1);
				if(count == 0){
					DbUtil.close(rs);
					DbUtil.close(ps);
					
					//插入subjectid = 'XXXX' and parentsubjectid = 'XX' and 临时科目
					sql = "insert into t_c_accpkgsubject(AccPackageID,SubjectID,ParentSubjectId,SubjectName,Property) values (?,?,?,?,?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.setString(2, "XXXX");
					ps.setString(3, "XX");
					ps.setString(4, "临时科目");
					ps.setString(5, "00");
					ps.execute();
					conn.commit();
					DbUtil.close(ps);
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			String tableName = ifValue("t_c_accpkgsubjectopenbegin","t_c_accpkgsubjectbegin");
			
			if("t_c_accpkgsubjectbegin".equals(tableName)){
				//修改期初数表：有本位币没有外币期初，但有外币发生。修正外币对应本位币的期初为本位币期初
				sql = " insert into t_c_accpkgsubjectbegin (AccPackageID,subjectid,DataType,DebitRemain,CreditRemain,accsign) " +
					"select a.AccPackageID,b.subjectid,b.Currency,0,0,1" +
					" from (" +
					
					/*
					" 	select a.* " +
					" 	from t_c_accpkgsubjectbegin a left join t_c_accpkgsubjectbegin b " +
					" 	on a.AccPackageID = ? and b.AccPackageID = ? " +
					" 	and a.subjectid = b.subjectid " +
					" 	and a.DataType = '0' " +
					" 	and b.DataType <> '0' " +
					" 	where a.AccPackageID = ? " +
					" 	and a.DataType = '0' " +
					" 	and b.AccPackageID is null " +
					*/
					"	select ? as AccPackageID,subjectid \n" +
					"	from t_c_accpkgsubjectbegin \n" +
					"	where AccPackageID = ? \n" +
					"	group by subjectid having count(*)=1  \n" +
					
					
					" ) a ,(" +
					"	select distinct subjectid,Currency" +
					"	from t_c_subjectentry" +
					" 	where AccPackageID = ?  and Currency <>'' " +
					") b " +
					"where a.AccPackageID = ? and a.subjectid = b.subjectid" ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
				
//				修改期初数表：有本位币没有数量期初，但有数量发生。修正数量对应本位币的期初为本位币期初
				sql = " insert into t_c_accpkgsubjectbegin (AccPackageID,subjectid,DataType,DebitRemain,CreditRemain,accsign) " +
					"select a.AccPackageID,b.subjectid,b.UnitName,0,0,2" +
					" from (" +
					
					/*
					" 	select a.* " +
					" 	from t_c_accpkgsubjectbegin a left join t_c_accpkgsubjectbegin b " +
					" 	on a.AccPackageID = ? and b.AccPackageID = ? " +
					" 	and a.subjectid = b.subjectid " +
					" 	and a.DataType = '0' " +
					" 	and b.DataType <> '0' " +
					" 	where a.AccPackageID = ? " +
					" 	and a.DataType = '0' " +
					" 	and b.AccPackageID is null " +
					*/
					"	select ? as AccPackageID,subjectid \n" +
					"	from t_c_accpkgsubjectbegin \n" +
					"	where AccPackageID = ? \n" +
					"	group by subjectid having count(*)=1  \n" +
					
					" ) a ,(" +
					"	select distinct subjectid,UnitName" +
					"	from t_c_subjectentry" +
					" 	where AccPackageID = ?  and UnitName <>'' " +
					") b " +
					"where a.AccPackageID = ? and a.subjectid = b.subjectid" ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
			
				sql = "update t_c_accpkgsubjectbegin a join t_c_accpkgsubjectbegin b " +
					"on b.accpackageid=? and b.datatype='0' " +
					"and a.accpackageid=? and a.datatype <> '0' " +
					"and a.subjectid=b.subjectid " +
					"set a.DebitRemainF=b.DebitRemain,a.CreditRemainF=b.CreditRemain";
				 ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
				
				sql = "update t_c_accpkgsubjectbegin set DebitRemainF=0 where accpackageid=? and DebitRemainF is null" ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();	
				
				sql = "update t_c_accpkgsubjectbegin set CreditRemainF=0 where accpackageid=? and CreditRemainF is null" ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();	
				
			}
			
			/**
			 * =======================================================================
			 * 把科目期初数有重复的主键的记录合并！
			 * =======================================================================
			 */
			org.util.Debug.prtOut("c0=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_voucher set property ='111' where accpackageid=? and (property is null or property='')";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();

			sql = "delete from " + tableName + " where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, "-1"+ year );
			ps.execute();
			conn.commit();
			ps.close();

			org.util.Debug.prtOut("c1=" + new ASFuntion().getCurrentTime());
			sql = "insert into  "
					+ tableName
					+ " ( AccPackageID,subjectid,datatype,DebitRemain,CreditRemain,DebitRemainF,CreditRemainF,accsign)"
					+ "select ?,subjectid,datatype,sum(debitremain),sum(creditremain),sum(debitremainF),sum(creditremainF),accsign "
					+ "from " + tableName + " where accpackageid=? "
					+ "group by  subjectid,datatype HAVING count(*)>1";
			ps = conn.prepareStatement(sql);
			ps.setString(1,  "-1"+ year );
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();

			org.util.Debug.prtOut("c2=" + new ASFuntion().getCurrentTime());
			sql = "delete  t1 from "
					+ tableName
					+ " t1,"
					+ tableName
					+ " t2 "
					+ "WHERE t1.accpackageid=? and t2.accpackageid=? "
					+ "and t1.subjectid=t2.subjectid and t1.datatype=t2.datatype";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2,  "-1"+ year );
			ps.execute();
			conn.commit();
			ps.close();

			org.util.Debug.prtOut("c3=" + new ASFuntion().getCurrentTime());
			sql = "update " + tableName
					+ " set accpackageid=? where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2,  "-1"+ year );
			ps.execute();
			conn.commit();
			ps.close();

			/**
			 * =======================================================================
			 * 科目表重建subjetfullname,parentsubjectid,isleaf,level字段
			 * =======================================================================
			 * 
			 * org.util.Debug.prtOut("B0="+new ASFuntion().getCurrentTime());
			 * //按照熊杰要求，删除掉不是1和5开头的科目；凭证分录暂时不删除； sql = "delete from
			 * t_c_accpkgsubject where accpackageid=? and
			 * substring(subjectid,1,1)>'5' "; ps = conn.prepareStatement(sql);
			 * ps.setString(1, AccPackageID); ps.execute(); conn.commit();
			 * ps.close();
			 * 
			 * sql = "delete from t_c_subjectentry where accpackageid=? and
			 * substring(subjectid,1,1)>'5' "; ps = conn.prepareStatement(sql);
			 * ps.setString(1, AccPackageID); ps.execute(); conn.commit();
			 * ps.close();
			 */

			// 设置标志值
			org.util.Debug.prtOut("B1=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_accpkgsubject set level0=-1,isleaf=null where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();

			/* 是否已经设置了上级科目编号 */
			int iHasSetParentSubjectid = 0;
			sql = "select count(*) from t_c_accpkgsubject where accpackageid=? and parentsubjectid>''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if (rs.next()) {
				iHasSetParentSubjectid = rs.getInt(1);
			} else {
				throw new Exception("科目表无法检索");
			}
			rs.close();
			ps.close();

			// 用于构造循环用的数组
			sql = "select distinct length(subjectid) from t_c_accpkgsubject where accpackageid=? order by 1 asc";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			int len1, iCount = -1;
			int maxlevel = 0;// 科目最大级次
			int[] iLens = {
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			while (rs.next()) {
				iLens[++iCount] = rs.getInt(1);
				if (iCount > 50) {
					// 要支持24层以上很简单，把iLens的定义多一点就可以了；
					throw new Exception("科目体系超过24层，不支持了!");
				}
			}
			rs.close();
			if (iCount < 0) {
				throw new Exception("科目表为空，无法获得科目体系");
			}

			// 设置第一级科目(长度最短的肯定是第一级,长度不是最短的不一定不是第一级);
			// 所以这里,把长度最短的无条件设置为第一级
			if (iHasSetParentSubjectid > 0) {
				sql = "update t_c_accpkgsubject set level0=1,parentsubjectid='',subjectfullname=subjectname "
						+ "where accpackageid=? and parentsubjectid=''";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
			} else {
				sql = "update t_c_accpkgsubject set level0=1,parentsubjectid='',subjectfullname=subjectname "
						+ "where accpackageid=? and LENGTH(subjectid)=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, iLens[0]);
			}
			ps1.execute();
			conn.commit();
			ps1.close();

			/**
			 * 检查科目体系是设置了上级科目还是没有设置，等着我来按照下级科目 like '上级科目%'的逻辑计算
			 * 而且可以同时兼容设置了上级科目,和没有设置上级科目的情况
			 */
			for (len1 = 1; len1 <= iCount; len1++) {
				// 先默认把上级科目ID设置成接近最短的上级
				sql = "update t_c_accpkgsubject set parentsubjectid = substring(subjectid,1,?)"
						+ "where accpackageid=? and LENGTH(subjectid)=? and level0=-1 and parentsubjectid =''"; // parentsubjectid
				// =''
				// and
				ps1 = conn.prepareStatement(sql);
				ps1.setInt(1, iLens[len1 - 1]);
				ps1.setString(2, AccPackageID);
				ps1.setInt(3, iLens[len1]);
				ps1.execute();
				conn.commit();
				ps1.close();

				/**
				 * 检查科目体系有父科目但科目编号与父科目的长度一样的科目，
				 * 要优先设置此科目的科目体系
				 */
				sql = "select 1 from t_c_accpkgsubject where accpackageid=? and level0 = -1 and LENGTH(subjectid) =LENGTH(parentsubjectid) limit 1";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				rs = ps1.executeQuery();
				conn.commit();
				if(rs.next()){
					rs.close();
					ps1.close();
					
					sql = "update t_c_accpkgsubject a join t_c_accpkgsubject b "
						+ "on a.accpackageid=? and length(a.subjectid)=LENGTH(a.parentsubjectid) and a.level0=-1 "
						+ "and b.accpackageid=? and a.parentsubjectid =b.subjectid "
						+ "set a.parentsubjectid=b.subjectid,a.level0=b.level0+1,a.subjectfullname=concat(b.subjectfullname,'/',a.subjectname)";
					ps1 = conn.prepareStatement(sql);
					ps1.setString(1, AccPackageID);
					ps1.setString(2, AccPackageID);
					ps1.execute();
					conn.commit();
					ps1.close();			
					
				}
				
				int iHasNotDealed = 1, cc = len1;
				for (; iHasNotDealed > 0;) {
					/**
					 * 至少执行一次,处理的逻辑是,和上级的比,然后看处理完成没有,没有就重新设置科目体系,然后重新检查
					 */
					sql = "update t_c_accpkgsubject a join t_c_accpkgsubject b "
							+ "on a.accpackageid=? and length(a.subjectid)=? and a.level0=-1 "
							+ "and b.accpackageid=? and a.parentsubjectid =b.subjectid "
							+ "set a.parentsubjectid=b.subjectid,a.level0=b.level0+1,a.subjectfullname=concat(b.subjectfullname,'/',a.subjectname)";
					ps1 = conn.prepareStatement(sql);
					ps1.setString(1, AccPackageID);
					ps1.setInt(2, iLens[len1]);
					ps1.setString(3, AccPackageID);
					ps1.execute();
					conn.commit();
					ps1.close();

					sql = "select subjectid from t_c_accpkgsubject where accpackageid=? and length(subjectid)=? and level0=-1 limit 1";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.setInt(2, iLens[len1]);
					rs = ps.executeQuery();
					if (rs.next()) {
						cc--;
						rs.close();
						ps.close();

						if (cc < 0) {
							// 说明已有的第一级都不存在;就把这个作为第一级;
							sql = "update t_c_accpkgsubject set level0=1,parentsubjectid='',subjectfullname=subjectname "
									+ "where accpackageid=? and LENGTH(subjectid)=? and level0=-1";
							ps1 = conn.prepareStatement(sql);
							ps1.setString(1, AccPackageID);
							ps1.setInt(2, iLens[len1]);
							ps1.execute();
							conn.commit();
							ps1.close();
							iHasNotDealed = 0;
						} else {
							// 说明设置成最临近的还不行,那就往远一点的设置
							sql = "update t_c_accpkgsubject set parentsubjectid = substring(subjectid,1,?)"
									+ "where accpackageid=? and LENGTH(subjectid)=? and level0=-1";
							ps1 = conn.prepareStatement(sql);
							ps1.setInt(1, iLens[cc]);
							ps1.setString(2, AccPackageID);
							ps1.setInt(3, iLens[len1]);
							ps1.execute();
							conn.commit();
							ps1.close();
						}
					} else {
						rs.close();
						ps.close();
						// 没有找到,说明已经处理完成了,全部匹配成功
						iHasNotDealed = 0;

					}
				}
			}

			
			//设置isleaf值
			org.util.Debug.prtOut("B4=" + new ASFuntion().getCurrentTime());
			sql = "update  t_c_accpkgsubject a join ("
					+ "select distinct parentsubjectid from t_c_accpkgsubject "
					+ "where accpackageid=?) b "
					+ "on a.accpackageid=? and a.subjectid =b.parentsubjectid "
					+ "set isleaf=0;";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accpkgsubject " + "set isleaf=1 "
					+ "where accpackageid=? and isleaf is null";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			
			/**
			 * 如果科目全路径名称有相同的，修改科目名称和全路径。
			 * 修改：(科目编号)科目名称
			 * 
			 */
			boolean bOK=false;
			iCount=1;
			while (bOK==false && iCount<10){
				
				bOK=true;
				
				//预防死循环
				iCount++;
				
				sql = "select subjectfullname,min(subjectid) as subjectid \n"
					+"from t_c_accpkgsubject \n"
					+"where accpackageid = ? \n"
					+"group by subjectfullname \n"
					+"having count(*)>1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				rs = ps.executeQuery();
				conn.commit();
				
				System.out.println("sql="+sql);
				
				while(rs.next()){
					String strSubjectfullname = rs.getString("subjectfullname");
					String strSubjectid = rs.getString("subjectid");
					
					//有对照，只能再看看以后有没有问题；
					bOK=false;
					
					System.out.println("strSubjectfullname="+strSubjectfullname+"|subjectid="+strSubjectid);

					sql = "update \n"
						+"t_c_accpkgsubject a join \n"
						+"( \n"
						+"	select subjectfullname,subjectid,subjectname, \n"
						+"	concat(subjectname,'(',subjectid,')') as newsubjectname, \n"
						+"	if (level0=1,concat(subjectname,'(',subjectid,')'), \n"
						+"		replace(subjectfullname,concat('/',subjectname),concat('/',subjectname,'(',subjectid,')')) \n"
						+"	) as newsubjectfullname \n"
						+"	from t_c_accpkgsubject b \n"
						+"	where b.accpackageid = ? and b.subjectfullname = '"+strSubjectfullname+"' \n"
						+"	and b.subjectid <>'"+strSubjectid+"' \n"
						+")b \n"
						+"on a.accpackageid = ? and a.subjectid <>'"+strSubjectid+"' \n"
						+"and (a.subjectfullname = '"+strSubjectfullname+"' or a.subjectfullname like concat(b.subjectfullname,'/%')) \n"
						+"and (a.subjectid like concat(b.subjectid,'%') or a.subjectid =b.subjectid) \n"
						+"set a.subjectname=if (a.subjectid=b.subjectid,b.newsubjectname,a.subjectname), \n"
						+"a.subjectfullname=replace(a.subjectfullname,b.subjectfullname,b.newsubjectfullname) " ;
					
					System.out.println("sql="+sql);
					
					ps1 = conn.prepareStatement(sql);
					ps1.setString(1, AccPackageID);
					ps1.setString(2, AccPackageID);
					ps1.execute();
					conn.commit();
					ps1.close();
				}
				rs.close();
				ps.close();
			}
			

			/**
			 * 对于科目表，增加参照标准科目的方向来设置用户帐套科目方向的判断 1、参照标准科目表，设置一级科目的方向；
			 * 2、检查剩下的未设置方向的科目，如果为1级科目，则按照科目编号来设置（1、3为1，2、4为-1，5为0）；
			 * 如果为下级科目，则等于1级科目的方向。
			 */
			org.util.Debug.prtOut("B5=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_accpkgsubject "
					+ "set property='' where accpackageid=? and (property is null or property='00')";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accpkgsubject a join ( \n"
				+ "select t1.subjectid,concat('0',t2.Property) as Property \n"
				+ "from ("
				+ "		select subjectid,subjectname from t_c_accpkgsubject where accpackageid=? and level0=1 \n"
				+ "		union \n"
				+ "		select distinct a.subjectid,TRIM(replace(CONCAT(a.subjectname,'                               '),d.key1,d.key2)) as subjectname \n"
				+ "		from ( \n"
				+ "			select distinct a.subjectid,TRIM(replace(CONCAT(a.subjectname,'                               '),c.key1,c.key2)) as subjectname \n"
				+ "			from ( \n"
				+ "				select distinct a.subjectid,TRIM(replace(CONCAT(a.subjectname,'                               '),b.key1,b.key2)) as subjectname \n"
				+ "				from t_c_accpkgsubject a,k_key b  \n"
				+ "				where a.accpackageid=? and a.level0=1	 \n"
				+ "				and (b.departid =0 or b.departid=? )  \n"
				+ "				and a.subjectname like concat('%',b.key1,'%')  \n"
				+ " 		) a ,k_key c  \n"
				+ "			where 1=1  \n"
				+ "			and (c.departid =0 or c.departid=? )  \n"
				+ "			and a.subjectname like concat('%',c.key1,'%')  \n"
				+ "		) a ,k_key d  \n"
				+ "		where 1=1 \n"
				+ "		and (d.departid =0 or d.departid=? ) \n"
				+ "		and a.subjectname like concat('%',d.key1,'%')  \n"
				+ ") t1,k_standsubject t2 \n"
				+ "where t2.level0=1 and t1.SubjectName = t2.subjectname ) b \n"
				+ "on a.accpackageid=? and a.Property='' and a.SubjectId =b.subjectid \n"
				+ "set a.Property = b.Property";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.setString(3, AccPackageID.substring(0, 6));
			ps1.setString(4, AccPackageID.substring(0, 6));
			ps1.setString(5, AccPackageID.substring(0, 6));
			
			ps1.setString(6, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
		
			org.util.Debug.prtOut("B6=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_accpkgsubject "
					+ "set property=case substring(SubjectId,1,1) "
					+ "when '1' then '01' WHEN '2' then '02' when '3' then '02' when '4' then '01' else '01' END "
					+ "where accpackageid=? and level0=1 and property=''";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("B7=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_accpkgsubject a join "
					+ "(select Property,subjectid,subjectfullname from t_c_accpkgsubject "
					+ "where accpackageid=? and level0=1 ) b "
//					+ "on a.SubjectId like concat(b.subjectid,'%') "
					+ "on (a.subjectfullname = b.subjectfullname or a.subjectfullname like concat(b.subjectfullname,'/%')) "
					+ "set a.Property=b.property where "
					+ "a.accpackageid=? and a.level0>1 and a.Property='' ";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			/*
			 * 这句话居然大科目体系要执行4个钟头还出不来，简直是... "update t_c_accpkgsubject a join
			 * t_c_accpkgsubject b " +"on a.accpackageid=? and a.level0>1 and
			 * a.Property='' and b.accpackageid=? and b.level0=1 " +"and
			 * a.SubjectId like concat(b.subjectid,'%')" +"set
			 * a.Property=b.property"; ps1 = conn.prepareStatement(sql);
			 * ps1.setString(1, AccPackageID); ps1.setString(2, AccPackageID);
			 * ps1.execute(); conn.commit(); ps1.close();
			 */

			sql = "";
			
			
			/**
			 * =======================================================================
			 * 对凭证分录表的特殊设置
			 * =======================================================================
			 */

			// 删除掉采集提供的年中建账程序中非底层凭证，否则会造成重复汇总发生额的现象
			org.util.Debug.prtOut("B8=" + new ASFuntion().getCurrentTime());
			String strSubjectids = "";

//			sql = "select GROUP_CONCAT(distinct b.subjectid SEPARATOR \"','\") from t_c_accpkgsubject b \n"
//					+ "where b.AccPackageID=? and b.isleaf=0 ";
			
			sql = "select GROUP_CONCAT(distinct b.subjectid SEPARATOR \"','\") from (" +
				"	select a.* " +
				"	from t_c_subjectentry a,t_c_accpkgsubject b" +
				"	where 1=1 " +
				"	and a.AccPackageID=?" +
				"	and b.AccPackageID=?" +
				"	and a.property = 199" +
				"	and a.subjectid = b.subjectid" +
				"	and b.isleaf = 1" +
				") a ,(" +
				"	select a.* " +
				"	from t_c_subjectentry a,t_c_accpkgsubject b" +
				"	where 1=1 " +
				"	and a.AccPackageID=?" +
				"	and b.AccPackageID=?" +
				"	and a.property = 199" +
				"	and a.subjectid = b.subjectid" +
				"	and b.isleaf = 0" +
				") b " +
				"where a.subjectid like concat(b.subjectid,'%')";
			
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			rs = ps1.executeQuery();
			if (rs.next()) {
				strSubjectids = rs.getString(1);
			}
			rs.close();
			ps1.close();

			if (strSubjectids != null && !strSubjectids.equals("")) {
				sql = "delete from t_c_subjectentry "
						+ "where AccPackageID=? and property='199' and subjectid in ('"
						+ strSubjectids + "')";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();
			}

			// 设置凭证分录表的科目名称和科目全称字段
			org.util.Debug.prtOut("B9=" + new ASFuntion().getCurrentTime());
			/*
			 * sql = "update t_c_subjectentry a join t_c_accpkgsubject b " +"on
			 * a.accpackageid=? and b.accpackageid=? " +"and
			 * a.subjectid=b.SubjectID " +"set
			 * a.subjectname1=b.subjectname,a.SubjectFullName1=b.SubjectFullName";
			 * 铁路的帐要2分50秒
			 */
			/*
			 * sql = "update t_c_subjectentry a join " +"(select
			 * SubjectID,subjectname,subjectfullname " +"from t_c_accpkgsubject
			 * where accpackageid=?) b " +"on a.subjectid=b.SubjectID " +"set
			 * a.subjectname1=b.subjectname,a.SubjectFullName1=b.SubjectFullName "
			 * +"where a.accpackageid=?"; 铁路的帐要2分40秒
			 */
			sql = "drop table if EXISTS t1_" + AccPackageID;
			ps1 = conn.prepareStatement(sql);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "create table t1_"
					+ AccPackageID
					+ " select a.AccPackageID,a.VoucherID,a.OldVoucherID,a.TypeID,a.VchDate,a.Serail,a.Summary,"
					+ "a.subjectid,a.Dirction,a.OccurValue,a.CurrRate,a.CurrValue,a.Currency,a.Quantity,a.UnitPrice,a.UnitName,a.BankID,"
					+ "a.Property,b.SubjectName as SubjectName1,b.subjectfullname as subjectfullname1 "
					+ "from t_c_subjectentry a , t_c_accpkgsubject b "
					+ "where a.accpackageid=? and b.accpackageid=? and a.subjectid=b.SubjectID";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "delete from t_c_subjectentry where accpackageid=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "insert into t_c_subjectentry (AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,subjectid,"
					+ "Dirction,OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,BankID,Property,SubjectName1,subjectfullname1)"
					+ "select * from t1_" + AccPackageID;
			ps1 = conn.prepareStatement(sql);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "drop table t1_" + AccPackageID;
			ps1 = conn.prepareStatement(sql);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 检查是否存在期初数总分不平，且总平分不平的情况 */
			org.util.Debug.prtOut("B10=" + new ASFuntion().getCurrentTime());

			double dZfbp1 = 0, dZfbp2 = 0;//dZfbp1 末级、dZfbp2 一级
			sql = "select  abs(sum(a.debitremain)+sum(a.creditremain)) "
					+ "from "
					+ tableName
					+ " a,t_c_accpkgsubject b "
					+ "where a.accpackageid=? and a.datatype='0' and b.accpackageid=? "
					+ "and b.isleaf=1 and a.subjectid=b.subjectid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			rs = ps.executeQuery();
			if (rs.next()) {
				dZfbp1 = rs.getDouble(1);
				if (dZfbp1 != 0) {
					// 发现本帐套数据底层科目年初数借贷不平衡";
					rs.close();
					ps.close();
					strResult = "<br>科目汇总发现本帐套数据底层科目年初数借贷不平衡" + dZfbp1 + "<br>";

					sql = "select  abs(sum(a.debitremain)+sum(a.creditremain)) "
							+ "from "
							+ tableName
							+ " a,t_c_accpkgsubject b "
							+ "where a.accpackageid=? and a.datatype='0' and b.accpackageid=? "
							+ "and b.level0=1 and a.subjectid=b.subjectid";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.setString(2, AccPackageID);
					rs = ps.executeQuery();
					if (rs.next()) {
						dZfbp2 = rs.getDouble(1);
						if (dZfbp2 != 0) {
							strResult += "科目汇总发现本帐套数据一级科目年初数借贷不平衡" + dZfbp2
									+ "，该帐套存在严重问题！<br>";
							rs.close();
							ps.close();

						} else {
							strResult += "科目汇总发现本帐套数据一级科目年初数借贷平衡，<br>"
									+ "系统将按照一级科目期初数来计算余额表，请注意下列科目的总分将不平衡：<br>";
							rs.close();
							ps.close();

							/*
							 * 这个SQL太慢，而且阅读不好理解，优化一下 sql="select b.subjectid as
							 * s2,(d.debitremain+d.creditremain) as
							 * d2,sum(c.debitremain+c.creditremain) as d1 "
							 * +"from t_c_accpkgsubject a,t_c_accpkgsubject
							 * b,t_c_accpkgsubjectbegin c,t_c_accpkgsubjectbegin
							 * d " +"where a.accpackageid=? and a.isleaf=1 and
							 * a.level0>1 and b.accpackageid=? and b.level0=1 "
							 * +"and a.subjectfullname like
							 * concat(b.subjectfullname,'/%') " +"and
							 * c.accpackageid=? and c.datatype='0' and
							 * c.subjectid=a.subjectid " +"and d.accpackageid=?
							 * and d.datatype='0' and d.subjectid=b.subjectid "
							 * +"group by s2,d2 having d2<>sum(c.debitremain+c.creditremain)";
							 */
							sql = "select t1.subjectid,t1.d1,sum(t2.d2) "
									+ "from (select a.subjectfullname,a.subjectid,b.debitremain+b.creditremain as d1 "
									+ "from t_c_accpkgsubject a,"
									+ tableName
									+ " b "
									+ "where a.accpackageid=? and a.level0=1 "
									+ "and b.accpackageid=? and b.datatype='0' and b.subjectid=a.subjectid "
									+ ") t1,( "
									+ "select a.subjectfullname,a.subjectid,b.debitremain+b.creditremain as d2 "
									+ "from t_c_accpkgsubject a,"
									+ tableName
									+ " b "
									+ "where a.accpackageid=? and a.isleaf=1 and a.level0>1 "
									+ "and b.accpackageid=? and b.datatype='0' and b.subjectid=a.subjectid "
									+ ") t2 "
									+ "where t2.subjectfullname like concat(t1.subjectfullname,'/%') "
									+ "group by subjectid,d1 having t1.d1<>sum(t2.d2)";
							ps = conn.prepareStatement(sql);
							ps.setString(1, AccPackageID);
							ps.setString(2, AccPackageID);
							ps.setString(3, AccPackageID);
							ps.setString(4, AccPackageID);
							rs = ps.executeQuery();
							while (rs.next()) {
								strResult += "科目号:" + rs.getString(1)
										+ "一级科目期初数:" + rs.getDouble(2)
										+ "下级底层科目期初数:" + rs.getDouble(3)
										+ "<br>";
							}

						}
					} // if (rs.next()){
				} // if (dZfbp1!=0){
			}
			rs.close();
			ps.close();

			/* 分平或者总不平、分不平的情况下，可以删除总的期初数 */
			String strid = "''";
			if (dZfbp1 == 0) {
				sql = "select GROUP_CONCAT(distinct b.subjectid SEPARATOR \"','\") from \n"
						+ ""
						+ tableName
						+ " a,t_c_accpkgsubject b where a.accpackageid=? and b.accpackageid=? "
						+ "and b.isleaf=0 and a.subjectid=b.subjectid";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				rs = ps.executeQuery();
				if (rs.next()) {
					strid = "'" + rs.getString(1) + "'";
				}
				rs.close();
				ps.close();

				sql = "delete t1 from " + tableName + " t1 \n"
						+ "where t1.accpackageid=? and t1.subjectid in ("
						+ strid + ")";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				// ps.setString(2, AccPackageID);
				// ps.setString(3, AccPackageID);
				ps.execute();
				conn.commit();
			}

			/**
			 * =======================================================================
			 * 开始汇总本位币余额表
			 * =======================================================================
			 */
			// 插入前先删除原有的记录
			org.util.Debug.prtOut("A1=" + new ASFuntion().getCurrentTime());
			sql = "delete from t_c_account where AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/**
			 * 修改栏目凭证 开始
			 */
			sql = "select group_concat(\"'\",subjectid,\"'\") from t_c_accpkgsubject b where b.accpackageid=? and b.property like '12%'";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			rs = ps1.executeQuery();
			if (rs.next()) {
				String str = rs.getString(1);
				sql = "update t_c_subjectentry a set a.dirction = -1 , a.OccurValue = (-1)* a.OccurValue where a.accpackageid=? and a.dirction = 1 and a.subjectid in ("
						+ str + ") ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
			}
			rs.close();
			ps1.close();

			sql = "select group_concat(\"'\",subjectid,\"'\") from t_c_accpkgsubject b where b.accpackageid=? and b.property like '11%'";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			rs = ps1.executeQuery();
			if (rs.next()) {
				String str = rs.getString(1);
				sql = "update t_c_subjectentry a set a.dirction = 1 , a.OccurValue = (-1)* a.OccurValue where a.accpackageid=? and a.dirction = -1 and a.subjectid in ("
						+ str + ") ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
			}
			rs.close();
			ps1.close();
			/**
			 * 修改栏目凭证 结束
			 */

			// 设置本位币的期初数，如果没有设置的话
			org.util.Debug.prtOut("A2=" + new ASFuntion().getCurrentTime());
			sql = "update "
					+ tableName
					+ " "
					+ "set datatype='0' where accpackageid=? and (datatype is null or datatype ='')";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 优先插入凭证科目（包括叶子和栏目上上级科目）发生额汇总值记录 */
			org.util.Debug.prtOut("A3=" + new ASFuntion().getCurrentTime());
			/*
			 * 优化 sql = "insert into
			 * t_c_account(AccPackageID,SubjectID,AccName,subjectfullname1,SubYearMonth,SubMonth,debitocc,creditocc,dataname,direction,isleaf1,level1) " +
			 * "select ?,tt1.subjectid,c.subjectname
			 * ,c.subjectfullname,?,submonth,debitocc,creditocc,0," +"CASE
			 * substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0
			 * END,c.isleaf,c.level0 " +"from " + "(select
			 * t1.debitocc,t2.creditocc,t1.submonth,t1.subjectid from " +
			 * "(select sum(OccurValue) as
			 * debitocc,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth from t_c_subjectentry " + "where
			 * t_c_subjectentry.AccPackageID=? " + "and
			 * t_c_subjectentry.Dirction=1 " + "and t_c_subjectentry.Property
			 * like '1%' " + "group by t_c_subjectentry.subjectid,
			 * substring(t_c_subjectentry.vchdate,6,2)) t1 " + "left outer join " +
			 * "(select sum(OccurValue) as
			 * creditocc,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth from t_c_subjectentry " + "where
			 * t_c_subjectentry.AccPackageID=? " + "and
			 * t_c_subjectentry.Dirction=-1 " + "and t_c_subjectentry.Property
			 * like '1%' " + "group by t_c_subjectentry.subjectid,
			 * substring(t_c_subjectentry.vchdate,6,2)) t2 " + "on
			 * t1.subjectid=t2.subjectid and t1.submonth=t2.submonth " + "union " +
			 * "select t1.debitocc,t2.creditocc,t2.submonth,t2.subjectid " +
			 * "from " + "(select sum(OccurValue) as
			 * debitocc,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth from t_c_subjectentry " + "where
			 * t_c_subjectentry.AccPackageID=? " + "and
			 * t_c_subjectentry.Dirction=1 " + "and t_c_subjectentry.Property
			 * like '1%' " + "group by t_c_subjectentry.subjectid,
			 * substring(t_c_subjectentry.vchdate,6,2)) t1 " + "right outer join " +
			 * "(select sum(OccurValue) as
			 * creditocc,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth from t_c_subjectentry " + "where
			 * t_c_subjectentry.AccPackageID=? " + "and
			 * t_c_subjectentry.Dirction=-1 " + "and t_c_subjectentry.Property
			 * like '1%' " + "group by t_c_subjectentry.subjectid,
			 * substring(t_c_subjectentry.vchdate,6,2)) t2 " + "on
			 * t1.subjectid=t2.subjectid and t1.submonth=t2.submonth)
			 * tt1,t_c_accpkgsubject c " + "where c.AccPackageID=? and
			 * c.subjectid=tt1.SubjectID";
			 */
			sql = "insert into t_c_account(AccPackageID,SubjectID,AccName,subjectfullname1,SubYearMonth,SubMonth,debitocc,creditocc,dataname,direction,isleaf1,level1) \n"
					+ "select ?,tt1.subjectid,c.subjectname ,c.subjectfullname,?,submonth,debitocc,creditocc,0, \n"
					+ "CASE substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END,c.isleaf,c.level0  \n"
					+ "from  \n"
					+ "( \n"
					+ "select sum(case when Dirction=1 then OccurValue else 0 end) as debitocc, \n"
					+ "sum(case when Dirction=-1 then OccurValue else 0 end) as creditocc, \n"
					+ "subjectid,substring(vchdate,6,2) as submonth from t_c_subjectentry \n"
					+ "where AccPackageID=?  \n"
					+ "and Property like '1%'  \n"
					+ "group by subjectid, substring(vchdate,6,2) \n"
					+ "\n"
					+ ") tt1 ,t_c_accpkgsubject c \n"
					+ "where c.AccPackageID=?  and c.subjectid=tt1.SubjectID \n";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/*
			 * 补充生成未出现的叶子科目的月份记录， 还有前面汇总过程中出现的底层科目节点（因下级还有栏目，所以实际非底层）的月份记录
			 * cooler修改于20070414,让生成的余额表只包含不为0的数据
			 * cooler修改于20070725,总平分不平的情况下，不删除，余额表有为0的数据
			 */
			org.util.Debug.prtOut("A4=" + new ASFuntion().getCurrentTime());
			sql = "delete from " + tableName + " where accpackageid=? "
					+ "and debitremain=0 and creditremain=0 and debitremainF=0 and creditremainF=0";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "insert into t_c_account(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,direction,isleaf1,level1) "
					+ "select subjectid,?,subjectname,subjectfullname,?,b.submonth,0,"
					+ "CASE substring(a.property,2,1) WHEN '2' THEN -1 "
					+ "WHEN '1' THEN 1 ELSE 0 END,isleaf,level0 "
					+ "from t_c_accpkgsubject as a,k_month b "
					+ "where a.AccPackageID=? and "
					+ "( a.subjectid in " // +"( a.isleaf=1 or subjectid in "
					+ "(select distinct subjectid from t_c_account where AccPackageID=? union "
					+ "select distinct subjectid  from "
					+ tableName
					+ " where accpackageid=? and datatype='0'))"
					+ "and b.monthtype=12 and not exists ("
					+ "select 1 from t_c_account c where c.AccPackageID=? and a.subjectid=c.SubjectID and b.submonth=c.submonth)";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.setString(5, AccPackageID);
			ps1.setString(6, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 更新期初数 */
			org.util.Debug.prtOut("A5=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_account a join "
					+ tableName
					+ " b "
					+ "on a.AccPackageID=? and a.submonth=1 and b.AccPackageID=? and b.datatype='0' "
					+ "and a.subjectid=b.subjectid set a.DebitRemain=b.DebitRemain,a.creditremain=b.creditremain";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_account set debitremain=0 where debitremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_account set creditremain=0 where creditremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_account set DebitOcc=0.00 where DebitOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_account set CreditOcc=0.00 where CreditOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/**
			 * 当叶子科目的期初数是平的，删除掉所有非叶子科目的期初数
			 */
			sql = " select sum(a.debitremain)+sum(a.creditremain) "
				+ " from t_c_account a "
				+ " where a.accpackageid=? and a.submonth='1' and a.isleaf1=1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			conn.commit();
			double dZfbp = 1;
			if(rs.next()){
				dZfbp = rs.getDouble(1);
			}
			rs.close();
			ps.close();
			if (dZfbp == 0) {
				sql = "update t_c_account set debitremain = 0.00 ,creditremain = 0.00 where accpackageid=? and isleaf1 = 0 and submonth=1";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();
			}
			
			
			/* 更新第一个月的叶子和底层含栏目科目记录的累计借方数/贷方数/余额字段 */
			org.util.Debug.prtOut("A6=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_account set DebitTotalOcc=DebitOcc,CreditTotalOcc=CreditOcc,"
					+ "balance=debitremain+creditremain+debitocc-creditocc,"
					
					//借期末=方向为1，余额是借；方向为-1，余额是贷
//					+ "debitbalance=case direction when 1 then debitremain+creditremain+debitocc-creditocc when -1 then 0 else case debitremain+creditremain+debitocc-creditocc>=0 when true then debitremain+creditremain+debitocc-creditocc else 0 end end,"
//					+ "creditbalance=case direction when -1 then debitremain+creditremain+debitocc-creditocc when 1 then 0 else case debitremain+creditremain+debitocc-creditocc<0 when true then debitremain+creditremain+debitocc-creditocc else 0 end end "
					
					//借期末=借期初+借发生,贷期末=贷期初+贷发生
//					+ "debitbalance = debitremain + debitocc,"		
//					+ "creditbalance = creditremain - creditocc "
					
					//借期末=余额为正，就是借；为负，就是贷
					+ "debitbalance= if(debitremain+creditremain+debitocc-creditocc>0,debitremain+creditremain+debitocc-creditocc,0) ,"
					+ "creditbalance=if(debitremain+creditremain+debitocc-creditocc<0,debitremain+creditremain+debitocc-creditocc,0) "
					
					+ "where AccPackageID=? and SubMonth='1'";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 更新叶子科目节点2月到12月的期初数,期末数,余额,累计额等字段 */
			int iSubMonthCount = 12;
			for (i = 2; i <= iSubMonthCount; i++) {
				org.util.Debug.prtOut("A7=" + new ASFuntion().getCurrentTime());
				sql = "update t_c_account t1 join t_c_account t2 "
						+ "on t2.accpackageid=? and t2.submonth=? and t1.subjectid=t2.subjectid "
						+ "set t1.debitremain=t2.debitbalance,"
						+ "t1.creditremain=t2.creditbalance,"
						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,"
						+ "t1.credittotalocc=t2.credittotalocc+t1.creditocc,"
						+ "t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
						
//						借期末=借期初+借发生,贷期末=贷期初+贷发生
//						+ "t1.debitbalance = t2.debitbalance + t1.debitocc ,"
//						+ "t1.creditbalance = t2.creditbalance -t1.creditocc "
						
//						借期末=余额为正，就是借；为负，就是贷
						+ "t1.debitbalance = if(t2.balance +t1.debitocc -t1.creditocc>0,t2.balance +t1.debitocc -t1.creditocc,0) ,"
						+ "t1.creditbalance =if(t2.balance +t1.debitocc -t1.creditocc<0,t2.balance +t1.debitocc -t1.creditocc,0) "

						
//						借期末=方向为1，余额是借；方向为-1，余额是贷
//						+ "t1.debitremain=(case t1.direction when 1 then t2.balance when -1 then 0 else case t2.balance>=0 when true then t2.balance else 0 end end ),"
//						+ "t1.creditremain=(case t1.direction when -1 then t2.balance when 1 then 0 else case t2.balance<0 when true then t2.balance  else 0 end end ),"
//						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,t1.credittotalocc=t2.credittotalocc+t1.creditocc,t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
//						+ "t1.debitbalance=case t1.direction when 1 then (t2.balance +t1.debitocc -t1.creditocc) when -1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)>=0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end,"
//						+ "t1.creditbalance=case t1.direction when -1 then (t2.balance +t1.debitocc -t1.creditocc) when 1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)<0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end "
						
						+ "where t1.accpackageid=? and t1.submonth=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, i - 1);
				ps1.setString(3, AccPackageID);
				ps1.setInt(4, i);
				ps1.execute();
				conn.commit();
				ps1.close();
			}

			/*
			 * 插入非叶子科目记录的1到12月的记录,由于有栏目的情况下会出现非叶子节点也有凭证，
			 * 所以前面的汇总已经发生了出现了非叶子的汇总记录，所以要排除出去
			 */
			org.util.Debug.prtOut("A8=" + new ASFuntion().getCurrentTime());
			sql = "insert into t_c_account(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,direction,isleaf1,level1) "
					+ "select subjectid,?,subjectname,subjectfullname,?,b.submonth,0, "
					+ "CASE substring(a.property,2,1) WHEN '2' THEN -1 "
					+ "WHEN '1' THEN 1 ELSE 0 END,isleaf,level0 "
					+ "from t_c_accpkgsubject as a,k_month b "
					+ "where a.AccPackageID=? and a.isleaf=0 and b.monthtype=12 "
					+ "and not exists  ( "
					+ "select  1 from t_c_account c where c.AccPackageID=? and c.subjectid=a.subjectid and c.submonth=b.submonth)";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 计算非叶子科目的期初数等字段 */
			// 首先取得总共的层次数目
			sql = "select max(level0) from t_c_accpkgsubject where AccPackageID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			rs.next();
			maxlevel = rs.getInt(1);
			rs.close();
			ps.close();

			for (i = maxlevel; i > 1; i--) {
				org.util.Debug.prtOut("A9=" + new ASFuntion().getCurrentTime());
				/*
				 * 这里是会按照科目方向来汇总的代码 sql = "update t_c_account t1 join (select
				 * a.ParentSubjectId,b.submonth," + "sum(b.DebitRemain) as
				 * tDebitRemain,sum(b.CreditRemain) as tCreditRemain," +
				 * "sum(DebitOcc) as tDebitOcc,sum(CreditOcc) as
				 * tCreditOcc,sum(balance) as tBalance," + "sum(DebitTotalOcc)
				 * as tDebitTotalOcc,sum(CreditTotalOcc) as tCreditTotalOcc " + "
				 * FROM t_c_accpkgsubject a,t_c_account b where a.AccPackageID=? " +
				 * "and a.level0=? and b.AccPackageID=? and a.subjectid =
				 * b.subjectid " + "group BY ParentSubjectId,submonth) as t2 on " +
				 * "t1.AccPackageID=? and t1.subjectid=t2.ParentSubjectId and
				 * t1.submonth=t2.submonth " +" set t1.DebitRemain=case
				 * t1.direction when 0 then t1.DebitRemain+t2.tDebitRemain " +"
				 * when 1 THEN
				 * t1.DebitRemain+t2.tDebitRemain+t1.CreditRemain+t2.tCreditRemain
				 * else 0 end," +" t1.CreditRemain=case t1.direction when 0 then
				 * t1.CreditRemain+t2.tCreditRemain " +" when -1 THEN
				 * t1.DebitRemain+t2.tDebitRemain+t1.CreditRemain+t2.tCreditRemain
				 * else 0 end," +"
				 * t1.DebitOcc=t1.DebitOcc+t2.tDebitOcc,t1.CreditOcc=t1.CreditOcc+t2.tCreditOcc,t1.DebitTotalOcc=t1.DebitTotalOcc+t2.tDebitTotalOcc,"
				 * +"t1.CreditTotalOcc=t1.CreditTotalOcc+t2.tCreditTotalOcc,t1.Balance=t1.Balance+t2.tBalance";
				 */
				// 这里是不按科目方向来汇总的代码
				sql = "update t_c_account t1 join (select a.ParentSubjectId,b.submonth,"
						+ "sum(b.DebitRemain) as tDebitRemain,sum(b.CreditRemain) as tCreditRemain,"
						+ "sum(DebitOcc) as tDebitOcc,sum(CreditOcc) as tCreditOcc,sum(balance) as tBalance,"
						+ "sum(DebitTotalOcc) as tDebitTotalOcc,sum(CreditTotalOcc) as tCreditTotalOcc,"
						+ "sum(DebitBalance) as tDebitBalance,sum(CreditBalance) as tCreditBalance "
						+ " FROM t_c_accpkgsubject a,t_c_account b where a.AccPackageID=? "
						+ "and a.level0=? and b.AccPackageID=? and a.subjectid = b.subjectid "
						+ "group BY ParentSubjectId,submonth) as t2 on "
						+ "t1.AccPackageID=? and t1.subjectid=t2.ParentSubjectId "
						+ "and t1.submonth=t2.submonth set t1.DebitRemain=t1.DebitRemain+t2.tDebitRemain,t1.CreditRemain=t1.CreditRemain+t2.tCreditRemain,t1.DebitOcc=t1.DebitOcc+t2.tDebitOcc,"
						+ "t1.CreditOcc=t1.CreditOcc+t2.tCreditOcc,t1.DebitTotalOcc=t1.DebitTotalOcc+t2.tDebitTotalOcc,t1.CreditTotalOcc=t1.CreditTotalOcc+t2.tCreditTotalOcc,t1.Balance=t1.Balance+t2.tBalance,"
						+ "t1.debitBalance=t1.debitBalance+t2.tdebitBalance,t1.creditBalance=t1.creditBalance+t2.tcreditBalance";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, i);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
			}

			// 如果出现了总平分不平，则放弃从下层汇总的机制，重算所有科目的期初和期末数（）
			// 总不平分也不平，还是这个逻辑
			if ((dZfbp1 != 0 && dZfbp2 < 0.005) || (dZfbp1 != 0 && dZfbp2 >= 0.005) ) {
				/* 重新更新非叶子科目的第一个月的期初数 */
				org.util.Debug.prtOut("C5=" + new ASFuntion().getCurrentTime());
				sql = "update t_c_account a join "
						+ tableName
						+ " b "
						+ "on a.AccPackageID=? and a.submonth=1 and a.isleaf1=0 and b.AccPackageID=? and b.datatype='0' "
						+ "and a.subjectid=b.subjectid set a.DebitRemain=b.DebitRemain,a.creditremain=b.creditremain";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setString(2, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();

				/**
				 * 因为之前删除了期初借和期初贷都为0的期初表，结果会导致上一条执行
				 * 后这些account的记录的期初数没有变成0，还是下级科目 所以要强制这部分记录归零
				 */
				sql = "update t_c_account set debitremain=0,creditremain=0 "
						+ "where accpackageid=? and isleaf1=0 and  not exists( "
						+ "select 1 from "
						+ tableName
						+ " b where b.accpackageid=? "
						+ "and b.datatype='0' and t_c_account.subjectid=b.subjectid)";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setString(2, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();

				sql = "update t_c_account set debitremain=0 where debitremain is null and AccPackageID=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();

				sql = "update t_c_account set creditremain=0 where creditremain is null and AccPackageID=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();

				sql = "update t_c_account set DebitOcc=0.00 where DebitOcc is null and AccPackageID=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();

				sql = "update t_c_account set CreditOcc=0.00 where CreditOcc is null and AccPackageID=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();

				/* 更新第一个月的非叶子科目记录的余额、借余额、贷余额字段,累计借方数/贷方数不用更新 */
				org.util.Debug.prtOut("C6=" + new ASFuntion().getCurrentTime());
				sql = "update t_c_account set balance=debitremain+creditremain+debitocc-creditocc,"
						+ "debitbalance=if(debitremain+creditremain+debitocc-creditocc>0,debitremain+creditremain+debitocc-creditocc,0),"
						+ "creditbalance=if(debitremain+creditremain+debitocc-creditocc<0,debitremain+creditremain+debitocc-creditocc,0) "
						+ "where AccPackageID=? and SubMonth='1' and isleaf1=0 ";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();

				/* 更新一级科目节点2月到12月的期初数,期末数,余额等字段 */
				for (i = 2; i <= iSubMonthCount; i++) {
					org.util.Debug.prtOut("C7="
							+ new ASFuntion().getCurrentTime());
					sql = "update t_c_account t1 join t_c_account t2 "
							+ "on t2.accpackageid=? and t2.submonth=? and t2.isleaf1=0 and t1.subjectid=t2.subjectid "
							+ "set t1.debitremain=t2.debitbalance,"
							+ "t1.creditremain=t2.creditbalance,"
							+ "t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
							+ "t1.debitbalance=if(t2.balance +t1.debitocc -t1.creditocc>0,t2.balance +t1.debitocc -t1.creditocc,0),"
							+ "t1.creditbalance=if(t2.balance +t1.debitocc -t1.creditocc<0,t2.balance +t1.debitocc -t1.creditocc,0) "
							+ "where t1.accpackageid=? and t1.submonth=? and t1.isleaf1=0 ";
					ps1 = conn.prepareStatement(sql);
					ps1.setString(1, AccPackageID);
					ps1.setInt(2, i - 1);
					ps1.setString(3, AccPackageID);
					ps1.setInt(4, i);
					ps1.execute();
					conn.commit();
					ps1.close();
				}
			}

			/**
			 * 删除掉没有任何发生的余额表记录
			 */
			org.util.Debug.prtOut("A10=" + new ASFuntion().getCurrentTime());
//			sql = "delete a from t_c_account a , (select subjectid from ("
//					+ "select subjectid from "
//					+ "(select subjectid,submonth from t_c_account "
//					+ "where accpackageid=? and debitremain=0 and creditremain=0 and creditocc=0 and debitocc=0) t "
//					+ "group by subjectid having count(*)=12) t1 "
//					+ "where not exists (select 1 from t_c_subjectentry c "
//					+ "where c.accpackageid=? and c.subjectid=t1.subjectid)) b "
//					+ "where a.accpackageid=? and a.subjectid=b.subjectid";
			
			sql = "delete a from t_c_account a , ("
				+ "		select distinct t1.subjectid from ( "
				+ "			select subjectid from t_c_account  "
				+ "			where accpackageid=? and debitremain=0 and creditremain=0 and creditocc=0 and debitocc=0"
				+ "			group by subjectid having count(*)=12"
				+ "		) t1 left join (select distinct subjectid from t_c_subjectentry c where c.accpackageid=? ) c "
				+ "		on c.subjectid like concat(t1.subjectid,'%') "
				+ " 	where c.subjectid is null "
				+ ") b "
				+ "where a.accpackageid=? and a.subjectid=b.subjectid";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();

			/**
			 * =======================================================================
			 * 开始汇总多币种和数量帐的余额表，只会生成有数据（发生或期初数）的，其余的就不生成了
			 * =======================================================================
			 */

			org.util.Debug.prtOut("1=" + new ASFuntion().getCurrentTime());
			sql = "delete from t_c_accountall where AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("2=" + new ASFuntion().getCurrentTime());

			// 清理掉为0的期初数表
			sql = "delete from " + tableName
					+ " where AccPackageID=? and datatype='0'";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			// 有些数量期初数的记录没有填对应的数量值，在此补上
			/*
			 * 这一句话执行不了，很奇怪很奇怪 sql = "update "+tableName+" a JOIN " +"(select
			 * distinct subjectid,unitname from t_c_subjectentry " +"where
			 * accpackageid=? and unitname>'') b " +"on a.accpackageid=? and
			 * a.datatype='数量账' and a.subjectid=b.subjectid " +"set
			 * a.datatype=b.UnitName"; ps1 = conn.prepareStatement(sql);
			 * ps1.setString(1, AccPackageID); ps1.setString(2, AccPackageID);
			 * ps1.execute(); conn.commit(); ps1.close();
			 */

			/* 优先插入凭证科目（包括叶子和栏目上上级科目）外币发生额汇总值记录到多币种/数量总账表 */
			/**
			 * 优化于20070917
			 * 
			 * sql = "insert into
			 * t_c_accountall(AccPackageID,SubjectID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,accsign,direction,isleaf1,level1) " +
			 * "select
			 * ?,tt1.subjectid,c.subjectname,c.subjectfullname,?,submonth,tt1.currency,debitocc,creditocc,1," +
			 * "CASE substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1
			 * ELSE 0 END,c.isleaf,c.level0 " + "from " + "(select
			 * t1.subjectid,t1.submonth,t1.currency,t1.debitocc,t2.creditocc
			 * from " + "(select
			 * currency,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth ,sum(currValue) as debitocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=1 and currency >'' and
			 * Property like '1%' " + "group by currency,subjectid,
			 * substring(vchdate,6,2) " + ") t1 " + "left outer join " +
			 * "(select
			 * currency,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth,sum(currValue) as creditocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=-1 and currency >'' and
			 * Property like '1%' " + "group by currency,subjectid,
			 * substring(vchdate,6,2) " + ") t2 " + "on t1.currency=t2.currency
			 * and t1.subjectid=t2.subjectid and t1.submonth=t2.submonth " +
			 * "union " + "select
			 * t2.subjectid,t2.submonth,t2.currency,t1.debitocc,t2.creditocc
			 * from " + "(select
			 * currency,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth ,sum(currValue) as debitocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=1 and currency >'' and
			 * Property like '1%' " + "group by currency,subjectid,
			 * substring(vchdate,6,2) " + ") t1 " + "right outer join " +
			 * "(select
			 * currency,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth,sum(currValue) as creditocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=-1 and currency >'' and
			 * Property like '1%' " + "group by currency,subjectid,
			 * substring(vchdate,6,2) " + ") t2 " + "on t1.currency=t2.currency
			 * and t1.subjectid=t2.subjectid and t1.submonth=t2.submonth " + ")
			 * tt1,t_c_accpkgsubject c " + "where c.AccPackageID=? and
			 * c.subjectid=tt1.SubjectID;";
			 */
			// 外币
			sql = "insert into t_c_accountall(AccPackageID,SubjectID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,debitoccF,creditoccF,accsign,direction,isleaf1,level1) \n"
					+ "select ?,tt1.subjectid,c.subjectname,c.subjectfullname,?,submonth,tt1.currency,debitocc,creditocc,debitoccF,creditoccF,1, \n"
					+ "CASE substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END,c.isleaf,c.level0  \n"
					+ "from  \n"
					+ "( \n"
					+ "	select currency,subjectid,substring(vchdate,6,2) as submonth , \n"
					+ "	sum(case when Dirction=1 then currValue else 0 end) as debitocc, \n"
					+ "	sum(case when Dirction=-1 then currValue else 0 end) as creditocc, \n"

					+ "	sum(case when Dirction=1 then OccurValue else 0 end) as debitoccF, \n"
					+ "	sum(case when Dirction=-1 then OccurValue else 0 end) as creditoccF \n"

					+ "          from t_c_subjectentry  \n"
					+ "          where AccPackageID=? and currency >'' and Property like '1%' \n"
					+ "          group by currency,subjectid, substring(vchdate,6,2)  \n"
					+ ") tt1,t_c_accpkgsubject c  \n"
					+ "where c.AccPackageID=?  and c.subjectid=tt1.SubjectID";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("3=" + new ASFuntion().getCurrentTime());

			/* 优先插入凭证科目（包括叶子和栏目上上级科目）数量发生额汇总值记录到多币种/数量总账表 */
			/**
			 * 优化于20070917 sql = "insert into
			 * t_c_accountall(AccPackageID,SubjectID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,accsign,direction,isleaf1,level1) " +
			 * "select
			 * ?,tt1.subjectid,c.subjectname,c.subjectfullname,?,submonth,tt1.UnitName,debitocc,creditocc,2," +
			 * "CASE substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1
			 * ELSE 0 END,c.isleaf,c.level0 " + "from " + "(select
			 * t1.subjectid,t1.submonth,t1.UnitName,t1.debitocc,t2.creditocc
			 * from " + "(select
			 * UnitName,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth ,sum(Quantity) as debitocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=1 and UnitName > '' and
			 * Property like '1%' " + "group by UnitName,subjectid,
			 * substring(vchdate,6,2) " + ") t1 " + "left outer join " +
			 * "(select
			 * UnitName,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth,sum(Quantity) as creditocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=-1 and UnitName > '' and
			 * Property like '1%' " + "group by UnitName,subjectid,
			 * substring(vchdate,6,2) " + ") t2 " + "on t1.UnitName=t2.UnitName
			 * and t1.subjectid=t2.subjectid and t1.submonth=t2.submonth " +
			 * "union " + "select
			 * t2.subjectid,t2.submonth,t2.UnitName,t1.debitocc,t2.creditocc
			 * from " + "(select
			 * UnitName,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth ,sum(Quantity) as debitocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=1 and UnitName > '' and
			 * Property like '1%' " + "group by UnitName,subjectid,
			 * substring(vchdate,6,2) " + ") t1 " + "right outer join " +
			 * "(select
			 * UnitName,subjectid,substring(t_c_subjectentry.vchdate,6,2) as
			 * submonth,sum(Quantity) as creditocc " + "from t_c_subjectentry " +
			 * "where AccPackageID=? and Dirction=-1 and UnitName > '' and
			 * Property like '1%' " + "group by UnitName,subjectid,
			 * substring(vchdate,6,2) " + ") t2 " + "on t1.UnitName=t2.UnitName
			 * and t1.subjectid=t2.subjectid and t1.submonth=t2.submonth " + ")
			 * tt1,t_c_accpkgsubject c " + "where c.AccPackageID=? and
			 * c.subjectid=tt1.SubjectID;";
			 */
			// 数量
			sql = "insert into t_c_accountall(AccPackageID,SubjectID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,debitoccF,creditoccF,accsign,direction,isleaf1,level1) \n"
					+ "select ?,tt1.subjectid,c.subjectname,c.subjectfullname,?,submonth,tt1.UnitName,debitocc,creditocc,debitoccF,creditoccF,2, \n"
					+ "CASE substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END,c.isleaf,c.level0 \n"
					+ "from \n"
					+ "( \n"
					+ "	select UnitName,subjectid,substring(vchdate,6,2) as submonth, \n"
					+ "	sum(case when Dirction=1 then Quantity else 0 end) as debitocc, \n"
					+ "	sum(case when Dirction=-1 then Quantity else 0 end) as creditocc, \n"

					+ "	sum(case when Dirction=1 then OccurValue else 0 end) as debitoccF, \n"
					+ "	sum(case when Dirction=-1 then OccurValue else 0 end) as creditoccF \n"
					+ "	from t_c_subjectentry  \n"
					+ "	where AccPackageID=?  and UnitName > '' and Property like '1%' \n"
					+ "	group by UnitName,subjectid, substring(vchdate,6,2) \n"
					+ ") tt1,t_c_accpkgsubject c \n"
					+ "where c.AccPackageID=?  and c.subjectid=tt1.SubjectID";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("4=" + new ASFuntion().getCurrentTime());

			/**
			 * 设置期初数表的项目类型字段是数量帐，还是外币帐currency
			 */
			sql = "select group_concat(distinct replace(currency,'\\'','\\\\\\'') SEPARATOR \"','\") from t_c_subjectentry where AccPackageID=? and currency>''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			strid = "''";
			if (rs.next()) {
				strid = "'" + rs.getString(1) + "'";
			}
			rs.close();
			ps.close();

			sql = "update " + tableName
					+ " set accsign=1 where AccPackageID=? and datatype in ("
					+ strid + ")";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			org.util.Debug.prtOut("5=" + new ASFuntion().getCurrentTime());
			sql = "select group_concat(distinct replace(unitname,'\\'','\\\\\\'') SEPARATOR \"','\") from t_c_subjectentry where AccPackageID=? and unitname>''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			strid = "''";
			if (rs.next()) {
				strid = "'" + rs.getString(1) + "'";
			}
			rs.close();
			ps.close();

			sql = "update " + tableName
					+ " set accsign=2 where AccPackageID=? and datatype in ("
					+ strid + ")";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			org.util.Debug.prtOut("5.5=" + new ASFuntion().getCurrentTime());
			sql = "update "
					+ tableName
					+ " a join ("
					+ "select distinct name from k_dic where ctype='currency' "
					+ "union select distinct value from k_dic where ctype='currency') b "
					+ "on a.accpackageid=? and a.accsign=0 and a.datatype=b.name set a.accsign=1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			sql = "update " + tableName + " set accsign=2 "
					+ "where accpackageid=? and accsign=0";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			org.util.Debug.prtOut("6=" + new ASFuntion().getCurrentTime());

			/*
			 * 补全叶子节点的其他月份的数额(这里和本位币不同，只补全已有叶子节点的不为0的， 其他叶子节点不会补D
			 * 
			 * 5分钟
			 */
//			sql = "insert into t_c_accountall(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,accsign,direction,isleaf1,level1)"
//					+ "select a.subjectid,?,a.accname,a.subjectfullname1,?,b.submonth,a.dataname,a.accsign,a.direction,a.isleaf1,a.level1 "
//					+ "from ( "
//					+ "select distinct subjectid,accname,subjectfullname1,dataname,accsign,direction,isleaf1,level1 "
//					+ "from t_c_accountall where AccPackageID=? "
//					+ ") a,k_month b "
//					+ "where b.monthtype=12 "
//					+ "and  not exists ( "
//					+ "select 1 from t_c_accountall c where c.AccPackageID=? and c.dataname=a.dataname "
//					+ "and c.subjectid=a.subjectid and c.submonth=b.submonth)";
//			ps1 = conn.prepareStatement(sql);
//			ps1.setString(1, AccPackageID);
//			ps1.setInt(2, year);
//			ps1.setString(3, AccPackageID);
//			ps1.setString(4, AccPackageID);
//			ps1.execute();
//			conn.commit();
//			ps1.close();

			for(int iMonth = 1;iMonth <= 12; iMonth++ ){
				sql = "insert into t_c_accountall(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,accsign,direction,isleaf1,level1)"
					+ "select distinct a.subjectid,?,a.accname,a.subjectfullname1,?,?,a.dataname,a.accsign,a.direction,a.isleaf1,a.level1 "
					+ "from  t_c_accountall a "
					+ "where AccPackageID=?  "
					+ "and  not exists ( "
					+ "select 1 from t_c_accountall c where c.AccPackageID=? and c.dataname=a.dataname "
					+ "and c.subjectid=a.subjectid and c.submonth=? )";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, year);
				ps1.setInt(3, iMonth);
				ps1.setString(4, AccPackageID);
				ps1.setString(5, AccPackageID);
				ps1.setInt(6, iMonth);
				ps1.execute();
				conn.commit();
				ps1.close();
				
			}
			
			org.util.Debug.prtOut("7=" + new ASFuntion().getCurrentTime());

			/* //再补全叶子节点中没有发生额但是有期初数的节点 2分钟*/
//			sql = "insert into t_c_accountall(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,accsign,direction,isleaf1,level1)"
//					+ "select a.subjectid,?,a.subjectname,a.subjectfullname,?,c.submonth,b.datatype,b.accsign,"
//					+ "CASE substring(a.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END,a.isleaf,a.level0 "
//					+ "from t_c_accpkgsubject as a,"
//					+ tableName
//					+ " b,k_month c "
//					+ "where a.AccPackageID=? and a.isleaf=1 "
//					+ "and b.AccPackageID=? and (abs(b.creditremain)>0.001 or abs(b.debitremain)>0.001) "
//					+ "and a.subjectid=b.SubjectID "
//					+ "and c.monthtype=12 "
//					+ "and  not exists ("
//					+ "select 1 from t_c_accountall d where d.AccPackageID=? and d.dataname=b.DataType "
//					+ "and d.subjectid=a.subjectid and d.submonth=c.submonth)";
//			ps1 = conn.prepareStatement(sql);
//			ps1.setString(1, AccPackageID);
//			ps1.setInt(2, year);
//			ps1.setString(3, AccPackageID);
//			ps1.setString(4, AccPackageID);
//			ps1.setString(5, AccPackageID);
//			ps1.execute();
//			conn.commit();
//			ps1.close();

			for(int iMonth = 1;iMonth <= 12; iMonth++ ){
				sql = "insert into t_c_accountall(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,accsign,direction,isleaf1,level1)"
					+ "select a.subjectid,?,a.subjectname,a.subjectfullname,?,?,b.datatype,b.accsign,"
					+ "CASE substring(a.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END,a.isleaf,a.level0 "
					+ "from t_c_accpkgsubject as a,"
					+ tableName
					+ " b "
					+ "where a.AccPackageID=? and a.isleaf=1 "
					+ "and b.AccPackageID=? and (abs(b.creditremain)>0.001 or abs(b.debitremain)>0.001) "
					+ "and a.subjectid=b.SubjectID "
					+ "and  not exists ("
					+ "		select 1 from t_c_accountall d where d.AccPackageID=? and d.dataname=b.DataType "
					+ "		and d.subjectid=a.subjectid and d.submonth=?"
					+ ")";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, year);
				ps1.setInt(3, iMonth);
				ps1.setString(4, AccPackageID);
				ps1.setString(5, AccPackageID);
				ps1.setString(6, AccPackageID);
				ps1.setInt(7, iMonth);
				ps1.execute();
				conn.commit();
				ps1.close();
			}
			
			
			org.util.Debug.prtOut("8=" + new ASFuntion().getCurrentTime());
			/* 更新期初数 */
			sql = "update t_c_accountall a join "
					+ tableName
					+ " b "
					+ "on a.AccPackageID=? and a.submonth=1 and b.AccPackageID=? and a.dataname=b.datatype "
					+ "and a.subjectid=b.subjectid set a.DebitRemain=b.DebitRemain,a.creditremain=b.creditremain,a.DebitRemainF=b.DebitRemainF,a.creditremainF=b.creditremainF";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			org.util.Debug.prtOut("9=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_accountall set debitremain=0 where debitremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set creditremain=0 where creditremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set DebitOcc=0.00 where DebitOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set CreditOcc=0.00 where CreditOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set debitremain=0 where debitremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set creditremain=0 where creditremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set DebitOcc=0.00 where DebitOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set CreditOcc=0.00 where CreditOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			//
			sql = "update t_c_accountall set debitremainF=0 where debitremainF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set creditremainF=0 where creditremainF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set DebitOccF=0.00 where DebitOccF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_accountall set CreditOccF=0.00 where CreditOccF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			org.util.Debug.prtOut("10=" + new ASFuntion().getCurrentTime());

			/* 更新第一个月的叶子和底层含栏目科目记录的累计借方数/贷方数/余额字段 */
			sql = "update t_c_accountall set DebitTotalOcc=DebitOcc,CreditTotalOcc=CreditOcc,balance=debitremain+creditremain+debitocc-creditocc,"
					
				
//					+ "debitbalance=case direction when 1 then debitremain+creditremain+debitocc-creditocc when -1 then 0 else case debitremain+creditremain+debitocc-creditocc>=0 when true then debitremain+creditremain+debitocc-creditocc else 0 end end,"
//					+ "creditbalance=case direction when -1 then debitremain+creditremain+debitocc-creditocc when 1 then 0 else case debitremain+creditremain+debitocc-creditocc<0 when true then debitremain+creditremain+debitocc-creditocc else 0 end end, "
//					+ "debitbalance=debitremain+debitocc,"
//					+ "creditbalance=creditremain-creditocc , "
				
					//借期末=余额为正，就是借；为负，就是贷
					+ "debitbalance= if(debitremain+creditremain+debitocc-creditocc>0,debitremain+creditremain+debitocc-creditocc,0) ,"
					+ "creditbalance=if(debitremain+creditremain+debitocc-creditocc<0,debitremain+creditremain+debitocc-creditocc,0) ,"

					+ "DebitTotalOccF=DebitOccF,CreditTotalOccF=CreditOccF,"
					+ "balanceF=debitremainF+creditremainF+debitoccF-creditoccF,"
					
//					+ "debitbalanceF=case direction when 1 then debitremainF+creditremainF+debitoccF-creditoccF when -1 then 0 else case debitremainF+creditremainF+debitoccF-creditoccF>=0 when true then debitremainF+creditremainF+debitoccF-creditoccF else 0 end end,"
//					+ "creditbalanceF=case direction when -1 then debitremainF+creditremainF+debitoccF-creditoccF when 1 then 0 else case debitremainF+creditremainF+debitoccF-creditoccF<0 when true then debitremainF+creditremainF+debitoccF-creditoccF else 0 end end "
//					+ "debitbalanceF=debitremainF+debitoccF,"
//					+ "creditbalanceF=creditremainF-creditoccF "
					
					//借期末=余额为正，就是借；为负，就是贷
					+ "debitbalanceF= if(debitremainF+creditremainF+debitoccF-creditoccF>0,debitremainF+creditremainF+debitoccF-creditoccF,0) ,"
					+ "creditbalanceF=if(debitremainF+creditremainF+debitoccF-creditoccF<0,debitremainF+creditremainF+debitoccF-creditoccF,0) "


					+ "where AccPackageID=? and SubMonth='1'";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 更新叶子科目节点2月到12月的期初数,期末数,余额,累计额等字段 */
			// 太大的科目体系下只能一条一条的更新
			org.util.Debug.prtOut("11:=" + new ASFuntion().getCurrentTime());
			for (i = 2; i <= iSubMonthCount; i++) {
				sql = "update t_c_accountall t1 join t_c_accountall t2 "
						+ "on t2.accpackageid=? and t2.submonth=? and t1.subjectid=t2.subjectid and t1.dataname=t2.dataname "
						+ "set "
						
						+ "t1.debitremain=t2.debitbalance ,"
						+ "t1.creditremain=t2.creditbalance,"
						
						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,"
						+ "t1.credittotalocc=t2.credittotalocc+t1.creditocc,"
						
						+ "t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
						
//						+ "t1.debitbalance=t2.debitbalance +t1.debitocc ,"
//						+ "t1.creditbalance=t2.creditbalance -t1.creditocc, "
						+ "t1.debitbalance=if(t2.balance +t1.debitocc -t1.creditocc>0,t2.balance +t1.debitocc -t1.creditocc,0) ,"
						+ "t1.creditbalance=if(t2.balance +t1.debitocc -t1.creditocc<0,t2.balance +t1.debitocc -t1.creditocc,0), "
						
						+ "t1.debitremainF=t2.debitbalanceF,"
						+ "t1.creditremainF=t2.creditbalanceF,"
						
						+ "t1.debittotaloccF=t2.debittotaloccF+t1.debitoccF,"
						+ "t1.credittotaloccF=t2.credittotaloccF+t1.creditoccF,"
						
						+ "t1.balanceF=t2.balanceF +t1.debitoccF -t1.creditoccF,"
						
//						+ "t1.debitbalanceF=t2.debitbalanceF +t1.debitoccF ,"
//						+ "t1.creditbalanceF=t2.creditbalanceF -t1.creditoccF "
						+ "t1.debitbalanceF=if(t2.balanceF +t1.debitoccF -t1.creditoccF>0,t2.balanceF +t1.debitoccF -t1.creditoccF,0) ,"
						+ "t1.creditbalanceF=if(t2.balanceF +t1.debitoccF -t1.creditoccF<0,t2.balanceF +t1.debitoccF -t1.creditoccF,0) "
						
//						+ "t1.debitremain=(case t1.direction when 1 then t2.balance when -1 then 0 else case t2.balance>=0 when true then t2.balance else 0 end end ),"
//						+ "t1.creditremain=(case t1.direction when -1 then t2.balance when 1 then 0 else case t2.balance<0 when true then t2.balance  else 0 end end ),"
//						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,t1.credittotalocc=t2.credittotalocc+t1.creditocc,t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
//						+ "t1.debitbalance=case t1.direction when 1 then (t2.balance +t1.debitocc -t1.creditocc) when -1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)>=0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end,"
//						+ "t1.creditbalance=case t1.direction when -1 then (t2.balance +t1.debitocc -t1.creditocc) when 1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)<0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end, "
//						+ "t1.debitremainF=(case t1.direction when 1 then t2.balanceF when -1 then 0 else case t2.balanceF>=0 when true then t2.balanceF else 0 end end ),"
//						+ "t1.creditremainF=(case t1.direction when -1 then t2.balanceF when 1 then 0 else case t2.balanceF<0 when true then t2.balanceF  else 0 end end ),"
//						+ "t1.debittotaloccF=t2.debittotaloccF+t1.debitoccF,t1.credittotaloccF=t2.credittotaloccF+t1.creditoccF,t1.balanceF=t2.balanceF +t1.debitoccF -t1.creditoccF,"
//						+ "t1.debitbalanceF=case t1.direction when 1 then (t2.balanceF +t1.debitoccF -t1.creditoccF) when -1 then 0 else case (t2.balanceF +t1.debitoccF -t1.creditoccF)>=0 when true then (t2.balanceF +t1.debitoccF -t1.creditoccF) else 0 end end,"
//						+ "t1.creditbalanceF=case t1.direction when -1 then (t2.balanceF +t1.debitoccF -t1.creditoccF) when 1 then 0 else case (t2.balanceF +t1.debitoccF -t1.creditoccF)<0 when true then (t2.balanceF +t1.debitoccF -t1.creditoccF) else 0 end end "

						+ "where t1.accpackageid=? and t1.submonth=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, i - 1);
				ps1.setString(3, AccPackageID);
				ps1.setInt(4, i);
				ps1.execute();
				conn.commit();
				ps1.close();
			}

			/*
			 * 插入非叶子科目记录的1到12月的记录,由于有栏目的情况下会出现非叶子节点也有凭证，
			 * 所以前面的汇总已经发生了出现了非叶子的汇总记录，所以要排除出去,另外,做了精简处理
			 * 只填入已有叶子节点的上级节点,并且会自动过滤栏目所带来的非叶子节点
			 */
			org.util.Debug.prtOut("12=" + new ASFuntion().getCurrentTime());
//			sql = "insert into t_c_accountall(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,accsign,direction,isleaf1,level1)"
//					+ "select distinct a.subjectid,?,a.subjectname,a.subjectfullname,?,c.submonth,b.dataname,b.accsign,"
//					+ "CASE substring(a.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END,a.isleaf,a.level0 "
//					+ "from t_c_accpkgsubject a,"
//					+ "(select distinct subjectid,accsign,dataname from t_c_accountall where AccPackageID=? ) b,k_month c "
//					+ "where a.AccPackageID=? and a.isleaf=0 and c.monthtype=12 "
//					+ "and b.subjectid like concat(a.SubjectID,'%') "
//					+ "and not exists ("
//					+ "select 1 from t_c_accountall d where d.AccPackageID=? and d.dataname=b.dataname "
//					+ "and d.subjectid=a.subjectid and d.submonth=c.submonth)";
//			ps1 = conn.prepareStatement(sql);
//			ps1.setString(1, AccPackageID);
//			ps1.setInt(2, year);
//			ps1.setString(3, AccPackageID);
//			ps1.setString(4, AccPackageID);
//			ps1.setString(5, AccPackageID);
//			ps1.execute();
//			conn.commit();
//			ps1.close();

			for(int iMonth = 1;iMonth <= 12; iMonth++ ){
				sql = "insert into t_c_accountall(SubjectID,AccPackageID,AccName,subjectfullname1,SubYearMonth,SubMonth,dataname,accsign,direction,isleaf1,level1)"
					+ "select distinct a.subjectid,?,a.subjectname,a.subjectfullname,?,?,b.dataname,b.accsign,"
					+ "CASE substring(a.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END,a.isleaf,a.level0 "
					+ "from t_c_accpkgsubject a,"
					+ "(select distinct subjectid,accsign,dataname from t_c_accountall where AccPackageID=? ) b "
					+ "where a.AccPackageID=? and a.isleaf=0  "
					+ "and b.subjectid like concat(a.SubjectID,'%') "
					+ "and not exists ("
					+ "select 1 from t_c_accountall d where d.AccPackageID=? and d.dataname=b.dataname "
					+ "and d.subjectid=a.subjectid and d.submonth=?)";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, year);
				ps1.setInt(3, iMonth);
				ps1.setString(4, AccPackageID);
				ps1.setString(5, AccPackageID);
				ps1.setString(6, AccPackageID);
				ps1.setInt(7, iMonth);
				ps1.execute();
				conn.commit();
				ps1.close();
			}
			
			org.util.Debug.prtOut("13=" + new ASFuntion().getCurrentTime());
			/* 计算非叶子科目的期初数等字段 */
			for (i = maxlevel; i > 1; i--) {
				// 这里是不按科目方向来汇总的代码,下级的余额已经按照方向调整过了
//				sql = "update t_c_accountall t1 join ("
//					+"select a.ParentSubjectId,b.submonth,b.dataname,"
//
//					+ "sum(b.DebitRemain) as tDebitRemain,sum(b.CreditRemain) as tCreditRemain,"
//					+ "sum(DebitOcc) as tDebitOcc,sum(CreditOcc) as tCreditOcc,sum(balance) as tBalance,"
//					+ "sum(DebitTotalOcc) as tDebitTotalOcc,sum(CreditTotalOcc) as tCreditTotalOcc,"
//					+ "sum(DebitBalance) as tDebitBalance,sum(CreditBalance) as tCreditBalance, "
//
//					+ "sum(b.DebitRemainF) as tDebitRemainF,sum(b.CreditRemainF) as tCreditRemainF,"
//					+ "sum(DebitOccF) as tDebitOccF,sum(CreditOccF) as tCreditOccF,sum(balanceF) as tBalanceF,"
//					+ "sum(DebitTotalOccF) as tDebitTotalOccF,sum(CreditTotalOccF) as tCreditTotalOccF,"
//					+ "sum(DebitBalanceF) as tDebitBalanceF,sum(CreditBalanceF) as tCreditBalanceF "
//
//					+ " FROM t_c_accpkgsubject a,t_c_accountall b where a.AccPackageID=? "
//					+ "and a.level0=? and b.AccPackageID=? and a.subjectid = b.subjectid "
//					+ "group BY ParentSubjectId,submonth,dataname"
//					+ ") as t2 "
//					+ "on t1.AccPackageID=? and t1.subjectid=t2.ParentSubjectId and t1.submonth=t2.submonth and t1.dataname=t2.dataname "
//					+ "set t1.DebitRemain=t1.DebitRemain+t2.tDebitRemain,t1.CreditRemain=t1.CreditRemain+t2.tCreditRemain,t1.DebitOcc=t1.DebitOcc+t2.tDebitOcc,"
//					+ "t1.CreditOcc=t1.CreditOcc+t2.tCreditOcc,t1.DebitTotalOcc=t1.DebitTotalOcc+t2.tDebitTotalOcc,t1.CreditTotalOcc=t1.CreditTotalOcc+t2.tCreditTotalOcc,t1.Balance=t1.Balance+t2.tBalance,"
//					+ "t1.debitBalance=t1.debitBalance+t2.tdebitBalance,t1.creditBalance=t1.creditBalance+t2.tcreditBalance,"
//
//					+ "t1.DebitRemainF=t1.DebitRemainF+t2.tDebitRemainF,t1.CreditRemainF=t1.CreditRemainF+t2.tCreditRemainF,t1.DebitOccF=t1.DebitOccF+t2.tDebitOccF,"
//					+ "t1.CreditOccF=t1.CreditOccF+t2.tCreditOccF,t1.DebitTotalOccF=t1.DebitTotalOccF+t2.tDebitTotalOccF,t1.CreditTotalOccF=t1.CreditTotalOccF+t2.tCreditTotalOccF,t1.BalanceF=t1.BalanceF+t2.tBalanceF,"
//					+ "t1.debitBalanceF=t1.debitBalanceF+t2.tdebitBalanceF,t1.creditBalanceF=t1.creditBalanceF+t2.tcreditBalanceF";
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, AccPackageID);
//				ps.setInt(2, i);
//				ps.setString(3, AccPackageID);
//				ps.setString(4, AccPackageID);
//				ps.execute();
//				conn.commit();
//				ps.close();
				
				for(int iMonth = 1;iMonth <= 12; iMonth++ ){
					sql = "update t_c_accountall t1 join ("
						+"select a.ParentSubjectId,b.submonth,b.dataname,"

						+ "sum(b.DebitRemain) as tDebitRemain,sum(b.CreditRemain) as tCreditRemain,"
						+ "sum(DebitOcc) as tDebitOcc,sum(CreditOcc) as tCreditOcc,sum(balance) as tBalance,"
						+ "sum(DebitTotalOcc) as tDebitTotalOcc,sum(CreditTotalOcc) as tCreditTotalOcc,"
						+ "sum(DebitBalance) as tDebitBalance,sum(CreditBalance) as tCreditBalance, "

						+ "sum(b.DebitRemainF) as tDebitRemainF,sum(b.CreditRemainF) as tCreditRemainF,"
						+ "sum(DebitOccF) as tDebitOccF,sum(CreditOccF) as tCreditOccF,sum(balanceF) as tBalanceF,"
						+ "sum(DebitTotalOccF) as tDebitTotalOccF,sum(CreditTotalOccF) as tCreditTotalOccF,"
						+ "sum(DebitBalanceF) as tDebitBalanceF,sum(CreditBalanceF) as tCreditBalanceF "

						+ " FROM t_c_accpkgsubject a,t_c_accountall b where a.AccPackageID=? "
						+ "and a.level0=? and submonth = ? and b.AccPackageID=? and a.subjectid = b.subjectid "
						+ "group BY ParentSubjectId,dataname"
						+ ") as t2 "
						+ "on t1.AccPackageID=? and t1.submonth=? and t1.subjectid=t2.ParentSubjectId  and t1.dataname=t2.dataname "
						+ "set t1.DebitRemain=t1.DebitRemain+t2.tDebitRemain,t1.CreditRemain=t1.CreditRemain+t2.tCreditRemain,t1.DebitOcc=t1.DebitOcc+t2.tDebitOcc,"
						+ "t1.CreditOcc=t1.CreditOcc+t2.tCreditOcc,t1.DebitTotalOcc=t1.DebitTotalOcc+t2.tDebitTotalOcc,t1.CreditTotalOcc=t1.CreditTotalOcc+t2.tCreditTotalOcc,t1.Balance=t1.Balance+t2.tBalance,"
						+ "t1.debitBalance=t1.debitBalance+t2.tdebitBalance,t1.creditBalance=t1.creditBalance+t2.tcreditBalance,"

						+ "t1.DebitRemainF=t1.DebitRemainF+t2.tDebitRemainF,t1.CreditRemainF=t1.CreditRemainF+t2.tCreditRemainF,t1.DebitOccF=t1.DebitOccF+t2.tDebitOccF,"
						+ "t1.CreditOccF=t1.CreditOccF+t2.tCreditOccF,t1.DebitTotalOccF=t1.DebitTotalOccF+t2.tDebitTotalOccF,t1.CreditTotalOccF=t1.CreditTotalOccF+t2.tCreditTotalOccF,t1.BalanceF=t1.BalanceF+t2.tBalanceF,"
						+ "t1.debitBalanceF=t1.debitBalanceF+t2.tdebitBalanceF,t1.creditBalanceF=t1.creditBalanceF+t2.tcreditBalanceF";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.setInt(2, i);
					ps.setInt(3, iMonth);
					ps.setString(4, AccPackageID);
					ps.setString(5, AccPackageID);
					ps.setInt(6, iMonth);
					ps.execute();
					conn.commit();
					ps.close();
				}
				
			}

			org.util.Debug.prtOut("14=" + new ASFuntion().getCurrentTime());
			
			/**
			 * 保存199凭证到t_c_subjectentrybegin表 （年中建帐）
			 */
			sql = "insert into t_c_subjectentrybegin (" +
				"AccPackageID, VoucherID, OldVoucherID, TypeID, VchDate, Serail, Summary, " +
				"SubjectID, Dirction, OccurValue, CurrRate, CurrValue, Currency, Quantity, " +
				"UnitPrice, UnitName, BankID, Property, subjectname1, SubjectFullName1, tokenid, standname " +
				")  " +
				"select " +
				"AccPackageID, VoucherID, OldVoucherID, TypeID, VchDate, Serail, Summary, " +
				"SubjectID, Dirction, OccurValue, CurrRate, CurrValue, Currency, Quantity, " +
				"UnitPrice, UnitName, BankID, Property, subjectname1, SubjectFullName1, tokenid, standname " +
				"from t_c_subjectentry where accpackageid=? and property='199' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			// 删除掉年中建帐创建的补录明细（property='199'）
			sql = "delete from t_c_subjectentry where accpackageid=? and property='199'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			update(AccPackageID,"t_c_accountall","DataName","$","＄");
			update(AccPackageID,"t_c_subjectentry","Currency","$","＄");
			update(AccPackageID,"t_c_subjectentrybegin","Currency","$","＄");
			
			updateNewSubjectFullName(AccPackageID);
			
		} catch (Exception e) {
			e.printStackTrace();
			org.util.Debug.prtOut("错误时的SQL=" + sql);
			throw new Exception("执行失败" + e.getMessage(), e);
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (ps1 != null)
				ps1.close();
		}
		return strResult;
	}

	/**
	 * 处理完成后汇总项目余额
	 * 
	 * @param AccPackageID
	 *            String 帐套编号
	 * @param year
	 *            int 帐套的年份
	 * @return int
	 * @throws Exception
	 */
	public String createXmhz(String AccPackageID, int year) throws Exception {
		String strResult = "";
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}

		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		org.util.Debug.prtOut("Assitem:B0="
				+ new ASFuntion().getCurrentTime());
		int subMonth = 12;
		String sql = null, strid = "";
		int i = 0;
		try {
			conn.setAutoCommit(false);

			String tableName = ifValue("t_c_assitemopenbegin","t_c_assitembegin");
			
			//判断核算有没有期初、发生，没有就把核算体系去掉
			sql = "select 1 from t_c_assitementry where AccPackageID = ? limit 1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if(!rs.next()){
				//没有发生，判断有没有期初
				DbUtil.close(rs);
				DbUtil.close(ps);
				sql = "select 1 from "+tableName+" where AccPackageID = ? limit 1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				rs = ps.executeQuery();
				if(!rs.next()){
					//也没有期初，去掉核算体系
					DbUtil.close(rs);
					DbUtil.close(ps);
					sql = "delete from t_c_assitem where AccPackageID = ? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.execute();
				}
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			//判断核算有没有期初、发生，没有就把核算体系去掉
			
			sql = "update t_c_assitem set AssItemName = replace(AssItemName,'`','') where AccPackageID = ? and AssItemName like '%`%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update t_c_assitem set AssItemName = replace(AssItemName,'''','') where AccPackageID = ? and AssItemName like '%''%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 修改核算名中有“&”为“＆”
			 */
			sql = "update t_c_assitem set AssItemName = replace(AssItemName,'&','＆') where AccPackageID = ? and AssItemName like '%&%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			//==============核算体系少上级核算==============
			sql = "insert into t_c_assitem(accpackageid,accid,assitemid,assitemname,parentassitemid) " +
			"	select ?,'0000',a.parentassitemid,'未知核算','未知' " +
			"	from ( " +
			"		select distinct parentassitemid from t_c_assitem where accpackageid =? and parentassitemid<>'' " +
			"	) a left join t_c_assitem b  " +
			"	on a.parentassitemid = b.assitemid " +
			"	where b.assitemid is null";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			ps.close();
			
			sql = "update t_c_assitem  a join ( " +
			"		select a.* " +
			"		from ( " +
			"			select a.assitemid,a.assitemname,b.assitemid as parentid,b.assitemname as parentname " +
			"			from t_c_assitem a,t_c_assitem b " +
			"			where a.accpackageid=? and b.accpackageid=? " +
			"			and a.parentassitemid='未知' " +
			"			and a.assitemid<>b.assitemid " +
			"			and a.assitemid like concat(b.assitemid,'%') " +
			"		)a,( " +
			"			select a.assitemid,a.assitemname,max(length(b.assitemid)) as maxlength " +
			"			from t_c_assitem a,t_c_assitem b " +
			"			where a.accpackageid=? and b.accpackageid=? " +
			"			and a.parentassitemid='未知' " +
			"			and a.assitemid<>b.assitemid " +
			"			and a.assitemid like concat(b.assitemid,'%') " +
			"			group by a.assitemid,a.assitemname " +
			"		)b " +
			"		where a.assitemid=b.assitemid " +
			"		and length(a.parentid)=b.maxlength " +
			"		group by a.assitemid,a.assitemname " +
			"	) b " +
			"	on a.assitemid=b.assitemid " +
			"	set a.parentassitemid=b.parentid " +
			"	where a.accpackageid=?  " +
			"	and a.parentassitemid='未知'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.setString(4, AccPackageID);
			ps.setString(5, AccPackageID);
			ps.execute();
			ps.close();
			
			//==============核算体系少上级核算==============
			
			/**
			 * 为了适应EXCEL采集辅助核算帐，特意做的修改，修正serail的字段；
			 */
			sql="select count(1) from t_c_accpackage where  softversion like '%Excel采集%' and accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs=ps.executeQuery();
			if (rs.next()){
				if (rs.getInt(1)>0){
					//是EXCEL采集；
					rs.close();
					ps.close();
					
					sql="update t_c_assitementry a  join t_c_subjectentry b \n"
						+"on a.accpackageid=? and b.accpackageid=? \n" 
						+"and a.voucherid=b.voucherid and a.subjectid=b.subjectid \n"
						+"and a.dirction*a.assitemsum=b.dirction * b.occurvalue \n"
						+"set a.serail=b.serail \n"
						+"where a.accpackageid=?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.setString(2, AccPackageID);
					ps.setString(3, AccPackageID);
					ps.execute();
					conn.commit();
					ps.close();
				}else{
					rs.close();
					ps.close();
				}
			}else{
				rs.close();
				ps.close();
			}
			
			
			
			/**
			 * 修改核算名中有“\”为“-”
			 */
			sql = "update t_c_assitem set AssItemName = replace(AssItemName,'\\\\','-') where AccPackageID = ? and AssItemName like '%\\\\\\\\%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("Assitem:B0.1="
					+ new ASFuntion().getCurrentTime());
			
			
			
			if("t_c_assitembegin".equals(tableName)){
				
//				修改核算期初数表：有本位币没有外币期初，但有外币发生。修正外币对应本位币的期初为本位币期初
				sql = " insert into t_c_assitembegin (AccPackageID,AccID,AssItemID,DataType,DebitRemain,CreditRemain,accsign) " +
					"select a.AccPackageID,b.AccID,b.AssItemID,b.Currency,0,0,1" +
					" from (" +
					/*
					" 	select a.* " +
					" 	from t_c_assitembegin a left join t_c_assitembegin b " +
					" 	on  " +
					" 	a.AccID = b.AccID " +
					"	and a.AssItemID = b.AssItemID " +
				
					" 	where a.AccPackageID = ? " +
					" 	and a.DataType = '0' " +
					" 	and b.AccPackageID = ? and b.DataType <> '0' " +
					*/
					"	select ? as AccPackageID,AccID,AssItemID \n" +
					"	from t_c_assitembegin \n" +
					"	where AccPackageID = ? \n" +
					"	group by AccID,AssItemID having count(*)=1  \n" +
					
					" ) a ,(" +
					"	select distinct subjectid as AccID,AssItemID,Currency" +
					"	from t_c_assitementry" +
					" 	where AccPackageID = ?  and Currency <>'' " +
					") b " +
					"where a.AccPackageID = ? and a.AccID = b.AccID and a.AssItemID = b.AssItemID" ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
				
				org.util.Debug.prtOut("Assitem:B0.2="+ new ASFuntion().getCurrentTime());
//				修改核算期初数表：有本位币没有数量期初，但有数量发生。修正数量对应本位币的期初为本位币期初
				sql = " insert into t_c_assitembegin (AccPackageID,AccID,AssItemID,DataType,DebitRemain,CreditRemain,accsign) " +
					"select a.AccPackageID,b.AccID,b.AssItemID,b.UnitName,0,0,2 "+
					" from (" +
					/*
					" 	select a.* " +
					" 	from t_c_assitembegin a left join t_c_assitembegin b " +
					" 	on " +
					" 	a.AccID = b.AccID " +
					"	and a.AssItemID = b.AssItemID " +
		
					" 	where a.AccPackageID = ?   " +
					" 	and a.DataType = '0' " +
					" 	and b.AccPackageID = ? and b.DataType <> '0'" +
					*/
					
					"	select ? as AccPackageID,AccID,AssItemID \n" +
					"	from t_c_assitembegin \n" +
					"	where AccPackageID = ? \n" +
					"	group by AccID,AssItemID having count(*)=1  \n" +
					
					" ) a ,(" +
					"	select distinct subjectid as AccID,AssItemID,UnitName" +
					"	from t_c_assitementry" +
					" 	where AccPackageID = ?  and UnitName <>'' " +
					") b " +
					"where a.AccPackageID = ? and a.AccID = b.AccID and a.AssItemID = b.AssItemID" ;
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);

				ps.execute();
				conn.commit();
				ps.close();
				
				org.util.Debug.prtOut("Assitem:B0.3="+ new ASFuntion().getCurrentTime());
				sql = "update t_c_assitembegin a join t_c_assitembegin b " +
					"on  a.AccID=b.AccID and a.AssItemID=b.AssItemID " +
					"set a.DebitRemainF=b.DebitRemain,a.CreditRemainF=b.CreditRemain " +
					" where b.accpackageid=? and b.datatype='0' " +
					" and a.accpackageid=? and  a.datatype <> '0' " ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
				
				org.util.Debug.prtOut("Assitem:B0.4="
						+ new ASFuntion().getCurrentTime());
				sql = "update t_c_assitembegin set DebitRemainF=0 where accpackageid=? and DebitRemainF is null" ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();	
				
				org.util.Debug.prtOut("Assitem:B0.5="
						+ new ASFuntion().getCurrentTime());
				sql = "update t_c_assitembegin set CreditRemainF=0 where accpackageid=? and CreditRemainF is null" ;
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();	
				
			}
			
			/**
			 * =======================================================================
			 *  采集提供的辅助核算期初数有可能是重复的
			 *  装载来汇总处理一下
			 * =======================================================================
			  */
			sql= "update "+tableName+" set datatype='0' where accpackageid=? and (datatype is null or datatype=\"\")";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();	

			sql="insert into "+tableName+" \n"
				+"(accpackageid,accid,assitemid,datatype,debitremain,creditremain,accsign,debitremainf,creditremainf) \n"
				+"select ? , accid,assitemid,datatype,sum(debitremain),sum(creditremain), \n"
				+"accsign,sum(debitremainf),sum(creditremainf) \n"
				+"from "+tableName+" \n"
				+"where accpackageid=? \n"
				+"group by accid,assitemid,datatype ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,  "-1"+ year );
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit(); 
			ps.close();	

			sql="delete from "+tableName+" where accpackageid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();	
			
			sql="update "+tableName+" set accpackageid=? where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2,  "-1"+ year );
			ps.execute();
			conn.commit();
			ps.close();

			
			/**
			 * =======================================================================
			 * 恢复t_c_assitem的结构，包括assitemtotalname,isleaf,level0,另外还包括accid;
			 * 生成的时候做了优化处理，只会生成有发生或者期初数的
			 * =======================================================================
			 */
			org.util.Debug.prtOut("Assitem:B1="
					+ new ASFuntion().getCurrentTime());
			// 重新生成非0的核算体系，ACCID无条件填0000，同时还可以避免与原来装载时有的ACCID＝0000的冲突；
			sql = "insert into t_c_assitem (AccPackageID,AccID,AssItemID,AssItemName,AssTotalName,ParentAssItemId, DebitRemain,CreditRemain,IsLeaf,level0,UomUnit,Curr,Property) "
					+ "select distinct ?,'0000',a.AssItemID,a.AssItemName,a.AssTotalName,a.ParentAssItemId,0,0,a.IsLeaf,a.level0,a.UomUnit,a.Curr,a.Property "
					+ "from t_c_assitem a "
					+ "where a.accpackageid=? and accid <>'0000' and AssItemID not in "
					+ "(select AssItemID from t_c_assitem b where b.accpackageid=? and b.accid ='0000')";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.setString(3, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("Assitem:B2="
					+ new ASFuntion().getCurrentTime());
			// 删除掉所有的非0000的，避免干扰
			sql = "delete from t_c_assitem  where accpackageid=? and accid <>'0000';";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("Assitem:B3="
					+ new ASFuntion().getCurrentTime());
			// 置level初始值，避免遗漏
			sql = "update t_c_assitem set level0=-1,assitemname=trim(assitemname),isleaf=1 where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();

			// 设置第一级节点；
			sql = "update t_c_assitem "
					+ "set level0=1,asstotalname=assitemname "
					+ "where accpackageid=? and parentassitemid=''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();

			// 按照最多嵌套10层的深度来计算每级的level、asstotalname
			int maxlevel = 10;
			for (i = 2; i < 10; i++) {
				org.util.Debug.prtOut("Assitem:B4="
						+ new ASFuntion().getCurrentTime());
				sql = "update t_c_assitem a join t_c_assitem b "
						+ "on a.accpackageid=? "
						+ "and b.accpackageid=? and b.level0=? "
						+ "and a.parentassitemid=b.assitemid "
						+ "set a.level0=?,a.asstotalname=concat(b.asstotalname,'/',a.assitemname)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setString(2, AccPackageID);
				ps.setInt(3, i - 1);
				ps.setInt(4, i);
				ps.execute();
				conn.commit();
				ps.close();
			}

			org.util.Debug.prtOut("Assitem:B5="
					+ new ASFuntion().getCurrentTime());
			// 检查看看是不是都设置完了
			sql = "select assitemid,assitemname,parentassitemid from t_c_assitem "
					+ "where accpackageid=? and level0=-1 limit 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if (rs.next()) {
				strResult = "核算体系中[编号=" + rs.getString(1) + "][名称＝"
						+ rs.getString(2) + "]的核算项目，它的上级核算项目[上级编号="
						+ rs.getString(3) + "]不存在，装载继续，但是核算余额表相关记录将会出现错误！";
			}
			rs.close();
			ps.close();

			org.util.Debug.prtOut("Assitem:B6="
					+ new ASFuntion().getCurrentTime());
			// 检查看看是不是都设置完了
			sql = "select group_concat(distinct parentassitemid SEPARATOR \"','\") from t_c_assitem where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if (rs.next()) {
				strid = rs.getString(1);
			}
			rs.close();
			ps.close();

			if (strid != null && strid.length() > 0) {
				// 恢复isleaf字段
				sql = "update  t_c_assitem set isleaf=0 where accpackageid=? and assitemid in ('"
						+ strid + "')";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();
			}

			org.util.Debug.prtOut("Assitem:B7="
					+ new ASFuntion().getCurrentTime());
			sql = "update t_c_assitem " + "set isleaf=1 "
					+ "where accpackageid=? and isleaf is null";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
 
			
			/**
			 * 如果核算全路径名称有相同的，修改核算名称和全路径。
			 * 修改：核算名称(核算编号)
			 * 
			 */
			
			sql = "select asstotalname,min(assitemid) as assitemid \n"
				+"from t_c_assitem \n"
				+"where accpackageid = ? \n"
				+"group by asstotalname \n"
				+"having count(*)>1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			conn.commit();
			if(rs.next()){ //判断是否有全路径重复
				//加索引
				try {
					sql = "alter table `t_c_assitem` add index `asstotalname` (`asstotalname`)";
					ps = conn.prepareStatement(sql);
					ps.execute();
					conn.commit();
				} catch (Exception e) {}
				try {
					String t = "tt_" + DELUnid.getNumUnid();
					sql = "create table "+t+" as	" +
					"	select asstotalname,group_concat(assitemid) as assitemid  " +
					"	from t_c_assitem  " +
					"	where accpackageid = ? " +
					"	group by asstotalname  " +
					"	having count(*)>1 ";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.execute();
					conn.commit();
					
					sql = "alter table "+t+" add index `asstotalname` (`asstotalname`)";
					ps = conn.prepareStatement(sql);
					ps.execute();
					conn.commit();
					
					//有重复,修改：核算名称 = 核算名称(核算编号)
					sql = "update t_c_assitem a,"+t+" b  " +
					"	set a.assitemname = concat(a.assitemname,'(',a.assitemid,')')" +
					"	where accpackageid = ? and a.asstotalname = b.asstotalname";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.execute();
					conn.commit();
					org.util.Debug.prtOut("重算Assitem:B3=" + new ASFuntion().getCurrentTime());
					
					sql = "drop table IF EXISTS " + t ;
					ps = conn.prepareStatement(sql);
					ps.execute();
					conn.commit();
				} catch (Exception e) {
					System.out.println("出错SQL:"+sql);
				}
				
				//重算核算体系
				sql = "update t_c_assitem "
						+ "set asstotalname=assitemname "
						+ "where accpackageid=? and level0=1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();

				for (i = 2; i < 10; i++) {
					org.util.Debug.prtOut("重算Assitem:B4=" + new ASFuntion().getCurrentTime());
					sql = "update t_c_assitem a join t_c_assitem b "
							+ "on a.accpackageid=? "
							+ "and b.accpackageid=? and b.level0=? "
							+ "and a.parentassitemid=b.assitemid "
							+ "set a.asstotalname=concat(b.asstotalname,'/',a.assitemname)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, AccPackageID);
					ps.setString(2, AccPackageID);
					ps.setInt(3, i - 1);
					ps.execute();
					conn.commit();
					ps.close();
				}

			}
//			boolean bOK=false;
//			int iCount=1;
//			while (bOK==false && iCount<10){
//				
//				bOK=true;
//				
//				//预防死循环
//				iCount++;
//				
//				sql = "select asstotalname,min(assitemid) as assitemid \n"
//					+"from t_c_assitem \n"
//					+"where accpackageid = ? \n"
//					+"group by asstotalname \n"
//					+"having count(*)>1";
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, AccPackageID);
//				rs = ps.executeQuery();
//				conn.commit();
//				
//				System.out.println("sql="+sql);
//				
//				while(rs.next()){
//					String strasstotalname = rs.getString("asstotalname");
//					String strassitemid = rs.getString("assitemid");
//					
//					//有对照，只能再看看以后有没有问题；
//					bOK=false;
//					
//					System.out.println("strasstotalname="+strasstotalname+"|assitemid="+strassitemid);
//
//					sql = "update \n"
//						+"t_c_assitem a join \n"
//						+"( \n"
//						+"	select asstotalname,assitemid,assitemname, \n"
//						+"	concat(assitemname,'(',assitemid,')') as newassitemname, \n"
//						+"	if (level0=1,concat(assitemname,'(',assitemid,')'), \n"
//						+"		replace(asstotalname,concat('/',assitemname),concat('/',assitemname,'(',assitemid,')')) \n"
//						+"	) as newasstotalname \n"
//						+"	from t_c_assitem b \n"
//						+"	where b.accpackageid = ? and b.asstotalname = ? \n"
//						+"	and b.assitemid <>'"+strassitemid+"' \n"
//						+")b \n"
//						+"on a.accpackageid = ? and a.assitemid <>'"+strassitemid+"' \n"
//						+"and (a.asstotalname = ? or a.asstotalname like concat(b.asstotalname,'/%')) \n"
//						+"and (a.assitemid like concat(b.assitemid,'%') or a.assitemid =b.assitemid) \n"
//						+"set a.assitemname=if (a.assitemid=b.assitemid,b.newassitemname,a.assitemname), \n"
//						+"a.asstotalname=replace(a.asstotalname,b.asstotalname,b.newasstotalname) " ;
//					
//					System.out.println("sql="+sql);
//					
//					ps1 = conn.prepareStatement(sql);
//					ps1.setString(1, AccPackageID);
//					ps1.setString(2, strasstotalname);
//					ps1.setString(3, AccPackageID);
//					ps1.setString(4, strasstotalname);
//					ps1.execute();
//					conn.commit();
//					ps1.close();
//				}
//				rs.close();
//				ps.close();
//			}
			
			/*
			org.util.Debug.prtOut("Assitem:B8="
					+ new ASFuntion().getCurrentTime());
			
			 * //检查是否有设置到非底层叶子核算项目的错误核算分录，有的话就报错退出，因为后面的汇总无法进行。 sql = "select
			 * vchdate,typeid,serail,assitemsum,summary from t_c_assitementry
			 * a,t_c_assitem b " +"where a.accpackageid=? and b.accpackageid=? "
			 * +"and b.isleaf=0 and a.assitemid=b.assitemid limit 1"; ps1 =
			 * conn.prepareStatement(sql); ps1.setString(1, AccPackageID);
			 * ps1.setString(2, AccPackageID); rs=ps1.executeQuery(); if
			 * (rs.next()){
			 * strResult="核算分录有错误，存在非底层核算项目的分录，类似得凭证为：[日期："+rs.getString(1)+"][凭证字："
			 * +rs.getString(2)+"][号："+rs.getString(3)+"][金额："+rs.getString(4)+"][摘要："+rs.getString(5)+"]，系统将忽略继续装载"; }
			 * rs.close(); ps1.close();
			 */

			//根据核算明细重算所有ITEM核算的科目对照关系，包括所有节点；
			org.util.Debug.prtOut("Assitem:B9="
					+ new ASFuntion().getCurrentTime() + " AccPackageID:="
					+ AccPackageID);
			//先算最下级的
			sql="insert into t_c_assitem (AccPackageID,AccID,AssItemID,AssItemName,AssTotalName,ParentAssItemId, \n" 
				+"DebitRemain,CreditRemain,IsLeaf,level0,UomUnit,Curr,Property)  \n"
				+"select distinct ?,b.subjectid,a.AssItemID,a.AssItemName,a.AssTotalName,a.ParentAssItemId,0,0, \n"
				+"a.IsLeaf,a.level0,a.UomUnit,a.Curr,a.Property	 \n"
				+"from t_c_assitem a,  \n"
				+"( \n"
				//+"	select a.assitemid,a.subjectid \n"
				//+"	from(  \n"
				+"	select distinct assitemid,subjectid \n"   
				+"	from t_c_assitementry where accpackageid=? \n"
				//+"	)a left join t_c_assitem b  \n"
				//+"	on b.accpackageid=? and a.assitemid=b.assitemid \n"
				+"	union  \n"
				+"	select a.assitemid,a.accid from "+tableName+" a, t_c_assitem b \n" 
				+"	where a.accpackageid=? and (abs(a.creditremain)>0.001 or abs(a.debitremain)>0.001) \n" 
				+"	and b.accpackageid=? and b.isleaf=1  \n"
				+"	and a.assitemid=b.assitemid  \n"
				+") b  \n"
				+"where a.accpackageid=? and a.assitemid = b.assitemid";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.setString(5, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			
			
			//取出实际的级次
			sql = "select max(level0) as maxlevel from t_c_assitem where accpackageid=?  and accid <>'0000'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			maxlevel=-1;
			if (rs.next()) {
				maxlevel = rs.getInt(1);
			}
			rs.close();
			ps.close();
			
			for (i=maxlevel;i>1 ;i--){
				sql="insert into t_c_assitem (AccPackageID,AccID,AssItemID,AssItemName,AssTotalName,ParentAssItemId, \n" 
					+"DebitRemain,CreditRemain,IsLeaf,level0,UomUnit,Curr,Property)  \n"
					+"select distinct ?,b.accid,a.AssItemID,a.AssItemName,a.AssTotalName,a.ParentAssItemId,0,0, \n"
					+"a.IsLeaf,a.level0,a.UomUnit,a.Curr,a.Property	 \n"
					+"from t_c_assitem a, t_c_assitem b \n"
					+"where  a.accpackageid=? \n"
					+"and a.accid='0000' and b.accpackageid=? \n"
					+"and b.level0=? and b.accid<>'0000' \n"
					+"and a.assitemid = b.ParentAssItemId \n"
					+"and not exists (\n"
					+"	select 1 from t_c_assitem c \n"
					+"	where c.accpackageid=?\n" 
					+"	and c.assitemid=a.assitemid\n"
					+"	and b.accid=c.accid)";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setString(2, AccPackageID);
				ps1.setString(3, AccPackageID);
				ps1.setInt(4, i);
				ps1.setString(5, AccPackageID);
				ps1.execute();
				conn.commit();
				ps1.close();
			}
			
			
			/*
			 * 
			 * 这里在装载核算体系很大的账的时候很慢,所以优化成上面的代码
			 * 
			// 根据核算明细重算所有ITEM核算的科目对照关系，包括所有节点；
			 sql = "insert into t_c_assitem (AccPackageID,AccID,AssItemID,AssItemName,AssTotalName,ParentAssItemId, DebitRemain,CreditRemain,IsLeaf,level0,UomUnit,Curr,Property) \n"
			 +"select distinct ?,b.subjectid,a.AssItemID,a.AssItemName,a.AssTotalName,a.ParentAssItemId,0,0,a.IsLeaf,a.level0,a.UomUnit,a.Curr,a.Property	\n"
			 +"from t_c_assitem a, \n"
			 
//			 +"(select AssItemID,AssItemName,AssTotalName,ParentAssItemId,IsLeaf,level0,UomUnit,Curr,Property \n"
//			 +"from t_c_assitem where accpackageid=? and accid='0000') a, \n"
			 +"(select distinct a.assitemid,a.subjectid,b.asstotalname from \n"
			 +"t_c_assitementry a, t_c_assitem b where a.accpackageid=? and \n"
			 +"b.accpackageid=? and a.assitemid=b.assitemid \n"
			 +"union \n"
			 +"select a.assitemid,a.accid,b.asstotalname from "+tableName+" a, t_c_assitem b \n"
			 +"where a.accpackageid=? and (abs(a.creditremain)>0.001 or abs(a.debitremain)>0.001) \n"
			 +"and b.accpackageid=? and b.isleaf=1 \n"
			 +"and a.assitemid=b.assitemid ) b \n"
			 +"where a.asstotalname=b.asstotalname or (b.asstotalname like concat(a.asstotalname,'/%') and isleaf=0)";
			 
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.setString(5, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			
			*/
			
			
			
			
	

			// 清理临时的节点
			sql = "delete from t_c_assitem  where accpackageid=? and accid ='0000';";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/**
			 * =======================================================================
			 * 这里是开始汇总的完整SQL：汇总核算本位币余额表
			 * =======================================================================
			 */
			org.util.Debug.prtOut("Assitem:A1="
					+ new ASFuntion().getCurrentTime());
			// 插入前先删除原有的记录
			sql = "delete from t_c_assitementryacc where AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitem a,(" +
			"	select * from t_c_assitem a " +
			"	where a.accpackageid=? and isleaf=0 " +
			"	and not exists (select distinct parentassitemid from t_c_assitem b where accpackageid=? and a.assitemid =b.parentassitemid )" +
			") b " +
			" set a.isleaf=1 " +
			" where a.accpackageid=? " +
			" and b.accpackageid=? " +
			" and a.accid =b.accid and a.assitemid =b.assitemid" ;
					
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			
				
			// 删除掉采集提供的年中建账程序中非底层凭证，否则会造成重复汇总发生额的现象
			org.util.Debug.prtOut("Assitem:A1="
					+ new ASFuntion().getCurrentTime());
			sql = "delete from t_c_assitementry "
					+ "where AccPackageID=? and property='199' and exists "
					+ "(select 1 from t_c_assitem b where b.AccPackageID=? and b.isleaf=0 and  t_c_assitementry.assitemid=b.assitemid)";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			// 设置本位币的期初数，如果没有设置的话
			sql = "update "
					+ tableName
					+ " "
					+ "set datatype='0' where accpackageid=? and (datatype is null or datatype ='')";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("Assitem:A2="
					+ new ASFuntion().getCurrentTime());
			/* 优先插入叶子核算项目发生额汇总值记录 */
			// /汇总的时候，过滤掉乐所有作废凭证；正常凭证的PROPERTY在核算明细中是1
			/**
			 * 优化于20070917
			 * 
			 * sql = "insert into
			 * t_c_assitementryacc(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,debitocc,creditocc,isleaf1,level1) " +
			 * "select
			 * ?,tt1.accid,tt1.AssItemID,c.AssItemName,c.AssTotalName,?,submonth,debitocc,creditocc,c.isleaf,c.level0
			 * from " + "(select
			 * t1.debitocc,t2.creditocc,t1.submonth,t1.AssItemID,t1.subjectid as
			 * accid " + "from " + "(select sum(AssItemSum) as
			 * debitocc,AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid from t_c_assitementry " + "where
			 * t_c_assitementry.AccPackageID=? and t_c_assitementry.Dirction=1
			 * and t_c_assitementry.Property like '1%' " + "group by
			 * t_c_assitementry.AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid) t1 " + "left
			 * outer join " + "(select sum(AssItemSum) as
			 * creditocc,AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid from t_c_assitementry " + "where
			 * t_c_assitementry.AccPackageID=? and t_c_assitementry.Dirction=-1
			 * and t_c_assitementry.Property like '1%' " + "group by
			 * t_c_assitementry.AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid) t2 " + "on
			 * t1.AssItemID=t2.AssItemID and t1.submonth=t2.submonth and
			 * t1.subjectid=t2.subjectid " + "union " + "select
			 * t1.debitocc,t2.creditocc,t2.submonth,t2.AssItemID,t2.subjectid as
			 * accid " + "from " + "(select sum(AssItemSum) as
			 * debitocc,AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid from t_c_assitementry " + "where
			 * t_c_assitementry.AccPackageID=? and t_c_assitementry.Dirction=1
			 * and t_c_assitementry.Property like '1%' " + "group by
			 * t_c_assitementry.AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid) t1 " + "right
			 * outer join " + "(select sum(AssItemSum) as
			 * creditocc,AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid from t_c_assitementry " + "where
			 * t_c_assitementry.AccPackageID=? and t_c_assitementry.Dirction=-1
			 * and t_c_assitementry.Property like '1%' " + "group by
			 * t_c_assitementry.AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid) t2 " + "on
			 * t1.AssItemID=t2.AssItemID and t1.submonth=t2.submonth and
			 * t1.subjectid=t2.subjectid) tt1,t_c_assitem c " + "where
			 * c.AccPackageID=? and c.AssItemID=tt1.AssItemID AND
			 * tt1.accid=c.accid";
			 */
			sql = "insert into t_c_assitementryacc(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,debitocc,creditocc,isleaf1,level1) \n"
					+ "select ?,tt1.accid,tt1.AssItemID,c.AssItemName,c.AssTotalName,?,submonth,debitocc,creditocc,c.isleaf,c.level0 from \n"
					+ "( \n"
					+ "	select sum(case when Dirction=1 then AssItemSum else 0 end) as debitocc, \n"
					+ "	sum(case when Dirction=-1 then AssItemSum else 0 end) as creditocc, \n"
					+ "	AssItemID,substring(t_c_assitementry.vchdate,6,2) as submonth,subjectid AS accid \n"
					+ "	from t_c_assitementry  \n"
					+ "	where AccPackageID=? and Property like '1%' \n"
					+ "	group by AssItemID, substring(vchdate,6,2),subjectid \n"
					+ ") tt1,t_c_assitem c  \n"
					+ "where c.AccPackageID=? and c.AssItemID=tt1.AssItemID AND tt1.accid=c.accid";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			String str = " and b.isleaf=1 ";
			sql = "select 1 from t_c_accpackage where AccPackageID=? and SoftVersion like '%金贸%' limit 1";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			if (ps1.executeQuery().next()) {
				str = "";
			}

			org.util.Debug.prtOut("Assitem:A3="
					+ new ASFuntion().getCurrentTime());
			/* 补充生成未出现的叶子核算项目的月份记录,但是只会生成有发生额或者期初数的1到12月记录 */
			sql = "insert into t_c_assitementryacc(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,isleaf1,level1) "
					+ "select ?,AccID,AssItemID,AssItemName,AssTotalName1,?,b.submonth,isleaf1,level1 "
					+ "from (select assitemid, accid,AssItemName,AssTotalName1,isleaf1,level1 from t_c_assitementryacc "
					+ "where AccPackageID=? group by assitemid, accid,AssItemName "
					+ "union select a.assitemid,a.accid,b.AssItemName,b.AssTotalName,b.isleaf,b.level0 from "
					+ tableName
					+ " a,t_c_assitem b "
					+ "where a.AccPackageID=? and (abs(a.creditremain)>0.001 or abs(a.debitremain)>0.001) and a.datatype='0' "
					+ "and b.AccPackageID=? "
					+ str
					+ " and a.assitemid=b.assitemid "
					+ "and a.accid=b.accid ) as a,k_month b "
					+ "where  b.monthtype=? "
					+ "and not exists ( "
					+ "select 1 from t_c_assitementryacc c where c.AccPackageID=? "
					+ "and c.AssItemID = a.AssItemID and c.accid=a.accid and c.submonth=b.submonth)";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.setString(5, AccPackageID);
			ps1.setInt(6, subMonth);
			ps1.setString(7, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 更新期初数 */
			org.util.Debug.prtOut("Assitem:A4="
					+ new ASFuntion().getCurrentTime());
			sql = "update t_c_assitementryacc a join "
					+ tableName
					+ " b "
					+ "on   a.AssItemID = b.AssItemID and a.accid=b.accid "
					+ "set a.debitremain=b.debitremain,a.creditremain=b.creditremain "
					+ "where 1=1 and  a.AccPackageID=? and b.AccPackageID=? and a.submonth=1 and b.datatype='0'";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryacc set debitremain=0 where debitremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryacc set creditremain=0 where creditremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryacc set DebitOcc=0.00 where DebitOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryacc set CreditOcc=0.00 where CreditOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/*
			 * 更新1月份余额 sql = "update t_c_assitementryacc set
			 * DebitTotalOcc=DebitOcc,CreditTotalOcc=CreditOcc,balance=debitremain+creditremain+debitocc-creditocc " +
			 * "where SubMonth='1' and AccPackageID=?";
			 */
			org.util.Debug.prtOut("Assitem:A5="
					+ new ASFuntion().getCurrentTime());
			sql = "update t_c_assitementryacc t1 join t_c_accpkgsubject t2 "
					+ "on t1.AccPackageID=? and t1.SubMonth='1' "
					+ "and t2.AccPackageID=? and t1.accid=t2.subjectid "
					+ "set t1.DebitTotalOcc=t1.DebitOcc,t1.CreditTotalOcc=t1.CreditOcc,t1.balance=t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc ,"
					+ "t1.direction=CASE substring(t2.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END ,"
					
//					+ "t1.debitbalance=case substring(t2.property,2,1) when '1' then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc when '2' then 0 else case (t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc)>=0 when true then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc else 0 end end,"
//					+ "t1.creditbalance=case substring(t2.property,2,1) when '2' then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc when '1' then 0 else case (t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc)<0 when true then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc else 0 end end ";
					
//					+ "t1.debitbalance=t1.debitremain+t1.debitocc,"
//					+ "t1.creditbalance=t1.creditremain-t1.creditocc ";
					+ "t1.debitbalance=if(t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc>0,t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc,0),"
					+ "t1.creditbalance=if(t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc<0,t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc,0) ";
			
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 计算叶子项目的期初数与年度总发生数 */
			org.util.Debug.prtOut("Assitem:A6="
					+ new ASFuntion().getCurrentTime());
			for (i = 2; i <= subMonth; i++) {
				sql = "update t_c_assitementryacc t1 join t_c_assitementryacc t2 "
						+ "on t2.accpackageid=? and t2.submonth=? and t1.AssItemID=t2.AssItemID and t1.Accid=t2.Accid "
						+ "set "
						+ "t1.direction=t2.direction,"
						+ "t1.debitremain=t2.debitbalance,"
						+ "t1.creditremain=t2.creditbalance,"
						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,t1.credittotalocc=t2.credittotalocc+t1.creditocc,t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
						
//						+ "t1.debitbalance=t2.debitbalance + t1.debitocc,"
//						+ "t1.creditbalance=t2.creditbalance - t1.creditocc "
						+ "t1.debitbalance=if(t2.balance +t1.debitocc -t1.creditocc>0,t2.balance +t1.debitocc -t1.creditocc,0),"
						+ "t1.creditbalance=if(t2.balance +t1.debitocc -t1.creditocc<0,t2.balance +t1.debitocc -t1.creditocc,0) "
						
//						+ "t1.direction=t2.direction,"
//						+ "t1.debitremain=(case t2.direction when 1 then t2.balance when -1 then 0 else case t2.balance>=0 when true then t2.balance else 0 end end ),"
//						+ "t1.creditremain=(case t2.direction when -1 then t2.balance when 1 then 0 else case t2.balance<0 when true then t2.balance  else 0 end end ),"
//						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,t1.credittotalocc=t2.credittotalocc+t1.creditocc,t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
//						+ "t1.debitbalance=case t2.direction when 1 then (t2.balance +t1.debitocc -t1.creditocc) when -1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)>=0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end,"
//						+ "t1.creditbalance=case t2.direction when -1 then (t2.balance +t1.debitocc -t1.creditocc) when 1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)<0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end "
						
						+ "where t1.accpackageid=? and t1.submonth=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, i - 1);
				ps1.setString(3, AccPackageID);
				ps1.setInt(4, i);
				ps1.execute();
				conn.commit();
				ps1.close();
			}

			/* 插入非叶子核算科目记录 */
			org.util.Debug.prtOut("Assitem:A7="
					+ new ASFuntion().getCurrentTime());
			sql = "select group_concat(distinct concat(accid,'~',assitemid) SEPARATOR \"','\") from t_c_assitementryacc "
					+ "where AccPackageID=? and submonth=1 and isleaf1=0";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if (rs.next()) {
				strid = rs.getString(1);
			}
			rs.close();
			ps.close();

			sql = "insert into t_c_assitementryacc(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,direction,isleaf1,level1, \n"
					+ "DebitRemain,CreditRemain,DebitOcc,CreditOcc,DebitTotalOcc,CreditTotalOcc,Balance,debitbalance,creditbalance) \n"
					+ "select ?,a.accid,a.AssItemID,a.AssItemName,a.AssTotalName,?,b.submonth,\n"
					+ "CASE substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END as direction,a.isleaf,a.level0,0,0,0,0,0,0,0,0,0 \n"
					+ "from \n"
					+ "(select accid,assitemid,assitemname,asstotalname,isleaf,level0 from t_c_assitem \n"
					+ "	where AccPackageID=? and isleaf=0 \n";
			if (strid != null && strid.length() > 0) {
				sql += "and concat(accid,'~',assitemid) not in ('" + strid
						+ "')";
			}
			sql += "	) a \n" + "inner join t_c_accpkgsubject c \n"
					+ "on c.AccPackageID=? and a.accid=c.subjectid \n"
					+ "inner join k_month b \n" + "on b.monthtype=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.setInt(5, subMonth);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 计算非叶子核算项目的期初数 */
			for (i = maxlevel; i > 1; i--) {
				org.util.Debug.prtOut("Assitem:A8="
						+ new ASFuntion().getCurrentTime());
				sql = "update t_c_assitementryacc t1 join (select a.ParentAssItemId,a.accid,b.submonth,\n"
						+ "sum(b.DebitRemain) as tDebitRemain,sum(b.CreditRemain) as tCreditRemain,\n"
						+ "sum(DebitOcc) as tDebitOcc,sum(CreditOcc) as tCreditOcc,sum(balance) as tBalance,sum(DebitBalance) as tDebitBalance,sum(CreditBalance) as tCreditBalance,\n"
						+ "sum(DebitTotalOcc) as tDebitTotalOcc,sum(CreditTotalOcc) as tCreditTotalOcc \n"
						+ "FROM t_c_assitem a,t_c_assitementryacc b where a.AccPackageID=? \n"
						+ "and a.level0=? and b.AccPackageID=? and a.AssItemID = b.AssItemID \n"
						+ "and a.accid=b.accid group BY ParentAssItemId,accid,submonth) as t2 on \n"
						+ "t1.AccPackageID=? and t1.AssItemID=t2.ParentAssItemId and t1.accid=t2.accid \n"
						+ "and t1.submonth=t2.submonth \n"
						+ "set t1.DebitRemain=t1.DebitRemain+tDebitRemain,\n"
						+ "t1.CreditRemain=t1.CreditRemain+t2.tCreditRemain,\n"
						+ "t1.DebitOcc=t1.DebitOcc+t2.tDebitOcc,\n"
						+ "t1.CreditOcc=t1.CreditOcc+t2.tCreditOcc,\n"
						+ "t1.DebitTotalOcc=t1.DebitTotalOcc+t2.tDebitTotalOcc,\n"
						+ "t1.CreditTotalOcc=t1.CreditTotalOcc+t2.tCreditTotalOcc,\n"
						+ "t1.Balance=ifnull(t1.Balance,0)+ifnull(t2.tBalance,0),\n"
						+ "t1.debitbalance=t1.debitbalance+ifnull(t2.tDebitBalance,0),\n"
						+ "t1.creditbalance=t1.creditbalance+ifnull(t2.tCreditBalance,0) ";

//						+ "t1.debitbalance=t1.debitbalance+case t1.direction when 1 then t2.tBalance when -1 then 0 else case t2.tBalance>=0 when true then t2.tBalance else 0 end end,\n"
//						+ "t1.creditbalance=t1.creditbalance+case t1.direction when -1 then t2.tBalance when 1 then 0 else case t2.tBalance<0 when true then t2.tBalance else 0 end end ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, i);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
			}

			org.util.Debug.prtOut("14=" + new ASFuntion().getCurrentTime());

			/**
			 * =======================================================================
			 * 开始汇总多币种和数量帐的辅助核算余额表，只会生成有数据（发生或期初数）的，其余的就不生成了
			 * =======================================================================
			 */
			// 清理掉为0的期初数表
			org.util.Debug.prtOut("Assitem:C1="
					+ new ASFuntion().getCurrentTime());

			sql = "delete from t_c_assitementryaccall where AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "delete from " + tableName
					+ " where AccPackageID=? and datatype='0'";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			org.util.Debug.prtOut("Assitem:C2="
					+ new ASFuntion().getCurrentTime());
			/* 优先插入叶子核算项目外币发生额汇总值记录 */
			// /汇总的时候，过滤掉乐所有作废凭证；正常凭证的PROPERTY在核算明细中是1
			/**
			 * 优化于 20070917
			 * 
			 * 
			 * sql = "insert into
			 * t_c_assitementryaccall(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,isleaf1,level1) " +
			 * "select
			 * ?,tt1.accid,tt1.AssItemID,c.AssItemName,c.AssTotalName,?,submonth,tt1.currency,debitocc,creditocc,c.isleaf,c.level0
			 * from " + "(select
			 * t1.debitocc,t2.creditocc,t1.submonth,t1.AssItemID,t1.subjectid as
			 * accid,t1.currency " + "from " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,currency,sum(CurrValue) as debitocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=1 and
			 * currency >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,currency) t1 " +
			 * "left outer join " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,currency,sum(CurrValue) as creditocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=-1 and
			 * currency >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,currency) t2 " +
			 * "on t1.AssItemID=t2.AssItemID and t1.submonth=t2.submonth and
			 * t1.subjectid=t2.subjectid and t1.currency=t2.currency " + "union " +
			 * "select
			 * t1.debitocc,t2.creditocc,t2.submonth,t2.AssItemID,t2.subjectid as
			 * accid,t1.currency " + "from " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,currency,sum(CurrValue) as debitocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=1 and
			 * currency >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,currency) t1 " +
			 * "right outer join " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,currency,sum(CurrValue) as creditocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=-1 and
			 * currency >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,currency) t2 " +
			 * "on t1.AssItemID=t2.AssItemID and t1.submonth=t2.submonth and
			 * t1.subjectid=t2.subjectid and t1.currency=t2.currency )
			 * tt1,t_c_assitem c " + "where c.AccPackageID=? and
			 * c.AssItemID=tt1.AssItemID AND tt1.accid=c.accid";
			 */
			sql = "insert into t_c_assitementryaccall(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,debitoccF,creditoccF,accsign,isleaf1,level1) \n"
					+ "select ?,tt1.accid,tt1.AssItemID,c.AssItemName,c.AssTotalName,?,submonth,tt1.currency,debitocc,creditocc,debitoccF,creditoccF,1,c.isleaf,c.level0 from  \n"
					+ "( \n"
					+ "	select AssItemID,substring(vchdate,6,2) as submonth,subjectid as accid,currency, \n"
					+ "	sum(case when Dirction=1 then CurrValue else 0 end) as debitocc, \n"
					+ "	sum(case when Dirction=-1 then CurrValue else 0 end) as creditocc, \n"
					+ "	sum(case when Dirction=1 then AssItemSum else 0 end) as debitoccF, \n"
					+ "	sum(case when Dirction=-1 then AssItemSum else 0 end) as creditoccF \n"
					+ "	from t_c_assitementry  \n"
					+ "	where AccPackageID=? and currency >'' and Property like '1%' \n"
					+ "	group by AssItemID, substring(vchdate,6,2),subjectid,currency \n"
					+ ") tt1,t_c_assitem c  \n"
					+ "where c.AccPackageID=? and c.AssItemID=tt1.AssItemID AND tt1.accid=c.accid";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/**
			 * 优化于 20070917
			 * 
			 * 
			 * sql = "insert into
			 * t_c_assitementryaccall(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,isleaf1,level1) " +
			 * "select
			 * ?,tt1.accid,tt1.AssItemID,c.AssItemName,c.AssTotalName,?,submonth,tt1.UnitName,debitocc,creditocc,c.isleaf,c.level0
			 * from " + "(select
			 * t1.debitocc,t2.creditocc,t1.submonth,t1.AssItemID,t1.subjectid as
			 * accid,t1.UnitName " + "from " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,UnitName,sum(Quantity) as debitocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=1 and
			 * UnitName >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,UnitName) t1 " +
			 * "left outer join " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,UnitName,sum(Quantity) as creditocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=-1 and
			 * UnitName >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,UnitName) t2 " +
			 * "on t1.AssItemID=t2.AssItemID and t1.submonth=t2.submonth and
			 * t1.subjectid=t2.subjectid and t1.UnitName=t2.UnitName " + "union " +
			 * "select
			 * t1.debitocc,t2.creditocc,t2.submonth,t2.AssItemID,t2.subjectid as
			 * accid,t1.UnitName " + "from " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,UnitName,sum(Quantity) as debitocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=1 and
			 * UnitName >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,UnitName) t1 " +
			 * "right outer join " + "(select
			 * AssItemID,substring(t_c_assitementry.vchdate,6,2) as
			 * submonth,subjectid,UnitName,sum(Quantity) as creditocc from
			 * t_c_assitementry " + "where AccPackageID=? and Dirction=-1 and
			 * UnitName >'' and Property like '1%' " + "group by AssItemID,
			 * substring(t_c_assitementry.vchdate,6,2),subjectid,UnitName) t2 " +
			 * "on t1.AssItemID=t2.AssItemID and t1.submonth=t2.submonth and
			 * t1.subjectid=t2.subjectid and t1.UnitName=t2.UnitName )
			 * tt1,t_c_assitem c " + "where c.AccPackageID=? and
			 * c.AssItemID=tt1.AssItemID AND tt1.accid=c.accid";
			 */
			sql = "insert into t_c_assitementryaccall(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,dataname,debitocc,creditocc,debitoccF,creditoccF,accsign,isleaf1,level1) \n"
					+ "select ?,tt1.accid,tt1.AssItemID,c.AssItemName,c.AssTotalName,?,submonth,tt1.UnitName,debitocc,creditocc,debitoccF,creditoccF,2,c.isleaf,c.level0 from \n"
					+ "( \n"
					+ "	select AssItemID,substring(vchdate,6,2) as submonth,subjectid as accid,UnitName, \n"
					+ "	sum(case when Dirction=1 then Quantity else 0 end) as debitocc, \n"
					+ "	sum(case when Dirction=-1 then Quantity else 0 end) as creditocc, \n"
					+ "	sum(case when Dirction=1 then AssItemSum else 0 end) as debitoccF, \n"
					+ "	sum(case when Dirction=-1 then AssItemSum else 0 end) as creditoccF \n"
					+ "	from t_c_assitementry  \n"
					+ "	where AccPackageID=? and UnitName >'' and Property like '1%' \n"
					+ "	group by AssItemID, substring(vchdate,6,2),subjectid,UnitName	 \n"
					+ ") tt1,t_c_assitem c  \n"
					+ "where c.AccPackageID=? and c.AssItemID=tt1.AssItemID AND tt1.accid=c.accid";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/**
			 * 设置期初数表的项目类型字段是数量帐，还是外币帐
			 */
			org.util.Debug.prtOut("Assitem:C3.1="
					+ new ASFuntion().getCurrentTime());
			sql = "select group_concat(distinct currency SEPARATOR \"','\") from t_c_assitementry where AccPackageID=? and currency>''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			strid = "''";
			if (rs.next()) {
				strid = "'" + rs.getString(1) + "'";
			}
			rs.close();
			ps.close();

			sql = "update " + tableName
					+ " set accsign=1 where AccPackageID=? and datatype in ("
					+ strid + ")";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			org.util.Debug.prtOut("Assitem:C3.2="
					+ new ASFuntion().getCurrentTime());
			sql = "select group_concat(distinct unitname SEPARATOR \"','\") from t_c_assitementry where AccPackageID=? and unitname>''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			strid = "''";
			if (rs.next()) {
				strid = "'" + rs.getString(1) + "'";
			}
			rs.close();
			ps.close();

			sql = "update " + tableName
					+ " set accsign=2 where AccPackageID=? and datatype in ("
					+ strid + ")";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			org.util.Debug.prtOut("Assitem:C3.3="
					+ new ASFuntion().getCurrentTime());
			sql = "update "
					+ tableName
					+ " a join ("
					+ "select distinct name from k_dic where ctype='currency' "
					+ "union select distinct value from k_dic where ctype='currency') b "
					+ "on a.accpackageid=? and a.accsign=0 and a.datatype=b.name set a.accsign=1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			sql = "update " + tableName + " set accsign=2 "
					+ "where accpackageid=? and accsign=0";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			org.util.Debug.prtOut("Assitem:C4="
					+ new ASFuntion().getCurrentTime());
			/* 补充生成未出现的叶子核算项目的月份记录,但是只会生成有发生额或者期初数的1到12月记录 */
			sql = "insert into t_c_assitementryaccall(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,dataname,SubYearMonth,SubMonth,isleaf1,level1,accsign) "
					+ "select ?,AccID,AssItemID,AssItemName,AssTotalName1,dataname,?,b.submonth,isleaf1,level1,accsign "
					+ "from (select assitemid, accid,dataname,AssItemName,AssTotalName1,isleaf1,level1,accsign from t_c_assitementryaccall "
					+ "where AccPackageID=? group by assitemid, accid,dataname "
					+ "union select a.assitemid,a.accid,a.datatype,b.AssItemName,b.AssTotalName,b.isleaf,b.level0,accsign from "
					+ tableName
					+ " a,t_c_assitem b "
					+ "where a.AccPackageID=? and (abs(a.creditremain)>0.001 or abs(a.debitremain)>0.001) "
					+ "and b.AccPackageID=? and b.isleaf=1 and a.assitemid=b.assitemid "
					+ "and a.accid=b.accid ) as a,k_month b "
					+ "where  b.monthtype=12 "
					+ "and not exists ( "
					+ "select 1 from t_c_assitementryaccall c where c.AccPackageID=? "
					+ "and c.AssItemID = a.AssItemID and c.accid=a.accid and c.submonth=b.submonth and c.dataname=a.dataname)";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.setString(5, AccPackageID);
			ps1.setString(6, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 更新期初数 */
			org.util.Debug.prtOut("Assitem:C5="
					+ new ASFuntion().getCurrentTime());
			sql = "update t_c_assitementryaccall a join "
					+ tableName
					+ " b "
					+ "on a.AccPackageID=? and a.submonth=1 and b.AccPackageID=?  and a.AssItemID = b.AssItemID and a.accid=b.accid and a.dataname=b.datatype "
					+ "set a.debitremain=b.debitremain,a.creditremain=b.creditremain,a.debitremainF=b.debitremainF,a.creditremainF=b.creditremainF";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set debitremain=0 where debitremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set creditremain=0 where creditremain is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set DebitOcc=0.00 where DebitOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set CreditOcc=0.00 where CreditOcc is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set debitremainF=0 where debitremainF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set creditremainF=0 where creditremainF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set DebitOccF=0.00 where DebitOccF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			sql = "update t_c_assitementryaccall set CreditOccF=0.00 where CreditOccF is null and AccPackageID=?";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();
			/* 更新1月份余额 */
			org.util.Debug.prtOut("Assitem:C6="
					+ new ASFuntion().getCurrentTime());
			sql = "update t_c_assitementryaccall t1 join t_c_accpkgsubject t2 "
					+ "on t1.AccPackageID=? and t1.SubMonth='1' "
					+ "and t2.AccPackageID=? and t1.accid=t2.subjectid "
					+ "set t1.DebitTotalOcc=t1.DebitOcc,t1.CreditTotalOcc=t1.CreditOcc,t1.balance=t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc ,"
					+ "t1.direction=CASE substring(t2.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END ,"
					
//					+ "t1.debitbalance=t1.debitremain+t1.debitocc,"
//					+ "t1.creditbalance=t1.creditremain-t1.creditocc, "
					+ "t1.debitbalance=if(t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc>0,t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc,0),"
					+ "t1.creditbalance=if(t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc<0,t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc,0), "
					
					+ "t1.DebitTotalOccF=t1.DebitOccF,"
					+ "t1.CreditTotalOccF=t1.CreditOccF,"
					+ "t1.balanceF=t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF ,"
					
//					+ "t1.debitbalanceF=t1.debitremainF+t1.debitoccF,"
//					+ "t1.creditbalanceF=t1.creditremainF-t1.creditoccF ";
					+ "t1.debitbalanceF=if(t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF>0,t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF,0),"
					+ "t1.creditbalanceF=if(t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF<0,t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF,0) ";

//					+ "t1.debitbalance=case substring(t2.property,2,1) when '1' then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc when '2' then 0 else case (t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc)>=0 when true then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc else 0 end end,"
//					+ "t1.creditbalance=case substring(t2.property,2,1) when '2' then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc when '1' then 0 else case (t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc)<0 when true then t1.debitremain+t1.creditremain+t1.debitocc-t1.creditocc else 0 end end, "
//					+ "t1.DebitTotalOccF=t1.DebitOccF,t1.CreditTotalOccF=t1.CreditOccF,t1.balanceF=t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF ,"
//					+ "t1.debitbalanceF=case substring(t2.property,2,1) when '1' then t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF when '2' then 0 else case (t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF)>=0 when true then t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF else 0 end end,"
//					+ "t1.creditbalanceF=case substring(t2.property,2,1) when '2' then t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF when '1' then 0 else case (t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF)<0 when true then t1.debitremainF+t1.creditremainF+t1.debitoccF-t1.creditoccF else 0 end end ";
					
			;

			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setString(2, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 计算叶子项目的期初数与年度总发生数 */
			org.util.Debug.prtOut("Assitem:C7="
					+ new ASFuntion().getCurrentTime());
			for (i = 2; i <= 12; i++) {
				sql = "update t_c_assitementryaccall t1 join t_c_assitementryaccall t2 "
						+ "on t2.accpackageid=? and t2.submonth=? and t1.AssItemID=t2.AssItemID and t1.Accid=t2.Accid and t1.dataname=t2.dataname "
						+ "set "
						+ "t1.direction=t2.direction,"

						+ "t1.debitremain=t2.debitbalance,"
						+ "t1.creditremain=t2.creditbalance,"
						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,"
						+ "t1.credittotalocc=t2.credittotalocc+t1.creditocc,"
						+ "t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
						
//						+ "t1.debitbalance=t2.debitbalance +t1.debitocc,"
//						+ "t1.creditbalance=t2.creditbalance -t1.creditocc, "
						+ "t1.debitbalance=if(t2.balance +t1.debitocc -t1.creditocc>0,t2.balance +t1.debitocc -t1.creditocc,0),"
						+ "t1.creditbalance=if(t2.balance +t1.debitocc -t1.creditocc<0,t2.balance +t1.debitocc -t1.creditocc,0), "
						
						+ "t1.debitremainF=t2.debitbalanceF,"
						+ "t1.creditremainF=t2.creditbalanceF,"
						+ "t1.debittotaloccF=t2.debittotaloccF+t1.debitoccF,"
						+ "t1.credittotaloccF=t2.credittotaloccF+t1.creditoccF,"
						+ "t1.balanceF=t2.balanceF +t1.debitoccF -t1.creditoccF,"
						
//						+ "t1.debitbalanceF=t2.debitbalanceF +t1.debitoccF ,"
//						+ "t1.creditbalanceF=t2.creditbalanceF -t1.creditoccF "
						+ "t1.debitbalanceF=if(t2.balanceF +t1.debitoccF -t1.creditoccF>0,t2.balanceF +t1.debitoccF -t1.creditoccF,0) ,"
						+ "t1.creditbalanceF=if(t2.balanceF +t1.debitoccF -t1.creditoccF<0,t2.balanceF +t1.debitoccF -t1.creditoccF,0) "
						
//						+ "t1.debitremain=(case t2.direction when 1 then t2.balance when -1 then 0 else case t2.balance>=0 when true then t2.balance else 0 end end ),"
//						+ "t1.creditremain=(case t2.direction when -1 then t2.balance when 1 then 0 else case t2.balance<0 when true then t2.balance  else 0 end end ),"
//						+ "t1.debittotalocc=t2.debittotalocc+t1.debitocc,t1.credittotalocc=t2.credittotalocc+t1.creditocc,t1.balance=t2.balance +t1.debitocc -t1.creditocc,"
//						+ "t1.debitbalance=case t2.direction when 1 then (t2.balance +t1.debitocc -t1.creditocc) when -1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)>=0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end,"
//						+ "t1.creditbalance=case t2.direction when -1 then (t2.balance +t1.debitocc -t1.creditocc) when 1 then 0 else case (t2.balance +t1.debitocc -t1.creditocc)<0 when true then (t2.balance +t1.debitocc -t1.creditocc) else 0 end end, "
//						+ "t1.debitremainF=(case t2.direction when 1 then t2.balanceF when -1 then 0 else case t2.balanceF>=0 when true then t2.balanceF else 0 end end ),"
//						+ "t1.creditremainF=(case t2.direction when -1 then t2.balanceF when 1 then 0 else case t2.balanceF<0 when true then t2.balanceF  else 0 end end ),"
//						+ "t1.debittotaloccF=t2.debittotaloccF+t1.debitoccF,t1.credittotaloccF=t2.credittotaloccF+t1.creditoccF,t1.balanceF=t2.balanceF +t1.debitoccF -t1.creditoccF,"
//						+ "t1.debitbalanceF=case t2.direction when 1 then (t2.balanceF +t1.debitoccF -t1.creditoccF) when -1 then 0 else case (t2.balanceF +t1.debitoccF -t1.creditoccF)>=0 when true then (t2.balanceF +t1.debitoccF -t1.creditoccF) else 0 end end,"
//						+ "t1.creditbalanceF=case t2.direction when -1 then (t2.balanceF +t1.debitoccF -t1.creditoccF) when 1 then 0 else case (t2.balanceF +t1.debitoccF -t1.creditoccF)<0 when true then (t2.balanceF +t1.debitoccF -t1.creditoccF) else 0 end end "

						+ "where t1.accpackageid=? and t1.submonth=?";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setInt(2, i - 1);
				ps1.setString(3, AccPackageID);
				ps1.setInt(4, i);
				ps1.execute();
				conn.commit();
				ps1.close();
			}

			/* 插入非叶子核算科目记录 */
			org.util.Debug.prtOut("Assitem:C8="
					+ new ASFuntion().getCurrentTime());
			sql = "select group_concat(distinct concat(accid,'~',assitemid) SEPARATOR \"','\") from t_c_assitementryaccall "
					+ "where AccPackageID=? and submonth=1 and isleaf1=0";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if (rs.next()) {
				strid = rs.getString(1);
			}
			rs.close();
			ps.close();

			org.util.Debug.prtOut("Assitem:C8.5="
					+ new ASFuntion().getCurrentTime());
			sql = "insert into t_c_assitementryaccall \n"
					+ "(AccPackageID,AccID,AssItemID,AssItemName,AssTotalName1,SubYearMonth,SubMonth,direction,isleaf1,level1,accsign,dataname, \n"
					+ "DebitRemain,CreditRemain,DebitOcc,CreditOcc,DebitTotalOcc,CreditTotalOcc,Balance,debitbalance,creditbalance,"
					+ "DebitRemainF,CreditRemainF,DebitOccF,CreditOccF,DebitTotalOccF,CreditTotalOccF,BalanceF,debitbalanceF,creditbalanceF) \n"
					+ "select ? ,a.accid,a.assitemid,a.assitemname,a.asstotalname,?,b.submonth, \n"
					+ "CASE substring(c.property,2,1) WHEN '2' THEN -1 WHEN '1' THEN 1 ELSE 0 END as direction,0,a.level0,a.accsign,a.dataname,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0 \n"
					+ "from \n"
					+ "( \n"
					+ "	select distinct a.accid,a.assitemid,a.assitemname,a.asstotalname,a.level0,b.accsign,b.dataname \n"
					+ "	from t_c_assitem a,t_c_assitementryaccall b \n"
					+ "	where a.accpackageid=? \n" + "	and a.isleaf=0 \n"
					+ "	and concat(a.accid,'~',a.assitemid) not in ('" + strid
					+ "') \n" + "	and b.accpackageid=? \n"
					+ "	and b.submonth=1 \n"
					+ "	and concat(b.accid,'~',b.assitemid) not in ('" + strid
					+ "') \n" + "	and a.accid=b.accid \n"
					+ "	and b.assitemid like concat(a.assitemid,'%') \n"
					+ ")a \n" + "inner join t_c_accpkgsubject c \n"
					+ "on c.AccPackageID=? and a.accid=c.subjectid \n"
					+ "inner join k_month b \n" + "on b.monthtype=12";
			ps1 = conn.prepareStatement(sql);
			ps1.setString(1, AccPackageID);
			ps1.setInt(2, year);
			ps1.setString(3, AccPackageID);
			ps1.setString(4, AccPackageID);
			ps1.setString(5, AccPackageID);
			ps1.execute();
			conn.commit();
			ps1.close();

			/* 计算非叶子核算项目的期初数 */
			for (i = maxlevel; i > 1; i--) {
				org.util.Debug.prtOut("Assitem:C9="
						+ new ASFuntion().getCurrentTime());
				sql = "update t_c_assitementryaccall t1 join (select a.ParentAssItemId,a.accid,b.submonth,"
						+ "sum(b.DebitRemain) as tDebitRemain,sum(b.CreditRemain) as tCreditRemain,"
						+ "sum(DebitOcc) as tDebitOcc,sum(CreditOcc) as tCreditOcc,sum(balance) as tBalance,"
						
						+ "sum(debitbalance) as tDebitbalance,sum(creditbalance) as tCreditbalance,"
						
						+ "sum(DebitTotalOcc) as tDebitTotalOcc,sum(CreditTotalOcc) as tCreditTotalOcc,"
						+ "sum(b.DebitRemainF) as tDebitRemainF,sum(b.CreditRemainF) as tCreditRemainF,"
						+ "sum(DebitOccF) as tDebitOccF,sum(CreditOccF) as tCreditOccF,sum(balanceF) as tBalanceF,"
						
						+ "sum(debitbalanceF) as tDebitbalanceF,sum(creditbalance) as tCreditbalanceF,"
						
						+ "sum(DebitTotalOccF) as tDebitTotalOccF,sum(CreditTotalOccF) as tCreditTotalOccF,"

						+ "b.dataname "
						+ "FROM t_c_assitem a,t_c_assitementryaccall b where a.AccPackageID=? "
						+ "and a.level0=? and b.AccPackageID=? and a.AssItemID = b.AssItemID "
						+ "and a.accid=b.accid group BY ParentAssItemId,accid,submonth,dataname) as t2 on "
						+ "t1.AccPackageID=? and t1.AssItemID=t2.ParentAssItemId and t1.accid=t2.accid "
						+ "and t1.submonth=t2.submonth and t1.dataname=t2.dataname "
						+ "set t1.DebitRemain=t1.DebitRemain+tDebitRemain,"
						+ "t1.CreditRemain=t1.CreditRemain+t2.tCreditRemain,"
						
						+ "t1.DebitOcc=t1.DebitOcc+t2.tDebitOcc,t1.CreditOcc=t1.CreditOcc+t2.tCreditOcc,t1.DebitTotalOcc=t1.DebitTotalOcc+t2.tDebitTotalOcc,"
						+ "t1.CreditTotalOcc=t1.CreditTotalOcc+t2.tCreditTotalOcc,t1.Balance=t1.Balance+t2.tBalance,"
						
						+ "t1.debitbalance=t1.debitbalance+t2.tDebitbalance,"
						+ "t1.creditbalance=t1.creditbalance+t2.tCreditbalance, "

						+ "t1.DebitRemainF=t1.DebitRemainF+tDebitRemainF,"
						+ "t1.CreditRemainF=t1.CreditRemainF+t2.tCreditRemainF,"
						
						+ "t1.DebitOccF=t1.DebitOccF+t2.tDebitOccF,t1.CreditOccF=t1.CreditOccF+t2.tCreditOccF,t1.DebitTotalOccF=t1.DebitTotalOccF+t2.tDebitTotalOccF,"
						+ "t1.CreditTotalOccF=t1.CreditTotalOccF+t2.tCreditTotalOccF,t1.BalanceF=t1.BalanceF+t2.tBalanceF,"
						
						+ "t1.debitbalanceF=t1.debitbalanceF+t2.tDebitbalanceF,"
						+ "t1.creditbalanceF=t1.creditbalanceF+t2.tCreditTotalOccF ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, i);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
			}

			/**
			 * 保存199凭证到t_c_assitementrybegin表 （年中建帐）
			 */
			sql = "insert into t_c_assitementrybegin (" +
				"AccPackageID, SubjectID, VoucherID, OldVoucherID, VchDate, TypeID, Serail, " +
				"AssItemID, Summary, Dirction, AssItemSum, CurrRate, CurrValue, Currency, " +
				"Quantity, UnitPrice, UnitName, Property " +
				") " +
				"select  " +
				"AccPackageID, SubjectID, VoucherID, OldVoucherID, VchDate, TypeID, Serail, " +
				"AssItemID, Summary, Dirction, AssItemSum, CurrRate, CurrValue, Currency, " +
				"Quantity, UnitPrice, UnitName, Property " +
				"from t_c_assitementry where accpackageid=? and property='199'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			// 删除掉年中建帐创建的补录明细（property='199'）
			org.util.Debug.prtOut("Assitem:D1="
					+ new ASFuntion().getCurrentTime());
			sql = "delete from t_c_assitementry where accpackageid=? and property='199'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();

			org.util.Debug.prtOut("Assitem:D2="
					+ new ASFuntion().getCurrentTime());

			update(AccPackageID,year);	//删除或修改有外币名称没有外币期初、发生的记录
			update(AccPackageID,"t_c_assitementryaccall","DataName","$","＄");
			update(AccPackageID,"t_c_assitementry","Currency","$","＄");
			update(AccPackageID,"t_c_assitementrybegin","Currency","$","＄");
			
			patch( AccPackageID,  year);
			
			return strResult;
		} catch (Exception e) {
			e.printStackTrace();
			org.util.Debug.prtOut("错误时的SQL=" + sql);
			throw new Exception("执行失败" + e.getMessage(), e);
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (ps1 != null)
				ps1.close();
		}
	}

	/**
	 * 导入后,重新刷新凭证分录表和核算凭证对照表的VOUCHERID字段,对应到凭证表的AUTOID字段上.
	 * 
	 * @param customerid
	 *            String
	 * @param accpackageId
	 *            String
	 * @throws Exception
	 */
	public void updateVoucherId(String accpackageId) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;
		try {
			org.util.Debug.prtOut("updateVoucherId:1="
					+ new ASFuntion().getCurrentTime());
			/*
			 *   这一段代码挪到了dataupload.jsp拷贝表那里就直接完成了
			 * 
			// 先把原来的凭证编号字段挪到OLDVOUCHERID里面去；
			String sql = "update c_subjectentry set OldVoucherID=VoucherID "
					+ "where c_subjectentry.accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();

			org.util.Debug.prtOut("updateVoucherId:2="
					+ new ASFuntion().getCurrentTime());
			// 再把凭证ID字段更新成凭证表的AUTOID；
			sql = "update c_subjectentry a join c_voucher b "
					+ "on a.accpackageid=? and b.accpackageid=? "
					+ "and a.OldVoucherID=b.VoucherID "
					+ "and a.typeid=b.typeid and a.vchdate=b.vchdate "
					+ "set a.VoucherID=b.autoid, "
					+ "a.voucherFillUser = b.FillUser, "
					+ "a.voucherAuditUser = b.AuditUser,"
					+ "a.voucherKeepUser = b.KeepUser,"
					+ "a.voucherAffixCount = b.AffixCount; ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();
			
			*/
			
			org.util.Debug.prtOut("updateVoucherId:3="
					+ new ASFuntion().getCurrentTime());
			String sql = "update c_voucher a join ("
					+ "select voucherid,sum((dirction+1)/2*occurvalue) as debitsum,sum(-1*(dirction-1)/2*occurvalue) as creditsum "
					+ "from c_subjectentry "
					+ "where accpackageid=? group by voucherid) b "
					+ "on a.accpackageid=? and a.autoid=b.VoucherID "
					+ "set a.debitocc=b.debitsum,a.creditocc=b.creditsum";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();

			
			/**
			 * 没有凭证表但有凭证分录的凭证：让凭证分录表voucherid不会重复为-1
			 */
			sql = "update c_subjectentry set voucherid= -1 * oldvoucherid where accpackageid=? and voucherid=-1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();
			
			/*
			 * 移到拷贝表的时候，就直接SELECT INTO了
			
			org.util.Debug.prtOut("updateVoucherId:4="
					+ new ASFuntion().getCurrentTime());
			// 再把核算凭证对照表ID字段的VOUCHERID更新到OLDVOUCHERID；
			sql = "update c_assitementry set OldVoucherID=VoucherID "
					+ "where c_assitementry.accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();

			
			org.util.Debug.prtOut("updateVoucherId:5="
					+ new ASFuntion().getCurrentTime());
			// 再把核算凭证对照表ID字段更新成凭证表c_voucher的AUTOID；
			sql = "UPDATE c_assitementry a  join c_voucher b "
					+ "on a.accpackageid=? and b.accpackageid=? "
					+ "and a.OldVoucherID=b.VoucherID "
					+ "and a.typeid=b.typeid and a.vchdate=b.vchdate "
					+ "set a.VoucherID=b.autoid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();
			*/
			
			org.util.Debug.prtOut("updateVoucherId:611="
					+ new ASFuntion().getCurrentTime());
			//复制辅助核算的摘要
			sql = "update c_assitementry as t1 join  c_subjectentry as t2 "
					+ " on t1.accpackageid=? " + " and t2.accpackageid=? "
					+ " and t2.Summary >''   " + " and t1.Summary =''   "
					+ " and t1.serail =t2.serail "
					+ " and t1.VoucherID=t2.VoucherID"
					+ " set t1.Summary =t2.summary"
					+ " where t1.accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.setString(3, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();

			org.util.Debug.prtOut("updateVoucherId:7="
					+ new ASFuntion().getCurrentTime());

			// 把凭证分录的科目编号及累计发生额汇总到debitsubjects,creditsubjects这2个字段
			sql = " update c_subjectentry a,( "
					+ "	select AccPackageID,voucherid,concat(',', "
					+ "	group_concat(distinct if(Dirction*occurvalue>0,subjectid,'')),',') as debitsubjects, "
					+ "	concat(',', "
					+ "	group_concat(distinct if(Dirction*occurvalue<0,subjectid,'')),',') as creditsubjects, "
					+ "sum((dirction+1)/2*occurvalue) as debitsum,sum(-1*(dirction-1)/2*occurvalue) as creditsum" 
					+ "	from c_subjectentry where AccPackageID = ? group by voucherid) b "
					+ "	set a.debitsubjects=b.debitsubjects,a.creditsubjects=b.creditsubjects,a.voucherDebitOcc = b.debitsum,a.voucherCreditOcc = b.creditsum "
					+ "	where a.VoucherID=b.VoucherID "
					+ "	and a.AccPackageID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
			ps.close();

			org.util.Debug.prtOut("updateVoucherId:8="
					+ new ASFuntion().getCurrentTime());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("更新凭证编号出错" + e.getMessage(), e);
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	/**
	 * 导出完成后处理, 更新客户编号
	 * 
	 * @param customerid
	 *            String
	 * @throws Exception
	 */
	public void updateKhbh(String customerid, String accpackageId,
			String username) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;

		try {
			conn.setAutoCommit(false);
			ps = conn
					.prepareStatement("update t_c_AccPackage set CustomerID=? ,userid=?,importDate=? where AccPackageID=?");
			int i = 1;
			ps.setString(i++, customerid);
			ps.setString(i++, username);
			ps.setString(i++, new ASFuntion().getCurrentDate() + " "
					+ new ASFuntion().getCurrentTime());
			ps.setString(i++, accpackageId);
			ps.execute();
			if (conn.getAutoCommit() == false) {
				conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("更新客户编号出错" + e.getMessage(), e);
		} finally {
			if (ps != null)
				ps.close();
		}

	}

	/**
	 * 判断table1是否有值，如果有，就返回table1，否则返回table2；
	 * 
	 * @param table1
	 * @param table2
	 * @return
	 * @throws Exception
	 */
	public String ifValue(String table1, String table2) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from " + table1 + " limit 1 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return table1;
			} else {
				return table2;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("表名出错" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 固定资产汇总
	 */
	public void createGdhz(String AccPackageID, int year) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn.setAutoCommit(false);
			
			try {
				sql = "alter table t_mt_deprate add index AccPackageID (AccPackageID)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				conn.commit();
				DbUtil.close(ps);
				sql = "alter table t_mt_deprate add index ItemNO (ItemNO)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				conn.commit();
				DbUtil.close(ps);
				sql = "alter table t_mt_deprate add index fullpathitemname (fullpathitemname)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				conn.commit();
				DbUtil.close(ps);
				
				sql = "alter table fa_deprate add index AccPackageID (AccPackageID)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				conn.commit();
				DbUtil.close(ps);
				sql = "alter table fa_deprate add index ItemNO (ItemNO)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				conn.commit();
				DbUtil.close(ps);
				sql = "alter table fa_deprate add index fullpathitemname (fullpathitemname)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				conn.commit();
				DbUtil.close(ps);
				
			} catch (Exception e) {
				System.out.println("t_mt_deprate表和fa_deprate表加索引");
			}
			
			sql = "select count(*) from t_fdata where accpackageid=? and (ifnull(itemMonth,'') = '' or ifnull(itemMonth,'') = 12) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID );
			rs = ps.executeQuery();
			conn.commit();
			int C1 = 0,C2 = 0;
			if(rs.next()){
				C1 = rs.getInt(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			sql = "select count(*) from t_fdata where accpackageid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID );
			rs = ps.executeQuery();
			conn.commit();
			if(rs.next()){
				C2 = rs.getInt(1);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			boolean bool = false;	//旧采集的固定资产 没有itemMonth字段 都itemMonth=''或itemMonth=12 要用旧，否则用新
			if(C1 != C2){
				bool = true; //新采集的固定资产 有itemMonth字段
			}
			
			sql = "update t_fdata set itemMonth = ? where accpackageid = ? and ifnull(itemMonth,'') = '' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, "12" );
			ps.setString(2, AccPackageID );
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g1=" + new ASFuntion().getCurrentTime());
			sql = "delete from t_fa_account where accpackageid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID );
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g2=" + new ASFuntion().getCurrentTime());
			sql = "update t_mt_deprate a ,(" +
				" select distinct AccPackageID,ItemNO,ItemName,ItemClass from t_fdata b where b.AccPackageID = ? " +
				" ) b set a.ItemName = b.ItemName ,a.ItemClass = b.ItemClass,a.fullpathitemname=concat(b.ItemClass,'/',b.ItemName) "
				+" where a.ItemNO =b.ItemNO and a.AccPackageID=? and b.AccPackageID = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g3=" + new ASFuntion().getCurrentTime());
			sql = "insert into t_fa_account (AccPackageID,ItemNO,ItemName,subyear,SubMonth,initAdd,initMinus,DepreAdd,DepreMinus,ReservedAdd,ReservedMinus,ParentItem,fullpathitemname,isleaf,level1) "
				+" select AccPackageID,ItemNO,ItemName,?,substring(itemdate,6,2) , "
				+" sum(if(Direction=1 , CostChange , 0 )) as initAdd,sum(if(Direction=-1 , CostChange , 0 )) as initMinus,"
				+" sum(if(Direction=1 , DeprChange , 0 )) as DepreAdd,sum(if(Direction=-1 , DeprChange , 0 )) as DepreMinus,"
				+" sum(if(Direction=1 , ResverdChange , 0 )) as ReservedAdd,sum(if(Direction=-1 , ResverdChange , 0 )) as ReservedMinus,"
				+" ItemClass,fullpathitemname,1,2 "
				+" from t_mt_deprate "
				+" where AccPackageID=? "
				+" group by ItemNO,substring(itemdate,6,2) ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, year);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g4=" + new ASFuntion().getCurrentTime());
			
			String strSQL = " and a.SubMonth = 1";
			if(bool){
				strSQL = " and a.SubMonth=b.itemMonth ";
			}
			
			sql = "update t_fa_account a ,t_fdata b " +
				" set initremain=OpenningBalance,Depreremain = OpenningTotalDep,ReservedRemain=OpenningReserved "
				+" where  a.AccPackageID =?  "
				+strSQL
				+" and a.AccPackageID=b.AccPackageID and a.ItemNO =b.ItemNO ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g5=" + new ASFuntion().getCurrentTime());
			String strSQL1 = "";
			if(bool){
				strSQL = "a.itemMonth,";
				strSQL1 = " and b.SubMonth=a.itemMonth ";
			}else{
				strSQL = "1,";
			}
			sql = "insert into t_fa_account (AccPackageID,ItemNO,ItemName,subyear,SubMonth,initremain,Depreremain,ReservedRemain,ParentItem,fullpathitemname,isleaf,level1) " 
				+" select a.AccPackageID,a.ItemNO,a.ItemName,?,"+strSQL+"a.OpenningBalance,a.OpenningTotalDep,a.OpenningReserved,a.ItemClass,concat(a.ItemClass,'/',a.ItemName),1,2 "
				+" from t_fdata a left join t_fa_account b "
				+" on a.AccPackageID = ?  and a.AccPackageID=b.AccPackageID and a.ItemNO =b.ItemNO " 
				+strSQL1
				+" where a.AccPackageID = ? and b.ItemNO is null";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, year);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g6=" + new ASFuntion().getCurrentTime());
			sql = "insert into t_fa_account(accpackageid,ItemNO,ItemName,subyear,SubMonth,ParentItem,isleaf,level1,fullpathitemname) "
				+" select distinct accpackageid,ItemNO,ItemName,subyear,b.submonth,ParentItem,isleaf,level1,fullpathitemname "
				+" from t_fa_account  a ,k_month b "
				+" where a.AccPackageID = ? and b.monthtype=12 "
				+" and not exists (select 1 from t_fa_account c where c.AccPackageID=? and a.ItemNO=c.ItemNO and b.submonth=c.submonth)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g7=" + new ASFuntion().getCurrentTime());
			sql = "update t_fa_account "
				+" set initbalance = initremain + initAdd - initMinus,"
				+" DepreBalance = Depreremain + DepreAdd - DepreMinus,"
				+ "ReservedBalance = ReservedRemain + ReservedAdd - ReservedMinus "
				+ "where AccPackageID=? and SubMonth='1'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
	
			
			/* 更新叶子科目节点2月到12月的期初数,期末数,余额,累计额等字段 */
			int iSubMonthCount = 12;
			for (int i = 2; i <= iSubMonthCount; i++) {
				org.util.Debug.prtOut("g8=" + new ASFuntion().getCurrentTime());
				sql = "update t_fa_account t1 join t_fa_account t2 "
					+ "on t2.accpackageid=? and t2.submonth=? and t1.ItemNO=t2.ItemNO "
					+ "set t1.initremain = t2.initbalance,"
					+ "t1.initbalance = t2.initbalance + t1.initAdd - t1.initMinus ,"
					+ "t1.Depreremain = t2.DepreBalance,"
					+ "t1.DepreBalance = t2.DepreBalance + t1.DepreAdd - t1.DepreMinus ,"
					+ "t1.ReservedRemain = t2.ReservedBalance,"
					+ "t1.ReservedBalance = t2.ReservedBalance + t1.ReservedAdd - t1.ReservedMinus "
					+ "where t1.accpackageid=? and t1.submonth=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, i - 1);
				ps.setString(3, AccPackageID);
				ps.setInt(4, i);
				ps.execute();
				conn.commit();
				ps.close();
			}
			
			org.util.Debug.prtOut("g9=" + new ASFuntion().getCurrentTime());
			sql = "insert into t_fa_account(accpackageid,ItemNO,ItemName,subyear,SubMonth,ParentItem,isleaf,level1,fullpathitemname) "
				+" select distinct AccPackageID,ItemClass,ItemClass,?,b.submonth,'',0,1,ItemClass "
				+" from t_fdata  a ,k_month b "
				+" where a.AccPackageID = ? and b.monthtype=12 ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, year);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g10=" + new ASFuntion().getCurrentTime());
			sql = "update t_fa_account a ,( "
				+" select parentitem,SubMonth, "
				+" sum(initremain) initremain, sum(initAdd) initAdd,sum(initMinus) initMinus,sum(initbalance) initbalance, "      
				+" sum(Depreremain ) Depreremain, sum(DepreAdd ) DepreAdd, sum(DepreMinus) DepreMinus, sum(DepreBalance) DepreBalance, "     
				+" sum(ReservedRemain) ReservedRemain, sum(ReservedAdd ) ReservedAdd,sum(ReservedMinus) ReservedMinus,sum(ReservedBalance)  ReservedBalance "
				+" from t_fa_account a where isleaf=1 and accpackageid=? "
				+" group by parentitem,SubMonth "
				+" ) b set "
				+" a.initremain = b.initremain,a.initAdd = b.initAdd,a.initMinus = b.initMinus,a.initbalance = b.initbalance, "
				+" a.Depreremain = b.Depreremain,a.DepreAdd = b.DepreAdd,a.DepreMinus = b.DepreMinus,a.DepreBalance = b.DepreBalance, "
				+" a.ReservedRemain = b.ReservedRemain,a.ReservedAdd = b.ReservedAdd,a.ReservedMinus = b.ReservedMinus,a.ReservedBalance = b.ReservedBalance "
				+" where a.accpackageid=? and a.ItemNO = b.parentitem and a.SubMonth = b.SubMonth ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 在固定资产结构表和卡片表增加内容：
			 * 原值增加、原值减少、原值期末、累计折旧增加、累计折旧减少、累计折旧期末
			 */
			org.util.Debug.prtOut("g11=" + new ASFuntion().getCurrentTime());
			sql = "insert into t_fitem(AccPackageID,ItemCode,ItemName,ItemTag,ParentID,InitFlag,StandupName) "
				+" values (?,'mt_initAdd','原值增加',0,'',0,'decimal(15,2)'), "
				+" (?,'mt_initMinus','原值减少',0,'',0,'decimal(15,2)'), "
				+" (?,'mt_initbalance','原值期末',0,'',0,'decimal(15,2)'), "
				+" (?,'mt_DepreAdd','累计折旧增加',0,'',0,'decimal(15,2)'), "
				+" (?,'mt_DepreMinus','累计折旧减少',0,'',0,'decimal(15,2)'), "
				+" (?,'mt_DepreBalance','累计折旧期末',0,'',0,'decimal(15,2)')";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.setString(4, AccPackageID);
			ps.setString(5, AccPackageID);
			ps.setString(6, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g12=" + new ASFuntion().getCurrentTime());
			sql = "alter table t_fdata  "
				+" add column mt_initAdd varchar (100) ,   "
				+" add column mt_initMinus varchar (100) ,   "
				+" add column mt_initbalance varchar (100) ,   "
				+" add column mt_DepreAdd varchar (100) ,   "
				+" add column mt_DepreMinus varchar (100) ,   "
				+" add column mt_DepreBalance varchar (100)  ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g13=" + new ASFuntion().getCurrentTime());
			if(bool){
				sql = "update t_fdata a ,  t_fa_account b  "
					
					+" set a.mt_initAdd=b.initAdd , "
					+" a.mt_initMinus=b.initMinus, "
					+" a.mt_initbalance=b.initbalance, "
					+" a.mt_DepreAdd=b.DepreAdd, "
					+" a.mt_DepreMinus=b.DepreMinus, "
					+" a.mt_DepreBalance=b.DepreBalance " 
					
					+" where a.accpackageid=b.accpackageid and a.itemno=b.itemno and a.itemmonth = b.submonth "
					+" and a.accpackageid=? and b.accpackageid=? ";
			}else{
				sql = "update t_fdata a , ( "
					+" 	select accpackageid, itemno, "
					+" 	sum(if(submonth=1,initremain,0)) mt_initremain, "
					+" 	sum(initAdd) mt_initAdd, "
					+" 	sum(initMinus) mt_initMinus, "
					+" 	sum(if(submonth=12,initbalance,0)) mt_initbalance, "
					+" 	sum(if(submonth=1,Depreremain,0)) mt_Depreremain, "
					+" 	sum(DepreAdd) mt_DepreAdd, "
					+" 	sum(DepreMinus) mt_DepreMinus, "
					+" 	sum(if(submonth=12,DepreBalance,0)) mt_DepreBalance "
					+" 	from t_fa_account where isleaf=1 "
					+" 	and accpackageid=?  "
					+" 	group by itemno "
					+" ) b  "
					+" set a.mt_initAdd=b.mt_initAdd , "
					+" a.mt_initMinus=b.mt_initMinus, "
					+" a.mt_initbalance=b.mt_initbalance, "
					+" a.mt_DepreAdd=b.mt_DepreAdd, "
					+" a.mt_DepreMinus=b.mt_DepreMinus, "
					+" a.mt_DepreBalance=b.mt_DepreBalance " 
					+" where a.accpackageid=b.accpackageid and a.itemno=b.itemno "
					+" and a.accpackageid=? ";
			}
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			org.util.Debug.prtOut("g14=" + new ASFuntion().getCurrentTime());
			
		} catch (Exception e) {
			e.printStackTrace();
			org.util.Debug.prtOut("出错SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	
	/**
	 * 存货汇总
	 * @throws Exception
	 */
	public void createChhz(String AccPackageID, int year) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn.setAutoCommit(false);
			
			//存货关联不上科目与凭证时，增加临时科目编号'XXXX'
			sql = "update t_c_inventorybegin set subjectid = 'XXXX'  where AccPackageID = ? and ifnull(subjectid,'') = '' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			sql = "update t_c_inventoryentry set subjectid = 'XXXX'  where AccPackageID = ? and ifnull(subjectid,'') = '' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			sql = "update t_c_inventoryentry set VchDate = InventoryDate  where AccPackageID = ? and ifnull(VchDate,'') = '' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 生成仓库的全路径和层次
			 */
			sql = "update t_c_stocktype set level0=1,StockParentsId='',StockFullName=StockName " +
				" where accpackageid=? and StockParentsId=''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			int leve0 = 1;
			while(true){
				int count =  0;
				sql = "update t_c_stocktype a , t_c_stocktype b " +
					" set a.level0=?,a.StockFullName=concat(b.StockFullName ,'/',a.StockName) " +
					" where a.accpackageid=? and b.accpackageid=? and b.level0=? and a.StockParentsId = b.StockId ";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, leve0 +1);
				ps.setString(2, AccPackageID);
				ps.setString(3, AccPackageID);
				ps.setInt(4, leve0);
				count = ps.executeUpdate();
				conn.commit();
				ps.close();
				
				if(count == 0){
					break;
				}
				leve0 ++ ;
			}
			
			/**
			 * 是否仓库的叶子
			 */
			sql = "update t_c_stocktype set isleaf = -1 where accpackageid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update  t_c_stocktype a join (" +
				" 	select distinct StockParentsId from t_c_stocktype where accpackageid=? " +
				" ) b on a.accpackageid=? and a.StockId =b.StockParentsId " +
				" set isleaf=0";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update t_c_stocktype set isleaf = 1 where accpackageid=? and isleaf = -1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 生成存货的全路径和层次
			 */
			sql = "update t_c_inventorytype set level0=1,InventoryParentsId='',InventoryFullName=InventoryName " +
				" where accpackageid=? and InventoryParentsId=''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			leve0 = 1;
			while(true){
				int count =  0;
				sql = "update t_c_inventorytype a , t_c_inventorytype b " +
					" set a.level0=?,a.InventoryFullName=concat(b.InventoryFullName ,'/',a.InventoryName) " +
					" where a.accpackageid=? and b.accpackageid=? and b.level0=? and a.InventoryParentsId = b.InventoryId ";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, leve0 +1);
				ps.setString(2, AccPackageID);
				ps.setString(3, AccPackageID);
				ps.setInt(4, leve0);
				count = ps.executeUpdate();
				conn.commit();
				ps.close();
				
				if(count == 0){
					break;
				}
				leve0 ++ ;
			}
			
			/**
			 * 是否存货的叶子
			 */
			sql = "update t_c_inventorytype set isleaf = -1 where accpackageid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update  t_c_inventorytype a join (" +
				" 	select distinct InventoryParentsId from t_c_inventorytype where accpackageid=? " +
				" ) b on a.accpackageid=? and a.InventoryId =b.InventoryParentsId " +
				" set isleaf=0";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update t_c_inventorytype set isleaf = 1 where accpackageid=? and isleaf = -1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 修改存货单据
			 */
			sql = "update t_c_inventoryentry set prices = OccurValue/Quantity where accpackageid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update  t_c_inventoryentry a,t_c_stocktype b " +
			" set a.StockName = b.StockName,a.StockFullName = b.StockFullName " +
			" where  a.accpackageid = ? and b.accpackageid = ? and a.StockId = b.StockId";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			sql = "update  t_c_inventoryentry a,t_c_inventorytype b " +
				" set a.InventoryName = b.InventoryName,a.InventoryFullName = b.InventoryFullName,a.UomUnit = b.UomUnit " +
				" where  a.accpackageid = ? and b.accpackageid = ? and a.InventoryId = b.InventoryId";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 优先插入（包括叶子和栏目上上级科目）发生额汇总值记录
			 */
			/**
			 * 2010-08-16
			 * 存货余额表 c_inventoryaccount 
			 * 修改原因：
			 * 例：
			 * 原材料		存货A	100
			 * 存商品	存货A	100
			 * 
			 * 现在[存货余额表],会把[存货A]汇总为200，
			 * 没有分出[原材料]和[库存商品]对应的[存货]
			 * 
			 * 修改存货
			 * 1、表结构：增加subjectid (t_c_inventoryaccount、t_c_inventorybegin、c_inventoryaccount、c_inventorybegin)
			 * 2、接口,期初要增加科目编号subjectid
			 * 3、装载,存货余额表
			 * 4、查询:存货余额表、存货明细账查询
			 * 5、取数:8888公式
			 */
			org.util.Debug.prtOut("c0=" + new ASFuntion().getCurrentTime());
			sql = "insert into t_c_inventoryaccount(" +
				" AccPackageID,InventoryId,InventoryName,InventoryFullName,Currency,StockId,StockName,StockFullName,subjectid, " +
				" SubYearMonth,submonth,direction,IsLeaf1,Level1,UomUnit,InventoryType ," +
				" debitocc,CreditOcc,debitoccF,creditoccF,debitoccQ,creditoccQ" +
				" ) " +
				" select distinct ? as AccPackageID,a.InventoryId,b.InventoryName,b.InventoryFullName,a.Currency,a.StockId,c.StockName,c.StockFullName,a.subjectid, " +
				" ? as SubYearMonth,a.submonth,1 as direction,b.IsLeaf,b.Level0,UomUnit,InventoryType , " +
				" debitocc,CreditOcc,debitoccF,creditoccF,debitoccQ,creditoccQ " +
				" from   " +
				" (  " +
				" 	select  " +
				" 	sum(case when InventoryInOutType=1 then CurrValue else 0 end) as debitocc,  " +
				" 	sum(case when InventoryInOutType=-1 then CurrValue else 0 end) as creditocc,  " +
				" 	sum(case when InventoryInOutType=1 then OccurValue else 0 end) as debitoccF,  " +
				" 	sum(case when InventoryInOutType=-1 then OccurValue else 0 end) as creditoccF,  " +
				" 	sum(case when InventoryInOutType=1 then Quantity else 0 end) as debitoccQ,  " +
				" 	sum(case when InventoryInOutType=-1 then Quantity else 0 end) as creditoccQ,  " +
				" 	InventoryId,StockId,Currency,substring(vchdate,6,2) as submonth,subjectid  " +
				" 	from t_c_inventoryentry  " +
				" 	where AccPackageID=? " + 
				" 	group by subjectid,InventoryId, StockId,Currency,substring(vchdate,6,2) " + 
				" ) a ,t_c_inventorytype b ,t_c_stocktype c " +
				" where b.AccPackageID=? and c.AccPackageID=?  and a.InventoryId=b.InventoryId and a.StockId = c.StockId";
				
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setInt(2, year);
			ps.setString(3, AccPackageID);
			ps.setString(4, AccPackageID);
			ps.setString(5, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			
			/*
			 * 补全叶子节点的其他月份的数额(这里和本位币不同，只补全已有叶子节点的不为0的， 其他叶子节点不会补D
			 */
			int iSubMonthCount = 12;
			for (int i = 1; i <= iSubMonthCount; i++) {
				org.util.Debug.prtOut("c0"+i+"=" + new ASFuntion().getCurrentTime());
				sql = "insert into t_c_inventoryaccount(InventoryId,AccPackageID,InventoryName,InventoryFullName,SubYearMonth,SubMonth,Currency,StockId,StockName,StockFullName,direction,isleaf1,level1,InventoryType,UomUnit,subjectid)"
					+ "select distinct a.InventoryId,?,a.InventoryName,a.InventoryFullName,?,?,a.Currency,a.StockId,a.StockName,a.StockFullName,a.direction,a.isleaf1,a.level1,a.InventoryType,a.UomUnit,a.subjectid "
					+ "from t_c_inventoryaccount a where AccPackageID=?  "
					+ "and  not exists ( "
					+ "	select 1 from t_c_inventoryaccount c where c.AccPackageID=? and c.Currency=a.Currency and c.StockId = a.StockId "
					+ " and c.InventoryId=a.InventoryId and c.subjectid=a.subjectid and c.submonth=?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, year);
				ps.setInt(3, i);
				ps.setString(4, AccPackageID);
				ps.setString(5, AccPackageID);
				ps.setInt(6, i);
				
				ps.execute();
				conn.commit();
				ps.close();
			
				/* //再补全叶子节点中没有发生额但是有期初数的节点 */
				org.util.Debug.prtOut("d0"+i+"=" + new ASFuntion().getCurrentTime());
				sql = "insert into t_c_inventoryaccount(InventoryId,AccPackageID,InventoryName,InventoryFullName,SubYearMonth,SubMonth,Currency,StockId,StockName,StockFullName,direction,isleaf1,level1,InventoryType,UomUnit,subjectid)"
						+ "select distinct a.InventoryId,?,a.InventoryName,a.InventoryFullName,?,?,b.Currency,b.StockId,e.StockName,e.StockFullName,1 as direction,a.isleaf,a.level0,a.InventoryType,a.UomUnit,b.subjectid "
						+ "from t_c_inventorytype as a,t_c_inventorybegin b,t_c_stocktype e  "
						+ "where a.AccPackageID=? and a.isleaf=1 "
						+ "and b.AccPackageID=?  "
						+ "and a.InventoryId=b.InventoryId "
						+ "and b.StockId = e.StockId "
						+ "and  not exists ("
						+ "select 1 from t_c_inventoryaccount d where d.AccPackageID=? and d.Currency=b.Currency and d.StockId = b.StockId and d.subjectid=b.subjectid "
						+ "and d.InventoryId=a.InventoryId and d.submonth=?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, year);
				ps.setInt(3, i);
				
				ps.setString(4, AccPackageID);
				ps.setString(5, AccPackageID);
				ps.setString(6, AccPackageID);
				ps.setInt(7, i);
				
				ps.execute();
				conn.commit();
				ps.close();
			}
			
			
			/* 更新期初数 */
			org.util.Debug.prtOut("e1=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_inventoryaccount a join t_c_inventorybegin b "
					+ "on a.AccPackageID=? and a.submonth=1 and b.AccPackageID=? and a.Currency=b.Currency and a.StockId = b.StockId and a.subjectid=b.subjectid "
					+ "and a.InventoryId=b.InventoryId set a.Remain=b.CurrRemain,a.RemainF=b.OccurRemain,a.remainQ=b.Quantity";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/* 更新第一个月的叶子和底层含栏目科目记录的累计借方数/贷方数/余额字段 */
			org.util.Debug.prtOut("e2=" + new ASFuntion().getCurrentTime());
			sql = "update t_c_inventoryaccount set "
					+" DebitTotalOcc=DebitOcc,CreditTotalOcc=CreditOcc,balance=Remain+debitocc-creditocc,"
					+" DebitTotalOccF=DebitOccF,CreditTotalOccF=CreditOccF,balanceF=RemainF+debitoccF-creditoccF,"
					+" DebitTotalOccQ=DebitOccQ,CreditTotalOccQ=CreditOccQ,balanceQ=RemainQ+debitoccQ-creditoccQ "
					+" where AccPackageID=? and SubMonth=1 and isleaf1 = 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			iSubMonthCount = 12;
			/* 更新叶子科目节点2月到12月的期初数,期末数,余额,累计额等字段 */
			// 太大的科目体系下只能一条一条的更新
//			org.util.Debug.prtOut("11:=" + new ASFuntion().getCurrentTime());
			for (int i = 2; i <= iSubMonthCount; i++) {
				org.util.Debug.prtOut("f"+i+"=" + new ASFuntion().getCurrentTime());
				sql = "update t_c_inventoryaccount t1 join t_c_inventoryaccount t2 "
						+ " on t2.accpackageid=? and t2.submonth=? and t1.InventoryId=t2.InventoryId and t1.Currency=t2.Currency and t1.StockId = t2.StockId and t1.subjectid = t2.subjectid "
						+ " set "
						
						+ " t1.remain = t2.balance,"
						+ " t1.debittotalocc = t2.debittotalocc + t1.debitocc,"
						+ " t1.credittotalocc = t2.credittotalocc + t1.creditocc,"
						+ " t1.balance = t2.balance + t1.debitocc - t1.creditocc,"
						
						+ " t1.remainF = t2.balanceF,"
						+ " t1.debittotaloccF = t2.debittotaloccF + t1.debitoccF,"
						+ " t1.credittotaloccF = t2.credittotaloccF + t1.creditoccF,"
						+ " t1.balanceF = t2.balanceF + t1.debitoccF - t1.creditoccF,"
						
						+ " t1.remainQ = t2.balanceQ,"
						+ " t1.debittotaloccQ = t2.debittotaloccQ + t1.debitoccQ,"
						+ " t1.credittotaloccQ = t2.credittotaloccQ + t1.creditoccQ,"
						+ " t1.balanceQ = t2.balanceQ + t1.debitoccQ - t1.creditoccQ "
						
						+ " where t1.accpackageid=? and t1.submonth=? and t1.isleaf1 = 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, i - 1);
				ps.setString(3, AccPackageID);
				ps.setInt(4, i);
				ps.execute();
				conn.commit();
				ps.close();
			}
			
			
			/*
			 * 插入非叶子科目记录的1到12月的记录,由于有栏目的情况下会出现非叶子节点也有凭证，
			 * 所以前面的汇总已经发生了出现了非叶子的汇总记录，所以要排除出去,另外,做了精简处理
			 * 只填入已有叶子节点的上级节点,并且会自动过滤栏目所带来的非叶子节点
			 */
//			org.util.Debug.prtOut("12=" + new ASFuntion().getCurrentTime());
			org.util.Debug.prtOut("g0=" + new ASFuntion().getCurrentTime());
			iSubMonthCount = 12;
			for (int i = 1; i <= iSubMonthCount; i++) {
				sql = "insert into t_c_inventoryaccount(InventoryId,AccPackageID,InventoryName,InventoryFullName,SubYearMonth,SubMonth,Currency,StockId,stockname,StockFullName,direction,isleaf1,level1,InventoryType,UomUnit,subjectid)"
					+ "select distinct a.InventoryId,?,a.InventoryName,a.InventoryFullName,?,?,b.Currency,b.StockId,b.stockname,b.StockFullName,1 as direction,a.isleaf,a.level0,a.InventoryType,a.UomUnit,b.subjectid "
						+ "from t_c_inventorytype a,"
						+ "(select distinct InventoryFullName,Currency,StockId,stockname,StockFullName,subjectid from t_c_inventoryaccount where AccPackageID=? ) b "
						+ "where a.AccPackageID=? and a.isleaf=0 "
						+ "and b.InventoryFullName like concat(a.InventoryFullName,'%') "
						+ "and not exists ("
						+ "select 1 from t_c_inventoryaccount d where d.AccPackageID=? and d.Currency=b.Currency and d.StockId = b.StockId and d.subjectid = b.subjectid "
						+ "and d.InventoryId=a.InventoryId and d.submonth=? )";
//				System.out.println(sql);
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, year);
				ps.setInt(3, i);
				ps.setString(4, AccPackageID);
				ps.setString(5, AccPackageID);
				ps.setString(6, AccPackageID);
				ps.setInt(7, i);
				ps.execute();
				conn.commit();
				ps.close();
			}
			

			/* 计算非叶子科目的期初数等字段 */
			// 首先取得总共的层次数目
			sql = "select max(level0) from t_c_inventorytype where AccPackageID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			rs.next();
			int maxlevel = rs.getInt(1);
			rs.close();
			ps.close();
			
			/* 计算非叶子科目的期初数等字段 */
			for (int i = maxlevel; i > 1; i--) {
				org.util.Debug.prtOut("h"+i+"=" + new ASFuntion().getCurrentTime());
				// 这里是不按科目方向来汇总的代码,下级的余额已经按照方向调整过了
				sql = "update t_c_inventoryaccount t1 join ("
						+"select a.InventoryParentsId,b.submonth,b.Currency,b.StockId,b.subjectid,"

						+ "sum(b.Remain) as tRemain,"
						+ "sum(DebitOcc) as tDebitOcc,sum(CreditOcc) as tCreditOcc,"
						+ "sum(DebitTotalOcc) as tDebitTotalOcc,sum(CreditTotalOcc) as tCreditTotalOcc,"
						+ "sum(Balance) as tBalance, "

						+ "sum(b.RemainF) as tRemainF,"
						+ "sum(DebitOccF) as tDebitOccF,sum(CreditOccF) as tCreditOccF,"
						+ "sum(DebitTotalOccF) as tDebitTotalOccF,sum(CreditTotalOccF) as tCreditTotalOccF,"
						+ "sum(BalanceF) as tBalanceF, "
						
						+ "sum(b.RemainQ) as tRemainQ,"
						+ "sum(DebitOccQ) as tDebitOccQ,sum(CreditOccQ) as tCreditOccQ,"
						+ "sum(DebitTotalOccQ) as tDebitTotalOccQ,sum(CreditTotalOccQ) as tCreditTotalOccQ,"
						+ "sum(BalanceQ) as tBalanceQ "
						
						+ " FROM t_c_inventorytype a,t_c_inventoryaccount b where a.AccPackageID=? "
						+ "and a.level0=? and b.AccPackageID=? and a.InventoryId = b.InventoryId "
						+ "group BY InventoryParentsId,submonth,Currency,StockId,subjectid"
						+ ") as t2 "
						+ "on t1.AccPackageID=? and t1.InventoryId=t2.InventoryParentsId and t1.submonth=t2.submonth and t1.Currency=t2.Currency  and t1.StockId=t2.StockId  and t1.subjectid=t2.subjectid "
						+ "set "
						+ "t1.Remain = t1.Remain + t2.tRemain,"
						+ "t1.DebitOcc = t1.DebitOcc + t2.tDebitOcc,"
						+ "t1.CreditOcc = t1.CreditOcc + t2.tCreditOcc,"
						+ "t1.DebitTotalOcc = t1.DebitTotalOcc + t2.tDebitTotalOcc,"
						+ "t1.CreditTotalOcc = t1.CreditTotalOcc + t2.tCreditTotalOcc,"
						+ "t1.Balance = t1.Balance + t2.tBalance,"

						+ "t1.RemainF = t1.RemainF + t2.tRemainF,"
						+ "t1.DebitOccF = t1.DebitOccF + t2.tDebitOccF,"
						+ "t1.CreditOccF = t1.CreditOccF + t2.tCreditOccF,"
						+ "t1.DebitTotalOccF = t1.DebitTotalOccF + t2.tDebitTotalOccF,"
						+ "t1.CreditTotalOccF = t1.CreditTotalOccF + t2.tCreditTotalOccF,"
						+ "t1.BalanceF = t1.BalanceF + t2.tBalanceF,"
						
						+ "t1.RemainQ = t1.RemainQ + t2.tRemainQ,"
						+ "t1.DebitOccQ = t1.DebitOccQ + t2.tDebitOccQ,"
						+ "t1.CreditOccQ = t1.CreditOccQ + t2.tCreditOccQ,"
						+ "t1.DebitTotalOccQ = t1.DebitTotalOccQ + t2.tDebitTotalOccQ,"
						+ "t1.CreditTotalOccQ = t1.CreditTotalOccQ + t2.tCreditTotalOccQ,"
						+ "t1.BalanceQ = t1.BalanceQ + t2.tBalanceQ";
						
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.setInt(2, i);
				ps.setString(3, AccPackageID);
				ps.setString(4, AccPackageID);
				ps.execute();
				conn.commit();
				ps.close();
			}
			
			//插入凭证摘要
			sql = "UPDATE c_subjectentry a,t_c_inventoryentry b " +
			"	SET b.InventDisgn = a.Summary " +
			"	WHERE a.AccPackageID =? " +
			"	AND b.AccPackageID = ? " +
			"	and ifnull(b.InventDisgn,'') = '' " +
			"	AND a.VchDate = b.VchDate " +
			"	AND a.TypeID = b.TypeID " +
			"	AND a.oldvoucherid = b.VoucherID " +
			"	AND a.Serail = b.Serail";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
			/**
			 * 2010-08-16 存货修改完成
			 */
			
			sql = "delete from t_c_inventoryentry where AccPackageID=? and property='199' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			conn.commit();
			ps.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			org.util.Debug.prtOut("出错SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	public void updateNewSubjectFullName(String AccPackageID) throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			boolean isUpdate = true;
			
//			conn.setAutoCommit(false);
			sql = "select projectid from z_project where AccPackageID = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			
			while(rs.next()){
				String projectID = rs.getString(1);
				
				sql = "update z_usesubject a,c_accpkgsubject b " +
				"set a.subjectfullname = concat(b.subjectfullname,'/',a.subjectname) " +
				"where b.accpackageid = ? " +
				"and projectid = ? " +
				"and a.ParentSubjectId = b.subjectid";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, AccPackageID);
				ps1.setString(2, projectID);
				if(ps1.executeUpdate() > 0){
					DbUtil.close(ps1);
					
					sql = "select max(level0) from z_usesubject where projectid = ? ";
					ps1 = conn.prepareStatement(sql);
					ps1.setString(1, projectID);
					rs1 = ps1.executeQuery();
					int iLevel = 0; 
					if(rs1.next()){
						iLevel = rs1.getInt(1);
					}
					DbUtil.close(rs1);
					DbUtil.close(ps1);
					
					int i = 1;
					while(i<=iLevel ){ 
						
						sql = "update z_usesubject a,z_usesubject b " +
						"set a.subjectfullname = concat(b.subjectfullname,'/',a.subjectname) " +
						"where a.projectid = ? " +
						"and b.projectid = ? " +
						"and a.ParentSubjectId = b.subjectid";
						
						ps1 = conn.prepareStatement(sql);
						ps1.setString(1, projectID);
						ps1.setString(2, projectID);
						int ii = ps1.executeUpdate();
						if(!(ii > 0)) {
							DbUtil.close(ps1);
							break;
						}
						DbUtil.close(ps1);
						
						i ++ ;
					}
					
				}
				DbUtil.close(ps1);
			}
			DbUtil.close(rs);
			
		} catch (Exception e) {
			e.printStackTrace();
			org.util.Debug.prtOut("出错SQL："+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}
		
	}
	
	/**
	 * OA导账判断,只要机构信息在oa_company存在则放过
	 * @param strKey
	 * @return
	 * @throws Exception
	 */
	public boolean checkOASysCo(String strKey) throws Exception {
		boolean flag = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
			sql = "select 1 from oa_company where 1=2";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			flag = true;
		} catch(Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
		}
		return flag;
	}
	
	/*
	 * 装载失败时提示正确的事务所名称
	 */
	public String getKeydatCustomerName() throws Exception {
		String result = "";
		FileReader keydat = null;
		BufferedReader br = null;
		File keyFile = new File(this.strDir + "key.dat");
		if (keyFile.exists()) {
			keydat = new FileReader(keyFile);
			br = new BufferedReader(keydat);
			br.readLine();
			try{
				result = br.readLine();
			} catch(Exception e) {
				System.out.println("zxs:获取keydat中正确的事务所名称出错！！");
				result = "";
			}
		}
		return result;
	}
	
	
//	CREATE TABLE `t_excelaccount` (             
//    `accpackageid` varchar(14) default NULL,  
//    `subjectid` varchar(30) default NULL,     
//    `subjectname` varchar(100) default NULL,  
//    `submonth` varchar(10) default '0',      
//    `direction` int(1) default NULL,          
//    `remain` decimal(15,2) default NULL,      
//    `debitocc` decimal(15,2) default NULL,    
//    `creditocc` decimal(15,2) default NULL,   
//    `balance` decimal(15,2) default NULL,     
//    `curname` varchar(30) default NULL,       
//    `remainF` decimal(15,2) default NULL,     
//    `debitoccF` decimal(15,2) default NULL,   
//    `creditoccF` decimal(15,2) default NULL,  
//    `balanceF` decimal(15,2) default NULL,    
//	 KEY `accpackageid` (`accpackageid`,`subjectid`,`submonth`)  	
//  ) ENGINE=MyISAM DEFAULT CHARSET=gbk  
	
	/**
	 * 装载EXCEL表转换
	 */
	public String createExcel(String AccPackageID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			/**
			 * 清理临时表
			 */
			DisposeTableService optable = new DisposeTableService(conn);
			optable.DeleteData("t_c_AccPackage", AccPackageID);
			optable.DeleteData("t_c_AccPkgSubject", AccPackageID);
			optable.DeleteData("t_c_Voucher", AccPackageID);
			optable.DeleteData("t_c_subjectentry", AccPackageID);
			
			optable.DeleteData("t_c_account", AccPackageID);
			optable.DeleteData("t_c_accountall", AccPackageID);
			optable.DeleteData("t_c_accpkgsubjectbegin", AccPackageID);
			optable.DeleteData("t_c_accpkgsubjectopenbegin", AccPackageID);
			
			optable.DeleteData("t_c_assitem", AccPackageID);
			optable.DeleteData("t_c_assitementry", AccPackageID);
			optable.DeleteData("t_c_assitementryacc", AccPackageID);
			optable.DeleteData("t_c_assitementryaccall", AccPackageID);
			optable.DeleteData("t_c_assitembegin", AccPackageID);
			optable.DeleteData("t_c_assitemopenbegin", AccPackageID);
			
			optable.DeleteData("t_c_assitementrybegin", AccPackageID);
			optable.DeleteData("t_c_subjectentrybegin", AccPackageID);
			
			conn.setAutoCommit(false);
			
			ASFuntion asf=new ASFuntion();	
			String year = AccPackageID.substring(6);
			String customerid = AccPackageID.substring(0, 6);
			int i = 1;
			
			/**
			 * 插入账套表
			 */
			sql = "insert into t_c_accpackage(AccPackageID,AccPackageType,CustomerID,AccPackageYear,ExportDate,SoftVersion,CurrName) values (?,'1000',?,?,CURDATE(),?,'人民币')";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, customerid);
			ps.setString(i++, year);
			ps.setString(i++, "EXCEL表导入");
			ps.execute();
			conn.commit();
			
			/**
			 * 插入科目表
			 */
			i = 1;
			sql = "insert into t_c_accpkgsubject (AccPackageID,SubjectID,SubjectName,Property) " +
			" select distinct accpackageid,subjectid,subjectname,if(direction = -1,'02','01') as property " +
			" from t_excelaccount " +
			" where accpackageid = ? " +
			" order by subjectid";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.execute();
			conn.commit();
			
			/**
			 * 插入本位币的科目期初数
			 */
			i = 1;
			sql = "insert into t_c_accpkgsubjectopenbegin (AccPackageID,subjectid,DataType,accsign,DebitRemain,CreditRemain,DebitRemainF,CreditRemainF) " +
			"\n	select accpackageid,subjectid,0,0, " +
			"\n	sum(if(submonth = 1,if(direction = 1,direction*remain,0),0)) as DebitRemain, " +
			"\n	sum(if(submonth = 1,if(direction = -1,direction*remain,0),0)) as CreditRemain, " +
			"\n	sum(if(submonth = 1,if(direction = 1,direction*remain,0),0)) as DebitRemainF, " +
			"\n	sum(if(submonth = 1,if(direction = -1,direction*remain,0),0)) as CreditRemainF " +
			"\n	from t_excelaccount  " +
			"\n	where accpackageid = ? and submonth <> 0 " +
			"\n	group by accpackageid,subjectid " +
 
			"\n	union  " +

			"\n	select accpackageid,subjectid,0,0, " +
			"\n	sum(if(direction = 1,direction*remain,0)) as DebitRemain, " +
			"\n	sum(if(direction = -1,direction*remain,0)) as CreditRemain, " +
			"\n	sum(if(direction = 1,direction*remain,0)) as DebitRemainF, " +
			"\n	sum(if(direction = -1,direction*remain,0)) as CreditRemainF " +
			"\n	from t_excelaccount a " +
			"\n	where accpackageid = ? and submonth = 0 " +
			"\n	and not exists (select 1 from t_excelaccount where accpackageid = ?  and submonth <> 0 and a.subjectid = subjectid) " +
			"\n	group by accpackageid,subjectid " +

			"\n	order by subjectid";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.execute();
			conn.commit();
			
			/**
			 * 插入外币的科目期初数 
			 */
			i = 1;
			sql = "insert into t_c_accpkgsubjectopenbegin (AccPackageID,subjectid,DataType,accsign,DebitRemain,CreditRemain,DebitRemainF,CreditRemainF) " +
			"\n	select accpackageid,subjectid,curname,1, " +
			"\n	sum(if(submonth = 1,if(direction = 1,direction*remainF,0),0)) as DebitRemain, " +
			"\n	sum(if(submonth = 1,if(direction = -1,direction*remainF,0),0)) as CreditRemain, " +
			"\n	sum(if(submonth = 1,if(direction = 1,direction*remain,0),0)) as DebitRemainF, " +
			"\n	sum(if(submonth = 1,if(direction = -1,direction*remain,0),0)) as CreditRemainF " +
			"\n	from t_excelaccount  " +
			"\n	where accpackageid = ? " +
			"\n	and submonth <> 0 " +
			"\n	and trim(ifnull(curname,'')) <> '' " +
			"\n	group by accpackageid,subjectid,trim(ifnull(curname,'')) " +
 
			"\n	union  " +

			"\n	select accpackageid,subjectid,curname,1, " +
			"\n	sum(if(direction = 1,direction*remainF,0)) as DebitRemain, " +
			"\n	sum(if(direction = -1,direction*remainF,0)) as CreditRemain, " +
			"\n	sum(if(direction = 1,direction*remain,0)) as DebitRemainF, " +
			"\n	sum(if(direction = -1,direction*remain,0)) as CreditRemainF " +
			"\n	from t_excelaccount a " +
			"\n	where accpackageid = ? " +
			"\n	and submonth = 0 " +
			"\n	and trim(ifnull(curname,'')) <> '' " +
			"\n	and not exists (select 1 from t_excelaccount where accpackageid = ?  and submonth <> 0 and a.subjectid = subjectid) " +
			"\n	group by accpackageid,subjectid,trim(ifnull(curname,'')) " +

			"\n	order by subjectid";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.execute();
			conn.commit();
			
			/**
			 * 插入凭证分录表：所有都设为199凭证（借方） 
			 */
			i = 1;
			sql = "insert into t_c_subjectentry (AccPackageID,VchDate,SubjectID,Dirction,OccurValue,CurrValue,Currency,Property) " +
			"\n	select AccPackageID,concat(?,'-',LPAD(if(SubMonth = 0,12,SubMonth),2,'0'),'-31') as  VchDate," +
			"\n	SubjectID,1 as Dirction," +
			"\n	debitocc,ifnull(debitoccF,0) as CurrValue, " +
			"\n	trim(ifnull(curname,'')) as Currency,'199' as Property" +
			"\n	from t_excelaccount a" +
			"\n	where accpackageid = ? " +
			"\n	and submonth <> 0 " +
			
			"\n	union " +
			
			"\n	select AccPackageID,concat(?,'-',LPAD(if(SubMonth = 0,12,SubMonth),2,'0'),'-31') as  VchDate," +
			"\n	SubjectID,1 as Dirction," +
			"\n	debitocc,ifnull(debitoccF,0) as CurrValue, " +
			"\n	trim(ifnull(curname,'')) as Currency,'199' as Property" +
			"\n	from t_excelaccount a" +
			"\n	where accpackageid = ? " +
			"\n	and submonth = 0 " +
			"\n	and not exists (select 1 from t_excelaccount where accpackageid = ?  and submonth <> 0 and a.subjectid = subjectid) " ;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, year);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, year);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.execute();
			conn.commit();
			
			/**
			 * 插入凭证分录表：所有都设为199凭证（贷方） 
			 */
			i = 1;
			sql = "insert into t_c_subjectentry (AccPackageID,VchDate,SubjectID,Dirction,OccurValue,CurrValue,Currency,Property) " +
			"\n	select AccPackageID,concat(?,'-',LPAD(if(SubMonth = 0,12,SubMonth),2,'0'),'-31') as  VchDate," +
			"\n	SubjectID,-1 as Dirction," +
			"\n	creditocc,ifnull(creditoccF,0) as CurrValue, " +
			"\n	trim(ifnull(curname,'')) as Currency,'199' as Property" +
			"\n	from t_excelaccount a" +
			"\n	where accpackageid = ? " +
			"\n	and submonth <> 0 " +
			
			"\n	union " +
			
			"\n	select AccPackageID,concat(?,'-',LPAD(if(SubMonth = 0,12,SubMonth),2,'0'),'-31') as  VchDate," +
			"\n	SubjectID,-1 as Dirction," +
			"\n	creditocc,ifnull(creditoccF,0) as CurrValue, " +
			"\n	trim(ifnull(curname,'')) as Currency,'199' as Property" +
			"\n	from t_excelaccount a" +
			"\n	where accpackageid = ? " +
			"\n	and submonth = 0 " +
			"\n	and not exists (select 1 from t_excelaccount where accpackageid = ?  and submonth <> 0 and a.subjectid = subjectid) " ;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, year);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, year);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.execute();
			conn.commit();
			
			/**
			 * 核算暂时不用
			 */
			
			/**
			 * 科目汇总
			 */
			String result = createKmhz(AccPackageID, Integer.parseInt(year));
//			result += this.createXmhz(AccPackageID, Integer.parseInt(year));
			
			/**
			 * 插入物理表
			 */
			org.util.Debug.prtOut("m11=开始更新帐套生产表:" + asf.getCurrentTime());
			sql = "insert into c_accpackage (AccPackageID,AccPackageType,CustomerID,AccPackageYear,ExportDate,"
					+ "ImportDate,UserId,SoftVersion,SubjectCodeRule,CurrName,Property)"
					+ "select AccPackageID,AccPackageType,CustomerID,AccPackageYear,ExportDate,"
					+ "ImportDate,UserId,SoftVersion,SubjectCodeRule,CurrName,Property "
					+ "from t_c_accpackage where accpackageid='"
					+ AccPackageID + "'";
			optable.CopyData("c_AccPackage", sql, AccPackageID);

			org.util.Debug.prtOut("m12=开始更新凭证信息生产表:" + asf.getCurrentTime());
			sql = "insert into c_voucher (AccPackageID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,"
					+ "Director,AffixCount,Description,DoubtUserId,Property,debitocc,creditocc)"
					+ "select AccPackageID,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,"
					+ "Director,AffixCount,Description,DoubtUserId,Property,debitocc,creditocc "
					+ "from t_c_voucher where accpackageid='"
					+ AccPackageID + "'";
			optable.CopyData("c_voucher", sql, AccPackageID);

			org.util.Debug.prtOut("m13=开始更新凭证分录生产表:" + asf.getCurrentTime());
			sql = "insert into c_subjectentry ( \n"
				+"AccPackageID,VoucherID,OldVoucherID,TypeID,VchDate,Serail, \n"
				+"Summary,SubjectID,SubjectName1,SubjectFullName1,Dirction,OccurValue,CurrRate,CurrValue, \n"
				+"Currency,Quantity,UnitPrice,UnitName,BankID,Property, \n"
				+"voucherFillUser,voucherAuditUser,voucherKeepUser,voucherAffixCount \n"
				+") \n"
				+"select "+ AccPackageID + ",ifnull(b.autoid,-1),a.VoucherID,a.TypeID,a.VchDate,a.Serail, \n"
				+"a.Summary,a.SubjectID,a.SubjectName1,a.SubjectFullName1,a.Dirction,a.OccurValue,a.CurrRate,a.CurrValue, \n"
				+"a.Currency,a.Quantity,a.UnitPrice,a.UnitName,a.BankID,a.Property, \n"
				+"b.FillUser,  b.AuditUser,b.KeepUser,b.AffixCount \n"
				+"from t_c_subjectentry a left join c_voucher b  \n"
				+"on a.accpackageid="+ AccPackageID + " and b.accpackageid="+ AccPackageID + " \n"
				+"and a.VoucherID=b.VoucherID  \n"
				+"and a.typeid=b.typeid and a.vchdate=b.vchdate \n"
				+"where a.accpackageid="+ AccPackageID;
			optable.CopyData("c_subjectentry", sql, AccPackageID);

			//更新999凭证表
			sql = "insert into c_subjectentrybegin (" +
				"AccPackageID, VoucherID, OldVoucherID, TypeID, VchDate, Serail, Summary, " +
				"SubjectID, Dirction, OccurValue, CurrRate, CurrValue, Currency, Quantity, " +
				"UnitPrice, UnitName, BankID, Property, subjectname1, SubjectFullName1, tokenid, standname " +
				")  " +
				"select " +
				"AccPackageID, VoucherID, OldVoucherID, TypeID, VchDate, Serail, Summary, " +
				"SubjectID, Dirction, OccurValue, CurrRate, CurrValue, Currency, Quantity, " +
				"UnitPrice, UnitName, BankID, Property, subjectname1, SubjectFullName1, tokenid, standname " +
				"from t_c_subjectentrybegin where accpackageid="+AccPackageID+" and property='199' ";
			optable.CopyData("c_subjectentrybegin", sql, AccPackageID);
			
			org.util.Debug.prtOut("m14=开始更新科目生产表:" + asf.getCurrentTime());
			sql = "insert into c_accpkgsubject (AccPackageID,SubjectCode,SubjectID,ParentSubjectCode,ParentSubjectId,"
					+ "SubjectName,SubjectFullName,AssistCode,UomUnit,Currency,IsLeaf,Level0,DebitRemain,CreditRemain,Property)"
					+ "select AccPackageID,SubjectCode,SubjectID,ParentSubjectCode,ParentSubjectId,"
					+ "SubjectName,SubjectFullName,AssistCode,UomUnit,Currency,IsLeaf,Level0,DebitRemain,CreditRemain,Property "
					+ "from t_c_accpkgsubject where accpackageid='"
					+ AccPackageID + "'";
			optable.CopyData("c_accpkgsubject", sql, AccPackageID);

			org.util.Debug.prtOut("m15=开始更新核算项目生产表:" + asf.getCurrentTime());
			sql = "insert into c_assitem (AccPackageID,AccID,AssItemID,AssItemName,AssTotalName,ParentAssItemId,"
					+ "DebitRemain,CreditRemain,IsLeaf,Level0,UomUnit,Curr,Property) "
					+ "select AccPackageID,AccID,AssItemID,AssItemName,AssTotalName,ParentAssItemId,"
					+ "DebitRemain,CreditRemain,IsLeaf,Level0,UomUnit,Curr,Property "
					+ "from t_c_assitem where accpackageid='"
					+ AccPackageID + "'";
			optable.CopyData("c_assitem", sql, AccPackageID);

			org.util.Debug.prtOut("m16=开始更新核算项目对照生产表:" + asf.getCurrentTime());
			sql = "insert into c_assitementry (AccPackageID,SubjectID,VoucherID,OldVoucherID,VchDate,TypeID,Serail,\n"
				+"AssItemID,Summary,Dirction,AssItemSum,CurrRate,CurrValue,Currency,Quantity,UnitPrice,UnitName,Property)\n"
				+"select "+ AccPackageID + ",a.SubjectID,ifnull(b.autoid,-1),a.VoucherID,a.VchDate,a.TypeID,a.Serail,\n"
				+"a.AssItemID,a.Summary,a.Dirction,a.AssItemSum,a.CurrRate,a.CurrValue,a.Currency,a.Quantity,a.UnitPrice,a.UnitName,a.Property \n"
				+"from t_c_assitementry a left join c_voucher b \n"
				+"on a.accpackageid="+ AccPackageID + " and b.accpackageid="+ AccPackageID + "\n"
				+"and a.VoucherID=b.VoucherID \n"
				+"and a.typeid=b.typeid and a.vchdate=b.vchdate \n"
				+"where a.accpackageid="+ AccPackageID + "\n";
			optable.CopyData("c_assitementry", sql, AccPackageID);

			//更新999凭证表
			sql = "insert into c_assitementrybegin (" +
				"AccPackageID, SubjectID, VoucherID, OldVoucherID, VchDate, TypeID, Serail, " +
				"AssItemID, Summary, Dirction, AssItemSum, CurrRate, CurrValue, Currency, " +
				"Quantity, UnitPrice, UnitName, Property " +
				") " +
				"select  " +
				"AccPackageID, SubjectID, VoucherID, OldVoucherID, VchDate, TypeID, Serail, " +
				"AssItemID, Summary, Dirction, AssItemSum, CurrRate, CurrValue, Currency, " +
				"Quantity, UnitPrice, UnitName, Property " +
				"from t_c_assitementrybegin where accpackageid="+ AccPackageID + " and property='199'";
			optable.CopyData("c_assitementrybegin", sql, AccPackageID);
			
			org.util.Debug.prtOut("m17=开始更新科目余额生产表:" + asf.getCurrentTime());
			sql = "insert into c_account select * from t_c_account where accpackageid='"+ AccPackageID + "'";
			optable.CopyData("c_account", sql, AccPackageID);

			org.util.Debug.prtOut("m18=开始更新科目余额外币/数量表:" + asf.getCurrentTime());
			sql = "insert into c_accountall select * from t_c_accountall where accpackageid='"+ AccPackageID + "'";
			optable.CopyData("c_accountall", sql, AccPackageID);

			org.util.Debug.prtOut("m19=开始更新核算项目余额生产表:" + asf.getCurrentTime());
			sql = "insert into c_assitementryacc select * from t_c_assitementryacc where accpackageid='"+ AccPackageID + "'";
			optable.CopyData("c_assitementryacc", sql, AccPackageID);

			org.util.Debug.prtOut("m20=开始更新核算项目余额外币/数量表:" + asf.getCurrentTime());
			sql = "insert into c_assitementryaccall select * from t_c_assitementryaccall where accpackageid='"+ AccPackageID + "'";
			optable.CopyData("c_assitementryaccall", sql, AccPackageID);

			// 更新凭证分录表的VOUCHERID字段
			org.util.Debug.prtOut("m21=正在更新凭证分录表外键:" + asf.getCurrentTime());
			updateVoucherId(AccPackageID);

			org.util.Debug.prtOut("m22=正在清理期初数据:" + asf.getCurrentTime());
			optable.DeleteData("t_c_assitembegin", AccPackageID);
			optable.DeleteData("t_c_accpkgsubjectbegin", AccPackageID);
			optable.DeleteData("t_c_accpkgsubjectopenbegin", AccPackageID);
			optable.DeleteData("t_c_assitemopenbegin", AccPackageID);
			
			org.util.Debug.prtOut("m26=正在科目对照:" + asf.getCurrentTime());
			new KeyValue().createKeyResult(conn, customerid);
			
			org.util.Debug.prtOut("m27=开始进行默认的往来帐设置:" + asf.getCurrentTime());
			new ComeService(conn).AssistCode(customerid);
			
			org.util.Debug.prtOut("m271=开始设置默认的科目辅助核算披露关系:" + asf.getCurrentTime());
			new SubjectAssitemService(conn).autoSetup(AccPackageID);
			
			org.util.Debug.prtOut("m28=科目类型设置:" + asf.getCurrentTime());
			SubjectTypeService subjecttypeservice=new SubjectTypeService(conn);
			subjecttypeservice.autoset(AccPackageID);
			//设置结转凭证的property属性为2
			subjecttypeservice.autosetEntryCarrydownProperty(AccPackageID);
			
			org.util.Debug.prtOut("m29=开始科目连续性的自动对照:" + asf.getCurrentTime());
			new AutoTokenService(conn).autoOne(AccPackageID);
			
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
	
	
	
	public static void main(String args[]) throws Exception {;
		Connection conn = null;
		conn = new DBConnect().getConnect("");
		String path = "c:\\temp\\1208413670828\\";
		UploadDataService uds = new UploadDataService(path, conn);
		//String d = uds.getExportDate();
		//System.out.println("yuanquan:" + d);
		//System.out.println("yqtime:" + uds.getDate());
		System.out.println(uds.checkOASysCo("东莞市和鑫会计有限公司"));
	}
}
