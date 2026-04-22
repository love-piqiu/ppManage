package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.domain.SysPersonSkill;
import com.ruoyi.system.domain.SysPersonProject;
import com.ruoyi.system.domain.SysSkillCategory;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.mapper.SysPersonMapper;
import com.ruoyi.system.mapper.SysPersonSkillMapper;
import com.ruoyi.system.mapper.SysPersonProjectMapper;
import com.ruoyi.system.mapper.SysSkillCategoryMapper;
import com.ruoyi.system.mapper.SysProjectMapper;
import com.ruoyi.system.service.ISysResourceService;

/**
 * 资源管理 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysResourceServiceImpl implements ISysResourceService
{
    @Autowired
    private SysPersonMapper personMapper;

    @Autowired
    private SysPersonSkillMapper personSkillMapper;

    @Autowired
    private SysPersonProjectMapper personProjectMapper;

    @Autowired
    private SysSkillCategoryMapper skillCategoryMapper;

    @Autowired
    private SysProjectMapper projectMapper;

    /**
     * 查询资源列表（含技能和项目数）
     *
     * @param params 筛选参数
     * @return 资源列表
     */
    @Override
    public List<Map<String, Object>> selectResourceList(Map<String, Object> params)
    {
        // 1. 查询在职人员
        SysPerson personQuery = new SysPerson();
        personQuery.setStatus("在职");
        List<SysPerson> persons = personMapper.selectPersonList(personQuery);

        // 2. 根据筛选条件过滤人员ID
        List<Long> filteredPersonIds = null;
        String category = (String) params.get("category");
        String skill = (String) params.get("skill");

        if (StringUtils.isNotEmpty(category) || StringUtils.isNotEmpty(skill))
        {
            filteredPersonIds = personSkillMapper.selectPersonIdsBySkill(category, skill);
            if (filteredPersonIds.isEmpty())
            {
                return new ArrayList<>();
            }
        }

        // 3. 构建资源列表
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysPerson person : persons)
        {
            // 如果有技能筛选，检查是否在筛选结果中
            if (filteredPersonIds != null && !filteredPersonIds.contains(person.getId()))
            {
                continue;
            }

            Map<String, Object> resource = new HashMap<>();
            resource.put("id", person.getId());
            resource.put("name", person.getName());
            resource.put("position", person.getPosition());
            resource.put("level", person.getLevel());
            resource.put("contact", person.getContact());
            resource.put("email", person.getEmail());
            resource.put("isDirect", person.getIsDirect());

            // 预计释放日期
            resource.put("expectedReleaseDate", person.getExpectedReleaseDate());

            // 根据预计释放日期自动计算资源状态
            String calculatedStatus = calculateResourceStatus(person.getExpectedReleaseDate());
            resource.put("resourceStatus", calculatedStatus);
            resource.put("resourceStatusRemark", person.getResourceStatusRemark());
            resource.put("nextProject", person.getNextProject());

            // 查询技能列表
            List<SysPersonSkill> skills = personSkillMapper.selectSkillByPersonId(person.getId());
            List<Map<String, String>> skillList = new ArrayList<>();
            for (SysPersonSkill s : skills)
            {
                Map<String, String> skillMap = new HashMap<>();
                skillMap.put("category", s.getCategory());
                skillMap.put("skill", s.getSkill());
                skillList.add(skillMap);
            }
            resource.put("skills", skillList);

            // 查询当前进行中项目数
            List<SysPersonProject> personProjects = personProjectMapper.selectByPersonId(person.getId());
            int projectCount = 0;
            List<String> projectNames = new ArrayList<>();
            for (SysPersonProject pp : personProjects)
            {
                SysProject project = projectMapper.selectProjectById(pp.getProjectId());
                if (project != null && "进行中".equals(project.getStatus()))
                {
                    projectCount++;
                    projectNames.add(project.getName());
                }
            }
            resource.put("projectCount", projectCount);
            resource.put("projects", projectNames);

            result.add(resource);
        }

        // 4. 根据资源状态筛选
        String statusFilter = (String) params.get("resourceStatus");
        if (StringUtils.isNotEmpty(statusFilter))
        {
            result = result.stream()
                    .filter(r -> statusFilter.equals(r.get("resourceStatus")))
                    .collect(Collectors.toList());
        }

        // 5. 根据项目名称关键词筛选
        String projectName = (String) params.get("projectName");
        if (StringUtils.isNotEmpty(projectName))
        {
            result = result.stream()
                    .filter(r -> {
                        List<String> projects = (List<String>) r.get("projects");
                        return projects.stream().anyMatch(p -> p.contains(projectName));
                    })
                    .collect(Collectors.toList());
        }

        return result;
    }

    /**
     * 查询技能分类列表
     *
     * @return 技能分类列表
     */
    @Override
    public List<SysSkillCategory> selectSkillCategoryAll()
    {
        List<SysSkillCategory> categories = skillCategoryMapper.selectSkillCategoryAll();
        // 解析 JSON 格式的技能列表
        for (SysSkillCategory category : categories)
        {
            if (StringUtils.isNotEmpty(category.getSkills()))
            {
                try
                {
                    // 只有以 '[' 开头才尝试解析为 JSON 数组
                    String skillsStr = category.getSkills().trim();
                    if (skillsStr.startsWith("["))
                    {
                        List<String> skillList = JSON.parseArray(skillsStr, String.class);
                        category.setSkillList(skillList);
                    }
                    else
                    {
                        // 非 JSON 数组格式，按逗号分隔处理
                        List<String> skillList = new ArrayList<>();
                        for (String s : skillsStr.split(","))
                        {
                            if (StringUtils.isNotEmpty(s.trim()))
                            {
                                skillList.add(s.trim());
                            }
                        }
                        category.setSkillList(skillList);
                    }
                }
                catch (Exception e)
                {
                    // 解析失败时忽略
                    category.setSkillList(new ArrayList<>());
                }
            }
        }
        return categories;
    }

    /**
     * 根据人员ID查询技能列表
     *
     * @param personId 人员ID
     * @return 技能列表
     */
    @Override
    public List<SysPersonSkill> selectSkillByPersonId(Long personId)
    {
        return personSkillMapper.selectSkillByPersonId(personId);
    }

    /**
     * 添加人员技能
     *
     * @param personSkill 人员技能
     * @return 结果
     */
    @Override
    public int insertPersonSkill(SysPersonSkill personSkill)
    {
        // 检查是否已存在相同技能
        int exists = personSkillMapper.checkSkillExists(personSkill.getPersonId(), personSkill.getCategory(), personSkill.getSkill());
        if (exists > 0)
        {
            return 0; // 已存在，不重复添加
        }
        return personSkillMapper.insertPersonSkill(personSkill);
    }

    /**
     * 删除人员技能
     *
     * @param id 技能ID
     * @return 结果
     */
    @Override
    public int deletePersonSkillById(Long id)
    {
        return personSkillMapper.deletePersonSkillById(id);
    }

    /**
     * 导出技能数据
     *
     * @return 技能数据列表
     */
    @Override
    public List<Map<String, Object>> exportSkillData()
    {
        List<Map<String, Object>> result = new ArrayList<>();

        SysPerson personQuery = new SysPerson();
        personQuery.setStatus("在职");
        List<SysPerson> persons = personMapper.selectPersonList(personQuery);

        for (SysPerson person : persons)
        {
            List<SysPersonSkill> skills = personSkillMapper.selectSkillByPersonId(person.getId());
            for (SysPersonSkill skill : skills)
            {
                Map<String, Object> row = new HashMap<>();
                row.put("name", person.getName());
                row.put("position", person.getPosition());
                row.put("level", person.getLevel());
                row.put("category", skill.getCategory());
                row.put("skill", skill.getSkill());
                row.put("source", skill.getSource());
                row.put("createTime", skill.getCreateTime());
                result.add(row);
            }
        }

        return result;
    }

    /**
     * 导出项目参与数据
     *
     * @return 项目参与数据列表
     */
    @Override
    public List<Map<String, Object>> exportProjectData()
    {
        List<Map<String, Object>> result = new ArrayList<>();

        SysPerson personQuery = new SysPerson();
        personQuery.setStatus("在职");
        List<SysPerson> persons = personMapper.selectPersonList(personQuery);

        for (SysPerson person : persons)
        {
            List<SysPersonProject> personProjects = personProjectMapper.selectByPersonId(person.getId());
            for (SysPersonProject pp : personProjects)
            {
                SysProject project = projectMapper.selectProjectById(pp.getProjectId());
                if (project != null)
                {
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", person.getName());
                    row.put("position", person.getPosition());
                    row.put("level", person.getLevel());
                    row.put("projectName", project.getName());
                    row.put("projectStatus", project.getStatus());
                    row.put("role", pp.getRole());
                    row.put("joinDate", pp.getJoinDate());
                    result.add(row);
                }
            }
        }

        return result;
    }

    /**
     * 根据预计释放日期计算资源状态
     * - 超过释放日期 → 空闲
     * - 距离释放日期 < 15天 → 即将空闲
     * - 15 <= 距离释放日期 <= 30天 → 临近空闲
     * - 距离释放日期 > 30天 → 忙碌
     * - 无释放日期 → 未设置
     *
     * @param expectedReleaseDate 预计释放日期
     * @return 资源状态
     */
    private String calculateResourceStatus(Date expectedReleaseDate)
    {
        if (expectedReleaseDate == null)
        {
            return "未设置";
        }

        LocalDate releaseDate = expectedReleaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        long daysUntilRelease = ChronoUnit.DAYS.between(today, releaseDate);

        if (daysUntilRelease < 0)
        {
            // 已超过释放日期
            return "空闲";
        }
        else if (daysUntilRelease < 15)
        {
            // 距离释放日期小于15天
            return "即将空闲";
        }
        else if (daysUntilRelease <= 30)
        {
            // 15 <= 距离释放日期 <= 30天
            return "临近空闲";
        }
        else
        {
            // 距离释放日期大于30天
            return "忙碌";
        }
    }
}