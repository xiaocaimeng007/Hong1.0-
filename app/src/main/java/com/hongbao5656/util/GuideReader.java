package com.hongbao5656.util;

import android.text.TextUtils;
import com.hongbao5656.base.App;
import com.hongbao5656.view.guide.GuideItem;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cz on 15/9/17.
 */
@Config("config/guide_config.xml")
public class GuideReader extends AssetReader<String, ArrayList<GuideItem>> {
    private static final ArrayList<String> ALL_GRAVITY;
    Pattern pattern = Pattern.compile("@(.+)/(.+)");

    static {
//        初如化所有方向映射
        ALL_GRAVITY = new ArrayList<>();
        ALL_GRAVITY.add("left_top");
        ALL_GRAVITY.add("left_center");
        ALL_GRAVITY.add("left_bottom");
        ALL_GRAVITY.add("top_left");
        ALL_GRAVITY.add("top_center");
        ALL_GRAVITY.add("top_right");
        ALL_GRAVITY.add("right_top");
        ALL_GRAVITY.add("right_center");
        ALL_GRAVITY.add("right_bottom");
        ALL_GRAVITY.add("bottom_left");
        ALL_GRAVITY.add("bottom_center");
        ALL_GRAVITY.add("bottom_right");
        ALL_GRAVITY.add("left_top_center");
        ALL_GRAVITY.add("right_top_center");
        ALL_GRAVITY.add("left_bottom_center");
        ALL_GRAVITY.add("right_bottom_center");

    }

    @Override
    public XmlParser.ParserListener getParserListener(HashMap<String, ArrayList<GuideItem>> configs) {
        return new XmlParser.ParserListener() {
            private String name;

            @Override
            public void startParser(XmlPullParser parser) {
                String tagName = parser.getName();
                if ("guide".equals(tagName)) {
                    XmlParser.runParser(parser, (String... ts) -> {
                        if ("name".equals(ts[0])) {
                            //记录引导名
                            this.name = ts[1];
                            configs.put(name, new ArrayList<>());
                        }
                    });
                } else if ("item".equals(tagName)) {
                    final GuideItem item = new GuideItem();
                    XmlParser.runParser(parser, (String... ts) -> {
                        if ("id".equals(ts[0])) {
                            item.id = ResUtils.id(ts[1]);
                        } else if ("list".equals(ts[0])) {
                            item.list = ResUtils.id(ts[1]);
                        } else if ("child".equals(ts[0])) {
                            item.child = Integer.valueOf(ts[1]);
                        } else if ("width".equals(ts[0])) {
                            item.width = getDevicePixels(ts[1]);
                        } else if ("height".equals(ts[0])) {
                            item.height = getDevicePixels(ts[1]);
                        } else if ("guide".equals(ts[0])) {
                            item.guideRes = getDrawable(ts[1]);
                        } else if ("mask".equals(ts[0])) {
                            item.maskRes = getDrawable(ts[1]);
                        } else if ("gravity".equals(ts[0])) {
                            item.gravity = getGravity(ts[1]);
                        } else if ("xOffset".equals(ts[0])) {
                            item.xOffset = getDevicePixels(ts[1]);
                        } else if ("yOffset".equals(ts[0])) {
                            item.yOffset = getDevicePixels(ts[1]);
                        } else if ("padding".equals(ts[0])) {
                            item.padding = getDevicePixels(ts[1]);
                        } else if ("infoWidth".equals(ts[0])) {
                            item.infoWidth = getDevicePixels(ts[1]);
                        } else if ("infoHeight".equals(ts[0])) {
                            item.infoHeight = getDevicePixels(ts[1]);
                        }
                    });
                    configs.get(name).add(item);
                }
            }

            @Override
            public void endParser(XmlPullParser parser) {
            }
        };
    }

    /**
     * 获得dp取值
     *
     * @param value
     * @return
     */
    private int getDevicePixels(String value) {
        int dp;
        try {
            dp = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            dp = 0;
        }
        return App.dip2px(dp);
    }

    private int getDrawable(String resValue) {
        int res = -1;
        Matcher matcher = pattern.matcher(resValue);
        if (matcher.find()) {
            String value = matcher.group(2);
            if (!TextUtils.isEmpty(value)) {
                res = ResUtils.drawable(value);
            }
        }
        return res;
    }

    public int getGravity(String value) {
        int index = ALL_GRAVITY.indexOf(value);
        return -1 == index ? 0 : index;
    }
}
