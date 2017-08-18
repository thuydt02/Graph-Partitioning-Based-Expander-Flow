/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GP_EF;

/**
 *
 * @author Do Thanh Thuy
 */
class GraphEdge {
    private final int from;
    private final int to;
    private final double cap;
    public GraphEdge(int from, int to, double capacity){
        this.from = from; this.to = to; this.cap = capacity;
    }
    public int from(){
        return this.from;
    }
    public int to(){
        return this.to;
    }
    public double cap(){
        return this.cap;
    }
}
