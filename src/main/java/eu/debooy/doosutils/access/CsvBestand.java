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
package eu.debooy.doosutils.access;

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
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Marco de Booij
 * 
 * Voor een CSV bestand telt:
 * - Velden die een komma, aanhalingsteken of regeleinde bevatten moeten
 *   tussen aanhalingstekens gezet worden.
 * - Een aanhalingsteken in een veld moet direct vooraf gegaan worden door een
 *   aanhalingsteken.
 * - Een spatie voor of na een komma tussen 2 velden mag niet worden verwijderd.
 *   Dit is volgens RFC 4180.
 * - Een regeleinde in een veld moet bewaard blijven.
 * - De eerste regel in een CSV bestand mag de namen van elk van de kolommen
 *   bevatten.
 */
public class CsvBestand {
  private static  ResourceBundle  resourceBundle  =
      ResourceBundle.getBundle("DoosUtils-file", Locale.getDefault());

  private final boolean     append;
  private final String      bestand;
  private final String      charset;
  private final ClassLoader classLoader;
  private final String      delimiter;
  private final String      fieldSeparator;
  private final boolean     header;
  private final String      lineSeparator;
  private final boolean     lezen;

  private BufferedReader  invoer;
  private BufferedWriter  uitvoer;
  private String[]        kolomNamen;
  private String          lijn;
  private long            lijnen;

  private CsvBestand(Builder builder) throws BestandException {
    append          = builder.isAppend();
    bestand         = builder.getBestand();
    charset         = builder.getCharset();
    classLoader     = builder.getClassLoader();
    delimiter       = builder.getDelimiter();
    fieldSeparator  = builder.getFieldSeparator();
    header          = builder.hasHeader();
    kolomNamen      = builder.getKolomNamen();
    lezen           = builder.isReadOnly();
    lineSeparator   = builder.getLineSeparator();

    if (!isAppend() && header && kolomNamen.length == 0) {
      throw new BestandException(
          resourceBundle.getString(BestandConstants.ERR_CSV_GEEN_KOLOMMEN));
    }

    open();
  }

  public static final class Builder {
    private boolean     append          = false;
    private String      bestand         = "";
    private String      charset         = Charset.defaultCharset().name();
    private ClassLoader classLoader     = null;
    private String      delimiter       = "\"";
    private String      fieldSeparator  = ",";
    private boolean     header          = true;
    private String[]    kolomNamen      = new String[0];
    private boolean     lezen           = true;
    private String      lineSeparator   = System.getProperty("line.separator");

    public Builder() {}

    public CsvBestand build() throws BestandException {
      return new CsvBestand(this);
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

    public String getDelimiter() {
      return delimiter;
    }

    public String getFieldSeparator() {
      return fieldSeparator;
    }

    public String[] getKolomNamen() {
      return Arrays.copyOf(kolomNamen, kolomNamen.length);
    }

    public String getLineSeparator() {
      return lineSeparator;
    }

    public boolean hasHeader() {
      return header;
    }

    public boolean isAppend() {
      return append;
    }

    public boolean isReadOnly() {
      return lezen;
    }

    public Builder setAppend(boolean append) {
      this.append       = append;
      return this;
    }

    public Builder setBestand(String bestand) {
      this.bestand        = bestand;
      return this;
    }

    public Builder setCharset(String charset) {
      this.charset        = charset;
      return this;
    }

    public Builder setClassLoader(ClassLoader classLoader) {
      this.classLoader    = classLoader;
      return this;
    }

    public Builder setDelimiter(String delimiter) {
      this.delimiter      = delimiter;
      return this;
    }

    public Builder setFieldSeparator(String fieldSeparator) {
      this.fieldSeparator = fieldSeparator;
      return this;
    }

    public Builder setHeader(boolean header) {
      this.header         = header;
      return this;
    }

    public Builder setKolomNamen(String[] kolomNamen) {
      this.kolomNamen     = Arrays.copyOf(kolomNamen, kolomNamen.length);
      return this;
    }

    public Builder setLezen(boolean lezen) {
      this.lezen        = lezen;
      return this;
    }

    public Builder setLineSeparator(String lineSeparator) {
      this.lineSeparator  = lineSeparator;
      return this;
    }
  }

