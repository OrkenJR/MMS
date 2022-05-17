package kz.iitu.orken.medical_managament_system.service;

import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface UserService {

    List<User> findAll();
    User findByUsername(String username);
    User findById(Long id);
    User save(User user);
    User randomDoctor();
    User getCurrentUser();
    void delete(User user);
    void deleteById(Long id);
    void deleteRole(User user, List<String> roles);
    void report(String message);
    byte[] export();

}
