package api.peridot.periapi.utils.replacements;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ReplacementUtil {

    private ReplacementUtil() {
    }

    public static List<String> replace(List<String> list, Replacement... replacements) {
        List<String> resultList = new ArrayList<>(list);
        for (int i = 0; i < resultList.size(); i++) {
            resultList.set(i, replace(resultList.get(i), replacements));
        }
        return resultList;
    }

    public static String replace(String msg, Replacement... replacements) {
        String toReturn = msg;
        for (Replacement r : replacements) {
            toReturn = StringUtils.replace(toReturn, r.getFrom(), r.getTo());
        }
        return toReturn;
    }

}
