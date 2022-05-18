package com.gudiantales.characterbook.example.method;

public interface Bar {
    default void printNameUpperCase(){
        System.out.println("BAR");
    };
}
