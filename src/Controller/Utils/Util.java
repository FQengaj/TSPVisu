package Controller.Utils;

import Model.City;
import Model.Graph;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Util {


    public static LinkedList<LinkedList<Double>> perfectMatch(Graph graph){
        LinkedList<LinkedList<Double>> perfectMatchAdj = copyGraphAdj(graph);

        LinkedList<Integer> vertexWithOddDeg = new LinkedList<>();

        Graph matching_graph = new Graph();
        // select all Vertecies with Odd Degre
        // Opt: no need for adj do all with perfectMatchAdj
        for (int i = 0; i < perfectMatchAdj.size(); i++) {
            int deg = 0;
            for (int j = 0; j < perfectMatchAdj.get(i).size(); j++) {
                if (perfectMatchAdj.get(i).get(j) != 0) deg++;
            }

            if (deg%2 != 0) {
                vertexWithOddDeg.add(i);
                matching_graph.addVertex(graph.getCity(i));
            }
        }
        LinkedList<LinkedList<Double>> matchingAdj = matching_graph.getAdjazenzmatrix();

        // same Graph so no join will be needed.
        // Perfect matching..


        LinkedList<LinkedList<Double>> matched = EmtyAdjazenzmatrix(vertexWithOddDeg.size());

        for (int i = 0; i < vertexWithOddDeg.size()-1; i+=2) {
            matched.get(i).set(i+1, 1.0);
            matched.get(i+1).set(i, 1.0);
        }

        printGraph(matched);

        // Add Edges from Perfect Matching
        for(int i = 0; i < vertexWithOddDeg.size(); i++){
            for (int j = 0; j < i; j++) {
                double newVal = matchingAdj.get(i).get(j);
                if (newVal!= 0){
                    int xindex = vertexWithOddDeg.get(i),
                        yindex = vertexWithOddDeg.get(j);

                    // undirected
                    perfectMatchAdj.get(i).set(j, newVal);
                    perfectMatchAdj.get(j).set(i, newVal);
                }
            }
        }

        // new graph with Eulercirc.
        //perfectMatchAdj = EulerCirc(new Graph(graph.getVertices(), perfectMatchAdj));

        return perfectMatchAdj;
    }

    private static LinkedList<LinkedList<Double>> copyGraphAdj(Graph graph) {
        LinkedList<LinkedList<Double>> result = new LinkedList<>();
        LinkedList<LinkedList<Double>> source = graph.getAdjazenzmatrix();

        for (int i = 0; i <graph.getSize(); i++) {
            LinkedList<Double> row = new LinkedList<>();
            for (int j = 0; j < graph.getSize(); j++) {
                double currVal = source.get(i).get(j);
                row.add(currVal);
            }
            result.add(row);
        }

        return result;
    }


    public static LinkedList<LinkedList<Double>> PrimsMST(Graph graph){
        LinkedList<LinkedList<Double>> MST = EmtyAdjazenzmatrix(graph.getSize());

        // Liste der bereits bekannten Knoten
        LinkedList<Integer> indexOfVisited = new LinkedList<>();

        int start = (int)(Math.random()*graph.getSize());
        indexOfVisited.add(start);

        while(indexOfVisited.size() < graph.getSize()){
            LinkedList<Integer> listOfBest = new LinkedList<>();
            // suche alle Besuchten Edges ab
            for (int i = 0; i < indexOfVisited.size(); i++) {

                //nehme eine Spalte aus der Adjazenzmatrix
                int currNodeIndex = indexOfVisited.get(i);
                LinkedList<Double> weightsFromCurr = graph.getAdjazenzmatrix().get(currNodeIndex);


                int nextBest = -1;
                // für jeden Besuchten Knoten finde den mit dem Kleinsten gewicht
                // der einen Neuen Knoten hinzufügt.
                for(int j = 0; j < weightsFromCurr.size(); j++){
                    Double weight = weightsFromCurr.get(j);

                    if (weight == 0) continue;
                    if (!indexOfVisited.contains(j)){
                        if (nextBest == -1) {
                            nextBest = j;
                        }else if (weight < weightsFromCurr.get(nextBest)){
                            nextBest = j;
                        }
                    }
                }
                listOfBest.add(nextBest);
            }

            int indexOfBest = 0;
            double weightOfBest = graph.getAdjazenzmatrix().get(indexOfVisited.get(indexOfBest)).get(listOfBest.get(indexOfBest));
            for (int i = 1; i < listOfBest.size(); i++){
                double temp = graph.getAdjazenzmatrix().get(indexOfVisited.get(i)).get(listOfBest.get(i));
                if (temp < weightOfBest){
                    indexOfBest = i;
                    weightOfBest = temp;
                }
            }

            MST.get(indexOfVisited.get(indexOfBest)).set(listOfBest.get(indexOfBest), weightOfBest);
            indexOfVisited.add(listOfBest.get(indexOfBest));
        }



        return MST;
    }

    public static LinkedList<LinkedList<Double>> EulerCirc(Graph graph){
        // Die TSP-Tour ergibt sich direkt aus der DFS-Numerierung.

        LinkedList<LinkedList<Double>> EulerCirc = EmtyAdjazenzmatrix(graph.getSize());

        // Add a start node
        Stack<Integer> stack = new Stack<>();
        int start = (int)(Math.random()*graph.getSize());
        stack.push(start);

        // first point
        LinkedList<Integer> path = new LinkedList<>();
        path.add(start);

        LinkedList<City> cities = graph.getVertices();
        LinkedList<LinkedList<Double>> adj = Util.Undirected(graph.getAdjazenzmatrix());


        while(!stack.empty()){
            LinkedList<Double> weights = adj.get(stack.pop());

            for(int i = weights.size() - 1; i >= 0; i--){
                if (path.contains(i)) continue;
                double w = weights.get(i);
                if (w > 0){
                    stack.push(i);
                    if(!path.contains(i)) path.add(i);
                }
            }
        }

        /*
        int currIndex = path.get(0);
        int nextIndex = path.get(1);

        City curr = cities.get(currIndex);
        City next = cities.get(nextIndex);
        */
        int currIndex, nextIndex;
        City curr, next;
        for(int i = 0; i < graph.getSize()-1; i++){
            currIndex = path.get(i);
            nextIndex = path.get(i+1);

            curr = cities.get(currIndex);
            next = cities.get(nextIndex);

            EulerCirc.get(currIndex).set(nextIndex, Util.EuclidDistance(curr, next));
        }


        EulerCirc.get(path.getLast()).set(path.getFirst(), Util.EuclidDistance(cities.get(path.getLast()), cities.get(path.getFirst())));

        return EulerCirc;
    }

    private static LinkedList<LinkedList<Double>> Undirected(LinkedList<LinkedList<Double>> adj) {
        LinkedList<LinkedList<Double>> result = EmtyAdjazenzmatrix(adj.size());

        for (int i = 0; i < adj.size(); i++){
            for (int j = 0; j < adj.size(); j++){
                    double w = adj.get(i).get(j);
                    if (w>0){
                        result.get(i).set(j, w);
                        result.get(j).set(i, w);
                    }
            }
        }

        return result;
    }

    public static LinkedList<LinkedList<Double>> RandomPath(Graph graph, Random r){
        LinkedList<LinkedList<Double>> adj = graph.getAdjazenzmatrix();
        if (graph.isDirected) return adj;

        LinkedList<LinkedList<Double>> result = EmtyAdjazenzmatrix(graph.getSize());
        LinkedList<Integer> path = new LinkedList<>();

        int curr = r.nextInt(graph.getSize());
        path.add(curr);

        for (int i = 1; i < graph.getSize(); i++) {
            int next = r.nextInt(graph.getSize());
            while(!path.contains(next)){
                next = r.nextInt(graph.getSize());
            }
            path.add(next);
            result.get(curr).set(next, adj.get(curr).get(next));
            curr = next;
        }

        return result;
    }

    public static void printGraph(Graph g){

        LinkedList<LinkedList<Double>> adj = g.getAdjazenzmatrix();

        printGraph(adj);
    }


    public static void printGraph(LinkedList<LinkedList<Double>> adj){
        String str = "|\t";

        for(int i=0;i<adj.size();i++){
            for(int j=0;j<adj.size();j++){
                str += String.format("%.4f\t" ,  adj.get(i).get(j));
            }

            System.out.println(str + "|");
            str = "|\t";
        }

        System.out.println("\n");
    }

    public static double EuclidDistance(City c1, City c2){
        //calculate the a and b sides of the Triangle between the cities
        double A = Math.abs(c1.x - c2.x);
        double B = Math.abs(c1.y - c2.y);

        // use pythagoras to get the length of the Path between both cities
        return Math.sqrt((A*A)+(B*B));
    }

    public static double PathCostOf(Graph g){
        double result = 0;

        for (LinkedList<Double> x : g.getAdjazenzmatrix()){
            for (double w : x) result += w;
        }

        return result;
    }



    private static LinkedList<LinkedList<Double>> EmtyAdjazenzmatrix(int bound){
        LinkedList<LinkedList<Double>> result = new LinkedList<>();

        //init with 0

        for (int i = 0; i < bound; i++){
            LinkedList<Double> tmp = new LinkedList<>();
            for (int j = 0; j < bound; j++) tmp.add(0.0);
            result.add(tmp);
        }

        return result;
    }

    //---- on ICE maybe dont need it...
    /*
    private static Graph joinGraphs(Graph g1, Graph g2) {
        LinkedList<City> g1citys = g1.getVertices(),
                        g2citys = g2.getVertices(),
                        resultCity  = new LinkedList<>();

        for(City c : g1citys){
            if(!resultCity.contains(c)){
                resultCity.add(c);
            }
        }

        for(City c : g2citys){
            if(!resultCity.contains(c)){
                resultCity.add(c);
            }
        }

        LinkedList<LinkedList<Double>> resultAdj = EmtyAdjazenzmatrix(resultCity.size());

        for (int i = 0; i < resultCity.size(); i++) {



        }


        Graph result = new Graph(resultCity, resultAdj);

        return result;
    }
    */
}
