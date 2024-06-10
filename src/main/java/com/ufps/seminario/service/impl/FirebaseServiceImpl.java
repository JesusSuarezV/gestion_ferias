package com.ufps.seminario.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.ufps.seminario.service.FirebaseService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FirebaseServiceImpl implements FirebaseService {
    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String bucketName = "fpaspringboot.appspot.com";
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);
        String fileName = multipartFile.getOriginalFilename();
        Blob blob = bucket.create(fileName, multipartFile.getInputStream(), multipartFile.getContentType());
        return "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + fileName + "?alt=media";
    }
}
