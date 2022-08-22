package com.gudiantales.characterbook.example.Optional;

import com.gudiantales.characterbook.example.stream.OnlineClass;

import java.util.*;
import java.util.stream.Stream;

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

        Optional<OptionalClass> optionalClassStream = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .findFirst();

        //

        OptionalClass optionalClass = optionalClassStream.get();
        System.out.println("optionalClass = " + optionalClass.getTitle());

        //위와 같이 get을 사용하지않고 사용할수있는 옵션들

        optionalClassStream.ifPresent(oc -> System.out.println("optionalClassStream ifPresent ::" + oc.getTitle()));

        OptionalClass optionalClass1 = optionalClassStream.orElse(createNewClass()); //무조건 실행됨
        OptionalClass optionalClass2 = optionalClassStream.orElseGet(() -> createNewClass()); //optionalClassStream 에 조건에 값이 있으면 실행안함
        optionalClass2 = optionalClassStream.orElseGet(Optional_C1::createNewClass); //위와 같음

        OptionalClass optionalClass3 = optionalClassStream.orElseThrow(); // 값이 없으면 에러던져줌
        System.out.println("optionalClass3 = " + optionalClass3.getTitle());

        optionalClassStream.orElseThrow(() -> {
            return new IllegalArgumentException();
        });

        optionalClassStream.orElseThrow(IllegalStateException::new);
    }

    private static OptionalClass createNewClass() {
        System.out.println("Optional_C1.createNewClass");
        return new OptionalClass(10, "New Class", false );
    }
}
