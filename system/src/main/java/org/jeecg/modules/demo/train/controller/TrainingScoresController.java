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
import org.jeecg.modules.demo.train.entity.TrainingScores;
import org.jeecg.modules.demo.train.service.ITrainingScoresService;

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
 * @Description: 培训成绩表
 * @Author: jeecg-boot
 * @Date:   2023-04-18
 * @Version: V1.0
 */
@Api(tags="培训成绩表")
@RestController
@RequestMapping("/train/trainingScores")
@Slf4j
public class TrainingScoresController extends JeecgController<TrainingScores, ITrainingScoresService> {
	@Autowired
	private ITrainingScoresService trainingScoresService;
	
	/**
	 * 分页列表查询
	 *
	 * @param trainingScores
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "培训成绩表-分页列表查询")
	@ApiOperation(value="培训成绩表-分页列表查询", notes="培训成绩表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TrainingScores>> queryPageList(TrainingScores trainingScores,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TrainingScores> queryWrapper = QueryGenerator.initQueryWrapper(trainingScores, req.getParameterMap());
		Page<TrainingScores> page = new Page<TrainingScores>(pageNo, pageSize);
		IPage<TrainingScores> pageList = trainingScoresService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param trainingScores
	 * @return
	 */
	@AutoLog(value = "培训成绩表-添加")
	@ApiOperation(value="培训成绩表-添加", notes="培训成绩表-添加")
//	@RequiresPermissions("train:training_scores:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TrainingScores trainingScores) {
		trainingScoresService.save(trainingScores);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param trainingScores
	 * @return
	 */
	@AutoLog(value = "培训成绩表-编辑")
	@ApiOperation(value="培训成绩表-编辑", notes="培训成绩表-编辑")
//	@RequiresPermissions("train:training_scores:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TrainingScores trainingScores) {
		trainingScoresService.updateById(trainingScores);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "培训成绩表-通过id删除")
	@ApiOperation(value="培训成绩表-通过id删除", notes="培训成绩表-通过id删除")
//	@RequiresPermissions("train:training_scores:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		trainingScoresService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "培训成绩表-批量删除")
	@ApiOperation(value="培训成绩表-批量删除", notes="培训成绩表-批量删除")
//	@RequiresPermissions("train:training_scores:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.trainingScoresService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "培训成绩表-通过id查询")
	@ApiOperation(value="培训成绩表-通过id查询", notes="培训成绩表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TrainingScores> queryById(@RequestParam(name="id",required=true) String id) {
		TrainingScores trainingScores = trainingScoresService.getById(id);
		if(trainingScores==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(trainingScores);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param trainingScores
    */
    @RequiresPermissions("train:training_scores:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TrainingScores trainingScores) {
        return super.exportXls(request, trainingScores, TrainingScores.class, "培训成绩表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("train:training_scores:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TrainingScores.class);
    }

}
