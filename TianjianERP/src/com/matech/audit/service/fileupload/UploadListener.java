package com.matech.audit.service.fileupload;  
  
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.ProgressListener;

  
public class UploadListener implements ProgressListener {   
       
    private HttpSession session=null;   
    
    private String beanName = "" ;
       
    public UploadListener (HttpSession session,String beanName){   
        this.session=session;   
        this.beanName = beanName ;
    }   
    /**   
     * 更新状态  
     * @param pBytesRead 读取字节总数  
     * @param pContentLength 数据总长度   
     * @param pItems 当前正在被读取的field号  
     */  
    public void update(long pBytesRead, long pContentLength, int pItems) {
    	
        Object obj=session.getAttribute(beanName);   
        FileUploadStatus fuploadStatus = (FileUploadStatus)obj;   
        fuploadStatus.setUploadTotalSize(pContentLength);   
        //读取完成   
        if (pContentLength == -1) {   
            fuploadStatus.setStatus("上传成功...读取了 " + pBytesRead + "/"  + pContentLength+ " bytes.");   
            fuploadStatus.setReadTotalSize(pBytesRead);   
            fuploadStatus.setCurrentUploadFileNum(pItems);   
            fuploadStatus.setProcessEndTime(System.currentTimeMillis());   
            fuploadStatus.setProcessRunningTime(fuploadStatus.getProcessEndTime());   
        }else{//读取过程中   
               fuploadStatus.setStatus("已经读取了 " + pBytesRead + " / " + pContentLength+ " bytes.");   
               fuploadStatus.setReadTotalSize(pBytesRead);   
               fuploadStatus.setCurrentUploadFileNum(pItems);   
               fuploadStatus.setProcessRunningTime(System.currentTimeMillis());   
        }   
        //System.out.println("已经读取：" + pBytesRead);   
    //    MyFileUpload.storeFileUploadStatusBean(this.session, fuploadStatus);   
        this.session.setAttribute(beanName,fuploadStatus);
    }   
  
}  
