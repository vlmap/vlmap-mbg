package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractInsertWithPrimaryKeyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * void saveOrUpdate(Student record)
 */
public class SaveOrUpdateGenerator extends AbstractInsertWithPrimaryKeyGenerator {


    @Override
    public String getId() {
        return "saveOrUpdate";
    }

    @Override
    public String getName() {
        return null;
    }


    @Override
    public void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable) {


        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Objects"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.HashMap"));

    }

    @Override
    public List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable) {
        List<Method> list = super.methodGenerated(pre, answer, interfaze, introspectedTable);
        Context context=introspectedTable.getContext();

        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        answer.addParameter(new Parameter(baseRecordType, "record"));

        answer.setReturnType(FullyQualifiedJavaType.getIntInstance());
        answer.setDefault(true);


        if (introspectedTable.getPrimaryKeyColumns().isEmpty()) {
            answer.addBodyLine("//没有主键，直接调用保存操作");
            answer.addBodyLine("   return " + introspectedTable.getInsertStatementId() + "(record);");
        } else {


            String identityPropertyName =IntrospectedTableUtils.geIdentityPropertyName(introspectedTable);
            List<String> lines = new ArrayList<>();

            if(StringUtils.isNotBlank(identityPropertyName)){
                IntrospectedColumn primaryKey = new IntrospectedColumn();
                primaryKey.setJavaProperty(identityPropertyName);
                primaryKey.setFullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
                primaryKey.setIdentity(true);
                Method idGetter=JavaBeansUtil.getJavaBeansGetter(primaryKey, context, introspectedTable);

                lines.add("Objects.nonNull(record." + idGetter.getName() + "())");

                for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
                    Method attrGetter=JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable);

                    lines.add("Objects.nonNull(record." +  idGetter.getName() + "()."+attrGetter.getName() + "())");

                }


            }else{
                for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
                    Method attrGetter=JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable);

                    lines.add("Objects.nonNull(record." +attrGetter.getName() + "())");

                }

            }




            String update=introspectedTable.getUpdateByPrimaryKeyStatementId();
            String insert=introspectedTable.getInsertStatementId();
            answer.addBodyLine("if(" + StringUtils.join(lines, "&&") + "){");
            String methodName=this.properties.getProperty("count","count");
            answer.addBodyLine("Long count = "+methodName+"(record);");
            answer.addBodyLine("if (count.longValue() > 0) {");
            answer.addBodyLine("  return " + update + "(record);");


            if (introspectedTable.getGeneratedKey() != null) {
                answer.addBodyLine("} else {");
                answer.addBodyLine("//不使用selectKey防止 primaryKey被selectKey值覆盖");

                answer.addBodyLine("  return " + getInsertWithPrimaryKeytStatementId(introspectedTable) + "(record);");
                 answer.addBodyLine("}");
            }else{
                answer.addBodyLine("}");
            }
            answer.addBodyLine("} ");
            answer.addBodyLine(" return "+insert + "(record);");

        }
        list.add(answer);





        return list;
    }


    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {

        return super.xmlGenerated(pre, answer, document, introspectedTable);
    }


}
