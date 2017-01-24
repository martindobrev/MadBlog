package com.maddob.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Article provider that keeps all article data in a hash map
 *
 * This will hold the articles data in memory which will guarantee quick data access.
 * Persistence and concurrence will be handled later (if necessary at all).
 *
 * Created by martindobrev on 1/18/17.
 */
public class InMemoryArticleProvider implements ArticleProvider {

    /** actual storage for all articles **/
    private final Map<String, Article> articleDatabase;

    public InMemoryArticleProvider() {
        this.articleDatabase = new ConcurrentHashMap<String, Article>();
    }

    @Override
    public List<Article> getLatestArticles(int number) {
        return this.articleDatabase.values().stream().limit(number).collect(Collectors.toList());
    }

    @Override
    public List<Article> getAllArticles() {
        return articleDatabase.values().stream().collect(Collectors.toList());
    }

    @Override
    public Article getArticleByTitle(String title) {
        if (title != null) {
            return this.articleDatabase.values().stream()
                    .filter(article -> title.equals(article.getTitle()) ).findFirst().get();
        }
        return null;
    }

    @Override
    public void addArticle(Article article) {
        articleDatabase.put(article.getId().toString(), article);
    }

    @Override
    public void editArticle(Article article) {
        articleDatabase.put(article.getId().toString(), article);
    }

    @Override
    public void deleteArticle(Article article) {
        articleDatabase.remove(article.getId().toString());
    }
}
