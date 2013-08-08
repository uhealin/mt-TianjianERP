package com.matech.framework.pub.autocode;

import java.sql.*;
import java.io.*;
import java.util.*;

import com.matech.audit.service.doc.model.AutoCodeUsedVO;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.autocode.model.AutocodeTable;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.StringUtil;
import com.sun.corba.se.spi.activation._ActivatorImplBase;
/**
 *
 * <p>Title:自动生成编码的通用类
 * 需要数据库表的支持;
 * 参数支持的包括
 * ${1}、${1F}、${2}、${2F}、${3}、${3F}
 * ${OWNER}、${CURYEAR}、${EXT1}、${EXT2}、${EXT3}
 *  </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DELAutocode {

  private final String TABLENAME = "k_autocode";
  private String ST_ATYPE = "";// 编号种类
  private String ST_OWNER = "";// 使用者

  private AutocodeTable UVO_CUR_AC = null;//当前执行了getSeries_asc1 之后的数据库表记录的UVO

  private int[] INT_SER_LENGTH = new int[]{0,0,0};    //显示执行了getSeries_asc1之后的长度数组,就是SERNUM1_LEN, SERNUM2_LEN SERNUM3_LEN

  public DELAutocode (){}
  
  private static DELAutocode _dAutocode;
  
  public static DELAutocode getInstant() {
	  if(_dAutocode==null){
		  _dAutocode=new DELAutocode();
	  }
	  return _dAutocode;
  }

  
  
  /**
   * @return 当前执行了setSeries后的UVO
   *（注意：必须在getSeries_asc1执行之后调用才有效）
   */
  public AutocodeTable getCurSeriesUVO(){
    return this.UVO_CUR_AC;
  }

  /**
   * @return 当前执行了setSeries后的显示长度SERNUM1_LEN
   *（注意：必须在getSeries_asc1执行之后调用才有效）
   */
  public int[] getCurSeriesShowLen(){
    return this.INT_SER_LENGTH;
  }

  /**
   * 查看指定种类与使用者的当前值
   * @param Atype  编号种类
   * @param Owner  使用者
   * @return 一个S_AUTOCODE的UVO
   * @throws Exception
   */
  public AutocodeTable peekCurrentSeries(Connection conn,String Atype,String Owner) throws Exception{
    //conn=conn==null?this.conn:conn;
    if (Atype == null)
      Atype = "";
    if (Owner == null)
      Owner = "";
    if (Atype.equals(""))
      throw new Exception("没有指定编号种类");
    if (Owner.equals(""))
      Owner = "all"; //default is all (大家都用的)

    String sql = "select * from " + TABLENAME + " where atype='" + Atype
        + "' and aowner='" + Owner + "';";

    AutocodeTable act = new AutocodeTable();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();

      if (rs.next()) {

        org.util.Debug.prtOut(rs.getInt("curnum1"));
        org.util.Debug.prtOut(rs.getInt("showlen1"));
        org.util.Debug.prtOut(rs.getString("format"));

        act.setId(rs.getInt("id"));
        act.setAowner(rs.getString("aowner"));
        act.setAtype(rs.getString("atype"));
        act.setCurnum1(rs.getInt("curnum1"));
        act.setCurnum2(rs.getInt("curnum2"));
        act.setCurnum3(rs.getInt("curnum3"));
        act.setShowlen1(rs.getInt("showlen1"));
        act.setShowlen2(rs.getInt("showlen2"));
        act.setShowlen3(rs.getInt("showlen3"));
        act.setFormat(rs.getString("format"));
      }
    }
    catch (Exception dbe) {
      dbe.printStackTrace();
      throw new Exception("DB Exception at DELUniAutoCode.peekCurrentSeries",
                          dbe);
    }finally {
		DbUtil.close(null, ps, rs);

	} // end try~catch~finally

    return act;
  }

  /**
   * 得到指定种类与使用者的自加1的值, 并更新数据表
   * 注：如果 为小于0的值则为不使用的值，仍返回但不加一
   * @param Atype  编号种类
   * @param Owner  使用者
   * @return S_AUTOCODE表中的 CUR_SERNUM1、CUR_SERNUM2、CUR_SERNUM3组成的int[]数组
   * @throws Exception
 
  public int[] getSeries_asc1(String Atype,String Owner)throws Exception{
    if(Atype == null) Atype = "";
    if(Owner == null) Owner = "";
    if(Atype.trim().equals("")) throw new Exception("没有指定编号种类");
    if(Owner.trim().equals("")) Owner = "all";//default is all (大家都用的)

    AutocodeTable uvo_AC =  this.peekCurrentSeries(Atype,Owner);
    this.UVO_CUR_AC = uvo_AC;
    if(uvo_AC == null){
      throw new Exception("还没有配置种类为["+Atype+"]的自动编号策略");
    }
//System.err.println(">>>" + uvo_AC);
    int ai[] = new int[]{-1,-1,-1};

    ai[0] = uvo_AC.getCurnum1();
    ai[1] = uvo_AC.getCurnum2();
    ai[2] = uvo_AC.getCurnum3();

    if(ai[0] >= 0){
      ai[0] = ai[0] + 1;
    }
    if(ai[1] >= 0){
      ai[1] = ai[1] + 1;
    }
    if(ai[2] >= 0){
      ai[2] = ai[2] + 1;
    }

    this.setSeries(Atype,Owner,ai);
    if(this.UVO_CUR_AC != null){
      this.UVO_CUR_AC.setCurnum1(ai[0]);
      this.UVO_CUR_AC.setCurnum2(ai[1]);
      this.UVO_CUR_AC.setCurnum3(ai[2]);

      this.INT_SER_LENGTH[0] = uvo_AC.getShowlen1();
      this.INT_SER_LENGTH[1] = uvo_AC.getShowlen2();
      this.INT_SER_LENGTH[2] = uvo_AC.getShowlen3();

    }
    return ai;
  }
    */

  /**
   * 得到自动编号字符串
   * @param aType 编号类型
   * @param owner 编号所有者
   * @return 系统生成的自动编号
   * @throws Exception
   */
  public String getAutoCode(String aType,String owner)throws Exception{
    return getAutoCode(aType, owner, null,new DBConnect().getConnect());
  }

  
  public synchronized  String getAutoCode(String aType,String owner, String []exts)throws Exception{
        return getAutoCode(aType, owner,exts,new DBConnect().getConnect());	  
  }
  
  
  
  public synchronized AutoCodeUsedVO bookAutoCode(Connection conn,String aType,String owner, String []exts,String year,int number1,int number2,int number3){
	    String fullnumber="";
	    AutocodeTable uvoAutoCode=null;
	    DbUtil dbUtil=null;
	    AutoCodeUsedVO autoCodeUsedVO=new AutoCodeUsedVO();
		try {
			uvoAutoCode = this.peekCurrentSeries(conn,aType,owner);
			this.UVO_CUR_AC = uvoAutoCode;
			dbUtil=new DbUtil(conn);
			fullnumber=formatNum(uvoAutoCode, owner, exts,year,number1,number2,number3);
			List<AutoCodeUsedVO> autoCodeUsedVOs=dbUtil.select(AutoCodeUsedVO.class, "select * from {0} where atype=? and year=? and number=?", 
					aType,year,number1);
			if(autoCodeUsedVOs.size()>0){
				autoCodeUsedVO=autoCodeUsedVOs.get(0);
				autoCodeUsedVO.setState(1);
				dbUtil.update(autoCodeUsedVO);
				fullnumber=autoCodeUsedVO.getFullnumber();
			}else{
				autoCodeUsedVO.setAbandondate(StringUtil.getCurDate());
				autoCodeUsedVO.setAbandonuser(owner);
				autoCodeUsedVO.setApplydate(StringUtil.getCurDate());
				autoCodeUsedVO.setAtype(aType);
				autoCodeUsedVO.setFullnumber(fullnumber);
				autoCodeUsedVO.setNumber(String.valueOf(number1));
				autoCodeUsedVO.setState(1);
				autoCodeUsedVO.setUuid(UUID.randomUUID().toString());
				autoCodeUsedVO.setYear(Integer.parseInt(StringUtil.getCurYear()));
				dbUtil.insert(autoCodeUsedVO);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return autoCodeUsedVO;
  }
  
  public synchronized  String getAutoCode(String aType,String owner, String []exts,Connection conn)throws Exception{
	  return getAutoCode(aType, owner, exts, conn,false);
  }
  
  /**
   * 得到自动编号字符串
   * @param aType 编号类型
   * @param owner 编号所有者
   * @param exts 扩展输入信息
   * @return 系统生成的自动编号
   * @throws Exception
   */
  public synchronized  String getAutoCode(String aType,String owner, String []exts,Connection conn,boolean isBooked)throws Exception{
    /**
     * 检查输入参数的合法性
     */
    if ( aType == null || aType.trim().equals("") ) {
      throw new Exception("没有指定编号种类");
    }
    if ( owner == null || owner.trim().equals("") ) {
      /**
       * 默认为所有人
       */
      owner = "all";
    }

    /**
     * 从领号历史表中查看有无可用编号
     * 
     */
    ASFuntion CHF = new ASFuntion() ;
    String sql = "";

	PreparedStatement ps = null;
	ResultSet rs = null;
	String strResult="";
	int iNum=0,count=0;
	try {
		
	 
	  
	  /**
	     * 取字段编号配置
	     */
	    AutocodeTable uvoAutoCode =  this.peekCurrentSeries(conn,aType,owner);
	    this.UVO_CUR_AC = uvoAutoCode;
	    if(uvoAutoCode == null){
	      throw new Exception("还没有配置种类为["+aType+"],所有者为["+owner+"]的自动编号策略");
	    }
	  
	  
	  sql="select min(number),count(uuid) from k_autocodeused where atype='" + aType  + "' and year='" + CHF.getCurrentDate("yyyy") + "' and state=0;";
	  ps = conn.prepareStatement(sql);
	  rs = ps.executeQuery();
	  if (rs.next()) {
		  //有废弃的，复用废弃的
		  iNum=rs.getInt(1);
		  count=rs.getInt(2);
	  }
	  if(count>0){
		  uvoAutoCode.setCurnum1(iNum);
		  strResult=formatNum(uvoAutoCode,owner,exts);
		  
		  rs.close();
		  ps.close();
		  
		  //重新标记成已用
		  sql="update k_autocodeused set state=1,applydate='"+CHF.getCurrentDate()+"' "
		  	+"where atype='" + aType  + "' and year='" + CHF.getCurrentDate("yyyy") + "' and number="+iNum;
		  ps = conn.prepareStatement(sql);
		  ps.execute();
		  
		  return strResult;
	  }else{
		  //没有废弃的，就加1
		  iNum=uvoAutoCode.getCurnum1()+1;
		  
		  //得到第一个没用的
		  sql="select number from k_autocodeused where atype=? and year=? and state=0 order by number asc limit 0,1 ";
		  ps = conn.prepareStatement(sql);
		  ps.setString(1, aType);
		  ps.setString(2, CHF.getCurrentDate("yyyy"));
		 // ps.setString(3, String.valueOf(iNum));
		  rs = ps.executeQuery();
		  if(rs.next()){
			 // rs.close();
			  
			  iNum=rs.getInt(1)+1;
			  //ps.setString(3, String.valueOf(iNum));
		  }
		  rs.close();
		  ps.close();
		  
		  //登记使用情况
		  uvoAutoCode.setCurnum1(iNum);
		  strResult=formatNum(uvoAutoCode,owner,exts);
		  
		  sql="insert k_autocodeused (uuid,number,fullnumber,YEAR,atype,state,applydate) value (UUID(),?,?,?,?,?,?)";
		  ps = conn.prepareStatement(sql);
		  ps.setString(1,String.valueOf(iNum));
		  ps.setString(2,strResult);
		  ps.setString(3,CHF.getCurrentDate("yyyy"));
		  ps.setString(4,aType);
		  ps.setString(5,isBooked?"1":"0");
		  ps.setString(6,CHF.getCurrentDate());
		  ps.execute();
		  ps.close();
		  
		  //更新k_autocode
		  sql="update k_autocode set curnum1=? where atype=? and aowner=?";
		  ps = conn.prepareStatement(sql);
		  ps.setString(1,String.valueOf(iNum));
		  ps.setString(2,aType);
		  ps.setString(3,owner);
		  
		  ps.execute();
	  }
	}
	catch (Exception dbe) {
	  dbe.printStackTrace();
	  throw new Exception("DB Exception at DELUniAutoCode.peekCurrentSeries",
	                      dbe);
	}finally {
		//2006-11-07 void
		DbUtil.close(null, ps, rs);
	
	} // end try~catch~finally

    return strResult;
  }
  
  private void addNum(AutocodeTable uvoAutoCode){
	  	int ai[] = new int[]{-1,-1,-1};
	    ai[0] = uvoAutoCode.getCurnum1();
	    ai[1] = uvoAutoCode.getCurnum2();
	    ai[2] = uvoAutoCode.getCurnum3();

	    int codeLen[] = new int[]{-1,-1,-1};
	    codeLen[0] = uvoAutoCode.getShowlen1();
	    codeLen[1] = uvoAutoCode.getShowlen2();
	    codeLen[2] = uvoAutoCode.getShowlen3();
	    
	    /**
	     * 取配置字符串,使用MEMO字段
	     */
	    String strConf = uvoAutoCode.getFormat();

	    /**
	     * 解析配置字符串,替换变量,系统支持的变量包括-------
	     */
	    String strResult = "";
	    int oldIdx = 0;
	    int idx = strConf.indexOf("${");
	    while ( idx >= 0 ) {
	      String env = getEnv(strConf.substring(idx));
	      strResult += strConf.substring(oldIdx, idx);
	      if ( env.equals("1") ) {
	        /**
	         * 第一个数值
	         */
	    	  uvoAutoCode.setCurnum1(uvoAutoCode.getCurnum1()+1);
	      } else if ( env.startsWith("1F") ) {
	        /**
	         * 第一个数值,如果长度不足,则补足长度
	         */
	    	  uvoAutoCode.setCurnum1(uvoAutoCode.getCurnum1()+1);
	      } else if ( env.equals("2") ) {
	    	  uvoAutoCode.setCurnum2(uvoAutoCode.getCurnum2()+1);
	      } else if ( env.startsWith("2F") ) {
	    	  uvoAutoCode.setCurnum2(uvoAutoCode.getCurnum2()+1);
	      } else if ( env.equals("3") ) {
	    	  uvoAutoCode.setCurnum3(uvoAutoCode.getCurnum3()+1);
	      } else if ( env.startsWith("3F") ) {
	    	  uvoAutoCode.setCurnum3(uvoAutoCode.getCurnum3()+1);
	      } 
	      oldIdx = idx + 2 + env.length() + 1;
	      idx = strConf.indexOf("${", oldIdx);
	    }
  }
  
  public String formatNum(AutocodeTable uvoAutoCode,String owner, String []exts)throws Exception{
	    return formatNum(uvoAutoCode, owner, exts, StringUtil.getCurYear(), uvoAutoCode.getCurnum1(),uvoAutoCode.getCurnum2(),uvoAutoCode.getCurnum3());
  }
  
  public String formatNum(AutocodeTable uvoAutoCode,String owner, String []exts,String year,int number1,int number2, int number3)throws Exception{
	  
	  	int ai[] = new int[]{number1,number2,number3};

	    int codeLen[] = new int[]{-1,-1,-1};
	    codeLen[0] = uvoAutoCode.getShowlen1();
	    codeLen[1] = uvoAutoCode.getShowlen2();
	    codeLen[2] = uvoAutoCode.getShowlen3();
	  
	    /**
	     * 取配置字符串,使用MEMO字段
	     */
	    String strConf = uvoAutoCode.getFormat();

	    /**
	     * 解析配置字符串,替换变量,系统支持的变量包括-------
	     */
	    String strResult = "";
	    int oldIdx = 0;
	    int idx = strConf.indexOf("${");
	    while ( idx >= 0 ) {
	      String env = getEnv(strConf.substring(idx));
	      strResult += strConf.substring(oldIdx, idx);
	      if ( env.equals("1") ) {
	        /**
	         * 第一个数值
	         */
	        ai[0] = ai[0];
	        strResult += ai[0];
	      } else if ( env.startsWith("1F") ) {
	        /**
	         * 第一个数值,如果长度不足,则补足长度
	         */
	        ai[0] = ai[0];
	        char fc = '0';
	        if ( env.length() >= 3 ) {
	          fc = env.charAt(2);
	        }
	        strResult += fillChar(ai[0], codeLen[0], fc);
	      } else if ( env.equals("2") ) {
	        /**
	         * 第二个数值
	         */
	        ai[1] = ai[1];
	        strResult += ai[1];
	      } else if ( env.startsWith("2F") ) {
	        /**
	         * 第二个数值,如果长度不足,则补足长度
	         */
	        ai[1] = ai[1];
	        char fc = '0';
	        if ( env.length() >= 3 ) {
	          fc = env.charAt(2);
	        }
	        strResult += fillChar(ai[1], codeLen[1], fc);
	      } else if ( env.equals("3") ) {
	        /**
	         * 第三个数值
	         */
	        ai[2] = ai[2];
	        strResult += ai[2];
	      } else if ( env.startsWith("3F") ) {
	        /**
	         * 第三个数值,如果长度不足,则补足长度
	         */
	        ai[2] = ai[2] ;
	        char fc = '0';
	        if ( env.length() >= 3 ) {
	          fc = env.charAt(2);
	        }
	        strResult += fillChar(ai[2], codeLen[2], fc);
	      } else if ( env.equals("OWNER") ) {
	        /**
	         * 所有者
	         */
	        strResult += owner;
	      } else if ( env.equals("CURYEAR") ) {
	        /**
	         * 当前年份
	         */
	        String strDate =ASFuntion.getToday();
	        strDate = year;
	        strResult += strDate;
	      } else if ( env.startsWith("EXT") ) {
	        String extNum = "1";
	        if ( env.length() > 3 ) {
	          extNum = env.substring(3);
	        }
	        int iExtNum = (new Integer(extNum)).intValue();
	        if ( exts == null || iExtNum > exts.length ) {
	          throw new Exception("自动编号配置错误,扩展信息长度非法["+strConf+"]");
	        }
	        String strExt = exts[iExtNum-1];
	        if ( strExt == null ) {
	          strExt = "";
	        }
	        strResult += strExt;
	      }

	      oldIdx = idx + 2 + env.length() + 1;
	      idx = strConf.indexOf("${", oldIdx);
	    }
	    strResult += strConf.substring(oldIdx);
	    
	    return strResult;
  }
  

  /**
   * 取变量,格式为${ENV_NAME}
   * @param str 输入字符串
   * @return
   */
  private String getEnv(String str) {
    int index = str.indexOf("}");
    return str.substring(2, index);
  }

  public String fillChar(int values,int len,char fchar,char aorb)throws Exception{
    String st_temp = "";
    int int_len = -1;
    st_temp = st_temp.valueOf(values);
    int_len = st_temp.length();
    if(len < int_len) throw new Exception("编码已经超过了长度限制,请与系统管理员联系");
    for(int i=1;i<=len-int_len;i++){
      if(aorb == 'a'){   //after
        st_temp = fchar + st_temp;
      }else if(aorb == 'b'){//before
        st_temp = st_temp + fchar;
      }

    }
    return st_temp;
  }

  public String fillChar(int values, int len, char fchar) throws Exception {
    return this.fillChar(values, len, fchar, 'a');
  }
  
  
  /**
   * 检查记录在k_autocode表中是否存在，不在就新建
 * @param Atype
 * @param Owner
 * @param format
 * @param length
 * @throws Exception
 */
public void checkAutoCode(String atype,String owner,String format,String length) throws Exception{

	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    try {
	      conn = new DBConnect().getConnect();
	      
	      String sql = "select 1 from k_autocode where atype='" + atype
	        + "' and aowner='" + owner + "';";

	      ps = conn.prepareStatement(sql);
	      rs = ps.executeQuery();

	      if (!rs.next()) {
	    	  //新增记录
	    	  sql = "insert into k_autocode(atype,aowner,curNum1,showlen1,format) values(?,?,?,?, ?)" ;
	    	  ps = conn.prepareStatement(sql) ;
	    	  ps.setString(1,atype) ;
	    	  ps.setString(2,owner) ;
	    	  ps.setString(3,"0") ;
	    	  ps.setString(4,length) ;
	    	  ps.setString(5,format) ;
	    	  
	    	  ps.execute() ;
	       
	      }
	    }catch (Exception e) {
	      e.printStackTrace();
	    }finally {
	    	try {
	    		if(rs != null)
	        		rs.close();
	    		if(ps != null)
	    			ps.close();
	    		if(conn != null)
	    			conn.close();
	    	}catch(SQLException ex) {
	    		ex.printStackTrace();
	    	}
		} // end try~catch~finally

	  }

  public static void   main(String[] para) {
    DELAutocode t = new DELAutocode();
    try{
      String ai[] = new String[]{"泉商行","贷","合同"};

      System.err.println(t.getAutoCode("KHDH","",ai));
      System.err.println(t.getAutoCode("XMBH",""));

    }catch(Exception e){
        e.printStackTrace();
    }finally{
    }

  }
}
