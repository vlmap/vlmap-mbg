package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;

import java.util.Set;

public interface Generator {
    enum Optation {
        ADD,DELETE,REPLACE
    }
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable);

    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable);
    public   void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable);

}
