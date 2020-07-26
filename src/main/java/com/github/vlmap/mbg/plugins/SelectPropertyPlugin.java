package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.DelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IdentityDelegatingIntrospectedTable;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * mgb生成内容和自定义内容分离,该插件应该被最后被加载
 *
 * @author vlmap
 */
public class SelectPropertyPlugin extends PluginAdapter {

    List<Generator> list = new ArrayList<>();

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        list.clear();
        list.add(new SelectGenerator("count", SelectGenerator.Result.COUNT));
        list.add(new SelectGenerator(introspectedTable.getSelectAllStatementId(), SelectGenerator.Result.ALL));
        list.add(new SelectGenerator("selectOne", SelectGenerator.Result.ONE));
        list.add(new SelectGenerator("selectList", SelectGenerator.Result.LIST));
        list.add(new SaveOrUpdateGenerator("saveOrUpdate"));
        list.add(new GetSelectKeyGenerator( ));

    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if(topLevelClass==null){
            for(Generator generator:list){
                generator.clientGenerated(interfaze,topLevelClass,introspectedTable);
                generator.addImportedType(interfaze,interfaze.getImportedTypes(),interfaze.getStaticImports(),introspectedTable);
            }
        }



        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }



    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        for(Generator generator:list){
            generator.sqlMapDocumentGenerated(document,introspectedTable);
        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


}
