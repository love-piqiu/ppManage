-- 更新管理类别的具体技能列表
UPDATE sys_skill_category
SET skills = '["项目经理"]'
WHERE category = '管理' AND del_flag = '0';