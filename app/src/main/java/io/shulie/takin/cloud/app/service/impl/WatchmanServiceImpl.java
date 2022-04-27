package io.shulie.takin.cloud.app.service.impl;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.util.ResourceUtil;
import io.shulie.takin.cloud.constant.enums.EventType;
import io.shulie.takin.cloud.app.entity.WatchmanEntity;
import io.shulie.takin.cloud.app.model.resource.Resource;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.mapper.WatchmanEventMapper;
import io.shulie.takin.cloud.app.entity.WatchmanEventEntity;
import io.shulie.takin.cloud.app.model.callback.ResourceUpload;
import io.shulie.takin.cloud.app.service.mapper.WatchmanMapperService;

/**
 * 调度服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@Service
public class WatchmanServiceImpl implements WatchmanService {
    @javax.annotation.Resource
    WatchmanEventMapper watchmanEventMapper;
    @javax.annotation.Resource
    WatchmanMapperService watchmanMapperService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public PageInfo<WatchmanEntity> list(int pageNumber, int pageSize) {
        try (Page<?> ignored = PageHelper.startPage(pageNumber, pageSize)) {
            return new PageInfo<>(watchmanMapperService.list());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getResourceList(Long watchmanId) throws JsonProcessingException {
        List<Resource> result = new ArrayList<>(0);
        // 找到最后一次上报的数据
        try (Page<Resource> ignored = PageHelper.startPage(1, 1)) {
            // 查询条件 - 资源类型的上报
            Wrapper<WatchmanEventEntity> wrapper = new LambdaQueryWrapper<WatchmanEventEntity>()
                .orderByDesc(WatchmanEventEntity::getTime)
                .eq(WatchmanEventEntity::getType, EventType.WATCHMAN_UPLOAD.getCode())
                .eq(WatchmanEventEntity::getWatchmanId, watchmanId);
            // 执行SQL
            PageInfo<WatchmanEventEntity> watchmanEventList = new PageInfo<>(watchmanEventMapper.selectList(wrapper));
            // 组装数据
            if (watchmanEventList.getList().size() > 0) {
                // 组装返回数据
                String eventContextString = watchmanEventList.getList().get(0).getContext();
                HashMap<String, String> eventContext = objectMapper.readValue(eventContextString, new TypeReference<HashMap<String, String>>() {});
                {
                    long resourceTime = Long.parseLong(String.valueOf(eventContext.get("time")));
                    // TODO 要校验时效
                    if (resourceTime < 0) {
                        log.warn("调度资源获取:最后一次上报的资源时效了");
                    }
                    String resourceListString = eventContext.get("data");
                    List<Resource> resourceList = objectMapper.readValue(resourceListString, new TypeReference<List<Resource>>() {});
                    // 处理数据
                    result.addAll(resourceList);
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("调度资源获取:JSON解析失败");
            throw e;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(String ref, String refSign) {
        // 已存在返回TRUE
        if (ofRefSign(refSign) != null) {return true;}
        return watchmanMapperService.save(new WatchmanEntity() {{
            setRef(ref);
            setRefSign(refSign);
        }});
    }

    @Override
    public WatchmanEntity ofRefSign(String refSign) {
        // TODO 签名校验
        return watchmanMapperService.lambdaQuery().eq(WatchmanEntity::getRefSign, refSign).one();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(long watchmanId, ResourceUpload content) throws JsonProcessingException {
        // resource -> 转换
        List<String> errorMessage = new ArrayList<>(2);
        List<Resource> resourceList = content.getData().stream().map(t -> new Resource() {{
            Double cpu = ResourceUtil.convertCpu(t.getCpu());
            Long memory = ResourceUtil.convertMemory(t.getMemory());
            if (cpu == null) {errorMessage.add("无法解析的CPU值:" + t.getCpu());}
            if (memory == null) {errorMessage.add("无法解析的内存值:" + t.getMemory());}
            setCpu(cpu);
            setMemory(memory);
        }}).collect(Collectors.toList());
        // 转换校验
        if (errorMessage.size() > 0) {throw new RuntimeException(String.join(",", errorMessage));}
        // 组装入库数据
        HashMap<String, String> context = new HashMap<>(2);
        context.put("time", content.getTime().toString());
        context.put("data", objectMapper.writeValueAsString(resourceList));
        // 插入数据库
        watchmanEventMapper.insert(new WatchmanEventEntity() {{
            setWatchmanId(watchmanId);
            setType(EventType.WATCHMAN_UPLOAD.getCode());
            setContext(objectMapper.writeValueAsString(context));
        }});
    }
}
