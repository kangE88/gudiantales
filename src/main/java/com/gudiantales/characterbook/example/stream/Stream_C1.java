package com.gudiantales.characterbook.example.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stream_C1 {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();

        names.add("one");
        names.add("two");
        names.add("three");
        names.add("four");

        names.forEach(System.out::println);

        System.out.println("\n------------------------------------\n");

        //스트림을 사용하면 위의 names에 영향이 가지 않는다. temp에 담는 형식
        //중계형 오퍼레이터는 반드시 종료형 오퍼레이터가 존재해야한다.
        List<String> stringStream = names.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        System.out.println("stringStream::"+ stringStream);
        //stringStream::[ONE, TWO, THREE, FOUR]

        System.out.println("\n----------------ASIS-----------------\n");

        //스트림으로 병렬처리
        for (String name : names){
            if (name.startsWith("t")){
                System.out.println("name:"+name.toUpperCase());
            }
        }
        //iteration 작업을 병렬적으로 처리하기 어렵다.

        System.out.println("\n----------------Stream---------------\n");
        //Stream은 parallelStream를 통해서 병렬처리가 가능하다.

        List<String> collect = names.parallelStream().map(String::toUpperCase).collect(Collectors.toList());
        collect.forEach(System.out::println);


    }
}
