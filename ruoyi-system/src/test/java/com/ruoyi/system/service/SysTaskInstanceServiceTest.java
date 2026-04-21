package com.ruoyi.system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.system.domain.SysTaskInstance;
import com.ruoyi.system.mapper.SysTaskInstanceMapper;
import com.ruoyi.system.service.impl.SysTaskInstanceServiceImpl;

/**
 * SysTaskInstanceService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysTaskInstanceServiceTest {

    @Mock
    private SysTaskInstanceMapper instanceMapper;

    @InjectMocks
    private SysTaskInstanceServiceImpl instanceService;

    private SysTaskInstance testInstance;

    @BeforeEach
    void setUp() {
        testInstance = new SysTaskInstance();
        testInstance.setId(1L);
        testInstance.setTaskId(1L);
        testInstance.setPersonId(1L);
        testInstance.setPeriod("2026-W16");
        testInstance.setCompleted(0);
        testInstance.setDeadline(new Date(System.currentTimeMillis() + 3600000)); // 1小时后
    }

    @Test
    @DisplayName("查询实例列表 - 成功")
    void testSelectInstanceList() {
        // Given
        List<SysTaskInstance> expectedList = Arrays.asList(testInstance);
        when(instanceMapper.selectInstanceList(any(SysTaskInstance.class))).thenReturn(expectedList);

        // When
        List<SysTaskInstance> result = instanceService.selectInstanceList(new SysTaskInstance());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(instanceMapper).selectInstanceList(any(SysTaskInstance.class));
    }

    @Test
    @DisplayName("通过ID查询实例 - 存在")
    void testSelectInstanceByIdExists() {
        // Given
        when(instanceMapper.selectInstanceById(1L)).thenReturn(testInstance);

        // When
        SysTaskInstance result = instanceService.selectInstanceById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("通过ID查询实例 - 不存在")
    void testSelectInstanceByIdNotExists() {
        // Given
        when(instanceMapper.selectInstanceById(999L)).thenReturn(null);

        // When
        SysTaskInstance result = instanceService.selectInstanceById(999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("完成任务 - 按时完成")
    void testCompleteTaskOnTime() {
        // Given
        Date deadline = new Date(System.currentTimeMillis() + 3600000); // 1小时后
        testInstance.setDeadline(deadline);
        when(instanceMapper.selectInstanceById(1L)).thenReturn(testInstance);
        when(instanceMapper.completeTask(any(SysTaskInstance.class))).thenReturn(1);

        // When
        int result = instanceService.completeTask(1L, "已完成", "admin");

        // Then
        assertEquals(1, result);
        verify(instanceMapper).completeTask(argThat(instance ->
            instance.getCompleted() == 1 &&
            instance.getOnTime() == 1 && // 按时完成
            instance.getCompleteTime() != null
        ));
    }

    @Test
    @DisplayName("完成任务 - 超期完成")
    void testCompleteTaskOverdue() {
        // Given
        Date pastDeadline = new Date(System.currentTimeMillis() - 3600000); // 1小时前
        testInstance.setDeadline(pastDeadline);
        when(instanceMapper.selectInstanceById(1L)).thenReturn(testInstance);
        when(instanceMapper.completeTask(any(SysTaskInstance.class))).thenReturn(1);

        // When
        int result = instanceService.completeTask(1L, "已完成", "admin");

        // Then
        assertEquals(1, result);
        verify(instanceMapper).completeTask(argThat(instance ->
            instance.getCompleted() == 1 &&
            instance.getOnTime() == 0 // 超期完成
        ));
    }

    @Test
    @DisplayName("完成任务 - 无截止时间默认按时")
    void testCompleteTaskNoDeadline() {
        // Given
        testInstance.setDeadline(null);
        when(instanceMapper.selectInstanceById(1L)).thenReturn(testInstance);
        when(instanceMapper.completeTask(any(SysTaskInstance.class))).thenReturn(1);

        // When
        int result = instanceService.completeTask(1L, "已完成", "admin");

        // Then
        assertEquals(1, result);
        verify(instanceMapper).completeTask(argThat(instance ->
            instance.getCompleted() == 1 &&
            instance.getOnTime() == 1 // 无截止时间默认按时
        ));
    }

    @Test
    @DisplayName("取消完成任务")
    void testUncompleteTask() {
        // Given
        when(instanceMapper.uncompleteTask(any(SysTaskInstance.class))).thenReturn(1);

        // When
        int result = instanceService.uncompleteTask(1L, "admin");

        // Then
        assertEquals(1, result);
        verify(instanceMapper).uncompleteTask(argThat(instance ->
            instance.getCompleted() == 0 &&
            instance.getCompleteTime() == null &&
            instance.getOnTime() == null &&
            instance.getUpdateBy().equals("admin")
        ));
    }

    @Test
    @DisplayName("新增任务实例 - 默认未完成")
    void testInsertInstanceDefaultCompleted() {
        // Given
        SysTaskInstance newInstance = new SysTaskInstance();
        newInstance.setTaskId(1L);
        newInstance.setPersonId(1L);
        newInstance.setPeriod("2026-W16");
        when(instanceMapper.insertInstance(any(SysTaskInstance.class))).thenReturn(1);

        // When
        int result = instanceService.insertInstance(newInstance);

        // Then
        assertEquals(1, result);
        assertEquals(0, newInstance.getCompleted()); // 应默认设置为未完成
    }

    @Test
    @DisplayName("新增任务实例 - 保留已有状态")
    void testInsertInstanceWithCompleted() {
        // Given
        SysTaskInstance newInstance = new SysTaskInstance();
        newInstance.setTaskId(1L);
        newInstance.setPersonId(1L);
        newInstance.setPeriod("2026-W16");
        newInstance.setCompleted(1);
        when(instanceMapper.insertInstance(any(SysTaskInstance.class))).thenReturn(1);

        // When
        int result = instanceService.insertInstance(newInstance);

        // Then
        assertEquals(1, result);
        assertEquals(1, newInstance.getCompleted()); // 应保留已有状态
    }

    @Test
    @DisplayName("计算完成率 - 正常情况")
    void testGetCompletionRateNormal() {
        // Given
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 10L);
        stats.put("completed_count", 8L);
        when(instanceMapper.countCompletionRate(anyMap())).thenReturn(stats);

        // When
        int rate = instanceService.getCompletionRate(1L, "2026-W16");

        // Then
        assertEquals(80, rate); // 8/10 = 80%
    }

    @Test
    @DisplayName("计算完成率 - 无任务")
    void testGetCompletionRateNoTasks() {
        // Given
        when(instanceMapper.countCompletionRate(anyMap())).thenReturn(null);

        // When
        int rate = instanceService.getCompletionRate(1L, "2026-W16");

        // Then
        assertEquals(0, rate);
    }

    @Test
    @DisplayName("计算完成率 - 全部未完成")
    void testGetCompletionRateNoneCompleted() {
        // Given
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 10L);
        stats.put("completed_count", null);
        when(instanceMapper.countCompletionRate(anyMap())).thenReturn(stats);

        // When
        int rate = instanceService.getCompletionRate(1L, "2026-W16");

        // Then
        assertEquals(0, rate);
    }

    @Test
    @DisplayName("计算完成率 - total为0")
    void testGetCompletionRateZeroTotal() {
        // Given
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 0L);
        stats.put("completed_count", 0L);
        when(instanceMapper.countCompletionRate(anyMap())).thenReturn(stats);

        // When
        int rate = instanceService.getCompletionRate(1L, "2026-W16");

        // Then
        assertEquals(0, rate);
    }

    @Test
    @DisplayName("删除实例")
    void testDeleteInstanceById() {
        // Given
        when(instanceMapper.deleteInstanceById(1L)).thenReturn(1);

        // When
        int result = instanceService.deleteInstanceById(1L);

        // Then
        assertEquals(1, result);
        verify(instanceMapper).deleteInstanceById(1L);
    }

    @Test
    @DisplayName("查询某人某周期的任务实例")
    void testSelectByPersonAndPeriod() {
        // Given
        List<SysTaskInstance> expectedList = Arrays.asList(testInstance);
        when(instanceMapper.selectByPersonAndPeriod(anyMap())).thenReturn(expectedList);

        // When
        List<SysTaskInstance> result = instanceService.selectByPersonAndPeriod(1L, "2026-W16");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(instanceMapper).selectByPersonAndPeriod(argThat(params ->
            params.get("personId").equals(1L) &&
            params.get("period").equals("2026-W16")
        ));
    }

    @Test
    @DisplayName("获取周期性任务统计 - 正常情况")
    void testGetCycleTaskStats() {
        // Given
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("total", 20L);
        mockResult.put("completedCount", 15L);
        when(instanceMapper.countCycleTaskStats(anyMap())).thenReturn(mockResult);

        // When
        Map<String, Object> stats = instanceService.getCycleTaskStats("2026-W16", null, null);

        // Then
        assertEquals(20L, stats.get("total"));
        assertEquals(15L, stats.get("completedCount"));
        assertEquals(75, stats.get("rate")); // 15/20 = 75%
    }

    @Test
    @DisplayName("获取周期性任务统计 - 无数据")
    void testGetCycleTaskStatsNoData() {
        // Given
        when(instanceMapper.countCycleTaskStats(anyMap())).thenReturn(null);

        // When
        Map<String, Object> stats = instanceService.getCycleTaskStats("2026-W16", null, null);

        // Then
        assertEquals(0, stats.get("total"));
        assertEquals(0, stats.get("completedCount"));
        assertEquals(0, stats.get("rate"));
    }
}