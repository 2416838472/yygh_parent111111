package com.atguigu.yygh.cmn;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {


    public static void main(String[] args) {
        List<test> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            test test = new test();
            test.setSno(i);
            test.setSname("lucy" + i);
            list.add(test);
            String filePath = "D:\\test\\write.xlsx";   //写入的文件路径

            EasyExcel.write(filePath, test.class)
                    .sheet("学生列表").doWrite(list);

        }
    }
}
