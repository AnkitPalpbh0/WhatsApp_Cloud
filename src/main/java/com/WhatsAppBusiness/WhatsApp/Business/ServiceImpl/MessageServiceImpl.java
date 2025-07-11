package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.MessageException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.ChatMessageResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.UserChatsResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.ChatRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MessageRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Service.MessageService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.WhatsappService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.whatsapp.api.domain.messages.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessageServiceImpl implements MessageService {

    Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ChatServiceImpl chatService;

    @Autowired
    private WhatsappService whatsappService;

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public Message sendMessage(SendMessageRequest req, Users user) throws UserException, ChatException {

        Chat chat = this.chatService.findByChatNumberAndUserId(req.getTo(), user.getId());
        if (chat == null) {
            chat = new Chat();
            chat.setChatNumber(req.getTo());
            chat.setUser(user);
            chat = this.chatRepository.save(chat);
        }
        Message message = new Message();
        try {
            MessageResponse response = whatsappService.sendTextMessage(req);
            String messageId = response.messages().get(0).id();
            String status = response.messages().get(0).messageStatus();
            message.setMessageId(messageId);
            message.setChat(chat);
            message.setContent(req.getContent());
            message.setTimestamp(LocalDateTime.now());
            message.setMessageType("text");
            message.setStatus(status);

            message = this.messageRepository.save(message);
        } catch (Exception e) {
            LOGGER.error("Error sending message", e);
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }

        return message;
    }

    @Override
    public List<ChatMessageResponse> getChatsMessages(Integer chatId, Users reqUser) throws ChatException, UserException {

        Chat chat = this.chatService.findChatById(chatId);

        List<Message> messages = this.messageRepository.findByChatId(chat.getId());
        return messages.stream().map(message -> new ChatMessageResponse(
                message.getId(),
                message.getMessageId(),
                message.getMessageType(),
                message.getContent(),
                message.getMedia() != null ? message.getMedia().getUrl() : null,  // Assuming MediaMetadata has getUrl()
                message.getTimestamp(),
                message.getStatus()
        )).collect(Collectors.toList());
    }

    @Override
    public Message findMessageById(Integer messageId) throws MessageException {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("The required message is not found"));
        return message;
    }

    @Override
    public List<UserChatsResponse> getUserChats(Users user) {
        List<Chat> chats = this.chatService.findAllChatByUserId(user.getId());
        return chats.stream().map(chat -> new UserChatsResponse(
                chat.getId(),
                chat.getChatNumber()
        )).collect(Collectors.toList());
    }

    @Override
    public void deleteMessage(Integer messageId, Users reqUser) throws MessageException {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("The required message is not found"));

        this.messageRepository.delete(message);
    }

}
