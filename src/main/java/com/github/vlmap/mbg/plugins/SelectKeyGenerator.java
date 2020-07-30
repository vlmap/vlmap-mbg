package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.GeneratedKey;

import java.util.Arrays;
import java.util.List;

public class SelectKeyGenerator extends AbstractGenerator {

    @Override
    public Optation getOptation() {
        return Optation.ADD;
    }

    @Override
    public String getId() {
        return "selectKey";
    }

    @Override
    public String getName() {
        return "select";
    }
    @Override
    public List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable) {


        GeneratedKey generatedKey = introspectedTable.getGeneratedKey();

        if (generatedKey != null) {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(generatedKey.getColumn());
            String identityColumnType = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();
            answer.setReturnType(new FullyQualifiedJavaType(identityColumnType));
            return Arrays.asList(answer);

        }
        return null;


    }

    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {
        GeneratedKey generatedKey = introspectedTable.getGeneratedKey();
        if (generatedKey != null) {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(generatedKey.getColumn());
            String identityColumnType = introspectedColumn
                    .getFullyQualifiedJavaType().getFullyQualifiedName();
            XmlElement selectKey = getSelectKey(introspectedColumn, introspectedTable.getGeneratedKey());


            answer.addAttribute(new Attribute("resultType", identityColumnType));
            answer.getElements().addAll(selectKey.getElements());
            return Arrays.asList(answer);

        }

        return null;
    }

    protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn,
                                      GeneratedKey generatedKey) {
        String identityColumnType = introspectedColumn
                .getFullyQualifiedJavaType().getFullyQualifiedName();

        XmlElement answer = new XmlElement("selectKey"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultType", identityColumnType)); //$NON-NLS-1$
        answer.addAttribute(new Attribute(
                "keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
        answer.addAttribute(new Attribute("order", //$NON-NLS-1$
                generatedKey.getMyBatis3Order()));

        answer.addElement(new TextElement(generatedKey
                .getRuntimeSqlStatement()));

        return answer;
    }

}
