package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.domain.SysPersonSkill;
import com.ruoyi.system.domain.SysSkillCategory;
import com.ruoyi.system.service.ISysPersonService;
import com.ruoyi.system.service.ISysResourceService;

/**
 * 资源管理操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/resource")
public class SysResourceController extends BaseController
{
    @Autowired
    private ISysResourceService resourceService;

    @Autowired
    private ISysPersonService personService;

    /**
     * 获取资源列表（含技能和项目数）
     */
    @PreAuthorize("@ss.hasPermi('system:resource:list')")
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String resourceStatus,
            @RequestParam(required = false) String projectName)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("category", category);
        params.put("skill", skill);
        params.put("resourceStatus", resourceStatus);
        params.put("projectName", projectName);
        List<Map<String, Object>> list = resourceService.selectResourceList(params);
        return success(list);
    }

    /**
     * 获取技能分类列表
     */
    @GetMapping("/skillCategories")
    public AjaxResult skillCategories()
    {
        List<SysSkillCategory> list = resourceService.selectSkillCategoryAll();
        return success(list);
    }

    /**
     * 获取人员技能列表
     */
    @PreAuthorize("@ss.hasPermi('system:resource:query')")
    @GetMapping("/skills/{personId}")
    public AjaxResult getSkills(@PathVariable Long personId)
    {
        List<SysPersonSkill> skills = resourceService.selectSkillByPersonId(personId);
        return success(skills);
    }

    /**
     * 添加人员技能
     */
    @PreAuthorize("@ss.hasPermi('system:resource:edit')")
    @Log(title = "资源管理", businessType = BusinessType.INSERT)
    @PostMapping("/skill")
    public AjaxResult addSkill(@RequestBody SysPersonSkill personSkill)
    {
        personSkill.setCreateBy(getUsername());
        personSkill.setSource("pm");
        return toAjax(resourceService.insertPersonSkill(personSkill));
    }

    /**
     * 删除人员技能
     */
    @PreAuthorize("@ss.hasPermi('system:resource:edit')")
    @Log(title = "资源管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/skill/{id}")
    public AjaxResult removeSkill(@PathVariable Long id)
    {
        return toAjax(resourceService.deletePersonSkillById(id));
    }

    /**
     * 更新人员资源状态
     */
    @PreAuthorize("@ss.hasPermi('system:resource:edit')")
    @Log(title = "资源管理", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{personId}")
    public AjaxResult updateStatus(@PathVariable Long personId, @RequestBody Map<String, String> statusInfo)
    {
        SysPerson person = new SysPerson();
        person.setId(personId);
        person.setResourceStatusRemark(statusInfo.get("resourceStatusRemark"));
        person.setNextProject(statusInfo.get("nextProject"));
        // 解析预计释放日期
        String expectedReleaseDateStr = statusInfo.get("expectedReleaseDate");
        if (expectedReleaseDateStr != null && !expectedReleaseDateStr.isEmpty())
        {
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date expectedReleaseDate = sdf.parse(expectedReleaseDateStr);
                person.setExpectedReleaseDate(expectedReleaseDate);
            }
            catch (ParseException e)
            {
                // 解析失败时忽略
            }
        }
        person.setUpdateBy(getUsername());
        return toAjax(personService.updatePerson(person));
    }

    /**
     * 导出技能数据
     */
    @Log(title = "资源管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:resource:export')")
    @PostMapping("/exportSkill")
    public void exportSkill(HttpServletResponse response)
    {
        try
        {
            List<Map<String, Object>> list = resourceService.exportSkillData();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("人员技能数据");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("姓名");
            headerRow.createCell(1).setCellValue("职位");
            headerRow.createCell(2).setCellValue("等级");
            headerRow.createCell(3).setCellValue("技能大类");
            headerRow.createCell(4).setCellValue("具体技能");
            headerRow.createCell(5).setCellValue("来源");
            headerRow.createCell(6).setCellValue("创建时间");

            // 填充数据
            int rowNum = 1;
            for (Map<String, Object> data : list)
            {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.get("name") != null ? data.get("name").toString() : "");
                row.createCell(1).setCellValue(data.get("position") != null ? data.get("position").toString() : "");
                row.createCell(2).setCellValue(data.get("level") != null ? data.get("level").toString() : "");
                row.createCell(3).setCellValue(data.get("category") != null ? data.get("category").toString() : "");
                row.createCell(4).setCellValue(data.get("skill") != null ? data.get("skill").toString() : "");
                row.createCell(5).setCellValue(data.get("source") != null ? data.get("source").toString() : "");
                row.createCell(6).setCellValue(data.get("createTime") != null ? data.get("createTime").toString() : "");
            }

            // 输出文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=skill_" + System.currentTimeMillis() + ".xlsx");
            workbook.write(response.getOutputStream());
            workbook.close();
        }
        catch (Exception e)
        {
            logger.error("导出技能数据失败", e);
        }
    }

    /**
     * 导出项目参与数据
     */
    @Log(title = "资源管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:resource:export')")
    @PostMapping("/exportProject")
    public void exportProject(HttpServletResponse response)
    {
        try
        {
            List<Map<String, Object>> list = resourceService.exportProjectData();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("人员项目参与数据");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("姓名");
            headerRow.createCell(1).setCellValue("职位");
            headerRow.createCell(2).setCellValue("等级");
            headerRow.createCell(3).setCellValue("项目名称");
            headerRow.createCell(4).setCellValue("项目状态");
            headerRow.createCell(5).setCellValue("角色");
            headerRow.createCell(6).setCellValue("参与时间");

            // 填充数据
            int rowNum = 1;
            for (Map<String, Object> data : list)
            {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.get("name") != null ? data.get("name").toString() : "");
                row.createCell(1).setCellValue(data.get("position") != null ? data.get("position").toString() : "");
                row.createCell(2).setCellValue(data.get("level") != null ? data.get("level").toString() : "");
                row.createCell(3).setCellValue(data.get("projectName") != null ? data.get("projectName").toString() : "");
                row.createCell(4).setCellValue(data.get("projectStatus") != null ? data.get("projectStatus").toString() : "");
                row.createCell(5).setCellValue(data.get("role") != null ? data.get("role").toString() : "");
                row.createCell(6).setCellValue(data.get("joinDate") != null ? data.get("joinDate").toString() : "");
            }

            // 输出文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=project_participation_" + System.currentTimeMillis() + ".xlsx");
            workbook.write(response.getOutputStream());
            workbook.close();
        }
        catch (Exception e)
        {
            logger.error("导出项目参与数据失败", e);
        }
    }

    /**
     * 获取资源统计概览
     */
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> allResources = resourceService.selectResourceList(params);

        int total = allResources.size();
        int freeCount = 0;
        int soonFreeCount = 0;
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Map<String, Object> resource : allResources)
        {
            String status = (String) resource.get("resourceStatus");
            if ("空闲".equals(status))
            {
                freeCount++;
            }
            else if ("即将空闲".equals(status))
            {
                soonFreeCount++;
            }
            List<Map<String, String>> skills = (List<Map<String, String>>) resource.get("skills");
            for (Map<String, String> skill : skills)
            {
                String cat = skill.get("category");
                categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("freeCount", freeCount);
        result.put("soonFreeCount", soonFreeCount);
        result.put("categoryDistribution", categoryCount);
        return success(result);
    }
}