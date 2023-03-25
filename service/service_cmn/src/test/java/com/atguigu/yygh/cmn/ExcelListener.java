package com.atguigu.yygh.cmn;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListener extends AnalysisEventListener<test> {
    @Override
    public void invoke(test test, AnalysisContext analysisContext) {
        System.out.println("****"+test);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {


    }

    @Override
    public void invokeHeadMap(Map<Integer,String> headMap, AnalysisContext context){
        System.out.println("表头信息:"+headMap);
    }
}
