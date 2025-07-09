package com.WhatsAppBusiness.WhatsApp.Business.Service;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.MessageException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.ChatMessageResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.UserChatsResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import java.util.List;

public interface MessageService {

    public Message sendMessage(SendMessageRequest req, Users user) throws UserException, ChatException;

    public List<ChatMessageResponse> getChatsMessages(Integer chatId, Users reqUser) throws ChatException, UserException;

    public Message findMessageById(Integer messaageId) throws MessageException;

    public void deleteMessage(Integer messageId, Users reqUser) throws MessageException;

    List<UserChatsResponse> getUserChats(Users user);
}
