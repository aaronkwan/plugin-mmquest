package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class AllMyQuests {
    public ArrayList<Quest> questArrayList;

    public static String writeToJSON() throws IOException {
        // NOTE: Function is incomplete.
        Document htmlRegion1 = Jsoup.connect("http://help.websiteos.com/websiteos/example_of_a_simple_html_page.htm").get();
        Element h1Element = htmlRegion1.select("p").get(2);
        String h1Text = h1Element.text();
        return h1Text;
    }
}


