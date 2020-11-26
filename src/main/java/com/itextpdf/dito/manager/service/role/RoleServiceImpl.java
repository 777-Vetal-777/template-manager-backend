package com.itextpdf.dito.manager.service.role;

import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.exception.RoleCannotBeRemovedException;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(final RoleRepository roleRepository,
                           final UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoleDTO create(final RoleEntity roleEntity) {
        return null;
    }

    @Override
    public Page<RoleEntity> list(final Pageable pageable, final String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? roleRepository.findAll(pageable)
                : roleRepository.search(pageable, searchParam);
    }

    @Override
    public RoleDTO update(final RoleEntity entity) {
        return null;
    }

    @Override
    public void delete(final String name) {
        if (userRepository.countOfUserWithOnlyOneRole(name) > 0) {
            throw new RoleCannotBeRemovedException(new StringBuilder("Role cannot be removed. There are users with only one role: ")
                    .append(name)
                    .toString());
        }
        // TODO uncomment when DTM-150 is implemented
        // roleRepository.delete(roleRepository.findByName(name));
    }
}
