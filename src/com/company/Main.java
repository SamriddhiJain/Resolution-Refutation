package com.company;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.company.KB;

public class Main {

    public static void main(String[] args) throws JDOMException, IOException {


        File inputFile = new File("blocks.txt");
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element root = document.getRootElement();

        KB newKB = new KB();
        List<Element> l1 = newKB.readKB(root);

//        Resolution res = new Resolution(l1.get(0),l1.get(2));
//        System.out.println(res.resolve());

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

        ResStrategies str = new ResStrategies();
        str.unitResolution(l1);

    }
}
