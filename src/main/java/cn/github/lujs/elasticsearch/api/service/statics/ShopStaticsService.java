package cn.github.lujs.elasticsearch.api.service.statics;

/**
 * @author Lujs
 * @desc 门店查询数据
 * @date 2021/6/6 8:17 下午
 */
public interface ShopStaticsService extends StatisticsService {

    /**
     * 门店业绩
     *
     * @param shopCode 门店code
     * @param dateType 0 日 1 周 2 月 3 年
     * @return 业绩数据
     */
    String getShopCount(String shopCode, int dateType);

}
