package com.WhatsAppBusiness.WhatsApp.Business.Controller;

import com.WhatsAppBusiness.WhatsApp.Business.Common.Exceptions.UserException;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.ApiResponse;
import com.WhatsAppBusiness.WhatsApp.Business.DTOs.UpdateUserRequest;
import com.WhatsAppBusiness.WhatsApp.Business.Model.Users;
import com.WhatsAppBusiness.WhatsApp.Business.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Users> getUserProfileHandler(@RequestHeader("Authorization") String token)
            throws UserException {

        Users user = this.userService.findUserProfile(token);
        return new ResponseEntity<Users>(user, HttpStatus.OK);
    }

    @GetMapping("/{query}")
    public ResponseEntity<List<Users>> searchUserHandler(@PathVariable("query") String query) {

        List<Users> users = this.userService.searchUser(query);
        return new ResponseEntity<List<Users>>(users, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUserHandler(@RequestBody UpdateUserRequest request,
                                                         @RequestHeader("Authorization") String token) throws UserException {

        Users user = this.userService.findUserProfile(token);
        this.userService.updateUser(user.getId(), request);

        ApiResponse response = new ApiResponse();
        response.setMessage("User updated Successfully");
        response.setStatus(true);

        return new ResponseEntity<ApiResponse>(response, HttpStatus.ACCEPTED);
    }

}
