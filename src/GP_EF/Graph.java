/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GP_EF;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Do Thanh Thuy
 * The class Graph represents a graph with a matrix of capacity. The graph is directed and weighted
 */

public class Graph {
    private static int V; //the number of vertices of the graph
    private static int E;//the number of edges of the graph
    private  double[][] capMatrix; //
    

    /*
    Construct a Graph from a text file in the following format:
    line 1: an integer indicated the number of vertices, for example 5
     * line 2: an edge in the graph, including 2 vertices and capacity separated by a space, for example 0 1 100. This means that an edge from vertex 0 to vertex 1 with capacity =100
     * ...
     * next line: similarly line 2
     * @param fname the input file name     
     * @throws IllegalArgumentException if {@code V < 0}
     
    */    
    public Graph(String fname) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File(fname));
        int  v = 0;
        int e = 0;
        if (scanner.hasNextLine()){
            v = Integer.parseInt(scanner.nextLine());
        }
        if (v < 0) throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
        this.V = v;
        this.capMatrix = new double[v][v];
        for (int i = 0; i<v; i++)
            for (int j = 0; j<v;j++) this.capMatrix[i][j] = 0;
        
        while (scanner.hasNextLine()){
            String edge = scanner.nextLine();
            String[] edges = edge.split(" ");
            if (edges.length != 3) throw new IllegalArgumentException("One edge in the graph is not valid! Missing a vertex or capacity");
            int i = Integer.parseInt(edges[0]); int j = Integer.parseInt(edges[1]);
            validateVertex(i); validateVertex(j);
            this.capMatrix[i][j] = Integer.parseInt(edges[2]);
            e++;
        }
        this.E = e;
    }
    public int V(){
        return this.V;
    }
    public int E(){
        return this.E;
    }
    public double[][] capMatrix(){
        return this.capMatrix;
    }
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + "\n");
        for (int i = 0; i < V; i++) {
            String st = "";
            for (int j = 0; j<V; j++) st = st + String.valueOf(this.capMatrix[i][j]) + "  ";
            s.append(st + "\n");                        
        }
        return s.toString();
    }
    /**
     * Unit tests the {@code FlowNetwork} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        String fname = "data/lucky_puck.inp";
        Graph G = new Graph(fname);
        System.out.println(G);
    }
}
