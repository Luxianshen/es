package cn.github.lujs.elasticsearch.api.service;

/**
 * @desc 部门服务
 * @author Lujs
 * @date 2021/6/3 10:08 下午
 */
public interface DeptService {

    /**
     * 获取部门
     * @return 返回部门信息
     */
    String getDept(String deptCode);

    /**
     * 根据员工ID获取部门
     * @return 返回部门信息
     */
    String getDeptByEmployee(String employeeId);


}
