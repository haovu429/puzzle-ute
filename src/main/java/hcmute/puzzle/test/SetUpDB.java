package hcmute.puzzle.test;

import hcmute.puzzle.entities.RoleEntity;
import hcmute.puzzle.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetUpDB {
    @Autowired
    RoleRepository roleRepository;

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


    }
}
