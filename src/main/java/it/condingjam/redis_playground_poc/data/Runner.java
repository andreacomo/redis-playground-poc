package it.condingjam.redis_playground_poc.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Profile(DataConfig.PROFILE)
public class Runner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    public Runner(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role customer = getOrSaveRole("customer");
        Role admin = getOrSaveRole("admin");

        User user = userRepository.findFirstByEmail("andrea.como@gmail.com");
        if (user == null) {
            user = userRepository.save(createUser(admin, customer));
            LOGGER.info("User saved with id {}", user.getId());
        } else {
            LOGGER.info("Found user with id {} with roles {}", user.getId(), user.getRoles());
        }
    }

    private User createUser(Role... role) {
        User user = new User();
        user.setEmail("andrea.como@gmail.com");
        user.setName("Andrea");
        user.setPassword("enc:1234");
        user.setRoles(Set.of(role));

        return user;
    }

    private Role getOrSaveRole(String roleName) {
        List<Role> roles = roleRepository.findByName(roleName);
        if (roles.isEmpty()) {
            Role customer = roleRepository.save(new Role(roleName));
            LOGGER.info("Role saved with id {}", customer.getId());
            return customer;
        } else {
            LOGGER.info("Role found with id {}", roles.getFirst().getId());
            return roles.getFirst();
        }
    }
}
