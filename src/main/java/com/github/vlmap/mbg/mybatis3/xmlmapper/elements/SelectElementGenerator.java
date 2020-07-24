package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class SelectElementGenerator extends AbstractXmlElementGenerator {


    private String id;
    private boolean where=true;
    private boolean count=false;
    public SelectElementGenerator(String id) {
        super();
        this.id=id;
    }


    public void setCount(boolean count) {
        this.count = count;
    }

    public void setWhere(boolean where) {
        this.where = where;
    }

    public String getId(){
        return id;
    }
    @Override
    public void addElements(XmlElement parentElement) {

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$
        parentElement.addElement(answer);

        answer.addAttribute(new Attribute(
                "id",getId() )); //$NON-NLS-1$
        if(count){
            answer.addAttribute(new Attribute("resultType", "java.lang.Long"));
        }else{
            if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
                answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                        introspectedTable.getResultMapWithBLOBsId()));
            } else {
                answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                        introspectedTable.getBaseResultMapId()));
            }
        }


        String   parameterType = "map"; //$NON-NLS-1$

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterType));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        if(count){
            sb.append("select count(*) "); //$NON-NLS-1$
            answer.addElement(new TextElement(sb.toString()));
        }else {
            sb.append("select "); //$NON-NLS-1$

            if (stringHasValue(introspectedTable
                    .getSelectByPrimaryKeyQueryId())) {
                sb.append('\'');
                sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
                sb.append("' as QUERYID,"); //$NON-NLS-1$
            }
            answer.addElement(new TextElement(sb.toString()));
            answer.addElement(getBaseColumnListElement());
            if (introspectedTable.hasBLOBColumns()) {
                answer.addElement(new TextElement(",")); //$NON-NLS-1$
                answer.addElement(getBlobColumnListElement());
            }
        }


        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        if(!where){
            return;

        }














        XmlElement insertTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        insertTrimElement.addAttribute(new Attribute("prefix", "where ")); //$NON-NLS-1$ //$NON-NLS-2$
//        insertTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", "and")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(insertTrimElement);

//        XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
//        valuesTrimElement.addAttribute(new Attribute("prefix", "values (")); //$NON-NLS-1$ //$NON-NLS-2$
//        valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
//        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
//        answer.addElement(valuesTrimElement);

        for (IntrospectedColumn introspectedColumn : getColumns()) {
            String innerProperty=IntrospectedTableUtils.getIdentityIntrospectedColumn(introspectedColumn);
            if(StringUtils.isNotBlank(innerProperty)){
                introspectedColumn= IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn);

            }

            if (introspectedColumn.isSequenceColumn()
                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                // if it is a sequence column, it is not optional
                // This is required for MyBatis3 because MyBatis3 parses
                // and calculates the SQL before executing the selectKey

                // if it is primitive, we cannot do a null check
                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));

//                sb.setLength(0);
//                sb.append(MyBatis3FormattingUtilities
//                        .getParameterClause(introspectedColumn));
//                sb.append(',');
//                valuesTrimElement.addElement(new TextElement(sb.toString()));

                continue;
            }

//            sb.setLength(0);
//            sb.append(introspectedColumn.getJavaProperty());
//            sb.append(" != null"); //$NON-NLS-1$
//            XmlElement insertNotNullElement = new XmlElement("if"); //$NON-NLS-1$
//            insertNotNullElement.addAttribute(new Attribute(
//                    "test", sb.toString())); //$NON-NLS-1$
//
//            sb.setLength(0);
//            sb.append(MyBatis3FormattingUtilities
//                    .getEscapedColumnName(introspectedColumn));
//            sb.append(',');
//            insertNotNullElement.addElement(new TextElement(sb.toString()));
//            insertTrimElement.addElement(insertNotNullElement);


            sb.setLength(0);
            sb.append("_parameter.containsKey('");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append("')"); //$NON-NLS-1$
            XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            valuesNotNullElement.addAttribute(new Attribute(
                    "test", sb.toString())); //$NON-NLS-1$
            String name=innerProperty;
            if(StringUtils.isNotBlank(innerProperty)){
                XmlElement bind = new XmlElement("bind");
                name=StringUtils.replace(innerProperty,".","_");
                Attribute attribute=new Attribute("name",name);
                bind.addAttribute(attribute);
                attribute=new Attribute("value","_parameter.get('"+innerProperty+"')");
                bind.addAttribute(attribute);
                valuesNotNullElement.addElement(bind);
            }
            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append("=");
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn,name)));
//            sb.append(',');
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            insertTrimElement.addElement(valuesNotNullElement);
        }


















    }

     protected List<IntrospectedColumn> getColumns(){
        return introspectedTable.getAllColumns();
    }
}
