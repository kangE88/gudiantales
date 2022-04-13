package com.gudiantales.characterbook.example;

import java.util.function.*;

public class Foo {

    public static void main(String[] args) {
        ////2022-04-12

        //Function<T, R>
        //T 타입을 받아서 R타입을 리턴하는 함수 인터페이스
        Plus10 plus10 = new Plus10();
        System.out.println(plus10.apply(1));

        //바로 사용 가능
        Function<Integer, Integer> plus10ex2 = (i) -> i + 10;
        Function<Integer, Integer> multiply = (i) -> i * 2;

        //Function<Integer, Integer> 입력, 리턴 값이 같다면 UnaryOperator<Integer> 로 변환해서 사용 가능하다.
        UnaryOperator<Integer> plus10Ex3 = (i) -> i + 10;
        UnaryOperator<Integer> plus10Ex4 = (i) -> i * 2;

        System.out.println(plus10Ex3.apply(2));
        System.out.println(plus10Ex3.compose(plus10Ex4).apply(4));

        //함수 결합
        //multiply 수행 후 plus10ex2 실행
        //plus10ex2.compose(multiply);
        Function<Integer, Integer> compose = plus10ex2.compose(multiply);
        System.out.println("compose >> "+compose.apply(2));

        //andThen
        //plus10ex2 수행 후 multiply 실행
        //plus10ex2.andThen(multiply);
        Function<Integer, Integer> andThen = plus10ex2.andThen(multiply);
        System.out.println("andThen >> " + andThen.apply(3));

        //BiFuntion<T, U, R>
        //두 개의 값(T, U)를 받아서 R타입을 리턴하는 함수 인터페이스
//        BiFunction<Integer, Integer, Integer> biFunction = plus10ex2

        //BinaryOperator<Integer> T U R 타입이 전부 같을때 사용
        //BiFunction<T, U, R>의 특수한 형태로, 동일한 타입의 입력값 두개를 받아 리턴하는 함수 인터페이스

        //  Consumer<Integer> printT = (i) -> System.out.println(i);
        // T타입을 받아서 아무값도 리턴하지 않는 함수 인터페이스
        Consumer<Integer> printT = System.out::println;
        printT.accept(10);

        //Supplier<T>
        // T 타입의 값을 제공하는 함수 인터페이스
        Supplier<Integer> get10 = () -> 10;
        System.out.println("get====>"+10);

        //Predicate<T>
        //T 타입을 받아서 boolean을 리턴하는 함수 인터페이스
        // and, or, Negate 사용 가능
        Predicate<String> startWith = (s) -> s.startsWith("st");
        Predicate<Integer> isEven = (i) -> i%2==0;




        ////2022-04-07

        int baseNumber = 10;
        RunSomething runSomething0 = new RunSomething() {
            int inNumber = 10; //가능하지만 pure하지 못한 함수형 프로그래밍이다. 추가하게되면 Lamda로 줄일 수 가 없다.
            @Override
            public int doIt(int number) {
                inNumber++;
                return number + inNumber + baseNumber; //baseNumber는 final로 가정함으로 사용한다.
            }
        };

        // ^ Lambda
        RunSomething runSomethingLambda = number -> {
            return number + baseNumber; //baseNumber는 final로 가정함으로 사용한다.
        };

        RunSomething runSomethingLambda2 = number ->  number + baseNumber; //return 생략 가능

        //RunSomething runSomething0 = (number) -> {
        //  return number + 10;
        //};

        //System.out.println("Foo.main 1::"+ runSomething0.doIt(1));
        //System.out.println("Foo.main 2::"+ runSomething0.doIt(1));
        //System.out.println("Foo.main 3::"+ runSomething0.doIt(1));

        // Next
        RunSomething runSomething = number -> number + baseNumber;

        // java8 이전사용
        // 익명 내부 클래스 anonymous inner class
//        RunSomething runSomethingAsis = new RunSomething() {
//            @Override
//            public void doItAgain() {
//                System.out.println("doItAgain");
//            }
//        };
//        runSomethingAsis.doItAgain();

        //Lamda 로 변경
        //RunSomething runSomething = () -> System.out.println("doItAgain");
        //runSomething.doItAgain();

    }
}
