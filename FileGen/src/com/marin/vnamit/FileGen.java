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

    void createCombinations(List<Integer> indexArray, int comboSize) {
        for(int i = 0; i < indexArray.size(); i++) {

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
