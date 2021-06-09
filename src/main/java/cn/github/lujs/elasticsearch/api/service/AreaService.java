package cn.github.lujs.elasticsearch.api.service;

/**
 * @desc 地区服务
 * @author Lujs
 * @date 2021/6/3 10:03 下午
 */
public interface AreaService {

    /**
     * 以中国地区为标准
     * @return 省信息
     */
    String getProvince(String areaCode);

    /**
     * 以中国地区为标准
     * @return 城市信息
     */
    String getCity(String areaCode);

    /**
     * 以中国地区为标准
     * @return 县城/区信息
     */
    String getTwon(String areaCode);

    /**
     * todo 自定义区域
     */

}
