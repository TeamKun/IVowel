package net.kunmc.lab.ivowel;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.ibm.icu.text.Transliterator;
import com.mariten.kanatools.KanaConverter;
import dev.felnull.fnjl.tuple.FNPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class VowelManager {
    private static final VowelManager INSTANCE = new VowelManager();
    private static final Pattern HIRAGANA_PATTERN = Pattern.compile("^[\\u3040-\\u309F!?！？\\-ー\\[\\]「」【】（）()#%。.~～]+$");
    private static final String VOWELS = "aiueon";

    public static VowelManager getInstance() {
        return INSTANCE;
    }

    public FNPair<String, String> convertVowelOnly(String text, boolean nonThrow, LeaveType... types) {
        if (Config.hiraganaOnly && false) {
            if (!isHiragana(text))
                throw new IllegalStateException("平仮名のみ入力可能");
        }
        List<LeaveType> type = Arrays.asList(types);

        text = convertHiragana(text, nonThrow);
        String hira = text;

        text = replaceHiraganaToLatin(text);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (VOWELS.contains(String.valueOf(c)))
                sb.append(c);
        }

        return FNPair.of(sb.toString(), hira);
    }

    public String convertHiragana(String text, boolean nonThrow) {
        text = replaceLatinToHiragana(text);
        text = convertHiraganaByKanji(text);
        text = KanaConverter.convertKana(text, KanaConverter.OP_HAN_KATA_TO_ZEN_HIRA);
        text = KanaConverter.convertKana(text, KanaConverter.OP_ZEN_KATA_TO_ZEN_HIRA);

        return text;
    }

    private String replaceLatinToHiragana(String text) {
        Transliterator kataToHira = Transliterator.getInstance("Latin-Hiragana");
        return kataToHira.transliterate(text);
    }

    private String replaceHiraganaToLatin(String text) {
        Transliterator kataToHira = Transliterator.getInstance("Hiragana-Latin");
        return kataToHira.transliterate(text);
    }

    private boolean isHiragana(String value) {
        if (value.isEmpty()) return true;
        return HIRAGANA_PATTERN.matcher(value).matches();
    }

    private String convertHiraganaByKanji(String text) {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> list = tokenizer.tokenize(text);
        StringBuilder build = new StringBuilder();

        for (Token token : list) {
            String[] splits = token.getAllFeatures().split(",");
            if (splits[7].equals("*")) {
                build.append(token.getSurface());
            } else {
                build.append(splits[7]);
            }
        }
        return build.toString();
    }

    public static enum LeaveType {
        MIN, NOT_CNV;

        public static LeaveType[] current() {
            List<LeaveType> types = new ArrayList<>();
            if (Config.leaveLowercase)
                types.add(MIN);
            if (Config.leaveSymbol)
                types.add(NOT_CNV);
            return types.toArray(new LeaveType[0]);
        }
    }
}
