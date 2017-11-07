package com.example.marcschnaebe.util;


import com.example.marcschnaebe.mynacho.Nachos;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by christop.hirschi on 03.11.2017.
 */

public class SourceHandler extends DefaultHandler {
    ArrayList<Nachos> listNachos = new ArrayList<>();

    boolean bNachosName = false;

    public SourceHandler(ArrayList<Nachos> nachos)
    {
        this.listNachos = nachos;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if (bNachosName)
        {
            bNachosName = false;

            Nachos n = listNachos.get(listNachos.size() - 1);
            String name = new String(ch, start, length);
            n.setName(name);
        }
    }

    @Override
    public void endDocument() throws SAXException
    {
        super.endDocument();
        //Log.d("read", "end of document :  " + listNachos.toString());
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
        //Log.d("read", "Dealing with element " + qName);
        if (qName.equalsIgnoreCase("nachos"))
        {
            String type = attributes.getValue("type");

            int level = parseInt(attributes.getValue("level"));
            int xpCurrent = parseInt(attributes.getValue("xpCurrent"));
            int xpMax = parseInt(attributes.getValue("xpMax"));

            int ap = parseInt(attributes.getValue("ap"));
            int hpCurrent = parseInt(attributes.getValue("hpCurrent"));
            int hpMax = parseInt(attributes.getValue("hpMax"));

            int hpBonus = parseInt(attributes.getValue("hpBonus"));
            int apBonus = Integer.parseInt(attributes.getValue("apBonus"));

            Nachos nachos = new Nachos(null, "", type, level, xpCurrent, xpMax, ap, hpCurrent, hpMax, hpBonus, apBonus);
            listNachos.add(nachos);
        }
        else if (qName.equalsIgnoreCase("name"))
        {
            bNachosName = true;
        }
    }
}
