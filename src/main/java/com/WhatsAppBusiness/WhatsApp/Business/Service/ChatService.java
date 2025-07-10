package com.WhatsAppBusiness.WhatsApp.Business.Service;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.ChatMessageResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.GroupChatRequest;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.UserChatsResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;

import java.util.List;

public interface ChatService {
    Chat createChat(Users reqUser, Integer userId);

    Chat createGroup(GroupChatRequest groupChatRequest, Users reqUser);

    Chat findChatById(int chatId) throws ChatException;

    List<Chat> findAllChatByUserId(Integer id);

    Chat addUserToGroup(Integer userId, Integer chatId, Users reqUser) throws ChatException;

    Chat removeFromGroup(Integer userId, Integer chatId, Users reqUser) throws ChatException, UserException;

    void deleteChat(Integer chatId, Integer id) throws ChatException;

    List<ChatMessageResponse> getChatsMessages(Integer chatId, Users user) throws ChatException, UserException;

    List<UserChatsResponse> getUserChats(Users user);
}
