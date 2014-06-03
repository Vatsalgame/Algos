package com.marin.vnamit;

import com.cedarsoftware.util.io.JsonReader;

import java.io.*;
import java.util.*;

/**
 * This class is responsible for generating files in the bulk format.
 * The way this class works is that it creates all possible combinations of headers of size numHeaders and then
 * creates & populates files for each combination. The populating data is selected from the List data.
 * @author vnamit
 */

public class FileGen {

    // Headers for the files to be generated
    String[] headers;
    // Num of headers in each file
    long numHeaders;
    // The data with which the files will be populated
    List<List<String>> data;

    public FileGen(){}

    /**
     * Method to create files in the bulk format (i.e., tab (\t) separated values)
     * @param inputFile path to the input JSON file with the required information;
     *                  expected to have Headers, NumHeaders, and Data.
     * @param outputPath path to the folder where the files are to be created.
     */
    public void createFiles(String inputFile, String outputPath) throws FileNotFoundException {

        // Reading the JSON data and populating all the necessary variables.
        readAndParseJSON(inputFile);

        // Creating an array of indices to each of the headers.
        List<Integer> indexArray = new ArrayList<Integer>();
        for(int i = 0; i < this.headers.length; i++) {
            indexArray.add(i);
        }

        // Getting all the possible combinations of headers of size numHeaders.
        List<List<Integer>> indexCombinations = createCombinations(indexArray, this.numHeaders);

        // Getting all the files that would need to be created.
        List<List<List<String>>> fileCombinations = createFileCombinations(indexCombinations, this.headers, this.data);

        // Creating all the files.
        for(int i = 0; i < fileCombinations.size(); i++) {
            createFile(fileCombinations.get(i), outputPath, i);
        }

    }

    /**
     * Method to read the JSON data and subsequently parse it
     * @param JSONFile path to the json file
     *
     */
    public void readAndParseJSON(String JSONFile) throws FileNotFoundException {

        // Reading JSON using an external JSON parser
        JsonReader jr = new JsonReader(new FileInputStream(JSONFile));
        HashMap map = null;
        // Just protection against invalid files
        try {
            map = (HashMap) jr.readObject();
        } catch (IOException e) {
            System.out.println("Invalid JSON!");
        }

        // Reading the headers
        Object [] tempHeaders = (Object []) map.get("Headers");
        // The map created has generic objects. Need to copy it into a String array to use properly.
        this.headers = Arrays.copyOf(tempHeaders, tempHeaders.length, String[].class);

        // Reading the number of headers
        this.numHeaders = (Long)map.get("NumHeaders");

        // Reading the data that is to be associated with the headers.
        Object [] tempData = (Object []) map.get("Data");

        // Since the data read is basically a list of lists, we shall parse it so.
        this.data = new ArrayList<List<String>>();

        for(int i = 0; i < tempData.length; i++) {
            Object [] tempRow = (Object []) tempData[i];
            List<String> row = new ArrayList<String>();
            for(int j = 0; j < tempRow.length; j++) {
                try {
                    row.add((String)tempRow[j]);
                }
                catch (ClassCastException cce) {
                    Long num = (Long)tempRow[j];
                    row.add(num.toString());
                }
            }
            this.data.add(row);
        }
    }

    /**
     * Method to create a (txt) File with certain data
     * @param fileData the data to populate the files with
     * @param outputPath the folder where the created file will be stored
     * @param fileNum using to name the created files
     * @throws FileNotFoundException
     */
    void createFile (List<List<String>> fileData, String outputPath, int fileNum) throws FileNotFoundException {

        List<String> rowData;
        try {
            PrintWriter writer = new PrintWriter(outputPath + "file" + fileNum + ".txt", "UTF-8");
            for(int j = 0; j < fileData.size(); j++) {
                rowData = fileData.get(j);
                // Adding all the headers and then the corresponding data
                for(int k = 0; k < rowData.size(); k++) {
                    writer.print(rowData.get(k));
                    writer.print("\t");
                }
                writer.println();
            }
            writer.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println("File encoding not supported.");
        }
    }


