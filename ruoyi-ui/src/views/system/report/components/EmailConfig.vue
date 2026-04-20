<template>
  <div class="pp-page-content">
    <!-- 自动发送设置 -->
    <div class="pp-config-section">
      <div class="pp-config-title">自动发送设置</div>
      <div class="pp-toggle-row">
        <div class="pp-toggle-info">
          <div class="pp-toggle-label">每周自动发送周报</div>
          <div class="pp-toggle-desc">开启后系统将在每周五自动发送周报到指定邮箱</div>
        </div>
        <div class="pp-toggle-switch" :class="{ active: form.autoSend }" @click="form.autoSend = !form.autoSend"></div>
      </div>

      <!-- 发送时间 -->
      <div class="pp-schedule-row" v-if="form.autoSend">
        <div class="pp-schedule-icon">📅</div>
        <div class="pp-schedule-info">
          <div class="pp-schedule-title">下次发送时间</div>
          <div class="pp-schedule-desc">系统将自动生成并发送本周周报</div>
        </div>
        <div class="pp-schedule-time">{{ nextSendTime }}</div>
      </div>

      <div class="pp-form-row" v-if="form.autoSend">
        <div class="pp-form-group">
          <label class="pp-form-label">发送日期</label>
          <el-select v-model="form.sendDay" class="pp-form-input">
            <el-option label="周一" value="周一" />
            <el-option label="周二" value="周二" />
            <el-option label="周三" value="周三" />
            <el-option label="周四" value="周四" />
            <el-option label="周五" value="周五" />
            <el-option label="周六" value="周六" />
            <el-option label="周日" value="周日" />
          </el-select>
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">发送时间</label>
          <el-time-picker v-model="form.sendTime" class="pp-form-input" value-format="HH:mm" format="HH:mm" />
        </div>
      </div>

      <div class="pp-form-group" v-if="form.autoSend">
        <label class="pp-form-label">收件人邮箱</label>
        <el-input v-model="form.recipients" class="pp-form-input" placeholder="输入收件人邮箱" />
        <div class="pp-form-hint">多个邮箱用逗号分隔</div>
      </div>
    </div>

    <!-- SMTP配置 -->
    <div class="pp-config-section">
      <div class="pp-config-title">SMTP 邮件服务器配置</div>
      <div class="pp-form-row">
        <div class="pp-form-group">
          <label class="pp-form-label">SMTP服务器</label>
          <el-input v-model="form.smtpServer" class="pp-form-input" placeholder="如: smtp.sina.com" />
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">端口</label>
          <el-input v-model.number="form.smtpPort" class="pp-form-input pp-port-input" placeholder="如: 465" type="number" />
        </div>
      </div>
      <div class="pp-form-row">
        <div class="pp-form-group">
          <label class="pp-form-label">发件人账号</label>
          <el-input v-model="form.senderAccount" class="pp-form-input" placeholder="发件人邮箱账号" />
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">授权码/密码</label>
          <el-input v-model="form.senderPassword" class="pp-form-input" type="password" placeholder="邮箱授权码或密码" show-password />
          <div class="pp-form-hint">邮箱需使用授权码，非登录密码</div>
        </div>
      </div>
      <div class="pp-form-row">
        <div class="pp-form-group">
          <label class="pp-form-label">发件人名称</label>
          <el-input v-model="form.senderName" class="pp-form-input" placeholder="显示在邮件发件人位置" />
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">邮件主题</label>
          <el-input v-model="form.subjectTemplate" class="pp-form-input" placeholder="支持变量: {日期}, {周数}" />
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="pp-config-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleTest">📤 发送测试邮件</button>
        <button class="pp-btn pp-btn-primary" @click="handleSave">💾 保存配置</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getEmailConfig, saveEmailConfig, testReportEmail } from '@/api/system/report'

