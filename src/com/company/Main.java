package com.company;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.company.Unification.unification;
import com.company.KB;

public class Main {

    public static void main(String[] args) throws JDOMException, IOException {
	// write your code here
        File inputFile = new File("input21.txt");
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element root = document.getRootElement();

        KB newKB = new KB();
        List<Element> l1 = newKB.readKB(root);
        System.out.println(l1.size());

        Resolution res = new Resolution(l1.get(0),l1.get(1));
        res.resolve();

        //Assumption: root has Or TAG
//        List<Element> l1=root.getChildren("Atom");
//        System.out.println(l1.size());
//        for(int i=0;i<l1.size();i++){
//            Element e1=l1.get(i);
//            //System.out.println(e1.getName());
//
//            List<Element> l2=e1.getChildren("Not");
//            if(l2.size()==0)
//                System.out.println("Positive Literal");
//            else
//                System.out.println("Negative Literal");
//
//        }
//        checkForResolution();
//        unification("u1.txt","u2.txt");
    }
}
