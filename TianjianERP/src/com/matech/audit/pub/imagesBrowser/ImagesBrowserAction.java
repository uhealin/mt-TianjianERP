package com.matech.audit.pub.imagesBrowser;

import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.service.attachFileUploadService.model.Attach;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.attachFileUploadService.model.AttachLog;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * @author 图片处理类
 *
 */
public class ImagesBrowserAction extends MultiActionController {

	
	private String VIEW = "imageBrowser/open_control.jsp";
	
	/**
	 * 根据indexId 得到图片信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getImage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(VIEW);

		try{
			ASFuntion asf = new ASFuntion();
			String indexId = asf.showNull(request.getParameter("indexId"));
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String defaultImageId = asf.showNull(request.getParameter("defaultImageId"));
			
			conn = new DBConnect().getConnect(); 
			
			ImagesBrowserService browserService  = new ImagesBrowserService(conn);
			AttachService attachService = new AttachService(conn);
			//indexId = "c17946f8-ad51-4381-b1cf-11d0eec72126";  测试数据
			List<Attach> listImages  = null;
			
			if(!"".equals(indexId)){
				
				listImages =  browserService.getImages(indexId); 
				
				//得到图片的长宽
				for (int i = 0; i < listImages.size(); i++) {
					try {
						Attach images = listImages.get(i);
						  
						String attachFilePath = AttachService.ATTACH_FILE_PATH;

						if (attachFilePath.lastIndexOf("/") != attachFilePath.length()) {
							attachFilePath += "/";
						}
						// 如果不指定模块，则放到
						if (!"".equals(images.getIndexTable())) {
							attachFilePath += images.getIndexTable() + "/";
						} else {
							attachFilePath += AttachService.ATTACH_FILE_DEFAULT_FOLDER;
						}
							
						File _file = new File(attachFilePath+images.getAttachId()); //读入文件  
						Image src = javax.imageio.ImageIO.read(_file); //构造Image对象  
						int width=src.getWidth(null); //得到源图宽  
						int height=src.getHeight(null); //得到源图长  

						if(width != 0 && height!=0){
							images.setWidth(width+"");
							images.setHeight(height+"");
						}

//					      AttachLog attachLog = new AttachLog();
//					      attachLog.setIndexId(indexId);
//					      attachLog.setFileId(images.getId());
//					      attachLog.setFileName(images.getFileName());
//					      attachLog.setFilePath(images.getFilePath());
//					      attachLog.setLookDate(asf.getCurrentDate()+"" + asf.getCurrentTime());
//					      attachLog.setUserName(userSession.getUserName());
//					      attachLog.setUserIp(userSession.getUserIp());
//					      attachLog.setProperty("企业资质");
//					      
//					      attachService.saveLog(attachLog); //企业资质
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				      
				}
				
				
			}
			
			modelAndView.addObject("listImages",listImages);
			modelAndView.addObject("defaultImageId",defaultImageId);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
}
