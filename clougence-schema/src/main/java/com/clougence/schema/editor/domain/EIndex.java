/*
 * Copyright 2002-2010 the original author or authors.
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
package com.clougence.schema.editor.domain;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mode 2021/5/21 19:56
 */
@Getter
@Setter
public class EIndex extends EChange<EIndex> {

    private String              name;
    private EIndexType          type;
    private List<String>        columnList = new ArrayList<>();
    private Map<String, String> attribute  = new HashMap<>();

    @Override
    public EIndex clone() {
        EIndex eIndex = new EIndex();
        eIndex.name = this.name;
        eIndex.type = this.type;
        //
        eIndex.columnList.addAll(this.columnList);
        eIndex.attribute.putAll(this.attribute);
        return eIndex;
    }

    @Override
    boolean testChanged(EIndex initData) {
        if (initData == null) {
            return true;
        }
        if (Objects.equals(this.name, initData.name)) {
            return true;
        }
        if (this.type == initData.type) {
            return true;
        }
        if (Objects.deepEquals(this.columnList, initData.columnList)) {
            return true;
        }
        if (EditorUtils.testAttribute(this.attribute, initData.attribute)) {
            return true;
        }
        return false;
    }
}
