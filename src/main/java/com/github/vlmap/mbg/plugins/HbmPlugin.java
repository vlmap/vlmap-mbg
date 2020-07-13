package com.github.vlmap.mbg.plugins;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.ResultMapWithoutBLOBsElementGenerator;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;


/**
 * mgb生成内容和自定义内容分离,该插件应该被最后被加载
 *
 * @author vlmap
 */
public class HbmPlugin extends PluginAdapter {


    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public void initialized(IntrospectedTable introspectedTable) {


        if (!isHbm(introspectedTable)) return;
        String modelClass = introspectedTable.getTableConfigurationProperty("model");


        EntityManagerFactory entityManagerFactory = null;
        try {
            Class clazz = Class.forName(modelClass);

            List<String> classList = new ArrayList<>();
            classList.add(clazz.getName());
            java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Class type = method.getReturnType();
                if (type != null) {
                    classList.add(type.getName());
                }
                for (Class c : method.getParameterTypes()) {
                    classList.add(c.getName());
                }


            }
            Properties properties = new Properties();
            properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
            PersistenceUnitInfoDescriptor descriptor = new PersistenceUnitInfoDescriptor(new MutablePersistenceUnitInfo()) {
                @Override
                public List<String> getManagedClassNames() {
                    return classList;
                }

                @Override
                public String getName() {
                    return "defaut";
                }

                @Override
                public ClassLoader getTempClassLoader() {
                    return clazz.getClassLoader();
                }

                @Override
                public ClassLoader getClassLoader() {
                    return clazz.getClassLoader();
                }
            };

            entityManagerFactory = new EntityManagerFactoryBuilderImpl(descriptor
                    , properties).build();

            System.out.println(entityManagerFactory);
        } catch (Exception e) {
            return;
        }

        MetamodelImplementor metamodelImplementor = (MetamodelImplementor) entityManagerFactory.getMetamodel();
        AbstractEntityPersister entityPersister = (AbstractEntityPersister) metamodelImplementor.entityPersister(modelClass);
        introspectedTable.setAttribute("entityPersister",entityPersister);

