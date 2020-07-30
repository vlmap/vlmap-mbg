package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IdentityDelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.io.File;
import java.util.*;

/**
 * 生成内连复合主键
 */
public class EmbeddedPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if (IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            boolean create = IntrospectedTableUtils.isCreateEntityFile(this);
            if (!create) {
                return false;
            }
            GeneratedJavaFile javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
            File file = SplitPlugin.Util.getTargetFile(javaFile);
            if (file.exists()) {
                System.out.println("Delete  JavaFile:" + file.toString());
                file.delete();

            }
            String identifierPropertyName = IntrospectedTableUtils.geIdentityPropertyName(introspectedTable);
            if (StringUtils.isNotBlank(identifierPropertyName)) {
                IntrospectedColumn primaryKey = new IntrospectedColumn();
                primaryKey.setJavaProperty(identifierPropertyName);
                primaryKey.setFullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
                primaryKey.setIdentity(true);
                topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
                Field field= JavaBeansUtil.getJavaBeansField(primaryKey, context, introspectedTable);
                topLevelClass.getFields().add(0,field );
                topLevelClass.getMethods().add(0, JavaBeansUtil.getJavaBeansSetter(primaryKey, context, introspectedTable));
                topLevelClass.getMethods().add(0, JavaBeansUtil.getJavaBeansGetter(primaryKey, context, introspectedTable));
                if(field.getType().equals(topLevelClass.getSuperClass())){
                    topLevelClass.setSuperClass((FullyQualifiedJavaType)null);
                }
            }


        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        String identifierPropertyName = IntrospectedTableUtils.geIdentityPropertyName(introspectedTable);


         if (StringUtils.isNotBlank(identifierPropertyName)) {
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
            boolean create = IntrospectedTableUtils.isCreateEntityFile(this);
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
