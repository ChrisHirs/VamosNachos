package com.example.marcschnaebe.util;

import java.util.Random;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Util {

    /**
     * Double aléatoire
     *
     * @param min nombre minimum
     * @param max nombre maximum
     * @return nombre aléatoire double
     */
    public static double randomDouble(double min, double max) {
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }
}