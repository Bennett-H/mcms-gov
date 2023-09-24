package net.mingsoft.qa.bean;

/**
 * 具体投票表单的字段信息bean
 */
public class FieldBean {
    /**
     * 选项名
     */
    private String name;
    /**
     * 选项类型
     */
    private String type;

    /**
     * 字段名
     */
    private String field;

    /**
     * 是否显示
     */
    private String isShow;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
