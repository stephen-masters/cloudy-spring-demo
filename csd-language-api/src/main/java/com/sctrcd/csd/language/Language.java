package com.sctrcd.csd.language;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "language")
public class Language {

    @Id
    @Column(length = 2)
    private String isoCode;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String nativeName;

    public Language() {}

    public Language(String isoCode, String name, String nativeName) {
        this.isoCode = isoCode;
        this.name = name;
        this.nativeName = nativeName;
    }

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
