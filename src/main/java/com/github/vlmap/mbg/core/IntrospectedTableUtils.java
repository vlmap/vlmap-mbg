package com.github.vlmap.mbg.core;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.lang.reflect.Method;

public class IntrospectedTableUtils {
    private final static String FULL_IDENTIFIER_PROPERTY = "___fullKey___";


    public static String  getHbmModelClass(IntrospectedTable introspectedTable){
        return  introspectedTable.getTableConfigurationProperty("model");

    }
    public static boolean isIgnoreTableGenerator(IntrospectedTable introspectedTable) {
        String property =     introspectedTable.getTableConfiguration().getProperty("ignoreTableGenerator");

        if (StringUtils.isBlank(property)) {
            property = "false";
        }
        return BooleanUtils.toBoolean(property);
    }
    public static boolean createEntityFile(IntrospectedTable introspectedTable) {
        String property = introspectedTable.getTableConfigurationProperty("entityJavaFile");
        if (StringUtils.isBlank(property)) {
            property = "false";
        }
        return BooleanUtils.toBoolean(property);
    }
    public static boolean isHbmIntrospectedTable(IntrospectedTable introspectedTable){
        return StringUtils.isNotBlank(getHbmModelClass(introspectedTable));
    }

    public static AbstractEntityPersister entityPersiter(IntrospectedTable introspectedTable){
        return (AbstractEntityPersister)introspectedTable.getAttribute("entityPersister");
    }
    public static void entityPersiter(IntrospectedTable introspectedTable, AbstractEntityPersister entityPersister){
        introspectedTable.setAttribute("entityPersister", entityPersister);
    }



    public static  IntrospectedColumn withIdentityIntrospectedColumn(IntrospectedColumn introspectedColumn) {
        String property = introspectedColumn.getProperties().getProperty(FULL_IDENTIFIER_PROPERTY);
        if (StringUtils.isNotBlank(property)) {
            IntrospectedColumn column = new IntrospectedColumn();
            column.setJdbcType(introspectedColumn.getJdbcType());
            column.setJdbcTypeName(introspectedColumn.getJdbcTypeName());
            column.setTypeHandler(introspectedColumn.getTypeHandler());
            column.setActualColumnName(introspectedColumn.getActualColumnName());
            column.setFullyQualifiedJavaType(introspectedColumn.getFullyQualifiedJavaType());
            column.setAutoIncrement(introspectedColumn.isAutoIncrement());
            column.setColumnNameDelimited(introspectedColumn.isColumnNameDelimited());
            column.setContext(introspectedColumn.getContext());
            column.setDefaultValue(introspectedColumn.getDefaultValue());
            column.setGeneratedAlways(introspectedColumn.isGeneratedAlways());
            column.setGeneratedColumn(introspectedColumn.isGeneratedColumn());
            column.setIdentity(introspectedColumn.isIdentity());
            column.setIntrospectedTable(introspectedColumn.getIntrospectedTable());
            column.setLength(introspectedColumn.getLength());
            column.setNullable(introspectedColumn.isNullable());
            column.setRemarks(introspectedColumn.getRemarks());
            column.setScale(introspectedColumn.getScale());
            column.setSequenceColumn(introspectedColumn.isSequenceColumn());
            column.setTableAlias(introspectedColumn.getTableAlias());
            column.setProperties(introspectedColumn.getProperties());
            column.setJavaProperty(property);

            return column;
        }
        return introspectedColumn;

    }

    public static void setIdentifierPropertyfullKey( IntrospectedColumn column,String value){
        column.getProperties().setProperty(FULL_IDENTIFIER_PROPERTY, value);

    }

    public static void calculate(IntrospectedTable introspectedTable) {

         invoker(IntrospectedTable.class,introspectedTable, "calculateJavaClientAttributes");
        invoker(IntrospectedTable.class,introspectedTable, "calculateModelAttributes");
        invoker(IntrospectedTable.class,introspectedTable, "calculateXmlAttributes");

    }

    protected static void invoker(Class clazz,Object object, String methodName) {
        try {


            Method method =  clazz.getDeclaredMethod(methodName);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
