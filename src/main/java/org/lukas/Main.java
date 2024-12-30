package org.lukas;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int iterations = 1000;

        List<List<Boolean>> map = List.of(
                List.of(false, false, false, false, true),
                List.of(false, false, false, false, false),
                List.of(false, false, true, false, false),
                List.of(false, true, true, false, false),
                List.of(false, true, false,  true, true)
        );

        List<List<Boolean>> nextGen;

        for (int u = 0; u < iterations; u++) {
            nextGen = new ArrayList<>();

            for (int y = 0; y < map.size(); y++) {
                List<Boolean> row = map.get(y);
                nextGen.add(new ArrayList<>(row.size()));


                for (int x = 0; x < row.size(); x++) {
                    nextGen.get(y).add(canLive(map, x, y));
                }
            }

            map = nextGen;
            System.out.println(map);
        }
    }

    private static boolean canLive(List<List<Boolean>> map, int i, int j) {
        int livingNeighbors = 0;

//        System.out.print("Checking (" + i + ", " + j + "): ");

        for (int m = -1; m <= 1; m++) {

            int y = (j + m + map.size()) % map.size();
//            Cannot be y = (j + m) % map.size()
//            because it returns a negative remainder

            for (int n = -1; n <= 1; n++) {
                if (n == 0 && m == 0) {
                    continue;
                }

                int x = i + n;

                if (x < 0 || x > map.getFirst().size() - 1) {
                    continue;
                }

//                System.out.print("(" + x + ", " + y + ")");

                if (map.get(y).get(x)) {
                    livingNeighbors += 1;
                }
            }
        }

        return livingNeighbors == 3 || livingNeighbors == 2;
    }
}