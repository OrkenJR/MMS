package kz.iitu.orken.medical_managament_system.controller;

import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyRole;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService userService) {
        this.service = userService;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public ResponseEntity<List<User>> findAll(Authentication authentication){

        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/t")
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasRole('ADMIN_ROLE')")
    public ResponseEntity<List<User>> t(){
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

}
