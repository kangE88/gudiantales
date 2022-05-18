package com.gudiantales.characterbook.example.method;

import java.util.Locale;

public class DefaultHoo implements Hoo {

    String name;

    public DefaultHoo(String name) {
        this.name = name;
    }

    @Override
    public void printName() {
        System.out.println(this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    //메소드 재정의 가능
    @Override
    public void printNameUpperCase() {
        System.out.println(this.name.toUpperCase(Locale.ROOT));
    }


}