        String identifierPropertyName = entityPersister.getIdentifierPropertyName();
        Type identifierType = entityPersister.getIdentifierType();
        List<IntrospectedColumn> introspectedColumns = new ArrayList<>();
        if (StringUtils.isNotBlank(identifierPropertyName)) {


            if (identifierType instanceof ComponentType) {
                String[] identifierColumnNames = entityPersister.getIdentifierColumnNames();

                ComponentType componentType = (ComponentType) identifierType;
                String[] propertyNames = componentType.getPropertyNames();

                for (int i = 0; i < propertyNames.length; i++) {
                    IntrospectedColumn column = introspectedTable.getColumn(identifierColumnNames[i]);
                    column.setJavaProperty(propertyNames[i]);
                    column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(componentType.getSubtypes()[i].getReturnedClass().getName()));
                    introspectedColumns.add(column);
                }

            }

        }


        String[] propertyNames = entityPersister.getPropertyNames();
        Type[] propertyTypes = entityPersister.getPropertyTypes();

        if (propertyNames != null) {
            for (int i = 0, size = propertyNames.length; i < size; i++) {
                String columnName = entityPersister.getPropertyColumnNames(propertyNames[i])[0];
                String javaType = propertyTypes[i].getReturnedClass().getName();
                String propertyName = propertyNames[i];

                IntrospectedColumn column = introspectedTable.getColumn(columnName);

                column.setJavaProperty(propertyName);
                column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(javaType));

                introspectedColumns.add(column);

            }

        }
        Iterator<IntrospectedColumn> iterator = introspectedTable.getBaseColumns().iterator();

        while (iterator.hasNext()) {
            IntrospectedColumn next = iterator.next();
            if (!introspectedColumns.contains(next)) {
                iterator.remove();
            }
        }
        iterator = introspectedTable.getBLOBColumns().iterator();

        while (iterator.hasNext()) {
            IntrospectedColumn next = iterator.next();
            if (!introspectedColumns.contains(next)) {
                iterator.remove();
            }
        }
        iterator = introspectedTable.getPrimaryKeyColumns().iterator();

        while (iterator.hasNext()) {
            IntrospectedColumn next = iterator.next();
            if (!introspectedColumns.contains(next)) {
                iterator.remove();
            }
        }
         introspectedTable.addPrimaryKeyColumn(identifierPropertyName);
         introspectedTable.setPrimaryKeyType(identifierType.getReturnedClass().getName());


    }

    boolean isHbm(IntrospectedTable introspectedTable) {
        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        String modelClass = introspectedTable.getTableConfigurationProperty("model");

        return StringUtils.isNotBlank(modelClass);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        AbstractEntityPersister entityPersister  =(AbstractEntityPersister)introspectedTable.getAttribute("entityPersister");
        if(entityPersister!=null){
            IntrospectedColumn primaryKey=new IntrospectedColumn();
            primaryKey.setJavaProperty(entityPersister.getIdentifierPropertyName());
            primaryKey.setFullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType() ));
            primaryKey.setIdentity(true);
            topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));

            topLevelClass.getFields().add(0,JavaBeansUtil.getJavaBeansField(primaryKey,context,introspectedTable));
            topLevelClass.getMethods().add(0,        JavaBeansUtil.getJavaBeansSetter(primaryKey,context,introspectedTable));
            topLevelClass.getMethods().add(0,      JavaBeansUtil.getJavaBeansGetter(primaryKey,context,introspectedTable));
            topLevelClass.setSuperClass((FullyQualifiedJavaType)null);
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {


        if (isHbm(introspectedTable)) {
            XmlElement associationElement = new XmlElement("association"); //$NON-NLS-1$


            associationElement.addAttribute(new Attribute(
                    "property", "id")); //$NON-NLS-1$
            associationElement.addAttribute(new Attribute(
                    "javaType", introspectedTable.getPrimaryKeyType())); //$NON-NLS-1$


            List<Element> elements = element.getElements();
            if (elements != null) {
                Iterator<Element> iterator = elements.iterator();
                while (iterator.hasNext()) {

                    Element next = iterator.next();
                    if (next instanceof XmlElement) {
                        XmlElement xml = (XmlElement) next;
                        if ("id".equals(xml.getName())) {
                            associationElement.addElement(xml);
                            iterator.remove();
                        }
                    }
                }

            }
            elements.add(0, associationElement);
        }
        return super.sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        return super.sqlMapResultMapWithBLOBsElementGenerated(element, introspectedTable);
    }
    //
//    @Override
//    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
//        if( Plugin.ModelClassType.PRIMARY_KEY==modelClassType){
//
//        }
//    }
//
//    @Override
//    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//
//        for (IntrospectedColumn introspectedColumn : introspectedTable
//                .getPrimaryKeyColumns()) {
//            if (RootClassInfo.getInstance(rootClass, warnings)
//                    .containsProperty(introspectedColumn)) {
//                continue;
//            }
//
//            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
//            if (plugins.modelFieldGenerated(field, topLevelClass,
//                    introspectedColumn, introspectedTable,
//                    Plugin.ModelClassType.PRIMARY_KEY)) {
//                topLevelClass.addField(field);
//                topLevelClass.addImportedType(field.getType());
//            }
//
//            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
//            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
//                    introspectedColumn, introspectedTable,
//                    Plugin.ModelClassType.PRIMARY_KEY)) {
//                topLevelClass.addMethod(method);
//            }
//
//            if (!introspectedTable.isImmutable()) {
//                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
//                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
//                        introspectedColumn, introspectedTable,
//                        Plugin.ModelClassType.PRIMARY_KEY)) {
//                    topLevelClass.addMethod(method);
//                }
//            }
//        }
//
//        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
//    }
}
