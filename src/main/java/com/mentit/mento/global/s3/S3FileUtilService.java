package com.mentit.mento.global.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3FileUtilService {

     String upload(MultipartFile image);
     void deleteImageFromS3(String imageAddress);
     String getS3(String fileName);

}
