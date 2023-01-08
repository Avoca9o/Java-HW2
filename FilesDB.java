import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

final public class FilesDB {
    // путь к корню файловой системы
    private final File root;

    // список зависимостей (неполные пути)
    private final HashMap<String, ArrayList<String>> dependencies;

    // список полных путей к файлам
    private final ArrayList<String> listOfPaths;

    public FilesDB(final File root) {
        this.root = root;
        dependencies = new HashMap<>();
        listOfPaths = new ArrayList<>();
        fillDB(root);
    }

    /**
     * Добавляет в список все файлы в текущей директории, а также
     * рекурсивно вызывает себя же для поддиректорий
     * @param curDir текущая директория
     */
    private void fillDB(final File curDir) {
        for (var file : curDir.listFiles()) {
            if (file.isDirectory()) {
                fillDB(file);
            } else {
                if (!file.getAbsolutePath().endsWith("output.txt")) {
                    listOfPaths.add(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Заполняет список зависимостей
     * @throws LoopException при зацикливании выбрасывает исключение
     * с файлом, на котором это зацикливание произошло
     */
    public void putDependencies() throws LoopException {
        for (final var file : listOfPaths) {
            dependencies.put(MyLib.getShortFilename(file), new ArrayList<>());
        }
        for (final var file : listOfPaths) {
            addDependenciesOfOneFile(file);
        }
    }

    /**
     * Добавляет к зависимостям все зависимости одного файла
     * @param file входной файл
     * @throws LoopException исключение при возникновении цикла в зависимостях
     */
    private void addDependenciesOfOneFile(final String file) throws LoopException {
        FileInputStream currentFile;
        try {
            currentFile = new FileInputStream(file);
        } catch (Throwable th) {
            System.out.println("something went wrong...");
            return;
        }
        var scanner = new Scanner(currentFile);
        scanner.useDelimiter("\\Z");
        final String text = scanner.next();
        ArrayList<String> matches = MyLib.getMatches(text);

        for (final var dep : MyLib.getFilenamesFromMatches(matches)) {
            if (dependencies.containsKey(dep)) {
                dependencies.get(MyLib.getShortFilename(file)).add(dep);
            }
        }
        checkCycles();
    }

    /**
     * Проверяет, зацикливаются зависимости или нет
     * @throws LoopException исключение, возникающее при зацикливании зависимостей
     */
    private void checkCycles() throws LoopException {
        for (final var file : dependencies.keySet()) {
            ArrayList<String> met = new ArrayList<>();
            ArrayList<String> queue = new ArrayList<>();
            queue.add(file);

            while (!queue.isEmpty()) {
                String cur = queue.get(0);
                queue.remove(0);

                for (final var dep : dependencies.get(cur)) {
                    if (met.contains(file)) {
                        throw new LoopException(file);
                    }

                    if (!met.contains(dep)) {
                        queue.add(dep);
                        met.add(dep);
                    }
                }
            }
        }
    }

    /**
     * вывод текстов всех файлов из файловой системы корня root
     */
    public void print() {
        sortListOfPaths();
        FileOutputStream fout;

        try {
            String text = "";
            for (final var file : listOfPaths) {
                var fin = new FileInputStream(file);
                Scanner scanner = new Scanner(fin);
                while (scanner.hasNextLine()) {
                    text += scanner.nextLine() + '\n';
                }
                text += "\n";
            }

            fout = new FileOutputStream(root + "\\output.txt");
            fout.write(text.getBytes());
            fout.close();
        } catch (Throwable th) {
            System.out.println(th.getMessage() + "\nsomething went wrong...");
        }
    }

    /**
     * сортирует список файлов (если файл А зависит от файла Б, то он
     * должен быть ниже файла Б в списке)
     */
    private void sortListOfPaths() {
        for (int i = 0; i < listOfPaths.size() - 1; ++i) {
            for (int j = i + 1; j < listOfPaths.size(); ++j) {
                if (dependsOn(MyLib.getShortFilename(listOfPaths.get(i)),
                        MyLib.getShortFilename(listOfPaths.get(j)))) {
                    MyLib.swap(listOfPaths, i, j);
                    --i;
                    break;
                }
            }
        }
    }

    /**
     * проверяет, зависит ли первый файл от второго в цепочке зависимостей
     * @param first имя первого файла
     * @param second имя второго файла
     * @return да или нет
     */
    private boolean dependsOn(final String first, final String second) {
        final var queue = new ArrayList<String>();
        final var met = new ArrayList<String>();
        queue.add(first);
        while (!queue.isEmpty()) {
            final String file = queue.get(0);
            queue.remove(0);
            for (final var dep : dependencies.get(file)) {
                if (dep.equals(second)) {
                    return true;
                }
                if (!met.contains(dep)) {
                    met.add(dep);
                    queue.add(dep);
                }
            }
        }
        return false;
    }

    /**
     * печатает цикл (вызывается только когда уже точно известно, что цикл есть)
     * @param file имя файла
     */
    public void printCycle(final String file) {
        final var base = new ArrayList<String>();
        base.add(file);
        for (final var dep : dependencies.get(file)) {
            dfs(file, dep, base);
        }
    }

    /**
     * обход в глубину по цепочке зависимостей, и в момент когда находим повторяющийся элемент
     * выводим все файлы из цепочки и заканчиваем обход
     * @param startFile имя начального файла (при добавлении которого произошло зацикливание)
     * @param file имя текущего файла
     * @param base текущий список зависимостей
     */
    private void dfs(final String startFile, final String file, final ArrayList<String> base) {
        if (file.equals(startFile)) {
            for (final var fileName : base) {
                System.out.println(fileName);
            }
            System.out.println(startFile);
        } else {
            base.add(file);
            for (final var dep : dependencies.get(file)) {
                dfs(startFile, dep, base);
            }
            base.remove(base.size() - 1);
        }
    }
}
