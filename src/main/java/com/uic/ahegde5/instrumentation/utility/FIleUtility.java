package com.uic.ahegde5.instrumentation.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FIleUtility {

    public static String readFileToString(String s) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(s)));
        return content;
    }
}
