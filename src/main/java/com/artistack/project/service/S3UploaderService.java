package com.artistack.project.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3UploaderService {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	// 단일 파일 업로드
	public String uploadFile(MultipartFile multipartFile) throws IOException {
		String fileUrl;

		String fileName = creatFileName(multipartFile.getOriginalFilename());
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(multipartFile.getSize());
		objectMetadata.setContentType(multipartFile.getContentType());

		try(InputStream inputStream = multipartFile.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		fileUrl = amazonS3.getUrl(bucket, fileName).toString();
		System.out.println("fileName : " + fileName);
		System.out.println("fileUrl : " + fileUrl);

		return fileUrl;
	}

	public void deleteFile(String fileName) {
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}

	// 파일명을 난수화하기 위해 random 사용
	private String creatFileName(String fileName) {
		if (getFileExtension(fileName).equals("mp4")) {
			return "video/" + UUID.randomUUID().toString().concat(getFileExtension(fileName));
		}
		return UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	private String getFileExtension(String fileName) {
		try {
			int pos = fileName.lastIndexOf(".");
			return fileName.substring(pos+1);
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
