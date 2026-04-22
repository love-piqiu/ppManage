package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.domain.SysPersonProject;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.domain.PersonProjectDetail;
import com.ruoyi.system.mapper.SysPersonMapper;
import com.ruoyi.system.mapper.SysPersonProjectMapper;
import com.ruoyi.system.mapper.SysProjectMapper;
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

    @Autowired
    private SysProjectMapper projectMapper;

    /**
     * 查询人员列表
     *
     * @param person 人员信息
     * @return 人员列表
     */
    @Override
    public List<SysPerson> selectPersonList(SysPerson person)
    {
        List<SysPerson> list = personMapper.selectPersonList(person);
        // 为每个人员查询在建项目
        for (SysPerson p : list)
        {
            List<SysPersonProject> personProjects = personProjectMapper.selectByPersonId(p.getId());
            List<String> projects = new ArrayList<>();
            for (SysPersonProject pp : personProjects)
            {
                // 查询项目状态
                var project = projectMapper.selectProjectById(pp.getProjectId());
                if (project != null && "进行中".equals(project.getStatus()))
                {
                    // 显示格式：客户-项目名
                    String displayName = (project.getCustomer() != null ? project.getCustomer() + "-" : "") + project.getName();
                    projects.add(displayName);
                }
            }
            p.setProjects(projects);
        }
        return list;
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
     * 查询所有在职人员（用于项目关联，不限制直属下级）
     *
     * @return 人员列表
     */
    @Override
    public List<SysPerson> selectPersonAllForProject()
    {
        return personMapper.selectPersonAllForProject();
    }

    /**
     * 通过人员ID查询人员信息（包含项目参与详情）
     *
     * @param id 人员ID
     * @return 人员信息
     */
    @Override
    public SysPerson selectPersonById(Long id)
    {
        SysPerson person = personMapper.selectPersonById(id);
        if (person != null)
        {
            // 查询人员参与的项目详情
            List<SysPersonProject> personProjects = personProjectMapper.selectByPersonId(id);
            List<String> projects = new ArrayList<>();
            List<PersonProjectDetail> projectDetails = new ArrayList<>();
            for (SysPersonProject pp : personProjects)
            {
                SysProject project = projectMapper.selectProjectById(pp.getProjectId());
                if (project != null)
                {
                    // 在建项目列表（仅进行中的项目）
                    if ("进行中".equals(project.getStatus()))
                    {
                        String displayName = (project.getCustomer() != null ? project.getCustomer() + "-" : "") + project.getName();
                        projects.add(displayName);
                    }
                    // 项目详情列表（所有参与的项目）
                    PersonProjectDetail detail = new PersonProjectDetail();
                    detail.setName(project.getName());
                    detail.setRole(pp.getRole());
                    detail.setProgress(project.getProgress());
                    detail.setStatus(project.getStatus());
                    detail.setJoinDate(pp.getJoinDate());
                    projectDetails.add(detail);
                }
            }
            person.setProjects(projects);
            person.setProjectDetails(projectDetails);
        }
        return person;
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