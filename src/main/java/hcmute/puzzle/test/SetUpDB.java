package hcmute.puzzle.test;

import hcmute.puzzle.entities.CandidateEntity;
import hcmute.puzzle.entities.RoleEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetUpDB {
    @Autowired
    RoleRepository roleRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    public void preStart() {

        List<RoleEntity> roles = new ArrayList<>();
        long num = roleRepository.count();

        if (num == 0) {
            List<String> roleCodes = new ArrayList<>();

            roleCodes.add("user");
            roleCodes.add("admin");

            roleCodes.stream().forEach(code -> {
                RoleEntity role = new RoleEntity();
                role.setCode(code);
                role.setName(code.toUpperCase());
                roles.add(role);
            });

            roleRepository.saveAll(roles);
        }

        RoleEntity userRole1 = new RoleEntity();
        userRole1.setCode("admin");
        userRole1.setName(userRole1.getCode().toUpperCase());

        RoleEntity userRole2 = new RoleEntity();
        userRole2.setCode("user");
        userRole2.setName(userRole2.getCode().toUpperCase());

        RoleEntity userRole3 = new RoleEntity();
        userRole3.setCode("employer");
        userRole3.setName(userRole3.getCode().toUpperCase());

        RoleEntity userRole4 = new RoleEntity();
        userRole4.setCode("candidate");
        userRole4.setName(userRole4.getCode().toUpperCase());


        // User
        UserEntity user1 = new UserEntity();
        user1.setEmail("candidate1@gmail.com");
        user1.setPassword(passwordEncoder.encode("123456"));

        UserEntity user2 = new UserEntity();
        user2.setEmail("candidate2@gmail.com");
        user2.setPassword(passwordEncoder.encode("123456"));

        UserEntity user3 = new UserEntity();
        user3.setEmail("employer1@gmail.com");
        user3.setPassword(passwordEncoder.encode("123456"));

        UserEntity user4 = new UserEntity();
        user4.setEmail("employer2@gmail.com");
        user4.setPassword(passwordEncoder.encode("123456"));

        UserEntity user5 = new UserEntity();
        user5.setEmail("admin1@gmail.com");
        user5.setPassword(passwordEncoder.encode("123456"));


        //Candidate
        CandidateEntity candidate1 = new CandidateEntity();
        candidate1.setFirstName("Minh");
        candidate1.setLastName("Lê Quang");

        CandidateEntity candidate2 = new CandidateEntity();
        candidate2.setFirstName("Phong");
        candidate2.setLastName("Vũ");

        //


    }
}
