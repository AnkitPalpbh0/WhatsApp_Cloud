package com.WhatsAppBusiness.WhatsApp.Business.Service;

import java.io.File;
import java.io.IOException;

public interface S3Service {

    File downloadFile(String mediaUrl) throws IOException;

    String uploadFile(String bulkUpload, String fileName, byte[] mediaBytes, String mimeType);
}