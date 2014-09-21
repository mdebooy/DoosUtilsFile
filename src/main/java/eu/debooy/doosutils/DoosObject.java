/**
 * Copyright 2012 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://www.osor.eu/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package eu.debooy.doosutils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Marco de Booij
 */
public class DoosObject {
  private static final String[] GET_METHODS_PREFIXES = {"get", "is"};

  /**
   * Zoek alle 'getters'.
   * 
   * @return
   */
  protected Method[] findGetters() {
    List<Method>  getters   = new ArrayList<Method>();
    Method[]      methodes  = this.getClass().getMethods();
    for (Method method : methodes) {
      for (String prefix : GET_METHODS_PREFIXES) {
        if (method.getName().startsWith(prefix)) {
          if (method.getParameterTypes() == null
              || method.getParameterTypes().length == 0) {
            getters.add(method);
          }
          break;
        }
      }
    }
    methodes = new Method[getters.size()];

    return getters.toArray(methodes);
  }

  /**
   * Maak een String van alle attributen die via een getter te benaderen zijn.
   */
  @Override
  public String toString() {
    StringBuilder sb        = new StringBuilder();
    String        attribute = null;
    Object        waarde    = null;

    sb.append(this.getClass().getSimpleName()).append(" (");
    for (Method method : findGetters()) {
      try {
        if (method.getName().startsWith("get")) {
          attribute = method.getName().substring(3);
        } else if (method.getName().startsWith("is")) {
          attribute = method.getName().substring(2);
        } else {
          continue;
        }
        attribute = attribute.substring(0, 1).toLowerCase()
                    + attribute.substring(1);
        sb.append(", ").append(attribute).append("=");
        // Enkel voor methodes zonder parameter.
        waarde = method.invoke(this);
        if (null != waarde) {
          if (waarde instanceof DoosObject) {
            // Geef enkel de naam van andere DoosObject.
            sb.append("<").append(waarde.getClass().getSimpleName())
              .append(">");
          } else {
            sb.append("[").append(waarde.toString()).append("]");
          }
        } else {
          sb.append("<null>");
        }
      } catch (IllegalArgumentException e) {
        // Enkel als de methode niet kan worden 'invoked'.
      } catch (IllegalAccessException e) {
        // Enkel als de methode niet kan worden 'invoked'.
      } catch (InvocationTargetException e) {
        // Enkel als de methode niet kan worden 'invoked'.
      }
    }
    sb.append(")");

    return sb.toString().replaceFirst("\\(, ", "\\(");
  }
}
