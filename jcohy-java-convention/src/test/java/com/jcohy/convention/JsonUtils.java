package com.jcohy.convention;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:23:35
 * @since 0.0.5.1
 */
public class JsonUtils {

    private List<String> lines = new ArrayList<>();

    public List<String>  analysisJson(JSONObject objJson){
        JSONArray jsonArray = objJson.getJSONArray("files");

        jsonArray.stream().forEach(jsonobejct-> arrayIdToString((String) jsonobejct,this.lines));
        JSONArray dirs = objJson.getJSONArray("dirs");
        dirs.stream().forEach(jsonobejct -> analysisJson((JSONObject)jsonobejct));
        return this.lines;
    }

    private static void arrayIdToString(String str,List<String> lines) {
        lines.add(str);
    }
}
