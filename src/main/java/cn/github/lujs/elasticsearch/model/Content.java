package cn.github.lujs.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description:
 * Author lujs
 * Date 2021/2/9 15:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content implements Serializable {
    private static final long serialVersionUID = -8049497962627482693L;
    private String name;
    private String img;
    private String price;
}
