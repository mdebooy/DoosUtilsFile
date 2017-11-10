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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * @author Marco de Booij
 */
public final class ZipBestand {
  private ZipBestand() {}

  public static void inpakken(String bron, String zip) throws BestandException {
    ZipOutputStream doel  = null;
    try {
      doel  = new ZipOutputStream(new FileOutputStream(zip));
      add(new File(bron), bron, doel);
    } catch (FileNotFoundException e) {
      throw new BestandException(e);
    } finally {
      if (null != doel) {
        try {
          doel.close();
        } catch (IOException e) {
          throw new BestandException(e);
        }
      }
    }
  }

  public static void uitpakken(String zip, String doel)
      throws BestandException {
    ZipFile           bron  = null;
    FileOutputStream  fos   = null;
    try {
      bron  = new ZipFile(zip);
      Enumeration<? extends ZipEntry> entries = bron.entries();
      while (entries.hasMoreElements()) {
        ZipEntry  entry   = entries.nextElement();
        File      bestand = new File(doel + File.separator + entry.getName());
        if (entry.isDirectory()) {
          if (!bestand.mkdir()) {
            throw new BestandException("mkdir " + bestand + " failed.");
          }
        } else {
          InputStream is  = bron.getInputStream(entry);
          fos = new FileOutputStream(bestand);
          while (is.available() > 0) {
            fos.write(is.read());
          }
          fos.close();
          is.close();
        }
      }
    } catch (IOException e) {
      throw new BestandException(e);
    } finally {
      IOException ie  = null;
      if (null != bron) {
        try {
          bron.close();
        } catch (IOException e) {
          ie = e;
        }
      }
      if (null != fos) {
        try {
          fos.close();
        } catch (IOException e) {
          ie = e;
        }
      }
      if (null != ie) {
        throw new BestandException(ie);
      }
    }
  }

  private static void add(File bron, String basis, ZipOutputStream doel)
      throws BestandException {
    BufferedInputStream in  = null;
    try {
      String  bestand     = bron.getPath().replace("\\", "/");
      String  zipBestand  = bron.getCanonicalPath()
                                .substring(basis.length());
      if (bron.isDirectory()) {
        if (!bestand.isEmpty()) {
          if (!bestand.endsWith("/")) {
            zipBestand  += "/";
          }
          ZipEntry  entry = new ZipEntry(zipBestand);
          entry.setTime(bron.lastModified());
          doel.putNextEntry(entry);
          doel.closeEntry();
        }
        for (File nestedFile : bron.listFiles()) {
          add(nestedFile, basis, doel);
        }
        return;
      }

      ZipEntry  entry = new ZipEntry(zipBestand);
      entry.setTime(bron.lastModified());
      doel.putNextEntry(entry);
      in  = new BufferedInputStream(new FileInputStream(bron));

      byte[]  buffer  = new byte[1024];
      int     count   = in.read(buffer);
      while (count >= 0) {
        doel.write(buffer, 0, count);
        count = in.read(buffer);
      }
      doel.closeEntry();
    } catch (IOException e) {
      throw new BestandException(e);
    } finally {
      if (null != in) {
        try {
          in.close();
        } catch (IOException e) {
          throw new BestandException(e);
        }
      }
    }
  }
}
