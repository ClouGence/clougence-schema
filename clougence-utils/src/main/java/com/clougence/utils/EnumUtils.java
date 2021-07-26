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
package com.clougence.utils;

/**
 *
 * @version : 2013-4-12
 * @author 赵永春 (zyc@hasor.net)
 */
public class EnumUtils {

    public static Enum<?> readEnum(String enumName, Class<?> enumType) {
        Class<Enum<?>> forEnum = (Class<Enum<?>>) enumType;
        for (Enum<?> item : forEnum.getEnumConstants()) {
            String enumValue = item.name().toLowerCase();
            if (enumValue.equals(enumName.toLowerCase())) {
                return item;
            }
        }
        return null;
    }
}
