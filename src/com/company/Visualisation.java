package com.company;

import javafx.util.Pair;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.util.List;

/**
 * Created by riddle on 14/2/17.
 */
public class Visualisation {
    protected String styleSheet;

    public void visualise(List<Element> kb,List<Pair<Pair<String, String>, String>> l1){
        XMLOutputter outp = new XMLOutputter();
        outp.setFormat(Format.getPrettyFormat());

        Graph graph = new SingleGraph("Tutorial 1");
        styleSheet =
                "node {" +
                        "	fill-color: black;" +
                        "}" +
                        "node.marked {" +
                        "	fill-color: red;" +
                        "}"+
                        "node.null {" +
                        "	fill-color: green;" +
                        "}";
        graph.addAttribute("ui.stylesheet", styleSheet);

        for(int i=0;i<kb.size();i++){
            graph.addNode(outp.outputString(kb.get(i)));
        }

        for (Node node : graph) {
            node.addAttribute("ui.class", "marked");
        }

        Node node1 = graph.addNode("Null");
        node1.addAttribute("ui.class", "null");

        graph.setStrict(false);
        graph.setAutoCreate( true );

        for(int i=0;i<l1.size();i++){
            Pair<Pair<String, String>, String> p = l1.get(i);
            graph.addEdge(Integer.toString(i),p.getKey().getValue(),p.getValue(),true);
            graph.addEdge(Integer.toString(i)+"a",p.getKey().getKey(),p.getValue(),true);
        }

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        graph.display();
    }
}
