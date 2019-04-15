package ru.darkkeks.vcoin.game.vk;

import java.util.Random;

public class VkUtil {

    private static Random random = new Random();

    public static int randomId() {
        return random.nextInt();
    }
}
