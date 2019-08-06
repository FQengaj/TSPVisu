package Model;

public class City implements Comparable<City>{
    public double x, y;
    //public String name;

    public City(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if (!(o instanceof City)) return false;
        City c = (City) o;
        /*if (Double.compare(this.x, c.x) == 0
                && Double.compare(this.y, c.y) == 0){
            return name.equals(c.name);
        }*/
        return Double.compare(this.x, c.x) == 0 && Double.compare(this.y, c.y) == 0;
    }


    @Override
    public int compareTo(City c) {
        int yComp = Double.compare(this.y, c.y);
        if (yComp == 0){
            return Double.compare(this.x, c.x);
        }else{
            return yComp;
        }
    }
}
