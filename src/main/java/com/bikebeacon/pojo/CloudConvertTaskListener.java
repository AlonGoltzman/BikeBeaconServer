package com.bikebeacon.pojo;

import com.bikebeacon.cch.Case;

import java.io.File;

public interface CloudConvertTaskListener {

    void onFileDownloaded(Case caseFile, File outputFile);

}
