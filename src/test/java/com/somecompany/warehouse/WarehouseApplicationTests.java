package com.somecompany.warehouse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.somecompany.warehouse.exception.ProductNotFoundException;
import com.somecompany.warehouse.exception.ProductOutOfStockException;
import com.somecompany.warehouse.repository.ArticlesRepository;
import com.somecompany.warehouse.repository.ProductRepository;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class WarehouseApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ArticlesRepository articlesRepository;

  @Autowired
  private ProductRepository productRepository;


  @Test
  void whenValidArticlesFile_thenLoadArticleReturn200AndArticlesExistsOnDatabase()
      throws Exception {
    //given
    String givenInventoryFile = getFileAsString("inventory.json");
    //when
    this.mockMvc.perform(multipart("/loadArticles").file("file", givenInventoryFile.getBytes())).
        andDo(print()).
        andExpect(status().is2xxSuccessful());
    //then assert articles exist
    assertEquals(4, articlesRepository.findAll().size());

  }

  @Test
  void whenINValidArticlesFile_thenLoadArticleReturn400AndNothingIsSaved()
      throws Exception {
    //given products file instead of articles
    String givenInventoryFile = getFileAsString("products.json");
    //when
    this.mockMvc.perform(multipart("/loadArticles").file("file", givenInventoryFile.getBytes())).
        andDo(print()).
        andExpect(status().is(400))
        .andExpect(jsonPath("$", startsWith("Bad file format, Check your file again.")));
    //then assert articles exist
    assertEquals(0, articlesRepository.findAll().size());

  }

  @Test
  void whenValidProductsFile_thenLoadProductsReturn200AndNothingIsSaved()
      throws Exception {
    //given inventory file instead of products
    String productsFileContent = getFileAsString("inventory.json");
    this.mockMvc.perform(multipart("/loadProducts").file("file", productsFileContent.getBytes())).
        andDo(print()).
        andExpect(status().is(400))
        .andExpect(jsonPath("$", startsWith("Bad file format, Check your file again.")));
    //then
    assertEquals(0, productRepository.findAll().size());
  }

  @Test
  void whenINValidProductsFile_thenLoadProductsReturn400AndProductsExistsOnDatabase()
      throws Exception {
    //given
    String productsFileContent = getFileAsString("products.json");
    this.mockMvc.perform(multipart("/loadProducts").file("file", productsFileContent.getBytes())).
        andDo(print()).
        andExpect(status().is2xxSuccessful());
    //then
    assertEquals(2, productRepository.findAll().size());
  }


  @Test
  void givenDBWithProducts_whenGetProducts_thenProductsReturnedProperly() throws Exception {

    //given
    givenUploadedProductFile();
    //when & then
    this.mockMvc.perform(get("/getProducts")).
        andDo(print()).
        andExpect(status().is2xxSuccessful()).
        andExpect(jsonPath("$").isNotEmpty()).
        andExpect(jsonPath("$", hasSize(2))).
        andExpect(jsonPath("$[0].name", is("Dining Chair"))).
        andExpect(jsonPath("$[0].quantity", is(0)));
  }

  private void givenUploadedProductFile() throws Exception {
    String productsFileContent = getFileAsString("products.json");
    this.mockMvc.perform(multipart("/loadProducts").file("file", productsFileContent.getBytes())).
        andDo(print()).
        andExpect(status().is2xxSuccessful());
  }

  @Test
  void givenDBWithProductsAndInventory_whenGetProducts_thenProductsReturnedWithQuantity()
      throws Exception {

    //given PRODUCTS & ARTICLES
    givenUploadedProductFile();
    givenUploadedInventoryFile();

    //when & then
    this.mockMvc.perform(get("/getProducts")).
        andDo(print()).
        andExpect(status().is2xxSuccessful()).
        andExpect(jsonPath("$").isNotEmpty()).
        andExpect(jsonPath("$", hasSize(2))).
        andExpect(jsonPath("$[0].name", is("Dining Chair"))).
        andExpect(jsonPath("$[0].quantity", is(2))).
        andExpect(jsonPath("$[1].name", is("Dinning Table"))).
        andExpect(jsonPath("$[1].quantity", is(1)));
  }

  private void givenUploadedInventoryFile() throws Exception {
    String inventoryFileContent = getFileAsString("inventory.json");
    this.mockMvc.perform(multipart("/loadArticles").file("file", inventoryFileContent.getBytes())).
        andExpect(status().is2xxSuccessful());
  }

  @Test
  void givenDBWithProductsAndInventory_whenSellProduct_thenInvetoryUpdatedCorrectly()
      throws Exception {

    //given PRODUCTS & ARTICLES
    givenUploadedProductFile();
    givenUploadedInventoryFile();
    //given stock before update
    assertEquals(12, articlesRepository.findById(1L).orElseThrow().getStock());
    assertEquals(17, articlesRepository.findById(2L).orElseThrow().getStock());
    assertEquals(2, articlesRepository.findById(3L).orElseThrow().getStock());

    //when
    this.mockMvc
        .perform(post("/sellProduct").param("quantity", "1").param("productName", "Dining Chair")).
        andDo(print()).
        andExpect(status().is2xxSuccessful());
    //then assert Stock is updated
    assertEquals(8, articlesRepository.findById(1L).orElseThrow().getStock());
    assertEquals(9, articlesRepository.findById(2L).orElseThrow().getStock());
    assertEquals(1, articlesRepository.findById(3L).orElseThrow().getStock());
  }

  @Test
  void givenDBWithProductsWITHOUTInventory_whenSellProduct_thenOutOfStockException()
      throws Exception {
    givenUploadedProductFile();
    //when & then
    Exception resolvedException = this.mockMvc
        .perform(post("/sellProduct").
            param("quantity", "1").
            param("productName", "Dining Chair")).
            andDo(print()).
            andExpect(status().is(417)).andReturn().getResolvedException();
    assertTrue(resolvedException instanceof ProductOutOfStockException);
    assertThat(resolvedException.getMessage())
        .isEqualTo("OutOfStock Not enough articles to sell product:Dining Chair");
  }

  @Test
  void whenProductNotFound_then404Error()
      throws Exception {
    //when & then
    Exception resolvedException = this.mockMvc
        .perform(post("/sellProduct").
            param("quantity", "1").
            param("productName", "Not Existing Chair")).
            andDo(print()).
            andExpect(status().is(404)).andReturn().getResolvedException();
    assertTrue(resolvedException instanceof ProductNotFoundException);
    assertThat(resolvedException.getMessage())
        .isEqualTo("No product found for name=Not Existing Chair");

  }

  @Test
  void givenEmptyDB_whenGetProducts_thenEmptyList() throws Exception {

    this.mockMvc.perform(get("/getProducts")).
        andDo(print()).
        andExpect(status().is2xxSuccessful()).
        andExpect(jsonPath("$").isEmpty());

  }

  @Test
  void whenUploadProductFileWithDuplicateName_thenException()
      throws Exception {
    //given
    givenUploadedProductFile();
    //when upload same file again
    String productsFileContent = getFileAsString("products.json");
    this.mockMvc.perform(multipart("/loadProducts").file("file", productsFileContent.getBytes())).
        andDo(print()).
        andExpect(status().isBadRequest()).
        andExpect(jsonPath("$", startsWith("ConstraintViolationException")));

  }

  private String getFileAsString(String fileName) throws IOException {
    return IOUtils
        .toString(this.getClass().getClassLoader().getResource(fileName),
            "UTF-8"
        );
  }
}
