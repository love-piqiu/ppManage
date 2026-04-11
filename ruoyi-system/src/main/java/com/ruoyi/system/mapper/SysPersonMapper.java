package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPerson;

/**
 * 人员信息 数据层
 *
 * @author ppmanage
 */
public interface SysPersonMapper
{
    /**
     * 查询人员列表
     *
     * @param person 人员信息
     * @return 人员列表
     */
    public List<SysPerson> selectPersonList(SysPerson person);

    /**
     * 查询所有人员
     *
     * @return 人员列表
     */
    public List<SysPerson> selectPersonAll();

    /**
     * 通过人员ID查询人员信息
     *
     * @param id 人员ID
     * @return 人员信息
     */
    public SysPerson selectPersonById(Long id);

    /**
     * 根据姓名查询人员
     *
     * @param name 姓名
     * @return 人员信息
     */
    public SysPerson selectPersonByName(String name);

    /**
     * 新增人员
     *
     * @param person 人员信息
     * @return 结果
     */
    public int insertPerson(SysPerson person);

    /**
     * 修改人员
     *
     * @param person 人员信息
     * @return 结果
     */
    public int updatePerson(SysPerson person);

    /**
     * 删除人员
     *
     * @param id 人员ID
     * @return 结果
     */
    public int deletePersonById(Long id);

    /**
     * 批量删除人员
     *
     * @param ids 需要删除的人员ID
     * @return 结果
     */
    public int deletePersonByIds(Long[] ids);

    /**
     * 校验姓名是否唯一
     *
     * @param name 姓名
     * @return 结果
     */
    public SysPerson checkNameUnique(String name);

    /**
     * 查询在职人员数量
     *
     * @return 数量
     */
    public int countActivePerson();

    /**
     * 查询离职人员数量
     *
     * @return 数量
     */
    public int countInactivePerson();
}