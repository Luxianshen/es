package cn.github.lujs.elasticsearch.api.service.statics;

/**
 * @desc 统计方法
 * @author Lujs
 * @date 2021/6/6 8:14 下午
 */
public interface StatisticsService {

    /**
     * 日统计
     * @param param 参数
     */
    String dayCount(String param);

    /**
     * 周统计
     * @param param 参数
     */
    String weekCount(String param);

    /**
     * 月统计
     * @param param 参数
     */
    String monthCount(String param);

    /**
     * 年统计
     * @param param 参数
     */
    String yearCount(String param);

    /**
     * 前10统计
     * @param param 参数
     */
    String topTen(String param);

}
