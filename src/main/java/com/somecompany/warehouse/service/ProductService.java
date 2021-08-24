package com.somecompany.warehouse.service;

import com.somecompany.warehouse.exception.FileUploadException;
import com.somecompany.warehouse.exception.ProductNotFoundException;
import com.somecompany.warehouse.exception.ProductOutOfStockException;
import com.somecompany.warehouse.model.Product;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Product Service
 */
public interface ProductService {

  /**
   * Load products into database
   *
   * @param productsFile productsFile
   * @throws IOException when fileupload/reading error
   * @throws FileUploadException when file Is null.
   */
  void loadProductsFromFile(MultipartFile productsFile) throws IOException, FileUploadException;

  /**
   * List of Product with on spot quantity calculated.
   *
   * @return List of Product with on spot quantity calculated.
   */
  List<Product> getProductsAndQuantity();

  /**
   * Sell product and update inventory.
   *
   * @param productName productName
   * @param quantity quantity
   * @throws ProductNotFoundException when not found
   * @throws ProductOutOfStockException when out of stock
   */
  void sellProduct(String productName, Long quantity)
      throws ProductNotFoundException, ProductOutOfStockException;
}
