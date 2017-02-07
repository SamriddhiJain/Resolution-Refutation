/**
 * Created by riddle on 5/2/17.
 * Code by Samya: Hope it is correct.
 */
package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Unification {


    /**
     * Global variables
     * Result- for storing result in ruleml format
     * fail- to keep track of failure
     * map - for mapping corresponding substitutions
     */
    public Element result = new Element ("Substitution");
    public boolean fail= false;
    public HashMap<String,Element> map = new HashMap<String,Element>();


    /**
     * CheckVarInPat - for finding the variable is present in the pattern or not
     * 1st argument - should be a variable
     * 2nd argument - should be a pattern(cannot  just be a variable)
     */
    public static boolean CheckVarInPat(Element var, Element pat){
        if(pat.getName().equals("Var")){
            if(var.getText().equals(pat.getText()))
                return true;
            else
                return false;
        }
        else{
            List<Element> Children=  pat.getChildren();
            boolean result= false;
            for(int i=0;i<Children.size();++i){
                result=  result || CheckVarInPat(var,Children.get(i));
            }
            return result;
        }
    }


    /**
     * unify - takes two patterns as input, applies unifiaction algorithm
     * @throws IOException
     *
     */
    public void unify(Element pat1, Element pat2) throws IOException{
//		 System.out.println("-------------------------------");
//		 pat1.detach();
//		printResult(pat1);
//		pat1.detach();
//		 System.out.println("*****************");
//		 pat2.detach();
//		printResult(pat2);
//		pat2.detach();
//		 System.out.println("-------------------------------");
        if(fail==true)
            return;
        else if("Var".equals(pat1.getName())){
            if(map.containsKey(pat1.getText())){
                Element e=map.get(pat1.getText());

                unify(e,pat2);
                return;
            }
            else if(CheckVarInPat(pat1,pat2) && !"Var".equals(pat2.getName())){
                System.out.println(pat1.getName()+ "-4--"+ pat2.getName());
                fail=true;
                return;
            }
            else if("Var".equals(pat2.getName()) && pat1.getText().equals(pat2.getText()))
                return;
            else{
                map.put(pat1.getText(),pat2);
                System.out.println(pat2.getChildren().size());
                //printResult(pat2);
                pat1.detach();

            }
        }
        else if("Var".equals(pat2.getName())){
            if(map.containsKey(pat2.getText())){
                Element e=map.get(pat2.getText());

                unify(e,pat1);
                return;
            }
            else if(CheckVarInPat(pat2,pat1) && !"Var".equals(pat1.getName())){
                System.out.println(pat1.getName()+ "--3--"+ pat2.getName());
                fail=true;
                return;
            }
            else if("Var".equals(pat1.getName()) && pat2.getText().equals(pat1.getText()))
                return;
            else{
                map.put(pat2.getText(),pat1);
//

            }
        }
        else{
            String patName1= pat1.getName();
            String patName2= pat2.getName();
            if(patName1.equals(patName2) && ("Rel".equals(patName1) || "Ind".equals(patName1))){
                if(pat1.getText().equals(pat2.getText()))
                    return ;
                else{
                    fail= true;
                    return;
                }
            }
            else if(patName1.equals(patName2)){
                List<Element> Children1 = pat1.getChildren();
                List<Element> Children2 =  pat2.getChildren();
                if(Children1.size()==Children2.size()){
                    for(int temp=Children2.size()-1;temp>=0;--temp){
                        Element c1= Children1.get(temp);
                        Element c2= Children2.get(temp);
                        //c1.detach();
                        //c2.detach();
                        unify(c1,c2);
                    }
                }
                else{

                    fail=true;
                    return;
                }
            }
            else{

                fail=true;
                return;
            }
        }

    }
    /**
     *
     * unification- takes two files names(full path of file should be passed) , parses it and calls the unify method.
     * if you have two documents already parsed in memory, you can use unify method directly
     */
    public void unification(String filename1, String filename2){
        try{
            File inputFile = new File(filename1);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element root = document.getRootElement();

            File inputFile1 = new File(filename2);
            SAXBuilder saxBuilder1 = new SAXBuilder();
            Document document1 = saxBuilder.build(inputFile1);
            Element root1 = document1.getRootElement();


            unify(root1,root);

            computeResult();

            printResult();

        }catch(JDOMException e){
            e.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    /**
     * ComputeResult-  puts all the substitutions in hashmap in proper ruleml format
     * @throws IOException
     */

    public void computeResult() throws IOException{
        if(fail)
            System.out.println("Failed");
        else{
            Iterator<String> it = map.keySet().iterator();
            while(it.hasNext()){
                String key= it.next();
                Element e= new Element("Pair");
                Element v= new Element("Var");
                Element p= map.get(key);
                p.detach();
                v.setText(key);
                e.addContent(v);


                e.addContent(p);
                result.addContent(e);

            }
        }
    }
    /**
     * printResult - prints result in xml format, only for debugging purpose.
     */
    public void printResult() throws IOException{
        Document doc = new Document(result);
        XMLOutputter xmlOutput = new XMLOutputter();

        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, System.out);
        //doc.getRootElement().addContent(result);
    }

    public static void printResult(Element e) throws IOException{
        Document doc = new Document(e);
        XMLOutputter xmlOutput = new XMLOutputter();

        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, System.out);
        //doc.getRootElement().addContent(result);
    }

//    public static void main(String[] args){
//
//        unification("input2.txt","input21.txt");
//    }

}