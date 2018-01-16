package com.mongo;

import com.mongo.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring的data repository封装了一套增删改查的方法，就和JPA实现的一样，ArticleRepository继承PagingAndSortingRepository，
 * 就集成了常用的增删改查方法，比如save、findOne、exists、findAll、delete等等，可以采用默认实现方式来完成常用的增删改查操作。
 */
@Repository("ArticleRepository")
public interface ArticleRepository extends PagingAndSortingRepository<Article, String> {
    //分页查询
    public Page<Article> findAll(Pageable pageable);

    //根据author查询
    public List<Article> findByAuthor(String author);

    //根据作者和标题查询
    public List<Article> findByAuthorAndTitle(String author, String title);

    //忽略参数大小写
    public List<Article> findByAuthorIgnoreCase(String author);

    //忽略所有参数大小写
    public List<Article> findByAuthorAndTitleAllIgnoreCase(String author, String title);

    //排序
    public List<Article> findByAuthorOrderByVisitCountDesc(String author);

    public List<Article> findByAuthorOrderByVisitCountAsc(String author);

    //自带排序条件
    public List<Article> findByAuthor(String author, Sort sort);
}
