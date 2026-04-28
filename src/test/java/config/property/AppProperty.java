package config.property;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppProperty {

    private String url;
    private Integer connectionTimeout;
    private Integer socketTimeout;

}
