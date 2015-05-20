package com.bau5.projectbench.common;

import com.bau5.projectbench.client.TileEntityProjectBench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(playerIn.isSneaking())
            return false;
        if(worldIn.getTileEntity(pos) == null || !(worldIn.getTileEntity(pos) instanceof TileEntityProjectBench))
            return false;
        TileEntityProjectBench tile = (TileEntityProjectBench) worldIn.getTileEntity(pos);
        ItemStack held = playerIn.getHeldItem();
        if(held != null && held.getItem() == ProjectBench.upgrade
                && tile.getCanAcceptUpgrade()){
            tile.performUpgrade(playerIn.getHeldItem());
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
                            playerIn.setCurrentItemOrArmor(0, held);
                        }
                        playerIn.inventory.addItemStackToInventory(data.emptyContainer);
                    }
                    return true;
                }else{
                    String message = "";
                    if(tile.getFluidInTank().amount > 16000){
                        message = "Tank is full.";
                    }else{
                        message = "Cannot accept this fluid.";
                    }
                    if(!worldIn.isRemote)
                        playerIn.addChatComponentMessage(new ChatComponentText(message));
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

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public BlockState getBlockState() {
        return super.getBlockState();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public IBlockState getStateForEntityRender(IBlockState state) {
        return super.getStateForEntityRender(state);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getExtendedState(state, world, pos);
    }

    @Override
    public int getRenderType() {
        return 3;
    }
}
