package cn.github.lujs.elasticsearch.dataMaker;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Lujs
 * @desc TODO
 * @date 2021/6/7 3:31 下午
 */
@SpringBootTest
public class OrderDataMaker {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void makerOrder() throws IOException, InterruptedException {

        /**
         * 模拟2019年开始
         * 每天200条数据
         * 地区 广东-广州
         * 部门 20个
         * 人员 200个
         * 门店 20000家
         */
        //地区json数据读取
        JSONArray jsonObject = JSONUtil.readJSONArray(new File("/Users/lulu/IdeaProjects/myproject/es/doc/Area.txt"), Charset.defaultCharset());

        //产生20个最低级部门
        List<String> deptList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            deptList.add("销售" + i + "部");
        }

        //产生200个员工 分配到部门
        Map<String, List<String>> employeeMap = new HashMap<>(200);
        for (int i = 0; i < 200; i++) {
            int rand = RandomUtil.randomInt(0, 19);
            String key = deptList.get(rand);
            String value = "销售" + i + "号";
            if (employeeMap.containsKey(key)) {
                List<String> list = employeeMap.get(key);
                list.add(value);
                employeeMap.put(key, list);
            } else {
                List<String> list = new ArrayList<>();
                list.add(value);
                employeeMap.put(key, list);
            }
        }

        List<Map<String, Object>> shopList = new ArrayList<>();
        //产生20000个店
        for (int i = 0; i < 20000; i++) {

            int randDept = RandomUtil.randomInt(0,19);

            List<String> employeeNames = employeeMap.get(deptList.get(randDept));

            int rand = RandomUtil.randomInt(0, employeeNames.size());
            int areaRand = RandomUtil.randomInt(0, jsonObject.size());
            Map<String, Object> o = (Map<String, Object>) jsonObject.get(areaRand);
            String areaName = o.get("provinceName").toString();
            List<Object> cityList = (List<Object>) o.get("mallCityList");
            int cityRand = RandomUtil.randomInt(0, cityList.size());
            String cityName = ((Map<String, Object>) cityList.get(cityRand)).get("cityName").toString();
            Map<String, Object> map = new HashMap<>();
            map.put("area", areaName);
            map.put("city", cityName);
            map.put("dept", deptList.get(randDept));
            map.put("employeeName", employeeNames.get(rand));
            map.put("shopName", "第" + shopList.size() + 1 + "号门店");
            shopList.add(map);

        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        Date nowDate = new Date();
        //分配订单 每天200单
        for (int i = 0; i < 1096; i++) {
            for (int j = 0; j < 200; j++) {
                int rand = RandomUtil.randomInt(0, 1919);
                Map<String, Object> shopDataMap = shopList.get(rand);
                DecimalFormat df = new DecimalFormat("#.##");
                shopDataMap.put("orderMoney", df.format(1.01)); //RandomUtil.randomDouble(1.00, 1.00)
                shopDataMap.put("createTime", DateUtil.format(DateUtil.offsetDay(nowDate, -i), "yyyy-MM-dd hh:mm:ss"));
                dataList.add(shopDataMap);
            }
        }


        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(dataList.size());

        for (int i = 0; i < dataList.size(); i++) {
            final int j = i;

            executorService.execute(() -> {

                // 内容放入 es 中
                BulkRequest bulkRequest = new BulkRequest();

                // 可更具实际业务是指
                bulkRequest.timeout("200m");
                bulkRequest.add(
                        new IndexRequest("sale_data")
                                .id("" + j).source(JSON.toJSONString(dataList.get(j)), XContentType.JSON)
                );

                try {
                    BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }

            });

        }
        while (countDownLatch.getCount() > 0) {
            Thread.sleep(1000);
        }
        //放进es 20条
        System.out.println("end");
    }


    @Test
    public void deleteTestData() throws IOException {

        for (int i = 0; i < 2000; i++) {
            DeleteIndexRequest request = new DeleteIndexRequest("sale_data");
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            System.out.println(response.isAcknowledged());// 是否删除成功
        }
        restHighLevelClient.close();
    }

    @Test
    public void queryThread() throws IOException {
        //ExecutorService executorService = Executors.newFixedThreadPool(10);
        //CountDownLatch countDownLatch = new CountDownLatch(dataList.size());

        System.out.println("开始时间："+System.currentTimeMillis());
        SearchRequest saleData = new SearchRequest("sale_data");
        // 创建搜索源建造者对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 条件采用：精确查询 通过keyword查字段name
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("area.keyword", "广东省");
        //TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("city.keyword", "荆门市");
        //TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("city", "荆门市");
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("createTime.keyword");
        rangeQueryBuilder.format("yyyy-MM-dd hh:mm:ss");
        rangeQueryBuilder.gte("2018-06-11 00:00:00");
        rangeQueryBuilder.lte("2018-06-12 00:00:00");

        QueryBuilder qb2 = QueryBuilders.boolQuery()
                .should(termQueryBuilder)
                .must(rangeQueryBuilder);

        searchSourceBuilder.query(qb2);

        //searchSourceBuilder.query(termQueryBuilder);
        //searchSourceBuilder.query(termQueryBuilder1);

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));// 60s
        // 分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10000);
        // 高亮
        // ....
        // 搜索源放入搜索请求中
        saleData.source(searchSourceBuilder);

        System.out.println("查询开始时间："+System.currentTimeMillis());
        // 执行查询，返回结果
        SearchResponse searchResponse = restHighLevelClient.search(saleData, RequestOptions.DEFAULT);
        System.out.println("查询结束时间："+System.currentTimeMillis());
        // 解析结果
        SearchHits hits = searchResponse.getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        double total = 0.00;
        for (SearchHit documentFields : hits.getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            total += Double.valueOf(sourceAsMap.get("orderMoney").toString());
            results.add(sourceAsMap);
        }
        // 输出
        System.out.println(total);
        System.out.println("结束时间："+System.currentTimeMillis());

    }



}
