package Model;

import Controller.Utils.Util;

import java.util.LinkedList;


/*
 - representation of a Complete Graph
 - size is the Power of the Vertex Set
 - though this Graph is undirected, every Edge is only represented once
 - Totalweight also counts the Weight of every edge only once.
*/
public class Graph {

    private LinkedList<LinkedList<Double>> Adj;
    private LinkedList<City> vertices;
    public boolean isDirected = true;

    public Graph(LinkedList<City> vertices) {
        this.vertices = vertices;
        this.Adj = new LinkedList<>();

        for (int i = 0; i < this.vertices.size(); i++) {
            City curr = vertices.get(i);
            LinkedList<Double> newEntry = new LinkedList<>();
            for (int j = 0; j < this.vertices.size(); j++){

                City other = vertices.get(0);
                double dist = (curr.equals(other)) ? 0 : Util.EuclidDistance(curr, other);

                newEntry.add(dist);
            }
            this.Adj.add(newEntry);
        }

    }

    public Graph(){
        Adj = new LinkedList<>();
        vertices = new LinkedList<>();
    }

    public void addVertex(City c){
        LinkedList<Double> newEntry = new LinkedList<>();
        if (this.Adj.size() == 0){
            newEntry.add(0.0);
            this.Adj.add(newEntry);
            this.vertices.add(c);
            return;
        }

        for (int i = 0; i < this.vertices.size(); i++) {
            City curr = vertices.get(i);


            double dist = (c.equals(curr)) ? 0 : Util.EuclidDistance(curr, c);

            LinkedList<Double> connToCurr = Adj.get(i);
            connToCurr.add(connToCurr.size(), dist);

            newEntry.add(dist);
        }

        newEntry.add(0.0);
        this.Adj.add(newEntry);

        vertices.add(c);

    }

    public int getSize() {
        return vertices.size();
    }

    public LinkedList<City> getVertices() {
        return vertices;
    }

    public void setVertices(LinkedList<City> vertices) {
        this.vertices = vertices;
    }

    public LinkedList<LinkedList<Double>> getAdjazenzmatrix() {
        return Adj;
    }

    public void setAdjazenzmatrix(LinkedList<LinkedList<Double>> adj) {
        Adj = adj;
    }

    public void clear() {
        this.vertices.clear();
        this.Adj.clear();
    }
}