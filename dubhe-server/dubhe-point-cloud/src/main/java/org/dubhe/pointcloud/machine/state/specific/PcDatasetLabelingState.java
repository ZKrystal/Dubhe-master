/**
 * Copyright 2020 Tianshu AI Platform. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =============================================================
 */
package org.dubhe.pointcloud.machine.state.specific;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.dubhe.biz.log.enums.LogEnum;
import org.dubhe.biz.log.utils.LogUtil;
import org.dubhe.biz.statemachine.exception.StateMachineException;
import org.dubhe.pointcloud.domain.entity.PcDataset;
import org.dubhe.pointcloud.domain.entity.PcDatasetFile;
import org.dubhe.pointcloud.enums.MarkStatusEnum;
import org.dubhe.pointcloud.enums.PcDatasetMachineStatusEnum;
import org.dubhe.pointcloud.machine.state.AbstractPcDatasetState;
import org.dubhe.pointcloud.service.FileService;
import org.dubhe.pointcloud.service.PcDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * @description 标注中状态类
 * @date 2022-04-02
 */
@Component
public class PcDatasetLabelingState extends AbstractPcDatasetState {

    @Autowired
    private PcDatasetService pcDatasetService;

    @Autowired
    private FileService fileService;

    @Override
    public void publishedPcDatasetEvent(Long datasetId) {
        List<PcDatasetFile> pointCloudFiles = fileService.selectList(new LambdaQueryWrapper<PcDatasetFile>().eq(PcDatasetFile::getDatasetId, datasetId)
                .eq(PcDatasetFile::getMarkStatus, MarkStatusEnum.MANUAL_MARKED.getCode()));
        if (CollectionUtils.isEmpty(pointCloudFiles)) {
            LogUtil.error(LogEnum.POINT_CLOUD,"The dataset with id {} failed to publish,and the marked point cloud file is empty",datasetId);
           throw new StateMachineException("文件还未手动标注完成，请手动标注完成点云文件后再发布");
        }
        pcDatasetService.updatePcDataset(new LambdaUpdateWrapper<PcDataset>()
                .eq(PcDataset::getId, datasetId)
                .set(PcDataset::getStatus, PcDatasetMachineStatusEnum.PUBLISHED.getCode()));
    }

    @Override
    public void difficultCasePublishingEvent(Long datasetId) {
        pcDatasetService.updatePcDataset(new LambdaUpdateWrapper<PcDataset>()
                .eq(PcDataset::getId, datasetId).set(PcDataset::getStatus, PcDatasetMachineStatusEnum.DIFFICULT_CASE_PUBLISHING.getCode()));
    }

    @Override
    public String currentStatus() {
        return PcDatasetMachineStatusEnum.LABELING.getDesc();
    }
}
