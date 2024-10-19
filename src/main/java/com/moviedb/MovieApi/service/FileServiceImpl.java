package com.moviedb.MovieApi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService{
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        // Get name of the file
        String fileName = file.getOriginalFilename();

        // Get file path
        String filePath = path + File.separator + fileName;

        // create a file object
        File f = new File(path);
        if(!f.exists()){
            try {
                boolean created = f.mkdirs();
                if (created) {
                    System.out.println("Directory created successfully: " + path);
                } else {
                    System.out.println("Failed to create directory: " + path);
                    System.out.println("Directory exists: " + f.exists());
                    System.out.println("Directory is writable: " + f.canWrite());
                    throw new IOException("Failed to create directory: " + path);
                }
            } catch (SecurityException se) {
                System.out.println("Security exception when creating directory: " + se.getMessage());
                throw new IOException("Security exception when creating directory", se);
            }
        }

        // copy the file or upload file to the path
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String path, String filename) throws FileNotFoundException {
        String filePath = path + File.separator + filename;
        return new FileInputStream(filePath);
    }
}
