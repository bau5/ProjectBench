package com.bau5.projectbench.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;

/**
 * Created by bau5 on 4/15/2015.
 */
public class BlockProjectBench extends BlockContainer {
    private IIcon[] icons;

    protected BlockProjectBench() {
        super(Material.wood);
        setHardness(1.0F);
        setHarvestLevel("net.minecraft.item.ItemAxe", 0);
    }

    @Override
    public String getUnlocalizedName() {
        return "pb_block";
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityProjectBench();
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        switch(p_149691_1_){
            case 0: return icons[2];
            case 1: return icons[0];
            default: return icons[1];
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister registrar) {
        icons = new IIcon[3];
        icons[0] = registrar.registerIcon("projectbench:pb_top");
        icons[1] = registrar.registerIcon("projectbench:pb_side");
        icons[2] = registrar.registerIcon("projectbench:pb_bottom");
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX, float hitY, float hitZ) {if(playerIn.isSneaking())
        return false;
        if(worldIn.getTileEntity(x, y, z) == null || !(worldIn.getTileEntity(x, y, z) instanceof TileEntityProjectBench))
            return false;
        TileEntityProjectBench tile = (TileEntityProjectBench) worldIn.getTileEntity(x, y, z);
        ItemStack held = playerIn.getHeldItem();
        if(held != null && held.getItem() == ProjectBench.upgrade
                && held.getItemDamage() == 1 && tile.getCanAcceptUpgrade()){
            tile.performUpgrade(held);
            if(!playerIn.capabilities.isCreativeMode)
                held.stackSize--;
            return true;
        }
        FluidStack fstack = FluidContainerRegistry.getFluidForFilledItem(held);
        if(fstack != null){
            if(tile.canFill(ForgeDirection.UP, fstack.getFluid())){
                if(tile.fill(ForgeDirection.UP, fstack, true) > 0){
                    FluidContainerRegistry.FluidContainerData data = null;
                    for(FluidContainerRegistry.FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()){
                        if(container.filledContainer.getItem() == held.getItem()
                                && (tile.getFluidInTank() == null || tile.getFluidInTank().isFluidEqual(container.fluid))){
                            data = container;
                            break;
                        }
                    }
                    if(!playerIn.capabilities.isCreativeMode){
                        held.stackSize -= 1;
                        if(held.stackSize == 0){
                            playerIn.setCurrentItemOrArmor(0, held);
                        }
                        if(data.emptyContainer != null){
                            ItemStack containerCopy = data.emptyContainer.copy();
                            if(containerCopy.stackSize == 0){
                                containerCopy.stackSize = 1;
                            }
                            playerIn.inventory.addItemStackToInventory(containerCopy);
                        }
                    }
                    return true;
                }else{
                    if(!worldIn.isRemote) {
                        String message = "" + EnumChatFormatting.GRAY;
                        if (tile.getFluidInTank().amount >= 16000) {
                            message += "Tank is full.";
                        } else {
                            message += "Cannot accept this fluid.";
                        }
                        playerIn.addChatComponentMessage(new ChatComponentText(message));
                    }
                    return true;
                }
            }
        }
        if(!worldIn.isRemote){
            playerIn.openGui(ProjectBench.instance, 0, worldIn, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int p_149749_6_) {
        TileEntity tileentity = worldIn.getTileEntity(x, y, z);
        Random rand = new Random();

        if (tileentity instanceof IInventory)
        {
            IInventory inv = (IInventory)tileentity;
            for(int i = 0; i < inv.getSizeInventory(); i++){
                ItemStack stack = inv.getStackInSlot(i);
                if(stack != null){
                    EntityItem ent = new EntityItem(worldIn, x, y+0.5, z, stack.copy());
                    float f3 = 0.05F;
                    ent.motionX = rand.nextGaussian() * (double)f3;
                    ent.motionY = rand.nextGaussian() * (double)f3 + 0.20000000298023224D;
                    ent.motionZ = rand.nextGaussian() * (double)f3;
                    if(!worldIn.isRemote)
                        worldIn.spawnEntityInWorld(ent);
                }
            }
        }

        if(((TileEntityProjectBench)tileentity).getHasFluidUpgrade()){
            EntityItem entityitem = new EntityItem(worldIn, x, y, z, new ItemStack(ProjectBench.upgrade, 1, 1));
            float f3 = 0.05F;
            entityitem.motionX = rand.nextGaussian() * (double)f3;
            entityitem.motionY = rand.nextGaussian() * (double)f3 + 0.20000000298023224D;
            entityitem.motionZ = rand.nextGaussian() * (double)f3;
            worldIn.spawnEntityInWorld(entityitem);
        }

        super.breakBlock(worldIn, x, y, z, block, p_149749_6_);
    }
}
