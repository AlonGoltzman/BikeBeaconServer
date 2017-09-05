package com.bikebeacon.utils.cloudconvert;

import com.bikebeacon.cch.Case;

import java.io.File;

public interface CloudConvertTaskListener {

    void onFileDownloaded(Case caseFile, File outputFile);

}
