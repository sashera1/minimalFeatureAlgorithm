import java.io.*;
import java.util.*;

public class Main {
    static int operations = 0;
    static List<String> featureNames = new ArrayList<>();
    static List<String> phonemeNames = new ArrayList<>();
    static List<String[]> matrix = new ArrayList<>();
    static List<String> inputPhonemes = new ArrayList<>();
    public static void main(String[] args) {
        processCSV("src/FullTest2.csv");
        filterMatrixByPhonemes(inputPhonemes); //preserve only rows of the phonemes in the set
        System.out.println("\nSelected Phonemes With All Features:");
        printMatrix();
        getRidOfZeros(); //turn any '0' into '-'
        removeUniformColumns(); //columns of all '-' or '+' are removed
        System.out.println("\nAfter Removing Uniform Columns:");
        printMatrix();
        removeIdenticalAndInverseColumns(); //sets of columns identical or inverse to one another add no additional data: all but one per set may be removed
        System.out.println("\nAfter Removing Identical/Inverse Columns:");
        printMatrix();
        List<Integer> necessaryColumns = findNecessaryColumns(); //for any phoneme comparison differentiated by a single feature, that feature will be needed in the final formulation
        System.out.println("\nNecessary Columns Regardless of final formulation:");
        for (int col : necessaryColumns) {
            System.out.println(featureNames.get(col));
        }
        List<Integer> minimalSet = findMinimalDescriptiveSet(necessaryColumns); //optimized brute-force algorithm
        System.out.print("\nFinal Formulation of ");
        for (String phoneme : phonemeNames) System.out.print(phoneme+" ");
        System.out.println();
        for (int col : minimalSet) {
            System.out.println(featureNames.get(col));
        }
        System.out.println("\n finding the final formulation involved checking if " + operations + " sets distinguished all phonemes");

    }

