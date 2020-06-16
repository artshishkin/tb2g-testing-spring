package org.springframework.samples.petclinic.sfg.junit5;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.sfg.HearingInterpreter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource("classpath:yanny.properties")
@ActiveProfiles("externalized")
@SpringJUnitConfig(classes = PropertiesTest.TestConfig.class)
class PropertiesTest {

    @Profile("externalized")
    @Configuration
    @ComponentScan("org.springframework.samples.petclinic.sfg")
    static class TestConfig{
    }

    @Autowired
    HearingInterpreter hearingInterpreter;

    @Test
    void whatIheard() {
        //when
        String word = hearingInterpreter.whatIheard();
        //then
        assertThat(word).isEqualTo("YaNNy");
    }
}