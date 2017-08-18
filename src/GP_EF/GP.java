/**
 *
 * @author Thuy Do
 * 06/2017
 * Given a graph G(V,E) (G is unweighted and directed) and an expansion parameter alpha
 * This class is to partition G
 * such that
 * + V = S U S_bar, S is called the cut
 * + and phi(S) <= alpha
 * where phi(S) = min (E(S, S_bar)/|S|) |S| <= (the number of vertices of G)/2. S is a subset in V
 * The algorithm to do the cut belongs to the papers "Graph partitioning using single commodity flows" (ACM 2006)
 * and "Geometry, Flows and Graph Partitioning" (ACM 2008)
 * it is based on the expander flow approach.
 * Dependencies: FlowNetwork.java, FoldFukerson.java, UW_graph.java, perfectMatching.java
 * Note: the vertices of a graph are ordered from 0
 * Note: you should run the class in several times to get the better cut as you want depending on your time you have because there are some random generations of cuts and vector
 * if you want to run the class with an undirected graph, you should specify an edge between 2 vertices like this
 * for example you have a link between vertex 0 and vertex 1, you should add 2 edges (0,1) and (1,0) in the input file
 * so the GP will count 2 edges for the link => the output should be divided by 2 for the number of edges
 * for example, with the follow input file, you get a best balanced cut with the cut = {3  6  2  1  7}
 * and the number of edges crossing the cut is 4 => you should actually have 4/2 = 2 edges crossing the cut
 * similarly,
 * and the number of edges in part 1 is 12 => it should be 12/2 = 6 edges
 * and the number of edges in part 2 is 12 => it should be 12/2 = 6 edges
 * Input file content:
 *10
1 2
1 6
2 3
2 7
2 1
3 2
3 4
3 6
4 3
4 5
4 9
5 4
5 8
5 0
6 1
6 3
6 7
7 2
7 6
7 8
8 5
8 7
8 9
9 4
9 8
9 0
0 5
0 9

* output

There are 2 cuts obtained
 Cut 0: 3  6  2  1  7  
 Cut 1: 1  2  6  
The Graph: 10   28
The best balanced cut: 
               Part1               Part2           Crossing
  #Vertices:       5                   5                   0
     #Edges:      12                  12                   4
 The cut:
3  6  2  1  7  

The Graph: 10   28
The last (min) cut: 
               Part1               Part2           Crossing
  #Vertices:       3                   7                   0
     #Edges:       4                  16                   8
 The cut: 
1  2  6  
There are 0 perfect matchings obtainedBUILD SUCCESSFUL (total time: 0 seconds)
* 
* You can run the class with METIS data, METIS ordered vertices of a graph from 1
* So the class will reduce the vertex order in METIS by 1.
* For example, vertex 1 in METIS => vertex 0 in GP
*              vertex 2 in METIS => vertex 1 in GP, ....
* For working with METIS data, you should call the class like the follow
* String src_fname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform.txt"; // the METIS data file
  String des_fname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform-converted.txt"; // the destination file which are converted from the METIS data file
  UW_Graph G = new UW_Graph();
  G.convertGraph(src_fname, des_fname); // to convert the METIS data file into the new format corresponding to GP
  G = new UW_Graph(des_fname, true); //call the constructor for the data input file converted from METIS data. Note: the second parameter should be TRUE
  GP gp = new GP(G, 0.5);
  gp.findCuts();
  gp.showCuts();
  gp.showBestBalanceCut();
  System.out.println();
  gp.showLastCut();

 */
