package org.jeecg.modules.system.controller;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.model.TreeModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.vo.SysUserRoleCountVo;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * ????????? ???????????????
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@RestController
@RequestMapping("/sys/role")
@Slf4j
public class SysRoleController {
	@Autowired
	private ISysRoleService sysRoleService;
	
	@Autowired
	private ISysPermissionDataRuleService sysPermissionDataRuleService;
	
	@Autowired
	private ISysRolePermissionService sysRolePermissionService;
	
	@Autowired
	private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

	/**
	  * ?????????????????? ???????????????????????????????????????
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@RequiresPermissions("system:role:list")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysRole>> queryPageList(SysRole role,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
		Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
		IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * ??????????????????????????????????????????????????????
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/listByTenant", method = RequestMethod.GET)
	public Result<IPage<SysRole>> listByTenant(SysRole role,
												@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												HttpServletRequest req) {
		Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
		//------------------------------------------------------------------------------------------------
		//?????????????????????????????????????????????????????????SAAS??????????????????
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			role.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(),0));
		}
		//------------------------------------------------------------------------------------------------
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
		Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
		IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   ??????
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("system:role:add")
	public Result<SysRole> add(@RequestBody SysRole role) {
		Result<SysRole> result = new Result<SysRole>();
		try {
			role.setCreateTime(new Date());
			sysRoleService.save(role);
			result.success("???????????????");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????");
		}
		return result;
	}
	
	/**
	  *  ??????
	 * @param role
	 * @return
	 */
    @RequiresPermissions("system:role:edit")
	@RequestMapping(value = "/edit",method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<SysRole> edit(@RequestBody SysRole role) {
		Result<SysRole> result = new Result<SysRole>();
		SysRole sysrole = sysRoleService.getById(role.getId());
		if(sysrole==null) {
			result.error500("?????????????????????");
		}else {
			role.setUpdateTime(new Date());
			boolean ok = sysRoleService.updateById(role);
			//TODO ??????false???????????????
			if(ok) {
				result.success("????????????!");
			}
		}
		
		return result;
	}
	
	/**
	  *   ??????id??????
	 * @param id
	 * @return
	 */
    @RequiresPermissions("system:role:delete")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysRoleService.deleteRole(id);
		return Result.ok("??????????????????");
	}
	
