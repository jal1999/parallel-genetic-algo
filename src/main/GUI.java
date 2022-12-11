package main;

import javax.swing.*;
import java.awt.*;

public class GUI {
    JFrame frame;
    JButton[][] grid;
    JLabel label;
    public void paint(Station[][] floor) {
        frame = new JFrame();
        frame.add(label);
        frame.setLayout(new GridLayout(floor.length, floor[0].length));
        grid = new JButton[floor.length][floor[0].length];
        for (int i = 0; i < floor.length; ++i) {
            for (int j = 0; j < floor[0].length; ++j) {
                grid[i][j] = new JButton();
                grid[i][j].setText(String.valueOf(floor[i][j].getType()));
                frame.add(grid[i][j]);
            }
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void repaint(Station[][] floor) {
        for (int i = 0; i < floor.length; ++i) {
            for (int j = 0; j < floor[0].length; ++j) {
                grid[i][j].setText(String.valueOf(floor[i][j].getType()));
            }
        }
    }
}
