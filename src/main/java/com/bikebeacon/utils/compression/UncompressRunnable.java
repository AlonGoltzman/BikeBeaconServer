package com.bikebeacon.utils.compression;

import java.io.*;
import java.util.zip.GZIPInputStream;

import static com.bikebeacon.utils.AssetsUtil.ensureFileReady;

public class UncompressRunnable extends BaseCompressionRunnable {

    private File uncompressFrom;
    private File uncompressTo;

    public UncompressRunnable(CompressionUtil util) {
        super(util);
        uncompressFrom = util.getCompressedFile();
        uncompressTo = util.getUncompressedFile();
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            if (!ensureFileReady(uncompressFrom))
                return;
            try (OutputStream out = new FileOutputStream(uncompressTo);
                 GZIPInputStream in = new GZIPInputStream(new FileInputStream(uncompressFrom))) {
                int aRead;
                while ((aRead = in.read(buffer)) != -1)
                    out.write(buffer, 0, aRead);
            } catch (IOException e) {
                e.printStackTrace();
                cUtil.failed(e);
            }
            cUtil.success();
        } catch (Exception e) {
            e.printStackTrace();
            cUtil.failed(e);
        }
    }
}
