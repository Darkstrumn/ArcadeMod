package superhb.arcademod.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import superhb.arcademod.Reference;
import superhb.arcademod.network.*;
import superhb.arcademod.util.*;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import superhb.arcademod.util.RecipeUtil;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit (FMLPreInitializationEvent event) {
		super.preInit(event);
		
		KeyHandler.registerKeyBinding();
		
		// Register Network Message
		ArcadePacketHandler.INSTANCE.registerMessage(ClientCoinMessage.Handler.class, ClientCoinMessage.class, 0, Side.CLIENT);
		ArcadePacketHandler.INSTANCE.registerMessage(ClientBuyMessage.Handler.class, ClientBuyMessage.class, 1, Side.CLIENT);
		
		// Register Sounds
		//ArcadeSoundRegistry.registerSounds();
	}
	
	@Override
	public void init (FMLInitializationEvent event) {
		super.init(event);
		
		// Register Recipes
		RecipeUtil.registerRecipes();
	}
}
