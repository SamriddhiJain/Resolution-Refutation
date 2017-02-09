package com.company;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by riddle on 8/2/17.
 */
public class AnsStrategies {
    /*Brute force matching, needs optimisation*/
    public Element forwardChaining(List<Element> kb) throws IOException, FileNotFoundException, UnsupportedEncodingException {
        List<Element> l1 = new ArrayList<>();
        for(int i=0;i<kb.size()-1;i++){
            for(int j=i+1;j<kb.size();j++) {
//                changeVariables(kb.get(i).clone(), kb.get(j).clone());
                Resolution res = new Resolution(kb.get(i).clone(), kb.get(j).clone());
                if(res.resolve()) {
                    Element e = res.getFinalResolved().clone();
                    if (!kb.contains(e)){
                        if(checkAnswer(e)) {
                            printElement(e);
                            return e;
                        }
                        l1.add(e);
                    }
                }
            }
        }

        System.out.println(l1.size()+" new clauses");
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());

//        PrintWriter writer = new PrintWriter("out.txt", "UTF-8");
//        writer.println();
//        writer.println(outp.outputString(l1));

        List<Element> l2 = new ArrayList<>();
        while (true){
            for(int i=0;i<kb.size();i++){
                for(int j=0;j<l1.size();j++) {
                    Resolution res = new Resolution(kb.get(i).clone(), l1.get(j).clone());
                    if(res.resolve()){
                        Element e = res.getFinalResolved().clone();
                        if (!kb.contains(e)){
                            if(checkAnswer(e)) {
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

    /*Check if element is resolved Answer literal*/
    private Boolean checkAnswer(Element e) {
        if(e.getName().equals("Atom") && e.getChildren().size()==2){
            List<Element> l1 = e.getChildren();
            if(l1.get(0).getName().equals("Rel") && l1.get(0).getValue().equals("Answer") && !isVarInPat(l1.get(1)))
                return true;
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
