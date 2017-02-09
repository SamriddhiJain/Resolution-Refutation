package com.company;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by riddle on 9/2/17.
 */
public class ChangeVariables {
    /*Change the common variables of two clauses
    * Deep copy: e2 is automatically updated.
    * */
    public void changeVariables(Element e1, Element e2){
        HashMap<String,Boolean> mp = new HashMap<>();
        fillHashTablewithVar(e1,mp);
        replaceElementWithValues(e2,mp);
        printElement(e2);
    }

    private void replaceElementWithValues(Element pat, HashMap<String, Boolean> mp) {
        if(pat.getName().equals("Var") && mp.containsKey(pat.getValue())){
            String str = pat.getValue()+"1";
            pat.setText(str);
        }
        else{
            List<Element> Children =  pat.getChildren();
            for(int i=0;i<Children.size();++i){
                replaceElementWithValues(Children.get(i),mp);
            }
        }
    }

    private void fillHashTablewithVar(Element pat, HashMap<String, Boolean> mp) {
        if(pat.getName().equals("Var") && !mp.containsKey(pat.getValue())){
            mp.put(pat.getValue(),true);
        }
        else{
            List<Element> Children =  pat.getChildren();
            for(int i=0;i<Children.size();++i){
                fillHashTablewithVar(Children.get(i),mp);
            }
        }
    }

    public void printElement(Element e){
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());
        System.out.println(outp.outputString(e));
    }
}
