/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.darwish.nppsim;

import static com.darwish.nppsim.NPPSim.stateArray;
import java.io.Serializable;

/**
 *
 * @author ali
 */
abstract class Component implements Serializable {
    public Component() {
        if (stateArray != null) {
            stateArray.add(this);
        }
    }
    
}
