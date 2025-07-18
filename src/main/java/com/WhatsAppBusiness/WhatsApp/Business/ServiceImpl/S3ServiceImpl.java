package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.Service.S3Service;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class S3ServiceImpl implements S3Service {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(S3ServiceImpl.class);

    private final S3Client s3Client;

    static String S3_AWS_ACCESS_KEY = System.getenv().getOrDefault("S3_AWS_ACCESS_KEY", "");
    static String S3_AWS_SECRET_KEY = System.getenv().getOrDefault("S3_AWS_SECRET_KEY", "");
    static String S3_REGION = System.getenv().getOrDefault("S3_REGION", "");
    static String S3_BUCKET_NAME = System.getenv().getOrDefault("S3_BUCKET_NAME", "");

    @Autowired
    public S3ServiceImpl() {
        // Initialize the S3 client with credentials and region
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(S3_AWS_ACCESS_KEY, S3_AWS_SECRET_KEY);
        this.s3Client = S3Client.builder()
                .region(Region.of(S3_REGION))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    /**
     * Downloads a file from S3 to a temporary file.
     *
     * @param mediaUrl        S3 object key
     * @return           File downloaded locally
     */
    public File downloadFile(String mediaUrl) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(S3_BUCKET_NAME)
                    .key(mediaUrl)
                    .build();

            ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(getObjectRequest);

            // Get file extension
            String extension = mediaUrl.contains(".")
                    ? mediaUrl.substring(mediaUrl.lastIndexOf("."))
                    : "";

            // Create timestamp-based prefix (safe: remove invalid filename chars)
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String prefix = "s3-media-" + timestamp + "-";

            // Create temp file
            File tempFile = File.createTempFile(prefix, extension);
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(s3ObjectStream, out);
            }

            LOGGER.info("Successfully downloaded file from S3: {}", tempFile.getAbsolutePath());
            return tempFile;

        } catch (S3Exception e) {
            LOGGER.error("S3 error occurred: {}", e.awsErrorDetails().errorMessage());
            return null;
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred: {}", e.getMessage(), e);
            return null;
        }
    }

//    @Override
//    public String uploadToS3(byte[] mediaBytes, String fileName, String mimeType) {
//        LOGGER.info("Uploading media to S3");
//        LOGGER.info("File name: {}", fileName);
//        LOGGER.info("MIME type: {}", mimeType);
//        LOGGER.info("File size: {}", mediaBytes.length);
//        try {
//            // Determine file extension from MIME type
//            String extension = switch (mimeType) {
//                case "image/jpeg" -> ".jpg";
//                case "image/png" -> ".png";
//                case "image/gif" -> ".gif";
//                case "video/mp4" -> ".mp4";
//                case "application/pdf" -> ".pdf";
//                case "application/msword" -> ".doc";
//                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
//                case "application/vnd.ms-excel" -> ".xls";
//                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> ".xlsx";
//                default -> ".bin";
//            };
//
//            // Generate timestamp
//            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
//
//            // Use default name if fileName is null/blank
//            if (fileName == null || fileName.isBlank()) {
//                fileName = "media";
//            }
//
//            // Remove extension if present in fileName
//            if (fileName.contains(".")) {
//                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
//            }
//
//            // Sanitize file name to remove unsafe characters
//            String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9_\\- ]", "_");
//
//            // Final file name: original name + timestamp + extension
//            String finalFileName = sanitizedFileName + "_" + timestamp + extension;
//
//            // S3 key path
//            String s3Key = "bulkUpload/" + finalFileName;
//
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(S3_BUCKET_NAME)
//                    .key(s3Key)
//                    .contentType(mimeType)
//                    .contentLength((long) mediaBytes.length)
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(mediaBytes));
//
//            LOGGER.info("Successfully uploaded file to S3: {}", s3Key);
//
//            // Return the full S3 path (or just the key if preferred)
//            return s3Key;
//
//        } catch (Exception e) {
//            LOGGER.error("Error uploading file to S3", e);
//            throw new RuntimeException("Failed to upload file to S3", e);
//        }
//    }
    @Override
    public String uploadFile(String folderName, String fileName, byte[] fileContent, String contentType) {
        try {
            if (folderName == null || folderName.isBlank()) {
                throw new IllegalArgumentException("Folder name cannot be null or blank");
            }
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("File name cannot be null or blank");
            }
            if (fileContent == null || fileContent.length == 0) {
                throw new IllegalArgumentException("File content cannot be null or empty");
            }
            if (contentType == null || contentType.isBlank()) {
                throw new IllegalArgumentException("Content type cannot be null or blank");
            }

            // ✅ Build the S3 Key (generic folder structure)
            String key = folderName + "/" + fileName;

            // ✅ Log the upload details
            LOGGER.info("Uploading byte array to S3 bucket: {}, folder: {}, file: {}", S3_BUCKET_NAME, folderName, fileName);

            // ✅ Build PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(S3_BUCKET_NAME)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) fileContent.length)
                    .build();

            // ✅ Upload File to S3
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));

            LOGGER.info("File uploaded successfully to S3: {}/{}", S3_BUCKET_NAME, key);

            // ✅ Return Full Resource URL
    //        String resourceUrl = String.format("https://%s.s3.amazonaws.com/%s", S3_BUCKET_NAME, key);
            return key;

        } catch (S3Exception e) {
            LOGGER.error("S3 upload failed: {}", e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Failed to upload byte array to S3: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            LOGGER.error("General error during S3 upload: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while uploading byte array: " + e.getMessage(), e);
        }
    }

}