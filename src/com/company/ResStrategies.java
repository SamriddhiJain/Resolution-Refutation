package com.company;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riddle on 7/2/17.
 */
public class ResStrategies {
    /*Brute force matching, needs optimisation*/
    public void forwardChaining(List<Element> kb) throws IOException, FileNotFoundException, UnsupportedEncodingException {
        List<Element> l1 = new ArrayList<>();
        for(int i=0;i<kb.size()-1;i++){
            for(int j=i+1;j<kb.size();j++) {
                Resolution res = new Resolution(kb.get(i).clone(), kb.get(j).clone());
                if(res.resolve()){
                    if(res.nullFound()){
                        return;
                    }else{
                        Element e = res.getFinalResolved();
                        if(!kb.contains(e))
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
                        if(res.nullFound()){
                            return;
                        }else{
                            Element e = res.getFinalResolved();
                            if(!kb.contains(e))
                                l2.add(e);
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
}
