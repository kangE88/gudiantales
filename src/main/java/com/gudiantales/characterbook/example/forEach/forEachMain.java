package com.gudiantales.characterbook.example.forEach;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

public class forEachMain {
    public static void main(String[] args) {

        List<String> name = new ArrayList<>();
        name.add("one");
        name.add("two");
        name.add("three");
        name.add("four");

        //name.forEach(System.out::println); //List name 전체 출력

        Spliterator<String> spliterator = name.spliterator(); // iterator랑 비슷하고, 전체 목록이 출력된다.
        Spliterator<String> stringSpliterator = spliterator.trySplit(); //반으로 쪼갬
        //spliterator.trySplit();
        while (spliterator.tryAdvance(System.out::println));
        while (stringSpliterator.tryAdvance(System.out::println));

    }
}
