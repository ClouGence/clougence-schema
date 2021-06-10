/*
 * Copyright 2008-2009 the original author or authors.
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
package com.clougence.schema.umi;
import com.clougence.schema.metadata.FieldType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
@Getter
@Setter
public abstract class AbstractUmiSchema implements UmiSchema {
    private String              name;
    private String              comment;
    private FieldType           typeDef;
    private String[]            parentPath;
    private List<UmiConstraint> constraints = new ArrayList<>();
    private UniSchemaAttributes attributes  = new UniSchemaAttributes();

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public String[] getPath() {
        if (this.parentPath == null) {
            return new String[] { this.name };
        }
        List<String> stringList = Arrays.asList(this.parentPath);
        stringList.add(this.name);
        return stringList.toArray(new String[0]);
    }

    @Override
    public List<UmiConstraint> getConstraints() {
        return this.constraints;
    }

    @Override
    public UniSchemaAttributes getAttributes() {
        return this.attributes;
    }

    public <T extends UmiConstraint> boolean hasConstraint(Class<T> classType) {
        return !getConstraints(classType).isEmpty();
    }

    public boolean hasConstraint(UmiConstraintType constraintType) {
        return !getConstraints(constraintType).isEmpty();
    }

    public <T extends UmiConstraint> List<T> getConstraints(Class<T> classType) {
        return this.getConstraints().stream().filter(classType::isInstance).map(c -> {
            return (T) c;
        }).collect(Collectors.toList());
    }

    public List<UmiConstraint> getConstraints(UmiConstraintType constraintType) {
        return this.getConstraints().stream().filter(c -> {
            return constraintType == c.getType();
        }).collect(Collectors.toList());
    }

    public <T extends UmiConstraint> List<T> getConstraints(Class<T> classType, UmiConstraintType constraintType) {
        return this.getConstraints().stream().filter(c -> {
            return classType.isInstance(c) && constraintType == c.getType();
        }).map(c -> {
            return (T) c;
        }).collect(Collectors.toList());
    }

    protected <T extends UmiConstraint> void overwriteConstraint(Class<T> classType, List<T> values) {
        List<T> constraints = getConstraints(classType);
        for (T constraint : constraints) {
            getConstraints().remove(constraint);
        }
        getConstraints().addAll(values);
    }

    protected void overwriteConstraint(UmiConstraintType constraintType, List<UmiConstraint> values) {
        List<UmiConstraint> constraints = getConstraints(constraintType);
        for (UmiConstraint constraint : constraints) {
            getConstraints().remove(constraint);
        }
        getConstraints().addAll(values);
    }

    protected <T extends UmiConstraint> void overwriteConstraint(Class<T> classType, UmiConstraintType constraintType, List<T> values) {
        List<T> constraints = getConstraints(classType, constraintType);
        for (T constraint : constraints) {
            getConstraints().remove(constraint);
        }
        getConstraints().addAll(values);
    }
}