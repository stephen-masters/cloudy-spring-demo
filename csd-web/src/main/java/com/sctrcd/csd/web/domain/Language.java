package com.sctrcd.csd.web.domain;

public class Language {

    private String isoCode;
    private String name;
    private String nativeName;

    public Language() {}

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    @Override
    public String toString() {
        return "Language(isoCode=" + isoCode + ", name=" + name + ", nativeName=" + nativeName + ")";
    }
}
