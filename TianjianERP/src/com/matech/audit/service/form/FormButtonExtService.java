package com.matech.audit.service.form;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.form.model.FormButton;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;

public class FormButtonExtService {
	public String handler(String btnId, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();

			FormButton formButton = new FormQueryConfigService(conn)
					.getFormButtonByUuid(btnId);

			String className = formButton.getClassName();

			if (className == null || "".equals(className)) {
				throw new Exception("未定义JAVA接口!");
			}

			Class<?> clazz = Class.forName(className);
			FormButtonExtInterface buttonExt = (FormButtonExtInterface) clazz
					.newInstance();

			return buttonExt.handle(conn,formButton, request,response);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			DbUtil.close(conn);
		}
	}

	public static StringBuffer createButtonExtjs(String formId, String context, String buttonType) {
	   return createButtonExtjs(formId, context, buttonType,null);
	}
	
	/**
	 * 通用按钮生成
	 * 
	 * @param formId
	 * @param context
	 * @return
	 */
	public static StringBuffer createButtonExtjs(String formId, String context, String buttonType,HttpServletRequest request) {

		Connection conn = null;
		StringBuffer strExt = new StringBuffer();
		List<FormButton> list = new ArrayList<FormButton>();
		String[] arrHiddenBtn=new String[]{};
		String hiddenBtn="";
        String menuid=null;
        if(request!=null){
        menuid=request.getParameter("menuid");
        }
		menuid=menuid==null?"":menuid;
		if(request!=null){
			hiddenBtn=request.getParameter("hiddenBtn");
			if(hiddenBtn!=null&&!hiddenBtn.isEmpty()){
				arrHiddenBtn=hiddenBtn.split(",");
			}
		}
		try {
			conn = new DBConnect().getConnect();
			FormQueryConfigService formQueryConfigService = new FormQueryConfigService(
					conn);
			list = formQueryConfigService.getFormButton(formId, buttonType);
            
			for (int i = 0; i < list.size(); i++) {
				
				
				FormButton formButton = list.get(i);
                boolean isHidden=false;
				for(int j=0;j<arrHiddenBtn.length;j++){
					   try{
					   if(arrHiddenBtn[j].trim().equals(formButton.getEnname().trim())){
						  isHidden=true;break;  
					   }}catch(Exception ex){}
					}
				if(isHidden)continue;
				strExt.append("{text:'");
				strExt.append(formButton.getName());
				strExt.append("', \n ");
				strExt.append("cls:'x-btn-text-icon', \n ");
				strExt.append("icon:'"); 
				strExt.append(context);
				strExt.append("/img/");
				strExt.append(formButton.getIcon());
				strExt.append("',");
				
				strExt.append("handler:function(){ \n ");

				if(formButton.getBeforeClick() != null && !"".equals(formButton.getBeforeClick())) {
					strExt.append(" var flag = ").append(formButton.getBeforeClick()).append(" \n ");
					strExt.append(" if(!flag) { \n ");
					strExt.append(" 	return; \n ");
					strExt.append(" } \n ");
				}

				if ("1".equals(formButton.getHandleType())) {
					
					if("1".equals(formButton.getButtonType())) {
						//表单按钮
						strExt.append("mt_form_formBtn_CallJava('").append(formButton.getUuid()).append("'); \n ");
						
					} else {
						//列表按钮
						strExt.append("mt_form_listBtn_callJava('")
							.append(FormDefineService.getDataGridId(formId)+"_"+menuid)
							.append("','").append(formButton.getUuid())
							.append("'); \n ");
					}
					
				} else {
					// JS处理
					strExt.append(formButton.getOnclick()).append("\n ");
				}
				
				if(formButton.getAfterClick() != null && !"".equals(formButton.getAfterClick())) {
					strExt.append(formButton.getAfterClick()).append("\n ");
				}
				
				strExt.append("}},").append("\n ");

				if (formButton.getAftergroup() == 1) { // 1的话会在按钮后显�?| 分隔
					strExt.append("'-',");
				}
			}

			strExt.append("''"); // Extjs拼写结束
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return strExt;
	}

	/**
	 * 通用按钮的扩展函数生�?
	 * 
	 * @param formId
	 * @return
	 */
	public static StringBuffer createButtonExtjsExt(String formId, String buttonType) {
		Connection conn = null;
		StringBuffer strExt = new StringBuffer();
		List<FormButton> list = new ArrayList<FormButton>();
		try {
			conn = new DBConnect().getConnect();
			FormQueryConfigService updateFormService = new FormQueryConfigService(
					conn);
			list = updateFormService.getFormButton(formId, buttonType);
			for (int i = 0; i < list.size(); i++) {
				String extjs = list.get(i).getExtjs();
				if (extjs != null) {
					strExt.append(extjs).append("\n"); // 获取扩展函数
				}

				String beforeClickJs = list.get(i).getBeforeClickJs();
				if (beforeClickJs != null) {
					strExt.append(beforeClickJs).append("\n");// 获取扩展函数
				}

				String afterClickJs = list.get(i).getAfterClickJs();
				if (afterClickJs != null) {
					strExt.append(afterClickJs).append("\n"); // 获取扩展函数
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return strExt;
	}
	
	
	public static StringBuffer createButtonExtjs(String formId, String context, String buttonType,HttpServletRequest request,String whereHideButton) {

		Connection conn = null;
		StringBuffer strExt = new StringBuffer();
		List<FormButton> list = new ArrayList<FormButton>();
		String[] arrHiddenBtn=new String[]{};
		String hiddenBtn="";
        String menuid=null;
       
        
        if(request!=null){
        menuid=request.getParameter("menuid");
        
        }
		menuid=menuid==null?"":menuid;
		if(request!=null){
			
			hiddenBtn=request.getParameter("hiddenBtn");
			hiddenBtn=hiddenBtn==null?"":hiddenBtn;
			hiddenBtn=StringUtil.trim(hiddenBtn, ",");
			whereHideButton=whereHideButton==null?"":whereHideButton;
			hiddenBtn+=","+whereHideButton;
			if(hiddenBtn!=null&&!hiddenBtn.isEmpty()){
				arrHiddenBtn=hiddenBtn.split(",");
			}
		}
		try {
			conn = new DBConnect().getConnect();
			FormQueryConfigService formQueryConfigService = new FormQueryConfigService(
					conn);
			list = formQueryConfigService.getFormButton(formId, buttonType);
            
			for (int i = 0; i < list.size(); i++) {
				
				
				FormButton formButton = list.get(i);
                boolean isHidden=false;
				for(int j=0;j<arrHiddenBtn.length;j++){
					   try{
					   if(arrHiddenBtn[j].trim().equals(formButton.getEnname().trim())){
						  isHidden=true;break;  
					   }}catch(Exception ex){}
					}
				if(isHidden)continue;
				strExt.append("{text:'");
				strExt.append(formButton.getName());
				strExt.append("', \n ");
				strExt.append("cls:'x-btn-text-icon', \n ");
				strExt.append("icon:'"); 
				strExt.append(context);
				strExt.append("/img/");
				strExt.append(formButton.getIcon());
				strExt.append("',");
				
				strExt.append("handler:function(){ \n ");

				if(formButton.getBeforeClick() != null && !"".equals(formButton.getBeforeClick())) {
					strExt.append(" var flag = ").append(formButton.getBeforeClick()).append(" \n ");
					strExt.append(" if(!flag) { \n ");
					strExt.append(" 	return; \n ");
					strExt.append(" } \n ");
				}

				if ("1".equals(formButton.getHandleType())) {
					
					if("1".equals(formButton.getButtonType())) {
						//表单按钮
						strExt.append("mt_form_formBtn_CallJava('").append(formButton.getUuid()).append("'); \n ");
						
					} else {
						//列表按钮
						strExt.append("mt_form_listBtn_callJava('")
							.append(FormDefineService.getDataGridId(formId)+"_"+menuid)
							.append("','").append(formButton.getUuid())
							.append("'); \n ");
					}
					
				} else {
					// JS处理
					strExt.append(formButton.getOnclick()).append("\n ");
				}
				
				if(formButton.getAfterClick() != null && !"".equals(formButton.getAfterClick())) {
					strExt.append(formButton.getAfterClick()).append("\n ");
				}
				
				strExt.append("}},").append("\n ");

				if (formButton.getAftergroup() == 1) { // 1的话会在按钮后显�?| 分隔
					strExt.append("'-',");
				}
			}

			strExt.append("''"); // Extjs拼写结束
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return strExt;
	}
	
}
