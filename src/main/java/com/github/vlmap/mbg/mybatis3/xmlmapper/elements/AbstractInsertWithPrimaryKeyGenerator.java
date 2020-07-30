package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IdentityDelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.InsertElementGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.PluginAggregator;

import java.util.*;

/**
 * void saveOrUpdate(Student record)
 */
public abstract class AbstractInsertWithPrimaryKeyGenerator extends AbstractGenerator {

    @Override
    public Optation getOptation() {
        return Optation.ADD;
    }


    protected String getInsertWithPrimaryKeytStatementId(IntrospectedTable introspectedTable) {
        String result = "__" + StringUtils.replace(introspectedTable.getInsertStatementId(), "__", "") + "WithPrimaryKey" + "__";
        return result;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
        String insertStatementId=this.getProperties().getProperty("insertStatementId");
        if(StringUtils.isNotBlank(insertStatementId)){
            introspectedTable.setInsertStatementId(insertStatementId);

        }
//        String result = "__" + StringUtils.replace(introspectedTable.getInsertStatementId(), "__", "") + "__";


    }


    @Override
    public List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable) {
        List<Method> list = new ArrayList<>();
        GeneratedKey generatedKey = introspectedTable.getGeneratedKey();
        if (generatedKey != null) {

            Method method = new Method();

            method.setReturnType(FullyQualifiedJavaType.getIntInstance());
            method.setName(getInsertWithPrimaryKeytStatementId(introspectedTable));

            FullyQualifiedJavaType parameterType;
            if (isSimple(introspectedTable)) {
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


    //__insertWithPrimaryKey__

    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {
        List<XmlElement> list = new ArrayList<>();

        if (introspectedTable.getGeneratedKey() == null) return list;
        XmlElement parent = new XmlElement("parent");
        InsertElementGenerator generator = new InsertElementGenerator(isSimple(introspectedTable));
        generatedWithIdentityIntrospectedColumn(parent, generator, introspectedTable);
        for (Element element : parent.getElements()) {
            if (element instanceof XmlElement) {
                list.add((XmlElement) element);
            }
        }
        return list;
    }

    protected void generatedWithIdentityIntrospectedColumn(XmlElement parent, InsertElementGenerator generator, IntrospectedTable introspectedTable) {
        String insertStatementId = getInsertWithPrimaryKeytStatementId(introspectedTable);
        DelegatingIntrospectedTable delegating = new IdentityDelegatingIntrospectedTable(introspectedTable.getTargetRuntime(), introspectedTable) {
            @Override
            public String getInsertStatementId() {
                return insertStatementId;
            }

            @Override
            public GeneratedKey getGeneratedKey() {
                return null;
            }

            @Override
            public List<IntrospectedColumn> getAllColumns() {
                List<IntrospectedColumn> list = new ArrayList<>();
                for (IntrospectedColumn column : super.getAllColumns()) {
                    column = IntrospectedTableUtils.clone(column);
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
