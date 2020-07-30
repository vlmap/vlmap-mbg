package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractInsertWithPrimaryKeyGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.List;
import java.util.Set;

/**
 * Long save(Student record)
 */
public class SaveGenerator extends AbstractInsertWithPrimaryKeyGenerator {

    @Override
    public Optation getOptation() {
        return Optation.ADD;
    }

    @Override
    public String getId() {
        return "save";
    }

    @Override
    public String getName() {
        return null;
    }


    @Override
    public void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Objects"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.HashMap"));

    }

    @Override
    public List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable) {
        List<Method> list = super.methodGenerated(pre, answer, interfaze, introspectedTable);
        GeneratedKey generatedKey = introspectedTable.getGeneratedKey();


        Method method = answer;

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setDefault(true);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()), "record"));
        if (generatedKey != null) {
            Method primaryKeyGetter = JavaBeansUtil.getJavaBeansGetter(introspectedTable.getColumn(generatedKey.getColumn()), introspectedTable.getContext(), introspectedTable);

            method.addBodyLine(primaryKeyGetter.getReturnType().getShortName() + " id = record." + primaryKeyGetter.getName() + "();");

            method.addBodyLine(" if(id==null){ ");
            method.addBodyLine("    return " + introspectedTable.getInsertStatementId() + "(record);");
            method.addBodyLine("  } else {");

            method.addBodyLine("    return " + getInsertWithPrimaryKeytStatementId(introspectedTable) + "(record);");
            method.addBodyLine("  }");
        } else {
            method.addBodyLine(" return " + introspectedTable.getInsertStatementId() + "(record);");

        }

        Method insert = getMethod(interfaze.getMethods(), introspectedTable.getInsertStatementId());
        if (insert != null) {
            insert.getJavaDocLines().add("//使用save方法替代本方法，主键有值时会被SelectKey覆盖");

        }


        list.add(method);


        return list;
    }


    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {
        return super.xmlGenerated(pre, answer, document, introspectedTable);
    }


}
