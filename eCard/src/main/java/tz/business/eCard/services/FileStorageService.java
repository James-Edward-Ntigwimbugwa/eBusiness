package tz.business.eCard.services;

public interface FileStorageService {

    public  String saveFile(byte[] fileBytes  , String fileName, String subPath) throws Exception;

    public  void deleteFile(String relativePath) throws Exception;

    public  byte[] readFile(String relativePath) throws Exception;

    public  boolean fileExists(String relativePath) throws Exception;
}

