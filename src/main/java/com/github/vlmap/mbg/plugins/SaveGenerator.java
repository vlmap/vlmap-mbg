package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractInsertWithPrimaryKeyGenerator;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
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
    final static String returnValueProperty = "returnValue";

    final static String ignoreNullGeneratorKeyProperty = "ignoreNullGeneratorKey";


    final static String RETURN_VALUE_PRIMARY_KEY = "primaryKey";
    final static String RETURN_VALUE_ROW_COUNT = "count";
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

        Method insert = getMethod(interfaze.getMethods(), introspectedTable.getInsertStatementId());
        if (insert != null) {
            insert.getJavaDocLines().add("//使用save方法替代本方法，主键有值时会被SelectKey覆盖");

        }
        String returnValue = introspectedTable.getTableConfigurationProperty(returnValueProperty);
        if (StringUtils.isBlank(returnValue)) {
            returnValue = this.getProperties().getProperty(returnValueProperty, RETURN_VALUE_ROW_COUNT);
        }


        if (returnValue.equals(RETURN_VALUE_ROW_COUNT)) {
            list.add(returnTypeCount(answer, interfaze, introspectedTable));
        } else {
            list.add(returnTypePrimaryKey(answer, interfaze, introspectedTable));
        }
        return list;
    }

    public Method returnTypeCount(Method answer, Interface interfaze, IntrospectedTable introspectedTable) {
        GeneratedKey generatedKey = introspectedTable.getGeneratedKey();

        answer.setReturnType(FullyQualifiedJavaType.getIntInstance());
        answer.setDefault(true);
        answer.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()), "record"));
        if (generatedKey != null) {
            Method primaryKeyGetter = JavaBeansUtil.getJavaBeansGetter(introspectedTable.getColumn(generatedKey.getColumn()), introspectedTable.getContext(), introspectedTable);

            answer.addBodyLine(primaryKeyGetter.getReturnType().getShortName() + " id = record." + primaryKeyGetter.getName() + "();");

            answer.addBodyLine(" if(id==null){ ");
            answer.addBodyLine("    return " + introspectedTable.getInsertStatementId() + "(record);");
            answer.addBodyLine("  } else {");
            answer.addBodyLine("    return " + getInsertWithPrimaryKeytStatementId(introspectedTable) + "(record);");
            answer.addBodyLine("  }");
        } else {
            answer.addBodyLine(" return " + introspectedTable.getInsertStatementId() + "(record);");

        }


        return answer;
    }

    //

    protected Method returnTypePrimaryKey(Method answer, Interface interfaze, IntrospectedTable introspectedTable) {

        answer.setDefault(true);
        answer.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()), "record"));
        GeneratedKey generatedKey = introspectedTable.getGeneratedKey();


        if (generatedKey != null) {
            Method primaryKeyGetter = JavaBeansUtil.getJavaBeansGetter(introspectedTable.getColumn(generatedKey.getColumn()), introspectedTable.getContext(), introspectedTable);

            answer.addBodyLine(primaryKeyGetter.getReturnType().getShortName() + " id = record." + primaryKeyGetter.getName() + "();");

            answer.addBodyLine(" if(id==null){ ");
            answer.addBodyLine(introspectedTable.getInsertStatementId() + "(record);");
            answer.addBodyLine("  } else {");
            answer.addBodyLine(getInsertWithPrimaryKeytStatementId(introspectedTable) + "(record);");
            answer.addBodyLine("  }");
        } else {
            answer.addBodyLine(introspectedTable.getInsertStatementId() + "(record);");

        }


        String identityPropertyName = IntrospectedTableUtils.geIdentityPropertyName(introspectedTable);

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();


        if (StringUtils.isNotBlank(identityPropertyName)) {
            answer.setReturnType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));

            IntrospectedColumn column = new IntrospectedColumn();
            column.setJavaProperty(identityPropertyName);
            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
            column.setIdentity(true);
            Method primaryKeyGetter = JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable);

            answer.addBodyLine("return record." + primaryKeyGetter.getName() + "();//1");
            return answer;

        } else {


            if (primaryKeyColumns.size() == 1) {

                IntrospectedColumn column = primaryKeyColumns.get(0);

                if (generatedKey == null) {
                    String msg = "Schema["+introspectedTable.getTableConfiguration().getSchema()+"],Table["+introspectedTable.getTableConfiguration().getTableName()+"],Column[" + column.getActualColumnName() + "],Property[" + column.getJavaProperty() + "] 没有配置selectKey";
                    answer.addJavaDocLine("//" + msg + " 返回的primaryKey可能会是空值");
                    String ignoreNullGeneratorKey = introspectedTable.getTableConfigurationProperty(ignoreNullGeneratorKeyProperty);
                    if (StringUtils.isBlank(ignoreNullGeneratorKey)) {
                        ignoreNullGeneratorKey = this.getProperties().getProperty(ignoreNullGeneratorKeyProperty, "false");
                    }

                    if (!StringUtils.equals("true", ignoreNullGeneratorKey)) {
                        throw new IllegalArgumentException(msg+",可以在<Table>标签里添加<property name=\"ignoreNullGeneratorKey\" value=\"true\"/> 忽略");
                    }
                }


                Method attrGetter = JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable);
                answer.addBodyLine("return record." + attrGetter.getName() + "();//2");
                answer.setReturnType(attrGetter.getReturnType());

                return answer;

            } else if (primaryKeyColumns.size() > 1) {

                answer.setReturnType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
                FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
                interfaze.addImportedType(returnType);
                answer.addBodyLine(returnType.getShortName() + " primaryKey = new " + returnType.getShortName() + "();");


                for (IntrospectedColumn column : primaryKeyColumns) {
                    Method attrSetter = JavaBeansUtil.getJavaBeansSetter(column, context, introspectedTable);
                    Method attrGetter = JavaBeansUtil.getJavaBeansGetter(column, context, introspectedTable);
                    answer.addBodyLine("primaryKey." + attrSetter.getName() + "( record." + attrGetter.getName() + "());");

                }
                answer.addBodyLine("return primaryKey;//3");


                return answer;


            }


        }
        return null;

    }

    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {
        return super.xmlGenerated(pre, answer, document, introspectedTable);
    }


}
