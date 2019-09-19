package Controller;

import Controller.Utils.Util;
import Model.City;
import Model.Graph;
import Model.GraphState;
import View.MapPane;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.Random;

public class LobbyCTRL {
    @FXML private BorderPane BaseLayout;
    @FXML private ScrollPane topPane;
    @FXML private TextField txt;
    @FXML private Label lbl;
    @FXML private Label lbllw; // line width info
    @FXML private Label lblTC;
    @FXML private Label lblProcess; // aktueller Prozess in der Navigation
    @FXML private Button btnar; // add random point
    @FXML private Button btnCls; // clear graph
    @FXML private Button btnhl; // hide lines
    @FXML private Button btnadd; // add number of elements
    @FXML private Slider slider;

    private Random r = new Random();
    private MapPane map;
    private Graph graph;
    private DrawLoop drawloop;


    @FXML
    public void initialize(){
        graph = new Graph();
        this.map = new MapPane(this.graph);
        //drawloop = new DrawLoop(this.map);
        //new Thread(drawloop).start();

        Pane p = new Pane();
        p.getChildren().add(map);

        initListeners(p);

        BaseLayout.setCenter(p);
    }
    private void initListeners(Pane p) {
        ChangeListener<Number> onResizeWidth = (observable, oldValue, newValue)-> {
            map.setWidth(newValue.doubleValue());
            RedrawMap();
            updateTotalCost();
        };
        ChangeListener<Number> onResizeHeight = (observable, oldValue, newValue)-> {
            map.setHeight(newValue.doubleValue());
            RedrawMap();
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

            map.lagacyOnDraw();
            updateTotalCost();
            lbl.setText(graph.getSize()+"");
        });
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            map.lineWith = newValue.doubleValue();
            lbllw.setText(String.format("Line Width: %.2f", map.lineWith));
            map.lagacyOnDraw();
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
    }


    @FXML void addRandomPoint(ActionEvent event) {
        addRandomPoint();

        map.lagacyOnDraw();
        updateTotalCost();
        lbl.setText(graph.getSize()+"");
    }
    private void addRandomPoint() {
        double x = r.nextDouble();
        double y = r.nextDouble();
        //System.out.printf("%d: [%.4f/%.4f]\n", (graph.getSize()+1),x,y);
        addPoint(x,y);

    }
    private void addPoint(double x, double y){
        graph.addVertex(new City(x,y));
        //graph.addstate(new GraphState(graph));
    }
    @FXML private void addPoints() {
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

        map.lagacyOnDraw();
        updateTotalCost();
        lbl.setText(graph.getSize()+"");
    }

    @FXML void clearList() {
        graph.clear();
        lbl.setText("0");
        //map.lagacyOnDraw();
        RedrawMap();
        updateTotalCost();
        lblProcess.setText("Cleared:");
    }
    @FXML void toogleLines(){
        map.toogleLines();
        if (map.drawLines){
            btnhl.setText("Hide Lines");
        }else{
            btnhl.setText("Show Lines");
        }
        //map.lagacyOnDraw();
        RedrawMap();
        updateTotalCost();
    }
    private void updateTotalCost() {
        double TotalCost = Util.PathCostOf(this.graph);
        String TC = String.format("%.4f", TotalCost);
        this.lblTC.setText(TC);
    }

    @FXML private void primsMST(){
        addSnapshot("Starting: Prims Minimum Spanning Tree");
        this.graph.setAdjazenzmatrix(Util.PrimsMST(graph));
        this.graph.isDirected = false;
        updateTotalCost();
        //map.lagacyOnDraw();
        //Util.printGraph(graph);
    }
    @FXML private void perfectMatch(){
        addSnapshot("Perfect Matching");
        this.graph.setAdjazenzmatrix(Util.perfectMatch(graph));
        this.graph.isDirected = false;
        updateTotalCost();
        //map.lagacyOnDraw();
        //Util.printGraph(graph);
    }
    @FXML private void eulerCirc(){
        if(!graph.isDirected){
            addSnapshot("Starting EulerCirc");
            this.graph.setAdjazenzmatrix(Util.EulerCirc(graph));
            updateTotalCost();
            addSnapshot("finished EulerCirc :) ");

            //map.onDraw();
            //Util.printGraph(graph);
            //drawloop.start();
        }

    }
    @FXML public void TreeTSP(){
        primsMST();
        eulerCirc();
    }

    @FXML public void aniSkip(){
        GraphState currstate = this.graph.getNextState();
        if (currstate == null) return;
        while(!currstate.transition) {
            currstate = this.graph.getNextState();
        }

        this.map.onDraw(currstate.snapshot);
        this.lblProcess.setText(currstate.process);
    }
    @FXML public void aniStep() {
        DrawStep(graph.getNextState());
    }
    @FXML public void aniprev() {
        graph.statePointer--;
        RedrawMap();
    }
    @FXML public void anirewind() {
        GraphState currstate = graph.getPrevState();
        if (currstate == null) return;
        while(!currstate.transition) {
            currstate = graph.getPrevState();
        }

        this.map.onDraw(currstate.snapshot);
        this.lblProcess.setText(currstate.process);
    }

    private void addSnapshot(String process){
        this.graph.addstate(new GraphState(new Graph(graph.getVertices(), graph.getAdjazenzmatrix()), process));
    }
    private void DrawStep(GraphState curState){
        if (curState != null){
            if(curState.transition){
                this.map.onDraw(curState.snapshot);
            }else if (curState.line){
                LinkedList<City> sel = curState.selection;
                for (int i = 0; i < sel.size()-1; i++) {
                    this.map.highlightLine(sel.get(i), sel.get(i+1));
                }
            }else{
                this.map.highlightPoint(curState.selection);
            }
            this.lblProcess.setText(curState.process+":");
        }
    }
    private void RedrawMap(){
        int currstatePointer = graph.statePointer;
        GraphState currstate = graph.getCurrState();
        if(currstate == null) {map.onDraw(this.graph); return;}
        while(!currstate.transition) currstate = graph.getPrevState();
        while(graph.statePointer != currstatePointer){
            DrawStep(currstate);
            currstate = graph.getNextState();
        }
        this.lblProcess.setText(currstate.process);
    }
}
