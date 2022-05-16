package kz.iitu.orken.medical_managament_system.security;

import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.RoleRepository;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class SetupSecurityData implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public SetupSecurityData(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        var doctorRole = createRoleIfNotFound(Role.DOCTOR_ROLE);
        var adminRole = createRoleIfNotFound(Role.ADMIN_ROLE);

        createUserIfNotFound("doctor", doctorRole);
        createUserIfNotFound("admin", adminRole);

        alreadySetup = true;
    }

    @Transactional
    Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = Role.builder().name(name).build();
            role = roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String name, final Role role) {
        User user = userRepository.findByUsername(name);
        if (user == null) {
            user = User.builder().firstName(name).lastName(name).username(name).password("test").build();
            user.setRoles(Set.of(role));
            user = userRepository.save(user);
        }
        return user;
    }

}
