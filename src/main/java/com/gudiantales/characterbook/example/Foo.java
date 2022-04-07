package com.gudiantales.characterbook.example;

public class Foo {

    public static void main(String[] args) {
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

        // java 8 이전
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
