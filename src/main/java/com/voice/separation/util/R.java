package com.voice.separation.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.voice.separation.util.ResponseCode.*;


@Data
@NoArgsConstructor
public class R<T> {

    private ResponseCode code;
    private String msg;
    private T data;

    private static final ResponseCode DEFAULT_OK_CODE = CODE_200;

    public R setCode(ResponseCode code) {
        this.code = code;
        return this;
    }

    public R setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public R setData(T data) {
        this.data = data;
        return this;
    }

    public R(T data) {
        this.data = data;
    }

    /**
     * 不需要传递数据的成功响应
     * @return
     */
    public static R success() {
        R result = new R();
        result.setCode(DEFAULT_OK_CODE);
        result.setMsg(DEFAULT_OK_CODE.getCodeMessage());
        return result;
    }

    /**
     * 需要传递数据的成功响应
     * @param data
     * @return
     * @param <T>
     */
    public static <T> R<T> success(T data) {
        R result = new R();
        result.setData(data);
        result.setCode(DEFAULT_OK_CODE);
        result.setMsg(DEFAULT_OK_CODE.getCodeMessage());
        return result;
    }

    /**
     * 失败响应
     * @param errorCode 错误代码
     * @param errMsg    错误说明
     * @return
     */
    public static R error(ResponseCode errorCode, String errMsg) {
        R result = new R();
        result.setCode(errorCode);
        result.setMsg(errMsg);
        return result;
    }
}
