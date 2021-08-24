package com.somecompany.warehouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 * Article of Inventory
 */
@Entity
@Data
public class Article {

  @JsonProperty("art_id")
  @Id
  private Long id;
  private String name;
  private Long stock;
}
