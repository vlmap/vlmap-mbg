package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class SelectKeyElementGenerator extends AbstractXmlElementGenerator {

    String id;

    public SelectKeyElementGenerator(String id) {
        this.id = id;
    }

    @Override
    public void addElements(XmlElement parentElement) {
//    <selectKey keyProperty="aa" order="AFTER" resultType="java.lang.Integer">
//                select last_insert_id()
//                </selectKey>


        GeneratedKey generatedKey= introspectedTable.getGeneratedKey();
        if(generatedKey!=null){
            String columnName=generatedKey.getColumn();
            IntrospectedColumn   introspectedColumn=  introspectedTable.getColumn(columnName);
            String identityColumnType = introspectedColumn
                    .getFullyQualifiedJavaType().getFullyQualifiedName();
            XmlElement selectKey =getSelectKey(introspectedColumn,introspectedTable.getGeneratedKey());
            selectKey.setName("select");


            selectKey.getAttributes().clear();
            selectKey.addAttribute(new Attribute("id",id));
            selectKey.addAttribute(new Attribute("resultType",identityColumnType));


            parentElement.addElement(selectKey);

        }


    }


 }
