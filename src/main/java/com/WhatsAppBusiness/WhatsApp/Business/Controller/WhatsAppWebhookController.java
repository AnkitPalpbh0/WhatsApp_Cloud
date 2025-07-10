package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.WebhookResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappWebhookPubSub.WebhookEventProducer;
import com.whatsapp.api.domain.webhook.WebHook;
import com.whatsapp.api.domain.webhook.WebHookEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    Logger logger = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    @Autowired
    private WebhookEventProducer webhookEventProducer;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        final String VERIFY_TOKEN = "my_custom_token_123"; // <-- must match Meta Console setting
        logger.info("Inside verifyWebhook");

        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            logger.info("Webhook verified");
            return ResponseEntity.ok(challenge);
        } else {
            logger.error("Invalid verify token for webhook");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid verify token");
        }
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody String payload) {
        try {
            WebHookEvent event = WebHook.constructEvent(payload);

            for (var entry : event.entry()) {
                for (var change : entry.changes()) {
                    var value = change.value();

                    if (value.statuses() != null) {
                        value.statuses().forEach(status -> {
                            WebhookResponse response = new WebhookResponse();
                            response.setMessageId(status.id());
                            logger.info("Status of message id and status: {}", status.id() + " " + status.status());
                            response.setStatus(status.status().name());

                            webhookEventProducer.sendMessage(response);
                        });
                    }

                    String businessNumber = value.metadata().displayPhoneNumber();

                    if (value.messages() != null) {
                        value.messages().forEach(message -> {
                            WebhookResponse response = new WebhookResponse();
                            response.setMessageId(message.id());
                            response.setStatus("received");
                            response.setSenderNumber(message.from().substring(message.from().length() - 10));
                            if ("text".equals(message.type()) && message.text() != null) {
                                String content = message.text().body();
                                response.setContent(content);  // <- you must add a 'content' field in your WebhookResponse
                            }

                            response.setRecipientBusinessNumber(businessNumber);
                            webhookEventProducer.sendMessage(response);
                        });
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

}

