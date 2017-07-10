package superhb.arcademod;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import superhb.arcademod.client.UpdateAnnouncer;
import superhb.arcademod.content.ArcadeItems;
import superhb.arcademod.proxy.CommonProxy;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import superhb.arcademod.tileentity.TileEntityArcade;

import java.util.Map;
import java.util.Set;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, updateJSON = Reference.UPDATE_URL)
public class Arcade {
    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    @Instance(Reference.MODID)
    public static Arcade instance;

    public static final CreativeTabs tab = new CreativeTabs(Reference.MODID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ArcadeItems.coin);
        }

        @Override
        public String getTranslatedTabLabel () {
            return I18n.format("mod.arcademod:name.locale");
        }
    };

    public static Set<Map.Entry<ComparableVersion, String>> changelog;
    public static ForgeVersion.Status status;

    // Configuration Variables
    public static boolean disableCoins;
    public static boolean requireRedstone;
    public static boolean disableUpdateNotification;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        // Mod info
        event.getModMetadata().autogenerated = false;
        event.getModMetadata().credits = Reference.CREDIT;
        event.getModMetadata().authorList.add(Reference.AUTHOR);
        event.getModMetadata().description = Reference.DESCRIPTION;
        event.getModMetadata().url = Reference.URL;
        event.getModMetadata().logoFile = Reference.LOGO;
        event.getModMetadata().updateJSON = Reference.UPDATE_URL;

        // Configuration File
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        disableCoins = config.getBoolean("disableCoins", Configuration.CATEGORY_GENERAL, false, "Disable the need to use coins to play the arcade machines");
        requireRedstone = config.getBoolean("requireRedstone", Configuration.CATEGORY_GENERAL, false, "Require the machines to be powered by redstone to play");
        disableUpdateNotification = config.getBoolean("disableUpdateNotification", Configuration.CATEGORY_GENERAL, false, "Disable message in chat when update is available");
        config.save();

        // Register TileEntity
        GameRegistry.registerTileEntity(TileEntityArcade.class, Reference.MODID + ":tile_arcade");

        // Register Event
        if (!disableUpdateNotification) MinecraftForge.EVENT_BUS.register(new UpdateAnnouncer());

        proxy.preInit(event);
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        for (ModContainer mod : Loader.instance().getModList()) {
            if (mod.getModId().equals(Reference.MODID)) {
                status = ForgeVersion.getResult(mod).status;
                if (status == ForgeVersion.Status.OUTDATED || status == ForgeVersion.Status.BETA_OUTDATED) changelog = ForgeVersion.getResult(mod).changes.entrySet();
            }
        }

        proxy.postInit(event);
    }
}