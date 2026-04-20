<template>
  <div class="pp-email-config">
    <!-- 自动发送设置 -->
    <div class="pp-config-section">
      <div class="pp-config-title">自动发送设置</div>

      <div class="pp-toggle-row">
        <div>
          <div class="pp-toggle-label">每周自动发送周报</div>
          <div class="pp-toggle-desc">开启后系统将在每周自动发送周报到指定邮箱</div>
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
            <el-option label="周一" value="1" />
            <el-option label="周二" value="2" />
            <el-option label="周三" value="3" />
            <el-option label="周四" value="4" />
            <el-option label="周五" value="5" />
            <el-option label="周六" value="6" />
            <el-option label="周日" value="7" />
          </el-select>
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">发送时间</label>
          <el-input v-model="form.sendTime" placeholder="如: 17:00" class="pp-form-input pp-time-input" />
        </div>
      </div>

      <div class="pp-form-group" v-if="form.autoSend">
        <label class="pp-form-label">收件人邮箱</label>
        <el-input v-model="form.recipients" placeholder="多个邮箱用逗号分隔" class="pp-form-input" />
        <div class="pp-form-hint">多个邮箱用逗号分隔</div>
      </div>
    </div>

    <!-- SMTP配置 -->
    <div class="pp-config-section">
      <div class="pp-config-title">SMTP 邮件服务器配置</div>

      <div class="pp-form-row">
        <div class="pp-form-group">
          <label class="pp-form-label">SMTP服务器</label>
          <el-input v-model="form.smtpHost" placeholder="如: smtp.qq.com" class="pp-form-input" />
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">端口</label>
          <el-input v-model.number="form.smtpPort" type="number" placeholder="如: 465" class="pp-form-input pp-port-input" />
        </div>
      </div>

      <div class="pp-form-row">
        <div class="pp-form-group">
          <label class="pp-form-label">发件人账号</label>
          <el-input v-model="form.smtpUser" placeholder="发件人邮箱账号" class="pp-form-input" />
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">授权码/密码</label>
          <el-input v-model="form.smtpPassword" type="password" :placeholder="form.hasPassword ? '密码已保存，如需修改请输入新密码' : '邮箱授权码或密码'" class="pp-form-input" />
          <div class="pp-form-hint">邮箱需使用授权码，非登录密码</div>
        </div>
      </div>

      <div class="pp-form-row">
        <div class="pp-form-group">
          <label class="pp-form-label">发件人名称</label>
          <el-input v-model="form.senderName" placeholder="显示在邮件发件人位置" class="pp-form-input" />
        </div>
        <div class="pp-form-group">
          <label class="pp-form-label">邮件主题</label>
          <el-input v-model="form.subjectTemplate" placeholder="支持变量: {日期}, {周数}" class="pp-form-input" />
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="pp-config-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleTestEmail">📤 发送测试邮件</button>
        <button class="pp-btn pp-btn-primary" @click="handleSave">💾 保存配置</button>
      </div>
    </div>

    <!-- 邮件模板预览 -->
    <div class="pp-config-section">
      <div class="pp-config-title">邮件模板预览</div>
      <div class="pp-preview-tip">以下是发送的邮件内容样式预览</div>

      <div class="pp-email-preview" v-html="previewHtml"></div>
    </div>
  </div>
</template>

<script>
import { getEmailConfig, saveEmailConfig, testEmail, getEmailPreview } from "@/api/report";

