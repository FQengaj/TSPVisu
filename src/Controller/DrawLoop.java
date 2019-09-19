package Controller;

import Model.City;
import Model.Graph;
import Model.GraphState;
import View.MapPane;
import javafx.animation.AnimationTimer;

import java.util.LinkedList;

public class DrawLoop implements Runnable {

    Graph g;
    private MapPane map;
    private boolean enabled = false;


    public DrawLoop(MapPane map) {
        init(map, map.getGraph());
    }

    private void init(MapPane map, Graph g){
        this.map = new MapPane(g);
        this.g = g;
        //this.start();
    }

    @Override
    public void run() {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 3;
        final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
        long lastFpsTime = 0;

        while(true){
            if(enabled){
                long now = System.nanoTime();
                long updateLength = now - lastLoopTime;
                lastLoopTime = now;
                double delta = updateLength / ((double) OPTIMAL_TIME);
                lastFpsTime += updateLength;
                if ( lastFpsTime >= 1_000_000_000){
                    lastFpsTime = 0;
                }

                aniStep();

                try{
                    long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1_000_000;
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    System.out.println("EEEEXXEEEPPPTTTIOOOONNN!!!!");
                }
            }
        }
    }


    private void aniStep() {
        GraphState currstate = this.g.getNextState();
        if (currstate != null){
            //this.lblProcess.setText(currstate.process+":");
            if(currstate.transition){
                this.map.onDraw(currstate.snapshot);
            }else if (currstate.line){
                LinkedList<City> sel = currstate.selection;
                for (int i = 0; i < sel.size()-1; i++) {
                    this.map.highlightLine(sel.get(i), sel.get(i+1));
                }
            }else{
                this.map.highlightPoint(currstate.selection);
            }
        }
    }

    public void start(){this.enabled = true;}
    public void stop(){this.enabled = false;}
}
