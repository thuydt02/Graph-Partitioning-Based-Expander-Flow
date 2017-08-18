  @author Thuy Do
  06/2017
The code is based on the paper: https://people.eecs.berkeley.edu/~vazirani/pubs/arvcacm.pdf
"Geometry, Flows and Graph-Partitioning Algorithms"
This code follow the expander flow approach, for geometry approach I did in matlab

Given a graph G(V,E) (G is unweighted and directed) and an expansion parameter alpha
This class is to partition G
  such that
  + V = S U S_bar, S is called the cut
  + and phi(S) <= alpha
  where phi(S) = min (E(S, S_bar)/|S|) |S| <= (the number of vertices of G)/2. S is a subset in V
  The algorithm to do the cut belongs to the papers "Graph partitioning using single commodity flows" (ACM 2006)
  and "Geometry, Flows and Graph Partitioning" (ACM 2008)
  it is based on the expander flow approach.
  Dependencies: FlowNetwork.java, FoldFukerson.java, UW_graph.java, perfectMatching.java
  Note: the vertices of a graph are ordered from 0
  Note: you should run the class in several times to get the better cut as you want depending on your time you have because there are some random generations of cuts and vector
  if you want to run the class with an undirected graph, you should specify an edge between 2 vertices like this
  for example you have a link between vertex 0 and vertex 1, you should add 2 edges (0,1) and (1,0) in the input file
  so the GP will count 2 edges for the link => the output should be divided by 2 for the number of edges
  for example, with the follow input file, you get a best balanced cut with the cut = {3  6  2  1  7}
  and the number of edges crossing the cut is 4 => you should actually have 4/2 = 2 edges crossing the cut
  similarly,
  and the number of edges in part 1 is 12 => it should be 12/2 = 6 edges
  and the number of edges in part 2 is 12 => it should be 12/2 = 6 edges
  Input file content:
 10
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

 output

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
 
 You can run the class with METIS data, METIS ordered vertices of a graph from 1
 So the class will reduce the vertex order in METIS by 1.
 For example, vertex 1 in METIS => vertex 0 in GP
              vertex 2 in METIS => vertex 1 in GP, ....
 For working with METIS data, you should call the class like the follow
 String src_fname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform.txt"; // the METIS data file
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
