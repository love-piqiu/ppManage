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
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.domain.SysPersonProject;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.mapper.SysPersonMapper;
import com.ruoyi.system.mapper.SysPersonProjectMapper;
import com.ruoyi.system.mapper.SysProjectMapper;
import com.ruoyi.system.service.impl.SysPersonServiceImpl;

/**
 * SysPersonService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class SysPersonServiceTest {

    @Mock
    private SysPersonMapper personMapper;

    @Mock
    private SysPersonProjectMapper personProjectMapper;

    @Mock
    private SysProjectMapper projectMapper;

    @InjectMocks
    private SysPersonServiceImpl personService;

    private SysPerson testPerson;

    @BeforeEach
    void setUp() {
        testPerson = new SysPerson();
        testPerson.setId(1L);
        testPerson.setName("张三");
        testPerson.setStatus("在职");
    }

    @Test
    @DisplayName("查询人员列表 - 成功")
    void testSelectPersonList() {
        // Given
        List<SysPerson> expectedList = Arrays.asList(testPerson);
        when(personMapper.selectPersonList(any(SysPerson.class))).thenReturn(expectedList);
        when(personProjectMapper.selectByPersonId(1L)).thenReturn(new ArrayList<>());

        // When
        List<SysPerson> result = personService.selectPersonList(new SysPerson());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personMapper).selectPersonList(any(SysPerson.class));
    }

    @Test
    @DisplayName("查询人员列表 - 包含在建项目")
    void testSelectPersonListWithProjects() {
        // Given
        List<SysPerson> expectedList = Arrays.asList(testPerson);
        when(personMapper.selectPersonList(any(SysPerson.class))).thenReturn(expectedList);

        // 模拟人员参与的项目
        SysPersonProject pp = new SysPersonProject();
        pp.setPersonId(1L);
        pp.setProjectId(1L);
        when(personProjectMapper.selectByPersonId(1L)).thenReturn(Arrays.asList(pp));

        // 模拟进行中的项目
        SysProject project = new SysProject();
        project.setId(1L);
        project.setName("测试项目");
        project.setCustomer("客户A");
        project.setStatus("进行中");
        when(projectMapper.selectProjectById(1L)).thenReturn(project);

        // When
        List<SysPerson> result = personService.selectPersonList(new SysPerson());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getProjects());
        assertEquals(1, result.get(0).getProjects().size());
        assertEquals("客户A-测试项目", result.get(0).getProjects().get(0));
    }

    @Test
    @DisplayName("查询所有人员 - 成功")
    void testSelectPersonAll() {
        // Given
        List<SysPerson> expectedList = Arrays.asList(testPerson);
        when(personMapper.selectPersonAll()).thenReturn(expectedList);

        // When
        List<SysPerson> result = personService.selectPersonAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personMapper).selectPersonAll();
    }

    @Test
    @DisplayName("查询所有在职人员（用于项目关联）")
    void testSelectPersonAllForProject() {
        // Given
        List<SysPerson> expectedList = Arrays.asList(testPerson);
        when(personMapper.selectPersonAllForProject()).thenReturn(expectedList);

        // When
        List<SysPerson> result = personService.selectPersonAllForProject();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personMapper).selectPersonAllForProject();
    }

    @Test
    @DisplayName("通过ID查询人员 - 存在")
    void testSelectPersonByIdExists() {
        // Given
        when(personMapper.selectPersonById(1L)).thenReturn(testPerson);

        // When
        SysPerson result = personService.selectPersonById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("张三", result.getName());
    }

    @Test
    @DisplayName("通过ID查询人员 - 不存在")
    void testSelectPersonByIdNotExists() {
        // Given
        when(personMapper.selectPersonById(999L)).thenReturn(null);

        // When
        SysPerson result = personService.selectPersonById(999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("校验姓名唯一性 - 唯一")
    void testCheckNameUnique() {
        // Given
        SysPerson newPerson = new SysPerson();
        newPerson.setName("新人员");
        when(personMapper.checkNameUnique("新人员")).thenReturn(null);

        // When
        boolean result = personService.checkNameUnique(newPerson);

        // Then
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @DisplayName("校验姓名唯一性 - 不唯一（同名已存在）")
    void testCheckNameUniqueNotUnique() {
        // Given
        SysPerson existingPerson = new SysPerson();
        existingPerson.setId(2L);
        existingPerson.setName("张三");
        when(personMapper.checkNameUnique("张三")).thenReturn(existingPerson);

        SysPerson newPerson = new SysPerson();
        newPerson.setName("张三");

        // When
        boolean result = personService.checkNameUnique(newPerson);

        // Then
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @DisplayName("校验姓名唯一性 - 更新时自身不算重复")
    void testCheckNameUniqueSelfUpdate() {
        // Given
        SysPerson existingPerson = new SysPerson();
        existingPerson.setId(1L);
        existingPerson.setName("张三");
        when(personMapper.checkNameUnique("张三")).thenReturn(existingPerson);

        SysPerson updatePerson = new SysPerson();
        updatePerson.setId(1L);
        updatePerson.setName("张三");

        // When
        boolean result = personService.checkNameUnique(updatePerson);

        // Then
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @DisplayName("新增人员")
    void testInsertPerson() {
        // Given
        SysPerson newPerson = new SysPerson();
        newPerson.setName("李四");
        when(personMapper.insertPerson(any(SysPerson.class))).thenReturn(1);

        // When
        int result = personService.insertPerson(newPerson);

        // Then
        assertEquals(1, result);
        verify(personMapper).insertPerson(newPerson);
    }

    @Test
    @DisplayName("修改人员")
    void testUpdatePerson() {
        // Given
        testPerson.setName("张三更新");
        when(personMapper.updatePerson(any(SysPerson.class))).thenReturn(1);

        // When
        int result = personService.updatePerson(testPerson);

        // Then
        assertEquals(1, result);
        verify(personMapper).updatePerson(testPerson);
    }

    @Test
    @DisplayName("删除单个人员")
    void testDeletePersonById() {
        // Given
        when(personMapper.deletePersonById(1L)).thenReturn(1);

        // When
        int result = personService.deletePersonById(1L);

        // Then
        assertEquals(1, result);
        verify(personMapper).deletePersonById(1L);
    }

    @Test
    @DisplayName("批量删除人员")
    void testDeletePersonByIds() {
        // Given
        Long[] ids = {1L, 2L, 3L};
        when(personMapper.deletePersonByIds(ids)).thenReturn(3);

        // When
        int result = personService.deletePersonByIds(ids);

        // Then
        assertEquals(3, result);
        verify(personMapper).deletePersonByIds(ids);
    }

    @Test
    @DisplayName("统计在职人员数量")
    void testCountActivePerson() {
        // Given
        when(personMapper.countActivePerson()).thenReturn(5);

        // When
        int count = personService.countActivePerson();

        // Then
        assertEquals(5, count);
        verify(personMapper).countActivePerson();
    }

    @Test
    @DisplayName("统计离职人员数量")
    void testCountInactivePerson() {
        // Given
        when(personMapper.countInactivePerson()).thenReturn(2);

        // When
        int count = personService.countInactivePerson();

        // Then
        assertEquals(2, count);
        verify(personMapper).countInactivePerson();
    }
}