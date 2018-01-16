package mongo;

import com.mongo.ArticleRepository;
import com.mongo.model.Article;
import com.mongo.util.MongoAutoidUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by Sunc on 2018/1/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:application-mongodb.xml"})
public class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoAutoidUtil mongoAutoidUtil;

    /*新增记录*/
    @Test
    public void add() {
        //新增加一条记录
        Article article = new Article();
        article.setId("1");
        article.setTitle("MongoTemplate的基本使用");
        article.setAuthor("K");
        article.setUrl("http://jianshu.com");
        article.setTags(Arrays.asList("java", "mongodb", "spring"));
        article.setVisitCount(0L);
        article.setAddTime(new Date());
        articleRepository.save(article);

        //添加一批记录
        List<Article> articles = new ArrayList<Article>();
        for (int i = 0; i < 10; i++) {
            Article article1 = new Article();
            article1.setId(String.valueOf(i + 1));
            article1.setTitle("MongoTemplate的基本使用");
            article1.setAuthor("K");
            article1.setUrl("http://jianshu.com" + i);
            article1.setTags(Arrays.asList("java", "mongodb", "spring"));
            article1.setVisitCount(0L);
            article1.setAddTime(new Date());
            articles.add(article1);
        }
        articleRepository.save(articles);
    }

    /*更新数据*/
    @Test
    public void update() {
        Article article = articleRepository.findOne("1");
        article.setVisitCount(article.getVisitCount() + 1);
        articleRepository.save(article);
    }

    /*批量修改*/
    @Test
    public void batchUpdate() {
        List<Article> list = articleRepository.findByAuthor("K");
        list.forEach(article -> {
            article.setAuthor("K2");
        });
        articleRepository.save(list);
    }

    /*删除记录，删除记录为10的*/
    @Test
    public void delete() {
        Article article = articleRepository.findOne("10");
        articleRepository.delete(article);
    }

    @Test
    public void batchDelete() {
        List<Article> articles = articleRepository.findByAuthor("K2");
        articleRepository.delete(articles);
    }

    /*查询所有*/
    @Test
    public void findAll() {
        Iterable<Article> articles = articleRepository.findAll();
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*根据author查询*/
    @Test
    public void findByAuthor() {
        List<Article> articles = articleRepository.findByAuthor("K");
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*按照author和title查询*/
    @Test
    public void findByAuthorAndTitle() {
        List<Article> articles = articleRepository.findByAuthorAndTitle("K", "MongoTemplate的基本使用");
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*根据作者忽略大小写*/
    @Test
    public void findByAuthorIgnoreCase() {
        List<Article> articles = articleRepository.findByAuthorIgnoreCase("k");
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*忽略所有参数的大小写*/
    @Test
    public void findByAuthorAndTitleAllIgnoreCase() {
        List<Article> articles = articleRepository.findByAuthorAndTitleAllIgnoreCase("k", "mongoTemplate的基本使用");
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*根据author查询，并且以访问次数降序排序显示*/
    @Test
    public void findByAuthorOrderByVisitCountDesc() {
        List<Article> articles = articleRepository.findByAuthorOrderByVisitCountDesc("K");
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*根据author查询，并且以访问次数升序排序显示*/
    @Test
    public void findByAuthorOrderByVisitCountAsc() {
        List<Article> articles = articleRepository.findByAuthorOrderByVisitCountAsc("K");
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*自带排序条件*/
    @Test
    public void findByAuthorBySort() {
        /*sort参数为：排序方式，排序字段*/
        List<Article> articles = articleRepository.findByAuthor("K", new Sort(Sort.Direction.DESC, "VisitCount"));
        articles.forEach(article -> {
            System.out.println(article.toString());
        });
    }

    /*分页查询所有，并且排序*/
    @Test
    public void findByPage() {
        /*参数分别是当前页数，每页大小，排序条件*/
        Pageable pageable = new PageRequest(1, 2, new Sort(Sort.Direction.ASC, "VisitCount"));
        Page<Article> pageInfo = articleRepository.findAll(pageable);
        System.out.println(pageInfo);
        System.out.println("总页数" + pageInfo.getTotalPages());
        System.out.println("总数量" + pageInfo.getTotalElements());
        for (Article article : pageInfo.getContent()) {
            System.out.println(article.toString());
        }

    }
    /*测试自动增长*/
    @Test
    public void addAutoid() {
        //新增加一条记录
        Article article = new Article();
        article.setId(String.valueOf(mongoAutoidUtil.getNextSequence("seq_article")));
        article.setTitle("MongoTemplate的基本使用");
        article.setAuthor("K");
        article.setUrl("http://jianshu.com");
        article.setTags(Arrays.asList("java", "mongodb", "spring"));
        article.setVisitCount(0L);
        article.setAddTime(new Date());
        articleRepository.save(article);
    }

    //聚合统计
    @Test
    public void testAggregation(){
        //表示先按照author分组，然后求visit_count的和，起别名为count，然后取每个组的第一个author值作为name字段的值
        //之后对上述结果进行显示，显示字段为count和name
        //根据count值进行降序排序
        //再过滤结果count大于0的值并返回
        //这里面采用了newAggregation这个对象，定义了按照group方法，project映射名称,sort排序字段，match结果匹配字段
        Aggregation aggregation = newAggregation(
                group("author").sum("visit_count").as("count").first("author").as("name"),
                project("count","name"),
                sort(Sort.Direction.DESC,"count"),
                match(Criteria.where("count").gt(0))
        );
        //使用模板执行获取结果集
        AggregationResults<Article> aggreationResults = mongoTemplate.aggregate(aggregation, "article_info", Article.class);
        List<Article> articles = aggreationResults.getMappedResults();
        for(Article article:articles){
            System.out.println(article.getCount());
        }
    }
}
