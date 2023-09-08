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
package org.dubhe.model.client.fallback;

import org.dubhe.biz.base.dto.PtModelStatusQueryDTO;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.model.client.BatchServingClient;

/**
 * @description 云端Serving：批量服务管理远程调用熔断类
 * @date 2021-03-09
 */
public class BatchServingClientFallback implements BatchServingClient {
    @Override
    public DataResponseBody<Boolean> getServingModelStatus(PtModelStatusQueryDTO ptModelStatusQueryDTO) {
        return DataResponseFactory.failed("call dubhe-serving server BatchServingClient:getServingModelStatus error");
    }
}