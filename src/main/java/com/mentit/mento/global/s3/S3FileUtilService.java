package com.mentit.mento.global.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3FileUtilService {

    public String upload(MultipartFile image);
    public void deleteImageFromS3(String imageAddress);
    public String getS3(String fileName);

}
