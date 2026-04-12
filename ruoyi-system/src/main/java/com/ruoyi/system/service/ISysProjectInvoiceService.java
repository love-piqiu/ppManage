package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysProjectInvoice;

/**
 * 项目开票Service接口
 *
 * @author ppmanage
 */
public interface ISysProjectInvoiceService
{
    /**
     * 查询项目开票列表
     */
    public List<SysProjectInvoice> selectInvoiceByProjectId(Long projectId);

    /**
     * 查询项目开票
     */
    public SysProjectInvoice selectInvoiceById(Long id);

    /**
     * 新增项目开票
     */
    public int insertInvoice(SysProjectInvoice invoice);

    /**
     * 修改项目开票
     */
    public int updateInvoice(SysProjectInvoice invoice);

    /**
     * 删除项目开票
     */
    public int deleteInvoiceById(Long id);

    /**
     * 批量删除项目开票
     */
    public int deleteInvoiceByIds(Long[] ids);
}