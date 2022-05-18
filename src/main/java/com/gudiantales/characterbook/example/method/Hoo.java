package com.gudiantales.characterbook.example.method;

import java.util.Locale;

public interface Hoo {

    void printName();

    //기능을 추가하려면? DefaultHoo 같이 사용하는곳의 class들은 전부 에러가 발생한다.
    //void printNameUpperCase();

    //이렇게 추가하면 추가가 가능하다.

    /**
     * @implSpec
     * 이 구현체는 getName()으로 가져온 문자열을 대문자로 바꿔 출력한다.
     */
    default void printNameUpperCase(){
        System.out.println(getName().toUpperCase(Locale.ROOT));
    }

    static void printAnything() {
        System.out.println("Hoo");
    }

    //Object의 내용들은 재정의 할 수 없다.
    //    default String toString() {
    //    }
    //    String toString(); //가능

    String getName();
}
