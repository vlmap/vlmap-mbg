package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.HbmEntityUtils;
import com.github.vlmap.mbg.core.IdentityDelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.io.File;
import java.util.*;


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

    protected void calculateIdentifierGenerator(IntrospectedTable introspectedTable, AbstractEntityPersister entityPersister) {
        SessionFactoryImplementor entityManagerFactory = (SessionFactoryImplementor) introspectedTable.getAttribute("sessionFactoryImplementor");

        Dialect dialect = entityManagerFactory.getJdbcServices().getDialect();
        IdentifierGenerator identifierGenerator = entityPersister.getIdentifierGenerator();
        GeneratedKey key = null;
        //处理主键生成策略
        if (identifierGenerator instanceof SequenceStyleGenerator) {
            SequenceStyleGenerator generator = (SequenceStyleGenerator) identifierGenerator;

            String select = dialect.getSequenceNextValString(Objects.toString(generator.generatorKey()));
            GeneratedKey generatedKey = introspectedTable.getTableConfiguration().getGeneratedKey();
            if (generatedKey == null) {
                key = new GeneratedKey(entityPersister.getIdentifierColumnNames()[0], select, true, null);
            } else {
                key = new GeneratedKey(generatedKey.getColumn(), select, generatedKey.isIdentity(), generatedKey.getType());
            }

        } else if (identifierGenerator instanceof IdentityGenerator) {


            String select = entityPersister.getIdentitySelectString();
            GeneratedKey generatedKey = introspectedTable.getTableConfiguration().getGeneratedKey();


            if (generatedKey == null) {
                key = new GeneratedKey(entityPersister.getIdentifierColumnNames()[0], select, true, null);
            } else {
                key = new GeneratedKey(generatedKey.getColumn(), select, generatedKey.isIdentity(), generatedKey.getType());
            }

        } else if (identifierGenerator instanceof TableGenerator) {
            if (!IntrospectedTableUtils.isIgnoreTableGenerator(introspectedTable)) {
                throw new IllegalArgumentException("不支持 Hibernate 生成策略为 GenerationType.TABLE的主键转换");
            }

        }

        if (key != null) {
            introspectedTable.getTableConfiguration().setGeneratedKey(key);
            introspectedTable.getColumn(key.getColumn()).setGeneratedAlways(true);
        }

    }

    protected void calculateIntrospectedColumn(IntrospectedTable introspectedTable, AbstractEntityPersister entityPersister, Type identifierType, String propertyName, List<IntrospectedColumn> collect) {
        //普通主键、单列对象
        if (identifierType instanceof SingleColumnType) {

            String columnName = entityPersister.getPropertyColumnNames(propertyName)[0];
            String javaType = identifierType.getReturnedClass().getName();

            IntrospectedColumn column = introspectedTable.getColumn(columnName);
            if (column == null) {
                String msg = String.format("Schema[%s] Table[%s] Column[%s] not exist ,please check class[%s] field[%s]",
                        ObjectUtils.toString(introspectedTable.getTableConfiguration().getSchema()),
                        introspectedTable.getTableConfiguration().getTableName(),
                        columnName,
                        entityPersister.getEntityName(),
                        propertyName);
                throw new IllegalArgumentException(msg);

            }
            column.setJavaProperty(propertyName);
            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(javaType));

            collect.add(column);
        }
        //复合主键、复合对象
        if (identifierType instanceof ComponentType) {
            String[] identifierColumnNames = entityPersister.getIdentifierColumnNames();
            ComponentType componentType = (ComponentType) identifierType;
            String[] propertyNames = componentType.getPropertyNames();
            for (int i = 0; i < propertyNames.length; i++) {
                String columnName = identifierColumnNames[i];
                IntrospectedColumn column = introspectedTable.getColumn(columnName);
                if (column == null) {
                    String msg = String.format("Schema[%s] Table[%s] Column[%s] not exist ,please check class[%s] field[%s]",
                            ObjectUtils.toString(introspectedTable.getTableConfiguration().getSchema()),
                            introspectedTable.getTableConfiguration().getTableName(),
                            columnName,
                            entityPersister.getEntityName(),
                            propertyName);
                    throw new IllegalArgumentException(msg);

                }
                column.setJavaProperty(propertyNames[i]);
                column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(componentType.getSubtypes()[i].getReturnedClass().getName()));
                if (!componentType.isEmbedded()) {
                    IntrospectedTableUtils.setIdentifierPropertyfullKey(column, propertyName + "." + propertyNames[i]);
                }


                collect.add(column);
            }
