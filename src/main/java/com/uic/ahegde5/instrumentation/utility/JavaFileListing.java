package com.uic.ahegde5.instrumentation.utility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adarsh Hegde
 * This class lists all java files in a directory
 *
 */

public class JavaFileListing {

    static final String FILE_TYPE = ".java";

    public static List<String> listFilesInDirectory(String dirPath, String backupPath) {
        File directory = new File(dirPath);
        File backupDirectory = new File(backupPath);
        backupDirectory.mkdir();

        List<String> listOfFiles = new ArrayList<>();

        File[] listOfFilesInDirectory = directory.listFiles();
        for(File file : listOfFilesInDirectory){

            if(file.isDirectory()){
                List<String> fileList = listFilesInDirectory(file.toPath().toString(), backupPath + "\\" + file.getName());
                if(null != fileList && !fileList.isEmpty())
                    listOfFiles.addAll(fileList);

            } else if(file.isFile()){
                if(file.getName().endsWith(FILE_TYPE)){
                    File backupFile = new File(backupPath + "\\old_" + file.getName());
                    try {
                        FileUtils.copyFile(file,backupFile);
                    } catch (IOException e) {
                        System.out.println("Error while creating backup file for file " + file.getPath()
                                + " error message " + e);
                    }
                    listOfFiles.add(file.getPath());
                }

            }
        }
        return listOfFiles;
    }

    /*public static void main(String[] args){
        JavaFileListing fileListing = new JavaFileListing();
        List<String> fileList = fileListing.listFilesInDirectory("D:\\IntellijWorkspace\\OOLE\\adarsh_hegde_hw1\\src","D:\\backup\\adarsh_hegde_hw1\\src");

        System.out.println("Number of files : " + fileList.size());
        for(String fileName : fileList){
            System.out.println("File name >" + fileName);
        }

    }*/
}
