package com.company;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.util.*;

import static com.company.Unification.unify;

/**
 * Created by riddle on 5/2/17.
 */
public class Resolution {

    private boolean check = false;
    private static Element e1;
    private static Element e2;
    /*
    * pmap: hashmap for positive literals: string, <Atom></Atom> tag
    * nmap: hashmap for nagative literals: string, <Not></Not> tag
    * */
    private HashMap<String,Element> pmap1 = new HashMap<String,Element>();
    private HashMap<String,Element> nmap1 = new HashMap<String,Element>();
    private HashMap<String,Element> pmap2 = new HashMap<String,Element>();
    private HashMap<String,Element> nmap2 = new HashMap<String,Element>();

    public Resolution(Element a, Element b){
        e1 = a;
        e2 = b;
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

//        Iterator<String> it = map1.keySet().iterator();
//        while(it.hasNext()){
//            String key= it.next();
//
//            Element p= map1.get(key);
//            System.out.println(key+" "+p.getName());
//            System.out.println(p.getChildren().size());
//        }


        if(b==Boolean.TRUE){
            pmap1 = map1;
            nmap1 = map2;
        }
        else{
            pmap2 = map1;
            nmap2 = map2;
        }

    }

    public void resolve(){
        this.makeHashTable(Boolean.TRUE);
        this.makeHashTable(Boolean.FALSE);
        Unification u1 = new Unification();

        Iterator<String> it = pmap1.keySet().iterator();
        while(it.hasNext()){
            String key= it.next();
            Element p = pmap1.get(key);
            if(nmap2.containsKey(key)){
                Element p1 = nmap2.get(key);
                p1 = p1.setName("Atom");
                try {
                    System.out.println("Unify!!");
                    u1.unify(p,p1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Iterator<String> it2 = nmap1.keySet().iterator();
        while(it2.hasNext()){
            String key= it2.next();
            Element p = nmap1.get(key);
            p = p.setName("Atom");
            if(pmap2.containsKey(key)){
                Element p1 = pmap2.get(key);
                try {
                    System.out.println("Unify!!");
                    u1.unify(p,p1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        HashMap<String,Element> mp = u1.map;
        Iterator<String> itF = mp.keySet().iterator();
        while(itF.hasNext()){
            String key= itF.next();
            Element p = mp.get(key);

            System.out.println(p.getValue());
        }
    }
}
