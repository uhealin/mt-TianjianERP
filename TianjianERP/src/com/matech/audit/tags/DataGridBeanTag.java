package com.matech.audit.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.datagrid.MTDataGrid;

/**
 * <p>Title: 查询模块控件</p>
 * <p>Description: 查询模块控件，，
 * 用来输出数据table到jsp</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author k
 * 2007-6-8
 */
public class DataGridBeanTag extends BodyTagSupport {
	/**
	 * <p>Title: 查询模块控件</p>
	 * <p>Description: 查询模块控件，，
	 * 用来输出数据table到jsp</p>
	 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
	 * <p>Company: Matech  广州铭太信息科技有限公司</p>
	 *
	 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
	 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
	 *
	 * 贡献者团队:
	 *     铭太科技 - 研发中心，审计开发组
	 *
	 * @author k
	 * 2007-6-8
	 */
	private static final long serialVersionUID = 1000000001L;
//  property的名字
	private String name="";

//	输出数据的模式
	private String outputType="";

//  是否输出数据
	private String outputData="true";

//  只输出框架时，带的信息
	private String message="";
	
	private String isOldGrid = "false" ;

	public int doEndTag() {
		DataGridProperty pp;
//		String bodyContentStr="";
		try {

			JspWriter out = pageContext.getOut();
			HttpSession session = pageContext.getSession();
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			HttpServletResponse response = (HttpServletResponse) pageContext
					.getResponse();

//			bodyContentStr=this.bodyContent.getString();

			pp=(DataGridProperty)session.getAttribute(DataGrid.sessionPre+this.getName());
			
			if("true".equals(isOldGrid)) {
				MTDataGrid dg = new MTDataGrid(session, request, response, pp) ;
				if("true".equals(getOutputData())){
					if("invokeSearch".equals(outputType)){
						dg.printGridFrame(out, null);
						dg.invokeSearch(out); 
					}else{
						dg.printGrid(out);
					}

				}else{
					dg.printGridFrame(out, message);
				}
			}else {
				DataGrid dg=new DataGrid(session, request, response, pp);
				if("true".equals(getOutputData())){
					dg.printGrid(out);
					dg.invokeSearch(out);
				}else{
					dg.printGridFrame(out, message);
				}
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return SKIP_BODY;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}







	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}



	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}



	/**
	 * @return the outputData
	 */
	public String getOutputData() {
		return outputData;
	}



	/**
	 * @param outputData the outputData to set
	 */
	public void setOutputData(String outputData) {
		this.outputData = outputData;
	}



	public String getOutputType() {
		return outputType;
	}



	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}



	public String getIsOldGrid() {
		return isOldGrid;
	}



	public void setIsOldGrid(String isOldGrid) {
		this.isOldGrid = isOldGrid;
	}


}

