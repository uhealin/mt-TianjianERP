package com.matech.sms;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SmsSchedule
  implements Job
{
  public void execute(JobExecutionContext arg0)
    throws JobExecutionException
  {
	/*
    Connection conn = null;
    DbUtil dbUtil = null;
    List<SmsVO> smsVOs = null;
    SmsConfigVO smsConfigVO = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Date ndate = new Date();
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(11);
    int minute = cal.get(12);
    boolean allowSend = true;
    try {
      conn = new DBConnect().getConnect();
      dbUtil = new DbUtil(conn);
      smsConfigVO = (SmsConfigVO)dbUtil.load(SmsConfigVO.class, "sys");
      if (StringUtil.isBlank(smsConfigVO.getCode())) {
        System.out.println("未配置短信sys");
        return;
      }

      String[] start_hm = smsConfigVO.getStart_time().split(":");
      String[] end_hm = smsConfigVO.getEnd_time().split(":");
      int start_h = Integer.parseInt(start_hm[0]); int start_m = Integer.parseInt(start_hm[1]);
      int end_h = Integer.parseInt(end_hm[0]); int end_m = Integer.parseInt(end_hm[1]);

      if ((hour < start_h) || (hour > end_h))
        allowSend = false;
      else if ((hour == start_h) && (minute < start_m))
        allowSend = false;
      else if ((hour == end_h) && (minute > end_m)) {
        allowSend = false;
      }
      if (!allowSend)
      {
        System.out.println(MessageFormat.format("{0} 超过短信发送时间:{1} 至  {2}", new Object[] { StringUtil.getCurDateTime(), smsConfigVO.getStart_time(), smsConfigVO.getEnd_time() }));
        return;
      }

       smsVOs = dbUtil.select(SmsVO.class, "select * from {0} where send_time <=? and state not in (?, ?) ", new Object[] { StringUtil.getCurDate() + " " + StringUtil.getCurTime(), "s","d" });
      System.out.println(MessageFormat.format("{0} 扫描到  {1} 条待发短信,开始发送", new Object[] { StringUtil.getCurDateTime(), Integer.valueOf(smsVOs.size()) }));

      for (SmsVO smsVO : smsVOs) {
        Date sdate = sdf.parse(smsVO.getCreate_time());
        String re = "";
        if(smsVO.getMobile()==null||!smsVO.getMobile().matches("[0-9]{11}")){
        	smsVO.setSend_result("c");
        	dbUtil.update(smsVO);
        	continue;
        }
		String regexp=MessageFormat.format("于{0}年", StringUtil.getCurYear()),content=smsVO.getContext();
		if(content.contains(regexp+"0")){
			content=content.replace(regexp+"0", "于");
		}else if(content.contains(regexp)){
			content=content.replace(regexp, "于");
		}
		smsVO.setContext(content);
        if (StringUtil.isBlank(smsConfigVO.getTest_phone()))
        {
          if(StringUtil.isBlank(smsVO.getMobile())||smsVO.getMobile().length()<11){
        	  smsVO.setState("d");
          }else{
              re = SmsOpt.sendRealSm(smsVO.getMobile(), smsVO.getContext(), smsConfigVO);
          }
        }
        else {
          re = SmsOpt.sendRealSm(smsConfigVO.getTest_phone(), smsVO.getContext(), smsConfigVO);
        }
        smsVO.setClient_num(smsConfigVO.getClient_no());
        smsVO.setHost_ip(InetAddress.getLocalHost().getHostAddress());
        smsVO.setSend_result(re);

        System.out.println("=====================re=" + re);
        if ("1".equals(re))
        {
          System.out.println("=====================re11111111111111111111=" + re);
          smsVO.setReach_time(StringUtil.getCurDateTime());
          smsVO.setState("s");
        }
        dbUtil.update(new Object[] { smsVO });
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      DbUtil.close(conn);
    }
    */
  }
}