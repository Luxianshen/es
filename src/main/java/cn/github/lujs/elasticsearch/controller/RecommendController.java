package cn.github.lujs.elasticsearch.controller;

import cn.github.lujs.elasticsearch.service.RecommendService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author Lujs
 * @desc TODO
 * @date 2021/6/3 2:16 下午
 */
//@RestController
@RequestMapping("recommend")
public class RecommendController {

    @Resource
    private RecommendService recommendService;

    @ResponseBody
    @GetMapping("/hots/{keyword}")
    public Object parse(@PathVariable("keyword") String keyword) throws IOException {
        return recommendService.search(keyword,0,10);
    }

}
