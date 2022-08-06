package com.artistack.project.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.artistack.base.GeneralException;
import com.artistack.base.constant.Code;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3UploaderService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    // 제이) 단일 파일 업로드
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        return uploadFile(multipartFile, Optional.empty(), false);
    }

    /**
     * 메이슨) 단일 파일 업로드
     *
     * @param multipartFile 업로드할 파일
     * @param path          custom하게 앞에 붙일 경로(profile/post/image...). custom하게 안 붙이고 싶다면 Optional.empty()
     * @param saveThumbnail thumbnail 저장 여부(이미지에만 한정)
     * @return 저장할 경로
     */
    public String uploadFile(MultipartFile multipartFile, Optional<String> path, Boolean saveThumbnail)
        throws IOException {
        String fileUrl;

        String fileName = creatFileName(multipartFile.getOriginalFilename(), path);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            putS3(fileName, inputStream, objectMetadata);

            if (saveThumbnail) { // 메이슨이 추가함
                saveThumbnail(multipartFile, fileName, 100);
            }
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
    private String creatFileName(String fileName, Optional<String> path) {
        String uuid = UUID.randomUUID().toString().concat("." + getFileExtension(fileName));

        if (path.isPresent()) {
            return path.get() + "/" + uuid;
        }
        if (getFileExtension(fileName).equals("mp4")) {
            return "video/" + uuid;
        }
        return uuid;
    }

    private String getFileExtension(String fileName) {
        try {
            int pos = fileName.lastIndexOf(".");
            return fileName.substring(pos + 1);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 메이슨
    private void putS3(String fileName, InputStream inputstream, ObjectMetadata objectMetadata) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputstream, objectMetadata).withCannedAcl(
            CannedAccessControlList.PublicRead));
    }

    // 메이슨
    private void putS3(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file).withCannedAcl(
            CannedAccessControlList.PublicRead));
    }

    // 메이슨
    private void saveThumbnail(MultipartFile multipartFile, String originFileName, Integer size) {
        try {
            String format = "png";
            File thumbnailImg = resizeImageFile(multipartFile, "png", size);
            putS3(originFileName + ".t" + size + "." + format, thumbnailImg);
            thumbnailImg.delete();
        } catch (Exception e) {
            throw new GeneralException(Code.S3_UPLOAD_ERROR, e);
        }
    }

    // 메이슨
    private File resizeImageFile(MultipartFile multipartFile, String format, int size) {
        try {
            String tempSavePathName = System.getProperty("user.dir") + "/temp." + format;
            BufferedImage inputImage = ImageIO.read(multipartFile.getInputStream());
            int originWidth = inputImage.getWidth();
            int originHeight = inputImage.getHeight();
            int newWidth, newHeight;
            if (originWidth < originHeight) {
                newWidth = size;
                newHeight = (originHeight * newWidth) / originWidth;
            } else {
                newHeight = size;
                newWidth = (originWidth * newHeight) / originHeight;
            }

            Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = newImage.getGraphics();
            graphics.drawImage(resizeImage, 0, 0, null);
            graphics.dispose();

            File newFile = new File(tempSavePathName);
            ImageIO.write(newImage, format, newFile);
            return newFile;
        } catch (Exception e) {
            throw new GeneralException(Code.S3_UPLOAD_ERROR, e);
        }
    }
}
