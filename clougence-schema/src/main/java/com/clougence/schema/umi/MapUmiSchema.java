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
package com.clougence.schema.umi;

import com.clougence.schema.metadata.FieldType;
import com.clougence.schema.umi.serializer.MapUmiSchemaSerializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * Map类型，和StrutsType类似但对 key 无限制
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
@Getter
@Setter
@JsonSerialize(using = MapUmiSchema.JacksonSerializer.class)
@JsonDeserialize(using = MapUmiSchema.JacksonDeserializer.class)
public class MapUmiSchema extends AbstractUmiSchema {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<MapUmiSchema> {

        public JacksonDeserializer(){
            super(new MapUmiSchemaSerializer<>());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<MapUmiSchema> {

        public JacksonSerializer(){
            super(new MapUmiSchemaSerializer<>());
        }
    }

    @Override
    public final FieldType getDataType() { return UmiStrutsTypes.Map; }

}
