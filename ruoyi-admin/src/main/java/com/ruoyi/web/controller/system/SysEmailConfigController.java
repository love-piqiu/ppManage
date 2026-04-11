package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysEmailConfig;
import com.ruoyi.system.service.ISysEmailConfigService;
import com.ruoyi.system.service.ISysEmailService;

/**
 * 邮件配置操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/email")
public class SysEmailConfigController extends BaseController
{
    @Autowired
    private ISysEmailConfigService configService;

    @Autowired
    private ISysEmailService emailService;

    /**
     * 获取邮件配置
     */
    @PreAuthorize("@ss.hasPermi('system:email:query')")
    @GetMapping("/config")
    public AjaxResult getConfig()
    {
        SysEmailConfig config = configService.getConfig();
        // 隐藏密码
        if (config != null)
        {
            config.setPassword(null);
        }
        return success(config);
    }

    /**
     * 更新邮件配置
     */
    @PreAuthorize("@ss.hasPermi('system:email:edit')")
    @Log(title = "邮件配置", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public AjaxResult updateConfig(@RequestBody SysEmailConfig config)
    {
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 发送测试邮件
     */
    @PreAuthorize("@ss.hasPermi('system:email:edit')")
    @Log(title = "邮件配置", businessType = BusinessType.OTHER)
    @PostMapping("/test")
    public AjaxResult sendTestEmail()
    {
        boolean result = emailService.sendTestEmail();
        return result ? success("测试邮件发送成功") : error("测试邮件发送失败");
    }
}