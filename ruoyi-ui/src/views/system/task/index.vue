<template>
  <div class="pp-page-container">
    <!-- 顶部导航 -->
    <div class="pp-top-nav">
      <div class="pp-breadcrumb">
        <a @click="goTo('/dashboard')">首页</a>
        <span>/</span>
        <span class="current">任务管理</span>
      </div>
      <div class="pp-page-actions">
        <button class="pp-btn pp-btn-secondary" @click="handleExport">导出Excel</button>
        <button class="pp-btn pp-btn-primary" @click="handleAdd">+ 新增任务</button>
      </div>
    </div>

    <!-- 周选择器 -->
    <week-selector v-model="period" @change="loadData" />

    <!-- 统计卡片 -->
    <stat-cards :stats="filteredStats" />

    <!-- Tab切换 -->
    <div class="pp-task-tabs">
      <div class="pp-task-tab" :class="{ active: activeTab === 'cycle' }" @click="switchTab('cycle')">周期性任务</div>
      <div class="pp-task-tab" :class="{ active: activeTab === 'once' }" @click="switchTab('once')">一次性任务</div>
    </div>

    <!-- 任务内容 -->
    <div class="pp-task-content" v-loading="loading">
      <!-- 任务卡片列表 -->
      <task-card
        v-for="task in currentTasks"
        :key="task.id"
        :task="task"
        :persons="persons"
        :instances="getTaskInstances(task.id)"
        :period="period"
        @complete="handleComplete"
        @uncomplete="handleUncomplete"
        @createAndComplete="handleCreateAndComplete"
        @completeAll="handleCompleteAll"
        @uncompleteAll="handleUncompleteAll"
        @completeDay="handleCompleteDay"
        @uncompleteDay="handleUncompleteDay"
        @edit="handleUpdate"
        @delete="handleDelete"
      />

      <!-- 空状态 -->
      <div class="pp-empty-state" v-if="currentTasks.length === 0">
        <div class="pp-empty-icon">📋</div>
        <div class="pp-empty-text">暂无任务数据</div>
      </div>

      <!-- 完成统计区域 -->
      <div class="pp-stats-section" v-if="currentTasks.length > 0">
        <div class="pp-stats-header">
          <div class="pp-stats-title">📊 完成统计</div>
        </div>

        <!-- 统计子Tab -->
        <div class="pp-stats-tabs">
          <div class="pp-stats-tab" :class="{ active: statsTab === 'task' }" @click="statsTab = 'task'">按任务查看</div>
          <div class="pp-stats-tab" :class="{ active: statsTab === 'person' }" @click="statsTab = 'person'">按人员查看</div>
        </div>

        <!-- 按任务查看：矩阵表格 -->
        <matrix-table
          v-if="statsTab === 'task'"
          :tasks="currentTasks"
          :persons="persons"
          :instances="filteredInstances"
          @complete="handleComplete"
          @uncomplete="handleUncomplete"
        />

        <!-- 按人员查看 -->
        <div class="pp-person-stats" v-if="statsTab === 'person'">
          <div class="pp-person-stats-grid">
            <div class="pp-person-stats-item" :class="{ 'pp-person-incomplete': stat.rate < 100 }" v-for="stat in currentPersonStats" :key="stat.personId">
              <div class="pp-avatar-sm" :style="avatarStyle(stat.personName)">{{ stat.personName.charAt(0) }}</div>
              <div class="pp-person-stats-info">
                <div class="pp-person-stats-name">{{ stat.personName }}</div>
                <div class="pp-person-stats-numbers">
                  <span class="pp-done">{{ stat.completed }} 完成</span>
                  <span class="pp-pending">{{ stat.pending }} 待完成</span>
                </div>
                <div class="pp-person-stats-rate" :style="{ color: getRateColor(stat.rate) }">
                  {{ stat.rate }}% 完成率
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body custom-class="pp-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
            <el-option label="周期性" value="周期性" />
            <el-option label="一次性" value="一次性" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === '周期性'" label="周期" prop="cycle">
          <el-select v-model="form.cycle" placeholder="请选择周期">
            <el-option label="每日" value="每日" />
            <el-option label="每工作日" value="每工作日" />
            <el-option label="每周" value="每周" />
            <el-option label="每月" value="每月" />
            <el-option label="每季" value="每季" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === '周期性'" label="截止时间" prop="deadlineTime">
          <el-time-picker v-model="form.deadlineTime" placeholder="选择截止时间" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item v-if="form.type === '一次性'" label="截止日期" prop="deadlineDate">
          <el-date-picker v-model="form.deadlineDate" type="datetime" placeholder="选择截止日期时间" value-format="yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-form-item v-if="form.cycle === '每周'" label="截止星期" prop="deadlineWeekday">
          <el-select v-model="form.deadlineWeekday" placeholder="请选择星期">
            <el-option label="周一" value="一" />
            <el-option label="周二" value="二" />
            <el-option label="周三" value="三" />
            <el-option label="周四" value="四" />
            <el-option label="周五" value="五" />
            <el-option label="周六" value="六" />
            <el-option label="周日" value="日" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.cycle === '每月'" label="截止日期" prop="deadlineDay">
          <el-input-number v-model="form.deadlineDay" :min="1" :max="28" />
        </el-form-item>
        <el-form-item v-if="form.cycle === '每季'" label="截止时间" prop="deadlineTime">
          <el-time-picker v-model="form.deadlineTime" placeholder="选择截止时间" value-format="HH:mm:ss" />
          <div class="el-form-item__tip" style="color: #909399; font-size: 12px; margin-top: 4px;">截止日期自动为季度最后一天</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="启用">启用</el-radio>
            <el-radio label="禁用">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="pp-dialog-footer">
        <button class="pp-btn pp-btn-secondary" @click="cancel">取消</button>
        <button class="pp-btn pp-btn-primary" @click="submitForm">确定</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, getTask, addTask, updateTask, delTask, listInstance, completeTask, uncompleteTask, getPeriodStatistics, getOnceTaskStatistics, addInstance } from '@/api/system/task'
