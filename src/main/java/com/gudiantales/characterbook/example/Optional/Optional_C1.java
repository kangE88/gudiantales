package com.gudiantales.characterbook.example.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class Optional_C1 {

    public static void main(String[] args) {
        List<OptionalClass> springClasses = new ArrayList<>();
        springClasses.add(new OptionalClass(1, "spring boot", true));
        springClasses.add(new OptionalClass(2, "spring data jpa", true));
        springClasses.add(new OptionalClass(3, "spring mvc", false));
        springClasses.add(new OptionalClass(4, "spring core", false));
        springClasses.add(new OptionalClass(5, "spring rest api development", false));

        OptionalClass spring_boot = new OptionalClass(1, "spring boot", true);
        Optional<Progress> progress = spring_boot.getProgress();

        //값이 없으면 아래와 같이 처리해야 하고, return값이 null인것 자체가 문제다. 애초에 값이 null이면 예외처리를 해야한다.
        //예외처리 throw new IllegalException 사용해서 체크하면 stack메모리사용하기때문에 부하가 간다.
        //if (progress != null){
        //    System.out.println(progress.getStudyDuration());
        //}

        Optional.of(10);
        OptionalInt.of(10);

        System.out.println(progress); //Optional.empty



    }
}
