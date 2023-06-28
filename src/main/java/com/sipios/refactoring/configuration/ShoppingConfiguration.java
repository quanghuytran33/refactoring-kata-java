package com.sipios.refactoring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

import static java.time.ZoneId.SHORT_IDS;

@Configuration
public class ShoppingConfiguration {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(SHORT_IDS.get("ECT")));
    }
}