package com.matech.framework.pub.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
 

import com.matech.framework.listener.UserSession;






public class WebUtil {

	public final static String TAG_SCRIPT_START="<script type=\"text/javascript\">";
	public final static String TAG_SCRIPT_END="</script>";
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public WebUtil(HttpServletRequest request,HttpServletResponse response){
		this.request=request;
		this.response=response;
	}
	
	
	public String getWebRootPath(){

		return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();

	}
	
	public <T> T evalObject(T t){
		if(t==null)return t; 
		for(Iterator<String> it=request.getParameterMap().keySet().iterator();it.hasNext(); ){
			 String key=it.next();
			 Field field=null;
			 String seterName="set"+key.substring(0,1).toUpperCase()+key.substring(1);
			 Method method=null;
			 String value=request.getParameter(key);
			 if(value==null)continue;
			 try{
			   field= t.getClass().getDeclaredField(key);
			   method=t.getClass().getDeclaredMethod(seterName,field.getType());
			   if(field.getType().equals(String.class)){
				   method.invoke(t, value);
			   }else if(field.getType().equals(Integer.class)){
				   
				   method.invoke(t, Integer.parseInt(value));
			   }else if(field.getType().equals(Float.class)){
				   method.invoke(t, Float.parseFloat(value));
			   }else if(field.getType().equals(Double.class)){
				   method.invoke(t, Double.parseDouble(value));
			   }
			 }catch (Exception e) {
				// TODO: handle exception
			}
			 
	    }
		return t;
	}
	
	public  <T> T evalObject(Class<T> cls) throws Exception{
		return evalObject(cls.newInstance() );
	}
	
	public boolean alert(String msg){
		StringBuffer context=new StringBuffer("");
		context.append(TAG_SCRIPT_START);
		context.append("alert(\""+msg+"\");");
		context.append(TAG_SCRIPT_END);
		try {
			response.getWriter().write(context.toString());
			response.flushBuffer();
			
		} catch (IOException e) {return false;}
		return true;
	}
	
	public boolean confirm(String msg,String scriptYes,String scriptNo){
		StringBuffer context=new StringBuffer("");
		context.append(TAG_SCRIPT_START)
		.append("if(confirm(\""+msg+"\")")
		.append("'{' "+scriptYes+" '}'")
		.append("else '{' "+scriptNo+" '}'")
		.append(TAG_SCRIPT_END);
		try {
			response.getWriter().write(context.toString());
		} catch (IOException e) {return false;}
		return true;
	}
	
	public boolean alert(Exception ex){
		return this.alert(MessageFormat.format("程序异常:{0}", ex.getLocalizedMessage()));
	}
	
	public UserSession getUserSession() throws NullPointerException{
		
		Object obj=request.getSession().getAttribute("userSession");
		UserSession userSession=new UserSession();
		
			userSession=(UserSession)obj;
		return userSession;
	}
	
	public static final String CONTENT_TYPE_UTF8="text/html;charset=utf-8";
	
	public static String DoGet(HttpGet get){
		DefaultHttpClient httpclient=new DefaultHttpClient();
		String context="";
		HttpResponse response=null;
		InputStream is=null;
		
		try {
		    response = httpclient.execute(get);
		    is = response.getEntity().getContent();
			context=StreamToString(is);
			httpclient.clearRequestInterceptors();
            httpclient.clearResponseInterceptors();
			is.close();
			//resultModel.setCode(ResultModel.CODE_SUCCESS);
			//resultModel.setMsg(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		    context=e.getLocalizedMessage();
			//resultModel.setCode(ResultModel.CODE_ERROR);
			
			//resultModel.setMsg(context);
		}
		return context;
	}
	
	public static String DoPost(HttpPost post){
		DefaultHttpClient httpclient=new DefaultHttpClient();
		String context="";
		HttpResponse response=null;
		InputStream is=null;
		
		try {
		    response = httpclient.execute(post);
		    is = response.getEntity().getContent();
			context=StreamToString(is);
			httpclient.clearRequestInterceptors();
            httpclient.clearResponseInterceptors();
			is.close();
			//resultModel.setCode(ResultModel.CODE_SUCCESS);
			//resultModel.setMsg(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		    context=e.getLocalizedMessage();
			//resultModel.setCode(ResultModel.CODE_ERROR);
			
			//resultModel.setMsg(context);
		}
		return context;
	}
	
