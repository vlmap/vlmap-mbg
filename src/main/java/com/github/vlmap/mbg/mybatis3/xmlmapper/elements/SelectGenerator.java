package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class SelectGenerator extends AbstractGenerator {
    public enum Result {
        ONE, LIST, ALL, COUNT
    }

    Result result;

    public SelectGenerator(String id, Result result) {
        super("select", id, Optation.ADD);
        this.result = result;
    }

    @Override
    public void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));

        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        interfaze.addImportedType(baseRecordType);
    }

    @Override
    public List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable) {

        List<Method> list = new ArrayList<>();

        FullyQualifiedJavaType baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        switch (result) {
            case ONE: {
                Method method = new Method(answer);

                method.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
                Parameter parameter = new Parameter(new FullyQualifiedJavaType("java.util.Map<String,Object>"), "params");
                parameter.addAnnotation("@Param(\"params\")");
                method.addParameter(parameter);
                list.add(method);
                method = new Method(answer);
                method.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
                parameter = new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()), "record");
                parameter.addAnnotation("@Param(\"record\")");
                method.addParameter(parameter);
                list.add(method);
                break;
            }
            case LIST: {
                Method method = new Method(answer);
                method.setReturnType(new FullyQualifiedJavaType("java.util.List<" + baseRecordType.getShortName() + ">"));

                 Parameter parameter = new Parameter(new FullyQualifiedJavaType("java.util.Map<String,Object>"), "params");
                parameter.addAnnotation("@Param(\"params\")");
                method.addParameter(parameter);
                list.add(method);
                method = new Method(answer);
                method.setReturnType(new FullyQualifiedJavaType("java.util.List<" + baseRecordType.getShortName() + ">"));
                parameter = new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()), "record");
                parameter.addAnnotation("@Param(\"record\")");
                method.addParameter(parameter);
                list.add(method);
                break;
            }
            case ALL: {
                answer.setReturnType(new FullyQualifiedJavaType("java.util.List<" + baseRecordType.getShortName() + ">"));

                return Arrays.asList(answer);
            }
            case COUNT: {



                Method method = new Method(answer);
                method.setReturnType(new FullyQualifiedJavaType("Long"));

                 Parameter parameter = new Parameter(new FullyQualifiedJavaType("java.util.Map<String,Object>"), "params");
                parameter.addAnnotation("@Param(\"params\")");
                method.addParameter(parameter);
                list.add(method);
                method = new Method(answer);
                method.setReturnType(new FullyQualifiedJavaType("Long"));
                parameter = new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()), "record");
                parameter.addAnnotation("@Param(\"record\")");
                method.addParameter(parameter);
                list.add(method);
                break;





            }
        }
        return list;

    }

    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {
        if (result == Result.COUNT) {
            answer.addAttribute(new Attribute("resultType", "java.lang.Long"));

        } else {
            if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
                answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                        introspectedTable.getResultMapWithBLOBsId()));
            } else {
                answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                        introspectedTable.getBaseResultMapId()));
            }
        }

        Context context = introspectedTable.getContext();


        answer.addAttribute(new Attribute("parameterType", "map"));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        if (result == Result.COUNT) {
            sb.append("select count(*) "); //$NON-NLS-1$
            answer.addElement(new TextElement(sb.toString()));

        } else {
            sb.append("select "); //$NON-NLS-1$

            if (stringHasValue(introspectedTable
                    .getSelectByPrimaryKeyQueryId())) {
                sb.append('\'');
                sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
                sb.append("' as QUERYID,"); //$NON-NLS-1$
            }
            answer.addElement(new TextElement(sb.toString()));
            answer.addElement(getBaseColumnListElement(introspectedTable));
            if (introspectedTable.hasBLOBColumns()) {
                answer.addElement(new TextElement(",")); //$NON-NLS-1$
                answer.addElement(getBlobColumnListElement(introspectedTable));
            }
        }


        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        if (result == Result.ALL) {
            return Arrays.asList(answer);
        }

        XmlElement insertTrimElement = where("record", false, introspectedTable);

        answer.addElement(insertTrimElement);
        insertTrimElement = where("params", true, introspectedTable);

        answer.addElement(insertTrimElement);
