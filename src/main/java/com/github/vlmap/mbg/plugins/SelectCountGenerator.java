package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractSelectGenerator;

public class SelectCountGenerator extends AbstractSelectGenerator {
    @Override
    public String getId() {
        return "count";
    }

    @Override
    protected Result getSelectType() {
        return Result.COUNT;
    }
}
