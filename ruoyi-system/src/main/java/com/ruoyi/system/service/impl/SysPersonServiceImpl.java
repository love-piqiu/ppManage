package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.mapper.SysPersonMapper;
import com.ruoyi.system.mapper.SysPersonProjectMapper;
import com.ruoyi.system.service.ISysPersonService;

/**
 * 人员信息 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysPersonServiceImpl implements ISysPersonService
{
    @Autowired
    private SysPersonMapper personMapper;

    @Autowired
    private SysPersonProjectMapper personProjectMapper;

    /**
     * 查询人员列表
     *
     * @param person 人员信息
     * @return 人员列表
     */
    @Override
    public List<SysPerson> selectPersonList(SysPerson person)
    {
        return personMapper.selectPersonList(person);
    }

    /**
     * 查询所有人员
     *
     * @return 人员列表
     */
    @Override
    public List<SysPerson> selectPersonAll()
    {
        return personMapper.selectPersonAll();
    }

    /**
     * 通过人员ID查询人员信息
     *
     * @param id 人员ID
     * @return 人员信息
     */
    @Override
    public SysPerson selectPersonById(Long id)
    {
        return personMapper.selectPersonById(id);
    }

    /**
     * 校验姓名是否唯一
     *
     * @param person 人员信息
     * @return 结果
     */
    @Override
    public boolean checkNameUnique(SysPerson person)
    {
        Long personId = StringUtils.isNull(person.getId()) ? -1L : person.getId();
        SysPerson info = personMapper.checkNameUnique(person.getName());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != personId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增人员
     *
     * @param person 人员信息
     * @return 结果
     */
    @Override
    public int insertPerson(SysPerson person)
    {
        return personMapper.insertPerson(person);
    }

    /**
     * 修改人员
     *
     * @param person 人员信息
     * @return 结果
     */
    @Override
    public int updatePerson(SysPerson person)
    {
        return personMapper.updatePerson(person);
    }

    /**
     * 删除人员
     *
     * @param id 人员ID
     * @return 结果
     */
    @Override
    public int deletePersonById(Long id)
    {
        return personMapper.deletePersonById(id);
    }

    /**
     * 批量删除人员
     *
     * @param ids 需要删除的人员ID
     * @return 结果
     */
    @Override
    public int deletePersonByIds(Long[] ids)
    {
        return personMapper.deletePersonByIds(ids);
    }

    /**
     * 查询在职人员数量
     *
     * @return 数量
     */
    @Override
    public int countActivePerson()
    {
        return personMapper.countActivePerson();
    }

    /**
     * 查询离职人员数量
     *
     * @return 数量
     */
    @Override
    public int countInactivePerson()
    {
        return personMapper.countInactivePerson();
    }
}