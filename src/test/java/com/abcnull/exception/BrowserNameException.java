package com.abcnull.exception;

/**
 * @author abcnull@qq.com
 * @version 1.0.0
 * @date 2020/8/4 12:41
 */
public class BrowserNameException extends RuntimeException {
    public BrowserNameException() {
        super();
    }

    public BrowserNameException(String s) {
        super(s);
    }
}
