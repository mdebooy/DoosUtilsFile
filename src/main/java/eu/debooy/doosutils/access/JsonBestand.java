/**
 * Copyright 2019 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
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
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * @author Marco de Booij
 */
public class JsonBestand {
  private static  ResourceBundle  resourceBundle  =
      ResourceBundle.getBundle("DoosUtils-file", Locale.getDefault());

  private final boolean     append;
  private final String      bestand;
  private final String      charset;
  private final ClassLoader classLoader;
  private final boolean     lezen;
//  private final boolean     prettify;

  private BufferedReader  invoer;
  private JSONObject      json;
  private BufferedWriter  uitvoer;

  private JsonBestand(Builder builder) throws BestandException {
    append      = builder.isAppend();
    bestand     = builder.getBestand();
    charset     = builder.getCharset();
    classLoader = builder.getClassLoader();
    lezen       = builder.isReadOnly();
//    prettify    = builder.isPrettify();

    open();
  }

  public static final class Builder {
    private boolean     append      = false;
//    private boolean     prettify    = false;
    private String      bestand     = "";
    private String      charset     = "UTF-8";
    private ClassLoader classLoader = null;
    private boolean     lezen       = true;

    public Builder() {}

    public JsonBestand build() throws BestandException {
      return new JsonBestand(this);
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

    public boolean isAppend() {
      return append;
    }
//
//    public boolean isPrettify() {
//      return prettify;
//    }

    public boolean isReadOnly() {
      return lezen;
    }

    public Builder setAppend(boolean append) {
      this.append       = append;
      return this;
    }

    public Builder setBestand(String bestand) {
      this.bestand      = bestand;
      return this;
    }

    public Builder setCharset(String charset) {
      this.charset      = charset;
      return this;
    }

    public Builder setClassLoader(ClassLoader classLoader) {
      this.classLoader  = classLoader;
      return this;
    }

    public Builder setLezen(boolean lezen) {
      this.lezen        = lezen;
      return this;
    }
//
//    public Builder setPrettify(boolean prettify) {
//      this.prettify     = prettify;
//      return this;
//    }
  }

  public void close() throws BestandException {
    if (null == invoer
        && null == uitvoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_DICHT),
                                                      bestand));
    }

    try {
      if (null != invoer) {
        invoer.close();
      }
      if (null != uitvoer) {
        json.writeJSONString(uitvoer);
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

  public boolean isAppend() {
    return append;
  }
//
//  public boolean isPrettify() {
//    return prettify;
//  }

  public boolean isReadOnly() {
    return lezen;
  }

  public void open() throws BestandException {
    if (null != invoer
        || null != uitvoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_OPEN), bestand));
    }

    try {
      if (null == classLoader) {
        if (lezen || append) {
          invoer  = new BufferedReader(
                      new InputStreamReader(
                        new FileInputStream (bestand), charset));
        }
        if (append) {
          readBestand();
          close();
        }
        if (!lezen) {
          uitvoer = new BufferedWriter(
                      new OutputStreamWriter(
                        new FileOutputStream(bestand), charset));
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
      readBestand();
    }
  }

  public JSONObject read() {
    return json;
  }

  private void readBestand() throws BestandException {
    if (null == invoer) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_BEST_DICHT),
          bestand));
    }

    JSONParser  parser  = new JSONParser();
    try {
      json = (JSONObject) parser.parse(invoer);
    } catch (IOException | ParseException e) {
      throw new BestandException(e);
    }

    if (null == json) {
      json  = new JSONObject();
    }
  }

  public void write(JSONObject node) throws BestandException {
    write(node.toJSONString());
  }

  public void write(String node) throws BestandException {
    JSONParser  parser  = new JSONParser();
    try {
      json  = (JSONObject) parser.parse(node);
    } catch (ParseException e) {
      throw new BestandException(MessageFormat.format(
          resourceBundle.getString(BestandConstants.ERR_JSON_ERROR), e));
    }
  }
}
