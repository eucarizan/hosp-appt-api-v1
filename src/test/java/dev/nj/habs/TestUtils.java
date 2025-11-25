package dev.nj.habs;

import tools.jackson.databind.ObjectMapper;

public class TestUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(final Object obj) {
        return objectMapper.writeValueAsString(obj);
    }
}
