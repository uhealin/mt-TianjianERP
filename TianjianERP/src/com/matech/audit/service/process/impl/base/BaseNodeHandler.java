/**
 * 
 */
package com.matech.audit.service.process.impl.base;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.framework.pub.log.Log;

/**
 * @author bill
 *
 */
public class BaseNodeHandler extends NodeHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5139806931001407961L;
	Log log = new Log(BaseNodeHandler.class) ;

	@Override
	public void nodeStart(EventListenerExecution execution) {
		log.debug("流程节点进入") ;
		String aa = (String)execution.getVariable("测试1") ;
		log.debug("流程节点进入xxxxxxxxxxxxxxxxxxxxxxxxxx"+aa) ;
	}

	
	@Override
	public void nodeEnd(EventListenerExecution execution) {
		log.debug("流程节点结束") ;
	}

}
