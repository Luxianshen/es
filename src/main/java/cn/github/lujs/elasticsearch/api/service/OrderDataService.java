package cn.github.lujs.elasticsearch.api.service;

/**
 * @desc 订单数据服务
 * @author Lujs
 * @date 2021/6/3 10:09 下午
 */
public interface OrderDataService {

    /**
     * json格式
     * {
     *     "area":"广东",
     *     "city":"广州",
     *     "employee":"张三",
     *     "dept":"市场一部",
     *     "orderMoney":"1001.00",
     *     "orderNum":"1",
     *     "createTime":"2021-06-01 00:00:00"
     * }
     */

    /**
     * 数据接收地址
     * jsonData 订单数据
     * {"area":"广东","city":"广州","employee":"张三","dept":"市场一部","orderMoney":"1001.00","orderNum":"1","createTime":"2021-06-01"}
     */
     boolean addData(String jsonData);

    /**
     * 删除数据
     * jsonData 删除订单
     * {"area":"广东","city":"广州","employee":"张三","dept":"市场一部","orderMoney":"1001.00","orderNum":"1","createTime":"2021-06-01"}
     */
    boolean delData();

}
