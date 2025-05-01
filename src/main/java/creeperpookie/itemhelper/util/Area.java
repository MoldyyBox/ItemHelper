package creeperpookie.itemhelper.util;

import creeperpookie.itemhelper.ItemHelperPlugin;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class Area
{
	private Location pos1;
	private Location pos2;
	private int xSize;
	private int ySize;
	private int zSize;
	private int volume;

	public Area()
	{
		pos1 = null;
		pos2 = null;
		xSize = 0;
		ySize = 0;
		zSize = 0;
		volume = 0;
	}

	public Area(Location pos1, Location pos2)
	{
		if (pos1 == null || pos1.getWorld() == null || pos2 == null || pos2.getWorld() == null) throw new IllegalArgumentException("Area location arguments and/or their world cannot be null");
		else if (!pos1.getWorld().equals(pos2.getWorld())) throw new IllegalArgumentException("Locations pos1 and pos2 must refer to the same world");
		this.pos1 = pos1;
		this.pos2 = pos2;
		xSize = Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1;
		ySize = Math.abs(pos1.getBlockY() - pos2.getBlockY()) + 1;
		zSize = Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1;
		volume = xSize * ySize * zSize;
	}

	public Location getPos1()
	{
		return pos1;
	}

	public void setPos1(@Nullable Location pos1)
	{
		if (pos1 != null && pos2 != null && !pos1.getWorld().equals(pos2.getWorld())) throw new IllegalArgumentException("Area pos1 and pos2 Locations must refer to the same world");
		else this.pos1 = pos1;
		calculateSize();
	}

	public Location getPos2()
	{
		return pos2;
	}

	public void setPos2(@Nullable Location pos2)
	{
		if (pos1 != null && pos2 != null && !pos2.getWorld().equals(pos1.getWorld())) throw new IllegalArgumentException("Area pos1 and pos2 Locations must refer to the same world");
		else this.pos2 = pos2;
		calculateSize();
	}

	public int getSize(Axis axis)
	{
		return switch (axis)
		{
			case X -> xSize;
			case Y -> ySize;
			case Z -> zSize;
		};
	}

	public int getVolume()
	{
		return volume;
	}

	public Location getCenter()
	{
		return getCenter(0);
	}

	public Location getCenter(int yOffset)
	{
		if (!isValid()) return null;
		else if (pos1.equals(pos2)) return pos1.clone().add(0, yOffset, 0);
		Location midpointBase = pos1.clone().add((pos1.getX() < pos2.getX() ? 1 : -1) * (xSize / 2.0), ((pos1.getY() < pos2.getY() ? 1 : -1) * ((ySize / 2.0) + yOffset)), (pos1.getZ() < pos2.getZ() ? 1 : -1) * (zSize / 2.0));
		ItemHelperPlugin.getInstance().getLogger().info("Calculated center of " + Utility.locationAsString(pos1) + " to " + Utility.locationAsString(pos2) + " as " + Utility.locationAsString(volume % 2 == 0 ? midpointBase.toBlockLocation() : midpointBase.toCenterLocation()));
		return volume % 2 == 0 ? midpointBase.toBlockLocation() : midpointBase.toCenterLocation();
	}

	public boolean containsBlock(Block block, boolean ignoreY)
	{
		return containsLocation(block.getLocation(), ignoreY);
	}

	public boolean containsLocation(Location location, boolean ignoreY)
	{
		if (!isValid()) return false;
		boolean withinX = false;
		boolean withinY = false;
		boolean withinZ = false;
		if (pos2.getBlockX() < pos1.getBlockX()) withinX = location.getBlockX() >= pos2.getBlockX() && location.getBlockX() <= pos1.getBlockX();
		else if (pos1.getBlockX() == pos2.getBlockX() && location.getBlockX() == pos1.getBlockX()) withinX = true;
		else if (pos2.getBlockX() > pos1.getBlockX()) withinX = location.getBlockX() >= pos1.getBlockX() && location.getBlockX() <= pos2.getBlockX();
		if (!withinX) return false;
		else if (!ignoreY && pos2.getBlockY() < pos1.getBlockY()) withinY = location.getBlockY() >= pos2.getBlockY() && location.getBlockY() <= pos1.getBlockY();
		else if (!ignoreY && pos1.getBlockY() == pos2.getBlockY() && location.getBlockY() == pos1.getBlockY()) withinY = true;
		else if (!ignoreY && pos2.getBlockY() > pos1.getBlockY()) withinY = location.getBlockY() >= pos1.getBlockY() && location.getBlockY() <= pos2.getBlockY();
		if (!ignoreY && !withinY) return false;
		else if (pos2.getBlockZ() < pos1.getBlockZ()) withinZ = location.getBlockZ() >= pos2.getBlockZ() && location.getBlockZ() <= pos1.getBlockZ();
		else if (pos1.getBlockZ() == pos2.getBlockZ() && location.getBlockZ() == pos1.getBlockZ()) withinZ = true;
		else if (pos2.getBlockZ() > pos1.getBlockZ()) withinZ = location.getBlockZ() >= pos1.getBlockZ() && location.getBlockZ() <= pos2.getBlockZ();
		return withinZ;
	}

	public boolean containsArea(Area area, boolean ignoreY)
	{
		return isValid() && area.isValid() && containsLocation(area.pos1, ignoreY) && containsLocation(area.pos2, ignoreY);
	}

	public boolean isCrossWorld()
	{
		return pos1 != null && pos2 != null && !pos1.getWorld().equals(pos2.getWorld());
	}

	public boolean isValid()
	{
		return pos1 != null && pos2 != null && pos1.getWorld() != null && pos2.getWorld() != null && !isCrossWorld();
	}

	public void reset()
	{
		pos1 = null;
		pos2 = null;
		calculateSize();
	}

	@Override
	public String toString()
	{
		return "Area from " + Utility.locationAsString(pos1) + " to " + Utility.locationAsString(pos2, true, false) + "; X size: " + xSize + "; Y size: " + ySize + "; Z size: " + zSize + "; total volume: " + volume;
	}

	private void calculateSize()
	{
		if (pos1 != null && pos2 != null)
		{
			xSize = Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1;
			ySize = Math.abs(pos1.getBlockY() - pos2.getBlockY()) + 1;
			zSize = Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1;
			volume = xSize * ySize * zSize;
		}
		else
		{
			xSize = 0;
			ySize = 0;
			zSize = 0;
			volume = 0;
		}
	}
}
