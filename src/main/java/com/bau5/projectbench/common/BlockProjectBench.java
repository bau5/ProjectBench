package com.bau5.projectbench.common;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 * Created by bau5 on 4/15/2015.
 */
public class BlockProjectBench extends BlockContainer {

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    protected BlockProjectBench() {
        super(Material.WOOD);
        setUnlocalizedName("pb_block");
        setDefaultState(this.blockState.getBaseState());
        setHardness(1.0F);
        setHarvestLevel("net.minecraft.item.ItemAxe", 0);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityProjectBench();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(playerIn.isSneaking())
            return false;
        if(worldIn.getTileEntity(pos) == null || !(worldIn.getTileEntity(pos) instanceof TileEntityProjectBench))
            return false;

        TileEntityProjectBench tile = (TileEntityProjectBench) worldIn.getTileEntity(pos);

        ItemStack held = playerIn.getHeldItem(hand);
        if(held != null && held.getItem() == ProjectBench.upgrade && tile.getCanAcceptUpgrade()){
            tile.performUpgrade(held);
            if (!playerIn.capabilities.isCreativeMode) {
                held.stackSize--;
            }
            return true;
        }

        if (tile.getHasFluidUpgrade()) {
            if (FluidUtil.interactWithFluidHandler(held, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null), playerIn)) {
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
        TileEntityProjectBench tileentity = (TileEntityProjectBench) worldIn.getTileEntity(pos);

        boolean spawnLiquid = false;

        if (tileentity != null) {
            InventoryHelper.dropInventoryItems(worldIn, pos, tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);

            if (tileentity.getHasFluidUpgrade()) {
                EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ProjectBench.upgrade, 1, 1));
                float f3 = 0.05F;
                entityitem.motionX = Math.random() * (double) f3;
                entityitem.motionY = Math.random() * (double) f3 + 0.20000000298023224D;
                entityitem.motionZ = Math.random() * (double) f3;
                worldIn.spawnEntityInWorld(entityitem);

                if (tileentity.getFluidInTank() != null && tileentity.getFluidInTank().amount > 0) {
                    spawnLiquid = true;
                }
            }
        }

        super.breakBlock(worldIn, pos, state);

        if (spawnLiquid) {
            worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockState(pos, tileentity.getFluidInTank().getFluid().getBlock().getDefaultState(), 11);
            EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ProjectBench.projectBench, 1, 0));
            float f3 = 0.05F;
            entityitem.motionX = Math.random() * (double) f3;
            entityitem.motionY = Math.random() * (double) f3 + 0.20000000298023224D;
            entityitem.motionZ = Math.random() * (double) f3;
            worldIn.spawnEntityInWorld(entityitem);

            worldIn.notifyBlockOfStateChange(pos, state.getBlock());
        }
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getExtendedState(state, world, pos);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
