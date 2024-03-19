/**
 * Copyright 2011 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
/**
 *
 */
package eu.debooy.doosutils.access;

import eu.debooy.doosutils.exception.BestandException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipFile;


/**
 * @author Marco de Booij
 */
public class ZipBestand {
  private static final  ResourceBundle  resourceBundle  =
      ResourceBundle.getBundle("DoosUtils-file", Locale.getDefault());

  private final ClassLoader classLoader;
  private final boolean     lezen;
  private final String      zip;

  private ZipBestand(ZipBestand.Builder builder) {
    classLoader = builder.getClassLoader();
    if (null == classLoader) {
      lezen     = builder.isReadOnly();
    } else {
      lezen     = true;
    }
    zip         = builder.getZip();
  }

  public static final class Builder {
    private ClassLoader classLoader = null;
    private boolean     lezen       = true;
    private String      zip         = "";

    public ZipBestand build() {
      return new ZipBestand(this);
    }

    public ClassLoader getClassLoader() {
      return classLoader;
    }

    public String getZip() {
      return zip;
    }

    public boolean isReadOnly() {
      return lezen;
    }

    public Builder setClassLoader(ClassLoader classLoader) {
      this.classLoader  = classLoader;
      return this;
    }

    public Builder setLezen(boolean lezen) {
      this.lezen        = lezen;
      return this;
    }

    public Builder setZip(String zip) {
      this.zip          = zip;
      return this;
    }
  }

  public String getZip() {
    return zip;
  }

  public boolean isReadOnly() {
    return lezen;
  }

  public void inpakken(String bestand) throws BestandException {
    var                 bron  = Paths.get(bestand);
    Map<String, String> env   = new HashMap<>();

    if (isReadOnly()) {
            throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_READONLY),
                                                      zip));
    }

    if (!Files.isRegularFile(bron)) {
            throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_FOUT),
                                                      bestand));
    }

    env.put("create", "true");
    var                 uri   = URI.create("jar:file:" + zip);

    try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
      var zipPath = zipfs.getPath(bron.getFileName().toString());
      Files.copy(bron, zipPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new BestandException(e);
    }
  }

  public void uitpakken(String doel) throws BestandException {
    try (var bron = new ZipFile(zip)) {
      var entries = bron.entries();
      while (entries.hasMoreElements()) {
        var entry   = entries.nextElement();
        var bestand = new File(doel + File.separator + entry.getName());
        if (entry.isDirectory()) {
          if (!bestand.mkdir()) {
            throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_MKDIR_FAILED),
                                                      bestand));
          }
        } else {
          try (var is  = bron.getInputStream(entry);
               var fos = new FileOutputStream(bestand)) {
            while (is.available() > 0) {
              fos.write(is.read());
            }
          }
        }
      }
    } catch (IOException e) {
      throw new BestandException(e);
    }
  }
}
