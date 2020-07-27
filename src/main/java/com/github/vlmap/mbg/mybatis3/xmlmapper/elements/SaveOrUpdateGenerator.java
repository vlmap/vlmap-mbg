package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IdentityDelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3SimpleImpl;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.InsertElementGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.*;

public class SaveOrUpdateGenerator extends AbstractGenerator {
    public SaveOrUpdateGenerator(String id ) {
        super(null,id, Optation.ADD);
    }

    @Override
    public void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Objects"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.HashMap"));

    }

    @Override
    public List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable) {
        List<Method> list=new ArrayList<>();
        Context context=introspectedTable.getContext();
        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        answer.addParameter(new Parameter(baseRecordType, "record"));

//        method.setReturnType(new FullyQualifiedJavaType("java.util.List<"+ baseRecordType.getShortName()+">"));

        answer.setDefault(true);

        if (introspectedTable.getPrimaryKeyColumns().isEmpty()) {
            answer.addBodyLine("//没有主键，直接调用保存操作");
            answer.addBodyLine("  " + introspectedTable.getInsertStatementId() + "(record);");
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
            answer.addBodyLine("if(" + StringUtils.join(lines, "&&") + "){");
            answer.addBodyLine("Map<String,Object> params=new HashMap<>();");
            for (String line : paramLines) {
                answer.addBodyLine(line);
            }

            answer.addBodyLine("Long count=count(params);");
            String updateName=introspectedTable.getUpdateByPrimaryKeyStatementId();

            answer.addBodyLine("if (count.intValue() > 0) {");
            answer.addBodyLine("  " + updateName + "(record);");
            answer.addBodyLine("  return;");


             if(introspectedTable.getGeneratedKey()!=null){
                 answer.addBodyLine("} else {");
                 answer.addBodyLine("//不使用selectKey");

                 answer.addBodyLine("  "+getInsertWithPrimaryKeytStatementId(introspectedTable) + "(record);");
                 answer.addBodyLine("return;");
                 answer.addBodyLine("}");
//                answer.addBodyLine(" throw new IllegalArgumentException(\""+updateName+"方法使用了selectKey,primaryKey有值的情况不能执行insert操作,record[\"+record+\"]\");");
            }else{
                 answer.addBodyLine("}");
             }
            answer.addBodyLine("} ");
            answer.addBodyLine(introspectedTable.getInsertStatementId() + "(record);");

         }
        list.add(answer);
        if(introspectedTable.getGeneratedKey()!=null) {
             Method method = new Method();

            method.setReturnType(FullyQualifiedJavaType.getIntInstance());
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setName(getInsertWithPrimaryKeytStatementId(introspectedTable));

            FullyQualifiedJavaType parameterType;
            boolean isSimple=isSample(introspectedTable);
            if (isSimple) {
                parameterType = new FullyQualifiedJavaType(
                        introspectedTable.getBaseRecordType());
            } else {
                parameterType = introspectedTable.getRules()
                        .calculateAllFieldsClass();
            }

            Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
            importedTypes.add(parameterType);
            method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$
            list.add(method);
        }



        return list;
    }

    protected  static String getInsertWithPrimaryKeytStatementId(IntrospectedTable introspectedTable){
      return   introspectedTable.getInsertStatementId()+"WithPrimaryKey";
    }
    protected boolean isSample(IntrospectedTable introspectedTable){
        if (introspectedTable instanceof IntrospectedTableMyBatis3SimpleImpl) {
            return true;
        }

        return false;
    }

    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {
        if(introspectedTable.getGeneratedKey()==null)return null;
        XmlElement parent=new XmlElement("parent");
        InsertElementGenerator generator=       new    InsertElementGenerator(isSample(introspectedTable));
        generatedWithIdentityIntrospectedColumn(parent, generator, introspectedTable);
        List<XmlElement> list=new ArrayList<>();
        for(Element element:parent.getElements()){
            if(element instanceof  XmlElement){
                list.add((XmlElement) element);
            }
        }
        return list;
    }
    protected void generatedWithIdentityIntrospectedColumn(XmlElement parent, InsertElementGenerator generator, IntrospectedTable introspectedTable) {

        DelegatingIntrospectedTable delegating = new IdentityDelegatingIntrospectedTable(introspectedTable.getTargetRuntime(), introspectedTable){
            @Override
            public String getInsertStatementId() {
                return getInsertWithPrimaryKeytStatementId(target);
            }

            @Override
            public GeneratedKey getGeneratedKey() {
                return null;
            }

            @Override
            public List<IntrospectedColumn> getAllColumns() {
                List<IntrospectedColumn> list=new ArrayList<>();
                for(IntrospectedColumn column: super.getAllColumns()){
                    column   =IntrospectedTableUtils.clone(column);
                    column.setGeneratedAlways(false);
                    column.setIdentity(false);
                    list.add(column);
                }
                return list;
            }
        };
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

         generator.addElements(parent);

    }

}
