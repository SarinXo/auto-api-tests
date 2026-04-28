package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityGetAllRequest {

    private String title;
    private Boolean verified;
    private Integer page;
    private Integer perPage;

}
