package com.somecompany.warehouse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somecompany.warehouse.exception.FileUploadException;
import com.somecompany.warehouse.exception.ProductNotFoundException;
import com.somecompany.warehouse.exception.ProductOutOfStockException;
import com.somecompany.warehouse.model.Product;
import com.somecompany.warehouse.model.ProductsDTO;
import com.somecompany.warehouse.repository.ProductRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Default Product Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ArticleService articleService;

  private final ObjectMapper objectMapper;

  /**
   * Load products into database
   *
   * @param productsFile productsFile
   * @throws IOException when fileupload/reading error
   * @throws FileUploadException when file Is null.
   */
  @Override
  public void loadProductsFromFile(MultipartFile productsFile)
      throws IOException, FileUploadException {
    if (null == productsFile) {
      log.error("No Products file provided...");
      throw new FileUploadException("Products File is Null");
    }
    log.info("Recieved loadProducts file ");
    ProductsDTO productsDTO = objectMapper
        .readValue(productsFile.getInputStream(), ProductsDTO.class);
    log.debug("Inserting {} productsDTO to database... ", productsDTO.getProducts().size());
    productsDTO.getProducts().forEach(productRepository::save);
  }

  /**
   * List of Product with on spot quantity calculated.
   *
   * @return List of Product with on spot quantity calculated.
   */
  @Override
  public List<Product> getProductsAndQuantity() {
    log.info("Recieved request to get Products and calculate quantity...");
    List<Product> products = productRepository.findAll();
    products.forEach(product -> product.setQuantity(calculateProductQuantity(product)));
    return products;
  }


  private Long calculateProductQuantity(Product product) {
    log.debug("Calculating quantity for {}", product.getName());
    SortedSet<Long> treeSet = new TreeSet<>();
    product.getContainArticles().forEach(productArticle -> {
      Long articleStockById = articleService.getArticleStockById(productArticle.getArticleId());
      Long quantityPossibleForThisArticle = articleStockById / productArticle.getAmountOf();
      treeSet.add(quantityPossibleForThisArticle);
    });
    return treeSet.contains(0L) ? 0L : treeSet.first();
  }

  /**
   * Sell product and update inventory.
   *
   * @param productName productName
   * @param quantity quantity
   * @throws ProductNotFoundException when not found
   * @throws ProductOutOfStockException when out of stock
   */
  public void sellProduct(String productName, Long quantity)
      throws ProductNotFoundException, ProductOutOfStockException {
    log.info("Received sell product request {} with quantity {}", productName, quantity);
    Optional<Product> productByName = productRepository.findByName(productName);
    if (!productByName.isPresent()) {
      log.error("No product found for name= " + productName);
      throw new ProductNotFoundException("No product found for name=" + productName);
    }
    Long productQuantity = calculateProductQuantity(
        productByName.get());
    if (quantity <= productQuantity) {
      sellAndUpdateInventory(productByName.get(), quantity);
    } else {
      log.debug("Not enough Stock for Product= " + productName);
      throw new ProductOutOfStockException(
          "OutOfStock Not enough articles to sell product:" + productName);
    }
  }

  private void sellAndUpdateInventory(Product product, Long quantity) {
    product.getContainArticles().forEach(productArticle -> {
      Long neededQuantityOfArticle = quantity * productArticle.getAmountOf();
      articleService
          .subtractStockFromArticleStock(productArticle.getArticleId(), neededQuantityOfArticle);
    });
  }
}
