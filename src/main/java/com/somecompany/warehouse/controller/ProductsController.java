package com.somecompany.warehouse.controller;

import com.somecompany.warehouse.exception.ProductNotFoundException;
import com.somecompany.warehouse.exception.ProductOutOfStockException;
import com.somecompany.warehouse.model.Product;
import com.somecompany.warehouse.service.ArticleService;
import com.somecompany.warehouse.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductsController {

  private final ArticleService articleService;

  private final ProductService productService;

  /**
   *
   * @return
   */
  @GetMapping("getProducts")
  @Operation(summary = "Load ProductsDTO from database and calculate Quantity")
  public List<Product> getProductsAndQuantity() {
    return productService.getProductsAndQuantity();
  }

  /**
   *
   * @param productName
   * @param quantity
   * @throws ProductNotFoundException
   * @throws ProductOutOfStockException
   */
  @PostMapping("sellProduct")
  @Operation(summary = "Sell or remove Product from database and update InventoryDTO")
  public void sellProduct(@RequestParam("productName") String productName,
      @RequestParam("quantity") Long quantity)
      throws ProductNotFoundException, ProductOutOfStockException {
    productService.sellProduct(productName, quantity);
  }

}
