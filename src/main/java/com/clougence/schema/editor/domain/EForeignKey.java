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
import net.hasor.utils.StringUtils;

import java.util.*;

/**
 * @author mode 2021/5/21 19:56
 */
@Getter
@Setter
public class EForeignKey extends EChange<EForeignKey> {
    private String              name;
    private List<String>        columnList       = new ArrayList<>();
    private String              referenceSchema;
    private String              referenceTable;
    private Map<String, String> referenceMapping = new HashMap<>();
    private EForeignKeyRule     updateRule       = EForeignKeyRule.NoAction;
    private EForeignKeyRule     deleteRule       = EForeignKeyRule.NoAction;
    private Map<String, String> attribute        = new HashMap<>();

    @Override
    public EForeignKey clone() {
        EForeignKey eForeignKey = new EForeignKey();
        eForeignKey.name = this.name;
        eForeignKey.columnList.addAll(this.columnList);
        //
        eForeignKey.referenceSchema = this.referenceSchema;
        eForeignKey.referenceTable = this.referenceTable;
        //
        eForeignKey.referenceMapping.putAll(this.referenceMapping);
        //
        eForeignKey.updateRule = this.updateRule;
        eForeignKey.deleteRule = this.deleteRule;
        //
        eForeignKey.attribute.putAll(this.attribute);
        return eForeignKey;
    }

    boolean testChanged(EForeignKey initData) {
        if (initData == null) {
            return true;
        }
        if (!StringUtils.equals(this.name, initData.name)) {
            return true;
        }
        if (Objects.deepEquals(this.columnList, initData.columnList)) {
            return true;
        }
        if (Objects.equals(this.referenceSchema, initData.referenceSchema)) {
            return true;
        }
        if (Objects.equals(this.referenceTable, initData.referenceTable)) {
            return true;
        }
        if (Objects.equals(this.updateRule, initData.updateRule)) {
            return true;
        }
        if (Objects.equals(this.deleteRule, initData.deleteRule)) {
            return true;
        }
        if (EditorUtils.testAttribute(this.referenceMapping, initData.referenceMapping)) {
            return true;
        }
        if (EditorUtils.testAttribute(this.attribute, initData.attribute)) {
            return true;
        }
        return false;
    }
}
