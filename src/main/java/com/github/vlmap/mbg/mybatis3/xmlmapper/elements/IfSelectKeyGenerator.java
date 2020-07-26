package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

 import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;

 import java.util.Arrays;
 import java.util.List;
 import java.util.Set;

public class IfSelectKeyGenerator extends AbstractGenerator {
    public IfSelectKeyGenerator(String name, String id) {
        super(name, id, Optation.REPLACE);
    }

    @Override
    public void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable) {

    }

    @Override
    public List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable) {

        return null;
    }

    @Override
    public List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable) {
        if (pre != null) {
            answer.getAttributes().addAll(pre.getAttributes());

            for (Element element : pre.getElements()) {
                if(element instanceof XmlElement){
                    XmlElement xmlElement=(XmlElement)   element;
                    if(xmlElement.getName().equals("selectKey")){
                        XmlElement ifElement=   new XmlElement("if");
                        ifElement.addElement(element);
                        String value=getAttribute(xmlElement,"keyProperty");
                        if(StringUtils.isNotBlank(value)){
                            IntrospectedColumn introspectedColumn=      getIntrospectedColumn(introspectedTable,value);
                            if(introspectedColumn!=null){
                                ifElement.addAttribute(new Attribute("test", "!_parameter.containsKey('"+introspectedColumn.getJavaProperty()+"')"));
                            }
                        }
                        answer.addElement(ifElement);
                        continue;

                    }
                }
                answer.addElement(element);
            }
        }
        return Arrays.asList(answer);
    }

}
