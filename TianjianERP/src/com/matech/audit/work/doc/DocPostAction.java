package com.matech.audit.work.doc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.doc.DocPostService;
import com.matech.audit.service.doc.DocRecService;
import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.audit.service.doc.model.AutoCodeUsedVO;
import com.matech.audit.service.doc.model.AutoCodeVO;
import com.matech.audit.service.doc.model.DocPostFileVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.listener.UserSession;

import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class DocPostAction extends MultiActionController{

	public enum Jsp{
		editWord,tabs,handle,docTree;
		
		public String getPath(){
			return MessageFormat.format("docpost/{0}.jsp", this.name());
		}
		
		public ModelAndView createModelAndView(){
			return new ModelAndView(getPath());
		}
	}
	
	private File getWordFile(String fileName) throws Exception{
		String path = "d:/docpost/";
		File fileDir=new File(path),
		fileWord=new File(path+"/"+fileName),
		fileWordTemplate=new File(path+"/template.doc");
		if(!fileDir.exists()){
			fileDir.mkdir();
		}
		if(!fileWordTemplate.exists()){
			throw new IOException(MessageFormat.format("模板不存在，请创建:{0}", fileWordTemplate.toString()));
		}
		if(!fileWord.exists()){
			ManuFileService mfs = new ManuFileService() ;
			mfs.copyFile(fileWordTemplate, fileWord) ;
		}
		return fileWord;
         
	}

	public ModelAndView tabsDocPost(HttpServletRequest req,HttpServletResponse res){
		return Jsp.tabs.createModelAndView();
	}
	
	public ModelAndView openWord(HttpServletRequest req,HttpServletResponse res){
		String uuid=req.getParameter("uuid");
		try{
		if(uuid==null||uuid.length()==0){
			res.getWriter().print("错误!!uuid不能为空");
			return null;
		}
		String fileName=uuid+".doc";
		
		File fileWord=getWordFile(fileName);
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
	
	public ModelAndView editWord(HttpServletRequest req,HttpServletResponse res){
        return Jsp.editWord.createModelAndView();
	}

	
	/**
	 * 
	 * 保存发文正文
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void saveWord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = null ;
		try {
			
			out = response.getWriter();
			//文件上传路径
		
			MyFileUpload myfileUpload = new MyFileUpload(request);
			String uploadTempPath = myfileUpload.UploadFile(null,null);
			Map parameters = myfileUpload.getMap();
			String filename = (String)parameters.get("filename") ;
		    String uuid=request.getParameter("uuid");
			File newFile = getWordFile(uuid+".doc");
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
	
	
	public ModelAndView doHandle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv=Jsp.handle.createModelAndView();
		WebUtil webUtil=new WebUtil(request, response);
		DocPostVO docPostVO=webUtil.evalObject(DocPostVO.class);
		Connection conn=null;
		DbUtil dbUtil=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docPostVO=dbUtil.load(docPostVO, docPostVO.getUuid());
			mv.addObject("vo", docPostVO);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		mv.addObject(docPostVO);
		return mv;
	}

	
	
	public ModelAndView doBookCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		Connection conn=null;
		DocPostService docPostService=null;
		String atype=request.getParameter("atype").toUpperCase();
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		AutoCodeVO autoCodeVO=null;
		DbUtil dbUtil=null;
		AutoCodeUsedVO autoCodeUsedVO=null;
		String re="";
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
		
			autoCodeVO= dbUtil.load(AutoCodeVO.class, "atype", atype);
			docPostService=new DocPostService(conn);
			
			autoCodeUsedVO= docPostService.doBookCode(userSession, autoCodeVO);
			if(autoCodeUsedVO==null){
				re=MessageFormat.format("文号类型 {0} 无法预定", autoCodeVO.getAtype());
			}else{
				re=MessageFormat.format("文号类型 {0} 成功预定,文号:{1}", autoCodeVO.getAtype(),autoCodeUsedVO.getFullnumber());
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
			ex.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doCounterSign(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		DocPostService docPostService=null;
		DocPostVO vo=webUtil.evalObject(DocPostVO.class);
		String uuids=request.getParameter("uuids");
		uuids=uuids.replace("'", "''");
		uuids=StringUtil.trim(uuids, ",");
	    String remark=request.getParameter("remark");
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docPostService=new DocPostService(conn);
			List<DocPostVO> docPostVOs=dbUtil.select(DocPostVO.class, "select * from {0} where uuid in ("+uuids+")");
			for(DocPostVO docPostVO:docPostVOs){
		    List<String> results=docPostService.doCounterSign(docPostVO, userSession,remark);
			re+=results.get(0);
			}
			
		  
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	
	public ModelAndView doProjectSign(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		DocPostService docPostService=null;
		DocPostVO vo=webUtil.evalObject(DocPostVO.class);
		String uuids=request.getParameter("uuids");
		uuids=uuids.replace("'", "''");
		uuids=StringUtil.trim(uuids, ",");
	    String remark=request.getParameter("remark");
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docPostService=new DocPostService(conn);
			List<DocPostVO> docPostVOs=dbUtil.select(DocPostVO.class, "select * from {0} where uuid in ("+uuids+")");
			for(DocPostVO docPostVO:docPostVOs){
		    List<String> results=docPostService.doProjectSign(docPostVO, userSession,remark);
			eff++;
			}
			if(eff>0){
				re=MessageFormat.format("成功确认 {0}个 发文",eff);
			}else{
				re="确认失败";
			}
		  
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doSignissue(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		DocPostService docPostService=null;
		String uuids=request.getParameter("uuids");
		String ischeck=request.getParameter("ischeck");
		uuids=uuids.replace("'", "''");
		uuids=StringUtil.trim(uuids, ",");
		List<DocPostVO> docPostVOs=new ArrayList<DocPostVO>();
	    String remark=request.getParameter("remark");
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docPostService=new DocPostService(conn);
			docPostVOs=dbUtil.select(DocPostVO.class, "select * from {0} where uuid in ("+uuids+")");

			if("true".equals(ischeck)){
				for(DocPostVO docPostVO:docPostVOs){
			    List<String> results=docPostService.doSignIssue(docPostVO, userSession,remark);
				re+=results.get(0);
				}
				
			}else{
				for(DocPostVO docPostVO:docPostVOs){
				    docPostVO.setNode_code(Node.end.name());
				    re=MessageFormat.format("{0} 签发不通过，流程结束", StringUtil.getCurDateTime());
					docPostVO.setNode_remark(re);
				    dbUtil.update(docPostVO);
				}
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	
	public ModelAndView doCheck(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		DocPostService docPostService=null;
		String uuids=request.getParameter("uuids");
		uuids=uuids.replace("'", "''");
		uuids=StringUtil.trim(uuids, ",");
		List<DocPostVO> docPostVOs=new ArrayList<DocPostVO>();
	    String remark=request.getParameter("remark");
	    String ischeck=request.getParameter("ischeck");
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docPostService=new DocPostService(conn);
			docPostVOs=dbUtil.select(DocPostVO.class, "select * from {0} where uuid in ("+uuids+")");
		
		    for(DocPostVO docPostVO:docPostVOs){
			  List<String> results=docPostService.doCheck(docPostVO, userSession,remark,"true".equals(ischeck));
			  re+=results.get(0);
			}
				
			
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doCancelFile(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		String uuid=request.getParameter("uuid");
		DocPostFileVO docPostFileVO=null;
		DocPostVO docPostVO=null;
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			docPostFileVO=dbUtil.load(DocPostFileVO.class, uuid);
			docPostFileVO=webUtil.evalObject(docPostFileVO);
			
            re=MessageFormat.format("文件 {0} 作废或变更完成", docPostFileVO.getTitle());
            if(!StringUtil.isBlank(docPostFileVO.getDoc_no())){
                String sql="update k_autocodeused set state=0 where atype=? and fullnumber=?";
                dbUtil.executeUpdate(sql, new Object[]{docPostFileVO.getDoc_type(),docPostFileVO.getDoc_no()});
                docPostFileVO.setDoc_no("");
                if("n".equals(docPostFileVO.getCancel_state())){
                	
                	docPostFileVO.setDoc_type("FW_WWH");
                	re=MessageFormat.format("文件 {0} 已设为无文号", docPostFileVO.getTitle());
                }else if("c".equals(docPostFileVO.getCancel_state())){
                    re=docPostFileVO.getCancel_reason();
                }else if("d".equals(docPostFileVO.getCancel_state())){
                	docPostFileVO.setDel_ind(1);
                	re=MessageFormat.format("文件 {0} 删除成功", docPostFileVO.getTitle());
                }
             }
           
            dbUtil.update(docPostFileVO);
            if(!StringUtil.isBlank(docPostFileVO.getDoc_id())){
            	docPostVO=dbUtil.load(DocPostVO.class, docPostFileVO.getDoc_id());
            	 docPostVO.setDoc_no("");
            	 docPostVO.setDoc_type("FW_WWH");
                 docPostVO.setNode_remark(StringUtil.getCurDateTime()+" "+re);
                 dbUtil.update(docPostVO);
            }
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		
		response.getWriter().write(re);

		return null;
	}
	
	public ModelAndView doc(HttpServletRequest req,HttpServletResponse res){
		return Jsp.docTree.createModelAndView();
	}

	public ModelAndView tree(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		Connection conn=null;
		try {
			WebUtil webUtil=new WebUtil(request, response);
			UserSession userSession=webUtil.getUserSession();
			
			conn=new DBConnect().getConnect();
			DbUtil db=new DbUtil(conn);
			DepartmentService ds = new DepartmentService(conn);
			
			//判断是否有权看到所有文号【总所文号管理员】 ,1是有权
			String sql = "select 1 from k_userrole a,k_role b where a.rid= b.id and a.userid =? and rolename= ? ";
			String flag = StringUtil.showNull(db.queryForString(sql, new String[]{userSession.getUserId(),"总所文号管理员"})); 
			
			String departid = StringUtil.showNull(request.getParameter("departid"));
			String areaid = StringUtil.showNull(request.getParameter("areaid"));
			String departname = StringUtil.showNull(request.getParameter("departname"));
			String isSubject = StringUtil.showNull(request.getParameter("isSubject"));	//判断是哪个节目
			
			List list = null;
			if("2".equals(isSubject)){
				//打开文号树
				list = new ArrayList();
				sql = "select a.atype,a.aname FROM k_autocode a WHERE a.atype LIKE 'FW_%' AND a.areaid LIKE '%"+areaid+"%' ORDER BY id";
				List l = db.getList(sql);
				for (int i = 0; i < l.size(); i++) {
					Map m = (Map)l.get(i);
					Map map = new HashMap();
					map.put("isSubject","0");//用于标志：当前节目的类型
					map.put("cls","folder");
					map.put("leaf",true);	
					map.put("id","dic_"+StringUtil.showNull(m.get("atype")) +"_"+DELUnid.getNumUnid()) ;
					map.put("atype",StringUtil.showNull(m.get("atype")));
					map.put("areaid",areaid);
					map.put("departname",StringUtil.showNull(m.get("aname")));
					map.put("isSubject","3");
					map.put("text",StringUtil.showNull(m.get("aname")));
					list.add(map);
				}
				
			}else{
				if("1".equals(flag)){
					list = ds.getArea("555555", null);
				}else{
					list = ds.getArea("555555", null, userSession.getAreaid(), "true");
				}	
			}
			
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			System.out.println(isSubject + "|json="+json);
			response.getWriter().write(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		
		return null;
	}
}
