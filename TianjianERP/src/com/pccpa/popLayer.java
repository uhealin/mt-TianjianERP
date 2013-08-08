package com.pccpa;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.matech.framework.listener.UserSession;

public class popLayer {

	private final String chatUrl = "/erp/login.do";
	
	/**
	 * @description 获取当前登陆用的ID(员工编号) 
	 * @return
	 */
	private String getCurrUserIdByHttpSession(){
		WebContext wctx = WebContextFactory.get();
		HttpSession httpSession = wctx.getSession();
		UserSession userSession = (UserSession)httpSession.getAttribute("userSession");
		if(userSession==null) return "-1";
		else return userSession.getUserId();
	}
	
	public void setScriptSessionMark(){
		String currUserId = getCurrUserIdByHttpSession();
		WebContext wctx = WebContextFactory.get();
		ScriptSession thisSession = wctx.getScriptSession();
		if (thisSession.getAttribute("userid") == null){
			thisSession.setAttribute("userid", currUserId); 
		}
		//删除已失效ScriptSession
		String currentPage=chatUrl;
		
		Collection pages = wctx.getScriptSessionsByPage(currentPage);
		for (Iterator it = pages.iterator(); it.hasNext();) {
			ScriptSession tmpSession = (ScriptSession) it.next();
			String userString=String.valueOf(tmpSession.getAttribute("userid"));
			if(userString.equals("null")){
				tmpSession.invalidate();
				continue;
			}
			if(userString.equals(String.valueOf(currUserId))){
				if(!thisSession.getId().equals(tmpSession.getId())){
					tmpSession.invalidate();
					continue;
				}
			}
		}
	}
	
	public static String codingString(String str) {
		try {
			str = java.net.URLEncoder.encode(str,"UTF-8");
			str = str.replace("+", "%20");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	
	public void thisPopLayer(String senderName,String content,String currUserId){
		senderName=codingString(senderName);
		content=codingString(content);
		
		WebContext wctx = WebContextFactory.get();
		String currentPage = chatUrl;
		ScriptBuffer script = new ScriptBuffer();
		Collection pages = wctx.getScriptSessionsByPage(currentPage);
		
		script.appendScript("popNewMsgLayer('"+senderName+"','"+content+"');");
		for (Iterator it = pages.iterator(); it.hasNext();) {
			ScriptSession otherSession = (ScriptSession) it.next();
			String markUser = String.valueOf(otherSession.getAttribute("userid"));
			if(currUserId.equals(markUser)){
				otherSession.addScript(script);
			}
		}
	}
	
}
