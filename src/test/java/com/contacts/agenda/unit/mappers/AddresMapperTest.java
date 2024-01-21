package com.contacts.agenda.unit.mappers;

import com.contacts.agenda.model.dtos.address.AddressAddDto;
import com.contacts.agenda.model.dtos.address.AddressReadDTO;
import com.contacts.agenda.model.dtos.contact.ContactReadDTO;
import com.contacts.agenda.model.entities.AddressEntity;
import com.contacts.agenda.model.entities.ContactEntity;
import com.contacts.agenda.model.mappers.AddressMapper;
import com.contacts.agenda.model.mappers.ContactMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class AddresMapperTest {
    @Autowired
    AddressMapper addressMapper;

    @DisplayName("Dado un addressAddDTO, mapear a AddressEntity")
    @Test
    public void addressAddDtoToEntity() throws Exception {
        AddressAddDto addDto = new AddressAddDto("Correa", "4747");
        AddressEntity entity = addressMapper.addressAddDtoToEntity(addDto);
        assertThat(entity.getNumber()).isEqualTo(addDto.getNumber());
        assertThat(entity.getStreet()).isEqualTo(addDto.getStreet());
        assertThat(entity.getId()).isNull();
    }

    @DisplayName("Dado una AddressEntity, mapear a ReadDTO")
    @Test
    public void addressEntityToReadDto() throws Exception {
        AddressEntity entity = new AddressEntity(1,"Correa", "4747");
        AddressReadDTO readDTO = addressMapper.addressEntityToReadDto(entity);
        assertThat(readDTO.getNumber()).isEqualTo(entity.getNumber());
        assertThat(readDTO.getStreet()).isEqualTo(entity.getStreet());
        assertThat(readDTO.getId()).isEqualTo(entity.getId());
    }
    @DisplayName("Dado un ReadDTO, mapear a Entity")
    @Test
    public void addressReadDtoToEntity() throws Exception {
        AddressReadDTO readDTO = new AddressReadDTO(1,"Correa", "4747");
        AddressEntity entity = addressMapper.addressReadDtoToEntity(readDTO);
        assertThat(entity.getNumber()).isEqualTo(readDTO.getNumber());
        assertThat(entity.getStreet()).isEqualTo(readDTO.getStreet());
        assertThat(entity.getId()).isEqualTo(readDTO.getId());
    }
}
