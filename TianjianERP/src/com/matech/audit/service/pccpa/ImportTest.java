package com.matech.audit.service.pccpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import junit.framework.Assert;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;


import com.matech.audit.service.attachFileUploadService.model.MtComAttachVO;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.doc.DocPostService;
import com.matech.audit.service.doc.DocPostService.Node;
import com.matech.audit.service.doc.model.DocLogVO;
import com.matech.audit.service.doc.model.DocPostVO;
import com.matech.audit.service.form.model.FormVO;
import com.matech.audit.service.news.model.News;
import com.matech.audit.service.pccpa.model.A001VO;
import com.matech.audit.service.pccpa.model.FsEmpolyeeVO;
import com.matech.audit.service.pccpa.model.PccpaNewVO;
import com.matech.audit.service.pccpa.model.WordMsVO;
import com.matech.audit.service.pccpa.model.WordVO;
import com.matech.audit.service.pccpa.model.XCarShop4sVO;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.net.Web;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.TestUtil;
import com.matech.framework.pub.util.WebUtil;

public class ImportTest extends TestUtil {

	
	protected ImportService importService;
	protected DocPostService docPostService;
	protected DepartmentService departmentService;
	public ImportTest(){
		importService=new ImportService(conn);
		docPostService=new DocPostService(conn);
		departmentService=new DepartmentService(conn);
	}
	
	//@Test
	public void importA001(){
		
	   int eff=importService.importA001();
	   Assert.assertNotSame(eff, 0);
	}
	
	//@Test
	public void dropSubset(){
		int eff=importService.dropSubsets();
		 Assert.assertNotSame(eff, 0);
	}
	
	//@Test
	public void importDepart(){
		//dbUtil.executeUpdate("");
		int eff=importService.importDepartment();
		Assert.assertNotSame(eff, 0);
	}
	
	//@Test
	public void fixCharSet(){
		int eff=importService.fixColumnCharset("latin1");
		Assert.assertNotSame(eff, 0);
	}
	
	//@Test
	public void importNews(){
		List<PccpaNewVO> pccpaNewVOs=dbUtil.select(PccpaNewVO.class, "select * from {0}");
		for(PccpaNewVO pccpaNewVO:pccpaNewVOs){
			News news=new News();
			news.setArea(pccpaNewVO.getArea());
			news.setBig_type("1");
			news.setType(pccpaNewVO.getClassCName());
			news.setTitle(pccpaNewVO.getTitle());
			news.setUpdateTime(pccpaNewVO.getAddDate());
			news.setContents(pccpaNewVO.getContent());
			news.setDoc_no(pccpaNewVO.getAuthor());
			news.setMenuid(pccpaNewVO.getMenuid());
			news.setTitle(pccpaNewVO.getTitle());
			news.setSub_title(pccpaNewVO.getSubTitle());
			news.setPublishUserId("55772");
			
			//dbUtil.insert(news);
		}
	}
	
