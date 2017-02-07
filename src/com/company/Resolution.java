package com.company;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.util.*;

import com.company.Unification;

/**
 * Created by riddle on 5/2/17.
 */
public class Resolution {

    private boolean check = false;
    private Element e1;
    private Element e2;
    private Element eResolved = null;
    /*
    * pmap: hashmap for positive literals: string, <Atom></Atom> tag
    * nmap: hashmap for nagative literals: string, <Not></Not> tag
    * */
    private HashMap<String,Element> pmap1 = new HashMap<String,Element>();
    private HashMap<String,Element> nmap1 = new HashMap<String,Element>();
    private HashMap<String,Element> pmap2 = new HashMap<String,Element>();
    private HashMap<String,Element> nmap2 = new HashMap<String,Element>();

    /*finalHashMap contains all the final substitutions*/
    public HashMap<String,Element> finalHashMap = new HashMap<>();

    public Resolution(Element a, Element b){
        e1 = a;
        e2 = b;
    }

    public Element getFinalResolved(){
        return eResolved;
    }

    public Boolean nullFound(){
        return check;
    }

    /*
    * Input: Boolean: True for 1st clause, False for 2nd
    * constructs the positive and negative hash tables
    * TODO: Optimise it.
    * */
    public void makeHashTable(Boolean b){
        //map1 for +ve literal, map2 for negative
        HashMap<String,Element> map1 = new HashMap<String,Element>();
        HashMap<String,Element> map2 = new HashMap<String,Element>();

        Element e;
        if(b==Boolean.TRUE)
            e = this.e1;
        else
            e = this.e2;

        if(e.getName()!="Or"){ //single literal: starting with Atom
            String str = "";
            List<Element> l1 = e.getChildren("Not");
            if(l1.size()==1) {//negative literal
                str = l1.get(0).getChild("Rel").getValue();
                map2.put(str,l1.get(0));
            }else{//positive literal
                str = e.getChild("Rel").getValue();
                map1.put(str,e);
            }
        }else{//multiple literals
            List<Element> l1 = e.getChildren();//List of atoms
            for(int i=0;i<l1.size();i++){
                String str = "";
                List<Element> l2 = l1.get(i).getChildren("Not");
                if(l2.size()==1) {//negative literal
                    str = l2.get(0).getChild("Rel").getValue();
                    map2.put(str,l2.get(0));
                }else{//positive literal
                    str = l1.get(i).getChild("Rel").getValue();
                    map1.put(str,l1.get(i));
                }
            }
        }

        if(b==Boolean.TRUE){
            pmap1 = map1;
            nmap1 = map2;
        }
        else{
            pmap2 = map1;
            nmap2 = map2;
        }

    }

