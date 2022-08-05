package me.miquiis.devlog.common.utils;

import java.util.Random;

public class MathUtils {

    public static boolean chance(int chance)
    {
        double m = Math.random() * 100;
        return m < chance;
    }

    public static int getRandomMax(int length)
    {
        Random random = new Random();
        return random.nextInt(length);
    }

    public static double getRandomMinMax(double min, double max)
    {
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    public static int getRandomMinMax(int min, int max)
    {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}
