package asia.fourtitude.interviewq.jumble.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class JumbleEngine {

    private List<String> wordList;

    public JumbleEngine() {
        this.wordList = loadWords();
        if (this.wordList == null){
            this.wordList = new ArrayList<>();
        }
    }

    private List<String> loadWords() {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/words.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public String scramble(String word) {
        if (word == null || word.length() <=1){
            return word;
        }
        List<String> letters = Arrays.asList(word.split(""));
        String scrambledWord;
        do { 
            Collections.shuffle(letters);
            scrambledWord = String.join("", letters);
        } while (scrambledWord.equals(word));
        return scrambledWord;
    }

    public Collection<String> retrievePalindromeWords() {
        return wordList.stream()
                .filter(word -> word.length() > 1 && isPalindrome(word))
                .collect(Collectors.toList());
    }

    private boolean isPalindrome(String word) {
        int length = word.length();
        for (int i = 0; i < length / 2; i++) {
            if (word.charAt(i) != word.charAt(length - i - 1)) {
                return false;
            }
        }
        return true;
    }


    public String pickOneRandomWord(Integer length) {
        if (length == null || length < 1){
            return null;
        }
        List<String> filteredWords = wordList.stream()
                .filter(word -> word.length() == length)
                .collect(Collectors.toList());
        if (filteredWords.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return filteredWords.get(random.nextInt(filteredWords.size()));
    }


    public boolean exists(String word) {
        if (word == null || word.trim().isEmpty()){
            return false;
        }
        return wordList.contains(word.toLowerCase());
    }


    public Collection<String> wordsMatchingPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty() || !prefix.matches("[a-zA-Z]+")) {
            return Collections.emptyList();
        }
        String lowerPrefix = prefix.toLowerCase();
        return wordList.stream()
                .filter(word -> word.startsWith(lowerPrefix))
                .collect(Collectors.toList());
    }


    public Collection<String> searchWords(Character startChar, Character endChar, Integer length) {
        if (startChar == null && endChar == null && (length == null || length < 1)) {
            return Collections.emptyList();
        }
        return wordList.stream()
                .filter(word -> (startChar == null || word.charAt(0) == Character.toLowerCase(startChar)) &&
                        (endChar == null || word.charAt(word.length() - 1) == Character.toLowerCase(endChar)) &&
                        (length == null || word.length() == length))
                .collect(Collectors.toList());
    }


    public Collection<String> generateSubWords(String word, Integer minLength) {
        Set<String> subWords = new HashSet<>();
        if (word.length() < minLength) {
            return subWords;
        }
        for (int i = 0; i < word.length(); i++) {
            for (int j = i + minLength; j <= word.length(); j++) {
                String subWord = word.substring(i, j);
                if (subWord.length() >= minLength && wordList.contains(subWord)) {
                    subWords.add(subWord);
                }
            }
        }
        return subWords;
    }

    // Existing createGameState method
    public GameState createGameState(Integer length, Integer minLength) {
        Objects.requireNonNull(length, "length must not be null");
        if (minLength == null) {
            minLength = 3;
        } else if (minLength <= 0) {
            throw new IllegalArgumentException("Invalid minLength=[" + minLength + "], expect positive integer");
        }
        if (length < 3) {
            throw new IllegalArgumentException("Invalid length=[" + length + "], expect greater than or equals 3");
        }
        if (minLength > length) {
            throw new IllegalArgumentException("Expect minLength=[" + minLength + "] greater than length=[" + length + "]");
        }
        String original = this.pickOneRandomWord(length);
        if (original == null) {
            throw new IllegalArgumentException("Cannot find valid word to create game state");
        }
        String scramble = this.scramble(original);
        Map<String, Boolean> subWords = new TreeMap<>();
        for (String subWord : this.generateSubWords(original, minLength)) {
            subWords.put(subWord, Boolean.FALSE);
        }
        return new GameState(original, scramble, subWords);
    }

    public Collection<String> searchWords(String startChar, String endChar, Integer length) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchWords'");
    }
}
