package main;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GUI {
    public static class Pair {
        JButton b;
        JFrame f;

        public Pair(JButton j, JFrame k) {
            b = j;
            f = k;
        }
    }
    public static Pair paint(Station[][] floor, double score) {
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        JButton button1 = new JButton("<html><h1>Score: " + score + "</h1>" + "<h1>" + matrixToString(floor) +"</h1>" + "</html>");
        frame.getContentPane().add(button1);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        return new Pair(button1, frame);
    }

    public static void repaint(Station[][] floor, Pair p, double score) {
        JFrame frame = p.f;
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        p.b.setText("<html><h1>Score: " + score + "</h1>" + "<h1>" + matrixToString(floor) + "</h1>" + "</html>");
        p.f.setVisible(true);
    }

    public static void finalPaint(Station[][] floor, Pair p, double score) {
        JFrame frame = p.f;
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        p.b.setText("<html><h1>Best configuration w/ score of " + score + "</h1>" + "<h1>" + matrixToString(floor) +"</h1>" + "</html>");
        p.f.setVisible(true);
    }

    private static String matrixToString(Station[][] floor) {
        String s = "";
        String newLine = System.getProperty("line.separator");


        for (Station[] arr : floor) {
            List<Integer> newArr = Arrays.stream(arr).map(Station::getType).collect(Collectors.toList());
            s += newArr + "<br/>";
        }

        return "<br/>" + newLine + s + newLine + "<br/>";
    }
}
