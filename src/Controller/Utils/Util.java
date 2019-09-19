package Controller.Utils;

import Model.City;
import Model.Graph;
import Model.GraphState;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Util {


    public static LinkedList<LinkedList<Double>> perfectMatch(Graph graph){
        LinkedList<LinkedList<Double>> perfectMatchAdj = Undirected(graph.getAdjazenzmatrix());

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
        if(vertexWithOddDeg.isEmpty()){
            return graph.getAdjazenzmatrix();
        }

        LinkedList<LinkedList<Double>> matchingAdj = matching_graph.getAdjazenzmatrix();

        // same Graph so no join will be needed.
        // Perfect matching..


        LinkedList<LinkedList<Double>> matched = EmtyAdjazenzmatrix(vertexWithOddDeg.size());

        for (int i = 0; i < vertexWithOddDeg.size()-1; i+=2) {
            double newVal = matchingAdj.get(i).get(i+1);
            matched.get(i).set(i+1, newVal);
            matched.get(i+1).set(i, newVal);
        }
        matching_graph.setAdjazenzmatrix(matched);

        // print Vertex indices with odd Deg
        System.out.print("[");
        for (int i = 0; i < vertexWithOddDeg.size()-1; i++) {
            int x = vertexWithOddDeg.get(i);
            System.out.print(x+", ");
        }
        System.out.printf("%d]\n", vertexWithOddDeg.getLast());

        printGraph(matched);

        // Add Edges from Perfect Matching
        // todo nicht ganz richtig !!
        for(int i = 0; i < vertexWithOddDeg.size(); i++){
            for (int j = 0; j < i; j++) {
                double newVal = matchingAdj.get(i).get(j);
                if (newVal!= 0){
                    int xindex = vertexWithOddDeg.get(i),
                        yindex = vertexWithOddDeg.get(j);

                    // undirected
                    perfectMatchAdj.get(xindex).set(yindex, newVal);
                    perfectMatchAdj.get(yindex).set(xindex, newVal);
                }
            }
        }
        printGraph(perfectMatchAdj);

        // new graph with Eulercirc.
        //perfectMatchAdj = EulerCirc(new Graph(graph.getVertices(), perfectMatchAdj));

        return perfectMatchAdj;
    }
    public static LinkedList<LinkedList<Double>> PrimsMST(Graph graph){
        LinkedList<LinkedList<Double>> MST = EmtyAdjazenzmatrix(graph.getSize());

        // Liste der bereits bekannten Knoten
        LinkedList<Integer> indexOfVisited = new LinkedList<>();

        int start = (int)(Math.random()*graph.getSize());
        indexOfVisited.add(start);
        addPointState(graph, "Prims MST", start);
        //addPointState(graph, "Prims Minimum Spanning Tree", start);

        while(indexOfVisited.size() < graph.getSize()){
            LinkedList<Integer> listOfBest = new LinkedList<>(),
                                verticesToDisplay = new LinkedList<>();
            // suche von allen Besuchten Edges den mit dem Geringsten Kosten(dist)
            for (int i = 0; i < indexOfVisited.size(); i++) {

                // nehme eine Spalte aus der Adjazenzmatrix
                int currNodeIndex = indexOfVisited.get(i);
                LinkedList<Double> weightsFromCurr = graph.getAdjazenzmatrix().get(currNodeIndex);

                int nextBest = -1;
                // für jeden Besuchten Knoten finde einen neuen mit dem Kleinsten gewicht
                for(int j = 0; j < weightsFromCurr.size(); j++){
                    Double weight = weightsFromCurr.get(j);

                    if (weight == 0) continue;
                    if (!indexOfVisited.contains(j)){
                        verticesToDisplay.add(j);
                        if (nextBest == -1) {
                            nextBest = j;
                        }else if (weight < weightsFromCurr.get(nextBest)){
                            nextBest = j;
                        }
                    }
                }
                //addPointState(graph, "PrimsMST: Check Neigbors",verticesToDisplay);
                listOfBest.add(nextBest);
                //addPointState(graph, "PrimsMST: Selected Best Vertex for this Parent", nextBest);
            }

            int indexOfBest = 0;
            addPointState(graph, "PrimsMST: Select Best of the Best", listOfBest);
            double weightOfBest = graph.getAdjazenzmatrix().get(indexOfVisited.get(indexOfBest)).get(listOfBest.get(indexOfBest));
            for (int i = 1; i < listOfBest.size(); i++){
                double temp = graph.getAdjazenzmatrix().get(indexOfVisited.get(i)).get(listOfBest.get(i));
                if (temp < weightOfBest){
                    indexOfBest = i;
                    weightOfBest = temp;
                }
            }
            //addPointState(graph, "PrimsMST: Next Best Vertex", listOfBest.get(indexOfBest));
            addLineState(graph, "PrimsMST: add Best", listOfBest.get(indexOfBest), indexOfVisited.get(indexOfBest));
            MST.get(indexOfVisited.get(indexOfBest)).set(listOfBest.get(indexOfBest), weightOfBest);

            indexOfVisited.add(listOfBest.get(indexOfBest));
        }


        graph.addstate(new GraphState(new Graph(graph.getVertices(), MST),"Done with Primes MST"));
        return MST;
    }
    public static LinkedList<LinkedList<Double>> EulerCirc(Graph graph){
        // Die TSP-Tour ergibt sich direkt aus der DFS-Numerierung.
        LinkedList<LinkedList<Double>> adj = Undirected(graph.getAdjazenzmatrix());

        LinkedList<Integer> queue = new LinkedList<>(),
                            visited = new LinkedList<>();

        int start = (int)(Math.random()*graph.getSize());

        queue.add(start);
        while(!queue.isEmpty()){
            int curr = queue.remove(); // get the Next element in Queue
            if (visited.size() > 0){
                addLineState(graph, "EulerCirc Depth First Search", curr, visited.getLast());
            }
            // mark different as seen!
            addPointState(graph, "EulerCirc Depth First Search", curr);
            visited.add(curr);  // has in the end the DFS order.


            LinkedList<Double> ConnWeights = adj.get(curr);
            LinkedList<Integer> ConnCityIndex = new LinkedList<>();

            // create a orderd List of connected Vert
            for (int i = 0; i < ConnWeights.size(); i++) {
                double newweight = ConnWeights.get(i);
                
                if (newweight != 0 && !visited.contains(i)){
                    boolean added = false;
                    for (int j = 0; j < ConnCityIndex.size(); j++) {
                        if (newweight < ConnWeights.get(ConnCityIndex.get(j))){
                            ConnCityIndex.add(j, i);
                            added = true;
                            break;
                        }
                    }
                    if (!added){
                        ConnCityIndex.add(i);
                    }
                }
            }

            // ConnCityIndex now contains a orderd list of Citys to curr.

            addPointState(graph, "EulerCirc Depth First Search", ConnCityIndex);
            
            queue.addAll(0,ConnCityIndex);
            
            
        }
        addLineState(graph, "EulerCirc DepthFirstSearch", visited.getFirst(), visited.getLast());

        LinkedList<LinkedList<Double>> EulerCirc = EmtyAdjazenzmatrix(graph.getSize());


        LinkedList<City> cities = graph.getVertices();
        int currIndex, nextIndex;
        City curr, next;
        for(int i = 0; i < graph.getSize()-1; i++){
            currIndex = visited.get(i);
            nextIndex = visited.get(i+1);

            curr = cities.get(currIndex);
            next = cities.get(nextIndex);
            // todo ohne berechnung ?
            EulerCirc.get(currIndex).set(nextIndex, Util.EuclidDistance(curr, next));
        }


        EulerCirc.get(visited.getLast()).set(visited.getFirst(), Util.EuclidDistance(cities.get(visited.getLast()), cities.get(visited.getFirst())));

        return EulerCirc;
    }


    private static void addLineState(Graph graph, String s, int x, int y) {
        LinkedList<City> tmp = new LinkedList<>();
        tmp.add(graph.getCity(x));
        tmp.add(graph.getCity(y));
        graph.addstate(new GraphState(tmp, s));
    }
    private static void addPointState(Graph graph, String s, LinkedList<Integer> indices) {
        LinkedList<City> tmp = new LinkedList<>();
        for (int i : indices) tmp.add(graph.getCity(i));
        GraphState gs = new GraphState(tmp, s);
        gs.line = false;
        graph.addstate(gs);

    }
    private static void addPointState(Graph g, String process, City city){
        GraphState tmp = new GraphState(city, process);
        g.addstate(tmp);
    }
    private static void addPointState(Graph g, String process, int city){
        addPointState(g, process, g.getCity(city));
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

    public static double PathCostOf(Graph g){
        double result = 0;

        for (LinkedList<Double> x : g.getAdjazenzmatrix()){
            for (double w : x) result += w;
        }

        return result;
    }
    public static double EuclidDistance(City c1, City c2){
        //calculate the a and b sides of the Triangle between the cities
        double A = Math.abs(c1.x - c2.x);
        double B = Math.abs(c1.y - c2.y);

        // use pythagoras to get the length of the Path between both cities
        return Math.sqrt((A*A)+(B*B));
    }


    private static void printGraph(LinkedList<LinkedList<Double>> adj){
        StringBuilder str = new StringBuilder("|\t");

        for(int i=0;i<adj.size();i++){
            for(int j=0;j<adj.size();j++){
                str.append(String.format("%.4f\t", adj.get(i).get(j)));
            }

            System.out.println(str + "|");
            str = new StringBuilder("|\t");
        }

        System.out.println("\n");
    }
    public static void printGraph(Graph g){
        LinkedList<LinkedList<Double>> adj = g.getAdjazenzmatrix();
        printGraph(adj);
    }
    private static LinkedList<LinkedList<Double>> RandomPath(Graph graph, Random r){
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
