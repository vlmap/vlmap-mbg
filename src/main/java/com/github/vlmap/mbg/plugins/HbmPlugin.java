package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.HbmEntityUtils;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


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


        if (!IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) return;


        AbstractEntityPersister entityPersister =   HbmEntityUtils.entityPersister(introspectedTable);
         introspectedTable.getTableConfiguration().setDomainObjectName(entityPersister.getType().getReturnedClass().getSimpleName());
        String modelClass = IntrospectedTableUtils.getHbmModelClass(introspectedTable);
        introspectedTable.setBaseRecordType(modelClass);


        IntrospectedTableUtils.entityPersiter(introspectedTable, entityPersister);
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
                    String propertyName = propertyNames[i];
                    column.setJavaProperty(propertyName);
                    column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(componentType.getSubtypes()[i].getReturnedClass().getName()));
                    IntrospectedTableUtils.setIdentifierPropertyfullKey(column, identifierPropertyName + "." + propertyName);

                    introspectedColumns.add(column);
                }

            }
            introspectedTable.addPrimaryKeyColumn(identifierPropertyName);
        }
        introspectedTable.setPrimaryKeyType(identifierType.getReturnedClass().getName());



        Type[] propertyTypes = entityPersister.getPropertyTypes();

//        if(entityPersister.getEntityMetamodel().getIdentifierProperty().isEmbedded()){
//            if (propertyTypes != null) {
//                String[] propertyNames = entityPersister.getPropertyNames();
//
//                for (int i = 0, size = propertyNames.length; i < size; i++) {
//                    String columnName = entityPersister.getPropertyColumnNames(propertyNames[i])[0];
//                    String javaType = propertyTypes[i].getReturnedClass().getName();
//                    String propertyName = propertyNames[i];
//
//                    IntrospectedColumn column = introspectedTable.getColumn(columnName);
//
//                    column.setJavaProperty(propertyName);
//                    column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(javaType));
//
//                    introspectedColumns.add(column);
//
//                }
//
//            }
//        }else{
//             if (propertyTypes != null) {
                for (int i = 0, size = propertyTypes.length; i < size; i++) {
                    Type type=propertyTypes[i];
                    if(type instanceof  ComponentType){
                        ComponentType componentType = (ComponentType) type;
                        String[] propertyNames = componentType.getPropertyNames();

                        for (int j = 0; j < propertyNames.length; j++) {
                            String columnName = entityPersister.getPropertyColumnNames(propertyNames[j])[0];

                            IntrospectedColumn column = introspectedTable.getColumn(columnName);
                            String propertyName = propertyNames[j];
                            column.setJavaProperty(propertyName);
                            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(componentType.getSubtypes()[j].getReturnedClass().getName()));
                            if(StringUtils.isNotBlank(identifierPropertyName)){
                                IntrospectedTableUtils.setIdentifierPropertyfullKey(column, identifierPropertyName + "." + propertyName);

                            }

                            introspectedColumns.add(column);
                        }

//                        String columnName = entityPersister.getPropertyColumnNames(propertyNames[i])[0];
//
//                        String javaType = componentType.getReturnedClass().getName();
//                        String propertyName = propertyNames[i];
//
//                        IntrospectedColumn column = introspectedTable.getColumn(columnName);
//
//                        column.setJavaProperty(propertyName);
//                        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(javaType));
//
//                        introspectedColumns.add(column);
                    }else if(type instanceof AbstractSingleColumnStandardBasicType){
                        AbstractSingleColumnStandardBasicType standardBasicType=(AbstractSingleColumnStandardBasicType)type;
                        String propertyName = entityPersister.getPropertyNames()[i];

                        String columnName = entityPersister.getPropertyColumnNames(propertyName)[0];
                        String javaType = propertyTypes[i].getReturnedClass().getName();

                        IntrospectedColumn column = introspectedTable.getColumn(columnName);

                        column.setJavaProperty(propertyName);
                        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(javaType));

                        introspectedColumns.add(column);
                    }
//                    else {
//                        String[] propertyNames = entityPersister.getPropertyNames();
//                        Type[] propertyTypes = entityPersister.getPropertyTypes();
//                        if (propertyTypes != null) {
//                            for (int i = 0, size = propertyNames.length; i < size; i++) {
//                                String columnName = entityPersister.getPropertyColumnNames(propertyNames[i])[0];
//                                String javaType = propertyTypes[i].getReturnedClass().getName();
//                                String propertyName = propertyNames[i];
//
//                                IntrospectedColumn column = introspectedTable.getColumn(columnName);
//
//                                column.setJavaProperty(propertyName);
//                                column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(javaType));
//
//                                introspectedColumns.add(column);
//
//                            }
//
//                        }
//                    }

