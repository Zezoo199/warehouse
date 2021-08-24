package com.somecompany.warehouse.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * ArticleService
 */
public interface ArticleService {

  /**
   * Load articles from file into database.
   */
  void loadArticlesFromFile(MultipartFile multipartFile)
      throws IOException, com.somecompany.warehouse.exception.FileUploadException;

  /**
   * Get Article Stock by ID
   *
   * @param articleId article ID
   * @return Long stock 0 if article not found
   */
  Long getArticleStockById(Long articleId);

  /**
   * Subtract some stock from article stock
   *
   * @param articleId article ID
   * @param valueToSubtract value to be sub'd
   */
  void subtractStockFromArticleStock(Long articleId, Long valueToSubtract);

}
