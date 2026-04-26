package util;

import config.property.YmlConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class YamlReader {

    public YmlConfig readConfig() {
        try (InputStream inputStream = YamlReader.class
                .getClassLoader()
                .getResourceAsStream("app.yml")
        ) {
            if (inputStream == null) {
                throw new RuntimeException("Файл app.yml не найден в resources!");
            }

            Yaml yaml = new Yaml();
            return yaml.loadAs(inputStream, YmlConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении конфигурации YAML: " + e.getMessage(), e);
        }
    }

}
