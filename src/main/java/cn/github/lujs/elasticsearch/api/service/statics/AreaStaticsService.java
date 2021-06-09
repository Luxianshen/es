package cn.github.lujs.elasticsearch.api.service.statics;

/**
 * @desc 区域查询数据
 * @author Lujs
 * @date 2021/6/6 8:17 下午
 */
public interface AreaStaticsService extends StatisticsService{

    /**
     * 区域及以下的业绩
     * @param areaCode 查询的地区code
     * @param dateType 0 日 1 周 2 月 3 年
     * @return 业绩数据
     */
    String getAreasCount(String areaCode,int dateType);

}
