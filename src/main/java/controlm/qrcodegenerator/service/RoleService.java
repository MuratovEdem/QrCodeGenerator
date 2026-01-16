package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.Role;
import controlm.qrcodegenerator.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() ->
                new NotFoundException("Role with name = " + name + " not found"));
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Role with id = " + id + " not found"));
    }
}
