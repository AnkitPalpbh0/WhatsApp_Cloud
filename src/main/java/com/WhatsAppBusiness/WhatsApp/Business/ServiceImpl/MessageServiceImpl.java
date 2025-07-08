package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.MessageException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.SendMessageRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MessageRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Service.MessageService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.WhatsappService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public Message sendMessage(SendMessageRequest req) throws UserException, ChatException {
        Users user = this.userService.findUserById(req.getUserId());
        Chat chat = new Chat();
        if (req.getChatId() == null) {
            chat = new Chat();

        } else {
            chat = this.chatService.findChatById(req.getChatId());
        }
        Message message = new Message();
        try {
            whatsappService.sendTextMessage(req);
            message.setChat(chat);
            message.setContent(req.getContent());
            message.setTimestamp(LocalDateTime.now());

            message = this.messageRepository.save(message);

            // Send message to WebSocket topic based on chat type
//        if (chat.isGroup()) {
//            messagingTemplate.convertAndSend("/group/" + chat.getId(), message);
//        } else {
//            messagingTemplate.convertAndSend( "/user/" + chat.getId(), message);
//        }
        } catch (Exception e) {
            LOGGER.error("Error sending message", e);
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }

        return message;
    }

    @Override
    public List<Message> getChatsMessages(Integer chatId, Users reqUser) throws ChatException, UserException {

        Chat chat = this.chatService.findChatById(chatId);

//        if (!chat.getUsers().contains(reqUser)) {
//            throw new UserException("You are not related to this chat");
//        }

        List<Message> messages = this.messageRepository.findByChatId(chat.getId());

        return messages;

    }

    @Override
    public Message findMessageById(Integer messageId) throws MessageException {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("The required message is not found"));
        return message;
    }

    @Override
    public void deleteMessage(Integer messageId, Users reqUser) throws MessageException {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("The required message is not found"));

//        if (message.getUser().getId() == reqUser.getId()) {
//            this.messageRepository.delete(message);
//        } else {
//            throw new MessageException("You are not authorized for this task");
//        }
    }

}
