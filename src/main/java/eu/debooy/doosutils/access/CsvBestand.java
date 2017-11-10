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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
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
  private boolean         eof             = true;
  private boolean         header          = true;
  private BufferedReader  invoer          = null;
  private String          delimiter       = "\"";
  private String          fieldSeparator  = ",";
  private String          charsetIn       = Charset.defaultCharset().name();
  private String          lijn            = null;
  private String          lineSeparator   =
    System.getProperty("line.separator");
  private String[]        kolomNamen      = null;

  protected CsvBestand() {}

  public CsvBestand(String bestand) throws BestandException {
    openBestand(bestand);
  }

  public CsvBestand(String bestand, String charsetIn) throws BestandException {
    this.charsetIn  = charsetIn;
    openBestand(bestand);
  }

  /**
   * @deprecated Gebruik de close() methode.
   */
  @Deprecated
  public void closeBestand() throws BestandException {
     if (null == invoer) {
       throw new BestandException("Geen bestand open.");
     }

     try {
      invoer.close();
    } catch (IOException e) {
      throw new BestandException(e);
    }
  }
  public void close() throws BestandException {
    if (null == invoer) {
      throw new BestandException("Geen bestand open.");
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
    return !eof;
  }

  public boolean isEof() {
    return eof;
  }

  public String[] next() throws BestandException {
    if (eof) {
      throw new BestandException("Lezen na EOF.");
    }

    String[]  velden  = splits(lijn);

    try {
      lijn = invoer.readLine();
    } catch (IOException e) {
      throw new BestandException(e);
    }

    if (null == lijn) {
      eof = true;
    }

    return velden;
  }

  private void openBestand(String bestand) throws BestandException {
    eof = false;

    try {
      invoer  = new BufferedReader(
                  new InputStreamReader(
                    new FileInputStream (bestand), charsetIn));
    } catch (UnsupportedEncodingException e) {
      throw new BestandException(e);
    } catch (FileNotFoundException e) {
      throw new BestandException(e);
    }

    try {
      lijn  = invoer.readLine();
    } catch (IOException e) {
      throw new BestandException(e);
    }

    if (null == lijn) {
      eof = true;
      throw new BestandException("Leeg bestand [" + bestand + "]");
    }

    if (header) {
      kolomNamen  = splits(lijn);
      try {
        lijn  = invoer.readLine();
      } catch (IOException e) {
        throw new BestandException(e);
      }
      if (null == lijn) {
        eof = true;
      }
    }
  }

  /**
   * @deprecated Gebruik de openBestand(String bestand) methode en de 
   * setCharsetIn(String charsetIn) methode om de characterset te zetten.
   */
  @Deprecated
  protected void openBestand(String bestand, String charsetIn)
      throws BestandException {
    this.charsetIn  = charsetIn;

    openBestand(bestand);
  }

  public void setCharsetIn(String charsetIn) {
    this.charsetIn = charsetIn;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public void setFieldSeparator(String fieldSeparator) {
    this.fieldSeparator = fieldSeparator;
  }

  public void setHeader(boolean header) {
    this.header = header;
  }

  public void setKolomNamen(String[] kolomNamen) {
    if (null == kolomNamen) { 
      this.kolomNamen = new String[0]; 
    } else { 
      this.kolomNamen = Arrays.copyOf(kolomNamen, kolomNamen.length); 
    }
  }

  public void setLineSeparator(String lineSeparator) {
    this.lineSeparator = lineSeparator;
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
