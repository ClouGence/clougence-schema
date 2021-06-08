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
import com.clougence.schema.DataSourceType;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author mode 2021/5/21 19:56
 */
@Getter
@Setter
public class EType extends EChange<EType> {
    private String         typeName;
    private DataSourceType dsType;

    @Override
    public EType clone() {
        EType eType = new EType();
        eType.typeName = this.typeName;
        eType.dsType = this.dsType;
        return eType;
    }

    @Override
    boolean testChanged(EType initData) {
        if (Objects.equals(this.typeName, initData.typeName)) {
            return true;
        }
        if (Objects.equals(this.dsType, initData.dsType)) {
            return true;
        }
        return false;
    }
}
