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
package com.clougence.schema.metadata.domain.storage.kudu;

import org.apache.kudu.ColumnSchema.Encoding;

/**
 * Kudu Encoding
 * @version : 2021-04-01
 * @author 赵永春 (zyc@hasor.net)
 */
public enum KuduEncoding {

    UNKNOWN("unknown", Encoding.UNKNOWN),
    AUTO_ENCODING("auto_encoding", Encoding.AUTO_ENCODING),
    PLAIN_ENCODING("plain_encoding", Encoding.PLAIN_ENCODING),
    PREFIX_ENCODING("prefix_encoding", Encoding.PREFIX_ENCODING),
    GROUP_VARINT("group_varint", Encoding.GROUP_VARINT),
    RLE("rle", Encoding.RLE),
    DICT_ENCODING("dict_encoding", Encoding.DICT_ENCODING),
    BIT_SHUFFLE("bit_shuffle", Encoding.BIT_SHUFFLE),;

    private String   codeKey;
    private Encoding encodingType;

    KuduEncoding(String codeKey, Encoding encodingType){
        this.codeKey = codeKey;
        this.encodingType = encodingType;
    }

    public String getCodeKey() {
        return codeKey;
    }

    public Encoding getEncodingType() {
        return encodingType;
    }
}
