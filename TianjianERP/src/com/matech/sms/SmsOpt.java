package com.matech.sms;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.xml.rpc.ParameterMode;

import cn.emay.sdk.client.api.Client;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;


public class SmsOpt {
	
	/** 默认的超时时间： */
	private static final int TIME_OUT = 1000 * 60;
	private static String DEFAULT_URL = "http://huixin.huigt.com/no";
	public static String DEFAULT_ACCOUNT = "502358";
	public static String DEFAULT_VALIDATE_CODE = "987f58e3d9f54232ba78a6a023d2c144";
	
	private SmsOpt() {
		
	}
	
	
	/**
	 * 天健发送短信函数
	 * 其实只记录到数据库表，不发送
	 * 要立即发送，请调用 sendRealSm
	 * @param recvNum	//测试接收号码，号码间用逗号隔开
	 * @param content	//测试内容
	 * @return
	 */
	public synchronized static String sendSm(String recvNum,String content,String unkey,Connection conn,String sendtime){
		//recvNum="13632254864";
		String clientNo="057188216999"; //"057187719000,057188216999";
		String remark=MessageFormat.format("平台{2}发短信{0}:{1}", recvNum,content,clientNo);
		////System.out.println(remark);
		
		DbUtil dbUtil=null;
		String re="";
		int eff=0;
		SmsConfigVO smsConfigVO=null;
		try{
			dbUtil=new DbUtil(conn);
            smsConfigVO=dbUtil.load(SmsConfigVO.class, "sys");
            SmsVO smsVO=new SmsVO();
            smsVO.setClient_num(smsConfigVO.getClient_no());
            smsVO.setContext(content);
            smsVO.setCreate_time(StringUtil.getCurDateTime());
            smsVO.setSend_date(StringUtil.getCurDate());
            //System.out.println("=====================sendtime="+sendtime);
            smsVO.setSend_time(sendtime);
            smsVO.setRemark(remark);
            smsVO.setUn_key(unkey);
            smsVO.setUuid(UUID.randomUUID().toString());
            smsVO.setState("r");	//
            smsVO.setMobile(recvNum);
            
			eff+=dbUtil.insert(smsVO);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return re;
	}
	
	/**
	 * 短信号码，多个用,分割
	 * 内容：短信内容
	 * unkey，唯一主键（确保不重复发送）
	 * @param recvNum
	 * @param content
	 * @param unkey
	 * @return
	 */
	public synchronized static String sendSm(String recvNum,String content,String unkey,String sendtime){
		Connection conn=null;
		try {
			conn=new DBConnect().getConnect();
			
			return sendSm(recvNum, content,unkey,conn,sendtime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-1";
		}finally{
			DbUtil.close(conn);
		}
	}
	
	public static String sendSm(String recvNum, String content) {
		// TODO Auto-generated method stub
		return sendSm(recvNum, content, UUID.randomUUID().toString(),StringUtil.getCurDateTime());
	}
	
	public static String sendSm(String recvNum, String content,String unkey) {
		// TODO Auto-generated method stub
		return sendSm(recvNum, content, unkey,StringUtil.getCurDateTime());
	}
	
	public static String sendSMS(String recvNum, String content) {
		// TODO Auto-generated method stub
		return sendSm(recvNum, content, UUID.randomUUID().toString(),StringUtil.getCurDateTime());
	}

	
	/**
	 * ==========================================================================================================
	 * 真的发短信
	 * ==========================================================================================================
	*/
	
	public synchronized static String sendRealSm(String recvNum,String content){
		return sendRealSm( recvNum, content,null);
	}
	
	public synchronized static String sendRealSm(String recvNum,String content,SmsConfigVO smsConfigVO){
		
		if (smsConfigVO==null){
			return sendRealSm(
					DEFAULT_URL,
					DEFAULT_ACCOUNT,
					DEFAULT_VALIDATE_CODE,
					"SEND",
					"88216999",
					recvNum,
					content
			);	
		}else{
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date sdate=null,ndate=new Date(),edate=null;
			try {
				sdate=sdf.parse(StringUtil.getCurDate()+" "+smsConfigVO.getStart_time());
				edate=sdf.parse(StringUtil.getCurDate()+" "+smsConfigVO.getEnd_time());
				if(ndate.before(sdate)||ndate.after(edate)){
					return MessageFormat.format("{2}超过短信发送时间{0}至{1}",
					smsConfigVO.getStart_time(),
					smsConfigVO.getEnd_time(),
					StringUtil.getCurTime()
					);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return sendRealSm(smsConfigVO.getService_url(),
					smsConfigVO.getUnit_code(),
					smsConfigVO.getSubject_code(),
					 smsConfigVO.getSend_type(),
					 //"88216999",
					smsConfigVO.getClient_no(),
					StringUtil.isBlank(smsConfigVO.getTest_phone())?recvNum:smsConfigVO.getTest_phone(),
					 content+smsConfigVO.getContext_subfix()
			);	
		}
		
	}
	
	
	
	/**
	 * ==========================================================================================================
	 * 以上为调用方法
	 * 为了简便和修改方便，就不用接口来封装实际调用函数了
	 * 下面是实际调用函数代码
	 * ==========================================================================================================
	 */
	
	
	/**
	 * String serviceURL="http://sms.huigt.com:8080/smsService/services/smsService?wsdl";
			String unitCode="";                //使用单位
			String subjectCode="";             //主题编码   4-64位
			String sendType="";                //发送类型   1电信
			String sendNum="";	           //测试主叫号码		
			String recvNum="";                 //测试接收号码，号码间用逗号隔开
			String content="";                 //测试内容
			String value="";                   //返回结果
	 * @param serviceURL
	 * @param unitCode
	 * @param subjectCode
	 * @param sendType
	 * @param sendNum
	 * @param recvNum
	 * @param content
	 * @return
	 */
	private synchronized static String sendRealSm(String serviceURL,String unitCode,String subjectCode,String sendType,String sendNum,String recvNum,String content){
		String result="";
		try{
			
			result = send(serviceURL, getDatas(unitCode, subjectCode,sendType,recvNum,content));
			
			/*
			 * 原来的用WEBSERVICE方式刷新
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(serviceURL));
			call.addParameter("unitCode", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("subjectCode", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sendType", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sendNum", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("recvNum", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("content", XMLType.XSD_STRING, ParameterMode.IN);			
			call.setOperationName("sendSm");
			call.setReturnType(XMLType.XSD_STRING);
			result = (String) call.invoke(new Object[]{unitCode,subjectCode,sendType,sendNum,recvNum,content});
			*/
		}catch(Exception e){
			e.printStackTrace();
			result=e.getMessage();
		}
		return result;
	}
	
	
	/**
	 * 发送及接收返回值
	 * <br> 说明：超时时间这里默认设为1分钟
	 * @param path				请求路径
	 * @param datas				发送的数据
	 * @param timeout			连接超时时间与读取超时间(此时间似乎没起作用)
	 * @return
	 */
	public static String send(String path, byte[] datas) throws Exception {
		return send(path, datas, TIME_OUT);
	}

	/**
	 * 发送及接收返回值
	 * @param path				请求路径
	 * @param datas				发送的数据
	 * @param timeout			连接超时时间与读取超时间(此时间似乎没起作用)
	 * @return
	 */
	public static String send(String path, byte[] datas, int timeout) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-type", "application/x-java-serialized-object;");
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.connect();
		
		// 发送：
		BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream());
		if (datas.length <= 1024 * 8) {
			bos.write(datas);
		} else {
			ByteArrayInputStream bais = new ByteArrayInputStream(datas);
			byte[] b = new byte[1024 * 4];
			int len;
			while ((len = bais.read(b)) != -1) {
				bos.write(b, 0, len);
			}
			bais.close();
		}
		bos.close();
		
		// 接收：
		BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024 * 4];
		int len;
		while ((len = bis.read(b)) != -1) {
			baos.write(b, 0, len);
		}
		baos.close();
		bis.close();
		
		return decode(baos.toByteArray());
	}
	
	
	
	/** 参数组装及加密： */
	private static byte[] getDatas(String ACCOUNT,String VALIDATE_CODE,String sendType,String phonenumbers,String content) throws Exception {
		
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		//把,替换成;
		if (phonenumbers==null){
			return null;
		}else{
			phonenumbers=phonenumbers.replaceAll(",", ";");
		}
		
		String xmlStr = "<root>"
				+ "<service_type>" + sendType + "</service_type>"
				+ "<timestamp>" + timestamp + "</timestamp>"
				+ "<account>" + ACCOUNT + "</account>"
				+ "<vilidate_code>" + VALIDATE_CODE + "</vilidate_code>"
				+ "<main>"
					+ "<rece_account>"+phonenumbers+"</rece_account>"
					+ "<content>"+content+"</content>"
				+ "</main>"
			+ "</root>";
		
		////System.out.println("=================发送"+content+"到"+phonenumbers);
		return new BASE64Encoder().encode(xmlStr.getBytes("UTF-8")).getBytes("UTF-8");
		
	}
	
	/** 解密及输出： */
	private static String decode(byte[] bytes) throws Exception {
		byte[] b = new BASE64Decoder().decodeBuffer(new String(bytes, "UTF-8"));
		String result=new String(b, "UTF-8");
		if (result!=null){
			result=result.replace("<root><code>", "");
			result=result.replace("</code></root>", "");
		}
		return result;
	}
	
	
	
	public static void main(String[] args){
		try {

			String content="屈文浩收短信123ffff";
			String recvNum="139222937257";        //接收号码
			//String value=SmsOpt.sendRealSm(recvNum, content);
			//System.out.println("发送测试，返回结果:"+recvNum.matches("[0-9]{11}"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
