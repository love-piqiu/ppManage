package com.ruoyi.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysEmailConfig;
import com.ruoyi.system.mapper.SysEmailConfigMapper;
import com.ruoyi.system.service.ISysEmailConfigService;

/**
 * 邮件配置 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysEmailConfigServiceImpl implements ISysEmailConfigService
{
    @Autowired
    private SysEmailConfigMapper configMapper;

    /**
     * 获取邮件配置
     *
     * @return 邮件配置
     */
    @Override
    public SysEmailConfig getConfig()
    {
        return configMapper.selectConfig();
    }

    /**
     * 更新邮件配置
     *
     * @param config 邮件配置
     * @return 结果
     */
    @Override
    public int updateConfig(SysEmailConfig config)
    {
        return configMapper.updateConfig(config);
    }
}