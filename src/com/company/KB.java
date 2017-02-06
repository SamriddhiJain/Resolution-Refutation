package com.company;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by riddle on 6/2/17.
 */
public class KB {
    /*
    * reads and processes the KB from given file
    * removes AND tag, returns list of clauses in the KB: each clause start with <Or> or <Atom>
    * */
    public List<Element> readKB(Element e1){
        List<Element> l1 = new ArrayList<>();
        String str = e1.getName();

        if(str=="And"){//Multiple Clauses: can be combination of ORs and Atoms
            l1 = e1.getChildren();
        }else if(str=="Or" || str=="Atom"){//Single Clause, Multiple Literals
            l1.add(e1);
        }else{
            System.out.println("Unknown Case: Check KB");
        }

        return l1;
    }
}
