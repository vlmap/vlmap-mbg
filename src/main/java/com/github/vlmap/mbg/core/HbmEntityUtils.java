package com.github.vlmap.mbg.core;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.mybatis.generator.api.IntrospectedTable;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HbmEntityUtils {
    public static AbstractEntityPersister entityPersister(IntrospectedTable introspectedTable){
        String modelClass = IntrospectedTableUtils.getHbmModelClass(introspectedTable);
        introspectedTable.setBaseRecordType(modelClass);
        String dialect=   introspectedTable.getTableConfigurationProperty("hibernate.dialect");


        EntityManagerFactory entityManagerFactory = null;
        try {
            Class clazz = Class.forName(modelClass);

            List<String> classList = new ArrayList<>();
            classList.add(clazz.getName());
            java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Class type = method.getReturnType();
                if (type != null) {
                    classList.add(type.getName());
                }
                for (Class c : method.getParameterTypes()) {
                    classList.add(c.getName());
                }


            }
            Properties properties = new Properties();
            if(StringUtils.isBlank(dialect)){
                properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
            }else{
                properties.put("hibernate.dialect", dialect);
            }

            PersistenceUnitInfoDescriptor descriptor = new PersistenceUnitInfoDescriptor(new MutablePersistenceUnitInfo()) {
                @Override
                public List<String> getManagedClassNames() {
                    return classList;
                }

                @Override
                public String getName() {
                    return "defaut";
                }

                @Override
                public ClassLoader getTempClassLoader() {
                    return clazz.getClassLoader();
                }

                @Override
                public ClassLoader getClassLoader() {
                    return clazz.getClassLoader();
                }
            };

            entityManagerFactory = new EntityManagerFactoryBuilderImpl(descriptor
                    , properties).build();
        } catch (Exception e) {
            return null;
        }

        MetamodelImplementor metamodelImplementor = (MetamodelImplementor) entityManagerFactory.getMetamodel();
        return (AbstractEntityPersister) metamodelImplementor.entityPersister(modelClass);
    }
}
