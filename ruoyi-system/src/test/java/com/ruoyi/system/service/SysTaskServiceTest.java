package com.ruoyi.system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.system.domain.SysTask;
import com.ruoyi.system.mapper.SysTaskMapper;
import com.ruoyi.system.mapper.SysTaskInstanceMapper;
import com.ruoyi.system.service.impl.SysTaskServiceImpl;

/**
 * SysTaskService 单元测试
 * 使用 Mockito 模拟 Mapper 层
 */
@ExtendWith(MockitoExtension.class)
class SysTaskServiceTest {

    @Mock
    private SysTaskMapper taskMapper;

    @Mock
    private SysTaskInstanceMapper instanceMapper;

    @InjectMocks
    private SysTaskServiceImpl taskService;

    private SysTask testTask;

    @BeforeEach
    void setUp() {
        testTask = new SysTask();
        testTask.setId(1L);
        testTask.setName("日报提交");
        testTask.setType("周期性");
        testTask.setCycle("每日");
        testTask.setStatus("启用");
    }

    @Test
    @DisplayName("查询任务列表 - 成功")
    void testSelectTaskList() {
        // Given
        List<SysTask> expectedTasks = Arrays.asList(testTask);
        when(taskMapper.selectTaskList(any(SysTask.class))).thenReturn(expectedTasks);

        // When
        List<SysTask> result = taskService.selectTaskList(new SysTask());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("日报提交", result.get(0).getName());
        verify(taskMapper, times(1)).selectTaskList(any(SysTask.class));
    }

    @Test
    @DisplayName("查询所有启用的任务 - 成功")
    void testSelectActiveTaskAll() {
        // Given
        List<SysTask> expectedTasks = Arrays.asList(testTask);
        when(taskMapper.selectActiveTaskAll()).thenReturn(expectedTasks);

        // When
        List<SysTask> result = taskService.selectActiveTaskAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskMapper, times(1)).selectActiveTaskAll();
    }

    @Test
    @DisplayName("通过ID查询任务 - 存在")
    void testSelectTaskByIdExists() {
        // Given
        when(taskMapper.selectTaskById(1L)).thenReturn(testTask);

        // When
        SysTask result = taskService.selectTaskById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("日报提交", result.getName());
    }

    @Test
    @DisplayName("通过ID查询任务 - 不存在")
    void testSelectTaskByIdNotExists() {
        // Given
        when(taskMapper.selectTaskById(999L)).thenReturn(null);

        // When
        SysTask result = taskService.selectTaskById(999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("新增任务 - 默认状态为启用")
    void testInsertTaskWithDefaultStatus() {
        // Given
        SysTask newTask = new SysTask();
        newTask.setName("新任务");
        newTask.setType("周期性");
        newTask.setCycle("每周");
        // 不设置状态
        when(taskMapper.insertTask(any(SysTask.class))).thenReturn(1);

        // When
        int result = taskService.insertTask(newTask);

        // Then
        assertEquals(1, result);
        assertEquals("启用", newTask.getStatus()); // 应自动设置默认状态
        verify(taskMapper).insertTask(argThat(task ->
            task.getStatus().equals("启用")
        ));
    }

    @Test
    @DisplayName("新增任务 - 保留已有状态")
    void testInsertTaskWithExistingStatus() {
        // Given
        SysTask newTask = new SysTask();
        newTask.setName("新任务");
        newTask.setType("周期性");
        newTask.setStatus("禁用");
        when(taskMapper.insertTask(any(SysTask.class))).thenReturn(1);

        // When
        int result = taskService.insertTask(newTask);

        // Then
        assertEquals(1, result);
        assertEquals("禁用", newTask.getStatus()); // 应保留已有状态
    }

    @Test
    @DisplayName("修改任务")
    void testUpdateTask() {
        // Given
        testTask.setName("更新后的日报");
        when(taskMapper.updateTask(any(SysTask.class))).thenReturn(1);

        // When
        int result = taskService.updateTask(testTask);

        // Then
        assertEquals(1, result);
        verify(taskMapper).updateTask(testTask);
    }

    @Test
    @DisplayName("删除单个任务 - 应同时删除关联实例")
    void testDeleteTaskById() {
        // Given
        when(instanceMapper.deleteInstanceByTaskId(1L)).thenReturn(5);
        when(taskMapper.deleteTaskById(1L)).thenReturn(1);

        // When
        int result = taskService.deleteTaskById(1L);

        // Then
        assertEquals(1, result);
        verify(instanceMapper).deleteInstanceByTaskId(1L); // 应先删除实例
        verify(taskMapper).deleteTaskById(1L);
    }

    @Test
    @DisplayName("批量删除任务 - 应删除所有关联实例")
    void testDeleteTaskByIds() {
        // Given
        Long[] ids = {1L, 2L, 3L};
        when(instanceMapper.deleteInstanceByTaskId(anyLong())).thenReturn(1);
        when(taskMapper.deleteTaskByIds(ids)).thenReturn(3);

        // When
        int result = taskService.deleteTaskByIds(ids);

        // Then
        assertEquals(3, result);
        verify(instanceMapper, times(3)).deleteInstanceByTaskId(anyLong());
        verify(taskMapper).deleteTaskByIds(ids);
    }
}