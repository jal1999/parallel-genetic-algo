package main;

public class Main {
    public static void main(String[] args) {
      GeneticAlgorithm ga = new GeneticAlgorithm();
      ga.runIteration();
      GUI gui = new GUI();
      gui.paint(ga.getPop().getBestFloorPlan());
      for (int i = 0; i < 1000; ++i) {
          ga.runIteration();
          gui.repaint(ga.getPop().getBestFloorPlan());
      }
    }
}
