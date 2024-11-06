package com.emazon.ms_shopping_cart.application.utils;

import com.emazon.ms_shopping_cart.ConsUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParsingUtils {

    private ParsingUtils() {
    }

    public static <T> String joinListElements(List<T> elements) {
        return elements.stream().map(String::valueOf).collect(Collectors.joining(ConsUtils.COMMA_DELIMITER));
    }

    public static <T, K, V> Map<K, V> mapSetToMap(Set<T> list, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }
}
