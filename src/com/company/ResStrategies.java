package com.company;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by riddle on 7/2/17.
 */
public class ResStrategies {
    /*Brute force matching, needs optimisation*/
    public void forwardChaining(List<Element> kb) throws IOException {

        HashMap<String,Boolean> kbMap = new HashMap<>();
        for(int i=0;i<kb.size();i++){
            String str = elementAsString(kb.get(i));
            if(!kbMap.containsKey(str))
                kbMap.put(str,true);
        }

        List<Element> l1 = new ArrayList<>();
        for(int i=0;i<kb.size()-1;i++){
            for(int j=i+1;j<kb.size();j++) {
                Resolution res = new Resolution(kb.get(i).clone(), kb.get(j).clone());
                if(res.resolve()){
                    if(res.nullFound()){
                        System.out.println(l1.size() + " new clauses");
                        System.out.println("Null clause found while resolving");
                        return;
                    }else{
                        Element e = res.getFinalResolved();
                        if(!kbMap.containsKey(elementAsString(e))) {
                            l1.add(e);
                            kbMap.put(elementAsString(e),true);
                        }
                    }
                }
            }
        }

        System.out.println(l1.size()+" new clauses");

        List<Element> l2 = new ArrayList<>();
        while (true){
            for(int i=0;i<kb.size();i++){
                for(int j=0;j<l1.size();j++) {
                    Resolution res = new Resolution(kb.get(i).clone(), l1.get(j).clone());
                    if(res.resolve()){
                        if(res.nullFound()){
                            System.out.println(l2.size() + " new clauses");
                            System.out.println("Null clause found while resolving");
                            return;
                        }else{
                            Element e = res.getFinalResolved();
                            if(!kbMap.containsKey(elementAsString(e))) {
                                l2.add(e);
                                kbMap.put(elementAsString(e),true);
                            }
                        }
                    }
                }
            }

            System.out.println(l2.size()+" new clauses");

            if(l2.size()==0){
                System.out.println("Can't predict");
                return;
            }else {
                for(int i=0;i<l1.size();i++){
                    kb.add(l1.get(i).clone());
                }

                l1= new ArrayList<>();
                for(int i=0;i<l2.size();i++){
                    l1.add(l2.get(i).clone());
                }
                l2 = new ArrayList<>();
            }
        }
    }

    /*One of the resolvents is always in negated goal set or its derivative*/
    public void setOfSupportStrategy(List<Element> kb,Element g){
        HashMap<String,Boolean> kbMap = new HashMap<>();
        for(int i=0;i<kb.size();i++){
            String str = elementAsString(kb.get(i));
            if(!kbMap.containsKey(str))
                kbMap.put(str,true);
        }

        List<Element> negatedGoal = new ArrayList<>();
        negatedGoal.add(g.clone());

        List<Element> l1 = new ArrayList<>();
        while (true) {
            for (int i = 0; i < kb.size(); i++) {
                for (int j = 0; j < negatedGoal.size(); j++) {
                    Resolution res = new Resolution(kb.get(i).clone(), negatedGoal.get(j).clone());
                    if (res.resolve()) {
                        if (res.nullFound()) {
                            System.out.println(l1.size() + " new clauses");
                            System.out.println("Null clause found while resolving");
                            return;
                        } else {
                            Element e = res.getFinalResolved();
                            if(!kbMap.containsKey(elementAsString(e))) {
                                l1.add(e);
                                kbMap.put(elementAsString(e),true);
                            }
                        }
                    }
                }
            }

            System.out.println(l1.size() + " new clauses");
            if(l1.size()==0){
                System.out.println("Can't predict");
                return;
            }else {
                for(int i=0;i<l1.size();i++){
                    kb.add(l1.get(i).clone());
                }
                for(int i=0;i<l1.size();i++){
                    negatedGoal.add(l1.get(i).clone());
                }
                l1 = new ArrayList<>();
            }
        }
    }

