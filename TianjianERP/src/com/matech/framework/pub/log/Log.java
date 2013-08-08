/**
 * 
 */
package com.matech.framework.pub.log;

import org.apache.log4j.Logger;

/**
 * @author bill
 * 
 */
public class Log {
	private Logger log = null;

	protected Log() {
		
	}
	
	public Log(Class clazz) {
		log = Logger.getLogger(clazz);
	}

	/**
	 * 记录系统异常日志,通常放于catch块中记录
	 * 
	 * @param log
	 * @param e
	 */
	public void exception(Object logInfo, Exception e) {
		log.error(logInfo + " " + e.getMessage());
	}

	/**
	 * 记录系统运行日志,如系统启动，停止等信息
	 * 
	 * @param log
	 */
	public void sysInfo(Object logInfo) {
		log.fatal(logInfo);
	}

	/**
	 * 记录模块操作日志，如模块的修改和删除
	 * 
	 * @param log
	 */
	public void log(Object logInfo) {
		log.warn(logInfo);
	}

	/**
	 * 系统调试日志，放弃使用System.out.println吧
	 * 
	 * @param log
	 */
	public void debug(Object logInfo) {
		log.info(logInfo);
	}

}
