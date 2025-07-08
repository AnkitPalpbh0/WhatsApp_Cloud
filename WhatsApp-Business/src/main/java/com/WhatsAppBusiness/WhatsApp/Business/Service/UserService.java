package com.WhatsAppBusiness.WhatsApp.Business.Service;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.UpdateUserRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;

import java.util.List;

public interface UserService {

    Users findUserProfile(String token) throws UserException;

    List<Users> searchUser(String query);

    void updateUser(Integer id, UpdateUserRequest request) throws UserException;
}
