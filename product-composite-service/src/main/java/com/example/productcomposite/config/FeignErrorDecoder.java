package com.example.productcomposite.config;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        if (status == 503 || status == 404) {
            // ✅ Resilience4J captera cette exception
            return FeignException.errorStatus(methodKey, response);
        }

        return FeignException.errorStatus(methodKey, response);
    }
}
