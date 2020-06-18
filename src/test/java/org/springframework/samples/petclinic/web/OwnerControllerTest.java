package org.springframework.samples.petclinic.web;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.hamcrest.CoreMatchers;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.web.OwnerController.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class OwnerControllerTest {

    @Autowired
    OwnerController ownerController;

    @Autowired
    ClinicService clinicService;

    private MockMvc mockMvc;
    private Owner owner;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
        owner = new Owner();
        owner.setId(100);
        owner.setLastName("LastName");
    }

    @AfterEach
    void tearDown() {
        //It did not worked in John Thompson's test. So he added that reset
        //But it is not recommended by the docs
//        reset(clinicService);
    }

    @Test
    void testUpdateOwnerPostValid() throws Exception {
        int ownerId = 123;
        mockMvc.perform(
                post("/owners/{ownerId}/edit", ownerId)
                        .param("firstName", "Foo")
                        .param("lastName", "Bar")
                        .param("address", "UnFavouriteStreet 15")
                        .param("city", "Kram")
                        .param("telephone", "1234567890"))
                .andExpect(model().attributeHasNoErrors("owner"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"))       //valid
                .andExpect(redirectedUrlTemplate("/owners/{ownerId}", ownerId));  //valid
//                .andExpect(redirectedUrl("/owners/{ownerId}"));     //invalid
//                .andExpect(redirectedUrlTemplate("/owners/{ownerId}"));               //invalid
//        then(clinicService).should().saveOwner(ArgumentMatchers.any(Owner.class));
        verify(clinicService).saveOwner(ArgumentMatchers.any(Owner.class));
    }

    @Test
    void testUpdateOwnerPostInvalid() throws Exception {
        int ownerId = 123;
        mockMvc.perform(
                post("/owners/{ownerId}/edit", ownerId)
//                        .param("firstName", "Foo")
                        .param("lastName", "Bar")
                        .param("address", "UnFavouriteStreet 15")
                        .param("city", "")
                        .param("telephone", "1234567890"))
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeErrorCount("owner", 2))
                .andExpect(model().attributeHasFieldErrors("owner", "firstName", "city"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEWS_OWNER_CREATE_OR_UPDATE_FORM));
    }

    @Test
    void testNewOwnerPostValid() throws Exception {
        mockMvc.perform(
                post("/owners/new")
                        .param("firstName", "Foo")
                        .param("lastName", "Bar")
                        .param("address", "UnFavouriteStreet 15")
                        .param("city", "Kram")
                        .param("telephone", "1234567890"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(startsWith("redirect:/owners/")));
    }

    @Test
    void testNewOwnerPostInvalid() throws Exception {
        mockMvc.perform(
                post("/owners/new")
                        .param("firstName", "Foo")
                        .param("lastName", "Bar")
//                        .param("address", "UnFavouriteStreet 15")
                        .param("city", "Kram")
                        .param("telephone", "1234567890123"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEWS_OWNER_CREATE_OR_UPDATE_FORM))
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address", "telephone"));
    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEWS_OWNER_CREATE_OR_UPDATE_FORM))
                .andExpect(model().attributeExists("owner"))
                .andExpect(model().attribute("owner", instanceOf(Owner.class)));
    }


    @Test
    void processFindByNameNotFound() throws Exception {
        mockMvc.perform(get("/owners").param("lastName", "NoName"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));
    }


    @Test
    void processFindListOfOwners() throws Exception {
        //given
        given(clinicService.findOwnerByLastName("")).willReturn(Lists.list(new Owner(), new Owner()));

        //when
        ResultActions resultActions = mockMvc.perform(get("/owners"));

        //then
        then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("");
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"))
                .andExpect(model().attributeExists("selections"))
                .andExpect(model().attribute("selections", everyItem(instanceOf(Owner.class))))
                .andExpect(model().attribute("selections", IsCollectionWithSize.hasSize(2)));


    }

    @Test
    void processFindByName1And2Results() throws Exception {
        given(clinicService.findOwnerByLastName(anyString()))
                .willReturn(Collections.singletonList(owner))
                .willReturn(Arrays.asList(owner, new Owner()));

        mockMvc.perform(get("/owners").param("lastName", "Foo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/100"));

        mockMvc.perform(get("/owners").param("lastName", "Foo"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"))
                .andExpect(model().attributeExists("selections"))
                .andExpect(model().attribute("selections", everyItem(instanceOf(Owner.class))))
                .andExpect(model().attribute("selections", IsCollectionWithSize.hasSize(2)));
    }


}
