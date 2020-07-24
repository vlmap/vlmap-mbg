package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.*;

public abstract class AbstractGenerator implements Generator {
    Optation optation;

    String name;
    String id;

    public AbstractGenerator(String name, String id, Optation optation) {
        this.optation = optation;
        this.name = name;
        this.id = id;
    }


    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        switch (optation) {
            case ADD: {
                Method method = methodGenerated(null, new Method(id), interfaze, introspectedTable);

                if (method != null) {
                    interfaze.addMethod(method);
                }
                break;
            }
            case DELETE: {
                Method pre = getMethod(interfaze.getMethods(), id);
                if (pre != null) {
                    interfaze.getMethods().remove(pre);
                }
                methodGenerated(pre, null, interfaze, introspectedTable);
                break;
            }
            case REPLACE: {
                Method pre = getMethod(interfaze.getMethods(), id);

                Method method = methodGenerated(pre, new Method(id), interfaze, introspectedTable);

                if (method != null) {
                    if (pre != null) {
                        interfaze.getMethods().remove(pre);
                    }
                    interfaze.addMethod(method);
                }
                break;
            }
        }


        addImportedType(interfaze, interfaze.getImportedTypes(), interfaze.getStaticImports(), introspectedTable);
        return false;
    }

    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        switch (optation) {
            case ADD: {
                XmlElement answer = new XmlElement(name);
                if (StringUtils.isNotBlank(id)) {
                    answer.addAttribute(new Attribute("id", id));
                }

                answer = xmlGenerated(null, answer, document, introspectedTable);

                if (answer != null) {
                    document.getRootElement().getElements().add(answer);
                }
                break;
            }
            case DELETE: {

                XmlElement pre = getElement(document.getRootElement().getElements(), name, id);

                if (pre != null) {
                    document.getRootElement().getElements().remove(pre);
                }
                xmlGenerated(pre, null, document, introspectedTable);
                break;
            }
            case REPLACE: {
                XmlElement pre = getElement(document.getRootElement().getElements(), name, id);

                XmlElement answer = new XmlElement(name);
                if (StringUtils.isNotBlank(id)) {
                    answer.addAttribute(new Attribute("id", id));
                }
                answer = xmlGenerated(pre, answer, document, introspectedTable);

                if (answer != null) {
                    if (pre != null) {
                        document.getRootElement().getElements().remove(pre);
                    }
                    document.getRootElement().getElements().add(answer);

                }


            }
        }
        Collections.sort(document.getRootElement().getElements(), new MapperComparator());
        return false;
    }

    public Method getMethod(List<Method> list, String name) {
        for (Method method : list) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    public XmlElement getElement(List<Element> list, String name, String id) {
        for (Element element : list) {
            XmlElement el = (XmlElement) element;
            String _name = el.getName();
            String _id = getAttribute(el, "id");
            if (StringUtils.equals(_name, name) && StringUtils.equals(_id, id)) {
                return el;
            }

        }


        return null;
    }

    public String getAttribute(XmlElement element, String name) {
        for (Attribute attribute : element.getAttributes()) {
            if (attribute.getName().equals(name)) {
                return attribute.getValue();
            }

        }

        return null;

    }

    public abstract void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable);

    public abstract Method methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable);

    public abstract XmlElement xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable);

    class MapperComparator implements Comparator<Element> {
        //    cache-ref | cache | resultMap* | parameterMap* | sql* | insert* | update* | delete* | select
        Map<String, Integer> map = new HashMap<>();

        public MapperComparator() {
            int i = Integer.MIN_VALUE;
            map.put("cache-ref", i++);
            map.put("cache", i++);
            map.put("resultMap", i++);
            map.put("parameterMap", i++);
            map.put("sql", i++);
            map.put("insert", i++);
            map.put("update", i++);
            map.put("delete", i++);
            map.put("select", i++);
        }

        @Override
        public int compare(Element o1, Element o2) {
            XmlElement e1 = (XmlElement) o1;
            XmlElement e2 = (XmlElement) o2;
            Integer value1 = e1 != null && map.containsKey(e1.getName()) ? map.get(e1.getName()) : 0;
            Integer value2 = e2 != null && map.containsKey(e2.getName()) ? map.get(e2.getName()) : 0;

            return value1.compareTo(value2);

        }
    }
}
