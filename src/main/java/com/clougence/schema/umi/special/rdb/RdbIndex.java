package com.clougence.schema.umi.special.rdb;
import com.clougence.schema.umi.UniSchemaAttributes;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RdbIndex {
    private String              name;
    private String              type;
    private List<String>        columnList = new ArrayList<>();
    private UniSchemaAttributes attributes = new UniSchemaAttributes();
}
