package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.config.ClientConfig;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;

public class RogueLootConfigs {
	//instance of your config loaded from file and automatically registered to the SyncedConfigRegistry and ClientConfigRegistry using the getId() method
	//ConfigApiJava can come in handy to avoid pernicious compiler errors depending on your IDE and gradle setup.
//	public static ServerConfig SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new);
	public static ClientConfig CLIENT_CONFIG = ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);

	//Init function would be called in ModInitializer or some other entrypoint. Not strictly necessary if loading on-reference is ok.
	public static void bootstrap() {
	}
}