    public static void processCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int rowIndex = 0;
            inputPhonemes = Arrays.asList(br.readLine().split(",")); //add input phonemes
            String[] features = br.readLine().split(",");
            featureNames.addAll(Arrays.asList(features).subList(1, features.length)); //add feature names
            String line;
            while ((line = br.readLine()) != null) { // add features for all phonemes
                String[] values = line.split(",");
                    phonemeNames.add(values[0]);
                    matrix.add(Arrays.copyOfRange(values, 1, values.length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void filterMatrixByPhonemes(List<String> inputPhonemes) {

        for (String phoneme : inputPhonemes){ //verify all selected phonemes have a row associated with them.
            if (!phonemeNames.contains(phoneme)) {
                throw new IllegalArgumentException("Phoneme " + phoneme + " not recognized.");
            }
        }
        List<String> filteredPhonemeNames = new ArrayList<>();
        List<String[]> filteredMatrix = new ArrayList<>();
        for (int i = 0; i < phonemeNames.size(); i++) { //cut rows from matrix not associated with a selected phoneme
            if (inputPhonemes.contains(phonemeNames.get(i))) {
                filteredPhonemeNames.add(phonemeNames.get(i));
                filteredMatrix.add(matrix.get(i));
            }
        }
        phonemeNames = filteredPhonemeNames;
        matrix = filteredMatrix;
    }

    public static void getRidOfZeros(){ //make all 0's '-'
        for (int i = 0; i<matrix.size();i++){
            for(int j = 0; j<matrix.get(0).length;j++){
                if ("0".equals(matrix.get(i)[j])){
                    matrix.get(i)[j] = "-";
                }
            }
        }
    }

    public static void removeUniformColumns() {
        int numRows = matrix.size();
        int numCols = featureNames.size();

        // Track columns to keep
        List<Integer> columnsToKeep = new ArrayList<>();

        for (int col = 0; col < numCols; col++) {
            boolean allPositive = true;
            boolean allNegative = true;

            for (int row = 0; row < numRows; row++) {
                String value = matrix.get(row)[col];
                if (!value.equals("+")) {
                    allPositive = false;
                }
                if (!value.equals("-")) {
                    allNegative = false;
                }
            }

            if (!(allPositive || allNegative)) {
                columnsToKeep.add(col); //keep track of indexes of columns to retain
            }
        }


        List<String> updatedFeatureNames = new ArrayList<>();
        List<String[]> updatedMatrix = new ArrayList<>();

        for (String[] row : matrix) { //retain only columns not uniform
            List<String> newRow = new ArrayList<>();
            for (int col : columnsToKeep) {
                newRow.add(row[col]);
            }
            updatedMatrix.add(newRow.toArray(new String[0]));
        }

        for (int col : columnsToKeep) { //do the same with assocaited features (column names)
            updatedFeatureNames.add(featureNames.get(col));
        }

        featureNames = updatedFeatureNames;
        matrix = updatedMatrix;
    }

    public static void removeIdenticalAndInverseColumns() {
        int numRows = matrix.size();
        int numCols = featureNames.size();

        // Track columns to keep
        List<Integer> columnsToKeep = new ArrayList<>();
        boolean[] processed = new boolean[numCols];

        for (int col1 = 0; col1 < numCols; col1++) { //iterate through columns, adding them to keep list.
            if (processed[col1]) continue;
            columnsToKeep.add(col1);
            processed[col1] = true;

            for (int col2 = col1 + 1; col2 < numCols; col2++) {
                if (processed[col2]) continue;

                boolean identical = true;
                boolean inverse = true;

                for (int row = 0; row < numRows; row++) {
                    String value1 = matrix.get(row)[col1];
                    String value2 = matrix.get(row)[col2];

                    if (!value1.equals(value2)) {
                        identical = false;
                    }
                    if (!value1.equals(invert(value2))) {
                        inverse = false;
                    }
                }

                if (identical || inverse) {
                    processed[col2] = true; //if a column is identical/inverse, adds them to "processed." Thus these columns will be skipped when its time to add them to columns to keep
                }
            }
        }

        List<String> updatedFeatureNames = new ArrayList<>();
        List<String[]> updatedMatrix = new ArrayList<>();

        for (String[] row : matrix) { //new matrix
            List<String> newRow = new ArrayList<>();
            for (int col : columnsToKeep) {
                newRow.add(row[col]);
            }
            updatedMatrix.add(newRow.toArray(new String[0]));
        }

        for (int col : columnsToKeep) { //new associated feature names
            updatedFeatureNames.add(featureNames.get(col));
        }

        featureNames = updatedFeatureNames;
        matrix = updatedMatrix;
    }
    public static String invert(String value) {
        if (value.equals("+")) return "-";
        if (value.equals("-")) return "+";
        return value; //
    }

    public static List<Integer> findNecessaryColumns() {
        int numRows = matrix.size();
        int numCols = featureNames.size();

        List<Integer> necessaryColumns = new ArrayList<>();

        for (int row1 = 0; row1 < numRows; row1++) {
            for (int row2 = row1 + 1; row2 < numRows; row2++) { //iterates through all pairs of rows
                int differingColumn = -1;


                for (int col = 0; col < numCols; col++) { //goes thought each feature for row pair
                    String value1 = matrix.get(row1)[col];
                    String value2 = matrix.get(row2)[col];

                    if (!value1.equals(value2)) {//if they differ
                        if (differingColumn != -1) { //if they already differed by a column, they now differ by multiple columns.
                            differingColumn = -1;
                            break; //Thus this row pair will not highligh a necessary column
                        }
                        differingColumn = col;
                    }
                }

                if (differingColumn != -1 && !necessaryColumns.contains(differingColumn)) {
                    necessaryColumns.add(differingColumn);
                }
            }
        }
        return necessaryColumns;
    }

    public static List<Integer> findMinimalDescriptiveSet(List<Integer> necessaryColumns ) {
        int numCols = featureNames.size();

        Set<Integer> baseSet = new HashSet<>(necessaryColumns);

        List<Integer> minimalSet = null;


        for (int setSize = baseSet.size(); setSize <= numCols; setSize++) { //generate powersets, from smaller to large

            List<List<Integer>> subsets = generateSubsets(baseSet, numCols, setSize); //generate all subsets of certain size

            for (List<Integer> subset : subsets) {

                if (!subset.containsAll(necessaryColumns)) continue; //ignore subsets not containing all necessary columns

                if (isDescriptive(subset)) {
                    minimalSet = subset;
                    return minimalSet;  //because we go from smaller to large subsets, any subset we find either is the smallest subset or is among them.
                }
            }
        }

        return minimalSet; // Return the minimal descriptive set
    }

    private static List<List<Integer>> generateSubsets(Set<Integer> baseSet, int numCols, int setSize) {
        List<List<Integer>> subsets = new ArrayList<>();
        List<Integer> baseList = new ArrayList<>(baseSet);

        for (int col = 0; col < numCols; col++) {
            if (!baseSet.contains(col)) baseList.add(col);
        }
        generateCombinations(subsets, new ArrayList<>(), baseList, 0, setSize); //because generateCombinations is recursive, list is instantiated here
        return subsets;
    }


    private static void generateCombinations(List<List<Integer>> subsets, List<Integer> current, List<Integer> remaining, int index, int size) {
        if (current.size() == size) { //base case: "current" is of desired size and is added to subset list
            subsets.add(new ArrayList<>(current));
            return;
        }

        for (int i = index; i < remaining.size(); i++) { //otherwise, loop through all elements in remaining (intially all elements, period) and recursively call this method
            current.add(remaining.get(i)); //adds element to current
            generateCombinations(subsets, current, remaining, i + 1, size); //call recursive method
            current.remove(current.size() - 1);
        }
    }

    private static boolean isDescriptive(List<Integer> subset) {
        int numRows = matrix.size();
        operations++; //not needed for algorithm, but added to assist in determining efficiency of algorithm

        for (int row1 = 0; row1 < numRows; row1++) {
            for (int row2 = row1 + 1; row2 < numRows; row2++) { //compares all pairs of rows
                boolean differs = false;

                for (int col : subset) {
                    if (!matrix.get(row1)[col].equals(matrix.get(row2)[col])) { //check for contrastiveness
                        differs = true;
                        break;
                    }
                }

                if (!differs) return false; //pair of phonemes not contrastive
            }
        }
        return true;
    }

    public static void printMatrix() {
        System.out.println(matrix.get(0).length + " Features: " + featureNames);
        for (String[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
