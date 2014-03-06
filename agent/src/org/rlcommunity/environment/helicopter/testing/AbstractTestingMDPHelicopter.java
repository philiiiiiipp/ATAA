/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.environment.helicopter.testing;

import org.rlcommunity.environment.helicopter.Helicopter;
//import Helicopter.Helicopter;
/**
 *
 * @author mradkie
 */
public abstract class AbstractTestingMDPHelicopter extends Helicopter {
        
        public AbstractTestingMDPHelicopter() {
            super();
            super.recordLogs=true;
            super.allowSaveLoadSeed=false;
            super.allowSaveLoadState=false;
        }
        
}
