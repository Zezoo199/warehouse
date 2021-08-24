package com.somecompany.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somecompany.warehouse.exception.FileUploadException;
import com.somecompany.warehouse.exception.ProductNotFoundException;
import com.somecompany.warehouse.exception.ProductOutOfStockException;
import com.somecompany.warehouse.model.Product;
import com.somecompany.warehouse.model.ProductArticle;
import com.somecompany.warehouse.repository.ProductRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ProductServiceTest {

  private ProductRepository productRepository = mock(ProductRepository.class);
  private ArticleService articleService = mock(ArticleService.class);
  private ObjectMapper objectMapper = new ObjectMapper();

  private ProductService subject = new ProductServiceImpl(productRepository, articleService,
      objectMapper);

  @Test
  void whenLoadProducts_thenCallRepositoryWithSave() throws IOException, FileUploadException {
    //given
    MultipartFile multipartFile = new MockMultipartFile("file",
        getFileAsString("products.json").getBytes());
    //when
    subject.loadProductsFromFile(multipartFile);

    //then
    verify(productRepository, times(2)).save(any());
  }

  @Test
  void shouldNotLoadProductsFromNullFile() {
    Assertions.assertThrows(FileUploadException.class, () -> {
      //given
      MultipartFile multipartFile = null;
      //when
      subject.loadProductsFromFile(multipartFile);

      //then
      verify(productRepository, times(0)).save(any());
    });
  }

  @Test
  void shouldNotLoadProductsFromBadFile() {
    //given
    Assertions.assertThrows(IOException.class, () -> {
      MultipartFile multipartFile = new MockMultipartFile("file",
          "BlaFileBla".getBytes());
      //when
      subject.loadProductsFromFile(multipartFile);

      //then
      verify(productRepository, times(0)).save(any());
    });
  }

  @Test
  void whenGetProductsWithoutStock_thenQuantityProductsReturnWithZeroQuantity() {
    //given
    when(productRepository.findAll()).thenReturn(givenProducts());
    //when
    List<Product> productsAndQuantity = subject.getProductsAndQuantity();
    //then

    assertEquals("PName", productsAndQuantity.get(0).getName());
    assertEquals(0L, productsAndQuantity.get(0).getQuantity());
  }

  @Test
  void whenGetProductsWitStock_thenQuantityProductsReturns() {
    //given
    when(productRepository.findAll()).thenReturn(givenProducts());
    when(articleService.getArticleStockById(1L)).thenReturn(9L);
    //when
    List<Product> productsAndQuantity = subject.getProductsAndQuantity();
    //then

    assertEquals("PName", productsAndQuantity.get(0).getName());
    assertEquals(2L, productsAndQuantity.get(0).getQuantity());
  }

  @Test
  void whenSellProductsWithStock_thenSoldAndInventoryUpdated()
      throws ProductNotFoundException, ProductOutOfStockException {
    //given a product and article with stock
    Optional<Product> givenProduct = Optional.of(givenProduct("PName"));
    when(productRepository.findByName("PName")).thenReturn(givenProduct);
    when(articleService.getArticleStockById(1L)).thenReturn(4L);

    //when
    subject.sellProduct("PName", 1L);
    //then check if it calls with correct parameters
    verify(articleService, times(1)).subtractStockFromArticleStock(
        givenProduct.get().getContainArticles().get(0).getArticleId(),
        givenProduct.get().getContainArticles().get(0).getAmountOf());
  }

  @Test
  void whenSellProductsWithoutStock_thenException() {
    //given a product and article with stock 0 by default
    //when sell then throw exception
    Assertions.assertThrows(ProductOutOfStockException.class, () -> {
      Optional<Product> givenProduct = Optional.of(givenProduct("PName"));
      when(productRepository.findByName("PName")).thenReturn(givenProduct);
      //when
      subject.sellProduct("PName", 1L);
      //then check if it calls with correct parameters
      verify(articleService, times(0)).subtractStockFromArticleStock(
          givenProduct.get().getContainArticles().get(0).getArticleId(),
          givenProduct.get().getContainArticles().get(0).getAmountOf());
    });
  }

  @Test
  void whenSellProductsNotFound_thenException() {
    //given a product and article with stock 0 by default
    //when sell then throw exception
    Assertions.assertThrows(ProductNotFoundException.class, () -> {
      when(productRepository.findByName("NOT_FOUND_NAME")).thenReturn(Optional.empty());
      //when
      subject.sellProduct("NOT_FOUND_NAME", 1L);

    });
  }

  private List<Product> givenProducts() {
    return Collections.singletonList(givenProduct("PName"));
  }

  private Product givenProduct(String name) {
    Product product = new Product();
    product.setName(name);
    product.setId(1L);
    product.setQuantity(0L);
    product.setContainArticles(new ArrayList<>());
    product.getContainArticles().add(givenProductArticle(1L, 4L));
    return product;
  }

  private ProductArticle givenProductArticle(long productId, long amountOf) {
    ProductArticle productArticle = new ProductArticle();
    productArticle.setAmountOf(amountOf);
    productArticle.setProductId(productId);
    productArticle.setId(1L);
    productArticle.setArticleId(1L);
    return productArticle;
  }


  private String getFileAsString(String fileName) throws IOException {
    return IOUtils
        .toString(this.getClass().getClassLoader().getResource(fileName).openStream(),
            "UTF-8"
        );
  }
}
