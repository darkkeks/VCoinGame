package ru.darkkeks.vcoin.game.hangman;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.darkkeks.vcoin.game.Launcher;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WordDescGenerator {

    private static final String WORD_DESC_JSON = "/words_with_desc.json";

    private static final JsonParser jsonParser = new JsonParser();

    private Random random;
    private ArrayList<String> words;
    private Map<String, String> definitions;

    public WordDescGenerator(InputStream wordSource) {
        random = new Random();
        words = new ArrayList<>();
        definitions = new HashMap<>();

        JsonArray jsonWords = jsonParser.parse(new InputStreamReader(wordSource)).getAsJsonArray();

        jsonWords.forEach(element -> {
            JsonObject object = element.getAsJsonObject();

            String word = object.get("word").getAsString();
            String definition = object.get("definition").getAsString();
            words.add(word);
            definitions.put(word, definition);
        });
    }

    public String getDefinition(String word) {
        return definitions.get(word);
    }

    public String getWord() {
        return words.get(random.nextInt(words.size()));
    }

    public ArrayList<String> getWords() {
        return words;
    }
}
