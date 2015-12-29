package storagecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.tile.TileController;

import java.util.List;

public class BlockController extends BlockBase
{
	public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumControllerType.class);
	public static final PropertyInteger ENERGY = PropertyInteger.create("energy", 0, 8);

	public BlockController()
	{
		super("controller");
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems)
	{
		for (int i = 0; i <= 1; i++)
		{
			subItems.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]
			{
				DIRECTION,
				TYPE,
				ENERGY
			});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(TYPE, meta == 0 ? EnumControllerType.NORMAL : EnumControllerType.CREATIVE);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(TYPE) == EnumControllerType.NORMAL ? 0 : 1;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return super.getActualState(state, world, pos)
			.withProperty(ENERGY, ((TileController) world.getTileEntity(pos)).getEnergyScaled(8));
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileController();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		((TileController) world.getTileEntity(pos)).onDestroyed();

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos)
	{
		return ((TileController) world.getTileEntity(pos)).getEnergyScaled(15);
	}
}
