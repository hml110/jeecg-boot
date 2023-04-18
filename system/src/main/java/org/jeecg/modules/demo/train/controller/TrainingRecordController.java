package org.jeecg.modules.demo.train.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.train.entity.TrainingRecord;
import org.jeecg.modules.demo.train.service.ITrainingRecordService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 培训记录表
 * @Author: jeecg-boot
 * @Date:   2023-04-18
 * @Version: V1.0
 */
@Api(tags="培训记录表")
@RestController
@RequestMapping("/train/trainingRecord")
@Slf4j
public class TrainingRecordController extends JeecgController<TrainingRecord, ITrainingRecordService> {
	@Autowired
	private ITrainingRecordService trainingRecordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param trainingRecord
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "培训记录表-分页列表查询")
	@ApiOperation(value="培训记录表-分页列表查询", notes="培训记录表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TrainingRecord>> queryPageList(TrainingRecord trainingRecord,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TrainingRecord> queryWrapper = QueryGenerator.initQueryWrapper(trainingRecord, req.getParameterMap());
		Page<TrainingRecord> page = new Page<TrainingRecord>(pageNo, pageSize);
		IPage<TrainingRecord> pageList = trainingRecordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param trainingRecord
	 * @return
	 */
	@AutoLog(value = "培训记录表-添加")
	@ApiOperation(value="培训记录表-添加", notes="培训记录表-添加")
//	@RequiresPermissions("train:training_record:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TrainingRecord trainingRecord) {
		trainingRecordService.save(trainingRecord);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param trainingRecord
	 * @return
	 */
	@AutoLog(value = "培训记录表-编辑")
	@ApiOperation(value="培训记录表-编辑", notes="培训记录表-编辑")
//	@RequiresPermissions("train:training_record:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TrainingRecord trainingRecord) {
		trainingRecordService.updateById(trainingRecord);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训记录表-通过id删除")
	@ApiOperation(value="培训记录表-通过id删除", notes="培训记录表-通过id删除")
//	@RequiresPermissions("train:training_record:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		trainingRecordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "培训记录表-批量删除")
	@ApiOperation(value="培训记录表-批量删除", notes="培训记录表-批量删除")
//	@RequiresPermissions("train:training_record:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.trainingRecordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "培训记录表-通过id查询")
	@ApiOperation(value="培训记录表-通过id查询", notes="培训记录表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TrainingRecord> queryById(@RequestParam(name="id",required=true) String id) {
		TrainingRecord trainingRecord = trainingRecordService.getById(id);
		if(trainingRecord==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(trainingRecord);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param trainingRecord
    */
    @RequiresPermissions("train:training_record:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TrainingRecord trainingRecord) {
        return super.exportXls(request, trainingRecord, TrainingRecord.class, "培训记录表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("train:training_record:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TrainingRecord.class);
    }

}
