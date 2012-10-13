/**
 * Copyright 2009 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.0 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/7330l5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package eu.debooy.doosutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Marco de Booij
 */
public class Arguments {
  private boolean             valid       = true;
  private Map<String, String> arguments   = new HashMap<String, String>();
  private List<String>        parameters  = new ArrayList<String>();
  private List<String>        verplicht   = new ArrayList<String>();

  public Arguments() {
  }

  public Arguments(String[] args) {
    setArguments(Arrays.copyOf(args, args.length));
  }

  /**
   * Return the value of the argument
   * @param argument
   * @return value of the argument
   */
  public String getArgument(String argument) {
    if (arguments.containsKey(argument)) {
      return arguments.get(argument);
    }

    return null;
  }

  /**
   * Checks if the argument is defined.
   * @param argument
   * @return
   */
  public boolean hasArgument(String argument) {
    return arguments.containsKey(argument);
  }

  /**
   * Returns if the arguments are valid or not.
   * @return
   */
  public boolean isValid() {
    boolean juist     = true;
    boolean volledig  = true;

    // Alle verplichte parameters aanwezig?
    for (String key: verplicht) {
      if (!arguments.containsKey(key)) {
        volledig  = false;
      }
    }

    // Enkel juiste parameters aanwezig?
    if (parameters.size() > 0) {
      for (String parameter: arguments.keySet()) {
        if (!parameters.contains(parameter)) {
          juist = false;
        }
      }
    }

    return juist && valid && volledig;
  }

  /**
   * @param args
   * @return
   */
  public boolean setArguments(String[] args){
    String  key;
    String  value;

    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        String  argument;
        if (args[i].startsWith("--")) {
          argument  = args[i].substring(2);
        } else {
          argument  = args[i].substring(1);
        }
        if (argument.indexOf('=') > 0) {
          key   = argument.substring(0, argument.indexOf('='));
          value = argument.substring(argument.indexOf('=')+1);
        } else {
          key   = argument;
          if (i+1 < args.length
              && !args[i+1].startsWith("-")) {
            i++;
            value = args[i];
          } else {
            value = "";
          }
        }
        arguments.put(key, value);
        continue;
      }
      if (args[i].indexOf('=') > 0) {
        key   = args[i].substring(0, args[i].indexOf('='));
        value = args[i].substring(args[i].indexOf('=')+1);
        arguments.put(key, value);
        continue;
      }
      valid = false;
    }

    return valid;
  }

  public void setParameters(String[] parameters) {
    this.parameters = Arrays.asList(parameters);
  }

  public void setVerplicht(String[] verplicht) {
    this.verplicht  = Arrays.asList(verplicht);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuffer  result  = new StringBuffer();
    result.append("Arguments:");
    for (String key: arguments.keySet()) {
      result.append(key).append("=").append(arguments.get(key)).append("|");
    }
    result.append("Verplicht:");
    for (String key: verplicht) {
      result.append(key).append("|");
    }

    return result.toString();
  }
}
