/*
 * Copyright (c) 2004, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */
package com.clougence.schema.metadata.domain.rdb.postgres.driver;

public interface PgVersion {

    /**
     * Get a machine-readable version number.
     * @return the version in numeric XXYYZZ form, e.g. 90401 for 9.4.1
     */
    int getVersionNum();
}
