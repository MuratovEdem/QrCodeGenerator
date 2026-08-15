package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.CipherDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.Cipher;
import controlm.qrcodegenerator.repository.CipherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CipherService {
    private final CipherRepository cipherRepository;

    public Cipher findById(Long id) {
        return cipherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cipher not found with id: " + id));
    }

    public List<CipherDto> findAll() {
        return cipherRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Cipher save(CipherDto dto) {
        Cipher cipher = new Cipher();
        cipher.setName(dto.getName());
        cipher.setDescription(dto.getDescription());

        return cipherRepository.save(cipher);
    }

    public void deleteById(Long id) {
        cipherRepository.deleteById(id);
    }

    public void update(CipherDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID шифра не может быть null");
        }
        Cipher cipher = findById(dto.getId());
        cipher.setName(dto.getName());
        cipher.setDescription(dto.getDescription());
        cipherRepository.save(cipher);
    }

    private CipherDto toDto(Cipher cipher) {
        CipherDto cipherDto = new CipherDto();
        cipherDto.setId(cipher.getId());
        cipherDto.setName(cipher.getName());
        cipherDto.setDescription(cipher.getDescription());
        return cipherDto;
    }
}