	public static String DoPost(String url,Map<String, Object> params, Map<String, File> postFiles){
		List<NameValuePair> nvps=new ArrayList<NameValuePair>();
		for(Iterator<String> it=params.keySet().iterator();it.hasNext();){
			String key=it.next();
			Object object=params.get(key).toString();
			if(object==null)continue;
			nvps.add(new BasicNameValuePair(key, object.toString()));
		}
	   return DoPost(url, nvps, postFiles);
	}
	
	public static String DoPost(String url,List<NameValuePair> nvps, Map<String, File> postFiles){
		
		HttpPost httpPost=new HttpPost(url);
		String responseText="";
		MultipartEntity entity = new MultipartEntity();
		
		for(NameValuePair nvp:nvps){
			try {
				String encodeVal=encode(nvp.getValue(),"utf8");
				StringBody stringBody=new StringBody(encodeVal);
				entity.addPart(nvp.getName(),stringBody);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(Iterator<String> it=postFiles.keySet().iterator();it.hasNext();){
			String key=it.next();
			File file=postFiles.get(key);
			if(!file.exists()){continue;}
			FileBody fb = new FileBody(file);
			entity.addPart(key,fb );
		}
		
			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			
			httpPost.setEntity(entity);
			return DoPost(httpPost);
	
		
		
	}
	
	
	   public static String StreamToString(InputStream is){
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
		    StringBuilder sb = new StringBuilder(); 
		    String line = null; 
		    try 
		    { 
		      while ((line = reader.readLine()) != null) 
		      { 
		        sb.append(line); 
		      } 
		    } 
		    catch (IOException e) 
		    { 
		      e.printStackTrace(); 
		    } 
		    finally 
		    { 
		      try 
		      { 
		        is.close(); 
		      } 
		      catch (IOException e) 
		      { 
		        e.printStackTrace(); 
		      } 
		    } 
		    return sb.toString(); 
		}
	   
	   public static String encode(String value,String encode){
			String lStrReturn="";
			if(value==null)return lStrReturn;
			try {
				lStrReturn =URLEncoder.encode(value,encode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return lStrReturn;
		}
	   
	   public String getPreUrl(){
		   Object obj=request.getSession().getAttribute("url");
		   if(obj==null){
			   try {
				Thread.sleep(2000);
				obj=request.getSession().getAttribute("url");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   
		   }
		   return obj==null?"#":obj.toString();
	   }
	   
	   public void setPreUrl(){
		   setPreUrl(request.getParameter("url"));
	   }
	   
	   public void setPreUrl(String url){
		    request.getSession().setAttribute("url",url);
	   }
	   
	   public static String httpPost(String url, Map<String, String> params) {
		   URL u = null;
		   HttpURLConnection con = null;
		   //构建请求参数
		   StringBuffer sb = new StringBuffer();
		   if(params!=null){
		   for (Entry<String, String> e : params.entrySet()) {
		   sb.append(e.getKey());
		   sb.append("=");
		   sb.append(e.getValue());
		   sb.append("&");
		   }
		   sb.substring(0, sb.length() - 1);
		   }
		   System.out.println("send_url:"+url);
		   System.out.println("send_data:"+sb.toString());
		   //尝试发送请求
		   try {
		   u = new URL(url);
		   con = (HttpURLConnection) u.openConnection();
		   con.setRequestMethod("POST");
		   con.setDoOutput(true);
		   con.setDoInput(true);
		   con.setUseCaches(false);
		   con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		   OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
		   osw.write(sb.toString());
		   osw.flush();
		   osw.close();
		   } catch (Exception e) {
		   e.printStackTrace();
		   } finally {
		   if (con != null) {
		   con.disconnect();
		   }
		   }
		    
		   //读取返回内容
		   StringBuffer buffer = new StringBuffer();
		   try {
		   BufferedReader br = new BufferedReader(new InputStreamReader(con
		   .getInputStream(), "UTF-8"));
		   String temp;
		   while ((temp = br.readLine()) != null) {
		   buffer.append(temp);
		   buffer.append("\n");
		   }
		   } catch (Exception e) {
		   e.printStackTrace();
		   }
		    
		   return buffer.toString();
		   }
}
