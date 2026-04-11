package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysEmailConfig;

/**
 * 邮件配置 服务层
 *
 * @author ppmanage
 */
public interface ISysEmailConfigService
{
    /**
     * 获取邮件配置
     *
     * @return 邮件配置
     */
    public SysEmailConfig getConfig();

    /**
     * 更新邮件配置
     *
     * @param config 邮件配置
     * @return 结果
     */
    public int updateConfig(SysEmailConfig config);
}