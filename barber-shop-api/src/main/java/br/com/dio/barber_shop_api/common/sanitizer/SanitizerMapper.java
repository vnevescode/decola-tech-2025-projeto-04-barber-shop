package br.com.dio.barber_shop_api.common.sanitizer;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class SanitizerMapper {

    @Named("sanitize")
    public String sanitize(String value) {
        return SanitizationUtil.sanitize(value);
    }
}
