package Controller;

import Model.Graph;
import javafx.animation.AnimationTimer;

import java.util.LinkedList;

public class DrawLoop extends AnimationTimer {

    LinkedList<Graph> AnimatedGraph = new LinkedList<>();
    public DrawLoop(Graph g) {
        AnimatedGraph.add(g);
        this.start();
    }

    @Override
    public void handle(long now) {
        if (AnimatedGraph.size()>1){
            // i did stuff i swear
        }
    }
}
