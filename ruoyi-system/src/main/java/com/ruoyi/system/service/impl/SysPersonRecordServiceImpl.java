package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysPersonRecord;
import com.ruoyi.system.mapper.SysPersonRecordMapper;
import com.ruoyi.system.service.ISysPersonRecordService;

/**
 * 人员动态记录 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysPersonRecordServiceImpl implements ISysPersonRecordService
{
    @Autowired
    private SysPersonRecordMapper recordMapper;

    /**
     * 查询人员动态记录列表
     *
     * @param record 记录信息
     * @return 记录列表
     */
    @Override
    public List<SysPersonRecord> selectRecordList(SysPersonRecord record)
    {
        return recordMapper.selectRecordList(record);
    }

    /**
     * 查询某人员的所有记录
     *
     * @param personId 人员ID
     * @return 记录列表
     */
    @Override
    public List<SysPersonRecord> selectByPersonId(Long personId)
    {
        return recordMapper.selectByPersonId(personId);
    }

    /**
     * 通过ID查询记录
     *
     * @param id 记录ID
     * @return 记录
     */
    @Override
    public SysPersonRecord selectRecordById(Long id)
    {
        return recordMapper.selectRecordById(id);
    }

    /**
     * 新增记录
     *
     * @param record 记录信息
     * @return 结果
     */
    @Override
    public int insertRecord(SysPersonRecord record)
    {
        return recordMapper.insertRecord(record);
    }

    /**
     * 修改记录
     *
     * @param record 记录信息
     * @return 结果
     */
    @Override
    public int updateRecord(SysPersonRecord record)
    {
        return recordMapper.updateRecord(record);
    }

    /**
     * 删除记录
     *
     * @param id 记录ID
     * @return 结果
     */
    @Override
    public int deleteRecordById(Long id)
    {
        return recordMapper.deleteRecordById(id);
    }

    /**
     * 批量删除记录
     *
     * @param ids 记录ID数组
     * @return 结果
     */
    @Override
    public int deleteRecordByIds(Long[] ids)
    {
        return recordMapper.deleteRecordByIds(ids);
    }
}