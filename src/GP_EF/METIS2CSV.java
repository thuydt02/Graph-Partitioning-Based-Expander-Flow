/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GP_EF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Thuy Do
 * This class is to convert a graph in the METIS file into an equivalent graph in the csv file of an adjacent matrix
 */
public class METIS2CSV {
    String fname_in, fname_out;
    METIS2CSV(String srcFname, String desFname) throws FileNotFoundException, IOException{
        fname_in = srcFname; fname_out = desFname;
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
            String fmt = headers[2].trim();
            int i = 0;int num_vertices = Integer.valueOf(headers[0]);
            while (scanner.hasNextLine()){
                String edge = scanner.nextLine();
                String[] edges = edge.split(" ");
                String written_st = "";
                if (fmt.equalsIgnoreCase("1")){ //weight associated with edges
                    int j = 0;
                    while (j < edges.length){
                        written_st = written_st + String.valueOf(Integer.parseInt(edges[j])) + " " ; j += 2;
                    //    adjMat[i][Integer.parseInt(edges[j])] = true; j = j+2;
                    }
                }else if (fmt.equalsIgnoreCase("10")){ //weight associated with vertices
                    int j = 1;
                    while (j < edges.length){
                        written_st = written_st + String.valueOf(Integer.parseInt(edges[j])) + " " ; j ++;
                        //adjMat[i][Integer.parseInt(edges[j])] = true; j = j++;
                    }
                }else if (fmt.equalsIgnoreCase("011") || (fmt.equalsIgnoreCase("11"))){ // weight associated with both
                    int j = 1;
                    while (j < edges.length){
                        written_st = written_st + String.valueOf(Integer.parseInt(edges[j])) + " " ; j += 2;
                        //adjMat[i][Integer.parseInt(edges[j])] = true; j = j+2;
                    }
                }
                //writer.write(written_st + "\n");            
                String[] indexs_st = written_st.split(" ");
                int[] indexs = new int[indexs_st.length];
                for (int k = 0; k<indexs_st.length; k++){
                    indexs[k] = Integer.parseInt(indexs_st[k]);
                }
                indexs = arr_sort(indexs);
                
                int end = indexs[0];int start = 1;int t = 0;
                int k=0;String to_write = ""; boolean found = false;
                while (t<indexs.length){                    
                    to_write = "";
                    for (k = start; k<end; k++){
                        //to_write = to_write + "0,";
                        writer.write("0,");}
                    if (k == num_vertices) {//to_write = to_write + "1";
                        writer.write("1");
                        found = true;}
                    else
                    if (k == end) {//to_write = to_write + "1,";
                        writer.write("1,");
                    }
                    //writer.write(to_write);
                    
                    start = end + 1; t = t + 1;
                    if ((found) || (t >= indexs.length)) break;
                    end = indexs[t];
//writer.write("0");
                    
                }
                if (found == false){                    
                    to_write = "";
                    for (k = indexs[indexs.length-1] + 1; k<num_vertices; k++)
                    {   // to_write = to_write + "0,";
                        writer.write("0,");
                    }
                    //to_write = to_write.substring(0, to_write.length()-2);
                    //writer.write(to_write + "\n");
                    writer.write("0\n");
                }else
                    writer.write("\n");
                
             i++;   
            }                        
        }                        
        writer.close();scanner.close();
    }

    private int[] arr_sort(int[] indexs_) {                
        int n = indexs_.length;
        for (int i = 0; i<n-1; i++){
            int min = indexs_[i];int ind_min = i;        
            for (int j=i+1;j<n; j++){
                if (min > indexs_[j]){
                    min = indexs_[j]; ind_min = j;
                }
            }
            int tmp = indexs_[i]; indexs_[i] = min;indexs_[ind_min] = tmp;
        }
        return indexs_;
    }
    //unit test

    public static void main(String[] args) throws IOException{
        String srcFname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform.txt";
        String desFname = "data/need_converting/graph/graph-dblp-r-w-degree-e-uniform_converted.csv";
        //String srcFname = "data/need_converting/graph/metis_011_5_vertices.txt";
        //String desFname = "data/need_converting/graph/metis_011_5_vertices_adjMat.csv";
        METIS2CSV cvf = new METIS2CSV(srcFname, desFname);
    }
        
    
}



//for (i = 0; i<num_vertices; i++)
            //{
            //    String st = "";
            //    for (int j = 0; j<num_vertices; j++){
            //        st = st + String.valueOf(adjMat[i][j]) + ",";
            //    }
            //    int last = st.length()-2;
            //    writer.write(st.substring(0, last)+"\n");
//writer.write(headers[0] + " " + headers[1] + "\n");
            //int num_vertices = Integer.parseInt(headers[0]);
            //int[][] adjMat = new int[num_vertices][num_vertices];
            //boolean[][] adjMat = new boolean[num_vertices][num_vertices];
            //for (int ii = 0; ii<num_vertices; ii++)
            //    for (int jj = 0; jj<num_vertices; jj++){
            //        adjMat[ii][jj] = false;
            //    }
            