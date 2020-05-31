/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simulatedannealing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

/**
 *
 * @author Artur
 */
public class Function {

    private Map<String,double[]> k;
    private Map<Set<String>,double[]> compositeK=new HashMap<>();
    private double b;

    public Function(double b,Map<String,double[]> k,Map<Set<String>,double[]> compositeK) {
        this.b = b;
        this.k=k;
        this.compositeK=compositeK;
    }

    public Function() {
        this.k=new HashMap<>();
    }
    
    public Function.Decision simulatedAnnealing(Map<String,Double> point,double range,double t, double tStep){
        int counter=1;
        try{
            double maxt=t;
            while(t>0){
                Map<String,Double> lb=findLeftBorder(point, range);
                double delta=range*2/100;
                Map<String,Double> minNextPoint=lb;
                double minF=getValue(minNextPoint);
                for (int i=1;i<=100;i++){
                    Map<String,Double> nextPoint=nextPoint(i,delta,lb);
                    double f=getValue(nextPoint);
                    if (minF>f){
                        minNextPoint=nextPoint;
                        minF=f;
                    }
                }
                System.out.println("iter:"+counter);
                if (isNextPoint(point, minNextPoint, t)){
                        point=minNextPoint;
                        t=t-tStep;
                        range=range*t/maxt;
                }
                else
                    break;
                System.out.println(toString(point));
                counter++;
            }
            return new Decision(point,getValue(point));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private Map<String,Double> nextPoint(int order,double delta,Map<String,Double> lb){
        Map<String,Double> newPoint=new HashMap();
        for (String varName:lb.keySet())
            newPoint.put(varName, lb.get(varName)+order*delta);
        return newPoint;
    }
    
        public String toString(Map<String,Double> map) throws Exception {
            String str="";
            for (String varName:map.keySet())
                str+=varName+"="+map.get(varName)+"\n";
            str+="Func="+getValue(map);
            return str;
        }
    
    public Function.Decision simulatedAnnealingRandomPointFast(Map<String,Double> point,double range,double t, double tStep){
        try{
            double maxt=t;
            while(t>0){
                Map<String,Double> lb=findLeftBorder(point, range);
                boolean find=false;
                for (int i=1;i<=10000;i++){
                    Map<String,Double> nextPoint=nextPointRandomGen(lb, range);
                    if (isNextPoint(point, nextPoint, t)){
                        point=nextPoint;
                        t=t-tStep;
                        range=range*t/maxt;
                        find=true;
                        break;
                    }
                }
                if (!find)
                    return new Decision(point, getValue(point));
            }
            return new Decision(point,getValue(point));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public Function.Decision simulatedAnnealingRandomPointLong(Map<String,Double> point,double range,double t, double tStep){
        int counter=1;
        try{
            double maxt=t;
            while(t>0){
                Map<String,Double> lb=findLeftBorder(point, range);
                Map<String,Double> minNextPoint=lb;
                double minF=getValue(minNextPoint);
                long iters=Math.round(range);//!
                for (long i=1;i<=iters*point.size();i++){
                    Map<String,Double> nextPoint=nextPointRandomGenV2(point, range);
                    double f=getValue(nextPoint);
                    if (minF>f){
                        minNextPoint=nextPoint;
                        minF=f;
                    }
                }
                System.out.println("iter:"+counter);
                if (isNextPoint(point, minNextPoint, t)){
                        point=minNextPoint;
                        t=t-tStep;
                        range=range*t/maxt;
                }
                else
                    break;
                System.out.println(toString(point));
                counter++;
            }
            return new Decision(point,getValue(point));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private Map<String,Double> findLeftBorder(Map<String,Double> point,double range){
        Map<String,Double> lb=new HashMap();
        for (String varName:point.keySet())
            lb.put(varName, point.get(varName)-range);
        return lb;
    }
    
    private Map<String,Double> nextPointRandomGen(Map<String,Double> lb,double range){
        Map<String,Double> newPoint=new HashMap();
        for (String varName:lb.keySet())
            newPoint.put(varName, lb.get(varName)+round(Math.random()*range*2));
        return newPoint;
    }
    
    private Map<String,Double> nextPointRandomGenV2(Map<String,Double> startPoint,double range){
        Map<String,Double> newPoint=new HashMap();
        for (String varName:startPoint.keySet()){
            double sign=1;
            if (Math.random()>0.5)
                sign=-1;
            newPoint.put(varName, startPoint.get(varName)+round(Math.random()*range*sign));
        }
        return newPoint;
    }
    
    private boolean isNextPoint(Map<String,Double> oldPoint,Map<String,Double> newPoint,double t){
        try {
            double oldF=getValue(oldPoint);
            double newF=getValue(newPoint);
            if (newF<oldF)
                return true;
            if (Math.random()<=Math.pow(Math.E,-((newF-oldF)/t)))
                return true;
            return false;
        } catch (Exception ex) {
            Logger.getLogger(Function.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }  
    }
    
    public void addСompositeK(double[] k,String...varNames){
        Set<String> vars=new HashSet<>();
        for (String varName:varNames)
            vars.add(varName);
        this.compositeK.put(vars, k);
    }
    
    public void addVarK(String varName,double[] k){
        this.getK().put(varName, k);
    }
    
    public double getDerivative(String varName,Map<String,Double> point) throws Exception{
        double[] varK=this.getK().get(varName);
        
        int maxSizeOfk=1;//проставляем индексы и выяснеяем количество переменных
        Map<String,Integer> indexes=new HashMap<>();
        indexes.put(varName, Integer.valueOf(0));
        int indexCounter=1;
        for (Set<String> key:this.compositeK.keySet()){
            if(key.contains(varName)){
                if (key.size()>maxSizeOfk)
                    maxSizeOfk=key.size();
                for (String var:key){
                    if (!indexes.containsKey(var)){
                        indexes.put(var, indexCounter);
                        indexCounter++;
                    }
                }
            }
        }
        
        if (varK==null)
            throw new Exception("unexpected var name");
        DerivativeStructure deriv=new DerivativeStructure(maxSizeOfk, 1);
        DerivativeStructure dPart=new DerivativeStructure(maxSizeOfk,1,indexes.get(varName),point.get(varName));
        for (int i=0;i<varK.length;i++){
            deriv=deriv.add(dPart.pow(i+1).multiply(varK[i]));
        }
        for (Set<String> key:this.compositeK.keySet()){
            if(key.contains(varName)){
                dPart=new DerivativeStructure(maxSizeOfk, 1,indexes.get(varName),point.get(varName));
                for (String val:key){
                    if (!val.equals(varName))
                        dPart=dPart.multiply(new DerivativeStructure(maxSizeOfk, 1, indexes.get(val),point.get(val)));
                }
            }
            for (int i=0;i<this.compositeK.get(key).length;i++){
                deriv=deriv.add(dPart.pow(i+1).multiply(this.compositeK.get(key)[i]));
            }
        }
        deriv=deriv.add(this.b);
        
        int[] derivOrder= new int[maxSizeOfk];
        for (int i=0;i<maxSizeOfk;i++){
            derivOrder[i]=(i==0)?1:0;
        }
        
        return deriv.getPartialDerivative(derivOrder);
    }
    
    public double getValue(Map<String,Double> point) throws Exception{
        double res=this.b;
        if (point.size()!=this.getK().size())
            throw new Exception("num of vars doesnt match");
        res+=getValOfSimplexK(point);
        res+=getValOfCompositeK(point);
        return res;
    }
    
    private double round(double d){
        double scale = Math.pow(10, 5);
        return Math.round(d * scale) / scale;
    }
    
    public double getValOfSimplexK(Map<String,Double> point){
        double res=0;
        for (String varName:this.getK().keySet()){
            double val=round(point.get(varName));
            double[] varK=getK().get(varName);
            for (int i=0;i<varK.length;i++){
                res+=round(Math.pow(val, i+1)*varK[i]);
            }
        }
        return res;
    }
    
    public double getValOfCompositeK(Map<String,Double> point){
        double res=0;
        for (Set<String> nameSet:this.compositeK.keySet()){
            double m=1;
            for (String name:nameSet)
                m=round(m)*round(point.get(name));
            double[] currentK=this.compositeK.get(nameSet);
            for (int i=0;i<currentK.length;i++){
                res+=round(Math.pow(m,i+1)*currentK[i]);
            }
        }
        return res;
    }
    
    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }
    
    public boolean varIsExist(String varName){
        return this.k.get(varName)!=null;
    }

    public Map<String,double[]> getK() {
        return k;
    }

    public void setK(Map<String,double[]> k) {
        this.k = k;
    }
    
    public static class Decision{
        
        private Map<String,Double> solution;
        private double funcVal;

        public Decision(Map<String, Double> solution, double funcVal) {
            this.solution = solution;
            this.funcVal = funcVal;
        }

        @Override
        public String toString() {
            String str="";
            for (String varName:getSolution().keySet())
                str+=varName+"="+getSolution().get(varName)+"\n";
            str+="Func="+getFuncVal();
            return str;
        }
        
        public Map<String,Double> getSolution() {
            return solution;
        }

        public double getFuncVal() {
            return funcVal;
        }

        public void setSolution(Map<String,Double> solution) {
            this.solution = solution;
        }

        public void setFuncVal(double funcVal) {
            this.funcVal = funcVal;
        }
        
    }
    
}
