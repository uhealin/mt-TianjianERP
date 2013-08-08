package com.matech.audit.service.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import org.jbpm.api.Execution;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.HistoryService;
import org.jbpm.api.ManagementService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.TaskService;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.model.Transition;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.env.EnvironmentFactory;
import org.jbpm.pvm.internal.env.EnvironmentImpl;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.model.TransitionImpl;

public class JbpmTemplate {
	
	private ProcessEngine processEngine ;
	private RepositoryService repositoryService = null ;
	private ExecutionService executionService = null ;
	private TaskService taskService = null ;
	private HistoryService historyService = null ;
	private ManagementService managementService = null ;
	
	public JbpmTemplate(ProcessEngine processEngine) {
		this.repositoryService = processEngine.getRepositoryService() ;
		this.executionService = processEngine.getExecutionService() ;
		this.taskService = processEngine.getTaskService() ;
		this.historyService = processEngine.getHistoryService() ;
		this.managementService = processEngine.getManagementService() ;
	}
	
	public JbpmTemplate() {
		
	}
	
	/**
	 * 把xml的流程定义文件发布出来
	 * 
	 * @param resourceName
	 *            资源文件名字 比如(process.jpdl.xml)
	 * @return 返回流程定义id(格式：key-version)
	 */
	public String deployByXml(String resourceName) {
		return repositoryService.createDeployment().addResourceFromClasspath(
				resourceName).deploy();
	}
	
	/**
	 * 把xml的流程定义文件发布出来
	 * 
	 * @param file
	 *            资源文件
	 * @return 返回流程定义id(格式：key-version)
	 */
	public String deployByFile(File file) {
		return repositoryService.createDeployment().addResourceFromFile(file).deploy();
	}
	
