package com.zackmurry.zmdb.settings;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomizationPort implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {


    //todo add port changing
    @Override
    public void customize(ConfigurableServletWebServerFactory server) {
        server.setPort(9001);
    }
}
