package com.somecompany.warehouse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Data;

/**
 * Product both Json and Database Entity
 */
@Entity
@Data
public class Product {

  @JsonProperty("contain_articles")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "productId", referencedColumnName = "id")
  List<ProductArticle> containArticles;
  @Id
  @GeneratedValue
  private Long id;
  @Column(unique = true)
  private String name;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Transient
  private Long quantity;

}
