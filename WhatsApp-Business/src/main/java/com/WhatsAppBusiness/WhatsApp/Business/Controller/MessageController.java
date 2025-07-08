package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.MessageException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.ApiResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Service.MessageService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.UserService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private WhatsappService whatsAppService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<String> sendTextMessage(@RequestBody SendMessageRequest request) {
        whatsAppService.sendTextMessage(request);
        return ResponseEntity.ok("Message sent");
    }

    @PostMapping("/create")
    public ResponseEntity<Message> sendMessageHandler(@RequestBody SendMessageRequest sendMessageRequest,
                                                      @RequestHeader("Authorization") String jwt) throws UserException, ChatException {

        Users user = this.userService.findUserProfile(jwt);

        sendMessageRequest.setUserId(user.getId());

        Message message = this.messageService.sendMessage(sendMessageRequest);

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<List<Message>> getChatMessageHandler(@PathVariable Integer chatId,
                                                               @RequestHeader("Authorization") String jwt) throws UserException, ChatException {

        Users user = this.userService.findUserProfile(jwt);

        List<Message> messages = this.messageService.getChatsMessages(chatId, user);

        return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
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

