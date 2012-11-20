package com.dp.bigdata.taurus.core;

import java.text.NumberFormat;

/**
 * 
 * ID
 * @author damon.zhu
 *
 */
public abstract class ID {
    protected static final String SEPARATOR = "_";
    protected int id;

    protected static final NumberFormat idFormat = NumberFormat.getInstance();
    static {
        idFormat.setGroupingUsed(false);
        idFormat.setMinimumIntegerDigits(4);
    }

    /** constructs an ID object from the given int */
    public ID(int id) {
        this.id = id;
    }

    protected ID() {
    }
}
