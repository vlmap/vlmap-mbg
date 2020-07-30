package com.github.vlmap.mbg.core;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.lang.reflect.Method;
import java.util.List;


public class IntrospectedTableUtils {
    public final static String IDENTIFIER_PROPERTY_NAME = "identifierPropertyName";


    public static String  getHbmModelClass(IntrospectedTable introspectedTable){
        return  introspectedTable.getTableConfigurationProperty("model");

    }

    /**
     * 忽略不支持的主键生成策略
     *
     * @param plugin
     * @return
     */
    public static boolean isIgnoreTableGenerator(PluginAdapter plugin) {


        String property = plugin.getProperties().getProperty("ignoreTableGenerator");

        if (StringUtils.isBlank(property)) {
            property = "false";
        }
        return BooleanUtils.toBoolean(property);
    }

    public static boolean isCreateEntityFile(PluginAdapter plugin) {
        String property = plugin.getProperties().getProperty("entityJavaFile");
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

    public static void setIdentifierPropertyName(IntrospectedTable introspectedTable, String name) {
        if (StringUtils.isNotBlank(name)) {
            introspectedTable.getTableConfiguration().addProperty(IDENTIFIER_PROPERTY_NAME, name);
        }

    }

    public static String geIdentityPropertyName(IntrospectedTable introspectedTable) {
        return introspectedTable.getTableConfiguration().getProperty(IDENTIFIER_PROPERTY_NAME);

    }

    public static String getWithIdentityColumnName(IntrospectedColumn introspectedColumn) {
        IntrospectedTable introspectedTable = introspectedColumn.getIntrospectedTable();
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns.contains(introspectedColumn)) {
            String identitfierProperty = introspectedTable.getTableConfiguration().getProperty(IDENTIFIER_PROPERTY_NAME);
            if (StringUtils.isNotBlank(identitfierProperty)) {
                return identitfierProperty + "." + introspectedColumn.getJavaProperty();
            }

        }
        return null;

//        return introspectedColumn.getProperties().getProperty(FULL_IDENTIFIER_PROPERTY);

    }

    public static  IntrospectedColumn withIdentityIntrospectedColumn(IntrospectedColumn introspectedColumn) {
        String property = getWithIdentityColumnName(introspectedColumn);
        return withIdentityIntrospectedColumn(introspectedColumn,property);

    }
    public static  IntrospectedColumn clone(IntrospectedColumn introspectedColumn){
        return withIdentityIntrospectedColumn(introspectedColumn,introspectedColumn.getJavaProperty());
    }
    public static  IntrospectedColumn withIdentityIntrospectedColumn(IntrospectedColumn introspectedColumn,String property) {
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
