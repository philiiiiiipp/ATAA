/* Helicopter Domain  for RL - Competition - RLAI's Port of Pieter Abbeel's code submission
* Copyright (C) 2007, Pieter Abbeel
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package org.rlcommunity.environment.helicopter.generalized;

import org.rlcommunity.environment.helicopter.Helicopter;
import rlVizLib.general.hasVersionDetails;
import rlVizLib.general.ParameterHolder;

/**
 * @author btanner
 */
public class GeneralizedHelicopter extends Helicopter {

    public GeneralizedHelicopter() {
        this(getDefaultParameters());
    }

    public GeneralizedHelicopter(ParameterHolder P) {
        super();
        int paramSet = P.getIntegerParam("pnum");
        GenHeliParamData theParamSetter = new GenHeliParamData();
        theParamSetter.setParameters(paramSet, this);
    }

    public static ParameterHolder getDefaultParameters() {
        ParameterHolder p = new ParameterHolder();
        rlVizLib.utilities.UtilityShop.setVersionDetails(p, new DetailsProvider());
        p.addIntegerParam("ParameterSet [0,9]", 0);
        p.setAlias("pnum", "ParameterSet [0,9]");
        return p;
    }
}

class DetailsProvider implements hasVersionDetails {

    public String getName() {
        return "Generalized Helicopter Hovering 1.0";
    }

    public String getShortName() {
        return "RLC-Heli";
    }

    public String getAuthors() {
        return "Pieter Abbeel, Mark Lee, Brian Tanner, Chris Rayner";
    }

    public String getInfoUrl() {
        return "http://rl-competition.org";
    }

    public String getDescription() {
        return "Generalized RL-Competition 2009 Java Version of the Helicopter Hovering RL-Problem.";
    }
}
