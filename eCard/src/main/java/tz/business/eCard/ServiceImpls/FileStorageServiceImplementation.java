package tz.business.eCard.ServiceImpls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import tz.business.eCard.services.FileStorageService;

@Service
public class FileStorageServiceImplementation implements FileStorageService {

    private final String basePath;
    private final Logger log = Logger.getLogger(String.valueOf(FileStorageServiceImplementation.class));

    public FileStorageServiceImplementation(@Value("${eCard.upload.base-path}") String basePath) {
        this.basePath = basePath;
    }

    public  String saveFile(byte[] fileBytes  ,String fileName ,String subPath) throws Exception{
        Path uploadPath = Paths.get(basePath,subPath);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath,fileBytes);

        log.info("File saved successfully at " + filePath.toString());
        return Paths.get(subPath,fileName).toString();
    };

    public  void deleteFile(String relativePath) throws Exception {
        Path filePath = Paths.get(basePath,relativePath);
        if(Files.exists(filePath)){
            Files.delete(filePath);
            log.info("File deleted successfully at " + filePath.toString());
        }else{
            log.info("File not found at " + filePath.toString());
        }
    };

    public  byte[] readFile(String relativePath) throws Exception{
        Path filePath = Paths.get(basePath,relativePath);
        if(Files.exists(filePath)){
            return Files.readAllBytes(filePath);
        }else{
            log.info("File not found at " + filePath.toString());
            return null;
        }
    };

    public  boolean fileExists(String relativePath) throws Exception {
        Path filePath = Paths.get(basePath,relativePath);
        if(Files.exists(filePath)){
            return true;
        }else{
            log.info("File not found at " + filePath.toString());
            return false;
        }
    };
}
