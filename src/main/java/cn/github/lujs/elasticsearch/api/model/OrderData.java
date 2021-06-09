package cn.github.lujs.elasticsearch.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @desc 订单数据
 * @author Lujs
 * @date 2021/6/7 4:27 下午
 */
@Data
public class OrderData implements Serializable {

    private String area;

    private String city;

    private Integer employeeId;

    private String employeeName;

    private Integer shopId;

    private String shopName;

    private Double orderMoney;

    private Date createTime;

}
