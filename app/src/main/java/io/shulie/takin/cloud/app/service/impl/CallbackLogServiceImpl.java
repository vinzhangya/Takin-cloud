package io.shulie.takin.cloud.app.service.impl;

import java.util.Map;
import java.util.Date;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.cloud.constant.Message;
import io.shulie.takin.cloud.app.service.JsonService;
import io.shulie.takin.cloud.app.service.CallbackService;
import io.shulie.takin.cloud.data.entity.CallbackLogEntity;
import io.shulie.takin.cloud.app.service.CallbackLogService;
import io.shulie.takin.cloud.data.service.CallbackLogMapperService;

/**
 * 回调日志服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
@Slf4j(topic = "CALLBACK")
public class CallbackLogServiceImpl implements CallbackLogService {

    @javax.annotation.Resource
    JsonService jsonService;

    @Lazy
    @javax.annotation.Resource
    CallbackService callbackService;

    @javax.annotation.Resource(name = "callbackLogMapperServiceImpl")
    CallbackLogMapperService callbackLogMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long count(long callbackId) {
        return callbackLogMapper.lambdaQuery()
            .eq(CallbackLogEntity::getCallbackId, callbackId)
            .count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long create(long callbackId, Integer type, String url, byte[] data) {
        CallbackLogEntity callbackLogEntity = new CallbackLogEntity()
            .setType(type)
            .setRequestUrl(url)
            .setRequestData(data)
            .setCallbackId(callbackId)
            .setRequestTime(new Date());
        callbackLogMapper.save(callbackLogEntity);
        return callbackLogEntity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fill(long callbackLogId, byte[] data) {
        CallbackLogEntity callbackLogEntity = callbackLogMapper.getById(callbackLogId);
        if (callbackLogEntity == null) {
            log.warn("{}对应的数据库记录未找到", callbackLogId);
        } else {
            boolean completed = isSuccess(data);
            // 填充日志信息
            boolean updateResult = callbackLogMapper.
                lambdaUpdate()
                .eq(CallbackLogEntity::getId, callbackLogId)
                .set(CallbackLogEntity::getResponseData, data)
                .set(CallbackLogEntity::getResponseTime, new Date())
                .set(CallbackLogEntity::getCompleted, completed).update();
            // 更新回调的状态
            if (completed && updateResult) {
                callbackService.updateCompleted(callbackLogEntity.getCallbackId(), true);
            }
            // 更新阈值时间 - 防止回调堆积
            else {
                callbackService.updateThresholdTime(callbackLogEntity.getCallbackId());
            }
        }
    }

    /**
     * 回调的响应判断回调是否成功
     */
    private boolean isSuccess(byte[] responseData) {
        String response = StrUtil.utf8Str(responseData);
        Map<String, Object> resJson = jsonService.readValue(response, new TypeReference<Map<String, Object>>() {});
        return Objects.nonNull(resJson)
            && Objects.nonNull(resJson.get(Message.SUCCESS))
            && Boolean.TRUE.equals(BooleanUtil.toBoolean(resJson.get(Message.SUCCESS).toString()));
    }
}