	/**
	 * 把zip包流程文件发布（zip包包含了xml文件和png流程图）
	 * 
	 * @param resourceName
	 *            资源文件名字 比如(process.zip)
	 * @return 返回流程定义id(格式：key-version)
	 */
	public String deployByZip(String resourceName) {
		String processDefineId = "" ;
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(resourceName))) ;
			//ZipInputStream zis = new ZipInputStream(this.getClass().getResourceAsStream(resourceName));
			processDefineId = repositoryService.createDeployment()
								 .addResourcesFromZipInputStream(zis).deploy() ;
			if(zis !=null)
				zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return processDefineId ;
	}
	
	/**
	 * 彻底删除流程的部署
	 * 
	 * @param deploymentId流程定义id
	 */
	public void deleteDeploymentCascade(String deploymentId) {
		repositoryService.deleteDeploymentCascade(deploymentId);
	}
	
	/**
	 * 启动一个新的流程实例
	 * 
	 * @param processDefinitionKey
	 *            (process.jpdl.xml中process标签的key)
	 * @param userId
	 *            流程申请人的id
	 * @return 流程实例对象
	 */
	public ProcessInstance startProcessByKey(String processDefinitionKey,
			String userId) {
		
		Map sMap = new HashMap() ;
		sMap.put("applyUser", userId) ;
		return executionService.startProcessInstanceByKey(processDefinitionKey,sMap); 

	}
	
	/**
	 * 启动一个新的流程实例
	 * 
	 * @param processDefinitionKey
	 *            (process.jpdl.xml中process标签的key)
	 * @return 流程实例对象
	 */
	public ProcessInstance startProcessByKey(String processDefinitionKey) {
		return executionService.startProcessInstanceByKey(processDefinitionKey);
	}
	
	
	/**
	 * 启动一个新的流程实例
	 * 
	 * @param processDefinitionKey
	 *            (process.jpdl.xml中process标签的key)
	 * @return 流程实例对象
	 */
	public ProcessInstance startProcessByKey(String processDefinitionKey,Map sMap) {
		return executionService.startProcessInstanceByKey(processDefinitionKey,sMap);
	}
	
	/**
	 * 启动一个新的流程实例
	 * 
	 * @param processDefinitionId
	 *           
	 * @return 流程实例对象
	 */
	public ProcessInstance startProcessById(String processDefinitionId) {
		return executionService.startProcessInstanceById(processDefinitionId) ;
	}
	
	/**
	 * 启动一个新的流程实例
	 * 
	 * @param processDefinitionId
	 *            
	 * @param sMap 流程变量         
	 * @return 流程实例对象
	 */
	public ProcessInstance startProcessById(String processDefinitionId,Map sMap) {
		return executionService.startProcessInstanceById(processDefinitionId,sMap) ;
	}
	
	/**
	 * 获取指定用户名字的任务
	 * @param userId
	 * @return
	 */
	public List<Task> findPersonalTasks(String userId){
		return taskService.findPersonalTasks(userId);
	}
	
	/**
	 * 获取指定用户名字的任务
	 * @param userId
	 * @return
	 */
	public List<Task> findGroupTasks(String userId){
		return taskService.findGroupTasks(userId);
	}
	
	/**
	 * 设置指定taskId 的流程变量
	 * @param taskId
	 * @return
	 */
	public void setTaskVariables(String taskId,Map variables){
		taskService.setVariables(taskId, variables) ;
		//taskService.findGroupTasks(arg0)
	}
	
	/**
	 * 提交任务
	 * @param taskId 任务id
	 */
	public void completeTask(String taskId){
		taskService.completeTask(taskId);
	}
	
	/**
	 * 提交任务
	 * @param taskId 任务id
	 */
	public void completeTask(String taskId,String nextName){
		taskService.completeTask(taskId,nextName);
	}
	
	/**
	 * 获取流程图
	 * @param processInstanceId 流程实例编号
	 */
	public InputStream getProcessImageByInstanceId(String processInstanceId){
		
		ProcessInstance pi = executionService.findProcessInstanceById(processInstanceId) ;
		String pdId = pi.getProcessDefinitionId() ;
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult() ;
		InputStream is = repositoryService.getResourceAsStream(pd.getDeploymentId(),pd.getImageResourceName()) ;
		return is ;
	}
	
	/**
	 * 根据流程发布id获得流程定义
	 * @param deploymentId 流程发布id
	 */
	public ProcessDefinition getProcessDefinitionByDeployId(String deploymentId){
		return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).uniqueResult();
	}
	
	/**
	 * 获取流程图
	 * @param processDefinitionId 流程定义编号
	 */
	public InputStream getProcessImageByDefinitionId(String processDefinitionId){
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).uniqueResult() ;
		InputStream is = repositoryService.getResourceAsStream(pd.getDeploymentId(),pd.getImageResourceName()) ;
		return is ;
	}
	
	/**
	 * 根据流程发布id获得流程定义
	 * @param deploymentId 流程发布id
	 */
	public ProcessDefinition getProcessDefinition(String deploymentId){
		return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).uniqueResult();
	}
	
	/**
	 * 根据流程定义id获得流程定义
	 * @param pdid 流程发布id
	 */
	public ProcessDefinition getProcessDefinitionByPdid(String pdid){
		return repositoryService.createProcessDefinitionQuery().processDefinitionId(pdid).uniqueResult() ;
	}
	
	/**
	 * 获取流程图
	 * @param processDefinitionKey 流程定义key
	 */
	public InputStream getProcessImageByDefinitionKey(String processDefinitionKey){
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).uniqueResult() ;
		InputStream is = repositoryService.getResourceAsStream(pd.getDeploymentId(),pd.getImageResourceName()) ;
		return is ;
	}
	
	/**
	 * 根据流程实例id获取流程图活动结点的坐标
	 * @param processInstanceId 流程实例编号
	 */
	public ActivityCoordinates getActivityCoordinates(String processInstanceId){
		
		ProcessInstance processInstance = executionService.findProcessInstanceById(processInstanceId);
		Set<String> activityNames = processInstance.findActiveActivityNames();
		ActivityCoordinates ac = repositoryService.getActivityCoordinates(processInstance.getProcessDefinitionId(),activityNames.iterator().next());
		return ac ;
	}
	
	/**
	 * 获得当前任务结点的名称
	 * @param taskId 任务id
	 */
	public String getActivityName(String taskId){
		
		return taskService.getTask(taskId).getActivityName() ;
	}
	
	/**
	 * 获得当前任务结点的名称
	 * @param taskId 任务id
	 */
	public String getProcessInstanceId(String taskId){
		return taskService.getTask(taskId).getExecutionId() ;
	}
	
	/**
	 * 获得当前任务结点绑定的form
	 * @param taskId 任务id
	 */
	public String getFormResourceName(String taskId){
		//executionService.signalExecutionById(arg0)
		return taskService.getTask(taskId).getFormResourceName() ;
	}
	
	/**
	 * 获得当前活跃结点的名字
	 * @param taskId 任务id
	 */
	public String getActiveName(String processInstanceId){
		ProcessInstance processInstance = executionService.findProcessInstanceById(processInstanceId);
		Set<String> activityNames = processInstance.findActiveActivityNames();
		return activityNames.iterator().next() ;
	}
	
	/**
	 * 完成state结点
	 * @param taskId 任务id
	 */
	public void signalExecutionById(String processInstanceId,String activeName){
		ProcessInstance processInstance = executionService.findProcessInstanceById(processInstanceId);
		Execution execution = processInstance.findActiveExecutionIn(activeName) ;
		executionService.signalExecutionById(execution.getId()) ;
	}
	
	/**  
	* 动态创建连接当前任务节点至名称为destName的节点的Transition  
	* @param taskId 任务节点ID  
	* @param sourceName 源节点名称  
	* @param desName  目标节点名称  
	*/ 
	 public void addOutTransition(ProcessDefinitionImpl pd,String sourceName,String desName){ 
		 EnvironmentFactory ef =(EnvironmentFactory)processEngine; 
		 EnvironmentImpl evti = null; 
		 try{ 
			 evti = ef.openEnvironment(); 
			 ActivityImpl sourceActivity = pd.getActivity(sourceName); 
			 TransitionImpl tran = sourceActivity.createOutgoingTransition(); 
			 ActivityImpl desActivity = pd.getActivity(desName); 
	
			 tran.setName("to"+desName); 
			 tran.setDestination(desActivity); 
	
			 sourceActivity.addOutgoingTransition(tran); 
		 }catch(Exception e){ 
			 e.printStackTrace(); 
		 }finally{ 
			 if(evti != null)evti.close(); 
		 } 
	}
	 
	 /**  
	 * 动态删除连接sourceName与destName的Transition  
	 * @param taskId  
	 * @param sourceName  
	 * @param desName  
	 */  
	 public void removeOutTransition(ProcessDefinitionImpl pd,String sourceName,String destName){   
		 EnvironmentFactory environmentFactory = (EnvironmentFactory) processEngine;   
		 EnvironmentImpl env=null;   
		 try {   
		      env = environmentFactory.openEnvironment();   
		      //取得当前流程的活动定义   
		      ActivityImpl sourceActivity = pd.findActivity(sourceName);   
		         
		      //若存在这个连接，则需要把该连接删除   
		      List<Transition> trans=(List<Transition>)sourceActivity.getOutgoingTransitions();   
		      for(Transition tran:trans){   
		     if(destName.equals(tran.getDestination().getName())){//删除该连接   
		             trans.remove(tran);   
		             break;   
		         }   
		      }   
		 }catch(Exception ex){   
		      ex.printStackTrace() ;
		 }finally{   
		      if(env!=null)env.close();   
		 }   
	 }  
	 
	 /**
	  * 获得流程变量
	 * @param taskId 任务id
	 * @param variableKey 流程变量key
	 * @return
	 */
	public Object getVariable(String taskId,String variableKey) {
			
		return this.executionService.getVariable(this.getProcessInstanceId(taskId), variableKey) ;
	}
	
	public Task getActivityTask(String pId) {
		TaskQuery tq = taskService.createTaskQuery() ;   
		List<Task> taskList = tq.processInstanceId(pId).list();
		if(taskList.size() > 0) {
			Task task = taskList.get(0) ;
			return task ;
		}else {
			return null ;
		}
	}
	
	public void delProcessInstance(String pId) {
		this.getExecutionService().deleteProcessInstanceCascade(pId);
	}

	
	public ProcessEngine getProcessEngine() {
		return processEngine;
	}

	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public ExecutionService getExecutionService() {
		return executionService;
	}

	public void setExecutionService(ExecutionService executionService) {
		this.executionService = executionService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public HistoryService getHistoryService() {
		return historyService;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	public ManagementService getManagementService() {
		return managementService;
	}

	public void setManagementService(ManagementService managementService) {
		this.managementService = managementService;
	} 
	
}