package GP_EF;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class GP {
    /**
     * @param args the command line arguments
     */
    private UW_Graph G; // the graph to partition
    private final double[] r; // a random unit n-dimensional vector orthogonal to 1-vector. 1-vector is a vector of all 1s; n = the number of vertices of G
    private double[] u; // the u vector in the paper
    private final double alpha; // the expansion parameter
    private ArrayList<double[][]> P; // n x n matrix represents the probability transition matrix of the natural random walk.
    private final ArrayList<perfectMatching> M; // the sequance of perfect matchings of G
    private final ArrayList<int[]> cuts; // the list of possible cuts computed. Expected that the last cut in the list is the best one that algorithm can found
        
    
    public GP(UW_Graph uwG, double alpha){
        this.G = uwG; this.alpha = alpha;
        u = new double[uwG.V()]; 
//        P = new ArrayList<>();
        M = new ArrayList<>();
        cuts = new ArrayList<>();        
        
        /*if (uwG.V() == 2) {
            r = new double[2];
            r[0] = 1/Math.sqrt(2);r[1] = -1/Math.sqrt(2);
        }else
            if (uwG.V()>=4){
                r = new double[uwG.V()];
                r[0] = 0.5;r[1] = -0.5;r[2] = 0.5;r[3] = -0.5;
                for (int i =4; i<uwG.V();i++)r[i] = 0;
            }
            else r = null;            
        */
        Random rnd = new Random() ;
        r = new double[uwG.V()];
        ArrayList<Double> list = new ArrayList<Double>();
        int i = 0;
        while (i< uwG.V()){
            double tmp = rnd.nextDouble();
            list.add(new Double(tmp));
            list.add(new Double(tmp*(-1))); i+=2;            
        }
        
        //Collections.shuffle(list);
                    
        if (uwG.V() % 2 == 1) {
            int k = uwG.V() - 1;
            for (i = 0; i < k ; i++){
                double tmp = list.get(i).doubleValue() - list.get(k).doubleValue()/k;
                list.set(i, new Double(tmp));
            }
            int last = list.size() - 1;
            list.remove(last);
        }
        double norm_r = 0;
        for (i = 0; i<= uwG.V() - 1;i++){
            norm_r = norm_r + Math.pow(list.get(i).doubleValue(),2);
        }
        norm_r = Math.pow(norm_r, 0.5);
        Collections.shuffle(list);
        for (i = 0; i<= uwG.V()-1; i++){
            r[i] = list.get(i).doubleValue()/norm_r;            
        }
            
    }
    /*Construct a flow network from G and a cut S. S is the last cut in the list cuts.*/    
    private FlowNetwork create_FN(){        
        int[] S = cuts.get(cuts.size()-1);
        if (S.length == 0) throw new IllegalArgumentException("The cut must not be empty");
        int GV = G.V();        
        int[] S_bar = new int[G.V()- S.length];
        int j = 0;
        for (int i = 0; i<GV; i++){
            if (!inSet(i, S)){
                S_bar[j] = i; j++;
            }
        }
        //create the list of FlowEdge
        ArrayList<FlowEdge> feList = new ArrayList<FlowEdge>();
        int GE = G.Edges().size();
        for (int i = 0; i <GE; i++){
            int from = G.Edges().get(i).from(); int to = G.Edges().get(i).to();
            if ((inSet(from, S) && inSet(to, S_bar)) ||(inSet(from, S_bar) && inSet(to, S))) {
               // if (inSet(from, S_bar) ) {
               //     int tmp = from; from = to; to = tmp;
               // }
                from++; to++;                
                FlowEdge fe = new FlowEdge(from, to, 1/alpha, 0);
                feList.add(fe);
            }
        }
        for (int i = 0; i<S.length; i++){
            FlowEdge fe = new FlowEdge(0, S[i]+1, 1, 0);
            feList.add(fe);
        }        
        for (int i = 0; i<S_bar.length; i++){
            FlowEdge fe = new FlowEdge(S_bar[i]+1, GV + 1,1, 0);
            feList.add(fe);
        }                
        FlowNetwork fn = new FlowNetwork(GV+2, feList); // GV+2 = add source and sink: the first for source and the last for sink
        return fn;
    }    
    /*
    Find a bisection from the sequence of perfect matchings
    call this function when the graph have even number of vertices 
    this is because even number of vertices is one of conditions for a gragh having a perfect matching
    and this function find Bisection of a graph according to the sequence of perfect matchings of the graph
    */
    private int[] findBisection(){        
        //compute transition matrices P according to sequence of perfect matchings M
        P = new ArrayList<>();
        int n = G.V();int[] S = new int[n/2];
        if (n%2 !=0) throw new IllegalArgumentException("\nThe number of vertices of the graph must be even");
        for (int t = 0; t<M.size();t++)
        {   double[][] ap = new double[n][n];
            for (int i = 0; i<n; i++)
                for (int j = 0;j<n;j++){
                    GraphEdge ge = new GraphEdge(i, j, 0);
                    if (M.get(t).inPM(ge) || ( i ==j)) ap[i][j] = 0.5;
                    else ap[i][j] = 0;
                }
            P.add(ap);                  
        }
        //compute u
        u = dot_product(P.get(0), r);
        for (int t = 1; t<P.size();t++){
            u = dot_product(P.get(t), u);            
        }
        
        boolean[] picked = new boolean[u.length];
        for (int i=0; i<u.length;i++) picked[i] = false;
        for (int i = 0; i<u.length/2;i++)
        {   
            double min = 1000000; int min_ind = -1;
            for (int t = 0; t<u.length;t++){
                if (!picked[t]){min = u[t]; min_ind=t; break;}
            }            
            for (int j = 0; j<u.length;j++){
                if ((!picked[j]) && (min>u[j])){
                    min = u[j]; min_ind=j;
                }
            }
            //double tmp = u[i]; u[i] = min; u[min_ind] = tmp;            
            S[i] = min_ind; //if (i == u.length/2 - 1) break;            
            picked[min_ind] = true;
        }        
        for (int i = 0; i<S.length-1;i++)
        {   int min = S[i];int min_ind = i;
            for (int j = i+1; j<S.length;j++)
                if (min > S[j]){
                    min = S[j]; min_ind=j;
                }
            int tmp = S[i]; S[i] = min; S[min_ind] = tmp;            
        }
        return S;
    }
        
    private double[] dot_product(double[][] M, double[] v){
        if (v.length != M.length) throw new IllegalArgumentException("\nThe dimension of the matrix and the vector must be equal");
        double[] ret_val = new double[v.length];
        for (int i = 0; i<v.length;i++){
            ret_val[i] = 0;       
            for (int j = 0; j<v.length;j++ )
            {
                ret_val[i] += M[i][j] * v[j];
            }
        }
        return ret_val;
    }
    
    private boolean inSet(int v, int[] the_set){
        boolean found = false;
        for (int i = 0; i<the_set.length; i++)
            if (v == the_set[i]) {
                found = true; break;
            }
        return found;
    }
    private boolean inCuts(int[] S){
        boolean found = true;        
        if (cuts.size() > 0){
            for (int t = 0; t<cuts.size(); t++ )
            {
                int[] a_cut = cuts.get(t);
                if (S.length == a_cut.length){
                    for (int i = 0; i<a_cut.length; i++){
                        if (S[i] != a_cut[i]) {found = false;break;}
                    }
                    if (found) break;
                }else found = false;
                if (!found) break;
            }
        }else found = false;
        return found;
    }
    //find the best cut as it can for the graph
    public void findCuts(){
        // random a cut
        //int[] S = getRandomCut();
        this.cuts.add(getRandomCut());
        while (true){                    
            FlowNetwork fnGraph = create_FN();
            int n = (fnGraph.V() -2 );
            int s = 0, t = fnGraph.V()-1;
            System.out.println("\nThe flow network graph:" + fnGraph);
            // compute maximum flow and minimum cut
            FordFulkerson maxflow = new FordFulkerson(fnGraph, s, t);
            System.out.println("MaxFlow: " + String.valueOf(maxflow.value()));
            if ((n%2 ==1)||(maxflow.value() < (n/2))){
                //compute the cut and stop                                
                int ind = 0;
                for (int v = 1; v < fnGraph.V(); v++)
                    if (maxflow.inCut(v))ind++;
                int[] a_cut = new int[ind];          
                int tt = 0;
                for (int v = 1; v < fnGraph.V()-1; v++)
                    if (maxflow.inCut(v)){a_cut[tt] = v-1; tt++;}
                if (a_cut.length != 0) cuts.add(a_cut);
                //showCuts();
                break;
            }else //if (maxflow.value() == n/2) n is even and maxfolw.value()==n/2
            {
                //compute a perfect matching
                ArrayList<GraphEdge> ges = new ArrayList<>(n/2);
                int[] S = cuts.get(cuts.size()-1);
                int GV = G.V();
                int[] S_bar = new int[GV- S.length];
                int j = 0;
                for (int i = 0; i<GV; i++){
                    if (!inSet(i, S)){ S_bar[j] = i; j++; }
                }                
                for (int i = 0; i<S.length;i++){
                    for (FlowEdge e: fnGraph.adj(S[i]+1)){
                        if ((e.flow()>0) && (inSet(e.to()-1, S_bar))){
                            GraphEdge ge = new GraphEdge(e.from()-1, e.to()-1,0);
                            ges.add(ge);
                        }
                    }    
                }
                perfectMatching pm = new perfectMatching(fnGraph.V()-2, ges);
                //put the perfect matching pm in M
                M.add(pm);
                int[] new_biSection = findBisection();
                if (inCuts(new_biSection)) break;                
                cuts.add(new_biSection);
                //showCuts();
            }    
        }
    }            
    public ArrayList<int[]> getCuts(){
        return cuts;
    }
    public ArrayList<perfectMatching> getPMs(){
        return this.M;
    }    
    public int[] getLastCut(){
        int n = cuts.size();
        if (n == 0) return null;
        return cuts.get(n-1);
    }
    public void showLastCut(){
        int[] part1 = getLastCut();
        int GV = G.V();
        int[] part2 = new int[G.V()- part1.length];
        int j = 0;
        for (int i = 0; i<GV; i++){
            if (!inSet(i, part1)){
                part2[j] = i; j++;
            }
        }
        if (part1 == null){
            
            System.out.println("\nNo Cut :(");
        }else{           
            int i = 0;
            int part1Edges = 0; int part2Edges = 0, crossingEdges = 0;
            while (i<G.Edges().size()){
                GraphEdge e = G.Edges().get(i);
                int from = e.from(); int to = e.to();
                if (inSet(from, part1)){
                    if (inSet(to,part1)) part1Edges ++;
                    else crossingEdges++;
                }
                else {if (inSet(to, part2)) part2Edges++;
                      else crossingEdges++;
                }      
                i++;
            }
            System.out.println("\nThe Graph: " + String.valueOf(G.V()) +"   " +  String.valueOf(G.Edges().size()) );
            System.out.println("The last (min) cut: ");        
            System.out.printf("%20s%20s%20s", "Part1", "Part2", "Crossing\n");
            System.out.printf("%12s%8d%20d%20d", "#Vertices:",part1.length,part2.length, 0);
            System.out.println("");
            System.out.printf("%12s%8d%20d%20d","#Edges:",part1Edges,part2Edges,crossingEdges);
            System.out.println("\n The cut: ");
            
            for (int ii = 0; ii<part1.length;ii++){
                
                System.out.print(String.valueOf(part1[ii]) + "  ");
            }
            
        }                    
    }
    public int[] getBestBalanceCut(){
        int n = cuts.size();
        int[] ret_val;
        if (n == 0) return null;
        if (n == 1) ret_val = cuts.get(0);
        else
            if (n%2 == 0){
                if (cuts.get(n-1).length == (int)(G.V()/2)) return cuts.get(n-1);
                else ret_val = cuts.get(n-2);}
            else{
                ret_val = cuts.get(n-1);
            }
        return ret_val;
    }
    public void showBestBalanceCut(){
        int[] part1 = getBestBalanceCut();
        int GV = G.V();
        int[] part2 = new int[G.V()- part1.length];
        int j = 0;
        for (int i = 0; i<GV; i++){
            if (!inSet(i, part1)){
                part2[j] = i; j++;
            }
        }
        if (part1 == null){
            
            System.out.println("\nNo Best Balanced Cut :(");
        }else{           
            int i = 0;
            int part1Edges = 0; int part2Edges = 0, crossingEdges = 0;
            while (i<G.Edges().size()){
                GraphEdge e = G.Edges().get(i);
                int from = e.from(); int to = e.to();
                if (inSet(from, part1)){
                    if (inSet(to,part1)) part1Edges ++;
                    else crossingEdges++;
                }
                else {if (inSet(to, part2)) part2Edges++;
                      else crossingEdges++;
                }      
                i++;
            }
            System.out.println("\nThe Graph: " + String.valueOf(G.V()) +"   " +  String.valueOf(G.Edges().size()) );
            System.out.println("The best balanced cut: ");        
            System.out.printf("%20s%20s%20s", "Part1", "Part2", "Crossing\n");
            System.out.printf("%12s%8d%20d%20d", "#Vertices:",part1.length,part2.length, 0);
            System.out.println("");
            System.out.printf("%12s%8d%20d%20d","#Edges:",part1Edges,part2Edges,crossingEdges);
            System.out.println("\n The cut:");
            
            for (int ii = 0; ii<part1.length;ii++){
                
                System.out.print(String.valueOf(part1[ii]) + "  ");
            }
            
        }                    
    }
    public void showPMs(){
        int n = M.size();
        System.out.print("\nThere are " + String.valueOf(n) + " perfect matchings obtained");
        for (int i = 0; i<n;i++) System.out.println("\n PM " + String.valueOf(i) + ":\n" + M.get(i));
    }
    public void showCuts(){
        int n = cuts.size();
        System.out.print("\nThere are " + String.valueOf(n) + " cuts obtained");
        for (int i = 0; i<n; i++){
            System.out.print("\n Cut " + String.valueOf(i) + ": ");
            for (int j = 0; j < cuts.get(i).length; j++)
                System.out.print(cuts.get(i)[j] + "  ");
        }
            
    }
    private int[] getRandomCut() {
        int n = G.V(); int k = n/2;
        int[] ret_val = new int[k];
        //for (int i = 0; i<n; i++) {ret_val[i] = j;j+=2;}
        //return ret_val;
        
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i<G.V()-1; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        for (int i=0; i<k; i++) {
            ret_val[i] = list.get(i);
        }
        return ret_val;
    }
    
//Unit test
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
    //    String fname = "data/unweighted_graph_lucky_puck.inp"; //right
        //String fname = "data/uw_test1.inp"; //right
        //String fname = "data/uw_test2_pm.inp"; //right
//        String fname = "data/uw_test3_pm.inp"; //right
//        String fname = "data/uw_test4_cycle.inp"; //right
//        String fname = "data/undirected.inp"; //right
      //  String fname = "data/undirected2.inp"; //right
//      String fname = "data/mesh_4x4.inp"; //right
    //String fname = "data/mesh_5x5.inp"; //right
//        String fname = "data/3_vertices.inp"; //right
        String fname = "data/graph_10_vertices.inp"; //right
        

     //   UW_Graph G = new UW_Graph(fname); // this constructor for an input data file NOT come from METIS data
//        System.out.println(G);        
//        GP gp = new GP(G, 0.5);
//        gp.findCuts();
//        gp.showCuts();
 //       gp.showBestBalanceCut();
  //      gp.showPMs();        

        //String src_fname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform.txt"; //right
       // String des_fname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform-converted.txt"; //right
        //String src_fname = "data/need_converting/graph/graph-fb-r-w-degree-e-constant.txt"; //right
        //String des_fname = "data/need_converting/graph/graph-fb-r-w-degree-e-constant-converted.txt"; //right       
        //String src_fname = "data/need_converting/graph/graph-fb-r-w-degree-e-uniform.txt"; //right
        //Sring des_fname = "data/need_converting/graph/graph-fb-r-w-degree-e-uniform-converted.txt";//right
        String des_fname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform-converted.txt"; //right       
                
        UW_Graph G = new UW_Graph();
        //G.convertGraph(src_fname, des_fname);
        G = new UW_Graph(des_fname, true);
//        UW_Graph G = new UW_Graph(des_fname, true);
        //System.out.println(G);        
        //G = new UW_Graph(des_fname, true); // this constructor is for an input data file converted from METIS data file
        GP gp = new GP(G, 0.5); //alpha =0.5, it should be < 1
        gp.findCuts();
        gp.showCuts();
        gp.showBestBalanceCut();
        System.out.println();
        gp.showLastCut();
        gp.showPMs();  
    }        
}

