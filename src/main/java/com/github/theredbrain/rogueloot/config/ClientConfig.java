package com.github.theredbrain.rogueloot.config;

import com.github.theredbrain.rogueloot.RogueLoot;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;

public class ClientConfig extends Config {

	public ClientConfig() {
		super(RogueLoot.identifier("client"));
	}

	public ValidatedBoolean show_banished_loot_options = new ValidatedBoolean(true);
}
