package com.matech.audit.service.net;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
public class NetService {




		/*
		 * 判断地址是否可以访问
		 */
		public boolean canAccess(String urlAddress){
		    try{
			      URL url=new URL(urlAddress);
			      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			      return true;
			    } catch (Exception e) {
			      System.out.println("连接"+urlAddress+"失败 \n");
			      e.printStackTrace();
			      return false;
			    } 
			    
			
		}
		
		
		/*
		 * 获得指定地址的html
		 */
		public String getUrlHtml(String urlAddress){
			   String weather="";
				String weatherAll="暂时无法访问该网页"+urlAddress;
			    try{
			      URL url=new URL(urlAddress);
			      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			        String str="";
			        while ((str = in.readLine()) != null) {
			            weather+=str;
			        }
			        in.close();
					weatherAll=weather;


			    } catch (Exception e) {
			      e.printStackTrace();
				  weather=weatherAll;
			    } 
			    
			    return weatherAll;
		}
		/*
		 * 获得E审通的外部访问地址
		 */
		public String getEstOuterUrl(String url){
			url="http://"+url+":5199/AuditSystem/AS_SYSTEM/";
			return url;
		}
		public static void main(String[] args){
			NetService w = new NetService();
			String HTML=w.getUrlHtml("http://www.21cn.com");
			System.out.println(HTML);
		}
		/*
		 * 生成密码key
		 * 先生成一个8位的随机数据 得到变量a
		 * 前2位取asc码相加，取5的模  再+5。得到变量x
		 * 把后六位用作java md5 取前x位，得到变量y
		 * 最后key  为 a+y
		 */
		public String getWebKey(){
			return "";
		}
		
		public boolean validateWebKey(){
			return false;
		}
		
		//得到一个指定长度的随机字符串
		public String getRanDom(int length){
			
			for(int i=0;i<length;i++){
				
			}
			
			return "";
		}
	}
