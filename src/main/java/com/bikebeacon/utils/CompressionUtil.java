package com.bikebeacon.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.bikebeacon.utils.PrintUtil.log;

public class CompressionUtil {

    private static CompressionUtil compUtil;
    private static final int BUFFER_SIZE = 1024;

    public static CompressionUtil getUtil() {
        return compUtil == null ? new CompressionUtil() : compUtil;
    }

    private CompressionUtil() {
        compUtil = this;
    }

    public File compress(File compressFrom) {
        byte[] buffer = new byte[BUFFER_SIZE];
        File output = new File(compressFrom.getParentFile(), getName(compressFrom));
        if (!ensureEmptyFile(output))
            return null;
        try (InputStream in = new FileInputStream(compressFrom);
             GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output))) {
            int aRead;
            while ((aRead = in.read(buffer)) != -1)
                out.write(buffer, 0, aRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public void uncompress(File uncompressFrom, File uncompressTo) {
        byte[] buffer = new byte[BUFFER_SIZE];
        if (!ensureEmptyFile(uncompressFrom))
            return;
        try (OutputStream out = new FileOutputStream(uncompressTo);
             GZIPInputStream in = new GZIPInputStream(new FileInputStream(uncompressFrom))) {
            int aRead;
            while ((aRead = in.read(buffer)) != -1)
                out.write(buffer, 0, aRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean ensureEmptyFile(File file) {
        if (file.exists())
            if (!file.delete()) {
                log("CompresionUtil->compress", "Failed deleting file.");
                return false;
            }
        if (!file.exists())
            try {
                if (!file.createNewFile()) {
                    log("CompressionUtil->compress", "Failed creating new file.");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return true;
    }

    private String getName(File file) {
        String name = file.getName();
        String[] parts = name.split(".");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++)
            builder.append(parts[i]).append(".");

        builder.append("gzip");
        return builder.toString();
    }
}
