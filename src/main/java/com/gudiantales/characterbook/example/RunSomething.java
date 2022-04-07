package com.gudiantales.characterbook.example;

@FunctionalInterface
public interface RunSomething {
    // 함수 인터페이스
    // 추상 메소드가 2개 이상일경우 함수형 인터페이스가 아니다. 어노테이션에서 에러 발생

    int doIt(int number);
    //void doItAgain();


    /**
     Java 8
     추상메소드는 public 생략가능
     static정의 가능
     default 가능
     **/
//    static void printName() {
//        System.out.println("kang");
//    }
//
//    default void printAge() {
//        System.out.println("35");
//    }


}