    /**
     * Method to create the various file combinations that need to be created, i.e., pairing up the various header
     * combination with their respective data.
     * @param indexCombinations header combination represented by their indices for easy referral
     * @param headers the actual headers for the files
     * @param data the data for the headers
     * @return all the files that need to be created
     */
    List<List<List<String>>> createFileCombinations (List<List<Integer>> indexCombinations,
                                                     String[] headers,  List<List<String>> data) {

        List<List<List<String>>> fileCombinations = new ArrayList<List<List<String>>>();
        List<List<String>> fileData;
        List<String> rowData;
        List<Integer> indices;

        // Adding all the different files that need to be created
        for(int i = 0; i < indexCombinations.size(); i++) {
            fileData = new ArrayList<List<String>>();
            rowData = new ArrayList<String>();
            indices = indexCombinations.get(i);
            // Getting the apt headers for the current file
            for(int j = 0; j < indices.size(); j++) {
                rowData.add(headers[indices.get(j)]);
            }
            // Adding the headers
            fileData.add(rowData);
            // Adding the corresponding data
            for (int k = 0; k < data.size(); k++) {
                rowData = new ArrayList<String>();
                for (int l = 0; l < indices.size(); l++) {
                    rowData.add(data.get(k).get(indices.get(l)));
                }
                fileData.add(rowData);
            }
            fileCombinations.add(fileData);
        }

        return fileCombinations;
    }

    /**
     * Method to create all the possible combinations of elements in indexArray of size comboSize
     * @param indexArray all the indices (representing the headers)
     * @param comboSize size of each combination that is to be created
     * @return all the combinations of size comboSize
     */
    List<List<Integer>> createCombinations(List<Integer> indexArray, long comboSize) {

        List<List<Integer>> combinations = new ArrayList<List<Integer>>();

        // If comboSize is just one, then just simple return each element from the indexArray
        if (comboSize == 1) {
            for (int i = 0; i < indexArray.size(); i++) {
                List<Integer> comboList = new ArrayList<Integer>();
                comboList.add(indexArray.get(i));
                combinations.add(comboList);
            }
        }

        else {
            for(int i = 0; i < indexArray.size(); i++) {
                List<List<Integer>> comboList;
                if (indexArray.size() - i >= comboSize) {
                    // For each item in the indexArray, if a combination of size comboSize is possible, then
                    // take that item and recurse on the rest of the array
                    comboList = createComboHelper(indexArray.get(i), indexArray.subList(i+1, indexArray.size()), comboSize - 1);
                    for (int j = 0; j < comboList.size(); j++) {
                        combinations.add(comboList.get(j));
                    }
                }
            }
        }

        return combinations;
    }

    /**
     * Method to help createCombinations
     * @param head element that should go in front of each combination created
     * @param indexArray all the indices (representing the headers)
     * @param comboSize size of each combination that is to be created
     * @return all the combinations of size comboSize
     */
    List<List<Integer>> createComboHelper(int head, List<Integer> indexArray, long comboSize) {

        if (comboSize == 1) {
            List<List<Integer>> combos = new ArrayList<List<Integer>>();
            for (int i = 0; i < indexArray.size(); i++) {
                List<Integer> comboList = new ArrayList<Integer>();
                comboList.add(head);
                comboList.add(indexArray.get(i));
                combos.add(comboList);
            }
            return combos;
        }
        else {
            List<List<Integer>> combos = new ArrayList<List<Integer>>();
            for (int i = 0; i < indexArray.size(); i++) {
                List<List<Integer>> tempCombos;
                if(indexArray.size() - i >= comboSize) {
                    // For each item in the indexArray, if a combination of size comboSize is possible, then
                    // take that item and recurse on the rest of the array
                    tempCombos = createComboHelper(indexArray.get(i), indexArray.subList(i+1, indexArray.size()), comboSize - 1);
                    for (int j = 0; j < tempCombos.size(); j++) {
                        List<Integer> combo = tempCombos.get(j);
                        combo.add(0, head);
                        combos.add(combo);
                    }
                }
            }
            return combos;
        }
    }

    public static void main(String[] args) {

        String JSONFile = "files/test1.json";
        FileGen fg = new FileGen();
        try {
            fg.createFiles(JSONFile, "files/");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Done.");

        // Testing createComboHelper
//        int head = 1, comboSize = 2;
//        List<Integer> indexArray = new ArrayList<Integer>();
//        indexArray.add(2);
//        indexArray.add(3);
//        indexArray.add(4);

//        fg.createComboHelper(head, indexArray, comboSize); // WORKS!!!

        // Testing createCombinations
//        int comboSize = 2;
//        List<Integer> indexArray = new ArrayList<Integer>();
//        indexArray.add(1);
//        indexArray.add(2);
//        indexArray.add(3);
//        indexArray.add(4);
//
//        List<List<Integer>> combos = fg.createCombinations(indexArray, comboSize); // WORKS TOO!!! Tested with 2 & 3

    }
}
