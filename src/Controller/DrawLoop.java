package Controller;

import Model.Graph;
import Model.GraphState;
import View.MapPane;
import javafx.animation.AnimationTimer;

import java.util.LinkedList;

public class DrawLoop extends AnimationTimer {

    Graph g;
    private MapPane map;


    public DrawLoop(MapPane map) {
        init(map, map.getGraph());
    }

    private void init(MapPane map, Graph g){
        this.map = new MapPane(g);
        this.g = g;
        //this.start();
    }

    @Override
    public void handle(long now) {
        if (g != null){
            GraphState currstate = g.getNextState();
            if (currstate != null){
                this.map.highlightPoint(currstate.selection);
                //System.out.println("yup did some shit man :) ");
            }
        }
    }
}