    /*Resolves the two clauses
    * Returns true if null clause found or two clauses can be successfully resolved
    * returns false if clauses can not be resolved.
    * If true: resoved output in private variable
    * 1. Make hashtables
    * 2. Unify if needed
    * 3. Resolve clauses with substitution
    * */
    public boolean resolve(){

        int flag = 0;

        if(checkNull(e1,e2)){//e1 negation of e2
            System.out.println("Null clause found");
            check = true;
            return true;
        }else{

            /*Construct hashtables for both clauses*/
            this.makeHashTable(Boolean.TRUE);
            this.makeHashTable(Boolean.FALSE);

            /*Iterate over hashTables of first clause, unify whereever needed*/
            Iterator<String> it = pmap1.keySet().iterator();
            while(it.hasNext()){
                String key= it.next();
                Element p = pmap1.get(key).clone();
                if(nmap2.containsKey(key)){
                    Element p1 = nmap2.get(key).clone(); //clone to create a deep copy
                    p1 = p1.setName("Atom"); //Replacing Not with Atom
                    try {
                        System.out.println("Unify!!");

                        Unification u1 = new Unification();

                        if(checkEquals(p,p1))
                            flag = 1;
                        else
                            u1.unify(p,p1);

                        HashMap<String,Element> mp = u1.map;
                        Iterator<String> itF = mp.keySet().iterator();
                        while(itF.hasNext()){
                            String key2= itF.next();
                            if(!finalHashMap.containsKey(key2)){
                                Element keyElement = mp.get(key2);
                                finalHashMap.put(key2,keyElement);
                                System.out.println("Key added: "+key2+" : "+keyElement.getValue());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Iterator<String> it2 = nmap1.keySet().iterator();
            while(it2.hasNext()){
                String key= it2.next();
                Element p = nmap1.get(key).clone();
                p.setName("Atom");
                if(pmap2.containsKey(key)){
                    Element p1 = pmap2.get(key).clone();
                    try {
                        System.out.println("Unify!!");
                        Unification u1 = new Unification();

                        if(checkEquals(p,p1))
                            flag = 1;
                        else
                            u1.unify(p,p1);

                        HashMap<String,Element> mp = u1.map;
                        Iterator<String> itF = mp.keySet().iterator();
                        while(itF.hasNext()){
                            String key2= itF.next();
                            if(!finalHashMap.containsKey(key2)){
                                Element keyElement = mp.get(key2);
                                finalHashMap.put(key2,keyElement);
                                System.out.println("Key added: "+key2+" : "+keyElement.getValue());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            /*Combine the two clauses if there exists some substitutions*/
            if(finalHashMap.size()==0){//either no substitutions or no variables
                if(flag==1){//+- pair, no variables in literals
                    return resolveWithoutSubstitution();
                }else
                    return false;//no substitution and variables
            } else
                return substitute(finalHashMap);
        }
    }

    private boolean checkEquals(Element p, Element p1) {
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());

        return outp.outputString(p).equals(outp.outputString(p1));
    }

    private Boolean resolveWithoutSubstitution() {
        /*Combined literals of both the elements*/
        List<Element> unionList = unionLiterals(e1,e2);
        List<Element> newList = removePairs(unionList);

        /*If union list is empty: True literal, no new resolvent*/
        if(newList.size()==0)
            return false;
        else{
            eResolved = combineLiterals(newList); //Combine with Or
            System.out.println("Resolved: ");
            printElement(eResolved);
            return true;
        }
    }

    /*
    * Input: Two elements
    * Checks if one literal is negation of the other
    * Positive will start from <Atom>, negative will be <Atom><Not>
    * */
    private boolean checkNull(Element eo1,Element eo2) {
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());

        if(eo1.getName()=="Or" || eo2.getName()=="Or")
            return false;
        if(eo1.getChildren("Not").size()==0 && eo2.getChildren("Not").size()==1
                && outp.outputString(eo2.getChildren("Not").get(0).getContent()).equals(outp.outputString(eo1.getContent())))
            return true;
        else if(eo1.getChildren("Not").size()==1 && eo2.getChildren("Not").size()==0
                && outp.outputString(eo1.getChildren("Not").get(0).getContent()).equals(outp.outputString(eo2.getContent())))
            return true;

        return false;
    }

    /*Substitute the unification outputs into the two elements
    * Algo: 1. Take union of all literals/atoms
    * 2. Substitute each
    * 3. Check if any pair leads to null clause: just remove them
    * */
    private boolean substitute(HashMap<String, Element> mp) {
        if(mp.keySet().size()!=0) {
            /*Combined literals of both the elements*/

            List<Element> unionList = unionLiterals(e1,e2);

            /*Substitute each literal*/
            for(int i=0;i<unionList.size();i++){
                Element e1 = unionList.get(i).clone();
                unionList.set(i,substituteLiteral(e1,mp));
            }

            List<Element> newList = removePairs(unionList);

            /*If union list is empty: True literal, no new resolvent*/
            if(newList.size()==0)
                return false;
            else{
                eResolved = combineLiterals(newList); //Combine with Or
                System.out.println("Resolved: ");
                printElement(eResolved);
                return true;
            }
        }else return false;
    }

    private List<Element> unionLiterals(Element e1, Element e2) {
        List<Element> unionList = new ArrayList<>();
        if(e1.getName().equals("Or")){
            unionList.addAll(e1.getChildren());
        }else
            unionList.add(e1);

        if(e2.getName().equals("Or")){
            unionList.addAll(e2.getChildren());
        }else
            unionList.add(e2);

        return unionList;
    }

    private List<Element> removePairs(List<Element> unionList) {
        /*remove positive negative pairs, as they form truth
            * VERIFY LOGIC*/
        for (int i=0;i<unionList.size()-1;i++){
            Element e1 = unionList.get(i);
            for(int j=i+1;j<unionList.size();j++){
                Element e2 = unionList.get(j);
                if(checkNull(e1,e2)){
                    unionList.remove(e1);
                    unionList.remove(e2);
                    i -= 1;
                    break;
                }
            }
        }

        return unionList;
    }

    /*Combine non zero number of literals with Or*/
    private Element combineLiterals(List<Element> unionList) {
        if(unionList.size()==1)
            return unionList.get(0);
        else {
            Element finalE = new Element("Or");
            List<Element> newL = new ArrayList<>();
            for(int i=0;i<unionList.size();i++){
                newL.add(unionList.get(i).clone());
            }
            finalE.setContent(newL);
            return finalE;
        }
    }

    /*Substitute variables with values*/
    private Element substituteLiteral(Element element, HashMap<String, Element> mp) {
        if(element.getName().equals("Var")){
            if(mp.containsKey(element.getValue())) {
                return mp.get(element.getValue());
            } else
                return element;
        } else{
            List<Element> Children =  element.getChildren();
            List<Element> newChildren = new ArrayList<>();
            Element result = element.clone();

            for(int i=0;i<Children.size();i++){
                newChildren.add(substituteLiteral(Children.get(i),mp).clone());
            }
            if(Children.size()!=0) {
                result.setContent(newChildren);
//                printElement(result);
//                System.out.println();
            }

            return result;
        }
    }

    private void printElement(Element e){
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());
        System.out.println(outp.outputString(e));
    }
}
