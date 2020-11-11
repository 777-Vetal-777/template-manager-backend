package com.itextpdf.dito.manager.config;

import com.itextpdf.dito.manager.dto.UserCreateRequest;
import com.itextpdf.dito.manager.entity.User;
import ma.glasnost.orika.MapperFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingConfig implements OrikaMapperFactoryConfigurer {
    @Override
    public void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(User.class, UserCreateRequest.class)
                .byDefault()
                .register();
    }
}
