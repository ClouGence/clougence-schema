package com.clougence.schema.umi.special.rdb;
import com.clougence.schema.umi.constraint.Unique;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RdbUniqueKey extends Unique {
    private List<String> columnList = new ArrayList<>();
}
