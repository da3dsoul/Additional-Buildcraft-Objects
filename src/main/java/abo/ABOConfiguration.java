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

		super.save();
	}
}
