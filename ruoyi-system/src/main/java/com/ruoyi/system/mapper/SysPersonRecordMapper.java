package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPersonRecord;

/**
 * 人员动态记录 数据层
 *
 * @author ppmanage
 */
public interface SysPersonRecordMapper
{
    /**
     * 查询人员动态记录列表
     *
     * @param record 记录信息
     * @return 记录列表
     */
    public List<SysPersonRecord> selectRecordList(SysPersonRecord record);

    /**
     * 查询某人员的所有记录
     *
     * @param personId 人员ID
     * @return 记录列表
     */
    public List<SysPersonRecord> selectByPersonId(Long personId);

    /**
     * 通过ID查询记录
     *
     * @param id 记录ID
     * @return 记录
     */
    public SysPersonRecord selectRecordById(Long id);

    /**
     * 新增记录
     *
     * @param record 记录信息
     * @return 结果
     */
    public int insertRecord(SysPersonRecord record);

    /**
     * 修改记录
     *
     * @param record 记录信息
     * @return 结果
     */
    public int updateRecord(SysPersonRecord record);

    /**
     * 删除记录
     *
     * @param id 记录ID
     * @return 结果
     */
    public int deleteRecordById(Long id);

    /**
     * 批量删除记录
     *
     * @param ids 记录ID数组
     * @return 结果
     */
    public int deleteRecordByIds(Long[] ids);

    /**
     * 根据人员ID删除记录
     *
     * @param personId 人员ID
     * @return 结果
     */
    public int deleteRecordByPersonId(Long personId);
}