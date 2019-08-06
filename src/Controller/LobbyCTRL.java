package Controller;

import Controller.Utils.Util;
import Model.City;
import Model.Graph;
import View.MapPane;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.Random;

public class LobbyCTRL {
    /*
    * todo
    *  1. Git upload
    * */


    @FXML private BorderPane BaseLayout;
    @FXML private ScrollPane topPane;
    @FXML private TextField txt;
    @FXML private Label lbl;
    @FXML private Label lbllw; // line width info
    @FXML private Label lblTC;
    @FXML private Button btnar; // add random point
    @FXML private Button btnCls; // clear graph
    @FXML private Button btnhl; // hide lines
    @FXML private Button btnadd; // add number of elements
    @FXML private Slider slider;

    private Random r = new Random();
    private int pointCounter = 0;
    private MapPane map;
    private Graph graph;


    @FXML
    public void initialize(){
        graph = new Graph();
        this.map = new MapPane(this.graph);
        Pane p = new Pane();
        p.getChildren().add(map);

        ChangeListener<Number> onResizeWidth = (observable, oldValue, newValue)-> {
            map.setWidth(newValue.doubleValue());
            map.onDraw();
            updateTotalCost();
        };
        ChangeListener<Number> onResizeHeight = (observable, oldValue, newValue)-> {
            map.setHeight(newValue.doubleValue());
            map.onDraw();
            updateTotalCost();
        };
        map.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();

            x -= (map.OVALDIAMETER/2);
            y -= (map.OVALDIAMETER/2);

            x /= map.getWidth();
            y /= map.getHeight();


            addPoint(x,y);

            map.onDraw();
            updateTotalCost();
            lbl.setText(graph.getSize()+"");
        });
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            map.lineWith = newValue.doubleValue();
            lbllw.setText(String.format("Line Width: %.2f", map.lineWith));
            map.onDraw();
            updateTotalCost();
        });
        txt.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.ENTER){
                addPoints();
            }
        });

        p.heightProperty().addListener(onResizeHeight);
        p.widthProperty().addListener(onResizeWidth);

        topPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        BaseLayout.setCenter(p);
    }

    @FXML
    void addRandomPoint(ActionEvent event) {
        addRandomPoint();

        map.onDraw();
        updateTotalCost();
        lbl.setText(graph.getSize()+"");
    }

    private void addRandomPoint() {
        double x = r.nextDouble();
        double y = r.nextDouble();

        addPoint(x,y);
    }

    private void addPoint(double x, double y){
        graph.addVertex(new City(x,y));
    }


    @FXML
    void clearList() {
        graph.clear();
        lbl.setText("0");
        map.onDraw();
        updateTotalCost();
    }

    @FXML
    void toogleLines(){
        map.toogleLines();
        if (map.drawLines){
            btnhl.setText("Hide Lines");
        }else{
            btnhl.setText("Show Lines");
        }
        map.onDraw();
        updateTotalCost();
    }

    @FXML
    private void primsMST(){
        this.graph.setAdjazenzmatrix(Util.PrimsMST(graph));
        this.graph.isDirected = false;
        map.onDraw();
        updateTotalCost();
        Util.printGraph(graph);
    }

    @FXML
    private void eulerCirc(){
        if(!graph.isDirected){
            this.graph.setAdjazenzmatrix(Util.EulerCirc(graph));
            map.onDraw();
            updateTotalCost();
            Util.printGraph(graph);
        }

    }

    @FXML
    private void addPoints() {
        int pointstoadd;
        try{
            pointstoadd = Integer.parseInt(txt.getText());
        }catch (Exception e){
            txt.setText("0");
            return;
        }
        if (pointstoadd == 0){
            return;
        }

        for (int i = 0; i < pointstoadd; i++){
            addRandomPoint();
        }

        map.onDraw();
        updateTotalCost();
        lbl.setText(graph.getSize()+"");
    }

    @FXML
    private void randomPath(){
        graph.setAdjazenzmatrix(Util.RandomPath(graph, r));
        map.onDraw();
        updateTotalCost();
    }

    private void updateTotalCost() {
        double TotalCost = Util.PathCostOf(this.graph);
        String TC = String.format("%.4f", TotalCost);
        this.lblTC.setText(TC);
    }

}
