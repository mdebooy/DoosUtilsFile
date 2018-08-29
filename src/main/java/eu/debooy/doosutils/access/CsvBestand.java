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
import eu.debooy.doosutils.access.BestandConstants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

  private final String      bestand;
  private final String      charsetIn;
  private final ClassLoader classLoader;
  private final String      delimiter;
  private final String      fieldSeparator;
  private final boolean     header;
  private final String      lineSeparator;

  private BufferedReader  invoer;
  private String[]        kolomNamen;
  private String          lijn;

  private CsvBestand(CsvBestandBuilder builder) throws BestandException {
    bestand         = builder.getBestand();
    charsetIn       = builder.getCharsetIn();
    classLoader     = builder.getClassLoader();
    delimiter       = builder.getDelimiter();
    fieldSeparator  = builder.getFieldSeparator();
    header          = builder.getHeader();
    kolomNamen      = builder.getKolomNamen();
    lineSeparator   = builder.getLineSeparator();

    open();
  }

  public static class CsvBestandBuilder {
    private String      bestand         = "";
    private String      charsetIn       = Charset.defaultCharset().name();
    private ClassLoader classLoader     = null;
    private String      delimiter       = "\"";
    private String      fieldSeparator  = ",";
    private boolean     header          = true;
    private String[]    kolomNamen      = new String[0];
    private String      lineSeparator   = System.getProperty("line.separator");

    public CsvBestandBuilder() {}

    public CsvBestand build() throws BestandException {
      return new CsvBestand(this);
    }

    public String getBestand() {
      return bestand;
    }

    public String getCharsetIn() {
      return charsetIn;
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

    public boolean getHeader() {
      return header;
    }

    public String[] getKolomNamen() {
      return Arrays.copyOf(kolomNamen, kolomNamen.length);
    }

    public String getLineSeparator() {
      return lineSeparator;
    }

    public CsvBestandBuilder setBestand(String bestand) {
      this.bestand        = bestand;
      return this;
    }

    public CsvBestandBuilder setCharsetIn(String charsetIn) {
      this.charsetIn      = charsetIn;
      return this;
    }

    public CsvBestandBuilder setClassLoader(ClassLoader classLoader) {
      this.classLoader    = classLoader;
      return this;
    }

    public CsvBestandBuilder setDelimiter(String delimiter) {
      this.delimiter      = delimiter;
      return this;
    }

    public CsvBestandBuilder setFieldSeparator(String fieldSeparator) {
      this.fieldSeparator = fieldSeparator;
      return this;
    }

    public CsvBestandBuilder setHeader(boolean header) {
      this.header         = header;
      return this;
    }

    public CsvBestandBuilder setKolomNamen(String[] kolomNamen) {
      this.kolomNamen     = Arrays.copyOf(kolomNamen, kolomNamen.length);
      return this;
    }

    public CsvBestandBuilder setLineSeparator(String lineSeparator) {
      this.lineSeparator  = lineSeparator;
      return this;
    }
  }

  public void close() throws BestandException {
    if (null == invoer) {
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

  public String getCharsetIn() {
    return charsetIn;
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

  public boolean hasNext() {
    return (null != lijn);
  }

  public boolean isEof() {
    return !hasNext();
  }

  public String[] next() throws BestandException {
    if (isEof()) {
      throw new BestandException(
          resourceBundle.getString(BestandConstants.ERR_BEST_EOF));
    }

    String[]  velden  = splits(lijn);

    try {
      lijn = invoer.readLine();
    } catch (IOException e) {
      throw new BestandException(e);
    }

    return velden;
  }

  public void open() throws BestandException {
    if (null != invoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_OPEN),
                                                      bestand));
    }

    try {
      if (null == classLoader) {
        invoer  = new BufferedReader(
                    new InputStreamReader(
                      new FileInputStream (bestand), charsetIn));
      } else {
        invoer  = new BufferedReader(
            new InputStreamReader(
                classLoader.getResourceAsStream(bestand), charsetIn));
      }
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      throw new BestandException(e);
    }

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

    if (header) {
      kolomNamen  = splits(lijn);
      try {
        lijn  = invoer.readLine();
      } catch (IOException e) {
        throw new BestandException(e);
      }
    }
  }

  public void setKolomNamen(String[] kolomNamen) {
    if (null == kolomNamen) { 
      this.kolomNamen = new String[0]; 
    } else { 
      this.kolomNamen = Arrays.copyOf(kolomNamen, kolomNamen.length); 
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

    return velden;    
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
}
