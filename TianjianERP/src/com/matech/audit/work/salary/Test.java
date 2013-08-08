package com.matech.audit.work.salary;

import java.io.BufferedOutputStream;   
import java.io.File;   
import java.io.FileOutputStream;   
import java.io.IOException;   
import java.io.InputStream;   
import java.util.Enumeration;   
  
import org.apache.tools.zip.ZipEntry;   
import org.apache.tools.zip.ZipFile;  
import com.matech.audit.service.datamanage.DataZip; 

public class Test {

	
	
	private static final int buffer = 2048;   
	       
	    public static void main(String[] args)   
	    {   
	        //unZip("C:\\temp\\1345572181703\\3.zip","4.xls");   
	        
	        
	        String f1="C:\\temp\\1345572181703\\3.zip";
	        com.matech.audit.service.datamanage.DataZip dz=new DataZip();
			
			try{
			dz.unZip(f1,"C:\\temp\\1345572181703\\",false);
			}catch(Exception e){
				e.printStackTrace();
			}
	        
	    }   
	    
	    
	    
	    public static void unZip(String infile,String outfile)   
	    {   
	        int count = -1;   
	        int index = -1;   
	        String savepath = "";   
	           
	        File file = null;    
	        InputStream is = null;     
	        FileOutputStream fos = null;     
	        BufferedOutputStream bos = null;   
	           
	        savepath = infile.substring(0, infile.lastIndexOf("\\")) + "\\";   
	  
	        try  
	        {    
	            ZipFile zipFile = new ZipFile(infile);    
	  
	            Enumeration<?> entries = zipFile.getEntries();   
	               
	            if(entries.hasMoreElements())   
	            {    
	                byte buf[] = new byte[buffer];    
	                   
	                ZipEntry entry = (ZipEntry)entries.nextElement();    
	                   
	                String filename =outfile;
	                   
	                filename = savepath + filename;   
	                   
	                file = new File(filename);    
	                file.createNewFile();   
	                   
	                is = zipFile.getInputStream(entry);    
	                   
	                fos = new FileOutputStream(file);    
	                bos = new BufferedOutputStream(fos, buffer);   
	                   
	                while((count = is.read(buf)) > -1)   
	                {    
	                    bos.write(buf, 0, count );    
	                }    
	                   
	                fos.close();    
	  
	                is.close();    
	            }    
	               
	            zipFile.close();    
	               
	        }catch(IOException ioe){    
	            ioe.printStackTrace();    
	        }    
	    }    
	    
	    public static void unZip(String infile)   
	    {   
	        int count = -1;   
	        int index = -1;   
	        String savepath = "";   
	           
	        File file = null;    
	        InputStream is = null;     
	        FileOutputStream fos = null;     
	        BufferedOutputStream bos = null;   
	           
	        savepath = infile.substring(0, infile.lastIndexOf("\\")) + "\\";   
	  
	        try  
	        {    
	            ZipFile zipFile = new ZipFile(infile);    
	  
	            Enumeration<?> entries = zipFile.getEntries();   
	               
	            while(entries.hasMoreElements())   
	            {    
	                byte buf[] = new byte[buffer];    
	                   
	                ZipEntry entry = (ZipEntry)entries.nextElement();    
	                   
	                String filename = entry.getName();   
	                index = filename.lastIndexOf("/");   
	                if(index > -1)   
	                    filename = filename.substring(index+1);   
	                   
	                filename = savepath + filename;   
	                   
	                file = new File(filename);    
	                file.createNewFile();   
	                   
	                is = zipFile.getInputStream(entry);    
	                   
	                fos = new FileOutputStream(file);    
	                bos = new BufferedOutputStream(fos, buffer);   
	                   
	                while((count = is.read(buf)) > -1)   
	                {    
	                    bos.write(buf, 0, count );    
	                }    
	                   
	                fos.close();    
	  
	                is.close();    
	            }    
	               
	            zipFile.close();    
	               
	        }catch(IOException ioe){    
	            ioe.printStackTrace();    
	        }    
	    }    
	
	
}
