package cn.github.lujs.elasticsearch.service;

import cn.github.lujs.elasticsearch.api.service.statics.AreaStaticsService;
import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @desc 区域统计服务类
 * @author Lujs
 * @date 2021/6/6 8:19 下午
 */
public class AreaStaticsServiceImpl implements AreaStaticsService {

    @Override
    public String dayCount(String deptJson) {

        return null;
    }

    @Override
    public String weekCount(String deptJson) {
        return null;
    }

    @Override
    public String monthCount(String deptJson) {
        return null;
    }

    @Override
    public String yearCount(String deptJson) {
        return null;
    }

    @Override
    public String topTen(String deptJson) {
        return null;
    }

    @Override
    public String getAreasCount(String areaCode, int dateType) {

        //获取所以要查询的code
        List<String> areaCodes = getNeedAreaCode(areaCode);
        int day;
        //转换日期
        switch (dateType) {
            case 1:
                day = DateUtil.dayOfWeek(new Date());
                break;
            case 2:
                day = DateUtil.dayOfMonth(new Date());
                break;
            case 3:
                day = DateUtil.dayOfYear(new Date());
                break;
            default:
        }

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建CompletionService
        CompletionService<String> cs = new ExecutorCompletionService<>(executor);
        // 用于保存Future对象
        List<Future<String>> futures = new ArrayList<>(areaCodes.size());
        //提交异步任务，并保存future到futures

        areaCodes.forEach(x -> futures.add(
                cs.submit(() -> dayCount(x))));

        // 获取最快返回的任务执行结果
        String r;
        try {
            // 只要有一个成功返回，则break
            for (int i = 0; i < 3; ++i) {
                r = cs.take().get();
                //简单地通过判空来检查是否成功返回
                if (r != null) {
                    break;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            //取消所有任务
            for (Future<String> f : futures){
                f.cancel(true);
            }
        }
        // 返回结果
        return null;
    }

    /**
     * 获取所以地区code
     *
     * @param areaCode 查询的code
     * @return 所有以下的区域code
     */
    private List<String> getNeedAreaCode(String areaCode) {

        return new ArrayList<>();
    }


}
