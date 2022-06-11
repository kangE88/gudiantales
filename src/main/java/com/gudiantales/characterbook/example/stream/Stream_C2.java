package com.gudiantales.characterbook.example.stream;

import java.util.ArrayList;
import java.util.List;

public class Stream_C2 {

    public static void main(String[] args) {
        List<OnlineClass> springClasses = new ArrayList<>();

        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring data jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));

        List<OnlineClass> javaClasses = new ArrayList<>();
        javaClasses.add(new OnlineClass(6, "The Java, Test", true));
        javaClasses.add(new OnlineClass(7, "The Java, Code manipulation", true));
        javaClasses.add(new OnlineClass(8, "The Java, 8 to 11", true));

        List<List<OnlineClass>> events = new ArrayList<>();
        events.add(springClasses);
        events.add(javaClasses);

        events.stream().parallel();

        System.out.println("Spring 으로 시작하는 수업");
        //TODO

        System.out.println("close 되지 않은 수업");
        //TODO

        System.out.println("수업 이름만 모아서 스트림 만들기");
        //TODO

        System.out.println("두 수업 목록에 들어이쓴ㄴ 모든 수업 아이디 출력");
        //TODO

        System.out.println("10부터 1씩 증가하는 무제한 스트림 중에서 앞에 10개 빼고 최대 10개 까지만");
        //TODO

        System.out.println("자바 수업 중에 Test가 들어있는 수업이 있는지 확인");
        //TODO



    }
}
