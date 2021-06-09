package com.clougence.schema.umi.special.rdb;
import com.clougence.schema.umi.constraint.Primary;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RdbPrimaryKey extends Primary {
    private List<String> columnList = new ArrayList<>();
}
