/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clougence.schema.editor.domain;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author mode 2021/5/21 19:56
 */
@Getter
@Setter
public class EColumn extends EChange<EColumn> {
    private String              name;
    private String              dbType;
    private boolean             nullable;
    private Long                length;
    private Integer             numericPrecision;
    private Integer             numericScale;
    private Integer             datetimePrecision;
    private String              defaultValue;
    private boolean             autoGenerate;
    private String              comment;
    private Map<String, String> attribute = new HashMap<>();

    @Override
    public EColumn clone() {
        EColumn eColumn = new EColumn();
        eColumn.name = this.name;
        eColumn.dbType = this.dbType;
        eColumn.nullable = this.nullable;
        eColumn.length = this.length;
        eColumn.numericPrecision = this.numericPrecision;
        eColumn.numericScale = this.numericScale;
        eColumn.datetimePrecision = this.datetimePrecision;
        eColumn.defaultValue = this.defaultValue;
        eColumn.autoGenerate = this.autoGenerate;
        eColumn.comment = this.comment;
        //
        eColumn.attribute.putAll(this.attribute);
        return eColumn;
    }

    boolean testChanged(EColumn initData) {
        if (initData == null) {
            return true;
        }
        if (!Objects.equals(this.name, initData.name)) {
            return true;
        }
        if (!Objects.equals(this.dbType, initData.dbType)) {
            return true;
        }
        if (this.nullable != initData.nullable) {
            return true;
        }
        if (!Objects.equals(this.length, initData.length)) {
            return true;
        }
        if (!Objects.equals(this.numericPrecision, initData.numericPrecision)) {
            return true;
        }
        if (!Objects.equals(this.numericScale, initData.numericScale)) {
            return true;
        }
        if (!Objects.equals(this.datetimePrecision, initData.datetimePrecision)) {
            return true;
        }
        if (!Objects.equals(this.defaultValue, initData.defaultValue)) {
            return true;
        }
        if (!Objects.equals(this.autoGenerate, initData.autoGenerate)) {
            return true;
        }
        if (!Objects.equals(this.comment, initData.comment)) {
            return true;
        }
        if (EditorUtils.testAttribute(this.attribute, initData.attribute)) {
            return true;
        }
        return false;
    }
}
