package cn.github.lujs.elasticsearch.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Lujs
 * @desc 推荐关键字服务
 * @date 2021/6/3 2:18 下午
 */
public class RecommendService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 2、根据keyword分页查询结果
    public List<Map<String, Object>> search(String keyword, Integer pageIndex, Integer pageSize) throws IOException {
        if (pageIndex < 0) {
            pageIndex = 0;
        }
        SearchRequest jd_goods = new SearchRequest("jd_goods_");
        // 创建搜索源建造者对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 条件采用：精确查询 通过keyword查字段name
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        // 60s
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 分页
        searchSourceBuilder.from(pageIndex);
        searchSourceBuilder.size(pageSize);
        // 高亮
        // ....
        // 搜索源放入搜索请求中
        jd_goods.source(searchSourceBuilder);
        // 执行查询，返回结果
        SearchResponse searchResponse = restHighLevelClient.search(jd_goods, RequestOptions.DEFAULT);
        // 解析结果
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            results.add(sourceAsMap);
        }
        // 返回查询的结果
        return results;
    }
}
