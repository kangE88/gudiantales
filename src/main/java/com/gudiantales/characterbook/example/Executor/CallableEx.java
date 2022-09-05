package com.gudiantales.characterbook.example.Executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class CallableEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor(); //싱글 스레드 생성 Executors

        //Callable
        Callable<String> callable = () -> {
            Thread.sleep(5000);
            return "result";
        };

        //isDone(), 작업의 상태 확인하기
        //cancel(), 작업 취소하기
        //invokeAll(), 여러 작업 동시에 실행하기
        //invokeAny(), 여러 작업 중에 하나라도 먼저 응답이 오면 끝내기

        // isDone(), cancel()
        Future<String> exFuture = executorService.submit(callable);
        System.out.println("exFuture = " + exFuture.isDone());
        System.out.println("Started!");

        //cancel 메서드에 true를 넘겨주면 현재 진행 중인 작업을 인터럽트 하면서 종료한다. 반면 false를 넘겨주면 작업을 기다린다.
        //exFuture.cancel(false);
        exFuture.get();

        System.out.println("exFuture = " + exFuture.isDone());
        System.out.println("End");

        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        Callable<String> thread1 = () -> {
            Thread.sleep(2000);
            return "thread1";
        };
        Callable<String> thread2 = () -> {
            Thread.sleep(4000);
            return "thread2";
        };
        Callable<String> thread3 = () -> {
            Thread.sleep(6000);
            return "thread3";
        };

        /*
            invokeAll, invokeAny
         */

        //invokeAll을 사용하면 모든 작업이 끝날 때까지 먼저 끝난 작업이 기다린다
        List<Future<String>> futures = threadPool.invokeAll(Arrays.asList(thread1, thread2, thread3));

        for (Future<String> future : futures) {
            System.out.println("future = " + future.get());
        }

        String futuresAny = threadPool.invokeAny(Arrays.asList(thread1, thread2, thread3));

        System.out.println("futuresAny = " + futuresAny);


        executorService.shutdown();
    }

    private static  Runnable getRunnable(String message) {
        return () -> System.out.println(message + " : " + Thread.currentThread().getName());
    }
}
