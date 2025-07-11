package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.ChatMessageResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.GroupChatRequest;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.UserChatsResponse;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.ChatRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.MessageRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.UserRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Service.ChatService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Chat createChat(Users reqUser, Integer userId) {

        Chat chat = new Chat();

        chat = this.chatRepository.save(chat);

        return chat;
    }

    @Override
    public Chat findChatById(int chatId) throws ChatException {
        return this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("The requested chat is not found"));
    }

    @Override
    public List<Chat> findAllChatByUserId(Integer userId) {
        Users user = this.userRepository.findUserById(userId);

        List<Chat> chats = this.chatRepository.findChatByUserId(user.getId());

        return chats;
    }

    @Override
    public Chat createGroup(GroupChatRequest req, Users reqUser) {
        Chat group = new Chat();
        for (Integer userId : req.getUserIds()) {
            Users user = this.userRepository.findUserById(userId);
//            group.getUsers().add(user);
        }

        group = this.chatRepository.save(group);
        return group;
    }

    @Override
    public Chat addUserToGroup(Integer userId, Integer chatId, Users reqUser) throws ChatException {
        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found"));
        return null;
    }

    @Override
    public Chat removeFromGroup(Integer chatId, Integer userId, Users reqUser) throws ChatException, UserException {
        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found"));

        throw new UserException("You have not access to remove user");

    }

    @Override
    public void deleteChat(Integer chatId, Integer userId) throws ChatException {
        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found while deleteing"));
        this.chatRepository.delete(chat);
    }

    public Chat findByChatNumber(String to) {
        return this.chatRepository.findByChatNumber(to);
    }

    @Override
    public List<ChatMessageResponse> getChatsMessages(Integer chatId, Users user) throws ChatException {
        Chat chat = this.chatRepository.findChatById(chatId);
        if (chat == null) {
            throw new ChatException("The expected chat is not found");
        }

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
    public List<UserChatsResponse> getUserChats(Users user) {
        List<Chat> chats = this.chatRepository.findChatByUserId(user.getId());
        return chats.stream().map(chat -> new UserChatsResponse(
                chat.getId(),
                chat.getChatNumber()
        )).collect(Collectors.toList());
    }

    public Chat findByChatNumberAndUserId(String to, Integer id) {
        return this.chatRepository.findByChatNumberAndUserId(to, id);
    }
}
