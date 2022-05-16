package kz.iitu.orken.medical_managament_system.service;

import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface UserService {

    List<User> findAll();
    User findByUsername(String username);
    User save(User user);
    User randomDoctor();
    User getCurrentUser();
    void delete(User user);
    void deleteRole(User user, List<Role> roles);
    void report(String message);
    void makeAppointment(Treatment treatment);
    void finishAppointment(Treatment treatment);
    byte[] export();

}
