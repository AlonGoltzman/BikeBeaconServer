package com.bikebeacon.utils.compression;

import com.bikebeacon.utils.compression.CompressRunnable;
import com.bikebeacon.utils.compression.UncompressRunnable;
import com.bikebeacon.pojo.BaseUtilClass;
import com.bikebeacon.pojo.CCHResponders;
import com.bikebeacon.pojo.TaskCompletionListener;
import org.jetbrains.annotations.Nullable;

import java.io.*;

import static com.bikebeacon.utils.PrintUtil.log;

public class CompressionUtil extends BaseUtilClass {

    private static final int RESPONDER_CODE = CCHResponders.COMPRESSION.getId();

    private File compressedFile;
    private File uncompressedFile;

    public CompressionUtil(TaskCompletionListener listener) {
        super(listener);
        setRunnable("compress", new CompressRunnable(this));
        setRunnable("uncompress", new UncompressRunnable(this));
    }

    @Override
    public void failed(@Nullable Object reason) {
        if (reason != null) {
            if (reason instanceof Exception)
                listener.onFailed(RESPONDER_CODE, (Throwable) reason);
        } else
            listener.onFailed(RESPONDER_CODE, null);
    }

    @Override
    public void success(@Nullable Object... results) {
        listener.onSuccess(RESPONDER_CODE, results);
    }



    public String getName(File file) {
        String name = file.getName();
        String[] parts = name.split(".");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++)
            builder.append(parts[i]).append(".");

        builder.append("gzip");
        return builder.toString();
    }

    public File getCompressedFile() {
        return compressedFile;
    }

    public File getUncompressedFile() {
        return uncompressedFile;
    }

    public void setCompressedFile(File compressedFile) {
        this.compressedFile = compressedFile;
    }

    public void setUncompressedFile(File uncompressedFile) {
        this.uncompressedFile = uncompressedFile;
    }
}
