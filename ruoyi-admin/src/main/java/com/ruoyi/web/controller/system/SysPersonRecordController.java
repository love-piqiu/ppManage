package com.ruoyi.web.controller.system;

import java.util.List;
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
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysPersonRecord;
import com.ruoyi.system.service.ISysPersonRecordService;

/**
 * 人员动态记录操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/person/record")
public class SysPersonRecordController extends BaseController
{
    @Autowired
    private ISysPersonRecordService recordService;

    /**
     * 获取某人员的动态记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:person:query')")
    @GetMapping("/list/{personId}")
    public AjaxResult list(@PathVariable Long personId)
    {
        List<SysPersonRecord> list = recordService.selectByPersonId(personId);
        return success(list);
    }

    /**
     * 获取记录详情
     */
    @PreAuthorize("@ss.hasPermi('system:person:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(recordService.selectRecordById(id));
    }

    /**
     * 新增动态记录
     */
    @PreAuthorize("@ss.hasPermi('system:person:edit')")
    @Log(title = "人员动态记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPersonRecord record)
    {
        record.setCreateBy(getUsername());
        return toAjax(recordService.insertRecord(record));
    }

    /**
     * 修改动态记录
     */
    @PreAuthorize("@ss.hasPermi('system:person:edit')")
    @Log(title = "人员动态记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPersonRecord record)
    {
        record.setUpdateBy(getUsername());
        return toAjax(recordService.updateRecord(record));
    }

    /**
     * 删除动态记录
     */
    @PreAuthorize("@ss.hasPermi('system:person:edit')")
    @Log(title = "人员动态记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(recordService.deleteRecordById(id));
    }
}