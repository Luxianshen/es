package cn.github.lujs.elasticsearch.CarEsTest;

import cn.github.lujs.elasticsearch.api.model.CarData;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Lujs
 * @desc TODO
 * @date 2021/6/9 2:18 下午
 */
@SpringBootTest
public class CarEsTest {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void addCarData() throws InterruptedException {


        //地区json数据读取
        JSONArray array = JSONUtil.readJSONArray(new File("/Users/lulu/IdeaProjects/myproject/es/doc/CarData.txt"), Charset.defaultCharset());
        List<CarData> dataList = array.stream().map(x->{
            JSONObject jsonObject = JSONUtil.parseObj(x);
            CarData carData = new CarData();
            carData.setRepairId(Integer.parseInt(jsonObject.get("repair_id").toString()));
            carData.setPlateNumber(jsonObject.get("car_no").toString());
            carData.setVin(jsonObject.get("car_vin").toString());
            carData.setEngine(jsonObject.get("car_en").toString());
            carData.setBusType(Integer.parseInt(jsonObject.get("bus_type").toString().isEmpty()?"0":jsonObject.get("bus_type").toString()));
            carData.setBrand(jsonObject.get("brand_id").toString().isEmpty()?"0":jsonObject.get("brand_id").toString());
            carData.setSeatCount(Integer.parseInt(jsonObject.get("seat_count").toString().isEmpty()?"5":jsonObject.get("seat_count").toString()));
            carData.setExhaustScale(jsonObject.get("exhaust_scale").toString());
            carData.setOwner(jsonObject.get("cn_name")== null?"":jsonObject.get("cn_name").toString());
            carData.setPhone(jsonObject.get("user_id")==null?"":jsonObject.get("user_id").toString());
            carData.setCarName(jsonObject.get("car_detail").toString());

            carData.setInsuredCompanyName(jsonObject.get("insured_company_name").toString());
            if(jsonObject.get("tci_time")!=null && !jsonObject.get("tci_time").toString().isEmpty()) {
                carData.setTciTime(DateUtil.format(DateUtil.parse(jsonObject.get("tci_time").toString(),"dd/MM/yyyy"),"yyyy-MM-dd"));
            }
            if(jsonObject.get("vic_time")!=null && !jsonObject.get("vic_time").toString().isEmpty()) {
                carData.setVicTime(DateUtil.format(DateUtil.parse(jsonObject.get("vic_time").toString(),"dd/MM/yyyy"),"yyyy-MM-dd"));
            }

            if(jsonObject.get("car_reg_date")!=null && !jsonObject.get("car_reg_date").toString().isEmpty()){
                carData.setCarRegTime(DateUtil.format(DateUtil.parse(jsonObject.get("car_reg_date").toString(),"dd/MM/yyyy"),"yyyy-MM-dd"));
            }

            int randNum =  RandomUtil.randomInt(0,2);
            carData.setPecNum(randNum);
            int randDay  = RandomUtil.randomInt(0,200);
            carData.setPecQueryTime(DateUtil.format(DateUtil.offsetDay(new Date(), -randDay),"yyyy-MM-dd"));

            carData.setInsertTime(DateUtil.format(DateUtil.parse(jsonObject.get("insert_time").toString(),"dd/MM/yyyy HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
            carData.setUpdateTime(DateUtil.format(DateUtil.parse(jsonObject.get("update_time").toString(),"dd/MM/yyyy HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));

            return carData;
        }).collect(Collectors.toList());

        //放进es
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(dataList.size());
        for (int i = 0; i < dataList.size(); i++) {
            final int j = i;

            executorService.execute(() -> {

                // 内容放入 es 中
                BulkRequest bulkRequest = new BulkRequest();

                System.out.println(JSON.toJSONString(dataList.get(j)));

                // 可更具实际业务是指
                bulkRequest.timeout("200m");
                bulkRequest.add(
                        new IndexRequest("car_data")
                                .id("" + j).source(JSONUtil.toJsonStr(dataList.get(j)), XContentType.JSON)
                );

                try {
                    BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    System.out.println(bulk.hasFailures());
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

        System.out.println(dataList.size());
        System.out.println("end");
    }

}
