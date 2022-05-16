package kz.iitu.orken.medical_managament_system.service.impl;

import kz.iitu.orken.medical_managament_system.Exception.NotAllowedException;
import kz.iitu.orken.medical_managament_system.Exception.TransactionException;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.RoleRepository;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import kz.iitu.orken.medical_managament_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CacheManager cacheManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable("all-users")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true,
            isolation = Isolation.READ_COMMITTED, rollbackFor = TransactionException.class,
            noRollbackFor = NotAllowedException.class)
    public List<User> findAll() {
        return userRepository.findAll().stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "user-by-username", key = "#username")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.REPEATABLE_READ,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Caching(evict = {
            @CacheEvict("all-users"),
            @CacheEvict(value = "user-by-username", key = "#user.username")
    })
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.SERIALIZABLE,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public User randomDoctor() {
        Random rand = new Random();
        List<User> allUsers = findAll();
        return allUsers.stream()
                .filter(x -> containsName(x.getRoles(), Role.DOCTOR_ROLE))
                .collect(Collectors.toList())
                .get(rand.nextInt(allUsers.size()));
    }

    @Override
    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.SERIALIZABLE,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return findByUsername(username);
    }

    @Override
    @Caching(evict = {
            @CacheEvict("all-users"),
            @CacheEvict(value = "user-by-username", key = "#user.username")
    })
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    @CacheEvict(value = "user-by-username", key = "#user.username")
    @Transactional(propagation = Propagation.NESTED,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public void deleteRole(User user, List<Role> roles) {
        List<Role> userRoles = new ArrayList<>(user.getRoles());
        user.setRoles(userRoles.stream().filter(x -> !roles.contains(x)).collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public void report(String message) {

    }

    @Override
    public void makeAppointment(Treatment treatment) {

    }

    @Override
    public void finishAppointment(Treatment treatment) {

    }

    @Override
    public byte[] export() {
        return new byte[0];
    }

    public boolean containsName(final Set<Role> list, final String name) {
        return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }

    @Scheduled(fixedDelay = 60000 * 60 * 12, initialDelay = 60000 * 60 * 12) // every 12 hours
    public void evictAllCaches() {

        List<String> caches = Arrays.asList("user-by-username", "all-users");
        caches.forEach(cacheName -> {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        });
    }
}
