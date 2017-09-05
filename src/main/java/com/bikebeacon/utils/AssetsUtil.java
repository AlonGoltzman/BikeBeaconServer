package com.bikebeacon.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import static com.bikebeacon.pojo.Constants.ASSETS_FOLDER;
import static com.bikebeacon.utils.PrintUtil.error;
import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log;

import java.io.*;
import java.util.ArrayList;

public class AssetsUtil {


    public static FileContentDistributor load(String fileName) {
        return new FileContentDistributor(new File(ASSETS_FOLDER, fileName));
    }

    public static void save(JsonObject data) {
        try (Writer writer = new FileWriter(new File(ASSETS_FOLDER, "alert.json"))) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            error_f("AssetsUtil->save", "Failed during save.\n%s", e.getMessage());
        }
    }

    public static boolean ensureFileReady(File file) {
        File parent = file.getParentFile();
        if (!parent.exists())
            if (!parent.mkdirs()) {
                error("AssetsUtil->ensureFileReady", "Failed creating directories for file.");
                return false;
            }
        if (file.exists())
            if (!file.delete()) {
                log("AssetsUtil->ensureFileReady", "Failed deleting file.");
                return false;
            }
        if (!file.exists())
            try {
                if (!file.createNewFile()) {
                    log("AssetsUtil->ensureFileReady", "Failed creating new file.");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        return true;

    }

    public static class FileContentDistributor {
        private ArrayList<String> content;

        private File file;

        public FileContentDistributor(File fileToLoad) {
            file = fileToLoad;
            content = new ArrayList<>();
        }

        public FileContentDistributor extractContent() {
            try (BufferedReader stream = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = stream.readLine()) != null)
                    content.add(line);
            } catch (FileNotFoundException e) {
                error_f("AssetsUtil->FileContentDistributer->extractContent", "File %s was not found.\n%s",
                        file.getName(), e.getMessage());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return this;
        }

        public String getLine(int lineNum) {
            if (content == null)
                throw new IllegalStateException("File's contents were not extracted.");
            return content.get(lineNum - 1);
        }

    }

}
