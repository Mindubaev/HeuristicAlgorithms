/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.heuristicalgorithms;

import java.util.HashMap;
import java.util.Map;
import javax.print.attribute.standard.Destination;

/**
 *
 * @author Artur
 */
public class Bee {
    
    private Map<String,Double> currentPos;
    private Map<String,Double> destinationPos;
    private Map<String,Double> vector;
    private double bestOfSelf;
    private Map<String,Double> bestOfSelfPos;
    private double self;
    private double swarm;
    private Function func;
    private int numOfStep;

    public Bee(Map<String, Double> currentPos, double bestOfSelf, Map<String, Double> bestOfSelfPos, double self, double swarm, Function func, int numOfStep) {
        this.currentPos = currentPos;
        this.bestOfSelf = bestOfSelf;
        this.bestOfSelfPos = bestOfSelfPos;
        this.self = self;
        this.swarm = swarm;
        this.func = func;
        this.numOfStep = numOfStep;
    }

    public Bee(Map<String, Double> currentPos, Map<String, Double> destinationPos, Map<String, Double> vector, double bestOfSelf, Map<String, Double> bestOfSelfPos, double self, double swarm, Function func, int numOfStep) {
        this.currentPos = currentPos;
        this.destinationPos = destinationPos;
        this.vector = vector;
        this.bestOfSelf = bestOfSelf;
        this.bestOfSelfPos = bestOfSelfPos;
        this.self = self;
        this.swarm = swarm;
        this.func = func;
        this.numOfStep = numOfStep;
    }
    
    public void findPath(Map<String,Double> bestOfSwarmPos,double bestOfSwarm,double range){
        Map<String,Double> destimation=null;
        if (bestOfSwarm!=getBestOfSelf()){
            destimation=findDestination(getBestOfSelfPos(), bestOfSwarmPos);
            setDestinationPos(destimation);
            setVector(findVectorTo(destimation,getNumOfStep()));
        }else{           
            destimation=randomizeClosePos(getBestOfSelfPos(), range/1000);
            setDestinationPos(destimation);
            setVector(findVectorTo(destimation,1));
        }
    }
    
    private Map<String,Double> randomizeClosePos(Map<String,Double> startPos,double range){
        Map<String,Double> pos=new HashMap();
        for (String varName:startPos.keySet()){
            int sign=1;
            if(Math.random()<=0.5)
                sign=-1;
            double delat=range*Math.random();
            pos.put(varName, startPos.get(varName)+delat*sign);
        }
        return pos;
    }
    
    private Map<String,Double> findDestination(Map<String,Double> bestOfSelfPos,Map<String,Double> bestOfSwarmPos){
        Map<String,Double> destimation=new HashMap();
            for (String varName:bestOfSelfPos.keySet()){
                double bestOfSwarmVal=bestOfSwarmPos.get(varName);
                double bestOfSelfVal=bestOfSelfPos.get(varName);
                destimation.put(varName, (bestOfSwarmVal-bestOfSelfVal)*getSelf()+bestOfSelfVal);
            }
        return destimation; 
    }
    
    private Map<String,Double> findVectorTo(Map<String,Double> destimation,int numOfStep){
        Map<String,Double> newVector=new HashMap();
        for (String varName:destimation.keySet()){
            double dest=destimation.get(varName);
            double current=getCurrentPos().get(varName);
            newVector.put(varName,(dest-current)/numOfStep);
        }
        return newVector;
    }
    
    public void stepToDestination() throws Exception{
        if (!arrived()){
            for (String varName:getCurrentPos().keySet()){
                getCurrentPos().put(varName, getCurrentPos().get(varName)+getVector().get(varName));
            }
        }
        double f=getFunc().getValue(getCurrentPos());
        if (f<this.bestOfSelf){
            this.setBestOfSelf(f);
            this.setBestOfSelfPos(new HashMap<String,Double>(getCurrentPos()));
        }
    }
    
    public boolean arrived(){
        for (String varName:getCurrentPos().keySet()){
            if (Math.abs(getCurrentPos().get(varName)-getDestinationPos().get(varName))>0.00001)
                return false;
        }
        return true;
    } 

    public Map<String,Double> getCurrentPos() {
        return currentPos;
    }

    public Map<String,Double> getDestinationPos() {
        return destinationPos;
    }

    public Map<String,Double> getVector() {
        return vector;
    }

    public double getBestOfSelf() {
        return bestOfSelf;
    }

    public Map<String,Double> getBestOfSelfPos() {
        return bestOfSelfPos;
    }

    public double getSelf() {
        return self;
    }

    public double getSwarm() {
        return swarm;
    }

    public Function getFunc() {
        return func;
    }

    public int getNumOfStep() {
        return numOfStep;
    }

    public void setCurrentPos(Map<String,Double> currentPos) {
        this.currentPos = currentPos;
    }

    public void setDestinationPos(Map<String,Double> destinationPos) {
        this.destinationPos = destinationPos;
    }

    public void setVector(Map<String,Double> vector) {
        this.vector = vector;
    }

    public void setBestOfSelf(double bestOfSelf) {
        this.bestOfSelf = bestOfSelf;
    }

    public void setBestOfSelfPos(Map<String,Double> bestOfSelfPos) {
        this.bestOfSelfPos = bestOfSelfPos;
    }

    public void setSelf(double self) {
        this.self = self;
    }

    public void setSwarm(double swarm) {
        this.swarm = swarm;
    }

    public void setFunc(Function func) {
        this.func = func;
    }

    public void setNumOfStep(int numOfStep) {
        this.numOfStep = numOfStep;
    }
     
}
