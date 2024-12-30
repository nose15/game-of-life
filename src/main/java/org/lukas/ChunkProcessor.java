package org.lukas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ChunkProcessor implements Runnable {
    private static int count = 0;

    private final int id;
    private final int start;
    private final int size;
    private CountDownLatch latch;
    private List<List<Boolean>> map;
    private List<List<Boolean>> output;

    public ChunkProcessor(List<List<Boolean>> map, int start, int size) {
        this.map = map;
        this.start = start;
        this.size = size;
        this.id = count;

        count++;

        System.out.println("Chunk processor from " + start + " to " + (start + size - 1));
    }

    @Override
    public void run() {
        try {
            output = new ArrayList<>();

            for (int y = start; y < start + size; y++) {
                List<Boolean> row = map.get(y);
                List<Boolean> newRow = new ArrayList<>();

                for (int x = 0; x < row.size(); x++) {
                    newRow.add(canLive(map, x, y));
                }

                output.add(newRow);
            }

            latch.countDown();
        } catch (RuntimeException e) {
            System.out.println("Thread " + id + " threw an exception " + e);
            e.printStackTrace();
        }
    }

    public void setMap(List<List<Boolean>> map) {
        this.map = map;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public List<List<Boolean>> getOutput() {
        return this.output;
    }

    private static boolean canLive(List<List<Boolean>> map, int i, int j) {
        int livingNeighbors = 0;
        for (int m = -1; m <= 1; m++) {

            int y = (j + m + map.size()) % map.size();
//            Cannot be y = (j + m) % map.size()
//            because it returns a negative remainder

            for (int n = -1; n <= 1; n++) {
                if (n == 0 && m == 0) {
                    continue;
                }

                int x = (i + n + map.getFirst().size()) % map.getFirst().size();

                if (map.get(y).get(x)) {
                    livingNeighbors += 1;
                }
            }
        }

        boolean val = map.get(j).get(i);
        return ((val && (livingNeighbors == 3 || livingNeighbors == 2)) || (!val && livingNeighbors == 3));
    }
}
