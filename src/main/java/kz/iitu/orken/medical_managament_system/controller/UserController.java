package kz.iitu.orken.medical_managament_system.controller;

import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/users", consumes = "application/json")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService userService) {
        this.service = userService;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<User>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/byUsername/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> findByUsername(@PathVariable() String username) {
        return new ResponseEntity<>(service.findByUsername(username), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> save(@RequestBody User user) {
        return new ResponseEntity<>(service.save(user), HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> myProfile() {
        return new ResponseEntity<>(service.getCurrentUser(), HttpStatus.OK);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUser(@RequestParam Long userId) {
        service.deleteById(userId);
        return new ResponseEntity<>("Successfully deleted user with id: " + userId, HttpStatus.OK);
    }

    @DeleteMapping("/deleteRoles")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUserRole(@RequestParam Long userId, @RequestBody List<String> roles) {
        User user = service.findById(userId);
        Optional.ofNullable(user).ifPresent(u -> service.deleteRole(u, roles));
        return new ResponseEntity<>(String.format("Successfully deleted roles %s from user: %s ", roles, Optional.ofNullable(user).map(User::getUsername).orElse("null")), HttpStatus.OK);
    }

}
