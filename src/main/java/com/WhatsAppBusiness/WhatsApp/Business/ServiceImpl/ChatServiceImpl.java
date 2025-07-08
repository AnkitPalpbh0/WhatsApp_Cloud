package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.ChatException;
import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.GroupChatRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.ChatRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.UserRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Service.ChatService;
import com.WhatsAppBusiness.WhatsApp.Business.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
//    @Override
//    public Chat createChat(Users reqUser, Integer userId) {
//        return null;
//    }
//
//    @Override
//    public Chat createGroup(GroupChatRequest groupChatRequest, Users reqUser) {
//        return null;
//    }
//
//    @Override
//    public Chat findChatById(int chatId) {
//        return null;
//    }
//
//    @Override
//    public List<Chat> findAllChatByUserId(Integer id) {
//        return List.of();
//    }
//
//    @Override
//    public Chat addUserToGroup(Integer userId, Integer chatId, Users reqUser) {
//        return null;
//    }
//
//    @Override
//    public Chat removeFromGroup(Integer userId, Integer chatId, Users reqUser) {
//        return null;
//    }
//
//    @Override
//    public void deleteChat(Integer chatId, Integer id) {
//
//    }

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Chat createChat(Users reqUser, Integer userId) {

        Users user = this.userRepository.findUserById(userId);

//        Chat isChatExist = this.chatRepository.findSingleChatByUserIds(user, reqUser);

//        System.out.println(isChatExist);
//        if (isChatExist != null) {
//            return isChatExist;
//        }

        Chat chat = new Chat();
//        chat.setCreatedBy(reqUser);
//        chat.getUsers().add(user);
//        chat.getUsers().add(reqUser);
//        chat.setGroup(false);

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
//        group.setGroup(true);
//        group.setChatImage(req.getChatImage());
//        group.setChatName(req.getChatName());
//        group.setCreatedBy(reqUser);
//        group.getAdmins().add(reqUser);

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

        Users user = this.userRepository.findUserById(userId);

//        if (chat.getAdmins().contains((reqUser))) {
//            chat.getUsers().add(user);
//            return chat;
//        } else {
//            throw new UserException("You have not access to add user");
//        }
        return null;
    }

//    public Chat renameGroup(Integer chatId, String groupName, Users reqUser) throws ChatException, UserException {
//        Chat chat = this.chatRepository.findById(chatId)
//                .orElseThrow(() -> new ChatException("The expected chat is not found"));
//
//        if (chat.getUsers().contains(reqUser)) {
//            chat.setChatName(groupName);
//            return this.chatRepository.save(chat);
//        } else {
//            throw new UserException("YOu are not the user");
//        }
//    }

    @Override
    public Chat removeFromGroup(Integer chatId, Integer userId, Users reqUser) throws ChatException, UserException {
        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found"));

        Users user = this.userRepository.findUserById(userId);

//        if (chat.getAdmins().contains((reqUser))) {
//            chat.getUsers().remove(user);
//            return chat;
//        } else if (chat.getUsers().contains(reqUser)) {
//            if (user.getId() == reqUser.getId()) {
//                chat.getUsers().remove(user);
//                return this.chatRepository.save(chat);
//            }
//
//        }
        throw new UserException("You have not access to remove user");

    }

    @Override
    public void deleteChat(Integer chatId, Integer userId) throws ChatException {
        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("The expected chat is not found while deleteing"));
        this.chatRepository.delete(chat);
    }
}
