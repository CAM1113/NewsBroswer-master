package com.example.newsbroswer.interfaces;

import com.example.newsbroswer.beans.json_beans.EvalutionResult;

/**
 * Created by 王灿 on 2018/7/16.
 */

public interface DianZanClickListener {
    void cancalDianZan(EvalutionResult.Evalution evalution);
    void dianZan(EvalutionResult.Evalution evalution);
}
