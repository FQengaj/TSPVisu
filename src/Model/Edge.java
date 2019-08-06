package Model;

public class Edge implements Comparable<Edge> {
    public City source;
    public City dest;

    double dist;

    public Edge(City source, City dest, double dist)  {
        this.source = source;
        this.dest = dest;
        this.dist = dist;
    }

    @Override
    public boolean equals(Object o){

        if(o == this) return true;
        if (!(o instanceof Edge)) return false;
        Edge e = (Edge) o;
        // returns true if start and end is the same or exectly turned around
        return (this.dest == e.dest && this.source == e.source) || (this.dest == e.source && this.source == e.dest);

    }

    @Override
    public int compareTo(Edge o) {
        Double x = this.dist;
        return x.compareTo(o.dist);
    }
}
