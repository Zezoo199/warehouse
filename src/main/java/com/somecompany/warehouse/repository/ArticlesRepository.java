package com.somecompany.warehouse.repository;

import com.somecompany.warehouse.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Default ArticleRepository
 */
public interface ArticlesRepository extends JpaRepository<Article, Long> {

}
