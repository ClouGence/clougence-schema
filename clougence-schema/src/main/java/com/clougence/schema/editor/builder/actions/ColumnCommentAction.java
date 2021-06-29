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
package com.clougence.schema.editor.builder.actions;
import com.clougence.schema.editor.domain.EColumn;
import lombok.Getter;

import java.util.List;

/**
 * @author mode 2021/6/8 19:56
 */
@Getter
public class ColumnCommentAction extends Action {
    private final EColumn columnInfo;
    private final String  comment;

    public ColumnCommentAction(List<String> sqlString, String catalog, String schema, String table, EColumn columnInfo, String comment) {
        super(sqlString, catalog, schema, table);
        this.columnInfo = columnInfo.clone();
        this.comment = comment;
    }
}