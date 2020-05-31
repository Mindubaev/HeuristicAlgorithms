/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.simulatedannealing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Artur
 */
public class Swarm {
    
    private List<Bee> bees;
    private Map<String,Double> bestOfSwarmPos;
    private double bestOfSwam;
    private Function function;
    private int numOfStep;
    private double range;

    public Swarm(Function function,int numOfBee,int numOfStep,Map<String,Double> startPos,double range) throws Exception {
        this.bees=new ArrayList();
        this.function = function;
        this.numOfStep=numOfStep;
        this.range=range;
        for (int i=0;i<numOfBee;i++){
            Map<String,Double> pos=generateRandomPos(startPos, range);
            Map<String,Double> dest=generateRandomPos(startPos, range*100);
            Bee bee=new Bee(pos,getFunction().getValue(pos),new HashMap<String, Double>(pos),1,0,getFunction(),numOfStep);
            bee.findPath(dest, getFunction().getValue(dest),range);
            getBees().add(bee);
        }
        if (numOfBee!=0){
            setBestOfSwam(getBees().get(0).getBestOfSelf());
            setBestOfSwarmPos(new HashMap<String,Double>(getBees().get(0).getBestOfSelfPos()));
        }
        for (Bee bee:getBees()){
            for (int i=0;i<getNumOfStep();i++)
                bee.stepToDestination();
            if (bee.getBestOfSelf()<getBestOfSwam()){
                setBestOfSwam(bee.getBestOfSelf());
                setBestOfSwarmPos(new HashMap<String,Double>(bee.getBestOfSelfPos()));
            }
        }
    }
    
    public Function.Decision minimaze() throws Exception{
        int counter=0;
        double loyalty=0;
        boolean allBeesStoped=false;
        while (!allBeesStoped){
            counter++;
            if (counter%(numOfStep-1)==0 && (numOfStep-1)*8>counter){
                loyalty+=0.1;
                for (Bee bee:getBees()){
                    bee.setSelf(1-loyalty);
                    bee.setSwarm(loyalty);
                }
            }
            allBeesStoped=true;
            for (Bee bee:getBees()){
                bee.findPath(getBestOfSwarmPos(), getBestOfSwam(),getRange());
                bee.stepToDestination();
                if (!bee.arrived())
                    allBeesStoped=false;
                if (getBestOfSwam()>bee.getBestOfSelf()){
                    setBestOfSwam(bee.getBestOfSelf());
                    setBestOfSwarmPos(new HashMap<String,Double>(bee.getBestOfSelfPos()));
                }
                if (this.bestOfSwam==0)//test
                    System.out.println("");
            }
            System.out.println(this.bestOfSwam);
        }
        return new Function.Decision(getBestOfSwarmPos(),getFunction().getValue(getBestOfSwarmPos()));
    }
    
    private Map<String,Double> generateRandomVector(Map<String,Double> startPos,double range){
        Map<String,Double> v=new HashMap();
        for (String varName:startPos.keySet()){
            int sign=1;
            if(Math.random()<=0.5)
                sign=-1;
            double delat=range*Math.random();
            v.put(varName, delat);
        }
        return v;
    }
    
    private Map<String,Double> generateRandomPos(Map<String,Double> startPos,double range){
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

    public List<Bee> getBees() {
        return bees;
    }

    public Map<String,Double> getBestOfSwarmPos() {
        return bestOfSwarmPos;
    }

    public double getBestOfSwam() {
        return bestOfSwam;
    }

    public Function getFunction() {
        return function;
    }

    public int getNumOfStep() {
        return numOfStep;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setNumOfStep(int numOfStep) {
        this.numOfStep = numOfStep;
    }

    public void setBees(List<Bee> bees) {
        this.bees = bees;
    }

    public void setBestOfSwarmPos(Map<String,Double> bestOfSwarmPos) {
        this.bestOfSwarmPos = bestOfSwarmPos;
    }

    public void setBestOfSwam(double bestOfSwam) {
        this.bestOfSwam = bestOfSwam;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
    
}
