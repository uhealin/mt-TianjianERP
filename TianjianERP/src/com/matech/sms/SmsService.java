package com.matech.sms;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;

import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

import cn.emay.sdk.client.api.Client;

public class SmsService {
	private static Client client = null;
	private static String password = null;
	private Connection conn;
	private DbUtil dbUtil;
	
	public SmsService(Connection conn) {
		this.conn=conn;
		try {
			dbUtil=new DbUtil(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化SDK客户端
	 * @return
	 */
	private synchronized static Client getClient() {
		ResourceBundle bundle = PropertyResourceBundle.getBundle("config");
		if (client == null) {
			try {
				client = new Client(bundle.getString("softwareSerialNo"),
						bundle.getString("key"));
				
				password = bundle.getString("password");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}

	/**
	 * 注册和激活序列号
	 * @return
	 */
	public synchronized static String registEx() {
		String result = "";
		int returnCode = SmsService.getClient().registEx(password);
		
		switch(returnCode) {
			case 0:
				result = "注册成功";
				break;
				
			case 10:
				result = "客户端注册失败";
				break;
				
			case 101:
				result = "客户端网络故障";
				break;
				
			case 305:
				result = "服务器端返回错误，错误的返回值（返回值不是数字字符串）";
				break;
				
			case 999:
				result = "操作频繁";
				break;
			
			default:
				result = "注册异常：" + returnCode;
				break;
			
		}

		return result;
	}
	
	/**
	 * 注销序列号
	 * @return
	 */
	public synchronized static String logout() {
		
		String result = "";
		int returnCode = SmsService.getClient().logout();
		
		switch(returnCode) {
			case 0:
				result = "注销成功";
				break;
				
			case 22:
				result = "注销失败";
				break;
				
			case 101:
				result = "客户端网络故障";
				break;
				
			case 305:
				result = "服务器端返回错误，错误的返回值（返回值不是数字字符串）";
				break;
				
			case 999:
				result = "操作频繁";
				break;
			
			default:
				result = "注册异常：" + returnCode;
				break;
			
		}

		return result;
	}
	
	/**
	 * 查询余额
	 * @return
	 */
	public synchronized static String getBalance() {
		String result = "";
		try {
			result = String.valueOf(SmsService.getClient().getBalance());
		} catch (Exception e) {
			result = "查询余额异常：" + e.getMessage();
		}
		return result;
	}
	
	
	/**
	 * 发送即时短信
	 * @param mobiles  手机号码(群发为逗号分隔，推荐最多为200个手机号码或以内)
	 * @param smsContent 短信内容(最多500个汉字或1000个纯英文，emay服务器程序能够自动分割；
	 * @return
	 */
	public synchronized static String sendSMS(String mobiles, String smsContent) {
		
		return sendSMS(mobiles, smsContent,null, 3);
	}
	
	/**
	 * 发送即时短信
	 * @param mobiles  手机号码(群发为逗号分隔，推荐最多为200个手机号码或以内)
	 * @param smsContent 短信内容(最多500个汉字或1000个纯英文，emay服务器程序能够自动分割；
	 * @param addSerial 扩展号 (长度小于15的字符串) 用户可通过扩展号自定义短信类别
	 * @return
	 */
	public synchronized static String sendSMS(String mobiles, String smsContent, int smsPriority) {
		
		return sendSMS(mobiles, smsContent,null, smsPriority);
	}
	
	/**
	 * 发送即时短信（带扩展号）
	 * @param mobiles  手机号码(群发为逗号分隔，推荐最多为200个手机号码或以内)
	 * @param smsContent 短信内容(最多500个汉字或1000个纯英文，emay服务器程序能够自动分割；
	 * @param addSerial 扩展号 (长度小于15的字符串) 用户可通过扩展号自定义短信类别
	 * @param smsPriority 优先级(级别从1到5的正整数，数字越大优先级越高，越先被发送)
	 * @return
	 */
	public synchronized static String sendSMS(String mobiles, String smsContent, String addSerial, int smsPriority) {
		
		int returnCode = -1;
		
		if(mobiles != null && mobiles.trim().length() != 0) {
			
			String[] mobile = mobiles.split(",");

			if(addSerial != null) {
				returnCode = SmsService.getClient().sendSMS(mobile, smsContent,addSerial, smsPriority);
			} else {
				returnCode = SmsService.getClient().sendSMS(mobile, smsContent, smsPriority);
			}
	
		}
		
		return getReturnValue(returnCode);
	}
	
	/**
	 * 返回值
	 * @param returnCode
	 * @return
	 */
	private synchronized static String getReturnValue(int returnCode) {
		String result = "无返回值";
		switch (returnCode) {
			case -1:
				result = "手机号不能为空";
				break;
			
			case 0:
				result = "短信发送成功";
				break;
				
			case 17:
				result = "发送信息失败";
				break;
				
			case 18:
				result = "发送定时信息失败";
				break;
				
			case 101:
				result = "客户端网络故障";
				break;
				
			case 305:
				result = "服务器端返回错误，错误的返回值（返回值不是数字字符串）";
				break;
	
			case 307:
				result = "目标电话号码不符合规则，电话号码必须是以0、1开头";
				break;
				
			case 997:
				result = "平台返回找不到超时的短信，该信息是否成功无法确定";
				break;
				
			case 998:
				result = "由于客户端网络问题导致信息发送超时，该信息是否成功下发无法确定";
				
			default:
				result = "发送信息异常:" + returnCode;
				break;
		}
		
		return result;
	}
	
	/**
	 * 短信日志
	 * @param sms
	 * @throws Exception 
	 */
	public static void addLog(Sms sms) throws Exception{
		
		PreparedStatement ps = null;
		Connection conn = null;
		String sql = "INSERT INTO `asdb`.`k_sms` \n"
		            +"(`fsUserId`,`content`,`jsUserId`,`state`,`jsMobilePhone`,`smsPriority`,`perproty`,createDate) \n"
		            +"VALUES (?,?,?,?,?,?,?,now() );";
		try {
			
			int i = 1;
			conn = new DBConnect().getConnect();
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, sms.getFsUserId());
			ps.setString(i++, sms.getContent());
			ps.setString(i++, sms.getJsUserId());
			ps.setString(i++, sms.getState());
			ps.setString(i++, sms.getJsMobilePhone());
			ps.setString(i++, sms.getSmsPriority());
			ps.setString(i++, sms.getPerproty());
			
			ps.execute();
			
		} catch (SQLException e) {
			System.out.println("新增短信日志出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
	}
	
	public SmsVO createSms(String mobile, String context,String unKey){
		SmsVO smsVO=new SmsVO();
		//smsVO.setClient_num(clientNo);
		smsVO.setContext(context);
		smsVO.setMobile(mobile);
		//smsVO.setRemark(remark);
		//smsVO.setSend_date(StringUtil.getCurDate());
		smsVO.setUuid(UUID.randomUUID().toString());
		//smsVO.setSend_time(StringUtil.getCurDateTime());
		smsVO.setCreate_time(StringUtil.getCurDateTime());
		//smsVO.setHost_ip(InetAddress.getLocalHost().getHostAddress());
		smsVO.setState("s");
		smsVO.setUuid(UUID.randomUUID().toString());
		smsVO.setUn_key(unKey);
		try {
			dbUtil.insert(smsVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return smsVO;
	}

	public String sendSms(String userId, String userIds, String mobiPhone,
			String content) {
		// TODO Auto-generated method stub
		return null;
	}
}
