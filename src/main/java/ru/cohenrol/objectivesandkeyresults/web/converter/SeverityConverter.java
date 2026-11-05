package ru.cohenrol.objectivesandkeyresults.web.converter;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.cohenrol.objectivesandkeyresults.domain.enums.Severity;

@Component
public class SeverityConverter implements Converter<String, Severity> {
    @Override
    public Severity convert(@NonNull String source) {
        if (source.isBlank()) {
            return null;
        }
        return Severity.valueOf(source.trim().toUpperCase());
    }
}