package com.bau5.projectbench.common.upgrades;

/**
 * Created by bau5 on 6/20/2016.
 */
public class InventorySizeUpgrade implements IUpgrade {
    @Override
    public String getUpgradeName() {
        return "Inventory Size";
    }

    public int getAdditionalSlotCount() {
        return 18;
    }

    @Override
    public int getType() {
        return 1;
    }
}
