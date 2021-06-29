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
package com.clougence.schema.editor.triggers;
import com.clougence.schema.metadata.CaseSensitivityType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author mode 2021/5/21 19:56
 */
@Getter
@Setter
public class TriggerContext {
    private CaseSensitivityType plainCaseSensitivity;
    private CaseSensitivityType delimitedCaseSensitivity;
    private boolean             useDelimited;
    private boolean             cascade;
    private boolean             ifExists;
}