	/**
	  *  ????????????
	 * @param ids
	 * @return
	 */
    @RequiresPermissions("system:role:deleteBatch")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysRole> result = new Result<SysRole>();
		if(oConvertUtils.isEmpty(ids)) {
			result.error500("??????????????????");
		}else {
			sysRoleService.deleteBatchRole(ids.split(","));
			result.success("??????????????????!");
		}
		return result;
	}
	
	/**
	  * ??????id??????
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<SysRole> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysRole> result = new Result<SysRole>();
		SysRole sysrole = sysRoleService.getById(id);
		if(sysrole==null) {
			result.error500("?????????????????????");
		}else {
			result.setResult(sysrole);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryall", method = RequestMethod.GET)
	public Result<List<SysRole>> queryall() {
		Result<List<SysRole>> result = new Result<>();
		LambdaQueryWrapper<SysRole> query = new LambdaQueryWrapper<SysRole>();
		//------------------------------------------------------------------------------------------------
		//?????????????????????????????????????????????????????????SAAS??????????????????
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			query.eq(SysRole::getTenantId, oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		List<SysRole> list = sysRoleService.list(query);
		if(list==null||list.size()<=0) {
			result.error500("?????????????????????");
		}else {
			result.setResult(list);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * ????????????????????????????????????????????????
	 *
	 * @return
	 */
	@RequiresPermissions("system:role:queryallNoByTenant")
	@RequestMapping(value = "/queryallNoByTenant", method = RequestMethod.GET)
	public Result<List<SysRole>> queryallNoByTenant() {
		Result<List<SysRole>> result = new Result<>();
		LambdaQueryWrapper<SysRole> query = new LambdaQueryWrapper<SysRole>();
		List<SysRole> list = sysRoleService.list(query);
		if(list==null||list.size()<=0) {
			result.error500("?????????????????????");
		}else {
			result.setResult(list);
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	  * ????????????????????????
	 */
	@RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
	public Result<Boolean> checkUsername(String id,String roleCode) {
		Result<Boolean> result = new Result<>();
        //??????????????????false?????????????????????
		result.setResult(true);
		log.info("--??????????????????????????????---id:"+id+"--roleCode:"+roleCode);
		try {
			SysRole role = null;
			if(oConvertUtils.isNotEmpty(id)) {
				role = sysRoleService.getById(id);
			}
			SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
			if(newRole!=null) {
				//?????????????????????roleCode???????????????????????????????????????????????????
				if(role==null) {
					//role??????=>????????????=>??????roleCode???????????????false
					result.setSuccess(false);
					result.setMessage("?????????????????????");
					return result;
				}else if(!id.equals(newRole.getId())) {
					//??????=>????????????=>????????????ID????????????-
					result.setSuccess(false);
					result.setMessage("?????????????????????");
					return result;
				}
			}
		} catch (Exception e) {
			result.setSuccess(false);
			result.setResult(false);
			result.setMessage(e.getMessage());
			return result;
		}
		result.setSuccess(true);
		return result;
	}

	/**
	 * ??????excel
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(SysRole sysRole,HttpServletRequest request) {
		//------------------------------------------------------------------------------------------------
		//?????????????????????????????????????????????????????????SAAS??????????????????
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysRole.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		
		// Step.1 ??????????????????
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(sysRole, request.getParameterMap());
		//Step.2 AutoPoi ??????Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysRole> pageList = sysRoleService.list(queryWrapper);
		//??????????????????
		mv.addObject(NormalExcelConstants.FILE_NAME,"????????????");
		mv.addObject(NormalExcelConstants.CLASS,SysRole.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("??????????????????","?????????:"+user.getRealname(),"????????????"));
		mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
		return mv;
	}

	/**
	 * ??????excel????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // ????????????????????????
			MultipartFile file = entity.getValue();
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				return sysRoleService.importExcelCheckRoleCode(file, params);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("??????????????????:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return Result.error("?????????????????????");
	}
	
	/**
	 * ????????????????????????
	 */
	@GetMapping(value = "/datarule/{permissionId}/{roleId}")
	public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("roleId") String roleId) {
		List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
		if(list==null || list.size()==0) {
			return Result.error("???????????????????????????");
		}else {
			Map<String,Object> map = new HashMap(5);
			map.put("datarule", list);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.isNotNull(SysRolePermission::getDataRuleIds)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				//return Result.error("?????????????????????????????????");
			}else {
				String drChecked = sysRolePermission.getDataRuleIds();
				if(oConvertUtils.isNotEmpty(drChecked)) {
					map.put("drChecked", drChecked.endsWith(",")?drChecked.substring(0, drChecked.length()-1):drChecked);
				}
			}
			return Result.ok(map);
			//TODO ????????????????????????????????????????????? ?????????map???????????????key
		}
	}
	
	/**
	 * ??????????????????????????????????????????
	 */
	@PostMapping(value = "/datarule")
	public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
		try {
			String permissionId = jsonObject.getString("permissionId");
			String roleId = jsonObject.getString("roleId");
			String dataRuleIds = jsonObject.getString("dataRuleIds");
			log.info("??????????????????>>"+"??????ID:"+permissionId+"??????ID:"+ roleId+"????????????ID:"+dataRuleIds);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				return Result.error("??????????????????????????????!");
			}else {
				sysRolePermission.setDataRuleIds(dataRuleIds);
				this.sysRolePermissionService.updateById(sysRolePermission);
			}
		} catch (Exception e) {
			log.error("SysRoleController.saveDatarule()???????????????" + e.getMessage(),e);
			return Result.error("????????????");
		}
		return Result.ok("????????????!");
	}
	
	
	/**
	 * ????????????????????????????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<Map<String,Object>> queryTreeList(HttpServletRequest request) {
		Result<Map<String,Object>> result = new Result<>();
		//????????????ids
		List<String> ids = new ArrayList<>();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			for(SysPermission sysPer : list) {
				ids.add(sysPer.getId());
			}
			List<TreeModel> treeList = new ArrayList<>();
			getTreeModelList(treeList, list, null);
			Map<String,Object> resMap = new HashMap(5);
            //?????????????????????
			resMap.put("treeList", treeList);
            //?????????ids
			resMap.put("ids", ids);
			result.setResult(resMap);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	private void getTreeModelList(List<TreeModel> treeList,List<SysPermission> metaList,TreeModel temp) {
		for (SysPermission permission : metaList) {
			String tempPid = permission.getParentId();
			TreeModel tree = new TreeModel(permission.getId(), tempPid, permission.getName(),permission.getRuleFlag(), permission.isLeaf());
			if(temp==null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if(!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			}else if(temp!=null && tempPid!=null && tempPid.equals(temp.getKey())){
				temp.getChildren().add(tree);
				if(!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			}
			
		}
	}

}
