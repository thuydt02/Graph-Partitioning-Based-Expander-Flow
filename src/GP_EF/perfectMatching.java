/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GP_EF;

import java.util.ArrayList;

/**
 *
 * @author Do Thanh Thuy
 * The perfectMatching class is a data structure representing a perfect matching of a graph
 */
class perfectMatching {
    private int V; // the number of vertices of the graph
    private ArrayList<GraphEdge> Matches;
    
    public perfectMatching(int v, ArrayList<GraphEdge> ges){
        if (v <= 0) throw new IllegalArgumentException("The number of a graph must be greater than 0!");
        this.V = v;
        this.Matches = ges;
    }
    public int V(){
        return this.V;
    }
    public ArrayList<GraphEdge> Matches(){
        return this.Matches;
    }
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + "\n");
        for (int i = 0; i< Matches.size(); i++) {
            String st = String.valueOf(Matches().get(i).from()) + " " + String.valueOf(Matches().get(i).to()) + " " + String.valueOf(Matches().get(i).cap());            
            s.append(st + "\n");                        
        }
        return s.toString();
    }
    public boolean inPM(GraphEdge ge){
        boolean found = false;
        for (int i = 0; i<Matches.size();i++)
            if ((ge.from() == Matches.get(i).from()) && (ge.to() == Matches.get(i).to())){
                found = true; break;
            }
        return found;
    }
   
    /*Unit test*/
    public static void main(String args[]){
        int v = 4;
        ArrayList<GraphEdge> ges = new ArrayList<GraphEdge>();
        ges.add(new GraphEdge(0, 1, 100));
        ges.add(new GraphEdge(2, 3, 200));
        perfectMatching pm  = new perfectMatching(v, ges);
        System.out.println("\n" + pm);
        
    }
}
