package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysEmailConfig;

/**
 * 邮件配置 数据层
 *
 * @author ppmanage
 */
public interface SysEmailConfigMapper
{
    /**
     * 查询邮件配置
     *
     * @return 邮件配置
     */
    public SysEmailConfig selectConfig();

    /**
     * 更新邮件配置
     *
     * @param config 邮件配置
     * @return 结果
     */
    public int updateConfig(SysEmailConfig config);
}