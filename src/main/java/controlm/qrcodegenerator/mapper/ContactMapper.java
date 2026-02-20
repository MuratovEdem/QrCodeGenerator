package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ContactRequestDto;
import controlm.qrcodegenerator.model.Contact;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContactMapper {

    public Contact dtoToContact(ContactRequestDto contactRequestDto) {
        Contact contact = new Contact();

        contact.setName(contactRequestDto.getName());
        contact.setPost(contactRequestDto.getPost());
        contact.setEmail(contactRequestDto.getEmail());
        contact.setPhoneNumber(contactRequestDto.getPhoneNumber());

        return contact;
    }

    public List<Contact> dtosToContacts(List<ContactRequestDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }

        List<Contact> contacts = new ArrayList<>();

        for (ContactRequestDto dto : dtos) {
            contacts.add(dtoToContact(dto));
        }

        return contacts;
    }
}
