package com.contacts.agenda.unit.mappers;

import com.contacts.agenda.model.dtos.contact.ContactAddDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.entities.ContactEntity;
import com.contacts.agenda.model.mappers.ContactMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactMapperTest {
    @Autowired
    ContactMapper contactMapper;


    @DisplayName("Dado una entidad contacto, mappear a ReadDTO")
    @Test
    public void contactEntityToReadDTO() throws Exception {
        AddressEntity addressEntity = new AddressEntity(1, "Correa", "4747");
        ContactEntity contactEntity = new ContactEntity(1 ,"David", "2644647474", addressEntity);
        ContactReadDTO contactReadDTO = contactMapper.toReadDTO(contactEntity);
        assertThat(contactReadDTO.getId()).isEqualTo(contactEntity.getId());
        assertThat(contactReadDTO.getName()).isEqualTo(contactEntity.getName());
        assertThat(contactReadDTO.getPhone()).isEqualTo(contactEntity.getPhone());
        assertThat(contactReadDTO.getAddress().getNumber()).isEqualTo(contactEntity.getAddress().getNumber());
        assertThat(contactReadDTO.getAddress().getStreet()).isEqualTo(contactEntity.getAddress().getStreet());

    }
    @DisplayName("Dado un addDTO contact, mappear a Entity")
    @Test
    public void contactAddDTOToEntity() throws Exception {
        AddressEntity addressEntity = new AddressEntity(1, "Correa", "4747");
        ContactAddDTO contactAddDTO = new ContactAddDTO("David", "2644647474",addressEntity);

        ContactEntity contactEntity = contactMapper.toEntity(contactAddDTO);
        assertThat(contactEntity.getName()).isEqualTo(contactAddDTO.getName());
        assertThat(contactEntity.getPhone()).isEqualTo(contactAddDTO.getPhone());
        assertThat(contactEntity.getAddress().getNumber()).isEqualTo(contactAddDTO.getAddress().getNumber());
        assertThat(contactEntity.getAddress().getStreet()).isEqualTo(contactAddDTO.getAddress().getStreet());
    }
}
