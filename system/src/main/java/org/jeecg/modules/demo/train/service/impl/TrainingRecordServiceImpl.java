package org.jeecg.modules.demo.train.service.impl;

import org.jeecg.modules.demo.train.entity.TrainingRecord;
import org.jeecg.modules.demo.train.mapper.TrainingRecordMapper;
import org.jeecg.modules.demo.train.service.ITrainingRecordService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 培训记录表
 * @Author: jeecg-boot
 * @Date:   2023-04-18
 * @Version: V1.0
 */
@Service
public class TrainingRecordServiceImpl extends ServiceImpl<TrainingRecordMapper, TrainingRecord> implements ITrainingRecordService {

}
