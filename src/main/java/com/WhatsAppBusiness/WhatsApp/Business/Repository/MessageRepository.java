package com.WhatsAppBusiness.WhatsApp.Business.Repository;

import com.WhatsAppBusiness.WhatsApp.Business.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("select m from Message m join m.chat c where c.id=:chatId")
    public List<Message> findByChatId(@Param("chatId") Integer chatId);

    Message findByMessageId(String messageId);
}
