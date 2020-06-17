package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VetControllerTest {

    @Mock
    ClinicService clinicService;

    @InjectMocks
    VetController vetController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        given(clinicService.findVets()).willReturn(Arrays.asList(new Vet(), new Vet()));

        mockMvc = MockMvcBuilders.standaloneSetup(vetController).build();

    }

    @Test
    void testControllerShowVetList() throws Exception {
        mockMvc
                .perform(get("/vets.html"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vets"))
                .andExpect(model().attribute("vets", instanceOf(Vets.class)))
                .andExpect(view().name("vets/vetList"));
    }

    @Test
    void showVetList() {
        //given
        Map<String, Object> model = new HashMap<>();

        //when
        String viewName = vetController.showVetList(model);

        //then
        then(clinicService).should().findVets();
        then(clinicService).shouldHaveNoMoreInteractions();
        assertThat(viewName).isEqualToIgnoringCase("vets/vetList");
        assertThat(model.get("vets")).isInstanceOf(Vets.class);
        assertThat(((Vets) model.get("vets")).getVetList()).hasSize(2);

    }

    @Test
    void showResourcesVetList() {
        //given
        //when
        Vets vets = vetController.showResourcesVetList();

        //then
        then(clinicService).should().findVets();
        assertThat(vets).isNotNull();
        assertThat(vets.getVetList()).hasSize(2);
    }
}
