package kz.iitu.orken.medical_managament_system.service.impl;

import kz.iitu.orken.medical_managament_system.Exception.NotAllowedException;
import kz.iitu.orken.medical_managament_system.Exception.TransactionException;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private ExcelService excelService;
    private CacheManager cacheManager;

    public UserServiceImpl() {
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, CacheManager cacheManager, ExcelService excelService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cacheManager = cacheManager;
        this.excelService = excelService;
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
    public User findById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict("all-users"),
            @CacheEvict(value = "user-by-username", key = "#user.username")
    })
    public User save(User user) {
        user.setRoles(Collections.singleton(roleRepository.findByName(Role.DOCTOR_ROLE)));
        return userRepository.save(user);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.SERIALIZABLE,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public User randomDoctor() {
        Random rand = new Random();
        List<User> allUsers = findAll();
        List<User> doctors = allUsers.stream()
                .filter(x -> containsName(x.getRoles(), Role.DOCTOR_ROLE))
                .collect(Collectors.toList());
        return doctors.get(rand.nextInt(doctors.size()));
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
    @Transactional(propagation = Propagation.SUPPORTS,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public void delete(User user) {
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Override
    public void deleteById(Long id) {
        User user = findById(id);
        Optional.ofNullable(user).ifPresent(this::delete);
    }

    @Override
    @CacheEvict(value = "user-by-username", key = "#user.username")
    @Transactional(propagation = Propagation.NESTED,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public void deleteRole(User user, List<String> roles) {
        List<Role> userRoles = new ArrayList<>(user.getRoles());
        user.setRoles(userRoles.stream().filter(x -> !roles.contains(x.getName())).collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public void report(String message) {

    }

    @Override
    public ByteArrayResource export() {
        ByteArrayOutputStream stream;
        try {
            stream = excelService.exportUsers(findAll());
        } catch (Exception e) {
            stream = new ByteArrayOutputStream();
        }
        return new ByteArrayResource(stream.toByteArray());
    }

    public boolean containsName(final Set<Role> list, final String name) {
        return list.stream().anyMatch(o -> o.getName().equals(name));
    }

    @Scheduled(fixedDelay = 60000 * 60 * 12, initialDelay = 60000 * 60 * 12) // every 12 hours
    public void evictAllCaches() {
        List<String> caches = Arrays.asList("user-by-username", "all-users");
        caches.forEach(cacheName -> {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        });
    }
}
