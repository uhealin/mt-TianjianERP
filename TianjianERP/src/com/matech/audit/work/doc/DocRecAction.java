package com.matech.audit.work.doc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.apache.catalina.connector.Request;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.doc.DocRecService;
import com.matech.audit.service.doc.DocRecTest;
import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.audit.service.doc.model.DocLogVO;
import com.matech.audit.service.doc.model.DocRecVO;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.work.doc.DocPostAction.Jsp;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;
import com.matech.sms.SmsService;

public class DocRecAction extends MultiActionController {

	protected enum Jsp{
		check,tabs;
		
		public String getPath(){
			return MessageFormat.format("docrec/{0}.jsp", this.name());
		}
		
		public ModelAndView createModelAndView(){
			return new ModelAndView(getPath());
		}
	}
	
	protected enum Layout{
		viewLog;
		public String getPath(){
			return MessageFormat.format("docrec/layout/{0}.jsp", this.name());
		}
	}
	
	
	
	private File getFile(String fileName) throws Exception{
		String path = "d:/docrec/";
		File fileDir=new File(path),
		file=new File(path+"/"+fileName),
		fileWordTemplate=new File(path+"/template.doc");
		if(!fileDir.exists()){
			fileDir.mkdir();
		}
		if(!file.exists()){
			return null;
		}
		return file;
		/*
		if(!fileWordTemplate.exists()){
			throw new IOException(MessageFormat.format("模板不存在，请创建:{0}", fileWordTemplate.toString()));
		}
		if(!fileWord.exists()){
			ManuFileService mfs = new ManuFileService() ;
			mfs.copyFile(fileWordTemplate, fileWord) ;
		}
		*/
		
         
	}
	
	public ModelAndView toCheck(HttpServletRequest req,HttpServletResponse res){
		String uuid=req.getParameter("uuid");
		Connection conn=null;
	    DocRecVO vo=new DocRecVO();
	    DbUtil dbUtil=null;
	    WebUtil webUtil=new WebUtil(req, res);
	    
	    UserSession userSession=webUtil.getUserSession();
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			vo=dbUtil.load(vo, uuid);
			//vo.setCheck_state("阅毕");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		req.setAttribute(DocRecVO.class.getName(), vo);
		return Jsp.check.createModelAndView();
	}
	
	
	public ModelAndView toTabs(HttpServletRequest req,HttpServletResponse res){

		return Jsp.tabs.createModelAndView();
	}
	
	public ModelAndView viewLog(HttpServletRequest req,HttpServletResponse res){

		ModelAndView mv=new ModelAndView(Layout.viewLog.getPath());
		Connection conn=null;
		DbUtil dbUtil=null;
		List<DocLogVO> docLogVOs=new ArrayList<DocLogVO>();
	    DocRecVO docRecVO=null;
		String uuid=req.getParameter("uuid");
		String auth=req.getParameter("auth");
		String authsql="all".equals(auth)?" ":" and public_ind=''true''";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docRecVO=dbUtil.load(DocRecVO.class, uuid);
			docLogVOs=dbUtil.select(DocLogVO.class, 
			 "select * from {0} where ctype=? and doc_id=?  order by handle_time desc",
			 "docrec"
			 ,docRecVO.getUuid()
			);
			List<DocLogVO> docLogVOs2=new ArrayList<DocLogVO>();
			for(DocLogVO docLogVO:docLogVOs){
				if("all".equals(auth)){
					//docLogVOs2.add(docLogVO);
				}
				else if(!"true".equals(docLogVO.getPublic_ind())){
					docLogVO.setRemark("");
					//docLogVOs2.add(docLogVO);
				}
					docLogVOs2.add(docLogVO);
				
				
			}
			req.setAttribute("logs", docLogVOs2);
		}catch(Exception ex){
			ex.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
		return mv;
	}
	
