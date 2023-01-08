
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class MyLib {
    // шаблон для поиска зависимостей в файле
    final static Pattern pattern = Pattern.compile("require '.+'");

    /**
     * ищет все совпадения с шаблоном в тексте
     * @param data текст файла
     * @return список всех строк вида "require '.+'"
     */
    public static ArrayList<String> getMatches(final String data) {
        final ArrayList<String> out = new ArrayList<>();
        final Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            out.add(data.substring(matcher.start(), matcher.end()));
        }
        return out;
    }

    /**
     * вытаскивает имена файлов из списка строк вида "require '<filename>'"
     * @param matches список для выполнения операций
     * @return список имен файлов
     */
    public static ArrayList<String> getFilenamesFromMatches(final ArrayList<String> matches) {
        final var out = new ArrayList<String>();
        for (final var match : matches) {
            out.add(match.substring(9, match.length() - 1));
        }
        return out;
    }

    /**
     * выявляет имя файла после root/, которое используется в списке зависимостей файла
     * @param fullName полное имя файла
     * @return укороченное имя файла
     */
    public static String getShortFilename(final String fullName) {
        final int index = fullName.indexOf("root");
        return fullName.substring(index + 5);
    }

    /**
     * свапает два элемента массива строк на заданных позициях
     * @param list входной список строк
     * @param i индекс первого элемента
     * @param j индекс второго элемента
     */
    public static void swap(final ArrayList<String> list, int i, int j) {
        final String toSwap = list.get(i);
        list.remove(i);
        list.add(i, list.get(j - 1));
        list.remove(j);
        list.add(j, toSwap);
    }
}
