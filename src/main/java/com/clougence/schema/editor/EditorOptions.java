package com.clougence.schema.editor;

import com.clougence.schema.metadata.CaseSensitivityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditorOptions {

    private CaseSensitivityType caseSensitivity;
    private boolean             useDelimited    = false;
    private boolean             includeAffected = true;
}
