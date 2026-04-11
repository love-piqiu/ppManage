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
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.domain.SysPersonProject;
import com.ruoyi.system.service.ISysPersonService;
import com.ruoyi.system.service.ISysPersonProjectService;

/**
 * 人员信息操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/person")
public class SysPersonController extends BaseController
{
    @Autowired
    private ISysPersonService personService;

    @Autowired
    private ISysPersonProjectService personProjectService;

    /**
     * 获取人员列表
     */
    @PreAuthorize("@ss.hasPermi('system:person:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPerson person)
    {
        startPage();
        List<SysPerson> list = personService.selectPersonList(person);
        return getDataTable(list);
    }

    @Log(title = "人员管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:person:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPerson person)
    {
        List<SysPerson> list = personService.selectPersonList(person);
        ExcelUtil<SysPerson> util = new ExcelUtil<SysPerson>(SysPerson.class);
        util.exportExcel(response, list, "人员数据");
    }

    /**
     * 根据人员编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:person:query')")
    @GetMapping(value = "/{personId}")
    public AjaxResult getInfo(@PathVariable Long personId)
    {
        return success(personService.selectPersonById(personId));
    }

    /**
     * 新增人员
     */
    @PreAuthorize("@ss.hasPermi('system:person:add')")
    @Log(title = "人员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPerson person)
    {
        if (!personService.checkNameUnique(person))
        {
            return error("新增人员'" + person.getName() + "'失败，姓名已存在");
        }
        person.setCreateBy(getUsername());
        return toAjax(personService.insertPerson(person));
    }

    /**
     * 修改人员
     */
    @PreAuthorize("@ss.hasPermi('system:person:edit')")
    @Log(title = "人员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPerson person)
    {
        if (!personService.checkNameUnique(person))
        {
            return error("修改人员'" + person.getName() + "'失败，姓名已存在");
        }
        person.setUpdateBy(getUsername());
        return toAjax(personService.updatePerson(person));
    }

    /**
     * 删除人员
     */
    @PreAuthorize("@ss.hasPermi('system:person:remove')")
    @Log(title = "人员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{personIds}")
    public AjaxResult remove(@PathVariable Long[] personIds)
    {
        return toAjax(personService.deletePersonByIds(personIds));
    }

    /**
     * 获取人员选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<SysPerson> persons = personService.selectPersonAll();
        return success(persons);
    }

    /**
     * 获取人员参与的项目列表
     */
    @PreAuthorize("@ss.hasPermi('system:person:query')")
    @GetMapping("/projects/{personId}")
    public AjaxResult getProjects(@PathVariable Long personId)
    {
        List<SysPersonProject> projects = personProjectService.selectByPersonId(personId);
        return success(projects);
    }

    /**
     * 分配人员到项目
     */
    @PreAuthorize("@ss.hasPermi('system:person:edit')")
    @Log(title = "人员管理", businessType = BusinessType.INSERT)
    @PostMapping("/project")
    public AjaxResult assignProject(@RequestBody SysPersonProject personProject)
    {
        personProject.setCreateBy(getUsername());
        return toAjax(personProjectService.insert(personProject));
    }

    /**
     * 移除人员项目关联
     */
    @PreAuthorize("@ss.hasPermi('system:person:edit')")
    @Log(title = "人员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/project/{id}")
    public AjaxResult removeProject(@PathVariable Long id)
    {
        return toAjax(personProjectService.deleteById(id));
    }
}