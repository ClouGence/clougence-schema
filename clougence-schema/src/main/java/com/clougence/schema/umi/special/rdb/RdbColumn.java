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
package com.clougence.schema.umi.special.rdb;

import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbColumnSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * 结构类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
@Getter
@Setter
@JsonSerialize(using = RdbColumn.JacksonSerializer.class)
@JsonDeserialize(using = RdbColumn.JacksonDeserializer.class)
public class RdbColumn extends ValueUmiSchema {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<RdbColumn> {

        public JacksonDeserializer(){
            super(new RdbColumnSerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<RdbColumn> {

        public JacksonSerializer(){
            super(new RdbColumnSerializer());
        }
    }

    private Long    charLength;
    private Long    byteLength;
    private Integer numericPrecision;
    private Integer numericScale;
    private Integer datetimePrecision;

}
