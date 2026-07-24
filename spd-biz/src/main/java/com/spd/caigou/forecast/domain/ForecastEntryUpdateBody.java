package com.spd.caigou.forecast.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * 批量更新建议行确认量/勾选
 */
public class ForecastEntryUpdateBody {

    private List<Item> entries;

    public List<Item> getEntries() { return entries; }
    public void setEntries(List<Item> entries) { this.entries = entries; }

    public static class Item {
        private Long id;
        private BigDecimal confirmQty;
        private String selected;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public BigDecimal getConfirmQty() { return confirmQty; }
        public void setConfirmQty(BigDecimal confirmQty) { this.confirmQty = confirmQty; }
        public String getSelected() { return selected; }
        public void setSelected(String selected) { this.selected = selected; }
    }
}
