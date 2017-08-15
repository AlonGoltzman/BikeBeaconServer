package com.bikebeacon.utils;

import static com.bikebeacon.utils.PrintUtils.error_f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AssetsUtil {

	private static final File parent = new File(System.getenv("user.dir") + "/assets");

	public static FileContentDistributer load(String fileName) {
		return new FileContentDistributer(new File(parent, fileName));
	}

	public static class FileContentDistributer {
		private ArrayList<String> content;

		private File file;

		public FileContentDistributer(File fileToLoad) {
			file = fileToLoad;
		}

		public FileContentDistributer extractContent() {
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
