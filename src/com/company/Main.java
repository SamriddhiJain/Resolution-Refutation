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

        File inputFile = new File("ManSoc.txt");
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        Element root = document.getRootElement();

        KB newKB = new KB();
        List<Element> l1 = newKB.readKB(root);
        System.out.println(l1.size());

        Resolution res = new Resolution(l1.get(0),l1.get(1));
        res.resolve();

    }
}
