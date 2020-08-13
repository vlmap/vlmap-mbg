package com.github.vlmap.mbg.mybatis3.xmlmapper.elements;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3SimpleImpl;

import java.util.*;

public abstract class AbstractGenerator extends PluginAdapter implements Generator {


    public void addImportedType(Interface interfaze, Set<FullyQualifiedJavaType> importedTypes, Set<String> staticImports, IntrospectedTable introspectedTable) {
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    public abstract Optation getOptation();

    public abstract String getName();

    public abstract String getId();


    public void initialized(IntrospectedTable introspectedTable) {
    }


    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Optation optation = getOptation();

        String id = StringUtils.defaultString(properties.getProperty("id"), getId());
        switch (optation) {
            case ADD: {
                Method pre = getMethod(interfaze.getMethods(), id);

                List<Method> methods = methodGenerated(pre, new Method(id), interfaze, introspectedTable);

                if (methods != null && !methods.isEmpty()) {

                    removeAllMethod(interfaze.getMethods(), methods);

                    interfaze.getMethods().addAll(methods);
                }
                break;
            }
            case DELETE: {
                Method pre = getMethod(interfaze.getMethods(), id);

                List<Method> methods = methodGenerated(pre, null, interfaze, introspectedTable);
                if (methods != null && !methods.isEmpty()) {
                    removeAllMethod(interfaze.getMethods(), methods);
                }
                break;
            }
            case REPLACE: {
                Method pre = getMethod(interfaze.getMethods(), id);
                if (pre != null) {
                    List<Method> methods = methodGenerated(pre, new Method(id), interfaze, introspectedTable);

                    if (methods != null && !methods.isEmpty()) {

                        interfaze.getMethods().remove(pre);
                    }
                    if (methods != null) {
                        removeAllMethod(interfaze.getMethods(), methods);
                        interfaze.getMethods().addAll(methods);
                    }

                }
                break;
            }
        }


        addImportedType(interfaze, interfaze.getImportedTypes(), interfaze.getStaticImports(), introspectedTable);
        return true;
    }

    private void removeAllMethod(List<Method> parentMethods, List<Method> methods) {
        if (methods == null) return;
        for (Method method : methods) {

            for (Method parent : parentMethods) {

                if (methodEquals(method, parent)) {
                    parentMethods.remove(parent);
                    break;
                }

            }


        }
    }

    private boolean methodEquals(Method m1, Method m2) {
        if (Objects.equals(m1.getReturnType(), m2.getReturnType())
                && Objects.equals(m1.getName(), m2.getName())
                && m1.getParameters().size() == m2.getParameters().size()
        ) {
            for (int i = 0, size = m1.getParameters().size(); i < size; i++) {
                Parameter p1 = m1.getParameters().get(i);
                Parameter p2 = m2.getParameters().get(i);
                if (Objects.equals(p1.getType(), p2.getType())) {
                    return true;
                }
            }

        }
        return false;
    }

    private void removeAllElement(List<Element> parentElements, List<XmlElement> elements) {
        for (XmlElement element : elements) {
            XmlElement parentXml = getElement(parentElements,  getAttribute(element, "id"));
            if (parentXml != null) {
                parentElements.remove(parentXml);
            }


        }
    }



    protected boolean isSimple(IntrospectedTable introspectedTable) {
        if (introspectedTable instanceof IntrospectedTableMyBatis3SimpleImpl) {
            return true;
        }

        return false;
    }

    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        Optation optation = getOptation();

        String name = getName();
        String id = StringUtils.defaultString(properties.getProperty("id"), getId());

        switch (optation) {
            case ADD: {
                XmlElement pre = getElement(document.getRootElement().getElements(), name, id);
                XmlElement answer = new XmlElement(name);
                if (StringUtils.isNotBlank(id)) {
                    answer.addAttribute(new Attribute("id", id));
                }

                List<XmlElement> elements = xmlGenerated(pre, answer, document, introspectedTable);

                if (elements != null && !elements.isEmpty()) {


                    removeAllElement(document.getRootElement().getElements(), elements);
                    document.getRootElement().getElements().addAll(elements);
                }
                break;
            }
            case DELETE: {

                XmlElement pre = getElement(document.getRootElement().getElements(), name, id);


                List<XmlElement> elements = xmlGenerated(pre, null, document, introspectedTable);
                if (elements != null && !elements.isEmpty()) {
                    removeAllElement(document.getRootElement().getElements(), elements);

                }
                break;
            }
            case REPLACE: {
                XmlElement pre = getElement(document.getRootElement().getElements(), name, id);

                XmlElement answer = new XmlElement(name);
                if (StringUtils.isNotBlank(id)) {
                    answer.addAttribute(new Attribute("id", id));
                }
                List<XmlElement> elements = xmlGenerated(pre, answer, document, introspectedTable);
                if (pre != null) {
                    document.getRootElement().getElements().remove(pre);
                    if (elements != null && !elements.isEmpty()) {

                        removeAllElement(document.getRootElement().getElements(), elements);
                        document.getRootElement().getElements().addAll(elements);

                    }
                }


            }
        }
        Collections.sort(document.getRootElement().getElements(), new MapperComparator());
        return true;
    }


    protected IntrospectedColumn getIntrospectedColumn(IntrospectedTable introspectedTable, String property) {
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (property.equals(introspectedColumn.getJavaProperty())
                    || property.equals(IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn).getJavaProperty())) {
                return introspectedColumn;
            }
        }
        return null;
    }

    protected Method getMethod(List<Method> list, String name) {
        if (StringUtils.isBlank(name)) return null;
        for (Method method : list) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
    protected XmlElement getElement(List<Element> list,   String id) {
        for (Element element : list) {
            if (element instanceof XmlElement) {
                XmlElement el = (XmlElement) element;
                String _name = el.getName();
                String _id = getAttribute(el, "id");
                if (StringUtils.equals(_id, id)) {
                    return el;
                }

            }

        }


        return null;
    }
    protected XmlElement getElement(List<Element> list, String name, String id) {
        for (Element element : list) {
            if (element instanceof XmlElement) {
                XmlElement el = (XmlElement) element;
                String _name = el.getName();
                String _id = getAttribute(el, "id");
                if (StringUtils.equals(_name, name) && StringUtils.equals(_id, id)) {
                    return el;
                }

            }

        }


        return null;
    }

    protected String getAttribute(XmlElement element, String name) {
        for (Attribute attribute : element.getAttributes()) {
            if (attribute.getName().equals(name)) {
                return attribute.getValue();
            }

        }

        return null;

    }


    public abstract List<Method> methodGenerated(Method pre, Method answer, Interface interfaze, IntrospectedTable introspectedTable);

    public abstract List<XmlElement> xmlGenerated(XmlElement pre, XmlElement answer, Document document, IntrospectedTable introspectedTable);

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