////        XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
////        valuesTrimElement.addAttribute(new Attribute("prefix", "values (")); //$NON-NLS-1$ //$NON-NLS-2$
////        valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
////        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$
////        answer.addElement(valuesTrimElement);
//
//        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
//            String innerProperty = IntrospectedTableUtils.getIdentityIntrospectedColumn(introspectedColumn);
//            if (StringUtils.isNotBlank(innerProperty)) {
//                introspectedColumn = IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn);
//
//            }
//
//            if (introspectedColumn.isSequenceColumn()
//                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
//
//                sb.setLength(0);
//                sb.append(MyBatis3FormattingUtilities
//                        .getEscapedColumnName(introspectedColumn));
//                sb.append(',');
//                insertTrimElement.addElement(new TextElement(sb.toString()));
//
//
//                continue;
//            }
//
//
//            sb.setLength(0);
//            sb.append("_parameter.containsKey('");
//            sb.append(introspectedColumn.getJavaProperty());
//            sb.append("')"); //$NON-NLS-1$
//            XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
//            valuesNotNullElement.addAttribute(new Attribute(
//                    "test", sb.toString())); //$NON-NLS-1$
//            String name = innerProperty;
////            if (StringUtils.isNotBlank(innerProperty)) {
////                XmlElement bind = new XmlElement("bind");
////                name = StringUtils.replace(innerProperty, ".", "_");
////                Attribute attribute = new Attribute("name", name);
////                bind.addAttribute(attribute);
////                attribute = new Attribute("value", "_parameter.get('" + innerProperty + "')");
////                bind.addAttribute(attribute);
////                valuesNotNullElement.addElement(bind);
////            }
//            sb.setLength(0);
//            sb.append(MyBatis3FormattingUtilities
//                    .getEscapedColumnName(introspectedColumn));
//            sb.append("=");
//            IntrospectedColumn column=    IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn, name);
////            sb.append(MyBatis3FormattingUtilities
////                    .getParameterClause(IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn, name)));
//            sb.append(getParameterClause(column,null));
//
////            sb.append(',');
//            valuesNotNullElement.addElement(new TextElement(sb.toString()));
//            insertTrimElement.addElement(valuesNotNullElement);
//        }
//

        return Arrays.asList(answer);
    }

    protected XmlElement where(String key, boolean ismap, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        XmlElement insertTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        insertTrimElement.addAttribute(new Attribute("prefix", "where ")); //$NON-NLS-1$ //$NON-NLS-2$
//        insertTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", "and")); //$NON-NLS-1$ //$NON-NLS-2$
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String innerProperty = IntrospectedTableUtils.getIdentityIntrospectedColumn(introspectedColumn);
            if (StringUtils.isNotBlank(innerProperty)) {
                introspectedColumn = IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn);

            }

            if (introspectedColumn.isSequenceColumn()
                    || introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {

                sb.setLength(0);
                sb.append(MyBatis3FormattingUtilities
                        .getEscapedColumnName(introspectedColumn));
                sb.append(',');
                insertTrimElement.addElement(new TextElement(sb.toString()));


                continue;
            }


            sb.setLength(0);
            if (ismap) {
                sb.append(key);
                sb.append(".containsKey('");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("')"); //$NON-NLS-1$
            } else {
                sb.append(key+"."+introspectedColumn.getJavaProperty()+"!=null");
            }

            XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            valuesNotNullElement.addAttribute(new Attribute(
                    "test", sb.toString())); //$NON-NLS-1$
            String name = innerProperty;
//            if (StringUtils.isNotBlank(innerProperty)) {
//                XmlElement bind = new XmlElement("bind");
//                name = StringUtils.replace(innerProperty, ".", "_");
//                Attribute attribute = new Attribute("name", name);
//                bind.addAttribute(attribute);
//                attribute = new Attribute("value", "_parameter.get('" + innerProperty + "')");
//                bind.addAttribute(attribute);
//                valuesNotNullElement.addElement(bind);
//            }
            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append("=");

            IntrospectedColumn column = IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn, name);
            String str = null;
            if (ismap) {
                str = getParameterClause(column, key);


            } else {
                str = MyBatis3FormattingUtilities.getParameterClause(column, key + ".");
            }
            sb.append(str);
//

//            sb.append(',');
            valuesNotNullElement.addElement(new TextElement(sb.toString()));
            insertTrimElement.addElement(valuesNotNullElement);
        }
        XmlElement result = new XmlElement("if"); //$NON-NLS-1$
        if(ismap){
            result.addAttribute(new Attribute("test", key + "!=null and !"+key+".isEmpty()"));

        }else{
            result.addAttribute(new Attribute("test", key + "!=null"));

        }
        result.addElement(insertTrimElement);
        return result;
    }

    public String getParameterClause(
            IntrospectedColumn introspectedColumn, String prefix) {
        StringBuilder sb = new StringBuilder();

        sb.append("#{"); //$NON-NLS-1$
        sb.append(prefix + ".get('" + introspectedColumn.getJavaProperty() + "')");
        sb.append(",jdbcType="); //$NON-NLS-1$
        sb.append(introspectedColumn.getJdbcTypeName());

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append(",typeHandler="); //$NON-NLS-1$
            sb.append(introspectedColumn.getTypeHandler());
        }

        sb.append('}');

        return sb.toString();
    }

    protected XmlElement getBaseColumnListElement(IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("include"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getBaseColumnListId()));
        return answer;
    }

    protected XmlElement getBlobColumnListElement(IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("include"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getBlobColumnListId()));
        return answer;
    }

}
