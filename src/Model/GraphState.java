package Model;

import java.util.LinkedList;

public class GraphState {

    public boolean line = false;
    public boolean transition = false;

    public LinkedList<City> selection;
    public Graph snapshot;

    public String process;

    public GraphState(LinkedList<City> selection, String process){
        this.selection = selection;
        this.line = true;
        this.process = process;
    }

    public GraphState(City selection, String process){
        this.selection = new LinkedList<>();
        this.selection.add(selection);
        this.line = false;
        this.process = process;
    }

    public GraphState(Graph g, String process){
        this.transition = true;
        this.snapshot = g;
        this.process = process;
    }
}
