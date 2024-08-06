package org.zerobase.jwitter.domain.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

@Configuration
public class JacksonConfig extends SimpleModule {
    @Bean
    public Module customModule() {
        return new Module() {

            @Override
            public String getModuleName() {
                return "CustomModule";
            }

            @Override
            public Version version() {
                return Version.unknownVersion();
            }

            @Override
            public void setupModule(SetupContext context) {

                SimpleSerializers serializers = new SimpleSerializers();

                serializers.addSerializer(Page.class,
                        new PageJsonSerializer());

                context.addSerializers(serializers);
            }
        };
    }
}