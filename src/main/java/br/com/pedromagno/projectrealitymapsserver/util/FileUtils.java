package br.com.pedromagno.projectrealitymapsserver.util;

import java.io.File;
import java.util.Objects;

public class FileUtils {
    public static File[] listarDiretorios(File directorio) {
        return Objects.requireNonNull(directorio.listFiles(File::isDirectory));
    }

}
