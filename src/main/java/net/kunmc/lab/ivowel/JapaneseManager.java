package net.kunmc.lab.ivowel;

import com.atilika.kuromoji.TokenizerBase;
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import dev.felnull.fnjl.tuple.FNPair;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class JapaneseManager {
    private static final char[] HANKAKU_KATAKANA = {'｡', '｢', '｣', '､', '･',
            'ｦ', 'ｧ', 'ｨ', 'ｩ', 'ｪ', 'ｫ', 'ｬ', 'ｭ', 'ｮ', 'ｯ', 'ｰ', 'ｱ', 'ｲ',
            'ｳ', 'ｴ', 'ｵ', 'ｶ', 'ｷ', 'ｸ', 'ｹ', 'ｺ', 'ｻ', 'ｼ', 'ｽ', 'ｾ', 'ｿ',
            'ﾀ', 'ﾁ', 'ﾂ', 'ﾃ', 'ﾄ', 'ﾅ', 'ﾆ', 'ﾇ', 'ﾈ', 'ﾉ', 'ﾊ', 'ﾋ', 'ﾌ',
            'ﾍ', 'ﾎ', 'ﾏ', 'ﾐ', 'ﾑ', 'ﾒ', 'ﾓ', 'ﾔ', 'ﾕ', 'ﾖ', 'ﾗ', 'ﾘ', 'ﾙ',
            'ﾚ', 'ﾛ', 'ﾜ', 'ﾝ', 'ﾞ', 'ﾟ'};

    private static final char[] ZENKAKU_KATAKANA = {'。', '「', '」', '、', '・',
            'ヲ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ッ', 'ー', 'ア', 'イ',
            'ウ', 'エ', 'オ', 'カ', 'キ', 'ク', 'ケ', 'コ', 'サ', 'シ', 'ス', 'セ', 'ソ',
            'タ', 'チ', 'ツ', 'テ', 'ト', 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ', 'ハ', 'ヒ', 'フ',
            'ヘ', 'ホ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ヤ', 'ユ', 'ヨ', 'ラ', 'リ', 'ル',
            'レ', 'ロ', 'ワ', 'ン', '゛', '゜'};
    private static final char HANKAKU_KATAKANA_FIRST_CHAR = HANKAKU_KATAKANA[0];

    private static final char HANKAKU_KATAKANA_LAST_CHAR = HANKAKU_KATAKANA[HANKAKU_KATAKANA.length - 1];
    private static final String VOWEL = "aiueon";
    private static final String MIN_HIRAGANA = "ぁぃぅぇぉっゃゅょゎ";
    private static final String MIN_HIRAGANA_UPPER = "あいうえおつやゆよわ";
    private static final String MIN_KATAKANA = "ァィゥェォヵㇰヶㇱㇲッㇳㇴㇵㇶㇷㇸㇹㇺャュョㇻㇼㇽㇾㇿヮ";
    private static final JapaneseManager INSTANCE = new JapaneseManager();
    private final Map<String, String> ROMAJIS = new HashMap<>();

    public static JapaneseManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        InputStream stream = resourceExtractor(JapaneseManager.class, "ivowel/romaji.csv");
        if (stream == null) return;
        byte[] rc = null;
        try {
            rc = streamToByteArray(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rc == null) return;
        String tx = new String(rc, StandardCharsets.UTF_8);
        String[] sp = tx.split("\n");
        for (String s : sp) {
            String[] spp = s.split(",");
            ROMAJIS.put(spp[0], spp[1]);
        }
        if (ROMAJIS.isEmpty())
            throw new IllegalStateException("romaji load failure");
    }

    public String toHiragana(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c >= 'ァ' && c <= 'ン') {
                sb.setCharAt(i, (char) (c - 'ァ' + 'ぁ'));
            } else if (c == 'ヵ') {
                sb.setCharAt(i, 'か');
            } else if (c == 'ヶ') {
                sb.setCharAt(i, 'け');
            } else if (c == 'ヴ') {
                sb.setCharAt(i, 'う');
                sb.insert(i + 1, '゛');
                i++;
            }
        }
        return sb.toString();
    }

    public String getRomaji(char hiragana) {
        return getRomaji(String.valueOf(hiragana));
    }

    public String getRomaji(String hiragana) {
        return ROMAJIS.entrySet().stream().filter(n -> n.getValue().equals(hiragana)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public String getHiragana(String romaji) {
        String hr = ROMAJIS.get(romaji);
        if (hr == null && romaji.length() == 3 && romaji.charAt(0) == romaji.charAt(1)) {
            String shr = ROMAJIS.get(romaji.substring(1));
            if (shr != null) return "っ" + shr;
        }
        return hr;
    }

    public boolean isVowel(char chr) {
        String romaji = getRomaji(chr);
        if (romaji == null || romaji.length() != 1) return false;
        return VOWEL.contains(romaji);
    }

    public String convertVowel(char str, boolean min) {
        boolean mi = MIN_HIRAGANA.indexOf(str) >= 0;
        if (mi && min)
            str = MIN_HIRAGANA_UPPER.charAt(MIN_HIRAGANA.indexOf(str));
        if (isVowel(str)) return String.valueOf(str);
        String ro = getRomaji(str);
        if (ro != null && ro.length() > 1) {
            String hr = getHiragana(ro.substring(ro.length() - 1));
            if (mi && min)
                hr = String.valueOf(MIN_HIRAGANA.charAt(MIN_HIRAGANA_UPPER.indexOf(hr)));
            return hr;
        }
        return null;
    }

    public FNPair<String, String> convertVowelOnly(String text, boolean nonThrow, LeaveType... types) {
        List<LeaveType> type = Arrays.asList(types);
        text = convertHiragana(text, nonThrow);
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            String cs = convertVowel(c, type.contains(LeaveType.MIN));
            if (cs != null)
                sb.append(cs);
            else if (type.contains(LeaveType.NOT_CNV))
                sb.append(c);
        }
        return FNPair.of(sb.toString(), text);
    }

    public static enum LeaveType {
        MIN, NOT_CNV;
    }

    public String convertHiragana(String text, boolean nonThrow) {
        text = hankakuKatakanaToZenkakuKatakana(text);
        text = toHiragana(text);
        text = text.toLowerCase(Locale.ROOT);
        text = toHiraganaByRomaji(text, nonThrow);
        if (!isHiragana(text)) {
            text = toKana(text, nonThrow);
            text = hankakuKatakanaToZenkakuKatakana(text);
            text = toHiragana(text);
        }
        return text;
    }

    private String toHiraganaByRomaji(String text, boolean nonThrow) {
        StringBuilder sb = new StringBuilder();
        StringBuilder ssb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (!Pattern.compile("^[A-Za-z]+$").matcher(String.valueOf(c)).matches()) {
                ssb = new StringBuilder();
                sb.append(c);
                continue;
            }
            ssb.append(c);
            String hr = getHiragana(ssb.toString());
            if (hr != null) {
                sb.append(hr);
                ssb = new StringBuilder();
            }
        }
        sb.append(ssb);
        if (!nonThrow && !ssb.toString().isEmpty()) {
            throw new IllegalStateException("変換できない文字がありました");
        }
        return sb.toString();
    }


    private static final Pattern pattern = Pattern.compile("^[\\u3040-\\u309F!?！？\\-ー\\[\\]「」【】（）()#%。.~～]+$");

    public boolean isHiragana(String value) {
        return pattern.matcher(value).matches();
    }

    public static char hankakuKatakanaToZenkakuKatakana(char c) {
        if (c >= HANKAKU_KATAKANA_FIRST_CHAR && c <= HANKAKU_KATAKANA_LAST_CHAR) {
            return ZENKAKU_KATAKANA[c - HANKAKU_KATAKANA_FIRST_CHAR];
        } else {
            return c;
        }
    }

    public String hankakuKatakanaToZenkakuKatakana(String s) {
        if (s.length() == 0) {
            return s;
        } else if (s.length() == 1) {
            return hankakuKatakanaToZenkakuKatakana(s.charAt(0)) + "";
        } else {
            StringBuffer sb = new StringBuffer(s);
            int i = 0;
            for (i = 0; i < sb.length() - 1; i++) {
                char originalChar1 = sb.charAt(i);
                char originalChar2 = sb.charAt(i + 1);
                char margedChar = mergeChar(originalChar1, originalChar2);
                if (margedChar != originalChar1) {
                    sb.setCharAt(i, margedChar);
                    sb.deleteCharAt(i + 1);
                } else {
                    char convertedChar = hankakuKatakanaToZenkakuKatakana(originalChar1);
                    if (convertedChar != originalChar1) {
                        sb.setCharAt(i, convertedChar);
                    }
                }
            }
            if (i < sb.length()) {
                char originalChar1 = sb.charAt(i);
                char convertedChar = hankakuKatakanaToZenkakuKatakana(originalChar1);
                if (convertedChar != originalChar1) {
                    sb.setCharAt(i, convertedChar);
                }
            }
            return sb.toString();
        }

    }

    public char mergeChar(char c1, char c2) {
        if (c2 == 'ﾞ') {
            if ("ｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾊﾋﾌﾍﾎ".indexOf(c1) > 0) {
                switch (c1) {
                    case 'ｶ':
                        return 'ガ';
                    case 'ｷ':
                        return 'ギ';
                    case 'ｸ':
                        return 'グ';
                    case 'ｹ':
                        return 'ゲ';
                    case 'ｺ':
                        return 'ゴ';
                    case 'ｻ':
                        return 'ザ';
                    case 'ｼ':
                        return 'ジ';
                    case 'ｽ':
                        return 'ズ';
                    case 'ｾ':
                        return 'ゼ';
                    case 'ｿ':
                        return 'ゾ';
                    case 'ﾀ':
                        return 'ダ';
                    case 'ﾁ':
                        return 'ヂ';
                    case 'ﾂ':
                        return 'ヅ';
                    case 'ﾃ':
                        return 'デ';
                    case 'ﾄ':
                        return 'ド';
                    case 'ﾊ':
                        return 'バ';
                    case 'ﾋ':
                        return 'ビ';
                    case 'ﾌ':
                        return 'ブ';
                    case 'ﾍ':
                        return 'ベ';
                    case 'ﾎ':
                        return 'ボ';
                }
            }
        } else if (c2 == 'ﾟ') {
            if ("ﾊﾋﾌﾍﾎ".indexOf(c1) > 0) {
                switch (c1) {
                    case 'ﾊ':
                        return 'パ';
                    case 'ﾋ':
                        return 'ピ';
                    case 'ﾌ':
                        return 'プ';
                    case 'ﾍ':
                        return 'ペ';
                    case 'ﾎ':
                        return 'ポ';
                }
            }
        }
        return c1;
    }

    private String toKana(String targetValue, boolean nonThrow) {
        Tokenizer.Builder builder = new Tokenizer.Builder();
        builder.mode(TokenizerBase.Mode.NORMAL);

        Tokenizer tokenizer = builder.build();
        List<Token> tokens = tokenizer.tokenize(targetValue);
        StringBuilder returnValue = new StringBuilder();
        for (Token token : tokens) {
            String tx = token.getReading();
            if (!"*".equals(tx)) {
                returnValue.append(tx);
            } else if (!nonThrow) {
                throw new IllegalStateException("変換できない文字がありました");
            }
        }
        return returnValue.toString();
    }

    private static InputStream resourceExtractor(Class<?> targetClass, String path) {
        InputStream stream = targetClass.getResourceAsStream("/" + path);
        if (stream == null)
            stream = ClassLoader.getSystemResourceAsStream(path);
        return stream != null ? new BufferedInputStream(stream) : null;
    }

    private static byte[] streamToByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int len = stream.read(buffer);
            if (len < 0) {
                break;
            }
            bout.write(buffer, 0, len);
        }
        return bout.toByteArray();
    }
}
