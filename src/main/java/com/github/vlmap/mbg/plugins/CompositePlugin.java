package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.*;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Document;

import java.util.ArrayList;
import java.util.List;


/**
 * mgb生成内容和自定义内容分离,该插件应该被最后被加载
 *
 * @author vlmap
 */
public class CompositePlugin extends PluginAdapter {

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
        list.add(new SelectKeyGenerator());
        for(Generator generator :list){
            if(generator instanceof AbstractGenerator){
                AbstractGenerator abstractGenerator=(AbstractGenerator)generator;
                abstractGenerator.initialized(introspectedTable);

            }
        }

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
