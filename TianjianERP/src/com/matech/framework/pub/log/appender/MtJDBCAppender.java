/**
 * 
 */
package com.matech.framework.pub.log.appender;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.util.StringUtil;

/**
 * @author bill
 * 
 */
public class MtJDBCAppender extends AppenderSkeleton implements
		org.apache.log4j.Appender {

	Log log = new Log(MtJDBCAppender.class);
	protected Connection connection = null;

	protected String sqlStatement = "";

	protected int bufferSize = 1;

	protected ArrayList buffer;

	protected ArrayList removes;

	public MtJDBCAppender() {
		super();
		buffer = new ArrayList(bufferSize);
		removes = new ArrayList(bufferSize);
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	protected void append(LoggingEvent event) {
		buffer.add(event);

		if (buffer.size() >= bufferSize)
			flushBuffer();
	}

	/*
	 * 关闭Appender,刷新缓存并关闭默认链接
	 */
	@Override
	public void close() {
		flushBuffer();

		try {
			if (connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			errorHandler.error("Error closing connection", e,
					ErrorCode.GENERIC_FAILURE);
		}
		this.closed = true;

	}

	/**
	 * 执行插入数据库sql
	 * 
	 * @param sql
	 * @throws SQLException
	 */
	protected void execute(String sql) {

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = new DBConnect().getConnect();

			stmt = conn.createStatement();
			log.debug("log4j执行sql:" + sql);
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			errorHandler.error("执行sql失败", e, ErrorCode.FLUSH_FAILURE);
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}

	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * 循环遍历缓存中的日志事件,从getLogStatement()中获取配置的sql 并发送给execute()方法执行
	 */
	public void flushBuffer() {
		removes.ensureCapacity(buffer.size());
		for (Iterator i = buffer.iterator(); i.hasNext();) {
			LoggingEvent logEvent = (LoggingEvent) i.next();
			String sql = getLogStatement(logEvent);
			String uuid = StringUtil.getUUID();
			sql = sql.replaceAll("%\\{UUID\\}", uuid);
			execute(sql);
			removes.add(logEvent);
		}
		buffer.removeAll(removes);
		removes.clear();
	}

	protected String getLogStatement(LoggingEvent event) {
		return getLayout().format(event);
	}

	/**
	 * @param s
	 */
	public void setSql(String s) {
		sqlStatement = s;
		if (getLayout() == null) {
			this.setLayout(new PatternLayout(s));
		} else {
			((PatternLayout) getLayout()).setConversionPattern(s);
		}
	}

	/**
	 * 返回未格式化前的sql
	 */
	public String getSql() {
		return sqlStatement;
	}

}
