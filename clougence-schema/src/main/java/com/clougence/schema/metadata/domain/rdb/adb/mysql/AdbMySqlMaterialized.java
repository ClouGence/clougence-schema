/*
 * Copyright 2008-2009 the original author or authors.
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
 */
package com.clougence.schema.metadata.domain.rdb.adb.mysql;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * https://help.aliyun.com/document_detail/200653.html
 * AdbMySql Materialized View
 * @version : 2021-04-09
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class AdbMySqlMaterialized extends AdbMySqlTable {

    private Date   firstRefreshTime;
    private String nextRefreshTimeFunc;
    private String owner;
    private String queryRewriteEnabled;
    private String refreshCondition;
    private String refreshState;
}
