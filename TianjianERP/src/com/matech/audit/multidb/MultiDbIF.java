package com.matech.audit.multidb;

import com.matech.framework.pub.exception.MatechException;
import java.sql.Connection;

public interface MultiDbIF extends com.matech.framework.multidb.MultiDbIF {
    /**
     * 创建客户数据库
     * @return boolean
     * @throws MatechException
     */
    public boolean createCustomerDb(String departID) throws MatechException;
    public boolean createCustomerDb(String sourceDatabaseName, String target) throws MatechException;

    /**
     * 遍历所有业务库，并执行SQL
     * @param conn Connection
     * @param strSql String
     * @return boolean
     * @throws MatechException
     */
    public boolean executeSqlAtAllDb(Connection conn,String strSql)throws MatechException;
    
    public void dropAllYwDb(Connection conn)throws MatechException;
}
