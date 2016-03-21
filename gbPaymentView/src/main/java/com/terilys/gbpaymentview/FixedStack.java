package com.terilys.gbpaymentview;

import java.util.Stack;

/**
 * 项目名称：GBPaymentView
 * 类描述：
 * 创建人：terilys
 * 创建时间：16/3/21 下午6:04
 * 修改人：terilys
 * 修改时间：16/3/21 下午6:04
 * 修改备注：
 *
 * @VERSION
 */
public class FixedStack<T> extends Stack<T> {

    int maxSize = 0;

    @Override public T push(T object) {
        if (maxSize > size()) {
            return super.push(object);
        }

        return object;
    }

    public String getText(){
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i <size() ; i++) {
            sb.append(get(i));
        }
        return sb.toString();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}

