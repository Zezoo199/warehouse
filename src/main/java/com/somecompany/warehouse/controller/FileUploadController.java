package com.somecompany.warehouse.controller;

import com.somecompany.warehouse.exception.FileUploadException;
import com.somecompany.warehouse.service.ArticleService;
import com.somecompany.warehouse.service.ProductService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Upload Controller to upload files.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class FileUploadController {

  private final ArticleService articleService;

  private final ProductService productService;

  /**
   * Load articles from file and save it to DB
   *
   * @param articlesFile articles Json file
   * @throws IOException when error on file reading.
   */
  @PostMapping(path = "loadArticles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Load articles File")
  public void loadArticles(
      @ApiParam("Article file in JSON Format") @RequestParam("file") MultipartFile articlesFile)
      throws IOException, FileUploadException {
    articleService.loadArticlesFromFile(articlesFile);
  }

  /**
   * @param productsFile products Json file.
   * @throws IOException when error on file reading.
   */
  @PostMapping(path = "loadProducts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Load ProductsDTO File")
  public void loadProducts(
      @ApiParam("Product file in JSON Format") @RequestParam("file") MultipartFile productsFile)
      throws IOException, FileUploadException {
    productService.loadProductsFromFile(productsFile);
  }

}
