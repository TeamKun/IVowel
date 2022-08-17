package net.kunmc.lab.ivowel;

import com.atilika.kuromoji.ipadic.Tokenizer;
import com.ibm.icu.text.Transliterator;
import com.mariten.kanatools.KanaConverter;

import java.util.regex.Pattern;

public class VowelConverter {
    private static final VowelConverter INSTANCE = new VowelConverter();
    private final char[] minHiras = {'っ', 'ゃ', 'ゅ', 'ょ', 'ゎ', 'ヵ', 'ヶ'};
    private final char[] minHiraVowels = {'ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ'};
    private final char[] vowels = {'a', 'i', 'u', 'e', 'o'};
    private final Pattern hiraganaRegex = Pattern.compile("([\u3040-\u309F]|ー|～)*");
    private final Pattern katakanaRegex = Pattern.compile("[ァ-ヶー]*");
    private final Pattern romajiRegex = Pattern.compile("([a-zA-Z]|'|~)*");
    private final Pattern numRegex = Pattern.compile("[0-9]*");
    private final Pattern symbolRegex = Pattern.compile("[0-9.!#$%&’*+/=?^_`{|}~～ー！？-]*");

    public static VowelConverter getInstance() {
        return INSTANCE;
    }

    public VowelResult convertVowelOnlyErrored(String text, boolean symbol) {
        VowelResult vr = convertVowelOnly(text, symbol);
        if (!Config.noError && vr.isError())
            throw new RuntimeException("Vowel convert error");
        return vr;
    }

    public VowelResult convertVowelOnly(String text, boolean symbol) {
        if (!symbol)
            text = symbolRegex.matcher(text).replaceAll("");

        text = IVUtils.replaceAll(text, romajiRegex, p -> convertRomajiToHiragana(p.group()));
        boolean[] error = new boolean[1];
        Tokenizer tokenizer = new Tokenizer();
        String[] strs = tokenizer.tokenize(text).stream().map(t -> {
            if ("*".equals(t.getReading())) {
                String val = convertKanaToAllHiragana(t.getSurface());
                boolean sysFlg = !symbol && symbolRegex.matcher(val).matches();

                if (sysFlg || (!symbolRegex.matcher(val).matches() && !numRegex.matcher(val).matches() && !hiraganaRegex.matcher(val).matches() && !katakanaRegex.matcher(val).matches()))
                    error[0] = true;
                return t.getSurface();
            }
            return t.getReading();
        }).toArray(String[]::new);
        String hira = convertKanaToAllHiragana(String.join("", strs));
        String romaji = IVUtils.replaceAll(hira, hiraganaRegex, t -> convertHiraganaToRomaji(t.group()));
        String vowel = IVUtils.replaceAll(romaji, romajiRegex, t -> convertRomajiToVowelOnly(t.group()));
        String vowelHira = convertRomajiToHiragana(vowel);

        String minHiraPre = convertHiraToMinHiraPre(convertKanaToAllHiragana(String.join("", strs)));
        String minRomajiPre = IVUtils.replaceAll(minHiraPre, hiraganaRegex, t -> convertHiraganaToRomaji(t.group()));
        String minVowel = IVUtils.replaceAll(minRomajiPre, romajiRegex, t -> convertRomajiToVowelOnly(t.group()));
        String minVowelHira = convertRomajiToHiragana(minVowel);

        return new VowelResult(vowel, hira, romaji, vowelHira, minVowelHira, error[0]);
    }

    public String convertKanaToAllHiragana(String text) {
        int flg = 0;
        flg |= KanaConverter.OP_HAN_KATA_TO_ZEN_HIRA;
        flg |= KanaConverter.OP_ZEN_KATA_TO_ZEN_HIRA;
        return KanaConverter.convertKana(text, flg);
    }

    public String convertHiraganaToRomaji(String hiragana) {
        //  if (!hiraganaRegex.matcher(hiragana).matches())
        //     throw new RuntimeException("Not hiragana only");

        Transliterator trans = Transliterator.getInstance("Hiragana-Latin");
        return trans.transliterate(hiragana);
    }

    public String convertRomajiToHiragana(String romaji) {
        Transliterator trans = Transliterator.getInstance("Latin-Hiragana");
        return trans.transliterate(romaji);
    }

    public String convertHiraToMinHiraPre(String hira) {
        char[] chars = hira.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            char mc = getMinhira(c);

            if (mc == '1') {
                if (i < chars.length - 1 && getMinhira(chars[i + 1]) != '1') {
                    String sc = String.valueOf(c);
                    sb.append(convertRomajiToHiragana(convertRomajiToVowelOnly(convertHiraganaToRomaji(sc))));
                } else {
                    sb.append(c);
                }
            } else {
                switch (c) {
                    case 'っ':
                    case 'ゅ':
                        sb.append("ぅ");
                        break;
                    case 'ゃ':
                    case 'ゎ':
                    case 'ヵ':
                        sb.append("ぁ");
                        break;
                    case 'ょ':
                        sb.append("ぉ");
                        break;
                    case 'ヶ':
                        sb.append("ぇ");
                        break;
                    default:
                        sb.append(c);
                }
            }

        }

        return sb.toString();
    }

    private char getMinhira(char c) {
        char mc = '1';
        for (char mh : minHiras) {
            if (c == mh) {
                mc = c;
                break;
            }
        }
        for (char mh : minHiraVowels) {
            if (c == mh) {
                mc = c;
                break;
            }
        }
        return mc;
    }

    public String convertRomajiToVowelOnly(String romaji) {
        //  if (!romajiRegex.matcher(romaji).matches())
        //       throw new RuntimeException("Not romaji only");
        StringBuilder sb = new StringBuilder();
        char[] chars = romaji.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            for (char v : vowels) {
                if (c == v) {
                    sb.append(c);
                    break;
                }
            }
            if (i >= 1 && c == '\'' && chars[i - 1] == 'n')
                sb.append("n'");

            if (i == chars.length - 1 && c == 'n')
                sb.append("n");

            if (i == 0 && c == 'n' && chars.length >= 2 && !isVowel(chars[1]))
                sb.append("n'");

            if (i < chars.length - 1 && c == '~' && isVowel(chars[i + 1]))
                sb.append("~");
        }
        return sb.toString();
    }

    private boolean isVowel(char c) {
        for (char vowel : vowels) {
            if (vowel == c)
                return true;
        }
        return false;
    }

    public static class VowelResult {
        private final String vowel;
        private final String hira;
        private final String romaji;
        private final String vowelHira;
        private final String minVowelHira;
        private final boolean error;

        public VowelResult(String vowel, String hira, String romaji, String vowelHira, String minVowelHira, boolean error) {
            this.vowel = vowel;
            this.hira = hira;
            this.romaji = romaji;
            this.vowelHira = vowelHira;
            this.minVowelHira = minVowelHira;
            this.error = error;
        }

        public String getHira() {
            return hira;
        }

        public String getRomaji() {
            return romaji;
        }

        public String getVowel() {
            return vowel;
        }

        public String getVowelHira() {
            return vowelHira;
        }

        public String getMinVowelHira() {
            return minVowelHira;
        }

        public boolean isError() {
            return error;
        }

        public String getCurrentVowel() {
            if (Config.leaveLowercase)
                return getMinVowelHira();
            return getVowel();
        }
    }
}
