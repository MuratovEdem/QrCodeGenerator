package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ContactRequestDto;
import controlm.qrcodegenerator.dto.response.ContactResponseDto;
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

    public ContactResponseDto toResponseDto(Contact contact) {
        ContactResponseDto contactResponseDto = new ContactResponseDto();

        contactResponseDto.setEmail(contact.getEmail());
        contactResponseDto.setName(contact.getName());
        contactResponseDto.setPost(contact.getPost());
        contactResponseDto.setPhoneNumber(contact.getPhoneNumber());

        return contactResponseDto;
    }

    public List<ContactResponseDto> toResponseDtos(List<Contact> contacts) {
        List<ContactResponseDto> contactResponseDtoList = new ArrayList<>();

        for (Contact contact : contacts) {
            contactResponseDtoList.add(toResponseDto(contact));
        }

        return contactResponseDtoList;
    }
}
