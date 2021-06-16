package com.clougence.schema.editor;
import com.clougence.schema.DsType;
import com.clougence.schema.editor.builder.actions.Action;
import com.clougence.schema.editor.provider.BuilderProvider;
import com.clougence.schema.editor.provider.EditorProviderRegister;
import com.clougence.schema.metadata.CaseSensitivityType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EditorContext {
    private final BuilderProvider     builderProvider;
    private       boolean             includeAffected;
    private final List<Action>        actions = new ArrayList<>();
    private       CaseSensitivityType plainCaseSensitivity;
    private       CaseSensitivityType delimitedCaseSensitivity;
    private       boolean             useDelimited;
    private       boolean             cascade;

    public EditorContext(DsType dsType) {
        this.builderProvider = EditorProviderRegister.findProvider(dsType);
    }
}
