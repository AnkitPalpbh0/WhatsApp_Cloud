package com.WhatsAppBusiness.WhatsApp.Business.Utils;

import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.configuration.ApiVersion;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WhatsappSdkConfig {

    @Value("${whatsapp.token}")
    private String token;

    @Bean
    public WhatsappBusinessCloudApi whatsappBusinessCloudApi() {
        WhatsappApiFactory factory = WhatsappApiFactory.newInstance(token);
        return factory.newBusinessCloudApi(ApiVersion.V19_0);
    }

}
