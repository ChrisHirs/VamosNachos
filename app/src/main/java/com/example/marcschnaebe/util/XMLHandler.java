package com.example.marcschnaebe.util;


import android.content.Context;
import android.util.Log;

import com.example.marcschnaebe.mynacho.Item;
import com.example.marcschnaebe.mynacho.Nachos;
import com.example.marcschnaebe.mynacho.Player;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * XML handler class
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class XMLHandler {

    /* -------  Methods ------- */

    /**
     * Population player's team and bag from an XML file
     *
     * @param file XML file
     * @param player player object
     */
    public static void populateFromXMLFile(File file, Player player) {
        try {
            InputStream inputStream = new FileInputStream(file);

            //XML file parser
            JaxParser parser = new JaxParser(inputStream);

            //Set list created by XMl parser
            ArrayList<Object> objectsFromParser = parser.getObjects();
            for (Object obj : objectsFromParser) {
                if (obj.getClass() == Nachos.class) {
                    player.team.add((Nachos) obj);
                }
                else if (obj.getClass() == Item.class) {
                    player.bag.add((Item) obj);
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write XML file
     *
     * @param player player object
     */
    public static void writeXMLFile(Player player, Context context) {
        //XML String
        String string = "";

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();
            doc.setXmlStandalone(true);

            Element root = doc.createElement("objects");
            doc.appendChild(root);

            //For each Nachos in the list
            for (Nachos n : player.team)
            {
                //Add Nachos element
                Element nachos = doc.createElement("nachos");

                //Add Nachos attributes
                nachos.setAttribute("type", n.getType());
                nachos.setAttribute("name", n.getName());
                nachos.setAttribute("level", Integer.toString(n.getLevel()));
                nachos.setAttribute("xpCurrent", Integer.toString(n.getXpCurrent()));
                nachos.setAttribute("xpMax", Integer.toString(n.getXpMax()));
                nachos.setAttribute("ap", Integer.toString(n.getAp()));
                nachos.setAttribute("hpCurrent", Integer.toString(n.getHpCurrent()));
                nachos.setAttribute("hpMax", Integer.toString(n.getHpMax()));
                nachos.setAttribute("hpBonus", Integer.toString(n.getHpBonus()));
                nachos.setAttribute("apBonus", Integer.toString(n.getApBonus()));
                root.appendChild(nachos);
            }

            //For each Items in the list
            for (Item i : player.bag)
            {
                //Add Item element
                Element item = doc.createElement("item");

                //Add Item attributes
                item.setAttribute("type", i.getType());
                item.setAttribute("name", i.getName());
                item.setAttribute("up", Integer.toString(i.getUpgradePoints()));
                root.appendChild(item);
            }

            //Display and write XML string if no Nachos
            if (!player.team.isEmpty())
            {
                Transformer tf = TransformerFactory.newInstance().newTransformer();
                tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                Writer out = new StringWriter();
                tf.transform(new DOMSource(doc), new StreamResult(out));

                //Console displaying
                Log.d("xml", out.toString());

                //String writing
                string = out.toString();
            }


            //Write XML in output file
            FileOutputStream outputStream;

            outputStream = context.openFileOutput("objects", Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
