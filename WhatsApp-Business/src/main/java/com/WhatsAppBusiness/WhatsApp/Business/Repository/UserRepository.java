package com.WhatsAppBusiness.WhatsApp.Business.Repository;

import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    public Users findByEmail(String email);

    @Query("select u from Users u where u.name like %:query% or u.email like %:query%")
    public List<Users> searchUser(@Param("query") String query);

    Users findUserById(Integer userId);
}
