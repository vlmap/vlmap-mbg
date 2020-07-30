package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.HbmEntityUtils;
import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.Type;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.GeneratedKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * 使用  Hibernate  JPA Annotation Class 方式生成  Mapper
 *
 * @author vlmap
 */
public class HbmPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    protected void calculateIdentifierGenerator(IntrospectedTable introspectedTable, AbstractEntityPersister entityPersister) {
        SessionFactoryImplementor entityManagerFactory = (SessionFactoryImplementor) introspectedTable.getAttribute("sessionFactoryImplementor");

        Dialect dialect = entityManagerFactory.getJdbcServices().getDialect();
        IdentifierGenerator identifierGenerator = entityPersister.getIdentifierGenerator();
        GeneratedKey key = null;
        //处理主键生成策略
        if (identifierGenerator instanceof SequenceStyleGenerator) {
            SequenceStyleGenerator generator = (SequenceStyleGenerator) identifierGenerator;

            String select = dialect.getSequenceNextValString(Objects.toString(generator.generatorKey()));
            GeneratedKey generatedKey = introspectedTable.getTableConfiguration().getGeneratedKey();
            if (generatedKey == null) {
                key = new GeneratedKey(entityPersister.getIdentifierColumnNames()[0], select, true, null);
            } else {
                key = new GeneratedKey(generatedKey.getColumn(), select, generatedKey.isIdentity(), generatedKey.getType());
            }

        } else if (identifierGenerator instanceof IdentityGenerator) {


            String select = entityPersister.getIdentitySelectString();
            GeneratedKey generatedKey = introspectedTable.getTableConfiguration().getGeneratedKey();


            if (generatedKey == null) {
                key = new GeneratedKey(entityPersister.getIdentifierColumnNames()[0], select, true, null);
            } else {
                key = new GeneratedKey(generatedKey.getColumn(), select, generatedKey.isIdentity(), generatedKey.getType());
            }

        } else if (identifierGenerator instanceof TableGenerator) {
            if (!IntrospectedTableUtils.isIgnoreTableGenerator(this)) {
                throw new IllegalArgumentException("不支持 Hibernate 生成策略为 GenerationType.TABLE的主键转换");
            }

        }

        if (key != null) {
            introspectedTable.getTableConfiguration().setGeneratedKey(key);
            introspectedTable.getColumn(key.getColumn()).setGeneratedAlways(true);
        }

    }

    protected void calculateIntrospectedColumn(IntrospectedTable introspectedTable, AbstractEntityPersister entityPersister, Type identifierType, String propertyName, List<IntrospectedColumn> collect) {
        //普通主键、单列对象
        if (identifierType instanceof SingleColumnType) {

            String columnName = entityPersister.getPropertyColumnNames(propertyName)[0];
            String javaType = identifierType.getReturnedClass().getName();

            IntrospectedColumn column = introspectedTable.getColumn(columnName);
            if (column == null) {
                String msg = String.format("Schema[%s] Table[%s] Column[%s] not exist ,please check class[%s] field[%s]",
                        ObjectUtils.toString(introspectedTable.getTableConfiguration().getSchema()),
                        introspectedTable.getTableConfiguration().getTableName(),
                        columnName,
                        entityPersister.getEntityName(),
                        propertyName);
                throw new IllegalArgumentException(msg);

            }
            column.setJavaProperty(propertyName);
            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(javaType));

            collect.add(column);
        }
        //复合主键、复合对象
        if (identifierType instanceof ComponentType) {
            String[] identifierColumnNames = entityPersister.getIdentifierColumnNames();
            ComponentType componentType = (ComponentType) identifierType;
            String[] propertyNames = componentType.getPropertyNames();
            String identifierPropertyName = entityPersister.getIdentifierPropertyName();

            if (!componentType.isEmbedded()) {
                IntrospectedTableUtils.setIdentifierPropertyName(introspectedTable, identifierPropertyName);
            }



            for (int i = 0; i < propertyNames.length; i++) {
                String columnName = identifierColumnNames[i];
                IntrospectedColumn column = introspectedTable.getColumn(columnName);
                if (column == null) {
                    String msg = String.format("Schema[%s] Table[%s] Column[%s] not exist ,please check class[%s] field[%s]",
                            ObjectUtils.toString(introspectedTable.getTableConfiguration().getSchema()),
                            introspectedTable.getTableConfiguration().getTableName(),
                            columnName,
                            entityPersister.getEntityName(),
                            propertyName);
                    throw new IllegalArgumentException(msg);

                }
                column.setJavaProperty(propertyNames[i]);
                column.setFullyQualifiedJavaType(new FullyQualifiedJavaType(componentType.getSubtypes()[i].getReturnedClass().getName()));


                collect.add(column);
            }

        }
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {


        if (!IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) return;

        try {


            AbstractEntityPersister entityPersister = HbmEntityUtils.entityPersister(introspectedTable);


            IntrospectedTableUtils.entityPersiter(introspectedTable, entityPersister);

            Type identifierType = entityPersister.getIdentifierType();


            List<IntrospectedColumn> introspectedColumns = new ArrayList<>();

            if (identifierType != null) {

                String identifierPropertyName = entityPersister.getIdentifierPropertyName();

                calculateIntrospectedColumn(introspectedTable, entityPersister, identifierType, identifierPropertyName, introspectedColumns);
                if (identifierType instanceof ComponentType) {
                    introspectedTable.addPrimaryKeyColumn(identifierPropertyName);
                }
            }


            Type[] propertyTypes = entityPersister.getPropertyTypes();

            for (int i = 0, size = propertyTypes.length; i < size; i++) {
                calculateIntrospectedColumn(introspectedTable, entityPersister, propertyTypes[i], entityPersister.getPropertyNames()[i], introspectedColumns);
            }
            //计算主键生成策略
            calculateIdentifierGenerator(introspectedTable, entityPersister);


//            //只使用 Entity类返回的Column生成和privaryKey，其他清理掉
            Iterator<IntrospectedColumn> iterator = introspectedTable.getNonPrimaryKeyColumns().iterator();

            while (iterator.hasNext()) {
                IntrospectedColumn next = iterator.next();
                if (!introspectedColumns.contains(next)) {
                    iterator.remove();
                }
            }

            IntrospectedTableUtils.calculate(introspectedTable);
            String modelClass = IntrospectedTableUtils.getHbmModelClass(introspectedTable);

            introspectedTable.setBaseRecordType(modelClass);

            if (identifierType instanceof ComponentType) {
                introspectedTable.setPrimaryKeyType(identifierType.getReturnedClass().getName());


            }
        } catch (Exception e) {
            String modelClass = IntrospectedTableUtils.getHbmModelClass(introspectedTable);
            System.out.println("modelClass:" + modelClass + ",parse error");
            throw new IllegalArgumentException(e);

        }
    }


}
