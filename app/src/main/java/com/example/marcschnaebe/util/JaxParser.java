package com.example.marcschnaebe.util;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * JAX Parser class
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class JaxParser {
    /* -------  Attributes  ------ */

    ArrayList<Object> listObjects = new ArrayList<>();

    /* -------  Constructor ------- */

    /**
     * Constructor
     *
     * @param stream input stream
     */
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

    /* -------  Getter & Setter  ------ */

    public ArrayList<Object> getObjects()
    {
        return listObjects;
    }
}
