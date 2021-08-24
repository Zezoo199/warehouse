package com.somecompany.warehouse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somecompany.warehouse.model.Article;
import com.somecompany.warehouse.model.InventoryDTO;
import com.somecompany.warehouse.repository.ArticlesRepository;
import java.io.IOException;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Default Article Service Impl
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

  private final ObjectMapper objectMapper;

  private final ArticlesRepository articlesRepository;

  /**
   *
   * @param multipartFile
   * @throws FileUploadException
   */
  @Override
  public void loadArticlesFromFile(MultipartFile multipartFile)
      throws IOException, com.somecompany.warehouse.exception.FileUploadException {
    if (null == multipartFile) {
      log.error("No Articles file provided...");
      throw new com.somecompany.warehouse.exception.FileUploadException("Articles File is Null");
    }
    log.info("Recieved loadArticles file ");
    InventoryDTO inventoryDTO = objectMapper
        .readValue(multipartFile.getInputStream(), InventoryDTO.class);
    log.debug("Saving {} articles...", inventoryDTO.getInventory().size());
    articlesRepository.saveAll(inventoryDTO.getInventory());
  }

  /**
   * Get Article Stock by ID
   *
   * @param articleId article ID
   * @return Long stock 0 if article not found
   */
  public Long getArticleStockById(Long articleId) {
    return articlesRepository.findById(articleId).map(Article::getStock).orElse(0L);
  }

  /**
   * Subtract some stock from article stock
   *
   * @param articleId article ID
   * @param valueToSubtract value to be sub'd
   */
  public void subtractStockFromArticleStock(Long articleId, Long valueToSubtract) {
    log.debug("Updating article ID={} stock ", articleId);
    Article article = articlesRepository.findById(articleId)
        .orElseThrow(EntityNotFoundException::new);
    article.setStock(article.getStock() - valueToSubtract);
    articlesRepository.save(article);
  }
}
