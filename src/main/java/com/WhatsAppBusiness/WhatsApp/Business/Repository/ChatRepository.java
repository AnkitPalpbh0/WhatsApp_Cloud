package com.WhatsAppBusiness.WhatsApp.Business.Repository;

import com.WhatsAppBusiness.WhatsApp.Business.Model.Chat;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
//    Chat findSingleChatByUserIds(Users user, Users reqUser);

    @Query("select c from Chat c join c.user u where u.id=:userId")
    public List<Chat> findChatByUserId(@Param("userId") Integer userId);

    @Query("select c from Chat c where c.chatNumber=:to")
    Chat findByChatNumber(String to);

    @Query(value = "SELECT * FROM chat WHERE RIGHT(chat_number, 10) = :lastTen", nativeQuery = true)
    Chat findByChatNumberEndingWith(@Param("lastTen") String lastTen);

    Chat findChatById(Integer chatId);

    @Query("select c from Chat c join c.user u where u.id=:userId and c.chatNumber=:to")
    Chat findByChatNumberAndUserId(String to, Integer userId);

//    @Query("select c from Chat c where c.isGroup=false and :user member of c.users and :reqUser member of c.users")
//    public Chat findSingleChatByUserIds(@Param("user") Users user, @Param("reqUser") Users reqUser);

}