//
//                }
//
//            }
        }

        //只使用 Entity类返回的COlumn生成，其他清理掉
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



    }



    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        AbstractEntityPersister entityPersister = IntrospectedTableUtils.entityPersiter(introspectedTable);


        if(entityPersister!=null){
            Class primaryKeyType =entityPersister.getIdentifierType().getReturnedClass();

            if(StringUtils.isNotBlank( entityPersister.getIdentifierPropertyName())){
                IntrospectedColumn primaryKey=new IntrospectedColumn();
                primaryKey.setJavaProperty(entityPersister.getIdentifierPropertyName());
                primaryKey.setFullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType() ));
                primaryKey.setIdentity(true);
                topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));

                topLevelClass.getFields().add(0,JavaBeansUtil.getJavaBeansField(primaryKey,context,introspectedTable));
                topLevelClass.getMethods().add(0,        JavaBeansUtil.getJavaBeansSetter(primaryKey,context,introspectedTable));
                topLevelClass.getMethods().add(0,      JavaBeansUtil.getJavaBeansGetter(primaryKey,context,introspectedTable));
                FullyQualifiedJavaType supperClass=topLevelClass.getSuperClass();
                if(primaryKeyType!=null&&supperClass!=null&&primaryKeyType.getName().equals(supperClass.getFullyQualifiedName())){
                    topLevelClass.setSuperClass((FullyQualifiedJavaType)null);

                }
            }else {
                if(!entityPersister.getType().getReturnedClass().isAssignableFrom(primaryKeyType)){
                    List<IntrospectedColumn> primaryKeyColumns=   new ArrayList<>(introspectedTable.getPrimaryKeyColumns());
                    Collections.reverse(primaryKeyColumns);
                    for(IntrospectedColumn primaryKey :primaryKeyColumns){

                        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));

                        topLevelClass.getFields().add(0,JavaBeansUtil.getJavaBeansField(primaryKey,context,introspectedTable));
                        topLevelClass.getMethods().add(0,        JavaBeansUtil.getJavaBeansSetter(primaryKey,context,introspectedTable));
                        topLevelClass.getMethods().add(0,      JavaBeansUtil.getJavaBeansGetter(primaryKey,context,introspectedTable));
                        topLevelClass.setSuperClass((FullyQualifiedJavaType)null);
                    }
                }else{
                    FullyQualifiedJavaType supperClass=topLevelClass.getSuperClass();
                    if(primaryKeyType!=null&&supperClass!=null&&primaryKeyType.getName().equals(supperClass.getFullyQualifiedName())){
                        topLevelClass.setSuperClass((FullyQualifiedJavaType)null);

                    }
                }

            }

        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        AbstractEntityPersister entityPersister = IntrospectedTableUtils.entityPersiter(introspectedTable);

        if (entityPersister!=null&&StringUtils.isNotBlank(entityPersister.getIdentifierPropertyName())) {


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
            for (int i = elements.size() - 1; i > -1; i--) {
                Element e = elements.get(i);
                if (e instanceof XmlElement) {
                    XmlElement node = (XmlElement) e;
                    String name = node.getName();
                    if ("result".equals(name) || "id".equals(name) || "constructor".equals(name)) {
                        elements.add(i + 1, associationElement);
                        break;
                    }
                }
            }


        }
        return super.sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        return super.sqlMapResultMapWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            generatedWithIdentityIntrospectedColumn(element, new InsertElementGenerator(true), introspectedTable);


        }


        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }


    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {
            InsertSelectiveElementGenerator generator = new InsertSelectiveElementGenerator();

            generatedWithIdentityIntrospectedColumn(element, generator, introspectedTable);

        }
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    protected void generatedWithIdentityIntrospectedColumn(XmlElement element, AbstractXmlElementGenerator generator, IntrospectedTable introspectedTable) {

        DelegatingIntrospectedTable delegating = new DelegatingIntrospectedTable(introspectedTable.getTargetRuntime(), introspectedTable) {
            private List<IntrospectedColumn> withIdentityJavaProperty(List<IntrospectedColumn> columnList) {
                List<IntrospectedColumn> result = new ArrayList<>();
                if (columnList != null) {
                    for (IntrospectedColumn introspectedColumn : columnList) {
                        result.add(IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn));
                    }
                }
                return result;

            }

            @Override
            public List<IntrospectedColumn> getAllColumns() {

                return withIdentityJavaProperty(target.getAllColumns());
            }

            @Override
            public List<IntrospectedColumn> getBaseColumns() {
                return withIdentityJavaProperty(target.getBaseColumns());
            }

            @Override
            public List<IntrospectedColumn> getBLOBColumns() {
                return withIdentityJavaProperty(target.getBLOBColumns());
            }

            @Override
            public List<IntrospectedColumn> getNonBLOBColumns() {
                return withIdentityJavaProperty(target.getNonBLOBColumns());
            }

            @Override
            public List<IntrospectedColumn> getPrimaryKeyColumns() {
                return withIdentityJavaProperty(target.getPrimaryKeyColumns());
            }

            @Override
            public List<IntrospectedColumn> getNonPrimaryKeyColumns() {
                return withIdentityJavaProperty(target.getNonPrimaryKeyColumns());
            }
        };
        generator.setContext(new Context(null) {
            PluginAggregator pluginAggregator = new PluginAggregator();

            @Override
            public Plugin getPlugins() {
                return pluginAggregator;
            }
        });
        generator.setIntrospectedTable(delegating);

        XmlElement parent = new XmlElement("parent");
        generator.addElements(parent);
        XmlElement node = (XmlElement) parent.getElements().get(0);
        element.getElements().clear();
        element.getElements().addAll(node.getElements());
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            generatedWithIdentityIntrospectedColumn(element, new UpdateByPrimaryKeySelectiveElementGenerator(), introspectedTable);

        }
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            generatedWithIdentityIntrospectedColumn(element, new UpdateByPrimaryKeyWithoutBLOBsElementGenerator(true), introspectedTable);

        }
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            generatedWithIdentityIntrospectedColumn(element, new UpdateByPrimaryKeyWithBLOBsElementGenerator(), introspectedTable);

        }
        return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            generatedWithIdentityIntrospectedColumn(element, new UpdateByExampleSelectiveElementGenerator(), introspectedTable);

        }
        return super.sqlMapUpdateByExampleSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            generatedWithIdentityIntrospectedColumn(element, new UpdateByExampleWithBLOBsElementGenerator(), introspectedTable);

        }
        return super.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            generatedWithIdentityIntrospectedColumn(element, new UpdateByExampleWithoutBLOBsElementGenerator(), introspectedTable);

        }
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }
//    updateByExample
    //    updateByExampleSelective
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
