package io.shulie.takin.cloud.open.api.scenetask;

import io.shulie.takin.cloud.open.req.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneStartPreCheckReq;
import io.shulie.takin.cloud.open.req.scenemanage.SceneTaskStartReq;
import io.shulie.takin.cloud.open.req.scenemanage.ScriptAssetBalanceReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneStartCheckResp;
import io.shulie.takin.cloud.open.req.scenetask.SceneTaskQueryTpsReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTaskUpdateTpsReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskCheckReq;
import io.shulie.takin.cloud.open.req.scenetask.SceneTryRunTaskStartReq;
import io.shulie.takin.cloud.open.req.scenetask.TaskFlowDebugStartReq;
import io.shulie.takin.cloud.open.req.scenetask.TaskInspectStartReq;
import io.shulie.takin.cloud.open.req.scenetask.TaskInspectStopReq;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneInspectTaskStartResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneInspectTaskStopResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStartResp;
import io.shulie.takin.cloud.open.resp.scenemanage.SceneTryRunTaskStatusResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneActionResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneJobStateResp;
import io.shulie.takin.cloud.open.resp.scenetask.SceneTaskAdjustTpsResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.asset.AssetBalanceExt;

/**
 * 压测任务
 *
 * @author qianshui
 * @date 2020/11/13 上午11:05
 */
public interface CloudTaskApi {

    /**
     * 启动压测
     *
     * @param req -
     * @return -
     */
    ResponseResult<SceneActionResp> start(SceneTaskStartReq req);

    /**
     * 停止任务
     *
     * @param req 入参
     * @return 停止结果
     */
    ResponseResult<String> stopTask(SceneManageIdReq req);

    /**
     * 检查任务状态
     *
     * @param req 入参
     * @return 状态检查返回值
     */
    ResponseResult<SceneActionResp> checkTask(SceneManageIdReq req);

    /**
     * 更新压测场景任务tps
     *
     * @param sceneTaskUpdateTpsReq -
     * @return -
     */
    ResponseResult<String> updateSceneTaskTps(SceneTaskUpdateTpsReq sceneTaskUpdateTpsReq);

    /**
     * 获取调整前tps
     *
     * @param sceneTaskQueryTpsReq -
     * @return -
     */
    ResponseResult<SceneTaskAdjustTpsResp> queryAdjustTaskTps(SceneTaskQueryTpsReq sceneTaskQueryTpsReq);

    /**
     * 启动流量调试任务
     *
     * @param taskFlowDebugStartReq -
     * @return -
     */
    ResponseResult<Long> startFlowDebugTask(TaskFlowDebugStartReq taskFlowDebugStartReq);

    /**
     * 启动巡检任务
     *
     * @param taskInspectStartReq -
     * @return -
     */
    ResponseResult<SceneInspectTaskStartResp> startInspectTask(TaskInspectStartReq taskInspectStartReq);

    /**
     * 停止巡检任务
     *
     * @param taskInspectStopReq -
     * @return -
     */
    ResponseResult<SceneInspectTaskStopResp> stopInspectTask(TaskInspectStopReq taskInspectStopReq);

    /**
     * 启动试跑任务
     *
     * @param sceneTryRunTaskStartReq -
     * @return -
     */
    ResponseResult<SceneTryRunTaskStartResp> startTryRunTask(SceneTryRunTaskStartReq sceneTryRunTaskStartReq);

    /**
     * 查询试跑任务状态
     *
     * @param sceneTryRunTaskCheckReq -
     * @return -
     */
    ResponseResult<SceneTryRunTaskStatusResp> checkTaskStatus(SceneTryRunTaskCheckReq sceneTryRunTaskCheckReq);

    /**
     * 检查压测场景任务状态
     *
     * @param req -
     * @return -
     */
    ResponseResult<SceneJobStateResp> checkSceneJobStatus(SceneManageIdReq req);

    /**
     * 压测场景启动前检查
     *
     * @param req -
     * @return -
     */
    ResponseResult<SceneStartCheckResp> sceneStartPreCheck(SceneStartPreCheckReq req);

    /**
     * 回调写入余额
     *
     * @param req 入参
     * @return 操作结果
     */
    ResponseResult<Boolean> callBackToWriteBalance(ScriptAssetBalanceReq req);
}
