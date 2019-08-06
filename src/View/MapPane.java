package View;

import Model.City;
import Model.Graph;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.LinkedList;

public class MapPane extends Canvas {

    public final double OVALDIAMETER = 10.0;
    public double lineWith = 0.8;


    private Graph graph;
    public Boolean drawLines = true;

    public MapPane(Graph graph){
        this.graph = graph;
    }

    // make it so that only new Linkes
    public void onDraw(){

        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,this.getWidth(), this.getHeight());

        gc.setStroke(Color.GRAY);
        gc.setFill(Color.GRAY);

        if (graph.getSize() == 0) return;


        gc.setFill(Color.RED);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);

        double W = this.getWidth();
        double H  = this.getHeight();
        if (drawLines){
            gc.setLineWidth(lineWith);

            LinkedList<LinkedList<Double>> adj = graph.getAdjazenzmatrix();
            if(graph.isDirected){
                for (int i = 0; i < graph.getSize(); i++){
                    City c1 = graph.getVertices().get(i);
                    double c1x = c1.x*W;
                    double c1y = c1.y*H;
                    for (int j = i+1; j < graph.getSize(); j++){

                        City c2 = graph.getVertices().get(j);
                        double c2x = c2.x*W;
                        double c2y = c2.y*H;

                        if (adj.get(i).get(j) != 0) {
                            gc.strokeLine(c1x + (OVALDIAMETER / 2), c1y + (OVALDIAMETER / 2), c2x + (OVALDIAMETER / 2), c2y + (OVALDIAMETER / 2));
                        }
                    }
                }
            }else{
                for (int i = 0; i < graph.getSize(); i++){
                    City c1 = graph.getVertices().get(i);
                    double c1x = c1.x*W;
                    double c1y = c1.y*H;
                    for (int j = 0; j < graph.getSize(); j++){

                        City c2 = graph.getVertices().get(j);
                        double c2x = c2.x*W;
                        double c2y = c2.y*H;

                        if (i == j || adj.get(i).get(j) == 0) {
                            continue;
                        }
                        gc.strokeLine(c1x+(OVALDIAMETER/2), c1y+(OVALDIAMETER/2), c2x+(OVALDIAMETER/2),c2y+(OVALDIAMETER/2));
                    }
                }
            }
        }

        for (City point : graph.getVertices()) {
            gc.fillOval((point.x*W), (point.y*H), OVALDIAMETER, OVALDIAMETER);
        }



    }

    public void toogleLines(){
        drawLines = !drawLines;
        onDraw();
    }

    public double getCostofCurrPath() {
        double result = 0;
        LinkedList<LinkedList<Double>> adj = graph.getAdjazenzmatrix();
        for (int i = 0; i < graph.getSize();i++){
            for (int j = i+1; j < graph.getSize(); j++){
                result += adj.get(i).get(j);
            }
        }

        return result;
    }
}
