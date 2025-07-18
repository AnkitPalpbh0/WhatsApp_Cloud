package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.MessageException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.*;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Service.MessageService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.UserService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.WhatsappService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private WhatsappService whatsAppService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


    @PostMapping("/sendTextMessage")
    public ResponseEntity<MessageResponse> sendMessageHandler(@Valid @RequestBody SendMessageRequest sendMessageRequest,
                                                              @RequestHeader("Authorization") String jwt) throws UserException, ChatException {

        Users user = this.userService.findUserProfile(jwt);

        sendMessageRequest.setUserId(user.getId());

        Message message = this.messageService.sendMessage(sendMessageRequest, user);
        if (message != null) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setStatus("Message sent");
            return new ResponseEntity<>(messageResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/send-media")
    public ResponseEntity<MediaResponse> sendMedia(@Valid @RequestBody MediaRequest request,
                                                   @RequestHeader("Authorization") String jwt) throws UserException {

        Users user = this.userService.findUserProfile(jwt);
        request.setUserId(user.getId());
        userService.processMediaRequest(request);
        MediaResponse response = new MediaResponse();
        response.setMessage("Media message sent successfully.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessageHandler(@PathVariable Integer messageId,
                                                            @RequestHeader("Authorization") String jwt) throws UserException, ChatException, MessageException {

        Users user = this.userService.findUserProfile(jwt);

        this.messageService.deleteMessage(messageId, user);

        ApiResponse res = new ApiResponse("Deleted successfully......", false);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}

