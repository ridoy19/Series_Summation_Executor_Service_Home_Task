package com.company;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class SeriesSummation implements Callable<BigInteger> {
    private BigInteger m;
    private BigInteger d;
    private BigInteger startPoint;
    private BigInteger endPoint;


    public SeriesSummation(BigInteger startPoint, BigInteger endPoint, BigInteger m, BigInteger d) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.m = m;
        this.d = d;
    }

    @Override
    public BigInteger call() throws Exception {
        BigInteger result = BigInteger.ZERO;
        for (BigInteger i = startPoint; i.compareTo(endPoint) <= 0; i = i.add(BigInteger.ONE)) {
            result = result.add((m.add((i.multiply(d)))));
        }
        return result;
    }
}


public class Main {

    // Parallel Way
    private static BigInteger parallelSeriesSum(BigInteger n, BigInteger m, BigInteger d) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        BigInteger nValue = n.divide(BigInteger.valueOf(availableProcessors));
        BigInteger result = BigInteger.ZERO;
        BigInteger startPoint = BigInteger.ZERO;


        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
        List<Future<BigInteger>> futureList = new ArrayList<>();

        Future<BigInteger> bigIntegerFuture = null;


        for (int i = 0; i < availableProcessors; i++) {
            if (i != availableProcessors - 1) {
                bigIntegerFuture = fixedThreadPool.submit(new SeriesSummation(startPoint, startPoint.add(nValue.subtract(BigInteger.ONE)), m, d));
            } else bigIntegerFuture = fixedThreadPool.submit(new SeriesSummation(startPoint, n, m, d));
            futureList.add(bigIntegerFuture);
            startPoint = startPoint.add(nValue);
        }

        fixedThreadPool.shutdown();
        fixedThreadPool.shutdownNow();

        for (Future<BigInteger> values : futureList) {
            try {
                result = result.add(values.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // n = 3,m = 5, d = 3
    // m + (m + d) + (m + 2d) + (m + 3d) + ... + (m + nd)
    // => 5 + (5 + 3) + (5 + (2 * 3))
    // => 5 + 8 + 11
    // => 24

    // BigInteger Way
    private static BigInteger calculateSeries(BigInteger n, BigInteger m, BigInteger d) {
        BigInteger result = BigInteger.ZERO;
        for (BigInteger i = BigInteger.ZERO; i.compareTo(n) <= 0; i = i.add(BigInteger.ONE)) {
            result = result.add((m.add((i.multiply(d)))));
            //System.out.println(i+"\n");
            //System.out.println(result+"\n");
        }

        return result;

    }

    // Long Way
    private static long sum(long n, long m, long d) {
        long result = 0;
        for (long i = 0; i <= n; i++) {
            result += m + (i * d);
        }
        return result;
    }

    public static void main(String[] args) {
        BigInteger n = BigInteger.valueOf(1000000000);
        BigInteger m = BigInteger.valueOf(999);
        BigInteger d = BigInteger.valueOf(99);

        // write your code here

        // Long Way
        /*long sumResult = sum(3,5,3);
        System.out.println(sumResult);*/

        // Sequential BigInteger Way
        long startTime = System.currentTimeMillis();
        System.out.println(calculateSeries(n, m, d));
        long endTime = System.currentTimeMillis();

        System.out.println("Time Taken : " + (endTime - startTime) / 1000 + " sec");


        // Parallel BigInteger Way
        startTime = System.currentTimeMillis();
        System.out.println(parallelSeriesSum(n, m, d));
        endTime = System.currentTimeMillis();
        System.out.println("Time Taken : " + (endTime - startTime) / 1000 + " sec");


        System.out.println("Done with everything :)");


    }
}