	public ModelAndView doCheckEnd(HttpServletRequest req,HttpServletResponse res){
		String uuid=req.getParameter("uuid");
		String assigner_id=req.getParameter("assigner_id");
		Connection conn=null;
	    DocRecVO vo=new DocRecVO(),vo1=new DocRecVO();
	    DbUtil dbUtil=null;
	    WebUtil webUtil=new WebUtil(req, res);
	    DocLogVO docLogVO=new DocLogVO();
	    UserSession userSession=webUtil.getUserSession();
	    UserVO assigner=null;
	    DocRecService docRecService=null;
	    SmsService smsService=null;
		try {
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			smsService=new SmsService(conn);
			docRecService=new DocRecService(conn);
			vo1=dbUtil.load(vo1, uuid);
			vo=webUtil.evalObject(vo);
			//vo.setState("已办结");
			//dbUtil.update(vo);
			//req.setAttribute(DocRecVO.class.getName(), vo);
			docLogVO.setCtype("docrec");
			docLogVO.setDoc_no(vo1.getRec_doc_no());
			docLogVO.setHandler_id(userSession.getUserId());
			docLogVO.setHandler_name(userSession.getUserName());
			docLogVO.setUuid(UUID.randomUUID().toString());
			docLogVO.setRemark(vo.getHandle_remark());
			docLogVO.setHandle_time(StringUtil.getCurDateTime());
			docLogVO.setPublic_ind(vo.getPublic_ind());
			docLogVO.setDoc_id(vo.getUuid());
			docLogVO.setNode_code(Node.hq.name());
			dbUtil.insert(docLogVO);
			if(!StringUtil.isBlank(assigner_id)){
				String[] aids=assigner_id.split(","),rids=vo1.getRoam_range_ids().split(",");
				Set<String> set_rids=new HashSet<String>();
				for(String rid:rids){
					set_rids.add(rid);
				}
				for(String aid:aids){
					if(!set_rids.contains(aid)){
						UserVO userVO=dbUtil.load(UserVO.class,Integer.valueOf(aid));
						docRecService.remindStart(vo1, userVO,false);
					}
				}
				/*
				for(String aid :aids){
					boolean isIn=false;
					for(String rid:rids){
                       if(aid.equals(rid)){isIn=true;break;}
					}
					if(!isIn){
						UserVO userVO=dbUtil.load(UserVO.class,Integer.valueOf(aid));
						docRecService.remindStart(vo1, userVO,false);
					}
				}*/
				vo1=docRecService.addRoam_range(vo1, aids);
				dbUtil.update(vo1);
				//docRecService.remindStart( vo1,aids);
				
			}
			if(docRecService.isAllCheckEnd(vo1)){
				vo1.setState("审核完毕");
				dbUtil.update(vo1);
				//docRecService.remindAllCheckEnd(userSession, vo1);
				docRecService.remindCreater(vo1, false);
			}
			String url= req.getSession().getAttribute("url").toString();
			res.sendRedirect(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}

		return null;
	}
	
	public ModelAndView doTodo(HttpServletRequest req,HttpServletResponse res){
		String uuid=req.getParameter("uuid");
		String assigner_id=req.getParameter("assigner_id");
		Connection conn=null;
	    DocRecVO vo=new DocRecVO();
	    DbUtil dbUtil=null;
	    WebUtil webUtil=new WebUtil(req, res);
	    UserVO assigner=null;
	   
		try {
			conn=new DBConnect().getConnect();
			
			
			
			
			//docRecService.doToDo(vo, webUtil.getUserSession(), assigner);
			String url= req.getSession().getAttribute("url").toString();
			res.sendRedirect(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	
		return null;
	}
	
	public ModelAndView openFile(HttpServletRequest req,HttpServletResponse res){
		String uuid=req.getParameter("uuid");
		try{
		if(uuid==null||uuid.length()==0){
			res.getWriter().print("错误!!uuid不能为空");
			return null;
		}
		String fileName=uuid+".doc";
		
		File fileWord=getFile(fileName);
		FileInputStream stream = new FileInputStream(fileWord);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] b = new byte[1024];
        int n;
        while ((n = stream.read(b)) != -1)
        	out.write(b, 0, n);
        
        stream.close();
        out.close();
        
        byte[] bytes =  out.toByteArray();
		fileName = URLEncoder.encode(fileName,"UTF-8") ;
		res.setContentType("application/x-msdownload");
		res.setHeader("Content-disposition",
				"attachment; filename=" + fileName);
		res.setHeader("Content-Length", String.valueOf(bytes.length));
		if (bytes != null ) {
			OutputStream outs = res.getOutputStream();
			outs.write(bytes);
			outs.flush();
			outs.close();
		} else {
			
			res.getWriter().print("错误!!文件不存在！");
		}
		}catch(Exception ex){}
		return new ModelAndView();
	}
	
	

	
	/**
	 * 
	 * 保存发文正文
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void saveFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null ;
		try {
			
			out = response.getWriter();
			//文件上传路径
		
			MyFileUpload myfileUpload = new MyFileUpload(request);
			String uploadTempPath = myfileUpload.UploadFile(null,null);
			Map parameters = myfileUpload.getMap();
			String filename = (String)parameters.get("file_upload") ;
		    String uuid=request.getParameter("uuid");
			File newFile = getFile(uuid+"-"+filename);
			File oldFile = new File(uploadTempPath+filename) ;
			ManuFileService mfs = new ManuFileService() ;
			mfs.copyFile(oldFile, newFile) ;
			out.write("Success\n文件修改成功!");
			out.close() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally { 
			
		}
	}
	
	public ModelAndView doFinish(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		DocRecVO docRecVO=webUtil.evalObject(DocRecVO.class);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docRecVO=dbUtil.load(DocRecVO.class, docRecVO.getUuid());
			docRecVO=webUtil.evalObject(docRecVO);
			docRecVO.setState("已处理");
			docRecVO.setHandle_date(StringUtil.getCurDate());
			eff+=dbUtil.update(docRecVO);
			if(eff==1){
				re=MessageFormat.format("收文:{0}办结成功", docRecVO.getRec_doc_no());
			}else{
				re=MessageFormat.format("收文:{0}办结失败", docRecVO.getRec_doc_no());
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}

}
