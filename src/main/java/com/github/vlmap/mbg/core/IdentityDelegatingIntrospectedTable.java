package com.github.vlmap.mbg.core;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.ArrayList;
import java.util.List;

public class IdentityDelegatingIntrospectedTable extends DelegatingIntrospectedTable{
    public IdentityDelegatingIntrospectedTable(TargetRuntime targetRuntime, IntrospectedTable target) {
        super(targetRuntime, target);
    }

    private List<IntrospectedColumn> withIdentityJavaProperty(List<IntrospectedColumn> columnList) {
        List<IntrospectedColumn> result = new ArrayList<>();
        if (columnList != null) {
            for (IntrospectedColumn introspectedColumn : columnList) {
                result.add(IntrospectedTableUtils.withIdentityIntrospectedColumn(introspectedColumn));
            }
        }
        return result;

    }

    @Override
    public List<IntrospectedColumn> getAllColumns() {

        return withIdentityJavaProperty(target.getAllColumns());
    }

    @Override
    public List<IntrospectedColumn> getBaseColumns() {
        return withIdentityJavaProperty(target.getBaseColumns());
    }

    @Override
    public List<IntrospectedColumn> getBLOBColumns() {
        return withIdentityJavaProperty(target.getBLOBColumns());
    }

    @Override
    public List<IntrospectedColumn> getNonBLOBColumns() {
        return withIdentityJavaProperty(target.getNonBLOBColumns());
    }

    @Override
    public List<IntrospectedColumn> getPrimaryKeyColumns() {
        return withIdentityJavaProperty(target.getPrimaryKeyColumns());
    }

    @Override
    public List<IntrospectedColumn> getNonPrimaryKeyColumns() {
        return withIdentityJavaProperty(target.getNonPrimaryKeyColumns());
    }
}