export default {
  name: 'EmailConfig',
  data() {
    return {
      configId: null, // 保存从后端获取的配置ID
      form: {
        autoSend: false,
        sendDay: '周五',
        sendTime: '17:00',
        recipients: '',
        smtpServer: '',
        smtpPort: 465,
        senderAccount: '',
        senderPassword: '',
        senderName: '',
        subjectTemplate: '项目周报 - {日期}'
      }
    }
  },
  computed: {
    nextSendTime() {
      // 确保 sendDay 和 sendTime 有默认值
      const day = this.form.sendDay ? this.form.sendDay : '周五'
      const time = this.form.sendTime ? this.form.sendTime : '17:00'
      return `${day} ${time}`
    }
  },
  created() {
    this.loadConfig()
  },
  methods: {
    async loadConfig() {
      try {
        const res = await getEmailConfig()
        if (res.data) {
          // 保存配置ID，用于更新
          this.configId = res.data.id
          // 映射后端字段到前端字段
          const data = res.data
          // 处理时间格式：数据库返回可能带秒或为null，需要处理
          let sendTime = '17:00' // 默认值
          if (data.sendTime) {
            sendTime = data.sendTime
            if (sendTime.length > 5) {
              sendTime = sendTime.substring(0, 5)
            }
          }
          this.form = {
            autoSend: data.enabled === 1,
            sendDay: data.sendDay || '周五',
            sendTime: sendTime,
            recipients: data.recipientEmail || '',
            smtpServer: data.host || '',
            smtpPort: data.port || 465,
            senderAccount: data.username || '',
            senderPassword: '', // 密码不返回，需要重新输入
            senderName: data.senderName || '',
            subjectTemplate: data.emailSubject || '项目周报 - {日期}'
          }
        }
      } catch (e) {
        console.error('加载邮件配置失败:', e)
      }
    },
    async handleTest() {
      // 先保存当前配置，再发送测试邮件
      await this.doSave()
      try {
        await testReportEmail()
        this.$modal.msgSuccess('测试邮件已发送，请检查收件箱')
      } catch (e) {
        // 错误信息会在API层面处理
      }
    },
    async handleSave() {
      await this.doSave()
      this.$modal.msgSuccess('配置保存成功')
    },
    async doSave() {
      // 映射前端字段到后端字段，必须传递id用于更新
      const config = {
        id: this.configId || 1, // 如果没有获取到id，默认使用1（数据库初始化的id）
        host: this.form.smtpServer,
        port: this.form.smtpPort,
        username: this.form.senderAccount,
        password: this.form.senderPassword,
        recipientEmail: this.form.recipients,
        senderName: this.form.senderName,
        emailSubject: this.form.subjectTemplate,
        sendDay: this.form.sendDay,
        sendTime: this.form.sendTime,
        enabled: this.form.autoSend ? 1 : 0
      }
      await saveEmailConfig(config)
      // 保存成功后重新加载配置
      await this.loadConfig()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

.pp-page-content { padding: 24px; }

.pp-config-section {
  background: white;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  padding: 24px;
  margin-bottom: 24px;
}

.pp-config-title {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 16px;
}

/* Toggle */
.pp-toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #F9FAFB;
  border-radius: 6px;
  margin-bottom: 16px;
}

.pp-toggle-info { flex: 1; }
.pp-toggle-label { font-size: 14px; font-weight: 500; color: #374151; }
.pp-toggle-desc { font-size: 12px; color: #6B7280; margin-top: 2px; }

.pp-toggle-switch {
  width: 44px;
  height: 24px;
  background: #D1D5DB;
  border-radius: 12px;
  position: relative;
  cursor: pointer;
  transition: background 0.2s;
  &.active { background: #2563EB; }
  &::after {
    content: '';
    position: absolute;
    width: 20px;
    height: 20px;
    background: white;
    border-radius: 50%;
    top: 2px;
    left: 2px;
    transition: transform 0.2s;
  }
  &.active::after { transform: translateX(20px); }
}

/* Schedule */
.pp-schedule-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #EFF6FF;
  border: 1px solid #2563EB;
  border-radius: 8px;
  margin-bottom: 16px;
}

.pp-schedule-icon {
  width: 40px;
  height: 40px;
  background: #2563EB;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.pp-schedule-info { flex: 1; }
.pp-schedule-title { font-size: 14px; font-weight: 600; color: #1F2937; }
.pp-schedule-desc { font-size: 12px; color: #6B7280; margin-top: 2px; }
.pp-schedule-time { font-size: 14px; font-weight: 600; color: #2563EB; }

/* Form */
.pp-form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.pp-form-group { display: flex; flex-direction: column; }
.pp-form-label {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}
.pp-form-input {
  width: 100%;
}
// 端口输入框样式优化
.pp-port-input {
  ::v-deep input {
    text-align: left;
    padding-left: 15px;
    // 去掉 number 输入框的上下箭头
    -moz-appearance: textfield;
    &::-webkit-outer-spin-button,
    &::-webkit-inner-spin-button {
      -webkit-appearance: none;
      margin: 0;
    }
  }
  ::v-deep .el-input__suffix {
    display: none;
  }
}
.pp-form-hint {
  font-size: 11px;
  color: #9CA3AF;
  margin-top: 4px;
}

.pp-config-actions { display: flex; gap: 12px; margin-top: 24px; }
</style>