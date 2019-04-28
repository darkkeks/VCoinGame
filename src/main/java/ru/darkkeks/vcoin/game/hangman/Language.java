package ru.darkkeks.vcoin.game.hangman;

import java.util.HashMap;
import java.util.Map;

public class Language {

    private String langPattern;
    private WordGenerator generator;
    private WordDescGenerator descGenerator;

    private Map<String, String> replace;

    public Language(String langPattern, WordGenerator generator, WordDescGenerator descGenerator) {
        this.langPattern = langPattern;
        this.generator = generator;
        this.descGenerator = descGenerator;
        this.replace = new HashMap<>();
    }

    public Language(String langPattern, WordDescGenerator descGenerator) {
        this(langPattern, new WordGenerator(descGenerator), descGenerator);
    }

    public String getLangPattern() {
        return langPattern;
    }

    public String getWord() {
        return generator.getWord();
    }

    public String getDescWord() {
        return descGenerator.getWord();
    }

    public String getDefinition(String word) {
        return descGenerator.getDefinition(word);
    }

    public void addReplace(String from, String to) {
        replace.put(from, to);
    }

    public Map<String, String> getReplace() {
        return replace;
    }
}

