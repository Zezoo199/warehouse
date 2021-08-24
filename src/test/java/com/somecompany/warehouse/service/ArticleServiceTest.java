package com.somecompany.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somecompany.warehouse.exception.FileUploadException;
import com.somecompany.warehouse.model.Article;
import com.somecompany.warehouse.repository.ArticlesRepository;
import java.io.IOException;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ArticleServiceTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  private ArticlesRepository articlesRepository = mock(ArticlesRepository.class);
  private ArticleService subject = new ArticleServiceImpl(objectMapper, articlesRepository);

  @Test
  void whenLoadArticles_thenCallRepositoryWithSave() throws IOException, FileUploadException {
    //given
    MultipartFile multipartFile = new MockMultipartFile("file",
        getFileAsString("inventory.json").getBytes());
    //when
    subject.loadArticlesFromFile(multipartFile);

    //then
    verify(articlesRepository, times(1)).saveAll(any());
  }

  @Test
  void shouldNotLoadProductsFromNullFile() {
    Assertions.assertThrows(FileUploadException.class, () -> {
      //given
      MultipartFile multipartFile = null;
      //when
      subject.loadArticlesFromFile(multipartFile);

      //then
      verify(articlesRepository, times(0)).save(any());
    });
  }

  @Test
  void shouldNotLoadProductsFromBadFile() {
    //given
    Assertions.assertThrows(IOException.class, () -> {
      MultipartFile multipartFile = new MockMultipartFile("file",
          "BlaFileBla".getBytes());
      //when
      subject.loadArticlesFromFile(multipartFile);

      //then
      verify(articlesRepository, times(0)).save(any());
    });
  }

  @Test
  void getArticleStockById() {
    //given non existing article
    //when get stock then 0

    assertEquals(0, subject.getArticleStockById(1L));
  }

  @Test
  void subtractStockFromArticleStockNOTFOUND() {
    //when not found article THEN exception
    Assertions.assertThrows(EntityNotFoundException.class, () -> {
      subject.subtractStockFromArticleStock(1L, 2L);
    });

    //then
  }

  @Test
  void subtractStockFromArticleStock() {
    //given
    when(articlesRepository.findById(1L)).thenReturn(Optional.of(givenArticle()));

    //when
    subject.subtractStockFromArticleStock(1L, 2L);

    //then
    Article updated = givenArticle();
    updated.setStock(3L);
    verify(articlesRepository).save(updated);
  }

  private Article givenArticle() {
    Article article = new Article();
    article.setName("A1");
    article.setStock(5L);
    return article;
  }

  private String getFileAsString(String fileName) throws IOException {
    return IOUtils
        .toString(this.getClass().getClassLoader().getResource(fileName).openStream(),
            "UTF-8"
        );
  }
}
