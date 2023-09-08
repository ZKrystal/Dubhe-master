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

import org.dubhe.pointcloud.enums.PcDatasetMachineStatusEnum;
import org.dubhe.pointcloud.machine.state.AbstractPcDatasetState;
import org.springframework.stereotype.Component;

/**
 * @description 难例发布失败状态类
 * @date 2022-05-07
 **/
@Component
public class PcDatasetDifficultCaseFailedToPublishState extends AbstractPcDatasetState {
    @Override
    public String currentStatus() {
        return PcDatasetMachineStatusEnum.DIFFICULT_CASE_FAILED_TO_PUBLISH.getDesc();
    }
}
