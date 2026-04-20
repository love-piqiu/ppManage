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
     * 更新邮件配置（有则更新，无则新增）
     *
     * @param config 邮件配置
     * @return 结果
     */
    @Override
    public int updateConfig(SysEmailConfig config)
    {
        // 先查询是否已有配置
        SysEmailConfig existing = configMapper.selectConfig();
        if (existing != null)
        {
            // 存在配置，使用更新
            config.setId(existing.getId());
            // 如果密码为空，保留原密码
            if (config.getPassword() == null || config.getPassword().isEmpty())
            {
                config.setPassword(existing.getPassword());
            }
            return configMapper.updateConfig(config);
        }
        else
        {
            // 不存在配置，使用新增
            return configMapper.insertConfig(config);
        }
    }
}