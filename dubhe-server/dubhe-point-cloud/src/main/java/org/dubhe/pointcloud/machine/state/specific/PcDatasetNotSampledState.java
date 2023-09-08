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


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.dubhe.pointcloud.domain.entity.PcDataset;
import org.dubhe.pointcloud.enums.PcDatasetMachineStatusEnum;
import org.dubhe.pointcloud.machine.state.AbstractPcDatasetState;
import org.dubhe.pointcloud.service.PcDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @description 未采样状态类
 * @date 2022-04-02
 */
@Component
public class PcDatasetNotSampledState extends AbstractPcDatasetState {

    @Autowired
    private PcDatasetService pcDatasetService;

    @Override
    public void importingPcDatasetEvent(Long datasetId) {
        pcDatasetService.updatePcDataset(new LambdaUpdateWrapper<PcDataset>()
                .eq(PcDataset::getId, datasetId)
                .set(PcDataset::getStatus, PcDatasetMachineStatusEnum.IMPORTING.getCode()));
    }

    @Override
    public String currentStatus() {
        return PcDatasetMachineStatusEnum.NOT_SAMPLED.getDesc();
    }
}
