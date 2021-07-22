/*
 * Copyright 2002-2010 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.clougence.schema.dialect.provider;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.clougence.commons.ResourcesUtils;
import com.clougence.commons.StringUtils;
import com.clougence.schema.dialect.Dialect;
import com.clougence.schema.umi.UmiSchema;
import net.hasor.utils.io.IOUtils;

/**
 * 公共 SqlDialect 实现
 * 
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractDialect implements Dialect {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDialect.class);
    private Set<String>         keyWords;

    @Override
    public final Set<String> keywords() {
        if (this.keyWords == null) {
            synchronized (this) {
                if (this.keyWords != null) {
                    return this.keyWords;
                }
                this.keyWords = new HashSet<>();
                String keyWordsResource = keyWordsResource();
                if (StringUtils.isBlank(keyWordsResource)) {
                    return this.keyWords;
                }
                try {
                    List<String> strings = IOUtils.readLines(ResourcesUtils.getResourceAsStream(keyWordsResource), StandardCharsets.UTF_8);
                    for (String term : strings) {
                        term = term.trim().toUpperCase();
                        if (!StringUtils.isBlank(term) && term.charAt(0) != '#') {
                            this.keyWords.add(term);
                        }
                    }
                } catch (Exception e) {
                    logger.error("load " + this.keywords() + ".keywords failed." + e.getMessage(), e);
                }
            }
        }
        return this.keyWords;
    }

    protected abstract String keyWordsResource();

    @Override
    public String tableName(boolean useQualifier, UmiSchema tableSchema) {
        List<String> columnSchemaPath = tableSchema.getPath();
        if (columnSchemaPath == null || columnSchemaPath.size() <= 1) {
            return fmtName(useQualifier, tableSchema.getName());
        } else {
            return fmtName(useQualifier, columnSchemaPath.get(columnSchemaPath.size() - 2)) + "." + fmtName(useQualifier, tableSchema.getName());
        }
    }

    @Override
    public String columnName(boolean useQualifier, UmiSchema columnSchema) {
        return fmtName(useQualifier, columnSchema.getName());
    }

    protected String fmtName(boolean useQualifier, String fmtString) {
        if (this.keywords().contains(fmtString.toUpperCase())) {
            useQualifier = true;
        }
        String leftQualifier = useQualifier ? leftQualifier() : "";
        String rightQualifier = useQualifier ? rightQualifier() : "";
        return leftQualifier + fmtString + rightQualifier;
    }

    protected String defaultQualifier() {
        return "";
    }

    public String leftQualifier() {
        return this.defaultQualifier();
    }

    public String rightQualifier() {
        return this.defaultQualifier();
    }
}
