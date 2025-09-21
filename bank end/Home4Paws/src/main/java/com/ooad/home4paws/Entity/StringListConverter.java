package com.ooad.home4paws.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return Optional.ofNullable(stringList)
                .orElse(new ArrayList<>()) // Return empty list to avoid NPE
                .stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return Optional.ofNullable(string)
                .filter(s -> !s.trim().isEmpty())
                .map(s -> Arrays.asList(s.split(SPLIT_CHAR)))
                .orElse(new ArrayList<>());
    }
}
