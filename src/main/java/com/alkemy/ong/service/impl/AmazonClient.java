package com.alkemy.ong.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class AmazonClient {

	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}")
	private String bucketName;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	private static final Logger log = LoggerFactory.getLogger(AmazonClient.class);

	@PostConstruct
	private void initializeAmazon() {
		
		BasicAWSCredentials creds = new BasicAWSCredentials(this.accessKey, this.secretKey); 
		s3client = AmazonS3ClientBuilder.
				standard().
				withRegion("sa-east-1").
				withCredentials(new AWSStaticCredentialsProvider(creds)).
				build();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException{
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
	
	private String generateFileName(MultipartFile multiPart) {
		return multiPart.getOriginalFilename().replace(" ", "_");
	}

	private void uploadFileTos3bucket(String fileName, File file){
				s3client.putObject(
	            new PutObjectRequest(bucketName, fileName, file)
	            .withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public String uploadFile(MultipartFile multipartFile) {
		String fileUrl = "";
		File file = null;
		try {
			file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			fileUrl = "https://" + bucketName + "." + endpointUrl + "/" + fileName;
			uploadFileTos3bucket(fileName, file);
			log.info("File updated");
		}
		catch (Exception e) {
			fileUrl = "";
			log.info("Error. File could not be updated. Message: " + e.getMessage());
		}
		
		if(file != null) file.delete();
		
		return fileUrl;
	}

	public void deleteFileFromS3Bucket(String fileUrl) {
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		try {
			s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
			log.info("File: "+ fileName + " successfully deleted!");
		}
		catch(Exception e) {
			log.info(e.getLocalizedMessage());
		}
	}
    
}