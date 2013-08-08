/**
 * 
 */
package com.pccpa;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateChatUrl { 
	private static final String RESERVED_WORDS = "panchina";
	private static String host="localhost";
	private static String port="8080";
	
	/**
	 * 获取url
	 * @param id
	 * @return
	 */
	public String getChatUrl(String id) {
		String checkCode = null;
		try {
			checkCode = getMD5String(id + "|" + getServerTime() + "|" +RESERVED_WORDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "<iframe src='http://" + host + ":" + port + "/ChatSys/loginCheck.action?id=" + id + "&checkCode=" + checkCode + 
				"' width='100%' height='100%'>" + 
				"</iframe>";
	}
	
	/**
	 * 系统时间
	 * @return
	 */
	private static String getServerTime(){
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
	/**
	 * MD5加密
	 * @param str
	 * @return
	 * @throws Exception
	 */
	private static String getMD5String(String str) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("md5");
		byte[] buf = digest.digest(str.getBytes());
		StringBuffer returnValue = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			returnValue.append(Integer.toHexString(buf[i] & 0XFF));
		}
		return returnValue.toString();
	}
	
	/*public static void main(String[] args) {
		GenerateChatUrl chatSys = new GenerateChatUrl ();
		System.out.println(chatSys.getChatUrl("59899"));
	}*/
}
