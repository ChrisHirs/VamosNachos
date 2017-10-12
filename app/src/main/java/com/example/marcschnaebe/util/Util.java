package com.example.marcschnaebe.util;

import java.util.Random;

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
        Random random = new Random();
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
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

}