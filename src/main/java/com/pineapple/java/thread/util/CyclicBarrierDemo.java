package com.pineapple.java.thread.util;

import java.util.concurrent.BrokenBarrierException;

public class CyclicBarrierDemo {
    final int N;
    final float[][] data;
    final java.util.concurrent.CyclicBarrier barrier;

    class Worker implements Runnable {
        int myRow;

        Worker(int row) {
            myRow = row;
        }

        public void run() {
            while (!done()) {
                processRow(myRow);
                try {
                    barrier.await();
                } catch (InterruptedException ex) {
                    return;
                } catch (BrokenBarrierException ex) {
                    return;
                }
            }
        }

        private boolean done() {
            return false;
        }

        private void processRow(int myRow) {
        }
    }

    public CyclicBarrierDemo(float[][] matrix) {
        data = matrix;
        N = matrix.length;
        barrier = new java.util.concurrent.CyclicBarrier(N, new Runnable() {
            @Override
            public void run() {
                mergeRows();
            }

            private void mergeRows() {
            }
        });
        for (int i = 0; i < N; ++i)
            new Thread(new Worker(i)).start();

        waitUntilDone();
    }

    private void waitUntilDone() {

    }
}