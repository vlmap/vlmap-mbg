/**
 * Copyright 2006-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.vlmap.mbg.core;

import org.mybatis.generator.api.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.rules.Rules;

import java.util.List;


public class DelegatingIntrospectedTable extends IntrospectedTable {
   protected IntrospectedTable target;

    public DelegatingIntrospectedTable(TargetRuntime targetRuntime, IntrospectedTable target) {
        super(targetRuntime);
        this.target = target;
    }

    public FullyQualifiedTable getFullyQualifiedTable() {
        return target.getFullyQualifiedTable();
    }

    public String getSelectByExampleQueryId() {
        return target.getSelectByExampleQueryId();
    }

    public String getSelectByPrimaryKeyQueryId() {
        return target.getSelectByPrimaryKeyQueryId();
    }

    public GeneratedKey getGeneratedKey() {
        return target.getGeneratedKey();
    }

    public IntrospectedColumn getColumn(String columnName) {
        return target.getColumn(columnName);
    }

    public boolean hasJDBCDateColumns() {
        return target.hasJDBCDateColumns();
    }

    public boolean hasJDBCTimeColumns() {
        return target.hasJDBCTimeColumns();
    }


    public List<IntrospectedColumn> getPrimaryKeyColumns() {
        return target.getPrimaryKeyColumns();

    }

    public boolean hasPrimaryKeyColumns() {
        return target.hasPrimaryKeyColumns();
    }

    public List<IntrospectedColumn> getBaseColumns() {
        return target.getBaseColumns();
    }


    public List<IntrospectedColumn> getAllColumns() {
        return target.getAllColumns();
    }


    public List<IntrospectedColumn> getNonBLOBColumns() {
        return target.getNonBLOBColumns();
    }

    public int getNonBLOBColumnCount() {
        return target.getNonBLOBColumnCount();
    }

    public List<IntrospectedColumn> getNonPrimaryKeyColumns() {
        return target.getNonPrimaryKeyColumns();
    }

    public List<IntrospectedColumn> getBLOBColumns() {
        return target.getBLOBColumns();
    }

    public boolean hasBLOBColumns() {
        return target.hasBLOBColumns();
    }

    public boolean hasBaseColumns() {
        return target.hasBaseColumns();
    }

    public Rules getRules() {
        return target.getRules();
    }

    public String getTableConfigurationProperty(String property) {
        return target.getTableConfigurationProperty(property);
    }

    public String getPrimaryKeyType() {
        return target.getPrimaryKeyType();
    }


    public String getBaseRecordType() {
        return target.getBaseRecordType();
    }


    public String getExampleType() {
        return target.getExampleType();
    }


    public String getRecordWithBLOBsType() {
        return target.getRecordWithBLOBsType();
    }


    public String getIbatis2SqlMapFileName() {
        return target.getIbatis2SqlMapFileName();
    }

    public String getIbatis2SqlMapNamespace() {
        return target.getIbatis2SqlMapNamespace();
    }

    public String getMyBatis3SqlMapNamespace() {
        return target.getMyBatis3SqlMapNamespace();

    }

    public String getMyBatis3FallbackSqlMapNamespace() {
        return target.getMyBatis3FallbackSqlMapNamespace();
    }

    /**
     * Calculates the package for the current table.
     *
     * @return the package for the SqlMap for the current table
     */
    public String getIbatis2SqlMapPackage() {
        return target.getIbatis2SqlMapPackage();
    }

    public String getDAOImplementationType() {
        return target.getDAOImplementationType();
    }

    public String getDAOInterfaceType() {
        return target.getDAOInterfaceType();
    }

    public boolean hasAnyColumns() {
        return target.hasAnyColumns();
    }

    public void setTableConfiguration(TableConfiguration tableConfiguration) {
        target.setTableConfiguration(tableConfiguration);
    }

    public void setFullyQualifiedTable(FullyQualifiedTable fullyQualifiedTable) {
        target.setFullyQualifiedTable(fullyQualifiedTable);

    }

    public void setContext(Context context) {
        target.setContext(context);
    }

    public void addColumn(IntrospectedColumn introspectedColumn) {
        target.addColumn(introspectedColumn);


    }

    public void addPrimaryKeyColumn(String columnName) {
        target.addPrimaryKeyColumn(columnName);
    }

    public Object getAttribute(String name) {
        return target.getAttribute(name);
    }

    public void removeAttribute(String name) {
        target.removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        target.setAttribute(name, value);
    }

    public void initialize() {
        target.initialize();

    }


    public void setBlobColumnListId(String s) {
        target.setBlobColumnListId(s);
    }

    public void setBaseColumnListId(String s) {
        target.setBaseColumnListId(s);

    }

    public void setExampleWhereClauseId(String s) {
        target.setExampleWhereClauseId(s);

    }

    public void setMyBatis3UpdateByExampleWhereClauseId(String s) {
        target.setMyBatis3UpdateByExampleWhereClauseId(s);

    }

    public void setResultMapWithBLOBsId(String s) {
        target.setResultMapWithBLOBsId(s);
    }

    public void setBaseResultMapId(String s) {
        target.setBaseResultMapId(s);
    }

    public void setUpdateByPrimaryKeyWithBLOBsStatementId(String s) {
        target.setUpdateByPrimaryKeyWithBLOBsStatementId(s);
    }

    public void setUpdateByPrimaryKeySelectiveStatementId(String s) {
        target.setUpdateByPrimaryKeySelectiveStatementId(s);

    }

    public void setUpdateByPrimaryKeyStatementId(String s) {
        target.setUpdateByPrimaryKeyStatementId(s);
    }

    public void setUpdateByExampleWithBLOBsStatementId(String s) {
        target.setUpdateByExampleWithBLOBsStatementId(s);
    }

    public void setUpdateByExampleSelectiveStatementId(String s) {
        target.setUpdateByExampleSelectiveStatementId(s);
    }

    public void setUpdateByExampleStatementId(String s) {
        target.setUpdateByExampleStatementId(s);
    }

    public void setSelectByPrimaryKeyStatementId(String s) {
        target.setSelectByPrimaryKeyStatementId(s);
    }

    public void setSelectByExampleWithBLOBsStatementId(String s) {
        target.setSelectByExampleWithBLOBsStatementId(s);
    }

    public void setSelectAllStatementId(String s) {
        target.setSelectAllStatementId(s);
    }

    public void setSelectByExampleStatementId(String s) {
        target.setSelectByExampleStatementId(s);
    }

    public void setInsertSelectiveStatementId(String s) {
        target.setInsertSelectiveStatementId(s);
    }

    public void setInsertStatementId(String s) {
        target.setInsertStatementId(s);
    }

    public void setDeleteByPrimaryKeyStatementId(String s) {
        target.setDeleteByPrimaryKeyStatementId(s);
    }

    public void setDeleteByExampleStatementId(String s) {
        target.setDeleteByExampleStatementId(s);
    }

    public void setCountByExampleStatementId(String s) {
        target.setCountByExampleStatementId(s);
    }

    public String getBlobColumnListId() {
        return target.getBlobColumnListId();
    }

    public String getBaseColumnListId() {
        return target.getBaseColumnListId();
    }

    public String getExampleWhereClauseId() {
        return target.getExampleWhereClauseId();
    }

    public String getMyBatis3UpdateByExampleWhereClauseId() {
        return target.getMyBatis3UpdateByExampleWhereClauseId();
    }

    public String getResultMapWithBLOBsId() {
        return target.getResultMapWithBLOBsId();
    }

    public String getBaseResultMapId() {
        return target.getBaseResultMapId();
    }

    public String getUpdateByPrimaryKeyWithBLOBsStatementId() {
        return target.getUpdateByPrimaryKeyWithBLOBsStatementId();
    }

    public String getUpdateByPrimaryKeySelectiveStatementId() {
        return target.getUpdateByPrimaryKeySelectiveStatementId();
    }

    public String getUpdateByPrimaryKeyStatementId() {
        return target.getUpdateByPrimaryKeyStatementId();
    }

    public String getUpdateByExampleWithBLOBsStatementId() {
        return target.getUpdateByExampleWithBLOBsStatementId();
    }

    public String getUpdateByExampleSelectiveStatementId() {
        return target.getUpdateByExampleSelectiveStatementId();
    }

    public String getUpdateByExampleStatementId() {
        return target.getUpdateByExampleStatementId();
    }

    public String getSelectByPrimaryKeyStatementId() {
        return target.getSelectByPrimaryKeyStatementId();
    }

    public String getSelectByExampleWithBLOBsStatementId() {
        return target.getSelectByExampleWithBLOBsStatementId();
    }

    public String getSelectAllStatementId() {
        return target.getSelectAllStatementId();
    }

    public String getSelectByExampleStatementId() {
        return target.getSelectByExampleStatementId();
    }

    public String getInsertSelectiveStatementId() {
        return target.getInsertSelectiveStatementId();
    }

    public String getInsertStatementId() {
        return target.getInsertStatementId();
    }

    public String getDeleteByPrimaryKeyStatementId() {
        return target.getDeleteByPrimaryKeyStatementId();
    }

    public String getDeleteByExampleStatementId() {
        return target.getDeleteByExampleStatementId();
    }

    public String getCountByExampleStatementId() {
        return target.getCountByExampleStatementId();
    }


    public String getFullyQualifiedTableNameAtRuntime() {
        return target.getFullyQualifiedTableNameAtRuntime();
    }

    public String getAliasedFullyQualifiedTableNameAtRuntime() {
        return target.getAliasedFullyQualifiedTableNameAtRuntime();
    }


    public void calculateGenerators(List<String> warnings,
                                    ProgressCallback progressCallback) {
        target.calculateGenerators(warnings, progressCallback);
    }

    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        return target.getGeneratedJavaFiles();
    }


    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        return target.getGeneratedXmlFiles();
    }


    public boolean isJava5Targeted() {
        return target.isJava5Targeted();
    }

    public int getGenerationSteps() {
        return target.getGenerationSteps();
    }


    public void setRules(Rules rules) {
        target.setRules(rules);
    }

    public TableConfiguration getTableConfiguration() {
        return target.getTableConfiguration();
    }

    public void setDAOImplementationType(String daoImplementationType) {
        target.setDAOImplementationType(daoImplementationType);
    }

    public void setDAOInterfaceType(String daoInterfaceType) {
        target.setDAOInterfaceType(daoInterfaceType);
    }

    public void setPrimaryKeyType(String primaryKeyType) {
        target.setPrimaryKeyType(primaryKeyType);
    }

    public void setBaseRecordType(String baseRecordType) {
        target.setBaseRecordType(baseRecordType);
    }

    public void setRecordWithBLOBsType(String recordWithBLOBsType) {
        target.setRecordWithBLOBsType(recordWithBLOBsType);
    }

    public void setExampleType(String exampleType) {
        target.setExampleType(exampleType);
    }

    public void setIbatis2SqlMapPackage(String sqlMapPackage) {
        target.setIbatis2SqlMapPackage(sqlMapPackage);
    }

    public void setIbatis2SqlMapFileName(String sqlMapFileName) {
        target.setIbatis2SqlMapFileName(sqlMapFileName);
    }

    public void setIbatis2SqlMapNamespace(String sqlMapNamespace) {
        target.setIbatis2SqlMapNamespace(sqlMapNamespace);
    }

    public void setMyBatis3FallbackSqlMapNamespace(String sqlMapNamespace) {
        target.setMyBatis3FallbackSqlMapNamespace(sqlMapNamespace);
    }

    public void setSqlMapFullyQualifiedRuntimeTableName(
            String fullyQualifiedRuntimeTableName) {
        target.setSqlMapFullyQualifiedRuntimeTableName(fullyQualifiedRuntimeTableName);
    }

    public void setSqlMapAliasedFullyQualifiedRuntimeTableName(
            String aliasedFullyQualifiedRuntimeTableName) {
        target.setSqlMapAliasedFullyQualifiedRuntimeTableName(aliasedFullyQualifiedRuntimeTableName);
    }

    public String getMyBatis3XmlMapperPackage() {
        return target.getMyBatis3XmlMapperPackage();
    }

    public void setMyBatis3XmlMapperPackage(String mybatis3XmlMapperPackage) {
        target.setMyBatis3XmlMapperPackage(mybatis3XmlMapperPackage);
    }

    public String getMyBatis3XmlMapperFileName() {
        return target.getMyBatis3XmlMapperFileName();
    }

    public void setMyBatis3XmlMapperFileName(String mybatis3XmlMapperFileName) {
        target.setMyBatis3XmlMapperFileName(mybatis3XmlMapperFileName);
    }

    public String getMyBatis3JavaMapperType() {
        return target.getMyBatis3JavaMapperType();
    }

    public void setMyBatis3JavaMapperType(String mybatis3JavaMapperType) {
        target.setMyBatis3JavaMapperType(mybatis3JavaMapperType);
    }

    public String getMyBatis3SqlProviderType() {
        return target.getMyBatis3SqlProviderType();
    }

    public void setMyBatis3SqlProviderType(String mybatis3SqlProviderType) {
        target.setMyBatis3SqlProviderType(mybatis3SqlProviderType);
    }

    public String getMyBatisDynamicSqlSupportType() {
        return target.getMyBatisDynamicSqlSupportType();
    }

    public void setMyBatisDynamicSqlSupportType(String s) {
        target.setMyBatisDynamicSqlSupportType(s);
    }

    public TargetRuntime getTargetRuntime() {
        return target.getTargetRuntime();
    }

    public boolean isImmutable() {
        return target.isImmutable();
    }

    public boolean isConstructorBased() {
        return target.isConstructorBased();


    }


    public boolean requiresXMLGenerator() {
        return target.requiresXMLGenerator();
    }

    public Context getContext() {
        return target.getContext();
    }

    public String getRemarks() {
        return target.getRemarks();
    }

    public void setRemarks(String remarks) {
        target.setRemarks(remarks);
    }

    public String getTableType() {
        return target.getTableType();
    }

    public void setTableType(String tableType) {
        target.setTableType(tableType);
    }
}
