package com.bau5.projectbench.common;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Created by bau5 on 4/15/2015.
 */
public class BlockProjectBench extends BlockContainer {
    BlockProjectBench() {
        super(Material.WOOD);
        setRegistryName("pb_block");
        setUnlocalizedName("pb_block");
        setHardness(1.0F);
        setHarvestLevel("net.minecraft.item.ItemAxe", 0);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityProjectBench();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(playerIn.isSneaking()) {
            return false;
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if(te == null || !(te instanceof TileEntityProjectBench)){
            return false;
        }

        TileEntityProjectBench tile = (TileEntityProjectBench)te;

        // TODO: maybe move to ItemUpgrade
        ItemStack held = playerIn.getHeldItem(hand);
        if(held != null && held.getItem() == ProjectBench.upgrade && tile.getCanAcceptUpgrade()){
            tile.performUpgrade(held);
            if (!playerIn.capabilities.isCreativeMode) {
                held.setCount(held.getCount()-1);
            }
            return true;
        }

        if (tile.getHasFluidUpgrade()) {
            IFluidHandler capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
            if (capability != null && FluidUtil.interactWithFluidHandler(playerIn, hand, capability)){
                playerIn.inventoryContainer.detectAndSendChanges();
                return true;
            }
        }

        if(!worldIn.isRemote){
            playerIn.openGui(ProjectBench.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity == null) {
            super.breakBlock(worldIn, pos, state);
            return;
        }

        TileEntityProjectBench tpb = (TileEntityProjectBench) tileentity;

        InventoryHelper.dropInventoryItems(worldIn, pos, tpb);
        worldIn.updateComparatorOutputLevel(pos, this);

        if(tpb.getHasFluidUpgrade()){
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ProjectBench.upgrade, 1, 1));
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
