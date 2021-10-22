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

public class Ttl74157 extends AbstractTtlGate {

  public static final int PORT_INDEX_nA = 0;
  public static final int PORT_INDEX_1A = 1;
  public static final int PORT_INDEX_1B = 2;
  public static final int PORT_INDEX_1Y = 3;
  public static final int PORT_INDEX_2A = 4;
  public static final int PORT_INDEX_2B = 5;
  public static final int PORT_INDEX_2Y = 6;
  public static final int PORT_INDEX_3Y = 7;
  public static final int PORT_INDEX_3B = 8;
  public static final int PORT_INDEX_3A = 9;
  public static final int PORT_INDEX_4Y = 10;
  public static final int PORT_INDEX_4B = 11;
  public static final int PORT_INDEX_4A = 12;
  public static final int PORT_INDEX_nG = 13;

  public static final int[] PORT_INDICES_A = {
      PORT_INDEX_1A, PORT_INDEX_2A, PORT_INDEX_3A, PORT_INDEX_4A
  };

  public static final int[] PORT_INDICES_B = {
      PORT_INDEX_1B, PORT_INDEX_2B, PORT_INDEX_3B, PORT_INDEX_4B
  };

  public static final int[] PORT_INDICES_Y = {
      PORT_INDEX_1Y, PORT_INDEX_2Y, PORT_INDEX_3Y, PORT_INDEX_4Y
  };

  public Ttl74157() {
    super(
        "74157",
        (byte) 16,
        new byte[] {
            PORT_INDEX_1Y + 1, PORT_INDEX_2Y + 1, PORT_INDEX_3Y + 2, PORT_INDEX_4Y + 2
        },
        new String[] {
            "nA", "1A", "1B", "1Y", "2A", "2B", "2Y",
            "3Y", "3B", "3A", "4Y", "4B", "4A", "nG",
        });
  }

  @Override
  public void paintInternal(InstancePainter painter, int x, int y, int height, boolean up) {
    super.paintBase(painter, true, false);
    Drawgates.paintPortNames(painter, x, y, height, super.portnames);
  }

  @Override
  public void ttlpropagate(InstanceState state) {
      Value nA = state.getPortValue(PORT_INDEX_nA),
          nG = state.getPortValue(PORT_INDEX_nG),
          override = null;

      if (!nA.isFullyDefined() || !nG.isFullyDefined()) {
        override = Value.ERROR;
      } else if (nG == Value.TRUE) {
        override = Value.FALSE;
      }

      for (int i = 0; i < 4; i++) {
        state.setPort(
            PORT_INDICES_Y[i],
            override != null ? override : state.getPortValue((nA == Value.FALSE ? PORT_INDICES_A : PORT_INDICES_B)[i]),
            1
        );
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
