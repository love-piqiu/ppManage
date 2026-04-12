<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header">
        <span>邮件配置</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" style="max-width: 600px;">
        <el-form-item label="SMTP服务器" prop="host">
          <el-input v-model="form.host" placeholder="如: smtp.qq.com" />
        </el-form-item>
        <el-form-item label="SMTP端口" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="发件人账号" prop="username">
          <el-input v-model="form.username" placeholder="发件人邮箱账号" />
        </el-form-item>
        <el-form-item label="授权码/密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="授权码或密码" show-password />
        </el-form-item>
        <el-form-item label="收件人地址" prop="recipientEmail">
          <el-input v-model="form.recipientEmail" placeholder="多个收件人用逗号分隔" />
        </el-form-item>
        <el-form-item label="发件人名称" prop="senderName">
          <el-input v-model="form.senderName" placeholder="发件人显示名称" />
        </el-form-item>
        <el-form-item label="邮件主题" prop="emailSubject">
          <el-input v-model="form.emailSubject" placeholder="邮件主题，{日期}会被替换为实际日期" />
        </el-form-item>
        <el-form-item label="发送日期" prop="sendDay">
          <el-select v-model="form.sendDay" placeholder="选择发送日期">
            <el-option label="周一" value="周一" />
            <el-option label="周二" value="周二" />
            <el-option label="周三" value="周三" />
            <el-option label="周四" value="周四" />
            <el-option label="周五" value="周五" />
            <el-option label="周六" value="周六" />
            <el-option label="周日" value="周日" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送时间" prop="sendTime">
          <el-time-picker v-model="form.sendTime" placeholder="选择发送时间" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item>
          <button class="pp-btn pp-btn-primary" @click="submitForm">保存配置</button>
          <button class="pp-btn pp-btn-success" @click="handleTest">发送测试邮件</button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getEmailConfig, updateEmailConfig, sendTestEmail } from "@/api/system/email";

export default {
  name: "EmailConfig",
  data() {
    return {
      form: {
        id: undefined,
        host: undefined,
        port: 465,
        username: undefined,
        password: undefined,
        recipientEmail: undefined,
        senderName: undefined,
        emailSubject: "项目周报 - {日期}",
        sendDay: "周五",
        sendTime: "17:00:00",
        enabled: 0
      },
      rules: {
        host: [{ required: true, message: "SMTP服务器不能为空", trigger: "blur" }],
        port: [{ required: true, message: "SMTP端口不能为空", trigger: "blur" }],
        username: [{ required: true, message: "发件人账号不能为空", trigger: "blur" }],
        password: [{ required: true, message: "授权码/密码不能为空", trigger: "blur" }],
        recipientEmail: [{ required: true, message: "收件人地址不能为空", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getConfig();
  },
  methods: {
    getConfig() {
      getEmailConfig().then(response => {
        if (response.data) {
          this.form = response.data;
          // 密码不返回，需要重新输入
          this.form.password = undefined;
        }
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateEmailConfig(this.form).then(() => {
            this.$modal.msgSuccess("保存成功");
          });
        }
      });
    },
    handleTest() {
      this.$modal.confirm('确认发送测试邮件？').then(() => {
        return sendTestEmail();
      }).then(() => {
        this.$modal.msgSuccess("测试邮件发送成功，请检查收件箱");
      }).catch(() => {});
    }
  }
};
</script>