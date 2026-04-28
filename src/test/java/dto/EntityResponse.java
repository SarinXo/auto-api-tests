package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityResponse {

    private Integer id;
    private AdditionResponse addition;
    @JsonProperty("important_numbers")
    private List<Integer> importantNumbers;
    private String title;
    private Boolean verified;

}
