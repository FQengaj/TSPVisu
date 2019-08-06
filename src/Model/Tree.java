package Model;

import Controller.Utils.Util;
import javafx.util.Pair;

import java.util.ArrayList;

public class Tree {

    private City root;
    private ArrayList<Pair<Tree, Double>> Children = new ArrayList<>();
    public Tree(City root){
        this.root = root;
    }

    public Tree() {
        this.root = null;
    }

    public void addChild(Tree c){
        Double dist = Util.EuclidDistance(this.root, c.getRoot());
        Pair<Tree, Double> p = new Pair<>(c, dist);
        Children.add(p);
    }

    public ArrayList<Pair<Tree,Double>> getChildren() {
        return this.Children;
    }

    public City getRoot() {
        return root;
    }

}