export default {
  name: "EmailConfig",
  data() {
    return {
      form: {
        autoSend: true,
        sendDay: '5',
        sendTime: '17:00',
        recipients: '',
        smtpHost: 'smtp.qq.com',
        smtpPort: 465,
        smtpUser: '',
        smtpPassword: '',
        hasPassword: false,
        senderName: '项目管理系统',
        subjectTemplate: '项目周报 - {日期}'
      },
      previewHtml: ''
    };
  },
  computed: {
    nextSendTime() {
      // 计算下次发送的具体日期和时间
      const now = new Date();
      const currentDayOfWeek = now.getDay(); // 0=周日, 1=周一, ..., 5=周五, 6=周六

      // 将周日=0 转换为 周一=1, 周日=7 的格式，便于计算
      const adjustedCurrentDay = currentDayOfWeek === 0 ? 7 : currentDayOfWeek; // 1=周一, ..., 5=周五, 7=周日

      // 处理 sendDay 可能是数字或字符串的情况
      let targetDayNum = 5; // 默认周五
      const dayValue = this.form.sendDay;
      if (dayValue) {
        const dayNameMap = { '周一': 1, '周二': 2, '周三': 3, '周四': 4, '周五': 5, '周六': 6, '周日': 7, '1': 1, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7 };
        targetDayNum = dayNameMap[dayValue] || parseInt(dayValue) || 5;
      }

      // 解析发送时间
      const timeStr = this.form.sendTime || '17:00';
      const [targetHour, targetMinute] = timeStr.split(':').map(n => parseInt(n) || 0);

      // 当前时间（小时和分钟）
      const currentHour = now.getHours();
      const currentMinute = now.getMinutes();
      const currentTotalMinutes = currentHour * 60 + currentMinute;
      const targetTotalMinutes = targetHour * 60 + targetMinute;

      // 计算距离下次发送的天数
      let daysUntilSend = 0;

      if (adjustedCurrentDay === targetDayNum) {
        // 今天是发送日
        if (currentTotalMinutes < targetTotalMinutes) {
          // 当前时间还没过发送时间，今天发送
          daysUntilSend = 0;
        } else {
          // 已经过了发送时间，下周发送日发送
          daysUntilSend = 7;
        }
      } else if (adjustedCurrentDay < targetDayNum) {
        // 当前日在发送日之前（如周一在周五之前）
        daysUntilSend = targetDayNum - adjustedCurrentDay;
      } else {
        // 当前日在发送日之后（如周六在周五之后）
        daysUntilSend = 7 - adjustedCurrentDay + targetDayNum;
      }

      // 计算下次发送日期
      const nextSendDate = new Date(now);
      nextSendDate.setDate(now.getDate() + daysUntilSend);

      const month = nextSendDate.getMonth() + 1;
      const day = nextSendDate.getDate();

      return `${month}月${day}日 ${timeStr}`;
    }
  },
  created() {
    this.loadConfig();
    this.loadPreview();
  },
  methods: {
    loadConfig() {
      getEmailConfig().then(res => {
        if (res.data) {
          const data = res.data;
          // 处理时间格式：数据库返回可能带秒，去掉秒部分
          let sendTime = data.sendTime || '17:00';
          if (sendTime && sendTime.length > 5) {
            sendTime = sendTime.substring(0, 5);
          }
          // 处理 sendDay：后端可能返回 '周五' 或 '5'
          let sendDay = '5'; // 默认周五
          if (data.sendDay) {
            const dayNameToNum = { '周一': '1', '周二': '2', '周三': '3', '周四': '4', '周五': '5', '周六': '6', '周日': '7' };
            sendDay = dayNameToNum[data.sendDay] || data.sendDay;
          }
          // 处理密码：如果返回 "******" 表示密码已配置
          let smtpPassword = '';
          let hasPassword = false;
          if (data.password === '******') {
            hasPassword = true;
            smtpPassword = ''; // 清空，让 placeholder 显示提示
          }
          // 映射后端字段到前端字段
          this.form = {
            autoSend: data.enabled === 1,
            sendDay: sendDay,
            sendTime: sendTime,
            recipients: data.recipientEmail || '',
            smtpHost: data.host || 'smtp.qq.com',
            smtpPort: data.port || 465,
            smtpUser: data.username || '',
            smtpPassword: smtpPassword,
            hasPassword: hasPassword, // 标记是否已有密码
            senderName: data.senderName || '项目管理系统',
            subjectTemplate: data.emailSubject || '项目周报 - {日期}'
          };
        }
      });
    },
    loadPreview() {
      getEmailPreview().then(res => {
        // 若依框架返回字符串时可能在 res.msg 或 res.data
        this.previewHtml = res.msg || res.data || '';
      }).catch(() => {
        this.previewHtml = '';
      });
    },
    handleTestEmail() {
      this.$modal.confirm('发送测试邮件到配置的收件人邮箱？').then(() => {
        return testEmail(this.form);
      }).then(() => {
        this.$modal.msgSuccess("测试邮件已发送，请检查收件箱");
      }).catch(() => {});
    },
    handleSave() {
      // 处理 sendDay：前端用数字 '1'-'7'，后端需要字符串
      const numToDayName = { '1': '周一', '2': '周二', '3': '周三', '4': '周四', '5': '周五', '6': '周六', '7': '周日' };
      const sendDayValue = numToDayName[this.form.sendDay] || '周五';
      // 处理密码：如果是占位符 "******"，表示用户没修改，不发送密码字段
      let passwordValue = null;
      if (this.form.smtpPassword && this.form.smtpPassword !== '******') {
        passwordValue = this.form.smtpPassword;
      }
      // 映射前端字段到后端字段
      const config = {
        host: this.form.smtpHost,
        port: this.form.smtpPort,
        username: this.form.smtpUser,
        password: passwordValue,
        recipientEmail: this.form.recipients,
        senderName: this.form.senderName,
        emailSubject: this.form.subjectTemplate,
        sendDay: sendDayValue,
        sendTime: this.form.sendTime,
        enabled: this.form.autoSend ? 1 : 0
      };
      saveEmailConfig(config).then(() => {
        this.$modal.msgSuccess("配置保存成功");
        // 保存后重新加载配置
        this.loadConfig();
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.pp-email-config { }

.pp-config-section { background: white; border-radius: 8px; border: 1px solid #E5E7EB; padding: 24px; margin-bottom: 24px; }
.pp-config-title { font-size: 16px; font-weight: 600; color: #374151; margin-bottom: 16px; }
.pp-preview-tip { font-size: 12px; color: #6B7280; margin-bottom: 12px; }

.pp-toggle-row { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; background: #F9FAFB; border-radius: 6px; margin-bottom: 16px; }
.pp-toggle-label { font-size: 14px; font-weight: 500; color: #374151; }
.pp-toggle-desc { font-size: 12px; color: #6B7280; margin-top: 2px; }
.pp-toggle-switch { width: 44px; height: 24px; background: #D1D5DB; border-radius: 12px; position: relative; cursor: pointer; transition: background 0.2s; &.active { background: #2563EB; } &::after { content: ''; position: absolute; width: 20px; height: 20px; background: white; border-radius: 50%; top: 2px; left: 2px; transition: transform 0.2s; } &.active::after { transform: translateX(20px); } }

.pp-schedule-row { display: flex; align-items: center; gap: 12px; padding: 16px; background: #EFF6FF; border: 1px solid #2563EB; border-radius: 8px; margin-bottom: 16px; }
.pp-schedule-icon { width: 40px; height: 40px; background: #2563EB; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: white; font-size: 20px; }
.pp-schedule-info { flex: 1; }
.pp-schedule-title { font-size: 14px; font-weight: 600; color: #374151; }
.pp-schedule-desc { font-size: 12px; color: #6B7280; margin-top: 2px; }
.pp-schedule-time { font-size: 14px; font-weight: 600; color: #2563EB; }

.pp-form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
.pp-form-group { display: flex; flex-direction: column; }
.pp-form-label { font-size: 13px; font-weight: 500; color: #374151; margin-bottom: 6px; }
.pp-form-input { width: 100%; }
.pp-port-input ::v-deep input { text-align: left; padding-left: 15px; -moz-appearance: textfield; &::-webkit-outer-spin-button, &::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; } }
.pp-time-input { text-align: left; }
.pp-form-hint { font-size: 11px; color: #9CA3AF; margin-top: 4px; }

.pp-config-actions { display: flex; gap: 12px; margin-top: 24px; }

.pp-email-preview { max-width: 680px; margin: 0 auto; background: white; border: 1px solid #E5E7EB; border-radius: 8px; padding: 24px; font-size: 14px; line-height: 1.6; overflow: auto;
  & >>> h1 { text-align: center; font-size: 24px; font-weight: 700; color: #2563EB; padding-bottom: 10px; border-bottom: 2px solid #2563EB; margin-bottom: 16px; }
  & >>> h2 { font-size: 16px; font-weight: 600; color: #374151; margin-bottom: 8px; padding-bottom: 8px; border-bottom: 2px solid #2563EB; }
  & >>> table { width: 100%; border-collapse: collapse; margin: 12px 0; th, td { padding: 8px; border: 1px solid #E5E7EB; text-align: left; } th { background: #F9FAFB; font-weight: 600; } }
  & >>> .stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; margin-bottom: 16px; .stat-item { background: #F9FAFB; border-radius: 8px; padding: 12px; text-align: center; .stat-val { font-size: 20px; font-weight: 700; } .stat-label { font-size: 11px; color: #6B7280; } } }
  & >>> .summary-bar { padding: 10px; background: #F9FAFB; border-radius: 6px; font-size: 12px; margin-top: 8px; }
}
</style>