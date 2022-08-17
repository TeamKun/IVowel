package net.kunmc.lab.ivowel;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IVUtils {
    public static String replaceAll(String templateText, Pattern pattern, Function<Matcher, String> replacer) {
        Matcher matcher = pattern.matcher(templateText);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacer.apply(matcher));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
