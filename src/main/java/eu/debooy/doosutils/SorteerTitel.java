/**
 * Copyright 2015 Marco de Booij
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

import java.util.Locale;



/**
 * @author Marco de Booij
 * 
 * Geeft de titel terug zonder lidwoord zodat de titel gesorteerd kan worden.
 */
public final class SorteerTitel {
  private SorteerTitel() {
  }

  public static String sorteerwaarde(String titel, String taal) {
    String[]  lidwoorden;
    try {
      lidwoorden  = Lidwoorden.valueOf(taal.toUpperCase()).getLidwoorden();
    } catch (IllegalArgumentException e) {
      return titel;
    }

    if (lidwoorden.length == 0) {
      return titel;
    }

    String  kleineletters = titel.toLowerCase(new Locale(taal));
    // Kijk of de titel begint met een lidwoord en verwijder deze.
    for (String lidwoord : lidwoorden) {
      if (kleineletters.startsWith(lidwoord)) {
        return titel.substring(lidwoord.length());
      }
    }
    
    return titel;
  }

  public static enum  Lidwoorden {
    DE(new String[]{"das ", "dem ", "den ", "der ", "des ", "die ",
                    "ein ", "eine ", "einem ", "einen", "einer ", "eines "}),
    EN(new String[]{"a ", "an ", "the "}),
    FR(new String[]{"d'", "de ", "de l'", "de la ", "des ", "du ",
                    "l'", "la ", "le ", "les ", "un ", "une "}),
    NL(new String[]{"de ", "een ", "het "});

    private Lidwoorden(String[] lidwoorden) {
      this.lidwoorden = lidwoorden.clone();
    }

    public  String[] getLidwoorden() {
      return lidwoorden.clone();
    }

    private String[] lidwoorden;
  }
}
