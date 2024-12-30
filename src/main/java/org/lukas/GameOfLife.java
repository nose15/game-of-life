package org.lukas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class GameOfLife implements Runnable {
    private final BlockingQueue<List<List<Boolean>>> mapOutput;

    private List<List<Boolean>> map;
    private List<ChunkProcessor> processors;
    private ExecutorService executorService;


    public GameOfLife(BlockingQueue<List<List<Boolean>>> mapOutput, List<List<Boolean>> map) throws InterruptedException {
        this.mapOutput = mapOutput;
        this.map = map;

        processors = createChunkProcessors();
        executorService = Executors.newFixedThreadPool(processors.size());

        mapOutput.put(map);
    }

    @Override
    public void run() {
        map = resolveNextGen(processors, executorService);

        try {
            mapOutput.put(map);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<List<Boolean>> resolveNextGen(List<ChunkProcessor> processors, ExecutorService executorService) {
        List<List<Boolean>> nextGen = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(processors.size());

        for (var processor : processors) {
            processor.setLatch(latch);
            processor.setMap(map);
            executorService.submit(processor);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (var processor : processors) {
            nextGen.addAll(processor.getOutput());
        }

        return nextGen;
    }

    private List<ChunkProcessor> createChunkProcessors() {
        int availableThreads = Runtime.getRuntime().availableProcessors() - Thread.activeCount();
        int rowsLeft = map.size();
        List<ChunkProcessor> processors = new ArrayList<>();
        int threadSize;

        if (availableThreads >= rowsLeft) {
            while (rowsLeft > 0) {
                processors.add(new ChunkProcessor(map, rowsLeft - 1, 1));
                rowsLeft--;
            }
            return processors;
        }

        for (int threadsLeft = availableThreads; threadsLeft > 0; threadsLeft--) {
            threadSize = (int)Math.ceil((float) rowsLeft / threadsLeft);
            processors.add(new ChunkProcessor(map, rowsLeft - threadSize, threadSize));
            rowsLeft -= threadSize;
        }

        Collections.reverse(processors);
        return processors;
    }
}
