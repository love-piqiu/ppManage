package com.ruoyi.web.controller.system;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.domain.SysProjectMilestone;
import com.ruoyi.system.service.ISysProjectService;
import com.ruoyi.system.service.ISysProjectMilestoneService;

/**
 * 项目信息操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/project")
public class SysProjectController extends BaseController
{
    @Autowired
    private ISysProjectService projectService;

    @Autowired
    private ISysProjectMilestoneService milestoneService;

    /**
     * 获取项目列表
     */
    @PreAuthorize("@ss.hasPermi('system:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysProject project)
    {
        startPage();
        List<SysProject> list = projectService.selectProjectList(project);
        return getDataTable(list);
    }

    @Log(title = "项目管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:project:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysProject project)
    {
        List<SysProject> list = projectService.selectProjectList(project);
        ExcelUtil<SysProject> util = new ExcelUtil<SysProject>(SysProject.class);
        util.exportExcel(response, list, "项目数据");
    }

    /**
     * 根据项目编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:project:query')")
    @GetMapping(value = "/{projectId}")
    public AjaxResult getInfo(@PathVariable Long projectId)
    {
        return success(projectService.selectProjectById(projectId));
    }

    /**
     * 新增项目
     */
    @PreAuthorize("@ss.hasPermi('system:project:add')")
    @Log(title = "项目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysProject project)
    {
        if (!projectService.checkNameUnique(project))
        {
            return error("新增项目'" + project.getName() + "'失败，项目名称已存在");
        }
        project.setCreateBy(getUsername());
        return toAjax(projectService.insertProject(project));
    }

    /**
     * 修改项目
     */
    @PreAuthorize("@ss.hasPermi('system:project:edit')")
    @Log(title = "项目管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysProject project)
    {
        if (!projectService.checkNameUnique(project))
        {
            return error("修改项目'" + project.getName() + "'失败，项目名称已存在");
        }
        project.setUpdateBy(getUsername());
        return toAjax(projectService.updateProject(project));
    }

    /**
     * 删除项目
     */
    @PreAuthorize("@ss.hasPermi('system:project:remove')")
    @Log(title = "项目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{projectIds}")
    public AjaxResult remove(@PathVariable Long[] projectIds)
    {
        return toAjax(projectService.deleteProjectByIds(projectIds));
    }

    /**
     * 获取项目选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<SysProject> projects = projectService.selectProjectAll();
        return success(projects);
    }

    /**
     * 获取项目重要事项列表
     */
    @PreAuthorize("@ss.hasPermi('system:project:query')")
    @GetMapping("/milestone/{projectId}")
    public AjaxResult getMilestones(@PathVariable Long projectId)
    {
        List<SysProjectMilestone> milestones = milestoneService.selectByProjectId(projectId);
        return success(milestones);
    }

    /**
     * 新增项目重要事项
     */
    @PreAuthorize("@ss.hasPermi('system:project:edit')")
    @Log(title = "项目管理", businessType = BusinessType.INSERT)
    @PostMapping("/milestone")
    public AjaxResult addMilestone(@Validated @RequestBody SysProjectMilestone milestone)
    {
        milestone.setCreateBy(getUsername());
        return toAjax(milestoneService.insert(milestone));
    }

    /**
     * 删除项目重要事项
     */
    @PreAuthorize("@ss.hasPermi('system:project:edit')")
    @Log(title = "项目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/milestone/{id}")
    public AjaxResult removeMilestone(@PathVariable Long id)
    {
        return toAjax(milestoneService.deleteById(id));
    }
}