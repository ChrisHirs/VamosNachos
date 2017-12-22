package com.example.marcschnaebe.util;


import com.example.marcschnaebe.mynacho.Nachos;
import com.example.marcschnaebe.mynacho.Item;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;


/**
 * Source handler class
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class SourceHandler extends DefaultHandler {

    /* -------  Attributes  ------ */

    ArrayList<Object> listObjects = new ArrayList<>();

    /* -------  Constructor ------- */

    /**
     * Constructor
     *
     * @param listObj list of objects
     */
    public SourceHandler(ArrayList<Object> listObj)
    {
        this.listObjects = listObj;
    }

    /* -------  Methods ------- */

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {

    }

    @Override
    public void endDocument() throws SAXException
    {
        super.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {

    }

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if (qName.equalsIgnoreCase("nachos"))
        {
            String type = attributes.getValue("type");
            String name = attributes.getValue("name");

            int level = parseInt(attributes.getValue("level"));
            int xpCurrent = parseInt(attributes.getValue("xpCurrent"));
            int xpMax = parseInt(attributes.getValue("xpMax"));

            int ap = parseInt(attributes.getValue("ap"));
            int hpCurrent = parseInt(attributes.getValue("hpCurrent"));
            int hpMax = parseInt(attributes.getValue("hpMax"));

            int hpBonus = parseInt(attributes.getValue("hpBonus"));
            int apBonus = Integer.parseInt(attributes.getValue("apBonus"));

            Nachos nachos = new Nachos(null, name, type, level, xpCurrent, xpMax, ap, hpCurrent, hpMax, hpBonus, apBonus);
            listObjects.add(nachos);
        }
        else if (qName.equalsIgnoreCase("item"))
        {
            String type = attributes.getValue("type");
            String name = attributes.getValue("name");

            int upgradePoints = parseInt(attributes.getValue("up"));

            Item item = new Item(null, name, type, upgradePoints);
            listObjects.add(item);
        }
    }
}
