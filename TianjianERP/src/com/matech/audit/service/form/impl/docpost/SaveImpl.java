package com.matech.audit.service.form.impl.docpost;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.doc.DocPostSchedule;
import com.matech.audit.service.doc.DocPostService;
import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.audit.service.doc.model.DocLogVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.form.FormExtInterface;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class SaveImpl implements FormExtInterface {

	@Override
	public String beforeAdd(Connection conn, String formId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
	
		return null;
	}

	@Override
	public String afterAdd(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		DbUtil dbUtil=new DbUtil(conn);
		DocPostService docPostService=new DocPostService(conn);
		DocPostVO docPostVO=dbUtil.load(DocPostVO.class, dataUUID);
		WebUtil webUtil=new WebUtil(req, res);
		UserSession userSession=webUtil.getUserSession();
		//docPostVO.setDoc_seq(docPostService.getNextSeqNo());
		//docPostVO.setDoc_no(MessageFormat.format("{0}-{1}", docPostVO.getDoc_type(),String.valueOf(docPostVO.getDoc_seq())));
		
		if(StringUtil.isIn(docPostVO.getNode_code(),new String[]{Node.fq.name()})&& "a".equals(docPostVO.getCtype())){
			docPostVO.setNode_code(Node.hq.name());
			docPostVO.setNode_remark(MessageFormat.format("{0} 进入会签", StringUtil.getCurDateTime()));

			   if(StringUtil.isBlank(docPostVO.getCountersigner_ids())&&StringUtil.isBlank(docPostVO.getSignissuer_ids())){
						docPostVO.setNode_code(Node.hy.name());
						docPostVO.setNode_remark(MessageFormat.format("{0} 无会签人和签发人，直接进入核阅", StringUtil.getCurDateTime()));
			  }else if(StringUtil.isBlank(docPostVO.getCountersigner_ids())){
				docPostVO.setNode_code(Node.qf.name());
				docPostVO.setNode_remark(MessageFormat.format("{0} 无会签人，直接进入签发", StringUtil.getCurDateTime()));
			}
		}else if(StringUtil.isIn(docPostVO.getNode_code(),new String[]{Node.fq.name()})&&"b".equals(docPostVO.getCtype())){
			  docPostVO.setNode_code(Node.xm.name());
			  docPostVO.setNode_remark(MessageFormat.format("{0} 进入相关人员确认", StringUtil.getCurDateTime()));
			   if(StringUtil.isBlank(docPostVO.getProject_member_ids())&&StringUtil.isBlank(docPostVO.getCountersigner_ids())&&StringUtil.isBlank(docPostVO.getSignissuer_ids())){
					docPostVO.setNode_code(Node.hy.name());
					docPostVO.setNode_remark(MessageFormat.format("{0} 无会签人和签发人，直接进入核阅", StringUtil.getCurDateTime()));
				}
			   else if(StringUtil.isBlank(docPostVO.getProject_member_ids())&&StringUtil.isBlank(docPostVO.getCountersigner_ids())){
					docPostVO.setNode_code(Node.qf.name());
					docPostVO.setNode_remark(MessageFormat.format("{0} 无会签人，直接进入签发", StringUtil.getCurDateTime()));
				}
			   else if(StringUtil.isBlank(docPostVO.getProject_member_ids())){
					docPostVO.setNode_code(Node.hq.name());
					docPostVO.setNode_remark(MessageFormat.format("{0} 进入会签", StringUtil.getCurDateTime()));

				}
		
				
			}
		
		docPostVO=docPostService.updateSignInfoContext(docPostVO, Node.hq);
		//docPostVO=docPostService.updateSignInfoContext(docPostVO, Node.xm);
		docPostVO=docPostService.updateSignInfoContext(docPostVO, Node.qf);
		docPostVO=docPostService.updateSignInfoContext(docPostVO, Node.hy);
		new DocPostSchedule().handlerTimeout(docPostVO);
		int i= dbUtil.update(docPostVO);
		docPostService.remaindStart(docPostVO, userSession);
		//if(i==1){
		//return MessageFormat.format("文号 {0} 新增成功", docPostVO.getDoc_no());
		//} 		
		return null;
	}

	@Override
	public String beforeUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return afterAdd(conn, formId, dataUUID, req, res);
	}

	@Override
	public String beforeDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beforeView(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res,
			ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		String hq_html="";
		
		DbUtil dbUtil=null;
		DocPostVO docPostVO=null;
		boolean isView="true".equals(req.getParameter("view"));
		try{
			dbUtil=new DbUtil(conn);

			if(!StringUtil.isBlank(dataUUID)){
				docPostVO=dbUtil.load(DocPostVO.class, dataUUID);
				if(StringUtil.isBlank(docPostVO.getCountersigner_ids())){
					return;
				}
				String[] arr_hq_ids=docPostVO.getCountersigner_ids().split(",");
			for(int i=0;i<arr_hq_ids.length;i++){
				String hq_id=arr_hq_ids[i];
				UserVO userVO=dbUtil.load(UserVO.class, hq_id);
				StringBuffer context=new StringBuffer();
				List<DocLogVO> docLogVOs= dbUtil.select(DocLogVO.class, "select * from {0} where handler_id=? and doc_id=? and node_code=?", hq_id,docPostVO.getUuid(),Node.hq.name());
				
				String liId="liId_"+i,inputId="inputHq_"+i;
				String removeClick="\\$('#"+liId+"').remove();reflashHqName();";
				boolean isReadonly=false;
				if(docLogVOs.size()>0){
					DocLogVO docLogVO=docLogVOs.get(0);
					String confirmMsg=MessageFormat.format("{0} 于  {1} 进行过会签,确认删除该会签人？",docLogVO.getHandler_name() ,docLogVO.getHandle_time());
				    removeClick="if(confirm('"+confirmMsg+"')){"+removeClick+"}";
				    isReadonly=true;
				}
				if(isView){
					context.append("<li>").append(userVO.getName()).append(isReadonly?"(已签)":"(未签)").append("</li>");
				}else{
				context
				.append("<li id='"+liId+"'>")
				.append("<div class='divHqName'>"+(i+1)+"级复核人</div>")
				.append("<input class='inputHq "+(isReadonly?"readonly":"")+"' id='"+inputId+"' name='"+inputId+"' autoid='10016' value='"+hq_id+"' ext_size=40  size=40  ext_validate=required  "+(isReadonly?" ext_readonly=true  readonly=true  ext_noinput=true  noinput=true  ":"")+"   />")
				.append("<div><a href=\"javascript:;\" onclick=\""+removeClick+"\" ><img src='img/delete.gif' /><a></div>")
				.append("</li>")
				;
				}
				/*
				String trId="trHq_"+i,inputId="inputHq_"+i;
				context
				.append("<tr id='"+trId+"'>")
				.append("<th>"+(i+1)+"级复核人</th>")
				.append("<td><input class='inputHq' id='"+inputId+"' name='"+inputId+"' autoid='10016' value='"+hq_id+"' ext_size=40  size=40 /></td>")
				.append("<td><a href=\"javascript:;\" onclick=\"\\$('#"+trId+"').empty().remove();\" ><img src='img/delete.gif' /><a></td>")
				.append("</tr>")
				;*/
				
				hq_html+=context.toString();
			}
			}else{
			  for(int i=0;i<4;i++){
					StringBuffer context=new StringBuffer();
					
					String liId="liId_"+i,inputId="inputHq_"+i;
					context
					.append("<li id='"+liId+"'>")
					.append("<div class='divHqName'>"+(i+1)+"级复核人</div>")
					.append("<input class='inputHq required' id='"+inputId+"' name='"+inputId+"' autoid='10016' value='' ext_size=40  size=40  valuemustexist=true  ext_validate=required   />")
					.append("<div><a href=\"javascript:;\" onclick=\"\\$('#"+liId+"').empty().remove();reflashHqName();\" ><img src='img/delete.gif' /><a></div>")
					.append("</li>")
					;
				/*
				String trId="trHq_"+i,inputId="inputHq_"+i;
				StringBuffer context=new StringBuffer("");
				context
				.append("<tr id='"+trId+"'>")
				.append("<th>"+(i+1)+"级复核人</th>")
				.append("<td><input class='inputHq' id='"+inputId+"' name='"+inputId+"' autoid='10016' value='' ext_size=40  size=40 /></td>")
				.append("<td><a href=\"javascript:;\" onclick=\"\\$('#"+trId+"').empty().remove();\" ><img src='img/delete.gif' /><a></td>")
				.append("</tr>")
				;
				*/
				//hq_html+=context.toString();
			  }
			}
			
			req.setAttribute("hq_html", hq_html);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	

	
}
