package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.mybatis3.xmlmapper.elements.AbstractSelectGenerator;

public class SelectListGenerator extends AbstractSelectGenerator {
    @Override
    public String getId() {
        return "selectList";
    }

    @Override
    protected Result getSelectType() {
        return Result.LIST;
    }
}
