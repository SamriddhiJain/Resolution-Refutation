package com.company;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riddle on 8/2/17.
 */
public class AnsStrategies {
    /*Brute force matching, needs optimisation*/
    public Element forwardChaining(List<Element> kb) throws IOException {
        List<Element> l1 = new ArrayList<>();
        for(int i=0;i<kb.size()-1;i++){
            for(int j=i+1;j<kb.size();j++) {
                Resolution res = new Resolution(kb.get(i).clone(), kb.get(j).clone());
                if(res.resolve()) {
                    Element e = res.getFinalResolved().clone();
                    if (!kb.contains(e)){
                        if(checkAnswer(e)) {
                            System.out.println(l1.size() + " new clauses");
                            printElement(e);
                            return e;
                        }
                        l1.add(e);
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
                        Element e = res.getFinalResolved().clone();
                        if (!kb.contains(e)){
                            if(checkAnswer(e)) {//Answer tag with no variable
                                System.out.println(l2.size() + " new clauses");
                                printElement(e);
                                return e;
                            }
                            l2.add(e);
                        }
                    }
                }
            }

            System.out.println(l2.size()+" new clauses");

            if(l2.size()==0){
                System.out.println("Can't predict");
                return null;
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
    public Element setOfSupportStrategy(List<Element> kb,Element g){
        List<Element> negatedGoal = new ArrayList<>();
        List<Element> goalDerivatives = new ArrayList<>();
        negatedGoal.add(g.clone());
        goalDerivatives.add(g.clone());

        List<Element> l1 = new ArrayList<>();
        while (true) {
            for (int i = 0; i < kb.size(); i++) {
                for (int j = 0; j < negatedGoal.size(); j++) {
                    Resolution res = new Resolution(kb.get(i).clone(), negatedGoal.get(j).clone());
                    if (res.resolve()) {
                        Element e = res.getFinalResolved();
                        if (!kb.contains(e) && !goalDerivatives.contains(e)){
                            if(checkAnswer(e)) {//Answer tag with no variable
                                System.out.println(l1.size() + " new clauses");
                                printElement(e);
                                return e;
                            }
                            l1.add(e);
                            goalDerivatives.add(e);
                        }
                    }
                }
            }

            System.out.println(l1.size() + " new clauses");
            if(l1.size()==0){
                System.out.println("Can't predict");
                return null;
            }else {
                for(int i=0;i<negatedGoal.size();i++){
                    kb.add(negatedGoal.get(i).clone());
                }

                negatedGoal = new ArrayList<>();
                for(int i=0;i<l1.size();i++){
                    negatedGoal.add(l1.get(i).clone());
                }
                l1 = new ArrayList<>();
            }
        }
    }

    /*Preference to unit clauses, if no other option then BFS*/
    public Element unitResolution(List<Element> kb) throws IOException {
        List<Element> l1 = new ArrayList<>();
        for(int i=0;i<kb.size()-1;i++){
            for(int j=i+1;j<kb.size();j++) {

                Element e1 = kb.get(i).clone();
                Element e2 = kb.get(j).clone();
                if(isUnitClause(e1) || isUnitClause(e2)){
                    Resolution res = new Resolution(e1,e2);
                    if(res.resolve()){
                        Element e = res.getFinalResolved().clone();
                        if (!kb.contains(e)){
                            if(checkAnswer(e)) {
                                System.out.println(l1.size() + " new clauses");
                                printElement(e);
                                return e;
                            }
                            l1.add(e);
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
                            Element e = res.getFinalResolved().clone();
                            if (!kb.contains(e)){
                                if(checkAnswer(e)) {
                                    System.out.println(l2.size() + " new clauses");
                                    printElement(e);
                                    return e;
                                }
                                l2.add(e);
                            }
                        }
                    }
                }
            }

            System.out.println(l2.size()+" new clauses");

            if(l2.size()==0 || l1.size()==0){
                return forwardChaining(kb);
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
    public Element inputStrategy(List<Element> kb){
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
                        Element e = res.getFinalResolved();
                        if (!kb.contains(e) && !goalDerivatives.contains(e)){
                            if(checkAnswer(e)) {//Answer tag with no variable
                                System.out.println(goalDerivatives.size() + " new clauses");
                                printElement(e);
                                return e;
                            }
                            goalDerivatives.add(e);
                        }
                    }
                }
            }

            System.out.println(goalDerivatives.size() + " new clauses");
            if(goalDerivatives.size()==0){
                System.out.println("Can't predict");
                return null;
            }else{
                originalKB = new ArrayList<>();
                for(int i=0;i<goalDerivatives.size();i++){
                    originalKB.add(goalDerivatives.get(i).clone());
                }
                goalDerivatives = new ArrayList<>();
            }
        }
    }

    private boolean isUnitClause(Element e2) {
        return e2.getName().equals("Atom");
    }

    /*Check if element is resolved Answer literal*/
    private Boolean checkAnswer(Element e) {
        if(e.getName().equals("Atom") && e.getChildren().size()==2){
            List<Element> l1 = e.getChildren();
            if(l1.get(0).getName().equals("Rel") && l1.get(0).getValue().equals("Answer") && !isVarInPat(l1.get(1)))
                return true;
        }else if(e.getName().equals("Or")){
            List<Element> l1 = e.getChildren();
            Boolean result = true;
            for(int i=0;i<l1.size();i++){
                result = result && checkAnswer(l1.get(i));
            }

            return result;
        }
        return false;
    }

    /**
     * isVarInPat - for finding if some variable is present in the pattern
     */
    public boolean isVarInPat(Element pat){
        if(pat.getName().equals("Var")){
            return true;
        }
        else{
            List<Element> Children =  pat.getChildren();
            boolean result = false;
            for(int i=0;i<Children.size();++i){
                result =  result || isVarInPat(Children.get(i));
            }
            return result;
        }
    }

    public void printElement(Element e){
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());
        System.out.println(outp.outputString(e));
    }
}
