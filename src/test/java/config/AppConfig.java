package config;

import config.property.YmlConfig;
import util.YamlReader;

public class AppConfig {

    public final static String URL;
    public final static Integer CONNECTION_TIMEOUT;
    public final static Integer SOCKET_TIMEOUT;

    static {
        YamlReader yamlReader = new YamlReader();
        YmlConfig ymlConfig = yamlReader.readConfig();

        URL = ymlConfig.getApp().getUrl();
        CONNECTION_TIMEOUT = ymlConfig.getApp().getConnectionTimeout();
        SOCKET_TIMEOUT = ymlConfig.getApp().getSocketTimeout();
    }
}
