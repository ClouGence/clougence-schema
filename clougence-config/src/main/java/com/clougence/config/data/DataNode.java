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
package com.clougence.config.data;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.clougence.utils.StringUtils;
import com.clougence.config.Settings;
import com.clougence.config.UpdateValue;

/**
 * @version : 2014年10月11日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DataNode {

    private final TreeNode     parent;
    private final List<String> allVarList = new CopyOnWriteArrayList<>();

    public DataNode(TreeNode parent){
        this.parent = parent;
    }

    public String getValue() {
        if (this.allVarList.isEmpty()) {
            return null;
        } else {
            return allVarList.get(this.allVarList.size() - 1);
        }
    }

    public String[] getValues() { return this.allVarList.toArray(new String[0]); }

    public void setValue(String... values) {
        this.allVarList.clear();
        this.addValue(values);
    }

    public void addValue(String... values) {
        if (values != null && values.length > 0) {
            this.allVarList.addAll(Arrays.asList(values));
        }
    }

    public void clear() {
        this.allVarList.clear();
    }

    public void update(UpdateValue updateValue, Settings context) {
        updateValue.update(this, context);
    }

    public void replace(int index, String newVar) {
        if (index >= this.allVarList.size()) {
            return;
        }
        this.allVarList.set(index, newVar);
    }

    public String toString() {
        return "[" + StringUtils.join(this.allVarList.toArray(), ",") + "]";
    }

    public boolean isEmpty() { return this.allVarList.isEmpty(); }
}
