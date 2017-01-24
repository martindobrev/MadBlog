package com.maddob.data;

import java.util.List;

/**
 * Basic interface for all Article Providers
 *
 * Created by martindobrev on 1/17/17.
 */
public interface ArticleProvider {

    /**
     * Retrieves the number of desired latest articles
     *
     * @param number
     * @return
     */
    public List<Article> getLatestArticles(int number);

    /**
     * Retrieves all articles
     *
     * Maybe this method shall be removed later when
     * the database of articles is too big
     *
     * @return
     */
    public List<Article> getAllArticles();

    /**
     * Retrieve article by title
     *
     * will be used by the router to find the appropriate article
     *
     * @param title
     * @return
     */
    public Article       getArticleByTitle(String title);

    /**
     * Adds new article
     *
     * @param article
     */
    public void          addArticle(Article article);

    /**
     * Edits the article if it exists
     *
     * @param article
     */
    public void          editArticle(Article article);

    /**
     * Deletes all articles
     *
     * @param article
     */
    public void          deleteArticle(Article article);

}
