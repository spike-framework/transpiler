package com.spike.transpiler.model;

public class ExtendingModel {

    public String extendsFrom;
    public String extendsTo;

    public ExtendingModel(String extendsFrom, String extendsTo) {
        this.extendsFrom = extendsFrom;
        this.extendsTo = extendsTo;
    }

    @Override
    public String toString() {
        return "ExtendingModel{" +
                "extendsFrom='" + extendsFrom + '\'' +
                ", extendsTo='" + extendsTo + '\'' +
                '}';
    }
}
