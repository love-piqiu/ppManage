<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header">
        <span>周报生成</span>
      </div>
      <el-form :model="queryParams" :inline="true" size="small">
        <el-form-item label="开始日期">
          <el-date-picker v-model="queryParams.startDate" type="date" value-format="yyyy-MM-dd" placeholder="选择开始日期" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="queryParams.endDate" type="date" value-format="yyyy-MM-dd" placeholder="选择结束日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleGenerate">生成周报</el-button>
          <el-button type="success" icon="el-icon-message" @click="handleSend">发送邮件</el-button>
          <el-button type="info" icon="el-icon-document-copy" @click="handleCopy">复制内容</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px;" v-loading="loading">
      <div slot="header">
        <span>周报预览</span>
      </div>
      <div class="report-content" v-html="reportHtml"></div>
    </el-card>
  </div>
</template>

<script>
import { generateWeeklyHtml, sendWeeklyReport } from "@/api/report";

export default {
  name: "Report",
  data() {
    return {
      loading: false,
      queryParams: {
        startDate: undefined,
        endDate: undefined
      },
      reportHtml: ""
    };
  },
  created() {
    this.initDateRange();
    this.handleGenerate();
  },
  methods: {
    initDateRange() {
      const today = new Date();
      const dayOfWeek = today.getDay();
      const monday = new Date(today);
      monday.setDate(today.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1));
      const sunday = new Date(monday);
      sunday.setDate(monday.getDate() + 6);

      const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      };

      this.queryParams.startDate = formatDate(monday);
      this.queryParams.endDate = formatDate(sunday);
    },
    handleGenerate() {
      this.loading = true;
      generateWeeklyHtml(this.queryParams).then(response => {
        this.reportHtml = response.data;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    handleSend() {
      this.$modal.confirm('确认发送周报到指定邮箱？').then(() => {
        return sendWeeklyReport(this.queryParams);
      }).then(() => {
        this.$modal.msgSuccess("邮件发送成功");
      }).catch(() => {});
    },
    handleCopy() {
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = this.reportHtml;
      document.body.appendChild(tempDiv);
      const range = document.createRange();
      range.selectNodeContents(tempDiv);
      const selection = window.getSelection();
      selection.removeAllRanges();
      selection.addRange(range);
      try {
        document.execCommand('copy');
        this.$modal.msgSuccess("已复制到剪贴板");
      } catch (e) {
        this.$modal.msgError("复制失败");
      }
      selection.removeAllRanges();
      document.body.removeChild(tempDiv);
    }
  }
};
</script>

<style scoped>
.report-content {
  padding: 20px;
  background: #fff;
  min-height: 400px;
}
.report-content >>> h1 {
  color: #2563EB;
  border-bottom: 2px solid #2563EB;
  padding-bottom: 10px;
}
.report-content >>> h2 {
  color: #374151;
  margin-top: 30px;
}
.report-content >>> table {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
}
.report-content >>> th, .report-content >>> td {
  border: 1px solid #E5E7EB;
  padding: 10px;
  text-align: left;
}
.report-content >>> th {
  background: #F9FAFB;
  font-weight: 600;
}
</style>