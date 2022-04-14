package com.gudiantales.characterbook.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class GreetringMain {
    public static void main(String[] args) {
        Function<Integer, String> intToString = (i) -> "number:"+ i;
        System.out.println("intToString = " + intToString.apply(2));

        //스태틱 메소드 참조 - 타입::static 메소드
        UnaryOperator<String> hi = Greeting::hi;
        System.out.println(" -> " + hi.apply("kang"));

        //특정 객체의 인스턴스 메소드 참조 - 타입::인스턴스 메소드 사용
        //기존
        Greeting greeting1 = new Greeting();
        String result = greeting1.hello("kange");
        System.out.println("asis result = " + result);

        //자바8
        Greeting greeting = new Greeting();
        UnaryOperator<String> hello = greeting::hello;
        System.out.println(" -> " + hello.apply("kangE"));

        //생성자 참조 - 타입::new
        Supplier<Greeting> supplier = Greeting::new;
        supplier.get(); // 이 시점에 Greeting 생성
        String supResult = supplier.get().hello("keke");
        System.out.println("supResult = " + supResult);

        Function<String, Greeting> example = Greeting::new;
        Greeting resultExam = example.apply("example");
        System.out.println("resultExam = " + resultExam.getName());

        Supplier<Greeting> exampleGreeting = Greeting::new;
        String helloGreeting = exampleGreeting.get().hello("helloGreeting");
        System.out.println("helloGreeting = " + helloGreeting);

        //임의 객체의 인스턴스 메소드 참조
        String[] names = {"kang","Aee","B"};
//        Arrays.sort(names, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return 0;
//                //음수 리턴  or 양수 리턴
//            }
//        });
        Arrays.sort(names, String::compareToIgnoreCase);
        System.out.println("-->"+Arrays.toString(names));


    }
}
