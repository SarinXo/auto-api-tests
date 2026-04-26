package util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class JsonParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> T loadObject(String filePath, Class<T> clazz) {
        try (InputStream is = JsonParser.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new RuntimeException("Файл не найден: " + filePath);
            }

            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return MAPPER.readValue(json, clazz);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при Конвертации json -> object: " + filePath, e);
        }
    }

    public static <T> List<T> loadListObjects(String filePath, Class<T> clazz) {
        try (InputStream is = JsonParser.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new RuntimeException("Файл не найден: " + filePath);
            }

            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JavaType listType = MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, clazz);

            return MAPPER.readValue(json, listType);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при Конвертации json -> object: " + filePath, e);
        }
    }

    public static <T> T convertTo(String json, Class<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при Конвертации json -> object: " + json, e);
        }
    }

}
