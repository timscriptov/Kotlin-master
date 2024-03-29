package com.mcal.kotlin.module;

import com.mcal.kotlin.App;
import com.mcal.kotlin.R;
import com.mcal.kotlin.adapters.ListAdapter;
import com.mcal.kotlin.view.MainView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ListParser {
    private ArrayList<String> items = new ArrayList<>();
    private MainView mainView;

    public ListParser(MainView mainView) {
        this.mainView = mainView;
        if (items.size() == 0) {
            try {
                InputStream is = App.getContext().getResources().openRawResource(R.raw.lessons);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbf.newDocumentBuilder();
                Document doc = builder.parse(is);
                is.close();
                NodeList lessons = doc.getElementsByTagName("lesson");
                for (int x = 0; x < lessons.getLength(); x++) {
                    items.add(lessons.item(x).getTextContent());
                }
            } catch (Exception ignored) {
            }
        }
    }

    public ListAdapter getListAdapter() {
        return new ListAdapter(items, mainView);
    }
}