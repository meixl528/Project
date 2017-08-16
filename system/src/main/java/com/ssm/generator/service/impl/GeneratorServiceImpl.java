package com.ssm.generator.service.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ssm.generator.dto.DBColumn;
import com.ssm.generator.dto.DBTable;
import com.ssm.generator.dto.GeneratorInfo;
import com.ssm.generator.service.IGeneratorService;
import com.ssm.mybatis.util.StringUtil;

import freemarker.template.TemplateException;

/**
 */
@Service
public class GeneratorServiceImpl implements IGeneratorService {

    @Autowired
    @Qualifier("sqlSessionFactory")
    SqlSessionFactory sqlSessionFactory;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<String> showTables() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<String> tables;

            Connection conn = DBUtil.getConnectionBySqlSession(sqlSession);
            tables = DBUtil.showAllTables(conn);
            conn.close();
            return tables;
        } catch (SQLException e) {
            logger.error("数据库查询出错");
        }
        return new ArrayList<String>();
    }

    @Override
    public int generatorFile(GeneratorInfo info) {
        int rs = 0;
        String tableName = info.getTargetName();
        DBTable dbTable = getTableInfo(tableName);
        //FileUtil.generatorInfo = info;
        try {
            rs = createFile(dbTable, info);
        } catch (IOException e) {
            rs = -1;
            logger.error(e.getMessage());
        } catch (TemplateException e) {
            rs = -1;
            logger.error(e.getMessage());
        }

        return rs;
    }

    // 获取table信息
    public DBTable getTableInfo(String tableName) {
        Connection conn = null;
        DBTable dbTable = new DBTable();
        List<DBColumn> columns = dbTable.getColumns();
        List<String> multiColumns = null;
        List<String> NotNullColumns = null;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            // 设置tablename
            dbTable.setName(tableName);
            conn = DBUtil.getConnectionBySqlSession(sqlSession);
            DatabaseMetaData dbmd = conn.getMetaData();
            // 是否为多语言表
            boolean multiLanguage = DBUtil.isMultiLanguageTable(tableName);
            if (multiLanguage) {
                dbTable.setMultiLanguage(multiLanguage);
                multiColumns = DBUtil.isMultiLanguageColumn(tableName, dbmd);
                // 判断多语言字段
            }
            // 获取主键字段
            String columnPk = DBUtil.getPrimaryKey(tableName, dbmd);
            // 获取不为空的字段
            NotNullColumns = DBUtil.getNotNullColumn(tableName, dbmd);
            // 获取表列信息
            ResultSet rs1 = DBUtil.getTableColumnInfo(tableName, dbmd);
            while (rs1.next()) {
                String columnName = rs1.getString("COLUMN_NAME");
                if ("OBJECT_VERSION_NUMBER".equalsIgnoreCase(columnName)) {
                    break;
                }
                columns.add(setColumnInfo(rs1, columnPk, NotNullColumns, multiLanguage, multiColumns));
            }
            // 是否是多语言表
            rs1.close();
            conn.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return dbTable;
    }

    private DBColumn setColumnInfo(ResultSet rs1, String columnPk, List<String> NotNullColumns, boolean multiLanguage, List<String> multiColumns) throws SQLException {
        DBColumn column = new DBColumn();
        String columnName = rs1.getString("COLUMN_NAME");
        column.setName(columnName);
        String typeName = rs1.getString("TYPE_NAME");
        column.setJdbcType(typeName);
        if (StringUtil.isNotEmpty(rs1.getString("REMARKS"))) {
            column.setRemarks(rs1.getString("REMARKS"));
        }
        // 判断是否为主键
        if (columnName.equalsIgnoreCase(columnPk)) {
            column.setId(true);
        }
        // 判断是否为null字段
        for (String n : NotNullColumns) {
            if (columnName.equalsIgnoreCase(n) && !columnName.equalsIgnoreCase(columnPk)) {
                if ("BIGINT".equalsIgnoreCase(typeName)) {
                    column.setNotNull(true);
                } else if ("VARCHAR".equalsIgnoreCase(typeName)) {
                    column.setNotEmpty(true);
                }
            }
        }
        // 判断多语言表中的多语言字段
        if (multiLanguage) {
            for (String m : multiColumns) {
                if (m.equals(columnName)) {
                    column.setMultiLanguage(true);
                    break;
                }
            }
        }
        return column;
    }

    public int createFile(DBTable table, GeneratorInfo info) throws IOException, TemplateException {

        int rs = FileUtil.isFileExist(info);
        if (rs == 0) {
            if (!"NotOperation".equalsIgnoreCase(info.getDtoStatus())) {
                FileUtil.createDto(table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getControllerStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Controller, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getMapperStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Mapper, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getImplStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Impl, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getServiceStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Service, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getMapperXmlStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.MapperXml, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getHtmlStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Html, table, info);
            }
        }
        return rs;
    }

}
