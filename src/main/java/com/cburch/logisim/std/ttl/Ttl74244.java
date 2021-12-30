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
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.fpga.designrulecheck.CorrectLabel;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;

public class Ttl74244 extends AbstractTtlGate {

  public static final int PORT_INDEX_1nOE = 0;
  public static final int PORT_INDEX_1A0 = 1;
  public static final int PORT_INDEX_2Y0 = 2;
  public static final int PORT_INDEX_1A1 = 3;
  public static final int PORT_INDEX_2Y1 = 4;
  public static final int PORT_INDEX_1A2 = 5;
  public static final int PORT_INDEX_2Y2 = 6;
  public static final int PORT_INDEX_1A3 = 7;
  public static final int PORT_INDEX_2Y3 = 8;
  public static final int PORT_INDEX_2A3 = 9;
  public static final int PORT_INDEX_1Y3 = 10;
  public static final int PORT_INDEX_2A2 = 11;
  public static final int PORT_INDEX_1Y2 = 12;
  public static final int PORT_INDEX_2A1 = 13;
  public static final int PORT_INDEX_1Y1 = 14;
  public static final int PORT_INDEX_2A0 = 15;
  public static final int PORT_INDEX_1Y0 = 16;
  public static final int PORT_INDEX_2nOE = 17;

  public static final byte[] INPUTS_1A = {
      PORT_INDEX_1A0, PORT_INDEX_1A1, PORT_INDEX_1A2, PORT_INDEX_1A3 
  };
  
  public static final byte[] INPUTS_2A = {
      PORT_INDEX_2A0, PORT_INDEX_2A1, PORT_INDEX_2A2, PORT_INDEX_2A3
  };
  
  public static final byte[] OUTPUTS_1Y = {
      PORT_INDEX_1Y0, PORT_INDEX_1Y1, PORT_INDEX_1Y2, PORT_INDEX_1Y3
  };
  
  public static final byte[] OUTPUTS_2Y = {
      PORT_INDEX_2Y0, PORT_INDEX_2Y1, PORT_INDEX_2Y2, PORT_INDEX_2Y3
  };

  private static final byte[][] INPUTS = { INPUTS_1A, INPUTS_2A };
  private static final byte[][] OUTPUTS = { OUTPUTS_1Y, OUTPUTS_2Y };

  public Ttl74244() {
    super(
        "74244",
        (byte) 20,
        new byte[] {
            PORT_INDEX_2Y0 + 1, PORT_INDEX_2Y1 + 1, PORT_INDEX_2Y2 + 1, PORT_INDEX_2Y3 + 1,
            PORT_INDEX_1Y0 + 2, PORT_INDEX_1Y1 + 2, PORT_INDEX_1Y2 + 2, PORT_INDEX_1Y3 + 2
        },
        new String[] {
            "1nOE", "1A0", "2Y0", "1A1", "2Y1", "1A2", "2Y2", "1A3", "2Y3",
            "2A3", "1Y3", "2A2", "1Y2", "2A1", "1Y1", "2A0", "1Y0", "2nOE"
        });
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
    Drawgates.paintPortNames(painter, x, y, height, super.portnames);
  }

  @Override
  public void ttlpropagate(InstanceState state) {
    for (int i = 0; i < 2; i++) {
      Value nOE = state.getPortValue(i == 0 ? PORT_INDEX_1nOE : PORT_INDEX_2nOE), override = null;

      if (!nOE.isFullyDefined() || nOE == Value.TRUE) {
        override = Value.NIL;
      }

      for (int j = 0; j < 4; j++) {
        state.setPort(OUTPUTS[i][j], override == null ? state.getPortValue(INPUTS[i][j]) : override, 1);
      }
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
