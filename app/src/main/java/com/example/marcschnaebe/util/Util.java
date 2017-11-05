package com.example.marcschnaebe.util;

import android.util.Log;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Util {

    /**
     * Integer aléatoire
     *
     * @param min nombre minimum
     * @param max nombre maximum
     * @return nombre aléatoire int
     */
    public static int randomInteger(int min, int max)
    {
        Random random = new Random(System.nanoTime());
        return random.nextInt(max + 1 - min) + min;
    }

    /**
     * Double aléatoire
     *
     * @param min nombre minimum
     * @param max nombre maximum
     * @return nombre aléatoire double
     */
    public static double randomDouble(double min, double max) {
        Random random = new Random(System.nanoTime());
        return min + (max - min) * random.nextDouble();
    }

}