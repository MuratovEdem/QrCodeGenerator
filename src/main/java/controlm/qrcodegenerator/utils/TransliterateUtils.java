package controlm.qrcodegenerator.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TransliterateUtils {

    public String transliterateToLatin(String text) {
        if (text == null) return "";

        Map<Character, String> mapping = new HashMap<>();
        mapping.put('а', "a"); mapping.put('б', "b"); mapping.put('в', "v"); mapping.put('г', "g");
        mapping.put('д', "d"); mapping.put('е', "e"); mapping.put('ё', "e"); mapping.put('ж', "zh");
        mapping.put('з', "z"); mapping.put('и', "i"); mapping.put('й', "i"); mapping.put('к', "k");
        mapping.put('л', "l"); mapping.put('м', "m"); mapping.put('н', "n"); mapping.put('о', "o");
        mapping.put('п', "p"); mapping.put('р', "r"); mapping.put('с', "s"); mapping.put('т', "t");
        mapping.put('у', "u"); mapping.put('ф', "f"); mapping.put('х', "h"); mapping.put('ц', "c");
        mapping.put('ч', "ch"); mapping.put('ш', "sh"); mapping.put('щ', "sch"); mapping.put('ъ', "");
        mapping.put('ы', "y"); mapping.put('ь', ""); mapping.put('э', "e"); mapping.put('ю', "yu");
        mapping.put('я', "ya");
        mapping.put('А', "A"); mapping.put('Б', "B"); mapping.put('В', "V"); mapping.put('Г', "G");
        mapping.put('Д', "D"); mapping.put('Е', "E"); mapping.put('Ё', "E"); mapping.put('Ж', "Zh");
        mapping.put('З', "Z"); mapping.put('И', "I"); mapping.put('Й', "I"); mapping.put('К', "K");
        mapping.put('Л', "L"); mapping.put('М', "M"); mapping.put('Н', "N"); mapping.put('О', "O");
        mapping.put('П', "P"); mapping.put('Р', "R"); mapping.put('С', "S"); mapping.put('Т', "T");
        mapping.put('У', "U"); mapping.put('Ф', "F"); mapping.put('Х', "H"); mapping.put('Ц', "C");
        mapping.put('Ч', "Ch"); mapping.put('Ш', "Sh"); mapping.put('Щ', "Sch"); mapping.put('Ъ', "");
        mapping.put('Ы', "Y"); mapping.put('Ь', ""); mapping.put('Э', "E"); mapping.put('Ю', "Yu");
        mapping.put('Я', "Ya");

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (mapping.containsKey(c)) {
                result.append(mapping.get(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
