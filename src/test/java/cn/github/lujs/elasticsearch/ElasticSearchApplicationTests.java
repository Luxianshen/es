package cn.github.lujs.elasticsearch;

import cn.github.lujs.elasticsearch.model.Content;
import com.alibaba.fastjson.JSON;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ElasticSearchApplicationTests {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
    }

    @Test
    void addHotsList() throws IOException {
        List<String> hots = Lists.newArrayList("java","k8s","docker","mysql","python");
        List<Content> hotList = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            Content c = new Content();
            c.setName(hots.get(i));
            hotList.add(c);
        }
        // 内容放入 es 中
        BulkRequest bulkRequest = new BulkRequest();
        // 可更具实际业务是指
        bulkRequest.timeout("200m");

        System.out.println(JSON.toJSONString(hotList));

        for (int h = 0; h < hotList.size(); h++) {
            bulkRequest.add(
                    new IndexRequest("hots")
                            .id(h+"")
                            .source(JSON.toJSONString(hotList.get(h)), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        restHighLevelClient.close();

    }


    @Test
    public void addOneData() throws IOException {

        String json = "{\"area\":\"广东\",\"city\":\"广州\",\"employeeId\":\"1\",\"employeeName\":\"张三\",\"shopId\":\"1\",\"shopName\":\"第一门店\",\"dept\":\"市场一部\",\"orderMoney\":\"1001.00\",\"orderNum\":\"1\",\"createTime\":\"2021-06-01\"}";

        // 内容放入 es 中
        BulkRequest bulkRequest = new BulkRequest();
        // 可更具实际业务是指
        bulkRequest.timeout("200m");
        bulkRequest.add(
                new IndexRequest("sale_data")
                        .id("" + 1)
                        .source(json, XContentType.JSON)
        );

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        restHighLevelClient.close();

    }

    @Test
    public void testSearch() throws IOException {
        SearchRequest saleData = new SearchRequest("sale_data");
        // 创建搜索源建造者对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 条件采用：精确查询 通过keyword查字段name
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("area", "广东");
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));// 60s
        // 分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        // 高亮
        // ....
        // 搜索源放入搜索请求中
        saleData.source(searchSourceBuilder);
        // 执行查询，返回结果
        SearchResponse searchResponse = restHighLevelClient.search(saleData, RequestOptions.DEFAULT);
        restHighLevelClient.close();
        // 解析结果
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            results.add(sourceAsMap);
        }
        // 输出
        System.out.println(results);
    }


}
