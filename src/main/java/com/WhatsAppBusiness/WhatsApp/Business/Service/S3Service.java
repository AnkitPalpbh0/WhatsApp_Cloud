package com.WhatsAppBusiness.WhatsApp.Business.Service;

import java.io.File;
import java.io.IOException;

public interface S3Service {

    File downloadFile(String mediaUrl) throws IOException;

}