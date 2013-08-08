package com.matech.audit.service.project;

import java.io.*;
import java.util.zip.*;
import org.del.DelPublic;

import com.matech.framework.pub.sys.UTILSysProperty;

public class ZipUtil {
  /**
   * 将指定目录下指定类型的文件打成指定名称的zip包
   * 最后保存到指定的文件夹下面
   */
  public void zip() {
    zip(UTILSysProperty.SysProperty.getProperty("系统临时目录"));
  }

  public String gzip(String path, String type) {
  File[] files = getFiles(path, type);
  if (files == null || files.length < 1)
    return "";
  String[] filenames = new String[files.length];
  String outFilename = path + File.separator + "data" + DelPublic.getDateValue() +".zip";

  byte[] buf = new byte[1024];
  try {
    for (int i = 0; i < files.length; i++) {
      //if(!files[i].isDirectory())
      filenames[i] = path + File.separator + files[i].getName();
    }
    org.apache.tools.zip.ZipOutputStream out =
        new org.apache.tools.zip.ZipOutputStream(new FileOutputStream(outFilename));
    out.setEncoding("GBK");
    for (int i = 0; i < filenames.length; i++) {
      FileInputStream in = new FileInputStream(filenames[i]);
      out.putNextEntry(new org.apache.tools.zip.ZipEntry(files[i].getName()));
      int len;
      while ( (len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }

      out.closeEntry();
      in.close();
    }

    out.close();

    for(int i=0; i<files.length; i++) {
      files[i].deleteOnExit();
    }

  }
  catch (IOException e) {
    org.util.Debug.prtOut(e);
  }
  return outFilename;
}



  public void zip(String path) {
    File[] files = getFiles(path, null);
    if(files==null || files.length<1)
      return;
    String name = path+File.separator+"data"+DelPublic.getDateValue()+".zip";
    try{
      //true如果文件存在则提示覆盖，false文件存在也不提示覆盖，因为是恢复
      ZipOutputStream zipOutput = new ZipOutputStream(new
                                                      BufferedOutputStream(new FileOutputStream(name)));
      zipOutput.setMethod(ZipOutputStream.DEFLATED);
      for (int i = 0; i < files.length; i++) {
        org.util.Debug.prtOut(files[i].getName());
        ZipEntry entry = new ZipEntry(files[i].getName());
        entry.setMethod(ZipOutputStream.DEFLATED);
        zipOutput.putNextEntry(entry);
        zipOutput.write(getBytesFromFile(files[i]));
      }
      if (zipOutput != null) {
        zipOutput.closeEntry();
        zipOutput.close();
      }
      //备份成功后删除原文件
      for(int i=0; i<files.length; i++) {
        files[i].deleteOnExit();
      }
    }catch (Exception ex) {
      ex.printStackTrace();
    }
    return;
  }

  /**
   * 解压指定目录下的zip包的文件内容，最后删除该zip包
   * 最后保存到指定的文件夹下面
   */
  public void unZip() {
    unZip(UTILSysProperty.SysProperty.getProperty("系统临时目录"));
  }

  public void unZip(String path) {
    byte[] bs ;
    final int BUFFER = 2048;
    File[] files = getFiles(path, ".zip");

    try{
      for(int i=0; i<files.length; i++) {
        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(files[i]);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;
        while ( (entry = zis.getNextEntry()) != null) {
          int count;
          byte data[] = new byte[BUFFER];
          FileOutputStream fos = new FileOutputStream(path + File.separator +
              entry.getName());
          dest = new BufferedOutputStream(fos, BUFFER);
          while ( (count = zis.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
          }
          dest.flush();
          dest.close();
        }
        if (zis != null) {
          zis.closeEntry();
          zis.close();
        }
        files[i].delete();
      }
    }catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 得到某个目录下的所有文件对象
   * 可以过滤得到某类特定的带扩展名的或包含某名字的文件
   */
  public String[] getFilesAndDir(String path, final String sFilter) {
    if (path == null)
      return null;
    File dir = new File(path);
    String filename;
    String[] children;
    FilenameFilter filter;

    if (sFilter != null) {
      filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.indexOf(sFilter) != -1;
        }
      };
      children = dir.list(filter);
    }
    else {
      children = dir.list();
    }

    return children;
  }

  /**
   * 得到某个目录下的所有文件对象
   * 可以过滤得到某类特定的带扩展名的或包含某名字的文件
   */
  public File[] getFiles(String path, final String sFilter) {
    if (path == null)
      return null;
    File dir = new File(path);
    String filename;
    File[] children;
    FilenameFilter filter;

    if (sFilter != null) {
      filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.indexOf(sFilter) != -1;
        }
      };
      children = dir.listFiles(filter);
    }
    else {
      children = dir.listFiles();
    }

    return children;
  }

  /**
   * 清除整个目录及其子目录下的所有文件
   */
  public void delDir(final String path) {
    if (path == null || path.equals(""))
      return ;
    File dir = new File(path);
    File[] children = dir.listFiles();
    for(int i=0; i<children.length; i++) {
      if(children[i].isDirectory())
        delDir(children[i].getPath());
      children[i].delete();
    }
    dir.delete();
  }

  /**
   * 读取文件在字节数组中
   * @param file File
   * @throws IOException
   * @return byte[]
   */
  public byte[] getBytesFromFile(File file) throws Exception {
    InputStream is = new FileInputStream(file);
    long length = file.length();
    if (length > Integer.MAX_VALUE) {
      throw new Exception("文件太长啦！");
    }
    byte[] bytes = new byte[ (int) length];
    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length &&
           (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
      offset += numRead;
    }
    if (offset < bytes.length) {
      throw new Exception("读取文件出错啦！");
    }
    is.close();
    return bytes;
  }

//  public static void main(String[] args) {
//    ZipUtil zip = new ZipUtil();
//    String path = "D:/project/sql";
//    zip.zip(path);
////    zip.delDir(path);
//  }

}
