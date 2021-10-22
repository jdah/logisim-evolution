/*
 * This file is part of logisim-evolution.
 *
 * Logisim-evolution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Logisim-evolution is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with logisim-evolution. If not, see <http://www.gnu.org/licenses/>.
 *
 * Original code by Carl Burch (http://www.cburch.com), 2011.
 * Subsequent modifications by:
 *   + College of the Holy Cross
 *     http://www.holycross.edu
 *   + Haute École Spécialisée Bernoise/Berner Fachhochschule
 *     http://www.bfh.ch
 *   + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *     http://hepia.hesge.ch/
 *   + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *     http://www.heig-vd.ch/
 */

package com.cburch.logisim.std.ttl;

import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.fpga.designrulecheck.CorrectLabel;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;

public class Ttl74253 extends AbstractTtlGate {

  public static final int PORT_INDEX_1nG = 0;
  public static final int PORT_INDEX_B = 1;
  public static final int PORT_INDEX_1C3 = 2;
  public static final int PORT_INDEX_1C2 = 3;
  public static final int PORT_INDEX_1C1 = 4;
  public static final int PORT_INDEX_1C0 = 5;
  public static final int PORT_INDEX_1Y = 6;
  public static final int PORT_INDEX_2Y = 7;
  public static final int PORT_INDEX_2C0 = 8;
  public static final int PORT_INDEX_2C1 = 9;
  public static final int PORT_INDEX_2C2 = 10;
  public static final int PORT_INDEX_2C3 = 11;
  public static final int PORT_INDEX_A = 12;
  public static final int PORT_INDEX_2nG = 13;

  public static final byte[] INPUTS_nG = { PORT_INDEX_1nG, PORT_INDEX_2nG };

  public static final byte[] OUTPUTS_Y = { PORT_INDEX_1Y, PORT_INDEX_2Y };

  public static final byte[][] INPUTS_C = {
      { PORT_INDEX_1C0, PORT_INDEX_1C1, PORT_INDEX_1C2, PORT_INDEX_1C3 },
      { PORT_INDEX_2C0, PORT_INDEX_2C1, PORT_INDEX_2C2, PORT_INDEX_2C3 }
  };

  public Ttl74253() {
    super(
        "74253",
        (byte) 16,
        new byte[] {
            PORT_INDEX_1Y + 1, PORT_INDEX_2Y + 2
        },
        new String[] {
            "1nG", "B", "1C3", "1C2", "1C1", "1C0", "1Y",
            "2Y", "2C0", "2C1", "2C2", "2C3", "A", "2nG"
        });
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
    Drawgates.paintPortNames(painter, x, y, height, super.portnames);
  }

  @Override
  public void ttlpropagate(InstanceState state) {
    Value a = state.getPortValue(PORT_INDEX_A),
        b = state.getPortValue(PORT_INDEX_B),
        override = null;

    byte select = 0;

    if (!a.isFullyDefined() || !b.isFullyDefined()) {
      override = Value.ERROR;
    } else {
       select = (byte) ((b.toLongValue() << 1) | a.toLongValue());
    }

    for (int i = 0; i < 2; i++) {
      Value y = override;

      if (y == null) {
        Value nG = state.getPortValue(INPUTS_nG[i]);

        if (!nG.isFullyDefined()) {
          y = Value.ERROR;
        } else if (nG == Value.TRUE) {
          y = Value.NIL;
        } else {
          y = state.getPortValue(INPUTS_C[i][select]);
        }
      }

      state.setPort(OUTPUTS_Y[i], y, 1);
    }
  }

  @Override
  public String getHDLName(AttributeSet attrs) {
    return CorrectLabel.getCorrectLabel("TTL" + this.getName()).toUpperCase();
  }

  @Override
  public boolean HDLSupportedComponent(AttributeSet attrs) {
      return false;
  }
}
