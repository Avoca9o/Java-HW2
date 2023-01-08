final public class LoopException extends Exception {
    final String file; // файл, на котором цепочка файлов зацикливается
    public LoopException(final String file) {
        this.file = file;
    }
}
