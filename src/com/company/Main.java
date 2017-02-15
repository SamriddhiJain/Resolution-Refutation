package com.company;

import javafx.util.Pair;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws JDOMException, IOException {

        System.out.print("Please enter the KB filePath: ");
        Scanner sc = new Scanner(System.in);
        String fName = sc.next();

        File inputFile = new File(fName);
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element root = document.getRootElement();
        KB newKB = new KB();
        List<Element> l1 = newKB.readKB(root);
        List<Element> origKB = new ArrayList<>();
        for(int i=0;i<l1.size();i++){
            origKB.add(l1.get(i).clone());
        }

        System.out.println();
        System.out.println("Enter the resolution type and strategy: ");
        System.out.println("Resolution Types: ");
        System.out.println("Resolution Refutation (1)");
        System.out.println("Query Answering (2)");
        int resType = sc.nextInt();

        System.out.println("Resolution Strategy: ");
        System.out.println("Brute Force Matching (1)");
        System.out.println("Set of Support (2)");
        System.out.println("Unit Resolution (3)");
        System.out.println("Input Strategy (4)");
        int resStrategy = sc.nextInt();
        List<Pair<Pair<String, String>, String>> fList = new ArrayList<>();

        if(resType==1){//null clause found
            ResStrategies strategy = new ResStrategies();

            if(resStrategy==1){
                fList = strategy.forwardChaining(l1);
            }else if(resStrategy==2){
                Element e = l1.get(l1.size()-1).clone();
                l1.remove(l1.size()-1);
                fList = strategy.setOfSupportStrategy(l1,e);
            }else if(resStrategy==3){
                fList = strategy.unitResolution(l1);
            }else if(resStrategy==4){
                fList = strategy.inputStrategy(l1);
            }else{
                System.out.println("Please enter a valid resolution strategy");
                return;
            }

            Visualisation v1 = new Visualisation();
            v1.visualise(origKB,fList);

        }else if(resType==2){//answer predicate
            AnsStrategies strategy = new AnsStrategies();

            if(resStrategy==1){
                strategy.forwardChaining(l1);
            }else if(resStrategy==2){
                Element e = l1.get(l1.size()-1).clone();
                l1.remove(l1.size()-1);
                strategy.setOfSupportStrategy(l1,e);
            }else if(resStrategy==3){
                strategy.unitResolution(l1);
            }else if(resStrategy==4){
                strategy.inputStrategy(l1);
            }else{
                System.out.println("Please enter a valid resolution strategy");
            }
        }else{
            System.out.println("Please enter a valid resolution type");
        }

//
//
//        ResStrategies str = new ResStrategies();
//        str.unitResolution(l1);
//        System.out.println(l1.get(0).equals(l1.get(0).clone()));


//        Resolution res = new Resolution(l1.get(0),l1.get(1));
//        System.out.println(res.resolve());
//        res.printElement(res.getFinalResolved());
//        System.out.println(res.finalHashMap.toString());

//        Unification u1 = new Unification();
//        u1.unification("file1.txt","file2.txt");
//        System.out.println(u1.map.toString());
//        Resolution res1 = new Resolution(l1.get(4),l1.get(1));
//        System.out.println(res1.resolve());
//
//        Element e = l1.get(l1.size()-1).clone();
//        l1.remove(l1.size()-1);
//        ResStrategies str = new ResStrategies();
//        str.setOfSupportStrategy(l1,e);


//        Element e = l1.get(l1.size()-1).clone();
//        l1.remove(l1.size()-1);
//        AnsStrategies str = new AnsStrategies();
//        str.setOfSupportStrategy(l1,e);
//        AnsStrategies str = new AnsStrategies();
//        str.inputStrategy(l1);
//        System.out.println(str.forwardChaining(l1));



    }
}
