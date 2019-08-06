package Controller.Utils;

import Model.City;
import Model.Graph;

import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class Util {

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

    public static void printGraph(Graph g){

        LinkedList<LinkedList<Double>> adj = g.getAdjazenzmatrix();

        String str = "|\t";

        for(int i=0;i<g.getSize();i++){
            for(int j=0;j<g.getSize();j++){
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

}
