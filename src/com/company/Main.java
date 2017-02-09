package com.company;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.company.KB;

public class Main {

    public static void main(String[] args) throws JDOMException, IOException {


        File inputFile = new File("Queries/student.txt");
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element root = document.getRootElement();

        KB newKB = new KB();
        List<Element> l1 = newKB.readKB(root);


//        ResStrategies str = new ResStrategies();
//        str.forwardChaining(l1);


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
        AnsStrategies str = new AnsStrategies();
        str.unitResolution(l1);
//        System.out.println(str.forwardChaining(l1));



    }
}
