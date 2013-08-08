package com.matech.audit.work.uploadProcess;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import java.util.Queue;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.framework.pub.util.ASFuntion;

public class FrontProcessAction extends MultiActionController {
	
	public static Map map =  new HashedMap() ;
	
	public static void printMessage(String key,String message) {
		
		Queue<String> queue = (Queue<String>)map.get(key);
		
		if (queue==null){
			 queue=new LinkedList<String>();
		}
		 
		queue.offer(message);
	     
		map.put(key,queue);
		System.out.println("插入："+message);
	}	
	
	public static String getMessage(String key){
		
		Queue<String> queue = (Queue<String>)map.get(key);
		String result="";
		if (queue!=null){
			result=(String)queue.poll();
			if (queue.size()==0){
				map.put(key, null);
			}
		}
		return result;
	}
	
	//清除
	public static void clearMessage(String key){
		Queue<String> queue = (Queue<String>)map.get(key);
		String result="";
		if (queue!=null){
			queue.clear();
			map.put(key, null);
		}
	}
	
	//初始化时清空之前没有订阅完的消息
	public void initMessage(HttpServletRequest request,HttpServletResponse response) {
	 	
		ASFuntion ASF = new ASFuntion() ;
		PrintWriter out = null ;
	    
		String key = ASF.showNull(request.getParameter("key")) ;
		
		try {
			
			response.setContentType("text/html;charset=utf-8");
	        response.setHeader("Cache-Control", "no-cache"); 
			
			out = response.getWriter() ;
			
			if(map != null) { 
				 clearMessage(key);
			}
			
		} catch (IOException e) {
			out.write("请等待...") ;
		}finally {
			if(out != null)	{
				out.close() ;
			}
		}
	 }
	
	//前台订阅消息
	public void getMessage(HttpServletRequest request,HttpServletResponse response) {
		 	
		ASFuntion ASF = new ASFuntion() ;
		PrintWriter out = null ;
	    
		String key = ASF.showNull(request.getParameter("key")) ;
		
		try {
			
			response.setContentType("text/html;charset=utf-8");
	        response.setHeader("Cache-Control", "no-cache"); 
			
			out = response.getWriter() ;
			
			if(map != null) { 
				 out.write(getMessage(key)) ;
			}else {
				out.write("请等待...")  ;
			}
			
		} catch (IOException e) {
			out.write("请等待...") ;
		}finally {
			if(out != null)	{
				out.close() ;
			}
		}
	 }
	 
	 public void test(HttpServletRequest request,HttpServletResponse response) {
		 	
		 	FrontProcessAction.printMessage("key","显示信息11111...") ;
System.out.println("++++++++++++++++++++++sleep1++++++++++++++++");	 	
		 	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 	
		 	FrontProcessAction.printMessage("key","显示信息22222...") ;
System.out.println("++++++++++++++++++++++sleep2++++++++++++++++");
		 	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
System.out.println("++++++++++++++++++++++sleep3++++++++++++++++");		 
			FrontProcessAction.printMessage("key","显示信息33333...") ;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			FrontProcessAction.printMessage("key","end") ;
	 }
	 
	 
	 public static void main(String[] args) {
	        Queue<String> queue = new LinkedList<String>();
	        queue.offer("张三");
	        queue.offer("李四");
	        queue.offer("王五");
	        String str;
	        System.out.println(queue.size());
	        while((str = queue.poll()) != null){
	           System.out.print(str);
	        }
	        System.out.println();
	        System.out.println(queue.size());
	    }

}
