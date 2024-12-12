import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Phoneme> phonemes = processCSV(args[0]);
    }

    static List<Phoneme> processCSV(String fileName){
        List<Phoneme> phonemes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String[] featureNames = reader.readLine().split(",");
            String line;
            while ((line = reader.readLine()) != null) {  //process and store each phoneme
                String[] phonemeCells = line.split(",");
                String phonemeName = phonemeCells[0];
                Map<String,Boolean> phonemeFeatures = new HashMap<>();
                for (int i = 1;i<phonemeCells.length;i++){
                    phonemeFeatures.put(featureNames[i],booleanOf(phonemeCells[i]));
                }
                phonemes.add(new Phoneme(phonemeName,phonemeFeatures));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return phonemes;
    }
    static boolean booleanOf(String s){
        if (s.equals("+")) {
            return true;
        }
        else if (s.equals("-")){
            return false;
        }
        throw new IllegalArgumentException("feature values for phonemes must b '+' or '-' ");


    }
}