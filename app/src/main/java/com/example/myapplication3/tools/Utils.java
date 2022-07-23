package com.example.myapplication3.tools;

import com.topjohnwu.superuser.Shell;

import java.util.List;

/**
 * added by sharan
 */

public class Utils {
    Shell.Result result;

    public List<String> getResult(String... cmd) {
        result = Shell.cmd(cmd).exec();
        return result.getOut();
    }

//    public Object[] getResultInArray(String... cmd) {
//        result = Shell.cmd(cmd).exec();
//        return result.getOut().toArray();
//    }
}
