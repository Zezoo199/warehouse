package com.somecompany.warehouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

/**
 * Product Article both JSON And DB Entity
 */
@Data
@Entity
public class ProductArticle {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @JsonProperty("art_id")
  private Long articleId;

  @JsonProperty("amount_of")
  private Long amountOf;

  private Long productId;
}
