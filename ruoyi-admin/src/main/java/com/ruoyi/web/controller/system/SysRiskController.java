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
import com.ruoyi.system.domain.SysRisk;
import com.ruoyi.system.service.ISysRiskService;

/**
 * 风险信息操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/risk")
public class SysRiskController extends BaseController
{
    @Autowired
    private ISysRiskService riskService;

    /**
     * 获取风险列表
     */
    @PreAuthorize("@ss.hasPermi('system:risk:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRisk risk)
    {
        startPage();
        List<SysRisk> list = riskService.selectRiskList(risk);
        return getDataTable(list);
    }

    @Log(title = "风险管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:risk:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRisk risk)
    {
        List<SysRisk> list = riskService.selectRiskList(risk);
        ExcelUtil<SysRisk> util = new ExcelUtil<SysRisk>(SysRisk.class);
        util.exportExcel(response, list, "风险数据");
    }

    /**
     * 根据风险编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:risk:query')")
    @GetMapping(value = "/{riskId}")
    public AjaxResult getInfo(@PathVariable Long riskId)
    {
        return success(riskService.selectRiskById(riskId));
    }

    /**
     * 新增风险
     */
    @PreAuthorize("@ss.hasPermi('system:risk:add')")
    @Log(title = "风险管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRisk risk)
    {
        risk.setCreateBy(getUsername());
        return toAjax(riskService.insertRisk(risk));
    }

    /**
     * 修改风险
     */
    @PreAuthorize("@ss.hasPermi('system:risk:edit')")
    @Log(title = "风险管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRisk risk)
    {
        risk.setUpdateBy(getUsername());
        return toAjax(riskService.updateRisk(risk));
    }

    /**
     * 更新风险状态
     */
    @PreAuthorize("@ss.hasPermi('system:risk:edit')")
    @Log(title = "风险管理", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult updateStatus(@RequestBody SysRisk risk)
    {
        risk.setUpdateBy(getUsername());
        return toAjax(riskService.updateStatus(risk));
    }

    /**
     * 删除风险
     */
    @PreAuthorize("@ss.hasPermi('system:risk:remove')")
    @Log(title = "风险管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{riskIds}")
    public AjaxResult remove(@PathVariable Long[] riskIds)
    {
        return toAjax(riskService.deleteRiskByIds(riskIds));
    }
}