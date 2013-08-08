/**
 * 
 */
package com.matech.audit.service.process.inter;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;

/**
 * @author bill
 *
 */
public interface NodeInterface extends EventListener {
	
	
	/**
	 * 节点进入时触发的事件
	 */
	void nodeStart(EventListenerExecution execution) ;
	
	/**
	 * 节点结束时触发的事件
	 */
	void nodeEnd(EventListenerExecution execution) ;
	
	
	/* (non-Javadoc)
	 * @see org.jbpm.api.listener.EventListener#notify(org.jbpm.api.listener.EventListenerExecution)
	 */
	public void notify(EventListenerExecution execution) ;
	

}
