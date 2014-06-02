package com.marin.vnamit;

import com.cedarsoftware.util.io.JsonReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FileGen {

    public FileGen(){}

    /**
     * method to read the JSON data
     * @param JSONFile path to the json file
     */
    public void readJSON(String JSONFile) throws FileNotFoundException {
        // Reading JSON
        JsonReader jr = new JsonReader(new FileInputStream(JSONFile));
        HashMap map = null;
        try {
            map = (HashMap) jr.readObject();
        } catch (IOException e) {
            System.out.println("Invalid JSON!");
        }

//        Iterator iter = map.entrySet().iterator();
//        while(iter.hasNext()) {
//            Map.Entry pairs = (Map.Entry) iter.next();
//            System.out.println(pairs.getKey() + ":");
//        }

        //
        Object [] tempHeaders = (Object []) map.get("Headers");
        String [] headers = Arrays.copyOf(tempHeaders, tempHeaders.length, String[].class);

        Object [] tempData = (Object []) map.get("Data");
        List<List<String>> data = new ArrayList<List<String>>();

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
            data.add(row);
        }

//        String [][] data = Arrays.copyOf(tempData, tempData.length, String[][].class);
//        for (int i = 0; i < data.length; i++) {
//            data[i] = Arrays.copyOf(tempData[i], tempData[i].length, )
//        }

        long numHeaders = (Long)map.get("NumHeaders");

        List<Integer> indexArray = new ArrayList<Integer>();
        for(int i = 0; i <  headers.length; i++) {
            indexArray.add(i);
        }

        List<List<Integer>> indexCombinations = createCombinations(indexArray, numHeaders);

        List<List<List<String>>> fileCombinations = new ArrayList<List<List<String>>>();

        List<List<String>> fileData;
        List<String> rowData;
        List<Integer> indices;

        // pull out into a function of its own

        for(int i = 0; i < indexCombinations.size(); i++) {
            fileData = new ArrayList<List<String>>();
            rowData = new ArrayList<String>();
            indices = indexCombinations.get(i);
            // creating headers
            for(int j = 0; j < indices.size(); j++) {
                rowData.add(headers[indices.get(j)]);
            }
            // adding headers
            fileData.add(rowData);
            // actual data
            for (int k = 0; k < data.size(); k++) {
                rowData = new ArrayList<String>();
                for (int l = 0; l < indices.size(); l++) {
                    rowData.add(data.get(k).get(indices.get(l)));
                }
                fileData.add(rowData);
            }
            fileCombinations.add(fileData);
        }

    }

    List<List<Integer>> createCombinations(List<Integer> indexArray, long comboSize) {
        List<List<Integer>> combinations = new ArrayList<List<Integer>>();

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
                    comboList = createComboHelper(indexArray.get(i), indexArray.subList(i+1, indexArray.size()), comboSize - 1);
                    for (int j = 0; j < comboList.size(); j++) {
                        combinations.add(comboList.get(j));
                    }
                }
            }
        }

        return combinations;
    }

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
//
        FileGen fg = new FileGen();
        try {
            fg.readJSON(JSONFile);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
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
