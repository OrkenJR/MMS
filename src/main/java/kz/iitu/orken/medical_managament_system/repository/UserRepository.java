package kz.iitu.orken.medical_managament_system.repository;

import kz.iitu.orken.medical_managament_system.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
