package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.*;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Service.ChatService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @GetMapping("/{chatId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessageHandler(@PathVariable Integer chatId,
                                                                           @RequestHeader("Authorization") String jwt) throws UserException, ChatException {

        Users user = this.userService.findUserProfile(jwt);

        List<ChatMessageResponse> messages = this.chatService.getChatsMessages(chatId, user);

        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/chats/{userId}")
    public ResponseEntity<List<UserChatsResponse>> getUserChatsHandler(@PathVariable Integer userId,
                                                                       @RequestHeader("Authorization") String jwt) throws UserException, ChatException {

        Users user = this.userService.findUserProfile(jwt);

        List<UserChatsResponse> messages = this.chatService.getUserChats(user);

        return new ResponseEntity<>(messages, HttpStatus.OK);

    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<ApiResponse> deleteChatHandler(@PathVariable Integer chatId,
            @RequestHeader("Authorization") String jwt)
            throws UserException, ChatException {

        Users reqUser = this.userService.findUserProfile(jwt);

        this.chatService.deleteChat(chatId, reqUser.getId());

        ApiResponse res = new ApiResponse("Deleted Successfully...", false);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
