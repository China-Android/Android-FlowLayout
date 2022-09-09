package com.example.androidflowlayoutlibrary;

/**
 * @author HL
 * @Date 2022/9/9
 */
public class FlowDataBean {
    private String itemText;
    private int drawableId;

    public FlowDataBean(String itemText) {
        this.itemText = itemText;
    }

    public FlowDataBean(String itemText, int drawableId) {
        this.itemText = itemText;
        this.drawableId = drawableId;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
