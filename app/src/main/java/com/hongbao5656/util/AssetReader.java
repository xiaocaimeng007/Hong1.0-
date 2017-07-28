package com.hongbao5656.util;

import android.content.Context;


import com.hongbao5656.base.App;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class AssetReader<K, T> {

    /**
     * 初始化资源
     */
    public HashMap<K, T> read() {
        InputStream inputStream = null;
        HashMap<K, T> configs = null;
        try {
            Context context = App.getAppContext();
            configs = new HashMap<>();
            Config config = getClass().getAnnotation(Config.class);
            if (null != config) {
                String[] values = config.value();
                if (0 < values.length) {
                    inputStream = context.getResources().getAssets().open(values[0]);
                    if (null != inputStream) {
                        XmlParser.ParserListener listener = getParserListener(configs);
                        if (null != listener) {
                            XmlParser.startParser(inputStream, listener);
                        } else {
                            throw new NullPointerException("listener 不能为空!");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(inputStream);
        }
        return configs;
    }

    /**
     * 读取配置项
     *
     * @return
     */
    public abstract XmlParser.ParserListener getParserListener(HashMap<K, T> configs);
}
