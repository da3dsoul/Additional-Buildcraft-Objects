package abo;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ABOConfiguration extends Configuration {
	public ABOConfiguration(File file) {
		super(file);
	}

	@Override
	public void save() {
		// Property versionProp = null;

		get(CATEGORY_GENERAL, "version", ABO.VERSION);

		super.save();
	}
}
