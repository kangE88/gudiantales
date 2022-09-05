package com.gudiantales.characterbook.example.Executor;

import java.util.concurrent.*;

public class App {
    public static void main(String[] args) {
        /**
         * Executors, 고수준(High-Level) Concurrency 프로그래밍
         * 쓰레드를 만들고 관리하는 작업을 어플리케이션에서 분리한다.
         * Executors가 쓰레드를 만들고 개발자는 Runnable에 해야 할 일을 정의해서 넘겨준다.
         * Executors가 하는 일
         * 쓰레드 만들기 : 어플리케이션이 사용할 쓰레드 풀을 만들어 관리한다.
         * 쓰레드 관리 : 쓰레드 생명 주기를 관리한다.
         * 작업 처리 및 실행 : 쓰레드로 실행할 작업을 제공할 수 있는 API를 제공한다.
         * 주요 인터페이스
         * Executor : execute(Runnable)
         * ExecutorService : Executor를 상속받은 인터페이스로, Callable도 실행할 수 있으며, Executor를 종료시키거나, 여러 Callable을 동시에 실행하는 등의 기능을 제공한다.
         * ScheduledExecutorService : ExecutorService를 상속받은 인터페이스로 특정 시간 이후에 또는 주기적으로 작업을 실행할 수 있다.
         */
        ExecutorService executorService = Executors.newSingleThreadExecutor(); //싱글 스레드 생성 Executors
        ExecutorService executorService2 = Executors.newFixedThreadPool(2); // 쓰레드를 n개 사용하는 Executors
        executorService.submit(() -> {
            System.out.println("Thread " + Thread.currentThread().getName());
        });

        /**
         * newFixedThreadPool example
         */
        executorService2.execute(getRunnable("test1"));
        executorService2.execute(getRunnable("test2"));
        executorService2.execute(getRunnable("test3"));
        executorService2.execute(getRunnable("test4"));
        executorService2.execute(getRunnable("test5"));

        //실행 후 종료
        executorService.shutdown();
        // 강제종료
        //executorService.shutdownNow();

        /**
         * Executors.newSingleThreadScheduledExecutor 특정 시간 이후 혹은 주기적으로 작업을 처리
         */
        //ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //scheduledExecutorService.schedule(getRunnable("test"), 3, TimeUnit.SECONDS); // 3초 뒤에 스레드를 실행
        //scheduledExecutorService.shutdown();
        //scheduledExecutorService.scheduleAtFixedRate(getRunnable("test"), 1, 2, TimeUnit.SECONDS); // 초기 딜레이는 1초, 주기는 3초마다 작업을 실행

    }

    private static  Runnable getRunnable(String message) {
        return () -> System.out.println(message + " : " + Thread.currentThread().getName());
    }
}
