/**
 * 06/2017
 * @author Thuy Do
 */
package GP_EF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


class UW_Graph {
    private static int V; //the number of vertices of the graph    
    private static ArrayList<GraphEdge> Edges;
    

    /*
    Construct a Graph from a text file in the following format:
    line 1: an integer indicated the number of vertices, for example 5
     * line 2: an edge in the graph, including 2 vertices separated by a space, for example 0 1 . This means that an edge from vertex 0 to vertex 1
     * ...
     * next line: similarly line 2
     * @param fname the input file name     
     * @throws IllegalArgumentException if {@code V < 0}
     
    */    
    public UW_Graph(String fname) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File(fname));
        int  v = 0;
        int e = 0;
        if (scanner.hasNextLine()){
            v = Integer.parseInt(scanner.nextLine());
        }
        if (v < 0) throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
        this.V = v;
        Edges = new ArrayList<GraphEdge>();
        
        while (scanner.hasNextLine()){
            String edge = scanner.nextLine();
            edge.trim();
            if (edge.equals("")) continue;
            String[] edges = edge.split(" ");
            if (edges.length != 2) throw new IllegalArgumentException("One edge in the graph is not valid! Missing a vertex or something else!");
            int i = Integer.parseInt(edges[0]); int j = Integer.parseInt(edges[1]);
            validateVertex(i); validateVertex(j);
            this.Edges.add(new GraphEdge(i, j, 0));            
            //this.Edges().add(new GraphEdge(j, i, 0));
        }
        
    }
    public UW_Graph() throws FileNotFoundException{
        this.V = 0;
        this.Edges = null;
    }
    /*Construct a Graph from a text file in the following format:
    line 1: an integer indicated the number of vertices and number of edges, for example 5 10
     * line 2: list of adjacent vertices of vertex 0, separated by " ". For example 1 2 3 => The vertices 1, 2, 3 are neighbours of vertex 0
     * line 3: list of adjacent vertices of vertex 1
     * ...
     */
    public UW_Graph(String fname, boolean adj_list) throws FileNotFoundException{
        if ( adj_list == false) System.exit(0);
        Scanner scanner = new Scanner(new File(fname));
        int  v = 0;
        int e = 0;
        if (scanner.hasNextLine()){
            String[] headers = scanner.nextLine().split(" ");
            v = Integer.parseInt(headers[0]);
        }
        if (v < 0) throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
        this.V = v;
        Edges = new ArrayList<GraphEdge>();
        int from = 0;
        while (scanner.hasNextLine()){
            String edge = scanner.nextLine();
            String[] edges = edge.split(" ");     
            for (int i = 0; i< edges.length; i++){
                int to = Integer.parseInt(edges[i]);
                validateVertex(from); validateVertex(to);
            this.Edges.add(new GraphEdge(from, to, 0));            
            }
            from ++;                        
        }
            
    }
    // convert a garph in METIS format into an unweighted directed graph. The destination graph is saved in a file with the format of list of adjacent vertices
    // srcFname: the source METIS file
    //desFname: the destination file
    //for example the destination file as
    // 7 11 => the graph has 7 vertices from 0 thru 6, and 11 edges (line 1)
    // 1 2 4 => the adjacent vertices of vertex 0 are 1 2 and 4 (line 2)
    // 0 3 4 => the adjacent vertices of vertex 1 are 0 3 and 4 (line 3)
    // ....
    public void convertGraph(String srcFname, String desFname) throws FileNotFoundException, IOException
    {   
        Scanner scanner = new Scanner(new File(srcFname));
        FileWriter writer = new FileWriter(desFname, false);        
        String header = "";
        if (scanner.hasNextLine()){
            header = scanner.nextLine();
        }
        if (header.length() <= 0) throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
        String[] headers = header.split(" ");
        if (headers.length < 2) throw new IllegalArgumentException("Must specify the number of vertices and edges!");        
        if (headers.length >=3) {
            writer.write(headers[0] + " " + headers[1] + "\n");
            String fmt = headers[2].trim();
            int i = 0;                    
            while (scanner.hasNextLine()){
                String edge = scanner.nextLine();
                String[] edges = edge.split(" ");
                String written_st = "";
                if (fmt.equalsIgnoreCase("1")){ //weight associated with edges
                    int j = 0;
                    while (j < edges.length){
                        written_st = written_st + String.valueOf(Integer.parseInt(edges[j]) - 1) + " " ; j += 2;
                    }
                }else if (fmt.equalsIgnoreCase("10")){ //weight associated with vertices
                    int j = 1;
                    while (j < edges.length){
                        written_st = written_st + String.valueOf(Integer.parseInt(edges[j]) - 1) + " " ; j ++;
                    }
                }else if (fmt.equalsIgnoreCase("011") || (fmt.equalsIgnoreCase("11"))){ // weight associated with both
                    int j = 1;
                    while (j < edges.length){
                        written_st = written_st + String.valueOf(Integer.parseInt(edges[j]) - 1) + " " ; j += 2;
                    }
                }
                writer.write(written_st + "\n");            
            }        
        }
        writer.close();scanner.close();
    }
    public int V(){
        return this.V;
    }
    
    public ArrayList<GraphEdge> Edges(){
        return this.Edges;
    }
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + "\n");
        for (int i = 0; i < Edges.size(); i++) {
            String st = String.valueOf(Edges.get(i).from()) + " " + String.valueOf(Edges.get(i).to());            
            s.append(st + "\n");                        
        }
        return s.toString();
    }
    /**
     * Unit tests the {@code FlowNetwork} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //String fname = "data/unweighted_graph_lucky_puck.inp";
        //UW_Graph G = new UW_Graph(fname);
        //System.out.println(G);
        //String src_fname = "data/need_converting/7_11_1.inp"; //right
        //String des_fname = "data/need_converting/7_11_1_converted.inp"; 
        
        //String src_fname = "data/need_converting/7_11_10.inp"; //right
        //String des_fname = "data/need_converting/7_11_10_converted.inp"; //right
        //String src_fname = "data/need_converting/7_11_11.inp"; //right
        String des_fname = "data/need_converting/7_11_11_converted.inp"; //right
        //UW_Graph G = new UW_Graph();
        //G.convertGraph(src_fname, des_fname);
        UW_Graph G = new UW_Graph(des_fname, true);
        System.out.println(G);
    }
}
