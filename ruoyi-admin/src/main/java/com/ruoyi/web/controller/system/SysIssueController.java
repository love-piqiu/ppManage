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
import com.ruoyi.system.domain.SysIssue;
import com.ruoyi.system.service.ISysIssueService;

/**
 * 问题信息操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/issue")
public class SysIssueController extends BaseController
{
    @Autowired
    private ISysIssueService issueService;

    /**
     * 获取问题列表
     */
    @PreAuthorize("@ss.hasPermi('system:issue:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysIssue issue)
    {
        startPage();
        List<SysIssue> list = issueService.selectIssueList(issue);
        return getDataTable(list);
    }

    @Log(title = "问题管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:issue:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysIssue issue)
    {
        List<SysIssue> list = issueService.selectIssueList(issue);
        ExcelUtil<SysIssue> util = new ExcelUtil<SysIssue>(SysIssue.class);
        util.exportExcel(response, list, "问题数据");
    }

    /**
     * 根据问题编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:issue:query')")
    @GetMapping(value = "/{issueId}")
    public AjaxResult getInfo(@PathVariable Long issueId)
    {
        return success(issueService.selectIssueById(issueId));
    }

    /**
     * 新增问题
     */
    @PreAuthorize("@ss.hasPermi('system:issue:add')")
    @Log(title = "问题管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysIssue issue)
    {
        issue.setCreateBy(getUsername());
        return toAjax(issueService.insertIssue(issue));
    }

    /**
     * 修改问题
     */
    @PreAuthorize("@ss.hasPermi('system:issue:edit')")
    @Log(title = "问题管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysIssue issue)
    {
        issue.setUpdateBy(getUsername());
        return toAjax(issueService.updateIssue(issue));
    }

    /**
     * 更新问题状态
     */
    @PreAuthorize("@ss.hasPermi('system:issue:edit')")
    @Log(title = "问题管理", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult updateStatus(@RequestBody SysIssue issue)
    {
        issue.setUpdateBy(getUsername());
        return toAjax(issueService.updateStatus(issue));
    }

    /**
     * 删除问题
     */
    @PreAuthorize("@ss.hasPermi('system:issue:remove')")
    @Log(title = "问题管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{issueIds}")
    public AjaxResult remove(@PathVariable Long[] issueIds)
    {
        return toAjax(issueService.deleteIssueByIds(issueIds));
    }
}