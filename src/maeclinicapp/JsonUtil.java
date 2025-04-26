/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package maeclinicapp;
import java.io.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/**
 *
 * @author USER
 */
public class JsonUtil {
 private static final String BASE_PATH = "src/maeclinicapp/";

    public static JSONObject readJsonFile(String fileName) throws IOException, org.json.simple.parser.ParseException {
        String path = resolvePath(fileName);
        File file = new File(path);
        
        // If file doesn't exist, create an empty JSONObject file
        if (!file.exists()) {
            JSONObject emptyObj = new JSONObject();
            writeJsonFile(fileName, emptyObj);
        }

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(path)) {
            return (JSONObject) parser.parse(reader);
        }
    }

    public static JSONArray readJsonArrayFile(String fileName) throws IOException, org.json.simple.parser.ParseException {
        String path = resolvePath(fileName);
        File file = new File(path);
        
        // If file doesn't exist, create an empty JSONArray file
        if (!file.exists()) {
            JSONArray emptyArray = new JSONArray();
            writeJsonArrayFile(fileName, emptyArray);
        }

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(path)) {
            return (JSONArray) parser.parse(reader);
        }
    }

    public static void writeJsonFile(String fileName, JSONObject jsonObject) throws IOException {
        String path = resolvePath(fileName);
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(jsonObject.toJSONString());
            writer.flush();
        }
    }

    public static void writeJsonArrayFile(String fileName, JSONArray jsonArray) throws IOException {
        String path = resolvePath(fileName);
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(jsonArray.toJSONString());
            writer.flush();
        }
    }

    private static String resolvePath(String fileName) {
        // If the fileName is an absolute path, use it directly
        File file = new File(fileName);
        if (file.isAbsolute()) {
            return fileName;
        }
        // Otherwise, prepend BASE_PATH
        return BASE_PATH + fileName;
    }
}