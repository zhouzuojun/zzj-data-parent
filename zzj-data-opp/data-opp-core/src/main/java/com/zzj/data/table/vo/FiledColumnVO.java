package com.zzj.data.table.vo;

import com.zzj.data.params.FieldObj;

import java.util.List;

public class FiledColumnVO   {
    private FieldObj field;
    private List<Object> columnData;

    public FieldObj getField() {
        return field;
    }

    public void setField(FieldObj field) {
        this.field = field;
    }

    public List<Object> getColumnData() {
        return columnData;
    }

    public void setColumnData(List<Object> columnData) {
        this.columnData = columnData;
    }
}
