package com.github.vlmap.mbg.plugins;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.ResultMapWithoutBLOBsElementGenerator;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.PluginAggregator;

import java.io.File;
import java.util.*;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;


/**
 * mgb生成内容和自定义内容分离,该插件应该被最后被加载
 *
 * @author vlmap
 */
public class HbmPlugin extends PluginAdapter {


    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public void initialized(IntrospectedTable introspectedTable) {


        if(!isHbm(introspectedTable))return ;
        IntrospectedColumn  column=      introspectedTable.getColumn("data");
        column.setJavaProperty("xxxxxxx");

        column=      introspectedTable.getColumn("dia");
        column.setJavaProperty("xxxxxxxdia");



//        introspectedTable.getPrimaryKeyType()
//         introspectedTable.initialize();




    }
    boolean isHbm(IntrospectedTable introspectedTable){
        TableConfiguration  tableConfiguration= introspectedTable.getTableConfiguration();
        return "demo".equals(tableConfiguration.getTableName());
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {




        if(isHbm(introspectedTable)) {
            XmlElement associationElement = new XmlElement("association"); //$NON-NLS-1$


            associationElement.addAttribute(new Attribute(
                    "property", "id")); //$NON-NLS-1$
            associationElement.addAttribute(new Attribute(
                    "javaType", introspectedTable.getPrimaryKeyType())); //$NON-NLS-1$


            List<Element> elements = element.getElements();
            if (elements != null) {
                Iterator<Element> iterator = elements.iterator();
                while (iterator.hasNext()) {

                    Element next = iterator.next();
                    if (next instanceof XmlElement) {
                        XmlElement xml = (XmlElement) next;
                        if ("id".equals(xml.getName())) {
                            associationElement.addElement(xml);
                            iterator.remove();
                        }
                    }
                }

            }
            elements.add(0, associationElement);
        }
        return super.sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        return super.sqlMapResultMapWithBLOBsElementGenerated(element, introspectedTable);
    }
    //
//    @Override
//    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
//        if( Plugin.ModelClassType.PRIMARY_KEY==modelClassType){
//
//        }
//    }
//
//    @Override
//    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//
//        for (IntrospectedColumn introspectedColumn : introspectedTable
//                .getPrimaryKeyColumns()) {
//            if (RootClassInfo.getInstance(rootClass, warnings)
//                    .containsProperty(introspectedColumn)) {
//                continue;
//            }
//
//            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
//            if (plugins.modelFieldGenerated(field, topLevelClass,
//                    introspectedColumn, introspectedTable,
//                    Plugin.ModelClassType.PRIMARY_KEY)) {
//                topLevelClass.addField(field);
//                topLevelClass.addImportedType(field.getType());
//            }
//
//            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
//            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
//                    introspectedColumn, introspectedTable,
//                    Plugin.ModelClassType.PRIMARY_KEY)) {
//                topLevelClass.addMethod(method);
//            }
//
//            if (!introspectedTable.isImmutable()) {
//                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
//                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
//                        introspectedColumn, introspectedTable,
//                        Plugin.ModelClassType.PRIMARY_KEY)) {
//                    topLevelClass.addMethod(method);
//                }
//            }
//        }
//
//        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
//    }
}
