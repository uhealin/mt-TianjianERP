/**
 * 
 */
package com.matech.audit.service.process.impl.base;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.jbpm.api.TaskQuery;
import org.jbpm.api.listener.EventListenerExecution;
import org.jbpm.api.task.Task;


import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.process.JbpmServicce;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.inter.NodeInterface;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;

import com.matech.audit.work.process.ProcessAction;
import com.matech.framework.pub.db.DbUtil;

import com.matech.framework.pub.util.StringUtil;

/**
 * @author bill
 *
 */
public abstract class NodeHandler implements NodeInterface {
	
	private String eventName ;
	private String msg ;
	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	
	private static final long serialVersionUID = 1L;

	/**
	 * 节点进入时触发的事件
	 */
	public abstract void nodeStart(EventListenerExecution execution) ;
	
	/**
	 * 节点结束时触发的事件
	 */
	public abstract void nodeEnd(EventListenerExecution execution) ;
	
	public void notify(EventListenerExecution execution) {
		
		if("start".equals(this.eventName)) {
			this.sendMsg(execution) ;
			this.nodeStart(execution) ;
		}else if("end".equals(this.eventName)) {
			this.nodeEnd(execution) ;
		}
	}
	
	private void sendMsg(EventListenerExecution execution){
		
		Connection conn = null;
		PlacardService placardService=null;
		//String pname= execution.getProcessInstance().getName();
		try {
			conn=new DBConnect().getConnect();
			String curDealUser = (String) execution.getVariable("curDealUser");
			String nextDealUser = (String) execution.getVariable("nextDealUser");
			String processName = (String) execution.getVariable("processName");
			String applyUser = (String) execution.getVariable("applyUser");
			String pKey = (String) execution.getVariable("pKey");
			if(msg != null && !"".equals(msg)) {
				
				placardService=new PlacardService(conn);
				//MessageService ms = new MessageService(conn) ;
				UserService us = new UserService(conn) ;
				JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate() ;
				
				//Message message = new Message() ;
				//message.setBatchId(StringUtil.getUUID()) ;
				//message.setIsRead("0") ;
				//message.setMsgTitle("流程待办任务提醒") ;
				//message.setMsgType("1") ;
				//message.setUuid(StringUtil.getUUID()) ;
				//message.setSendUserid(curDealUser) ;
				//message.setSendTime(StringUtil.getCurDateTime()) ;
				PlacardTable placardTable=new PlacardTable();
				placardTable.setIsReversion(0);
				placardTable.setAddresserTime(StringUtil.getCurDateTime());
				placardTable.setCaption(processName+" 流程待办任务提醒");
				placardTable.setMatter("你有1个 "+processName+" 流程未处理");
				placardTable.setIsRead(0);
				placardTable.setIsNotReversion(0);
				placardTable.setUuid(UUID.randomUUID().toString());
				placardTable.setUrl(ProcessAction.PATH_AUDIT_LIST(pKey));
				placardTable.setUuidName("uuid");
				placardTable.setModel("内部邮件");
				placardTable.setAddresser( "19");//发起
				placardTable.setAddressee(curDealUser);//发起
				placardService.AddPlacard(placardTable);
				if(msg.indexOf("sLetter") > -1) {
					//给发起人发送站内短信
					
					
					User user = us.getUser(applyUser, "id") ;
					String curDealUserName = "" ;
					if(user != null) curDealUserName = user.getName() ;
					/*
					message.setReceiveUserid(applyUser) ;
					message.setMsgParam("");
					message.setMessageContent("您发起的【" + processName + "】已经被【"+curDealUserName+"】处理,请查看!") ;
					
					ms.addMessage(message);
					*/
				    placardTable=new PlacardTable();
					placardTable.setIsReversion(0);
					placardTable.setAddresserTime(StringUtil.getCurDateTime());
					placardTable.setCaption(processName+" 流程办理提醒");
					placardTable.setMatter("您发起的【" + processName + "】已经被【"+curDealUserName+"】处理,请查看!");
					placardTable.setIsRead(0);
					placardTable.setIsNotReversion(0);
					placardTable.setUuid(UUID.randomUUID().toString());
					placardTable.setUrl(ProcessAction.PATH_AUDIT_LIST(pKey));
					placardTable.setUuidName("uuid");
					placardTable.setModel("内部邮件");
					placardTable.setAddresser( "19");//发起
					placardTable.setAddressee(applyUser);//发起
					placardService.AddPlacard(placardTable);
				}
				if(msg.indexOf("sMsg") > -1) {
					//给发起人发送手机短信
				}
				if(msg.indexOf("dLetter") > -1) {
					
					TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
					List<Task> taskList2 = tq.executionId(execution.getId()).list();  				
					String taskId = "" ;
					if(taskList2.size() > 0) {
						Task myTask = taskList2.get(0) ;
						taskId = myTask.getId() ;
					}
					//给办理人发送站内短信 
					if(nextDealUser != null && !"".equals(nextDealUser)) {
						//message.setReceiveUserid(nextDealUser) ;
						//message.setMsgParam("&taskId="+taskId+"&pKey="+pKey);
						//message.setMessageContent("流程【" + processName + "】等待您处理,请及时办理!") ;
						
						//ms.addMessage(message);
					    placardTable=new PlacardTable();
						placardTable.setIsReversion(0);
						placardTable.setAddresserTime(StringUtil.getCurDateTime());
						placardTable.setCaption(processName+" 流程办理提醒");
						placardTable.setMatter("流程【" + processName + "】等待您处理,请及时办理!");
						placardTable.setIsRead(0);
						placardTable.setIsNotReversion(0);
						placardTable.setUuid(UUID.randomUUID().toString());
						placardTable.setUrl(ProcessAction.PATH_AUDIT_LIST(pKey));
						placardTable.setUuidName("uuid");
						placardTable.setModel("内部邮件");
						placardTable.setAddresser(curDealUser );//发起
						placardTable.setAddressee(nextDealUser);//发起
						placardService.AddPlacard(placardTable);
					}
				}
				if(msg.indexOf("dMsg") > -1) {
					//给办理人发送手机短信
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
	}

	
}
