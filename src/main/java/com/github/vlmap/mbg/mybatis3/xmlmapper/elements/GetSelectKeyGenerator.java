package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GetSelectKeyGenerator extends AbstractGenerator {
    public GetSelectKeyGenerator() {
        super("select", "selectKey", Optation.ADD);
    }

    @Override
    public void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable) {

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
