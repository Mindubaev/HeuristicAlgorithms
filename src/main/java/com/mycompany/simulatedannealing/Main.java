/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simulatedannealing;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Artur
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Function function=new Function();
        function.setB(0);
        function.addVarK("x1", new double[]{4,1});
        function.addVarK("x2", new double[]{-8,1.5});
        function.addСompositeK(new double[]{-2,0}, "x1","x2");

        Map<String,Double> point=new HashMap<>();
        point.put("x1", Double.valueOf(500));
        point.put("x2", Double.valueOf(200));
        
        Swarm swarm=new Swarm(function, 100, 10, point, 1000);
        
//        Function.Decision dec=function.simulatedAnnealingRandomPointLong(point, 1000, 100, 20);//метод отжига
//        System.out.println("Result:\n"+dec.toString());//метод отжига
        System.out.println("Result:\n"+swarm.minimaze().toString());//метод улия
    }
    
}
