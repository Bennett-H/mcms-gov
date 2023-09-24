/**
 * 版权所有  铭软科技(mingsoft.net)
 * 未经授权，严禁通过任何介质未经授权复制此文件
 */
package net.mingsoft.impexp.constant.e;

import net.mingsoft.base.constant.e.BaseEnum;

/**
 * id增长类型
 */
public enum IdEnum implements BaseEnum {
    SNOW("snow"),
    AUTO("auto")
    ;

    private String str;
    IdEnum(String str) {
        this.str = str;
    }

    public String toString(){
        return this.str;
    }
    @Override
    public int toInt() {
        return 0;
    }
}
