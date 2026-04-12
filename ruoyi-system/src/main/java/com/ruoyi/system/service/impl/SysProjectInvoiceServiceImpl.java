package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysProjectInvoiceMapper;
import com.ruoyi.system.domain.SysProjectInvoice;
import com.ruoyi.system.service.ISysProjectInvoiceService;

/**
 * 项目开票Service实现
 *
 * @author ppmanage
 */
@Service
public class SysProjectInvoiceServiceImpl implements ISysProjectInvoiceService
{
    @Autowired
    private SysProjectInvoiceMapper invoiceMapper;

    @Override
    public List<SysProjectInvoice> selectInvoiceByProjectId(Long projectId)
    {
        return invoiceMapper.selectInvoiceByProjectId(projectId);
    }

    @Override
    public SysProjectInvoice selectInvoiceById(Long id)
    {
        return invoiceMapper.selectInvoiceById(id);
    }

    @Override
    public int insertInvoice(SysProjectInvoice invoice)
    {
        return invoiceMapper.insertInvoice(invoice);
    }

    @Override
    public int updateInvoice(SysProjectInvoice invoice)
    {
        return invoiceMapper.updateInvoice(invoice);
    }

    @Override
    public int deleteInvoiceById(Long id)
    {
        return invoiceMapper.deleteInvoiceById(id);
    }

    @Override
    public int deleteInvoiceByIds(Long[] ids)
    {
        return invoiceMapper.deleteInvoiceByIds(ids);
    }
}