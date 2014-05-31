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

        Object [] data = (Object []) map.get("Data");

        long numHeaders = (Long)map.get("NumHeaders");

        int [] headerIndices = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            headerIndices[i] = i;
        }


    }

    List<List<Integer>> createCombinations(List<Integer> indexArray, int comboSize) {
        List<List<Integer>> combinations = null;
        for(int i = 0; i < indexArray.size(); i++) {
            List<Integer> comboList = new ArrayList<Integer>();
            if (comboSize <= indexArray.size() - i)
                comboList = createComboHelper(indexArray.get(i), indexArray.subList(i+1, indexArray.size()), comboSize - 1);
            if (comboList.size() == comboSize)
                combinations.add(comboList);
        }

        return combinations;
    }

    List<Integer> createComboHelper(int num, List<Integer> indexArray, int comboSize) {
        if (comboSize > 0) {
            List<Integer> comboList = new ArrayList<Integer>();
            comboList.add(num);
            for(int i = 0; i < indexArray.size(); i++) {
                comboList.add(indexArray.get(i));
                if(comboSize <= indexArray.size() - i){
                    // Could be useful
                    // http://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java
                }
            }


            return null;
        }
        else{
            return null;
        }
    }

    public static void main(String[] args) {
        String JSONFile = "files/test1.json";

        FileGen fg = new FileGen();
        try {
            fg.readJSON(JSONFile);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        }

        System.out.println("Done.");
    }
}
