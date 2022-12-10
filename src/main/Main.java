package main;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
      GeneticAlgorithm ga = new GeneticAlgorithm();
      ga.runIteration();
      GUI.Pair p = GUI.paint(ga.getPop().getBestFloorPlan(), ga.getPop().getBestScore());
      for (int i = 0; i < 1000; ++i) {
          ga.runIteration();
          GUI.repaint(ga.getPop().getBestFloorPlan(), p, ga.getPop().getBestScore());
      }
      GUI.finalPaint(ga.getPop().getBestFloorPlan(), p, ga.getPop().getBestScore());
    }
}
