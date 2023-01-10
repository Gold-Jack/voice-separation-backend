package com.voice.separation.util;

/**
 * 响应码枚举类
 * 可通过static方法引入ResponseCode，并直接使用CODE
 * @author GoldJack
 * @since 2022/8/16
 */
public enum ResponseCode {

    /**
     * 枚举 响应码
     * 1xx : Systematic error
     *
     * 2xx : ok or warning
     *      200 : OK
     *
     * 3xx : database error
     *      30x : username/password error
     *      31x : manipulating database error
     *      38x : permission or authorization error
     * 4xx : frontend-backend interaction error
     *      404 : 404 not found
     */

    CODE_100("系统错误或异常"),
    CODE_101("文件上传失败"),
    CODE_102("文件下载失败"),

    CODE_200("成功OK"),
    CODE_210("单人声-噪声分离失败"),
    CODE_220("两人声分离失败"),

    CODE_300("密码错误"),
    CODE_301("用户名重复"),
    CODE_302("用户不存在"),
    CODE_303("注销失败"),

    CODE_310("更新失败"),
    CODE_311("查询失败"),
    CODE_312("删除失败"),
    CODE_313("持久化失败"),
    CODE_314("持久化对象存在可疑字段"),

    CODE_320("权限不足"),

    CODE_404("404 not found"),

    ;

    private final String codeMsg;

    ResponseCode(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    /**
     * 获取响应码的说明信息
     * @return
     */
    public String getCodeMessage() {
        return codeMsg;
    }
}
