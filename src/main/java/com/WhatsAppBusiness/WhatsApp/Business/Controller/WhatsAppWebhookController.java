package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.DTOs.WebhookResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Manager.rabbitmq.whatsappWebhookPubSub.WebhookEventProducer;
import com.whatsapp.api.domain.messages.type.MessageType;
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
            // ✅ Log the full JSON payload
            logger.info("Received Webhook JSON Payload:\n{}", payload);

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
                            response.setRecipientBusinessNumber(businessNumber.substring(businessNumber.length() - 10));

                            // Media handling
                            switch (message.type()) {
                                case IMAGE:
                                    logger.info("Received image message");
                                    response.setType("image");
                                    response.setMediaId(message.image().id());
                                    if (message.image().caption() != null) {
                                        response.setCaption(message.image().caption());
                                    }
                                    response.setMimeType(message.image().mimeType());
                                    break;

                                case VIDEO:
                                    logger.info("Received video message");
                                    response.setType("video");
                                    response.setMediaId(message.video().id());
                                    if (message.image().caption() != null) {
                                        response.setCaption(message.image().caption());
                                    }
                                    response.setMimeType(message.video().mimeType());
                                    break;

                                case DOCUMENT:
                                    logger.info("Received document message");
                                    response.setType("document");
                                    response.setMediaId(message.document().id());
                                    if (message.image().caption() != null) {
                                        response.setCaption(message.image().caption());
                                    }
                                    response.setMimeType(message.document().mimeType());
                                    response.setFileName(message.document().filename());
                                    break;

                                case TEXT:
                                    logger.info("Received text message");
                                    response.setType("text");
                                    response.setContent(message.text().body());
                                    break;

                                default:
                                    logger.warn("Unsupported message type received: {}", message.type().name());
                            }

                            // Send to RabbitMQ queue
                            webhookEventProducer.sendMessage(response);
                        });
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

}

