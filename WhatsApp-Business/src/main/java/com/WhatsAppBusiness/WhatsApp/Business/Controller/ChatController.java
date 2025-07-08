package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.ApiResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.GroupChatRequest;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SingleChatRequest;
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

    @PostMapping("/single")
    public ResponseEntity<Chat> createChatHandler(@RequestBody SingleChatRequest singleChatRequest,
                                                  @RequestHeader("Authorization") String jwt) throws UserException, UserException {

        Users reqUser = this.userService.findUserProfile(jwt);

        Chat chat = this.chatService.createChat(reqUser, singleChatRequest.getUserId());

        return new ResponseEntity<Chat>(chat, HttpStatus.CREATED);
    }

    @PostMapping("/group")
    public ResponseEntity<Chat> createGroupHandler(@RequestBody GroupChatRequest groupChatRequest,
            @RequestHeader("Authorization") String jwt) throws UserException {

        System.out.println(groupChatRequest);
        Users reqUser = this.userService.findUserProfile(jwt);

        Chat chat = this.chatService.createGroup(groupChatRequest, reqUser);

        return new ResponseEntity<Chat>(chat, HttpStatus.CREATED);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> findChatByIdHandler(@PathVariable int chatId) throws ChatException {

        Chat chat = this.chatService.findChatById(chatId);

        return new ResponseEntity<Chat>(chat, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Chat>> findChatByUserIdHandler(@RequestHeader("Authorization") String jwt)
            throws UserException {

        Users reqUser = this.userService.findUserProfile(jwt);

        List<Chat> chats = this.chatService.findAllChatByUserId(reqUser.getId());

        return new ResponseEntity<List<Chat>>(chats, HttpStatus.OK);
    }

    @PutMapping("/{chatId}/add/{userId}")
    public ResponseEntity<Chat> addUserToGroupHandler(@PathVariable Integer chatId,
            @PathVariable Integer userId, @RequestHeader("Authorization") String jwt)
            throws UserException, ChatException {

        Users reqUser = this.userService.findUserProfile(jwt);

        Chat chat = this.chatService.addUserToGroup(userId, chatId, reqUser);

        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @PutMapping("/{chatId}/remove/{userId}")
    public ResponseEntity<Chat> removeUserFromGroupHandler(@PathVariable Integer chatId,
            @PathVariable Integer userId, @RequestHeader("Authorization") String jwt)
            throws UserException, ChatException {

        Users reqUser = this.userService.findUserProfile(jwt);

        Chat chat = this.chatService.removeFromGroup(userId, chatId, reqUser);

        return new ResponseEntity<>(chat, HttpStatus.OK);
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
