package com.gudiantales.characterbook.example.Executor;

import org.springframework.data.spel.spi.Function;

import java.util.concurrent.*;

public class CompletableFutureEx {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //Executor future
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        Future<String> future = executorService.submit(() -> "hello");
//
//        String s = future.get();
//        s = s.toUpperCase();
//        System.out.println("s = " + s);
//
//        //ComplitableFuture
//        /**
//         * CompletableFuture는 Future + CompletionStage
//         *
//         * Completable이란 이름이 붙은 이유는 외부에서 Complete을 시킬 수 있기 때문이다. 가령 몇 초 이내에 응답이 안 온다면 기본 값을 반환하도록 코딩할 수 있다.
//         *
//         * 또한 CompletableFuture를 사용하면 더 이상 명시적으로 Executor를 만들어서 사용할 필요가 없다. CompletableFuture만을 가지고 비동기 작업을 실행할 수 있다.
//         */
//        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("CompletableFutureEx.main" + Thread.currentThread().getName());
//            return "completableFuture return";
//        });
//
//        completableFuture.get();
        /**
            return
            s = HELLO
            CompletableFutureEx.mainForkJoinPool.commonPool-worker-1
         */

        /**
         * Future에서 반환하는 결괏값을 가지고 어떤 작업을 수행해야 한다면 그 작업은 get 이후에 작성돼야 한다.반면 CompletableFuture는 작업이 완료되었을 때 Callback을 호출할 수 있다.
         *
         * Callback 종류
         * thenApply(function)  -> 작업의 반환 값을 받고 어떤 값을 반환하는 콜백
         * thenAccept(Consumer) -> 작업의 반환 값을 받아 어떤 로직을 처리하는 콜백
         * thenRun(Runnable)    -> 작업의 반환 값도 필요 없고 반환도 하지 않는 콜백
         */

        /*
            Future만 사용했을 땐 Callback(thenApply)를 get 호출 전에 정의하는 것이 불가능했다면 CompletableFuture를 사용하면 get 호출 전에 Callback을 정의하는 것이 가능
         */
        CompletableFuture<String> thenApply_example = CompletableFuture.supplyAsync(() -> {
            System.out.println("thenApply Example");
            return "CompletableFuture thenApply Example";
        }).thenApply(result -> {
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            return result.toUpperCase();
        });

        System.out.println(thenApply_example.get());

        /*
            // return
            thenApply Example
            Thread.currentThread().getName() = main
            COMPLETABLEFUTURE THENAPPLY EXAMPLE
         */

        CompletableFuture<Void> thenAccept_example = CompletableFuture.supplyAsync(() -> {
            System.out.println("thenAccept Example");
            return "thenAccept Ex";
        }).thenAccept(s -> {
            System.out.println(Thread.currentThread().getName());
        });

        thenAccept_example.get();

        /*
            // return
            thenAccept Example
            main
         */

        CompletableFuture<Void> thenRun_example = CompletableFuture.supplyAsync(() -> {
            System.out.println("thenRun Example");
            return "thenRun Ex";
        }).thenRun(() -> {
            System.out.println(Thread.currentThread().getName());
        });

        thenRun_example.get();

        /*
            //return
            thenRun Example
            main
         */

        /**
         * thenApply, thenApply, thenRun와 같은 콜백 메서드들은 콜백을 실행한 쓰레드나 그 쓰레드를 파생시킨 부모 쓰레드에서 실행하게 되어 있다.
         */

        /**
         * Thread Pool
         */
        ExecutorService threadPool_executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            return "Thread One";
        }, threadPool_executorService);

        CompletableFuture<String> stringCompletableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            return "Thread Two";
        }, threadPool_executorService);

        System.out.println(stringCompletableFuture.get());
        System.out.println(stringCompletableFuture2.get());

        /*
            //return
            pool-1-thread-1
            Thread One
            pool-1-thread-2
            Thread Two
         */


    }
}
