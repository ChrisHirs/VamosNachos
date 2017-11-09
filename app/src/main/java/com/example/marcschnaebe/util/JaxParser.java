package com.example.marcschnaebe.util;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by christop.hirschi on 03.11.2017.
 */

public class JaxParser {
    ArrayList<Object> listObjects = new ArrayList<>();

    public JaxParser(InputStream stream)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            SourceHandler sourceHandler = new SourceHandler(listObjects);

            xmlReader.setContentHandler(sourceHandler);
            InputSource source = new InputSource(stream);
            xmlReader.parse(source);

        } catch (Exception e)
        {
            Log.d("error", "ERROR parsing " + stream + " file");
            e.printStackTrace();
        }
    }

    //Retourne la liste des nachos
    public ArrayList<Object> getObjects()
    {
        return listObjects;
    }
}