	//@Test
	public void drop(){
		String[] prefixs={"a","b"},subfixs={"a001","a002","a003","a021","a022","a023"};
		
		for(String prefix :prefixs){
			for(int i=1;i<100;i++){
				String m=String.valueOf(i);
				if(m.length()==1){
					m="00"+m;
				}else if(m.length()==2){
					m="0"+m;
				}
				for(String subfix:subfixs){
					String tablename=prefix+m+subfix;
					try {
						dbUtil.executeUpdate("drop table "+tablename);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	//@Test
	public void sysRankRole(){
		try {
			List<Map> rankroles=dbUtil.getList("select * from temp_rankrole ");
			for(Map rankrole:rankroles){
				String rank_name=rankrole.get("rank_name").toString();
				String role_name=rankrole.get("role_name").toString();
				try{
				List<UserVO> userVOs=dbUtil.select(UserVO.class, "select * from {0} where rank=?", rank_name);
				String role_id=dbUtil.queryForString("select id from k_role where rolename = '"+role_name+"'");
				if(role_id==null||role_id.isEmpty())continue;
				for(UserVO userVO:userVOs){
					String sql="insert into k_userrole (userid,rid) values ("+userVO.getId()+","+role_id+") ";
					try{
					int i=dbUtil.executeUpdate(sql);
					System.out.println(sql+":"+i);
					}catch(Exception ex){}
				}
				}catch(Exception ex){System.err.println(ex.getLocalizedMessage()) ;continue;}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//@Test
	public void main(){
		int eff=0;
		//importService.dropBasicColumnToSubsets();
		//importService.appendBasicColumnToSubsets();
	    //importService.fixColumnCharset("gbk");
		//importService.synSubset2();
		//importService.fillUuid();
		//importService.synSubset2();
		//importService.setPk("uuid");
		//importService.fixNotNullColumn();
		//importService.importA001();
		//importService.synSubset();
		//importService.genTable();
		//importService.fixUserId();
		//importService.importA001();
		//eff+=importService.dropBasicColumnToSubsets();
		//importService.fixSubsetSourceStruct();
		//importService.fixSubsetTargetStruct();
		//importService.importSubset();
		//importService.synPccpa(false);
		
		importService.synDepartment();
		//importService.fixSubsetStruct();
		//importService.fixOrderid();
		Assert.assertEquals(eff,36);
		try {
			//departmentService.updateDepartmentPath();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DbUtil.close(this.conn);
	}
	
	
	    private boolean copy(String fileFrom, String fileTo) {  
	        try {  
	            FileInputStream in = new java.io.FileInputStream(fileFrom);  
	            FileOutputStream out = new FileOutputStream(fileTo);  
	            byte[] bt = new byte[1024];  
	            int count;  
	            while ((count = in.read(bt)) > 0) {  
	                out.write(bt, 0, count);  
	            }  
	            in.close();  
	            out.close();  
	            return true;  
	        } catch (IOException ex) {  
	            return false;  
	        }  
	    }  
	  
	
	//@Test
	public void importUserImg(){
		int eff=0;
		List<FsEmpolyeeVO> fsEmpolyeeVOs=dbUtil.select(FsEmpolyeeVO.class, "select * from {0}");
		String basePath="E:/Image/",targetPath="E:/ImageAtt/";
		for(FsEmpolyeeVO fsEmpolyeeVO:fsEmpolyeeVOs){
			if(StringUtil.isBlank(fsEmpolyeeVO.getPICName()))continue;
			String imgPath=basePath+fsEmpolyeeVO.getPICName();
			File sourceImg=new File(imgPath);
			if(!sourceImg.exists())continue;
			String subFix=imgPath.substring(imgPath.lastIndexOf("."));
			List<UserVO> userVOs=null;
			try {
				userVOs = dbUtil.select(UserVO.class, "select * from {0} where IdentityCard=?",fsEmpolyeeVO.getEID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(userVOs.size()<1)continue;
			UserVO userVO=userVOs.get(0);
			String targetImgPath=targetPath+userVO.getId()+subFix;
			//boolean isCopy= sourceImg..renameTo(new File(targetImgPath));
			copy(imgPath, targetImgPath);
		    try {
				dbUtil.executeUpdate("update k_user set userphoto=? where id=?",new Object[]{userVO.getId()+subFix,userVO.getId()});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.err.println(MessageFormat.format("source:{0} \n target:{1} ", imgPath,targetImgPath));
		}
		//return eff;
	}
	
	//@Test
	public void fixUUID(){
		try {
			List<Map> cadets=dbUtil.getList("select name from cadet_audit");
			for(Map cadet :cadets){
				String name=cadet.get("name").toString();
				String sql="update cadet_audit set uuid=UUID() where name=?";
				dbUtil.executeUpdate(sql,new Object[] {name});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void importDocs(){
		List<WordVO> wordVOs=dbUtil.select(WordVO.class, "select * from {0}");
		int eff=0;
        List<String> noExistDocFileNames=new ArrayList<String>();
		for(WordVO wordVO:wordVOs ){
			
			DocPostVO docPostVO=importService.toDocpost(wordVO);
			WordMsVO wordMsVO=null;
			List<DocLogVO> docLogVOs=new ArrayList<DocLogVO>();
			List<MtComAttachVO> mtComAttachVOs=new ArrayList<MtComAttachVO>();
			try {
				//Thread.sleep(50);
				docPostVO.setUuid(UUID.randomUUID().toString());
				docPostVO.setAttach_id(UUID.randomUUID().toString());
				wordMsVO=dbUtil.load(WordMsVO.class, wordVO.getId());
				//if(wordMsVO.getId()==null){
				  docLogVOs=importService.toDocLog(wordVO);	
				//}else{
				//  docLogVOs=importService.toDocLog(wordMsVO);
				//}
				for(DocLogVO docLogVO:docLogVOs){
					docLogVO.setDoc_id(docPostVO.getUuid());
					docLogVO.setDoc_no(docPostVO.getDoc_no());
					docLogVO.setPccpa_docid(wordVO.getId());
					try{
					dbUtil.insert(docLogVO);
					}catch(Exception ex){}
				}
				
				//docPostVO=docPostService.updateSignInfoContext(docPostVO, Node.hq);
				//docPostVO=docPostService.updateSignInfoContext(docPostVO, Node.qf);
				//docPostVO=docPostService.updateSignInfoContext(docPostVO, Node.hy);
				eff+=dbUtil.insert(docPostVO);
				//dbUtil.update(docPostVO);
				String sourcePath="h:/tj_docpost/"+wordVO.getFile_name(),targetPath="h:/mt_docpost/"+docPostVO.getUuid()+".doc";
				
				File source=new File(sourcePath);
				if(source.exists()){
				copy(sourcePath, targetPath);
				}else{
					
				   noExistDocFileNames.add(sourcePath);
				}
				mtComAttachVOs=importService.toDocAtts(wordVO, docPostVO);
				String attDirPath="h:/mt_docpost_att/"+docPostVO.getAttach_id()+"/";
				File attDir=new File(attDirPath);
				if(!attDir.exists()){
					attDir.mkdir();
					
				}
				for(MtComAttachVO mtComAttachVO:mtComAttachVOs){
					String sourceAttPath="h:/tj_docpost/"+mtComAttachVO.getATTACHNAME(),targetAttPath=attDirPath+docPostVO.getUuid()+".doc";
					dbUtil.insert(mtComAttachVO);
					copy(sourceAttPath, targetAttPath);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(String path :noExistDocFileNames){
			System.err.println(path);
		}
		Assert.assertEquals(eff, wordVOs.size());
	}
	
	
	//@Test
	public void importDoc(){
		int eff=0;
		List<FsEmpolyeeVO> fsEmpolyeeVOs=dbUtil.select(FsEmpolyeeVO.class, "select * from {0}");
		String basePath="E:/Image/",targetPath="E:/ImageAtt/";
		for(FsEmpolyeeVO fsEmpolyeeVO:fsEmpolyeeVOs){
			if(StringUtil.isBlank(fsEmpolyeeVO.getPICName()))continue;
			String imgPath=basePath+fsEmpolyeeVO.getPICName();
			File sourceImg=new File(imgPath);
			if(!sourceImg.exists())continue;
			String subFix=imgPath.substring(imgPath.lastIndexOf("."));
			List<UserVO> userVOs=null;
			try {
				userVOs = dbUtil.select(UserVO.class, "select * from {0} where IdentityCard=?",fsEmpolyeeVO.getEID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(userVOs.size()<1)continue;
			UserVO userVO=userVOs.get(0);
			String targetImgPath=targetPath+userVO.getId()+subFix;
			//boolean isCopy= sourceImg..renameTo(new File(targetImgPath));
			copy(imgPath, targetImgPath);
		    try {
				dbUtil.executeUpdate("update k_user set userphoto=? where id=?",new Object[]{userVO.getId()+subFix,userVO.getId()});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.err.println(MessageFormat.format("source:{0} \n target:{1} ", imgPath,targetImgPath));
		}
		//return eff;
	}
	
	//@Test
	public void fixHqid(){
		List<DocPostVO> docPostVOs=dbUtil.select(DocPostVO.class, "select * from {0}");
		
		for(DocPostVO docPostVO:docPostVOs){
			if(StringUtil.isBlank(docPostVO.getCountersigner_names()))continue;
			String[] names=docPostVO.getCountersigner_names().split(",");
			docPostVO.setCountersigner_ids(importService.parseUserNameToId(names));
			dbUtil.update(docPostVO);
		}
	}
	
	
	//http://www.mbmpv.com/js/dealers_V4.js  福建奔驰
	//var dealers = { 	1:	{done: '', aid: 'huabei', pid: 'beijing', cid: 'deauda', cname: '鍖椾含寰峰ゥ杈炬苯杞﹁繘鍑哄彛鏈夐檺鍏徃', typeA: true, typeB: true, photo: 'deauda_1.jpg', address: '鍦板潃锛氬寳浜競鏈濋槼鍖哄寳鍥涚幆鍗佸瓧鍙ｇ敳涓€鍙�', tel: '+86-(0)10-6433 5666', fax: '+86-(0)10-6435 1556', website: '', time: '鍛ㄤ竴鑷冲懆鏃ワ細08:30-17:30', lat: 39.980474, lng: 116.451902},...var provinces = {
	@Test
	public void importMbmp(){
		//String url="http://www.mbmpv.com/js/dealers_V4.js";
		String context="";
		try {
			context = FileInputStream("g:/dealers_V4.js");
			//context=FileInputStream("g:/mbpm.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //context=WebUtil.encode(context, "utf-8");
		context=context.substring(context.indexOf("["),context.indexOf("var provinces")-1);
		//JSONObject json=JSONObject.fromObject(context);
		JSONArray jarrShops=JSONArray.fromObject(context);
		for(int i=0;i<jarrShops.size();i++){
			//String key=it.next();
			JSONObject jsonShop=jarrShops.getJSONObject(i);
			XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
			try{
			
			//"servicephone":"","id":"0","webshort":"","address":"瀹氭捣鍖哄弻妗ラ晣姹借溅鍩�","name",sellphone":"","provinceid"
		   
		    xCarShop4sVO.setAddress(jsonShop.getString("address").trim());
		    xCarShop4sVO.setCatalog("福建奔驰");
		    xCarShop4sVO.setName(jsonShop.getString("cname").trim());
		    xCarShop4sVO.setPid(jsonShop.getString("pid").trim());
		    xCarShop4sVO.setSell_phone(jsonShop.getString("tel").trim());
		    xCarShop4sVO.setService_phone(jsonShop.getString("fax").trim());
		    xCarShop4sVO.setRemark(MessageFormat.format("{0} {1} {2}", 
		    		jsonShop.getString("aid"),
		    		jsonShop.getString("pid"),
		    		jsonShop.getString("cid")
		    		));
		    xCarShop4sVO.setUuid(UUID.randomUUID().toString());
		    dbUtil.insert(xCarShop4sVO);
			}catch(Exception ex){
				ex.printStackTrace();
				}
		}
	}
	
	/* 广汽三菱
	 * http://www.gmmc.com.cn/xsqd/jxscx/index.shtml
	 * var markers = 
{type:"4S店",province:"直辖市",city:"北京",lng:116.330325,lat:39.836885,name:"广汽三菱商贸丰台店",code:"J11A001",company:"",address:"北京市丰台区南四环西路73号（在建中）",bussinessTime:"",saleTelephone:"010-83600005",hotline:"",emergency:""}
, ..... ;
	 */
	@Test
	public void importGmmc(){
		//String url="http://www.gmmc.com.cn/xsqd/jxscx/index.shtml";
		//String context=WebUtil.DoPost(url, new ArrayList<NameValuePair>(), new HashMap<String, File>());
		String context="";
		try {
			context = FileInputStream("g://gmmc.shtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//context=context.substring(context.indexOf("var markers = ")+14,context.indexOf(";"));
		//JSONObject json=JSONObject.fromObject(context);
		JSONArray jarrShops=JSONArray.fromObject(context);
		for(int i=0;i<jarrShops.size();i++){
			//String key=it.next();
			JSONObject jsonShop=jarrShops.getJSONObject(i);
			//"servicephone":"","id":"0","webshort":"","address":"瀹氭捣鍖哄弻妗ラ晣姹借溅鍩�","name",sellphone":"","provinceid"
		    XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
			try{

		    xCarShop4sVO.setAddress(jsonShop.getString("address"));
		    xCarShop4sVO.setCatalog("广汽三菱");
		    xCarShop4sVO.setName(jsonShop.getString("name")+" "+jsonShop.getString("type"));
		    xCarShop4sVO.setPid(jsonShop.getString("province"));
		    xCarShop4sVO.setSell_phone(jsonShop.getString("saleTelephone"));
		    xCarShop4sVO.setService_phone(jsonShop.getString("hotline"));
		    xCarShop4sVO.setUuid(UUID.randomUUID().toString());
		    xCarShop4sVO.setRemark(MessageFormat.format("{0} {1}", 
		    		jsonShop.getString("province")
		    		,jsonShop.getString("city")
		    		));
		    dbUtil.insert(xCarShop4sVO);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	/* 广汽三菱
	 * http://auto.lifan.com/GetDealerList.ashx?provname=%u6D59%u6C5F&date=128
	 * date 128
        provname 浙江
	 * [{"Id":"156","DealerNumber":null,"DealerName":"金华市陆通汽车销售服务有限公司","ProvinceId":"31","CityId":null,"Tel":"0579-82082138","Address":"浙江省金华市汽车城横三路和纵二路交叉口（车管所对面）","Longitude":"119.647445","Latitude":"29.079059","WabUrl":null},
	 */
	@Test
	public void importLifan(){
		String urlPattern="http://auto.lifan.com/GetDealerList.ashx?provname={0}&date={1}";
		String[] provnames={"北京","上海","重庆","天津","安徽","福建","甘肃","广东","广西","贵州","海南","河北","河南","黑龙江","湖北","湖南","吉林","江苏","江西","辽宁","内蒙古","宁夏","青海","山东","山西","陕西","四川","西藏","新疆","云南","浙江"};
		for(String provname:provnames){
		List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
		//nameValuePairs.add(new BasicNameValuePair("provname", provname));
		//nameValuePairs.add(new BasicNameValuePair("date", "128"));
		//URLEncoder.encode(provname, "utf-8")
		String url=MessageFormat.format(urlPattern, provname,"128");
		String context=WebUtil.DoPost(url, nameValuePairs, new HashMap<String, File>());
		//context=context.substring(context.indexOf("var markers = ")+14,context.indexOf(";"));
		//JSONObject json=JSONObject.fromObject(context);
		JSONArray jarrShops=JSONArray.fromObject(context);
			for(int i=0;i<jarrShops.size();i++){
				try{
				JSONObject jsonShop=jarrShops.getJSONObject(i);
				//"servicephone":"","id":"0","webshort":"","address":"瀹氭捣鍖哄弻妗ラ晣姹借溅鍩�","name",sellphone":"","provinceid"
			    XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
			    xCarShop4sVO.setAddress(jsonShop.getString("Address"));
			    xCarShop4sVO.setCatalog("力帆");
			    xCarShop4sVO.setName(jsonShop.getString("DealerName"));
			    xCarShop4sVO.setPid(jsonShop.getString("ProvinceId"));
			    xCarShop4sVO.setSell_phone(jsonShop.getString("Tel"));
			    xCarShop4sVO.setService_phone(jsonShop.getString("Tel"));
			    xCarShop4sVO.setUuid(UUID.randomUUID().toString());
			    xCarShop4sVO.setRemark(MessageFormat.format("{0}", 
			             provname
			    		));
			    
			    dbUtil.insert(xCarShop4sVO);
				}catch(Exception ex){ex.printStackTrace();}
			}
		}
	}
	
	//http://www.skoda.com.cn/skoda/pages/dealer/dealermap/locator/getdealer.js
	//@Test
	public void importSkoda(){
		String url="http://www.skoda.com.cn/skoda/pages/dealer/dealermap/locator/getdealer.js";
		String context=WebUtil.DoPost(url, new ArrayList<NameValuePair>(), new HashMap<String, File>());
		JSONObject json=JSONObject.fromObject(context);
		JSONArray jarrShops=json.getJSONArray("list");
		for(int i=0;i<jarrShops.size();i++){
			try{
			JSONObject jsonShop=jarrShops.getJSONObject(i);
			//"servicephone":"","id":"0","webshort":"","address":"瀹氭捣鍖哄弻妗ラ晣姹借溅鍩�","name",sellphone":"","provinceid"
		    XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
		    xCarShop4sVO.setAddress(jsonShop.getString("address"));
		    xCarShop4sVO.setCatalog("斯柯达");
		    xCarShop4sVO.setName(jsonShop.getString("name"));
		    xCarShop4sVO.setPid(jsonShop.getString("provinceid"));
		    xCarShop4sVO.setSell_phone(jsonShop.getString("sellphone"));
		    xCarShop4sVO.setService_phone(jsonShop.getString("servicephone"));
		    xCarShop4sVO.setUuid(UUID.randomUUID().toString());
		    dbUtil.insert(xCarShop4sVO);
			}catch(Exception ex){ex.printStackTrace();}
		}
	}
	
	/*
	 * 上汽通用五菱
	 * 城市 url http://www.sgmw.com.cn/templates/T_ServiceOperator/getCity.aspx
	 * chexing 0
nodeid 29
province 甘肃
time 1357949733433

http://www.sgmw.com.cn/templates/T_ServiceOperator/PageList.aspx

chexing 0
citys 广州
nodeid 29
province 广东  pagesize 2
	 */
	@Test
		public void importSMGW(){
			String urlCityPattern="http://www.sgmw.com.cn/templates/T_ServiceOperator/getCity.aspx?chexing={0}&nodeid={1}&province={2}";
		    String urlPattern="http://www.sgmw.com.cn/templates/T_ServiceOperator/PageList.aspx?chexing={0}&nodeid={1}&province={2}&citys={3}&pagesize={4}";
			//Map<String,String> chexings=new HashMap<String, String>();
			//chexings.put("0", "五菱");
			//chexings.put("1", "宝骏 ");
			String[] provinces={"安徽","北京","福建","甘肃","广东","广西","贵州","海南","河北","河南","黑龙江","湖北","湖南","吉林","江苏","江西","辽宁","内蒙","内蒙古","宁夏","青海","山东","山西","陕西","上海","四川","天津","新疆","云南","浙江","重庆"};
			//for(Iterator<String> it=chexings.keySet().iterator();it.hasNext();){
			Set<String> unkeys=new HashSet<String>();
			for(String chexing:new String[]{"0","1"}){
			  
				String chexingname=chexing.equals("0")?"五菱":"宝骏";
				for(String province:provinces){
					String cityurl=MessageFormat.format(urlCityPattern, chexing,"29",province);
					String cityContext=WebUtil.DoPost(cityurl,new ArrayList<NameValuePair>(),new HashMap<String, File>());
					//潮州|潮州,东莞|东莞,佛山|佛山,广州|广州,河源|河源,惠州|惠州,江门|江门,茂名|茂名,梅州|梅州,汕头|汕头,韶关|韶关,深圳|深圳,湛江|湛江,肇庆|肇庆,中山|中山,珠海|珠海
					String[] citys=cityContext.split(",");
				
					for(String city:citys){
						String cityname="";
						try{
							cityname=city.split("\\|")[1];
						}catch(Exception ex){continue;}	
						for(int pagesize=1;pagesize<=5;pagesize++){
							String dataUrl=MessageFormat.format(urlPattern, chexing,"29",province,cityname,pagesize);
							try{
							String dataContext=WebUtil.DoPost(dataUrl,new ArrayList<NameValuePair>(),new HashMap<String, File>());
							dataContext=dataContext.substring(dataContext.indexOf("<tr class=\"tr_none\">")+5,dataContext.length());
							dataContext=dataContext.substring(dataContext.indexOf("<tr>"),dataContext.indexOf("</table>"));
							String[] trShops=dataContext.split("</tr>");
							for(String trShop :trShops){
							if(StringUtil.isBlank(trShop))continue;
							String[] tdShops=trShop.split("</td>");
							XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
							  try{
									xCarShop4sVO.setName(tdShops[0].substring(tdShops[0].indexOf("<td>")+4).trim());
									xCarShop4sVO.setAddress(tdShops[1].substring(tdShops[1].indexOf(">")+1).trim());
									xCarShop4sVO.setSell_phone(tdShops[2].substring(tdShops[2].indexOf(">")+1).trim());
									xCarShop4sVO.setService_phone(tdShops[2].substring(tdShops[2].indexOf(">")+1).trim());
									xCarShop4sVO.setCatalog("上汽通用五菱  "+chexingname);
									xCarShop4sVO.setUuid(UUID.randomUUID().toString());
									xCarShop4sVO.setPid(province);
									xCarShop4sVO.setRemark(MessageFormat.format("{0} {1} {2}", province,city,pagesize));
									String unkey=chexingname+"_"+xCarShop4sVO.getCatalog()+"_"+xCarShop4sVO.getName();
									if(unkeys.contains(unkey)){
										continue;
									}else{
									unkeys.add(unkey);
									dbUtil.insert(xCarShop4sVO);
									}
							  }catch(Exception ex){
								  ex.printStackTrace();
							  }
							}
							}catch(Exception ex){
								ex.printStackTrace();
								break;
								}
						}
					}
				}
			}
			
		}
	
	/*
	 * http://www.changansuzuki.com/khfw/search.php
	 * act 3
city 洪洞县
date Sat Jan 12 2013 08:08:09 GMT 0800
name 
pro 山西
ty 2
http://www.changansuzuki.com/khfw/xml/pro.xml
长安铃木
	 */
	@Test
	public void importSuzuki(){
		String cityUrl="http://www.changansuzuki.com/khfw/xml/pro.xml";
		String urlPattern="http://www.changansuzuki.com/khfw/search.php" ;  //?act={0}&city={1}&date={2}&name={3}&pro={4}&ty={5}";
		//Map<String,List<String>> prvCitys=new HashMap<String, List<String>>();
		String cityContext=WebUtil.DoPost(cityUrl,new HashMap<String, Object>(), new HashMap<String, File>());
		Document document=null;
		Date ndate=new Date();
		Element rootEl=null;
		try {
			document=DocumentHelper.parseText(cityContext);
			rootEl=document.getRootElement();
			List<Element> provNodes=rootEl.elements("province");
			for(Element provNode:provNodes){
			    String provName=provNode.attributeValue("name");
			    List<Element> cityEls=provNode.elements("city");
			    for(Element cityEl:cityEls){
			    	String cityName=cityEl.attributeValue("name");
			    	if("未设置".equals(cityName))continue;
			    	String url=MessageFormat.format(urlPattern, "3",cityName,ndate.toString(),"",provName,"2");	
			    	//广州泰润贸易有限公司|广州市番禺区新路140-146号(蔡二牌坊对面)|020-84664052||23.031715|113.329754@@广州日鼎汽车贸易有限公司|广州市白云区永平街白云大道北1363号|020-86055500|http://www.gzriding.cn/|23.22967|113.3023@@
			    	try{
			    		List<NameValuePair> nvps=new ArrayList<NameValuePair>();
			    		nvps.add(new BasicNameValuePair("act", "3"));
			    		nvps.add(new BasicNameValuePair("city", cityName));
			    		nvps.add(new BasicNameValuePair("date", MessageFormat.format("Sat Jan 12 {1} {0} GMT 0800", StringUtil.getCurTime(),StringUtil.getCurYear())));
			    		//nvps.add(new BasicNameValuePair("name","" ));
			    		nvps.add(new BasicNameValuePair("pro", provName));
			    		nvps.add(new BasicNameValuePair("ty", "2"));
			    		HttpPost post=new HttpPost(url);
			    		post.setHeader("Referer", "http://www.changansuzuki.com/khfw/sqcx.php?ty=2");
			    		post.setHeader("Host","www.changansuzuki.com");
			    		post.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			    		post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/20100101 Firefox/17.0)");
			    		
			    		HttpParams param=new BasicHttpParams();
			    		param.setParameter("act", "3");
			    		param.setParameter("city", cityName);
			    		param.setParameter("date", MessageFormat.format("Sat Jan 12 {1} {0} GMT 0800", StringUtil.getCurTime(),StringUtil.getCurYear()));
			    		param.setParameter("name", "");
			    		param.setParameter("pro", provName);
			    		param.setParameter("ty", "2");
			    		post.setParams(param);
			    		
			    		MultipartEntity entity = new MultipartEntity();
			    		//BasicHttpEntity entity=new BasicHttpEntity();
			    		
			    		for(NameValuePair nvp:nvps){
			    			try {
			    				String encodeVal=WebUtil.encode(nvp.getValue(),"utf8");
			    				StringBody stringBody=new StringBody(encodeVal);
			    				entity.addPart(nvp.getName(),stringBody);
			    			} catch (UnsupportedEncodingException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			}
			    		}
			    		post.setEntity(entity);
			    		
			    		Map<String,String> paramMap=new HashMap<String, String>();
			    		paramMap.put("act", "3");
			    		paramMap.put("city", cityName);
			    		paramMap.put("date", MessageFormat.format("Sun Jan 13 {1} {0} GMT 0800", StringUtil.getCurTime(),StringUtil.getCurYear()));
			    		paramMap.put("name", "");
			    		paramMap.put("pro", provName);
			    		paramMap.put("ty", "2");
			    		//String dataContext=WebUtil.DoPost(url, nvps, new HashMap<String, File>());
			    		//String dataContext=WebUtil.DoPost(post);
			    		String dataContext=WebUtil.httpPost(url, paramMap);
			    		if(dataContext.trim().contains("sorry"))continue;
			    		String[] datas=dataContext.split("@@");
			    		for(String data:datas){
			    			try{
			    			String[] arr_data=data.replace("\\|\\|", "\\|").split("\\|");
			    			XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
							xCarShop4sVO.setName(arr_data[0].trim());
							xCarShop4sVO.setAddress(arr_data[1].trim());
							xCarShop4sVO.setSell_phone(arr_data[2].trim());
							xCarShop4sVO.setService_phone(arr_data[2].trim());
							xCarShop4sVO.setCatalog("长安铃木 ");
							xCarShop4sVO.setUuid(UUID.randomUUID().toString());
							xCarShop4sVO.setPid(provName);
							xCarShop4sVO.setRemark(MessageFormat.format("{0} {1} ", provName,cityName));
							
							dbUtil.insert(xCarShop4sVO);
			    			}catch(Exception ex){ex.printStackTrace();}
			    		}
			    	}catch(Exception ex){
			    		ex.printStackTrace();
			    	}
			    }
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*保时捷
	 * http://www.porsche.com/all/dealer2/GetLocationsWebService.asmx/GetLocations?market=china&siteId=china&language=zh&_locationType=Search.LocationTypes.Dealer&searchMode=location&searchKey=116150

	 */
	@Test
	public void importPorsche(){
		String urlPattern="http://www.porsche.com/all/dealer2/GetLocationsWebService.asmx/GetLocations?market={0}&siteId={1}&language={2}&_locationType={3}&searchMode={4}&searchKey={5}";
	    Map<String,String> shop4s=new HashMap<String, String>();
		shop4s.put("116086","北京长安保时捷中心");
		shop4s.put("116142","北京金港保时捷中心");
		shop4s.put("116108","北京海淀保时捷中心");
		shop4s.put("116090","北京亦庄保时捷中心");
		shop4s.put("116132","长春保时捷中心");
		shop4s.put("116099","长沙保时捷中心");
		shop4s.put("116080","成都金牛保时捷中心");
		shop4s.put("116083","重庆人和保时捷中心");
		shop4s.put("116093","大连保时捷中心");
		shop4s.put("116116","东莞保时捷中心");
		shop4s.put("116102","福州保时捷中心");
		shop4s.put("116081","广州保时捷中心");
		shop4s.put("116124","海口保时捷中心");
		shop4s.put("116130","杭州滨江保时捷中心");
		shop4s.put("116082","杭州西湖保时捷中心");
		shop4s.put("116098","哈尔滨保时捷中心");
		shop4s.put("116140","合肥保时捷中心");
		shop4s.put("116150","呼和浩特保时捷中心");
		shop4s.put("116107","香港保时捷中心");
		shop4s.put("116144","济南保时捷中心");
		shop4s.put("116109","金华保时捷中心");
		shop4s.put("116095","昆明保时捷中心");
		shop4s.put("116146","兰州保时捷中心");
		shop4s.put("116106","澳门保时捷中心");
		shop4s.put("116096","南京保时捷中心");
		shop4s.put("116134","南宁保时捷中心");
		shop4s.put("116101","宁波保时捷中心");
		shop4s.put("116138","鄂尔多斯保时捷中心");
		shop4s.put("116091","青岛保时捷中心");
		shop4s.put("116118","上海浦东保时捷中心");
		shop4s.put("116087","上海浦西保时捷中心");
		shop4s.put("116092","沈阳保时捷中心");
		shop4s.put("116097","深圳保时捷中心");
		shop4s.put("116128","苏州保时捷中心");
		shop4s.put("116122","太原保时捷中心");
		shop4s.put("116148","唐山保时捷中心");
		shop4s.put("116085","天津保时捷中心");
		shop4s.put("116125","台州保时捷中心");
		shop4s.put("116084","温州鹿城保时捷中心");
		shop4s.put("116112","温州瓯海保时捷中心");
		shop4s.put("116136","武汉光谷保时捷中心");
		shop4s.put("116088","武汉盘龙保时捷中心");
		shop4s.put("116152","无锡保时捷中心");
		shop4s.put("116089","厦门鹭江保时捷中心");
		shop4s.put("116119","厦门翔安保时捷中心");
		shop4s.put("116094","西安保时捷中心");
		shop4s.put("116113","郑州保时捷中心");
		for(Iterator<String> it=shop4s.keySet().iterator();it.hasNext();){
			String code=it.next();
			String name=shop4s.get(code);
			//market=china&siteId=china&language=zh&_locationType=Search.LocationTypes.Dealer&searchMode=location&searchKey=116150
			String url=MessageFormat.format(urlPattern, 
					"china"
					,"china"
					,"zh"
					,"Search.LocationTypes.Dealer"
					,"location"
					,code
					);
    		List<NameValuePair> nvps=new ArrayList<NameValuePair>();
    		nvps.add(new BasicNameValuePair("market", "china"));
    		nvps.add(new BasicNameValuePair("siteId", "china"));
    		nvps.add(new BasicNameValuePair("language", "zh"));
    		nvps.add(new BasicNameValuePair("_locationType","Search.LocationTypes.Dealer" ));
    		nvps.add(new BasicNameValuePair("searchMode", "location"));
    		nvps.add(new BasicNameValuePair("searchKey", code));
    		XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
    		String context="";
    		try{
			HttpGet httpGet=new HttpGet(url);
			context=WebUtil.DoGet(httpGet);
			//Document document=DocumentHelper.parseText(context);
			//System.err.println(document.asXML());
			//Element rootEl=document.getRootElement();
			/* 
			 * <Name>呼和浩特保时捷中心</Name>
      <AddressData>
        <PostCode>10051</PostCode>
        <City>呼和浩特市</City>
        <Street>新城区兴安北路甲40号 </Street>
        <Phone>0471-3268911</Phone>
        <Fax>0471-3292911</Fax>
      </AddressData>
      <Url1>http://www.porsche-hohhot.com</Url1>
      <Email1>info@porsche-hohhot.com</Email1>
			 * */
			
			
			/*
			for(Element element :(List<Element>)rootEl()){
		       if("Name".equals(element.getName())){
		    	   xCarShop4sVO.setName(element.getText());
		       }else  if("Street".equals(element.getName())){
		    	   xCarShop4sVO.setAddress(element.getText());
		       }	else if("Phone".equals(element.getName())){
		    	   xCarShop4sVO.setSell_phone(element.getText());
		       }	else if("Fax".equals(element.getName())){
		    	   xCarShop4sVO.setService_phone(element.getText());
		       }	
			}*/
			xCarShop4sVO.setName(context.substring(context.indexOf("<Name>")+6, context.indexOf("</Name>")));
			xCarShop4sVO.setAddress(context.substring(context.indexOf("<Street>")+8, context.indexOf("</Street>")));
			xCarShop4sVO.setSell_phone(context.substring(context.indexOf("<Phone>")+7, context.indexOf("</Phone>")));
			xCarShop4sVO.setSell_phone(context.substring(context.indexOf("<Fax>")+5, context.indexOf("</Fax>")));
			xCarShop4sVO.setCatalog("保时捷");
			xCarShop4sVO.setUuid(UUID.randomUUID().toString());
			//xCarShop4sVO.setPid(context.substring(context.indexOf("<PostCode>")+10, context.indexOf("</PostCode>")));
			xCarShop4sVO.setRemark(url);
			dbUtil.insert(xCarShop4sVO);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
	}
	
	//@Test
	public void importGQCQShop(){
		String urlPattern="http://trumpchi.gacmotor.com/2.0l/index.php/manage/dealer_iframe?pid={0}&p={1}";
		for(int pid=1;pid<40;pid++){
			for(int page=1;page<30;page++){
				String url=MessageFormat.format(urlPattern, pid,page);
				String context=WebUtil.DoPost(url, new ArrayList<NameValuePair>(), new HashMap<String, File>());
				context=context.substring(context.indexOf("<tbody>"),context.indexOf("</tbody>")-1);
				
				Document document=null;
				try {
					String[] trContexts=context.split("</tr>");
					for(String trContext:trContexts){
						String[] tdContexts=trContext.split("</td>");
						XCarShop4sVO xCarShop4sVO=new XCarShop4sVO();
						xCarShop4sVO.setName(tdContexts[0].substring(tdContexts[0].indexOf("<td>")+4,tdContexts[0].length()));
						xCarShop4sVO.setAddress(tdContexts[1].substring(tdContexts[1].indexOf("<td>")+4,tdContexts[1].length()));
						xCarShop4sVO.setSell_phone(tdContexts[2].substring(tdContexts[2].indexOf("<td>")+4,tdContexts[2].length()));
						//xCarShop4sVO.setAddress(tdNodes.get(1).getText());
						//xCarShop4sVO.setPhone(tdNodes.get(2).getText());
						xCarShop4sVO.setPid(String.valueOf(pid));
						xCarShop4sVO.setUuid(UUID.randomUUID().toString());
						xCarShop4sVO.setCatalog("广汽传祺");
						dbUtil.insert(xCarShop4sVO);
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public String FileInputStream(String path) throws IOException {
        File file=new File(path);
        if(!file.exists()||file.isDirectory()){}
            //throw new FileNotFoundException();
        FileInputStream fis=new FileInputStream(file);
        byte[] buf = new byte[1024];
        StringBuffer sb=new StringBuffer();
        while((fis.read(buf))!=-1){
            sb.append(new String(buf));    
            buf=new byte[1024];//重新生成，避免和上次读取的数据重复
        }
        return sb.toString();
    }
}
