package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IdentityDelegatingIntrospectedTable;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.PluginAggregator;

import java.util.List;
import java.util.Properties;


/**
 * 生成常用SQL和定义常量
 *
 * @author vlmap
 */
public class XMLBasePlugin extends PluginAdapter {


    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public void initialized(IntrospectedTable introspectedTable) {


    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field fild = new Field("TABLE_NAME", new FullyQualifiedJavaType("String"));
        fild.setInitializationString("\"" + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + "\"");
        interfaze.addField(fild);


        fild = new Field("BASE_RESULT_MAP", new FullyQualifiedJavaType("String"));
        String value =   introspectedTable.getBaseResultMapId();
        fild.setInitializationString("\"" + value + "\"");
        interfaze.addField(fild);



        InnerInterface columns=new InnerInterface("Columns");
        for(IntrospectedColumn column:introspectedTable.getAllColumns()){
            fild = new Field(StringUtils.upperCase(column.getJavaProperty()), new FullyQualifiedJavaType("String"));
            fild.setInitializationString("\"" + column.getActualColumnName() + "\"");
            columns.addField(fild);
        }

        interfaze.addInnerInterfaces(columns);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    protected void generatedWithIdentityIntrospectedColumn(XmlElement element, AbstractXmlElementGenerator generator, IntrospectedTable introspectedTable) {

        DelegatingIntrospectedTable delegating = new IdentityDelegatingIntrospectedTable(introspectedTable.getTargetRuntime(), introspectedTable);
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

        XmlElement parent = new XmlElement("parent");
        generator.addElements(parent);
        XmlElement node = (XmlElement) parent.getElements().get(0);
        element.getElements().clear();
        element.getElements().addAll(node.getElements());
    }


}
