package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractSelectGenerator;

public class SelectAllGenerator extends AbstractSelectGenerator {
    @Override
    public String getId() {
        return "selectAll";
    }

    @Override
    protected Result getSelectType() {
        return Result.ALL;
    }
}
