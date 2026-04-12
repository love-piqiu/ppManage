<template>
  <div class="pp-stats-row">
    <div class="pp-stat-card success">
      <div class="pp-stat-value">{{ stats.completed }}</div>
      <div class="pp-stat-label">已完成</div>
    </div>
    <div class="pp-stat-card warning">
      <div class="pp-stat-value">{{ stats.pending }}</div>
      <div class="pp-stat-label">待完成</div>
    </div>
    <div class="pp-stat-card danger">
      <div class="pp-stat-value">{{ stats.overdue }}</div>
      <div class="pp-stat-label">已超期</div>
    </div>
    <div class="pp-stat-card info">
      <div class="pp-stat-value">{{ stats.rate }}%</div>
      <div class="pp-stat-label">完成率</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatCards',
  props: {
    stats: {
      type: Object,
      default: () => ({
        completed: 0,
        pending: 0,
        overdue: 0,
        rate: 0
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.pp-stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 20px 24px;
  background: white;
  border-bottom: 1px solid #E5E7EB;
}

.pp-stat-card {
  background: #F9FAFB;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  border-left: 4px solid #D1D5DB;

  &.success {
    border-left-color: #10B981;
    .pp-stat-value { color: #10B981; }
  }
  &.warning {
    border-left-color: #F59E0B;
    .pp-stat-value { color: #F59E0B; }
  }
  &.danger {
    border-left-color: #EF4444;
    .pp-stat-value { color: #EF4444; }
  }
  &.info {
    border-left-color: #2563EB;
    .pp-stat-value { color: #2563EB; }
  }
}

.pp-stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1F2937;
}

.pp-stat-label {
  font-size: 13px;
  color: #6B7280;
  margin-top: 4px;
}
</style>