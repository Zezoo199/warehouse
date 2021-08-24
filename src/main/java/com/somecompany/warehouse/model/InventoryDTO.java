package com.somecompany.warehouse.model;

import java.util.List;
import lombok.Data;

/**
 * InventoryDTO
 */
@Data
public class InventoryDTO {

  private List<Article> inventory;
}
