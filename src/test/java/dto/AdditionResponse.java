package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AdditionResponse {

    private Integer id;
    @JsonProperty("additional_info")
    private String additionalInfo;
    @JsonProperty("additional_number")
    private Integer additionalNumber;

}