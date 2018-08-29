/**
 * Copyright 2018 Marco de Booij
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
package eu.debooy.doosutils.access;

import eu.debooy.doosutils.DoosUtils;
import eu.debooy.doosutils.exception.BestandException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * @author Marco de Booij
 */
public class TekstBestand {
  private static  ResourceBundle  resourceBundle  =
      ResourceBundle.getBundle("DoosUtils-file", Locale.getDefault());

  private final boolean     append;
  private final String      bestand;
  private final String      charset;
  private final ClassLoader classLoader;
  private final boolean     lezen;

  private BufferedReader  invoer;
  private BufferedWriter  uitvoer;
  private String          lijn;

  private TekstBestand(TekstBestandBuilder builder) throws BestandException {
    append      = builder.getAppend();
    bestand     = builder.getBestand();
    charset     = builder.getCharset();
    classLoader = builder.getClassLoader();
    lezen       = builder.getLezen();

    open();
  }

  public static class TekstBestandBuilder {
    private boolean     append      = false;
    private String      bestand     = "";
    private String      charset     = Charset.defaultCharset().name();
    private ClassLoader classLoader = null;
    private boolean     lezen       = true;

    public TekstBestandBuilder() {}

    public TekstBestand build() throws BestandException {
      return new TekstBestand(this);
    }

    public boolean getAppend() {
      return append;
    }

    public String getBestand() {
      return bestand;
    }

    public String getCharset() {
      return charset;
    }

    public ClassLoader getClassLoader() {
      return classLoader;
    }

    public boolean getLezen() {
      return lezen;
    }

    public TekstBestandBuilder setAppend(boolean append) {
      this.append       = append;
      return this;
    }

    public TekstBestandBuilder setBestand(String bestand) {
      this.bestand      = bestand;
      return this;
    }

    public TekstBestandBuilder setCharset(String charset) {
      this.charset      = charset;
      return this;
    }

    public TekstBestandBuilder setClassLoader(ClassLoader classLoader) {
      this.classLoader  = classLoader;
      return this;
    }

    public TekstBestandBuilder setLezen(boolean lezen) {
      this.lezen        = lezen;
      return this;
    }
  }

  public void close() throws BestandException {
    if (null == invoer
        && null == uitvoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_DICHT),
                                                      bestand));
    }

    try {
     invoer.close();
   } catch (IOException e) {
     throw new BestandException(e);
   }
 }

  public String getBestand() {
    if (DoosUtils.isBlankOrNull(bestand)) {
      return classLoader.toString();
    }

    return bestand;
  }

  public String getCharset() {
    return charset;
  }

  public boolean hasNext() {
    return (null != lijn);
  }

  public boolean isAppend() {
    return append;
  }

  public boolean isEof() {
    return !hasNext();
  }

  public boolean isReadOnly() {
    return lezen;
  }

  public String next() throws BestandException {
    if (!lezen) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_WRITEONLY),
                                                      bestand));
    }

    if (isEof()) {
      throw new BestandException(
          resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }

    String  record  = lijn;

    try {
      lijn = invoer.readLine();
    } catch (IOException e) {
      throw new BestandException(e);
    }

    return record;
  }

  public void open() throws BestandException {
    if (null != invoer
        || null != uitvoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_OPEN),
                                                      bestand));
    }

    try {
      if (null == classLoader) {
        if (lezen) {
          invoer  = new BufferedReader(
                      new InputStreamReader(
                        new FileInputStream (bestand), charset));
        } else {
          uitvoer = new BufferedWriter(
                      new OutputStreamWriter(
                        new FileOutputStream(bestand, append), charset));
        }
      } else {
        if (lezen) {
          invoer  = new BufferedReader(
                      new InputStreamReader(
                          classLoader.getResourceAsStream(bestand), charset));
        } else {
          throw new BestandException(
              resourceBundle.getString(BestandConstants.ERR_CLP_READONLY));
        }
      }
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      throw new BestandException(e);
    }

    if (lezen) {
      try {
        lijn  = invoer.readLine();
      } catch (IOException e) {
        throw new BestandException(e);
      }

      if (null == lijn) {
        throw new BestandException(MessageFormat.format(
            resourceBundle.getString(BestandConstants.ERR_BEST_LEEG),
                                                        bestand));
      }
    }
  }

  public void write(String lijn) throws BestandException {
    if (lezen) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_READONLY),
                                                      bestand));
    }

    try {
      uitvoer.write(lijn);
      uitvoer.newLine();
    } catch (IOException e) {
      throw new BestandException(e);
    }
  }
}
