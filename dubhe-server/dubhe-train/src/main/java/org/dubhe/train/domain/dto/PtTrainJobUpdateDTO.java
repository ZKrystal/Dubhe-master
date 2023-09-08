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

package org.dubhe.train.domain.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dubhe.biz.base.constant.MagicNumConstant;
import org.dubhe.train.domain.entity.PtAtlasTrainParam;
import org.dubhe.train.utils.TrainUtil;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @description 训练任务修改
 * @date 2020-04-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PtTrainJobUpdateDTO extends PtTrainJobBaseDTO {

    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为null")
    @Min(value = MagicNumConstant.ONE, message = "id必须大于1")
    private Long id;

    @ApiModelProperty("描述, 长度不能超过255个字符")
    @Length(max = MagicNumConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE, message = "描述长度不能超过255个字符")
    private String description;

    @ApiModelProperty("数据集类型，输入长度不能超过128个字符")
    @Length(max = MagicNumConstant.ONE_HUNDRED_TWENTY_EIGHT, message = "数据集类型-输入长度不能超过128个字符")
    private String datasetType;

    @ApiModelProperty("验证数据集类型，输入长度不能超过128个字符")
    @Length(max = MagicNumConstant.ONE_HUNDRED_TWENTY_EIGHT, message = "验证数据集类型-输入长度不能超过128个字符")
    private String valDatasetType;

    @ApiModelProperty(value = "数据集来源路径,输入长度不能超过127个字符")
    @Length(max = MagicNumConstant.ONE_HUNDRED_TWENTY_SEVEN, message = "数据集来源路径-输入长度不能超过127个字符")
    private String dataSourcePath;

    @ApiModelProperty(value = "数据集来源名称,输入长度不能超过127个字符")
    @Length(max = MagicNumConstant.ONE_HUNDRED_TWENTY_SEVEN, message = "数据集来源名称-输入长度不能超过127个字符")
    private String dataSourceName;

    @ApiModelProperty("运行参数(算法来源为我的算法时为调优参数，算法来源为预置算法时为运行参数)")
    private JSONObject runParams;

    @ApiModelProperty("运行参数映射关系")
    private JSONObject runParamsNameMap;

    @ApiModelProperty(value = "类型(0为CPU，1为GPU)", required = true)
    @Min(value = MagicNumConstant.ZERO, message = "类型错误")
    @Max(value = MagicNumConstant.ONE, message = "类型错误")
    @NotNull(message = "类型(0为CPU，1为GPU)不能为空")
    private Integer resourcesPoolType;

    @ApiModelProperty(value = "规格名称", required = true)
    @NotNull(message = "规格名称不能为空")
    private String trainJobSpecsName;

    @ApiModelProperty(value = "CPU数量,单位：核", required = true)
    @NotNull(message = "CPU数量不能为空")
    @Min(value = MagicNumConstant.ZERO, message = "CPU数量不能小于0")
    private Integer cpuNum;

    @ApiModelProperty(value = "GPU数量，单位：核", required = true)
    @NotNull(message = "GPU数量不能为空")
    @Min(value = MagicNumConstant.ZERO, message = "GPU数量不能小于0")
    private Integer gpuNum;

    @ApiModelProperty(value = "内存大小，单位：M", required = true)
    @NotNull(message = "内存数值不能为空")
    @Min(value = MagicNumConstant.ZERO, message = "内存不能小于0")
    private Integer memNum;

    @ApiModelProperty(value = "工作空间的存储配额，单位：M", required = true)
    @NotNull(message = "工作空间的存储配额不能为空")
    @Min(value = MagicNumConstant.ZERO, message = "工作空间的存储配额不能小于0")
    private Integer workspaceRequest;

    @ApiModelProperty(value = "训练类型 0：普通训练，1：分布式训练，2：炼知重组训练", required = true)
    @Min(value = MagicNumConstant.ZERO, message = "训练类型错误")
    @Max(value = MagicNumConstant.TWO, message = "训练类型错误")
    @NotNull(message = "训练类型(0为普通训练，1为分布式训练，2：炼知重组训练)不能为空")
    private Integer trainType;

    @ApiModelProperty(value = "节点个数", required = true)
    @Min(value = MagicNumConstant.ONE, message = "节点个数在1~8之间")
    @Max(value = MagicNumConstant.EIGHT, message = "节点个数在1~8之间")
    @NotNull(message = "节点个数")
    private Integer resourcesPoolNode;

    @ApiModelProperty("验证数据来源名称")
    private String valDataSourceName;

    @ApiModelProperty("验证数据来源路径")
    private String valDataSourcePath;

    @ApiModelProperty("是否验证数据集")
    private Integer valType;

    @ApiModelProperty(value = "训练延时启动时长，单位为小时")
    @Min(value = MagicNumConstant.ZERO, message = "训练延时启动时长不能小于0小时")
    @Max(value = MagicNumConstant.ONE_HUNDRED_SIXTY_EIGHT, message = "训练延时启动时长不能大于168小时即时长不能超过一周（7*24小时）")
    private Integer delayCreateTime;

    @ApiModelProperty(value = "训练自动停止时长，单位为小时")
    @Min(value = MagicNumConstant.ZERO, message = "训练自动停止时长必须不能小于0小时")
    @Max(value = MagicNumConstant.ONE_HUNDRED_SIXTY_EIGHT, message = "训练自动停止时长不能大于168小时即时长不能超过一周（7*24小时）")
    private Integer delayDeleteTime;

    @ApiModelProperty(value = "模型类型(0我的模型1预置模型2炼知模型)" +
            "当值为0和1的时候，需要传递ModelId, 当值为2的时候传递teacherModelIds和studentModelIds")
    private Integer modelResource;

    @ApiModelProperty(value = "模型id")
    @Min(value = MagicNumConstant.ONE, message = "id必须大于1")
    private Long modelId;

    @ApiModelProperty(value = "我的模型版本对应的id")
    @Min(value = MagicNumConstant.ONE, message = "模型版本对应的id必须大于1")
    private Long modelBranchId;

    @ApiModelProperty(value = "学生模型结构")
    @Length(max = MagicNumConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE, message = "学生模型长度不能超过255个字符")
    private String studentModelStruct;

    private List<PtAtlasTrainParam> baseAtlasParams;

    @ApiModelProperty(value = "炼知模型重组任务类型(1：单任务，2：多任务)")
    @Min(value = MagicNumConstant.ONE, message = "任务类型错误")
    @Max(value = MagicNumConstant.TWO, message = "任务类型错误")
    private Integer jobType;
}
