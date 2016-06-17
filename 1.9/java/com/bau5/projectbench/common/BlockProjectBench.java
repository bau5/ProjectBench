package com.bau5.projectbench.common;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by bau5 on 4/15/2015.
 */
public class BlockProjectBench extends BlockContainer {

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    protected BlockProjectBench() {
        super(Material.wood);
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
        if(held != null && held.getItem() == ProjectBench.upgrade
                && held.getMetadata() == 1 && tile.getCanAcceptUpgrade()){
            tile.performUpgrade(held);
            held.stackSize--;
            return true;
        }

        FluidStack fstack = FluidContainerRegistry.getFluidForFilledItem(held);
        if(fstack != null){
            if(tile.canFill(EnumFacing.UP, fstack.getFluid())){
                if(tile.fill(EnumFacing.UP, fstack, true) > 0){
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
                            playerIn.setHeldItem(hand, held);
                        }
                        if(data != null && data.emptyContainer != null){
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
                        String message = "" + ChatFormatting.GRAY;
                        if (tile.getFluidInTank().amount >= 16000) {
                            message += "Tank is full.";
                        } else {
                            message += "Cannot accept this fluid.";
                        }
                        playerIn.addChatComponentMessage(new TextComponentString(message));
                    }
                    return true;
                }
            }
        }
        if(!worldIn.isRemote){
            playerIn.openGui(ProjectBench.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        if(((TileEntityProjectBench)tileentity).getHasFluidUpgrade()){
            EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ProjectBench.upgrade, 1, 1));
            float f3 = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * (double)f3;
            entityitem.motionY = RANDOM.nextGaussian() * (double)f3 + 0.20000000298023224D;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)f3;
            worldIn.spawnEntityInWorld(entityitem);
        }

        super.breakBlock(worldIn, pos, state);
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
