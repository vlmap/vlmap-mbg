package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractSelectGenerator;

public class SelectOneGenerator  extends AbstractSelectGenerator {
    @Override
    public String getId() {
        return "selectOne";
    }

    @Override
    protected Result getSelectType() {
        return Result.ONE;
    }
}
