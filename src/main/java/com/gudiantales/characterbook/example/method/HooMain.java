package com.gudiantales.characterbook.example.method;

import com.gudiantales.characterbook.example.method.DefaultHoo;
import com.gudiantales.characterbook.example.method.Hoo;

public class HooMain {
    public static void main(String[] args) {
        Hoo hoo = new DefaultHoo("kang");
        hoo.printName();
        hoo.printNameUpperCase();

        Hoo.printAnything();

//        Bar bar = new DefaultHoo("kang");
//        bar.printNameUpperCase();
    }
}
