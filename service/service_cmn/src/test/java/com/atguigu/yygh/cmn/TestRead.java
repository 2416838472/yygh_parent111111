package com.atguigu.yygh.cmn;

import com.alibaba.excel.EasyExcel;

public class TestRead {

    public static void main(String[] args) {
        String fileName = "D:\\test\\write.xlsx";
        //调用方法实现读取操作
        EasyExcel.read(fileName, test.class,
                new ExcelListener()).sheet().doRead();
    }
}
