package org.lukas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Gui extends JFrame {
    final private List<List<JPanel>> mapRepresentation = new ArrayList<>();

    public Gui(BlockingQueue<List<List<Boolean>>> mapOutput, int xSize, int ySize) {
        setLayout(new GridLayout(ySize, xSize));

        for (int i = 0; i < ySize; i++) {
            List<JPanel> rowRepr = new ArrayList<>();

            for (int j = 0; j < xSize; j++) {
                JPanel panel = new JPanel();
                panel.setPreferredSize(new Dimension(10, 10));
                add(panel);
                rowRepr.add(panel);
            }

            mapRepresentation.add(rowRepr);
        }

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) { }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
                    try {
                        List<List<Boolean>> map = mapOutput.take();
                        SwingUtilities.invokeLater(() -> {
                            updateMap(map);
                        });
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) { }
        });

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

//
//        scheduler.scheduleWithFixedDelay(() -> {
//            try {
//                List<List<Boolean>> map = mapOutput.take();
//                SwingUtilities.invokeLater(() -> {
//                    updateMap(map);
//                });
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }, 5, 100, TimeUnit.MILLISECONDS);

        setSize(1000, 1000);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void updateMap(List<List<Boolean>> map) {
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.getFirst().size(); j++) {
                Color color = map.get(i).get(j) ? Color.WHITE : Color.BLACK;
                mapRepresentation.get(i).get(j).setBackground(color);
            }
        }
    }

}
