package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.Contact;
import controlm.qrcodegenerator.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public List<Contact> getByClientId(Long clientId) {
        return contactRepository.findAllByClientId(clientId);
    }

    public void saveListByClient(List<Contact> contacts, Client client) {
        for (Contact contact : contacts) {
            contact.setClient(client);
            contactRepository.save(contact);
        }
    }
}
