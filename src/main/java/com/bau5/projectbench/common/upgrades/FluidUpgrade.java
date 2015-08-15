package com.bau5.projectbench.common.upgrades;

/**
 * Created by bau5 on 5/19/2015.
 */
public class FluidUpgrade implements IUpgrade {

    @Override
    public String getUpgradeName() {
        return "Fluid";
    }

    @Override
    public int getType() {
        return 0;
    }
}
