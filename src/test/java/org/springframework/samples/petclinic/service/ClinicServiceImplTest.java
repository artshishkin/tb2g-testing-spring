package org.springframework.samples.petclinic.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ClinicServiceImplTest {

    @Test
    void findPetTypes() {
        //given
        PetRepository petRepository = Mockito.mock(PetRepository.class);
        ClinicServiceImpl clinicService = new ClinicServiceImpl(
                petRepository, null, null, null);
        List<PetType> petTypes = Arrays.asList(new PetType(), new PetType());
        given(petRepository.findPetTypes()).willReturn(petTypes);

        //when
        Collection<PetType> foundPetTypes = clinicService.findPetTypes();

        //then
        then(petRepository).should().findPetTypes();
        then(petRepository).shouldHaveNoMoreInteractions();
        assertThat(foundPetTypes).isNotNull().hasSize(2);
    }
}
