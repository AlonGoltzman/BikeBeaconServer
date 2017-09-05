package com.bikebeacon.utils.compression;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import static com.bikebeacon.utils.AssetsUtil.ensureFileReady;

public class CompressRunnable extends BaseCompressionRunnable {

    private File compressFrom;

    public CompressRunnable(CompressionUtil util) {
        super(util);
        compressFrom = util.getCompressedFile();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        File output = new File(compressFrom.getParentFile(), cUtil.getName(compressFrom));
        try {
            if (!ensureFileReady(output)) {
                cUtil.failed(null);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            cUtil.failed(e);
        }
        try (InputStream in = new FileInputStream(compressFrom);
             GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output))) {
            int aRead;
            while ((aRead = in.read(buffer)) != -1)
                out.write(buffer, 0, aRead);
        } catch (IOException e) {
            e.printStackTrace();
            cUtil.failed(e);
        }
        cUtil.success(output);
    }
}
