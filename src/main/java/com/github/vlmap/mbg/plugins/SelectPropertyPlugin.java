package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IdentityDelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.SelectElementGenerator;
import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.SelectKeyElementGenerator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * mgb生成内容和自定义内容分离,该插件应该被最后被加载
 *
 * @author vlmap
 */
public class SelectPropertyPlugin extends PluginAdapter {


    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public void initialized(IntrospectedTable introspectedTable) {


    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {


        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        interfaze.addImportedType(baseRecordType);


        Method method = new Method("select");

        method.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("java.util.Map<String,Object>"), "record");

        method.addParameter(parameter);
        interfaze.addMethod(method);
        method = new Method("selectList");

        method.setReturnType(new FullyQualifiedJavaType("java.util.List<" + baseRecordType.getShortName() + ">"));
        parameter = new Parameter(new FullyQualifiedJavaType("java.util.Map<String,Object>"), "record");

        method.addParameter(parameter);
        interfaze.addMethod(method);


        method = new Method("count");

        method.setReturnType(new FullyQualifiedJavaType("Long"));
        parameter = new Parameter(new FullyQualifiedJavaType("java.util.Map<String,Object>"), "record");

        method.addParameter(parameter);

        interfaze.addMethod(method);
        boolean hasSelectAll = false;
        String selectAllName = introspectedTable.getSelectAllStatementId();

        for (Method m : interfaze.getMethods()) {
            if (selectAllName.equals(m.getName())) {
                hasSelectAll = true;
                break;
            }
        }

        if (!hasSelectAll) {

            method = new Method(selectAllName);
            method.setReturnType(new FullyQualifiedJavaType("java.util.List<" + baseRecordType.getShortName() + ">"));


            interfaze.addMethod(method);
        }


        addSaveOrUpdate(interfaze, topLevelClass, introspectedTable);

        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }



    protected void addSaveOrUpdate(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        Method method = new Method("saveOrUpdate");
        method.addParameter(new Parameter(baseRecordType, "record"));

//        method.setReturnType(new FullyQualifiedJavaType("java.util.List<"+ baseRecordType.getShortName()+">"));

        method.setDefault(true);

        if (introspectedTable.getPrimaryKeyColumns().isEmpty()) {
            method.addBodyLine("//没有主键，直接调用保存操作");
            method.addBodyLine("  " + introspectedTable.getInsertStatementId() + "(record);");
        } else {


            AbstractEntityPersister entityPersister = IntrospectedTableUtils.entityPersiter(introspectedTable);
            List<String> lines = new ArrayList<>();
            List<String> paramLines = new ArrayList<>();

            if (entityPersister != null) {
                Type identifierType = entityPersister.getIdentifierType();
//                FullyQualifiedJavaType primaryKeyType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());

//                interfaze.addImportedType(primaryKeyType);


                //复合主键、复合对象

                if (identifierType instanceof ComponentType) {

                    ComponentType componentType = (ComponentType) identifierType;
                    if (componentType.isEmbedded()) {
                        //@Idclass

                    } else {
                        //@EmbeddedId
                        IntrospectedColumn primaryKey = new IntrospectedColumn();
                        primaryKey.setJavaProperty(entityPersister.getIdentifierPropertyName());
                        primaryKey.setFullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
                        primaryKey.setIdentity(true);

                        lines.add("Objects.nonNull(record." + JavaBeansUtil.getJavaBeansGetter(primaryKey, context, introspectedTable).getName() + "())");

                        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
                            String line = "record." + JavaBeansUtil.getJavaBeansGetter(primaryKey, context, introspectedTable).getName() + "()."
                                    + JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable).getName() + "()";
                            lines.add("Objects.nonNull(" + line + ")");
                            String name = IntrospectedTableUtils.withIdentityIntrospectedColumn(column).getJavaProperty();
                            paramLines.add("params.put(\"" + name + "\"," + line + ");");
                        }


                    }


                }
            }


            if (lines.isEmpty()) {
                for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
                    String line = "record." + JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable).getName() + "()";
                    lines.add("Objects.nonNull(" + line + ")");
                    String name = IntrospectedTableUtils.withIdentityIntrospectedColumn(column).getJavaProperty();
                    paramLines.add("params.put(\"" + name + "\"," + line + ");");
                }
            }
            method.addBodyLine("if(" + StringUtils.join(lines, "&&") + "){");
            method.addBodyLine("Map<String,Object> params=new HashMap<>();");
            interfaze.addImportedType(new FullyQualifiedJavaType("java.util.HashMap"));
            for (String line : paramLines) {
                method.addBodyLine(line);
            }
            method.addBodyLine("Long count=count(params);");
            method.addBodyLine("if(count.intValue()>0){");
            method.addBodyLine("  " + introspectedTable.getUpdateByPrimaryKeyStatementId() + "(record);");
            method.addBodyLine("  return;");

            method.addBodyLine("}");


            method.addBodyLine("} ");
            method.addBodyLine(introspectedTable.getInsertStatementId() + "(record);");
        }
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Objects"));
        interfaze.addMethod(method);

        GeneratedKey generatedKey=introspectedTable.getGeneratedKey();

        if(generatedKey!=null){
            method=new Method("selectKey");
            IntrospectedColumn   introspectedColumn=  introspectedTable.getColumn(generatedKey.getColumn());
            String identityColumnType = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();
            method.setReturnType(new FullyQualifiedJavaType(identityColumnType));
            interfaze.addMethod(method);

        }

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
        Properties properties = new Properties();
        properties.setProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE, "false");
        properties.setProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS, "true");
        properties.setProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS, "false");

        context.getCommentGenerator().addConfigurationProperties(properties);


        generator.setContext(context);
        generator.setIntrospectedTable(delegating);

        generator.addElements(element);
//        XmlElement node = (XmlElement) parent.getElements().get(0);
//        element.getElements().clear();
//        element.getElements().addAll(node.getElements());
    }

    protected String getAttribute(XmlElement element, String attributeName) {
        for (Attribute attribute : element.getAttributes()) {
            if (attributeName.equals(attribute.getName())) {
                return attribute.getValue();
            }

        }
        return null;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        SelectElementGenerator generator = new SelectElementGenerator("select");
        generatedWithIdentityIntrospectedColumn(document.getRootElement(), generator, introspectedTable);

        generator = new SelectElementGenerator("selectList");
        generatedWithIdentityIntrospectedColumn(document.getRootElement(), generator, introspectedTable);
        generator = new SelectElementGenerator("count");
        generator.setCount(true);
        generatedWithIdentityIntrospectedColumn(document.getRootElement(), generator, introspectedTable);

        boolean hasSelectAll = false;
        String selectAllName = introspectedTable.getSelectAllStatementId();
        for (Element element : document.getRootElement().getElements()) {
            XmlElement e = (XmlElement) element;
            if ("select".equals(e.getName()) && selectAllName.equals(getAttribute(e, "id"))) {

                hasSelectAll = true;
                break;
            }
        }

        if (!hasSelectAll) {


            generator = new SelectElementGenerator(selectAllName);
            generator.setWhere(false);
            generatedWithIdentityIntrospectedColumn(document.getRootElement(), generator, introspectedTable);
        }

        generatedWithIdentityIntrospectedColumn(document.getRootElement(), new SelectKeyElementGenerator("selectKey"), introspectedTable);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


}
