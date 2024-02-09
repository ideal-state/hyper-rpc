package team.idealstate.hyper.rpc.api;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>JarUtils</p>
 *
 * <p>创建于 2024/2/7 3:54</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.2
 */
public abstract class JarUtils {

    @NotNull
    public static File copy(@NotNull String resourcePath, @NotNull File destinationDirectory) {
        return copy(JarUtils.class, resourcePath, destinationDirectory, false);
    }

    @NotNull
    public static File copy(@NotNull String resourcePath, @NotNull File destinationDirectory, boolean replaceExisting) {
        return copy(JarUtils.class, resourcePath, destinationDirectory, replaceExisting);
    }

    @NotNull
    public static File copy(@NotNull Class<?> sourceClass, @NotNull String resourcePath, @NotNull File destinationDirectory) {
        return copy(sourceClass, resourcePath, destinationDirectory, false);
    }

    @NotNull
    public static File copy(@NotNull Class<?> sourceClass, @NotNull String resourcePath, @NotNull File destinationDirectory, boolean replaceExisting) {
        AssertUtils.notNull(sourceClass, "来源类型不允许为 null");
        AssertUtils.notBlank(resourcePath, "资源路径不允许为纯空白字符串或 null");
        AssertUtils.notNull(destinationDirectory, "目标目录不允许为 null");
        URI uri;
        try {
            uri = sourceClass.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (JarFile jarFile = new JarFile(new File(uri))) {
            resourcePath = resourcePath.replace('\\', '/');
            if (resourcePath.charAt(0) == '/') {
                resourcePath = resourcePath.substring(1);
            }
            JarEntry jarEntry = jarFile.getJarEntry(resourcePath);
            if (jarEntry == null) {
                throw new FileNotFoundException(uri + "!" + resourcePath);
            }
            final byte[] buf = new byte[512];
            final File file = copyJarEntry(jarFile, jarEntry, resourcePath, destinationDirectory, buf, replaceExisting);
            if (!jarEntry.isDirectory()) {
                return file;
            }
            final String prefix = jarEntry.getName();
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                jarEntry = entries.nextElement();
                if (!jarEntry.getName().startsWith(prefix)) {
                    continue;
                }
                copyJarEntry(jarFile, jarEntry, resourcePath, destinationDirectory, buf, replaceExisting);
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File copyJarEntry(JarFile jarFile, JarEntry jarEntry, String resourcePath, File destinationDirectory, byte[] buf, boolean replaceExisting) throws IOException {
        String jarEntryName = jarEntry.getName();
        final File file;
        final boolean isDirectory = jarEntry.isDirectory();
        if (!isDirectory && jarEntryName.equals(resourcePath)) {
            file = new File(destinationDirectory, jarEntryName.substring(jarEntryName.lastIndexOf('/')));
        } else {
            file = new File(destinationDirectory, jarEntryName.substring(resourcePath.length()));
        }
        if (!isDirectory && file.exists() && !replaceExisting) {
            return file;
        }
        if (isDirectory) {
            return file;
        }
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                int len;
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
            }
            fileOutputStream.flush();
        }
        return file;
    }
}
