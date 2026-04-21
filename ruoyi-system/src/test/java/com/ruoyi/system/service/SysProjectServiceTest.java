package com.ruoyi.system.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.domain.SysPersonProject;
import com.ruoyi.system.domain.SysProjectMilestoneCustom;
import com.ruoyi.system.mapper.SysProjectMapper;
import com.ruoyi.system.mapper.SysProjectMilestoneMapper;
import com.ruoyi.system.mapper.SysProjectMilestoneCustomMapper;
import com.ruoyi.system.mapper.SysPersonProjectMapper;
import com.ruoyi.system.service.impl.SysProjectServiceImpl;

/**
 * SysProjectService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysProjectServiceTest {

    @Mock
    private SysProjectMapper projectMapper;

    @Mock
    private SysProjectMilestoneMapper milestoneMapper;

    @Mock
    private SysProjectMilestoneCustomMapper customMilestoneMapper;

    @Mock
    private SysPersonProjectMapper personProjectMapper;

    @InjectMocks
    private SysProjectServiceImpl projectService;

    private SysProject testProject;

    @BeforeEach
    void setUp() {
        testProject = new SysProject();
        testProject.setId(1L);
        testProject.setName("测试项目");
        testProject.setCustomer("客户A");
        testProject.setStatus("进行中");
        testProject.setIsSubordinate("是");
        testProject.setProjectType("项目");
    }

    @Test
    @DisplayName("查询项目列表 - 成功")
    void testSelectProjectList() {
        // Given
        List<SysProject> expectedList = Arrays.asList(testProject);
        when(projectMapper.selectProjectList(any(SysProject.class))).thenReturn(expectedList);

        // When
        List<SysProject> result = projectService.selectProjectList(new SysProject());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectMapper).selectProjectList(any(SysProject.class));
    }

    @Test
    @DisplayName("查询项目列表 - 外包项目查询自定义里程碑")
    void testSelectProjectListWithCustomMilestones() {
        // Given
        SysProject outsourceProject = new SysProject();
        outsourceProject.setId(2L);
        outsourceProject.setName("外包项目");
        outsourceProject.setProjectType("外包");
        outsourceProject.setStatus("进行中");

        List<SysProject> expectedList = Arrays.asList(outsourceProject);
        when(projectMapper.selectProjectList(any(SysProject.class))).thenReturn(expectedList);

        SysProjectMilestoneCustom customMilestone = new SysProjectMilestoneCustom();
        customMilestone.setId(1L);
        customMilestone.setProjectId(2L);
        customMilestone.setMilestoneName("里程碑A");
        when(customMilestoneMapper.selectByProjectId(2L)).thenReturn(Arrays.asList(customMilestone));

        // When
        List<SysProject> result = projectService.selectProjectList(new SysProject());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getCustomMilestones());
        assertEquals(1, result.get(0).getCustomMilestones().size());
        assertEquals("里程碑A", result.get(0).getCustomMilestones().get(0).getMilestoneName());
    }

    @Test
    @DisplayName("查询所有下辖项目（用于问题/风险关联）")
    void testSelectProjectAll() {
        // Given
        List<SysProject> expectedList = Arrays.asList(testProject);
        when(projectMapper.selectProjectList(any(SysProject.class))).thenReturn(expectedList);

        // When
        List<SysProject> result = projectService.selectProjectAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectMapper).selectProjectList(argThat(p ->
            p.getIsSubordinate().equals("是")
        ));
    }

    @Test
    @DisplayName("通过ID查询项目 - 存在")
    void testSelectProjectByIdExists() {
        // Given
        when(projectMapper.selectProjectById(1L)).thenReturn(testProject);
        when(personProjectMapper.selectByProjectId(1L)).thenReturn(new ArrayList<>());

        // When
        SysProject result = projectService.selectProjectById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试项目", result.getName());
    }

    @Test
    @DisplayName("通过ID查询项目 - 包含参与人员")
    void testSelectProjectByIdWithParticipants() {
        // Given
        when(projectMapper.selectProjectById(1L)).thenReturn(testProject);

        SysPersonProject pp = new SysPersonProject();
        pp.setPersonId(1L);
        pp.setProjectId(1L);
        when(personProjectMapper.selectByProjectId(1L)).thenReturn(Arrays.asList(pp));

        // When
        SysProject result = projectService.selectProjectById(1L);

        // Then
        assertNotNull(result);
        assertNotNull(result.getParticipants());
        assertEquals(1, result.getParticipants().size());
        assertEquals(1L, result.getParticipants().get(0));
    }

    @Test
    @DisplayName("通过ID查询项目 - 不存在")
    void testSelectProjectByIdNotExists() {
        // Given
        when(projectMapper.selectProjectById(999L)).thenReturn(null);

        // When
        SysProject result = projectService.selectProjectById(999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("校验项目名称唯一性 - 唯一")
    void testCheckNameUnique() {
        // Given
        when(projectMapper.checkNameUnique("新项目")).thenReturn(null);

        SysProject newProject = new SysProject();
        newProject.setName("新项目");

        // When
        boolean result = projectService.checkNameUnique(newProject);

        // Then
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @DisplayName("校验项目名称唯一性 - 不唯一")
    void testCheckNameUniqueNotUnique() {
        // Given
        SysProject existingProject = new SysProject();
        existingProject.setId(2L);
        existingProject.setName("测试项目");
        when(projectMapper.checkNameUnique("测试项目")).thenReturn(existingProject);

        SysProject newProject = new SysProject();
        newProject.setName("测试项目");

        // When
        boolean result = projectService.checkNameUnique(newProject);

        // Then
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @DisplayName("新增项目 - 普通项目")
    void testInsertProjectNormal() {
        // Given
        SysProject newProject = new SysProject();
        newProject.setName("新项目");
        newProject.setIsSubordinate("是");
        newProject.setProjectType("项目");
        newProject.setParticipants(Arrays.asList(1L, 2L));
        when(projectMapper.insertProject(any(SysProject.class))).thenReturn(1);
        when(personProjectMapper.insert(any(SysPersonProject.class))).thenReturn(1);

        // When
        int result = projectService.insertProject(newProject);

        // Then
        assertEquals(1, result);
        verify(projectMapper).insertProject(newProject);
        verify(personProjectMapper, times(2)).insert(any(SysPersonProject.class));
    }

    @Test
    @DisplayName("修改项目")
    void testUpdateProject() {
        // Given
        testProject.setName("更新项目");
        testProject.setParticipants(Arrays.asList(1L));
        when(personProjectMapper.deleteByProjectId(1L)).thenReturn(1);
        when(personProjectMapper.insert(any(SysPersonProject.class))).thenReturn(1);
        when(projectMapper.updateProject(any(SysProject.class))).thenReturn(1);

        // When
        int result = projectService.updateProject(testProject);

        // Then
        assertEquals(1, result);
        verify(personProjectMapper).deleteByProjectId(1L);
        verify(personProjectMapper).insert(any(SysPersonProject.class));
        verify(projectMapper).updateProject(testProject);
    }

    @Test
    @DisplayName("删除单个项目 - 应删除关联数据")
    void testDeleteProjectById() {
        // Given
        when(milestoneMapper.deleteByProjectId(1L)).thenReturn(1);
        when(personProjectMapper.deleteByProjectId(1L)).thenReturn(2);
        when(customMilestoneMapper.deleteByProjectId(1L)).thenReturn(0);
        when(projectMapper.deleteProjectById(1L)).thenReturn(1);

        // When
        int result = projectService.deleteProjectById(1L);

        // Then
        assertEquals(1, result);
        verify(milestoneMapper).deleteByProjectId(1L);
        verify(personProjectMapper).deleteByProjectId(1L);
        verify(customMilestoneMapper).deleteByProjectId(1L);
        verify(projectMapper).deleteProjectById(1L);
    }

    @Test
    @DisplayName("批量删除项目 - 应删除所有关联数据")
    void testDeleteProjectByIds() {
        // Given
        Long[] ids = {1L, 2L};
        when(milestoneMapper.deleteByProjectId(anyLong())).thenReturn(1);
        when(personProjectMapper.deleteByProjectId(anyLong())).thenReturn(1);
        when(customMilestoneMapper.deleteByProjectId(anyLong())).thenReturn(0);
        when(projectMapper.deleteProjectByIds(ids)).thenReturn(2);

        // When
        int result = projectService.deleteProjectByIds(ids);

        // Then
        assertEquals(2, result);
        verify(milestoneMapper, times(2)).deleteByProjectId(anyLong());
        verify(personProjectMapper, times(2)).deleteByProjectId(anyLong());
        verify(customMilestoneMapper, times(2)).deleteByProjectId(anyLong());
        verify(projectMapper).deleteProjectByIds(ids);
    }

    @Test
    @DisplayName("统计进行中的项目数量")
    void testCountActiveProject() {
        // Given
        when(projectMapper.countActiveProject()).thenReturn(5);

        // When
        int count = projectService.countActiveProject();

        // Then
        assertEquals(5, count);
        verify(projectMapper).countActiveProject();
    }

    @Test
    @DisplayName("统计已完成的项目数量")
    void testCountCompletedProject() {
        // Given
        when(projectMapper.countCompletedProject()).thenReturn(10);

        // When
        int count = projectService.countCompletedProject();

        // Then
        assertEquals(10, count);
        verify(projectMapper).countCompletedProject();
    }
}