import { listPersonAll } from '@/api/system/person'
import { getCurrentWeek, getWeekDays, getWeekRange, isQuarterEndWeek } from '@/utils/date'
import WeekSelector from './components/WeekSelector.vue'
import StatCards from './components/StatCards.vue'
import TaskCard from './components/TaskCard.vue'
import MatrixTable from './components/MatrixTable.vue'

export default {
  name: 'Task',
  components: { WeekSelector, StatCards, TaskCard, MatrixTable },
  data() {
    return {
      period: getCurrentWeek(),
      activeTab: 'cycle',
      statsTab: 'task',
      loading: false,

      // 周期性任务统计数据
      cycleStats: { completed: 0, pending: 0, overdue: 0, rate: 0 },
      cycleInstances: [],
      cyclePersonStats: [], // 人员统计数据
      // 一次性任务统计数据
      onceStats: { completed: 0, pending: 0, overdue: 0, rate: 0 },
      onceInstances: [],
      oncePersonStats: [], // 一次性任务人员统计数据

      // 任务数据
      cycleTasks: [],
      onceTasks: [],

      // 人员数据
      persons: [],

      // 对话框
      title: '',
      open: false,
      form: {},
      rules: {
        name: [{ required: true, message: '任务名称不能为空', trigger: 'blur' }],
        type: [{ required: true, message: '类型不能为空', trigger: 'change' }]
      },

      // 头像颜色
      avatarColors: [
        'linear-gradient(135deg, #2563EB, #3B82F6)',
        'linear-gradient(135deg, #10B981, #34D399)',
        'linear-gradient(135deg, #F59E0B, #FBBF24)',
        'linear-gradient(135deg, #6366F1, #818CF8)',
        'linear-gradient(135deg, #EF4444, #F87171)',
        'linear-gradient(135deg, #8B5CF6, #A78BFA)'
      ]
    }
  },
  computed: {
    currentTasks() {
      return this.activeTab === 'cycle' ? this.cycleTasks : this.onceTasks
    },
    instances() {
      return this.activeTab === 'cycle' ? this.cycleInstances : this.onceInstances
    },
    filteredInstances() {
      return this.instances // 已经是当前 tab 的数据了
    },
    filteredStats() {
      return this.activeTab === 'cycle' ? this.cycleStats : this.onceStats
    },
    currentPersonStats() {
      return this.activeTab === 'cycle' ? this.cyclePersonStats : this.oncePersonStats
    }
  },
  created() {
    this.loadPersons()
    this.loadData()
  },
  methods: {
    async loadPersons() {
      const res = await listPersonAll()
      this.persons = (res.data || []).filter(p => p.status === '在职')
    },

    async loadData() {
      this.loading = true
      try {
        // 获取任务列表
        const taskRes = await listTask({ status: '启用' })
        const allTasks = taskRes.rows || []
        // 季度任务只在季度末周显示
        this.cycleTasks = allTasks.filter(t => t.type === '周期性').filter(t => {
          if (t.cycle === '每季') {
            return isQuarterEndWeek(this.period)
          }
          return true
        })

        // 获取周期性任务统计数据（依赖 period）
        const cycleRes = await getPeriodStatistics(this.period)
        const cycleData = cycleRes.data || {}
        this.cycleStats = {
          completed: cycleData.completed || 0,
          pending: cycleData.pending || 0,
          overdue: cycleData.overdue || 0,
          rate: cycleData.rate || 0
        }
        this.cycleInstances = cycleData.instances || []
        this.cyclePersonStats = cycleData.personStats || []

        // 获取一次性任务统计数据（不依赖 period）
        const onceRes = await getOnceTaskStatistics()
        const onceData = onceRes.data || {}

        // 根据当前周期过滤一次性任务：只显示截止日期在本周范围内的任务
        const weekRange = getWeekRange(this.period)
        const weekStart = weekRange.monday
        const weekEnd = weekRange.sunday
        weekEnd.setHours(23, 59, 59, 999) // 设置为周日最后一刻

        // 过滤一次性任务：截止日期在本周范围内
        const allOnceTasks = allTasks.filter(t => t.type === '一次性')
        this.onceTasks = allOnceTasks.filter(t => {
          if (!t.deadlineDate) return false // 没有截止日期的不显示
          const deadline = new Date(t.deadlineDate)
          return deadline >= weekStart && deadline <= weekEnd
        })

        // 过滤一次性任务实例：只保留当前显示任务的实例
        const visibleTaskIds = this.onceTasks.map(t => t.id)
        const filteredInstances = (onceData.instances || []).filter(i => visibleTaskIds.includes(i.taskId))

        this.onceStats = {
          completed: filteredInstances.filter(i => i.completed === 1).length,
          pending: filteredInstances.filter(i => i.completed === 0).length,
          overdue: 0,
          rate: filteredInstances.length > 0 ? Math.round(filteredInstances.filter(i => i.completed === 1).length * 100 / filteredInstances.length) : 0
        }
        this.onceInstances = filteredInstances

        // 计算一次性任务的人员统计（基于过滤后的实例）
        const oncePersonStatsMap = new Map()
        this.persons.forEach(p => {
          const personInstances = filteredInstances.filter(i => i.personId === p.id)
          if (personInstances.length > 0) {
            const completed = personInstances.filter(i => i.completed === 1).length
            const pending = personInstances.filter(i => i.completed === 0).length
            const rate = Math.round(completed * 100 / personInstances.length)
            oncePersonStatsMap.set(p.id, {
              personId: p.id,
              personName: p.name,
              completed,
              pending,
              overdue: 0,
              rate
            })
          }
        })
        this.oncePersonStats = Array.from(oncePersonStatsMap.values())
      } finally {
        this.loading = false
      }
    },

    getTaskInstances(taskId) {
      return this.instances.filter(i => i.taskId === taskId)
    },

    getPersonCompleted(personId) {
      return this.filteredInstances.filter(i => i.personId === personId && i.completed === 1).length
    },

    getPersonPending(personId) {
      return this.filteredInstances.filter(i => i.personId === personId && i.completed === 0).length
    },

    getPersonRate(personId) {
      const personInstances = this.filteredInstances.filter(i => i.personId === personId)
      const total = personInstances.length
      const completed = personInstances.filter(i => i.completed === 1).length
      return total > 0 ? Math.round(completed * 100 / total) : 0
    },

    getPersonRateColor(personId) {
      const rate = this.getPersonRate(personId)
      return rate >= 80 ? '#10B981' : rate >= 50 ? '#F59E0B' : '#EF4444'
    },

    getRateColor(rate) {
      return rate >= 80 ? '#10B981' : rate >= 50 ? '#F59E0B' : '#EF4444'
    },

    avatarStyle(name) {
      if (!name) return { background: '#D1D5DB' }
      const idx = name.charCodeAt(0) % this.avatarColors.length
      return { background: this.avatarColors[idx] }
    },

    switchTab(tab) {
      this.activeTab = tab
    },

    async handleComplete(instance) {
      try {
        await this.$modal.confirm('确认完成该任务？')
        await completeTask(instance.id)
        this.$modal.msgSuccess('已完成')
        await this.loadData()
      } catch (e) {
        // 用户取消或失败
      }
    },

    async handleCreateAndComplete(data) {
      try {
        await this.$modal.confirm('确认完成该任务？')
        // 格式化日期为 yyyy-MM-dd HH:mm:ss
        const now = new Date()
        const deadlineStr = this.formatDate(now)
        // 先创建 instance
        // 对于每日任务，period 使用具体日期；对于每周/每月，使用周标识
        const instancePeriod = data.date || this.period
        const newInstance = {
          taskId: data.taskId,
          taskName: data.taskName,
          personId: data.personId,
          personName: data.personName,
          period: instancePeriod,
          deadline: deadlineStr,
          completed: 0
        }
        const res = await addInstance(newInstance)
        if (res.code === 200) {
          // 然后标记完成
          await completeTask(res.data) // addInstance 返回新创建的 id
          this.$modal.msgSuccess('已完成')
          await this.loadData()
        }
      } catch (e) {
        // 用户取消或失败
      }
    },

    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },

    async handleCompleteAll(data) {
      try {
        await this.$modal.confirm('确认完成所有未完成的任务？')
        const now = new Date()
        const deadlineStr = this.formatDate(now)
        const { taskId, taskName, persons, instances, cycle } = data

        // 对于每日/每工作日任务，需要为每个人员每个日期创建 instance
        if (cycle === '每日' || cycle === '每工作日') {
          const weekDays = getWeekDays(this.period)
          const days = cycle === '每工作日' ? weekDays.slice(0, 5) : weekDays
          const promises = []

          // 遍历每个日期和每个人员
          days.forEach(day => {
            persons.forEach(person => {
              // 查找该人员该日期的 instance
              const existingInstance = instances.find(i => i.personId === person.id && i.period === day.date)
              if (existingInstance) {
                // 如果存在但未完成，完成它
                if (existingInstance.completed === 0) {
                  promises.push(completeTask(existingInstance.id))
                }
              } else {
                // 如果不存在，创建并完成
                const newInstance = {
                  taskId: taskId,
                  taskName: taskName,
                  personId: person.id,
                  personName: person.name,
                  period: day.date,
                  deadline: deadlineStr,
                  completed: 0
                }
                promises.push(addInstance(newInstance).then(res => {
                  if (res.code === 200) {
                    return completeTask(res.data)
                  }
                }))
              }
            })
          })

          await Promise.all(promises)
        } else {
          // 对于每周/每月任务，按人员处理
          const pendingInstances = instances.filter(i => i.completed === 0)
          const noInstancePersons = persons.filter(p => !instances.find(i => i.personId === p.id))

          // 并行处理：完成已有 instance 的
          const completePromises = pendingInstances.map(inst => completeTask(inst.id))

          // 并行处理：创建并完成没有 instance 的
          const createAndCompletePromises = noInstancePersons.map(person => {
            const newInstance = {
              taskId: taskId,
              taskName: taskName,
              personId: person.id,
              personName: person.name,
              period: this.period,
              deadline: deadlineStr,
              completed: 0
            }
            return addInstance(newInstance).then(res => {
              if (res.code === 200) {
                return completeTask(res.data)
              }
            })
          })

          await Promise.all([...completePromises, ...createAndCompletePromises])
        }

        this.$modal.msgSuccess('全部已完成')
        await this.loadData()
      } catch (e) {
        // 用户取消或失败
      }
    },

    async handleUncomplete(instance) {
      try {
        await this.$modal.confirm('确认取消完成该任务？')
        await uncompleteTask(instance.id)
        this.$modal.msgSuccess('已取消完成')
        await this.loadData()
      } catch (e) {
        // 用户取消或失败
      }
    },

    async handleUncompleteAll(data) {
      try {
        await this.$modal.confirm('确认取消所有已完成的任务？')
        const { instances } = data
        // 找出所有已完成的 instance
        const completedInstances = instances.filter(i => i.completed === 1)
        // 并行处理：取消完成
        const uncompletePromises = completedInstances.map(inst => uncompleteTask(inst.id))
        await Promise.all(uncompletePromises)
        this.$modal.msgSuccess('已取消全部完成')
        await this.loadData()
      } catch (e) {
        // 用户取消或失败
      }
    },

    async handleCompleteDay(data) {
      try {
        await this.$modal.confirm(`确认完成 ${data.date} 所有人员的任务？`)
        const now = new Date()
        const deadlineStr = this.formatDate(now)
        const { taskId, taskName, instances, noInstancePersons, date } = data

        // 并行处理：完成已有 instance 的
        const completePromises = instances.map(inst => completeTask(inst.id))

        // 并行处理：创建并完成没有 instance 的
        const createAndCompletePromises = noInstancePersons.map(person => {
          const newInstance = {
            taskId: taskId,
            taskName: taskName,
            personId: person.id,
            personName: person.name,
            period: date,
            deadline: deadlineStr,
            completed: 0
          }
          return addInstance(newInstance).then(res => {
            if (res.code === 200) {
              return completeTask(res.data)
            }
          })
        })

        await Promise.all([...completePromises, ...createAndCompletePromises])
        this.$modal.msgSuccess('已完成')
        await this.loadData()
      } catch (e) {
        // 用户取消或失败
      }
    },

    async handleUncompleteDay(data) {
      try {
        await this.$modal.confirm(`确认取消 ${data.date} 所有已完成任务？`)
        const { instances } = data
        // 并行处理：取消完成
        const uncompletePromises = instances.map(inst => uncompleteTask(inst.id))
        await Promise.all(uncompletePromises)
        this.$modal.msgSuccess('已取消完成')
        await this.loadData()
      } catch (e) {
        // 用户取消或失败
      }
    },

    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        type: '周期性',
        cycle: '每日',
        deadlineTime: '18:00',
        deadlineDay: 5,
        deadlineWeekday: '五',
        deadlineDate: undefined,
        deadlineQuarterMonth: undefined,
        deadlineQuarterDay: undefined,
        status: '启用',
        description: undefined
      }
      this.resetForm('form')
    },

    cancel() {
      this.open = false
      this.reset()
    },

    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增任务'
    },

    handleUpdate(row) {
      this.reset()
      getTask(row.id).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改任务'
      })
    },

    handleTypeChange() {
      this.form.cycle = undefined
    },

    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id) {
            updateTask(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.loadData()
            })
          } else {
            addTask(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.loadData()
            })
          }
        }
      })
    },

    handleDelete(row) {
      this.$modal.confirm('是否确认删除该任务？').then(() => delTask(row.id)).then(() => {
        this.loadData()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },

    handleExport() {
      this.download('system/task/export', { period: this.period }, `task_${this.period}.xlsx`)
    },

    goTo(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/styles/ppmanage.scss";

.pp-page-container {
  background: white;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  overflow: hidden;
}

.pp-top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #E5E7EB;
  background: #F9FAFB;
}

.pp-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6B7280;
  a { color: #6B7280; cursor: pointer; &:hover { color: #2563EB; } }
  .current { color: #1F2937; font-weight: 500; }
}

.pp-page-actions { display: flex; gap: 8px; }

/* Tab切换 */
.pp-task-tabs {
  display: flex;
  background: white;
  border-bottom: 1px solid #E5E7EB;
}

.pp-task-tab {
  padding: 14px 24px;
  font-size: 14px;
  font-weight: 500;
  color: #6B7280;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  &:hover { color: #374151; }
  &.active {
    color: #2563EB;
    border-bottom-color: #2563EB;
    background: #EFF6FF;
  }
}

/* 任务内容 */
.pp-task-content {
  padding: 24px;
}

/* 空状态 */
.pp-empty-state {
  text-align: center;
  padding: 60px 20px;
}

.pp-empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.pp-empty-text {
  font-size: 14px;
  color: #9CA3AF;
}

/* 统计区域 */
.pp-stats-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #E5E7EB;
}

.pp-stats-header {
  margin-bottom: 20px;
}

.pp-stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

/* 统计子Tab */
.pp-stats-tabs {
  display: flex;
  gap: 4px;
  background: #F3F4F6;
  padding: 4px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.pp-stats-tab {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  color: #6B7280;
  border-radius: 4px;
  cursor: pointer;
  &:hover { color: #374151; }
  &.active {
    background: white;
    color: #2563EB;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  }
}

/* 按人员查看 */
.pp-person-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.pp-person-stats-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #F9FAFB;
  border-radius: 8px;
  border: 1px solid #E5E7EB;

  &.pp-person-incomplete {
    background: #FEF3C7;
    border-color: #F59E0B;
  }
}

.pp-avatar-sm {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: white;
  flex-shrink: 0;
}

.pp-person-stats-info {
  flex: 1;
}

.pp-person-stats-name {
  font-size: 14px;
  font-weight: 500;
  color: #1F2937;
}

.pp-person-stats-numbers {
  font-size: 12px;
  color: #6B7280;
  margin-top: 4px;
}

.pp-done { color: #10B981; }
.pp-pending { color: #F59E0B; margin-left: 8px; }

.pp-person-stats-rate {
  font-size: 14px;
  font-weight: 600;
  margin-top: 6px;
}

.pp-dialog-footer { display: flex; justify-content: flex-end; gap: 8px; }
</style>