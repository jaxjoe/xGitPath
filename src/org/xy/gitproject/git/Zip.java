/**
 * 
 */
package org.xy.gitproject.git;

/**
 * @author xingxiangyang
 *        165653096@qq.com
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/***
 * Class provides method to zip/unzip files using the java.util.zip package.
 */
public class Zip {
    /**
     * Zips file/folder and subfolders specified in src to destination.
     * @param src File/folder to compress.
     * @param dest Zip file.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void zip(Path src, Path dest) throws FileNotFoundException, IOException {
        String sourceFile = src.toAbsolutePath().toString();
        FileOutputStream fos = new FileOutputStream(dest.toAbsolutePath().toString());
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
 
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }
    
    /***
     * Unzips zipfile to specified destination.
     * @param src Zip file.
     * @param dest Uncompressed folder.
     */
    public static void unzip(File src, Path dest) {
        unzipFile(src.getAbsolutePath(), dest.toAbsolutePath().toString());
    }
    
   private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
    
   private static void unzipFile(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                if (ze.isDirectory()) {
                    Files.createDirectory(Paths.get(destDir, ze.getName()));
                } else {
                    String fileName = ze.getName();
                    File newFile = new File(destDir + File.separator + fileName);
                    System.out.println("Unzipping to "+newFile.getAbsolutePath());
                    //create directories for sub directories in zip
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    }
                    fos.close();
                    //close this ZipEntry
                    zis.closeEntry();
                }

                ze = zis.getNextEntry();                
            }

            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
