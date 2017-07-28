package com.ssm.generator.service.impl;

import org.apache.ibatis.session.SqlSession;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DBUtil {

    private static final String COLUMN_NAME = "COLUMN_NAME";

    private DBUtil() {

    }

    public static Connection getConnectionBySqlSession(SqlSession sqlSession) throws SQLException {
        return sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection();
    }

    public static List<String> showAllTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData dbmd = conn.getMetaData();
        String database = conn.getCatalog();
        ResultSet rs = dbmd.getTables(database, null, null, new String[] { "TABLE" });
        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME"));
        }
        return tables;
    }

    public static ResultSet getTableColumnInfo(String table, DatabaseMetaData dbmd) throws SQLException {
        return dbmd.getColumns(null, null, table, null);
    }

    public static boolean isMultiLanguageTable(String table) throws SQLException {
        boolean result = false;
        if (table.toUpperCase().endsWith("_B")) {
            result = true;
        }
        return result;
    }

    public static List<String> getNotNullColumn(String table, DatabaseMetaData dbmd) throws SQLException {
        List<String> result = new ArrayList<>();
        ResultSet rs = dbmd.getColumns(null, null, table, null);
        while (rs.next()) {
            if ("NO".equalsIgnoreCase(rs.getString("IS_NULLABLE"))) {
                result.add(rs.getString(DBUtil.COLUMN_NAME));
            }
        }
        return result;
    }

    public static List<String> isMultiLanguageColumn(String table, DatabaseMetaData dbmd) throws SQLException {
        List<String> result = new ArrayList<>();
        String tlTable = table.substring(0, table.length() - 2) + "_tl";
        ResultSet rs = getTableColumnInfo(tlTable, dbmd);
        boolean key = false;
        while (rs.next()) {
            if ("OBJECT_VERSION_NUMBER".equalsIgnoreCase(rs.getString(DBUtil.COLUMN_NAME))) {
                break;
            }
            if (key) {
                result.add(rs.getString(DBUtil.COLUMN_NAME));
            }
            if ("LANG".equalsIgnoreCase(rs.getString(DBUtil.COLUMN_NAME))) {
                key = true;
            }
        }
        return result;
    }

    public static String getPrimaryKey(String table, DatabaseMetaData dbmd) throws SQLException {
        String columnPK = null;
        ResultSet rs = dbmd.getPrimaryKeys(null, null, table);
        while (rs.next()) {
            columnPK = rs.getString(DBUtil.COLUMN_NAME);
        }
        return columnPK;
    }
}
