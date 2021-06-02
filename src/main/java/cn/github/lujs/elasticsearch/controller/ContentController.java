package cn.github.lujs.elasticsearch.controller;

import cn.github.lujs.elasticsearch.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author lujs
 * Date 2021/2/9 15:28
 */
@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;

    @ResponseBody
    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws IOException {

        return contentService.parseContent(keyword);
    }

    @ResponseBody
    @GetMapping("/search/{keyword}/{pageIndex}/{pageSize}")
    public List<Map<String, Object>> parse(@PathVariable("keyword") String keyword,
                                           @PathVariable("pageIndex") Integer pageIndex,
                                           @PathVariable("pageSize") Integer pageSize) throws IOException {
        return contentService.search(keyword,pageIndex,pageSize);
    }

    @ResponseBody
    @GetMapping("/h_search/{keyword}/{pageIndex}/{pageSize}")
    public List<Map<String, Object>> highlightParse(@PathVariable("keyword") String keyword,
                                                    @PathVariable("pageIndex") Integer pageIndex,
                                                    @PathVariable("pageSize") Integer pageSize) throws IOException {
        return contentService.highlightSearch(keyword,pageIndex,pageSize);
    }
}
