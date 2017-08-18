/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GP_EF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author thuydt
 */
public class draft {
    public static void main(String[] args){
        double[] r; int n = 8;
        Random rnd = new Random() ;
        r = new double[n];
        ArrayList<Double> list = new ArrayList<Double>();
        int i = 0;
        while (i< n){
            double tmp = rnd.nextDouble();
            list.add(new Double(tmp));
            list.add(new Double(tmp*(-1))); i+=2;            
        }
        
        //Collections.shuffle(list);
                    
        if (n % 2 == 1) {
            int k = n - 1;
            for (i = 0; i < k ; i++){
                double tmp = list.get(i).doubleValue() - list.get(k).doubleValue()/k;
                list.set(i, new Double(tmp));
            }
            int last = list.size() - 1;
            list.remove(last);
            //list.set(last, null);
        }
        double norm_r = 0;
        for (i = 0; i<=n - 1;i++){
            norm_r = norm_r + Math.pow(list.get(i).doubleValue(),2);
        }
        norm_r = Math.pow(norm_r, 0.5);
        Collections.shuffle(list);
        for (i = 0; i<= n-1; i++){
            r[i] = list.get(i).doubleValue()/norm_r;            
            System.out.printf("%10.3f", r[i]);
        }
    }
    
}
