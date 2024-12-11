import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Phoneme> phonemes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
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

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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