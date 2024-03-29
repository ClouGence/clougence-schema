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
package com.clougence.schema.metadata.provider.rdb;

import java.io.Closeable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import javax.sql.DataSource;

import com.clougence.utils.StringUtils;
import com.clougence.utils.convert.ConverterUtils;
import com.clougence.utils.function.EFunction;
import com.clougence.utils.function.ESupplier;
import com.clougence.schema.metadata.CaseSensitivityType;

/**
 * MetadataSupplier 系列的公共类。
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class AbstractMetadataProvider {

    protected final ESupplier<Connection, SQLException> connectSupplier;
    private CaseSensitivityType                         plainCaseSensitivityType;
    private CaseSensitivityType                         delimitedCaseSensitivityType;

    /** Connection will be proxy, Calling the close method in an AbstractMetadatasupplier subclass will be invalid. */
    public AbstractMetadataProvider(Connection connection){
        Connection conn = newProxyConnection(connection);
        this.connectSupplier = () -> conn;
    }

    /** Each time data is requested in the AbstractMetadatasupplier subclass a new Connection is created and then closed. */
    public AbstractMetadataProvider(DataSource dataSource){
        this.connectSupplier = dataSource::getConnection;
    }

    public CaseSensitivityType getPlain() throws SQLException {
        if (this.plainCaseSensitivityType == null) {
            try (Connection conn = this.connectSupplier.eGet()) {
                DatabaseMetaData metaData = conn.getMetaData();
                if (metaData.supportsMixedCaseIdentifiers()) {
                    this.plainCaseSensitivityType = CaseSensitivityType.Exact;
                } else if (metaData.storesUpperCaseIdentifiers()) {
                    this.plainCaseSensitivityType = CaseSensitivityType.Upper;
                } else if (metaData.storesLowerCaseIdentifiers()) {
                    this.plainCaseSensitivityType = CaseSensitivityType.Lower;
                } else if (metaData.storesMixedCaseIdentifiers()) {
                    this.plainCaseSensitivityType = CaseSensitivityType.Fuzzy;
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        return this.plainCaseSensitivityType;
    }

    public CaseSensitivityType getDelimited() throws SQLException {
        if (this.delimitedCaseSensitivityType == null) {
            try (Connection conn = this.connectSupplier.eGet()) {
                DatabaseMetaData metaData = conn.getMetaData();
                if (metaData.supportsMixedCaseQuotedIdentifiers()) {
                    this.delimitedCaseSensitivityType = CaseSensitivityType.Exact;
                } else if (metaData.storesUpperCaseQuotedIdentifiers()) {
                    this.delimitedCaseSensitivityType = CaseSensitivityType.Upper;
                } else if (metaData.storesLowerCaseQuotedIdentifiers()) {
                    this.delimitedCaseSensitivityType = CaseSensitivityType.Lower;
                } else if (metaData.storesMixedCaseQuotedIdentifiers()) {
                    this.delimitedCaseSensitivityType = CaseSensitivityType.Fuzzy;
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        return this.delimitedCaseSensitivityType;
    }

    public <R> R applySql(EFunction<Connection, R, SQLException> applySql) throws SQLException {
        try (Connection conn = this.connectSupplier.eGet()) {
            return applySql.eApply(conn);
        }
    }

    protected static Connection newProxyConnection(Connection connection) {
        CloseIsNothingInvocationHandler handler = new CloseIsNothingInvocationHandler(connection);
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class, Closeable.class }, handler);
    }

    /** Connection 接口代理，目的是拦截 close 方法，使其失效。 */
    private static class CloseIsNothingInvocationHandler implements InvocationHandler {

        private final Connection connection;

        CloseIsNothingInvocationHandler(Connection connection){
            this.connection = connection;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (method.getName().equals("getTargetConnection")) {
                return connection;
            } else if (method.getName().equals("toString")) {
                return this.connection.toString();
            } else if (method.getName().equals("equals")) {
                return proxy == args[0];
            } else if (method.getName().equals("hashCode")) {
                return System.identityHashCode(proxy);
            } else if (method.getName().equals("close")) {
                return null;
            }
            //
            try {
                return method.invoke(this.connection, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

    protected static String safeToString(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    protected static Integer safeToInteger(Object obj) {
        return (obj == null) ? null : (Integer) ConverterUtils.convert(Integer.class, obj);
    }

    protected static Long safeToLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return (Long) ConverterUtils.convert(Long.class, obj);
    }

    protected static Boolean safeToBoolean(Object obj) {
        return (obj == null) ? null : (Boolean) ConverterUtils.convert(Boolean.class, obj);
    }

    protected static Date safeToDate(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Date) {
            return (Date) obj;
        } else if (obj instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = ((ZonedDateTime) obj);
            return Timestamp.from(zonedDateTime.toInstant());
        } else if (obj instanceof OffsetDateTime) {
            ZonedDateTime zonedDateTime = ((OffsetDateTime) obj).atZoneSameInstant(ZoneOffset.systemDefault());
            return Timestamp.from(zonedDateTime.toInstant());
        } else if (obj instanceof OffsetTime) {
            ZonedDateTime zonedDateTime = ((OffsetTime) obj).atDate(LocalDate.ofEpochDay(0)).atZoneSameInstant(ZoneOffset.UTC);
            return Timestamp.from(zonedDateTime.toInstant());
        } else if (obj instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) obj);
        } else if (obj instanceof LocalDate) {
            LocalDateTime dateTime = LocalDateTime.of((LocalDate) obj, LocalTime.of(0, 0, 0, 0));
            return Timestamp.valueOf(dateTime);
        } else if (obj instanceof Number) {
            return new Date(((Number) obj).longValue());
        } else {
            throw new ClassCastException(obj.getClass() + " Type cannot be converted to Date");
        }
    }

    protected static String buildWhereIn(Collection<?> paramMap) {
        StringBuilder whereIn = new StringBuilder();
        whereIn.append("(");
        whereIn.append(StringUtils.repeat("?,", paramMap.size()));
        whereIn.deleteCharAt(whereIn.length() - 1);
        whereIn.append(")");
        return whereIn.toString();
    }

    protected static List<String> stringArray2List(String[] stringArray) {
        if (stringArray == null || stringArray.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<String> stringList = new ArrayList<>();
        for (String string : stringArray) {
            if (StringUtils.isNotBlank(string)) {
                stringList.add(string);
            }
        }
        return stringList;
    }
}
