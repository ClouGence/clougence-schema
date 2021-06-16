package com.clougence.schema.editor.provider;
import com.clougence.schema.DsType;
import com.clougence.schema.editor.triggers.*;

public interface BuilderProvider extends TableTriggers, ColumnTriggers, IndexTriggers, PrimaryKeyTriggers, ForeignKeyTriggers {
    public DsType getDataSourceType();
}
