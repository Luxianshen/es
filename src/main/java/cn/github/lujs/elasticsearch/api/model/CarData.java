package cn.github.lujs.elasticsearch.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lujs
 * @desc TODO
 * @date 2021/6/9 9:27 上午
 */
@Accessors
@Data
public class CarData implements Serializable {

    /**
     * {
     * "repairId":"",
     * #车辆信息
     * "plateNumber":"",
     * "vin":"",
     * "engine":"",
     * "busType":"",
     * "brand":"",
     * "seatCount":"",
     * "exhaustScale":"",
     * "owner":"",
     * "phone":"",
     * #车险信息
     * "insuredCompanyName":"",
     * "tciTime":"",
     * "vciTime":"",
     * #年审
     * "carRegDate":"",
     * #违章信息
     * "pecNum":"",
     * "pecQueryTime":"",
     *
     * "insertTime":"",
     * "updateTime":""
     * }
     */
    private int repairId;

    private String plateNumber;

    private String vin;

    private String engine;

    private int busType;

    private String brand;

    private int seatCount;

    private String exhaustScale;

    private String owner;

    private String phone;

    private String carName;

    /**
     * 车险
     */
    private String insuredCompanyName;

    @JSONField(format = "yyyy-MM-dd")
    private String tciTime;

    @JSONField(format = "yyyy-MM-dd")
    private String vicTime;

    /**
     * 车辆注册日期 推算年审
     */
    @JSONField(format = "yyyy-MM-dd")
    private String carRegTime;

    /**
     * 违章
     */
    private int pecNum;

    @JSONField(format = "yyyy-MM-dd")
    private String pecQueryTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String insertTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;
}
