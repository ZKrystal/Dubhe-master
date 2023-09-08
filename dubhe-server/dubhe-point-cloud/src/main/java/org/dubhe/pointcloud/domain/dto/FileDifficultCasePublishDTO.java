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
package org.dubhe.pointcloud.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description 难例发布DTO
 * @date 2022-04-01
 **/
@Data
public class FileDifficultCasePublishDTO {
    @ApiModelProperty("数据集id")
    @NotNull(message = "数据集id不能为空")
    private Long datasetId;

    @ApiModelProperty("数据集名称")
    @NotBlank(message = "数据集名称不能为空")
    private String name;

    @ApiModelProperty("数据集描述")
    private String remark;

    @ApiModelProperty("选择标签组id")
    @NotNull(message = "标签组id不能为空")
    private Long labelGroupId;


}
