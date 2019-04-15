package ru.darkkeks.vcoin.game.hangman;

import ru.darkkeks.vcoin.game.Launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class WordGenerator {

    private static final String WORD_LIST = "/words.txt";

    private ArrayList<String> words;
    private Random random;

    public WordGenerator() {
        words = new ArrayList<>();
        random = new Random();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(Launcher.class.getResourceAsStream(WORD_LIST)))) {
            reader.lines().forEach(words::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new WordGenerator();
    }

    public String getWord() {
        return words.get(random.nextInt(words.size()));
    }
}