  public void close() throws BestandException {
    if (null == invoer
        && null == uitvoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_DICHT),
                                                      getBestand()));
    }

    try {
      if (null != invoer) {
        invoer.close();
      }
      if (null != uitvoer) {
        uitvoer.close();
      }
   } catch (IOException e) {
     throw new BestandException(e);
   }
 }

  public String getBestand() {
    if (null != classLoader) {
      return "CLASSPATH/" + bestand;
    }

    return bestand;
  }

  public String getCharset() {
    return charset;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public String getFieldSeparator() {
    return fieldSeparator;
  }

  public String[] getKolomNamen() {
    return Arrays.copyOf(kolomNamen, kolomNamen.length);
  }

  public String getLineSeparator() {
    return lineSeparator;
  }

  public boolean hasHeading() {
    return header;
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

  public String[] next() throws BestandException {
    if (isEof()) {
      throw new BestandException(
          resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }

    lijnen++;

    String[]  velden  = splits(lijn);

    if (velden.length != kolomNamen.length) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_CSV_KOLOM_AANTAL),
                                                      velden.length,
                                                      kolomNamen.length,
                                                      lijnen));
    }

    try {
      lijn = invoer.readLine();
    } catch (IOException e) {
      throw new BestandException(e);
    }

    return velden;
  }

  public void open() throws BestandException {
    lijnen  = 0;
    if (null != invoer
        || null != uitvoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_OPEN),
                                                      getBestand()));
    }

    try {
      if (null == classLoader) {
        if (lezen) {
          invoer  = new BufferedReader(
                      new InputStreamReader(
                        new FileInputStream (bestand), charset));
        } else {
          if (isAppend() && hasHeading()) {
            BufferedReader  head  = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream (bestand), charset));
            String[]  headr = splits(head.readLine());
            head.close();
            if (kolomNamen.length == 0) {
              kolomNamen  = Arrays.copyOf(headr, headr.length);
            } else {
              if (!Arrays.equals(headr, kolomNamen)) {
                throw new BestandException(
                    resourceBundle.getString(
                        BestandConstants.ERR_CSV_KOLOM_FOUT));
              }
            }
          }

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
    } catch (IOException e) {
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
                                                        getBestand()));
      }
    
      if (header) {
        kolomNamen  = splits(lijn);
        try {
          lijn  = invoer.readLine();
          if (null == lijn) {
            throw new BestandException(MessageFormat.format(
                resourceBundle.getString(BestandConstants.ERR_BEST_LEEG),
                                                            getBestand()));
          }
        } catch (IOException e) {
          throw new BestandException(e);
        }
      } else {
        kolomNamen  = new String[splits(lijn).length];
      }
    } else {
      if (!isAppend() && kolomNamen.length > 0) {
        write((Object[])kolomNamen);
      }
    }
  }

  private String[] splits(String lijn) {
    String[]  hulp    = lijn.split(fieldSeparator, -1);
    String[]  velden  = new String[hulp.length];
    int i = 0;
    int j = 0;
    while (i < hulp.length) {
      StringBuilder veldbuf = new StringBuilder(hulp[i]);
      while (!testVeld(veldbuf.toString())) {
        if (i < hulp.length) {
          i++;
        }
        veldbuf.append(fieldSeparator).append(hulp[i]);
      }
      String  veld  = veldbuf.toString();
      if (veld.startsWith(delimiter)
          && veld.endsWith(delimiter)) {
        veld  = veld.substring(delimiter.length(),
                               veld.length()-delimiter.length());
      }
      velden[j] = veld.replace(delimiter+delimiter, delimiter);
      i++;
      j++;
    }

    return Arrays.copyOf(velden, j);    
  }

  private boolean testVeld(String veld) {
    if ((veld.startsWith(delimiter)
        && veld.endsWith(delimiter))
        || (!veld.startsWith(delimiter)
              && !veld.endsWith(delimiter))) {
      Pattern pattern = Pattern.compile(delimiter);
      Matcher matcher = pattern.matcher(veld);
      int count = 0;
      while (matcher.find()) {
          count++;
      }
      if ((count%2) == 0) {
        return true;
      }
    }

    return false;
  }

  public void write(Object... kolommen) throws BestandException {
    if (lezen) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_READONLY),
                                                      getBestand()));
    }
    lijnen++;
    if (header && kolommen.length != kolomNamen.length) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_CSV_KOLOM_AANTAL),
                                                      kolommen.length,
                                                      kolomNamen.length,
                                                      lijnen));
    }

    StringBuilder lijn  = new StringBuilder();

    for (Object kolom : kolommen) {
      if (null == kolom) {
        lijn.append(fieldSeparator);
      } else {
        if (kolom instanceof String) {
          if (((String) kolom).contains(fieldSeparator)
              || ((String) kolom).contains(delimiter)
              || ((String) kolom).contains(lineSeparator)) {
            lijn.append(fieldSeparator).append(delimiter)
                .append(((String) kolom).replaceAll(delimiter,
                                                    delimiter + delimiter))
                .append(delimiter);
          } else {
            lijn.append(fieldSeparator).append(kolom);
          }
        } else {
          lijn.append(fieldSeparator).append(kolom);
        }
      }
    }

    try {
      if (lijn.length() > 0) {
        uitvoer.write(lijn.toString().substring(fieldSeparator.length()));
      }
      uitvoer.newLine();
    } catch (IOException e) {
      throw new BestandException(e);
    }
  }
}
