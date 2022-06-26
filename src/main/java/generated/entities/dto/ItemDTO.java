package generated.entities.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ItemDTO {
  private UUID id;

  private String name;
  
  private String type;
  
  private Integer price;
  
  private UUID parentId;
}