    /*Preference to unit clauses, if no other option then BFS*/
    public void unitResolution(List<Element> kb) throws IOException {
        HashMap<String,Boolean> kbMap = new HashMap<>();
        for(int i=0;i<kb.size();i++){
            String str = elementAsString(kb.get(i));
            if(!kbMap.containsKey(str))
                kbMap.put(str,true);
        }
        List<Element> l1 = new ArrayList<>();
        for(int i=0;i<kb.size()-1;i++){
            for(int j=i+1;j<kb.size();j++) {

                Element e1 = kb.get(i).clone();
                Element e2 = kb.get(j).clone();
                if(isUnitClause(e1) || isUnitClause(e2)){
                    Resolution res = new Resolution(e1,e2);
                    if(res.resolve()){
                        if(res.nullFound()){
                            System.out.println(l1.size() + " new clauses");
                            System.out.println("Null clause found while resolving");
                            return;
                        }else{
                            Element e = res.getFinalResolved();
                            if(!kbMap.containsKey(elementAsString(e))) {
                                l1.add(e);
                                kbMap.put(elementAsString(e),true);
                            }
                        }
                    }
                }
            }
        }

        System.out.println(l1.size()+" new clauses");

        List<Element> l2 = new ArrayList<>();
        while (true){
            for(int i=0;i<kb.size();i++){
                for(int j=0;j<l1.size();j++) {
                    Element e1 = kb.get(i).clone();
                    Element e2 = l1.get(j).clone();
                    if(isUnitClause(e1) || isUnitClause(e2)) {
                        Resolution res = new Resolution(e1, e2);
                        if (res.resolve()) {
                            if (res.nullFound()) {
                                System.out.println(l2.size() + " new clauses");
                                System.out.println("Null clause found while resolving");
                                return;
                            } else {
                                Element e = res.getFinalResolved();
                                if(!kbMap.containsKey(elementAsString(e))) {
                                    l2.add(e);
                                    kbMap.put(elementAsString(e),true);
                                }
                            }
                        }
                    }
                }
            }

            System.out.println(l2.size()+" new clauses");

            if(l2.size()==0 || l1.size()==0){
                forwardChaining(kb);
                return;
            }else {
                for(int i=0;i<l1.size();i++){
                    kb.add(l1.get(i).clone());
                }

                l1= new ArrayList<>();
                for(int i=0;i<l2.size();i++){
                    l1.add(l2.get(i).clone());
                }
                l2 = new ArrayList<>();
            }
        }
    }

    /*One of the resolvents is always from original clauses: Not complete*/
    public void inputStrategy(List<Element> kb){
        HashMap<String,Boolean> kbMap = new HashMap<>();
        for(int i=0;i<kb.size();i++){
            String str = elementAsString(kb.get(i));
            if(!kbMap.containsKey(str))
                kbMap.put(str,true);
        }

        List<Element> goalDerivatives = new ArrayList<>();
        List<Element> originalKB = new ArrayList<>();

        //copy initial kb with negated goal
        for(int i=0;i<kb.size();i++){
            originalKB.add(kb.get(i).clone());
        }
        while (true) {
            for (int i = 0; i < kb.size()-1; i++) {//original inputs
                for (int j = 0; j < originalKB.size(); j++) {
                    Resolution res = new Resolution(kb.get(i).clone(), originalKB.get(j).clone());
                    if (res.resolve()) {
                        if (res.nullFound()) {
                            System.out.println(goalDerivatives.size() + " new clauses");
                            System.out.println("Null clause found while resolving");
                            return;
                        } else {
                            Element e = res.getFinalResolved();
                            if(!kbMap.containsKey(elementAsString(e))) {
                                goalDerivatives.add(e);
                                kbMap.put(elementAsString(e),true);
                            }
                        }
                    }
                }
            }

            System.out.println(goalDerivatives.size() + " new clauses");
            if(goalDerivatives.size()==0){
                System.out.println("Can't predict");
                return;
            }else{
                originalKB = new ArrayList<>();
                for(int i=0;i<goalDerivatives.size();i++){
                    originalKB.add(goalDerivatives.get(i).clone());
                }
                goalDerivatives = new ArrayList<>();
            }
        }
    }

    public String elementAsString(Element e){
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());
        return outp.outputString(e);
    }

    private boolean isUnitClause(Element e2) {
        return e2.getName().equals("Atom");
    }
}
