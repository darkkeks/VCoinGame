package ru.darkkeks.vcoin.game.hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class WordGenerator {

    private ArrayList<String> words;
    private Random random;

    public WordGenerator(WordDescGenerator generator) {
        random = new Random();
        words = generator.getWords();
    }

    public WordGenerator(InputStream wordSource) {
        words = new ArrayList<>();
        random = new Random();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(wordSource))) {
            reader.lines().forEach(words::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getWord() {
        return words.get(random.nextInt(words.size()));
    }
}
