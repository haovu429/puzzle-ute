package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.NotFoundException;
import hcmute.puzzle.infrastructure.entities.Candidate;
import hcmute.puzzle.infrastructure.entities.Employer;
import hcmute.puzzle.infrastructure.entities.Role;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.models.enums.Roles;
import hcmute.puzzle.infrastructure.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Transactional
	public void setRoleWithCreateAccountTypeUser(List<String> roleCodes, User user) {
		if (roleCodes != null && !roleCodes.isEmpty()) {
			Set<Role> roleFromDB = new HashSet<>();
			for (String code : roleCodes) {
				Role role = roleRepository.findByCode(code)
										  .orElseThrow(() -> new NotFoundException("NOT_FOUND_ROLE: + " + code));
				if (role.getCode().equalsIgnoreCase(Roles.CANDIDATE.getValue())) {
					Candidate candidate = Candidate.builder()
												   .emailContact(user.getEmail())
												   .firstName(user.getFullName())
												   .build();
					candidate.setUser(user);
					user.setCandidate(candidate);
				} else if (role.getName().equalsIgnoreCase(Roles.EMPLOYER.getValue())) {
					Employer employer = Employer.builder().firstName(user.getFullName()).build();
					employer.setUser(user);
					user.setEmployer(employer);
				}
				roleFromDB.add(role);
			}
			user.setRoles(roleFromDB);
		}
	}
}
