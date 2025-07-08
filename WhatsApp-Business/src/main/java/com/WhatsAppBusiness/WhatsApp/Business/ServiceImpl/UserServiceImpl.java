package com.WhatsAppBusiness.WhatsApp.Business.ServiceImpl;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.UpdateUserRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Repository.UserRepository;
import com.WhatsAppBusiness.WhatsApp.Business.Security.config.TokenProvider;
import com.WhatsAppBusiness.WhatsApp.Business.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    public Users findUserById(Integer id) throws UserException {
        return this.userRepository.findById(id).orElseThrow(() -> new UserException("The requested user is not found"));
    }

    @Override
    public Users findUserProfile(String jwt) throws UserException {
        String email = this.tokenProvider.getEmailFromToken(jwt);

        if (email == null) {
            throw new BadCredentialsException("Recieved invalid token...");
        }

        Users user = this.userRepository.findByEmail(email);

        if (user == null) {
            throw new UserException("User not found with the provided email ");
        }
        return user;

    }

    @Override
    public void updateUser(Integer userId, UpdateUserRequest req) throws UserException {
        Users user = this.findUserById(userId);

        if (req.getName() != null) {
            user.setName(req.getName());
        }
        if (req.getProfile() != null) {
//            user.setProfile(req.getProfile());
        }
    }

    @Override
    public List<Users> searchUser(String query) {
        return this.userRepository.searchUser(query);
    }
}
