package demo.avoris.infrastructure.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registro de módulo para Java 8 Date/Time
        mapper.registerModule(new JavaTimeModule());

        // Evita escribir fechas como timestamps (long)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Evita ajuste automático de zona horaria
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        return mapper;
    }
}