//            introspectedTable.addPrimaryKeyColumn(identifierPropertyName);

        }
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {


        if (!IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) return;

        try {


            AbstractEntityPersister entityPersister = HbmEntityUtils.entityPersister(introspectedTable);


            IntrospectedTableUtils.entityPersiter(introspectedTable, entityPersister);

            Type identifierType = entityPersister.getIdentifierType();


            List<IntrospectedColumn> introspectedColumns = new ArrayList<>();

            if (identifierType != null) {

                String identifierPropertyName = entityPersister.getIdentifierPropertyName();

                calculateIntrospectedColumn(introspectedTable, entityPersister, identifierType, identifierPropertyName, introspectedColumns);
                if (identifierType instanceof ComponentType) {
                    introspectedTable.addPrimaryKeyColumn(identifierPropertyName);
                }
            }


            Type[] propertyTypes = entityPersister.getPropertyTypes();

            for (int i = 0, size = propertyTypes.length; i < size; i++) {
                calculateIntrospectedColumn(introspectedTable, entityPersister, propertyTypes[i], entityPersister.getPropertyNames()[i], introspectedColumns);
            }
            //计算主键生成策略
            calculateIdentifierGenerator(introspectedTable, entityPersister);


//            //只使用 Entity类返回的Column生成和privaryKey，其他清理掉
            Iterator<IntrospectedColumn> iterator = introspectedTable.getNonPrimaryKeyColumns().iterator();

            while (iterator.hasNext()) {
                IntrospectedColumn next = iterator.next();
                if (!introspectedColumns.contains(next)) {
                    iterator.remove();
                }
            }

            IntrospectedTableUtils.calculate(introspectedTable);
            String modelClass = IntrospectedTableUtils.getHbmModelClass(introspectedTable);

            introspectedTable.setBaseRecordType(modelClass);

            if (identifierType instanceof ComponentType) {
                introspectedTable.setPrimaryKeyType(identifierType.getReturnedClass().getName());


            }
        } catch (Exception e) {
            String modelClass = IntrospectedTableUtils.getHbmModelClass(introspectedTable);
            System.out.println("modelClass:" + modelClass + ",parse error");
            throw new IllegalArgumentException(e);

        }
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            boolean create = IntrospectedTableUtils.createEntityFile(introspectedTable);
            if (!create) {
                return false;
            }
            GeneratedJavaFile javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
            File file = SplitPlugin.Util.getTargetFile(javaFile);
            if (file.exists()) {
                System.out.println("Delete  JavaFile:" + file.toString());
                file.delete();

            }

            AbstractEntityPersister entityPersister = IntrospectedTableUtils.entityPersiter(introspectedTable);


            Type identifierType = entityPersister.getIdentifierType();
            //复合主键、复合对象
            if (identifierType instanceof ComponentType) {
                ComponentType componentType = (ComponentType) identifierType;
                if (componentType.isEmbedded()) {
                    //@Idclass
                    List<IntrospectedColumn> primaryKeyColumns = new ArrayList<>(introspectedTable.getPrimaryKeyColumns());
                    Collections.reverse(primaryKeyColumns);
                    for (IntrospectedColumn primaryKey : primaryKeyColumns) {

                        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));

                        topLevelClass.getFields().add(0, JavaBeansUtil.getJavaBeansField(primaryKey, context, introspectedTable));
                        topLevelClass.getMethods().add(0, JavaBeansUtil.getJavaBeansSetter(primaryKey, context, introspectedTable));
                        topLevelClass.getMethods().add(0, JavaBeansUtil.getJavaBeansGetter(primaryKey, context, introspectedTable));
                    }
                } else {
                    //@EmbeddedId
                    IntrospectedColumn primaryKey = new IntrospectedColumn();
                    primaryKey.setJavaProperty(entityPersister.getIdentifierPropertyName());
                    primaryKey.setFullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
                    primaryKey.setIdentity(true);
                    topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));

                    topLevelClass.getFields().add(0, JavaBeansUtil.getJavaBeansField(primaryKey, context, introspectedTable));
                    topLevelClass.getMethods().add(0, JavaBeansUtil.getJavaBeansSetter(primaryKey, context, introspectedTable));
                    topLevelClass.getMethods().add(0, JavaBeansUtil.getJavaBeansGetter(primaryKey, context, introspectedTable));
                }


            }
            Class clazz = entityPersister.getMappedClass();

            Class superClass = clazz.getSuperclass();
            topLevelClass.setSuperClass((FullyQualifiedJavaType) null);
            if (!Object.class.equals(superClass)) {
                FullyQualifiedJavaType type = new FullyQualifiedJavaType(superClass.getName());
                topLevelClass.addImportedType(type);
                topLevelClass.setSuperClass(type);
            }
            Class[] interfaces = clazz.getInterfaces();

            topLevelClass.getSuperInterfaceTypes().clear();
            if (interfaces != null) {
                for (Class interfaceClass : interfaces) {
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType(interfaceClass.getName());
                    topLevelClass.addImportedType(type);
                    topLevelClass.addSuperInterface(type);
                }
            }
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        AbstractEntityPersister entityPersister = IntrospectedTableUtils.entityPersiter(introspectedTable);

        if (entityPersister != null) {
            Type identifierType = entityPersister.getIdentifierType();


            if (identifierType instanceof ComponentType) {
                ComponentType type = (ComponentType) identifierType;
                if (!type.isEmbedded()) {
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
                                    XmlElement object = new XmlElement(xml);
                                    associationElement.addElement(object);

                                    Attribute attribute = getAttribute(xml, "property");
                                    if (attribute != null) {
                                        xml.getAttributes().remove(attribute);
                                    }


//                                    iterator.remove();
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


            }


        }
        return super.sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    private Attribute getAttribute(XmlElement xml, String name) {
        for (Attribute attribute : xml.getAttributes()) {
            if (name.equals(attribute.getName())) {
                return attribute;
            }
        }
        return null;
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

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {
            boolean create = IntrospectedTableUtils.createEntityFile(introspectedTable);
            if (!create) {
                return false;
            }

        }

        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }

    protected void generatedWithIdentityIntrospectedColumn(XmlElement element, AbstractXmlElementGenerator generator, IntrospectedTable introspectedTable) {

        DelegatingIntrospectedTable delegating = new IdentityDelegatingIntrospectedTable(introspectedTable.getTargetRuntime(), introspectedTable);
        Context context = new Context(null) {
            PluginAggregator pluginAggregator = new PluginAggregator();

            @Override
            public Plugin getPlugins() {
                return pluginAggregator;
            }
        };
//        context.getCommentGeneratorConfiguration().
        Properties properties = new Properties();
        properties.setProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE, "false");
        properties.setProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS, "true");
        properties.setProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS, "false");

        context.getCommentGenerator().addConfigurationProperties(properties);


        generator.setContext(context);
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
}
