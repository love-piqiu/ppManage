package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysProjectInvoice;
import com.ruoyi.system.service.ISysProjectInvoiceService;

/**
 * 项目开票Controller
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/invoice")
public class SysProjectInvoiceController extends BaseController
{
    @Autowired
    private ISysProjectInvoiceService invoiceService;

    /**
     * 查询项目开票列表
     */
    @PreAuthorize("@ss.hasPermi('system:project:list')")
    @GetMapping("/list/{projectId}")
    public AjaxResult list(@PathVariable("projectId") Long projectId)
    {
        List<SysProjectInvoice> list = invoiceService.selectInvoiceByProjectId(projectId);
        return AjaxResult.success(list);
    }

    /**
     * 获取项目开票详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:project:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(invoiceService.selectInvoiceById(id));
    }

    /**
     * 新增项目开票
     */
    @PreAuthorize("@ss.hasPermi('system:project:edit')")
    @Log(title = "项目开票", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysProjectInvoice invoice)
    {
        invoice.setCreateBy(getUsername());
        return toAjax(invoiceService.insertInvoice(invoice));
    }

    /**
     * 修改项目开票
     */
    @PreAuthorize("@ss.hasPermi('system:project:edit')")
    @Log(title = "项目开票", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysProjectInvoice invoice)
    {
        invoice.setUpdateBy(getUsername());
        return toAjax(invoiceService.updateInvoice(invoice));
    }

    /**
     * 删除项目开票
     */
    @PreAuthorize("@ss.hasPermi('system:project:edit')")
    @Log(title = "项目开票", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(invoiceService.deleteInvoiceByIds(ids));
    }
}